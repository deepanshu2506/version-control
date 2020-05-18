package vcs;

import Objects.Blob.Blob;
import Objects.Tree.Tree;
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

    public static String hashFile(String fileContents) {
        String hash = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("sha-1");
            byte[] hashByteArray = messageDigest.digest(fileContents.getBytes());
            hash = new BigInteger(1, hashByteArray).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Algorithm sha-1 not found");
        }
//        saveHashToDisk(fileContents, hash);
        return hash;
    }

    public static StringBuilder getFileContents(Path path) {
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

    public static void saveHashToDisk(Blob object, Path repoPath) {
        Path objectsDirectoryPath = repoPath.resolve(Constants.VCS_OBJECTS);
        String hash = object.getHash();
        String bucket = hash.substring(0, 2);
        Path bucketPath = objectsDirectoryPath.resolve(bucket);
        if (!Files.isDirectory(bucketPath)) {
            try {
                bucketPath = Files.createDirectories(bucketPath);
            } catch (IOException ex) {
                System.out.println("could not create bucket");
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(bucketPath.resolve(hash.substring(2)), Charset.forName("UTF-8"))) {
            writer.write(object.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void saveHashToDisk(Tree object, Path repoPath) {
        Path objectsDirectoryPath = repoPath.resolve(Constants.VCS_OBJECTS);
        String hash = object.getHash();
        String bucket = hash.substring(0, 2);
        Path bucketPath = objectsDirectoryPath.resolve(bucket);
        if (!Files.isDirectory(bucketPath)) {
            try {
                bucketPath = Files.createDirectories(bucketPath);
            } catch (IOException ex) {
                System.out.println("could not create bucket");
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(bucketPath.resolve(hash.substring(2)), Charset.forName("UTF-8"))) {
            writer.write(object.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
