package org.eclipse.emf.henshin.cpa.atomic.cc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.cpa.atomic.PushoutResult;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.runner.RulePreparator;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.util.InterpreterUtil;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.HenshinPackage;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

public class MinimalReasonComputation {

	private Action deleteAction = new Action(Action.Type.DELETE);
	private Action preserveAction = new Action(Action.Type.PRESERVE);
	private Set<EPackage> metamodels;
	private Rule rule1;
	private Rule rule2;

	public MinimalReasonComputation(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		this.metamodels = new HashSet<EPackage>();

		for (Node node : rule1.getLhs().getNodes())
			metamodels.add(node.getType().getEPackage());

	}

	public Set<MinimalConflictReason> computeMinimalConflictReasons() {
		Set<MinimalConflictReason> result = new HashSet<MinimalConflictReason>();
		Set<Node> atomicNodes = getAtomCandidateNodes();
		Set<Edge> atomicEdges = getAtomCandidateEdges();
		List<SubGraph> connectedComponents = computeAllConnectedComponents(atomicNodes, atomicEdges);

		System.out.println(connectedComponents.size());
		List<Span> connectedComponentEmbeddings = computeEmbeddings(connectedComponents);

		for (Span span : connectedComponentEmbeddings) {
			if (fulfillsTransformationCondition(span))
				result.add(new MinimalConflictReason(span));
		}
		return result;
	}

	private Set<Node> getAtomCandidateNodes() {
		Set<Node> result = new HashSet<Node>();
		for (Node n1 : new HashSet<Node>(rule1.getActionNodes(deleteAction))) {
			for (Node n2 : new HashSet<Node>(rule2.getActionNodes(preserveAction))) {
				if (n2.getType() == n1.getType() || n2.getType().getEAllSuperTypes().contains(n1.getType()))
					result.add(n1);
			}
		}
		return result;
	}

	private Set<Edge> getAtomCandidateEdges() {
		Set<Edge> result = new HashSet<Edge>();
		for (Edge e1 : new HashSet<Edge>(rule1.getActionEdges(deleteAction))) {
			for (Edge e2 : new HashSet<Edge>(rule2.getActionEdges(preserveAction))) {
				if (e2.getType() == e1.getType()) {
					result.add(e1);
				}
			}
		}
		return result;
	}

	private List<SubGraph> computeAllConnectedComponents(Set<Node> atomicNodes, Set<Edge> atomicEdges) {
		List<SubGraph> result = new LinkedList<SubGraph>();

		List<Node> used = new ArrayList<Node>();
		for (Node node : atomicNodes) {
			if (!used.contains(node)) {
				SubGraph sg = new SubGraph(new HashSet<Node>(Arrays.asList(node)), new HashSet<Edge>());
				getConnectedComponent(sg, atomicNodes, atomicEdges);
				used.addAll(sg.getNodes());
				result.add(sg);
			}
		}

		for (Edge edge : atomicEdges) {
			if (!used.contains(edge.getSource()) && !used.contains(edge.getTarget())) {
				result.add(new SubGraph(new HashSet<Node>(Arrays.asList(edge.getSource(), edge.getTarget())),
						new HashSet<Edge>(Arrays.asList(edge))));
			}
		}

		return result;
	}

	private void getConnectedComponent(SubGraph sg, Set<Node> atomicNodes, Set<Edge> atomicEdges) {
		Set<Node> additionCandidates = new HashSet<Node>();
		for (Node n : sg.getNodes()) {
			if (n.getAction().getType() == Type.DELETE) {
				for (Edge e : n.getAllEdges().stream().filter(x -> atomicEdges.contains(x))
						.collect(Collectors.toSet())) {
					additionCandidates.add(e.getSource());
					additionCandidates.add(e.getTarget());
					sg.getEdges().add(e);
				}
			}
		}
		additionCandidates.removeAll(sg.getNodes());
		if (additionCandidates.isEmpty()) {
			sg.getNodes().addAll(additionCandidates);
			getConnectedComponent(sg, atomicNodes, atomicEdges);	
		} // else, we have reached the fixed point.
	}

	private List<Span> computeEmbeddings(List<SubGraph> connectedComponents) {
		List<Span> result = new LinkedList<Span>();
		for (SubGraph connectedComponent : connectedComponents) {
			Pair<Rule, Map<Node, Node>> hotRule = convertToHigherOrderRule(connectedComponent);

			Engine engine = new EngineImpl();
			EGraphImpl graph = new EGraphImpl();
			graph.addTree(rule2.getLhs());
			metamodels.forEach(m -> graph.addTree(m));
			List<Match> matches = InterpreterUtil.findAllMatches(engine, hotRule.getComponent1(), graph, null);

			for (Match m : matches) {
				result.add(createSpan(m, hotRule.getComponent1(), hotRule.getComponent2()));
			}
		}

		return result;
	}

	private Pair<Graph, Map<Node, Node>> toHigherOrderPattern(Graph lhs) {
		Graph result = HenshinFactory.eINSTANCE.createGraph("Lhs");
		Map<EClass, Node> types = new HashMap<EClass, Node>();
		Map<Node, Node> lo2hi = new HashMap<Node, Node>();
		for (Node n : lhs.getNodes()) {
			Node nn = HenshinFactory.eINSTANCE.createNode(result, HenshinPackage.Literals.NODE, n.getName());
			Node type = types.get(n.getType());
			if (type == null) {
				type = HenshinFactory.eINSTANCE.createNode(result, EcorePackage.Literals.ECLASS, "");
				HenshinFactory.eINSTANCE.createAttribute(type, EcorePackage.Literals.ENAMED_ELEMENT__NAME,
						"\"" + n.getType().getName() + "\"");
				types.put(n.getType(), type);
			}
			HenshinFactory.eINSTANCE.createEdge(nn, type, HenshinPackage.Literals.NODE__TYPE);
			lo2hi.put(n, nn);
		}
		Map<EReference, Node> reftypes = new HashMap<EReference, Node>();
		for (Edge e : lhs.getEdges()) {
			Node ne = HenshinFactory.eINSTANCE.createNode(result, HenshinPackage.Literals.EDGE, "");
			Node reftype = reftypes.get(e.getType());
			if (reftype == null) {
				reftype = HenshinFactory.eINSTANCE.createNode(result, EcorePackage.Literals.EREFERENCE,
						e.getType().getName());
				HenshinFactory.eINSTANCE.createAttribute(reftype, EcorePackage.Literals.ENAMED_ELEMENT__NAME,
						"\"" + e.getType().getName() + "\"");
				reftypes.put(e.getType(), reftype);
			}
			HenshinFactory.eINSTANCE.createEdge(ne, reftype, HenshinPackage.Literals.EDGE__TYPE);
			HenshinFactory.eINSTANCE.createEdge(ne, lo2hi.get(e.getSource()), HenshinPackage.Literals.EDGE__SOURCE);
			HenshinFactory.eINSTANCE.createEdge(ne, lo2hi.get(e.getTarget()), HenshinPackage.Literals.EDGE__TARGET);
		}
		return new Pair<Graph, Map<Node, Node>>(result, lo2hi);
	}

	private Pair<Rule, Map<Node, Node>> convertToHigherOrderRule(SubGraph connectedComponent) {
		Copier rule1toCopy = new Copier();
		Rule rule = (Rule) rule1toCopy.copy(rule1);
		rule1toCopy.copyReferences();

		for (Edge e : rule1.getLhs().getEdges())
			if (!connectedComponent.contains(e))
				rule.removeEdge((Edge) rule1toCopy.get(e), true);
		for (Node n : rule1.getLhs().getNodes())
			if (!connectedComponent.contains(n)) {
				rule.removeNode((Node) rule1toCopy.get(n), true);
			}

		rule.getMappings().clear();
		rule.setName("HigherOrderVersionOf" + rule.getName());
		Pair<Graph, Map<Node, Node>> higherOrderPattern = toHigherOrderPattern(rule.getLhs());

		rule.setLhs(higherOrderPattern.getComponent1());
		Copier lhsToCopy = new Copier();
		Graph rhs = (Graph) lhsToCopy.copy(rule.getLhs());
		lhsToCopy.copyReferences();
		rule.setRhs(rhs);
		for (Node n : rule.getLhs().getNodes()) {
			rule.getMappings().add(HenshinFactory.eINSTANCE.createMapping(n, (Node) lhsToCopy.get(n)));
		}

		Rule result = RulePreparator.prepareRule(rule);
		HenshinUtil.persist(result, metamodels);

		Map<Node, Node> rule1toHi = new HashMap<Node, Node>();
		for (Node n : rule1.getLhs().getNodes())
			if (connectedComponent.contains(n))
				rule1toHi.put(n, higherOrderPattern.getComponent2().get(rule1toCopy.get(n)));
		return new Pair<Rule, Map<Node, Node>>(result, rule1toHi);

	}

	private boolean fulfillsTransformationCondition(Span span) {
		PushoutResult pushoutResult = new PushoutResult(rule1, span, rule2);
		boolean hasDangling = !findDanglingEdgesOfRule1(rule1, pushoutResult.getMappingsOfRule1()).isEmpty();
		return !hasDangling;
	}

	private List<Edge> findDanglingEdgesOfRule1(Rule rule1, List<Mapping> embedding) {
		HashMap<Node, Node> mapL1toG = new HashMap<Node, Node>();
		HashMap<Node, Node> mapGtoL1 = new HashMap<Node, Node>();
		for (Mapping mapping : embedding) {
			mapL1toG.put(mapping.getOrigin(), mapping.getImage());
			mapGtoL1.put(mapping.getImage(), mapping.getOrigin());
		}

		EList<Node> l1DeletingNodes = rule1.getActionNodes(new Action(Action.Type.DELETE));
		List<Edge> danglingEdges = new LinkedList<Edge>();

		for (Node l1Deleting : l1DeletingNodes) {
			Node poDeleting = mapL1toG.get(l1Deleting);

			EList<Edge> poDeletingsEdges = poDeleting.getAllEdges();
			for (Edge poDeletingsEdge : poDeletingsEdges) {
				Node l1DelSource = mapGtoL1.get(poDeletingsEdge.getSource());
				if (l1DelSource == null)
					danglingEdges.add(poDeletingsEdge);

				Node l1DelTarget = mapGtoL1.get(poDeletingsEdge.getTarget());
				if (l1DelTarget == null)
					danglingEdges.add(poDeletingsEdge);
			}

		}
		return danglingEdges;
	}

	private Span createSpan(Match m, Rule higherOrderRule, Map<Node, Node> rule1ToHi) {
		Copier r1toS1map = new Copier();
		Graph s1 = (Graph) r1toS1map.copy(rule1.getLhs());
		r1toS1map.copyReferences();

		Set<Mapping> rule1Mappings = new HashSet<Mapping>();
		Set<Mapping> rule2Mappings = new HashSet<Mapping>();

		for (Node n : rule1.getLhs().getNodes()) {
			if (!rule1ToHi.keySet().contains(n)) {
				s1.removeNode((Node) r1toS1map.get(n));
			} else {
				Node n2 = (Node) m.getNodeTarget(rule1ToHi.get(n));
				rule1Mappings.add(HenshinFactory.eINSTANCE.createMapping((Node) r1toS1map.get(n), n));
				rule2Mappings.add(HenshinFactory.eINSTANCE.createMapping((Node) r1toS1map.get(n), n2));
			}
		}

		return new Span(rule1Mappings, s1, rule2Mappings);
	}

}
