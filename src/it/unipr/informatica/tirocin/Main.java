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

public class Main {
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException  {
		//int c = 0;
		long tempo1 = System.currentTimeMillis();
		File dir=new File("gruppo1");
		List<Graph> list = new ArrayList<Graph>();
		List<String> fnames = new ArrayList<String>();
		for(File f:dir.listFiles()) {
			if(f.getName().equals(".DS_Store")) continue;
			list.add(new Graph("gruppo1"+File.separator+f.getName()+File.separator+"nodes_graph.json",
								"gruppo1"+File.separator+f.getName()+File.separator+"edges_graph.json"));
			fnames.add(f.getName());
			//System.out.println("added: "+ c++);
		}		
		
		//c = 0;
		Map<Integer,Integer> map = new HashMap<>();
		for (int i = 0; i < list.size(); i++) {
		    Graph g = list.get(i);
		    String fname = fnames.get(i);
		    int value = map.containsKey(g.nodes.size())?map.get(g.nodes.size()):0;
		 map.put(g.nodes.size(),value);
		    List<Graph> subgraphs = g.subgraphs(3, 3);
		    
			try (FileOutputStream fos1 = new FileOutputStream("outs_group1" + File.separator + fname + ".dot");
				     PrintStream ps1 = new PrintStream(fos1);
				     FileOutputStream fos2 = new FileOutputStream("outs_subgraphs" + File.separator + fname + "_combined.dot");
				     PrintStream ps2 = new PrintStream(fos2)) 
				{
				    g.printGraph(ps1, false);
				    g.printMultipleGraphs(ps2, subgraphs);
				}
			//System.out.println("dimensione: "+ subgraphs.size());
		}  
		//System.out.println(map);
		long tempo2=System.currentTimeMillis();
		long tempone = tempo2-tempo1;
		System.out.println("coumputed time:"+((double)tempone/1000)+"s.");
		list.get(2).compare(list.get(3),3,6,3);
	}
}

