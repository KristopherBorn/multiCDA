package org.eclipse.emf.henshin.cpa.atomic.conflict;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

public class InitialConflictReason extends Span {
	
	Set<MinimalConflictReason> originMCRs;

	protected Set<ModelElement> deletionElementsInRule1;

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
//		result = prime * result + getOuterType().hashCode(); //superfluous
		result = prime * result + ((originMCRs == null) ? 0 : originMCRs.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		if (!(obj instanceof InitialConflictReason)) {
			return false;
		}
		InitialConflictReason other = (InitialConflictReason) obj;
		// superfluous
//		if (!getOuterType().equals(other.getOuterType())) {
//			return false;
//		}
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

	public InitialConflictReason(Span minimalConflictReason) {
		super(minimalConflictReason);
		if(minimalConflictReason instanceof MinimalConflictReason){
			MinimalConflictReason mcr = (MinimalConflictReason) minimalConflictReason;
			this.deletionElementsInRule1 = mcr.getDeletionElementsInRule1();
			originMCRs = new HashSet<MinimalConflictReason>();
			originMCRs.add(mcr);
		}else {
			// wenn der Konstruktur durch einen super call von der Klasse MinimalConflictReason aufgerufen wurde und 'minimalConflictReason' wirklich vom Typ "Span" ist.
			this.deletionElementsInRule1 = getDeletionElementsOfSpan(minimalConflictReason);
			originMCRs = new HashSet<MinimalConflictReason>();
		}
		
	}
	
	public InitialConflictReason(Set<Mapping> mappingsOfNewSpanInRule1, Graph graph1Copy,
			Set<Mapping> mappingsOfNewSpanInRule2, Set<MinimalConflictReason> originMCRs) {
		super(mappingsOfNewSpanInRule1, graph1Copy, mappingsOfNewSpanInRule2);
		this.deletionElementsInRule1 = getDeletionElementsOfSpan(this);
		this.originMCRs = originMCRs;
	}
	
	private Set<ModelElement> getDeletionElementsOfSpan(Set<Mapping> mappingsOfSpanInRule1, Graph graph,
			Set<Mapping> mappingsOfSpanInRule2) {
		Set<ModelElement> deletionElements = new HashSet<ModelElement>();
		// alle Elemente im Graph des Span müssen geprüft werden, ob es sich dabei um löschende Elemente der ersten Regel handelt!
		// Kanten im Graph sind (für delete-use) immer löschende Elemente (Das geht aus der Definition der ConflictAtoms und MCR hervor)
			// dafür ist es schwieriger die Kanten zu identifizieren!
		// check Nodes to be deletionElements
		for(Mapping mapping : mappingsOfSpanInRule1){
			if(mapping.getImage().getAction().getType().equals(Action.Type.DELETE))
				deletionElements.add(mapping.getImage());
		}
		// find all related Edges in Rule1
		for(Edge egdeInS : graph.getEdges()){
			Node sourceNodeInS = egdeInS.getSource();
			Node targetNodeInS = egdeInS.getTarget();
			Mapping mappingOfSourceInR1 = getMappingIntoRule(mappingsOfSpanInRule1, sourceNodeInS);
			Node sourceNodeInR1 = mappingOfSourceInR1.getImage();
			Mapping mappingOfTargetInR1 = getMappingIntoRule(mappingsOfSpanInRule1, targetNodeInS);
			Node targetNodeInR1 = mappingOfTargetInR1.getImage();
			Edge associatedEdgeInR1 = sourceNodeInR1.getOutgoing(egdeInS.getType(), targetNodeInR1); //TODO: Vorsicht! hier kann auch null rauskommen, wenn es ein bug ist!
			if(associatedEdgeInR1 != null && associatedEdgeInR1.getAction().getType().equals(Action.Type.DELETE))
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
		return getDeletionElementsOfSpan(minimalConflictReason.getMappingsInRule1(), minimalConflictReason.getGraph(), minimalConflictReason.getMappingsInRule2());
	}

	// superfluous
//	private AtomicCoreCPA getOuterType() {
//		return AtomicCoreCPA.this;
//	}

	public Set<ConflictAtom> getCoveredEdgeConflictAtoms() {
		Set<ConflictAtom> edgeConflictAtoms = new HashSet<ConflictAtom>();
		for(MinimalConflictReason mcr : originMCRs){
			 Set<ConflictAtom> containedConflictAtoms = mcr.getContainedConflictAtoms();
			 for(ConflictAtom conflictAtom : containedConflictAtoms){
				 if(conflictAtom.isDeleteEdgeConflictAtom())
					 edgeConflictAtoms.add(conflictAtom);
			 }
		}
		return edgeConflictAtoms;
	}

	public Set<ConflictReason> getAllDerivedConflictReasons(Set<ConflictAtom> byInitialReasonUncoveredConflictAtoms){
		Set<ConflictReason> derivedConflictReasons = new HashSet<ConflictReason>();
		/* über jedes uncoveredCA iterieren
		 * 	jeweils für beide Knoten abarbeiten
		 * 		für jeden Knoten jeweils alle 'use'-Knoten der zweiten Regel finden! (auch Attr. berücksichtigen!)
		 * 			An jeder Position rekursiv versuchen auch weitere Überlappungskombinationen zu bilden!
		 * 			iteriere über Kombinationen für Knoten 1 - "Nullposition" ist ebenfalls gültig und behandelt den Fall, dass Knoten 2 nciht mit einbezogen ist
		 * 				iteriere über Positionen für Knoten 2 - "Nullposition" ist ebenfalls gültig und behandelt den Fall,d ass Knoten 1 nciht mit einbezogen ist
		 * 					bei jeder Kombination noch versuchen rekursiv ein weiteres uncoveredCA miteinzubeziehen!
		 * 
		 * 		Kombinationen beider Knoten sind erlaubt, solange es keien Kante des zu löschenden Typs zwischen diesen gibt!
		 * 
		 */
		
		//initiales CR aus dem IR selbst
		if(!(this instanceof ConflictReason)){//this.toShortString()
			ConflictReason conflictReasonWithoutBA = new ConflictReason(this);
			derivedConflictReasons.add(conflictReasonWithoutBA);
		}
		
		
		//über jedes uncoveredCA iterieren
		for(ConflictAtom uncoveredCA : byInitialReasonUncoveredConflictAtoms){
			Set<Node> allreadyUsedNodesInR1 = getAlreadyUsedNodesOfR1();
			Set<Node> allreadyUsedNodesInR2 = getAlreadyUsedNodesOfR2();
			//TODO: bisher wird vererbung nicht berücksichtigt! Können boundary atoms auch bei Vererbung entstehen? Muss geprüft und ggf. angepasst werden!
			EList<Node> nodesOfUncoveredCA = uncoveredCA.getSpan().getGraph().getNodes();
			Node node1 = nodesOfUncoveredCA.get(0);
			Node node2 = nodesOfUncoveredCA.get(1);
			Rule rule2 = this.getRule2();
				
		// 	jeweils für beide Knoten abarbeiten
		// 		für jeden Knoten jeweils alle 'use'-Knoten der zweiten Regel finden! (auch Attr. berücksichtigen!)
		// 			An jeder Position rekursiv versuchen auch weitere Überlappungskombinationen zu bilden!
		// 			iteriere über Kombinationen für Knoten 1 - "Nullposition" ist ebenfalls gültig und behandelt den Fall, dass Knoten 2 nciht mit einbezogen ist
			List<Node> potentialUseNodesForN1InLhsOfR2 = new LinkedList<Node>(rule2.getLhs().getNodes(node1.getType()));
			potentialUseNodesForN1InLhsOfR2.removeAll(allreadyUsedNodesInR2);
			// Knoten aus R1 dürfen nicht mehrfach in ein CR involviert sein!
			boolean node1AlreadyUsedInR1 = allreadyUsedNodesInR1.contains(uncoveredCA.getSpan().getMappingIntoRule1(node1).getImage());
			boolean node1AlreadyUsedInR2 = allreadyUsedNodesInR2.contains(uncoveredCA.getSpan().getMappingIntoRule2(node1).getImage());
			if( ! (node1AlreadyUsedInR1 || node1AlreadyUsedInR2)){
				for(Node potentialUseNodeForN1InLhsOfR2 : potentialUseNodesForN1InLhsOfR2){
					// hier zuerst den Fall für node1 alleine (ohne node2) behandeln!
					// bei jeder Kombination noch versuchen rekursiv ein weiteres uncoveredCA miteinzubeziehen!
					// TODO: extract three times equal code to common method!
					//TODO: completion test here!
					boolean potentialUseNodeCompletesContainedBA_OrSecondUncoveredCANodeIsAlreadyPresent = potentialUseNodeCompletesContainedBAOrSecondUncoveredCANodeIsAlreadyPresent(
							uncoveredCA, node2, potentialUseNodeForN1InLhsOfR2);
					
					if(!potentialUseNodeCompletesContainedBA_OrSecondUncoveredCANodeIsAlreadyPresent){
						ConflictReason conflictReasonWithOneNewBA = new ConflictReason(this, node1, potentialUseNodeForN1InLhsOfR2, uncoveredCA);
						derivedConflictReasons.add(conflictReasonWithOneNewBA);
						Set<ConflictAtom> remainingUncoveredConflictAtomsOneBA = new HashSet<ConflictAtom>(byInitialReasonUncoveredConflictAtoms);
						remainingUncoveredConflictAtomsOneBA.remove(uncoveredCA);
						Set<ConflictReason> recursiveDerivedConflictReasonsOneBA = conflictReasonWithOneNewBA.getAllDerivedConflictReasons(remainingUncoveredConflictAtomsOneBA);
						derivedConflictReasons.addAll(recursiveDerivedConflictReasonsOneBA);
						
						// iteriere über Positionen für Knoten 2 - "Nullposition" ist ebenfalls gültig und behandelt den Fall,d ass Knoten 1 nciht mit einbezogen ist
						List<Node> potentialUseNodesForN2InLhsOfR2 = new LinkedList<Node>(rule2.getLhs().getNodes(node2.getType()));
						potentialUseNodesForN2InLhsOfR2.removeAll(allreadyUsedNodesInR2);
						potentialUseNodesForN2InLhsOfR2.remove(potentialUseNodeForN1InLhsOfR2);

						Set<Node> allreadyUsedNodesInR1OfExtendedCR = conflictReasonWithOneNewBA.getAlreadyUsedNodesOfR1();
						Set<Node> allreadyUsedNodesInR2OfExtendedCR  = conflictReasonWithOneNewBA.getAlreadyUsedNodesOfR2();
						for(Node potentialUseNodeForN2InLhsOfR2 : potentialUseNodesForN2InLhsOfR2){
							boolean node2AlreadyUsedInR1 = allreadyUsedNodesInR1OfExtendedCR.contains(uncoveredCA.getSpan().getMappingIntoRule1(node2).getImage());
							boolean node2AlreadyUsedInR2 = allreadyUsedNodesInR2OfExtendedCR.contains(potentialUseNodeForN2InLhsOfR2);
							if( ! (node2AlreadyUsedInR1 || node2AlreadyUsedInR2)){
								// 		Kombinationen beider Knoten sind erlaubt, solange es keine Kante des zu löschenden Typs zwischen diesen gibt!
								boolean node1MatchedOnCAOrigin = uncoveredCA.getSpan().getMappingIntoRule2(node1).getImage() == potentialUseNodeForN1InLhsOfR2;
								boolean node2MatchedOnCAOrigin = uncoveredCA.getSpan().getMappingIntoRule2(node2).getImage() == potentialUseNodeForN2InLhsOfR2;
								//check that its not exactly the pattern on which the conflict atom is based on
								// das heißt, dass node1 und node2 genau so in Regel zwei abgebildet werden wie im CA
								if(!(node1MatchedOnCAOrigin && node2MatchedOnCAOrigin)){ //blocking should happen once per uncoveredCA
									// TODO: extract three times equal code to common method!
									//TODO: completion test here!
									//								boolean potentialUseNodeCompletesContainedBoundaryAtom = false;
									boolean potentialUseNodeCompletesContainedBA_OrSecondUncoveredCANodeIsAlreadyPresent_N2 = potentialUseNodeCompletesContainedBAOrSecondUncoveredCANodeIsAlreadyPresent(
											uncoveredCA, node1, potentialUseNodeForN2InLhsOfR2);
									if(!potentialUseNodeCompletesContainedBA_OrSecondUncoveredCANodeIsAlreadyPresent_N2){
										ConflictReason conflictReasonWithTwoNewBA = new ConflictReason(conflictReasonWithOneNewBA, node2, potentialUseNodeForN2InLhsOfR2, uncoveredCA);
										derivedConflictReasons.add(conflictReasonWithTwoNewBA);
										Set<ConflictAtom> remainingUncoveredConflictAtomsTwoBA = new HashSet<ConflictAtom>(byInitialReasonUncoveredConflictAtoms);
										remainingUncoveredConflictAtomsTwoBA.remove(uncoveredCA);
										// 					bei jeder Kombination noch versuchen rekursiv ein weiteres uncoveredCA miteinzubeziehen!
										Set<ConflictReason> recursiveDerivedConflictReasonsTwoBA = conflictReasonWithTwoNewBA.getAllDerivedConflictReasons(remainingUncoveredConflictAtomsTwoBA);
										derivedConflictReasons.addAll(recursiveDerivedConflictReasonsTwoBA);
									}
								}
							}
						}
					}

				}
			}
			List<Node> potentialUseNodesForN2AloneInLhsOfR2 = new LinkedList<Node>(rule2.getLhs().getNodes(node2.getType()));
			potentialUseNodesForN1InLhsOfR2.removeAll(allreadyUsedNodesInR2);
			// Knoten aus R2 dürfen nicht mehrfach in ein CR involviert sein!
			boolean node2AlreadyUsedInR1 = allreadyUsedNodesInR1.contains(uncoveredCA.getSpan().getMappingIntoRule1(node2).getImage());
			boolean node2AlreadyUsedInR2 = allreadyUsedNodesInR2.contains(uncoveredCA.getSpan().getMappingIntoRule2(node2).getImage());
			if( ! (node2AlreadyUsedInR1 || node2AlreadyUsedInR2)){
				for(Node potentialUseNodeForN2InLhsOfR2 : potentialUseNodesForN2AloneInLhsOfR2){
					// 					bei jeder Kombination noch versuchen rekursiv ein weiteres uncoveredCA miteinzubeziehen!
					// TODO: extract three times equal code to common method!
					//TODO: completion test here!
//				boolean potentialUseNodeCompletesContainedBoundaryAtom = false;
					boolean potentialUseNodeCompletesContainedBA_OrSecondUncoveredCANodeIsAlreadyPresent = potentialUseNodeCompletesContainedBAOrSecondUncoveredCANodeIsAlreadyPresent(
							uncoveredCA, node2, potentialUseNodeForN2InLhsOfR2);
					if(!potentialUseNodeCompletesContainedBA_OrSecondUncoveredCANodeIsAlreadyPresent){
						ConflictReason conflictReasonWithOneNewBA = new ConflictReason(this, node2, potentialUseNodeForN2InLhsOfR2, uncoveredCA);
						derivedConflictReasons.add(conflictReasonWithOneNewBA);
						Set<ConflictAtom> remainingUncoveredConflictAtomsOneBA = new HashSet<ConflictAtom>(byInitialReasonUncoveredConflictAtoms);
						remainingUncoveredConflictAtomsOneBA.remove(uncoveredCA);
						Set<ConflictReason> recursiveDerivedConflictReasonsOneBA = conflictReasonWithOneNewBA.getAllDerivedConflictReasons(remainingUncoveredConflictAtomsOneBA);
						derivedConflictReasons.addAll(recursiveDerivedConflictReasonsOneBA);
					}
				}
			}
		}
		
		return derivedConflictReasons;
	}

	protected Set<Node> getAlreadyUsedNodesOfR1() {
		Set<Node> usedNodesOfR1 = new HashSet<Node>();
		if(graph.getNodes().size() != mappingsInRule1.size()){
			System.err.println("Error!");
		}
		for(Mapping mappingInRule1 : mappingsInRule1){
			usedNodesOfR1.add(mappingInRule1.getImage());
		}
		return usedNodesOfR1;
	}

	protected Set<Node> getAlreadyUsedNodesOfR2() {
		Set<Node> usedNodesOfR2 = new HashSet<Node>();
		if(graph.getNodes().size() != mappingsInRule2.size()){
			System.err.println("Error!");
		}
		for(Mapping mappingInRule2 : mappingsInRule2){
			usedNodesOfR2.add(mappingInRule2.getImage());
		}
		return usedNodesOfR2;
	}

	private boolean potentialUseNodeCompletesContainedBAOrSecondUncoveredCANodeIsAlreadyPresent(
			ConflictAtom uncoveredCA, Node shouldntBeUsedYetNode, Node potentiallyUseNodeInLhsOfR2) {
//		Rule rule2 = uncoveredCA.getSpan().getRule2();
		boolean potentialUseNodeCompletesContainedBA = false;
		boolean secondUncoveredCANodeIsAlreadyPresent = false;
		// wenn "this" kein ConflictReason (sondern ein ICR), dann ist es ohnehin hinfällig.
		//TODO: für den zweiten Teil stimmt das doch nciht mehr, oder?!!
		if(this instanceof ConflictReason){
			//Sonst muss geprüft werden,
			// 1. dass der use-Knoten noch nicht durch ein additionallyInvolvedConflictAtoms referenziert wird 
			//		(dieses würde sonst zum vollständigen CA und zu Duplikaten in der Ergebnissen führen)
			Set<Node> useNodesOfLhsOfR2OfAdditionallyInvolvedConflictAtoms = ((ConflictReason)this).getAllActiveInvolvedUseNodesOfLhsOfR2();
			if(useNodesOfLhsOfR2OfAdditionallyInvolvedConflictAtoms.contains(potentiallyUseNodeInLhsOfR2))
				potentialUseNodeCompletesContainedBA = true;
		}
			// 2. dass für das neue uncovered CA der zweite Knoten noch nicht vorhanden ist
			/*		d.h. hier je nach potential use node darf der andere noch nicht vom S1-graph per mapping referenziert werden!
			 */
//			Node useNodeOfUncoveredCAInLhsOfR2 = uncoveredCA.getSpan().getMappingIntoRule2(shouldntBeUsedYetNode).getImage();
			Set<Node> useNodesOfLhsOfR2OfAllInvolvedConflictAtoms = getAllUseNodesOfLhsOfR2();//getAllUseNodesOfLhsOfR2OfAllInvolvedConflictAtoms();
			if(useNodesOfLhsOfR2OfAllInvolvedConflictAtoms.contains(potentiallyUseNodeInLhsOfR2))
				secondUncoveredCANodeIsAlreadyPresent = true;
		return potentialUseNodeCompletesContainedBA || secondUncoveredCANodeIsAlreadyPresent;
	}

	private Set<Node> getAllUseNodesOfLhsOfR2() {
		Set<Node> allUseNodesOfLhsOfR2 = new HashSet<Node>();
		for(Mapping mappingInRule2 : mappingsInRule2){
			allUseNodesOfLhsOfR2.add(mappingInRule2.getImage());
		}
		return allUseNodesOfLhsOfR2;
	}

}