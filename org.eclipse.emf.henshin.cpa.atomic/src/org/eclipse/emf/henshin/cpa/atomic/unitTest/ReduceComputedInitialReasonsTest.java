package org.eclipse.emf.henshin.cpa.atomic.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.swt.internal.win32.MINMAXINFO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReduceComputedInitialReasonsTest {

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

	
	// Für diese Regelkombination sind die dangling edges kein Problem. Es kommt nciht zur Überapproximation -> 
//	@Test
//		public void compute_decapsulate_pullUp_ConflictReasonTest() {
//			AtomicCoreCPA atomicCoreCPA = new AtomicCoreCPA();
//			List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms(decapsulateAttributeRule,
//					pullUpEncapsulatedAttributeRule);
//			assertEquals(3, computedConflictAtoms.size());
//			
//			Set<ConflictAtom> methodCAs = new HashSet<ConflictAtom>();
//			
//			ConflictAtom conflictAtom_Method_2_13 = null;
//			ConflictAtom conflictAtom_Method_3_14 = null;
//			ConflictAtom conflictAtom_Parameter_5_15 = null;
//	
//			int numberOf_METHOD_atoms = 0;
//			int numberOf_PARAMETER_atoms = 0;
//			for (ConflictAtom conflictAtom : computedConflictAtoms) {
//				Span span = conflictAtom.getSpan();
//				Graph graph = span.getGraph();
//				EList<Node> nodes = graph.getNodes();
//				for (Node nodeInOverlapGraph : nodes) {
//					if (nodeInOverlapGraph.getType().getName().equals("Method")) {
//						numberOf_METHOD_atoms++;
//						if(nodeInOverlapGraph.getName().contains("13"))
//							conflictAtom_Method_2_13 = conflictAtom;
//						if(nodeInOverlapGraph.getName().contains("14"))
//							conflictAtom_Method_3_14 = conflictAtom;
//					} else if (nodeInOverlapGraph.getType().getName().equals("Parameter")) {
//						numberOf_PARAMETER_atoms++;
//						if(nodeInOverlapGraph.getName().contains("15"))
//							conflictAtom_Parameter_5_15 = conflictAtom;
//					} else {
//						assertTrue("node of wrong type in overlap graph", false);
//					}
//				}
//			}
//			assertEquals(2, numberOf_METHOD_atoms);
//			assertEquals(1, numberOf_PARAMETER_atoms);
//			
//			Set<Span> allMinimalConflictReasons = new HashSet<Span>();
//			//TODO: check that the two Reasons had been found AND that the three ConflictAtoms only have two (minimal)conflict reasons!
//			for(ConflictAtom conflictAtom : computedConflictAtoms){
//				Set<Span> reasons = conflictAtom.getReasons();
//				Assert.assertEquals(1, reasons.size());
//				allMinimalConflictReasons.addAll(reasons);
//			}
//			Assert.assertEquals(2, allMinimalConflictReasons.size());
//			
//			Span conflictReasonOfMethod_3_14_Atom = conflictAtom_Method_3_14.getReasons().iterator().next();
//			Span conflictReasonOfParameter_5_15_Atom = conflictAtom_Parameter_5_15.getReasons().iterator().next();
//	//		System.out.println(conflictReasonOfMethod_3_14_Atom);
//	//		System.out.println(conflictReasonOfParameter_5_15_Atom);
//			Assert.assertTrue(conflictReasonOfMethod_3_14_Atom.equals(conflictReasonOfParameter_5_15_Atom));
//			
//			Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
//			for(Span conflictReason : allMinimalConflictReasons){
//				minimalConflictReasons.add(atomicCoreCPA.new MinimalConflictReason(conflictReason));
//			}
//			
//			Set<InitialConflictReason> computeConflictReason = atomicCoreCPA.computeConflictReason(minimalConflictReasons);
//			Assert.assertEquals(3, computeConflictReason.size());
//			
//			//TODO: prüfen, dass es 5 CofnlictREason gibt, die sich aus einem zusammensetzen und 3 die sich asu zweien zusammen setzen und einen, der sich aus dreien zusammensetzt!
//			
//			
//	//		TODO: Prüfen!
//	//		Es sollten die beiden bekannten minimal conflict reason enthalten sein (Siehe auch Übersichtsgrafik. Dort ist notiert, dass jedes MCR auch ein CR ist)
//	//		Außerdem sollte die zusätzliche Kombination entstehen!
//	//		Für zusätzliche Kombination prüfen, dass der Graph fünf Knoten hat. zweimal Class, zweimal Method und einmal PArameter
//	//		Außerdem die jeweiligen Mappings prüfen!
//			
//		}

	@Test
	public void compute_pullUp_decapsulate_ConflictReasonTest() {
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pullUpEncapsulatedAttributeRule, decapsulateAttributeRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
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
		
		// alles alter TestCode durchs kopieren der im Papier betrachteten Regelreihenfolge!
//		Span conflictReasonOfMethod_3_14_Atom = conflictAtom_Method_3_14.getReasons().iterator().next();
//		Span conflictReasonOfParameter_5_15_Atom = conflictAtom_Parameter_5_15.getReasons().iterator().next();
////		System.out.println(conflictReasonOfMethod_3_14_Atom);
////		System.out.println(conflictReasonOfParameter_5_15_Atom);
//		Assert.assertTrue(conflictReasonOfMethod_3_14_Atom.equals(conflictReasonOfParameter_5_15_Atom));
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span conflictReason : allMinimalConflictReasons){
			minimalConflictReasons.add(new MinimalConflictReason(conflictReason));
		}
		
		Set<InitialConflictReason> computedInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(17, computedInitialReason.size());
		
		//von diesen 17 potentiellen CRs verletzen 10 die dangling condition. 
		// Das sind jeweils diejenigen, die die beiden folgenden Conflict Atoms enhalten:
		// 11_1 --> 13_3
		// 11_1 --> 14_2
		// dies könnte sich ggf. über ein "isApplicable" der zweiten Regel (hier 'decapsulate...') identifizieren
		// ABER: dazu müsste zuerst aus den InitialReasons jeweils ein vollständiger Overlap als Instanz erzeugt werden.
		// SEHR AUFWENDIG!!!
		
		// Versuch für ERkennung von danglingEdges udn somit Vermeidung der OVerapproximation:
		Set<InitialConflictReason> cleanedResults = new HashSet<InitialConflictReason>();
		Set<InitialConflictReason> filteredOutResults = new HashSet<InitialConflictReason>();
		int skippedCRs = 0;
		for(InitialConflictReason initialReason : computedInitialReason){
			Set<Mapping> mappingsInRule1 = initialReason.getMappingsInRule1();
			Set<Mapping> mappingsInRule2 = initialReason.getMappingsInRule2();
//			// track erroneous mappings
//			boolean skipCR = false;
//			Set<Node> nodesInrule2 = new HashSet<Node>();
//			for(Mapping mapping : mappingsInRule2){
//				if(nodesInrule2.contains(mapping.getImage())){
//					System.err.println("FEHLER");
//					skipCR = true;
//					skippedCRs++;
//				} else{
//					nodesInrule2.add(mapping.getImage());
//				}
//			}
//			if(!skipCR){
				List<Edge> danglingEdges = atomicCoreCPA.findDanglingEdgesByLHSOfRule2(mappingsInRule1, decapsulateAttributeRule, mappingsInRule2);
				if(danglingEdges.size()>0){
					filteredOutResults.add(initialReason);
				}else {
					cleanedResults.add(initialReason);
				}
//			}
		}

		System.err.println("cleanedResults: "+cleanedResults.size());
		System.err.println("filteredOutResults: "+filteredOutResults.size());
		System.err.println("skippedCRs: "+skippedCRs);
		
		//TODO: print details on the remaining InitialConflictReason
		int oneOriginMCR = 0;
		int twoOriginMCR = 0;
		int threeOriginMCR = 0;
		int undefinedOriginMCR = 0;
		Set<InitialConflictReason> threeOriginMCR_CRs = new HashSet<InitialConflictReason>();
		for(InitialConflictReason initialReason : cleanedResults){
			if(initialReason.getOriginMCRs().size() == 1)
				oneOriginMCR++;
			if(initialReason.getOriginMCRs().size() == 2)
				twoOriginMCR++;
			if(initialReason.getOriginMCRs().size() == 3){
				threeOriginMCR++;
				threeOriginMCR_CRs.add(initialReason);
			}
		}
		System.out.println("oneOriginMCR: "+oneOriginMCR);
		System.out.println("twoOriginMCR: "+twoOriginMCR);
		System.out.println("threeOriginMCR: "+threeOriginMCR);
		for(InitialConflictReason aThreeMcrCr : threeOriginMCR_CRs){
			aThreeMcrCr.getOriginMCRs();
			System.out.println(aThreeMcrCr.getGraph().toString());
		}
	}

}


