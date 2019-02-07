package genepi.haplocheck.steps.contamination.objects;

import java.util.ArrayList;

public class Tree {

	private ArrayList<Edge> edges;
	private ArrayList<Node> nodes;

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
}
