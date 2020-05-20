/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import index.IndexElement;
import vcs.Constants;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.Date;
import Objects.Blob.Blob;
import java.nio.file.FileAlreadyExistsException;
import vcs.FileHasher;

/**
 *
 * @author Deepanshu Vangani
 */
public class RepositoryIndex {

    private final Path repoPath;
    private final Map<String, IndexElement> index;

    private RepositoryIndex(Path repoPath) {
        this.repoPath = repoPath;
        this.index = new HashMap<>();

    }

    private void populateIndex() throws IOException {
        List<String> lines = new ArrayList<>(Files.readAllLines(this.repoPath.resolve(Constants.RELATIVE_INDEXFILE_PATH), StandardCharsets.UTF_8));

        lines.forEach((String line) -> {
            String[] words = line.split(",");
            IndexElement indexElement = new IndexElement(words[0]);
            indexElement.createExistingElement(Long.parseLong(words[1]),
                    Boolean.parseBoolean(words[2]),
                    Boolean.parseBoolean(words[3]),
                    words[4], words[5],
                    Boolean.parseBoolean(words[6]));
            this.addEntry(indexElement);
        });
    }

    public static RepositoryIndex createIndex(Path repoPath) throws IOException {
        RepositoryIndex index = new RepositoryIndex(repoPath);
        index.populateIndex();
        return index;
    }

    public void addEntry(IndexElement item) {
        this.index.put(item.getFilePath(), item);
    }

    public IndexElement findByPath(String relativePath) {
        IndexElement foundElement = index.get(relativePath);
        return foundElement;
    }

    public String getLatestHash(String relativePath) {
        IndexElement element = findByPath(relativePath);
        if (element.getLastCommitHash() == null && element.getLatestStagedHash() == null) {
            return null;
        }

        if (element.getLatestStagedHash() != null) {
            return element.getLatestStagedHash();
        } else {
            return element.getLastCommitHash();
        }

    }

    public void commitChanges(String commitMessage) throws IOException {
        for (IndexElement entry : this.index.values()) {
            entry.commit();
        }
        this.flushToStore();
        Path indexFilePath = this.repoPath.resolve(Constants.RELATIVE_INDEXFILE_PATH);
        Blob indexFileBlob = Blob.createBlobObject(indexFilePath);
        FileHasher.saveHashToDisk(indexFileBlob, repoPath);
        Date dateTimeObject = new Date();
        try {
            String commitFileContent = indexFileBlob.getHash() + ", " + commitMessage + ", " + dateTimeObject.toString();
            String commitFileName = FileHasher.hashFile(commitFileContent);
            Path commitFilePath = this.repoPath.resolve(Constants.VCS_COMMIT + "\\" + commitFileName);
            this.flushToStore(commitFilePath, commitFileContent);
        } catch (IOException e) {
            System.err.print("Error");
        }
    }
    
    private void clearIndex() {
        this.index.clear();
    }

    public void flushToStore() throws IOException {
        List<String> lines = this.index.values().stream()
                .map((tuple) -> tuple.toString())
                .collect(Collectors.toList());

        Files.write(this.repoPath.resolve(Constants.RELATIVE_INDEXFILE_PATH), lines, StandardCharsets.UTF_8);
    }
    
    public void flushToStore(Path filePath,String contents) throws IOException {
        if(!Files.exists(filePath)){
            Files.createFile(filePath);
        }
        Files.write(filePath, contents.getBytes());
    }
    

    public void refresh() {
        //future implementation calculate the diff of the files and change only the updated entry.
        try {
            this.clearIndex();
            this.populateIndex();
        } catch (IOException ex) {
            System.out.println("Could not update index");
        }
    }
}
