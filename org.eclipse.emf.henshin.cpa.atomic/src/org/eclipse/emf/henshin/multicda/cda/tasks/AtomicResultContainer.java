package org.eclipse.emf.henshin.multicda.cda.tasks;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;

public class AtomicResultContainer {
	
	Rule firstRule;
	Rule secondRule; 



	List<ConflictAtom> atomicCoreCpaConflictAtoms;
	List<Span> atomicCoreCpaCandidates;
	Set<MinimalConflictReason> atomicCoreCpaMinimalConflictsReasons;
	
	
	long minimalConflictReasonRunTime;
	private long conflictReasonOverallRuneTime;
	private Set<InitialReason> initialReasons;

	public AtomicResultContainer(Rule firstRule, Rule secondRule) {
		this.firstRule = firstRule;
		this.secondRule = secondRule;
		// TODO Auto-generated constructor stub
	}


	public Rule getFirstRule() {
		// TODO Auto-generated method stub
		return firstRule;
	}

	public Rule getSecondRule() {
		// TODO Auto-generated method stub
		return secondRule;
	}


	public void setCalculationTime(long normalRunTime) {
		this.minimalConflictReasonRunTime = normalRunTime;
	}

//	public long getNormalRunTime() {
//		return minimalConflictReasonRunTime;
//	}


	public List<ConflictAtom> getConflictAtoms() {
		if(atomicCoreCpaConflictAtoms == null){
			return new LinkedList<ConflictAtom>();
		}else {			
			return atomicCoreCpaConflictAtoms;
		}
	}


	public List<Span> getCandidates() { //TODO: is this a valid strategy to prevent NPEs based on returned "null"?
		if(atomicCoreCpaCandidates == null){
			return new LinkedList<Span>();
		}else {			
			return atomicCoreCpaCandidates;
		}
	}
	

	public void setCandidates(List<Span> atomicCoreCpaCandidates) {
		this.atomicCoreCpaCandidates = atomicCoreCpaCandidates;;
	}


	public Set<MinimalConflictReason> getMinimalConflictReasons() { //TODO: is this a valid strategy to prevent NPEs based on returned "null"?
		if(atomicCoreCpaMinimalConflictsReasons == null){
			return new HashSet<MinimalConflictReason>();
		}else {			
			return atomicCoreCpaMinimalConflictsReasons;
		}
	}


	public void addResult(List<ConflictAtom> atomicCoreCpaConflictAtoms) {
		this.atomicCoreCpaConflictAtoms = atomicCoreCpaConflictAtoms;
	}


	public void setMinimalConflictReasons(Set<MinimalConflictReason> overallReasons) {
		this.atomicCoreCpaMinimalConflictsReasons = overallReasons;
	}


	public void setConflictReasonOverallTime(long conflictReasonOverallRuneTime) {
		this.conflictReasonOverallRuneTime = conflictReasonOverallRuneTime;
	}


	public void setConflictReasons(Set<InitialReason> initialReasons) {
		this.initialReasons = initialReasons;
	}


	public Set<InitialReason> getConflictReasons() {
		if(initialReasons == null){
			return new HashSet<InitialReason>();
		}else {			
			return initialReasons;
		}
	}


	public long getRunTimeOfMinimalConflictReasons() {
		return minimalConflictReasonRunTime;
	}


	public long getConflictReasonOverallTime() {
		return conflictReasonOverallRuneTime;
	}

}
