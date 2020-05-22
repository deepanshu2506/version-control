/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Object.commit.Commit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import vcs.Constants;

/**
 *
 * @author Deepanshu Vangani
 */
public class Branch {

    private String name;
    private String commitId;
    private final Path branchFilePath;

    private Branch(String name, Path branchFilePath, String commitId) {
        this.name = name;
        this.commitId = commitId;
        this.branchFilePath = branchFilePath;
    }

    public String getCommitId() {
        return commitId;
    }

    public Path getBranchFilePath() {
        return branchFilePath;
    }

    public String getName() {
        return name;
    }

    private static String getCurrentBranchName(Path repoPath) throws IOException {
        Path configFile = repoPath.resolve(Constants.CONFIG_FILE);
        String currentBranchName = Files.readAllLines(configFile).get(Constants.BRANCH_LINE_IN_CONFIG).split(":")[1];
        return currentBranchName;
    }

    public static Branch getCurrentBranch(Path repoPath) {
        Branch currentBranch = null;
        try {
            String currentBranchName = getCurrentBranchName(repoPath);
            Path branchFilePath = repoPath.resolve(Constants.BRANCHES_DIR + "\\" + currentBranchName);
            String commitId = null;
            try {
                commitId = Files.readAllLines(branchFilePath).get(0);
            } catch (IndexOutOfBoundsException e) {
            }

            currentBranch = new Branch(currentBranchName, branchFilePath, commitId);
        } catch (IOException ex) {
            System.err.println("could not get current branch");
        }
        return currentBranch;
    }

    public static void displayAllBranches(Path repoPath) throws IOException {
        String currentBranchName = getCurrentBranchName(repoPath);
        Path branchesDirPath = repoPath.resolve(Constants.BRANCHES_DIR);
        Files.walk(branchesDirPath)
                .filter(path -> !path.equals(branchesDirPath))
                .forEach((path) -> {
                    String branchName = path.getFileName().toString();
                    if (branchName.equals(currentBranchName)) {
                        System.out.println("-> " + branchName);
                    } else {
                        System.out.println(branchName);
                    }
                });

    }

    public void registerToBranch(Commit newCommit) {
        try {
            Files.write(this.branchFilePath, newCommit.getHash().getBytes());
        } catch (IOException ex) {
            System.out.println(Paths.get(Constants.MASTER_BRANCH));
        }
    }

}
