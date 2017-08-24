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
import org.junit.Before;
import org.junit.Test;

public class FeatureModellTest {

	private static AtomicTester aTester;
	private static CPATester cTester;
	private static List<Rule> rules;
	private static String pathNoAttr = "testData/featureModelingWithoutUpperLimitsOnReferences/fmedit_noAmalgamation_noNACs_noAttrChange/rules/";
	private static File[] atomicA;

	@Before
	public void prepare() {
		File folder = new File(pathNoAttr + "atomic/arbitrary_edit/");
		atomicA = folder.listFiles();
		rules = new ArrayList<Rule>();
		for (File path : atomicA) {
			if (path.getPath().endsWith(".henshin")) {
				String henshin = path.getPath();
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
	}

	@Test
	public void test() {
		Set<InitialConflictReason> inits = new HashSet<>();
		int all = 0;
		for (Rule r : rules) {
			for (Rule r2 : rules) {
				aTester = new AtomicTester(r, r2);
				inits.addAll(aTester.getInitialReasons());
				all += aTester.getInitialReasons().size();
			}
		}
		System.out.println("Found: " + all + "\nno Doubles: " + inits.size());
		AtomicTester.printICR(inits);
		cTester = new CPATester(rules);
		CPATester.printCP(cTester.getInitialCriticalPairs());
	}

}
