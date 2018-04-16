/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.conflict;

import org.eclipse.emf.henshin.multicda.cda.Span;

/**
 * @author vincentcuccu
 * 23.02.2018
 */
public class DeleteDeleteConflictReason extends DeleteUseConflictReason{
	Span s2;

	/**
	 * @param s1
	 */
	public DeleteDeleteConflictReason(Span s1, Span s2) {
		super(s1, "DDCR");
		this.s2 = s2;
	}
}
