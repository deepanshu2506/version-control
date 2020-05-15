/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package version.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Deepanshu Vangani
 */
public class WatcherGenerator extends TimerTask {
    private final Map<Path,WatchDir> repositories;
    private final File repoPathsFile;
    private final BufferedReader repoPathsFileReader;
    
    public WatcherGenerator(Map<Path,WatchDir> repositories , String repoPathsFilePath){
        this.repoPathsFile = new File(repoPathsFilePath);
        this.repositories = repositories;
        FileReader fileReader= null;
        try {
            fileReader = new FileReader(this.repoPathsFile);
        } catch (FileNotFoundException ex) {
            System.err.println("Could not open file");
        }
        this.repoPathsFileReader = new BufferedReader(fileReader);
    }  
    @Override
    public void run(){
        System.out.println("Scanning");
        String repoPath;
        try {
            while((repoPath = repoPathsFileReader.readLine())!= null){
                
                Path repo = Paths.get(repoPath);
                if(!repositories.containsKey(repo)){
                    WatchDir watcher = new WatchDir(repo ,true);
                    repositories.put(repo,watcher);
                    System.out.println(repoPath);
                }
            }
           
        } catch (IOException ex) {
            
        }
    }
}
