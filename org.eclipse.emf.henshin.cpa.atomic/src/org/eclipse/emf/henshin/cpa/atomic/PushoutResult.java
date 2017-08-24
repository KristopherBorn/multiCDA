package org.eclipse.emf.henshin.cpa.atomic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

// TODO: noch ist unklar ob eine solche Datenstruktur notwendig ist,
// oder es sich um Instanzen einer bereits bekannten Datenstruktur handelt.
// Je nach Ergebnis löschen oder in eigenständiges class-file auslagern.

// Generell: muss die matches m1 und m2 aus L1 und L2 enthalten und somit auch G.
// daher kennt es oder referenziert es auch (indirekt?) die beiden Regeln
public class PushoutResult {

	private static final boolean DELETE_DUPLICATE_EDGES = false;

	HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;

	/**
	 * @return the mappingsOfRule1
	 */
	public List<Mapping> getMappingsOfRule1() {
		return toMappingList(mappingsOfRule1);
	}

	/**
	 * @return the mappingsOfRule2
	 */
	public List<Mapping> getMappingsOfRule2() {
		return toMappingList(mappingsOfRule1);
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
		return resultGraph;
	}

	Graph resultGraph;

	private HashMap<Node, Node> mappingsOfRule1;
	private HashMap<Node, Node> mappingsOfRule2;

	@SuppressWarnings("unused")
	public PushoutResult(Rule rule1, Span s1span, Rule rule2) {

		// TODO: prüfen, dass alle mappings in die beiden Regeln verweisen, bzw.
		// keine der Regeln NULL ist. Sonst werfen einer Exception!
		// throw new IllegalStateException("blabla")
		// ggf. in "static" Methode (Span) s1span.isValid() auslagern die eine
		// "IllegalStateException" wirft
		// s1span.validate(rule1, rule2);
		ConflictAnalysis.checkNull(rule1);
		ConflictAnalysis.checkNull(s1span);
		ConflictAnalysis.checkNull(rule2);
		if (!s1span.validate(rule1, rule2))
			throw new IllegalArgumentException("Span is in invalide state.");

		// Predicate<Object> a = Objects::nonNull; // AHA Prädikate - keine
		// Ahnung!
		// Objects::nonNull(rule1); // bringt nichts, nur boolscher
		// Rückgabewert!
		// Objects.nonNull(rule2); // bringt nichts, nur boolscher Rückgabewert!

		Graph l1 = rule1.getLhs();
		mappingsOfRule1 = new HashMap<Node, Node>();
		Copier copierForRule1 = new Copier();
		Graph pushout = (Graph) copierForRule1.copy(l1);
		copierForRule1.copyReferences();
		for (Node node : l1.getNodes()) {
			Node copyResultNode = (Node) copierForRule1.get(node);
			mappingsOfRule1.put(node, copyResultNode);
		}
		Graph l2 = rule2.getLhs();
		mappingsOfRule2 = new HashMap<Node, Node>();
		Copier copierForRule2 = new Copier();
		Graph l2copy = (Graph) copierForRule2.copy(l2);
		copierForRule2.copyReferences();
		for (Node node : l2.getNodes()) {
			Node copyResultNode = (Node) copierForRule2.get(node);
			mappingsOfRule2.put(node, copyResultNode);
		}

		Graph s1 = s1span.getGraph();
		// replace common nodes in copyOfRule2 with the one in copyOfRule1
		for (Node node : s1.getNodes()) {
			// retarget associated edges
			// get associated node and mapping in both copies

			Node l1node = s1span.getMappingIntoRule1(node).getImage();
			Node l2node = s1span.getMappingIntoRule2(node).getImage();

			if (l1node == null || l2node == null) {
				System.out.println("Did not find a L1 or L2 counterpart for one of the nodes in S1!");
				// TODO: Exception werfen!
			} else {
				Node mergedNode = mappingsOfRule1.get(l1node);
				Node discardNode = mappingsOfRule2.get(l2node);
				mergedNode.setName(mergedNode.getName() + "," + discardNode.getName());

				Set<Edge> duplicateEdgesToDelete = new HashSet<Edge>();

				List<Edge> l2nodesIncoming = new LinkedList<Edge>(discardNode.getIncoming());
				for (Edge eIn : l2nodesIncoming) {
					// hier prüfen, ob es zwischen den beiden Knoten bereits
					// eine Kante des Typs gibt. Dann keine weitere Kante
					// erzeugen!
					if (DELETE_DUPLICATE_EDGES && mergedNode.getIncoming(eIn.getType(), eIn.getSource()) != null) {
						/* löschen der Kante */
						duplicateEdgesToDelete.add(eIn);
					} else {
						eIn.setTarget(mergedNode);
					}
				}

				List<Edge> l2nodesOutgoing = new LinkedList<Edge>(discardNode.getOutgoing());
				for (Edge eOut : l2nodesOutgoing) {
					// hier prüfen, ob es zwischen den beiden Knoten bereits
					// eine Kante des Typs gibt. Dann keine weitere Kante
					// erzeugen!
					if (DELETE_DUPLICATE_EDGES && mergedNode.getOutgoing(eOut.getType(), eOut.getTarget()) != null) {
						/* löschen der Kante */
						duplicateEdgesToDelete.add(eOut);
					} else {
						eOut.setSource(mergedNode);
					}
				}

				mappingsOfRule2.put(l2node, mergedNode);

				if (discardNode.getAllEdges().size() > 0) {
					System.err.println("All Edges of should have been removed, but still " + l2node.getAllEdges().size()
							+ " are remaining!");
				}

				// löschen des Knoten "nodeInL2y"
				Graph graphOfNodeL2 = discardNode.getGraph();
				boolean removedNode = graphOfNodeL2.removeNode(discardNode);
<<<<<<< HEAD
//				System.err.println("removedNode: " + removedNode);
=======
>>>>>>> dd9130c215467510a5725873183358d318c6c94e

				// löschen der doppelten Kanten
				if (DELETE_DUPLICATE_EDGES) {
					for (Edge edgeToDelete : duplicateEdgesToDelete) {
						graphOfNodeL2.removeEdge(edgeToDelete);
					}
				}
			}
		}

		List<Node> nodesInCopyOfLhsOfRule2 = new LinkedList<Node>(l2copy.getNodes());
		for (Node nodeInCopyOfLhsOfRule2 : nodesInCopyOfLhsOfRule2) {
			nodeInCopyOfLhsOfRule2.setGraph(pushout);
		}
		List<Edge> edgesInCopyOfLhsOfRule2 = new LinkedList<Edge>(l2copy.getEdges());
		for (Edge edgeInCopyOfLhsOfRule2 : edgesInCopyOfLhsOfRule2) {
			edgeInCopyOfLhsOfRule2.setGraph(pushout);
		}

		// check that NO edges and nodes are remaining in copyOfLhsOfRule2
		if (l2copy.getEdges().size() > 0) {
			System.err.println(l2copy.getEdges().size() + " edges remaining in " + l2copy + ", but should be 0");
		}
		if (l2copy.getNodes().size() > 0) {
			System.err.println(l2copy.getNodes().size() + " nodes remaining in " + l2copy + ", but should be 0");
		}

		// check that the number of edges and nodes in edgeInCopyOfLhsOfRule1 is
		// correct
		int numberOfExpectedNodes = (l1.getNodes().size() + l2.getNodes().size() - s1.getNodes().size());
		if (pushout.getNodes().size() != numberOfExpectedNodes) {
			System.err.println("Amount of nodes in created result graph of pushout not as expected. Difference: "
					+ (pushout.getNodes().size() - numberOfExpectedNodes));
		}
		// set copyOfLhsOfRule1 as resultGraph
		resultGraph = pushout;

	}

	private Mapping getMappingOfOrigin(List<Mapping> mappingsOfRules, Node origin) {
		for (Mapping mapping : mappingsOfRules) {
			if (mapping.getOrigin() == origin)
				return mapping;
		}
		return null;
	}

}