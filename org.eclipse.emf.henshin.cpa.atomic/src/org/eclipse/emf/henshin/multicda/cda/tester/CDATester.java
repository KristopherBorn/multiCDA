package org.eclipse.emf.henshin.multicda.cda.tester;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.DependencyAnalysis;
import org.eclipse.emf.henshin.multicda.cda.MultiGranularAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.multicda.cda.dependency.InitialDependencyReason;
import org.eclipse.emf.henshin.multicda.cda.dependency.MinimalDependencyReason;
import org.eclipse.emf.henshin.multicda.cda.runner.RulePreparator;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.CR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.Conditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.Edge;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.ICR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.InitialConditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.MCR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.MinimalConditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.Node;
import org.eclipse.emf.henshin.multicda.cda.tester.Tester.Options;
import org.eclipse.emf.henshin.preprocessing.NonDeletingPreparator;

public class CDATester extends Tester {
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
	private Set<Span> deleteReadCR;

	public CDATester(String henshin, String rule, Options... options) {
		this(henshin, rule, rule, options);
	}

	/**
	 * Geeignet nur f�r Henshin Dateien mit nur einer Regel! Denn es wird nur
	 * diese eine Regel mit sich selbst analysiert!
	 * 
	 * @param henshin
	 * @param options
	 *            1:dependency, 2:prepare, 3:nonDeletionSecondRule,
	 *            4:printHeader, 5:printResult, 6:silent
	 */
	public CDATester(String henshin, Options... options) {
		this(henshin, null, null, options);
	}

	/**
	 * Initialisiert und f�hrt die multiCDA aus
	 * 
	 * @param henshin
	 *            path to the henshin file
	 * @param firstRule
	 *            name of the first rule
	 * @param secondRule
	 *            name of the second rule
	 * @param options
	 *            1:dependency, 2:prepare, 3:nonDeletionSecondRule,
	 *            4:printHeader, 5:printResult, 6:silent
	 */
	public CDATester(String henshin, String firstRule, String secondRule, Options... options) {
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
	 * Initialisiert und f�hrt die multiCDA aus
	 * 
	 * @param first
	 * @param second
	 * @param options
	 *            1:dependency, 2:prepare, 3:nonDeletionSecondRule,
	 *            4:printHeader, 5:printResult, 6:silent
	 */
	public CDATester(Rule first, Rule second, Options... options) {
		this.first = first;
		this.second = second;
		init(options);
	}

	protected void init(Options... opt) {
		Options options = new Options();
		if (opt.length != 0)
			options = opt[0];
		NAME = "CDA Tester";
		if (options.is(Options.PRINT_HEADER))
			System.out.println("\n\t\t  " + first.getName() + " --> " + second.getName() + "\n\t\t\tCDA");
		assertTrue(print("First rule not found", false), first != null && first instanceof Rule);
		assertTrue(print("Second rule not found", false), second != null && second instanceof Rule);
				
		if (options.is(Options.PREPARE)) {
			if (first != second) {
				first = RulePreparator.prepareRule(first);
				second = RulePreparator.prepareRule(second);
			} else {
				first = RulePreparator.prepareRule(first);
				second = first;
			}
		}

		if (true || options.is(Options.NONE_DELETION_SECOND_RULE)) { //TODO Testing
//			second = NonDeletingPreparator.prepareNoneDeletingsVersionsRules(second);

			if (options.is(Options.DEPENDENCY))
				analyser = new DependencyAnalysis(first, second);
			else
				analyser = new ConflictAnalysis(first, second);

			minimalReasons = analyser.computeResultsCoarse();
			initialReasons = analyser.computeResultsFine();
			deleteReadCR = analyser.computeDRCR();
			computedAtoms = analyser.computeAtoms();
			conflictReasons = new HashSet<>();

			print(options.toCDAString() + "\n");
			if (options.is(Options.PRINT_RESULT)) {
				printMCR();
				printICR();
				printDRCR();
				//printCR();
				print();
			}
			System.out.println();
		} else
			System.out.println("NonDeletion-Option for second rule is not enabled.");
	}

	public Set<Span> getDeleteReadCR() {
		return deleteReadCR;
	}

	public void setDeleteReadCR(Set<Span> deleteReadCR) {
		this.deleteReadCR = deleteReadCR;
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
			else if (span instanceof DeleteReadConflictReason)
				type = "DRCR";
			else
				type = "SPAN";
			System.out.println(type + ": " + span.getGraph().getEdges() + "\t| " + span.getGraph().getNodes());
		}
	}

	public void printMCR() {
		CDATester.print(minimalReasons);
		// for (Span minimalReason : minimalReasons)
		// print("MCR: " + minimalReason.getGraph().getEdges() + "\t| " +
		// minimalReason.getGraph().getNodes());
	}

	public void printICR() {
		CDATester.print(initialReasons);
		// for (Span initialReason : initialReasons)
		// print("ICR: " + initialReason.getGraph().getEdges());
	}

	public void printCR() {
		CDATester.print(conflictReasons);
		// for (Span conflictReason : conflictReasons)
		// print("CR: " + conflictReason.getGraph().getEdges() + "\t| " +
		// conflictReason.getGraph().getNodes());

	}

	public void printDRCR() {
		CDATester.print(deleteReadCR);
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
		if (analyser instanceof ConflictAnalysis)
			return minimalReasons.size() + " Minimal Conflict Reasons, " + initialReasons.size() 
					+ " Initial Conflict Reasons, " + deleteReadCR.size() + " Delete-Read Conflict Reasons, " + conflictReasons.size() + " Conflict Reasons";
		if (analyser instanceof DependencyAnalysis)
			return minimalReasons.size() + " Minimal Dependency Reasons, " + initialReasons.size()
					+ " Initial Dependency Reasons, " + conflictReasons.size() + " Dependency Reasons";
		else
			return super.toString();

	}

}