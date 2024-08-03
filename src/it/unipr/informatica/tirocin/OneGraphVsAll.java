package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import it.unipr.informatica.tirocin.print.Color;
import it.unipr.informatica.tirocin.print.DotFileGraph;
import json.ValueNotFoundException;

public class OneGraphVsAll {
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException  {
		long tempo1 = System.currentTimeMillis();
		File dir=new File("gruppo1");
		List<Graph> list = new ArrayList<Graph>();
		List<String> fnames = new ArrayList<String>();
		
		String nomeHardcoded= "01_Ay_08_00";
		Graph graphHardcoded = new Graph("gruppo1"+File.separator+nomeHardcoded+File.separator+"nodes_graph.json",
				"gruppo1"+File.separator+nomeHardcoded+File.separator+"edges_graph.json",nomeHardcoded);
		list.add(graphHardcoded);
		fnames.add(nomeHardcoded);
		
		for(File f:dir.listFiles()) {
			if(f.getName().equals(".DS_Store")) continue;
			if(fnames.get(0).equals(f.getName())) continue;
			list.add(new Graph("gruppo1"+File.separator+f.getName()+File.separator+"nodes_graph.json",
								"gruppo1"+File.separator+f.getName()+File.separator+"edges_graph.json",f.getName()));
			fnames.add(f.getName());
		}

		int treshold = 9;
		int boxCompare = 100;
		int boxCreate = 10;
		int max = 0;
		String stringmax="";
		
		for (int i = 1; i < list.size(); i++) {
			System.out.println("computing "+i+"/"+list.size()+"...");
			List<Graph> result =list.get(0).compare(list.get(i),boxCreate,boxCompare,treshold);
			if((result.size()/2)>max) {
				max=result.size()/2;
				stringmax=fnames.get(0)+"-"+fnames.get(i)+".dot";
			}
			for(int j = 0 ; j <result.size(); j+=2) {
				new DotFileGraph("compared_outs"+ File.separator + fnames.get(i)+"-"+fnames.get(j)+"-"+j/2)
			 	.printGraph(list.get(0),0,Color.LIGHTGRAY, true)
				.printGraph(result.get(j),0,null, false)
				.printBox(result.get(j).getCentered().getX(), result.get(j).getCentered().getY(), boxCompare, 0, Color.GREEN)
				.printGraph(list.get(i),1,Color.LIGHTGRAY, true)
				.printGraph(result.get(j+1),1,null, false)
				.printBox(result.get(j).getCentered().getX(), result.get(j).getCentered().getY(), boxCompare, 1, Color.GREEN)
				.addLabel(fnames.get(0), 0)
				.addLabel(fnames.get(i), 1)
				.saveFile();
			}

		}  
		System.out.println("max verstappen: "+max);
		System.out.println("filename: "+stringmax);
		
		long tempo2=System.currentTimeMillis();
		long tempone = tempo2-tempo1;
		System.out.println("coumputed time:"+((double)tempone/1000)+"s.");
}
}