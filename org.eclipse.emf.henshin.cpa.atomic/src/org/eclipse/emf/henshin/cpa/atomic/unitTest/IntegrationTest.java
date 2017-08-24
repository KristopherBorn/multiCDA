package org.eclipse.emf.henshin.cpa.atomic.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Before;
import org.junit.Test;

public class IntegrationTest {

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

	// erwartet: 2,13:Method , 3,14:Method und 5,15:Parameter - 3 Stück
	@Test
	public void conflictAtoms_decapsulateAttr_pullUpEncAttr_Test() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule,
				pullUpEncapsulatedAttributeRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
	assertEquals(3, computedConflictAtoms.size());
	}
	
	// erwartet: 2,2:Method , 3,3:Method und 5,5:Parameter - 3 Stück
	@Test
	public void conflictAtoms_decapsulateAttr_decapsulateAttr() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule,
				decapsulateAttributeRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
	assertEquals(3, computedConflictAtoms.size());
	}
	
	//erwartet: 1,11->4,12:variables , 1,11->2,13:methods , 1,11->3,13:methods , 1,11->2,14:methods , 1,11->3,14:methods
	@Test
	public void conflictAtoms_pullUpEncAttr_decapsulateAttr() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pullUpEncapsulatedAttributeRule,
				decapsulateAttributeRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			System.out.println(conflictAtom.toShortString());
		}
	assertEquals(5, computedConflictAtoms.size());
	}
	
	//erwartet: 11,11->12,12:variables , 11,11->13,13:methods , 11,11->13,14:methods , 11,11->14,14:methods , 11,11->14,13:methods
	@Test
	public void conflictAtoms_pullUpEncAttr_pullUpEncAttr() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pullUpEncapsulatedAttributeRule,
				pullUpEncapsulatedAttributeRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			System.out.println(conflictAtom.toShortString());
		}
	assertEquals(5, computedConflictAtoms.size());
	}
	
	
	
	// erwartet: 2,13:Method , 2,14:Method , 3,13:Method, 3,14:Method und 5,15:Parameter - 5 Stück
	@Test
	public void conflictPartCandidates_decapsulateAttr_pullUpEncAttr_Test() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule,
				pullUpEncapsulatedAttributeRule);
		List<Span> computedConflictPartCandidates = atomicCoreCPA.computeAtomCandidates(decapsulateAttributeRule,
			pullUpEncapsulatedAttributeRule);
	assertEquals(5, computedConflictPartCandidates.size());
	}
	
	// erwartet: 2,2:Method , 2,3:Method , 3,2:Method , 3,3:Method und 5,5:Parameter - 5 Stück
	@Test
	public void conflictPartCandidates_decapsulateAttr_decapsulateAttr() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule,
				decapsulateAttributeRule);
		List<Span> computedConflictPartCandidates = atomicCoreCPA.computeAtomCandidates(decapsulateAttributeRule,
				decapsulateAttributeRule);
	assertEquals(5, computedConflictPartCandidates.size());
	}
	
	//erwartet: 1,11->4,12:variables , 1,11->2,13:methods , 1,11->3,13:methods , 1,11->2,14:methods , 1,11->3,14:methods - 5 Stück
	@Test
	public void conflictPartCandidates_pullUpEncAttr_decapsulateAttr() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pullUpEncapsulatedAttributeRule,
				decapsulateAttributeRule);
		List<Span> computedConflictPartCandidates = atomicCoreCPA.computeAtomCandidates(pullUpEncapsulatedAttributeRule,
				decapsulateAttributeRule);
	assertEquals(5, computedConflictPartCandidates.size());
	}
	
	//erwartet: 11,11->12,12:variables , 11,11->13,13:methods , 11,11->13,14:methods , 11,11->14,14:methods , 11,11->14,13:methods - 5 Stück
	@Test
	public void conflictPartCandidates_pullUpEncAttr_pullUpEncAttr() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pullUpEncapsulatedAttributeRule,
				pullUpEncapsulatedAttributeRule);
		List<Span> computedConflictPartCandidates = atomicCoreCPA.computeAtomCandidates(pullUpEncapsulatedAttributeRule,
				pullUpEncapsulatedAttributeRule);
	assertEquals(5, computedConflictPartCandidates.size());
	}
	
	//erwartet: 2 minimal reasons
	// 1.: [1,11->2,13:methods, 2,13->6,16:type, 2,13:Method]
	// 2.: [1,11->3,14:methods, 3,14->5,15:parameters, 5,15->6,16:type]
	@Test
	public void conflictMinReason_decapsulateAttr_pullUpEncAttr_Test() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule,
				pullUpEncapsulatedAttributeRule);
		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(decapsulateAttributeRule,
				pullUpEncapsulatedAttributeRule);
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(decapsulateAttributeRule, pullUpEncapsulatedAttributeRule, candidate,
					reasons);
		}
		assertEquals(2, reasons.size());
	}
	
	//erwartet: 2 minimal reasons
	// 1.: [1,1->2,2:methods, 2,2->6,6:type]
	// 2.: [1,1->3,3:methods, 3,3->5,5:parameters, 5,5->6,6:type]
	@Test
	public void conflictMinReason_decapsulateAttr_decapsulateAttr() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule,
				decapsulateAttributeRule);
		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(decapsulateAttributeRule,
				decapsulateAttributeRule);
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(decapsulateAttributeRule, decapsulateAttributeRule, candidate,
					reasons);
		}
		assertEquals(2, reasons.size());
	}	
	
	//erwartet: 5 minimal reasons
	// 1.: [11,1->12,4:variables]
	// 2.: [11,1->13,2:methods]
	// 3.: [11,1->13,3:methods]
	// 4.: [11,1->14,2:methods]
	// 5.: [11,1->14,3:methods]
	@Test
	public void conflictMinReason_pullUpEncAttr_decapsulateAttr() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pullUpEncapsulatedAttributeRule,
				decapsulateAttributeRule);
		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(pullUpEncapsulatedAttributeRule,
				decapsulateAttributeRule);
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(pullUpEncapsulatedAttributeRule, decapsulateAttributeRule, candidate,
					reasons);
		}
		assertEquals(5, reasons.size());
	}
	
	//erwartet: 5 minimal reasons
	// 1.: [11,11->12,12:variables]
	// 2.: [11,11->13,13:methods]
	// 3.: [11,11->13,14:methods]
	// 4.: [11,11->14,13:methods]
	// 5.: [11,11->14,14:methods]
	@Test
	public void conflictMinReason_pullUpEncAttr_pullUpEncAttr() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pullUpEncapsulatedAttributeRule,
				pullUpEncapsulatedAttributeRule);
		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(pullUpEncapsulatedAttributeRule,
				pullUpEncapsulatedAttributeRule);
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(pullUpEncapsulatedAttributeRule, pullUpEncapsulatedAttributeRule, candidate,
					reasons);
		}
		assertEquals(5, reasons.size());
	}

//	TODO: rename-besseren Namen!
//
	
	/* TODO: was sind die "Outputs" der Atomic CPA?
	 * conflict atoms? [!] DONE!
	 * 		2,13:Method , 3,14:Method und 5,15:Parameter
	 * 			grob getestet in ComputeConflictAtomsTest
	 * conflict atom/part candidates? DONE!
	 * 		nur :Method und :Parameter, oder?
	 * 			sehr grob getestet in ComputeCandidatesTest
	 * conflict reason? [!] DONE!
	 * 		2 für "decapsulate"->"pullUp"
	 * 			getestet in ComputeMinReasonTest
	 */
	
}
