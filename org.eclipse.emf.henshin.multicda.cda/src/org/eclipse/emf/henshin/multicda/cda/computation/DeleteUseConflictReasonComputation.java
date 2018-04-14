/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.computation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.ConflictPushout;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteDeleteConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;

/**
 * 
 * @author vincentcuccu 17.12.2017
 */
public class DeleteUseConflictReasonComputation {

	private Rule rule1;
	private Rule rule2;
	private Set<ConflictReason> normalCRs;
	private Set<ConflictReason> DUCRs;

	private Set<Mapping> mapS1ToL1;
	private Span conflictReason;
	private Set<Mapping> mapS1ToL2;
	private MinimalReasonComputation helperForCheckDangling;
	private HenshinFactory factory = HenshinFactory.eINSTANCE;

	/**
	 * constructor
	 * 
	 * @param rule1
	 * @param rule2
	 * @param conflictReasonsFromR22
	 */
	public DeleteUseConflictReasonComputation(Rule rule1, Rule rule2, Set<ConflictReason> normalCR,
			Set<ConflictReason> DUCRs) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		this.normalCRs = new HashSet<>();
		this.normalCRs.add(new ArrayList<>(normalCR).get(3));
		this.DUCRs = new HashSet<>();
		this.DUCRs.add(new ArrayList<>(DUCRs).get(1));
	}

	/**
	 * constructs all Initial Reasons as candidates for r1 and r2
	 * 
	 * @param conflictReasons
	 * @return result
	 */
	public Set<DeleteUseConflictReason> computeDeleteUseConflictReason() {
		Set<DeleteUseConflictReason> result = new HashSet<DeleteUseConflictReason>();
		for (Span normalCR : normalCRs)
			computeDeleteUseConflictReasons(normalCR, result);
		return result;

	}

	/**
	 * the Method to encounter the delete read conflict reasons
	 * 
	 * @param s1
	 * @param result
	 */
	private void computeDeleteUseConflictReasons(Span s1, Set<DeleteUseConflictReason> result) {
		this.conflictReason = s1;
		Rule rule1 = s1.getRule1();
		Rule conflictRule2 = s1.getRule2();
		helperForCheckDangling = new MinimalReasonComputation(rule1, rule2);

		if (findEmbeddingS1toK2(s1, rule2)) {
			Pushout pushout = new Pushout(rule1, s1, conflictRule2);
			if (helperForCheckDangling.findDanglingEdgesOfRule1(rule1, pushout.getRule1Mappings()).isEmpty()
					&& helperForCheckDangling.findDanglingEdgesOfRule1(conflictRule2, pushout.getRule2Mappings())
							.isEmpty()) {
				DeleteReadConflictReason res = new DeleteReadConflictReason(s1);
				result.add(res);
			}
		} else {
			mapS1ToL1 = s1.mappingsInRule1;
			mapS1ToL2 = s1.mappingsInRule2;
			Set<Span> ddSet = ConstructDeleteDeleteSet(rule1, rule2, s1);
			for (Span s2 : ddSet)
				result.add(new DeleteDeleteConflictReason(s1, s2));
		}
	}

	/**
	 * @param r2
	 * @param r1
	 * @param sp1
	 * @return
	 */
	private Set<Span> ConstructDeleteDeleteSet(Rule r1, Rule r2, Span sp1) {
		Set<Span> s2Set = new HashSet<>();
		for (Span sp2 : DUCRs) {
			Span Si = compatibleSpans(sp1, sp2);
			if (Si != null) {
				if (!isEmpty(Si.getGraph())) {
					Pushout pushout = new Pushout(sp1, Si, sp2);
					Span uniquePushout = computeUniquePushout(pushout);
					Pushout po = new Pushout(rule1, uniquePushout, rule2);
					if (helperForCheckDangling.findDanglingEdgesOfRule1(rule1, po.getRule1Mappings()).isEmpty()
							&& helperForCheckDangling.findDanglingEdgesOfRule1(rule2, po.getRule2Mappings())
									.isEmpty()) {
						s2Set.add(sp2);
					}
				}
			}
		}
		return s2Set;
	}

	/**
	 * @param pushout
	 * @return
	 */
	private Span computeUniquePushout(Pushout pushout) { //TODO Unique Pushout reparieren... funzt wahrscheinlich nicht
		Span uniqueSpan = null;
//		Graph s = pushout.getGraph();
//
//		Graph lhs1 = rule1.getLhs();
//		Graph lhs2 = rule2.getLhs();
//
//		Set<Mapping> mappingsInL1 = uniqueMapping(pushout, lhs1); // S --x-->
//																	// LHS1
//		Set<Mapping> mappingsInL2 = uniqueMapping(pushout, lhs2); // S --x-->
		// LHS2

//		uniqueSpan = new Span(mappingsInL1, s, mappingsInL2);
//		uniqueSpan.setRule1(rule1);
//		uniqueSpan.setRule2(rule2);

		return uniqueSpan;
	}

	/**
	 * @param pushout
	 * @param lhs1
	 * @return
	 */
	private Set<Mapping> uniqueMapping(ConflictPushout pushout, Graph lhs) {

		Set<Mapping> uniqMappings = new HashSet<Mapping>(); // Das ist unser x =
															// S --x--> LHS
															// Durch pushout existiert S' -- a --> S1 -- c --> S schon.
		Graph s = pushout.getGraph();
		Span sap = pushout.getSap();
		Graph sapGraph = sap.getGraph();
		Span s1 = pushout.getSpan1();
		Span s2 = pushout.getSpan2();

		Set<Mapping> a = sap.getMappingsInRule1();
		Set<Mapping> b = sap.getMappingsInRule2();
		Set<Mapping> c = pushout.getMappingsFromSpan1();
		Set<Mapping> d = pushout.getMappingsFromSpan2();
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
			Node mappingIntoSpan1 = pushout.getMappingIntoSpan1(node);
			Node mappingIntoSpan2 = pushout.getMappingIntoSpan2(node);
			if (mappingIntoSpan1 == null && mappingIntoSpan2 == null) {
				return null;
			}
			if (mappingIntoSpan1 != null) {
				Node image = s1.getMappingIntoRule1(mappingIntoSpan1).getImage();
				Mapping createMapping = factory.createMapping(node, image);
				uniqMappings.add(createMapping);
			} else {
				if (mappingIntoSpan2 != null) {
					Node image = s2.getMappingIntoRule1(mappingIntoSpan2).getImage();
					Mapping createMapping = factory.createMapping(node, image);
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
	private Span compatibleSpans(Span sp1, Span sp2) {
		Span s1i = null;
		Span s2i = null;
		Span si = null;

		s1i = addCompatibleElements(sp1, sp2);
		if (s1i != null) {
			s2i = addCompatibleElements(sp2, sp1);
			if (s2i != null) {
				si = intersection(s1i, s2i);
				if (si != null) {
					si.setRule1(sp1.getRule1());
					si.setRule2(sp1.getRule2());
				}
			} else {
				return null;
			}
		} else

		{
			return null;
		}

		return si;
	}

	/**
	 * @param S1i
	 * @param S2i
	 *            intersection ist die Überschneidung der beiden Graphen
	 * @param sp2
	 * @param sp1
	 * @return
	 */
	private Span intersection(Span S1i, Span S2i) {
		Graph Si = factory.createGraph("S'");
		Set<Mapping> rule1Mappings = new HashSet<>();
		Set<Mapping> rule2Mappings = new HashSet<>();
		Map<Node, Node> nodes = new HashMap<>();

		for (Node n1 : S1i.getGraph().getNodes())
			for (Node n2 : S2i.getGraph().getNodes())
				if (Span.nodeEqual(n1.getName(), n2.getName())) {
					rule1Mappings.add(factory.createMapping(n1, S1i.getMappingIntoRule1(n1).getImage()));
					rule1Mappings.add(factory.createMapping(n1, S2i.getMappingIntoRule1(n2).getImage()));
					rule2Mappings.add(factory.createMapping(n1, S1i.getMappingIntoRule2(n1).getImage()));
					rule2Mappings.add(factory.createMapping(n1, S2i.getMappingIntoRule2(n2).getImage()));
					nodes.put(n1, factory.createNode(Si, n1.getType(), n1.getName()));
					break;
				}
		for (Edge e1 : S1i.getGraph().getEdges())
			for (Edge e2 : S2i.getGraph().getEdges())
				if (Span.nodeEqual(e1.getSource().getName(), e2.getSource().getName())
						&& Span.nodeEqual(e1.getTarget().getName(), e2.getTarget().getName())) {
					Node source = nodes.get(e1.getSource());
					Node target = nodes.get(e1.getTarget());
					Si.getEdges().add(factory.createEdge(source, target, e1.getType()));
				}
		return nodes.isEmpty() ? null : new Span(rule1Mappings, Si, rule2Mappings);
	}

	/**
	 * @param sp1
	 * @param sp2
	 * @param s1Apostrophe
	 * @return
	 */
	private Span addCompatibleElements(Span sp1, Span sp2) {
		Graph Si = factory.createGraph("S'");
		EList<GraphElement> s1 = new BasicEList<>(sp1.getGraph().getNodes());
		s1.addAll(sp1.getGraph().getEdges());

		Set<Mapping> mappingsR1 = new HashSet<>();
		Set<Mapping> mappingsR2 = new HashSet<>();

		for (GraphElement ge : s1) {
			try {
				if (ge instanceof Node) {
					Node x = (Node) ge;
					Node y = existCompatibleElement(x, sp1, sp2);
					if (y != null)
						addNodeToGraph(x, y, Si, mappingsR1, mappingsR2);
				} else {
					Edge x = (Edge) ge;
					Edge y = existCompatibleElement(x, sp1, sp2);
					if (y != null)
						addEdgeToGraph(x, y, Si);
				}

			} catch (NotCompatibleException e) {
				System.out.println(e.getMessage() + e.getCause());
				Si = null;
			}
		}
		return new Span(mappingsR1, Si, mappingsR2);
	}

	private Edge addEdgeToGraph(Edge edge1, Edge edge2, Graph graph) {
		if (edge1.getType() == edge2.getType()) {
			Node source = graph.getNode(edge1.getSource().getName() + "<>" + edge2.getSource().getName());
			Node target = graph.getNode(edge1.getTarget().getName() + "<>" + edge2.getTarget().getName());
			if (source == null || target == null) {
				source = graph.getNode(edge1.getSource().getName() + "--" + edge2.getSource().getName());
				target = graph.getNode(edge1.getTarget().getName() + "--" + edge2.getTarget().getName());
				if (source == null || target == null)
					return null;
			}
			Edge edge = factory.createEdge(source, target, edge1.getType());
			return edge;
		}
		return null;
	}

	private Node addNodeToGraph(Node node1, Node node2, Graph graph, Set<Mapping> rule1Mappings,
			Set<Mapping> rule2Mappings) {
		EClass subNodeType = identifySubNodeType(node1, node2);
		String space = "<>";
		if (node1.getName().contains("<>") || node2.getName().contains("<>"))
			space = "--";
		Node commonNode = factory.createNode(graph, subNodeType, node1.getName() + space + node2.getName());

		rule1Mappings.add(factory.createMapping(commonNode, node1));
		rule2Mappings.add(factory.createMapping(commonNode, node2));
		return commonNode;
	}

	private EClass identifySubNodeType(Node node1, Node node2) {
		if (node1.getType().equals(node2.getType()))
			return node1.getType();
		if (node1.getType().getEAllSuperTypes().contains(node2.getType()))
			return node1.getType();
		if (node2.getType().getEAllSuperTypes().contains(node1.getType()))
			return node2.getType();
		return null;
	}

	/**
	 * @param x
	 * @param sp1
	 * @param sp2
	 * @param conflictReason2
	 * @return
	 * @throws Exception
	 */
	private <GE extends GraphElement> GE existCompatibleElement(GE x, Span sp1, Span sp2)
			throws NotCompatibleException {
		EList<GraphElement> sp1Elements = new BasicEList<>(sp1.getGraph().getNodes());
		sp1Elements.addAll(sp1.getGraph().getEdges());

		EList<GraphElement> sp2Elements = new BasicEList<>(sp2.getGraph().getNodes());
		sp2Elements.addAll(sp2.getGraph().getEdges());

		if (sp1Elements.contains(x)) {
			for (GraphElement y : sp2Elements)
				if (checkEqualityMapping(x, y, sp1, sp2))
					return (GE) y;
			return null;
		} else {
			throw new NotCompatibleException();
		}

	}

	/**
	 * @param a
	 * @param b
	 * @param sp
	 * @return
	 */
	private boolean checkEqualityMapping(GraphElement a, GraphElement b, Span sp1, Span sp2) {
		if (a instanceof Node && b instanceof Node) {
			Node x = sp1.getMappingIntoRule1((Node) a).getImage();
			Node y = sp2.getMappingIntoRule2((Node) b).getImage();
			if (x.getName().equals(y.getName()) && x.getType() == y.getType()) {
				x = sp1.getMappingIntoRule2((Node) a).getImage();
				y = sp2.getMappingIntoRule1((Node) b).getImage();
				if (x.getName().equals(y.getName()) && x.getType() == y.getType())
					return true;
			}
		} else if (a instanceof Edge && b instanceof Edge) {
			Edge x = (Edge) a;
			Edge y = (Edge) b;
			if (checkEqualityMapping(x.getSource(), y.getSource(), sp1, sp2)
					&& checkEqualityMapping(x.getTarget(), y.getTarget(), sp1, sp2))
				return true;
		}
		return false;
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
	 * @param rule2
	 * @return boolean
	 */
	public static boolean findEmbeddingS1toK2(Span conflictReason, Rule rule2) {
		Graph s1 = conflictReason.getGraph();
		EList<Node> l2N = new BasicEList<>(rule2.getActionNodes(new Action(Action.Type.PRESERVE)));
		EList<Edge> l2E = new BasicEList<>(rule2.getActionEdges(new Action(Action.Type.PRESERVE)));

		ArrayList<Mapping> s1tol2 = computeMappings(s1, l2N, l2E);

		return !s1tol2.isEmpty();

	}

	/**
	 * computes Mappings of two ELists of Nodes our of two Graphs
	 * 
	 * @param g1
	 * @param g2
	 * @return
	 */
	private static ArrayList<Mapping> computeMappings(Graph g1, EList<Node> g2N, EList<Edge> g2E) {
		HenshinFactory helper = HenshinFactory.eINSTANCE;
		ArrayList<Mapping> G1toG2 = new ArrayList<Mapping>();

		for (Node first : g1.getNodes()) {
			boolean found = false;
			for (Node second : g2N) {
				String[] names = first.getName().split("_");
				if (first.getType() == second.getType())
					if (names[0].equals(second.getName()) || names[1].equals(second.getName())) {
						Mapping mapping = helper.createMapping(first, second);
						G1toG2.add(mapping);
						found = true;
						break;
					}
			}
			if (!found)
				return new ArrayList<>();
		}
		Set<Edge> visited = new HashSet<>();
		for (Edge first : g1.getEdges()) {
			boolean found = false;
			for (Edge second : g2E) {
				String[] namesSource = first.getSource().getName().split("_");
				String[] namesTarget = first.getTarget().getName().split("_");
				if ((namesSource[0].equals(second.getSource().getName())
						|| namesSource[1].equals(second.getSource().getName()))
						&& (namesTarget[0].equals(second.getTarget().getName())
								|| namesTarget[1].equals(second.getTarget().getName()))
						&& visited.add(second)) {
					found = true;
					break;
				}
			}
			if (!found)
				return new ArrayList<>();

		}
		return G1toG2;
	}

	public static class NotCompatibleException extends Exception {
		private static final long serialVersionUID = 3555111140711032351L;

		public NotCompatibleException() {
			super("Ein Fehler bei der Delete Use Conflict Reason berechnung ist aufgetaucht.");
		}

	}

}
