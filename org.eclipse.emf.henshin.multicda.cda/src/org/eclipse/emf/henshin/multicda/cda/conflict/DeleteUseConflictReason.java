/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.conflict;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.multicda.cda.Span;

/**
 * @author vincentcuccu 23.02.2018
 */
public abstract class DeleteUseConflictReason extends ConflictReason {

	/**
	 * @param s1
	 */
	public DeleteUseConflictReason(Span s1) {
		super(s1);
		this.span2 = null;
	}

	private Span span2;

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
			System.out.println("DDCR: <(\t" + this.getGraph().getEdges() + "\t| " + this.getGraph().getNodes() + "\t)"
					+ "(\t" + span2.getGraph().getEdges() + " |\t" + span2.getGraph().getNodes() + "\t)>");
		} else {
			System.out.println("DRCR: " + this.getGraph().getEdges() + " |\t" + this.getGraph().getNodes());
		}
	}

}
