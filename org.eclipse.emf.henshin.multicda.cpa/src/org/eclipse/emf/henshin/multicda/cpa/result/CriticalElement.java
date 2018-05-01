/**
 * <copyright>
 * Copyright (c) 2010-2016 Henshin developers. All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * </copyright>
 */
package org.eclipse.emf.henshin.multicda.cpa.result;

import org.eclipse.emf.henshin.model.GraphElement;

import agg.xt_basis.GraphObject;

/**
 * This class maps the different occurrences of each critical element.
 * 
 * @author Kristopher Born
 *
 */
public class CriticalElement extends OverlapElement {
	public CriticalElement() {
		super();
	}

	public CriticalElement(GraphObject commonElementOfCriticalGraph, GraphElement elementInFirstRule,
			GraphElement elementInSecondRule) {
		super(commonElementOfCriticalGraph, elementInFirstRule, elementInSecondRule);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CriticalElement) {
			String a = toString();
			String b = obj.toString();
			return a.equals(b);
		}
		return false;
	}
}
