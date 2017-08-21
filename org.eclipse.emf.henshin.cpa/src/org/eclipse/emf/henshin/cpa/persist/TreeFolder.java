/**
 * <copyright>
 * Copyright (c) 2010-2016 Henshin developers. All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * </copyright>
 */
package org.eclipse.emf.henshin.cpa.persist;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the structure for persisting the results. Each TreeFolder represents the combination of two
 * rules for which critical pairs exist.
 * 
 * @author Kristopher Born
 *
 */
public class TreeFolder {

	/**
	 * List of nodes, of which each node represents a single critical pair.
	 */
	List<Object> containedElements;

	/**
	 * A name combining the two involved rules.
	 */
	String nameOfInvolvedRules;

	/**
	 * The default constructor.
	 * 
	 * @param nameOfInvolvedRules The name of the two involved rules.
	 */
	public TreeFolder(String nameOfInvolvedRules) {
		this.nameOfInvolvedRules = nameOfInvolvedRules;
		containedElements = new LinkedList<Object>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add()
	 */
	public boolean addChild(Object child) {
		// criticalPairNode.setParent(this);
		return containedElements.add(child);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove()
	 */
	public boolean removeChild(Object child) {
		// child.setParent(null);
		return containedElements.remove(child);
	}

	/**
	 * Returns the combined name of the two involved rules.
	 * 
	 * @return The combined name of the two involved rules.
	 */
	public String toString() {
		return nameOfInvolvedRules;
	}

	/**
	 * Returns the information if critical pair results are contained.
	 * 
	 * @return whether the number of contained single critical pairs is greater than zero.
	 */
	public boolean hasChildren() {
		return containedElements.size() > 0;
	}

	/**
	 * Returns the contained single critical pairs as an Array.
	 * 
	 * @return the contained single critical pairs as an Array.
	 */
	public Object[] getChildren() {
		return /*(CriticalPairNode[])*/ containedElements.toArray(); //toArray(new CriticalPairNode[containedElements.size()]); //old/outdated code
	}
}
