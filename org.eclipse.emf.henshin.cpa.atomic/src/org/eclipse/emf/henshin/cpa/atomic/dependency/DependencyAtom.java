package org.eclipse.emf.henshin.cpa.atomic.dependency;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.Atom;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;

public class DependencyAtom extends Atom{
	
	boolean createEdgeDependencyAtom = false;
	Set<MinimalDependencyReason> minimalDependencyReasons;

	/**
	 * @return the deleteEdgeConflictAtom
	 */
	public boolean isCreateEdgeDependencyAtom() {
		return createEdgeDependencyAtom;
	}

	
	// WICHTIG!: die minimalDependencyReasons müssen im Anschluss noch gesetzt werden! 
	public DependencyAtom(Span span){
		this.span = span;
		minimalDependencyReasons = new HashSet<MinimalDependencyReason>();

		// required for boundary nodes
		if(span.getGraph().getNodes().size() == 2)
			createEdgeDependencyAtom = true;
			// S1 graph of a conflict atom should contain two nodes.  Conflict Atoms with one node in the S1-span are based on node creation or attribute change.
	}
	

	//stammt vom ConflictAtom und ist hier nicht mehr nutzbar!
	// in Algo Zeile 6 wird ein Atom mit den Parametern candidate und reasons initilisiert.
	// dennoch ist die Datenstruktur noch nicht klar!
//	public ConflictAtom(Span candidate, Set<MinimalConflictReason> minimalConflictReasons) {
//		this.span = candidate;
//		this.minimalConflictReasons = minimalConflictReasons;
//		for(MinimalConflictReason mcr : minimalConflictReasons){
//			mcr.addContainedConflictAtom(this);
//		}
//		// required for boundary nodes
//		if(candidate.getGraph().getNodes().size() == 2)
//			deleteEdgeConflictAtom = true;
//			// S1 graph of a conflict atom should contain two nodes.  Conflict Atoms with one node in the S1-span are based on node deletion or attribute change.
//	}
	
	// bisher werden die "reason's" nicht berücksichtigt, 
	// da auch nicht klar ist wozu diese eigentlich da sind um was es sich dabei handelt. 


	/**
	 * @return the reasons
	 */
	public Set<MinimalDependencyReason> getMinimalDependencyReasons() {
		return minimalDependencyReasons;
	}
	
	public boolean addMinimalDependencyReasons(MinimalDependencyReason mdr) {
		return minimalDependencyReasons.add(mdr);
	}

}
