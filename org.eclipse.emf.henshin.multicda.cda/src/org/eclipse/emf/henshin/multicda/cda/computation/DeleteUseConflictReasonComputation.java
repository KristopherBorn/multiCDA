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
import java.util.Objects;
import java.util.Set;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
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
import org.eclipse.emf.henshin.model.impl.NodeImpl;
import org.eclipse.emf.henshin.model.impl.NodePair;
import org.eclipse.emf.henshin.model.impl.RuleImpl;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.ConflictPushout;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.SpanMappings;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;
import org.hamcrest.core.IsNull;

import com.google.common.base.CaseFormat;

import agg.util.Pair;

import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;

/**
 * 
 * @author vincentcuccu 17.12.2017
 */
public class DeleteUseConflictReasonComputation {

	/**
	 * 
	 */
	private static final String NODESEPARATOR = "%";
	private Rule rule1;
	private Rule rule2;
	private Set<Span> conflictReasonsFromR2;

	private ConflictAnalysis analyser;
	private Set<Mapping> mapS1ToL1;
	private ConflictReasonComputation conflictHelper;
	private Span conflictReason;
	private Set<Mapping> mapS1ToL2;
	private String notCompatibleException;
	private Throwable notCompatible = new Throwable(notCompatibleException);
	private NotCompatibleException compatibleException = new NotCompatibleException("Not compatible!", notCompatible);
	private MinimalReasonComputation helperForCheckDangling;
	private HenshinFactoryImpl helper;

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
		helperForCheckDangling = new MinimalReasonComputation(rule1, rule2);

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
			Span DeleteDeleteSet = ConstructDeleteDeleteSet(rule1, rule2, conflictReason);
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
	private Span ConstructDeleteDeleteSet(Rule r1, Rule r2, Span sp1) {
		Pair<Span, Span> ddSet;
		for (Span sp2 : conflictReasonsFromR2) {
			Span s = compatibleSpans(sp1, sp2);
			if (s != null) {
				if (!isEmpty(s.getGraph())) {
					Pushout pushout = new Pushout(s.getRule1(), s, s.getRule2());
					Span l1Sl2 = computeUniquePushoutMorphisms(rule1, pushout, sp2.getRule1(), s); // TODO
					// VC
					// Benennung
					// verbessern
					Pushout po = new Pushout(rule1, l1Sl2, sp2.getRule1());
					if (helperForCheckDangling.findDanglingEdgesOfRule1(rule1, po.getRule1Mappings()).isEmpty()
							&& helperForCheckDangling.findDanglingEdgesOfRule1(rule2, po.getRule2Mappings())
									.isEmpty()) { // fullfillDanglingG(pushout)
						// Hier muss noch was rein
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param rule1
	 * @param pushout
	 * @param sap
	 * @return
	 */
	private Span computeUniquePushoutMorphisms(Rule rule1, Pushout pushout, Rule rule2, Span sap) {
		Span uniqueSpan = null;
		Graph pushoutGraph = pushout.getResultGraph();
		List<Mapping> s1ToS = pushout.getRule1Mappings();
		List<Mapping> s2ToS = pushout.getRule2Mappings();

		Graph lhs1 = rule1.getLhs();
		Graph lhs2 = rule2.getLhs();
		// Set<Mapping> mappingsInL1 = new HashSet<Mapping>();
		// Set<Mapping> mappingsInL2 = new HashSet<Mapping>();

		Set<Mapping> mappingsInL1 = uniqueMappingtoRule(pushout, rule1, sap); // S

		Set<Mapping> mappingsInL2 = uniqueMappingtoRule(pushout, rule2, sap); // S

		uniqueSpan = new Span(mappingsInL1, pushoutGraph, mappingsInL2);
		uniqueSpan.setRule1(rule1);
		uniqueSpan.setRule2(rule2);

		return uniqueSpan;
	}

	/**
	 * @param pushout
	 * @param sap 
	 * @param rule12
	 * @return
	 */
	private Set<Mapping> uniqueMappingtoRule(Pushout pushout, Rule rule, Span sap) {
		HashSet<Mapping> result = new HashSet<Mapping>();

		Graph pushoutGraph = pushout.getResultGraph();
		List<Mapping> s1ToS = pushout.getRule1Mappings();
		List<Mapping> s2ToS = pushout.getRule2Mappings();
		EList<GraphElement> elements = new BasicEList<GraphElement>();
		EList<Node> poNodes = pushoutGraph.getNodes();
		EList<Node> nodes = poNodes;
		EList<Edge> edges = pushoutGraph.getEdges();
		nodes.forEach(n -> elements.add(n));
		edges.forEach(e -> elements.add(e));
		
		Graph s1 = helper.createGraph("S1");
		Graph s2 = helper.createGraph("S2");
		Graph sapGraph = sap.getGraph();
		
		for (Node node : poNodes){
			nodeFromS1 = getMappingInRule(node, s1ToS);
		}
		
		for (GraphElement s : elements) {
			if (s instanceof Node) {
				
			} else if (s instanceof Edge) {

			}
		}

		return result;
	}

	// /**
	// * @param pushout
	// * @return
	// */
	// private Set<Mapping> uniqueMappingToL1(Pushout pushout) {
	//
	// Set<Mapping> uniqMappings = new HashSet<Mapping>(); // Das ist unser x =
	// // S --x--> LHS
	// // Durch pushout existiert S' -- a --> S1 -- c --> S schon.
	// Graph s = pushout.getResultGraph();
	// Span s1 = pushout.getSpan1();
	// Span s2 = pushout.getSpan2();
	//
	// Set<Mapping> a = sap.getMappingsInRule1();
	// Set<Mapping> b = sap.getMappingsInRule2();
	// Set<Mapping> e = s1.getMappingsInRule1();
	// Set<Mapping> f = s2.getMappingsInRule2();
	//
	// ArrayList<Node> ae = new ArrayList<Node>(checking(a, s1, e));
	// ArrayList<Node> bf = new ArrayList<Node>(checking(b, s2, f));
	//
	// Comparator<Node> comp = new Comparator<Node>() {
	//
	// @Override
	// public int compare(Node n1, Node n2) {
	// String o1 = n1.getName();
	// String o2 = n2.getName();
	// int origins = o1.compareTo(o2);
	//
	// return origins;
	// }
	// };
	// ae.sort(comp);
	// bf.sort(comp);
	//
	// if (!ae.isEmpty() && !bf.isEmpty() && ae.size() == bf.size()) {
	// for (int i = 0; i < ae.size(); i++) {
	// if (!ae.get(i).toString().equals(bf.get(i).toString())) {
	// return null;
	// }
	//
	// }
	// }
	//
	// EList<Node> sNodes = s.getNodes();
	// for (Node node : sNodes) {
	// Node c = pushout.getMappingIntoSpan1(node);
	// Node d = pushout.getMappingIntoSpan2(node);
	// if (c == null && d == null) {
	// return null;
	// }
	// if (c != null) {
	// Node image = s1.getMappingIntoRule1(c).getImage();
	// Mapping createMapping = helper.createMapping(node, image);
	// uniqMappings.add(createMapping);
	// } else {
	// if (d != null) {
	// Node image = s2.getMappingIntoRule1(d).getImage();
	// Mapping createMapping = helper.createMapping(node, image);
	// uniqMappings.add(createMapping);
	// }
	// }
	//
	// }
	//
	// // TODO VC EdgeMappings
	//
	// return uniqMappings;
	// }

	// /**
	// * @param pushout
	// * @return
	// */
	// private Set<Mapping> uniqueMappingToL2(Pushout pushout) {
	//
	// Set<Mapping> uniqMappings = new HashSet<Mapping>(); // Das ist unser x =
	// // S --x--> LHS
	// // Durch pushout existiert S' -- a --> S1 -- c --> S schon.
	// Graph s = pushout.getGraph();
	// Span sap = pushout.getSap();
	// Span s1 = pushout.getSpan1();
	// Span s2 = pushout.getSpan2();
	//
	// Set<Mapping> a = sap.getMappingsInRule1();
	// Set<Mapping> b = sap.getMappingsInRule2();
	// Set<Mapping> e = s1.getMappingsInRule2();
	// Set<Mapping> f = s2.getMappingsInRule1();
	//
	// ArrayList<Node> ae = new ArrayList<Node>(checking(a, s1, e));
	// ArrayList<Node> bf = new ArrayList<Node>(checking(b, s2, f));
	//
	// Comparator<Node> comp = new Comparator<Node>() {
	//
	// @Override
	// public int compare(Node n1, Node n2) {
	// String o1 = n1.getName();
	// String o2 = n2.getName();
	// int origins = o1.compareTo(o2);
	//
	// return origins;
	// }
	// };
	// ae.sort(comp);
	// bf.sort(comp);
	//
	// if (!ae.isEmpty() && !bf.isEmpty() && ae.size() == bf.size()) {
	// for (int i = 0; i < ae.size(); i++) {
	// if (!ae.get(i).toString().equals(bf.get(i).toString())) {
	// return null;
	// }
	//
	// }
	// }
	//
	// EList<Node> sNodes = s.getNodes();
	// for (Node node : sNodes) {
	// Node c = pushout.getMappingIntoSpan2(node);
	// Node d = pushout.getMappingIntoSpan1(node);
	// if (c == null && d == null) {
	// return null;
	// }
	// if (c != null) {
	// Node image = s2.getMappingIntoRule1(c).getImage();
	// Mapping createMapping = helper.createMapping(node, image);
	// uniqMappings.add(createMapping);
	// } else {
	// if (d != null) {
	// Node image = s1.getMappingIntoRule1(d).getImage();
	// Mapping createMapping = helper.createMapping(node, image);
	// uniqMappings.add(createMapping);
	// }
	// }
	//
	// }
	//
	// return uniqMappings;
	// }

	private HashSet<Node> checking(Set<Mapping> sapToSpan, Span span, Set<Mapping> spanToLhs) {

		HashSet<Node> checkedNodes = new HashSet<Node>();

		for (Mapping mapping : sapToSpan) {
			Node spanOrigin = mapping.getOrigin();
			Node spanImage = mapping.getImage();
			Graph graph = span.getGraph();
			EList<Node> nodes = graph.getNodes();
			if (nodes.contains(spanImage)) {
				Node newOrigin = spanImage;
				Mapping mappingSpanToLhs = getMappingInRule(newOrigin, spanToLhs);
				Node lhsNode = mappingSpanToLhs.getImage();
				if (lhsNode == null) {

				} else {
					checkedNodes.add(lhsNode);
				}
			} else {

			}
		}

		return checkedNodes;

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
		helper = new HenshinFactoryImpl();
		Graph sApostroph = null;
		Span s = null;
		Span s2Apostrophe = null;
		Span s1Apostrophe = null;
		s1Apostrophe = compatibleElements(sp1, sp2);
		if (s1Apostrophe != null) {
			s2Apostrophe = compatibleElements(sp2, sp1);
			if (s2Apostrophe != null) {
				Span intersection = intersection(s1Apostrophe, s2Apostrophe);
				sApostroph = intersection.getGraph();
				if (intersection != null && sApostroph != null) {
					RuleImpl ruleSp1 = new RuleImpl();
					ruleSp1.setLhs(sp1.getGraph());
					RuleImpl ruleSp2 = new RuleImpl();
					ruleSp2.setLhs(sp2.getGraph());
					intersection.setRule1(ruleSp1);
					intersection.setRule2(ruleSp2);
					return intersection;
					// for (Node node : sApostroph.getNodes()) {
					// System.out.println("For " + node + " find mapping into
					// cr1");
					// for (Node s1Node : s1Apostrophe.getNodes()) {
					// NodePair s1NodePair = (NodePair) s1Node;
					// if (node instanceof NodePair) {
					// NodePair nodePair = (NodePair) node;
					// Node node1 = nodePair.getNode1();
					// Node node12 = s1NodePair.getNode1();
					// System.out.println("node is nodepair:" + nodePair);
					//
					// if (checkOriginNodes(node1, node12, "_")) {
					// setMapping1.add(helper.createMapping(node, node12));
					// }
					// }
					// }
					// System.out.println("For " + node + "find mapping into
					// cr2");
					// for (Node s2Node : s2Apostrophe.getNodes()) {
					// NodePair s2NodePair = (NodePair) s2Node;
					// if (node instanceof NodePair) {
					// NodePair nodePair = (NodePair) node;
					// Node node1 = nodePair.getNode2();
					// Node node12 = s2NodePair.getNode1();
					// System.out.println("node is nodepair:" + nodePair);
					// if (checkOriginNodes(node1, node12, "_")) {
					//
					// setMapping2.add(helper.createMapping(node, node12));
					// }
					// }
					// }
					// }
				}

				// sGraph = sApostroph;
				// s = new Span(setMapping1, sGraph, setMapping2);

			} else

			{
				return null;
			}
		} else

		{
			return null;
		}

		return s;
	}

	/**
	 * @param graph1
	 * @param graph2
	 *            intersection ist die Überschneidung der beiden Graphen
	 * @param sp2
	 * @param sp1
	 * @return
	 */
	private Span intersection(Span sp1, Span sp2) {
		Graph result = helper.createGraph("S'");
		Graph graph1 = sp1.getGraph();
		Graph graph2 = sp2.getGraph();
		EList<Node> s1Nodes = graph1.getNodes();
		EList<Edge> s1Edges = graph1.getEdges();
		EList<Node> s2Nodes = graph2.getNodes();
		EList<Edge> s2Edges = graph2.getEdges();
		Set<Mapping> mappingsIntoSpan1 = new HashSet<Mapping>();
		Set<Mapping> mappingsIntoSpan2 = new HashSet<Mapping>();
		Set<Mapping> sp1Map1 = sp1.getMappingsInRule1();
		Set<Mapping> sp1Map2 = sp1.getMappingsInRule2();

		for (Node node1 : s1Nodes) {
			for (Node node2 : s2Nodes) {
				if (checkOriginNodes(node1, node2, "%")) {
					Node newNode = helper.createNode(result, node1.getType(), node1.getName());
					Mapping mappingFromGraphToRule12 = sp1.getMappingIntoRule1(node1);
					Node mappingFromGraphToRule1 = mappingFromGraphToRule12.getImage();
					Mapping createMapping = helper.createMapping(newNode, mappingFromGraphToRule1);
					mappingsIntoSpan1.add(createMapping);
					Mapping mappingIntoRule2 = sp2.getMappingIntoRule1(node2);
					Node image = mappingIntoRule2.getImage();
					Mapping createMapping2 = helper.createMapping(newNode, image);
					mappingsIntoSpan2.add(createMapping2);
				}

			}
		}

		for (Edge e1 : s1Edges) {
			for (Edge e2 : s2Edges) {
				Node source1 = e1.getSource();
				Node source2 = e2.getSource();
				Node target1 = e1.getTarget();
				Node target2 = e2.getTarget();
				if (checkEdges(e1, e2, NODESEPARATOR)) {
					Node source = null;
					Node target = null;
					EReference type = e1.getType();
					for (Node node : result.getNodes()) {
						if (checkOriginNodes(node, source1, NODESEPARATOR)
								&& checkOriginNodes(node, source2, NODESEPARATOR)) {
							source = node;
						}
						if (checkOriginNodes(node, target1, NODESEPARATOR)
								&& checkOriginNodes(node, target2, NODESEPARATOR)) {
							target = node;
						}
					}
					if (source != null && target != null) {
						helper.createEdge(source, target, type);
					}
				}
			}
		}
		Span span = new Span(mappingsIntoSpan1, result, mappingsIntoSpan2);
		return span;
	}

	/**
	 * @param sp1
	 * @param sp2
	 * @param s1Apostrophe
	 * @return
	 */
	private Span compatibleElements(Span sp1, Span sp2) {
		Graph compatibleGraph = helper.createGraph();
		EList<Node> s1Nodes = sp1.getGraph().getNodes();
		EList<Edge> s1Edges = sp1.getGraph().getEdges();
		EList<GraphElement> allObjectsS1 = new BasicEList<GraphElement>();
		s1Nodes.forEach(n -> allObjectsS1.add(n));
		s1Edges.forEach(e -> allObjectsS1.add(e));
		Set<Mapping> mappingsIntoSpan1 = new HashSet<Mapping>();
		Set<Mapping> mappingsIntoSpan2 = new HashSet<Mapping>();

		for (GraphElement x : allObjectsS1) {
			try {
				GraphElement y = existCompatibleElement(x, sp1, sp2);
				if (y != null) {
					if (x instanceof Node) {
						EClass xType = ((Node) x).getType();
						Node xNode = (Node) x;
						Node yNode = (Node) y;
						String newName = xNode.getName() + NODESEPARATOR + yNode.getName();
						Node newNode = helper.createNode(compatibleGraph, xType, newName);
						Mapping createMapping = helper.createMapping(newNode, xNode);
						Mapping createMapping2 = helper.createMapping(newNode, yNode);
						mappingsIntoSpan1.add(createMapping);
						mappingsIntoSpan2.add(createMapping2);

					}
					if (x instanceof Edge) {
						Edge xEdge = (Edge) x;
						Edge yEdge = (Edge) y;
						Node xSource = xEdge.getSource();
						Node xTarget = xEdge.getTarget();
						Node ySource = yEdge.getSource();
						Node yTarget = yEdge.getTarget();
						String newSource = xSource.getName() + NODESEPARATOR + ySource.getName();
						String newTarget = xTarget.getName() + NODESEPARATOR + yTarget.getName();
						Node source = null;
						Node target = null;
						EReference type = xEdge.getType();
						for (Node node : compatibleGraph.getNodes()) {
							String name = node.getName();
							if (name.equals(newSource)) {
								source = node;
							}
							if (name.equals(newTarget)) {
								target = node;
							}
						}
						if (source != null && target != null) {
							helper.createEdge(source, target, type);
						}
					}

				}

			} catch (NotCompatibleException e) {
				System.out.println(e.getMessage() + e.getCause());
				compatibleGraph = null;
			}
		}

		Span comSpan = new Span(mappingsIntoSpan1, compatibleGraph, mappingsIntoSpan2);
		return comSpan;
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

			for (GraphElement y : allObjectsS2) {
				if (checkEquality(x, y, sp1)) {
					if (checkEquality(x, y, sp2)) {
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
	private boolean checkEquality(GraphElement x, GraphElement y, Span sp) {
		if (x instanceof Node && y instanceof Node) {
			Node n1 = (Node) x;
			Mapping s1 = getMappingInRule(n1, sp.mappingsInRule1);
			Node n2 = (Node) y;
			Mapping s2 = getMappingInRule(n2, sp.mappingsInRule2);
			try {
				if (checkOriginNodes(s1.getOrigin(), s2.getOrigin(), "_")) {
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
				return checkEdges(e1, e2, "_");
			} catch (NullPointerException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	private boolean checkEdges(Edge e1, Edge e2, String regex) {

		EReference e1Type = e1.getType();
		Node e1Source = e1.getSource();
		Node e1target = e1.getTarget();

		EReference e2Type = e2.getType();
		Node e2Source = e2.getSource();
		Node e2target = e2.getTarget();

		if (!e1Type.equals(e2Type)) {
			return false;
		}
		if (!checkOriginNodes(e1Source, e2Source, regex)) {
			return false;
		}
		if (!checkOriginNodes(e1target, e2target, regex)) {
			return false;
		}

		return true;

	}

	/**
	 * @param originNode
	 * @param mappingsInRule
	 * @return
	 */
	public Mapping getMappingInRule(Node originNode, Set<Mapping> mappingsInRule) {
		for (Mapping mapping : mappingsInRule) {
			if (checkOriginNodes(originNode, mapping.getOrigin(), "_")) { // Hier
				return mapping;
			}
		}
		return null;
	}

	/**
	 * @param originNode
	 * @param origin
	 * @return
	 */
	private boolean checkOriginNodes(Node originNode, Node originNode2, String regex) {
		String[] revert = originNode.getName().split(regex);
		String[] revertMapping = originNode2.getName().split(regex);
		return (revert[0].equals(revertMapping[0]) && revert[1].equals(revertMapping[1])
				|| revert[0].equals(revertMapping[1]) && revert[1].equals(revertMapping[0]))
				&& (originNode.getType().equals(originNode2.getType()));
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
		HenshinFactory helper = HenshinFactory.eINSTANCE; // wird zur
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
					Mapping mapping = helper.createMapping(origin, image);
					G1toG2.add(mapping);
				}
			}
		}
		return G1toG2;
	}

}
