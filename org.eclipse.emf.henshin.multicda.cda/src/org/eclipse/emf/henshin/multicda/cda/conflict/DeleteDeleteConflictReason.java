/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.conflict;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.multicda.cda.Span;

/**
 * @author vincentcuccu
 * 23.02.2018
 */
public class DeleteDeleteConflictReason extends DeleteUseConflictReason{

	private Span span2;
	private Span span1;

	/**
	 * @param s1
	 * @param s2 
	 */
	public DeleteDeleteConflictReason(Span s1, Span s2) {
		super(s1);
		span1 = s1;
		this.span2 = s2;
	}
	
	/**
	 * 
	 */
	public void print() {
		if (span2 != null) {
			System.out.println("<( " + this.getGraph().getEdges() + "\t| " + this.getGraph().getNodes() + " )"
					+ "( " + span2.getGraph().getEdges() + " |\t" + span2.getGraph().getNodes() + " )>");
		}
	}
	
	@Override
	public int hashCode() {
		return span1.hashCode() * 11 + hashForSpan2() * 37;
	}
	
	
	private int hashForSpan2() {
		int result = 0;
		Graph graph = span2.getGraph();
		EList<Node> nodes = graph.getNodes();
		EList<Edge> edges = graph.getEdges();

		for (Node node : nodes) {
			result += hashNode2(node);
		}

		for (Edge edge : edges) {
			if (edge.getSource() == null || edge.getTarget() == null) {
				result += 0;
			} else {
				result += hashNode2(edge.getSource()) * 101 + hashNode2(edge.getTarget()) * 53
						+ edge.getType().getName().hashCode() * 37;
			}
		}
		return result;
	}

	/**
	 * @param result
	 * @param node
	 * @return
	 */
	private int hashNode2(Node node) {
		String name = node.getName();
		EClass type = node.getType();
		int result = 0;
		if (name == null || type == null) {
			result  = 0;
		} else {
			String[] split = name.split(NODESEPARATOR);
			name = split[1] + "&&" + NODESEPARATOR + split[0];
			String name2 = type.getName();
			result = (name + ":" + name2).hashCode() * 13;
		}
		return result;
	}

}
