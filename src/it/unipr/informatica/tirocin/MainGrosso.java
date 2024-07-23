package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import json.ValueNotFoundException;

public class MainGrosso {
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException  {
		int box  = 100;
		int min = 10;
		int max = 12;
		
		String filename1= "01_Ay_10_00";
		Graph g1 = new Graph("gruppo1"+File.separator+filename1+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename1+File.separator+"edges_graph.json");


		    List<Graph> subgraphs = g1.subgraphs2(box, min, max);
		    
			try (FileOutputStream fos1 = new FileOutputStream("outs_prova" + File.separator + filename1 + ".dot");
				     PrintStream ps1 = new PrintStream(fos1);){   
				    g1.printMultipleGraphs(ps1, subgraphs);
				}
			
	
		}  

	}


