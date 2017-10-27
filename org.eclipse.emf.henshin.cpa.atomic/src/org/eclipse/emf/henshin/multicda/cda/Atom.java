package org.eclipse.emf.henshin.multicda.cda;

public class Atom {
	
	public Span span;
	

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
