/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package version.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Deepanshu Vangani
 */
public class FileEvents {

    private final Path repoPath;
    private final RepositoryIndex repositoryIndex;

    public FileEvents(Path repoPath, RepositoryIndex repositoryIndex) {
        this.repoPath = repoPath;
        this.repositoryIndex = repositoryIndex;
    }

    public void modifyEvent(Path fileChanged) throws IOException {
        boolean changes = false;
        while (!fileChanged.equals(this.repoPath)) {
            String relativeFilePath = fileChanged.toString().substring(this.repoPath.toString().length());
            if (!relativeFilePath.endsWith("tracked.vcs")) {
                IndexElement indexElement = this.repositoryIndex.findByPath(relativeFilePath);
                if (indexElement == null || !indexElement.isModified()) {
                    changes = true;
                }
                if (indexElement == null) {
                    IndexElement newElement = new IndexElement(relativeFilePath);
                    if (Files.isDirectory(fileChanged)) {
                        newElement.setAsDirectory();
                    }
                    this.repositoryIndex.addEntry(newElement);
                } else {
                    indexElement.setAsModified();
                }
            } else {
                break;
            }
            fileChanged = fileChanged.getParent();
        }
        if (changes) {
            this.repositoryIndex.flushToStore();
        }

    }
}
