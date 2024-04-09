package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import json.ValueNotFoundException;

public class JustOneGraph {
	
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException {
		
		String filename= "01_Ay_01_00";
		Graph g = new Graph("gruppo1"+File.separator+filename+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename+File.separator+"edges_graph.json");
		List<Graph> subgraphs = g.subgraphs(3,3);
		System.out.println(subgraphs.size());
		
		try(FileOutputStream fos = new FileOutputStream("outs_subgraphs"+File.separator+filename+"_combined.dot");
				PrintStream ps = new PrintStream(fos);) {
			g.printMultipleGraphs(ps, subgraphs);
		}
		
	}

}
