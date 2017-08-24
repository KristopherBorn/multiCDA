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
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class AtomicTester extends Tester {
	public boolean PrintFounds = true;
	private ConflictAnalysis atomic;
	private Rule first;
	private Rule second;
	private Set<MinimalConflictReason> minimalConflictReasons;
	private Set<ConflictReason> conflictReasons;
	private Set<InitialConflictReason> initialReasons;
	private String checked = "";
	private int iCheckedCounter = 0;
	private int mCheckedCounter = 0;
	List<ConflictAtom> computedConflictAtoms;

	public AtomicTester(String henshin, String rule) {
		this(henshin, rule, rule);
	}

	/**
	 * Geeignet nur für Henshin Dateien mit nur einer Regel! Denn es wird nur diese eine Regel mit sich selbst analysiert!
	 * 
	 * @param henshin
	 */
	public AtomicTester(String henshin) {
		this(henshin, null, null);
	}

	/**
	 * Initialisiert und führt die AtomicCoreCPa aus
	 * 
	 * @param henshin
	 * @param firstRule
	 * @param secondRule
	 * @param options werden in dieser Reihenfilge akzeptiert: 1:printHeader, 2:printResult
	 */
	public AtomicTester(String henshin, String firstRule, String secondRule, boolean... options) {
		if (henshin.isEmpty()
				|| ((firstRule != null && !firstRule.isEmpty()) ^ (secondRule != null && !secondRule.isEmpty())))
			return;
		HenshinResourceSet resourceSet = new HenshinResourceSet(henshin.substring(0, henshin.lastIndexOf("/") + 1));
		Module module = resourceSet.getModule(henshin.substring(henshin.lastIndexOf("/") + 1, henshin.length()), false);

		if (firstRule == null || secondRule == "") {
			firstRule = "All";
			secondRule = "All";
			int time = 0;
			for (Unit u : module.getUnits()) {
				if (u instanceof Rule) {
					if (time == 1) {
						System.err.println("Es gibt mehr als nur eine Regel! Bitte in diesem Fall Regeln angeben!");
						return;
					}
					time = 1;
					first = (Rule) u;
					second = (Rule) u;
				}
			}
		} else {
			first = (Rule) module.getUnit(firstRule);
			second = (Rule) module.getUnit(secondRule);
		}
		init(options);
	}

	/**
	 * Initialisiert und führt die AtomicCoreCPa aus
	 * 
	 * @param first
	 * @param second
	 * @param options werden in dieser Reihenfilge akzeptiert: 1:printHeader, 2:printResult
	 */
	public AtomicTester(Rule first, Rule second, boolean... options) {
		this.first = first;
		this.second = second;
		init(options);
	}

	protected void init(boolean... options) {
		if (options.length >= 1 && options[0])
			System.out.println("\n\t\t  " + first.getName() + " --> " + second.getName() + "\n\t\t\tAtomic");
		assertTrue(print("First rule not found", false), first != null && first instanceof Rule);
		assertTrue(print("Second rule not found", false), second != null && second instanceof Rule);
		atomic = new ConflictAnalysis(first, second);
		NAME = "Atomic Tester";

		computedConflictAtoms = atomic.computeConflictAtoms();
		minimalConflictReasons = atomic.getMinimalConflictReasons();
		initialReasons = atomic.computeInitialReasons(minimalConflictReasons);
		conflictReasons = atomic.computeConflictReasons(computedConflictAtoms, initialReasons);
		if (options.length >= 2 && options[1]) {
			printMCR();
			printICR();
			printCR();
			print();
		}
	}

	public Set<InitialConflictReason> getInitialReasons() {
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

	public static void printMCR(Set<MinimalConflictReason> mr) {
		for (MinimalConflictReason minimalReason : mr)
			System.out.println(
					"MCR: " + minimalReason.getGraph().getEdges() + "\t| " + minimalReason.getGraph().getNodes());
	}

	public void printMCR() {
		for (MinimalConflictReason minimalReason : minimalConflictReasons)
			print("MCR: " + minimalReason.getGraph().getEdges() + "\t| " + minimalReason.getGraph().getNodes());
	}

	public static void printICR(Set<InitialConflictReason> ir) {
		for (InitialConflictReason initialReason : ir)
			System.out.println(
					"ICR: " + initialReason.getGraph().getEdges() + "\t| " + initialReason.getGraph().getNodes());
	}

	public void printICR() {
		for (InitialConflictReason initialReason : initialReasons)
			print("ICR: " + initialReason.getGraph().getEdges() + "\t| " + initialReason.getGraph().getNodes());
	}

	public static void printCR(Set<ConflictReason> cr) {
		for (ConflictReason conflictReason : cr) {
			System.out.println(
					"CR: " + conflictReason.getGraph().getEdges() + "\t| " + conflictReason.getGraph().getNodes());
		}
	}

	public void printCR() {
		for (ConflictReason conflictReason : conflictReasons) {
			print("CR: " + conflictReason.getGraph().getEdges() + "\t| " + conflictReason.getGraph().getNodes());
		}
	}

	public Set<ConflictReason> getConflictReasons() {
		return conflictReasons;
	}

	public List<Rule> getRules() {
		ArrayList<Rule> result = new ArrayList<Rule>();
		result.add(first);
		result.add(second);
		return result;
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