package org.eclipse.emf.henshin.cpa.atomic.unitTest;

//import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.CpaByAGG;
import org.eclipse.emf.henshin.cpa.UnsupportedRuleException;
import org.eclipse.emf.henshin.cpa.atomic.AtomicCoreCPA;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BoundaryNodesTest {

	final String PATH = "testData/automataExample/";
	final String henshinFileName = "executingTransitions.henshin";

	Rule executeNonLoop;

	@Before
	public void setUp() throws Exception {
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		Module module = resourceSet.getModule(henshinFileName, false);
		
		executeNonLoop = (Rule) module.getUnit("executeNonLoop");
	}

	@Test
	public void compute_executeNonLoop_MCR_Test() {
		AtomicCoreCPA atomicCoreCPA = new AtomicCoreCPA();
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms(executeNonLoop,
				executeNonLoop);
		Assert.assertEquals(3, computedConflictAtoms.size());
		
		Set<ConflictAtom> methodCAs = new HashSet<ConflictAtom>();
		
		ConflictAtom conflictAtom_c_2_5 = null;
		ConflictAtom conflictAtom_c_8_9 = null;
		ConflictAtom conflictAtom_n_9_10 = null;

		int numberOf_METHOD_atoms = 0;
		int numberOf_PARAMETER_atoms = 0;
		for (ConflictAtom conflictAtom : computedConflictAtoms) {
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
		}
//			assertEquals(2, numberOf_METHOD_atoms);
//			assertEquals(1, numberOf_PARAMETER_atoms);
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		//TODO: check that the two Reasons had been found AND that the three ConflictAtoms have three MCR (minimal conflict reasons)!
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(3, allMinimalConflictReasons.size());
		
//			Span initialReasonOfMethod_3_14_Atom = conflictAtom_Method_3_14.getReasons().iterator().next();
//			Span initialReasonOfParameter_5_15_Atom = conflictAtom_Parameter_5_15.getReasons().iterator().next();
//		System.out.println(initialReasonOfMethod_3_14_Atom);
//		System.out.println(initialReasonOfParameter_5_15_Atom);
//			Assert.assertTrue(initialReasonOfMethod_3_14_Atom.equals(initialReasonOfParameter_5_15_Atom));
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : allMinimalConflictReasons){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		
		Set<InitialConflictReason> computedInitialReason = atomicCoreCPA.computeInitialReason(minimalConflictReasons);
		Assert.assertEquals(7, computedInitialReason.size());
		//SUPER! 
		/* Die Anzahl entspricht zumindest der im Dokument
		 * "Von den 49 essential CPs sind 7 minimal CPs (Ergebnis 1, 23, 28, 33, 47, 48, 49)."
		 * 
		 */
		
		Set<ConflictReason> conflictReasonsDerivedFromInitialReason = new HashSet<ConflictReason>();
		Set<MinimalConflictReason> originMCRs = new HashSet<MinimalConflictReason>();
		for(InitialConflictReason initialReason : computedInitialReason){
			System.err.println(initialReason.toShortString());
			originMCRs.addAll(initialReason.getOriginMCRs());
			Set<ConflictAtom> byInitialReasonCoveredEdgeConflictAtoms = initialReason.getCoveredEdgeConflictAtoms();
			Set<ConflictAtom> allEdgeConflictAtoms = atomicCoreCPA.extractEdgeConflictAtoms(computedConflictAtoms);
			allEdgeConflictAtoms.removeAll(byInitialReasonCoveredEdgeConflictAtoms);
			Set<ConflictAtom> byInitialReasonUncoveredConflictAtoms = allEdgeConflictAtoms;
			Set<ConflictReason> allDerivedConflictReasons = initialReason.getAllDerivedConflictReasons(byInitialReasonUncoveredConflictAtoms);
			for(ConflictReason conflictReason : allDerivedConflictReasons){
				System.out.println(conflictReason.toShortString());
			}
			conflictReasonsDerivedFromInitialReason.addAll(allDerivedConflictReasons);
		}
		for(ConflictReason conflictReason : conflictReasonsDerivedFromInitialReason){
			System.out.println(conflictReason.toShortString());
		}
		Assert.assertEquals(3, originMCRs.size());
		Assert.assertEquals(29, conflictReasonsDerivedFromInitialReason.size());
		
		Set conflictAtoms = new HashSet<>();
		for(MinimalConflictReason mcr : originMCRs){
			conflictAtoms.addAll(mcr.getOriginMCRs());
		}
		Assert.assertEquals(3, conflictAtoms.size());
		
		
		

	}

	@Test
	public void essCpa_Test() {
		List<Rule> rules = new LinkedList<Rule>();
		rules.add(executeNonLoop);
		CpaByAGG cpaByAGG = new CpaByAGG();
		CPAOptions cpaOptions = new CPAOptions();
		cpaOptions.setEssential(true);
		CPAResult essCPs = null;
		try {
			cpaByAGG.init(rules, cpaOptions);
			essCPs = cpaByAGG.runConflictAnalysis();
		} catch (UnsupportedRuleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<CriticalPair> criticalPairs = essCPs.getCriticalPairs();
		
		Assert.assertEquals(29, criticalPairs.size());
		// die 29 Ergebnisse sind die verbleibenden Ergebnisse der ursprünglich 49 Ergebnsise,
		// nach Abzug der Ergebnisse mit parallelen Kanten
	}
	

}
