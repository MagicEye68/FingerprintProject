package it.unipr.informatica.tirocin;

import java.util.ArrayList;
import java.util.List;

public class TreeNode{
	private int[] coordinates;
	public String type;
	public List<TreeNode> children;
	
	public TreeNode(int [] coordinates, String type) {
		this.coordinates = coordinates;
		this.type=type;
		children = new ArrayList<>();
	}
	
	
	public void addChild(TreeNode child) {
		children.add(children.size(),child);
	}

	
	@Override
	public String toString() {
		return "x: "+coordinates[0]+" y: "+coordinates[1]+" ,type: "+type;
	}	
}