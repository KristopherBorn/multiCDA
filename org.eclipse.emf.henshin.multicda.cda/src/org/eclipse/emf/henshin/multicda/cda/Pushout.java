package org.eclipse.emf.henshin.multicda.cda;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
		return getGraph();
	}

	private Graph graph;

	private HashMap<Node, Node> rule1toPOmap;
	private HashMap<Node, Node> rule2toPOmap;

	private Graph shadowGraph;

	/**
	 * @param rule1
	 * @param s1span
	 * @param rule2
	 */
	public Pushout(Rule rule1, Span s1span, Rule rule2) {
		ConflictAnalysis.checkNull(rule1);
		ConflictAnalysis.checkNull(s1span);
		ConflictAnalysis.checkNull(rule2);
		if (!s1span.validate(rule1, rule2))
			throw new IllegalArgumentException("Span is in invalide state.");
		Graph l1 = rule1.getLhs();
		Graph l2 = rule2.getLhs();

		setGraph(preparePushoutGraph(l1));
		HashMap shadow2Rule2 = prepareShadowPushoutGraph(l2);

		Graph s1 = s1span.getGraph();
		for (Node node : s1.getNodes()) {
			glue(s1span, new SpanMappings(s1span), node, shadow2Rule2);
		}

		moveShadowContentsToPushout(getGraph(), shadowGraph);

		validatePushout(l1, l2, s1);
		getGraph().setName("Pushout");

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
		HashMap<EObject, EObject> shadow2Rule2 = new HashMap<EObject, EObject>();
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
		if (getGraph().getNodes().size() != numberOfExpectedNodes) {
			System.err.println("Number of nodes in created result graph (" + getGraph().getNodes().size()
					+ ") not as expected (" + numberOfExpectedNodes + "). Difference: "
					+ (getGraph().getNodes().size() - numberOfExpectedNodes));
		}
		int numberOfExpectedEdges = (l1.getEdges().size() + l2.getEdges().size() - s1.getEdges().size());
		if (getGraph().getEdges().size() != numberOfExpectedEdges) {
			System.err.println("Number of edges in created result graph (" + getGraph().getEdges().size()
					+ ") not as expected (" + numberOfExpectedEdges + "). Difference: "
					+ (getGraph().getEdges().size() - numberOfExpectedEdges));
		}
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	@Override
	public String toString() {
		return "Pushout:\n" + graph.getEdges() + " : " + graph.getNodes() + "\nShadow graph:\n" + shadowGraph.getEdges() + " : " + shadowGraph.getNodes();
	}

}