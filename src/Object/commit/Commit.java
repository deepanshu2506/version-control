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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import vcs.FileHasher;

/**
 *
 * @author Deepanshu Vangani
 */
public class Commit extends Objects.Object {
    private User user;
    private Date timeStamp;
    private RepositoryIndex indexSnapshot;
    private String commitMessage;
    private String indexFileBlobHash;
    
    private Commit(Path filePath , RepositoryIndex indexSnapShot){
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
    
    public static Commit createCommit(RepositoryIndex indexSnapshot){
        Commit commit = new Commit(indexSnapshot.getIndexFilePath(), indexSnapshot);
        commit.indexSnapshot.commitStagedHashes();
        Path indexFilePath = commit.indexSnapshot.getIndexFilePath();
        Blob commitBlob = Blob.createBlobObject(indexFilePath);
        commit.setIndexFileBlobHash(commitBlob.getHash());
        commit.setHash(FileHasher.hashFile(commit.toString()));
        FileHasher.saveHashToDisk(commitBlob, commit.indexSnapshot.getRepoPath());
        return commit;
    }
    
    public void saveCommit(){
        Path commitFilePath = this.indexSnapshot.getRepoPath().resolve(vcs.Constants.VCS_COMMIT + "\\" + this.getHash());
        try {
            this.flushCommit(commitFilePath, this.toString());
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
        return this.indexFileBlobHash + ","+this.commitMessage+","+this.timeStamp.toString();
    }



}
