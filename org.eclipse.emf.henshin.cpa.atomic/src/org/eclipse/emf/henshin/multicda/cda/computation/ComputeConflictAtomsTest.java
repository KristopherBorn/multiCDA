package org.eclipse.emf.henshin.multicda.cda.computation;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ComputeConflictAtomsTest {

	final String PATH = "testData/refactoring/";
	final String henshinFileName = "refactorings.henshin";

	Rule decapsulateAttributeRule;
	Rule pullUpEncapsulatedAttributeRule;

	@Before
	public void setUp() throws Exception {
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		Module module = resourceSet.getModule(henshinFileName, false);

		for (Unit unit : module.getUnits()) {
			if (unit.getName().equals("decapsulateAttribute"))
				decapsulateAttributeRule = (Rule) unit;
			if (unit.getName().equals("pullUpEncapsulatedAttribute"))
				pullUpEncapsulatedAttributeRule = (Rule) unit;
		}
	}

	@Test
	public void computeConflictAtomsTest() {
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule,
				pullUpEncapsulatedAttributeRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		assertEquals(3, computedConflictAtoms.size());
		
		Set<ConflictAtom> methodCAs = new HashSet<ConflictAtom>();
		
		ConflictAtom conflictAtom_Method_2_13 = null;
		ConflictAtom conflictAtom_Method_3_14 = null;
		ConflictAtom conflictAtom_Parameter_5_15 = null;

		int numberOf_METHOD_atoms = 0;
		int numberOf_PARAMETER_atoms = 0;
		for (ConflictAtom conflictAtom : computedConflictAtoms) {
			Span span = conflictAtom.getSpan();
			Graph graph = span.getGraph();
			EList<Node> nodes = graph.getNodes();
			for (Node nodeInOverlapGraph : nodes) {
				if (nodeInOverlapGraph.getType().getName().equals("Method")) {
					numberOf_METHOD_atoms++;
					if(nodeInOverlapGraph.getName().contains("13"))
						conflictAtom_Method_2_13 = conflictAtom;
					if(nodeInOverlapGraph.getName().contains("14"))
						conflictAtom_Method_3_14 = conflictAtom;
				} else if (nodeInOverlapGraph.getType().getName().equals("Parameter")) {
					numberOf_PARAMETER_atoms++;
					if(nodeInOverlapGraph.getName().contains("15"))
						conflictAtom_Parameter_5_15 = conflictAtom;
				} else {
					assertTrue("node of wrong type in overlap graph", false);
				}
			}
		}
		assertEquals(2, numberOf_METHOD_atoms);
		assertEquals(1, numberOf_PARAMETER_atoms);
		
		//TODO: check that the two Reasons had been found AND that the three ConflictAtoms only have two (minimal)conflict reasons!
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
		}
		
		Span minimalConflictReasonOfMethod_3_14_Atom = conflictAtom_Method_3_14.getMinimalConflictReasons().iterator().next();
		Span minimalConflictReasonOfParameter_5_15_Atom = conflictAtom_Parameter_5_15.getMinimalConflictReasons().iterator().next();
//		System.out.println(conflictReasonOfMethod_3_14_Atom);
//		System.out.println(conflictReasonOfParameter_5_15_Atom);
		Assert.assertTrue(minimalConflictReasonOfMethod_3_14_Atom.equals(minimalConflictReasonOfParameter_5_15_Atom));
	}
	
	// TODO: hier einen Test hinzuf�gen f�r die PushDownGroup Regel aus dem FeatureModelRefactoring Beispiel.

}

