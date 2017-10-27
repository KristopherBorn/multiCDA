package org.eclipse.emf.henshin.multicda.cda.conflict;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.Span;

public class InitialReason extends Span {

	Set<MinimalConflictReason> originMCRs;

	protected Set<ModelElement> deletionElementsInRule1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		// result = prime * result + getOuterType().hashCode(); //superfluous
		result = prime * result + ((originMCRs == null) ? 0 : originMCRs.hashCode());
		return result;
	}

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
		if (!(obj instanceof InitialReason)) {
			return false;
		}
		InitialReason other = (InitialReason) obj;
		// superfluous
		// if (!getOuterType().equals(other.getOuterType())) {
		// return false;
		// }
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
	public Set<ModelElement> getDeletionElementsInRule1() {
		return deletionElementsInRule1;
	}

	public InitialReason(Span minimalConflictReason) {
		super(minimalConflictReason);
		if (minimalConflictReason instanceof MinimalConflictReason) {
			MinimalConflictReason mcr = (MinimalConflictReason) minimalConflictReason;
			this.deletionElementsInRule1 = mcr.getDeletionElementsInRule1();
			originMCRs = new HashSet<MinimalConflictReason>();
			originMCRs.add(mcr);
		} else {
			// wenn der Konstruktur durch einen super call von der Klasse
			// MinimalConflictReason aufgerufen wurde und
			// 'minimalConflictReason' wirklich vom Typ "Span" ist.
			this.deletionElementsInRule1 = getDeletionElementsOfSpan(minimalConflictReason);
			originMCRs = new HashSet<MinimalConflictReason>();
		}

	}

	public InitialReason(Set<Mapping> mappingsOfNewSpanInRule1, Graph graph1Copy,
			Set<Mapping> mappingsOfNewSpanInRule2, Set<MinimalConflictReason> originMCRs) {
		super(mappingsOfNewSpanInRule1, graph1Copy, mappingsOfNewSpanInRule2);
		this.deletionElementsInRule1 = getDeletionElementsOfSpan(this);
		this.originMCRs = originMCRs;
	}

	private Set<ModelElement> getDeletionElementsOfSpan(Set<Mapping> mappingsOfSpanInRule1, Graph graph,
			Set<Mapping> mappingsOfSpanInRule2) {
		Set<ModelElement> deletionElements = new HashSet<ModelElement>();
		// alle Elemente im Graph des Span müssen geprüft werden, ob es sich
		// dabei um löschende Elemente der ersten Regel handelt!
		// Kanten im Graph sind (für delete-use) immer löschende Elemente (Das
		// geht aus der Definition der ConflictAtoms und MCR hervor)
		// dafür ist es schwieriger die Kanten zu identifizieren!
		// check Nodes to be deletionElements
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
			Edge associatedEdgeInR1 = sourceNodeInR1.getOutgoing(egdeInS.getType(), targetNodeInR1); // TODO:
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

	// TODO use "getMappingWithImage(...)" method
	private Mapping getMappingIntoRule(Set<Mapping> mappingsFromSpanInRule, Node originNode) {
		for (Mapping mapping : mappingsFromSpanInRule) {
			if (mapping.getOrigin() == originNode)
				return mapping;
		}
		return null;
	}

	private Set<ModelElement> getDeletionElementsOfSpan(Span minimalConflictReason) {
		return getDeletionElementsOfSpan(minimalConflictReason.getMappingsInRule1(), minimalConflictReason.getGraph(),
				minimalConflictReason.getMappingsInRule2());
	}

	// superfluous
	// private AtomicCoreCPA getOuterType() {
	// return AtomicCoreCPA.this;
	// }

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

	public Set<ConflictReason> getAllDerivedConflictReasons(Set<ConflictAtom> uncoveredConflictAtoms) {
		Set<ConflictReason> result = new HashSet<ConflictReason>();
		/*
		 * über jedes uncoveredCA iterieren jeweils für beide Knoten abarbeiten
		 * für jeden Knoten jeweils alle 'use'-Knoten der zweiten Regel finden!
		 * (auch Attr. berücksichtigen!) An jeder Position rekursiv versuchen
		 * auch weitere Überlappungskombinationen zu bilden! iteriere über
		 * Kombinationen für Knoten 1 - "Nullposition" ist ebenfalls gültig und
		 * behandelt den Fall, dass Knoten 2 nciht mit einbezogen ist iteriere
		 * über Positionen für Knoten 2 - "Nullposition" ist ebenfalls gültig
		 * und behandelt den Fall,d ass Knoten 1 nciht mit einbezogen ist bei
		 * jeder Kombination noch versuchen rekursiv ein weiteres uncoveredCA
		 * miteinzubeziehen!
		 * 
		 * Kombinationen beider Knoten sind erlaubt, solange es keien Kante des
		 * zu löschenden Typs zwischen diesen gibt!
		 * 
		 */

		// initiales CR aus dem IR selbst
		if (!(this instanceof ConflictReason)) {// this.toShortString()
			ConflictReason conflictReasonWithoutBA = new ConflictReason(this);
			result.add(conflictReasonWithoutBA);
		}

		// über jedes uncoveredCA iterieren
		for (ConflictAtom uncoveredCA : uncoveredConflictAtoms) {
			Set<Node> usedR1 = getUsedNodesOfR1();
			Set<Node> usedR2 = getUsedNodesOfR2();

			// TODO: bisher wird vererbung nicht berücksichtigt! Können boundary
			// atoms auch bei Vererbung entstehen? Muss geprüft und ggf.
			// angepasst werden!
			EList<Node> nodesOfUncoveredCA = uncoveredCA.getSpan().getGraph().getNodes();
			Node node1 = nodesOfUncoveredCA.get(0);
			Node node2 = nodesOfUncoveredCA.get(1);

			// jeweils für beide Knoten abarbeiten
			// für jeden Knoten jeweils alle 'use'-Knoten der zweiten Regel
			// finden! (auch Attr. berücksichtigen!)
			// An jeder Position rekursiv versuchen auch weitere
			// Überlappungskombinationen zu bilden!
			// iteriere über Kombinationen für Knoten 1 - "Nullposition" ist
			// ebenfalls gültig und behandelt den Fall, dass Knoten 2 nciht mit
			// einbezogen ist
			List<Node> potentialUsesN1R2 = new LinkedList<Node>(rule2.getLhs().getNodes(node1.getType()));
			potentialUsesN1R2.removeAll(usedR2);
			// Knoten aus R1 dürfen nicht mehrfach in ein CR involviert sein!
			boolean node1UsedInR1 = usedR1.contains(uncoveredCA.getSpan().getMappingIntoRule1(node1).getImage());
			boolean node1UsedInR2 = usedR2.contains(uncoveredCA.getSpan().getMappingIntoRule2(node1).getImage());
			if (!node1UsedInR1 && !node1UsedInR2) {
				for (Node potentialUseN1R2 : potentialUsesN1R2) {
					processPotentialUsesN1R2(uncoveredConflictAtoms, uncoveredCA, node1, node2, usedR2,
							potentialUseN1R2, result);
				}
			}

			List<Node> potentialUseNodesN2AloneR2 = new LinkedList<Node>(rule2.getLhs().getNodes(node2.getType()));
			potentialUsesN1R2.removeAll(usedR2);
			// Knoten aus R2 dürfen nicht mehrfach in ein CR involviert sein!
			boolean node2AlreadyUsedInR1 = usedR1.contains(uncoveredCA.getSpan().getMappingIntoRule1(node2).getImage());
			boolean node2AlreadyUsedInR2 = usedR2.contains(uncoveredCA.getSpan().getMappingIntoRule2(node2).getImage());
			if (!node2AlreadyUsedInR1 & !node2AlreadyUsedInR2) {
				for (Node potentialUseN2R2 : potentialUseNodesN2AloneR2) {
					// bei jeder Kombination noch versuchen rekursiv ein
					// weiteres uncoveredCA miteinzubeziehen!
					// TODO: completion test here!
					// boolean potentialUseNodeCompletesContainedBoundaryAtom =
					// false;
					extendCR(this, uncoveredConflictAtoms, uncoveredCA, node2, potentialUseN2R2, result);
				}
			}
		}

		return result;
	}

	private ConflictReason extendCR(InitialReason original,
			Set<ConflictAtom> byInitialReasonUncoveredConflictAtoms, ConflictAtom uncoveredCA, Node node2,
			Node potentialUseN2R2, Set<ConflictReason> result) {
		// stopping criterion = potentialUse completes contained, or second
		// uncovered CA node is
		// already present
		boolean stop = checkStoppingCriterion(uncoveredCA, potentialUseN2R2);
		if (!stop) {
			ConflictReason extendedCR = new ConflictReason(original, node2, potentialUseN2R2, uncoveredCA);
			result.add(extendedCR);
			Set<ConflictAtom> remainingUncoveredCAs = new HashSet<ConflictAtom>(byInitialReasonUncoveredConflictAtoms);
			remainingUncoveredCAs.remove(uncoveredCA);
			Set<ConflictReason> recursivelyDerivedCRs = extendedCR.getAllDerivedConflictReasons(remainingUncoveredCAs);
			result.addAll(recursivelyDerivedCRs);
			return extendedCR;
		} else
			return null;
	}

	private void processPotentialUsesN1R2(Set<ConflictAtom> uncoveredCAs, ConflictAtom uncoveredCA, Node node1,
			Node node2, Set<Node> usedR2, Node potentialUseN1R2, Set<ConflictReason> result) {
		// hier zuerst den Fall für node1 alleine (ohne node2) behandeln!
		// bei jeder Kombination noch versuchen rekursiv ein weiteres
		// uncoveredCA miteinzubeziehen!
		// TODO: extract three times equal code to common method!
		// TODO: completion test here!

		ConflictReason extendedCR = extendCR(this, uncoveredCAs, uncoveredCA, node1, potentialUseN1R2, result);

		if (extendedCR != null) {

			// iteriere über Positionen für Knoten 2 - "Nullposition" ist
			// ebenfalls gültig und behandelt den Fall, dass Knoten 1 nicht mit
			// einbezogen ist
			List<Node> potentialUsesN2R2 = new LinkedList<Node>(rule2.getLhs().getNodes(node2.getType()));
			potentialUsesN2R2.removeAll(usedR2);
			potentialUsesN2R2.remove(potentialUseN1R2);

			Set<Node> usedR1ExtendedCR = extendedCR.getUsedNodesOfR1();
			Set<Node> usedR2ExtendedCR = extendedCR.getUsedNodesOfR2();
			for (Node potentialUseN2N2 : potentialUsesN2R2) {
				boolean usedInR1 = usedR1ExtendedCR
						.contains(uncoveredCA.getSpan().getMappingIntoRule1(node2).getImage());
				boolean usedInR2 = usedR2ExtendedCR.contains(potentialUseN2N2);
				if (!usedInR1 && !usedInR2) {
					// Kombinationen beider Knoten sind erlaubt, solange es
					// keine Kante des zu löschenden Typs zwischen diesen gibt!
					boolean node1MatchedOnCAOrigin = uncoveredCA.getSpan().getMappingIntoRule2(node1)
							.getImage() == potentialUseN1R2;
					boolean node2MatchedOnCAOrigin = uncoveredCA.getSpan().getMappingIntoRule2(node2)
							.getImage() == potentialUseN2N2;
					// check that its not exactly the pattern on which the
					// conflict atom is based on
					// das heißt, dass node1 und node2 genau so in Regel zwei
					// abgebildet werden wie im CA
					if (!(node1MatchedOnCAOrigin && node2MatchedOnCAOrigin)) {
						// blocking should happen once per uncoveredCA
						// TODO: extract three times equal code to common
						// method!
						// TODO: completion test here!
						// boolean
						// potentialUseNodeCompletesContainedBoundaryAtom =
						// false;
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
		if (graph.getNodes().size() != mappingsInRule1.size()) {
			System.err.println("Error!");
		}
		for (Mapping mappingInRule1 : mappingsInRule1) {
			usedNodesOfR1.add(mappingInRule1.getImage());
		}
		return usedNodesOfR1;
	}

	protected Set<Node> getUsedNodesOfR2() {
		Set<Node> usedNodesOfR2 = new HashSet<Node>();
		if (graph.getNodes().size() != mappingsInRule2.size()) {
			System.err.println("Error!");
		}
		for (Mapping mappingInRule2 : mappingsInRule2) {
			usedNodesOfR2.add(mappingInRule2.getImage());
		}
		return usedNodesOfR2;
	}

	private boolean checkStoppingCriterion(ConflictAtom uncoveredCA, Node potentialUseInR2) {

		boolean potentialUseNodeCompletesContainedBA = false;
		boolean secondUncoveredCANodeIsAlreadyPresent = false;

		// wenn "this" kein ConflictReason (sondern ein ICR), dann ist es
		// ohnehin hinfällig.
		// TODO: für den zweiten Teil stimmt das doch nciht mehr, oder?!!
		if (this instanceof ConflictReason) {
			// Sonst muss geprüft werden,
			// 1. dass der use-Knoten noch nicht durch ein
			// additionalConflictAtom referenziert wird
			// (dieses würde sonst zum vollständigen CA und zu Duplikaten in der
			// Ergebnissen führen)
			Set<Node> lhsNodesOfR2UsedByAdditionalCAs = ((ConflictReason) this).getLhsNodesOfR2UsedByAdditionalCAs();
			if (lhsNodesOfR2UsedByAdditionalCAs.contains(potentialUseInR2))
				potentialUseNodeCompletesContainedBA = true;

			// 2. dass für das neue uncovered CA der zweite Knoten noch nicht
			// vorhanden ist
			/*
			 * d.h. hier je nach potential use node darf der andere noch nicht
			 * vom S1-graph per mapping referenziert werden!
			 */
			// Node useNodeOfUncoveredCAInLhsOfR2 =
			// uncoveredCA.getSpan().getMappingIntoRule2(shouldntBeUsedYetNode).getImage();
			Set<Node> useNodesOfR2OfAllInvolvedCAs = getAllUseNodesOfR2();// getAllUseNodesOfLhsOfR2OfAllInvolvedConflictAtoms();
			if (useNodesOfR2OfAllInvolvedCAs.contains(potentialUseInR2))
				secondUncoveredCANodeIsAlreadyPresent = true;

		}
		return potentialUseNodeCompletesContainedBA || secondUncoveredCANodeIsAlreadyPresent;
	}

	private Set<Node> getAllUseNodesOfR2() {
		Set<Node> allUseNodesOfLhsOfR2 = new HashSet<Node>();
		for (Mapping mappingInRule2 : mappingsInRule2) {
			allUseNodesOfLhsOfR2.add(mappingInRule2.getImage());
		}
		return allUseNodesOfLhsOfR2;
	}

}