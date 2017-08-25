package org.eclipse.emf.henshin.cpa.atomic.unitTest.jevTests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.runner.RulePreparator;
import org.eclipse.emf.henshin.cpa.atomic.tester.AtomicTester;
import org.eclipse.emf.henshin.cpa.atomic.tester.CPATester;
import org.eclipse.emf.henshin.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.preprocessing.NonDeletingPreparator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class NanoTest {

	private static CPATester eTester;
	private static AtomicTester aTester;
	private static String henshin = "testData/visualContracts-nanoXML/nanoXML.henshin";
	private static List<Rule> rules;
	private static Set<Span> initsNormal = new HashSet<>();
	private static Set<CriticalPair> initspNormal = new HashSet<>();
	private static Set<Span> initsPreserve = new HashSet<>();
	private static Set<CriticalPair> initspPreserve = new HashSet<>();

	@BeforeClass
	public static void prepare() {
		rules = new ArrayList<Rule>();
		String resource = henshin.substring(0, henshin.lastIndexOf("/") + 1);
		String mFile = henshin.substring(henshin.lastIndexOf("/") + 1, henshin.length());
		HenshinResourceSet resourceSet = new HenshinResourceSet(resource);
		Module module = resourceSet.getModule(mFile, false);
		for (Unit u : new ArrayList<>(module.getUnits()))
			if (u instanceof Rule) {
				Rule prepared = RulePreparator.prepareRule((Rule) u);
				rules.add(prepared);
			}
	}

	@Test
	public void beideRegelnNormal() {

		for (Rule r : rules) {
			for (Rule r2 : rules) {
				aTester = new AtomicTester(r, r2, true, false, false, true);
				initsNormal.addAll(aTester.getInitialReasons());
				aTester.print();
				
				List<Rule> a = new ArrayList<Rule>();
				List<Rule> b = new ArrayList<Rule>();
				a.add(r);
				b.add(r2);
				eTester = new CPATester(a, b, true, true, false, false, true);
				initspNormal.addAll(eTester.getInitialCriticalPairs());
				eTester.print();
				
				int icr = aTester.getInitialReasons().size();
				int icp = eTester.getInitialCriticalPairs().size();
				if (icr != icp)
					System.err.println(
							"Result:\n" + icr + " Initial Conflict Reasons\n" + icp + " Initial Critical Pairs");
			}
		}
	}

	@Test
	public void zweiteNichtLoeschend() {

		for (Rule r : rules) {
			for (Rule r2 : rules) {
				aTester = new AtomicTester(r, r2, true, false, true, true);
				initsPreserve.addAll(aTester.getInitialReasons());
				aTester.print();
				
				List<Rule> a = new ArrayList<Rule>();
				List<Rule> b = new ArrayList<Rule>();
				a.add(r);
				b.add(r2);
				eTester = new CPATester(a, b, true, true, false, true, true);
				initspPreserve.addAll(eTester.getInitialCriticalPairs());
				eTester.print();

				int icr = aTester.getInitialReasons().size();
				int icp = eTester.getInitialCriticalPairs().size();
				if (icr != icp)
					System.err.println(
							"Result:\n" + icr + " Initial Conflict Reasons\n" + icp + " Initial Critical Pairs");
			}
		}
	}
	@AfterClass
	public static void after(){
		System.out.println("_____________________________________________\nNormal second Rules\n");
		System.out.println("Result of Initial Dependency Reasons [Atomic]: " + initsNormal.size());
		System.out.println("Result of Initial Dependency Pairs [AGG]: " + initspNormal.size());
		System.out.println("_____________________________________________\nPreserved second Rules\n");
		System.out.println("Result of Initial Dependency Reasons [Atomic]: " + initsPreserve.size());
		System.out.println("Result of Initial Dependency Pairs [AGG]: " + initspPreserve.size());
	}
}
