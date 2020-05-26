/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import vcs.Constants;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import Objects.Blob;
import difflib.Delta;
import difflib.Delta.TYPE;
import java.util.Arrays;
import vcs.DiffGenerator;

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

    public List<IndexElement> getIndexEntries() {
        return new ArrayList(this.index.values());
    }

    public Path getRepoPath() {
        return repoPath;
    }

    private List<String> getIndexFileContents() throws IOException {
        return new ArrayList<>(Files.readAllLines(this.getIndexFilePath(), StandardCharsets.UTF_8));
    }

    private List<String> getIndexFileContents(String indexHash) throws IOException {
        Blob indexFileBlob = Blob.getBlobFromHash(this.getRepoPath(), indexHash);
        List<String> indexFileContents = Arrays.asList(indexFileBlob.getContents().split(System.lineSeparator()));
        return indexFileContents;
    }

    private void populateIndex(List<String> entries) throws IOException {
        System.out.println("-----------------------------");
        entries.forEach((String line) -> {
            String[] words = line.split(",");
            IndexElement indexElement = new IndexElement(words[0]);
            indexElement.createExistingElement(Long.parseLong(words[1]),
                    Boolean.parseBoolean(words[2]),
                    Boolean.parseBoolean(words[3]),
                    words[4], words[5],
                    Boolean.parseBoolean(words[6]),
                    Boolean.parseBoolean(words[7]));
            this.addEntry(indexElement);
        });
    }

    public static RepositoryIndex createIndex(Path repoPath) throws IOException {
        RepositoryIndex index = new RepositoryIndex(repoPath);
        List<String> entries = index.getIndexFileContents();
        index.populateIndex(entries);
        return index;
    }

    public static RepositoryIndex getCommitIndex(Path repoPath, String indexHash) {
        RepositoryIndex index = new RepositoryIndex(repoPath);
        try {
            List<String> entries = index.getIndexFileContents(indexHash);
            index.populateIndex(entries);
            return index;
        } catch (IOException ex) {
            System.err.println("Could not read index file from index object : " + indexHash);
            return null;
        }
    }

    public void addEntry(IndexElement item) {
        this.index.put(item.getFilePath(), item);
    }

    public void removeEntry(String filePath) {
        this.index.remove(filePath);
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

    public boolean hasUnstagedDeletedChanges() {
        for (IndexElement element : this.index.values()) {
            if (element.isDeleted()) {
                if (!element.isStaged()) {
                    return true;
                }
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
            List<String> entries = this.getIndexFileContents();
            this.populateIndex(entries);
        } catch (IOException ex) {
            System.out.println("Could not update index");
        }
    }

    public void showStagedFiles() {
        boolean flag = false;
        for (IndexElement element : this.index.values()) {
            if (element.isStaged()) {
                System.out.println("staged: " + element.getFilePath());
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("No Files Staged");
        }
    }

    public void showModifiedFiles() {
        for (IndexElement element : this.index.values()) {
            if (element.isModified()) {
                if (element.isDeleted()) {
                    System.out.println("deleted: " + element.getFilePath());
                } else {
                    System.out.println("modified: " + element.getFilePath());
                }
            }
        }
    }

    public void resolveChanges(RepositoryIndex newIndex) throws IOException {
        List<Delta<String>> deltas = DiffGenerator.getIndexDiff(newIndex, newIndex);
        deltas.forEach(delta -> {
            TYPE deltaType = delta.getType();

            if (deltaType == TYPE.INSERT) {
                List<String> newIndexEntries = delta.getRevised().getLines();
                newIndexEntries.forEach(indexEntry -> {
                    String pathString = indexEntry.split(",")[0];

                });
            }
        });
    }

}
