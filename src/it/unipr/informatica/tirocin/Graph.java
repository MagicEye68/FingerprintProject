package it.unipr.informatica.tirocin;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
	
	private boolean accepted(Node n) {
		return n.type.equals("bifurcation") || n.type.equals("ending");
		
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
		//rawPrint(ps, false);
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

	public static void printComputedGraphs(PrintStream ps, Graph g1, Graph g2, Graph computed1, Graph computed2, int segment, String graphname1,  String graphname2 ) {
		ps.println("digraph{");
		ps.println("subgraph cluster_"+graphname1+"{");
		ps.println("label=\"" + graphname1 + "\";");
		ps.println("fontsize=1000;"); 
		List<Node> nodi=g1.nodes.stream().filter((n) -> !computed1.nodes.contains(n)).toList();
		
		for(Node n:nodi) {
			
			ps.println(n.getID()+"_0"+" [label=\""+ n.index +"\", pos=\""+n.getX()+","+n.getY()+"!\", color=\"lightgray\"]");
		}
		List<Edge> archi=g1.edges.stream().filter((n) -> !computed1.edges.contains(n)).toList();
		for(Edge e:archi) {
			String color=getColorFromType(e.type);
			Node from = e.nodes[0];
			Node to = e.nodes[1];
			ps.println(from.getID()+"_0"+" -> "+to.getID()+"_0"+" [color=\"lightgray\", dir=none]");
		}

		for(Node n:computed1.nodes) {	
			String color=getColorFromType(n.type);
			ps.println(n.getID()+"_0"+" [label=\""+ n.index +"\", pos=\""+n.getX()+","+n.getY()+"!\", color=\""+color+"\"]");
		}

		for(Edge e:computed1.edges) {
			String color=getColorFromType(e.type);
			Node from = e.nodes[0];
			Node to = e.nodes[1];
			ps.println(from.getID()+"_0"+" -> "+to.getID()+"_0"+" [color=\""+color+"\", dir=none]");
		}
		Node center = computed1.getCentered();
		printbox(0,center.getX(),center.getY(),segment,ps,"darkmagenta",0);
		ps.println("}");
		ps.println("subgraph cluster_"+graphname2+"{");
		ps.println("label=\"" + graphname2 + "\";");
		ps.println("fontsize=1000;"); 
		nodi=g2.nodes.stream().filter((n) -> !computed2.nodes.contains(n)).toList();
		for(Node n:nodi) {			
			ps.println(n.getID()+"_1"+" [label=\""+ n.index +"\", pos=\""+(n.getX()+600)+","+n.getY()+"!\", color=\"gray\"]");
		}
	    archi=g2.edges.stream().filter((n) -> !computed2.edges.contains(n)).toList();
		for(Edge e:archi) {
			Node from = e.nodes[0];
			Node to = e.nodes[1];
			ps.println(from.getID()+"_1"+" -> "+to.getID()+"_1"+" [color=\"gray\", dir=none]");
		}

		for(Node n:computed2.nodes) {
			String color=getColorFromType(n.type);
			ps.println(n.getID()+"_1"+" [label=\""+ n.index +"\", pos=\""+(n.getX()+600)+","+n.getY()+"!\", color=\""+color+"\"]");
		}

		for(Edge e:computed2.edges) {
			String color=getColorFromType(e.type);
			Node from = e.nodes[0];
			Node to = e.nodes[1];
			ps.println(from.getID()+"_1"+" -> "+to.getID()+"_1"+" [color=\""+color+"\", dir=none]");
		}
		printbox(1,(center.getX()+600),center.getY(),segment,ps,"darkmagenta",1);
		ps.println("}");
//		ps.println("subgraph cluster_overlap{");
//		ps.println("label=\"overlap\";");
//		ps.println("fontsize=1000;"); 
//		nodi=g1.nodes.stream().filter((n) -> !computed1.nodes.contains(n)).toList();
//		for(Node n:nodi) {			
//			ps.println(n.getID()+"_2"+" [label=\""+ n.index +"\", pos=\""+(n.getX()+1000)+","+n.getY()+"!\", color=\"lightgray\"]");
//		}
//		archi=g1.edges.stream().filter((n) -> !computed1.edges.contains(n)).toList();
//		for(Edge e:archi) {
//			Node from = e.nodes[0];
//			Node to = e.nodes[1];
//			ps.println(from.getID()+"_2"+" -> "+to.getID()+"_2"+" [color=\"lightgray\", dir=none]");
//		}
//
//		for(Node n:computed1.nodes) {	
//			String color=getColorFromType(n.type);
//			ps.println(n.getID()+"_2"+" [label=\""+ n.index +"\", pos=\""+(n.getX()+1000)+","+n.getY()+"!\", color=\""+color+"\"]");
//		}
//
//		for(Edge e:computed1.edges) {
//			String color=getColorFromType(e.type);
//			Node from = e.nodes[0];
//			Node to = e.nodes[1];
//			ps.println(from.getID()+"_2"+" -> "+to.getID()+"_2"+" [color=\""+color+"\", dir=none]");
//		}
//		center = computed1.getCentered();
//		printbox(2,(center.getX()+1000),center.getY(),segment,ps,"darkmagenta",2);
//		nodi=g2.nodes.stream().filter((n) -> !computed2.nodes.contains(n)).toList();
//		for(Node n:nodi) {			
//			ps.println(n.getID()+"_3"+" [label=\""+ n.index +"\", pos=\""+(n.getX()+1000)+","+n.getY()+"!\", color=\"gray\"]");
//		}
//	    archi=g2.edges.stream().filter((n) -> !computed2.edges.contains(n)).toList();
//		for(Edge e:archi) {
//			Node from = e.nodes[0];
//			Node to = e.nodes[1];
//			ps.println(from.getID()+"_3"+" -> "+to.getID()+"_3"+" [color=\"gray\", dir=none]");
//		}
//
//		for(Node n:computed2.nodes) {	
//			String color=getColorFromType(n.type);
//			ps.println(n.getID()+"_3"+" [label=\""+ n.index +"\", pos=\""+(n.getX()+1000)+","+n.getY()+"!\", color=\""+color+"\"]");
//		}
//
//		for(Edge e:computed2.edges) {
//			String color=getColorFromType(e.type);
//			Node from = e.nodes[0];
//			Node to = e.nodes[1];
//			ps.println(from.getID()+"_3"+" -> "+to.getID()+"_3"+" [color=\""+color+"\", dir=none]");
//		}
//		ps.println("}");
		ps.println("}");
	}
	private static void printbox(int i,int x, int y, int segment, PrintStream ps, String color,int gi) {
		int tx = x+segment<(500*(gi+1))?x+segment:500*(gi+1);
		int bx = x-segment>(500*gi)?x-segment:500*gi;
		int ty = y+segment<(500)?y+segment:500;
		int by = y-segment>(0)?y-segment:0;
		ps.println("TL"+i+" [pos=\""+tx+", "+by+"!\" shape=point, color=\""+color+"\"];");
		ps.println("TR"+i+" [pos=\""+tx+", "+ty+"!\" shape=point, color=\""+color+"\"];");
		ps.println("BL"+i+" [pos=\""+bx+", "+by+"!\" shape=point, color=\""+color+"\"];");
		ps.println("BR"+i+" [pos=\""+bx+", "+ty+"!\" shape=point, color=\""+color+"\"];");
		ps.println("TL"+i+" -> TR"+i+" [dir=none, color=\""+color+"\"];");
		ps.println("TR"+i+" -> BR"+i+" [dir=none, color=\""+color+"\"];");
		ps.println("BR"+i+" -> BL"+i+" [dir=none, color=\""+color+"\"];");
		ps.println("BL"+i+" -> TL"+i+" [dir=none, color=\""+color+"\"];");
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
			Set<Node> nodiCollegati = new HashSet<>();
			
				for(Edge e1:newEdges) {
					nodiCollegati.add(e1.nodes[0]);
					nodiCollegati.add(e1.nodes[1]);
				}
			
			newNodes=nodiCollegati.stream().toList();
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
	
    public List<Graph> subgraphs2(int segment, int min, int max) {
        List<Graph> result = new ArrayList<>();
        Set<Node> added = new HashSet<>();
        
        for (Node n : nodes) {
            if (added.contains(n) || !accepted(n)) continue;
                
            List<Node> newNodes = new ArrayList<>();
            List<Edge> newEdges = new ArrayList<>();
            
            // Add starting node
            newNodes.add(n);
            //Track
            added.add(n);
            
            Range r = new Range(n.getX()-segment, n.getX()+segment, n.getY()-segment, n.getY()+segment);
            addRec(n, r, newNodes, newEdges, added, min, max);
            
            if (newNodes.size()< min){
                newNodes.forEach((nn) -> added.remove(nn));
            } else {
            	
                
                result.add(new Graph(newNodes, newEdges));
            }
            
           
        }
        
         
        return result;
    }
	
	private List<Node> neibors(Node n){
        List<Node> result = new ArrayList<>();
        
        edges.stream().filter((e) -> e.nodes[0] == n).forEach((e) -> result.add(e.nodes[1]));
        
        return result;
    }
	
    private Edge getEdgeFor(Node from, Node to) {
        for (Edge e : edges) {
            if (e.nodes[0] == from && e.nodes[1] == to) {
                return e;
            }
        }
        
        throw new RuntimeException("Edge not found");
    }
    
    private void addRec(Node n, Range r, List<Node> newnodes, List<Edge> newedges, Set<Node> added, int min, int max) {
        List<Node> neibors = neibors(n).stream().filter(e -> !added.contains(e)).filter(e ->accepted(e)).toList();
        for (Node n2 : neibors) {
            if (newnodes.size() == max)
                return;
            if (r.inside(n2.getX(), n2.getY()) && !added.contains(n2)) { 
            	try{
            		if(newedges.contains(getEdgeFor(n2,n)) || newedges.contains(getEdgeFor(n,n2)))System.out.println("wanr9ng");
            	}
            	catch(Exception ignored){};
            	
            	newedges.add(getEdgeFor(n, n2));
                newnodes.add(n2);
                added.add(n2);

                addRec(n2, r, newnodes, newedges, added, min, max);
                neibors = neibors(n).stream().filter(e -> !added.contains(e)).filter(e ->accepted(e)).toList();

            }
        }
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
	
	private boolean realInside(Range range) {
		for(Node n:nodes) {
			if(!(range.inside(n.getX(), n.getY()))) {
				return false;
			}
		}
		
		return true;
	}
	
	
	public List<Graph> compare (Graph other,int creation, int segment, int treshold){
		
		List<Graph>subgraph1 = this.subgraphs2(creation, treshold, treshold);
		List<Graph>subgraph2 = other.subgraphs2(creation, treshold, treshold);
		List<Graph> result = new ArrayList<Graph>();
		//System.out.println("sottografi impronta 1: "+subgraph1.size());
		//System.out.println("sottografi impronta 2: "+subgraph2.size());
		//int counter = 0;

		for(int i=0; i<subgraph1.size(); i++) {
			Node center1 = subgraph1.get(i).getCentered();
			Range range = new Range(center1.coordinates[0]-segment,center1.coordinates[0]+segment,
									center1.coordinates[1]-segment,center1.coordinates[1]+segment);
			
			for(int j=0; j<subgraph2.size(); j++) {			
				if(subgraph1.get(i).realInside(range) && subgraph2.get(j).realInside(range)) {
					if(subgraph1.get(i).graphCompare(subgraph2.get(j)) == true) {
						//counter++;				
						result.add(subgraph1.get(i));
						result.add(subgraph2.get(j));
					}
				}
			}
		}
		//System.out.println("computed "+counter+" times.");
		
		return result;
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
	private boolean graphCompare(Graph other) {
		int thisEnding = 0;
		int thisBifurcation = 0;
		int otherEnding = 0;
		int otherBifurcation = 0;
		for(Node n:nodes) {
		if(n.type.equals("bifurcation")) {
			thisBifurcation++;
		}
		else if(n.type.equals("ending")) {
			thisEnding++;
		}
		}
		
		for(Node n:other.nodes) {
		if(n.type.equals("bifurcation")) {
			otherBifurcation++;
		}
		else if(n.type.equals("ending")) {
			otherEnding++;
		}
		}
		if(thisEnding != otherEnding || thisBifurcation != otherBifurcation) {
			return false;
		}
		
		if(nodes.size() != other.nodes.size()) {
			return false;
		}
		Tree romeo = DFS();
		Tree luna = other.DFS();		
		return romeo.isomorph(luna);
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
			return "x: "+coordinates[0]+" y: "+coordinates[1]+" ,type: "+type+" ,index: "+index;
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
		@Override
		public boolean equals(Object other) {
			if(!(other instanceof Node)) return false;
			Node n = (Node)other;
			return n.index == index;
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
		public Node getFrom() {
			return nodes[0];
		}
		public Node getTo() {
			return nodes[1];
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
	
	public class NodesIterator implements Iterator<Node>{
		private int current;
		public NodesIterator() {
			current = 0;
		}
		@Override
		public boolean hasNext() {
			return current != nodes.size();
		}

		@Override
		public Node next() {			
			return nodes.get(current++);
		}	
	}
	
	public class EdgesIterator implements Iterator<Edge>{
		private int current;
		public EdgesIterator() {
			current = 0;
		}
		@Override
		public boolean hasNext() {
			return current != edges.size();
		}

		@Override
		public Edge next() {			
			return edges.get(current++);
		}
		
	}
}
