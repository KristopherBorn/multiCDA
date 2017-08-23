package org.eclipse.emf.henshin.cpa.atomic.tester;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.CR;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Conditions;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Edge;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.ICR;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.InitialConditions;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.MCR;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.MinimalConditions;
import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Node;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class AtomicTester extends Tester {
	public boolean PrintFounds = true;
	private ConflictAnalysis atomic;
	private Rule first;
	private Rule second;
	private Set<MinimalConflictReason> minimalConflictReasons;
	private Set<InitialConflictReason> initialReasons;
	private Set<ConflictReason> conflictReasons;
	private String checked = "";
	private int iCheckedCounter = 0;
	private int mCheckedCounter = 0;
	List<ConflictAtom> computedConflictAtoms;

	public AtomicTester(String henshin, String rule) {
		this(henshin, rule, rule);
	}

	public AtomicTester(String henshin, String firstRule, String secondRule) {
		System.out.println("\n\t\t  " + firstRule + " --> " + secondRule + "\n\t\t\tAtomic");
		if (henshin.isEmpty() || firstRule.isEmpty() || secondRule.isEmpty())
			return;
		HenshinResourceSet resourceSet = new HenshinResourceSet(henshin.substring(0, henshin.lastIndexOf("/") + 1));
		Module module = resourceSet.getModule(henshin.substring(henshin.lastIndexOf("/") + 1, henshin.length()), false);

		first = (Rule) module.getUnit(firstRule);
		second = (Rule) module.getUnit(secondRule);
		init();
		printMCR();
		printICR();
		printCR();
		print();
	}


	public AtomicTester(Rule first, Rule second) {
		this.first = first;
		this.second = second;
		init();
	}

	protected void init() {
		assertTrue(print("First rule not found", false), first != null && first instanceof Rule);
		assertTrue(print("Second rule not found", false), second != null && second instanceof Rule);
		atomic = new ConflictAnalysis();
		NAME = "Atomic Tester";
		
		
		computedConflictAtoms = atomic.computeConflictAtoms(first, second);
		minimalConflictReasons = atomic.getMinimalConflictReasons();
		initialReasons = atomic.computeInitialReasons(minimalConflictReasons);
		conflictReasons = atomic.computeConflictReasons(computedConflictAtoms, initialReasons);
	}

	public Set<InitialConflictReason> getInitialConflictReasons() {
		return initialReasons;
	}

	public Set<MinimalConflictReason> getMinimalConflictReasons() {
		return minimalConflictReasons;
	}

	@Override
	public boolean check(Class<?> type, Condition... conditions) {

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
			} else if (condition instanceof CR) {
				if (!condition.proove(conflictReasons.size()))
					return false;
				print(condition + " accepted");
			} else if (condition instanceof Edge || condition instanceof Node)
				edgeNode.add(condition);
			else
				System.err.println("This condition don't belong here --> " + condition);
		}
		if (edgeNode.size() == 0)
			return true;

		if (type == Conditions.class || type == InitialConditions.class) {
			for (InitialConflictReason initialReason : initialReasons) {
				Set<ModelElement> elements = initialReason.getDeletionElementsInRule1();
				// System.out.println(elements);
				if (!checked.contains("Initial" + elements + "") && checkReasons(elements, edgeNode.toArray())) {
					print("Found ICR: " + elements + "\twith " + type.getSimpleName() + " " + getContent(conditions));
					checked += "Initial" + elements + "\n";
					iCheckedCounter++;
					return true;
				}
			}
		}
		if (type == Conditions.class || type == MinimalConditions.class) {
			for (MinimalConflictReason minimalReason : minimalConflictReasons) {
				Set<ModelElement> elements = minimalReason.getDeletionElementsInRule1();
				// System.out.println(elements);
				if (!checked.contains("Minimal" + elements + "") && checkReasons(elements, edgeNode.toArray())) {
					print("Found MCR: " + elements + "\twith " + type.getSimpleName() + " " + getContent(conditions));
					checked += "Minimal" + elements + "\n";
					mCheckedCounter++;
					return true;
				}
			}
		}
		return false;
	}

	public void reset() {
		iCheckedCounter = 0;
		mCheckedCounter = 0;
		checked = "";
	}

	public void printMCR() {
		for (MinimalConflictReason minimalReason : minimalConflictReasons)
			print("MCR: " + minimalReason.getGraph().getEdges());
	}

	public void printICR() {
		for (InitialConflictReason initialReason : initialReasons)
			print("ICR: " + initialReason.getGraph().getEdges());
	}

	private void printCR() {
		for (ConflictReason conflictReason : conflictReasons) {
			print("CR: " + conflictReason.getGraph().getEdges());
		}
	}
	
	public Set<ConflictReason> getConflictReasons() {
		return conflictReasons;
	}

	@Override
	public void ready() {
		int iRest = initialReasons.size() - iCheckedCounter;
		int mRest = minimalConflictReasons.size() - mCheckedCounter;
		if (iRest > 0)
			print("Not all Initial Reasons are tested. " + iRest + (iRest == 1 ? " is" : " are") + " remaining.");
		if (mRest > 0)
			print("Not all Minimal Conflict Reasons are tested. " + mRest + (mRest == 1 ? " is" : " are")
					+ " remaining.");
		super.ready();
		reset();
	}

	@Override
	public String toString() {
		return minimalConflictReasons.size() + " Minimal Conflict Reasons, " + initialReasons.size()
				+ " Initial Conflict Reasons, " + conflictReasons.size() + " Conflict Reasons";
	}
}