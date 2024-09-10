package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unipr.informatica.tirocin.print.Color;
import it.unipr.informatica.tirocin.print.DotFileGraph;
import json.ValueNotFoundException;

public class AllVSAllNodes {
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException  {

		File dir=new File("gruppo1");
		List<Graph> list = new ArrayList<Graph>();
		List<String> fnames = new ArrayList<String>();
		Map<Graph, Boolean> checker = new HashMap<>();
		
		for(File f:dir.listFiles()) {
			if(f.getName().equals(".DS_Store")) continue;
			list.add(new Graph("gruppo1"+File.separator+f.getName()+File.separator+"nodes_graph.json",
								"gruppo1"+File.separator+f.getName()+File.separator+"edges_graph.json",f.getName()).removeAdded());
			fnames.add(f.getName());
		}
		for (Graph g : list) {
			checker.put(g, false);
		}
		int treshold = 1;
		int boxCreate = 1;
		int boxCompare = 4;
		int offsetBorders = 30;
		int counter = 0;
		long time1 = 0;
		long tempo1 = System.currentTimeMillis();
		double scoreMax = 0;
		String graphName1="";
		String graphName2="";
		int icounter = 0;
		int jcounter = 0;
		
		for (int i = 0; i < list.size(); i++) {
			List<Graph> result = new ArrayList<>();
			for (int j = 0; j < list.size(); j++) {
				
				if(i==j)continue;
				
				if (checker.get(list.get(j)) == false){
					System.out.print("computing graph "+(i+1)+" - "+(j+1)+"/"+list.size()+"...");
					long start = System.currentTimeMillis();
					result =list.get(i).nodesCompare(list.get(j),boxCreate,boxCompare,treshold);
					counter ++;
					time1+=System.currentTimeMillis()-start;
					int media = (list.get(i).getProcessedNodesSize(offsetBorders)+list.get(j).getProcessedNodesSize(offsetBorders))/2;
					double score = ((double)(result.size()/2)/media)*100;
					System.out.printf("Score: %.1f", score);
					System.out.print("% \n");
					if(score>scoreMax) {
						scoreMax = score;
						graphName1 = fnames.get(i);
						graphName2 = fnames.get(j);
						icounter = i;
						jcounter = j;
						
					}
					}
			} 
			checker.put(list.get(i), true);

		} 

		long tempo2=System.currentTimeMillis();
		
		System.out.println("Media tempo di un impronta con un altra impronta: "+time1/counter+" ms.");
			
		System.out.println("Tempo totale: "+(tempo2-tempo1)/1000+" s.");
		
		System.out.print("Best match: ["+graphName1+"-"+graphName2+"] with a score of ");
		System.out.printf("%.1f", scoreMax);
		System.out.print("% \n");
		System.out.println("Now writing the dot file...");
		List<Graph> result = new ArrayList<>();
		result =list.get(icounter).nodesCompare(list.get(jcounter),boxCreate,boxCompare,treshold);
		DotFileGraph output = new DotFileGraph("compared_outs_nodes"+ File.separator + "computed_"+fnames.get(icounter)+"-"+fnames.get(jcounter));
		for(int k = 0 ; k <result.size(); k+=2) {
		
		 	output.printGraph(list.get(icounter),0,Color.LIGHTGRAY, true)
			.printGraph(result.get(k),0,null, false)
			.printGraph(list.get(jcounter),1,Color.LIGHTGRAY, true)
			.printGraph(result.get(k+1),1,null, false)
			.addLabel(fnames.get(icounter), 0)
			.addLabel(fnames.get(jcounter), 1);
		}
		output.saveFile();
		System.out.println("wrote, check the folder compared_outs_nodes");
}
}
