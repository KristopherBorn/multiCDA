package org.eclipse.emf.henshin.cpa.atomic.tester;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.CpaByAGG;
import org.eclipse.emf.henshin.cpa.UnsupportedRuleException;
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
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class CPATester extends Tester {
	private CpaByAGG cpa;
	private CPAResult result;
	private String checked = "";
	private int checkedCounter = 0;
	private int checkedRightCounter = 0;

	public CPATester(String henshin, String[] first, String[] second) {
		this(henshin, true, false, first, second);
	}
	public CPATester(List<Rule> rules) {
		this(true, false, rules, rules);
	}

	public CPATester(String henshin, String... first) {
		this(henshin, true, false, first, first);
	}

	public CPATester(String henshin, boolean essential, String... first) {
		this(henshin, essential, false, first, first);
	}

	public CPATester(String henshin, boolean essential, boolean dependency, String... first) {
		this(henshin, essential, dependency, first, first);
	}

	public CPATester(String henshin, boolean essential, String[] first, String[] second) {
		this(henshin, essential, false, first, second);
	}
	
	public CPATester(boolean essential, boolean dependency, List<Rule> first, List<Rule> second){
		String ff="", ss="";
		for (Rule nameF : first)
			ff += (ff.isEmpty() ? "" : ", ") + nameF.getName();
		for (Rule nameS : second)
			ss += (ss.isEmpty() ? "" : ", ") + nameS.getName();
		ff = ff.isEmpty() ? "All" : ff;
		ss = ss.isEmpty() ? "All" : ss;
		System.out.println("\n\t\t  " + ff + " --> " + ss + "\n\t\t\tCPA " + (essential ? "Essential" : ""));

		CPAOptions o = new CPAOptions();
		o.setEssential(essential);
		o.setReduceSameRuleAndSameMatch(false);
		o.setIgnoreSameRules(false);

		cpa = new CpaByAGG();
		NAME = "CPA Tester";
		try {
			cpa.init(first, second, o);
		} catch (UnsupportedRuleException e) {
			System.err.println(e.getMessage());
		}
		if (dependency)
			result = cpa.runDependencyAnalysis();
		else
			result = cpa.runConflictAnalysis();

		printResult();
		print();
	}

	public CPATester(String henshin, boolean essential, boolean dependency, String[] first, String[] second) {

		String ff = "", ss = "";
		for (String nameF : first)
			ff += (ff.isEmpty() ? "" : ", ") + nameF;
		for (String nameS : second)
			ss += (ss.isEmpty() ? "" : ", ") + nameS;
		ff = ff.isEmpty() ? "All" : ff;
		ss = ss.isEmpty() ? "All" : ss;
		System.out.println("\n\t\t  " + ff + " --> " + ss + "\n\t\t\tCPA " + (essential ? "Essential" : ""));
		cpa = new CpaByAGG();
		NAME = "CPA Tester";
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

		CPAOptions o = new CPAOptions();
		o.setEssential(essential);
		o.setReduceSameRuleAndSameMatch(false);
		o.setIgnoreSameRules(false);

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

		try {
			cpa.init(f, s, o);
		} catch (UnsupportedRuleException e) {
			System.err.println(e.getMessage());
		}
		if (dependency)
			result = cpa.runDependencyAnalysis();
		else
			result = cpa.runConflictAnalysis();

		printResult();
		print();
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
		return result.getCriticalPairs().size() + " Critical Pairs. ";
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

}
