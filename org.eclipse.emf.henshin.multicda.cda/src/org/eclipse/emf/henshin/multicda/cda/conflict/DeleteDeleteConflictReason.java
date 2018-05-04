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

	/**
	 * @param s1
	 */
	public DeleteDeleteConflictReason(Span s1, Span s2) {
		super(s1);
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
		final int prime = 31;
	    int result = 53;
	    result = prime * graph.hashCode() + result*(originMCRs==null?0:originMCRs.hashCode()) + span2.getGraph().hashCode();
	    return result;
	}

}
