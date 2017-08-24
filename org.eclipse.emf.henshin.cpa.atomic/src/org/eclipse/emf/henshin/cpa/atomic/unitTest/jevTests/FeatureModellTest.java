package org.eclipse.emf.henshin.cpa.atomic.unitTest.jevTests;

import static org.junit.Assert.fail;

import org.eclipse.emf.henshin.cpa.atomic.tester.AtomicTester;
import org.eclipse.emf.henshin.cpa.atomic.tester.CPATester;
import org.junit.AfterClass;
import org.junit.Test;

public class FeatureModellTest {

	private AtomicTester aTester;
	private CPATester cTester;
	private String pathNoAttr = "testData/featureModelingWithoutUpperLimitsOnReferences/fmedit_noAmalgamation_noNACs_noAttrChange/rules/";
	private String[] atomicA = new String[]{"atomic/arbitrary_edit/AddFeatureToGroup_execute.henshin",
			"atomic/arbitrary_edit/CreateConcreteFeature_execute.henshin",
			"atomic/arbitrary_edit/CreateGroup_execute.henshin",
			"atomic/arbitrary_edit/DeleteConcreteFeature_execute.henshin",
			"atomic/arbitrary_edit/DeleteGroup_execute.henshin",
			"atomic/arbitrary_edit/FloatTest.henshin",
			"atomic/arbitrary_edit/MoveFeature_execute.henshin",
			"atomic/arbitrary_edit/RemoveFeatureFromGroup_execute.henshin",
	};

	@Test
	public void test() {
		aTester = new AtomicTester(pathNoAttr+atomicA[0]);
		cTester = new CPATester(pathNoAttr+atomicA[0]);
		CPATester.printCP(cTester.getInitialCriticalPairs());
	}

}
