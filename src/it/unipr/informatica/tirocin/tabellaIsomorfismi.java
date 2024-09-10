package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unipr.informatica.tirocin.Graph.Box;
import json.ValueNotFoundException;

public class tabellaIsomorfismi {
	public static void main(String[] args) throws ValueNotFoundException, FileNotFoundException, IOException  {

		File dir=new File("riferimenti");
		List<Graph> riferimenti = new ArrayList<Graph>();
		List<String> nomi_riferimenti = new ArrayList<String>();
		
		for(File f:dir.listFiles()) {
			if(f.getName().equals(".DS_Store")) continue;
			riferimenti.add(new Graph("riferimenti"+File.separator+f.getName()+File.separator+"nodes_graph.json",
								"riferimenti"+File.separator+f.getName()+File.separator+"edges_graph.json",f.getName()).removeAdded().removeBorders(30));
			nomi_riferimenti.add(f.getName());
		}
		dir=new File("gruppi_filtrati");
		List<Graph> totali = new ArrayList<Graph>();
		List<String> nomi_totali = new ArrayList<String>();
		for(File f:dir.listFiles()) {
			if(f.getName().equals(".DS_Store")) continue;
			totali.add(new Graph("gruppi_filtrati"+File.separator+f.getName()+File.separator+"nodes_graph.json",
								"gruppi_filtrati"+File.separator+f.getName()+File.separator+"edges_graph.json",f.getName()).removeAdded().removeBorders(30));
			nomi_totali.add(f.getName());
		}
		double score = 0;
		int treshold = 5;
		int boxCompare = 30;
		int boxCreate = 15;
		List<MedianCounter> medie = new ArrayList<>();
		String prev_group = "01";
		System.out.printf("Nomi\tGruppo1\tGruppo2\tGruppo6\tGruppo8\tGruppo13\tGruppo18\n");
		int matching = 0;
		for(int i = 0; i < riferimenti.size(); i++) {
			if(!(nomi_riferimenti.get(i).substring(0,2).equals(prev_group))) {
				
				System.out.print(prev_group+"\t");
			for(int k = 0 ; k < medie.size(); k++) {
				if(medie.get(k) == null) continue;
				System.out.printf("%.1f", medie.get(k).getMedia());
				System.out.print("\t");
			}
			medie=new ArrayList<>();
			prev_group=nomi_riferimenti.get(i).substring(0,2);
			System.out.print("\n");
			}
			
			//List<Graph> totale =riferimenti.get(i).compare(riferimenti.get(i),boxCreate,boxCompare,treshold);//totale bfs e dfs
			int group_counter = -1;
			for(int j = 0; j <totali.size(); j++) {
				
				group_counter=Integer.parseInt(nomi_totali.get(j).substring(0, 2));
				
				if(nomi_riferimenti.get(i).substring(0, 5).equals(nomi_totali.get(j).substring(0, 5))) continue;
				if(nomi_riferimenti.get(i).substring(6, 8).equals(nomi_totali.get(j).substring(6, 8))) {
					
					//List<Graph> result = riferimenti.get(i).compare(totali.get(j),boxCreate,boxCompare,treshold);//compare bfs e dfs
					//List<Graph> result = riferimenti.get(i).nodesCompare(totali.get(j),1,2,1);//compare node by node
					matching++;
					List <Box> result =riferimenti.get(i).boxesCompare(totali.get(j),15,15);//compare griglia
					
					for(int f=0;f<result.size();f++) {
						
						double finalScore=0;
						int maxEnding=result.get(f).getEndingCounterG1()>result.get(f).getEndingCounterG2()?result.get(f).getEndingCounterG1():result.get(f).getEndingCounterG2();
						int minEnding=result.get(f).getEndingCounterG1()<result.get(f).getEndingCounterG2()?result.get(f).getEndingCounterG1():result.get(f).getEndingCounterG2();
						int maxBifurcation=result.get(f).getBifurcationCounterG1()>result.get(f).getBifurcationCounterG2()?result.get(f).getBifurcationCounterG1():result.get(f).getBifurcationCounterG2();
						int minBifurcation=result.get(f).getBifurcationCounterG1()<result.get(f).getBifurcationCounterG2()?result.get(f).getBifurcationCounterG1():result.get(f).getBifurcationCounterG2();
						int differenzaEnding = maxEnding-minEnding;
						int differenzaBifurcation = maxBifurcation-minBifurcation;

						if(result.get(i).isEmpty()) continue;
						finalScore=100-Graph.f(differenzaEnding)-Graph.f(differenzaBifurcation);
					
						result.get(i).setBoxScore(finalScore);

					}
					double finalFinalScore=0;
					for(int h=0;h<result.size();h++) finalFinalScore+=result.get(h).getBoxScore();
					
					
					int size = result.size();
					for(int a=0; a<result.size(); a++) {
						if(result.get(a).isEmpty()) size--;	
					}
					finalFinalScore/=size;
					score = finalFinalScore*100;
//					int matches = 0;// griglia media
//					for(Box b:result) {//griglia media
//						matches+=b.getBifurcationCounterG1()<b.getBifurcationCounterG2()?b.getBifurcationCounterG1():b.getBifurcationCounterG2();
//						matches+=b.getEndingCounterG1()<b.getEndingCounterG2()?b.getEndingCounterG1():b.getEndingCounterG2();
//					}//griglia media
//					score=((double)matches/(riferimenti.get(i).nodes.size()))*100;//griglia media
					int medianodes = (riferimenti.get(i).getProcessedNodesSize(30)+totali.get(j).getProcessedNodesSize(30))/2;
					//score = ((double)(result.size()/2)/medianodes)*100;		//score node by node
					//score=((double)(result.size()/2)/(totale.size()/2))*100; //score bfs e dfs
					
					while(medie.size()<=group_counter)medie.add(null);
					if(medie.get(group_counter) == null) {
						medie.add(group_counter,new MedianCounter());
					}
					medie.get(group_counter).addValue(score);
				}


			}

			
		}
		
		System.out.print(prev_group+"\t");
		for(int k = 0 ; k < medie.size(); k++) {
			if(medie.get(k) == null) continue;
			System.out.printf("%.1f", medie.get(k).getMedia());
			System.out.print("\t");
		}
		medie=new ArrayList<>();
		System.out.print("\n");
		System.out.println(matching);
		
}
	
public static class MedianCounter{
	private double sum;
	private int counter;
	
	public MedianCounter() {
		sum = 0;
		counter =0;
	}
	
	public void addValue(double s) {
		sum+=s;
		counter++;
	}
	
	public double getMedia() {
		return sum/counter;
	}
	
	public void reset() {
		sum = 0;
		counter = 0;
	}
	
}
}

