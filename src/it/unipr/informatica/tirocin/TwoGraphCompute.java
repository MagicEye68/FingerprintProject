package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unipr.informatica.tirocin.Graph.Node;
import it.unipr.informatica.tirocin.print.Color;
import it.unipr.informatica.tirocin.print.DotFileGraph;
import json.ValueNotFoundException;

public class TwoGraphCompute {
	
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException {
		long tempo1 = System.currentTimeMillis();
		List<Graph> list = new ArrayList<Graph>();
		String filename1= "01_Ay_01_00";
		Graph g1 = new Graph("gruppo1"+File.separator+filename1+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename1+File.separator+"edges_graph.json",filename1).removeAdded();
		list.add(g1.removeBorders(30));
		
		
		String filename2= "01_Ay_01_00";
		//String filename2= "01_G1_05_00";
		Graph g2 = new Graph("gruppo1"+File.separator+filename2+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename2+File.separator+"edges_graph.json",filename2).removeAdded();
	
		list.add(g2.removeBorders(30));
		


		int treshold = 5;
		int boxCompare = 30;
		int boxCreate = 15;
	
		List<Graph> result =list.get(0).compare(list.get(1),boxCreate,boxCompare,treshold);
		List<Graph> totale =list.get(0).compare(list.get(0),boxCreate,boxCompare,treshold);


	for(int i = 0 ; i <result.size(); i+=2) {
		DotFileGraph output = new DotFileGraph("compared_outs"+ File.separator + "000computed_"+result.size()/2+"_times"+i)
				.printGraph(g1,0,Color.BLACK, true)
				.printGraph(g2,1,Color.BLACK, true)
				//.addLabel(filename1, 0)
				//.addLabel(filename2, 1)
	 	.printGraph(result.get(i),0,null, false)
		.printBox(result.get(i).getCentered().getX(), result.get(i).getCentered().getY(), boxCreate, 0, Color.RED)
		.printGraph(result.get(i+1),1,null, false)
		.printBox(result.get(i+1).getCentered().getX(), result.get(i).getCentered().getY(), boxCreate, 1, Color.RED);
		output.saveFile();
	}


		long tempo2=System.currentTimeMillis();
		long tempo3 = tempo2-tempo1;
		
		double media = ((double)(result.size()/2)/(totale.size()/2))*100;
		System.out.printf("Score: %.1f", media);
		System.out.print("% \n");
		System.out.println("coumputed time:"+((double)tempo3/1000)+"s.");
	}

}
