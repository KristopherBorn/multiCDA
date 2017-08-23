package org.eclipse.emf.henshin.cpa.result;

import org.eclipse.emf.henshin.model.GraphElement;

import agg.xt_basis.GraphObject;

public class OverlapElement {

	public OverlapElement() {
		
	}
	
	public OverlapElement(GraphObject commonElementOfCriticalGraph, GraphElement elementInFirstRule,
			GraphElement elementInSecondRule) {
		super();
		this.commonElementOfCriticalGraph = commonElementOfCriticalGraph;
		this.elementInFirstRule = elementInFirstRule;
		this.elementInSecondRule = elementInSecondRule;
	}

	/**
	 * The critical element from within the AGG result.
	 */
	public GraphObject commonElementOfCriticalGraph;

	/**
	 * The occurrence of the critical element in the first rule.
	 */
	public GraphElement elementInFirstRule;

	/**
	 * The occurrence of the critical element in the second rule.
	 */
	public GraphElement elementInSecondRule;
}
