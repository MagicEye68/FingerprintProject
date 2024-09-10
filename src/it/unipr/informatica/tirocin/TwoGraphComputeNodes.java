package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unipr.informatica.tirocin.Graph.Box;
import it.unipr.informatica.tirocin.Graph.Node;
import it.unipr.informatica.tirocin.print.Color;
import it.unipr.informatica.tirocin.print.DotFileGraph;
import json.ValueNotFoundException;

public class TwoGraphComputeNodes {
	
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException {
		long tempo1 = System.currentTimeMillis();
		List<Graph> list = new ArrayList<Graph>();
		String filename1= "01_Ay_01_00";
		Graph g1 = new Graph("gruppo1"+File.separator+filename1+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename1+File.separator+"edges_graph.json",filename1).removeAdded();
		list.add(g1.removeBorders(30));
		
		String filename2= "01_Ay_01_00";
		//String filename2= "01_G1_06_00";
		//String filename2= "01_G2_10_06";
		Graph g2 = new Graph("gruppo1"+File.separator+filename2+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+filename2+File.separator+"edges_graph.json",filename2).removeAdded();
	
		list.add(g2.removeBorders(30));
		
		

		int treshold = 1;
		int boxCompare = 4;
		int boxCreate = 1;
		int offsetBorders = 30;
		List<Graph> result =list.get(0).nodesCompare(list.get(1),boxCreate,boxCompare,treshold);
		List <Box> boxes =list.get(0).boxesCompare(list.get(1),15,15);
		//List<Graph> result =list.get(0).lastCompare(list.get(1),boxCreate,boxCompare,treshold);
		long tempo2=System.currentTimeMillis();
		long tempone = tempo2-tempo1;

		//---SCORES---
		
		for(int i=0;i<boxes.size();i++) {
			
			double finalScore=0;
			int maxEnding=boxes.get(i).getEndingCounterG1()>boxes.get(i).getEndingCounterG2()?boxes.get(i).getEndingCounterG1():boxes.get(i).getEndingCounterG2();
			int minEnding=boxes.get(i).getEndingCounterG1()<boxes.get(i).getEndingCounterG2()?boxes.get(i).getEndingCounterG1():boxes.get(i).getEndingCounterG2();
			int maxBifurcation=boxes.get(i).getBifurcationCounterG1()>boxes.get(i).getBifurcationCounterG2()?boxes.get(i).getBifurcationCounterG1():boxes.get(i).getBifurcationCounterG2();
			int minBifurcation=boxes.get(i).getBifurcationCounterG1()<boxes.get(i).getBifurcationCounterG2()?boxes.get(i).getBifurcationCounterG1():boxes.get(i).getBifurcationCounterG2();
			int differenzaEnding = maxEnding-minEnding;
			int differenzaBifurcation = maxBifurcation-minBifurcation;

			if(boxes.get(i).isEmpty()) continue;
			finalScore=100-Graph.f(differenzaEnding)-Graph.f(differenzaBifurcation);
		
			boxes.get(i).setBoxScore(finalScore);

		}
		double finalFinalScore=0;
		for(int i=0;i<boxes.size();i++) {
//			System.out.println("BOX NUMBER: "+boxes.get(i).getIndex());
//			System.out.println("G1ending:"+boxes.get(i).getEndingCounterG1()+", G1bif:"+boxes.get(i).getBifurcationCounterG1());
//			System.out.println("G2ending:"+boxes.get(i).getEndingCounterG2()+", G2bif:"+boxes.get(i).getBifurcationCounterG2());
//			System.out.printf("Box Score: %.1f", boxes.get(i).getBoxScore()); System.out.print("% \n");
//			System.out.println("-------");
			finalFinalScore+=boxes.get(i).getBoxScore();
		}
		
		int size = boxes.size();
		for(int i=0; i<boxes.size(); i++) {
			if(boxes.get(i).isEmpty()) {
				size--;
				
			}
		}
		finalFinalScore/=size;
		System.out.printf("Score (function): %.1f", finalFinalScore); System.out.print("% \n");
		int matches = 0;
		for(Box b:boxes) {
			matches+=b.getBifurcationCounterG1()<b.getBifurcationCounterG2()?b.getBifurcationCounterG1():b.getBifurcationCounterG2();
			matches+=b.getEndingCounterG1()<b.getEndingCounterG2()?b.getEndingCounterG1():b.getEndingCounterG2();
		}
		double matchScore=(double)matches/(list.get(0).nodes.size());
		int media = (g1.getProcessedNodesSize(offsetBorders)+g2.getProcessedNodesSize(offsetBorders))/2;
		double score = ((double)(result.size()/2)/media)*100;
		System.out.printf("Score (matches): %.1f", matchScore*100); System.out.print("% \n");
		System.out.printf("Score (node by node): %.1f", score);
		System.out.print("% \n");
		System.out.println("computed time:"+((double)tempone/1000)+"s.");
		
		
		
		
		
		//	FULLBOX PRINTER
		DotFileGraph output = new DotFileGraph("compared_outs_nodes"+ File.separator + "computed_"+filename1+"-"+filename2+"-fullbox");
		Set<Node> set = new HashSet<>();

		output.printGraph(g1,0,Color.LIGHTGRAY, true)
		.printGraph(g2,1,Color.LIGHTGRAY, true)
		.addLabel(filename1, 0)
		.addLabel(filename2, 1);
		for(int i = 0 ; i <result.size(); i+=2) {				 	
			output.printGraph(result.get(i),0,null, false)			
			.printGraph(result.get(i+1),1,null, false);	
		}
		for (Graph g:result) {
			for(Node n:g.nodes) {
				set.add(n);
			}
		}
		for(Node n:list.get(0).nodes) {
			Color color = set.contains(n)?Color.GREEN:Color.RED;
			output.printBox(n.getX(),n.getY(), boxCompare, 0, color);
		}

		for(Node n:list.get(1).nodes) {
			Color color = set.contains(n)?Color.GREEN:Color.RED;
			output.printBox(n.getX(),n.getY(), boxCompare, 1, color);
		}

		output.saveFile();
		
		//GREENBOX PRINTER
		output = new DotFileGraph("compared_outs_nodes"+ File.separator + "computed_"+filename1+"-"+filename2+"-greenbox");
		output.printGraph(g1,0,Color.LIGHTGRAY, true)
		.printGraph(g2,1,Color.LIGHTGRAY, true)
		.addLabel(filename1, 0)
		.addLabel(filename2, 1);
		for(int i = 0 ; i <result.size(); i+=2) {				 		
			output.printGraph(result.get(i),0,null, false)
			.printBox(result.get(i).getCentered().getX(), result.get(i).getCentered().getY(), boxCompare, 0, Color.GREEN)
			.printGraph(result.get(i+1),1,null, false)
			.printBox(result.get(i).getCentered().getX(), result.get(i).getCentered().getY(), boxCompare, 1, Color.GREEN);

		}
		output.saveFile();
		
		//CLEAN PRINTER
		output = new DotFileGraph("compared_outs_nodes"+ File.separator + "computed_"+filename1+"-"+filename2+"-clean");
		output.printGraph(g1,0,Color.BLACK, true)
		//.printGraph(g2,1,Color.LIGHTGRAY, true)	
		.printGraph(list.get(0),0,null, false);
		//.printGraph(list.get(1),1,null, false)
		//.addLabel(filename1, 0)
		//.addLabel(filename2, 1);
		output.saveFile();
		

		//GRIGLIA PRINTER 
		output = new DotFileGraph("compared_outs_nodes"+ File.separator + "computed_"+filename1+"-"+filename2+"-griglia");
					
		 	output.printGraph(g1,0,Color.LIGHTGRAY, true)
		 	//.addLabel(filename1, 0)
			.printGraph(list.get(0),0,null, false)
			.printGrid(boxes, 0, Color.RED)	
			.printGraph(g2,1,Color.LIGHTGRAY, true)		
			.printGraph(list.get(1),1,null, false)
			.printGrid(boxes, 1, Color.GREEN)
			
			.addLabel(filename2, 1);
			
	
		output.saveFile();
		
	}

}
