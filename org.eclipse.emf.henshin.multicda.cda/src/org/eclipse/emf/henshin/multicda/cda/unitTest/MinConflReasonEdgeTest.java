package org.eclipse.emf.henshin.multicda.cda.unitTest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.computation.AtomCandidateComputation;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.preprocessing.RuleSetModifier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class MinConflReasonEdgeTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void pushDownGroupTest() {

		String PATH = "testData/featureModelingWithoutUpperLimitsOnReferences/fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserveProgrammatic/";
		String filePATH = PATH + "normal_rules/complex/specialization/";
		String henshinFileName = "PushDownGroup.henshin";

		Rule pushDownGroupRule = null;
		
		FeatureModelPackage.eINSTANCE.eClass(); 
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		
		HenshinResourceSet resourceSet = new HenshinResourceSet(filePATH);
		Module module = resourceSet.getModule(henshinFileName, false);

		for (Unit unit : module.getUnits()) {
			if (unit.getName().equals("PushDownGroup"))
				pushDownGroupRule = (Rule) unit;
		}
		
		
		Copier copier = new Copier();
		Rule pushDownGroupRulePreserve = (Rule) copier.copy(pushDownGroupRule);
		copier.copyReferences();
		
		
		ConflictAnalysis atomicCoreCPA_forCA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);
		List<ConflictAtom> conflictAtoms = atomicCoreCPA_forCA.computeConflictAtoms();
		System.err.println("amount of conflictAtoms: "+conflictAtoms.size());
		

		AtomCandidateComputation candComp = new AtomCandidateComputation(pushDownGroupRule, pushDownGroupRulePreserve);
		List<Span> conflictAtomCandidates = candComp.computeAtomCandidates();
		System.err.println("amount of conflictAtomCandidates: "+conflictAtomCandidates.size());
		

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);

		Set<MinimalConflictReason> mCRs = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons();
		}
		System.err.println("amount of MCR: "+mCRs.size());
		
		for(Span mcr: mCRs){
			System.err.println("is MCR instance of?");
			if(mcr instanceof MinimalConflictReason){
				System.err.println("true");
			}else {
				System.err.println("false");
			}
		}
		

		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : mCRs){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		

		Set<ConflictReason> computedConflictReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		System.err.println("amount of computedConflictReason: "+computedConflictReason.size());
		
		Map<Integer, Integer> amoutOfMCRs = new HashMap<Integer, Integer>();
		for(ConflictReason cr : computedConflictReason){
			int amountOfMCRsInCR = cr.getOriginMCRs().size();
			Integer amountOfCRs = amoutOfMCRs.get(amountOfMCRsInCR);
			if(amountOfCRs == null){
				amoutOfMCRs.put(amountOfMCRsInCR , 1);
			}else {
				amoutOfMCRs.put(amountOfMCRsInCR, (amountOfCRs+1));
			}
		}
		System.err.println("Anzahl der Einträge im 'amoutOfMCRs': "+amoutOfMCRs.size());
		
		for (Map.Entry<Integer, Integer> entry : amoutOfMCRs.entrySet()) {
			System.out.println("Die Analyse ergibt " + entry.getValue() + " Confl.Reason die aus " + entry.getKey() + " MinimalConflictReason bestehen");
		}
	}

	
	@Test
	public void halfPushDownGroupTest() {

		String PATH = "testData/featureModelingWithoutUpperLimitsOnReferences/fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserveProgrammatic/";
		String filePATH = PATH;// + "normal_rules/complex/specialization/";
		String henshinFileName = "PushDownGroup.henshin";

		Rule pushDownGroupRule = null;
		
		FeatureModelPackage.eINSTANCE.eClass();
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		
		HenshinResourceSet resourceSet = new HenshinResourceSet(filePATH);
		Module module = resourceSet.getModule(henshinFileName, false);

		for (Unit unit : module.getUnits()) {
			if (unit.getName().equals("PushDownGroup"))
				pushDownGroupRule = (Rule) unit;
		}
		
		
		Copier copier = new Copier();
		Rule pushDownGroupRulePreserve = (Rule) copier.copy(pushDownGroupRule);
		copier.copyReferences();
		
		
		ConflictAnalysis atomicCoreCPA_forCA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);
		List<ConflictAtom> conflictAtoms = atomicCoreCPA_forCA.computeConflictAtoms();
		System.err.println("amount of conflictAtoms: "+conflictAtoms.size());
		

		AtomCandidateComputation candComp = new AtomCandidateComputation(pushDownGroupRule, pushDownGroupRulePreserve);
		List<Span> conflictAtomCandidates = candComp.computeAtomCandidates();
		System.err.println("amount of conflictAtomCandidates: "+conflictAtomCandidates.size());
		
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);


		Set<MinimalConflictReason> mCRs = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons();
		}
		System.err.println("amount of MCR: "+mCRs.size());
		
		for(Span mcr: mCRs){
			System.err.println("is MCR instance of?");
			if(mcr instanceof MinimalConflictReason){
				System.err.println("true");
			}else {
				System.err.println("false");
			}
		}
		

		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : mCRs){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		

		Set<ConflictReason> computedConflictReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		System.err.println("amount of computedConflictReason: "+computedConflictReason.size());
		
		Map<Integer, Integer> amoutOfMCRs = new HashMap<Integer, Integer>();
		for(ConflictReason cr : computedConflictReason){
			int amountOfMCRsInCR = cr.getOriginMCRs().size();
			Integer amountOfCRs = amoutOfMCRs.get(amountOfMCRsInCR);
			if(amountOfCRs == null){
				amoutOfMCRs.put(amountOfMCRsInCR , 1);
			}else {
				amoutOfMCRs.put(amountOfMCRsInCR, (amountOfCRs+1));
			}
		}
		System.err.println("Anzahl der Einträge im 'amoutOfMCRs': "+amoutOfMCRs.size());
		
		for (Map.Entry<Integer, Integer> entry : amoutOfMCRs.entrySet()) {
			System.out.println("Die Analyse ergibt " + entry.getValue() + " Confl.Reason die aus " + entry.getKey() + " MinimalConflictReason bestehen");
		}
	}
	

	@Test
	public void pushDownGroupDeletionCircleHalfTest() {

		String PATH = "testData/featureModelingWithoutUpperLimitsOnReferences/fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserveProgrammatic/";
		String filePATH = PATH + "pushDownGroupDeletionCircleHalf/";
		String henshinFileName = "HalfPushDownGroupCircle.henshin";

		Rule pushDownGroupRule = null;
		
		FeatureModelPackage.eINSTANCE.eClass(); 
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		
		HenshinResourceSet resourceSet = new HenshinResourceSet(filePATH);
		Module module = resourceSet.getModule(henshinFileName, false);

		for (Unit unit : module.getUnits()) {
			if (unit.getName().equals("PushDownGroup"))
				pushDownGroupRule = (Rule) unit;
		}
		
		
		Copier copier = new Copier();
		Rule pushDownGroupRulePreserve = (Rule) copier.copy(pushDownGroupRule);
		copier.copyReferences();
		
		
		ConflictAnalysis atomicCoreCPA_forCA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);
		List<ConflictAtom> conflictAtoms = atomicCoreCPA_forCA.computeConflictAtoms();
		System.err.println("amount of conflictAtoms: "+conflictAtoms.size());
		

		AtomCandidateComputation candComp = new AtomCandidateComputation(pushDownGroupRule, pushDownGroupRulePreserve);
		List<Span> conflictAtomCandidates = candComp.computeAtomCandidates();
		System.err.println("amount of conflictAtomCandidates: "+conflictAtomCandidates.size());
		

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);

		Set<MinimalConflictReason> mCRs = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons();
		}
		System.err.println("amount of MCR: "+mCRs.size());
		
		for(Span mcr: mCRs){
			System.err.println("is MCR instance of?");
			if(mcr instanceof MinimalConflictReason){
				System.err.println("true");
			}else {
				System.err.println("false");
			}
		}
		

		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : mCRs){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		

		Set<ConflictReason> computedConflictReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		System.err.println("amount of computedConflictReason: "+computedConflictReason.size());
		
		Map<Integer, Integer> amoutOfMCRs = new HashMap<Integer, Integer>();
		for(ConflictReason cr : computedConflictReason){
			int amountOfMCRsInCR = cr.getOriginMCRs().size();
			Integer amountOfCRs = amoutOfMCRs.get(amountOfMCRsInCR);
			if(amountOfCRs == null){
				amoutOfMCRs.put(amountOfMCRsInCR , 1);
			}else {
				amoutOfMCRs.put(amountOfMCRsInCR, (amountOfCRs+1));
			}
		}
		System.err.println("Anzahl der Einträge im 'amoutOfMCRs': "+amoutOfMCRs.size());
		
		for (Map.Entry<Integer, Integer> entry : amoutOfMCRs.entrySet()) {
			System.out.println("Die Analyse ergibt " + entry.getValue() + " Confl.Reason die aus " + entry.getKey() + " MinimalConflictReason bestehen");
		}
	}
}
