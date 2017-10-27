package org.eclipse.emf.henshin.multicda.cda.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.CpaByAGG;
import org.eclipse.emf.henshin.cpa.UnsupportedRuleException;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.cpa.result.Conflict;
import org.eclipse.emf.henshin.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReasonCreator;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PullbackTest {

	final String PATH = "testData/automataExample/";
	final String henshinFileName = "executingTransitions.henshin";

	Rule executeNonLoop;

	@Before
	public void setUp() throws Exception {
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		Module module = resourceSet.getModule(henshinFileName, false);
		
		executeNonLoop = (Rule) module.getUnit("executeNonLoop");
	}

	@Test
	public void conflictToCriticalPairTest() {
		List<Rule> rules = new LinkedList<Rule>();
		rules.add(executeNonLoop);
		CpaByAGG cpaByAGG = new CpaByAGG();
		CPAOptions cpaOptions = new CPAOptions();
		cpaOptions.setEssential(true);
		CPAResult essCPs = null;
		try {
			cpaByAGG.init(rules, cpaOptions);
			essCPs = cpaByAGG.runConflictAnalysis();
		} catch (UnsupportedRuleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<CriticalPair> criticalPairs = essCPs.getCriticalPairs();
		
		
		Assert.assertEquals(49, criticalPairs.size());
		// die 29 Ergebnisse sind die verbleibenden Ergebnisse der ursprünglich 49 Ergebnsise,
		// nach Abzug der Ergebnisse mit parallelen Kanten
		
		
		// bewusst 'List' als Datenstruktur um noch keine Ergebnisse durch 'equals()' zu reduzieren!
		List<ConflictReason> conflictReasonsBasedOnEssCpaResults = new LinkedList<ConflictReason>();
		
		for(CriticalPair cp : criticalPairs){
			ConflictReason cr = ConflictReasonCreator.createConflictReason((Conflict)cp);
			conflictReasonsBasedOnEssCpaResults.add(cr);
		}
		
		Assert.assertEquals(49, conflictReasonsBasedOnEssCpaResults.size());
		
		for(ConflictReason conflictReason : conflictReasonsBasedOnEssCpaResults){
			System.out.println(conflictReason.toShortString());
		}
		
		Set<ConflictReason> conflictReasonsBasedOnEssCpaResultsSet = new HashSet<ConflictReason>(conflictReasonsBasedOnEssCpaResults);
		
		Assert.assertEquals(49, conflictReasonsBasedOnEssCpaResultsSet.size());
		
		Set<ConflictReason> conflictReasonsByNewCDA = computeConflictReasons(executeNonLoop);
		
		boolean equalSets = conflictReasonsByNewCDA.equals(conflictReasonsBasedOnEssCpaResultsSet);
		Assert.assertEquals(true, equalSets);
	}
	
	
	// tested in "BoundaryNodesTest"
	private Set<ConflictReason> computeConflictReasons(Rule executeNonLoop) {
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(executeNonLoop,
				executeNonLoop);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		Assert.assertEquals(3, computedConflictAtoms.size());
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		//TODO: check that the two Reasons had been found AND that the three ConflictAtoms have three MCR (minimal conflict reasons)!
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(3, allMinimalConflictReasons.size());
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : allMinimalConflictReasons){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		
		Set<InitialReason> computedInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(7, computedInitialReason.size());
		//SUPER! 
		/* Die Anzahl entspricht zumindest der im Dokument
		 * "Von den 49 essential CPs sind 7 minimal CPs (Ergebnis 1, 23, 28, 33, 47, 48, 49)."
		 * 
		 */
		
		Set<ConflictReason> conflictReasonsDerivedFromInitialReason = new HashSet<ConflictReason>();
		Set<MinimalConflictReason> originMCRs = new HashSet<MinimalConflictReason>();
		for(InitialReason initialReason : computedInitialReason){
			System.err.println(initialReason.toShortString());
			originMCRs.addAll(initialReason.getOriginMCRs());
			Set<ConflictAtom> byInitialReasonCoveredEdgeConflictAtoms = initialReason.getCoveredEdgeConflictAtoms();
			Set<ConflictAtom> allEdgeConflictAtoms = atomicCoreCPA.extractEdgeConflictAtoms(computedConflictAtoms);
			allEdgeConflictAtoms.removeAll(byInitialReasonCoveredEdgeConflictAtoms);
			Set<ConflictAtom> byInitialReasonUncoveredConflictAtoms = allEdgeConflictAtoms;
			Set<ConflictReason> allDerivedConflictReasons = initialReason.getAllDerivedConflictReasons(byInitialReasonUncoveredConflictAtoms);
			for(ConflictReason conflictReason : allDerivedConflictReasons){
				System.out.println(conflictReason.toShortString());
			}
			conflictReasonsDerivedFromInitialReason.addAll(allDerivedConflictReasons);
		}
		for(ConflictReason conflictReason : conflictReasonsDerivedFromInitialReason){
			System.out.println(conflictReason.toShortString());
		}
		Assert.assertEquals(3, originMCRs.size());
		Assert.assertEquals(49, conflictReasonsDerivedFromInitialReason.size());
		
		Set conflictAtoms = new HashSet<>();
		for(MinimalConflictReason mcr : originMCRs){
			conflictAtoms.addAll(mcr.getOriginMCRs());
		}
		Assert.assertEquals(3, conflictAtoms.size());

		return conflictReasonsDerivedFromInitialReason;
	}
}