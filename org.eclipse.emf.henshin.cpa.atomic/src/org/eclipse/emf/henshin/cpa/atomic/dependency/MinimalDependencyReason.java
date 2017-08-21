package org.eclipse.emf.henshin.cpa.atomic.dependency;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.model.ModelElement;

public class MinimalDependencyReason extends InitialDependencyReason{
	private Set<DependencyAtom> containedDependencyAtoms;

	public MinimalDependencyReason(MinimalConflictReason minimalConflictReason, Set<MinimalDependencyReason> originMDRs) {
		super(minimalConflictReason.getMappingsInRule1(), minimalConflictReason.getGraph(), minimalConflictReason.getMappingsInRule2(), originMDRs);
		
		// superfluous due to constructor cal before.
//		this.mappingsInRule1 = minimalConflictReason.getMappingsInRule1();
//		this.mappingsInRule2 = minimalConflictReason.getMappingsInRule2();
//		this.graph = minimalConflictReason.getGraph();
		
//		super(minimalDependencyReasonSpan); // superfluous?
		containedDependencyAtoms = new HashSet<DependencyAtom>();
		// superfluous? have to be added later on.
//		if(minimalDependencyReasonSpan instanceof MinimalDependencyReason){
//			containedDependencyAtoms.addAll(((MinimalDependencyReason) minimalDependencyReasonSpan).getContainedDependencyAtoms());
//		}
	}

	public void addContainedDependencyAtom(DependencyAtom dependencyAtom) {
		containedDependencyAtoms.add(dependencyAtom);			
	}
	
	public Set<DependencyAtom> getContainedDependencyAtoms() {
		return containedDependencyAtoms;			
	}

	public Set<ModelElement> getCreationElementsInRule1() {
		return creationElementsInRule1;
	}
	
}
