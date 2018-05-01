package org.eclipse.emf.henshin.multicda.cda.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.computation.AtomCandidateComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.MinimalReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Survey2webshopConflictTest {

	final String PATH = "testData/survey2webshopConflict/";
	final String henshinFileName = "webshop.henshin";

	Rule returnOneUnpaidItemRule;
	Rule findOrderWithThreeOrderItems_incompleteRule;

	@Before
	public void setUp() throws Exception {
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		Module module = resourceSet.getModule(henshinFileName, false);

		returnOneUnpaidItemRule = (Rule) module.getUnit("returnOneUnpaidItem");
		findOrderWithThreeOrderItems_incompleteRule = (Rule) module.getUnit("findOrderWithThreeOrderItems_incomplete");
		// for (Unit unit : module.getUnits()) {
		// if (unit.getName().equals("decapsulateAttribute"))
		// decapsulateAttributeRule = (Rule) unit;
		// if (unit.getName().equals("pullUpEncapsulatedAttribute"))
		// pullUpEncapsulatedAttributeRule = (Rule) unit;
		// }
	}

	@Test
	public void computeConflictAtomsTest() {
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(returnOneUnpaidItemRule,
				findOrderWithThreeOrderItems_incompleteRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		for (ConflictAtom conflAtom : computedConflictAtoms) {
			System.out.println(conflAtom.toString());
			System.out.println(conflAtom.toShortString());
		}

		// 6 conflict atoms
		// Je 3 CA f�r die �berlappung der l�schenden 'owns' Kante zwischen
		// 'Customer' und dem jeweiligen 'Good'
		// Je 3 CA f�r die �berlappung der 'OrderItem'
		assertEquals(6, computedConflictAtoms.size());

		// Ergebnisse m�glich.
	}

	// MCR Test!
	@Test
	public void computeMinimalConflictReasonTest() {

		AtomCandidateComputation candComp = new AtomCandidateComputation(returnOneUnpaidItemRule,
				findOrderWithThreeOrderItems_incompleteRule);

		List<Span> conflictAtomCandidates = candComp.computeAtomCandidates();

		assertEquals(6, conflictAtomCandidates.size());

		;
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			new MinimalReasonComputation(returnOneUnpaidItemRule, findOrderWithThreeOrderItems_incompleteRule)
					.computeMinimalConflictReasons(candidate, reasons);
		}

		// aus den 6 CA gehen entsprecehnde 6 MCRs hervor
		assertEquals(6, reasons.size());

		for (Span mcr : reasons) {
			System.out.println(mcr.toString());
		}
		for (Span mcr : reasons) {
			System.out.println(mcr.toShortString());
		}
	}
}
