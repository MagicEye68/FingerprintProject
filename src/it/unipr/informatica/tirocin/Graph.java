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
	public List<Edge> edges;
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
	public Graph(List<Node> nodes, List<Edge> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}
	public Graph(Graph g) {
		
		this.nodes = new ArrayList<>(g.nodes);
		this.edges = new ArrayList<>(g.edges);
	}
	private boolean accepted(Node n) {
		return n.type.equals("bifurcation") || n.type.equals("ending");
		
	}
	
	public int getProcessedNodesSize(int offset) {
		Graph g = new Graph(this).removeAdded().removeBorders(offset);

		return g.nodes.size();
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
            
            
            newNodes.add(n);
            
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
            
            
            newNodes.add(n);
            
            added.add(n);
            
            Range r = new Range(n.getX()-segment, n.getX()+segment, n.getY()-segment, n.getY()+segment);
            Queue<Node> queue = new LinkedList<>();
            queue.add(n);
            while(!queue.isEmpty() && newNodes.size() < max) {
                Node tmp = queue.poll();
                List<Node> neighbor = neighbors(tmp).stream().filter(node -> !added.contains(node)).filter(node -> r.inside(node.getX(), node.getY())).toList();
                for(Node n2:neighbor) {
                	if(added.contains(n2))continue;
                	added.add(n2);
                	newNodes.add(n2);
                	newEdges.add(getEdgeFor(tmp,n2));
                	queue.add(n2);
                	if(newNodes.size() >= max) break;            	
                }
            }
   
            if (newNodes.size() == max) {
                result.add(new Graph(newNodes, newEdges, this.filename));
            } else {
                
                newNodes.forEach(added::remove);
            }             
        }    
        
        return result;
    }
	
	private List<Node> neighbors(Node n){
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
        List<Node> neighbors = neighbors(n).stream().filter(e -> !added.contains(e)).filter(e ->accepted(e)).toList();
        for (Node n2 : neighbors) {
            if (newnodes.size() == max)
                return;
            if (r.inside(n2.getX(), n2.getY()) && !added.contains(n2)) { 
            	try{
            		if(newedges.contains(getEdgeFor(n2,n)) || newedges.contains(getEdgeFor(n,n2)))System.out.println("warning");
            	}
            	catch(Exception ignored){};
            	
            	newedges.add(getEdgeFor(n, n2));
                newnodes.add(n2);
                added.add(n2);

                addRec(n2, r, newnodes, newedges, added, min, max);
                neighbors = neighbors(n).stream().filter(e -> !added.contains(e)).filter(e ->accepted(e)).toList();

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
	
	public boolean realInside(Range range) {
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
						//System.out.println("found something");
						
						result.add(subgraph1.get(i));
						result.add(subgraph2.get(j));
						subgraph2.remove(j);
						break;
						
					}
				}
			}
		}
		
		return result;
	}
	
	public Graph removeBorders(int offset){
			
    	List<Node> nodes = new ArrayList<>();
    	List<Edge> edges = new ArrayList<>();
		
		for(int i = 0; i < this.nodes.size(); i++) {
			Node n = this.nodes.get(i);
			Range range = new Range(this.nodes.get(i).coordinates[0]-offset,this.nodes.get(i).coordinates[0]+offset,
					this.nodes.get(i).coordinates[1]-offset,this.nodes.get(i).coordinates[1]+offset);
			boolean border = false;
			for(int j = 0 ; j<this.nodes.size(); j++) {
				Node n2 = this.nodes.get(j);
				if(n2.type.equals("border") && range.inside(n2.getX(), n2.getY())) {
					border=true;
					break;
				}
				
			}
			if(!border) {
				nodes.add(n);
			}
		}

		
		for(int i = 0; i < this.edges.size(); i++) {
			
			Edge e = this.edges.get(i);
			
			if(nodes.contains(e.getFrom()) && nodes.contains(e.getTo())) {
				edges.add(e);
			}
		}


		return new Graph(nodes,edges,this.filename);
	}
	public List<Graph> nodesCompare (Graph other,int creation, int segment, int treshold){

		
		List<Graph>subgraph1 = this.subgraphsDFS(creation, treshold, treshold);
		List<Graph>subgraph2 = other.subgraphsDFS(creation, treshold, treshold);
		List<Graph> result = new ArrayList<Graph>();
		
		for(int i=0; i<subgraph1.size(); i++) {
			Node center1 = subgraph1.get(i).getCentered();
			Range range = new Range(center1.coordinates[0]-segment,center1.coordinates[0]+segment,
									center1.coordinates[1]-segment,center1.coordinates[1]+segment);
			
			for(int j=0; j<subgraph2.size(); j++) {			
				
				if(subgraph1.get(i).realInside(range) && subgraph2.get(j).realInside(range)) {	
					if(subgraph1.get(i).graphCompare(subgraph2.get(j)) == true) {
						result.add(subgraph1.get(i));
						result.add(subgraph2.get(j));
						subgraph2.remove(j);
						
						break;				
				}
				}
			}
		}
		
		return result;
	}


	public List<Graph> lastCompare (Graph other,int creation, int segment, int treshold){
		
		List<Graph>subgraph1 = this.subgraphsBFS(creation, treshold, treshold);
		List<Graph>subgraph2 = other.subgraphsBFS(creation, treshold, treshold);
		List<Graph> result = new ArrayList<Graph>();
		
		for(int i=0; i<subgraph1.size(); i++) {
			Node center1 = subgraph1.get(i).getCentered();
			Range range = new Range(center1.coordinates[0]-segment,center1.coordinates[0]+segment,
									center1.coordinates[1]-segment,center1.coordinates[1]+segment);
			
			for(int j=0; j<subgraph2.size(); j++) {			
				if(subgraph1.get(i).realInside(range) && subgraph2.get(j).realInside(range)) {	
					if(subgraph1.get(i).lastGraphCompare(subgraph2.get(j)) == true) {
						result.add(subgraph1.get(i));
						result.add(subgraph2.get(j));
						break;				
					}
				}
			}
		}
		//System.out.println("found "+(result.size()/2)+" occurences.");
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
			//inizialize DFS colors
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
        	//inizialize BFS colors
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

    public Graph removeAdded() {
    	List<Node> nodes = new ArrayList<>();
    	List<Edge> edges = new ArrayList<>();
		for(int i = 0; i < this.edges.size(); i++) {
			
			Edge e = this.edges.get(i);
			
			if(!(e.type.equals("added") || e.getFrom().type.equals("added") || e.getTo().type.equals("added"))) {
				edges.add(e);
			}
		}
		for(int i = 0; i < this.nodes.size(); i++) {
			
			Node n = this.nodes.get(i);
			
			if(!(n.type.equals("added"))) {
				nodes.add(n);
			}
		}

		return new Graph(nodes,edges,this.filename);
    	
    }
	private boolean graphCompare(Graph other,int k) {
	
		if(nodes.size() != other.nodes.size()) {
			return false;
		}
		//per controllare l'isomorfismo uso sempre DFS
		Tree t1 = DFS();
		Tree t2 = other.DFS();	
		//Tree t1 = BFS();
		//Tree t2 = other.BFS();
		
			if(t1.isomorph(t2)) {
				if(!(this.filename.equals(other.filename))) {
				new DotFileTree("compared_outs"+ File.separator + this.filename+"-"+other.filename+"-"+k+"-tree")
				.printTree(t1,this.filename)
				.printTree(t2, other.filename)
				.saveFile();
				}
				return true;	
		}


		return false;
	}
	private boolean lastGraphCompare(Graph other) {
		int ending = 0;
		int bifurcation = 0;
		if(nodes.size() != other.nodes.size()) {
			return false;
		}
		
		for(int i = 0; i < this.nodes.size(); i++) {
			switch(this.nodes.get(i).type) {
			case "border":
				break;
			case "added":
				break;
			case "bifurcation":
				bifurcation++;
				break;
			case "ending":
				ending++;
				break;
			default:
				throw new RuntimeException("unexpected type "+nodes.get(i).type);
			}
			
			switch(other.nodes.get(i).type) {
			case "border":
				break;
			case "added":
				break;
			case "bifurcation":
				bifurcation--;
				break;
			case "ending":
				ending--;
				break;
			default:
				throw new RuntimeException("unexpected type "+other.nodes.get(i).type);
			}
			
		}

		if(bifurcation == 0 && ending == 0) return true;

		return false;
	}	
	private boolean graphCompare(Graph other) {
		
		if(nodes.size() != other.nodes.size()) {
			return false;
		}
		//per controllare l'isomorfismo uso sempre DFS
		Tree t1 = DFS();
		Tree t2 = other.DFS();	
		if(t1.isomorph(t2)) {
			return true;
		}

		return false;
	}


    public static double f(int x) {
        if (x == 0) {
            return 0;
        } else if (x <= 10) {
            return (50 / Math.log(11)) * Math.log(x + 1);
        } else {
            return 50;
        }
    }

	public List<Box> boxesCompare (Graph other, int righe, int colonne){

		Graph g1 = new Graph(this);
		Graph g2 = new Graph(other);
		List<Box> boxArray = new ArrayList<Box>();
		int maxX=0;
		int minX=10000;
		int maxY=0;
		int minY=10000;
		
		for (Node n:g1.nodes) {
			if(n.getX()>maxX) maxX = n.getX();
			if(n.getX()<minX) minX = n.getX();
			if(n.getY()>maxY) maxY = n.getY();
			if(n.getY()<minY) minY = n.getY();
		}
		
		for (Node n:g2.nodes) {
			if(n.getX()>maxX) maxX = n.getX();
			if(n.getX()<minX) minX = n.getX();
			if(n.getY()>maxY) maxY = n.getY();
			if(n.getY()<minY) minY = n.getY();
		}
		maxX++;
		minX--;
		maxY++;
		minY--;
		double segmentX = ((double)maxX-(double)minX)/colonne;
		double segmentY = ((double)maxY-(double)minY)/righe;
		double nuovoMinY=minY;	
		double nuovoMaxY=minY+segmentY;
				
		for(int i = 0; i < righe; i++) {

			double nuovoMinX=minX;
			double nuovoMaxX = minX+segmentX;
			
			for(int j = 0; j < colonne; j++) {
				boxArray.add(new Box((i*colonne)+j,nuovoMinX,nuovoMaxX,nuovoMinY,nuovoMaxY));
				nuovoMinX=nuovoMaxX;
				nuovoMaxX+=segmentX;
			}
			nuovoMinY=nuovoMaxY;
			nuovoMaxY+=segmentY;
			
		}

		for(Node n:g1.nodes) {			
			int colonna = (int)((double)(n.getX()-minX)/segmentX);
			int riga = (int)((double)(n.getY()-minY)/segmentY);
			int index = riga*colonne+colonna;
			Box b = boxArray.get(index);
			if(n.getX()<Math.round(b.getRange().getMinX()) || n.getX()>Math.round(b.getRange().getMaxX()) ||
					n.getY()<Math.round(b.getRange().getMinY()) || n.getY()>Math.round(b.getRange().getMaxY())) {
					throw new RuntimeException("node in incorrect box");
				}
			if(n.type.equals("bifurcation"))boxArray.get(index).increaseBifurcationCounterG1();
			if(n.type.equals("ending"))boxArray.get(index).increaseEndingCounterG1();
		
		}
		for(Node n:g2.nodes) {
			int colonna = (int)((double)(n.getX()-minX)/segmentX);
			int riga = (int)((double)(n.getY()-minY)/segmentY);
			int index = riga*colonne+colonna;
			Box b = boxArray.get(index);
			if(n.getX()<Math.round(b.getRange().getMinX()) || n.getX()>Math.round(b.getRange().getMaxX()) ||
					n.getY()<Math.round(b.getRange().getMinY()) || n.getY()>Math.round(b.getRange().getMaxY())) {
					throw new RuntimeException("node in incorrect box");
				}
			if(n.type.equals("bifurcation"))boxArray.get(index).increaseBifurcationCounterG2();
			if(n.type.equals("ending"))boxArray.get(index).increaseEndingCounterG2();
		}

		return boxArray;
	}
	
	public static class Box{
		private int index;
		private int bifurcationCounterG1;
		private int bifurcationCounterG2;
		private int endingCounterG1;
		private int endingCounterG2;
		private double boxScore;
		DoubleRange range;
		
		public Box(int index, double minX, double maxX, double minY, double maxY) {
			this.index=index;
			this.bifurcationCounterG1=0;
			this.endingCounterG1=0;
			this.bifurcationCounterG2=0;
			this.endingCounterG2=0;
			this.boxScore=0;
			range = new DoubleRange(minX,maxX,minY,maxY);
		}

		public int getIndex() {
			return index;
		}
		public DoubleRange getRange() {
			return range;
		}
		public int getBifurcationCounterG1() {
			return bifurcationCounterG1;
		}
		
		public int getEndingCounterG1() {
			return endingCounterG1;
		}
		public int getBifurcationCounterG2() {
			return bifurcationCounterG2;
		}
		public int getSumG1() {
			return endingCounterG1+bifurcationCounterG1;
		}
		public int getSumG2() {
			return endingCounterG2+bifurcationCounterG2;
		}
		
		public int getEndingCounterG2() {
			return endingCounterG2;
		}
		public double getBoxScore() {
			return boxScore;
		}
		public void increaseBifurcationCounterG1() {
			this.bifurcationCounterG1++;
		}
		public void increaseEndingCounterG1() {
			this.endingCounterG1++;
		}
		public void increaseBifurcationCounterG2() {
			this.bifurcationCounterG2++;
		}
		public void increaseEndingCounterG2() {
			this.endingCounterG2++;
		}

		public void setBoxScore(double counter) {
			this.boxScore=counter;
		}
		public boolean isEmpty() {
			if(this.getSumG1() == 0 && this.getSumG2() == 0)return true;
			return false;
		}
		
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
		@Override
		public int hashCode() {
			return 17*coordinates[0]*coordinates[1]*index;
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
		public String toString() {
			return "type:"+type+", distance:"+distance;
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
		public int getMinX() {
			return minX;
		}
		public int getMaxX() {
			return maxX;
		}
		public int getMinY() {
			return minY;
		}
		public int getMaxY() {
			return maxY;
		}
		public void setMinX(int value) {
			minX= value;
		}
		public void setMaxX(int value) {
			maxX= value;
		};
		public void setMinY(int value) {
			minY= value;
		}
		public void setMaxY(int value) {
			maxY= value;
		}
		public boolean inside(int x, int y) {
			return x>= minX && x <= maxX && y>=minY && y<=maxY;
		}
		
	}
	
	public static class DoubleRange{
		private double minX;
		private double maxX;
		private double minY;
		private double maxY;
		
		public DoubleRange(double minX, double maxX, double minY, double maxY) {
			this.minX = minX;
			this.maxX = maxX;
			this.minY = minY;
			this.maxY = maxY;
		}
		public double getMaxX() {
			return this.maxX;
		}
		public double getMaxY() {
			return this.maxY;
		}
		
		public double getMinX() {
			return this.minX;
		}
		public double getMinY() {
			return this.minY;
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
