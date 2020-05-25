/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package version.control;

import index.RepositoryIndex;
import index.IndexElement;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Deepanshu Vangani
 */
public class WatchDir implements Runnable {

    private final static String RELATIVE_INDEXFILE_PATH = ".vcs/tracked.vcs";
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private boolean trace = false;
    private final Thread t;
    private final Path directory;
    private final RepositoryIndex repositoryFileIndex;

    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    private String relativeFilePath(Path path) {
        return path.toString().substring(this.directory.toString().length());
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s => %s\n", prev, dir);
                    //replace the path in the index
                }
            }
        }

        try (Stream<Path> paths = Files.walk(dir, 1)) {
            paths.filter(path -> !(path.startsWith(this.directory.resolve(".vcs")) || path.equals(this.directory)))
                    .forEach((Path path) -> {
                        String relativeFilePath = this.relativeFilePath(path);
                        IndexElement indexElement = repositoryFileIndex.findByPath(relativeFilePath);
                        if (indexElement == null) {

                            IndexElement newElement = new IndexElement(relativeFilePath);
                            if (Files.isDirectory(path)) {
                                newElement.setAsDirectory();
                            }
                            this.repositoryFileIndex.addEntry(newElement);
                        }
                    });
            this.repositoryFileIndex.flushToStore();
        }
        this.keys.put(key, dir);
    }

    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (!dir.endsWith(RELATIVE_INDEXFILE_PATH)) {
                    register(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    return FileVisitResult.SKIP_SUBTREE;
                }
            }
        });
    }

    WatchDir(Path dir, boolean recursive) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.recursive = recursive;
        this.directory = dir;
        this.repositoryFileIndex = RepositoryIndex.createIndex(dir);
        if (recursive) {
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done");
        } else {
            register(dir);
        }

        repositoryFileIndex.flushToStore();

        this.trace = true;

        t = new Thread(this);
        this.startListening();
    }

    private void startListening() {
        t.start();
    }

    private void processEvents() {
        FileEvents fileEvents = new FileEvents(this.directory, this.repositoryFileIndex);
        for (;;) {

            WatchKey key;
            try {
                key = this.watcher.take();

            } catch (InterruptedException ex) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized");
                continue;
            }
            key.pollEvents().forEach((event) -> {
                WatchEvent.Kind kind = event.kind();
                if (!(kind == OVERFLOW)) {
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    Path fileModified = dir.resolve(name);
                    System.out.format("%s $ %s: %s\n", this.directory, event.kind().name(), fileModified);
                    if (recursive && (kind == ENTRY_CREATE)) {
                        try {
                            if (Files.isDirectory(fileModified, NOFOLLOW_LINKS)) {
                                registerAll(fileModified);
                            } else {
                                String relativeFilePath = this.relativeFilePath(fileModified);
                                IndexElement element = new IndexElement(relativeFilePath);
                                this.repositoryFileIndex.addEntry(element);
                                this.repositoryFileIndex.flushToStore();
                            }
                        } catch (IOException e) {

                        }
                    }

                    if (kind == ENTRY_MODIFY) {
                        if (!fileModified.toString().substring(this.directory.toString().length()).startsWith("\\.vcs")) {
                            try {
                                fileEvents.modifyEvent(fileModified);
                            } catch (IOException ex) {
                                System.err.println("IOException at entry modify");
                            }
                        } else {
                            this.repositoryFileIndex.refresh();
                        }
                    } else if (kind == ENTRY_DELETE) {
                        //handle delete
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        keys.remove(key);
                    }
                }
            });
        }
    }

    public void run() {
        this.processEvents();
    }

}
