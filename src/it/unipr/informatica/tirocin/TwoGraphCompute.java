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
		String filename1= "01_Ay_03_00";//mignolo
		Graph g = new Graph("gruppo1"+File.separator+filename1+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename1+File.separator+"edges_graph.json");
		list.add(g);
		
		
		String filename2= "01_Ay_03_01";//medio
		Graph g1 = new Graph("gruppo1"+File.separator+filename2+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename2+File.separator+"edges_graph.json");
	
		list.add(g1);
		
		

		int treshold = 3;
		int boxCompare = 100;
		int boxCreate = 10;
		List<Graph> result =list.get(0).compare(list.get(1),boxCreate,boxCompare,treshold);
		
		try (FileOutputStream fos1 = new FileOutputStream("compared_outs"+ File.separator + filename1 +".dot");
			     PrintStream ps1 = new PrintStream(fos1);) 
			{
			    g.printMultipleGraphsCompare(ps1, result);
			  
			}
		for(int i = 0 ; i <list.size(); i++) {
			
			try (FileOutputStream fos1 = new FileOutputStream("compared_outs"+ File.separator + i +".dot");
				     PrintStream ps1 = new PrintStream(fos1);) 
				{
				list.get(i).printMultipleGraphsCompare(ps1, list.get(i).subgraphs(10, 3));
				  
				}
		}

		
		
		
		long tempo2=System.currentTimeMillis();
		long tempone = tempo2-tempo1;
		System.out.println("coumputed time:"+((double)tempone/1000)+"s.");
	}

}
