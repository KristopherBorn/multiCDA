/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.computation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.EdgePair;
import org.eclipse.emf.henshin.model.impl.GraphImpl;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;
import org.eclipse.emf.henshin.model.impl.NodePair;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;
import org.eclipse.ui.internal.handlers.WizardHandler.New;
import org.hamcrest.core.IsNull;

import com.google.common.base.CaseFormat;

import agg.util.Pair;

import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;

/**
 * 
 * @author vincentcuccu 17.12.2017
 */
public class DeleteUseConflictReasonComputation {

	private Rule rule1;
	private static Rule rule2;
	private Set<Span> conflictReasonsFromR2;

	private ConflictAnalysis analyser;
	private Set<Mapping> mapS1ToL1;
	private ConflictReasonComputation conflictHelper;
	private Span conflictReason;
	private Set<Mapping> mapS1ToL2;
	private String notCompatibleException;
	private Throwable notCompatible = new Throwable(notCompatibleException);
	private NotCompatibleException compatibleException = new NotCompatibleException("Juhu! Eine Exception: ",
			notCompatible);

	/**
	 * constructor
	 * 
	 * @param rule1
	 * @param rule2
	 * @param conflictReasonsFromR22
	 */
	public DeleteUseConflictReasonComputation(Rule rule1, Rule rule2, Set<Span> conflictReasonsFromR2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		this.conflictReasonsFromR2 = conflictReasonsFromR2;
	}

	/**
	 * constructs all Initial Reasons as candidates for r1 and r2
	 * 
	 * @param conflictReasons
	 * @return result
	 */
	public Set<DeleteUseConflictReason> computeDeleteUseConflictReason(Set<Span> conflictReasons) {
		Set<DeleteUseConflictReason> result = new HashSet<DeleteUseConflictReason>();
		for (Span conflictReason : conflictReasons) {
			computeDeleteUseConflictReasons(conflictReason, result);
		}
		return result;

	}

	/**
	 * the Method to encounter the delete read conflict reasons
	 * 
	 * @param conflictReason
	 * @param result
	 */
	private void computeDeleteUseConflictReasons(Span conflictReason, Set<DeleteUseConflictReason> result) {
		this.conflictReason = conflictReason;
		Rule rule1 = conflictReason.getRule1();
		Rule conflictRule2 = conflictReason.getRule2();
		// System.out.println("Mappings in Rule 1: " + L1S1L2);
		MinimalReasonComputation helperForCheckDangling = new MinimalReasonComputation(rule1, rule2);

		if (findEmbeddingS1toK2(conflictReason, rule2)) {// If (there exists
															// embedding
			// S1 to K2 with S1 to K2 to
			// L2 = mappingsInRule2) {

			Pushout pushout = new Pushout(rule1, conflictReason, conflictRule2);
			if (helperForCheckDangling.findDanglingEdgesOfRule1(rule1, pushout.getRule1Mappings()).isEmpty()
					&& helperForCheckDangling.findDanglingEdgesOfRule1(conflictRule2, pushout.getRule2Mappings())
							.isEmpty()) { // fullfillDanglingG(pushout)
				DeleteUseConflictReason res = new DeleteUseConflictReason(conflictReason);
				result.add(res);
			}
		} else {
			// System.out.println("noEmbeddingS1ToK2");
			mapS1ToL1 = conflictReason.mappingsInRule1;
			mapS1ToL2 = conflictReason.mappingsInRule2;
			GraphElement DeleteDeleteSet = ConstructDeleteDeleteSet(rule1, rule2, conflictReason);
			// If DD(s1) is nonEmpty
			// Then For each pair s2 in DD(s1):
			// Add (s1,s2) to DDCR
		}
	}

	/**
	 * @param r2
	 * @param r1
	 * @param sp1
	 * @return
	 */
	private GraphElement ConstructDeleteDeleteSet(Rule r1, Rule r2, Span sp1) {

		Pair<Span, Span> ddSet;
		for (Span sp2 : conflictReasonsFromR2) {
			Span s = compatibleSpans(sp1, sp2);
			if (s != null) {
				if (!isEmpty(s.getGraph())) {
					System.out.println(s);
					Pushout pushout = new Pushout(rule1, s, rule2); // TODO
																	// geht
																	// nicht,
																	// da
																	// Span
																	// is
																	// not
																	// validate
																	// :(
					// uniquePushout = computeUniquePushout(pushout,)
				}
			}
		}
		return null;
	}

	/**
	 * @param graph
	 * @return
	 */
	private boolean isEmpty(Graph graph) {
		if (graph.getNodes().isEmpty() && graph.getEdges().isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * @param sp2
	 * @param sp1
	 * @return
	 */
	private Span compatibleSpans(Span sp1, Span sp2) {
		HenshinFactoryImpl helper = new HenshinFactoryImpl();
		Graph sApostroph = null;
		Span s = null;
		Graph s2Apostrophe = null;
		Graph s1Apostrophe = null;
		s1Apostrophe = addCompatibleElements(sp1, sp2);
		if (s1Apostrophe != null) {
			s2Apostrophe = addCompatibleElements(sp2, sp1);
			if (s2Apostrophe != null) {
				sApostroph = intersection(s1Apostrophe, s2Apostrophe, sp1, sp2);
				Set<Mapping> s1 = new HashSet<Mapping>();
				Set<Mapping> s2 = new HashSet<Mapping>();
				if (sApostroph != null) {

					for (Node node : sApostroph.getNodes()) {
						for (Node node1 : s1Apostrophe.getNodes()) {
							NodePair nodePair = (NodePair) node1;
							Node nodePairNode1 = nodePair.getNode1();
							if (nodePairNode1.toString().equals(node.toString())) {
								s1.add(helper.createMapping(node, nodePairNode1));
							}
						}
						for (Node node2 : s2Apostrophe.getNodes()) {
							NodePair nodePair = (NodePair) node2;
							Node nodePairNode2 = nodePair.getNode2();
							if (nodePairNode2.toString().equals(node.toString())) {
								s2.add(helper.createMapping(node, nodePairNode2));
							}
						}

					}
				}

				s = new Span(s1, sApostroph, s2);
				s.rule1 = rule1;
				s.rule2 = rule2;
				if (!s.validate(rule1, rule2)) {
					System.out.println("s is not valide!");
				}
			} else {
				return null;
			}
		} else {
			return null;
		}

		return s;
	}

	/**
	 * @param s1Apostrophe
	 * @param s2Apostrophe
	 *            intersection ist die Überschneidung der beiden Graphen
	 * @param sp2
	 * @param sp1
	 * @return
	 */
	private Graph intersection(Graph s1Apostrophe, Graph s2Apostrophe, Span sp1, Span sp2) {

		Graph fromS1 = new HenshinFactoryImpl().createGraph("S1<-S1'->S2");
		Graph fromS2 = new HenshinFactoryImpl().createGraph("S1<-S2'->S2");
		Graph result = new HenshinFactoryImpl().createGraph("S'");

		EList<Node> s1Nodes = s1Apostrophe.getNodes();
		EList<Edge> s1Edges = s1Apostrophe.getEdges();
		EList<Node> s2Nodes = s2Apostrophe.getNodes();
		EList<Edge> s2Edges = s2Apostrophe.getEdges();

		for (Node node : s1Nodes) {
			NodePair nodePair = (NodePair) node;
			for (Node node2 : s2Nodes) {
				NodePair nodePair2 = (NodePair) node2;
				System.out.println(
						"NodePair: " + nodePair.getNode1().toString() + " : " + nodePair.getNode2().toString());
				System.out.println(
						"NodePair: " + nodePair2.getNode1().toString() + " : " + nodePair2.getNode2().toString());

				if (nodePair.getNode1().toString().equals(nodePair.getNode2().toString())
						&& nodePair2.getNode1().toString().equals(nodePair2.getNode2().toString())) {
					if (nodePair.getNode1().toString().equals(nodePair2.getNode1().toString())) {
						result.getNodes().add(nodePair.getNode1());
					}

				}

			}
		}

		for (Edge edge : s1Edges) {
			EdgePair edgePair = (EdgePair) edge;
			for (Edge edge2 : s2Edges) {
				EdgePair edgePair2 = (EdgePair) edge2;
				if (edgePair.getEdge1().toString().equals(edgePair.getEdge2().toString())
						&& edgePair2.getEdge1().toString().equals(edgePair2.getEdge2().toString())) {
					if (edgePair.getEdge1().toString().equals(edgePair2.getEdge1().toString())) {
						result.getEdges().add(edgePair.getEdge1());
					}

				}

			}
		}

		System.out.println("Intersection");

		
		// Todo Checken ob Edges Target und Source stimmen?
		return result;

	}

	/**
	 * @param sp1
	 * @param sp2
	 * @param s1Apostrophe
	 * @return
	 */
	private Graph addCompatibleElements(Span sp1, Span sp2) {
		Graph s1Apostrophe = new HenshinFactoryImpl().createGraph();
		EList<Node> s1Nodes = sp1.getGraph().getNodes();
		EList<Edge> s1Edges = sp1.getGraph().getEdges();
		// System.out.println(s1Nodes + " : " + s1Edges);
		// System.out.println(s2Nodes + " : " + s2Edges);
		EList<GraphElement> allObjectsS1 = new BasicEList<GraphElement>();
		s1Nodes.forEach(n -> allObjectsS1.add(n));
		s1Edges.forEach(e -> allObjectsS1.add(e));
		for (GraphElement x : allObjectsS1) {
			// System.out.println("----------------Suche nach Kompatiblem
			// Element für:\t" + x);
			try {
				GraphElement y = existCompatibleElement(x, sp1, sp2);

				if (y != null) {
					if (x instanceof Node) {
						NodePair nodePair = new NodePair((Node) x, (Node) y);
						s1Apostrophe.getNodes().add((NodePair) nodePair);
					}
					if (x instanceof Edge) {
						EdgePair edgePair = new EdgePair((Edge) x, (Edge) y);
						NodePair source = new NodePair(((Edge) x).getSource(), ((Edge) y).getSource());
						NodePair target = new NodePair(((Edge) x).getTarget(), ((Edge) y).getTarget());
						edgePair.setTarget(target);
						edgePair.setSource(source);
						System.out.println("EdgePair: " + edgePair);
						s1Apostrophe.getEdges().add((EdgePair) edgePair);
					}

				}

			} catch (NotCompatibleException e) {
				System.out.println(e.getMessage() + e.getCause());
				s1Apostrophe = null;
			}
		}
		return s1Apostrophe;
	}

	/**
	 * @param x
	 * @param sp1
	 * @param sp2
	 * @param conflictReason2
	 * @return
	 * @throws Exception
	 */
	private GraphElement existCompatibleElement(GraphElement x, Span sp1, Span sp2) throws NotCompatibleException {
		EList<GraphElement> allObjectsS1 = new BasicEList<GraphElement>();
		EList<GraphElement> allObjectsS2 = new BasicEList<GraphElement>();
		EList<Node> s1Nodes = sp1.getGraph().getNodes();
		EList<Edge> s1Edges = sp1.getGraph().getEdges();
		EList<Node> s2Nodes = sp2.getGraph().getNodes();
		EList<Edge> s2Edges = sp2.getGraph().getEdges();
		s1Nodes.forEach(n -> allObjectsS1.add(n));
		s1Edges.forEach(e -> allObjectsS1.add(e));
		s2Nodes.forEach(n -> allObjectsS2.add(n));
		s2Edges.forEach(e -> allObjectsS2.add(e));
		if (allObjectsS1.contains(x)) {
			// System.out.println(x + "\t is there!");
			// System.out.println("S2: " + allObjectsS2);
			for (GraphElement y : allObjectsS2) {
				// System.out.println("Checking for:\t" + y);
				if (checkEqualityMapping(x, y, sp1)) {
					if (checkEqualityMapping(x, y, sp2)) { // If s12(x) = s22(y)
															// L1 <-s21- S2
															// -s22-> L2
						// System.out.println(checkEqualityMapping(x, y, sp2));
						return y;
					} else {
						throw compatibleException;
					}
				}

			}
			return null;
		} else {
			throw compatibleException;
		}

	}

	/**
	 * @param x
	 * @param y
	 * @param sp
	 * @return
	 */
	private boolean checkEqualityMapping(GraphElement x, GraphElement y, Span sp) {
		// If s11(x) = s21(y) L1 <-s11- S1 -s12-> L2
		System.out.println("Checking:\t" + x + " ?= " + y);
		if (x instanceof Node && y instanceof Node) {

			// System.out.println("checkEqualityMapping(x): " + x);
			// System.out.println("checkEqualityMapping(y): " + y);
			// System.out.println("sp.getMappingsInRule1(): " +
			// sp.getMappingsInRule1());
			// System.out.println("sp.getMappingsInRule2(): " +
			// sp.getMappingsInRule2());
			// Mapping s1 = sp.getMappingIntoRule1((Node) x);
			Node n1 = (Node) x;
			Mapping s1 = getMappingInRule(n1, sp.mappingsInRule1);
			// Mapping s2 = sp.getMappingIntoRule2((Node) y);
			Node n2 = (Node) y;
			Mapping s2 = getMappingInRule(n2, sp.mappingsInRule2);
			// System.out.println("sp.getMappingIntoRule1((Node) x): " + s1);
			// System.out.println("sp.getMappingIntoRule1((Node) y): " + s2);

			try {
				if (s1.toString().equals(s2.toString())) {// Hier werden nur die
															// Strings
															// verglichen.
					return true;
				} else {
					return false;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				return false;
			}
		}
		if (x instanceof Edge && y instanceof Edge) {
			Edge e1 = (Edge) x;
			Edge e2 = (Edge) y;
			try {
				if (e1.getSource().toString().equals(e2.getSource().toString())) {
					if (e1.getTarget().toString().contentEquals(e2.getTarget().toString())) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		}
		return false;

	}

	public Mapping getMappingInRule(Node originNode, Set<Mapping> mappingsInRule) {
		for (Mapping mapping : mappingsInRule) {
			if (mapping.getOrigin().toString().equals(originNode.toString())) { // Hier
																				// werden
																				// nur
																				// die
																				// Strings
																				// verglichen.
				return mapping;
			}
		}
		return null;
	}

	/**
	 * returns true, if there is a match from S1 to K2, which is equal to the
	 * match of S1 to lhs of rule 2
	 * 
	 * @param conflictReason
	 * @param rule
	 * @return boolean
	 */
	public static boolean findEmbeddingS1toK2(Span conflictReason, Rule rule) {
		// Rule rule2 = conflictReason.getRule2();
		Graph s1 = conflictReason.getGraph();
		Graph l2 = rule.getLhs();
		Action preserve = new Action(Action.Type.PRESERVE);
		// Get preserve Nodes of rule 2
		EList<Node> k2nodes = rule.getActionNodes(preserve);
		EList<Edge> k2Edges = rule.getActionEdges(preserve);
		// System.out.println("findEmbeddingS1toK2, k2edges: " + k2Edges);
		EList<Edge> l2Edges = conflictReason.getRule2().getActionEdges(preserve);
		// System.out.println("findEmbeddingS1toK2, l2edges: " + l2Edges);

		// S1 -> K2
		ArrayList<Mapping> s1tok2 = computeMappings(s1.getNodes(), k2nodes);
		// S1 -> L2
		ArrayList<Mapping> s1tol2 = computeMappings(s1.getNodes(), l2.getNodes());

		// Comparator to define sorting of Mappings
		// TODO: Push to MappingImpl?
		Comparator<Mapping> comp = new Comparator<Mapping>() {

			@Override
			public int compare(Mapping o1, Mapping o2) {
				String o1Origin = o1.getOrigin().getName();
				String o2Origin = o2.getOrigin().getName();
				String o1Image = o1.getImage().getName();
				String o2Image = o2.getImage().getName();
				int origins = o1Origin.compareTo(o2Origin);
				if (origins == 0) {
					return o1Image.compareTo(o2Image);
				}
				return origins;
			}
		};

		// System.out.println("vor sortiertung: " + s1tok2);
		s1tok2.sort(comp);
		// System.out.println("nach sortierung: " + s1tok2);
		// System.out.println("vor sortiertung: " + s1tol2);
		s1tol2.sort(comp);
		// System.out.println("nach sortierung: " + s1tol2);

		if (s1tok2.toString().equals(s1tol2.toString())) {
			if (k2Edges.toString().equals(l2Edges.toString())) {
				return true;
			}
		}
		return false;

	}

	/**
	 * computes Mappings of two ELists of Nodes our of two Graphs
	 * 
	 * @param graphNodes1
	 * @param graphNodes2
	 * @return
	 */
	private static ArrayList<Mapping> computeMappings(EList<Node> graphNodes1, EList<Node> graphNodes2) {
		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE; // wird zur
																	// Erstellung
																	// der
																	// Mappings
																	// benötigt
		ArrayList<Mapping> G1toG2 = new ArrayList<Mapping>();

		for (Node origin : graphNodes1) { // Alle Knoten in S1 sollen auf alle
											// Knoten in L1 gemappt werden
			for (Node image : graphNodes2) {
				if (origin.getType() == image.getType()) { // Nur wenn Typen
															// gleich sind.
					Mapping mapping = henshinFactory.createMapping(origin, image);
					G1toG2.add(mapping);
				}
			}
		}
		return G1toG2;
	}

}
