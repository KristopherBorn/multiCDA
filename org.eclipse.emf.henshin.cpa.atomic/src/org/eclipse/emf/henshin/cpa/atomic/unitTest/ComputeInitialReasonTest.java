package org.eclipse.emf.henshin.cpa.atomic.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.CpaByAGG;
import org.eclipse.emf.henshin.cpa.ICriticalPairAnalysis;
import org.eclipse.emf.henshin.cpa.MinimalConflict;
import org.eclipse.emf.henshin.cpa.UnsupportedRuleException;
import org.eclipse.emf.henshin.cpa.atomic.AtomicCoreCPA;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.swt.internal.win32.MINMAXINFO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ComputeInitialReasonTest {

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
		public void compute_decapsulate_pullUp_InitialReasonTest() {
			AtomicCoreCPA atomicCoreCPA = new AtomicCoreCPA();
			List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms(decapsulateAttributeRule,
					pullUpEncapsulatedAttributeRule);
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
			
			Set<Span> allMinimalConflictReasons = new HashSet<Span>();
			//TODO: check that the two Reasons had been found AND that the three ConflictAtoms only have two (minimal)conflict reasons!
			for(ConflictAtom conflictAtom : computedConflictAtoms){
				Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
				Assert.assertEquals(1, reasons.size());
				allMinimalConflictReasons.addAll(reasons);
			}
			Assert.assertEquals(2, allMinimalConflictReasons.size());
			
			Span initialReasonOfMethod_3_14_Atom = conflictAtom_Method_3_14.getMinimalConflictReasons().iterator().next();
			Span initialReasonOfParameter_5_15_Atom = conflictAtom_Parameter_5_15.getMinimalConflictReasons().iterator().next();
	//		System.out.println(initialReasonOfMethod_3_14_Atom);
	//		System.out.println(initialReasonOfParameter_5_15_Atom);
			Assert.assertTrue(initialReasonOfMethod_3_14_Atom.equals(initialReasonOfParameter_5_15_Atom));
			
			Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
			for(Span minimalConflictReason : allMinimalConflictReasons){
				minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
			}
			
			Set<InitialConflictReason> computeInitialReason = atomicCoreCPA.computeInitialReason(minimalConflictReasons);
			Assert.assertEquals(3, computeInitialReason.size());
			
			
	//		TODO: Prüfen!
	//		Es sollten die beiden bekannten minimal conflict reason enthalten sein (Siehe auch Übersichtsgrafik. Dort ist notiert, dass jedes MCR auch ein CR ist)
	//		Außerdem sollte die zusätzliche Kombination entstehen!
	//		Für zusätzliche Kombination prüfen, dass der Graph fünf Knoten hat. zweimal Class, zweimal Method und einmal PArameter
	//		Außerdem die jeweiligen Mappings prüfen!
			
		}

	@Test
	public void compute_pullUp_decapsulate_InitialReasonTest() {
		AtomicCoreCPA atomicCoreCPA = new AtomicCoreCPA();
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms(pullUpEncapsulatedAttributeRule, decapsulateAttributeRule);
		assertEquals(5, computedConflictAtoms.size());
		
		Set<ConflictAtom> methodCAs = new HashSet<ConflictAtom>();

		// alles alter TestCode durchs kopieren der im Papier betrachteten Regelreihenfolge!
//		ConflictAtom conflictAtom_Method_2_13 = null;
//		ConflictAtom conflictAtom_Method_3_14 = null;
//		ConflictAtom conflictAtom_Parameter_5_15 = null;
//
//		int numberOf_METHOD_atoms = 0;
//		int numberOf_PARAMETER_atoms = 0;
//		for (ConflictAtom conflictAtom : computedConflictAtoms) {
//			Span span = conflictAtom.getSpan();
//			Graph graph = span.getGraph();
//			EList<Node> nodes = graph.getNodes();
//			for (Node nodeInOverlapGraph : nodes) {
//				if (nodeInOverlapGraph.getType().getName().equals("Method")) {
//					numberOf_METHOD_atoms++;
//					if(nodeInOverlapGraph.getName().contains("13"))
//						conflictAtom_Method_2_13 = conflictAtom;
//					if(nodeInOverlapGraph.getName().contains("14"))
//						conflictAtom_Method_3_14 = conflictAtom;
//				} else if (nodeInOverlapGraph.getType().getName().equals("Parameter")) {
//					numberOf_PARAMETER_atoms++;
//					if(nodeInOverlapGraph.getName().contains("15"))
//						conflictAtom_Parameter_5_15 = conflictAtom;
//				} else {
//					assertTrue("node of wrong type in overlap graph", false);
//				}
//			}
//		}
//		assertEquals(2, numberOf_METHOD_atoms);
//		assertEquals(1, numberOf_PARAMETER_atoms);
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		//TODO: check that the two Reasons had been found AND that the three ConflictAtoms only have two (minimal)conflict reasons!
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(5, allMinimalConflictReasons.size());
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : allMinimalConflictReasons){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		
		Set<InitialConflictReason> computedInitialReason = atomicCoreCPA.computeInitialReason(minimalConflictReasons);
		Assert.assertEquals(17, computedInitialReason.size());
		
		//von diesen 17 potentiellen CRs verletzen 10 die dangling condition. 
		// Das sind jeweils diejenigen, die die beiden folgenden Conflict Atoms enhalten:
		// 11_1 --> 13_3
		// 11_1 --> 14_2
		// dies könnte sich ggf. über ein "isApplicable" der zweiten Regel (hier 'decapsulate...') identifizieren
		// ABER: dazu müsste zuerst aus den ConflictREasons jeweils ein vollständiger Overlap als Instanz erzeugt werden.
		// SEHR AUFWENDIG!!!
		
	}
	
	

	@Test
	public void essCpaTest() {
		
		ICriticalPairAnalysis cpaByAGG = new CpaByAGG();
		List<Rule> r1 = new LinkedList<Rule>();
		r1.add(decapsulateAttributeRule);
		List<Rule> r2  = new LinkedList<Rule>();
		r2.add(pullUpEncapsulatedAttributeRule);
		CPAOptions options = new CPAOptions();
		options.setEssential(true);
		CPAResult runConflictAnalysis;
		int amountOfEssConflicts = -1;
		try {
			cpaByAGG.init(r1, r2, options);
			runConflictAnalysis = cpaByAGG.runConflictAnalysis();
			amountOfEssConflicts = runConflictAnalysis.getCriticalPairs().size();
			System.err.println("amount of ess CPs: " + amountOfEssConflicts);
		} catch (UnsupportedRuleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(3, amountOfEssConflicts);
	}

}

