package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import it.unipr.informatica.tirocin.print.DotFileGraph;
import json.ValueNotFoundException;

public class PlainParsed {
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException  {

		File dir=new File("gruppo1");
		List<Graph> list = new ArrayList<Graph>();
		List<String> fnames = new ArrayList<String>();
		for(File f:dir.listFiles()) {
			if(f.getName().equals(".DS_Store")) continue;
			list.add(new Graph("gruppo1"+File.separator+f.getName()+File.separator+"nodes_graph.json",
								"gruppo1"+File.separator+f.getName()+File.separator+"edges_graph.json",f.getName()));
			fnames.add(f.getName());
		}		
		for (int i = 0; i < list.size(); i++) {		    
			new DotFileGraph("dot_group1_parsed" + File.separator + fnames.get(i))
		 	.printGraph(list.get(i),0,null, true)
			.saveFile();
		}  

	}
}

