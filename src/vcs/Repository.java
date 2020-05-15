package vcs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Deepanshu Vangani
 */

public class Repository {
    private static final String REGISTER_LOCATION = "D:\\versionControl\\repos.vcs";

    private String location;
    private boolean init;
    
    private Repository(String currentDirectory){
        this.location = currentDirectory;
    }
    
    public static Repository init(String currentDirectory){
        Repository repo = new Repository(currentDirectory);
        File vcsRoot = new File(currentDirectory + "\\" +".vcs");
        boolean success =false;
        
        if(!vcsRoot.exists()){
            if(vcsRoot.mkdir()){
                try{
                    success = new File(currentDirectory + "\\" +".vcs" + "\\objects").mkdir();
                    success = new File(currentDirectory + "\\" +".vcs" + "\\commits").mkdir();
                    success = new File(currentDirectory + "\\" +".vcs" + "\\tracked.vcs").createNewFile();
                    success = repo.registerRepository();
                    if(success){
                        repo.init = true;
                        return repo;
                    }else{
                        repo.cleanup();
                    }
                    
                }catch(IOException e){
                    System.out.println("Could not complete the task");
                }
            }else{
                System.out.println("Something went wrong");
            }
        }else{
            System.out.println("A repository already exists");
        }
        return null;
    }
    
    private boolean registerRepository(){
        
        try{
            FileWriter registerWriter = new FileWriter(REGISTER_LOCATION,true);
            
            registerWriter.write(this.location+System.lineSeparator());
            registerWriter.close();
            return true;
        }catch(IOException e){
            return false;
        }
    }
    
    private void cleanup(){
        //revert any changes made
    }
}
