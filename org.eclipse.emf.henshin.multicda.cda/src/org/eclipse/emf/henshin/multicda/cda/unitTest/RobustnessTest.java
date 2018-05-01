package org.eclipse.emf.henshin.multicda.cda.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.computation.AtomCandidateComputation;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.junit.Before;
import org.junit.Test;

public class RobustnessTest {


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
	public void computeConflictAtomsNotNullTest() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(null, pullUpEncapsulatedAttributeRule);
		boolean illeagalArgumentExceptionThrownOnRule1 = false;
		boolean illeagalArgumentExceptionThrownOnRule2 = false;
		try {			
			atomicCoreCPA.computeConflictAtoms();
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrownOnRule1 = true;
		}
		try {			
			atomicCoreCPA.computeConflictAtoms();
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrownOnRule2 = true;
		}
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrownOnRule1);
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrownOnRule2);
	}	
	
	
	@Test
	public void computeConflictPartCandidatesNotNullTest() {		
		AtomCandidateComputation candComp = new AtomCandidateComputation(null,
				pullUpEncapsulatedAttributeRule);
		boolean illeagalArgumentExceptionThrownOnRule1 = false;
		boolean illeagalArgumentExceptionThrownOnRule2 = false;
		try {					
			candComp.computeAtomCandidates();
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrownOnRule1 = true;
		}
		try {	
			candComp.computeAtomCandidates();
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrownOnRule2 = true;
		}
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrownOnRule1);
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrownOnRule2);
	}
	

	@Test
	public void computeMinimalReasonNotNullTest() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pullUpEncapsulatedAttributeRule,
				decapsulateAttributeRule);
		boolean illeagalArgumentExceptionThrownOnRule1 = false;
		boolean illeagalArgumentExceptionThrownOnRule2 = false;
		
		
		AtomCandidateComputation candComp = new AtomCandidateComputation(pullUpEncapsulatedAttributeRule,
				decapsulateAttributeRule);
		List<Span> conflictAtomCandidates = candComp.computeAtomCandidates();
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			try {		
				atomicCoreCPA.computeMinimalConflictReasons(candidate,
						reasons);			 
			} catch (IllegalArgumentException e) {
				illeagalArgumentExceptionThrownOnRule1 = true;
			}
			try {	

				atomicCoreCPA.computeMinimalConflictReasons(candidate,
						reasons);
			} catch (IllegalArgumentException e) {
				illeagalArgumentExceptionThrownOnRule2 = true;
			}
		}
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrownOnRule1);
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrownOnRule2);
	}
	
}
