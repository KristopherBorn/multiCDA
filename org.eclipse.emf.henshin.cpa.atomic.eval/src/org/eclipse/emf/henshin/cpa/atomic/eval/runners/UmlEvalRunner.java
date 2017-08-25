package org.eclipse.emf.henshin.cpa.atomic.eval.runners;

import java.util.List;

import org.eclipse.emf.henshin.cpa.atomic.eval.EvalRunner;
import org.eclipse.emf.henshin.cpa.atomic.eval.Granularity;
import org.eclipse.emf.henshin.cpa.atomic.eval.Type;
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
				for (RulePair r2 : nonDeleting) {
					Logger deleteUseLogger = new Logger(Logger.LogData.ESSENTIAL_DELTE_USE_CONFLICTS, rules);
					CPAResult res = EssCPARunner.runEssCPA(deleteUseLogger, null, r1, r2.getCopy(), r2.getOriginal());
					log(res.getCriticalPairs().size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}
		
		if (granularities.contains(Granularity.essUnfiltered)) {
			logn("[AGG] Computing essential critical pairs (unfiltered):");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					Logger deleteUseLogger = new Logger(Logger.LogData.ESSENTIAL_DELTE_USE_CONFLICTS, rules);
					CPAResult res = EssCPARunner.runEssCPA(deleteUseLogger, null, r1, r2.getCopy(), r2.getOriginal());
					log(res.getCriticalPairs().size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}
	}
}