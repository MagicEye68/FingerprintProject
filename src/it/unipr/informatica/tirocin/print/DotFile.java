package it.unipr.informatica.tirocin.print;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unipr.informatica.tirocin.Graph;
import it.unipr.informatica.tirocin.Graph.Edge;
import it.unipr.informatica.tirocin.Graph.EdgesIterator;
import it.unipr.informatica.tirocin.Graph.Node;
import it.unipr.informatica.tirocin.Graph.NodesIterator;

public class DotFile {
	private String filename;
	private List<List<InnerNode>> nodes;
	private List<List<InnerEdge>> edges;
	public DotFile(String filename) {
		if(filename == null) throw new IllegalArgumentException("filename == null");
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
		this.filename=filename;
	}
	
	private int getSize() {
		return nodes.size();
	}
	
	public void printGraph(Graph g, int index, Color color) {
		if(g == null) throw new IllegalArgumentException("graph == null");
		if(index < 0 || index > getSize()) throw new IllegalArgumentException("index < 0");
		if (index == getSize()) {
			nodes.add(new ArrayList<InnerNode>());
			edges.add(new ArrayList<InnerEdge>());
		}
		List<InnerNode> _nodes = nodes.get(index);
		List<InnerEdge> _edges = edges.get(index);
		Iterator<Node> nIter = g.new NodesIterator();
		while(nIter.hasNext()) {		
			Node n = nIter.next();
			int x = n.getX();
			int y = n.getY();
			int pos = containsNodeAtXY(x,y,index);
			if(pos == -1) {
				//nodo nuovo
				_nodes.add(new InnerNode(x,y,color));
			} else {
				//cambia colore nodo 
				_nodes.get(pos).color = color;
			}
		}
		Iterator<Edge> eIter = g.new EdgesIterator();
		while(eIter.hasNext()) {
			Edge e = eIter.next();
			Node from = e.getFrom();
			Node to = e.getTo();
			int pos = containsEdge(from.getX(),from.getY(),to.getX(),to.getY(),index);
			if(pos == -1) {
				//arco nuovo
				_edges.add(new InnerEdge(from.getX(),from.getY(),to.getX(),to.getY(),color));
			} else {
				//cambia colore arco
				_edges.get(pos).color = color;
			}
		}
	}
	private static String stringFromColor(Color color) {
		switch(color) {
			case PURPLE: return  "purple";
			case BLUE: return "blue";		
			case RED: return "red";	
			case GREEN: return "green";
			case BLACK: return "black";
		}
		return "black";	
	}
	
	public void saveFile() {
		try (FileOutputStream fos = new FileOutputStream(filename +".dot");
				PrintStream ps = new PrintStream(fos);) {
			ps.println("digraph{");
			int size = getSize();
			for(int i = 0 ; i < size; ++i) {
				List<InnerNode> currentNodes = nodes.get(i);
				int j = 0;
				for(InnerNode n:currentNodes) {
					ps.printf("X%dY%dI%d [label=\"%d\", pos=\"%d,%d!\", color=\"%s\"]\n",
							n.x, n.y, i, j++, n.x, n.y, stringFromColor(n.color));
				}
				List<InnerEdge> currentEdges = edges.get(i);
				for(InnerEdge e:currentEdges) {
					ps.printf("X%dY%dI%d -> X%dY%dI%d[color=\"%s\", dir=none]\n",
							e.fromX, e.fromY, i, e.toX, e.toY, i, stringFromColor(e.color));
				}
			}
			ps.println("}");
		} catch (Exception e) {
			throw new RuntimeException(e.getCause());
		}
	}
	
	private int containsNodeAtXY(int x, int y, int index) {
		List<InnerNode> _nodes = nodes.get(index);
		for(int i=0; i < _nodes.size(); i++) {
			InnerNode n = _nodes.get(i);
			if(n.x == x && n.y == y) return i;
		}
		return -1;
		
	}
	
	private int containsEdge(int fromX, int fromY, int toX, int toY, int index) {
		List<InnerEdge> _edges = edges.get(index);
		for(int i=0; i < _edges.size(); i++) {
			InnerEdge e = _edges.get(i);
			if(e.fromX == fromX && e.fromY == fromY && 
			   e.toX == toX && e.toY == toY) 
				return i;
		}
		return -1;
		
	}
	private class InnerNode{
		int x;
		int y;	
		Color color;
		public InnerNode(int x, int y, Color color) {
			this.x=x;
			this.y=y;
			this.color=color;
		}
	}
	private class InnerEdge{
		int fromX;
		int fromY;
		int toX;
		int toY;
		Color color;
		public InnerEdge(int fromX, int fromY, int toX, int toY, Color color) {
			this.fromX=fromX;
			this.fromY=fromY;
			this.toX=toX;
			this.toY=toY;
			this.color=color;
		}
	}
}
