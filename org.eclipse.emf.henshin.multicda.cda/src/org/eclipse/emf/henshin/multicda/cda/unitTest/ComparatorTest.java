package org.eclipse.emf.henshin.multicda.cda.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.CpaCdaComparator;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.CpaCdaComparator.CompareResult;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.EssentialConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.multicda.cpa.CDAOptions;
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
		CDAOptions cpaOptions = new CDAOptions();
		cpaOptions.setEssential(true);
		CPAResult essCPs = null;
		try {
			cpaByAGG.init(rules, cpaOptions);
			essCPs = cpaByAGG.runConflictAnalysis();
		} catch (UnsupportedRuleException e) {
			e.printStackTrace();
		}
		Set<CriticalPair> criticalPairs = new HashSet<CriticalPair>(essCPs.getEssentialCriticalPairs());
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
		
		Set<ConflictReason> computedInitialReason = atomicCoreCPA.computeConflictReasons(minimalConflictReasons);
		Assert.assertEquals(7, computedInitialReason.size());
		
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
		for(EssentialConflictReason cr : conflictReasonsDerivedFromInitialReason){
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
			Set<EssentialConflictReason> cRsOfCP = compare.getCRsOfCP(cp);//compare.getUnassignedCRs()
			if(cRsOfCP!=null){
				System.out.print(cRsOfCP.size()+", ");
			}else {
				System.out.println("why null?");
			}
//			Assert.assertEquals(1, cRsOfCP.size());
		}
		
	}

}
