/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Comparator;

/**
 *
 * @author Deepanshu Vangani
 */
class PathComparator implements Comparator<String> {

    public int compare(String path1, String path2) {
        String[] dirs1 = path1.split("/");
        String[] dirs2 = path2.split("/");
        if (dirs1.length < dirs2.length) {
            return -1;
        } else if (dirs1.length > dirs2.length) {
            return 1;
        } else {
            String lastDir1 = dirs1[dirs1.length - 1];
            String lastDir2 = dirs2[dirs2.length - 1];
            return lastDir1.compareTo(lastDir2);
        }
    }
}
