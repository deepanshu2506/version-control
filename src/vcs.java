
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import vcs.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Deepanshu Vangani
 */
public class vcs {
    
    public static void main(String[] args){
        File repoList = new File("D:/vcs/repos.vcs");
        String currentDirectory = System.getProperty("user.dir");
        if(args[0] != null){
            if(args[0].equals("init")){
                Repository repo =  Repository.init(currentDirectory);
            }
            if(args[0].equals("add") ){
                if(args.length > 1){
                    
                }else{
                    System.out.println("Usage , add [file names | . ]");
                }
            }
        }else{
            System.out.println("please specify a command to execute");
        }
    }
}
