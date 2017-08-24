package org.eclipse.emf.henshin.cpa.atomic.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
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


		/*
		 * TODO: alle drei Methoden
		 * conflict atoms? [!]
		 * conflict atom/part candidates?
		 * conflict reason? [!]
		 * 
		 * mit "Null" als übergabeparameter prüfen.
		 * Was könnten noch invalide Eingaben sein?
		 * Validierung der Regeln, dass es sich bei diesen um gültige Regeln handelt.
		 * 		ABER: was ist eine "gültige" Regel?
		 * BESSER: die Einschränkungen auf Regeln abprüfen: 
		 * 			- keine MultiRegeln
		 * 			- keine ACs
		 * 			- Attribute ????
		 * 
		 */
	
	
	// TODO: tests for unsupported rules (PAC,NAC, multiRule) with loaded rules -> set up unsupported rules	
	
	
	//TODO: the Exception checks need to be executed on the ConflictAnalysis constructor
	@Test
	public void computeConflictAtomsNotNullTest() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis();
		boolean illeagalArgumentExceptionThrownOnRule1 = false;
		boolean illeagalArgumentExceptionThrownOnRule2 = false;
		try {			
			atomicCoreCPA.computeConflictAtoms(null,
					pullUpEncapsulatedAttributeRule);
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrownOnRule1 = true;
		}
		try {			
			atomicCoreCPA.computeConflictAtoms(decapsulateAttributeRule,
					null);
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrownOnRule2 = true;
		}
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrownOnRule1);
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrownOnRule2);
	}	
	
	
	@Test
	public void computeConflictPartCandidatesNotNullTest() {		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(null,
				pullUpEncapsulatedAttributeRule);
		boolean illeagalArgumentExceptionThrownOnRule1 = false;
		boolean illeagalArgumentExceptionThrownOnRule2 = false;
		try {					
			atomicCoreCPA.computeAtomCandidates(null,
				pullUpEncapsulatedAttributeRule);
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrownOnRule1 = true;
		}
		try {	
			atomicCoreCPA.computeAtomCandidates(decapsulateAttributeRule,
					null);
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
		
		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(pullUpEncapsulatedAttributeRule,
				decapsulateAttributeRule);
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			try {		
				atomicCoreCPA.computeMinimalConflictReasons(null, decapsulateAttributeRule, candidate,
						reasons);			 
			} catch (IllegalArgumentException e) {
				illeagalArgumentExceptionThrownOnRule1 = true;
			}
			try {	

				atomicCoreCPA.computeMinimalConflictReasons(pullUpEncapsulatedAttributeRule, null, candidate,
						reasons);
			} catch (IllegalArgumentException e) {
				illeagalArgumentExceptionThrownOnRule2 = true;
			}
		}
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrownOnRule1);
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrownOnRule2);
	}
	
}
