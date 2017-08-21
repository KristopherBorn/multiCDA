package org.eclipse.emf.henshin.cpa.result;

import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Rule;

public class ConflictAtom extends CriticalPair {
	
	//TODO: vielleicht sollte man noch die Mappings zwischen  Span und den Regeln in das Datenmodell mitaufnehmen?
	
	// Graph should be the Span here!
	public ConflictAtom(Rule r1, Rule r2, Graph minimalModel) {
		super(r1, r2, minimalModel);
		// TODO Auto-generated constructor stub
	}

	//TODO: Felder
	/* appliedAnalysis , rule1, rul2, minimalModel(EPackage), criticalElements(List<CriticalElement-AGG>)
	 * bereits durch "CriticalPair" gegeben!
	 * 
	 */
	
	/**
	 * Kind of the conflict.
	 */
	ConflictKind conflictKind;
	
	//DONE: Span anstelle von minimalModel!
	
	//DONE: Konstruktur
	

}
