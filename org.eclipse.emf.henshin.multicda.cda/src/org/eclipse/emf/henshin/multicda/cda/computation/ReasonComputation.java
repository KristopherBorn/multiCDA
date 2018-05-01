package org.eclipse.emf.henshin.multicda.cda.computation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.EssentialConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;

public class ReasonComputation {

	public Set<EssentialConflictReason> computeConflictReasons(List<ConflictAtom> conflictAtoms,
			Set<ConflictReason> initialReasons) {
		Set<EssentialConflictReason> conflictReasonsDerivedFromInitialReason = new HashSet<EssentialConflictReason>();
		Set<MinimalConflictReason> originMCRs = new HashSet<MinimalConflictReason>();
		for (ConflictReason initialReason : initialReasons) {
			originMCRs.addAll(initialReason.getOriginMCRs());
			Set<ConflictAtom> byInitialReasonCoveredEdgeConflictAtoms = initialReason.getCoveredEdgeConflictAtoms();
			Set<ConflictAtom> allEdgeConflictAtoms = extractEdgeConflictAtoms(conflictAtoms);
			allEdgeConflictAtoms.removeAll(byInitialReasonCoveredEdgeConflictAtoms);
			Set<ConflictAtom> byInitialReasonUncoveredConflictAtoms = allEdgeConflictAtoms;
			Set<EssentialConflictReason> allDerivedConflictReasons = initialReason
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
