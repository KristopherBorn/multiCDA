package org.eclipse.emf.henshin.multicda.cda.eval.runners.overapproxrunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.DependencyAnalysis;
import org.eclipse.emf.henshin.multicda.cda.MultiGranularAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.eval.Granularity;
import org.eclipse.emf.henshin.multicda.cda.eval.Type;
import org.eclipse.emf.henshin.multicda.cda.eval.util.NonDeletingPreparator;
import org.eclipse.emf.henshin.multicda.cda.eval.util.RulePair;
import org.eclipse.emf.henshin.multicda.cda.runner.RulePreparator;
import org.eclipse.emf.henshin.multicda.cda.tester.CPATester;
import org.eclipse.emf.henshin.multicda.cda.tester.Tester.Options;
import org.eclipse.emf.henshin.multicda.cpa.result.CriticalPair;

public abstract class OverapproxEvalRunner {
	String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
	String path= getDomainName() + "\\"+timeStamp+ ".log" ;

	protected List<List<Integer>> essNonDeletingResults = new ArrayList<List<Integer>>();
	protected List<List<Integer>> essDeletingResults = new ArrayList<List<Integer>>();

	public void run(Type type) {
		init();
		List<Rule> rules = getRules();
		prepareRules(rules);
		List<RulePair> nonDeleting = NonDeletingPreparator.prepareNonDeletingVersions(rules);
		initLogs();
	
		doAggBasedCpa( type, rules, nonDeleting);

	}
	
	protected void doAggBasedCpa(Type type, List<Rule> rules,
			List<RulePair> nonDeleting) {
			logbn("[AGG] Computing essential critical pairs (R2 deleting):");
			for (Rule r1 : rules) {
				List<Integer> resultRow = new ArrayList<Integer>();
				essDeletingResults.add(resultRow);

				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					Set<CriticalPair> initspNormal = new HashSet<>();
					Options options = new Options(type == Type.dependencies, true, false, false,
							false, false, true);
					CPATester eTester = new CPATester(Collections.singletonList(r1),
							Collections.singletonList(r2.getOriginal()), options);
					initspNormal.addAll(eTester.getCriticalPairs());
					log(initspNormal.size() + " ");
					tlog(System.currentTimeMillis() - time + " ");
					resultRow.add(initspNormal.size());

				}
				logbn("   | " + r1.getName());
			}
			logbn("");

			logbn("[AGG] Computing essential critical pairs (R2 non-deleting):");
			for (Rule r1 : rules) {
				List<Integer> resultRow = new ArrayList<Integer>();
				essNonDeletingResults.add(resultRow);
				for (RulePair r2 : nonDeleting) {
					long time = System.currentTimeMillis();
					Set<CriticalPair> initspNormal = new HashSet<>();
					Options options = new Options(type == Type.dependencies, true, false, false,
							false, false, true);
					CPATester eTester = new CPATester(Collections.singletonList(r1),
							Collections.singletonList(r2.getCopy()), options);
					initspNormal.addAll(eTester.getCriticalPairs());
					log(initspNormal.size() + " ");
					tlog(System.currentTimeMillis() - time + " ");
					resultRow.add(initspNormal.size());
				}
				logbn("   | " + r1.getName());
			}
			logbn("");
			
			doComparison(rules);
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
	

	protected void logb(String string) {
		log(string);
		tlog(string);
	}

	protected void tlog(String string) {
			try {
				Files.write(Paths.get( "logs\\time\\"+path), string.getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		int correct = 0;
		int incorrect = 0;
		logbn("");
		for (int i=0; i<essNonDeletingResults.size(); i++) {
			for (int j=0; j<essDeletingResults.size(); j++) {
				int comp = essDeletingResults.get(i).get(j) - essNonDeletingResults.get(i).get(j); 
				if (comp > 0) {
					logb("+"+" "); 
					incorrect++;
				}
				else if (comp < 0)  {
					logb("!"+" ");
					incorrect++;
				}
				else {
					logb("_"+" ");
					correct++;
					}
			}
			logbn("    | " +rules.get(i));
		}
		logbn("");
		logbn("Intestigated "+(correct+incorrect)+" rule pairs, "+correct+" correct. Precision = "+((double) correct/(double) (correct+incorrect)));
	}
}
