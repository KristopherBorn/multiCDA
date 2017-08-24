package org.eclipse.emf.henshin.cpa.atomic.unitTest.jevTests;

import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.CpaCdaComparator;
import org.eclipse.emf.henshin.cpa.atomic.CpaCdaComparator.CompareResult;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.tester.AtomicTester;
import org.eclipse.emf.henshin.cpa.atomic.tester.CPATester;
import org.eclipse.emf.henshin.cpa.importer.AggHenshinCriticalPairTranslator;
import org.eclipse.emf.henshin.cpa.result.CriticalPair;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Hantel {

	private String path = "testData/jevsTests/attribute/hantel.henshin";
	private String delete = "delete";
	private String use = "use";

	private static AtomicTester aTester;
	private static CPATester eTester;

	@Test
	public void A() {
		aTester = new AtomicTester(path, delete, use);
		aTester.ready();
	}

	@Test
	public void B() {
		eTester = new CPATester(path, new String[] { delete }, new String[] { use });
//		Conditions _1 = new CriticalConditions(new Edge(3, 4));
//		assertTrue(_1 + " was not found", eTester.check(_1));
		eTester.ready();
	}

	@Test
	@Ignore
	public void C() {
		CPATester tester = new CPATester(path, false, new String[] { delete }, new String[] { use });
		tester.ready();

	}

	@AfterClass
	public static void after() {
		Set<InitialConflictReason> ir = aTester.getInitialReasons();
		Set<CriticalPair> cp = eTester.getInitialCriticalPairs();
		AtomicTester.printICR(ir);
		CPATester.printCP(cp);
	}
}
