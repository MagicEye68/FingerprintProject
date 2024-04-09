package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import json.ValueNotFoundException;

public class Main {
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException  {
		int c = 0;
		long tempo1 = System.currentTimeMillis();
		File dir=new File("gruppo1");
		List<Graph> list = new ArrayList<Graph>();
		List<String> fnames = new ArrayList<String>();
		for(File f:dir.listFiles()) {
			if(f.getName().equals(".DS_Store")) continue;
			list.add(new Graph("gruppo1"+File.separator+f.getName()+File.separator+"nodes_graph.json",
								"gruppo1"+File.separator+f.getName()+File.separator+"edges_graph.json"));
			fnames.add(f.getName());
			System.out.println("added"+ c++);
		}		
		c = 0;
		int count = 0 ;
		for(Graph g:list) {
			try(FileOutputStream fos = new FileOutputStream("outs_group1"+File.separator+ fnames.get(count++) +".dot");
				PrintStream ps = new PrintStream(fos);) {
				g.printGraph(ps,false);	
			}
			System.out.println("added"+ c++);
		}   
		long tempo2=System.currentTimeMillis();
		long tempone = tempo2-tempo1;
		System.out.println("ce ho messo "+((double)tempone/1000)+"s.");
	}
}

