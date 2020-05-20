package Objects.Tree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import vcs.Constants;
import vcs.FileHasher;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Deepanshu Vangani
 */
public class Tree extends Objects.Object {

    private final List<TreeItem> children;
    private final Path repoPath;

    private Tree(Path dirPath, Path repoPath) {
        super(dirPath);
        this.children = new ArrayList<>();
        this.repoPath = repoPath;
    }

    private Tree(Path dirPath, Path repoPath, String hash) {
        super(dirPath, hash);
        this.children = new ArrayList<>();
        this.repoPath = repoPath;
    }

    public static Tree createTree(Path repoPath, Path dirPath) {
        Tree tree = new Tree(dirPath, repoPath);
        return tree;
    }

    public static Tree createTree(Path repoPath, Path dirPath, String dirObjectHashStart) throws IOException {
        Tree tree = createTree(repoPath, dirPath);
        Path treeObjectPath = tree.getHashFile(dirObjectHashStart);
        if (treeObjectPath != null) {
            List<String> lines = new ArrayList<>(Files.readAllLines(treeObjectPath, StandardCharsets.UTF_8));
            lines.forEach(line -> {
                TreeItem item = new TreeItem(line);
                tree.addChild(item);
            });
        } else {
            System.err.println("Hash file does not exist");
        }
        return tree;
    }

    public void addChild(TreeItem item) {
        this.children.add(item);
        String hash = FileHasher.hashFile(this.toString());
        this.hash = hash;
    }

    public void addChild(Objects.Object item, ChildTypes type) {
        String hash = item.getHash();
        String name = item.getFilePath().getFileName().toString();
        TreeItem treeItem = new TreeItem(type, hash, name);
        addChild(treeItem);
    }

    public void addOrReplaceChild(Objects.Object item, ChildTypes type) {
        String hash = item.getHash();
        String name = item.getFilePath().getFileName().toString();
        try {

            TreeItem treeItem = this.children.stream().filter(i -> {
                return i.getChildName().equals(name);
            }).findFirst().get();
            treeItem.setChildHash(item.getHash());
        } catch (NoSuchElementException e) {
            TreeItem treeItem = new TreeItem(type, hash, name);
            addChild(treeItem);
        }
    }

    private Path getHashFile(String dirObjectHashStart) throws IOException {
        Path objectsDirectory = repoPath.resolve(Constants.VCS_OBJECTS);
        Path bucketPath = objectsDirectory.resolve(dirObjectHashStart.substring(0, 2));
        if (Files.exists(bucketPath) && Files.isDirectory(bucketPath)) {

            Optional<Path> treeObjectPathOptional = Files.list(bucketPath)
                    .filter(path -> {
                        return path.getFileName().toString().startsWith(dirObjectHashStart.substring(2));
                    })
                    .findAny();
            Path treeObjectPath = treeObjectPathOptional != null ? treeObjectPathOptional.get() : null;

            return treeObjectPath;

        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder treeSb = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        this.children.forEach(child -> {
            treeSb.append(child.toString());
            treeSb.append(lineSeparator);
        });
        return treeSb.toString();
    }

}
