package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import it.unipr.informatica.tirocin.print.Color;
import it.unipr.informatica.tirocin.print.DotFile;
import json.ValueNotFoundException;

public class mainPrinter {
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
		
		DotFile dot = new DotFile("compared_outs"+ File.separator + "test");
		dot.printGraph(g1,0,Color.BLACK);
		dot.printGraph(g2,1,Color.PURPLE);
		dot.saveFile();

		
		
		
		long tempo2=System.currentTimeMillis();
		long tempone = tempo2-tempo1;
		System.out.println("coumputed time:"+((double)tempone/1000)+"s.");
	}
}
