package org.eclipse.emf.henshin.cpa.atomic.cc;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.Node;

/**
 * The purpose of this data structure is to collect Henshin nodes and edges of a
 * particular graph without actually removing them from that graph.
 * 
 * @author strueber
 *
 */
public class SubGraph {
	Set<Node> nodes;
	Set<Edge> edges;

	public SubGraph(Set<Node> nodes, Set<Edge> edges) {
		super();
		this.nodes = nodes;
		this.edges = edges;
	}

	public Set<Node> getNodes() {
		return nodes;
	}

	public void setNodes(Set<Node> nodes) {
		this.nodes = nodes;
	}

	public Set<Edge> getEdges() {
		return edges;
	}

	public void setEdges(Set<Edge> edges) {
		this.edges = edges;
	}

	public boolean contains(GraphElement e) {
		return edges.contains(e) || nodes.contains(e);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Graph, ");
		sb.append(nodes.size());
		sb.append(" nodes, ");
		sb.append(edges.size());
		sb.append(" edges: [");
		Iterator<Node> it = nodes.iterator();
		while (it.hasNext()) {
			sb.append(it.next().getType().getName());
			if (it.hasNext())
				sb.append(",");
		}
		sb.append("] [");
		Iterator<Edge> eit = edges.iterator();
		while (eit.hasNext()) {
			sb.append(eit.next().getType().getName());
			if (eit.hasNext())
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}
}
