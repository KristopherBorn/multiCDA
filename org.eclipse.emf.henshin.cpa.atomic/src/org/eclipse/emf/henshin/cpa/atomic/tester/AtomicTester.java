package org.eclipse.emf.henshin.cpa.atomic.tester;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;

import org.eclipse.emf.henshin.cpa.atomic.AtomicCoreCPA;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Conditions;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Edge;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.ICR;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.MCR;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Node;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class AtomicTester extends Tester {
	public boolean PrintFounds = true;
	private AtomicCoreCPA atomic;
	private Rule first;
	private Rule second;
	private Set<MinimalConflictReason> minimalConflictReasons;
	private Set<InitialConflictReason> initialReasons;
	private String checked = "";
	private int checkedCounter = 0;

	public AtomicTester(String henshin, String rule) {
		this(henshin, rule, rule);
	}

	public AtomicTester(String henshin, String firstRule, String secondRule) {
		if(henshin.isEmpty() || firstRule.isEmpty() || secondRule.isEmpty())
			return;
		HenshinResourceSet resourceSet = new HenshinResourceSet(henshin.substring(0, henshin.lastIndexOf("/") + 1));
		Module module = resourceSet.getModule(henshin.substring(henshin.lastIndexOf("/") + 1, henshin.length()), false);

		first = (Rule) module.getUnit(firstRule);
		second = (Rule) module.getUnit(secondRule);
		init();
	}

	public AtomicTester(Rule first, Rule second) {
		this.first = first;
		this.second = second;
		init();
	}

	protected void init() {
		assertTrue(print("First rule not found", false), first != null && first instanceof Rule);
		assertTrue(print("Second rule not found", false), second != null && second instanceof Rule);
		atomic = new AtomicCoreCPA();
		NAME = "Atomic Tester";
		atomic.computeConflictAtoms(first, second);
		Set<MinimalConflictReason> mcr = atomic.getMinimalConflictReasons();

		minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for (Span conflictReason : mcr)
			minimalConflictReasons.add(new MinimalConflictReason(conflictReason));

		initialReasons = atomic.computeInitialReason(minimalConflictReasons);
	}

	public Set<InitialConflictReason> getInitialReasons() {
		return initialReasons;
	}

	public Set<MinimalConflictReason> getMinimalConflictReasons() {
		return minimalConflictReasons;
	}

	@Override
	public boolean check(Condition... conditions) {
		List<Condition> edgeNode = new ArrayList<Condition>();
		for (Condition condition : conditions) {
			if (condition instanceof ICR) {
				if (!condition.proove(initialReasons.size()))
					return false;
				print(condition + " accepted");
			} else if (condition instanceof MCR) {
				if (!condition.proove(minimalConflictReasons.size()))
					return false;
				print(condition + " accepted");
			} else if (condition instanceof Edge || condition instanceof Node)
				edgeNode.add(condition);
		}
		if (edgeNode.size() == 0)
			return true;
		for (InitialConflictReason initialReason : initialReasons) {
			Set<ModelElement> elements = initialReason.getDeletionElementsInRule1();
//			System.out.println(elements);
			if (!checked.contains(elements + "") && checkInitialReason(elements, edgeNode.toArray())) {
				checked += elements + "\n";
				checkedCounter++;
				return true;
			}
		}
		return false;
	}

	public void reset() {
		checkedCounter = 0;
		checked = "";
	}

	private boolean checkInitialReason(Set<ModelElement> elements, Object... conditions) {
		String checked = "";
		int index = 0;
		if (elements.size() != conditions.length)
			return false;
		for (ModelElement element : elements) {
			boolean found = false;
			for (Object condition : conditions) {
				if (!(condition instanceof Condition))
					return false;
				Condition c = (Condition) condition;
				if (index >= conditions.length)
					return false;
				if (!checked.contains("::" + condition) && c.proove(element)) {
					found = true;
					index++;
					checked += "::" + condition;
					break;
				}
			}
			if (!found)
				return false;
		}
		boolean result = index == conditions.length;
		if (PrintFounds && result)
			print("Found elements: " + elements + "\t\twith conditions: " + getContent(conditions));
		return result;
	}

	public void ready() {
		int rest = initialReasons.size() - checkedCounter;
		if (rest==1)
			print("Not all Initial Reasons are tested. " + rest + " is remaining.");
		else if (rest>1)
			print("Not all Initial Reasons are tested. " + rest + " are remaining.");
		else
			print("Ready");
		reset();
	}
	@Override
	public String toString() {
		return NAME + ": " + minimalConflictReasons.size() + " Minimal Conflict Reasons, " + 
				initialReasons.size() + " Initial Conflict Reasons.";
	}
}