/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects.Tree;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Deepanshu Vangani
 */
public enum ChildTypes {
    TREE("tree"),BLOB("blob");
    private String action; 
  
    public String getAction() 
    { 
        return this.action; 
    }
    private static final Map<String, ChildTypes> lookup = new HashMap<>();
  
    //Populate the lookup table on loading time
    static
    {
        for(ChildTypes type : ChildTypes.values())
        {
            lookup.put(type.getAction(), type);
        }
    }
    
    public static ChildTypes get(String type) 
    {
        return lookup.get(type);
    }
  
  
    private ChildTypes(String action) 
    { 
        this.action = action; 
    } 
}
