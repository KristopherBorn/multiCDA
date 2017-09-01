package org.eclipse.emf.henshin.cpa.atomic.eval;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.henshin.cpa.atomic.eval.util.EssCPARunner;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.Logger;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.RulePair;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.model.Rule;

public abstract class UmlEvalRunner extends EvalRunner {

	public UmlEvalRunner() {
		super();
	}

	@Override
	protected void doAggBasedCpa(List<Granularity> granularities, Type type, List<Rule> rules,
			List<RulePair> nonDeleting) {
		if (granularities.contains(Granularity.ess)) {
			logn("[AGG] Computing essential critical pairs (filtered):");
			for (Rule r1 : rules) {
				List<Integer> resultRow = new ArrayList<Integer>();
				essResults.add(resultRow);
				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					Logger deleteUseLogger = new Logger(Logger.LogData.ESSENTIAL_DELTE_USE_CONFLICTS, rules);
					CPAResult res = EssCPARunner.runEssCPA(deleteUseLogger, null, r1, r2.getCopy(), r2.getOriginal());
					log(res.getInitialCriticalPairs().size() + " ");
					tlog(System.currentTimeMillis() - time + " ");
					resultRow.add(res.getInitialCriticalPairs().size());
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}
		
		if (granularities.contains(Granularity.essUnfiltered)) {
			logn("[AGG] Computing essential critical pairs (unfiltered):");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					Logger deleteUseLogger = new Logger(Logger.LogData.ESSENTIAL_DELTE_USE_CONFLICTS, rules);
					CPAResult res = EssCPARunner.runEssCPA(deleteUseLogger, null, r1, r2.getCopy(), r2.getOriginal());
					log(res.getCriticalPairs().size() + " ");
					tlog(System.currentTimeMillis() - time + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}
	}
}