package org.eclipse.emf.henshin.cpa.atomic.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.CpaByAGG;
import org.eclipse.emf.henshin.cpa.UnsupportedRuleException;
import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.CpaCdaComparator;
import org.eclipse.emf.henshin.cpa.atomic.CpaCdaComparator.CompareResult;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		final String PATH = "testData/automataExample/";
		final String henshinFileName = "executingTransitions.henshin";

		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		Module module = resourceSet.getModule(henshinFileName, false);
		
		Rule executeNonLoop = (Rule) module.getUnit("executeNonLoop");
		
		// CPA -->>
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
		Set<CriticalPair> criticalPairs = new HashSet<CriticalPair>(essCPs.getCriticalPairs());
		Assert.assertEquals(29, criticalPairs.size());
		// <<-- CPA
		
		
		// CDA -->>
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(executeNonLoop, executeNonLoop);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		Assert.assertEquals(3, computedConflictAtoms.size());
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
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
		Assert.assertEquals(29, conflictReasonsDerivedFromInitialReason.size());
		// <<-- CDA
		
		
		
		CpaCdaComparator comparator = new CpaCdaComparator();
		CompareResult compare = comparator.compare(criticalPairs, conflictReasonsDerivedFromInitialReason);
		/*
		 * - [done] Abruf der nicht zugeordneten CPs (Ergebnis: Set)
		 * - [done] Abruf der nicht zugeordneten CRs (Ergebnis: Set)
		 * - [done] Abruf der zugeordneten CPs (Ergebnis: Set)
		 * - [done] Abruf der zugeordneten CRs (Ergebnis: Set)
		 */
//		Assert.assertEquals(0, compare.getUnassignedCPs().size());
//		Assert.assertEquals(0, compare.getUnassignedCRs().size());
		
		System.out.println("amount of cPsOfCR:");
		for(ConflictReason cr : conflictReasonsDerivedFromInitialReason){
			Set<CriticalPair> cPsOfCR = compare.getCPsOfCR(cr);
			if(cPsOfCR!=null){
				System.out.print(cPsOfCR.size()+", ");
			}else {
				System.out.println("why null?");
			}
//			Assert.assertEquals(1, cPsOfCR.size());
		}

		System.out.println("amount of cRsOfCP:");
		for(CriticalPair cp : criticalPairs){
			Set<ConflictReason> cRsOfCP = compare.getCRsOfCP(cp);//compare.getUnassignedCRs()
			if(cRsOfCP!=null){
				System.out.print(cRsOfCP.size()+", ");
			}else {
				System.out.println("why null?");
			}
//			Assert.assertEquals(1, cRsOfCP.size());
		}
		
	}

}
