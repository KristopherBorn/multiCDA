package org.eclipse.emf.henshin.cpa.atomic.unitTest.jevTests;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.henshin.cpa.atomic.tester.AtomicTester;
import org.eclipse.emf.henshin.cpa.atomic.tester.CPATester;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.CP;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Conditions;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Edge;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.ICR;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.MCR;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Node;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VererbungsTest {

	private String path = "testData/jevsTests/extendTest/extendRules.henshin";
	private String firstS = "deleteS";
	private String firstC = "deleteC";
	private String secondS = "useS";
	private String secondC = "useC";

	@Test
	public void ASCatomic() {
		Conditions _1 = new Conditions(new Node(2));
		AtomicTester tester = new AtomicTester(path, firstS, secondC);
		assertTrue("Minimal Conflict Reasons are not 1", tester.check(new MCR(1)));
		assertTrue("Initial Conflict Reasons are not 1", tester.check(new ICR(1)));
		assertTrue(_1 + " not found", tester.check(_1));
		tester.ready();
	}

	@Test
	public void ASCcpa() {

		CPATester tester = new CPATester(path, new String[] { firstS }, new String[] { secondC });
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();

		tester = new CPATester(path, new String[] { firstS }, new String[] { secondC }, true);
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();
	}

	@Test
	public void BSSatomic() {
		Conditions _1 = new Conditions(new Node(1));
		AtomicTester tester = new AtomicTester(path, firstS, secondS);
		assertTrue("Minimal Conflict Reasons are not 1", tester.check(new MCR(1)));
		assertTrue("Initial Conflict Reasons are not 1", tester.check(new ICR(1)));
		assertTrue(_1 + " not found", tester.check(_1));
		tester.ready();
	}

	@Test
	public void BSScpa() {

		CPATester tester = new CPATester(path, new String[] { firstS }, new String[] { secondS });
		tester.print();
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();

		tester = new CPATester(path, new String[] { firstS }, new String[] { secondS }, false);
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();
	}

	@Test
	public void CCCatomic() {
		Conditions _1 = new Conditions(new Node(3));
		AtomicTester tester = new AtomicTester(path, firstC, secondC);
		assertTrue("Minimal Conflict Reasons are not 1", tester.check(new MCR(1)));
		assertTrue("Initial Conflict Reasons are not 1", tester.check(new ICR(1)));
		assertTrue(_1 + " not found", tester.check(_1));
		tester.ready();
	}

	@Test
	public void CCCcpa() {

		CPATester tester = new CPATester(path, new String[] { firstC }, new String[] { secondC });
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();

		tester = new CPATester(path, new String[] { firstC }, new String[] { secondC }, false);
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();
	}

	@Test
	public void DCSatomic() {
		AtomicTester tester = new AtomicTester(path, firstC, secondS);
		assertTrue("Minimal Conflict Reasons are not 1", tester.check(new MCR(1)));
		assertTrue("Initial Conflict Reasons are not 1", tester.check(new ICR(1)));
		tester.ready();
	}

	@Test
	public void DCScpa() {
		CPATester tester = new CPATester(path, new String[] { firstC }, new String[] { secondS });
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();

		tester = new CPATester(path, new String[] { firstC }, new String[] { secondS }, false);
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();
	}

}
