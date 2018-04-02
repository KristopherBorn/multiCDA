/**
 * 
 */
package org.eclipse.emf.henshin.model.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.SpanMappings;

/**
 * @author vincentcuccu
 * 02.04.2018
 */
public class ConflictPushout {

	private Graph graphLeft;
	private Span s;
	private Graph graphRight;
	private Graph resultGraph;
	private HashMap<Node, Node> rule1toPOmap;
	private HashMap<Node, Node> rule2toPOmap;

	protected Graph shadowGraph;
	
	/**
	 * @param graph1
	 * @param s
	 * @param graph2
	 */
	public ConflictPushout(Graph graph1, Span s, Graph graph2) {
		this.setGraph1(graph1);
		this.setS(s);
		this.setGraph2(graph2);
		
		resultGraph = preparePushoutGraph(graph1);
		HashMap shadow2Rule2 = prepareShadowPushoutGraph(graph2);
		
		Graph s1 = s.getGraph();
		for (Node node : s1.getNodes()) {
			glue(s, new SpanMappings(s), node, shadow2Rule2);
		}

		moveShadowContentsToPushout(resultGraph, shadowGraph);

		validatePushout(graph1, graph2, s1);
		resultGraph.setName("ConflictPushout");
	}
	
	/**
	 * @return
	 */
	public Graph getGraph1() {
		return graphLeft;
	}

	/**
	 * @param graph1
	 */
	public void setGraph1(Graph graph1) {
		this.graphLeft = graph1;
	}
	/**
	 * @return
	 */
	public Span getS() {
		return s;
	}

	/**
	 * @param s
	 */
	public void setS(Span s) {
		this.s = s;
	}
	/**
	 * @return
	 */
	public Graph getGraph2() {
		return graphRight;
	}

	/**
	 * @param graph2
	 */
	public void setGraph2(Graph graph2) {
		this.graphRight = graph2;
	}
	@SuppressWarnings("unused")
	private void glue(Span s1span, SpanMappings spanMappings, Node node, HashMap shadow2Rule2) {
		Node l1node = s1span.getMappingIntoRule1(node).getImage();
		Node l2node = s1span.getMappingIntoRule2(node).getImage();

		if (l1node == null || l2node == null) {
			throw new RuntimeException("Did not find a L1 or L2 counterpart for one of the nodes in S1!");
		} else {
			Node glueNode = rule1toPOmap.get(l1node);
			Node discardNode = rule2toPOmap.get(l2node);
			glueNode.setName(glueNode.getName() + "," + discardNode.getName());

			List<Edge> l2nodesIncoming = new LinkedList<Edge>(discardNode.getIncoming());
			for (Edge eIn : l2nodesIncoming) {
				if (spanMappings.getEdgeMappingsRule2S1().get(shadow2Rule2.get(eIn)) == null) {
					eIn.setTarget(glueNode);
				} else {
					eIn.getGraph().removeEdge(eIn);
				}
			}

			List<Edge> l2nodesOutgoing = new LinkedList<Edge>(discardNode.getOutgoing());
			for (Edge eOut : l2nodesOutgoing) {
				if (spanMappings.getEdgeMappingsRule2S1().get(shadow2Rule2.get(eOut)) == null) {
					eOut.setSource(glueNode);
				} else {
					eOut.getGraph().removeEdge(eOut);
				}
			}

			rule2toPOmap.put(l2node, glueNode);

			if (discardNode.getAllEdges().size() > 0) {
				System.err.println("All Edges of should have been removed, but still " + l2node.getAllEdges().size()
						+ " are remaining!");
			}

			Graph graphOfNodeL2 = discardNode.getGraph();
			boolean removedNode = graphOfNodeL2.removeNode(discardNode);
		}
	}

	private void moveShadowContentsToPushout(Graph pushout, Graph shadowpushout) {
		List<Node> nodesInCopyOfLhsOfRule2 = new LinkedList<Node>(shadowpushout.getNodes());
		for (Node nodeInCopyOfLhsOfRule2 : nodesInCopyOfLhsOfRule2) {
			nodeInCopyOfLhsOfRule2.setGraph(pushout);
		}
		List<Edge> edgesInCopyOfLhsOfRule2 = new LinkedList<Edge>(shadowpushout.getEdges());
		for (Edge edgeInCopyOfLhsOfRule2 : edgesInCopyOfLhsOfRule2) {
			edgeInCopyOfLhsOfRule2.setGraph(pushout);
		}

		if (shadowpushout.getEdges().size() > 0) {
			System.err.println(
					shadowpushout.getEdges().size() + " edges remaining in " + shadowpushout + ", but should be 0");
		}
		if (shadowpushout.getNodes().size() > 0) {
			System.err.println(
					shadowpushout.getNodes().size() + " nodes remaining in " + shadowpushout + ", but should be 0");
		}
	}

	private HashMap prepareShadowPushoutGraph(Graph l2) {
		rule2toPOmap = new HashMap<Node, Node>();
		Copier copierForRule2 = new Copier();
		 shadowGraph = (Graph) copierForRule2.copy(l2);
		copierForRule2.copyReferences();
		for (Node node : l2.getNodes()) {
			Node copyResultNode = (Node) copierForRule2.get(node);
			rule2toPOmap.put(node, copyResultNode);
		}
		HashMap<EObject,EObject> shadow2Rule2 = new HashMap<EObject, EObject>();
		for (EObject o : copierForRule2.keySet()) {
			shadow2Rule2.put(copierForRule2.get(o), o);
		}
		return shadow2Rule2;
	}

	private Graph preparePushoutGraph(Graph l1) {
		rule1toPOmap = new HashMap<Node, Node>();
		Copier copierForRule1Map = new Copier();
		Graph pushout = (Graph) copierForRule1Map.copy(l1);
		copierForRule1Map.copyReferences();
		for (Node node : l1.getNodes()) {
			Node copyResultNode = (Node) copierForRule1Map.get(node);
			rule1toPOmap.put(node, copyResultNode);
		}
		return pushout;
	}

	private void validatePushout(Graph l1, Graph l2, Graph s1) {
		int numberOfExpectedNodes = (l1.getNodes().size() + l2.getNodes().size() - s1.getNodes().size());
		if (resultGraph.getNodes().size() != numberOfExpectedNodes) {
			System.err.println("Number of nodes in created result graph (" + resultGraph.getNodes().size()
					+ ") not as expected (" + numberOfExpectedNodes + "). Difference: "
					+ (resultGraph.getNodes().size() - numberOfExpectedNodes));
		}
		int numberOfExpectedEdges = (l1.getEdges().size() + l2.getEdges().size() - s1.getEdges().size());
		if (resultGraph.getEdges().size() != numberOfExpectedEdges) {
			System.err.println("Number of edges in created result graph (" + resultGraph.getEdges().size()
					+ ") not as expected (" + numberOfExpectedEdges + "). Difference: "
					+ (resultGraph.getEdges().size() - numberOfExpectedEdges));
		}
	}

	
}
