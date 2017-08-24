package org.eclipse.emf.henshin.cpa.atomic.unitTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Test;

import org.junit.Assert;

public class SimpleTest {

	/** 
	 * Relative path to the test model files.
	 */
	public static final String PATH = "testData/simpleTest";

	@Test
	public void deleteA_useAwithB_danglingEdgeTest() {

		// Create a resource set with a base directory:
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		
		// Load the module:
		Module module = resourceSet.getModule("simpleTgRules.henshin", false);

		Unit deleteAUnit = module.getUnit("deleteA");
		Rule deleteARule = (Rule) deleteAUnit;

		Unit useAwithBUnit = module.getUnit("useAwithB");
		Rule useAwithBRule = (Rule) useAwithBUnit;
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(deleteARule,
				useAwithBRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		// should be 0 due to dangling edge condition. First rule deletes a node and second rule requires to have an edge on that node!
		Assert.assertEquals(0, computedConflictAtoms.size());
//		System.out.println("number of conflict atoms: "+computedConflictAtoms.size());
//		for(ConflictAtom conflictAtom : computedConflictAtoms){
//			System.out.println(conflictAtom);
//		}
		
		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(deleteARule,
				useAwithBRule);
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(deleteARule, useAwithBRule, candidate,
					reasons);
		}
		// should be 0 due to dangling edge condition. First rule deletes a node and second rule requires to have an edge on that node!
		Assert.assertEquals(0, reasons.size());
		
//		Set<Span> minimalConflictReasons = reasons;
//		System.out.println("number of minimal conflict reasons: "+minimalConflictReasons.size());
//		for(Span minimalConflictReason : minimalConflictReasons){
//			System.out.println(minimalConflictReason);
//		}
	}
	
	@Test
	public void deleteA_useA() {

		// Create a resource set with a base directory:
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		
		// Load the module:
		Module module = resourceSet.getModule("simpleTgRules.henshin", false);

		Unit deleteAUnit = module.getUnit("deleteA");
		Rule deleteARule = (Rule) deleteAUnit;

		Unit useAwithBUnit = module.getUnit("useA");
		Rule useAwithBRule = (Rule) useAwithBUnit;
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(deleteARule,
				useAwithBRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		Assert.assertEquals(1, computedConflictAtoms.size());
		System.out.println("number of conflict atoms: "+computedConflictAtoms.size());
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			System.out.println(conflictAtom);
		}
		
		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(deleteARule,
				useAwithBRule);
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(deleteARule, useAwithBRule, candidate,
					reasons);
		}
		Assert.assertEquals(1, reasons.size());
		
		Set<MinimalConflictReason> minimalConflictReasons = reasons;
		System.out.println("number of minimal conflict reasons: "+minimalConflictReasons.size());
		for(Span minimalConflictReason : minimalConflictReasons){
			System.out.println(minimalConflictReason);
		}
	}
	
	@Test
	public void deleteA_withContainer_deleteA() {

		// Create a resource set with a base directory:
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		
		// Load the module:
		Module module = resourceSet.getModule("simpleTgRules.henshin", false);

		Unit deleteAUnit = module.getUnit("deleteA_withContainer");
		Rule deleteARule = (Rule) deleteAUnit;

		Unit useAwithBUnit = module.getUnit("deleteA");
		Rule useAwithBRule = (Rule) useAwithBUnit;
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(deleteARule,
				useAwithBRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		Assert.assertEquals(1, computedConflictAtoms.size());
		System.out.println("number of conflict atoms: "+computedConflictAtoms.size());
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			System.out.println(conflictAtom);
		}
		
		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(deleteARule,
				useAwithBRule);
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(deleteARule, useAwithBRule, candidate,
					reasons);
		}
		Assert.assertEquals(1, reasons.size());
		
		Set<MinimalConflictReason> minimalConflictReasons = reasons;
		System.out.println("number of minimal conflict reasons: "+minimalConflictReasons.size());
		for(Span minimalConflictReason : minimalConflictReasons){
			System.out.println(minimalConflictReason);
		}
		
		//TODO: hier den Ansatz zum identifizieren von falschen "over-approximation" Ergebnissen hinzufügen!
		//TODO: create a Class for that functionality
//		Set<Span> minimalConflictReasonsWithoutOverapproximation = removeOverapproximation(minimalConflictReasons);
	}
	
	
	@Test
	public void deleteA_withContainer_deleteAfromB() {

		// Create a resource set with a base directory:
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		
		// Load the module:
		Module module = resourceSet.getModule("simpleTgRules.henshin", false);

		Unit deleteAUnit = module.getUnit("deleteA_withContainer");
		Rule deleteARule = (Rule) deleteAUnit;

		Unit useAwithBUnit = module.getUnit("deleteAfromB");
		Rule useAwithBRule = (Rule) useAwithBUnit;
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(deleteARule,
				useAwithBRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
//		Assert.assertEquals(1, computedConflictAtoms.size());
		System.out.println("number of conflict atoms: "+computedConflictAtoms.size());
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			System.out.println(conflictAtom);
		}
		
		List<Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(deleteARule,
				useAwithBRule);
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(deleteARule, useAwithBRule, candidate,
					reasons);
		}
		Assert.assertEquals(1, reasons.size());
		
		Set<MinimalConflictReason> minimalConflictReasons = reasons;
		System.out.println("number of minimal conflict reasons: "+minimalConflictReasons.size());
		for(Span minimalConflictReason : minimalConflictReasons){
			System.out.println(minimalConflictReason);
		}
		
		//TODO: wieso kommt es hier nicht so dem over-approximation Fehler? Welchen unterschied macht das größere LHS Muster der zweiten Regel???
	}

}
