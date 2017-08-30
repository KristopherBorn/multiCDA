package org.eclipse.emf.henshin.cpa.atomic.tester;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.DependencyAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.MultiGranularAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.dependency.InitialDependencyReason;
import org.eclipse.emf.henshin.cpa.atomic.dependency.MinimalDependencyReason;
import org.eclipse.emf.henshin.cpa.atomic.runner.RulePreparator;
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
import org.eclipse.emf.henshin.preprocessing.NonDeletingPreparator;

public class AtomicTester extends Tester {
	public boolean PrintFounds = true;
	private MultiGranularAnalysis analyser;
	private Rule first;
	private Rule second;
	private Set<Span> minimalReasons;
	private Set<Span> initialReasons;
	private Set<Span> conflictReasons;
	private Set<Span> computedAtoms;
	private String checked = "";
	private int iCheckedCounter = 0;
	private int mCheckedCounter = 0;
	private Options options;

	public AtomicTester(String henshin, String rule, boolean... options) {
		this(henshin, rule, rule, options);
	}

	/**
	 * Geeignet nur f�r Henshin Dateien mit nur einer Regel! Denn es wird nur diese eine Regel mit sich selbst analysiert!
	 * 
	 * @param henshin
	 * @param options 1:dependency, 2:prepare, 3:nonDeletionSecondRule, 4:printHeader, 5:printResult
	 */
	public AtomicTester(String henshin, boolean... options) {
		this(henshin, null, null, options);
	}

	/**
	 * Initialisiert und f�hrt die AtomicCoreCPa aus
	 * 
	 * @param henshin
	 * @param firstRule
	 * @param secondRule
	 * @param options 1:dependency, 2:prepare, 3:nonDeletionSecondRule, 4:printHeader, 5:printResult
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
	 * Initialisiert und f�hrt die AtomicCoreCPa aus
	 * 
	 * @param first
	 * @param second
	 * @param options 1:dependency, 2:prepare, 3:nonDeletionSecondRule, 4:printHeader, 5:printResult
	 */
	public AtomicTester(Rule first, Rule second, boolean... options) {
		this.first = first;
		this.second = second;
		init(options);
	}

	protected void init(boolean... opt) {
		NAME = "Atomic Tester";
		options = new Options(opt);
		if (options.printHeader)
			System.out.println("\n\t\t  " + first.getName() + " --> " + second.getName() + "\n\t\t\tAtomic");
		assertTrue(print("First rule not found", false), first != null && first instanceof Rule);
		assertTrue(print("Second rule not found", false), second != null && second instanceof Rule);

		if (options.prepare) {
			if (first != second) {
				first = RulePreparator.prepareRule(first);
				second = RulePreparator.prepareRule(second);				
			} else {
				first = RulePreparator.prepareRule(first);
				second = first;			
			}
		}

		if (options.noneDeletionSecondRule)
			second = NonDeletingPreparator.prepareNoneDeletingsVersionsRules(second);

		if (options.dependency)
			analyser = new DependencyAnalysis(first, second);
		else
			analyser = new ConflictAnalysis(first, second);

		minimalReasons = analyser.computeResultsCoarse();
		initialReasons = analyser.computeResultsFine();
		conflictReasons = new HashSet<Span>();
//		conflictReasons = conflict.computeConflictReasons(computedAtoms, initialReasons);

		if (options.printResult) {
			printMCR();
			printICR();
			printCR();
			print();
		}
	}

	public Set<Span> getInitialReasons() {
		return initialReasons;
	}

	public Set<Span> getMinimalReasons() {
		return minimalReasons;
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
				if (!condition.proove(minimalReasons.size()))
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
			for (Span span : initialReasons) {
				if (span instanceof InitialReason) {
					InitialReason initialReason = (InitialReason) span;
					Set<ModelElement> elements = initialReason.getDeletionElementsInRule1();
					// System.out.println(elements);
					if (!checked.contains("Initial" + elements + "") && checkReasons(elements, edgeNode.toArray())) {
						print("Found ICR: " + elements + "\twith " + type.getSimpleName() + " "
								+ getContent(conditions));
						checked += "Initial" + elements + "\n";
						iCheckedCounter++;
						return true;
					}
				}
			}
		}
		if (type == Conditions.class || type == MinimalConditions.class) {
			for (Span span : minimalReasons) {
				if (span instanceof InitialReason) {
					MinimalConflictReason minimalReason = (MinimalConflictReason) span;
					Set<ModelElement> elements = minimalReason.getDeletionElementsInRule1();
					// System.out.println(elements);
					if (!checked.contains("Minimal" + elements + "") && checkReasons(elements, edgeNode.toArray())) {
						print("Found MCR: " + elements + "\twith " + type.getSimpleName() + " "
								+ getContent(conditions));
						checked += "Minimal" + elements + "\n";
						mCheckedCounter++;
						return true;
					}
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

	public static void print(Set<? extends Span> spans) {
		String type = "";

		for (Span span : spans) {
			if (span instanceof MinimalConflictReason)
				type = "MCR";
			else if (span instanceof InitialReason)
				type = "ICR";
			else if (span instanceof InitialDependencyReason)
				type = "IDCR";
			else if (span instanceof MinimalDependencyReason)
				type = "MDCR";
			else if (span instanceof ConflictReason)
				type = "CR";
			else
				type = "SPAN";
			System.out.println(type + ": " + span.getGraph().getEdges() + "\t| " + span.getGraph().getNodes());
		}
	}

	public void printMCR() {
		for (Span minimalReason : minimalReasons)
			print("MCR: " + minimalReason.getGraph().getEdges() + "\t| " + minimalReason.getGraph().getNodes());
	}

	public void printICR() {
		for (Span initialReason : initialReasons)
			print("ICR: " + initialReason.getGraph().getEdges());
	}

	public void printCR() {
		for (Span conflictReason : conflictReasons) {
			print("CR: " + conflictReason.getGraph().getEdges() + "\t| " + conflictReason.getGraph().getNodes());
		}
	}

	public Set<Span> getConflictReasons() {
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
		int mRest = minimalReasons.size() - mCheckedCounter;
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
		if(analyser instanceof ConflictAnalysis)
		return minimalReasons.size() + " Minimal Conflict Reasons, " + initialReasons.size()
				+ " Initial Conflict Reasons, " + conflictReasons.size() + " Conflict Reasons";
		if(analyser instanceof DependencyAnalysis)
			return minimalReasons.size() + " Minimal Dependency Reasons, " + initialReasons.size()
			+ " Initial Dependency Reasons, " + conflictReasons.size() + " Dependency Reasons";
		else return super.toString();
			
	}

	private static class Options {
		public boolean dependency;
		public boolean prepare;
		public boolean noneDeletionSecondRule;
		public boolean printHeader;
		public boolean printResult;

		public Options(boolean... options) {
			this.dependency = options.length >= 1 && options[0];
			this.prepare = options.length >= 2 && options[1];
			this.noneDeletionSecondRule = options.length >= 3 && options[2];
			this.printHeader = options.length >= 4 && options[3];
			this.printResult = options.length >= 5 && options[4];
		}

	}
}