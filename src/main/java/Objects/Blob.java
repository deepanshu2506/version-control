package Objects;

import java.nio.file.Path;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import vcs.FileUtils;
import vcs.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Optional;

/**
 *
 * @author Deepanshu Vangani
 */
public class Blob extends Objects.Object {

    private String contents;

    private Blob(Path filePath) {
        super(filePath);
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {

        this.contents = contents;
        StringBuilder fileContent = new StringBuilder();
        fileContent.insert(0, this.contents.length() + " ");
        fileContent.insert(0, "blob ");
        this.hash = FileUtils.hashFile(fileContent.toString());
    }

    public static Blob createBlobObject(Path filePath) {
        Blob blob = new Blob(filePath);
        String fileContents = FileUtils.getFileContents(filePath).toString();
        blob.setContents(fileContents);
        return blob;
    }

    public static Blob getBlobFromHash(Path repoPath, String hash) throws IOException {
        Path indexFilePath = repoPath.resolve(Constants.RELATIVE_INDEXFILE_PATH);
        Blob blob = new Blob(indexFilePath);

        Path objectFile = blob.getHashFile(repoPath, hash);
        blob.buildFromHash(objectFile);
        return blob;
    }

    private void buildFromHash(Path objectFile) {
        StringBuilder blobContent = FileUtils.getFileContents(objectFile);
        String[] blobObjectContentsArray = blobContent.toString().split(" ", Constants.BLOB_FIELDS);
        this.setContents(blobObjectContentsArray[2]);
    }

    private Path getHashFile(Path repoPath, String hash) throws IOException {
        Path objectsDirectory = repoPath.resolve(Constants.VCS_OBJECTS);
        Path bucketPath = objectsDirectory.resolve(hash.substring(0, 2));
        if (Files.exists(bucketPath) && Files.isDirectory(bucketPath)) {

            Optional<Path> treeObjectPathOptional = Files.list(bucketPath)
                    .filter(path -> {
                        return path.getFileName().toString().startsWith(hash.substring(2));
                    })
                    .findAny();
            Path treeObjectPath = treeObjectPathOptional != null ? treeObjectPathOptional.get() : null;

            return treeObjectPath;

        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "blob " + this.contents.length() + " " + this.contents;
    }

}
