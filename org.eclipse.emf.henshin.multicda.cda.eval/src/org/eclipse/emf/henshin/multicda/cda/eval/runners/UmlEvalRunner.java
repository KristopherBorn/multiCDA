package org.eclipse.emf.henshin.multicda.cda.eval.runners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.eval.EvalRunner;
import org.eclipse.emf.henshin.multicda.cda.eval.Granularity;
import org.eclipse.emf.henshin.multicda.cda.eval.Type;
import org.eclipse.emf.henshin.multicda.cda.eval.util.EssCPARunner;
import org.eclipse.emf.henshin.multicda.cda.eval.util.Logger;
import org.eclipse.emf.henshin.multicda.cda.eval.util.RulePair;
import org.eclipse.emf.henshin.multicda.cpa.result.CPAResult;

public abstract class UmlEvalRunner extends EvalRunner {

	public UmlEvalRunner() {
		super();
	}

	@Override
	protected void doAggBasedCpa(List<Granularity> granularities, Type type, List<Rule> rules,
			List<RulePair> nonDeleting) {
		if (granularities.contains(Granularity.ess)) {
			logn("[AGG] Computing essential critical pairs (filtered):");
			long overallTime = 0L;
			for (Rule r1 : rules) {
				List<Integer> resultRow = new ArrayList<Integer>();
				essResults.add(resultRow);
				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					Logger deleteUseLogger = new Logger(Logger.LogData.ESSENTIAL_DELTE_USE_CONFLICTS, rules);
					CPAResult res = EssCPARunner.runEssCPA(deleteUseLogger, null, r1, r2.getCopy(), r2.getOriginal());
					time = System.currentTimeMillis() - time;
					overallTime += time;
					tlog(time + " ");
					log(res.getCriticalPairs().size() + " ");
					resultRow.add(res.getCriticalPairs().size());
				}
				logn("   | "+r1.getName());
			}
			logbn("");
			tlogn("Overall time: " + (((double)overallTime)/1000.0) + " seconds");
			tlogn("");
		}
		
		if (granularities.contains(Granularity.essUnfiltered)) {
			logn("[AGG] Computing essential critical pairs (unfiltered):");
			long overallTime = 0L;
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					Logger deleteUseLogger = new Logger(Logger.LogData.ESSENTIAL_DELTE_USE_CONFLICTS, rules);
					CPAResult res = EssCPARunner.runEssCPA(deleteUseLogger, null, r1, r2.getCopy(), r2.getOriginal());
					time = System.currentTimeMillis() - time;
					overallTime += time;
					tlog(time + " ");
					log(res.getEssentialCriticalPairs().size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logbn("");
			tlogn("Overall time: " + (((double)overallTime)/1000.0) + " seconds");
			tlogn("");
		}
	}
}