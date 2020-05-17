package vcs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static vcs.Constants.REGISTER_LOCATION;

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
    private final FileHasher fileHasher;
    private final Path location;
    private boolean init;

    private Repository(String currentDirectory) {
        this.location = Paths.get(currentDirectory);
        this.fileHasher = new FileHasher(Paths.get(currentDirectory));
    }

    public static Repository getRepo(String currentDirectory) {
        Path currentDirectoryPath = Paths.get(currentDirectory);
        while (currentDirectoryPath != null) {
            if (Files.isDirectory(Paths.get(currentDirectoryPath.toString(), Constants.VCS_FOLDER))) {
                return new Repository(currentDirectoryPath.toString());
            }
            currentDirectoryPath = currentDirectoryPath.getParent();
        }
        return null;

    }

    public void stage(Path paths[]) {
        for(Path path:paths){
            while(path.equals(this.location)){
                if(Files.isRegularFile(path)){
                    String fileHash = fileHasher.hashFileandSave(path);
                        this.recordToIndex(path,fileHash,1);
                }
//                else if(Files.isDirectory(path)){
//                    
//                }
                
                
                path = path.getParent();
            }
        }

    }
    
    public void recordToIndex(Path path , String hash , int type){
        //save the hash to the index
        //to be implemented
    }

    public static Repository init(String currentDirectory) {
        Repository repo = new Repository(currentDirectory);
        File vcsRoot = new File(currentDirectory + "\\" + ".vcs");
        boolean success = false;
        if (vcsRoot.mkdir()) {
            try {
                success = new File(currentDirectory + "\\" + ".vcs" + "\\objects").mkdir();
                success = new File(currentDirectory + "\\" + ".vcs" + "\\commits").mkdir();
                success = new File(currentDirectory + "\\" + ".vcs" + "\\tracked.vcs").createNewFile();
                success = repo.registerRepository();
                if (success) {
                    repo.init = true;
                    return repo;
                } else {
                    repo.cleanup();
                }

            } catch (IOException e) {
                System.out.println("Could not complete the task");
            }
        } else {
            System.out.println("Something went wrong");
        }
        return null;
    }

    private boolean registerRepository() throws IOException {
        FileWriter registerWriter = null;
        try {
            registerWriter = new FileWriter(REGISTER_LOCATION, true);

            registerWriter.write(this.location + System.lineSeparator());
            registerWriter.close();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (registerWriter != null) {
                registerWriter.close();
            }

        }
    }

    private void cleanup() {
        //revert any changes made
    }

    @Override
    public String toString() {
        return this.location.toString();
    }

}
