/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package version.control;

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

/**
 *
 * @author Deepanshu Vangani
 */
public class WatchDir implements Runnable {
    private final WatchService watcher;
    private Map<WatchKey,Path> keys;
    private final boolean recursive;
    private boolean trace = false;
    private final Thread t;
    private final Path directory;
    
    static <T> WatchEvent<T> cast(WatchEvent<?> event){
        return (WatchEvent<T>) event;
    }
    
    private void register(Path dir)throws IOException{
        WatchKey key = dir.register(watcher, ENTRY_CREATE , ENTRY_DELETE,ENTRY_MODIFY);
        if(trace){
            Path prev = keys.get(key);
            if(prev == null){
                System.out.format("register: %s\n", dir);
            }else{
                if(!dir.equals(prev)){
                    System.out.format("update: %s => %s\n", prev,dir);
                }
            }
        }
        this.keys.put(key,dir);
    }
    private void registerAll(final Path start ) throws IOException{
        Files.walkFileTree(start, new SimpleFileVisitor<Path>(){
            
            @Override
            public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs) throws IOException{
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    WatchDir(Path dir,boolean recursive) throws IOException{
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.recursive = recursive;
        this.directory = dir;
        if(recursive){
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done");
        }else{
            register(dir);
        }
        this.trace = true;
        t = new Thread(this);
        this.startListening();
    }
    
    private void startListening(){
        t.start();
    }
    
    private void processEvents(){
        for(;;){
            
            WatchKey key;
            try{
                key = this.watcher.take();
                
            } catch (InterruptedException ex) {
                return;
            }
            
            Path dir = keys.get(key);
            if(dir == null){
                System.err.println("WatchKey not recognized");
                continue;
            }
            for(WatchEvent<?> event:key.pollEvents()){
                WatchEvent.Kind kind = event.kind();
                if(kind == OVERFLOW){
                    continue;
                }
                
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                System.out.format("%s $ %s: %s\n",this.directory, event.kind().name() , child);
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                       
                    }
                }
                 // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);
                }
            }
        }
    }
    

    public void run(){
        this.processEvents();
    }
    
    
}
