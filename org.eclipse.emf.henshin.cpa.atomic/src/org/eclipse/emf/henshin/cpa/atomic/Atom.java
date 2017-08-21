package org.eclipse.emf.henshin.cpa.atomic;

public class Atom {
	
	protected Span span;
	

	/**
	 * @return the span
	 */
	public Span getSpan() {
		return span;
	}
	
	@Override
	public String toString(){
		return span.toString();
	}

	public String toShortString() {
		return span.toShortString();
	}

}
