/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package version.control;

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
    private String latestStagedHash;
    private String lastCommitHash;

    public IndexElement(String filePath) {
        this.filePath = filePath;
        this.timeStamp = new Date().getTime();
        this.isStaged = false;
        this.isDirectory = false;
        this.latestStagedHash = "null";
        this.lastCommitHash = "null";
    }

    public IndexElement createExistingElement(Long timeStamp, boolean isDirectory, boolean isStaged, String latestCommitHash, String latestStagedHash) {
        this.timeStamp = timeStamp;
        this.isDirectory = isDirectory;
        this.isStaged = isStaged;
        this.lastCommitHash = lastCommitHash != "null" ? latestCommitHash : null;
        this.latestStagedHash = latestStagedHash != "null" ? latestStagedHash : null;
        return this;
    }

    public void setAsDirectory() {
        this.isDirectory = true;
    }

    public void stage() {
        this.isStaged = true;
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

    @Override
    public String toString() {
        return this.filePath + "," + this.timeStamp + "," + Boolean.toString(this.isDirectory) + "," + Boolean.toString(isStaged) + "," + this.lastCommitHash + "," + this.latestStagedHash;
    }

}
