package org.eclipse.emf.henshin.multicda.cda.tasks;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;

public class CalculateAtomicCpaTask implements Callable<List<ConflictAtom>> {

	Rule firstRule;
	Rule secondRule; 
	
	long conflictAtomRunTime;
	
	AtomicResultContainer resultKeeper;
	
	public CalculateAtomicCpaTask(AtomicResultContainer resultKeeper) {
		
		this.resultKeeper = resultKeeper;
		
		this.firstRule = resultKeeper.getFirstRule();
		this.secondRule = resultKeeper.getSecondRule();
	}

	@Override
	public List<ConflictAtom> call() throws Exception {
//		System.out.println("CALLL!");

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(firstRule, secondRule);

		List<ConflictAtom> computeConflictAtoms = new LinkedList<ConflictAtom>();

		long normalStartTime = System.currentTimeMillis();
		
		try {
			computeConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		} catch (NullPointerException  e) {
			System.err.println("Timeout!");
		}
		
		long normalEndTime = System.currentTimeMillis();
		conflictAtomRunTime = normalEndTime - normalStartTime;
		
		resultKeeper.addResult(computeConflictAtoms);
		resultKeeper.setCalculationTime(conflictAtomRunTime);
		resultKeeper.setCandidates(atomicCoreCPA.getCandidates());
		resultKeeper.setMinimalConflictReasons(atomicCoreCPA.getMinimalConflictReasons());
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span conflictReason : atomicCoreCPA.getMinimalConflictReasons()){
			minimalConflictReasons.add(new MinimalConflictReason(conflictReason));
		}
		long conflictReasonStartTime = System.currentTimeMillis();
		Set<ConflictReason> initialReasons = atomicCoreCPA.computeConflictReasons();
		
		Set<ConflictReason> filteredConflictReasons = new HashSet<ConflictReason>(); 
		for(ConflictReason initialReason : initialReasons){
			Set<Mapping> mappingsInRule1 = initialReason.getMappingsInRule1();
			Set<Mapping> mappingsInRule2 = initialReason.getMappingsInRule2();
//			List<Edge> danglingEdges = atomicCoreCPA.findDanglingEdgesByLHSOfRule2(mappingsInRule1, secondRule, mappingsInRule2);
//			if(danglingEdges.size() == 0){
//				filteredConflictReasons.add(initialReason);
//			}
		}

		long conflictReasonEndTime = System.currentTimeMillis();
		long conflictReasonAdditionalRunTime = conflictReasonEndTime - conflictReasonStartTime;
		long conflictReasonOverallRuneTime = conflictAtomRunTime + conflictReasonAdditionalRunTime;
		
		resultKeeper.setConflictReasonOverallTime(conflictReasonOverallRuneTime);
		resultKeeper.setConflictReasons(filteredConflictReasons);
		
		return computeConflictAtoms;
	}

}
