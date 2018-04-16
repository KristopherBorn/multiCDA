/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.conflict;

import org.eclipse.emf.henshin.multicda.cda.Span;

/**
 * @author vincentcuccu 23.02.2018
 */
public abstract class DeleteUseConflictReason extends ConflictReason {
	private final String TAG;

	/**
	 * @param span
	 */
	public DeleteUseConflictReason(Span deleteUseConflictReason) {
		this(deleteUseConflictReason, "DUCR");
	}

	protected DeleteUseConflictReason(Span s1, String tag) {
		super(s1);
		TAG = tag;
	}

	/**
	 * 
	 */
	public final void print() {
		System.out.println(TAG + ": " + this.getGraph().getEdges() + " |\t" + this.getGraph().getNodes());
	}

}
