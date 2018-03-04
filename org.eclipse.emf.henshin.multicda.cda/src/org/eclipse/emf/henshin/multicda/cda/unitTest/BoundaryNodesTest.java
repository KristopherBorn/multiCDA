package org.eclipse.emf.henshin.multicda.cda.unitTest;

//import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.EssentialConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.multicda.cpa.CPAOptions;
import org.eclipse.emf.henshin.multicda.cpa.CpaByAGG;
import org.eclipse.emf.henshin.multicda.cpa.UnsupportedRuleException;
import org.eclipse.emf.henshin.multicda.cpa.result.CPAResult;
import org.eclipse.emf.henshin.multicda.cpa.result.CriticalPair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BoundaryNodesTest {

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
	public void compute_executeNonLoop_MCR_Test() {
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(executeNonLoop, executeNonLoop);
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
		
		Set<ConflictReason> computedInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(7, computedInitialReason.size());
		//SUPER! 
		/* Die Anzahl entspricht zumindest der im Dokument
		 * "Von den 49 essential CPs sind 7 minimal CPs (Ergebnis 1, 23, 28, 33, 47, 48, 49)."
		 * 
		 */
		
		Set<EssentialConflictReason> conflictReasonsDerivedFromInitialReason = new HashSet<EssentialConflictReason>();
		Set<MinimalConflictReason> originMCRs = new HashSet<MinimalConflictReason>();
		for(ConflictReason initialReason : computedInitialReason){
			System.err.println(initialReason.toShortString());
			originMCRs.addAll(initialReason.getOriginMCRs());
			Set<ConflictAtom> byInitialReasonCoveredEdgeConflictAtoms = initialReason.getCoveredEdgeConflictAtoms();
			Set<ConflictAtom> allEdgeConflictAtoms = atomicCoreCPA.extractEdgeConflictAtoms(computedConflictAtoms);
			allEdgeConflictAtoms.removeAll(byInitialReasonCoveredEdgeConflictAtoms);
			Set<ConflictAtom> byInitialReasonUncoveredConflictAtoms = allEdgeConflictAtoms;
			Set<EssentialConflictReason> allDerivedConflictReasons = initialReason.getAllDerivedConflictReasons(byInitialReasonUncoveredConflictAtoms);
			for(EssentialConflictReason conflictReason : allDerivedConflictReasons){
				System.out.println(conflictReason.toShortString());
			}
			conflictReasonsDerivedFromInitialReason.addAll(allDerivedConflictReasons);
		}
		for(EssentialConflictReason conflictReason : conflictReasonsDerivedFromInitialReason){
			System.out.println(conflictReason.toShortString());
		}
		Assert.assertEquals(3, originMCRs.size());
		Assert.assertEquals(49, conflictReasonsDerivedFromInitialReason.size());
		
		Set conflictAtoms = new HashSet<>();
		for(MinimalConflictReason mcr : originMCRs){
			conflictAtoms.addAll(mcr.getOriginMCRs());
		}
		Assert.assertEquals(3, conflictAtoms.size());

	}

	@Test
	public void essCpa_Test() {
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
		List<CriticalPair> criticalPairs = essCPs.getEssentialCriticalPairs();
		
		Assert.assertEquals(49, criticalPairs.size());
		// die 29 Ergebnisse sind die verbleibenden Ergebnisse der ursprünglich 49 Ergebnsise,
		// nach Abzug der Ergebnisse mit parallelen Kanten
	}
	

}
