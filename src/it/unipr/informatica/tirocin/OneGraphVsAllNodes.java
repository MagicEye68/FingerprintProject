package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unipr.informatica.tirocin.Graph.Box;
import it.unipr.informatica.tirocin.print.Color;
import it.unipr.informatica.tirocin.print.DotFileGraph;
import json.ValueNotFoundException;

public class OneGraphVsAllNodes {
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException  {
		long tempo1 = System.currentTimeMillis();
		File dir=new File("gruppo1");
		List<Graph> list = new ArrayList<Graph>();
		List<String> fnames = new ArrayList<String>();
		
		String nomeHardcoded= "01_G1_01_00";
		Graph graphHardcoded = new Graph("gruppo1"+File.separator+nomeHardcoded+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+nomeHardcoded+File.separator+"edges_graph.json",nomeHardcoded).removeAdded();
		list.add(graphHardcoded.removeBorders(30));
		fnames.add(nomeHardcoded);
		
		for(File f:dir.listFiles()) {
			if(f.getName().equals(".DS_Store")) continue;
			if(fnames.get(0).equals(f.getName())) continue;
			list.add(new Graph("gruppo1"+File.separator+f.getName()+File.separator+"nodes_graph.json",
								"gruppo1"+File.separator+f.getName()+File.separator+"edges_graph.json",f.getName()).removeAdded().removeBorders(30));
			fnames.add(f.getName());
		}

		int treshold = 1;
		int boxCompare = 2;
		int boxCreate = 1;
		int offsetBorders = 30;
		double scoreMax = 0;
		List<Double>scorelist=new ArrayList<>();
		String graphName2="";
		
		for (int i = 1; i < list.size(); i++) {
			System.out.print("computing "+fnames.get(i)+"...");
			List<Graph> result =list.get(0).nodesCompare(list.get(i),boxCreate,boxCompare,treshold);
			List <Box> boxes =list.get(0).boxesCompare(list.get(i),15,15);

			int matches = 0;
			for(Box b:boxes) {
				matches+=b.getBifurcationCounterG1()<b.getBifurcationCounterG2()?b.getBifurcationCounterG1():b.getBifurcationCounterG2();
				matches+=b.getEndingCounterG1()<b.getEndingCounterG2()?b.getEndingCounterG1():b.getEndingCounterG2();
			}
			double score=(double)matches/(list.get(0).nodes.size());//score matches
			//double score = ((double)(result.size()/2)/list.get(i).getProcessedNodesSize(offsetBorders))*100;//score nodes
			scorelist.add(score);
			System.out.printf("Score (matches): %.1f", score*100); System.out.print("% \n");
			//System.out.printf("Score: %.1f", score);
			//System.out.print("% \n");
			if(score>scoreMax) {
				scoreMax = score;
				graphName2 = fnames.get(i);
			}

		}  
		double media = 0;
		for(Double s:scorelist) {
			media+=s;
		}
		media/=scorelist.size();
		long tempo2=System.currentTimeMillis();
		long tempone = tempo2-tempo1;
		System.out.println("----");
		System.out.println("coumputed time:"+((double)tempone/1000)+"s.");
		System.out.printf("Media scores: %.1f",media*100);System.out.print("% \n");
		System.out.print("Best match: ["+fnames.get(0)+"-"+graphName2+"] with a score of ");
		System.out.printf("%.1f", scoreMax*100);
		System.out.print("% \n");
}
}

