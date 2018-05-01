package org.eclipse.emf.henshin.multicda.cda.conflict;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.multicda.cda.Span;

public class MinimalConflictReason extends ConflictReason{
	
	private Set<ConflictAtom> containedConflictAtom;

	public MinimalConflictReason(Span minimalConflictReasonSpan) {
		super(minimalConflictReasonSpan);
		containedConflictAtom = new HashSet<ConflictAtom>();
		if(minimalConflictReasonSpan instanceof MinimalConflictReason){
			containedConflictAtom.addAll(((MinimalConflictReason) minimalConflictReasonSpan).getContainedConflictAtoms());
		}
	}

	public void addContainedConflictAtom(ConflictAtom conflictAtom) {
		containedConflictAtom.add(conflictAtom);			
	}
	
	public Set<ConflictAtom> getContainedConflictAtoms() {
		return containedConflictAtom;			
	}

	public Set<ModelElement> getDeletionElementsInRule1() {
		return this.deletionElementsInRule1;
	}
	
}
