
import Object.commit.Commit;
import Objects.User;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.Arrays;
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

    public static void main(String[] args) throws IOException {
        File repoList = new File("D:/vcs/repos.vcs");
        String currentDirectory = System.getProperty("user.dir");
        Repository repo = Repository.getRepo(currentDirectory);
        if (args[0] != null) {
            if (args[0].equals("init")) {
                if (repo != null) {
                    repo = Repository.init(currentDirectory);
                } else {
                    System.out.println("A repository already exists.");
                }
            }
            if (repo != null) {
                if (args[0].equals("add")) {
                    if (args.length > 1) {
                        Path[] paths = new Path[args.length - 1];

                        for (int i = 1; i < args.length; i++) {
                            paths[i - 1] = Paths.get(currentDirectory, args[i]);
                            repo.stage(paths);
                        }
                    } else {
                        System.out.println("Usage , add [file names | . ]");
                    }
                } else if (args[0].equals("commit")) {
                    if (args.length > 1) {
                        repo.commit(args[1]);
                    } else {
                        System.out.println("Commit Message Not Provided");
                    }
                } else if (args[0].equals("config")) {
                    User.configure(Arrays.copyOfRange(args, 1, args.length));
                } else if (args[0].equals("log")) {
                    Commit.logCommits(repo.getRepoPath());
                }
            } else {
                System.out.println("Repository does not exist");
            }
        } else {
            System.out.println("please specify a command to execute");
        }
    }
}
