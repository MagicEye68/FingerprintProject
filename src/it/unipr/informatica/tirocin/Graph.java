package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import json.JSON;
import json.JSONArray;
import json.JSONIzer;
import json.ValueNotFoundException;

public class Graph {
	private List<Node> nodes;
	private List<Edge> edges;
	
	public Graph(String NodeFile,String EdgeFile) throws ValueNotFoundException{
		
		JSON jsonFile = JSONIzer.parse(new File(NodeFile));
		
		JSONArray jsonNodeArray = (JSONArray)jsonFile.traverse(new String[] {"nodes"});
		
		nodes = new ArrayList<Node>();
		int index = 0;
		for(JSON j:jsonNodeArray){
			int[] _coordinates = j.traverse(new String[] {"coordinates"}).getObject(int[].class);
			String typeForEach = j.traverse(new String[] {"type"}).getObject(String.class);
			
			 nodes.add(new Node(_coordinates,typeForEach,index++));		 
		}
		
		
		jsonFile = JSONIzer.parse(new File(EdgeFile));
		JSONArray jsonEdgeArray = (JSONArray)jsonFile.traverse(new String[] {"edges"});
		edges = new ArrayList<Edge>();
		for(JSON j:jsonEdgeArray){
			int[] _nodes = j.traverse(new String[] {"nodes"}).getObject(int[].class);
			String typeForEach = j.traverse(new String[] {"type"}).getObject(String.class);
			double distanceForEach =j.traverse(new String[] {"distance"}).getObject(Double.class);
			Node[] nodesArrayForEach = {nodes.get(_nodes[0]), nodes.get(_nodes[1])};
			edges.add(new Edge(nodesArrayForEach,typeForEach,distanceForEach));	
			
		}
		
	     
	}
	
	public Graph(List<Node> nodes, List<Edge> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}
	private static String getColorFromType(String type) {
		switch(type) {
			case "border": return  "purple";
			case "bifurcation": return "blue";		
			case "added": return "red";	
			case "ending": return "green";
			case "ridge": return "blue";
			default: return "black";
		}	
	}
	
	public void printGraph(PrintStream ps, boolean isSubgraph) {

		ps.println("digraph{");
		rawPrint(ps, isSubgraph);
		ps.println("}");
		
	}
	
	public void rawPrint(PrintStream ps, boolean isSubgraph) {
		int i = 0;
		for(Node n:nodes) {			
			String color=isSubgraph?"yellow":getColorFromType(n.type);
			ps.println(n.getID()+" [label=\""+ i++ +"\", pos=\""+n.getX()+","+n.getY()+"!\", color=\""+color+"\"]");
		}
		for(Edge e:edges) {
			String color=isSubgraph?"yellow":getColorFromType(e.type);
			Node from = e.nodes[0];
			Node to = e.nodes[1];
			ps.println(from.getID()+" -> "+to.getID()+" [color=\""+color+"\", dir=none]");
		}
	
	}
	
	public void printMultipleGraphs(PrintStream ps, List<Graph> subgraphs) {
		ps.println("digraph{");
		rawPrint(ps, false);
		for(Graph g:subgraphs) {
			g.rawPrint(ps,true);
		}
		ps.println("}");
		
	}
	
	
	public List<Graph> subgraphs(int segment, int size) {

		List<Graph> subgraphs = new ArrayList<>();
		for(Node n:nodes) {
			if(!n.type.equals("bifurcation") && !n.type.equals("ending")) continue;
			
			List<Node> newNodes = new ArrayList<>();
			List<Edge> newEdges = new ArrayList<>();
			
			Range range = new Range(n.getX()-segment,n.getX()+segment,n.getY()-segment,n.getY()+segment);
			
			for(Node n2:nodes) {
				if(range.inside(n2.getX(),n2.getY())) {
					newNodes.add(n2);
				}
			}
			
			if(newNodes.size() < size) continue;
			for (Edge e:edges) {
				if(newNodes.contains(e.nodes[0]) && newNodes.contains(e.nodes[1])) {
					newEdges.add(e);
				}
			}
			
			subgraphs.add(new Graph(newNodes, newEdges));
			
			
		}
		
		List<Graph> results = new ArrayList<>(subgraphs);
		do {
			results = new ArrayList<>(subgraphs);
			
			for(Graph g1:results) {
				for(Graph g2:results.subList(results.indexOf(g1), results.size())) {
					if(g1==g2)continue;
					
					if(!g1.isDisjoint(g2)) {
						subgraphs.remove(g1);
						subgraphs.remove(g2);
						subgraphs.add(g1.merge(g2));
						break;
						
					}
				}
			}
			
		}while(!results.equals(subgraphs));
		
		return subgraphs;
	}
	
	public Graph merge(Graph other) {
		List<Node> newNodes = new ArrayList<>(nodes);
		List<Edge> newEdges = new ArrayList<>(edges);
		
		for(Node n:other.nodes) {
			if(!newNodes.contains(n)) {
				newNodes.add(n);
			}
		}
		for(Edge e:other.edges) {
			if(!newEdges.contains(e)) {
				newEdges.add(e);
			}
		}
		return new Graph(newNodes,newEdges);
	}
	
	public boolean isDisjoint(Graph other) {
		for(Node n:nodes) {
			if(other.nodes.contains(n)) {
				return false;
			}
		}
		return true;
	}
	
	public static class Node{
		public int[] coordinates;
		public String type;
		public int index;
		
		public Node(int [] coordinates, String type, int index) {
			this.coordinates = coordinates;
			this.type=type;
			this.index=index;
		}
		@Override
		public String toString() {
			return "x: "+coordinates[0]+" y: "+coordinates[1]+" ,type: "+type;
		}
		public String getID() {
			return "X"+coordinates[0]+"Y"+coordinates[1];
		}
		public int getX() {
			return coordinates[0];
		}
		public int getY() {
			return coordinates[1];
		}
	}
	
	public static class Edge{
		public Node[] nodes;
		public String type;
		public double distance;
			
		public Edge(Node[] nodes, String type, double distance) {
			this.nodes = nodes;
			this.type = type;
			this.distance = distance;		
		}	
	}
	
	public static class Range{
		private int minX;
		private int maxX;
		private int minY;
		private int maxY;
		
		public Range(int minX, int maxX, int minY, int maxY) {
			this.minX = minX;
			this.maxX = maxX;
			this.minY = minY;
			this.maxY = maxY;
		}
		
		public boolean inside(int x, int y) {
			return x>= minX && x <= maxX && y>=minY && y<=maxY;
		}
		
	}
}
