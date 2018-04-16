package org.eclipse.emf.henshin.multicda.cda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

public class Pushout {
	HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;

	/**
	 * @return the mappingsOfRule1
	 */
	public List<Mapping> getRule1Mappings() {
		return toMappingList(rule1toPOmap);
	}

	/**
	 * @return the mappingsOfRule2
	 */
	public List<Mapping> getRule2Mappings() {
		return toMappingList(rule2toPOmap);
	}

	private List<Mapping> toMappingList(HashMap<Node, Node> map) {
		List<Mapping> result = new LinkedList<Mapping>();
		for (Node node : map.keySet()) {
			result.add(henshinFactory.createMapping(node, map.get(node)));
		}
		return result;
	}

	/**
	 * @return the resultGraph
	 */
	public Graph getResultGraph() {
		return graph;
	}

	Graph graph;
	Graph graphNoneDeletion;

	private HashMap<Node, Node> rule1toPOmap = new HashMap<Node, Node>();
	private HashMap<Node, Node> rule2toPOmap = new HashMap<Node, Node>();

	private Graph shadowGraph;

	/**
	 * Creates Pushout of L1 <-- S1 --> L2 and L1 --> G <-- L2
	 * 
	 * @param rule1
	 * @param s1span
	 * @param rule2
	 */
	public Pushout(Rule rule1, Span s1span, Rule rule2) {
		this(rule1, s1span, rule2, rule1, rule2);
	}

	public Pushout(Rule rule1, Span s1span, Rule rule2, Rule rule1NoneDelete, Rule rule2NoneDelete) {
		ConflictAnalysis.checkNull(rule1);
		ConflictAnalysis.checkNull(s1span);
		ConflictAnalysis.checkNull(rule2);
		if (!s1span.validate(rule1, rule2, rule1NoneDelete, rule2NoneDelete))
			throw new IllegalArgumentException("Span is in invalide state.");
		Graph l1 = rule1.getLhs();
		Graph l2 = rule2.getLhs();

		graph = preparePushoutGraph(l1);
		HashMap shadow2Rule2 = prepareShadowPushoutGraph(l2);

		Graph s1 = s1span.getGraph();
		for (Node node : s1.getNodes()) {
			glue(s1span, new SpanMappings(s1span), node, shadow2Rule2);
		}

		moveShadowContentsToPushout(graph, shadowGraph);

		validatePushout(l1, l2, s1);
		graph.setName("Pushout");

	}

	/**
	 * Creates Pushout of sp1 <-- Si --> sp2 and sp1 --> S <-- sp2
	 * 
	 * @param sp1
	 * @param Si
	 * @param sp2
	 */
	public Pushout(Span sp1, Span sp2) {
		ConflictAnalysis.checkNull(sp1);
		ConflictAnalysis.checkNull(sp2);

		Graph s1 = sp1.getGraph();
		Graph s2 = sp2.getGraph();

		graph = preparePushoutGraph(sp1.getGraph());
		rule2toPOmap = new HashMap<Node, Node>();

		for (Node n2 : s2.getNodes()) {
			Node found = null;
			for (Node n1 : graph.getNodes()) {
				if (Span.nodeEqual(n1, n2))
					found = n1;
				else if(Span.nodeContains(n1, n2)) {
					graph = null;
					return;
				}
				if (found != null)
					break;
			}
			if (found == null)
				found = HenshinFactory.eINSTANCE.createNode(graph, n2.getType(), n2.getName());
			rule2toPOmap.put(n2, found);
		}
		for (Edge e2 : s2.getEdges()) {
			boolean found = false;
			for (Edge e1 : graph.getEdges()) {
				found = Span.nodeEqual(e1.getSource(), e2.getSource())
						&& Span.nodeEqual(e1.getTarget(), e2.getTarget());
				if (found)
					break;
			}
			if (!found) {
				Node n1 = e2.getSource();
				Node n2 = e2.getTarget();
				for (Node n3 : graph.getNodes())
					if (Span.nodeEqual(n1, n3))
						n1 = n3;
					else if (Span.nodeEqual(n2, n3))
						n2 = n3;
				HenshinFactory.eINSTANCE.createEdge(n1, n2, e2.getType());
			}
		}
		graph.setName("Pushout");
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
		Copier copierForRule2 = new Copier();
		shadowGraph = (Graph) copierForRule2.copy(l2);
		copierForRule2.copyReferences();
		for (Node node : l2.getNodes()) {
			Node copyResultNode = (Node) copierForRule2.get(node);
			rule2toPOmap.put(node, copyResultNode);
		}
		HashMap<EObject, EObject> shadow2Rule2 = new HashMap<EObject, EObject>();
		for (EObject o : copierForRule2.keySet()) {
			shadow2Rule2.put(copierForRule2.get(o), o);
		}
		return shadow2Rule2;
	}

	private Graph preparePushoutGraph(Graph l1) {
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
		if (graph.getNodes().size() != numberOfExpectedNodes) {
			System.err.println("Number of nodes in created result graph (" + graph.getNodes().size()
					+ ") not as expected (" + numberOfExpectedNodes + "). Difference: "
					+ (graph.getNodes().size() - numberOfExpectedNodes));
		}
		int numberOfExpectedEdges = (l1.getEdges().size() + l2.getEdges().size() - s1.getEdges().size());
		if (graph.getEdges().size() != numberOfExpectedEdges) {
			System.err.println("Number of edges in created result graph (" + graph.getEdges().size()
					+ ") not as expected (" + numberOfExpectedEdges + "). Difference: "
					+ (graph.getEdges().size() - numberOfExpectedEdges));
		}
	}

}