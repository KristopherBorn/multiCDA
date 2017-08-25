package org.eclipse.emf.henshin.cpa.atomic.unitTest.jevTests;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.henshin.cpa.atomic.tester.AtomicTester;
import org.eclipse.emf.henshin.cpa.atomic.tester.CPATester;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.CP;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.CR;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.ICP;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.ICR;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.MCR;
//import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.MinimalConditions;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoopsTest {

	private String path = "testData/jevsTests/loop/Loops.henshin";
	private String loop = "executeLoop";
	private String loop2 = "executeLoop2";
	private String loop3 = "executeNonLoop";

	@Test

	public void A() {

		ICR _1 = new ICR(7);
		AtomicTester tester = new AtomicTester(path, loop3);
		assertTrue(_1 + " not correct", tester.check(_1));

	}

	@Test
	@Ignore
	public void B() {
		CP _1 = new CP(49);
		CPATester tester = new CPATester(path, loop3);
		assertTrue(_1 + " not correct", tester.check(_1));
		tester.ready();
	}
	
	@Test
	@Ignore
	public void B2() {
		ICP _1 = new ICP(7);
		CPATester tester = new CPATester(path, loop3);
		assertTrue(_1 + " not correct", tester.check(_1));
		tester.ready();
	}
	

	@Test
	@Ignore
	public void C() {
		CPATester tester = new CPATester(path, false, loop3);
		tester.ready();
	}
}
