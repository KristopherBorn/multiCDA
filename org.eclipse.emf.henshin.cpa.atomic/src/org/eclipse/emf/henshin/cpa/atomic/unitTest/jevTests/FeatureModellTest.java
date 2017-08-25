package org.eclipse.emf.henshin.cpa.atomic.unitTest.jevTests;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialReason;
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class FeatureModellTest {

	private static AtomicTester aTester;
	private static CPATester cTester;
	private static List<Rule> rules;
	private static int toTest = 7;
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
			for (Rule r2 : NonDeletingPreparator.prepareNoneDeletingsVersionsRules(rules)) {
				aTester = new AtomicTester(r, r2, true);
				inits.addAll(aTester.getInitialReasons());
				List<Rule> a = new ArrayList<Rule>();
				List<Rule> b = new ArrayList<Rule>();
				a.add(r);
				b.add(r2);
				cTester = new CPATester(a, b, true);
				initsp.addAll(cTester.getInitialCriticalPairs());
				int icr = aTester.getInitialReasons().size();
				int icp = cTester.getInitialCriticalPairs().size();
				if (icr != icp)
					System.err.println(
							"Result:\n" + icr + " Initial Conflict Reasons\n" + icp + " Initial Critical Pairs");
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

//	@Test
//	public void test2() {
//		toTest++;
//	}
//
//	@Test
//	public void test3() {
//		toTest++;
//	}
//
//	@Test
//	public void test4() {
//		toTest++;
//	}
//
//	@Test
//	public void test5() {
//		toTest++;
//	}
//
//	@Test
//	public void test6() {
//		toTest++;
//	}
//
//	@Test
//	public void test7() {
//		toTest++;
//	}
//
//	@Test
//	public void test8() {
//		toTest++;
//	}
}
