/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package version.control;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Deepanshu Vangani
 */
public class RepositoryIndex {
    private final static String RELATIVE_INDEXFILE_PATH = ".vcs/tracked.vcs";
    private final Path repoPath;
    private final List<IndexElement> index;
    private RepositoryIndex(Path repoPath){
        this.repoPath = repoPath;
        this.index = new ArrayList<>();
        
    }
    
    public static RepositoryIndex createIndex(Path repoPath) throws IOException{
        RepositoryIndex index = new RepositoryIndex(repoPath);
        List<String> lines = new ArrayList<>(Files.readAllLines(index.repoPath.resolve(RELATIVE_INDEXFILE_PATH), StandardCharsets.UTF_8));
        
        lines.forEach((String line) -> {
            String[] words = line.split(",");
            IndexElement indexElement = new IndexElement(words[0]);
            indexElement.createExistingElement(Long.parseLong(words[1]), Boolean.parseBoolean(words[2]), Boolean.parseBoolean(words[3]), words[4], words[5]);
            index.addEntry(indexElement);
        });
        return index;
    }
    public void addEntry(IndexElement item){
        this.index.add(item);
    }
    
    public IndexElement findByPath(String relativePath){
        Optional<IndexElement> indexElement = this.index.stream().filter((indexItem) ->indexItem.getFilePath().equals(relativePath)).findAny();
        return indexElement.isPresent()?indexElement.get():null;
    }
    
    public void flushToStore() throws IOException{
        List<String> lines = this.index.stream()
                .map((tuple) -> tuple.toString())
                .collect(Collectors.toList());

        Files.write(this.repoPath.resolve(RELATIVE_INDEXFILE_PATH), lines, StandardCharsets.UTF_8);
    }
}
