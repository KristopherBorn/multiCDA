package org.eclipse.emf.henshin.cpa.atomic.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.DependencyAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.MultiGranularAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.EssCPARunner;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.Logger;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.Logger.LogData;
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


		if (granularities.contains(Granularity.atoms)) {
			logn("[MultiCDA] Computing " + type.getSingularName() +" atoms:");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					MultiGranularAnalysis ca = getAnalysis(r1, r2.getCopy(), type);
					Set<Span> result = ca.computeAtoms();
					log(result.size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}
		
		if (granularities.contains(Granularity.binary)) {
			logn("[MultiCDA] Computing binary granularity:");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					MultiGranularAnalysis ca = getAnalysis(r1, r2.getCopy(), type);
					Span result = ca.computeResultsBinary();
					log(result == null ? "0 " : "1 ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}

		if (granularities.contains(Granularity.coarse)) {
			logn("[MultiCDA] Computing minimal "+type.getSingularName()+" reasons:");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					MultiGranularAnalysis ca = getAnalysis(r1, r2.getCopy(), type);
					Set<Span> result = ca.computeResultsCoarse();
					log(result.size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}

		if (granularities.contains(Granularity.fine)) {
			logn("[MultiCDA] Computing initial "+type.getSingularName()+" reasons:");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					MultiGranularAnalysis ca = getAnalysis(r1, r2.getCopy(), type);
					Set<Span> result = ca.computeResultsFine();
					log(result.size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}

		if (granularities.contains(Granularity.ess)) {
			logn("[AGG] Computing essential critical pairs (filtered):");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					LogData kind = (type == Type.conflicts)? Logger.LogData.ESSENTIAL_DELTE_USE_CONFLICTS :
						Logger.LogData.ESSENTIAL_PRODUCE_USE_DEPENDENCY;
					Logger deleteUseLogger = new Logger(kind, rules);
					CPAResult res = EssCPARunner.runEssCPA(deleteUseLogger, new ArrayList<Rule>(), r1, r2.getCopy(), r2.getOriginal());
					log(res.getInitialCriticalPairs().size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}
		
		if (granularities.contains(Granularity.essUnfiltered)) {
			logn("[AGG] Computing essential critical pairs (unfiltered):");
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					LogData kind = (type == Type.conflicts)? Logger.LogData.ESSENTIAL_DELTE_USE_CONFLICTS :
						Logger.LogData.ESSENTIAL_PRODUCE_USE_DEPENDENCY;
					Logger deleteUseLogger = new Logger(kind, rules);
					CPAResult res = EssCPARunner.runEssCPA(deleteUseLogger,  new ArrayList<Rule>(), r1, r2.getCopy(), r2.getOriginal());
					log(res.getCriticalPairs().size() + " ");
				}
				logn("   | "+r1.getName());
			}
			logn("");
		}

	}

	private MultiGranularAnalysis getAnalysis(Rule r1, Rule r2, Type type) {
		switch (type) {
		case conflicts: return new ConflictAnalysis(r1, r2);
		case dependencies: return new DependencyAnalysis(r1, r2);
		}
		return null;
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
