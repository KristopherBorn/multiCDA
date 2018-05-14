package org.eclipse.emf.henshin.multicda.cda.unitTest.jevTests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.runner.RulePreparator;
import org.eclipse.emf.henshin.multicda.cda.tester.CDATester;
import org.eclipse.emf.henshin.multicda.cda.tester.CPATester;
import org.eclipse.emf.henshin.multicda.cda.tester.Tester.Options;
import org.eclipse.emf.henshin.multicda.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.preprocessing.NonDeletingPreparator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import agg.gui.options.OptionGUI;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NanoTest {

	private static CPATester eTester;
	private static CDATester aTester;
	private static String henshin = "testData/visualContracts-nanoXML/nanoXML.henshin";
	private static List<Rule> rules;
	private static Set<Span> initsNormal = new HashSet<>();
	private static Set<CriticalPair> initspNormal = new HashSet<>();
	private static Set<Span> initsPreserve = new HashSet<>();
	private static Set<CriticalPair> initspPreserve = new HashSet<>();
	private static String resultNormal = "";
	private static String resultPreserve = "";

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

//	@Test
//	public void AbeideRegelnNormal() {
//
//		for (Rule r : rules) {
//			for (Rule r2 : rules) {
//				Options dependency = new Options(Options.DEPENDENCY);
//				Options print = new Options(Options.PRINT_HEADER, Options.PRINT_RESULT);
//				aTester = new CDATester(r, r2, print);
//				initsNormal.addAll(aTester.getConflictReasons());
//				aTester.print();
//
//				List<Rule> a = new ArrayList<Rule>();
//				List<Rule> b = new ArrayList<Rule>();
//				a.add(r);
//				b.add(r2);
//				Options dependEssent = new Options(Options.DEPENDENCY + Options.ESSENTIAL);
//				eTester = new CPATester(a, b, dependEssent);
//				initspNormal.addAll(eTester.getCriticalPairs());
//				eTester.print();
//
//				int icr = aTester.getConflictReasons().size();
//				int icp = eTester.getCriticalPairs().size();
//				if (icr != icp) {
//					resultNormal += "\t\t" + r.getName() + " --> " + r2.getName() + "\nAtomic: " + aTester + "\nCPA: "
//							+ eTester + "\n" + "Result:\n" + icr + " Initial Conflict Reasons\n" + icp
//							+ " Initial Critical Pairs\n";
//					// System.err.println(
//					// "Result:\n" + icr + " Initial Conflict Reasons\n" + icp +
//					// " Initial Critical Pairs");
//				}
//			}
//		}
//	}
//
//	@Test
//	public void BzweiteNichtLoeschend() {
//
//		Options print = new Options(Options.PRINT_HEADER, Options.PRINT_RESULT);
//		for (Rule r : rules) {
//			for (Rule r2 : rules) {
//				Options option = new Options(true, true, false, true);
//				aTester = new CDATester(r, r2, print);
//				initsPreserve.addAll(aTester.getConflictReasons());
//				aTester.print();
//
//				List<Rule> a = new ArrayList<Rule>();
//				List<Rule> b = new ArrayList<Rule>();
//				a.add(r);
//				b.add(r2);
//				eTester = new CPATester(a, b, option);
//				initspPreserve.addAll(eTester.getCriticalPairs());
//				eTester.print();
//
//				int icr = aTester.getConflictReasons().size();
//				int icp = eTester.getCriticalPairs().size();
//				if (icr != icp) {
//
//					resultPreserve += "\t\t" + r.getName() + " --> " + r2.getName() + "\nAtomic: " + aTester + "\nCPA: "
//							+ eTester + "\n" + "Result:\n" + icr + " Initial Conflict Reasons\n" + icp
//							+ " Initial Critical Pairs\n\n";
//					// System.err.println(
//					// "Result:\n" + icr + " Initial Conflict Reasons\n" + icp +
//					// " Initial Critical Pairs");
//				}
//			}
//
//		}
//	}
	
	@AfterClass
	public static void after() {
//		System.out.println("_____________________________________________\nNormal second Rules\n");
//		System.out.println("Result of Initial Dependency Reasons [Atomic]: " + initsNormal.size());
//		System.out.println("Result of Initial Dependency Pairs [AGG]: " + initspNormal.size());
//		System.out.println("_____________________________________________\nPreserved second Rules\n");
//		System.out.println("Result of Initial Dependency Reasons [Atomic]: " + initsPreserve.size());
//		System.out.println("Result of Initial Dependency Pairs [AGG]: " + initspPreserve.size());
//		System.err.println("\n_____________________________________________\nAll Normal Errors:\n" + resultNormal);
//		System.err.println("_____________________________________________\nAll Preserve Errors:\n" + resultPreserve);
	}
	
	@Test
	public void ddcrTest() {

		Options optionen = new Options();
		optionen.add(Options.PRINT_HEADER);
		optionen.add(Options.PRINT_RESULT);
		for (Rule r : rules) {
			for (Rule r2 : rules) {
				aTester = new CDATester(r, r2, optionen);
			}
		}
	}

}
