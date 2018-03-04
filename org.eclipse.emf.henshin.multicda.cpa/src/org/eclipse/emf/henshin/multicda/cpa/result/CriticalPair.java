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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.model.Rule;

/**
 * This class represents a critical pair
 * 
 * @author Florian Heß, Kristopher Born
 *
 */
public abstract class CriticalPair {
	
	public enum AppliedAnalysis {
		CONFLICT_PART_CANDIDATE("conflict_part_candidate"), CONFLICT_ATOM("conflict_atom"), MINIMAL_CONFLICT_REASON("minimal_conflict_reason"), ESSENTIAL("essential"), COMPLETE("complete");
		
		private String name;

		/**
		 * Default internal constructor.
		 * 
		 * @param nameOfInvolvedRules The name of the conflict kind.
		 */
		private AppliedAnalysis(String s) {
			name = s;
		}

		/**
		 * Returns the name of the conflict kind.
		 * 
		 * @return The name of the conflict kind.
		 */
		public String toString() {
			return name;
		}
	};
	
	private AppliedAnalysis appliedAnalysis;// = AppliedAnalysis.COMPLETE; // Es sollte dafür keien default Fall geben!
	

	/**
	 * One of the two rules which lead to the critical pair.
	 */
	private Rule r1;

	/**
	 * One of the two rules which lead to the critical pair.
	 */
	private Rule r2;

	/**
	 * The minimal model, which allows to observe the circumstance of the critical pair.
	 */
	private EObject minimalModel;

	/**
	 * The critical elements, which are the root cause of the critical pair.
	 */
	private List<CriticalElement> criticalElements;

	protected CriticalPair(Rule r1, Rule r2, EObject minimalModel) {
		this.r1 = r1;
		this.r2 = r2;
		this.minimalModel = minimalModel;
		criticalElements = new ArrayList<CriticalElement>();
	}

	/**
	 * Returns one of the two rules which lead to the critical pair.
	 * 
	 * @return One of the two rules which lead to the critical pair.
	 */
	public Rule getFirstRule() {
		return r1;
	}

	/**
	 * Returns one of the two rules which lead to the critical pair.
	 * 
	 * @return One of the two rules which lead to the critical pair.
	 */
	public Rule getSecondRule() {
		return r2;
	}

	/**
	 * Returns the minimal model to inspect the relation of the critical pair.
	 * 
	 * @return The minimal model to inspect the relation of the critical pair.
	 */
	public EObject getMinimalModel() {
		return minimalModel;
	}

	/**
	 * Adds critical elements to the list of critical elements of this critical pair, given that each critical pair is
	 * complete.
	 * 
	 * @param newCriticalElements critical elements of this critical pair.
	 * @return <code>true</code> if all the critical elements were complete and thus could be all added.
	 */
	public boolean addCriticalElements(List<CriticalElement> newCriticalElements) {
		if (criticalElements == null)
			criticalElements = new ArrayList<CriticalElement>();

		boolean allCriticalElementsComplete = true;

		for (CriticalElement criticalElement : newCriticalElements) {
			if (criticalElement.commonElementOfCriticalGraph == null || criticalElement.elementInFirstRule == null
					|| criticalElement.elementInSecondRule == null) {
				allCriticalElementsComplete = false;
				// es muss auch möglich sein critical elements hinzuzufügen ohne das Element aus AGG - wie erknant durch das laden persitierter CPs
				// TODO: entsprechend muss die JavaDoc bzw. der Rückgabewert der MEthode angepasst werden.
				criticalElements.add(criticalElement);
			} else {
				allCriticalElementsComplete &= criticalElements.add(criticalElement);
			}
		}
		return allCriticalElementsComplete;
	}


	/**
	 * Returns a List of critical elements of this critical pair.
	 * 
	 * @return A List of <code>CriticalElement</code>s of this critical pair.
	 */
	public List<CriticalElement> getCriticalElements() {
		return criticalElements;
	}

	//TODO: improve comment on method!
	/**
	 * @return the appliedAnalysis
	 */
	public AppliedAnalysis getAppliedAnalysis() {
		return appliedAnalysis;
	}

	/**
	 * @param appliedAnalysis the appliedAnalysis to set
	 */
	public void setAppliedAnalysis(AppliedAnalysis appliedAnalysis) {
		this.appliedAnalysis = appliedAnalysis;
	}

}
