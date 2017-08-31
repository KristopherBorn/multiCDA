package org.eclipse.emf.henshin.cpa.atomic.computation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.cpa.atomic.CospanMappingToMaps;
import org.eclipse.emf.henshin.cpa.atomic.MapOfLSetEnumerator;
import org.eclipse.emf.henshin.cpa.atomic.Pushout;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.SpanMappings;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

public class MinimalReasonComputation {
	static Action deleteAction = new Action(Action.Type.DELETE);
	static Action preserveAction = new Action(Action.Type.PRESERVE);
	static HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;
	Map<Span,Pushout> span2pushout = new HashMap<Span, Pushout>();
	
	protected Rule rule1;
	protected Rule rule2;

	public MinimalReasonComputation(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
	}

	public Set<MinimalConflictReason> computeMinimalConflictReasons() {
		Set<MinimalConflictReason> result = new HashSet<MinimalConflictReason>();
		List<Span> candidates = new AtomCandidateComputation(rule1, rule2).computeAtomCandidates();
		for (Span candidate : candidates) {
			computeMinimalConflictReasons(candidate, result);
		}
		return result;
	}

	public void computeMinimalConflictReasons(Span s1, Set<MinimalConflictReason> result) {
		if (isMinReason(s1)) {
			result.add(new MinimalConflictReason(s1));
			return;
		}
		Set<Span> extendedSpans = findExtensions(s1);
		for (Span extendedSpan : extendedSpans) {
			computeMinimalConflictReasons(extendedSpan, result);
		}
	}

	private boolean isMinReason(Span s1) {
		Pushout pushoutResult = getPushout(s1);
		boolean rule1EmbeddingIsDanglingFree = findDanglingEdgesOfRule1(pushoutResult.getRule1Mappings()).isEmpty();
		return rule1EmbeddingIsDanglingFree;
	}


	private Pushout getPushout(Span s1) {
		Pushout result = span2pushout.get(s1);
		if (result == null) {
			result = new Pushout(rule1, s1, rule2);
			span2pushout.put(s1, result);
		}
		return result;
	}

	public List<Edge> findDanglingEdgesOfRule1(List<Mapping> embedding) {
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
				if (l1DelSource == null) {
					danglingEdges.add(poDeletingsEdge);
					continue;
				}
	
				Node l1DelTarget = mapGtoL1.get(poDeletingsEdge.getTarget());
				if (l1DelTarget == null) {
					danglingEdges.add(poDeletingsEdge);
				}
			}
	
		}
		return danglingEdges;
	}

	private Set<Span> findExtensions(Span s1) {
		Pushout pushoutResult = getPushout(s1);
		CospanMappingToMaps cospanMappings = new CospanMappingToMaps(pushoutResult.getRule1Mappings(),
				pushoutResult.getRule2Mappings());
		List<Edge> danglingEdges = findDanglingEdgesOfRule1(pushoutResult.getRule1Mappings());

		Map<Edge, Set<Edge>> fixingEdgeMap = new HashMap<>();
		for (Edge danglingEdge : danglingEdges) {
			Set<Edge> fixing = findFixingEdges(rule1, rule2, danglingEdge, cospanMappings, s1);
			if (!fixing.isEmpty()) {
				fixingEdgeMap.put(danglingEdge, fixing);
			} else {
				return new HashSet<Span>();
			}
		}
		Set<Span> extensions = enumerateExtensions(s1, fixingEdgeMap, cospanMappings);
		return extensions;
	}

	public Set<Span> enumerateExtensions(Span originalSpan, Map<Edge, Set<Edge>> fixingEdges,
			CospanMappingToMaps comaps) {
		Set<Span> extensions = new HashSet<Span>();
		// Choose an arbitrary dangling-fixing edge pair and use it to extend
		// the span. Additional fixing edges might be consumed to fix additional
		// dangling edges between the new and old end of the fixing edge.
		for (Edge danglingEdgePO : fixingEdges.keySet()) {
			for (Edge fixingEdgeR1 : fixingEdges.get(danglingEdgePO)) {
				Span span1 = new Span(originalSpan);
				SpanMappings maps = new SpanMappings(span1);
				Node srcR1 = fixingEdgeR1.getSource();
				Node trgR1 = fixingEdgeR1.getTarget();
				Node srcR2 = comaps.gToRule2.get(danglingEdgePO.getSource());
				Node trgR2 = comaps.gToRule2.get(danglingEdgePO.getTarget());
				Node srcS1 = maps.rule1ToS1.get(srcR1);
				Node trgS1 = maps.rule1ToS1.get(trgR1);

				boolean sourceDangling = true;
				if (srcS1 == null) {
					srcS1 = henshinFactory.createNode(span1.getGraph(), commonSubClass(srcR1, srcR2),
							srcR1.getName() + "_" + srcR2.getName());
					span1.mappingsInRule1.add(henshinFactory.createMapping(srcS1, srcR1));
					span1.mappingsInRule2.add(henshinFactory.createMapping(srcS1, srcR2));
				} else if (trgS1 == null) {
					sourceDangling = false;
					trgS1 = henshinFactory.createNode(span1.getGraph(), commonSubClass(trgR1, trgR2),
							trgR1.getName() + "_" + trgR2.getName());
					span1.mappingsInRule1.add(henshinFactory.createMapping(trgS1, trgR1));
					span1.mappingsInRule2.add(henshinFactory.createMapping(trgS1, trgR2));
				}

				// Treatment of conflict-atom edges
				EList<Node> preserveNodes = rule1.getActionNodes(new Action(Type.PRESERVE));
				if (preserveNodes.contains(fixingEdgeR1.getSource())
						&& preserveNodes.contains(fixingEdgeR1.getTarget())) {
					Map<Edge, Edge> entry = new HashMap<Edge, Edge>();
					entry.put(danglingEdgePO, fixingEdgeR1);
					createExtension(extensions, entry, span1);
				} else {// Treatment of conflict-atom nodes
					List<Map<Edge, Edge>> combinations = getFixingFamily(danglingEdgePO, sourceDangling, fixingEdges,
							span1, comaps);
					for (Map<Edge, Edge> combination : combinations) {
						createExtension(extensions, combination, span1);
					}

				}
			}
		}
		return extensions;
	}

	private EClass commonSubClass(Node node1, Node node2) {
		if (node1.getType() == node2.getType())
			return node1.getType();
		if (node1.getType().getEAllSuperTypes().contains(node2.getType()))
			return node1.getType();
		if (node2.getType().getEAllSuperTypes().contains(node1.getType()))
			return node2.getType();
		throw new RuntimeException("Incompatible types!");
	}

	private void createExtension(Set<Span> extensions, Map<Edge, Edge> combination, Span span1) {
		Span span = new Span(span1);
		SpanMappings maps = new SpanMappings(span);
		for (Edge fixingEdge : combination.values()) {
			Node src = maps.rule1ToS1.get(fixingEdge.getSource());
			Node trg = maps.rule1ToS1.get(fixingEdge.getTarget());
			henshinFactory.createEdge(src, trg, fixingEdge.getType());
		}
		extensions.add(span);
	}

	private List<Map<Edge, Edge>> getFixingFamily(Edge danglingEdgePO, boolean sourceDangling,
			Map<Edge, Set<Edge>> fixingEdges, Span span1, CospanMappingToMaps comaps) {
		SpanMappings maps = new SpanMappings(span1);
		List<Edge> brotherEdges = new ArrayList<Edge>();
		List<Edge> sisterEdges = new ArrayList<Edge>();
		Node srcPO = danglingEdgePO.getSource();
		Node trgPO = danglingEdgePO.getTarget();
		if (sourceDangling) {
			brotherEdges = trgPO.getIncoming().stream().filter(e -> e.getSource() == srcPO)
					.collect(Collectors.toList());
			sisterEdges = trgPO.getOutgoing().stream().filter(e -> e.getTarget() == srcPO).collect(Collectors.toList());
		} else {
			brotherEdges = srcPO.getIncoming().stream().filter(e -> e.getSource() == trgPO)
					.collect(Collectors.toList());
			sisterEdges = srcPO.getOutgoing().stream().filter(e -> e.getTarget() == trgPO).collect(Collectors.toList());
		}
		Set<Edge> danglingFamilyPO = Stream.concat(brotherEdges.stream(), sisterEdges.stream())
				.collect(Collectors.toSet());
		Map<Edge, Set<Edge>> result = new HashMap<Edge, Set<Edge>>();

		for (Edge toFixPO : danglingFamilyPO) {
			Set<Edge> potentialFixes = fixingEdges.get(toFixPO);
			Set<Edge> viable = new HashSet<Edge>();
			Set<Node> requiredMemberNodes = new HashSet<Node>();
			Node srcToFix = toFixPO.getSource();
			Node trgToFix = toFixPO.getTarget();
			requiredMemberNodes.add(maps.s1ToRule1.get(maps.rule2ToS1.get(comaps.gToRule2.get(srcToFix))));
			requiredMemberNodes.add(maps.s1ToRule1.get(maps.rule2ToS1.get(comaps.gToRule2.get(trgToFix))));

			for (Edge e : potentialFixes) {
				Set<Node> memberNodes = new HashSet<Node>();
				memberNodes.add(e.getSource());
				memberNodes.add(e.getTarget());
				if (memberNodes.equals(requiredMemberNodes))
					viable.add(e);
			}
			if (viable.isEmpty()) {
				return new LinkedList<Map<Edge, Edge>>();
			} else {
				result.put(toFixPO, viable);
			}
		}

		List<Map<Edge, Edge>> list = new LinkedList<Map<Edge, Edge>>();
		MapOfLSetEnumerator.combinations(result, list);
		return list;
	}

	/**
	 * 
	 * @param rule1
	 * @param rule2
	 * @param poDangling
	 * @param s1
	 * @param mappingOfRule1InOverlapG
	 * @param mappingOfRule2InOverlapG
	 * @return A set of fixing edges, that is, edges in R1 suitable to act as a
	 *         counterpart for the dangling edge originating from R2.
	 */
	public Set<Edge> findFixingEdges(Rule rule1, Rule rule2, Edge poDangling, CospanMappingToMaps comaps, Span s1) {
		SpanMappings maps = new SpanMappings(s1);
		Node poDanglingSource = poDangling.getSource();
		Node poDanglingTarget = poDangling.getTarget();
		Node r1DanglingSource = comaps.gToRule1.get(poDanglingSource);
		Node r1DanglingTarget = comaps.gToRule1.get(poDanglingTarget);

		if (r1DanglingSource == null) { // source dangling
			Set<Edge> r1Candidates = new HashSet<Edge>(r1DanglingTarget.getIncoming(poDangling.getType()));
			return r1Candidates.stream().filter(e -> maps.rule1ToS1.get(e.getSource()) == null)
					.collect(Collectors.toSet());
		} else if (r1DanglingTarget == null) { // source dangling
			Set<Edge> c1Candidates = new HashSet<Edge>(r1DanglingSource.getOutgoing(poDangling.getType()));
			return c1Candidates.stream().filter(e -> maps.rule1ToS1.get(e.getTarget()) == null)
					.collect(Collectors.toSet());
		}
		throw new RuntimeException("Invalid state: neither source nor target were dangling in L1!");
	}

}
