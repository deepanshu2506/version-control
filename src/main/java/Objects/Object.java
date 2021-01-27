/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.nio.file.Path;

/**
 *
 * @author Deepanshu Vangani
 */
public class Object {

    private Path filePath;
    protected String hash;

    /**
     *
     * @param filePath path to the file or directory to be referenced
     *
     * constructor to initialize the Object with only the filePath when the hash
     * hasn't yet been generated
     */
    public Object(Path filePath) {
        this.filePath = filePath;
    }
    
    public Object(){
    }

    /**
     *
     * @param filePath path of the file or directory to be referenced
     * @param hash hash of the contents of the file
     */
    public Object(Path filePath, String hash) {
        this.filePath = filePath;
        this.hash = hash;
    }

    /**
     *
     * @return returns the filePath of the object being referenced
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     *
     * @param filePath path of the file or directory to be referenced
     */
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    /**
     *
     * @return the SHA-1 hash of the file
     */
    public String getHash() {
        return hash;
    }

    /**
     *
     * @param hash the SHA-1 hash of the serialized object
     */
    public void setHash(String hash) {
        this.hash = hash;
    }
}
