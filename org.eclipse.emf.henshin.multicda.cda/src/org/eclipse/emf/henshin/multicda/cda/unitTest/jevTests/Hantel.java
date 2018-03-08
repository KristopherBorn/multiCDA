package org.eclipse.emf.henshin.multicda.cda.unitTest.jevTests;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.tester.CDATester;
import org.eclipse.emf.henshin.multicda.cda.tester.CPATester;
import org.eclipse.emf.henshin.multicda.cda.tester.Tester.Options;
import org.eclipse.emf.henshin.multicda.cpa.result.CriticalPair;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Hantel {
	//therealsatera

	private String path = "testData/jevsTests/attribute/hantel.henshin";
	private String delete = "delete";
	private String use = "use";

	private static CDATester aTester;
	private static CPATester eTester;

	@Test
	public void A() {
		aTester = new CDATester(path, delete, use);
		aTester.ready();
	}

	@Test
	public void B() {
		eTester = new CPATester(path, new String[] { delete }, new String[] { use }, new Options().add(Options.ESSENTIAL));
//		Conditions _1 = new CriticalConditions(new Edge(3, 4));
//		assertTrue(_1 + " was not found", eTester.check(_1));
		eTester.ready();
	}

	@Test
//	@Ignore
	public void C() {
		CPATester tester = new CPATester(path, new String[] { delete }, new String[] { use });
		tester.ready();

	}

	@AfterClass
	public static void after() {
		Set<Span> ir = aTester.getConflictReasons();
		List<CriticalPair> cp = eTester.getCriticalPairs();
		System.out.println("\n");
		CDATester.print(ir);
		CPATester.printCP(cp);
	}
}
