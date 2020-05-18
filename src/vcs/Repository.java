package vcs;

import Objects.Blob.Blob;
import Objects.Tree.ChildTypes;
import Objects.Tree.Tree;
import index.IndexElement;
import index.RepositoryIndex;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static vcs.Constants.REGISTER_LOCATION;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Deepanshu Vangani
 */
public class Repository {

    private final Path location;
    private boolean init;
    private final RepositoryIndex index;

    private Repository(String currentDirectory) throws IOException {
        this.location = Paths.get(currentDirectory);
        this.index = RepositoryIndex.createIndex(this.location);
    }

    public static Repository getRepo(String currentDirectory) throws IOException {
        Path currentDirectoryPath = Paths.get(currentDirectory);
        while (currentDirectoryPath != null) {
            if (Files.isDirectory(Paths.get(currentDirectoryPath.toString(), Constants.VCS_FOLDER))) {
                return new Repository(currentDirectoryPath.toString());
            }
            currentDirectoryPath = currentDirectoryPath.getParent();
        }
        return null;

    }

    public void stage(Path paths[]) {
        for (Path path : paths) {
            if (Files.isRegularFile(path)) {
                this.stageFile(path);
            } else if (Files.isDirectory(path)) {
                this.stageDirectory(path);
            } else {
                System.err.println(path + " does not exist.");
            }

        }

    }

    private void stageFile(Path filePath) {

        Objects.Object childObject = null;
        ChildTypes childType = null;
        while (!filePath.equals(this.location)) {
            if (Files.isRegularFile(filePath)) {
                Blob fileBlob = Blob.createBlobObject(filePath);
                FileHasher.saveHashToDisk(fileBlob, this.location);
                this.recordToIndex(fileBlob);
                childObject = fileBlob;
                childType = ChildTypes.BLOB;

            } else if (Files.isDirectory(filePath)) {
                System.out.println(filePath);
                String relativeFilePath = filePath.toString().substring(this.location.toString().length());
                String dirObjectHashStart = this.index.getLatestHash(relativeFilePath);
                System.out.println(dirObjectHashStart);
                Tree dirInstance;
                try {
                    if (!dirObjectHashStart.equals("null")) {
                        dirInstance = Tree.createTree(filePath, this.location, dirObjectHashStart);
                        
                        dirInstance.addOrReplaceChild(childObject,childType);
                    } else {
                        dirInstance = Tree.createTree(filePath,this.location);
                        dirInstance.addChild(childObject, childType);
                    }
                    FileHasher.saveHashToDisk(dirInstance, this.location);
                    this.recordToIndex(dirInstance);
                    childObject = dirInstance;
                    childType = ChildTypes.TREE;
                } catch (IOException e) {
                    System.err.println("could not access the objects files");
                }

            } else {
                System.err.println("file does not exist");
            }
            

            filePath = filePath.getParent();
        }

        try {
            index.flushToStore();
        } catch (IOException ex) {
            System.out.println("Could not modify the index");
        }
    }

    private void stageDirectory(Path directoryPath) {
        
    }

    public void recordToIndex(Objects.Object obj) {
        String relativeFilePath = obj.getFilePath().toString().substring(this.location.toString().length());
        IndexElement record = this.index.findByPath(relativeFilePath);
        record.clearModified();
        record.setLatestStagedHash(obj.getHash().substring(0, 9));

    }

    public static Repository init(String currentDirectory) {
        Repository repo = null;
        try {
            repo = new Repository(currentDirectory);
        } catch (IOException ex) {
            //
        }
        File vcsRoot = new File(currentDirectory + "\\" + ".vcs");
        boolean success = false;
        if (vcsRoot.mkdir()) {
            try {
                success = new File(currentDirectory + "\\" + ".vcs" + "\\objects").mkdir();
                success = new File(currentDirectory + "\\" + ".vcs" + "\\commits").mkdir();
                success = new File(currentDirectory + "\\" + ".vcs" + "\\tracked.vcs").createNewFile();
                success = repo.registerRepository();
                if (success) {
                    repo.init = true;
                    return repo;
                } else {
                    repo.cleanup();
                }

            } catch (IOException e) {
                System.out.println("Could not complete the task");
            }
        } else {
            System.out.println("Something went wrong");
        }
        return null;
    }

    private boolean registerRepository() throws IOException {
        FileWriter registerWriter = null;
        try {
            registerWriter = new FileWriter(REGISTER_LOCATION, true);

            registerWriter.write(this.location + System.lineSeparator());
            registerWriter.close();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (registerWriter != null) {
                registerWriter.close();
            }

        }
    }

    private void cleanup() {
        //revert any changes made
    }

    @Override
    public String toString() {
        return this.location.toString();
    }

}
