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
import org.eclipse.emf.henshin.model.impl.NodePair;
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
	private NotCompatibleException compatibleException = new NotCompatibleException("Juhu! Eine Exception: ",
			notCompatible);
	private MinimalReasonComputation helperForCheckDangling;
	private Span intersection;
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
		Span span1 = new Span(sp1);
		Pair<Span, Span> ddSet;
		for (Span sp2 : conflictReasonsFromR2) {
			Span span2 = new Span(sp2);
			Graph span1Graph = span1.getGraph();
			Graph span2Graph = span2.getGraph();
			Span s = compatibleSpans(sp1, sp2, span1Graph, span2Graph);
			if (s != null) {
				if (!isEmpty(s.getGraph())) {
					System.out.println("S is not null and not empty:\t" + s);
					ConflictPushout pushout = new ConflictPushout(sp1, s, sp2);
					Span uniquePushout = computeUniquePushout(pushout, sp2);
					Pushout po = new Pushout(rule1, uniquePushout, sp2.getRule1());
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
	 * @param pushout
	 * @param sp2
	 * @param intersection2
	 * @return
	 */
	private Span computeUniquePushout(ConflictPushout pushout, Span sp2) {
		Span uniqueSpan = null;
		Graph s = pushout.getGraph();

		Graph lhs1 = rule1.getLhs();
		Rule rule2 = sp2.getRule1();
		Graph lhs2 = rule2.getLhs();

		Set<Mapping> mappingsInL1 = uniqueMappingToL1(pushout); // S --x-->
																// LHS1
		Set<Mapping> mappingsInL2 = uniqueMappingToL2(pushout); // S --x-->
																// LHS2

		uniqueSpan = new Span(mappingsInL1, s, mappingsInL2);
		uniqueSpan.setRule1(rule1);
		uniqueSpan.setRule2(rule2);

		return uniqueSpan;
	}

	/**
	 * @param pushout
	 * @return
	 */
	private Set<Mapping> uniqueMappingToL1(ConflictPushout pushout) {

		Set<Mapping> uniqMappings = new HashSet<Mapping>(); // Das ist unser x =
															// S --x--> LHS
		// Durch pushout existiert S' -- a --> S1 -- c --> S schon.
		Graph s = pushout.getGraph();
		Span sap = pushout.getSap();
		Span s1 = pushout.getSpan1();
		Span s2 = pushout.getSpan2();

		Set<Mapping> a = sap.getMappingsInRule1();
		Set<Mapping> b = sap.getMappingsInRule2();
		Set<Mapping> e = s1.getMappingsInRule1();
		Set<Mapping> f = s2.getMappingsInRule2();

		ArrayList<Node> ae = new ArrayList<Node>(checking(a, s1, e));
		ArrayList<Node> bf = new ArrayList<Node>(checking(b, s2, f));

		Comparator<Node> comp = new Comparator<Node>() {

			@Override
			public int compare(Node n1, Node n2) {
				String o1 = n1.getName();
				String o2 = n2.getName();
				int origins = o1.compareTo(o2);

				return origins;
			}
		};
		ae.sort(comp);
		bf.sort(comp);

		if (!ae.isEmpty() && !bf.isEmpty() && ae.size() == bf.size()) {
			for (int i = 0; i < ae.size(); i++) {
				if (!ae.get(i).toString().equals(bf.get(i).toString())) {
					return null;
				}

			}
		}

		EList<Node> sNodes = s.getNodes();
		for (Node node : sNodes) {
			Node c = pushout.getMappingIntoSpan1(node);
			Node d = pushout.getMappingIntoSpan2(node);
			if (c == null && d == null) {
				return null;
			}
			if (c != null) {
				Node image = s1.getMappingIntoRule1(c).getImage();
				Mapping createMapping = helper.createMapping(node, image);
				uniqMappings.add(createMapping);
			} else {
				if (d != null) {
					Node image = s2.getMappingIntoRule1(d).getImage();
					Mapping createMapping = helper.createMapping(node, image);
					uniqMappings.add(createMapping);
				}
			}

		}

		return uniqMappings;
	}
	
	/**
	 * @param pushout
	 * @return
	 */
	private Set<Mapping> uniqueMappingToL2(ConflictPushout pushout) {

		Set<Mapping> uniqMappings = new HashSet<Mapping>(); // Das ist unser x =
															// S --x--> LHS
		// Durch pushout existiert S' -- a --> S1 -- c --> S schon.
		Graph s = pushout.getGraph();
		Span sap = pushout.getSap();
		Span s1 = pushout.getSpan1();
		Span s2 = pushout.getSpan2();

		Set<Mapping> a = sap.getMappingsInRule1();
		Set<Mapping> b = sap.getMappingsInRule2();
		Set<Mapping> e = s1.getMappingsInRule2();
		Set<Mapping> f = s2.getMappingsInRule1();

		ArrayList<Node> ae = new ArrayList<Node>(checking(a, s1, e));
		ArrayList<Node> bf = new ArrayList<Node>(checking(b, s2, f));

		Comparator<Node> comp = new Comparator<Node>() {

			@Override
			public int compare(Node n1, Node n2) {
				String o1 = n1.getName();
				String o2 = n2.getName();
				int origins = o1.compareTo(o2);

				return origins;
			}
		};
		ae.sort(comp);
		bf.sort(comp);

		if (!ae.isEmpty() && !bf.isEmpty() && ae.size() == bf.size()) {
			for (int i = 0; i < ae.size(); i++) {
				if (!ae.get(i).toString().equals(bf.get(i).toString())) {
					return null;
				}

			}
		}

		EList<Node> sNodes = s.getNodes();
		for (Node node : sNodes) {
			Node c = pushout.getMappingIntoSpan2(node);
			Node d = pushout.getMappingIntoSpan1(node);
			if (c == null && d == null) {
				return null;
			}
			if (c != null) {
				Node image = s2.getMappingIntoRule1(c).getImage();
				Mapping createMapping = helper.createMapping(node, image);
				uniqMappings.add(createMapping);
			} else {
				if (d != null) {
					Node image = s1.getMappingIntoRule1(d).getImage();
					Mapping createMapping = helper.createMapping(node, image);
					uniqMappings.add(createMapping);
				}
			}

		}

		return uniqMappings;
	}

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
	private Span compatibleSpans(Span sp1, Span sp2, Graph span1Graph, Graph span2Graph) {
		helper = new HenshinFactoryImpl();
		Graph sApostroph = null;
		Span s = null;
		Graph s2Apostrophe = null;
		Graph s1Apostrophe = null;
		System.out.println("Generating compatible Elements S1' for cr1 in cr2");
		s1Apostrophe = addCompatibleElements(sp1, sp2);
		Set<Mapping> s1 = new HashSet<Mapping>();
		Set<Mapping> s2 = new HashSet<Mapping>();
		Graph sGraph = helper.createGraph("S'");
		if (s1Apostrophe != null) {
			System.out.println("Generating compatible Elements S2' for cr2 in cr1");
			s2Apostrophe = addCompatibleElements(sp2, sp1);
			if (s2Apostrophe != null) {
				System.out.println("Generating intersection S' for S1' and S2'");
				intersection = intersection(s1Apostrophe, s2Apostrophe, sp1, sp2);
				sApostroph = intersection.getGraph();
				if (sApostroph != null) {
					System.out.println("S' is not null");
					for (Node node : sApostroph.getNodes()) {
						System.out.println("For " + node + " find mapping into cr1");
						for (Node s1Node : s1Apostrophe.getNodes()) {
							NodePair s1NodePair = (NodePair) s1Node;
							if (node instanceof NodePair) {
								NodePair nodePair = (NodePair) node;
								Node node1 = nodePair.getNode1();
								Node node12 = s1NodePair.getNode1();
								System.out.println("node is nodepair:" + nodePair);

								if (checkOriginNodes(node1, node12)) {
									s1.add(helper.createMapping(node1, node12));
								}
							}
						}
						System.out.println("For " + node + "find mapping into cr2");
						for (Node s2Node : s2Apostrophe.getNodes()) {
							NodePair s2NodePair = (NodePair) s2Node;
							if (node instanceof NodePair) {
								NodePair nodePair = (NodePair) node;
								Node node1 = nodePair.getNode2();
								Node node12 = s2NodePair.getNode1();
								System.out.println("node is nodepair:" + nodePair);
								if (checkOriginNodes(node1, node12)) {

									s2.add(helper.createMapping(node1, node12));
								}
							}
						}
					}
				}

				sGraph = sApostroph;
				s = new Span(s1, sGraph, s2);

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
	 * @param s1Apostrophe
	 * @param s2Apostrophe
	 *            intersection ist die Überschneidung der beiden Graphen
	 * @param sp2
	 * @param sp1
	 * @return
	 */
	private Span intersection(Graph s1Apostrophe, Graph s2Apostrophe, Span sp1, Span sp2) {

		HenshinFactoryImpl henshinFactoryImpl = helper;
		Graph fromS1 = henshinFactoryImpl.createGraph("S1<-S1'->S2");
		Graph fromS2 = henshinFactoryImpl.createGraph("S1<-S2'->S2");
		Graph result = henshinFactoryImpl.createGraph("S'");

		EList<Node> s1Nodes = s1Apostrophe.getNodes();
		EList<Edge> s1Edges = s1Apostrophe.getEdges();
		EList<Node> s2Nodes = s2Apostrophe.getNodes();
		EList<Edge> s2Edges = s2Apostrophe.getEdges();

		Set<Mapping> span1Mappings = new HashSet<Mapping>();
		Set<Mapping> rule2Mappings = new HashSet<Mapping>();

		for (Node node : s1Nodes) {
			NodePair nodePair = (NodePair) node;
			for (Node node2 : s2Nodes) {
				NodePair nodePair2 = (NodePair) node2;
				Node node11 = nodePair.getNode1();
				Node node21 = nodePair2.getNode1();
				Node node22 = nodePair2.getNode2();
				System.out.println("NodePair: " + node11.toString() + " : " + node21.toString());
				System.out.println(
						"NodePair: " + nodePair2.getNode1().toString() + " : " + nodePair2.getNode2().toString());

				if (checkOriginNodes(node11, node21)) {
					NodePair e = new NodePair(node11, node21);
					result.getNodes().add(e);
					Mapping createMapping = henshinFactoryImpl.createMapping(e, node11);
					span1Mappings.add(createMapping);
					Mapping createMapping2 = henshinFactoryImpl.createMapping(e, node22);
					rule2Mappings.add(createMapping2);
				}

			}
		}

		for (Edge edge : s1Edges) {
			EdgePair edgePair = (EdgePair) edge;
			for (Edge edge2 : s2Edges) {
				EdgePair edgePair2 = (EdgePair) edge2;
				Edge edge11 = edgePair.getEdge1();
				Edge edge21 = edgePair2.getEdge1();
				Edge edge22 = edgePair2.getEdge2();
				if (checkEdges(edge11, edge21)) {
					EdgePair e = new EdgePair(edge11, edge21);
					e.setType(edge11.getType());
					result.getEdges().add(e);
				}

			}
		}
		
		for (Edge edge : result.getEdges()){
			if (edge instanceof EdgePair){
				EdgePair edgePair = (EdgePair) edge;
				Edge edge1 = edgePair.getEdge1();
				Edge edge2 = edgePair.getEdge2();
				Node e1Source = edge1.getSource();
				Node e1Target = edge1.getTarget();
				Node e2Source = edge2.getSource();
				Node e2Target = edge2.getTarget();
				for (Node node : result.getNodes()){
					if (node instanceof NodePair){
						NodePair nodePair = (NodePair) node;
						Node node1 = nodePair.getNode1();
						Node node2 = nodePair.getNode2();
						if (checkOriginNodes(node1, e1Source) && checkOriginNodes(node2, e2Source)){//Check Source
							node.getOutgoing().add(edge);
							edge.setSource(node);
						} else {
							if (checkOriginNodes(node1, e1Target)&&checkOriginNodes(node2, e2Target)){
								node.getIncoming().add(edge);
								edge.setTarget(node);
								
							}
						}
					}
				}
				
			}
		}

		System.out.println("Intersection");

		Span span = new Span(span1Mappings, result, rule2Mappings);

		// Todo Checken ob Edges Target und Source stimmen?
		return span;

	}

	/**
	 * @param sp1
	 * @param sp2
	 * @param s1Apostrophe
	 * @return
	 */
	private Graph addCompatibleElements(Span sp1, Span sp2) {
		Graph s1Apostrophe = helper.createGraph();
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
				if (x.equals(y)) {
					System.out.println("x equals y!");
				}
				if (checkEquality(x, y, sp1)) {
					if (checkEquality(x, y, sp2)) { // If s12(x) = s22(y)
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
	private boolean checkEquality(GraphElement x, GraphElement y, Span sp) {
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
				if (checkOriginNodes(s1.getOrigin(), s2.getOrigin())) {
					System.out.println("true");
					return true;
				} else {
					System.out.println("false");
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

				return checkEdges(e1, e2);

			} catch (NullPointerException e) {
				e.printStackTrace();
				return false;
			}

		}
		return false;

	}

	private boolean checkEdges(Edge e1, Edge e2) {

		EReference e1Type = e1.getType();
		Node e1Source = e1.getSource();
		Node e1target = e1.getTarget();

		EReference e2Type = e2.getType();
		Node e2Source = e2.getSource();
		Node e2target = e2.getTarget();

		if (!e1Type.equals(e2Type)) {
			return false;
		}
		if (!checkOriginNodes(e1Source, e2Source)) {
			return false;
		}
		if (!checkOriginNodes(e1target, e2target)) {
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
			if (checkOriginNodes(originNode, mapping.getOrigin())) { // Hier
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
	private boolean checkOriginNodes(Node originNode, Node originNode2) {
		String[] revert = originNode.getName().split("_");
		String[] revertMapping = originNode2.getName().split("_");
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
