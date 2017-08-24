package org.eclipse.emf.henshin.cpa.atomic.unitTest;

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
import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
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
	
//	TODO: hier einen Test hinzufügen für die drei löschenden Regeln aus dem Visual Ciontract Beispiel.
//	Dabei sollte herauskommen, dass die #CA = # MCR      ABER: wieviele sollten das sein? 
//  wie stellen sich die Ergebniszahlen im Vergelich zur ess. CPA dar? 


//	TODO: hier einen Test hinzufügen für die PushDownGroup Regel aus dem FeatureModelRefactoring Beispiel.
//		Dabei sollte herauskommen, dass die #CA = # MCR      ABER: wieviele sollten das sein? 
//				Laut Gabi ausgehend von den sechs gelöschten Kanten entsprechend 6 MCR!
	@Test
	public void pushDownGroupTest() {

		String PATH = "testData/featureModelingWithoutUpperLimitsOnReferences/fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserveProgrammatic/";
		String filePATH = PATH + "normal_rules/complex/specialization/";
		String henshinFileName = "PushDownGroup.henshin";

		Rule pushDownGroupRule = null;
		
		// VERDAMMT noch mal schon wieder das registrieren!!!!
		FeatureModelPackage.eINSTANCE.eClass(); //TODO(11.04.2017): Gibt es einen programmatischen Weg um generell  
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		
		HenshinResourceSet resourceSet = new HenshinResourceSet(filePATH);
		Module module = resourceSet.getModule(henshinFileName, false);

		for (Unit unit : module.getUnits()) {
			if (unit.getName().equals("PushDownGroup"))
				pushDownGroupRule = (Rule) unit;
//			if (unit.getName().equals("pullUpEncapsulatedAttribute"))
//				pullUpEncapsulatedAttributeRule = (Rule) unit;
		}
		
		
		// DONE: zweite Regel "non deleting" machen
		Copier copier = new Copier();
		Rule pushDownGroupRulePreserve = (Rule) copier.copy(pushDownGroupRule);
		// TODO: soll die kopierte Regel auch Teil des Module sein? für den nachfolgenden Export vermutlich nicht verkehrt, aber wirklich gewollt? 
		copier.copyReferences();
		
		
//		TODO: atomicConflicts berechnen!
		ConflictAnalysis atomicCoreCPA_forCA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);
		List<ConflictAtom> conflictAtoms = atomicCoreCPA_forCA.computeConflictAtoms();
		System.err.println("amount of conflictAtoms: "+conflictAtoms.size());
		

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);

		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(pushDownGroupRule, pushDownGroupRulePreserve);
//		System.out.println("HALT");
		System.err.println("amount of conflictAtomCandidates: "+conflictAtomCandidates.size());
		
//		assertEquals(5, conflictAtomCandidates.size());

//		for (AtomicCoreCPA.Span candidate : conflictAtomCandidates) {
//			EList<Node> nodesOfCandidate = candidate.getGraph().getNodes();
//			assertEquals(1, nodesOfCandidate.size());
//		}

		Set<MinimalConflictReason> mCRs = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(pushDownGroupRule, pushDownGroupRulePreserve, candidate,
					mCRs);
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
		

		Set<InitialConflictReason> computedConflictReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		System.err.println("amount of computedConflictReason: "+computedConflictReason.size());
		
		// Map<AnzahlMCR, AnzahlCRs>
		Map<Integer, Integer> amoutOfMCRs = new HashMap<Integer, Integer>();
		// wie groß (= Anzahl Knoten) sind die InitialConflictReason? 
		for(InitialConflictReason cr : computedConflictReason){
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
		
		// VERDAMMT noch mal schon wieder das registrieren!!!!
		FeatureModelPackage.eINSTANCE.eClass(); //TODO(11.04.2017): Gibt es einen programmatischen Weg um generell  
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		
		HenshinResourceSet resourceSet = new HenshinResourceSet(filePATH);
		Module module = resourceSet.getModule(henshinFileName, false);

		for (Unit unit : module.getUnits()) {
			if (unit.getName().equals("PushDownGroup"))
				pushDownGroupRule = (Rule) unit;
//			if (unit.getName().equals("pullUpEncapsulatedAttribute"))
//				pullUpEncapsulatedAttributeRule = (Rule) unit;
		}
		
		
		// DONE: zweite Regel "non deleting" machen
		Copier copier = new Copier();
		Rule pushDownGroupRulePreserve = (Rule) copier.copy(pushDownGroupRule);
		// TODO: soll die kopierte Regel auch Teil des Module sein? für den nachfolgenden Export vermutlich nicht verkehrt, aber wirklich gewollt? 
		copier.copyReferences();
		
		
//		TODO: atomicConflicts berechnen!
		ConflictAnalysis atomicCoreCPA_forCA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);
		List<ConflictAtom> conflictAtoms = atomicCoreCPA_forCA.computeConflictAtoms();
		System.err.println("amount of conflictAtoms: "+conflictAtoms.size());
		

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);

		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(pushDownGroupRule, pushDownGroupRulePreserve);
//		System.out.println("HALT");
		System.err.println("amount of conflictAtomCandidates: "+conflictAtomCandidates.size());
		
//		assertEquals(5, conflictAtomCandidates.size());

//		for (AtomicCoreCPA.Span candidate : conflictAtomCandidates) {
//			EList<Node> nodesOfCandidate = candidate.getGraph().getNodes();
//			assertEquals(1, nodesOfCandidate.size());
//		}

		Set<MinimalConflictReason> mCRs = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(pushDownGroupRule, pushDownGroupRulePreserve, candidate,
					mCRs);
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
		

		Set<InitialConflictReason> computedConflictReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		System.err.println("amount of computedConflictReason: "+computedConflictReason.size());
		
		// Map<AnzahlMCR, AnzahlCRs>
		Map<Integer, Integer> amoutOfMCRs = new HashMap<Integer, Integer>();
		// wie groß (= Anzahl Knoten) sind die InitialConflictReason? 
		for(InitialConflictReason cr : computedConflictReason){
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
		//TODO: Assert für die vier Confl. Reason die auf einem MCR basieren
		//TODO: Assert für die sechs Confl. Reason die auf zwei MCR basieren
	}
	

	@Test
	public void pushDownGroupDeletionCircleHalfTest() {

		String PATH = "testData/featureModelingWithoutUpperLimitsOnReferences/fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserveProgrammatic/";
		String filePATH = PATH + "pushDownGroupDeletionCircleHalf/";
		String henshinFileName = "HalfPushDownGroupCircle.henshin";

		Rule pushDownGroupRule = null;
		
		// VERDAMMT noch mal schon wieder das registrieren!!!!
		FeatureModelPackage.eINSTANCE.eClass(); //TODO(11.04.2017): Gibt es einen programmatischen Weg um generell  
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		
		HenshinResourceSet resourceSet = new HenshinResourceSet(filePATH);
		Module module = resourceSet.getModule(henshinFileName, false);

		for (Unit unit : module.getUnits()) {
			if (unit.getName().equals("PushDownGroup"))
				pushDownGroupRule = (Rule) unit;
//			if (unit.getName().equals("pullUpEncapsulatedAttribute"))
//				pullUpEncapsulatedAttributeRule = (Rule) unit;
		}
		
		
		// DONE: zweite Regel "non deleting" machen
		Copier copier = new Copier();
		Rule pushDownGroupRulePreserve = (Rule) copier.copy(pushDownGroupRule);
		// TODO: soll die kopierte Regel auch Teil des Module sein? für den nachfolgenden Export vermutlich nicht verkehrt, aber wirklich gewollt? 
		copier.copyReferences();
		
		
//		TODO: atomicConflicts berechnen!
		ConflictAnalysis atomicCoreCPA_forCA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);
		List<ConflictAtom> conflictAtoms = atomicCoreCPA_forCA.computeConflictAtoms();
		System.err.println("amount of conflictAtoms: "+conflictAtoms.size());
		

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(pushDownGroupRule, pushDownGroupRulePreserve);

		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(pushDownGroupRule, pushDownGroupRulePreserve);
//		System.out.println("HALT");
		System.err.println("amount of conflictAtomCandidates: "+conflictAtomCandidates.size());
		
//		assertEquals(5, conflictAtomCandidates.size());

//		for (AtomicCoreCPA.Span candidate : conflictAtomCandidates) {
//			EList<Node> nodesOfCandidate = candidate.getGraph().getNodes();
//			assertEquals(1, nodesOfCandidate.size());
//		}

		Set<MinimalConflictReason> mCRs = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(pushDownGroupRule, pushDownGroupRulePreserve, candidate,
					mCRs);
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
		

		Set<InitialConflictReason> computedConflictReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		System.err.println("amount of computedConflictReason: "+computedConflictReason.size());
		
		// Map<AnzahlMCR, AnzahlCRs>
		Map<Integer, Integer> amoutOfMCRs = new HashMap<Integer, Integer>();
		// wie groß (= Anzahl Knoten) sind die InitialConflictReason? 
		for(InitialConflictReason cr : computedConflictReason){
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
		//TODO: Assert für die acht Confl. Reason die auf einem MCR basieren
		//TODO: Assert für die acht Confl. Reason die auf zwei MCR basieren (wieso 12 laut Ergebnis? - Kann ich gerade nicht nachvollziehen.)
			// 																(macht nur Sinn, wenn zwei MCRs verknüpft werden die nicht zusammenhängen)
												// TADA! Und siehe da(!): wenn dieser Fall ausgenommen wird, so kommt es genau zu den erwarteten Ergebnissen!
		//TODO: Assert für die acht Confl. Reason die auf drei MCR basieren
		//TODO: Assert für die zwei Confl. Reason die auf vier MCR basieren
	}
}
