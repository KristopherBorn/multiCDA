package org.eclipse.emf.henshin.multicda.cda.unitTest.jevTests;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.runner.RulePreparator;
import org.eclipse.emf.henshin.multicda.cda.tester.CDATester;
import org.eclipse.emf.henshin.multicda.cda.tester.CPATester;
import org.eclipse.emf.henshin.multicda.cda.tester.Tester.Options;
import org.eclipse.emf.henshin.multicda.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.preprocessing.NonDeletingPreparator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class FeatureModellTest {

	private static CDATester aTester;
	private static CPATester cTester;
	private static List<Rule> rules;
	private static int toTest = 0;
	private static Map<String, Set<Span>> resultA = new HashMap<String, Set<Span>>();
	private static Map<String, Set<CriticalPair>> resultE = new HashMap<String, Set<CriticalPair>>();

	private static String[] folders = new String[] { "atomic/arbitrary_edit/", "atomic/generalization/",
			"atomic/refactoring/", "atomic/specialization/", "complex/arbitrary_edit/", "complex/generalization/",
			"complex/refactoring/", "complex/specialization/", };
	private static String pathNoAttr = "testData/featureModeling/fmedit/rules/";
	private static File[] files;

	@BeforeClass
	public static void before() {
		FeatureModelPackage.eINSTANCE.eClass();
	}

	@Before
	public void prepare() {
		File folder = new File(pathNoAttr + folders[toTest]);
		files = folder.listFiles();
		rules = new ArrayList<Rule>();
		for (File file : files) {
			if (file.getPath().endsWith(".henshin")) {
				String henshin = file.getPath();
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
		}
		Set<Span> inits = new HashSet<>();
		Set<CriticalPair> initsp = new HashSet<>();
		for (Rule r : rules) {
			for (Rule r2 : rules) {
				new CDATester(r, r2, new Options(Options.ESSENTIAL, Options.PRINT_HEADER, Options.PRINT_RESULT));
//				inits.addAll(aTester.getConflictReasons());
//				List<Rule> a = new ArrayList<Rule>();
//				List<Rule> b = new ArrayList<Rule>();
//				a.add(r);
//				b.add(r2);
//				new CPATester(a, b, new Options(true));
//				initsp.addAll(cTester.getCriticalPairs());
//				int icr = aTester.getConflictReasons().size();
//				int icp = cTester.getCriticalPairs().size();
//				if (icr != icp)
//					System.err.println(
//							"Result:\n" + icr + " Initial Conflict Reasons\n" + icp + " Initial Critical Pairs");
			}
		}
		resultA.put(folders[toTest], inits);
		resultE.put(folders[toTest], initsp);
	}

	@AfterClass
	public static void results() {
		System.out.println("_________________________________________________________________________\n\nTested: "
				+ resultA.size());
		for (String folder : folders) {
			Set<Span> cr = resultA.get(folder);
			if (cr != null) {
				System.out
						.println("\nFolder Tested: " + folder + "\nFound: " + cr.size() + " Initial Conflict Reasons");
//			AtomicTester.printICR(cr);
			}
			Set<CriticalPair> cp = resultE.get(folder);
			if (cp != null) {
				System.out.println("Found: " + cp.size() + " Initial Critical Pairs");
//			CPATester.printCP(cp);
			}
		}
	}

	@Test
	public void test1() {
		toTest++;
	}

	@Test
	public void test2() {
		toTest++;
	}

	@Test
	public void test3() {
		toTest++;
	}

	@Test
	public void test4() {
		toTest++;
	}

	@Test
	public void test5() {
		toTest++;
	}

	@Test
	public void test6() {
		toTest++;
	}

	@Test
	public void test7() {
		toTest++;
	}

//	@Test
//	public void test8() {
//		toTest++;
//	}
}
