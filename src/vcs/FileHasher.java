package vcs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Deepanshu Vangani
 */
public class FileHasher {

    private final Path repoPath;

    public FileHasher(Path repoPath) {
        this.repoPath = repoPath;
    }

    public String hashFileandSave(Path path) {
        String fileContents = this.createBlobObject(path);
        String hash = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("sha-1");
            byte[] hashByteArray = messageDigest.digest(fileContents.getBytes());
            hash = new BigInteger(1, hashByteArray).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Algorithm sha-1 not found");
        }
        saveHashToDisk(fileContents, hash);
        return hash;
    }

    private String createBlobObject(Path path) {
        StringBuilder fileContents = this.getFileContents(path);
        fileContents.insert(0, fileContents.length() + " ");
        fileContents.insert(0, "blob");
        return fileContents.toString();
    }

    private StringBuilder getFileContents(Path path) {
        StringBuilder fileContents = new StringBuilder();
        BufferedReader fileReader = null;
        try {
            fileReader = Files.newBufferedReader(path);
            String line = null;
            String lineSeparator = System.getProperty("line.separator");
            while ((line = fileReader.readLine()) != null) {
                fileContents.append(line);
                fileContents.append(lineSeparator);
            }

        } catch (IOException ex) {
            System.out.println("cannot open the buffered reader stream");
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException ex) {
                    System.out.println(FileHasher.class.getName() + " cannot close file");
                }
            }
        }
        return fileContents;
    }

    private void saveHashToDisk(String fileContents, String hash) {
        Path objectsDirectoryPath = this.repoPath.resolve(Constants.VCS_OBJECTS);
        String bucket = hash.substring(0, 2);
        Path bucketPath = objectsDirectoryPath.resolve(bucket);
        if (!Files.isDirectory(bucketPath)) {
            try {
                Files.createDirectories(bucketPath);
            } catch (IOException ex) {
                System.out.println("could not create bucket");
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(objectsDirectoryPath.resolve(hash.substring(2)), Charset.forName("UTF-8"))) {
            writer.write(fileContents);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
