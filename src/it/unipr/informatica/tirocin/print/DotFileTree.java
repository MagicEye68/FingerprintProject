package it.unipr.informatica.tirocin.print;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import it.unipr.informatica.tirocin.Tree;
import it.unipr.informatica.tirocin.TreeNode;


public class DotFileTree {
		
	private String filename;
	private List<Tree> roots;
	private List <String> labels;

	public DotFileTree(String filename) {
		if(filename == null) throw new IllegalArgumentException("filename == null");
		roots = new ArrayList<>();
		labels = new ArrayList<>();
		this.filename=filename;
	}
	
	public DotFileTree printTree(Tree t,String label) {
		roots.add(t);
		labels.add(label);		
		return this;
	}
	
	public void saveFile() {

		
		try (FileOutputStream fos = new FileOutputStream(filename +".dot");
				PrintStream ps = new PrintStream(fos);) {
			ps.println("digraph{");
			
			for(int i = 0 ; i < roots.size(); ++i) {
				ps.printf("subgraph cluster_%d {\n", i);
				ps.printf("color = white\n");
				// print label
				if (labels.get(i) != null) {
					ps.printf("label = \"%s\";\n", labels.get(i));
					ps.printf("fontsize=10;\n");
				}
								
				// print nodes
				printRec(roots.get(i).getRoot(),i,ps);
				
				ps.printf("}\n");	// close subgraph
			}
			

			ps.println("}");
		} catch (Exception e) {
			throw new RuntimeException(e.getCause());
		}

	}
	
	private void printRec(TreeNode n, int id,PrintStream ps) {
		
		ps.printf("T%d_%d [label=\"%d\", color=\"%s\"]\n",
				id, n.getIndex(), n.getIndex(), stringFromColor(getColorFromType(n.type)));
		for(TreeNode t:n.children) {
			ps.printf("T%d_%d -> T%d_%d [color=\"black\"]\n",
					id,n.getIndex(),id,t.getIndex());
			printRec(t,id,ps);
		}
	}
	private static Color getColorFromType(String type) {
		switch(type) {
			case "border": return  Color.PURPLE;
			case "bifurcation": return Color.BLUE;		
			case "added": return Color.RED;	
			case "ending": return Color.GREEN;
			case "ridge": return Color.BLUE;
			default: return Color.LIGHTGRAY;
		}	
	}
	
	private static String stringFromColor(Color color) {
		switch(color) {
			case PURPLE: return  "purple";
			case BLUE: return "blue";		
			case RED: return "red";	
			case GREEN: return "green";
			case LIGHTGRAY: return "lightgray";
			case BLACK:return "black";
			
		default:
			break;
		}
		return "lightgray";	
	}
}
