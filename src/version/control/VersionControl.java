/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package version.control;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.nio.file.WatchKey;

/**
 *
 * @author Deepanshu Vangani
 */
public class VersionControl {

    /**
     * @param args the command line arguments
     */
    private static final String REPOPATHS_LOCATION = "D:\\versionControl\\repos.vcs";
    public static void main(String[] args)throws IOException {
        Service service = Service.createServiceInstance(REPOPATHS_LOCATION);
    }
    
}
