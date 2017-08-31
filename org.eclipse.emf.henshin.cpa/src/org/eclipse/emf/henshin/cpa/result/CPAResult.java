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
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.henshin.cpa.result.CriticalPair.AppliedAnalysis;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Action.Type;

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
		List<CriticalPair> result = new ArrayList<>();
		for (CriticalPair pair : getCriticalPairs()) {
			// Only retain those CPs without isolated boundary nodes.
			// Since the set of isolated boundary nodes is hard to compute
			// directly
			// based on the available data structures, we just compute their
			// number instead.
			Set<Node> boundaryNodes = new HashSet<Node>();
			for (CriticalElement el : pair.getCriticalElements()) {
				if (el.elementInFirstRule instanceof Edge) {
					Edge edge = (Edge) el.elementInFirstRule;
					if (edge.getSource().getActionNode().getAction().getType() == Type.PRESERVE)
						boundaryNodes.add(edge.getSource());
					if (edge.getTarget().getActionNode().getAction().getType() == Type.PRESERVE)
						boundaryNodes.add(edge.getTarget());
				}
			}
			int boundaryNodeCount = boundaryNodes.size();

			EPackage overlap = (EPackage) pair.getMinimalModel();
			int overlapSize = overlap.getEClassifiers().size();
			int graph1Size = pair.getCriticalElements().get(0).elementInFirstRule.getGraph().getNodes().size();
			int graph2Size = pair.getCriticalElements().get(0).elementInSecondRule.getGraph().getNodes().size();
			Set<CriticalElement> criticalNodes = pair.getCriticalElements().stream()
					.filter(c -> c.elementInFirstRule instanceof Node).collect(Collectors.toSet());
			int criticalNodeCount = criticalNodes.size();

			int isolatedBoundaryNodeCount = graph1Size + graph2Size - overlapSize - criticalNodeCount
					- boundaryNodeCount;
			if (isolatedBoundaryNodeCount == 0)
				result.add(pair);
		}
		return result;
	}

	//
	// /**
	// * Returns the list of critical pairs.
	// *
	// * @return The list of critical pairs.
	// */
	// public List<CriticalPair> getInitialCriticalPairs() {
	// List<CriticalPair> result = new ArrayList<>();
	// for (CriticalPair pair : getCriticalPairs()) {
	// List<Node> isolatedBoundaryNodes = new ArrayList<Node>();
	//
	// Set<Edge> criticalEdges = new HashSet<Edge>();
	// Set<Node> boundaryNodes = new HashSet<Node>();
	// for (CriticalElement el : pair.getCriticalElements()) {
	// if (el.elementInFirstRule instanceof Edge) {
	// Edge edge = (Edge) el.elementInFirstRule;
	// criticalEdges.add(edge);
	// if (edge.getSource().getActionNode().getAction().getType() ==
	// Type.PRESERVE)
	// boundaryNodes.add(edge.getSource());
	// if (edge.getTarget().getActionNode().getAction().getType() ==
	// Type.PRESERVE)
	// boundaryNodes.add(edge.getTarget());
	//
	// }
	// if (el.elementInSecondRule instanceof Edge) {
	// criticalEdges.add((Edge) el.elementInSecondRule);
	// }
	// }
	// for (Node bn : boundaryNodes) {
	// boolean isolated = true;
	// for (Edge e1 : bn.getAllEdges()) {
	// if (criticalEdges.contains(e1))
	// isolated = false;
	// }
	// if (isolated)
	// isolatedBoundaryNodes.add(bn);
	// }
	//
	// if (isolatedBoundaryNodes.isEmpty()) {
	// result.add(pair);
	//// printCriticalElements(pair);
	// }
	// }
	// return result;
	// }

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
