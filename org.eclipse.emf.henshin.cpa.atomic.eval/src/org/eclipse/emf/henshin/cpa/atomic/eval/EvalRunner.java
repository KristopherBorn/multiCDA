package org.eclipse.emf.henshin.cpa.atomic.eval;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.DependencyAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.MultiGranularAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.NonDeletingPreparator;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.RulePair;
import org.eclipse.emf.henshin.cpa.atomic.runner.RulePreparator;
import org.eclipse.emf.henshin.cpa.atomic.tester.CPATester;
import org.eclipse.emf.henshin.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.model.Rule;

public abstract class EvalRunner {
	String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
	String path= getDomainName() + "\\"+timeStamp+ ".log" ;
		
	protected List<List<Integer>> fineResults = new ArrayList<List<Integer>>();
	protected List<List<Integer>> essResults = new ArrayList<List<Integer>>();
	
	public void run(List<Granularity> granularities, Type type) {
		init();
		List<Rule> rules = getRules();
		prepareRules(rules);
		List<RulePair> nonDeleting = NonDeletingPreparator.prepareNonDeletingVersions(rules);
		initLogs();
		
		doMultiGranularCDA(granularities, type, rules, nonDeleting);
		doAggBasedCpa(granularities, type, rules, nonDeleting);
		
		if (granularities.contains(Granularity.fine) && granularities.contains(Granularity.ess))
			doComparison(rules);

	}

	protected void doMultiGranularCDA(List<Granularity> granularities, Type type, List<Rule> rules,
			List<RulePair> nonDeleting) {
		logbn("Starting CDA with " + rules.size() + " rules.");

		if (granularities.contains(Granularity.atoms)) {
			logn("[MultiCDA] Computing " + type.getSingularName() + " atoms:");
			long overallTime = 0L;
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					MultiGranularAnalysis ca = getAnalysis(r1, r2.getCopy(), type);
					Set<Span> result = ca.computeAtoms();
					time = System.currentTimeMillis() - time;
					overallTime += time;
					tlog(time + " ");
					log(result.size() + " ");
				}
				logbn("   | " + r1.getName());
			}
			logbn("");
		}

		if (granularities.contains(Granularity.binary)) {
			logn("[MultiCDA] Computing binary granularity:");
			long overallTime = 0L;
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					MultiGranularAnalysis ca = getAnalysis(r1, r2.getCopy(), type);
					Span result = ca.computeResultsBinary();
					time = System.currentTimeMillis() - time;
					overallTime += time;
					tlog(time + " ");
					log(result == null ? "0 " : "1 ");
				}
				logbn("   | " + r1.getName());
			}
			logbn("");
		}

		if (granularities.contains(Granularity.coarse)) {
			logn("[MultiCDA] Computing minimal " + type.getSingularName() + " reasons:");
			long overallTime = 0L;
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					MultiGranularAnalysis ca = getAnalysis(r1, r2.getCopy(), type);
					Set<Span> result = ca.computeResultsCoarse();
					log(result.size() + " ");
					time = System.currentTimeMillis() - time;
					overallTime += time;
					tlog(time + " ");
				}
				logbn("   | " + r1.getName());
			}
			logbn("");
		}

		if (granularities.contains(Granularity.fine)) {
			logn("[MultiCDA] Computing initial " + type.getSingularName() + " reasons:");
			long overallTime = 0L;
			for (Rule r1 : rules) {
				List<Integer> resultRow = new ArrayList<Integer>();
				fineResults.add(resultRow);
				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					MultiGranularAnalysis ca = getAnalysis(r1, r2.getCopy(), type);
					Set<Span> result = ca.computeResultsFine();
					time = System.currentTimeMillis() - time;
					overallTime += time;
					tlog(time + " ");
					log(result.size() + " ");
					resultRow.add(result.size());
				}
				logbn("   | " + r1.getName());
			}
			logbn("");
			tlogn("Overall time: " + (((double)overallTime)/1000.0) + " seconds");
			tlogn("");
		}
	}

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
					Set<CriticalPair> initspNormal = new HashSet<>();
					CPATester eTester = new CPATester(Collections.singletonList(r1),
							Collections.singletonList(r2.getCopy()), true, type == Type.dependencies, false, false,
							false, false, true);
					time = System.currentTimeMillis() - time;
					overallTime += time;
					tlog(time + " ");
					initspNormal.addAll(eTester.getInitialCriticalPairs());
					log(initspNormal.size() + " ");
					resultRow.add(initspNormal.size());

				}
				logbn("   | " + r1.getName());
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
					Set<CriticalPair> initspNormal = new HashSet<>();
					CPATester eTester = new CPATester(Collections.singletonList(r1),
							Collections.singletonList(r2.getCopy()), true, type == Type.dependencies, false, false,
							false, false, true);
					time = System.currentTimeMillis() - time;
					overallTime += time;
					tlog(time + " ");
					initspNormal.addAll(eTester.getCriticalPairs());
					log(initspNormal.size() + " ");
				}
				logbn("   | " + r1.getName());
			}
			logbn("");
			tlogn("Overall time: " + (((double)overallTime)/1000.0) + " seconds");
			tlogn("");
		}
		


		if (granularities.contains(Granularity.essSecondDeleting)) {
			logbn("[AGG] Computing essential critical pairs (filtered, second rule deleting):");
			long overallTime = 0L;
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					Set<CriticalPair> initspNormal = new HashSet<>();
					CPATester eTester = new CPATester(Collections.singletonList(r1),
							Collections.singletonList(r2.getOriginal()), true, type == Type.dependencies, false, false,
							false, false, true);
					initspNormal.addAll(eTester.getInitialCriticalPairs());
					log(initspNormal.size() + " ");
					time = System.currentTimeMillis() - time;
					overallTime += time;
					tlog(time + " ");
				}
				logbn("   | " + r1.getName());
			}
			logbn("");
			tlogn("Overall time: " + (((double)overallTime)/1000.0) + " seconds");
			tlogn("");
		}
		

		if (granularities.contains(Granularity.full)) {
			logbn("[AGG] Computing essential critical pairs (unfiltered):");
			long overallTime = 0L;
			for (Rule r1 : rules) {
				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					Set<CriticalPair> initspNormal = new HashSet<>();
					CPATester eTester = new CPATester(Collections.singletonList(r1),
							Collections.singletonList(r2.getCopy()), false, type == Type.conflicts, false, false,
							false, false, true);
					time = System.currentTimeMillis() - time;
					overallTime += time;
					tlog(time + " ");
					initspNormal.addAll(eTester.getCriticalPairs());
					log(initspNormal.size() + " ");
				}
				logbn("   | " + r1.getName());
			}
			logbn("");
			tlogn("Overall time: " + (((double)overallTime)/1000.0) + " seconds");
			tlogn("");
		}
	}

	protected void initLogs() {
		try {
			Files.write(Paths.get( "logs\\time\\"+path), new String().getBytes(), StandardOpenOption.CREATE_NEW);
			Files.write(Paths.get( "logs\\results\\"+path), new String().getBytes(), StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void logbn(String string) {
		log(string+"\n");
		tlog(string+"\n");
	}

	protected void log(String string) {
		System.out.print(string);
		try {
			Files.write(Paths.get( "logs\\results\\"+path), string.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void logn(String string) {
		log(string+ "\n");
	}


	protected void tlog(String string) {
			try {
				Files.write(Paths.get( "logs\\time\\"+path), string.getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	protected void tlogn(String string) {
		tlog(string+ "\n");
	}

	private MultiGranularAnalysis getAnalysis(Rule r1, Rule r2, Type type) {
		switch (type) {
		case conflicts:
			return new ConflictAnalysis(r1, r2);
		case dependencies:
			return new DependencyAnalysis(r1, r2);
		}
		return null;
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

	public abstract String getDomainName();

	private void doComparison(List<Rule> rules) {
		logn("");
		logn("Result comparison:");
		for (int i=0; i<fineResults.size(); i++) {
			for (int j=0; j<fineResults.size(); j++) {
				int comp = fineResults.get(i).get(j) - essResults.get(i).get(j); 
				if (comp > 0) 
					log("+"+" ");
				else if (comp < 0) 
					log("!"+" ");
				else
					log("_"+" ");
			}
			logn("    | " +rules.get(i));
		}
	}
	
}
