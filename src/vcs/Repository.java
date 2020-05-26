package vcs;

import Object.commit.Commit;
import Objects.Blob;
import Objects.Branch;
import Objects.Tree.ChildTypes;
import Objects.Tree.Tree;
import Objects.User;
import index.IndexElement;
import index.RepositoryIndex;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream;
import java.nio.file.StandardOpenOption;

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
    private RepositoryIndex index;
    private Branch currentBranch;

    private Repository(String currentDirectory) throws IOException {
        this.location = Paths.get(currentDirectory);
        this.index = RepositoryIndex.createIndex(this.location);
        this.currentBranch = Branch.getCurrentBranch(this.location);
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

    public Path getRepoPath() {
        return location;
    }

    private void setCurrentBranch(Branch currentBranch) {
        this.currentBranch = currentBranch;
        this.index = currentBranch.restoreBranchState(this.index);
    }

    public void recordToIndex(Objects.Object obj) {
        String relativeFilePath = obj.getFilePath().toString().substring(this.location.toString().length());
        IndexElement record = this.index.findByPath(relativeFilePath);
        record.clearModified();
        record.stage();
        record.setLatestStagedHash(obj.getHash().substring(0, 9));
    }

    public void stage(Path paths[]) {
        for (Path path : paths) {
            String relativeFilePath = path.toString().substring(this.location.toString().length());
            if (this.index.findByPath(relativeFilePath).isDeleted()) {
                this.index.removeEntry(relativeFilePath);
            } else {
                if (Files.isRegularFile(path)) {
                    this.stageFile(path);
                } else if (Files.isDirectory(path)) {
                    this.stageDirectory(path);
                } else {
                    System.err.println(path + " does not exist.");
                }
            }
        }
        try {
            index.flushToStore();
        } catch (IOException ex) {
            System.out.println("Could not modify the index");
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

                String relativeFilePath = filePath.toString().substring(this.location.toString().length());
                String dirObjectHashStart = this.index.getLatestHash(relativeFilePath);
                Tree dirInstance;
                try {
                    if (dirObjectHashStart.equals("null")) {
                        dirInstance = Tree.createTree(this.location, filePath);
                        dirInstance.addChild(childObject, childType);
                    } else {
                        dirInstance = Tree.createTree(this.location, filePath, dirObjectHashStart);
                        dirInstance.addOrReplaceChild(childObject, childType);
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
    }

    private void stageDirectory(Path directoryPath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    stageDirectory(entry);
                } else if (Files.isRegularFile(entry)) {
                    System.out.println("staging file: " + entry);
                    stageFile(entry);
                }
            }
        } catch (IOException e) {
            System.err.println("error in reading files from path : " + directoryPath);
        }
    }

    public void commit(String message) {
        if (User.exists()) {
            if (this.index.hasUnstagedDeletedChanges()) {
                System.out.println("You have Deleted Changes which are not staged. Cannot Commit Changes");
                System.out.println("Please Stage Deleted Files to Complete Commit");
            } else {
                if (this.index.hasStagedChanges()) {
                    Commit commit = Commit.createCommit(this.currentBranch, index);
                    commit.setCommitMessage(message);
                    commit.saveCommit();
                } else {
                    System.out.println("No changes to commit");
                }
            }
        } else {
            System.err.println("Please configure username and email");
        }
    }

    public void status() {
        System.out.println("\nunstaged changes: ");
        index.showModifiedFiles();
        System.out.println("\nStaged changes: ");
        index.showStagedFiles();
    }

    public void switchBranch(String branchName) {
        try {
            Branch currentBranch = this.currentBranch.switchBranch(branchName);
            this.setCurrentBranch(currentBranch);
            System.out.println("switched to branch " + branchName);
        } catch (IOException e) {
            System.out.println("could not switch to branch");
        }
    }

    public static Repository init(String currentDirectory) {
        Repository repo = null;
        File vcsRoot = new File(currentDirectory + "\\" + ".vcs");
        boolean success = false;
        if (vcsRoot.mkdir()) {
            try {
                success = success && new File(currentDirectory + "\\" + ".vcs" + "\\objects").mkdir();
                success = success && new File(currentDirectory + "\\" + ".vcs" + "\\commits").mkdir();
                success = success && new File(currentDirectory + "\\" + ".vcs" + "\\tracked.vcs").createNewFile();
                success = success && new File(currentDirectory + "\\" + ".vcs" + "\\refs").mkdir();
                success = success && new File(currentDirectory + "\\" + ".vcs" + "\\refs\\master").createNewFile();
                success = success && new File(currentDirectory + "\\" + ".vcs" + "\\config.vcs").createNewFile();
                repo = new Repository(currentDirectory);
                success = repo.registerRepository();
                if (success) {
                    repo.init = true;
                    repo.setUpConfig();
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
            Path registerLocation = Paths.get(REGISTER_LOCATION);
            System.out.println(registerLocation);
            Files.write(registerLocation, (System.lineSeparator() + this.location).getBytes(), StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
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

    private void setUpConfig() throws IOException {
        String config = "branch:master" + System.lineSeparator();
        Files.write(this.location.resolve(".vcs" + "\\config.vcs"), config.getBytes());
    }

    @Override
    public String toString() {
        return this.location.toString();
    }

}
