package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unipr.informatica.tirocin.print.Color;
import it.unipr.informatica.tirocin.print.DotFileGraph;
import json.ValueNotFoundException;

public class TwoGraphCompute {
	
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException {
		long tempo1 = System.currentTimeMillis();
		List<Graph> list = new ArrayList<Graph>();
		String filename1= "01_G2_10_01";
		Graph g1 = new Graph("gruppo1"+File.separator+filename1+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename1+File.separator+"edges_graph.json",filename1);
		list.add(g1);
		
		
		String filename2= "01_G2_10_03";
		Graph g2 = new Graph("gruppo1"+File.separator+filename2+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename2+File.separator+"edges_graph.json",filename2);
	
		list.add(g2);
		
		

		int treshold = 8;
		int boxCompare = 40;
		int boxCreate = 8;
		List<Graph> result =list.get(0).compare(list.get(1),boxCreate,boxCompare,treshold);
		
		
	for(int i = 0 ; i <result.size(); i+=2) {
		new DotFileGraph("compared_outs"+ File.separator + "computed_"+i/2)
	 	.printGraph(list.get(0),0,Color.LIGHTGRAY, true)
		.printGraph(result.get(i),0,null, false)
		.printBox(result.get(i).getCentered().getX(), result.get(i).getCentered().getY(), boxCompare, 0, Color.GREEN)
		.printGraph(list.get(1),1,Color.LIGHTGRAY, true)
		.printGraph(result.get(i+1),1,null, false)
		.printBox(result.get(i).getCentered().getX(), result.get(i).getCentered().getY(), boxCompare, 1, Color.GREEN)
		.addLabel(filename1, 0)
		.addLabel(filename2, 1)
		.saveFile();
	}
	
		long tempo2=System.currentTimeMillis();
		long tempone = tempo2-tempo1;
		System.out.println("coumputed time:"+((double)tempone/1000)+"s.");
	}

}
