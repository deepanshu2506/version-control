/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vcs.Constants;

/**
 *
 * @author Deepanshu Vangani
 */
public class User {

    public static String username;
    public static String email;

    static {
        try {
            List<String> attributes = Files.readAllLines(Paths.get(Constants.GLOBAL_USER_CONFIG));
            attributes.forEach(attribute -> {
                String[] parts = attribute.split(":");
                if (parts[0].equals("username")) {
                    username = parts[1];
                } else if (parts[0].equals("email")) {
                    email = parts[1];
                }
            });
        } catch (IOException ex) {
            System.out.println("could not get the user info");
        }
    }

    public static void configure(String[] args) {
        try {
            setProperty(args[0], args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Specify the proper options");
        }

    }

    private static void setProperty(String key, String value) {
        String[] validProps = {"username", "email"};
        if (Arrays.asList(validProps).contains(key)) {
            if (key.equals("username")) {
                username = value;
            } else if (key.equals("email")) {
                email = value;
            }
            persistDetails();
        } else {
            System.err.println("invalid parameter " + key);
        }
    }

    private static void persistDetails() {
        String lineSeparator = System.lineSeparator();
        StringBuilder details = new StringBuilder();
        if (username != null) {
            details.append("username:" + username);
            details.append(lineSeparator);
        }
        if (email != null) {
            details.append("email:" + email);
        }
        try {
            Files.write(Paths.get(Constants.GLOBAL_USER_CONFIG), details.toString().getBytes());
        } catch (IOException ex) {
            System.err.println("Could not configure the credentials ");
        }
    }
    
    public static boolean exists(){
        return username != null && email != null;
    }
}
