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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Deepanshu Vangani
 */
public class RepositoryIndex {
    private final static String RELATIVE_INDEXFILE_PATH = ".vcs/tracked.vcs";
    private final Path repoPath;
    private final Map<String , IndexElement> index;
    private RepositoryIndex(Path repoPath){
        this.repoPath = repoPath;
        this.index = new HashMap<>();
        
    }
    
    public static RepositoryIndex createIndex(Path repoPath) throws IOException{
        RepositoryIndex index = new RepositoryIndex(repoPath);
        List<String> lines = new ArrayList<>(Files.readAllLines(index.repoPath.resolve(RELATIVE_INDEXFILE_PATH), StandardCharsets.UTF_8));
        
        lines.forEach((String line) -> {
            String[] words = line.split(",");
            IndexElement indexElement = new IndexElement(words[0]);
            indexElement.createExistingElement(Long.parseLong(words[1]), 
                    Boolean.parseBoolean(words[2]), 
                    Boolean.parseBoolean(words[3]), 
                    words[4], words[5],
                    Boolean.parseBoolean(words[6]));
            index.addEntry(indexElement);
        });
        return index;
    }
    public void addEntry(IndexElement item){
        this.index.put(item.getFilePath() ,item);
    }
    
    public IndexElement findByPath(String relativePath){
        IndexElement foundElement = index.get(relativePath);
        return foundElement;
    }
    
    public void flushToStore() throws IOException{
        List<String> lines = this.index.values().stream()
                .map((tuple) -> tuple.toString())
                .collect(Collectors.toList());

        Files.write(this.repoPath.resolve(RELATIVE_INDEXFILE_PATH), lines, StandardCharsets.UTF_8);
    }
}
