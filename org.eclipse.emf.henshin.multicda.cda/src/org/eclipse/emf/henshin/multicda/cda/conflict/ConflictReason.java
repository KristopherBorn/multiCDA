package org.eclipse.emf.henshin.multicda.cda.conflict;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.multicda.cda.Span;

@SuppressWarnings("javadoc")
public class ConflictReason extends Span {

	Set<MinimalConflictReason> originMCRs;

	protected Set<GraphElement> deletionElementsInRule1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ConflictReason)) {
			return false;
		}
		ConflictReason other = (ConflictReason) obj;
		if (originMCRs == null) {
			if (other.originMCRs != null) {
				return false;
			}
		} else if (!originMCRs.equals(other.originMCRs)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the originMCRs
	 */
	public Set<MinimalConflictReason> getOriginMCRs() {
		return originMCRs;
	}

	/**
	 * @return the deletionElementsInRule1
	 */
	public Set<GraphElement> getDeletionElementsInRule1() {
		return deletionElementsInRule1;
	}

	/**
	 * @param minimalConflictReason
	 */
	public ConflictReason(Span minimalConflictReason) {
		super(minimalConflictReason);
		if (minimalConflictReason instanceof MinimalConflictReason) {
			MinimalConflictReason mcr = (MinimalConflictReason) minimalConflictReason;
			this.deletionElementsInRule1 = mcr.getDeletionElementsInRule1();
			originMCRs = new HashSet<MinimalConflictReason>();
			originMCRs.add(mcr);
		} else {
			this.deletionElementsInRule1 = getDeletionElementsOfSpan(minimalConflictReason);
			originMCRs = new HashSet<MinimalConflictReason>();
		}

	}

	/**
	 * @param mappingsOfNewSpanInRule1
	 * @param graph1Copy
	 * @param mappingsOfNewSpanInRule2
	 * @param originMCRs
	 */
	public ConflictReason(Set<Mapping> mappingsOfNewSpanInRule1, Graph graph1Copy,
			Set<Mapping> mappingsOfNewSpanInRule2, Set<MinimalConflictReason> originMCRs) {
		super(mappingsOfNewSpanInRule1, graph1Copy, mappingsOfNewSpanInRule2);
		this.deletionElementsInRule1 = getDeletionElementsOfSpan(this);
		this.originMCRs = originMCRs;
	}

	private Set<GraphElement> getDeletionElementsOfSpan(Set<Mapping> mappingsOfSpanInRule1, Graph graph,
			Set<Mapping> mappingsOfSpanInRule2) {
		Set<GraphElement> deletionElements = new HashSet<>();
		for (Mapping mapping : mappingsOfSpanInRule1) {
			if (mapping.getImage().getAction().getType().equals(Action.Type.DELETE))
				deletionElements.add(mapping.getImage());
		}
		// find all related Edges in Rule1
		for (Edge egdeInS : graph.getEdges()) {
			Node sourceNodeInS = egdeInS.getSource();
			Node targetNodeInS = egdeInS.getTarget();
			Mapping mappingOfSourceInR1 = getMappingIntoRule(mappingsOfSpanInRule1, sourceNodeInS);
			Node sourceNodeInR1 = mappingOfSourceInR1.getImage();
			Mapping mappingOfTargetInR1 = getMappingIntoRule(mappingsOfSpanInRule1, targetNodeInS);
			Node targetNodeInR1 = mappingOfTargetInR1.getImage();
			Edge associatedEdgeInR1 = sourceNodeInR1.getOutgoing(egdeInS.getType(), targetNodeInR1);
			// Vorsicht!
			// hier
			// kann
			// auch
			// null
			// rauskommen,
			// wenn
			// es
			// ein
			// bug
			// ist!
			if (associatedEdgeInR1 != null && associatedEdgeInR1.getAction().getType().equals(Action.Type.DELETE))
				deletionElements.add(associatedEdgeInR1);
		}
		return deletionElements;
	}

	private Mapping getMappingIntoRule(Set<Mapping> mappingsFromSpanInRule, Node originNode) {
		for (Mapping mapping : mappingsFromSpanInRule) {
			if (mapping.getOrigin() == originNode)
				return mapping;
		}
		return null;
	}

	private Set<GraphElement> getDeletionElementsOfSpan(Span minimalConflictReason) {
		return getDeletionElementsOfSpan(minimalConflictReason.getMappingsInRule1(), minimalConflictReason.getGraph(),
				minimalConflictReason.getMappingsInRule2());
	}

	/**
	 * @return
	 */
	public Set<ConflictAtom> getCoveredEdgeConflictAtoms() {
		Set<ConflictAtom> edgeConflictAtoms = new HashSet<ConflictAtom>();
		for (MinimalConflictReason mcr : originMCRs) {
			Set<ConflictAtom> containedConflictAtoms = mcr.getContainedConflictAtoms();
			for (ConflictAtom conflictAtom : containedConflictAtoms) {
				if (conflictAtom.isDeleteEdgeConflictAtom())
					edgeConflictAtoms.add(conflictAtom);
			}
		}
		return edgeConflictAtoms;
	}

	/**
	 * @param uncoveredConflictAtoms
	 * @return
	 */
	public Set<EssentialConflictReason> getAllDerivedConflictReasons(Set<ConflictAtom> uncoveredConflictAtoms) {
		Set<EssentialConflictReason> result = new HashSet<EssentialConflictReason>();
		if (!(this instanceof EssentialConflictReason)) {// this.toShortString()
			EssentialConflictReason conflictReasonWithoutBA = new EssentialConflictReason(this);
			result.add(conflictReasonWithoutBA);
		}

		for (ConflictAtom uncoveredCA : uncoveredConflictAtoms) {
			Set<Node> usedR1 = getUsedNodesOfR1();
			Set<Node> usedR2 = getUsedNodesOfR2();

			EList<Node> nodesOfUncoveredCA = uncoveredCA.getGraph().getNodes();
			if (nodesOfUncoveredCA.size() <= 1)
				return result;
			Node node1 = nodesOfUncoveredCA.get(0);
			Node node2 = nodesOfUncoveredCA.get(1);

			List<Node> potentialUsesN1R2 = new LinkedList<Node>(getRule2().getLhs().getNodes(node1.getType()));
			potentialUsesN1R2.removeAll(usedR2);
			boolean node1UsedInR1 = usedR1.contains(uncoveredCA.getMappingIntoRule1(node1).getImage());
			boolean node1UsedInR2 = usedR2.contains(uncoveredCA.getMappingIntoRule2(node1).getImage());
			if (!node1UsedInR1 && !node1UsedInR2) {
				for (Node potentialUseN1R2 : potentialUsesN1R2) {
					processPotentialUsesN1R2(uncoveredConflictAtoms, uncoveredCA, node1, node2, usedR2,
							potentialUseN1R2, result);
				}
			}

			List<Node> potentialUseNodesN2AloneR2 = new LinkedList<Node>(getRule2().getLhs().getNodes(node2.getType()));
			potentialUsesN1R2.removeAll(usedR2);
			// Knoten aus R2 d�rfen nicht mehrfach in ein CR involviert sein!
			boolean node2AlreadyUsedInR1 = usedR1.contains(uncoveredCA.getMappingIntoRule1(node2).getImage());
			boolean node2AlreadyUsedInR2 = usedR2.contains(uncoveredCA.getMappingIntoRule2(node2).getImage());
			if (!node2AlreadyUsedInR1 & !node2AlreadyUsedInR2) {
				for (Node potentialUseN2R2 : potentialUseNodesN2AloneR2) {
					extendCR(this, uncoveredConflictAtoms, uncoveredCA, node2, potentialUseN2R2, result);
				}
			}
		}

		return result;
	}

	private EssentialConflictReason extendCR(ConflictReason original,
			Set<ConflictAtom> byInitialReasonUncoveredConflictAtoms, ConflictAtom uncoveredCA, Node node2,
			Node potentialUseN2R2, Set<EssentialConflictReason> result) {
		boolean stop = checkStoppingCriterion(uncoveredCA, potentialUseN2R2);
		if (!stop) {
			EssentialConflictReason extendedCR = new EssentialConflictReason(original, node2, potentialUseN2R2,
					uncoveredCA);
			result.add(extendedCR);
			Set<ConflictAtom> remainingUncoveredCAs = new HashSet<ConflictAtom>(byInitialReasonUncoveredConflictAtoms);
			remainingUncoveredCAs.remove(uncoveredCA);
			Set<EssentialConflictReason> recursivelyDerivedCRs = extendedCR
					.getAllDerivedConflictReasons(remainingUncoveredCAs);
			result.addAll(recursivelyDerivedCRs);
			return extendedCR;
		} else
			return null;
	}

	private void processPotentialUsesN1R2(Set<ConflictAtom> uncoveredCAs, ConflictAtom uncoveredCA, Node node1,
			Node node2, Set<Node> usedR2, Node potentialUseN1R2, Set<EssentialConflictReason> result) {

		EssentialConflictReason extendedCR = extendCR(this, uncoveredCAs, uncoveredCA, node1, potentialUseN1R2, result);

		if (extendedCR != null) {

			List<Node> potentialUsesN2R2 = new LinkedList<Node>(getRule2().getLhs().getNodes(node2.getType()));
			potentialUsesN2R2.removeAll(usedR2);
			potentialUsesN2R2.remove(potentialUseN1R2);

			Set<Node> usedR1ExtendedCR = extendedCR.getUsedNodesOfR1();
			Set<Node> usedR2ExtendedCR = extendedCR.getUsedNodesOfR2();
			for (Node potentialUseN2N2 : potentialUsesN2R2) {
				boolean usedInR1 = usedR1ExtendedCR.contains(uncoveredCA.getMappingIntoRule1(node2).getImage());
				boolean usedInR2 = usedR2ExtendedCR.contains(potentialUseN2N2);
				if (!usedInR1 && !usedInR2) {
					boolean node1MatchedOnCAOrigin = uncoveredCA.getMappingIntoRule2(node1)
							.getImage() == potentialUseN1R2;
					boolean node2MatchedOnCAOrigin = uncoveredCA.getMappingIntoRule2(node2)
							.getImage() == potentialUseN2N2;
					if (!(node1MatchedOnCAOrigin && node2MatchedOnCAOrigin)) {
						boolean stop2 = checkStoppingCriterion(uncoveredCA, potentialUseN2N2);
						if (!stop2) {

							extendCR(extendedCR, uncoveredCAs, uncoveredCA, node2, potentialUseN2N2, result);
						}
					}
				}
			}
		}

	}

	protected Set<Node> getUsedNodesOfR1() {
		Set<Node> usedNodesOfR1 = new HashSet<Node>();
		if (getGraph().getNodes().size() != getMappingsInRule1().size()) {
			System.err.println("Error!");
		}
		for (Mapping mappingInRule1 : getMappingsInRule1()) {
			usedNodesOfR1.add(mappingInRule1.getImage());
		}
		return usedNodesOfR1;
	}

	protected Set<Node> getUsedNodesOfR2() {
		Set<Node> usedNodesOfR2 = new HashSet<Node>();
		if (getGraph().getNodes().size() != getMappingsInRule2().size()) {
			System.err.println("Error!");
		}
		for (Mapping mappingInRule2 : getMappingsInRule2()) {
			usedNodesOfR2.add(mappingInRule2.getImage());
		}
		return usedNodesOfR2;
	}

	private boolean checkStoppingCriterion(ConflictAtom uncoveredCA, Node potentialUseInR2) {

		boolean potentialUseNodeCompletesContainedBA = false;
		boolean secondUncoveredCANodeIsAlreadyPresent = false;

		if (this instanceof EssentialConflictReason) {
			Set<Node> lhsNodesOfR2UsedByAdditionalCAs = ((EssentialConflictReason) this)
					.getLhsNodesOfR2UsedByAdditionalCAs();
			if (lhsNodesOfR2UsedByAdditionalCAs.contains(potentialUseInR2))
				potentialUseNodeCompletesContainedBA = true;

			Set<Node> useNodesOfR2OfAllInvolvedCAs = getAllUseNodesOfR2();// getAllUseNodesOfLhsOfR2OfAllInvolvedConflictAtoms();
			if (useNodesOfR2OfAllInvolvedCAs.contains(potentialUseInR2))
				secondUncoveredCANodeIsAlreadyPresent = true;

		}
		return potentialUseNodeCompletesContainedBA || secondUncoveredCANodeIsAlreadyPresent;
	}

	private Set<Node> getAllUseNodesOfR2() {
		Set<Node> allUseNodesOfLhsOfR2 = new HashSet<Node>();
		for (Mapping mappingInRule2 : getMappingsInRule2()) {
			allUseNodesOfLhsOfR2.add(mappingInRule2.getImage());
		}
		return allUseNodesOfLhsOfR2;
	}

}