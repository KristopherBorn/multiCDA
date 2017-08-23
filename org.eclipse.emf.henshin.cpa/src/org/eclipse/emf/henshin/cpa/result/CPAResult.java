/**
 * <copyright>
 * Copyright (c) 2010-2016 Henshin developers. All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * </copyright>
 */
package org.eclipse.emf.henshin.cpa.result;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.result.CriticalPair.AppliedAnalysis;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.*;

import agg.xt_basis.GraphObject;
import agg.xt_basis.Node;

/**
 * This class stores all the <code>CriticalPair</code>s as a result of a
 * critical pair analysis.
 * 
 * @author Florian Heﬂ, Kristopher Born
 *
 */
public class CPAResult implements Iterable<CriticalPair> {

	/**
	 * List of the critical pairs.
	 */
	private List<CriticalPair> criticalPairs;

	/**
	 * Default constructor.
	 */
	public CPAResult() {
		criticalPairs = new ArrayList<CriticalPair>();
	}

	/**
	 * Adds a critical pair to this result set.
	 * 
	 * @param criticalPair
	 *            a critical pair which will be added to the result.
	 */
	public void addResult(CriticalPair criticalPair) {
		criticalPairs.add(criticalPair);
	}

	/**
	 * Returns an iterator over the critical pairs of the result set in proper
	 * sequence.
	 */
	public Iterator<CriticalPair> iterator() {
		return criticalPairs.iterator();
	}

	/**
	 * Returns the list of critical pairs.
	 * 
	 * @return The list of critical pairs.
	 */
	public List<CriticalPair> getCriticalPairs() {
		return criticalPairs;
	}

	/**
	 * Returns the list of critical pairs.
	 * 
	 * @return The list of critical pairs.
	 */
	public List<CriticalPair> getInitialCriticalPairs() {
		List<CriticalPair> result = new ArrayList();
		for (CriticalPair pair : getCriticalPairs()) {
//			printBoundaryNodes(pair);
			List<BoundaryNode> isolatedBoundaryNodes = new ArrayList<BoundaryNode>(pair.getBoundaryNodes());

			Set<Edge> criticalEdges = new HashSet<Edge>();
			for (CriticalElement el : pair.getCriticalElements()) {
				 if (el.elementInFirstRule instanceof Edge) {
					 criticalEdges.add((Edge) el.elementInFirstRule);
				 }
				 if (el.elementInSecondRule instanceof Edge) {
					 criticalEdges.add((Edge) el.elementInSecondRule);
				 }
			}

			for (BoundaryNode bn : pair.getBoundaryNodes()) {
				org.eclipse.emf.henshin.model.Node n = (org.eclipse.emf.henshin.model.Node) bn.elementInFirstRule;
				for (Edge e1 : n.getAllEdges()) {
					if (criticalEdges.contains(e1))
						isolatedBoundaryNodes.remove(bn);
				}
			}
			
			if (isolatedBoundaryNodes.isEmpty()) {
				result.add(pair);
				printCriticalElements(pair);
			}
		}
		System.out.println("Found "+result.size()+ " ICPs.");
		return result;
	}

	private void printBoundaryNodes(CriticalPair pair) {
		System.out.print("Boundary nodes (" + pair.getBoundaryNodes().size() + "): {");
		for (BoundaryNode br : pair.getBoundaryNodes()) {
			System.out.print(br.commonElementOfCriticalGraph + " ");
		}
		System.out.println("}");
	}

	private void printCriticalElements(CriticalPair pair) {
		System.out.print("Critical elements: {");
		for (CriticalElement cr : pair.getCriticalElements()) {
			System.out.print(cr.commonElementOfCriticalGraph + " ");
		}
		System.out.println("}");
	}

	public void setAppliedAnalysis(AppliedAnalysis appliedAnalysis) {
		for (CriticalPair criticalPair : criticalPairs) {
			criticalPair.setAppliedAnalysis(appliedAnalysis);
		}
	}

}
