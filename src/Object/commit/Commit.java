/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Object.commit;

import Objects.Blob.Blob;
import Objects.user.User;
import index.RepositoryIndex;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import vcs.Constants;
import vcs.FileHasher;

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
    
    private Commit(Path filePath, RepositoryIndex indexSnapShot) {
        super(filePath);
        this.indexSnapshot = indexSnapShot;
        this.timeStamp = new Date();
    }
    
    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }
    
    public void setIndexFileBlobHash(String indexFileBlobHash) {
        this.indexFileBlobHash = indexFileBlobHash;
    }
    
    public static Commit createCommit(RepositoryIndex indexSnapshot) {
        Commit commit = new Commit(indexSnapshot.getIndexFilePath(), indexSnapshot);
        commit.indexSnapshot.commitStagedHashes();
        Path indexFilePath = commit.indexSnapshot.getIndexFilePath();
        Blob commitBlob = Blob.createBlobObject(indexFilePath);
        commit.setIndexFileBlobHash(commitBlob.getHash());
        commit.setHash(FileHasher.hashFile(commit.toString()));
        FileHasher.saveHashToDisk(commitBlob, commit.indexSnapshot.getRepoPath());
        commit.setLastCommitHash();
        return commit;
    }
    
    public void saveCommit() {
        Path commitFilePath = this.indexSnapshot.getRepoPath().resolve(vcs.Constants.VCS_COMMIT + "\\" + this.getHash());
        try {
            System.out.println(this.toString());
            this.flushCommit(commitFilePath, this.toString());
            this.registerToBranch(this.hash);
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
        commitData.append("IndexFile :" + this.indexFileBlobHash);
        commitData.append(lineSeparator);
        commitData.append("timeStamp:" + this.timeStamp.toString());
        commitData.append(lineSeparator);
        commitData.append("Username:" + User.username);
        commitData.append(lineSeparator);
        commitData.append("email:" + User.email);
        commitData.append(lineSeparator);
        commitData.append("Message:" + this.commitMessage);
        commitData.append(lineSeparator);
        commitData.append("lastCommitHash:" + this.lastCommitHash);
        return commitData.toString();
    }
    
    private void setLastCommitHash() {
        try {
            Path branchPath = this.indexSnapshot.getRepoPath().resolve(Constants.MASTER_BRANCH);
            try {
                this.lastCommitHash = Files.readAllLines(branchPath).get(0);
            } catch (IndexOutOfBoundsException e) {
                this.lastCommitHash = null;
            }
        } catch (IOException ex) {
            System.err.println("Could not get previous hash");
        }
    }
    
    private void registerToBranch(String hash) {
        try {
            Path branchPath = this.indexSnapshot.getRepoPath().resolve(Constants.MASTER_BRANCH);
            Files.write(branchPath, hash.getBytes());
        } catch (IOException ex) {
            System.out.println(Paths.get(Constants.MASTER_BRANCH));
        }
        
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
        String commmitContents = FileHasher.getFileContents(commitFilePath).toString();
        System.out.println(commmitContents);
        System.out.println("-------------------------------------------");
        String lineSeparator = System.lineSeparator();
        String previousCommit = commmitContents.split(lineSeparator)[Constants.PREVIOUS_COMMIT_HASH_LINE].split(":")[1];
        if (!previousCommit.equals("null")) {
            printCommits(previousCommit, repoPath);
        }
        
    }
}
