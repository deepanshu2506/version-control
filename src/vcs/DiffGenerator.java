/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vcs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import difflib.DiffUtils;
import difflib.Patch;

/**
 *
 * @author Deepanshu Vangani
 */
import difflib.Delta;
import index.IndexElement;
import index.RepositoryIndex;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DiffGenerator {

    public static List<Delta<String>> getIndexDiff(RepositoryIndex currentIndex, RepositoryIndex newIndex) {
        List<String> original = currentIndex.getIndexEntries().stream().map(IndexElement::toString).collect(Collectors.toList());
        List<String> revised = newIndex.getIndexEntries().stream().map(IndexElement::toString).collect(Collectors.toList());
        Patch<String> patch = DiffUtils.diff(original, revised);
        return patch.getDeltas();
    }
}
