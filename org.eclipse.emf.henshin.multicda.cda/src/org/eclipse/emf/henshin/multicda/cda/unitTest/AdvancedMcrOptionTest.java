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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AdvancedMcrOptionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		// path:
		// advancedMcrOptionTest
		fail("Not yet implemented");
		
//		
//		final String PATH = "testData/refactoring/";
//		final String henshinFileName = "refactorings.henshin";
//
//		Rule decapsulateAttributeRule;
//		Rule pullUpEncapsulatedAttributeRule;
//		
//		
//		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
//		Module module = resourceSet.getModule(henshinFileName, false);
//
//		for (Unit unit : module.getUnits()) {
//			if (unit.getName().equals("decapsulateAttribute"))
//				decapsulateAttributeRule = (Rule) unit;
//			if (unit.getName().equals("pullUpEncapsulatedAttribute"))
//				pullUpEncapsulatedAttributeRule = (Rule) unit;
//		}
//		
//		
//		AtomicCoreCPA atomicCoreCPA = new AtomicCoreCPA();
//
//		List<AtomicCoreCPA.Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(decapsulateAttributeRule,
//				pullUpEncapsulatedAttributeRule);
//
//		System.out.println("HALT");
//
//		assertEquals(5, conflictAtomCandidates.size());
//
//		for (AtomicCoreCPA.Span candidate : conflictAtomCandidates) {
//			EList<Node> nodesOfCandidate = candidate.getGraph().getNodes();
//			assertEquals(1, nodesOfCandidate.size());
//		}
//
//		Set<Span> reasons = new HashSet<>();//
//		for (Span candidate : conflictAtomCandidates) {
//			atomicCoreCPA.computeMinimalConflictReasons(decapsulateAttributeRule, pullUpEncapsulatedAttributeRule, candidate,
//					reasons);
//		}
//
//		assertEquals(2, reasons.size());
	}

}
