/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Object.commit.Commit;
import index.RepositoryIndex;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import vcs.Constants;

/**
 *
 * @author Deepanshu Vangani
 */
public class Branch {

    private String name;
    private String commitId;
    private final Path branchFilePath;

    private Branch(String name, Path branchFilePath, String commitId) {
        this.name = name;
        this.commitId = commitId;
        this.branchFilePath = branchFilePath;
    }

    public String getCommitId() {
        return commitId;
    }

    public Path getBranchFilePath() {
        return branchFilePath;
    }

    public String getName() {
        return name;
    }

    private void setCommitId(String commitHash) {
        this.commitId = commitHash;
    }

    public Path getRepoPath() {
        return this.branchFilePath.resolve("../../../").normalize();
    }

    private static String getCurrentBranchName(Path repoPath) throws IOException {
        Path configFile = repoPath.resolve(Constants.CONFIG_FILE);
        String currentBranchName = Files.readAllLines(configFile).get(Constants.BRANCH_LINE_IN_CONFIG).split(":")[1];
        return currentBranchName;
    }

    private void setCurrentBranch() throws IOException {
        Path configFile = this.getRepoPath().resolve(Constants.CONFIG_FILE);
        String config = "branch:" + this.name + System.lineSeparator();
        Files.write(configFile, config.getBytes());
    }

    public static Branch getCurrentBranch(Path repoPath) {
        Branch currentBranch = null;
        try {
            String currentBranchName = getCurrentBranchName(repoPath);
            Path branchFilePath = repoPath.resolve(Constants.BRANCHES_DIR + "\\" + currentBranchName);
            String commitId = null;
            try {
                commitId = Files.readAllLines(branchFilePath).get(0);
            } catch (IndexOutOfBoundsException e) {
            }

            currentBranch = new Branch(currentBranchName, branchFilePath, commitId);
        } catch (IOException ex) {
            System.err.println("could not get current branch");
        }
        return currentBranch;
    }

    public static void displayAllBranches(Path repoPath) throws IOException {
        String currentBranchName = getCurrentBranchName(repoPath);
        Path branchesDirPath = repoPath.resolve(Constants.BRANCHES_DIR);
        Files.walk(branchesDirPath)
                .filter(path -> !path.equals(branchesDirPath))
                .forEach((path) -> {
                    String branchName = path.getFileName().toString();
                    if (branchName.equals(currentBranchName)) {
                        System.out.println("-> " + branchName);
                    } else {
                        System.out.println(branchName);
                    }
                });
    }

    public void registerToBranch(Commit newCommit) {
        try {
            String commitHash = newCommit.getHash();
            Files.write(this.branchFilePath, commitHash.getBytes());
            this.setCommitId(commitHash);
        } catch (IOException ex) {
            System.out.println(Paths.get(Constants.MASTER_BRANCH));
        }
    }

    public Branch switchBranch(String branchName) throws IOException {
        Branch newBranch;
        if (this.branchExists(branchName)) {
            Path branchFilePath = this.branchFilePath.getParent().resolve(branchName);
            String commitId = null;
            try {
                commitId = Files.readAllLines(branchFilePath).get(0);
            } catch (IndexOutOfBoundsException e) {
            }
            newBranch = new Branch(branchName, branchFilePath, commitId);
        } else {
            newBranch = createNewBranch(branchName, this.branchFilePath.getParent(), this.commitId);
        }
        newBranch.setCurrentBranch();
        return newBranch;
    }

    private boolean branchExists(String branchName) throws IOException {
        Path branchesDir = this.branchFilePath.getParent();
        try {
            Files.walk(branchesDir).filter(branch -> branch.getFileName().toString().equals(branchName)).findAny().get();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private static Branch createNewBranch(String branchName, Path branchesDirectory, String commitId) throws IOException {
        Path branchFilePath = branchesDirectory.resolve(branchName);
        Files.createFile(branchFilePath);
        Files.write(branchFilePath, commitId.getBytes());
        return new Branch(branchName, branchFilePath, commitId);

    }

    public void restoreBranchState() {
        try {
            Commit latestBranchCommit = Commit.getCommitFromId(this, commitId);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("could not return to branch branch state.");
        }

    }
}
