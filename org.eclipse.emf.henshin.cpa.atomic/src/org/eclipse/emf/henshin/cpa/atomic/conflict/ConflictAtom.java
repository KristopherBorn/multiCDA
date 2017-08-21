package org.eclipse.emf.henshin.cpa.atomic.conflict;

import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.Atom;
import org.eclipse.emf.henshin.cpa.atomic.Span;

// TODO: noch ist unklar ob eine solche Datenstruktur notwendig ist,
// oder es sich um Instanzen einer bereits bekannten Datenstruktur handelt.
// Je nach Ergebnis l�schen oder in eigenst�ndiges class-file auslagern.
public class ConflictAtom extends Atom{
	
	
	boolean deleteEdgeConflictAtom = false;
	Set<MinimalConflictReason> minimalConflictReasons;

	/**
	 * @return the deleteEdgeConflictAtom
	 */
	public boolean isDeleteEdgeConflictAtom() {
		return deleteEdgeConflictAtom;
	}


	// in Algo Zeile 6 wird ein Atom mit den Parametern candidate und reasons initilisiert.
	// dennoch ist die Datenstruktur noch nicht klar!
	public ConflictAtom(Span candidate, Set<MinimalConflictReason> minimalConflictReasons) {
		this.span = candidate;
		this.minimalConflictReasons = minimalConflictReasons;
		for(MinimalConflictReason mcr : minimalConflictReasons){
			mcr.addContainedConflictAtom(this);
		}
		// required for boundary nodes
		if(candidate.getGraph().getNodes().size() == 2)
			deleteEdgeConflictAtom = true;
			// S1 graph of a conflict atom should contain two nodes.  Conflict Atoms with one node in the S1-span are based on node deletion or attribute change.
	}
	
	// bisher werden die "reason's" nicht ber�cksichtigt, 
	// da auch nicht klar ist wozu diese eigentlich da sind um was es sich dabei handelt. 


	/**
	 * @return the reasons
	 */
	public Set<MinimalConflictReason> getMinimalConflictReasons() {
		return minimalConflictReasons;
	}

}
