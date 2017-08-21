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
public class KombinationsTest {

	private String path = "testData/refactoring/refactorings.henshin";

	@Test
	public void ADeDeAtomic() {
		System.out.println("\n\t\t1: Decapsulate --> Decapsulate\n\t\t\tAtomic");

		Conditions _1 = new Conditions(new Edge(5, 6), new Node(2), new Edge(1, 3), new Edge(2, 6), new Edge(3, 5),
				new Node(3), new Node(5), new Edge(1, 2));
		Conditions _2 = new Conditions(new Edge(2, 6), new Node(2), new Edge(1, 2));
		Conditions _3 = new Conditions(new Edge(5, 6), new Node(3), new Node(5), new Edge(1, 3), new Edge(3, 5));

		AtomicTester tester = new AtomicTester(path, "decapsulateAttribute");
		assertTrue("Minimal Conflict Reasons are not 2", tester.check(new MCR(2)));
		assertTrue("Initial Conflict Reasons are not 3", tester.check(new ICR(3)));
		assertTrue(_1 + " not found", tester.check(_1));
		assertTrue(_2 + " not found", tester.check(_2));
		assertTrue(_3 + " not found", tester.check(_3));
		tester.ready();
	}

	@Test
	public void ADeDeCpa() {

		System.out.println("\t\t\tCPA Essential");
		CPATester tester = new CPATester(path, "decapsulateAttribute");
		tester.print();
		assertTrue("Critical Pairs are not 3", tester.check(new CP(3)));
		tester.ready();

		System.out.println("\t\t\tCPA");
		tester = new CPATester(path, false, "decapsulateAttribute");
		tester.print();
		assertTrue("Critical Pairs are not 5", tester.check(new CP(5)));
		tester.ready();
	}

	@Test
	public void BDePuAtomic() {
		System.out.println("\n\t\t2: Decapsulate --> PullUp\n\t\t\tAtomic");

		Conditions _1 = new Conditions(new Edge(5, 6), new Node(2), new Edge(1, 3), new Edge(2, 6), new Edge(3, 5),
				new Node(3), new Node(5), new Edge(1, 2));
		Conditions _2 = new Conditions(new Edge(2, 6), new Node(2), new Edge(1, 2));
		Conditions _3 = new Conditions(new Edge(5, 6), new Node(3), new Node(5), new Edge(1, 3), new Edge(3, 5));

		AtomicTester tester = new AtomicTester(path, "decapsulateAttribute", "pullUpEncapsulatedAttribute");
		tester.print();
		assertTrue("Minimal Conflict Reasons are not 2", tester.check(new MCR(2)));
		assertTrue("Initial Conflict Reasons are not 3", tester.check(new ICR(3)));
		assertTrue(_1 + " not found", tester.check(_1));
		assertTrue(_2 + " not found", tester.check(_2));
		assertTrue(_3 + " not found", tester.check(_3));
		tester.ready();
	}

	@Test
	public void BDePuCpa() {

		System.out.println("\t\t\tCPA Essential");
		CPATester tester = new CPATester(path, new String[] { "decapsulateAttribute" },
				new String[] { "pullUpEncapsulatedAttribute" });
		tester.print();
		assertTrue("Critical Pairs are not 3", tester.check(new CP(3)));
		tester.ready();

		System.out.println("\t\t\tCPA");
		tester = new CPATester(path, false, new String[] { "decapsulateAttribute" },
				new String[] { "pullUpEncapsulatedAttribute" });
		tester.print();
		assertTrue("Critical Pairs are not 6", tester.check(new CP(6)));
		tester.ready();
	}

	@Test
	public void CPuPuAtomic() {
		System.out.println("\n\t\t3: PullUp --> PullUp\n\t\t\tAtomic");

		Conditions _1 = new Conditions(new Edge(11, 12));
		Conditions _2 = new Conditions(new Edge(11, 13));
		Conditions _3 = new Conditions(new Edge(11, 14));
		Conditions _4 = new Conditions(new Edge(11, 12), new Edge(11, 13));
		Conditions _5 = new Conditions(new Edge(11, 12), new Edge(11, 14));
		Conditions _6 = new Conditions(new Edge(11, 13), new Edge(11, 14));
		Conditions _7 = new Conditions(new Edge(11, 12), new Edge(11, 13), new Edge(11, 14));

		AtomicTester tester = new AtomicTester(path, "pullUpEncapsulatedAttribute");
		tester.print();
		assertTrue("Minimal Conflict Reasons are not 5", tester.check(new MCR(5)));
		assertTrue("Initial Conflict Reasons are not 7", tester.check(new ICR(13))); // 13
		assertTrue(_1 + " not found", tester.check(_1));
		assertTrue(_2 + " not found", tester.check(_2));
		assertTrue(_3 + " not found", tester.check(_3));
		assertTrue(_4 + " not found", tester.check(_4));
		assertTrue(_5 + " not found", tester.check(_5));
		assertTrue(_6 + " not found", tester.check(_6));
		assertTrue(_7 + " not found", tester.check(_7));
		tester.ready();
	}

	@Test
	public void CPuPuCpa() {

		System.out.println("\t\t\tCPA Essential");
		CPATester tester = new CPATester(path, "pullUpEncapsulatedAttribute");
		tester.print();
		assertTrue("Critical Pairs are not 13", tester.check(new CP(13)));
		tester.ready();

		System.out.println("\t\t\tCPA");
		tester = new CPATester(path, false, "pullUpEncapsulatedAttribute");
		tester.print();
		assertTrue("Critical Pairs are not 5", tester.check(new CP(5)));
		tester.ready();
	}

	@Test
	public void DPuDeAtomic() {
		System.out.println("\n\t\t4: PullUp --> Decapsulate\n\t\t\tAtomic");

		Conditions _1 = new Conditions(new Edge(11, 12));
		Conditions _2 = new Conditions(new Edge(11, 13));
		Conditions _3 = new Conditions(new Edge(11, 14));
		Conditions _4 = new Conditions(new Edge(11, 12), new Edge(11, 13));
		Conditions _5 = new Conditions(new Edge(11, 12), new Edge(11, 14));
		Conditions _6 = new Conditions(new Edge(11, 13), new Edge(11, 14));
		Conditions _7 = new Conditions(new Edge(11, 12), new Edge(11, 13), new Edge(11, 14));

		AtomicTester tester = new AtomicTester(path, "pullUpEncapsulatedAttribute", "decapsulateAttribute");
		tester.print();
		assertTrue("Minimal Conflict Reasons are not 5", tester.check(new MCR(5)));
		assertTrue("Initial Conflict Reasons are not 13", tester.check(new ICR(13)));
		assertTrue(_1 + " not found", tester.check(_1));
		assertTrue(_2 + " not found", tester.check(_2));
		assertTrue(_3 + " not found", tester.check(_3));
		assertTrue(_4 + " not found", tester.check(_4));
		assertTrue(_5 + " not found", tester.check(_5));
		assertTrue(_6 + " not found", tester.check(_6));
		assertTrue(_7 + " not found", tester.check(_7));
		tester.ready();
	}

	@Test
	public void DPuDeCpa() {

		System.out.println("\t\t\tCPA Essential");
		CPATester tester = new CPATester(path, new String[] { "pullUpEncapsulatedAttribute" },
				new String[] { "decapsulateAttribute" });
		tester.print();
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();

		System.out.println("\t\t\tCPA");
		tester = new CPATester(path, false, new String[] { "pullUpEncapsulatedAttribute" },
				new String[] { "decapsulateAttribute" });
		tester.print();
		assertTrue("Critical Pairs are not 7", tester.check(new CP(7)));
		tester.ready();
	}

}
