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

	/**
	 * @param s1
	 */
	public DeleteDeleteConflictReason(Span s1) {
		super(s1);
	}

}
