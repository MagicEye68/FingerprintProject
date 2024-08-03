package it.unipr.informatica.tirocin.print;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unipr.informatica.tirocin.Graph;
import it.unipr.informatica.tirocin.Graph.Edge;
import it.unipr.informatica.tirocin.Graph.Node;
import it.unipr.informatica.tirocin.Tree;

public class DotFileGraph {
	
	private static final int PADDING = 25;
	
	private String filename;
	private List<List<InnerNode>> nodes;
	private List<List<InnerEdge>> edges;
	private List<InnerBox> boxes;
	private List <String> labels;
	private List<Integer> offset;
	
	public DotFileGraph(String filename) {
		if(filename == null) throw new IllegalArgumentException("filename == null");
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
		boxes = new ArrayList<>();
		labels = new ArrayList<>();
		this.filename=filename;
		offset = new ArrayList<>();
	}
	
	private int getSize() {
		return nodes.size();
	}
	

	
	private int containsNodeAtXY(int x, int y, int index) {
		List<InnerNode> _nodes = nodes.get(index);
		for(int i=0; i < _nodes.size(); i++) {
			InnerNode n = _nodes.get(i);
			if(n.x == x && n.y == y)
				return i;
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
	
	public DotFileGraph printGraph(Graph g, int index, Color color, boolean printAll) {
		int maxX = 0;
		int minX = Integer.MAX_VALUE;
		if (g == null)
			throw new IllegalArgumentException("graph == null");
		if ((index < 0) || (index > getSize()))
			throw new IllegalArgumentException("index < 0");
		
		if (index == getSize()) {
			offset.add(0);
			nodes.add(new ArrayList<InnerNode>());
			edges.add(new ArrayList<InnerEdge>());
			labels.add(null);
		}
		
		List<InnerNode> _nodes = nodes.get(index);
		List<InnerEdge> _edges = edges.get(index);
		Iterator<Node> nIter = g.nodesIterator();
		while (nIter.hasNext()) {		
			Node n = nIter.next();
			Color color2 = color;
			if(color == null) color2 = getColorFromType(n.type);
			int x = n.getX();
			int y = n.getY();
			if (printAll) {
				// check if nodes already exist
				int pos = containsNodeAtXY(x,y,index);
				if (pos == -1) {
					//nodo nuovo
					_nodes.add(new InnerNode(x,y,color2));
				} else {
					//cambia colore nodo 
					_nodes.get(pos).color = color2;
				}
			} else {
				// insert without checking existence
				_nodes.add(new InnerNode(x,y,color2));
			}
			
			if (x > maxX)
				maxX = x;
			if(x < minX)
				minX = x;
		}
		int newOffset = maxX - minX;
		if(offset.get(index) < newOffset) 
			offset.set(index, newOffset);
		
		Iterator<Edge> eIter = g.edgesIterator();
		while (eIter.hasNext()) {

			Edge e = eIter.next();
			Color color2 = color;
			if(color == null) color2 = getColorFromType(e.type);
			Node from = e.getFrom();
			Node to = e.getTo();
			int pos = containsEdge(from.getX(),from.getY(),to.getX(),to.getY(),index);
			if (pos == -1) {
				//arco nuovo
				_edges.add(new InnerEdge(from.getX(),from.getY(),to.getX(),to.getY(),color2));
			} else {
				//cambia colore arco
				_edges.get(pos).color = color2;
			}
		}
		
		return this;
	}
	
	
	
	private static Color getColorFromType(String type) {
		switch(type) {
			case "border": return  Color.PURPLE;
			case "bifurcation": return Color.BLUE;		
			case "added": return Color.RED;	
			case "ending": return Color.GREEN;
			case "ridge": return Color.BLUE;
			default: return Color.LIGHTGRAY;
		}	
	}
	
	private static String stringFromColor(Color color) {
		switch(color) {
			case PURPLE: return  "purple";
			case BLUE: return "blue";		
			case RED: return "red";	
			case GREEN: return "green";
			case LIGHTGRAY: return "lightgray";
			case BLACK:return "black";
			
		default:
			break;
		}
		return "lightgray";	
	}
	
	
	public DotFileGraph printComputed(List<Graph> result,Color color) {
		for(int i = 0 ; i <result.size(); i++) {
			
			this.printGraph(result.get(i), i, color, true);

		}
		return this;
		
	}
	
	public DotFileGraph printBox(int centerX, int centerY, int segment, int index, Color color) {
		
		if (centerX - segment < 0)
			throw new IllegalArgumentException("centerX - segment < 0");
		if (centerY - segment < 0)
			throw new IllegalArgumentException("centerY - segment < 0");
		if (!(index < getSize()))
			throw new IllegalArgumentException("!(index < getSize())");
		
		boxes.add(new InnerBox(centerX, centerY, segment, index, color));

		if (centerX + segment > offset.get(index)) {
			int currentOffset = offset.get(index);
			currentOffset += centerX +segment - currentOffset;
			offset.set(index,currentOffset);
		}

		return this;
	}
	
	public DotFileGraph addLabel(String name, int index) {
		if (name == null)
			throw new IllegalArgumentException("name == null");
		if (!(index < getSize()))
			throw new IllegalArgumentException("no graph at index " + index);
		
		labels.set(index, name);
		return this;
	}
	
	
	
	public void saveFile() {
		offset.add(0,0);
		for(int i = 1; i<offset.size(); i++) {
			int prev = offset.get(i-1);
			offset.set(i, prev + offset.get(i));
		}
		
		try (FileOutputStream fos = new FileOutputStream(filename +".dot");
				PrintStream ps = new PrintStream(fos);) {
			ps.println("digraph{");
			
			int size = getSize();
			// print each subgraph
			for(int i = 0 ; i < size; ++i) {
				int shift = offset.get(i) + (PADDING * i);
				ps.printf("subgraph cluster_%d {\n", i);
				ps.printf("color = white\n");
				// print label
				if (labels.get(i) != null) {
					ps.printf("label = \"%s\";\n", labels.get(i));
					ps.printf("fontsize=1000;\n");
				}
					
				
				// print nodes
				List<InnerNode> currentNodes = nodes.get(i);
				int j = 0;
				for(InnerNode n:currentNodes) {
					ps.printf("X%dY%dI%d [label=\"%d\", pos=\"%d,%d!\", color=\"%s\"]\n",
							n.x + shift, n.y, i,
							j++, n.x + shift , n.y, stringFromColor(n.color));
				}
				
				// print edges
				List<InnerEdge> currentEdges = edges.get(i);
				for(InnerEdge e:currentEdges) {
					ps.printf("X%dY%dI%d -> X%dY%dI%d[color=\"%s\", dir=none]\n",
							e.fromX + shift, e.fromY, i,
							e.toX + shift, e.toY, i,
							stringFromColor(e.color));
				}
				
				ps.printf("}\n");	// close subgraph
			}
			
			// print boxes
			int j = 0;
			for (int i = 0; i != boxes.size(); ++i) {
				InnerBox box = boxes.get(i);
				int shift = offset.get(box.index) + (PADDING * box.index);
				String colorStr = stringFromColor(box.color);
				// nodes
				// bottom left
				ps.printf("box%d [pos = \"%d,%d!\", shape = point, color = \"%s\"]\n",
						j, box.x + shift, box.y, colorStr);
				// bottom right
				ps.printf("box%d [pos = \"%d,%d!\", shape = point, color = \"%s\"]\n",
						j + 1, box.x + box.len + shift, box.y, colorStr);
				// top left
				ps.printf("box%d [pos = \"%d,%d!\", shape = point, color = \"%s\"]\n",
						j + 2, box.x + shift, box.y + box.len, colorStr);
				// top right
				ps.printf("box%d [pos = \"%d,%d!\", shape = point, color = \"%s\"]\n",
						j + 3, box.x + box.len + shift, box.y + box.len, colorStr);
				// edges
				// bottom edge
				ps.printf("box%d -> box%d [dir=none color=\"%s\"]\n", j, j + 1, colorStr);
				// left edge
				ps.printf("box%d -> box%d [dir=none color=\"%s\"]\n", j, j + 2, colorStr);
				// right edge
				ps.printf("box%d -> box%d [dir=none color=\"%s\"]\n", j + 3, j + 1, colorStr);
				// top edge
				ps.printf("box%d -> box%d [dir=none color=\"%s\"]\n", j + 3, j + 2, colorStr);
				
				j += 4;
			}
			ps.println("}");
		} catch (Exception e) {
			throw new RuntimeException(e.getCause());
		}
		
		boxes = null;
		nodes = null;
		edges = null;
	}
	
	
	private static class InnerNode {
		int x;
		int y;	
		Color color;
		
		public InnerNode(int x, int y, Color color) {
			this.x=x;
			this.y=y;
			this.color=color;
		}
	}
	
	private static class InnerEdge {
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
	
	private static class InnerBox {
		private int x, y;
		private int len;
		private int index;
		private Color color;
		
		public InnerBox(int centerX, int centerY, int _len, int index, Color color) {
			x = centerX - _len;
			y = centerY - _len;
			len = _len * 2;
			this.index = index;
			this.color = color;
		}
	}
}
