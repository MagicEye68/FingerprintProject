package it.unipr.informatica.tirocin;

public class Tree {
	private TreeNode root;
	public Tree(TreeNode root) {
		this.root = root;
	}
	public TreeNode getRoot() {
		return this.root;
	}
	
	public boolean isomorphvecchio(Tree other) {
		if(root.children.size() != other.root.children.size()) {
			return false;
		}
		if(!root.type.equals(other.root.type)) {
			return false;
		}
		for(int i = 0; i < root.children.size(); i++) {
			Tree t1 =new Tree(root.children.get(i));
			Tree t2 =new Tree(other.root.children.get(i));
			if(!t1.isomorph(t2)) {
				return false;
			}
		}
		return true;
	}
	public boolean isomorph(Tree other) {
	    return root.stringHashStrict().equals(other.root.stringHashStrict());
	}
	
}
