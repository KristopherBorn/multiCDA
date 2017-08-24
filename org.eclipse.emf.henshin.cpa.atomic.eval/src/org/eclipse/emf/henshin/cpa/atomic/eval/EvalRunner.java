package org.eclipse.emf.henshin.cpa.atomic.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.EssCPARunner;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.Logger;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.NonDeletingPreparator;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.RulePair;
import org.eclipse.emf.henshin.cpa.atomic.runner.RulePreparator;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.model.Rule;

public abstract class EvalRunner {

	public void run(List<Granularity> granularities, Type type) {
		init();
		List<Rule> rules = getRules();
		prepareRules(rules);
		List<RulePair> nonDeleting = NonDeletingPreparator.prepareNonDeletingVersions(rules);

		logn("Starting CDA with " + rules.size() + " rules.");


		if (granularities.contains(Granularity.atoms) && type == Type.conflicts) {
			for (Rule r1 : rules) {
				logn("[MultiCDA] Computing conflict atoms:");
				for (RulePair r2 : nonDeleting) {
					ConflictAnalysis ca = new ConflictAnalysis(r1, r2.getCopy());
					List<ConflictAtom> result = ca.computeConflictAtoms();
					log(result.size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}
		
		if (granularities.contains(Granularity.binary) && type == Type.conflicts) {
			logn("[MultiCDA] Computing binary granularity:");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					ConflictAnalysis ca = new ConflictAnalysis(r1, r2.getCopy());
					ConflictAtom result = ca.hasConflicts();
					log(result == null ? "0 " : "1 ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}

		if (granularities.contains(Granularity.coarse) && type == Type.conflicts) {
			logn("[MultiCDA] Computing minimal conflict reasons:");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					ConflictAnalysis ca = new ConflictAnalysis(r1, r2.getCopy());
					Set<MinimalConflictReason> result = ca.computeMinimalConflictReasons();
					log(result.size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}

		if (granularities.contains(Granularity.fine) && type == Type.conflicts) {
			logn("[MultiCDA] Computing initial conflict reasons:");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					ConflictAnalysis ca = new ConflictAnalysis(r1, r2.getCopy());
					Set<InitialReason> result = ca.computeInitialReasons();
					log(result.size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}

		if (granularities.contains(Granularity.ess) && type == Type.conflicts) {
			logn("[AGG] Computing essential critical pairs (filtered):");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					Logger deleteUseLogger = new Logger(Logger.LogData.ESSENTIAL_DELTE_USE_CONFLICTS, rules);
					CPAResult res = EssCPARunner.runEssCPA(deleteUseLogger, null, r1, r2.getCopy(), r2.getOriginal());
					log(res.getInitialCriticalPairs().size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}
		
		if (granularities.contains(Granularity.essUnfiltered) && type == Type.conflicts) {
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

	private void log(String string) {
		System.out.print(string);
	}

	private void logn(String string) {
		System.out.println(string);
	}

	private static void prepareRules(List<Rule> rules) {
		List<Rule> prepared = new ArrayList<Rule>();
		rules.removeAll(rules.stream().filter(r -> !r.getMultiRules().isEmpty()).collect(Collectors.toList()));
		rules.forEach(r -> prepared.add(RulePreparator.prepareRule(r)));
		rules.clear();
		rules.addAll(prepared);
	}

	public abstract void init();

	public abstract List<Rule> getRules();

}
