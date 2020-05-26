/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Object.commit;

import Objects.Blob;
import Objects.Branch;
import Objects.User;
import index.RepositoryIndex;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import vcs.Constants;
import vcs.FileUtils;

/**
 *
 * @author Deepanshu Vangani
 */
public class Commit extends Objects.Object {

    private Date timeStamp;
    private RepositoryIndex indexSnapshot;
    private String commitMessage;
    private String indexFileBlobHash;
    private String lastCommitHash;
    private Branch branch;

    private Commit(Path filePath, RepositoryIndex indexSnapShot, Branch branch) {
        super(filePath);
        this.indexSnapshot = indexSnapShot;
        this.timeStamp = new Date();
        this.branch = branch;
    }

    private Commit() {

    }

    public RepositoryIndex getIndexSnapshot() {
        return indexSnapshot;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public void setIndexFileBlobHash(String indexFileBlobHash) {
        this.indexFileBlobHash = indexFileBlobHash;
    }

    public static Commit getCommitFromId(Branch currentBranch, String commitId) throws IOException {
        Path repoPath = currentBranch.getRepoPath();

        Path commitFilePath = repoPath.resolve(Constants.VCS_COMMIT).resolve(commitId);
        String indexFileHash = Files.readAllLines(commitFilePath).get(Constants.INDEX_HASH_IN_COMMIT).split(":")[1];
        System.out.println(indexFileHash);
        RepositoryIndex commitIndex = RepositoryIndex.getCommitIndex(repoPath, indexFileHash);

        Commit commit = Commit.createCommit(currentBranch, commitIndex);
        return commit;

    }

    public static Commit createCommit(Branch branch, RepositoryIndex indexSnapshot) {
        Commit commit = new Commit(indexSnapshot.getIndexFilePath(), indexSnapshot, branch);
        commit.indexSnapshot.commitStagedHashes();
        Path indexFilePath = commit.indexSnapshot.getIndexFilePath();
        Blob commitBlob = Blob.createBlobObject(indexFilePath);
        commit.setIndexFileBlobHash(commitBlob.getHash());
        commit.setHash(FileUtils.hashFile(commit.toString()));
        FileUtils.saveHashToDisk(commitBlob, commit.indexSnapshot.getRepoPath());
        commit.setLastCommitHash();
        return commit;
    }

    public void saveCommit() {
        Path commitFilePath = this.indexSnapshot.getRepoPath().resolve(vcs.Constants.VCS_COMMIT + "\\" + this.getHash());
        try {
            System.out.println(this.toString());
            this.flushCommit(commitFilePath, this.toString());
            this.branch.registerToBranch(this);
        } catch (IOException ex) {
            System.out.println("could not save commit");
        }
    }

    private void flushCommit(Path filePath, String contents) throws IOException {
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        Files.write(filePath, contents.getBytes());
    }

    @Override
    public String toString() {
        String lineSeparator = System.lineSeparator();
        StringBuilder commitData = new StringBuilder();
        commitData.append("IndexFile :").append(this.indexFileBlobHash);
        commitData.append(lineSeparator);
        commitData.append("Branch :").append(this.branch.getName());
        commitData.append(lineSeparator);
        commitData.append("timeStamp:").append(this.timeStamp.toString());
        commitData.append(lineSeparator);
        commitData.append("Username:").append(User.username);
        commitData.append(lineSeparator);
        commitData.append("email:").append(User.email);
        commitData.append(lineSeparator);
        commitData.append("Message:").append(this.commitMessage);
        commitData.append(lineSeparator);
        commitData.append("lastCommitHash:").append(this.lastCommitHash);
        return commitData.toString();
    }

    private void setLastCommitHash() {
        this.lastCommitHash = this.branch.getCommitId();
    }

    public static void logCommits(Path repoPath) {
        Path branchPath = repoPath.resolve(Constants.MASTER_BRANCH);
        try {
            String latestCommitHash = Files.readAllLines(branchPath).get(0);
            printCommits(latestCommitHash, repoPath);
        } catch (IOException e) {
            System.err.println("Could not read from master branch");
        } catch (IndexOutOfBoundsException e) {
            System.err.println("No commits yet");
        }
    }

    private static void printCommits(String commitHash, Path repoPath) {
        Path commitsDirectory = repoPath.resolve(Constants.VCS_COMMIT);
        Path commitFilePath = commitsDirectory.resolve(commitHash);
        String commmitContents = FileUtils.getFileContents(commitFilePath).toString();
        System.out.println(commmitContents);
        System.out.println("-------------------------------------------");
        String lineSeparator = System.lineSeparator();
        String previousCommit = commmitContents.split(lineSeparator)[Constants.PREVIOUS_COMMIT_HASH_LINE].split(":")[1];
        if (!previousCommit.equals("null")) {
            printCommits(previousCommit, repoPath);
        }

    }
}
