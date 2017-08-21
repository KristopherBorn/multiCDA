package org.eclipse.emf.henshin.cpa.atomic.tasks;

import java.util.List;

import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.model.Rule;

public class SingleCpaTaskResultContainer {
	
	List<Rule> firstRuleList;
	List<Rule> secondRuleList; 
	CPAOptions normalOptions;
	
	CPAResult cpaResult;
	
	long analysisDuration;

	public SingleCpaTaskResultContainer(List<Rule> firstRuleList, List<Rule> secondRuleList, CPAOptions cpaOptions) {
		this.firstRuleList = firstRuleList;
		this.secondRuleList = secondRuleList;
		this.normalOptions = cpaOptions;
		// TODO Auto-generated constructor stub
	}

	public List<Rule> getFirstRuleList() {
		// TODO Auto-generated method stub
		return firstRuleList;
	}

	public List<Rule> getSecondRuleList() {
		// TODO Auto-generated method stub
		return secondRuleList;
	}

	public CPAOptions getCpaOptions() {
		// TODO Auto-generated method stub
		return normalOptions;
	}

//	public void addResult(CPAResult normalResult) {
//		this.cpaResult = normalResult;
//	}

	public CPAResult getResult() {
		return cpaResult;
	}

//	public void setCalculationTime(long conflictAtomRunTime) {
//		this.analysisDuration = conflictAtomRunTime;
//	}

	public long getAnalysisDuration() {
		return analysisDuration;
	}

	public void setResult(CPAResult cpaResult, long analysisDuration) {
		this.cpaResult = cpaResult;
		this.analysisDuration = analysisDuration;
	}

}
