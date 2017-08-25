package org.eclipse.emf.henshin.cpa.atomic.tester;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.CpaByAGG;
import org.eclipse.emf.henshin.cpa.UnsupportedRuleException;
import org.eclipse.emf.henshin.cpa.atomic.runner.RulePreparator;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.CP;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Conditions;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.CriticalConditions;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.CriticalRightConditions;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Edge;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.ICP;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Node;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.cpa.result.CriticalElement;
import org.eclipse.emf.henshin.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.preprocessing.NonDeletingPreparator;

public class CPATester extends Tester {
	private CpaByAGG cpa;
	private CPAResult result;
	private String checked = "";
	private int checkedCounter = 0;
	private int checkedRightCounter = 0;
	private Options options;

	public CPATester(String henshin, String[] rules, boolean... options){
		this(henshin, rules, rules, options);
	}

	public CPATester(List<Rule> rules, boolean... options){
		this(rules, rules, options);
	}
	
	/**
	 * 
	 * @param henshin
	 * @param options 1:essential, 2:dependency, 3:prepare, 4:noneDeletionSecondRule, 5:printHeader, 6:printResults
	 */
	public CPATester(String henshin, boolean... options){
		this(henshin, new String[]{}, new String[]{}, options);
	}

	/**
	 * 
	 * @param first
	 * @param second
	 * @param options 1:essential, 2:dependency, 3:prepare, 4:noneDeletionSecondRule, 5:printHeader, 6:printResults
	 */
	public CPATester(List<Rule> first, List<Rule> second, boolean... options) {
		init(first, second, options);
	}

	/**
	 * 
	 * @param henshin
	 * @param first
	 * @param second
	 * @param options 1:essential, 2:dependency, 3:prepare, 4:noneDeletionSecondRule, 5:printHeader, 6:printResults
	 */
	public CPATester(String henshin, String[] first, String[] second, boolean... options) {

		String r = henshin.substring(0, henshin.lastIndexOf("/") + 1);
		String m = henshin.substring(henshin.lastIndexOf("/") + 1, henshin.length());

		HenshinResourceSet resourceSet = new HenshinResourceSet(r);
		Module module = resourceSet.getModule(m, false);

		List<Rule> f = new ArrayList<Rule>();
		List<Rule> s = new ArrayList<Rule>();
		for (String firstRule : first) {
			f.add((Rule) module.getUnit(firstRule));
		}
		for (String secondRule : second) {
			s.add((Rule) module.getUnit(secondRule));
		}
		if (f.isEmpty())
			f = new ArrayList<Rule>(s);
		if (s.isEmpty())
			s = new ArrayList<Rule>(f);
		if (f.isEmpty() && s.isEmpty())
			for (Unit u : module.getUnits())
				if (u instanceof Rule) {
					s.add((Rule) u);
					f.add((Rule) u);
				}
		init(f, s, options);
	}

	private void init(List<Rule> first, List<Rule> second, boolean... opt) {
		options = new Options(opt);
		String ff = "", ss = "";
		for (Rule nameF : first)
			ff += (ff.isEmpty() ? "" : ", ") + nameF.getName();
		for (Rule nameS : second)
			ss += (ss.isEmpty() ? "" : ", ") + nameS.getName();
		ff = ff.isEmpty() ? "All" : ff;
		ss = ss.isEmpty() ? "All" : ss;
		if (options.printHeader)
			System.out.println(
					"\n\t\t  " + ff + " --> " + ss + "\n\t\t\tCPA " + (this.options.essential ? "Essential" : ""));

		CPAOptions o = new CPAOptions();
		o.setEssential(this.options.essential);
		o.setReduceSameRuleAndSameMatch(false);
		o.setIgnoreSameRules(false);
		o.setIgnoreMultiplicities(true);

		cpa = new CpaByAGG();
		NAME = "CPA Tester";
		try {

			if (options.prepare) {
				List<Rule> r1 = RulePreparator.prepareRule(first);
				first = r1;
				List<Rule> r2 = RulePreparator.prepareRule(second);
				second = r2;
			}
			if(options.noneDeletionSecondRule)
				second = NonDeletingPreparator.prepareNoneDeletingsVersionsRules(second);
			cpa.init(first, second, o);
		} catch (UnsupportedRuleException e) {
			System.err.println(e.getMessage());
		}
		if (options.dependency)
			result = cpa.runDependencyAnalysis();
		else
			result = cpa.runConflictAnalysis();

		if (options.printResult) {
			printResult();
			print();
		}
	}

	@Override
	public boolean check(Class<?> type, Condition... conditions) {
		List<Condition> edgeNode = new ArrayList<>();
		for (Condition condition : conditions) {
			if (condition instanceof CP) {
				if (!condition.proove(result.getCriticalPairs().size()))
					return false;
				print(condition + " accepted");
			} else if (condition instanceof Edge || condition instanceof Node)
				edgeNode.add(condition);
			else if (condition instanceof ICP && !condition.proove(result.getInitialCriticalPairs().size()))
				return false;
			else
				System.err.println("This condition don't belong here --> " + condition);
		}
		if (edgeNode.size() == 0)
			return true;

		if (type == Conditions.class || type == CriticalConditions.class) {
			for (CriticalPair criticalPair : result) {
				Set<GraphElement> elements = new HashSet<>();
				for (CriticalElement e : criticalPair.getCriticalElements()) {
					elements.add(e.elementInFirstRule);
					elements.add(e.elementInSecondRule);
				}

				// System.out.println(elements);
				if (!checked.contains("Initial" + elements + "") && checkReasons(elements, edgeNode.toArray())) {
					print("Found: " + elements + "\twith " + type.getSimpleName() + " " + getContent(conditions));
					checked += "Initial" + elements + "\n";
					checkedCounter++;
					return true;
				}
			}
		}
		if (type == Conditions.class || type == CriticalRightConditions.class) {
			for (CriticalPair criticalPair : result) {
				Set<GraphElement> elements = new HashSet<>();
				for (CriticalElement e : criticalPair.getCriticalElements()) {
					elements.add(e.elementInSecondRule);
					elements.add(e.elementInFirstRule);
				}

				// System.out.println(elements);
				if (!checked.contains("Minimal" + elements + "") && checkReasons(elements, edgeNode.toArray())) {
					print("Found Right: " + elements + "\twith " + type.getSimpleName() + " " + getContent(conditions));
					checked += "Minimal" + elements + "\n";
					checkedRightCounter++;
					return true;
				}
			}
		}
		return false;
	}

	public Set<CriticalPair> getCriticalPairs() {
		Set<CriticalPair> r = new HashSet<CriticalPair>();
		for (CriticalPair cp : result.getCriticalPairs())
			r.add(cp);
		return r;
	}

	public Set<CriticalPair> getInitialCriticalPairs() {
		Set<CriticalPair> r = new HashSet<CriticalPair>();
		for (CriticalPair cp : result.getInitialCriticalPairs())
			r.add(cp);
		return r;
	}

	@Override
	public String toString() {
		if(!options.dependency)
			return getInitialCriticalPairs().size() + " Initial Critical Pairs. " + getCriticalPairs().size() + " Critical Pairs";
		else
			return getInitialCriticalPairs().size() + " Initial Dependency Critical Pairs. " + getCriticalPairs().size() + " Dependency Critical Pairs";
	}

	public static void printCP(Set<CriticalPair> cp) {

		for (CriticalPair criticalPair : cp) {
			String r = "";
			for (CriticalElement e : criticalPair.getCriticalElements())
				r += ", [" + e.elementInFirstRule + "] --> [" + e.elementInSecondRule + "]";
			System.out.println("CP: " + r.substring(2));
		}
	}

	private void printResult() {
		for (CriticalPair criticalPair : result) {
			String r = "";
			for (CriticalElement e : criticalPair.getCriticalElements())
				r += ", [" + e.elementInFirstRule + "] --> [" + e.elementInSecondRule + "]";
			print("CP: " + r.substring(2));
		}
	}

	private static class Options {
		public boolean essential;
		public boolean dependency;
		public boolean prepare;
		public boolean noneDeletionSecondRule;
		public boolean printHeader;
		public boolean printResult;

		public Options(boolean... options) {
			this.essential = options.length >= 1 && options[0] || options.length==0;
			this.dependency = options.length >= 2 && options[1];
			this.prepare = options.length >= 3 && options[2];
			this.noneDeletionSecondRule = options.length >= 4 && options[3];
			this.printHeader = options.length >= 5 && options[4];
			this.printResult = options.length >= 6 && options[5];
		}

	}
}
