package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import json.ValueNotFoundException;

public class AllVSAll {
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException  {

		File dir=new File("gruppo1");
		List<Graph> list = new ArrayList<Graph>();
		List<String> fnames = new ArrayList<String>();
		Map<Graph, Boolean> checker = new HashMap<>();
		
		for(File f:dir.listFiles()) {
			if(f.getName().equals(".DS_Store")) continue;
			list.add(new Graph("gruppo1"+File.separator+f.getName()+File.separator+"nodes_graph.json",
								"gruppo1"+File.separator+f.getName()+File.separator+"edges_graph.json"));
			fnames.add(f.getName());
		}
		for (Graph g : list) {
			checker.put(g, false);
		}
		int treshold = 20;
		int boxCompare = 40;
		int boxCreate = 20;
		int counter = 0;
		long time1 = 0;
		long tempo1 = System.currentTimeMillis();
		for (int i = 0; i < list.size(); i++) {
			List<Graph> result = new ArrayList<>();
			for (int j = 0; j < list.size(); j++) {
				
				if(i==j)continue;
				
				if (checker.get(list.get(j)) == false){
					System.out.println("computing graph "+(i+1)+" - "+(j+1)+"/"+list.size()+"...");
					long start = System.currentTimeMillis();
					result =list.get(i).compare(list.get(j),boxCreate,boxCompare,treshold);
					counter ++;
					time1+=System.currentTimeMillis()-start;
					}

				for(int k = 0 ; k <result.size(); k+=2) {
					try (FileOutputStream fos1 = new FileOutputStream("compared_outs"+ File.separator + fnames.get(i)+"-"+fnames.get(j)+"-"+k/2+".dot");
								PrintStream ps1 = new PrintStream(fos1);) {
						 	Graph.printComputedGraphs(ps1, list.get(i), list.get(j), result.get(k), result.get(k+1), boxCompare, fnames.get(i), fnames.get(j));
						 	System.out.println("found graph "+(i+1)+" - "+(j+1)+"/"+list.size());
						}
				}
			} 
			checker.put(list.get(i), true);

		} 
		long tempo2=System.currentTimeMillis();
		
		System.out.println("Media tempo di un impronta con un altra impronta: "+time1/counter+" ms.");
			
		System.out.println("Tempo totale: "+(tempo2-tempo1)+" ms.");
}
}
