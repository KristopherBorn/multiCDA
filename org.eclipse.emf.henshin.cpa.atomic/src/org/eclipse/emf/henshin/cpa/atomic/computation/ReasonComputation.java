package org.eclipse.emf.henshin.cpa.atomic.computation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;

public class ReasonComputation {

	public Set<ConflictReason> computeConflictReasons(List<ConflictAtom> conflictAtoms,
			Set<InitialReason> initialReasons) {
		Set<ConflictReason> conflictReasonsDerivedFromInitialReason = new HashSet<ConflictReason>();
		Set<MinimalConflictReason> originMCRs = new HashSet<MinimalConflictReason>();
		for (InitialReason initialReason : initialReasons) {
			originMCRs.addAll(initialReason.getOriginMCRs());
			Set<ConflictAtom> byInitialReasonCoveredEdgeConflictAtoms = initialReason.getCoveredEdgeConflictAtoms();
			Set<ConflictAtom> allEdgeConflictAtoms = extractEdgeConflictAtoms(conflictAtoms);
			allEdgeConflictAtoms.removeAll(byInitialReasonCoveredEdgeConflictAtoms);
			Set<ConflictAtom> byInitialReasonUncoveredConflictAtoms = allEdgeConflictAtoms;
			Set<ConflictReason> allDerivedConflictReasons = initialReason
					.getAllDerivedConflictReasons(byInitialReasonUncoveredConflictAtoms);
			conflictReasonsDerivedFromInitialReason.addAll(allDerivedConflictReasons);
		}
		return conflictReasonsDerivedFromInitialReason;
	}
	


	public Set<ConflictAtom> extractEdgeConflictAtoms(List<ConflictAtom> computedConflictAtoms) {
		Set<ConflictAtom> edgeConflictAtoms = new HashSet<ConflictAtom>();
		for (ConflictAtom ca : computedConflictAtoms) {
			if (ca.isDeleteEdgeConflictAtom())
				edgeConflictAtoms.add(ca);
		}
		return edgeConflictAtoms;
	}
}
