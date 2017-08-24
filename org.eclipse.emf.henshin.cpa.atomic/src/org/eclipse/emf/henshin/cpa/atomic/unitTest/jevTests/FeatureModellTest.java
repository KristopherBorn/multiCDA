package org.eclipse.emf.henshin.cpa.atomic.unitTest.jevTests;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.tester.AtomicTester;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.tester.CPATester;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class FeatureModellTest {

	private static AtomicTester aTester;
	private static CPATester cTester;
	private static List<Rule> rules;
	private static int toTest = 0;
	private static String[] folders = new String[]{
			"atomic/arbitrary_edit/",
			"atomic/generalization/",
			"atomic/refactoring/",
			"atomic/specialization/",
			"complex/arbitrary_edit/",
			"complex/generalization/",
			"complex/refactoring/",
			"complex/specialization/",
	};
	private static String pathNoAttr = "testData/featureModelingWithoutUpperLimitsOnReferences/fmedit_noAmalgamation_noNACs_noAttrChange/rules/";
	private static File[] files;
	private static List<Set<InitialConflictReason>> result = new ArrayList<Set<InitialConflictReason>>();

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

				for (Unit u : module.getUnits())
					if (u instanceof Rule) {
						System.out.println(u.getName());
						rules.add((Rule) u);
					}
			}
		}
		Set<InitialConflictReason> inits = new HashSet<>();
		for (Rule r : rules) {
			for (Rule r2 : rules) {
				aTester = new AtomicTester(r, r2);
				inits.addAll(aTester.getInitialReasons());
			}
		}
		result.add(inits);
//		cTester = new CPATester(rules);
//		CPATester.printCP(cTester.getInitialCriticalPairs());
	}

	@Test
	public void test1() {
		toTest++;
		System.out.println(toTest);
	}

	@Test
	public void test2() {
		toTest++;
		System.out.println(toTest);
	}
	@Test
	public void test3() {
		toTest++;
		System.out.println(toTest);
	}
	@Test
	public void test4() {
		toTest++;
		System.out.println(toTest);
	}
	@AfterClass
	public static void results(){
		System.out.println("_________________________________________________________________________\n\nTested: " + result.size());
		for(Set<InitialConflictReason> inits : result){
			System.out.println("Found: " + inits.size());
			AtomicTester.printICR(inits);
		}
	}

}
