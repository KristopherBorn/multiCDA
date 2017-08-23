package org.eclipse.emf.henshin.cpa.atomic.unitTest.jevTests;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.poi.hssf.dev.ReSave;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.CpaByAGG;
import org.eclipse.emf.henshin.cpa.UnsupportedRuleException;
import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FirstTest {

	private final String PATH = "testData/loop/";
	private final String henshinFileName = "Loops.henshin";

	private Rule execute;

	private CpaByAGG cpa;
	private ConflictAnalysis ccpa;
	private List<Rule> rules1;
	private List<Rule> rules2;
	private CPAOptions options;

	@Before
	public void setUp() throws Exception {
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		Module module = resourceSet.getModule(henshinFileName, false);

		cpa = new CpaByAGG();
		ccpa = new ConflictAnalysis();

		execute = (Rule) module.getUnit("executeNonLoop");

		rules1 = new LinkedList<Rule>();
		rules1.add(execute);
		rules2 = new LinkedList<Rule>();
		rules2.add(execute);
		options = new CPAOptions();
		options.setEssential(true);
	}

	@Test
	public void test() {

		List<ConflictAtom> ca = ccpa.computeConflictAtoms(execute, execute);
		Set<MinimalConflictReason> mcr = ccpa.getMinimalConflictReasons();
		
		List<Span> candidates = ccpa.getCandidates();
		
		//Alle 3 Schleifen liefern das gleiche Ergebnis
		for(Span span : candidates) {
			System.out.println(span);
		}
		System.out.println();
		for (ConflictAtom s : ca) {
			System.out.println(s); 
		}
		System.out.println();
		for (Span candidate : mcr) {
			System.out.println(candidate);
		}
		
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span conflictReason : mcr){
			minimalConflictReasons.add(new MinimalConflictReason(conflictReason));
		}
		Assert.assertEquals(3, minimalConflictReasons.size());
		
		// detailed test. checks weather the three MCRs are based on the correct elements of the rules
		boolean mcr52Found = false;
		boolean mcr89Found = false;
		boolean mcr910Found = false;
		
		for(MinimalConflictReason minCR : minimalConflictReasons){
			Graph mcrGraph = minCR.getGraph();
			EList<Node> nodesOfMcrGraph = mcrGraph.getNodes();
			if(nodesOfMcrGraph.size() == 2){
				Set<String> nodeTypeNames = extractNodeTypeNames(mcrGraph);
				if(nodeTypeNames.contains("Cursor") && nodeTypeNames.contains("State")){
					mcr52Found = true;
				}
				if(nodeTypeNames.contains("Queue") && nodeTypeNames.contains("Element")){
					mcr89Found = true;
				}
				if(nodeTypeNames.contains("Element") && nodeTypeNames.size()==1){
					mcr910Found = true;
				}
			}			
		}

		assertTrue(mcr52Found);
		assertTrue(mcr89Found);
		assertTrue(mcr910Found);
		
		Set<InitialConflictReason> computedInitialReason = ccpa.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(7, computedInitialReason.size());
		System.err.println("conflictReasons: ");
		for(InitialConflictReason initialReason : computedInitialReason){
//			conflictReason.getOriginMCRs()
			System.out.println(initialReason.toShortString());
			System.out.println(initialReason.toString());
		}	
		//TODO: the seven InitialReasons should be checked to be correct regarding the contained Elements.
		
		
		System.out.println();
//		try {
//			cpa.init(rules1, rules2, options);
//			cpa.runConflictAnalysis();
//		} catch (UnsupportedRuleException e) {
//			e.printStackTrace();
//		}

	}

	/**
	 * returns the name of the types of nodes present in the graph.
	 * consider that there will be no duplicates so that multiple nodes of the same type are just represented with their type ones.
	 * 
	 * @param mcrGraph a Graph
	 * @return the name of the types of nodes present in the graph 
	 */
	private Set<String> extractNodeTypeNames(Graph mcrGraph) {
		Set<String> typeNames = new HashSet<String>();
		for(Node node : mcrGraph.getNodes()){
			typeNames.add(node.getType().getName());
		}
		return typeNames;
	}

}
