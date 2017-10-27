package org.eclipse.emf.henshin.multicda.cda.eval.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.cpa.result.Conflict;
import org.eclipse.emf.henshin.cpa.result.ConflictKind;
import org.eclipse.emf.henshin.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.cpa.result.Dependency;
import org.eclipse.emf.henshin.cpa.result.DependencyKind;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.tasks.CalculateCpaTask;
import org.eclipse.emf.henshin.multicda.cda.tasks.SingleCpaTaskResultContainer;
import org.eclipse.emf.henshin.multicda.cda.tasks.CalculateCpaTask.AnalysisKind;

public class EssCPARunner {

	public static CPAResult runEssCPA(Logger essCpaLogger, List<Rule> skippedRules,
			Rule rule1, Rule rule2,  Rule originalRule2) {
		PrintStream original = System.out;
		System.setOut(new NullPrintStream());
		
		CPAResult result =  new CPAResult();
		boolean canceled = false;
		CPAOptions essentialOptions = new CPAOptions();
		essentialOptions.setEssential(true);
		essentialOptions.setIgnoreMultiplicities(true);
		essentialOptions.setComplete(true);;
		
		String runtime = "";
		String numberDeleteUse = "";
		
		long essentialStartTime = System.currentTimeMillis();
		List<Rule> firstRuleList = new LinkedList<Rule>();
		firstRuleList.add(rule1);
		List<Rule> secondRuleList = new LinkedList<Rule>();
		secondRuleList.add(rule2);
		CPAResult essentialResult = null;
		
		AnalysisKind analysisKind; 
		if(essCpaLogger.getLogData() == Logger.LogData.ESSENTIAL_DELTE_USE_CONFLICTS){
			analysisKind = CalculateCpaTask.AnalysisKind.CONFLICT;
		}else if(essCpaLogger.getLogData() == Logger.LogData.ESSENTIAL_PRODUCE_USE_DEPENDENCY){
			analysisKind = CalculateCpaTask.AnalysisKind.DEPENDENCY;
		}else {
			System.err.println("invalid configuration");
			return null;
		}
		
		SingleCpaTaskResultContainer singleCpaTaskResultContainer = new SingleCpaTaskResultContainer(firstRuleList, secondRuleList, essentialOptions);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			executor.submit(new CalculateCpaTask(singleCpaTaskResultContainer, analysisKind)).get(60, TimeUnit.MINUTES);
		} catch (NullPointerException | InterruptedException | ExecutionException e) {
			System.err.println("Timeout!");
			executor.shutdown();
		} catch (TimeoutException e) {
			canceled = true;
			skippedRules.add(rule1);
			skippedRules.add(rule2);
			System.err.println("Timeout!");
			e.printStackTrace();
		}
		long essentialEndTime = System.currentTimeMillis();
		
		executor.shutdown();
		essentialResult = singleCpaTaskResultContainer.getResult();
		
		long essentialRunTime = essentialEndTime - essentialStartTime;
		
		if (essentialResult != null) {
			runtime = String.valueOf(essentialRunTime);
			List<CriticalPair> filteredDeleteUseConflicts = filterCriticalPairs(essentialResult, analysisKind);
			numberDeleteUse = String.valueOf(filteredDeleteUseConflicts.size());
		} else {
			runtime = "TO";
			numberDeleteUse = "TO";
		}
		
		if(!canceled){			
			essCpaLogger.addData(rule1, originalRule2, runtime.toString(),
					numberDeleteUse.toString());
			for(CriticalPair cp : essentialResult.getCriticalPairs()){
				result.addResult(cp);
			}
		}
		System.setOut(original);
		return result;
	}
	
	// WICHTIG(!): 
	// filterd bei "AnalysisKind.CONFLICT" die delete-use-conflicts 
	// und bei "AnalysisKind.DEPENDENCIES" die produce-use-dependencies
	public static List<CriticalPair> filterCriticalPairs(CPAResult cpaResult, AnalysisKind analysisKind) {
		// filter delete-use conflicts:
		if (cpaResult != null) {
			List<CriticalPair> criticalPairs = cpaResult.getCriticalPairs();
			// System.out.println("number of essential CPs: "+criticalPairs.size());
			List<CriticalPair> filteredCriticalPairs = new LinkedList<CriticalPair>();
			for (CriticalPair cp : criticalPairs) {
				if (cp instanceof Conflict && analysisKind == AnalysisKind.CONFLICT) {
					if (((Conflict) cp).getConflictKind().equals(ConflictKind.DELETE_USE_CONFLICT)) {
						filteredCriticalPairs.add(cp);
					}
				}else if (cp instanceof Dependency && analysisKind == AnalysisKind.DEPENDENCY) {
						if (((Dependency) cp).getDependencyKind().equals(DependencyKind.PRODUCE_USE_DEPENDENCY)) {
							filteredCriticalPairs.add(cp);
						}
					}
			}
			return filteredCriticalPairs;
		} else {
			 throw new RuntimeException("Failure during essential CPA!");
		}
	}




}

 class NullPrintStream extends PrintStream {

	  public NullPrintStream() {
	    super(new NullByteArrayOutputStream());
	  }

	  private static class NullByteArrayOutputStream extends ByteArrayOutputStream {

	    @Override
	    public void write(int b) {
	      // do nothing
	    }

	    @Override
	    public void write(byte[] b, int off, int len) {
	      // do nothing
	    }

	    @Override
	    public void writeTo(OutputStream out) throws IOException {
	      // do nothing
	    }

	  }

	}