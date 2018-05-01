package org.eclipse.emf.henshin.multicda.cda;

import java.util.List;

import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cpa.result.ConflictKind;
import org.eclipse.emf.henshin.multicda.cpa.result.CriticalPair;

public class MinimalConflict extends CriticalPair {


	// Graph should be the Span here!
	public MinimalConflict(Rule r1, Rule r2, Graph minimalConflictReason, AppliedAnalysis appliedAnalysis) {
		super(r1, r2, minimalConflictReason, appliedAnalysis);
	}

	/* appliedAnalysis , rule1, rul2, minimalModel(EPackage), criticalElements(List<CriticalElement-AGG>)
	 * bereits durch "CriticalPair" gegeben!
	 * 
	 */

	public MinimalConflict(Rule firstRule, Rule originalRuleOfRule2, Span minimalConflictReason,
			List<ConflictAtom> conflictAtoms, List<Span> conflictAtomCandidates, AppliedAnalysis appliedAnalysis) {
		this(firstRule, originalRuleOfRule2, minimalConflictReason.getGraph(), appliedAnalysis);
		this.conflictAtoms = conflictAtoms;
		this.conflictAtomCandidates = conflictAtomCandidates;
	}

	/**
	 * Kind of the conflict.
	 */
	ConflictKind conflictKind;

	List<ConflictAtom> conflictAtoms;

	List<Span> conflictAtomCandidates;

	//DONE: Span anstelle von minimalModel!

	//DONE: Konstruktur

}
