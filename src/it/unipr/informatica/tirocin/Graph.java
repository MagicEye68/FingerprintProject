package it.unipr.informatica.tirocin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import it.unipr.informatica.tirocin.print.DotFileTree;
import json.JSON;
import json.JSONArray;
import json.JSONIzer;
import json.ValueNotFoundException;

public class Graph {
	public List<Node> nodes;
	private List<Edge> edges;
	public String filename;
	public Graph(String NodeFile,String EdgeFile, String filename) throws ValueNotFoundException{
		this.filename=filename;
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
	
	public Graph(List<Node> nodes, List<Edge> edges, String filename) {
		this.nodes = nodes;
		this.edges = edges;
		this.filename = filename;
	}

	
	private boolean accepted(Node n) {
		return n.type.equals("bifurcation") || n.type.equals("ending");
		
	}
	

	
	//old with merges
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
			subgraphs.add(new Graph(newNodes, newEdges,this.filename));
			
			
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
	
    public List<Graph> subgraphsDFS(int segment, int min, int max) {
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
            
            if (newNodes.size() < min){
                newNodes.forEach((nn) -> added.remove(nn));
            } else {               
                result.add(new Graph(newNodes, newEdges,this.filename));
            }              
        }         
        return result;
    }
    
    public List<Graph> subgraphsBFS(int segment, int min, int max) {
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
            Queue<Node> queue = new LinkedList<>();
            queue.add(n);
            while(!queue.isEmpty() && newNodes.size() < min) {
                Node tmp = queue.poll();
                List<Node> neibor = neibors(tmp).stream().filter(node -> !added.contains(node)).filter(node -> r.inside(node.getX(), node.getY())).toList();
                for(Node n2:neibor) {
                	added.add(n2);
                	newNodes.add(n2);
                	newEdges.add(getEdgeFor(n,n2));
                	if(newNodes.size() == max) break;            	
                }
            }
   
            if (newNodes.size() < min){
                newNodes.forEach((nn) -> added.remove(nn));
            } else {               
                result.add(new Graph(newNodes, newEdges,this.filename));
            }              
        }         
        return result;
    }
	
	private List<Node> neibors(Node n){
        List<Node> result = new ArrayList<>();
        
        edges.stream().filter((e) -> e.nodes[0] == n).forEach((e) -> result.add(e.nodes[1]));
        edges.stream().filter((e) -> e.nodes[1] == n).forEach((e) -> result.add(e.nodes[0]));
        
        return result;
    }
	
    private Edge getEdgeFor(Node from, Node to) {
        for (Edge e : edges) {
            if ((e.nodes[0] == from && e.nodes[1] == to) || (e.nodes[1] == from && e.nodes[0] == to)) {
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
		return new Graph(newNodes,newEdges,this.filename);
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
		
		List<Graph>subgraph1 = this.subgraphsDFS(creation, treshold, treshold);
		List<Graph>subgraph2 = other.subgraphsDFS(creation, treshold, treshold);
		List<Graph> result = new ArrayList<Graph>();

		int counter=0;
		for(int i=0; i<subgraph1.size(); i++) {
			Node center1 = subgraph1.get(i).getCentered();
			Range range = new Range(center1.coordinates[0]-segment,center1.coordinates[0]+segment,
									center1.coordinates[1]-segment,center1.coordinates[1]+segment);
			
			for(int j=0; j<subgraph2.size(); j++) {			
				if(subgraph1.get(i).realInside(range) && subgraph2.get(j).realInside(range)) {
					if(subgraph1.get(i).graphCompare(subgraph2.get(j),counter) == true) {
						counter++;
						System.out.println("found something");
						result.add(subgraph1.get(i));
						result.add(subgraph2.get(j));
						
					}
				}
			}
		}
		
		return result;
	}

	
	public Node getCentered() {
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
		TreeNode root = new TreeNode(center.coordinates,center.type,center.index);
		DFSVisit(root,center,map);	
		return new Tree(root);
	}
	
	private void DFSVisit(TreeNode treeNode, Node graphNode, Map<Integer,Integer> colorMap) {
		
		 colorMap.put(graphNode.index, 1);
		 for(Edge e:edges) {
			 if(!((e.nodes[0] .equals(graphNode) && colorMap.get(e.nodes[1].index) == 0) || (e.nodes[1] .equals(graphNode) && colorMap.get(e.nodes[0].index) == 0))) {
				 continue;
			 }
			 Node n  = e.nodes[0].equals(graphNode)?e.nodes[1]:e.nodes[0];
			 TreeNode tmp =  new TreeNode(n.coordinates,n.type,n.index);
			 treeNode.addChild(tmp);
			 DFSVisit(tmp, n, colorMap); 
		 }
		 colorMap.put(graphNode.index, 2);
	}
	
    private Tree BFS() {
        Map<Integer, Integer> colorMap = new HashMap<>();
        for (Node n : nodes) {
        	//inizialize BFS kolors
            colorMap.put(n.index, 0);  
        }

        Node center = getCentered();
        TreeNode root = new TreeNode(center.coordinates, center.type, center.index);
        BFSVisit(root, center, colorMap);

        return new Tree(root);
    }
    
    private void BFSVisit(TreeNode root, Node center, Map<Integer, Integer> colorMap) {
        Queue<Pair<TreeNode, Node>> queue = new LinkedList<>();
        queue.add(new Pair<>(root, center));
        colorMap.put(center.index, 1);  

        while (!queue.isEmpty()) {
            Pair<TreeNode, Node> pair = queue.poll();
            TreeNode treeNode = pair.getKey();
            Node graphNode = pair.getValue();

            for (Edge e : edges) {
                Node n = null;
                if (e.nodes[0].equals(graphNode) && colorMap.get(e.nodes[1].index) == 0) {
                    n = e.nodes[1];
                } else if (e.nodes[1].equals(graphNode) && colorMap.get(e.nodes[0].index) == 0) {
                    n = e.nodes[0];
                }

                if (n != null) {
                    TreeNode tmp = new TreeNode(n.coordinates, n.type, n.index);
                    treeNode.addChild(tmp);
                    queue.add(new Pair<>(tmp, n));
                    colorMap.put(n.index, 1);  
                }
            }
            colorMap.put(graphNode.index, 2);  
        }
    }
	private boolean graphCompare(Graph other,int k) {
	
		if(nodes.size() != other.nodes.size()) {
			return false;
		}
		//per controllare l'isomorfismo uso sempre DFS
		Tree romeo = DFS();
		Tree luna = other.DFS();	
		//Tree romeo = BFS();
		//Tree luna = other.BFS();
		if(romeo.isomorph(luna)) {
			new DotFileTree("compared_outs"+ File.separator + this.filename+"-"+other.filename+"-"+k+"-tree")
			.printTree(romeo,this.filename)
			.printTree(luna, other.filename)
			.saveFile();
			return true;
		}

		return false;
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
	public Iterator<Node> nodesIterator(){
		return new NodesIterator();
	}
	
	private class NodesIterator implements Iterator<Node>{
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
	public Iterator<Edge> edgesIterator(){
		return new EdgesIterator();
	}
	private class EdgesIterator implements Iterator<Edge>{
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
	
    private static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
