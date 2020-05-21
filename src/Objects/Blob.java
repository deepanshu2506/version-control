package Objects;

import java.nio.file.Path;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import vcs.FileHasher;
import Objects.Object;
/**
 *
 * @author Deepanshu Vangani
 */

public class Blob extends Objects.Object{
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
        fileContent.insert(0, this.contents.length()+" ");
        fileContent.insert(0, "blob ");
        this.hash= FileHasher.hashFile(fileContent.toString());
    }
    
    public static Blob createBlobObject(Path filePath){
        Blob blob = new Blob(filePath);
        String fileContents = FileHasher.getFileContents(filePath).toString();
        blob.setContents(fileContents);
        return blob;
    }

    @Override
    public String toString() {
        return "blob " + this.contents.length()+ " " + this.contents;
    }
    
}
