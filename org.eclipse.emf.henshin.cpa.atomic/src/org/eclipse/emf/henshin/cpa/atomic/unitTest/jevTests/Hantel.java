package org.eclipse.emf.henshin.cpa.atomic.unitTest.jevTests;

import static org.junit.Assert.assertTrue;
import org.eclipse.emf.henshin.cpa.atomic.tester.AtomicTester;
import org.eclipse.emf.henshin.cpa.atomic.tester.CPATester;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Conditions;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.CriticalConditions;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Edge;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Hantel {

	private String path = "testData/jevsTests/attribute/hantel.henshin";
	private String delete = "delete";
	private String use = "use";

	@Test
	public void A() {
		AtomicTester tester = new AtomicTester(path, delete, use);
		tester.ready();
	}

	@Test
	public void B() {
		CPATester tester = new CPATester(path, new String[] { delete }, new String[] { use });
//		Conditions _1 = new CriticalConditions(new Edge(3, 4));
//		assertTrue(_1 + " was not found", tester.check(_1));
		tester.ready();
	}

	@Test
	public void C() {
		CPATester tester = new CPATester(path, false, new String[] { delete }, new String[] { use });
		tester.ready();
		
	}
	
}
