package it.unipr.informatica.tirocin;

import java.util.ArrayList;
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
}