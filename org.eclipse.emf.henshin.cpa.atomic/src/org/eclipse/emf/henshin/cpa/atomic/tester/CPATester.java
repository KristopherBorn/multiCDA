package org.eclipse.emf.henshin.cpa.atomic.tester;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.henshin.cpa.CPAOptions;
import org.eclipse.emf.henshin.cpa.CpaByAGG;
import org.eclipse.emf.henshin.cpa.UnsupportedRuleException;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.CP;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class CPATester extends Tester {
	private CpaByAGG cpa;
	private CPAResult result;
	
	public CPATester(String henshin, String[] first, String[] second) {
		this(henshin, true, first, second);
	}

	public CPATester(String henshin, String... first) {
		this(henshin, true, first, first);
	}

	public CPATester(String henshin, boolean essential, String... first) {
		this(henshin, essential, first, first);
	}

	public CPATester(String henshin, boolean essential, String[] first, String[] second) {
		cpa = new CpaByAGG();
		NAME = "CPA Tester";
		HenshinResourceSet resourceSet = new HenshinResourceSet(henshin.substring(0, henshin.lastIndexOf("/") + 1));
		Module module = resourceSet.getModule(henshin.substring(henshin.lastIndexOf("/") + 1, henshin.length()), false);
		
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
		try {
			cpa.init(f, s, o);
		} catch (UnsupportedRuleException e) {
			System.err.println(e.getMessage());
		}
		result = cpa.runConflictAnalysis();

	}
	@Override
	public boolean check(Condition... conditions) {
		for (Condition condition : conditions)
			if(condition instanceof CP && !condition.proove(result.getCriticalPairs().size()))
				return false;
			else
				print(condition + " accepted");
		return true;
	}
	public List<CriticalPair> getCriticalPairs() {
		return result.getCriticalPairs();
	}
	@Override
	public String toString() {
		return NAME + ": " + result.getCriticalPairs().size() + " Critical Pairs. " + sizeOfCPs();
	}
	private String sizeOfCPs() {
		String r = "";
		for (CriticalPair criticalPair : result) {
			r += ", " + criticalPair.getCriticalElements().size();
		}
		return "";//"Sizes = (" + r.substring(2) + ")";
	}

}
