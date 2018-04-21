package org.eclipse.emf.henshin.multicda.cda.conflict;

import org.eclipse.emf.henshin.multicda.cda.Span;

/**
 * 
 * @author vincentcuccu
 * @date 17.12.2017
 */
public class DeleteReadConflictReason extends DeleteUseConflictReason{

	/**
	 * @param s1
	 */
	public DeleteReadConflictReason(Span s1) {
		super(s1);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason#print()
	 */
	@Override
	public void print() {
		System.out.println("DRCR: " + this.getGraph().getEdges() + " |\t" + this.getGraph().getNodes());
	}

}
