package it.unipr.informatica.tirocin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeNode{
	private int[] coordinates;
	public String type;
	int index;
	public List<TreeNode> children;
	
	public TreeNode(int [] coordinates, String type, int index) {
		this.coordinates = coordinates;
		this.type=type;
		this.index=index;
		children = new ArrayList<>();
	}
	public int getIndex() {
		return this.index;
	}
	
	
	public void addChild(TreeNode child) {
		children.add(children.size(),child);
	}

	
	@Override
	public String toString() {
		return "x: "+coordinates[0]+" y: "+coordinates[1]+" ,type: "+type;
	}	
	
	
    public String stringHashStrict() {
        StringBuilder string = new StringBuilder();
        string.append('(').append(type.charAt(0));
        List<String> childrenStringHash = new ArrayList<>();
        for(TreeNode n : children) {
            childrenStringHash.add(n.stringHashStrict());
        }
        Collections.sort(childrenStringHash);
        for (String hash : childrenStringHash) {
            string.append(hash);
        }
        string.append(")");
        
        return string.toString();
    }
    

    public String stringHash() {
        StringBuilder string = new StringBuilder();
        string.append('(');
        List<String> childrenStringHash = new ArrayList<>();
        for(TreeNode n : children) {
            childrenStringHash.add(n.stringHash());
        }
        Collections.sort(childrenStringHash);
        for (String hash : childrenStringHash) {
            string.append(hash);
        }
        string.append(")");
        
        return string.toString();
    }
    
    
}