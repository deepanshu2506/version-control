/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.util.Date;

/**
 *
 * @author Deepanshu Vangani
 */
public class IndexElement {

    private String filePath;
    private Boolean isDirectory;
    private Long timeStamp;
    private boolean isStaged;
    private boolean isModified;
    private String latestStagedHash;
    private String lastCommitHash;
    private Boolean isDeleted;

    public IndexElement(String filePath) {
        this.filePath = filePath;
        this.timeStamp = new Date().getTime();
        this.isStaged = false;
        this.isDirectory = false;
        this.latestStagedHash = "null";
        this.lastCommitHash = "null";
        this.isModified = true;
        this.isDeleted = false;
    }

    public IndexElement createExistingElement(Long timeStamp, boolean isDirectory, boolean isStaged, String latestCommitHash, String latestStagedHash, Boolean isModified, Boolean isDeleted) {
        this.timeStamp = timeStamp;
        this.isDirectory = isDirectory;
        this.isStaged = isStaged;
        if (!latestCommitHash.equals("null")) {
            this.lastCommitHash = latestCommitHash;
        }
        if (!latestStagedHash.equals("null")) {
            this.latestStagedHash = latestStagedHash;
        }
        this.isModified = isModified;
        this.isDeleted = isDeleted;
        return this;
    }

    public void setAsDirectory() {
        this.isDirectory = true;
    }

    public void setAsModified() {
        this.isModified = true;
        this.refreshTimeStamp();
    }

    public void clearModified() {
        this.isModified = false;
        this.refreshTimeStamp();
    }

    public Boolean isModified() {
        return this.isModified;
    }

    public Boolean isStaged() {
        return this.isStaged;
    }

    public Boolean isDirectory() {
        return this.isDirectory;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void stage() {
        this.isStaged = true;
        this.refreshTimeStamp();
    }

    public void refreshTimeStamp() {
        this.timeStamp = new Date().getTime();
    }

    public String getFilePath() {
        return filePath;
    }

    public String getLatestStagedHash() {
        return latestStagedHash;
    }

    public void setLatestStagedHash(String latestStagedHash) {
        this.latestStagedHash = latestStagedHash;
    }

    public String getLastCommitHash() {
        return lastCommitHash;
    }

    public void setLastCommitHash(String lastCommitHash) {
        this.lastCommitHash = lastCommitHash;
    }

    public void commit() {
        if (this.isStaged()) {
            this.setLastCommitHash(this.getLatestStagedHash());
            this.setLatestStagedHash(null);
            this.isStaged = false;
        }
    }

    @Override
    public String toString() {
        return this.filePath + "," + this.timeStamp + "," + this.isDirectory + "," + isStaged + "," + this.lastCommitHash + "," + this.latestStagedHash + "," + this.isModified + "," + this.isDeleted;
    }

}
