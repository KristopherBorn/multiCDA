package org.eclipse.emf.henshin.multicda.cda.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.eclipse.swt.internal.win32.MINMAXINFO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReduceComputedInitialReasonsTest {

	final String PATH = "testData/refactoring/";
	final String henshinFileName = "refactorings.henshin";

	Rule decapsulateAttributeRule;
	Rule pullUpEncapsulatedAttributeRule;

	@Before
	public void setUp() throws Exception {
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		Module module = resourceSet.getModule(henshinFileName, false);

		for (Unit unit : module.getUnits()) {
			if (unit.getName().equals("decapsulateAttribute"))
				decapsulateAttributeRule = (Rule) unit;
			if (unit.getName().equals("pullUpEncapsulatedAttribute"))
				pullUpEncapsulatedAttributeRule = (Rule) unit;
		}
	}


	@Test
	public void compute_pullUp_decapsulate_ConflictReasonTest() {
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pullUpEncapsulatedAttributeRule, decapsulateAttributeRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		assertEquals(5, computedConflictAtoms.size());
		
		Set<ConflictAtom> methodCAs = new HashSet<ConflictAtom>();

		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(5, allMinimalConflictReasons.size());
		
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span conflictReason : allMinimalConflictReasons){
			minimalConflictReasons.add(new MinimalConflictReason(conflictReason));
		}
		
		Set<ConflictReason> computedInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(17, computedInitialReason.size());
		
		
		Set<ConflictReason> cleanedResults = new HashSet<ConflictReason>();
		Set<ConflictReason> filteredOutResults = new HashSet<ConflictReason>();
		int skippedCRs = 0;
		for(ConflictReason initialReason : computedInitialReason){
			Set<Mapping> mappingsInRule1 = initialReason.getMappingsInRule1();
			Set<Mapping> mappingsInRule2 = initialReason.getMappingsInRule2();
		}

		System.err.println("cleanedResults: "+cleanedResults.size());
		System.err.println("filteredOutResults: "+filteredOutResults.size());
		System.err.println("skippedCRs: "+skippedCRs);
		
		int oneOriginMCR = 0;
		int twoOriginMCR = 0;
		int threeOriginMCR = 0;
		int undefinedOriginMCR = 0;
		Set<ConflictReason> threeOriginMCR_CRs = new HashSet<ConflictReason>();
		for(ConflictReason initialReason : cleanedResults){
			if(initialReason.getOriginMCRs().size() == 1)
				oneOriginMCR++;
			if(initialReason.getOriginMCRs().size() == 2)
				twoOriginMCR++;
			if(initialReason.getOriginMCRs().size() == 3){
				threeOriginMCR++;
				threeOriginMCR_CRs.add(initialReason);
			}
		}
		System.out.println("oneOriginMCR: "+oneOriginMCR);
		System.out.println("twoOriginMCR: "+twoOriginMCR);
		System.out.println("threeOriginMCR: "+threeOriginMCR);
		for(ConflictReason aThreeMcrCr : threeOriginMCR_CRs){
			aThreeMcrCr.getOriginMCRs();
			System.out.println(aThreeMcrCr.getGraph().toString());
		}
	}

}


