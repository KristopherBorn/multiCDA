/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.conflict;

import org.eclipse.emf.henshin.multicda.cda.Span;

/**
 * @author vincentcuccu 23.02.2018
 */
public class DeleteUseConflictReason {

	private Span span1;
	private Span span2;

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("javadoc")
	public boolean isEmpty() {
		if (getSpan1().equals(null) && getSpan2().equals(null)) {
			return true;
		}
		return false;
	}

	/**
	 * @return the span1
	 */
	public Span getSpan1() {
		return span1;
	}

	/**
	 * @param span1
	 *            the span1 to set
	 */
	public void setSpan1(Span span1) {
		this.span1 = span1;
	}

	/**
	 * @return the span2
	 */
	public Span getSpan2() {
		return span2;
	}

	/**
	 * @param span2
	 *            the span2 to set
	 */
	public void setSpan2(Span span2) {
		this.span2 = span2;
	}

	/**
	 * 
	 */
	public void print() {
		if (span2 != null) {
			System.out.println("DDCR: <(\t" + span1.getGraph().getEdges() + "\t| " + span1.getGraph().getNodes() + "\t)"
					+ "(\t" + span2.getGraph().getEdges() + "\t| " + span2.getGraph().getNodes() + "\t)>");
		} else {
			System.out.println("DRCR: <(\t" + span1.getGraph().getEdges() + "\t| " + span1.getGraph().getNodes() + "\t)"
					+ "(\t)>");
		}
	}

}
