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
import Objects.Blob;
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

    public Path getIndexFilePath() {
        return this.repoPath.resolve(Constants.RELATIVE_INDEXFILE_PATH);
    }

    public Path getRepoPath() {
        return repoPath;
    }

    private void populateIndex() throws IOException {
        List<String> lines = new ArrayList<>(Files.readAllLines(this.getIndexFilePath(), StandardCharsets.UTF_8));

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

    public void commitStagedHashes() {
        for (IndexElement entry : this.index.values()) {
            entry.commit();
        }
        try {
            this.flushToStore();
        } catch (IOException e) {
            System.err.println("Could not save index file");
        }
    }

    public boolean hasStagedChanges() {
        for (IndexElement element : this.index.values()) {
            if (element.isStaged()) {
                return true;
            }
        }
        return false;
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
