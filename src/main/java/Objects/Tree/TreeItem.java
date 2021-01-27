/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects.Tree;

/**
 *
 * @author Deepanshu Vangani
 */

public class TreeItem {
    private final ChildTypes childType;
    private String childHash;
    private final String childName;
    TreeItem(ChildTypes childType ,String childHash,String childName ){
        this.childHash = childHash;
        this.childName = childName;
        this.childType = childType;
    }
    TreeItem(String logEntry){
        String[] items = logEntry.split(",");
        this.childType = ChildTypes.get(items[0]);
        this.childHash = items[1];
        this.childName = items[2];
    }

    @Override
    public String toString() {
        StringBuilder serialStringSb = new StringBuilder();
        serialStringSb.append(this.childType.getAction() + ",");
        serialStringSb.append(this.childHash + ",");
        serialStringSb.append(this.childName);
        return serialStringSb.toString();
    }

    public ChildTypes getChildType() {
        return childType;
    }

    public String getChildHash() {
        return childHash;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildHash(String childHash) {
        this.childHash = childHash;
    }
    
    
    
}
