package org.eclipse.emf.henshin.multicda.cda.eval;

public enum Type {
	conflicts("conflict"), dependencies("dependency");
	
	private String singular;

	public String getSingularName() {
		return singular;
	}

	Type(String singular) {
		this.singular = singular;
	}
}
