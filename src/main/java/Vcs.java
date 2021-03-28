
import Object.commit.Commit;
import Objects.Branch;
import Objects.User;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class Vcs {

    public static void main(String[] args) throws IOException {
        File repoList = new File("D:/vcs/repos.vcs");
        String currentDirectory = System.getProperty("user.dir");
        Repository repo = Repository.getRepo(currentDirectory);

        if (args[0] != null) {
            if (args[0].equals("init")) {
                if (repo == null) {
                    repo = Repository.init(currentDirectory);
                } else {
                    System.out.println("A repository already exists.");
                }
            }
            if (repo != null) {
                if (args[0].equals("add")) {

                    if (args.length > 1) {
                        if (args[1].equals(".")) {
                            repo.stageContents(Paths.get(currentDirectory));
                        } else {
                            List<Path> paths = new ArrayList<>();
                            for (int i = 1; i < args.length; i++) {
                                paths.add(Paths.get(currentDirectory, args[i]));
                            }
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

                } else if (args[0].equals("branch")) {
                    System.out.println("Branches in repository are:");
                    Branch.displayAllBranches(repo.getRepoPath());

                } else if (args[0].equals("status")) {
                    repo.status();
                } else if (args[0].equals("checkout")) {
                    repo.switchBranch(args[1]);
                }
                
                else if(args[0].equals("rollback")){
                    int n = Integer.parseInt(args[1]);
                    repo.rollback(n);
                }
            } else {
                System.out.println("Repository does not exist");
            }
        } else {
            System.out.println("please specify a command to execute");
        }
    }
}
