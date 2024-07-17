package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import json.JSON;
import json.JSONArray;
import json.JSONIzer;
import json.ValueNotFoundException;

public class Graph {
	public List<Node> nodes;
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
	
	public void printGraphCompare(PrintStream ps, boolean isSubgraph) {

		ps.println("digraph{");
		rawPrintCompare(ps, isSubgraph);
		ps.println("}");
		
	}
	
	public void rawPrintCompare(PrintStream ps, boolean isSubgraph) {
		int i = 0;
		for(Node n:nodes) {			
			String color=isSubgraph?"green":"black";
			ps.println(n.getID()+" [label=\""+ i++ +"\", pos=\""+n.getX()+","+n.getY()+"!\", color=\""+color+"\"]");
		}
		for(Edge e:edges) {
			String color=isSubgraph?"green":"black";
			Node from = e.nodes[0];
			Node to = e.nodes[1];
			ps.println(from.getID()+" -> "+to.getID()+" [color=\""+color+"\", dir=none]");
		}
	
	}
	public void printMultipleGraphsCompare(PrintStream ps, List<Graph> subgraphs) {
		ps.println("digraph{");
		rawPrintCompare(ps, false);
		for(Graph g:subgraphs) {
			g.rawPrintCompare(ps,true);
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
				if(!n2.type.equals("bifurcation") && !n2.type.equals("ending")) continue;
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
	
	
	public List<Graph> compare (Graph other,int creation, int segment, int treshold){
		
		List<Graph>subgraph1 = this.subgraphs(creation, treshold);
		List<Graph>subgraph2 = other.subgraphs(creation, treshold);
		List<Graph> result = new ArrayList<Graph>();;
		System.out.println("sottografi impronta 1: "+subgraph1.size());
		System.out.println("sottografi impronta 2: "+subgraph2.size());
		int counter = 0;
		 List<Graph> max;
		 List<Graph> min;
		if (subgraph1.size()>subgraph2.size()) {
			 max = subgraph1;
			 min = subgraph2;
		}
		else {
			max = subgraph2;
			min = subgraph1;
		}
		for(int i=0; i<max.size(); i++) {
			Node center1 = max.get(i).getCentered();
			Range range = new Range(center1.coordinates[0]-segment,center1.coordinates[0]+segment,
									center1.coordinates[1]-segment,center1.coordinates[1]+segment);
			
			for(int j=0; j<min.size(); j++) {			
				Node center2 = min.get(j).getCentered();
				if(range.inside(center2.coordinates[0], center2.coordinates[1])) {
					if(max.get(i).graphCompare(min.get(j)) == true) {
						counter++;
						result.add(max.get(i));
					}
				}
			}
		}
		System.out.println("computed "+counter+" times.");
		
		return result;
	}
	private boolean graphCompare(Graph other) {
		if(nodes.size() != other.nodes.size()) {
			return false;
		}
		Tree romeo = DFS();
		Tree luna = other.DFS();		
		return romeo.isomorph(luna);
	}
	
	private Node getCentered() {
		Map<Node,Float> map = new HashMap<>();
		for(Node n:nodes) {
			float sum = 0;
			for(Node n2:nodes) {
				//distanza euclidea
				sum+=Math.sqrt(Math.pow(n.coordinates[0]-n2.coordinates[0], 2)+Math.pow(n.coordinates[1]-n2.coordinates[1], 2));
			}
			map.put(n, sum);
		}
		Node result = map.keySet().iterator().next();
		for(Node n:map.keySet()) {
			if(map.get(n)<map.get(result)) {
				result = n;
			}
		}
		return result;
	}
	
	public boolean isDisjoint(Graph other) {
		for(Node n:nodes) {
			if(other.nodes.contains(n)) {
				return false;
			}
		}
		return true;
	}
	
	private Tree DFS() {
		Map<Integer,Integer> map = new HashMap<>();
		for(Node n:nodes) {
			//inizialize DFS kolors
			map.put(n.index, 0);
		}
		Node center = getCentered();
		TreeNode root = new TreeNode(center.coordinates,center.type);
		DFSVisit(root,center,map);	
		return new Tree(root);
	}
	
	private void DFSVisit(TreeNode treeNode, Node graphNode, Map<Integer,Integer> colorMap) {
		
		 colorMap.put(graphNode.index, 1);
		 for(Edge e:edges) {
			 if(!(e.nodes[0] == graphNode && colorMap.get(e.nodes[1].index) == 0)) {
				 continue;
			 }
			 TreeNode tmp =  new TreeNode(e.nodes[1].coordinates,e.nodes[1].type);
			 treeNode.addChild(tmp);
			 DFSVisit(tmp, e.nodes[1], colorMap); 
		 }
		 colorMap.put(graphNode.index, 2);
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
