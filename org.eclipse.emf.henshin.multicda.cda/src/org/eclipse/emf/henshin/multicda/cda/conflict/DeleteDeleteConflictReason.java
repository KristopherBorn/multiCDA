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

	private Span span2;
	private Span span1;

	/**
	 * @param s1
	 * @param s2 
	 */
	public DeleteDeleteConflictReason(Span s1, Span s2) {
		super(s1);
		span1 = s1;
		this.span2 = s2;
	}
	
	/**
	 * 
	 */
	public void print() {
		if (span2 != null) {
			System.out.println("<( " + this.getGraph().getEdges() + "\t| " + this.getGraph().getNodes() + " )"
					+ "( " + span2.getGraph().getEdges() + " |\t" + span2.getGraph().getNodes() + " )>");
		}
	}
	
	@Override
	public int hashCode() {
		return span1.hashCode() * 11 + span2.hashCode() * 37;
	}

}
