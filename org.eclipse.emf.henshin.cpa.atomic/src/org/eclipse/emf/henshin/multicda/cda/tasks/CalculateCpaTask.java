package org.eclipse.emf.henshin.multicda.cda.tasks;

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.CpaByAGG;
import org.eclipse.emf.henshin.cpa.ICriticalPairAnalysis;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.model.Rule;

public class CalculateCpaTask implements Callable<CPAResult> {
	
	public enum AnalysisKind{
		CONFLICT,
		DEPENDENCY
	}

	List<Rule> firstRuleList;
	List<Rule> secondRuleList; 
	CPAOptions cpaOptions;
	
	AnalysisKind analysisKind;
	
	ICriticalPairAnalysis criticalPairAnalysis;
	
	long normalRunTime;
	
	SingleCpaTaskResultContainer taskResultContainer;
	
	public CalculateCpaTask(SingleCpaTaskResultContainer taskResultContainer, AnalysisKind analysisKind) {
		this.taskResultContainer = taskResultContainer;
		this.analysisKind = analysisKind;
		
		this.firstRuleList = taskResultContainer.getFirstRuleList();
		this.secondRuleList = taskResultContainer.getSecondRuleList();
		this.cpaOptions = taskResultContainer.getCpaOptions();	
		

		// normal CPA setup
		criticalPairAnalysis = new CpaByAGG();
	}

	@Override
	public CPAResult call() throws Exception {
//		System.out.println("CALLL!");

		CPAResult cpaResult = null;

		long normalStartTime = System.currentTimeMillis();
		try {
			criticalPairAnalysis.init(firstRuleList, secondRuleList, cpaOptions);
			if(analysisKind == AnalysisKind.CONFLICT){
				cpaResult = criticalPairAnalysis.runConflictAnalysis();
			}else {

				cpaResult = criticalPairAnalysis.runDependencyAnalysis();
			}	
		} catch (Exception /*UnsupportedRuleException*/ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long cpaEndTime = System.currentTimeMillis();
		normalRunTime = cpaEndTime - normalStartTime;
		
		//TODO: Fehlerbehandlung für den Fall einer Exception einführen! 
		taskResultContainer.setResult(cpaResult, normalRunTime);
		
		return cpaResult;
	}

}
