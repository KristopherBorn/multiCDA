package org.eclipse.emf.henshin.multicda.cda.computation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

public class AtomCandidateComputation {
	static Action deleteAction = new Action(Action.Type.DELETE);
	static Action preserveAction = new Action(Action.Type.PRESERVE);
	static HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;

	protected Rule rule1;
	protected Rule rule2;

	public AtomCandidateComputation(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
	}

	public List<Span> computeAtomCandidates() {
		List<Span> result = new LinkedList<Span>();

		List<ModelElement> atomicDeletionElements = new LinkedList<ModelElement>(rule1.getActionNodes(deleteAction));
		atomicDeletionElements.addAll(identifyAtomicDeletionEdges());
		for (ModelElement el1 : atomicDeletionElements) {
			addDeleteUseAtomCandidates(rule1, rule2, result, el1);
		}

		return result;
	}

	protected void addDeleteUseAtomCandidates(Rule rule1, Rule rule2, List<Span> result, ModelElement el1) {
		if (el1 instanceof Node) {
			Node n1 = (Node) el1;
			for (Node n2 : rule2.getLhs().getNodes()) {
				boolean r1NodeIsSuperTypeOfR2Node = n2.getType().getEAllSuperTypes().contains(n1.getType());
				boolean r2NodeIsSuperTypeOfR1Node = n1.getType().getEAllSuperTypes().contains(n2.getType());
				boolean identicalType = n2.getType() == n1.getType();
				if (r1NodeIsSuperTypeOfR2Node || r2NodeIsSuperTypeOfR1Node || identicalType) {
					Graph S1 = henshinFactory.createGraph();
					Set<Mapping> rule1Mappings = new HashSet<Mapping>();
					Set<Mapping> rule2Mappings = new HashSet<Mapping>();
					addNodeToGraph(n1, (Node) n2, S1, rule1Mappings, rule2Mappings);
					Span S1span = new Span(rule1Mappings, S1, rule2Mappings);
					result.add(S1span);
				}
			}
		}

		if (el1 instanceof Edge) {
			List<ModelElement> atomicUsageElements = new LinkedList<ModelElement>();
			atomicUsageElements.addAll(rule2.getLhs().getEdges(((Edge) el1).getType()));
			for (ModelElement el2 : atomicUsageElements) {
				if (el2 instanceof Edge) {
					Graph S1 = henshinFactory.createGraph();
					Set<Mapping> rule1Mappings = new HashSet<Mapping>();
					Set<Mapping> rule2Mappings = new HashSet<Mapping>();

					Node commonSourceNode = addNodeToGraph(((Edge) el1).getSource(), ((Edge) el2).getSource(), S1,
							rule1Mappings, rule2Mappings);
					Node commonTargetNode = addNodeToGraph(((Edge) el1).getTarget(), ((Edge) el2).getTarget(), S1,
							rule1Mappings, rule2Mappings);
					Span S1span = new Span(rule1Mappings, S1, rule2Mappings);
					result.add(S1span);

					S1.getEdges()
							.add(henshinFactory.createEdge(commonSourceNode, commonTargetNode, ((Edge) el2).getType()));
				}
			}
		}

	}

	private Set<Edge> identifyAtomicDeletionEdges() {
		Set<Edge> result = new HashSet<Edge>();
		for (Edge e1 : new HashSet<Edge>(rule1.getActionEdges(deleteAction))) {
			if (isPreserveNode(e1.getSource()) && isPreserveNode(e1.getTarget())) {
				for (Edge e2 : new HashSet<Edge>(rule2.getActionEdges(preserveAction))) {
					if (e2.getType() == e1.getType()) {
						result.add(e1);
					}
				}
			}

		}
		return result;
	}

	protected boolean isPreserveNode(Node node) {
		return node.getAction().getType() == Type.PRESERVE;
	}

	protected Node addNodeToGraph(Node nodeInRule1, Node nodeInRule2, Graph S1, Set<Mapping> rule1Mappings,
			Set<Mapping> rule2Mappings) {
		EClass subNodeType = identifySubNodeType(nodeInRule1, nodeInRule2);
		Node commonNode = henshinFactory.createNode(S1, subNodeType,
				nodeInRule1.getName() + "_" + nodeInRule2.getName());

		rule1Mappings.add(henshinFactory.createMapping(commonNode, nodeInRule1));
		rule2Mappings.add(henshinFactory.createMapping(commonNode, nodeInRule2));
		return commonNode;
	}

	/**
	 * identify the type of the both nodes which is the subtype of the other
	 * node.
	 * 
	 * @param node1
	 * @param node2
	 * @return returns the subnode type if one of both is, otherwise null.
	 */
	private EClass identifySubNodeType(Node node1, Node node2) {
		if (node1.getType().equals(node2.getType()))
			return node1.getType();
		if (node1.getType().getEAllSuperTypes().contains(node2.getType()))
			return node1.getType();
		if (node2.getType().getEAllSuperTypes().contains(node1.getType()))
			return node2.getType();
		return null;
	}

}
