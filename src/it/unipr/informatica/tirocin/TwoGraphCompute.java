package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import json.ValueNotFoundException;

public class TwoGraphCompute {
	
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException {
		long tempo1 = System.currentTimeMillis();
		List<Graph> list = new ArrayList<Graph>();
		String filename1= "01_G1_05_01";
		Graph g1 = new Graph("gruppo1"+File.separator+filename1+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename1+File.separator+"edges_graph.json");
		list.add(g1);
		
		
		String filename2= "01_G2_10_01";
		Graph g2 = new Graph("gruppo1"+File.separator+filename2+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename2+File.separator+"edges_graph.json");
	
		list.add(g2);
		
		

		int treshold = 20;
		int boxCompare = 20;
		int boxCreate = 20;
		List<Graph> result =list.get(0).compare(list.get(1),boxCreate,boxCompare,treshold);
		

		for(int i = 0 ; i <result.size(); i+=2) {
			
			try (FileOutputStream fos1 = new FileOutputStream("compared_outs"+ File.separator + "computed_"+i/2 +".dot");
				     PrintStream ps1 = new PrintStream(fos1);) 
				{
				 	Graph.printComputedGraphs(ps1, g1, g2, result.get(i), result.get(i+1), boxCompare, filename1, filename2);
				  
				}
		}

		
		
		
		long tempo2=System.currentTimeMillis();
		long tempone = tempo2-tempo1;
		System.out.println("coumputed time:"+((double)tempone/1000)+"s.");
	}

}
