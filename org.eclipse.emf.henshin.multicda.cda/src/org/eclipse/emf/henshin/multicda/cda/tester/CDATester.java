package org.eclipse.emf.henshin.multicda.cda.tester;

/**
 * @author Jevgenij Huebert
 */
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason.DeleteDeleteConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.EssentialConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.multicda.cda.dependency.CreateUseDependencyReason.CreateDeleteDependencyReason;
import org.eclipse.emf.henshin.multicda.cda.dependency.CreateUseDependencyReason.CreateReadDependencyReason;
import org.eclipse.emf.henshin.multicda.cda.dependency.CreateUseDependencyReason;
import org.eclipse.emf.henshin.multicda.cda.dependency.DependencyReason;
import org.eclipse.emf.henshin.multicda.cda.dependency.MinimalDependencyReason;
import org.eclipse.emf.henshin.multicda.cda.runner.RulePreparator;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.CDDR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.CDDRConditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.CRDR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.CRDRConditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.CUDR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.CUDRConditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.Conditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.DDCR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.DDCRConditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.DRCR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.DRCRConditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.DUCR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.DUCRConditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.Edge;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.MCR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.MinimalReasonConditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.Node;

public class CDATester extends Tester {
	public boolean PrintFounds = true;
	private MultiGranularAnalysis analyser;
	private Rule first;
	private Rule second;
	private Set<? extends Span> minimalReasons = new HashSet<>();
	private Set<? extends Span> conflictReasons = new HashSet<>();
	private Set<? extends Span> essentialConflictReasons = new HashSet<>();
	private Set<? extends Span> computedAtoms = new HashSet<>();
	private String checked = "";
	private int iCheckedCounter = 0;
	private Options options;

	public CDATester(String henshin, String rule, Options... options) {
		this(henshin, rule, rule, options);
	}

	/**
	 * Geeignet nur für Henshin Dateien mit nur einer Regel! Denn es wird nur diese eine Regel mit sich selbst analysiert!
	 * 
	 * @param henshin
	 * @param options 1:dependency, 2:prepare, 3:nonDeletionSecondRule, 4:printHeader, 5:printResult, 6:silent
	 */
	public CDATester(String henshin, Options... options) {
		this(henshin, null, null, options);
	}

	/**
	 * Initialisiert und fï¿½hrt die multiCDA aus
	 * 
	 * @param henshin path to the henshin file
	 * @param firstRule name of the first rule
	 * @param secondRule name of the second rule
	 * @param options 1:dependency, 2:prepare, 3:nonDeletionSecondRule, 4:printHeader, 5:printResult, 6:silent
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
			if (first == null)
				System.out.println(firstRule + " nicht gefunden.");
			if (second == null)
				System.out.println(secondRule + " nicht gefunden.");
			if (first == null || second == null)
				return;
		}
		init(options);
	}

	/**
	 * Initialisiert und fï¿½hrt die multiCDA aus
	 * 
	 * @param first
	 * @param second
	 * @param options 1:dependency, 2:prepare, 3:nonDeletionSecondRule, 4:printHeader, 5:printResult, 6:silent
	 */
	public CDATester(Rule first, Rule second, Options... options) {
		this.first = first;
		this.second = second;
		init(options);
	}

	protected void init(Options... opt) {
		options = new Options();
		if (opt.length != 0)
			options = opt[0];
		NAME = "CDA Tester";
		if (options.is(Options.PRINT_HEADER))
			System.out.println("\n\t\t  " + first.getName() + " --> " + second.getName() + "\n\t\t\tCDA");
		assertTrue(print("First rule not found", true, false), first != null && first instanceof Rule);
		assertTrue(print("Second rule not found", true, false), second != null && second instanceof Rule);

		if (options.is(Options.PREPARE)) {
			if (first != second) {
				first = RulePreparator.prepareRule(first);
				second = RulePreparator.prepareRule(second);
			} else {
				first = RulePreparator.prepareRule(first);
				second = first;
			}
		}

		//if (options.is(Options.NONE_DELETION_SECOND_RULE))
		//second = NonDeletingPreparator.prepareNoneDeletingsVersionsRules(second);

		if (options.is(Options.DEPENDENCY))
			analyser = new DependencyAnalysis(first, second);
		else
			analyser = new ConflictAnalysis(first, second);

		minimalReasons = analyser.computeResultsCoarse();
		conflictReasons = analyser.computeResultsFine();
		computedAtoms = analyser.computeAtoms();

		print(options.toCDAString() + "\n");
		if (options.is(Options.PRINT_RESULT)) {
			printMCR();
			printICR();
//			printCR();
			print();
		}
		System.out.println();
	}

	public Set<? extends Span> getConflictReasons() {
		return conflictReasons;
	}

	public Set<? extends Span> getMinimalReasons() {
		return minimalReasons;
	}

	private List<Condition> edgeNode = new ArrayList<>();

	private boolean check(Condition condition, boolean printError) {
		if (condition instanceof Conditions) {
			Conditions conditions = (Conditions) condition;
			Class<?> type = condition.getClass();
			Condition[] conditionsElements = conditions.getValues();
			edgeNode = new ArrayList<>();
			for (Condition conElement : conditionsElements)
				check(conElement, printError);

			if (edgeNode.size() == 0)
				return true;
			if (type == Conditions.class || type == MinimalReasonConditions.class) {
				for (Span deleteUseConflictReason : minimalReasons) {
					if (deleteUseConflictReason instanceof ConflictReason) {
						MinimalConflictReason minimalReason = (MinimalConflictReason) deleteUseConflictReason;
						if (iChecker(minimalReason, type, condition.name, edgeNode))
							return true;
					}
				}
			}
			//______________________new conditions check________________________
			if ((type == DRCRConditions.class || type == DUCRConditions.class || type == DDCRConditions.class)
					&& options.is(Options.DEPENDENCY)) {
				if (printError)
					System.err.println("Condition type: '" + type + "' is not a Dependency Reason");
				return false;
			}
			if ((type == CRDRConditions.class || type == CUDRConditions.class || type == CDDRConditions.class)
					&& !options.is(Options.DEPENDENCY)) {
				if (printError)
					System.err.println("Condition type: '" + type + "' is not a Conflict Reason");
				return false;
			}
			if (type == Conditions.class || type == DRCRConditions.class || type == DUCRConditions.class
					|| type == CRDRConditions.class || type == CUDRConditions.class)
				for (Span drReason : getDRReasons())
					if (iChecker(drReason, type, condition.name, edgeNode))
						return true;
			if (type == Conditions.class || type == DDCRConditions.class || type == DUCRConditions.class
					|| type == CDDRConditions.class || type == CUDRConditions.class)
				for (Span ddReason : getDDReasons())
					if (iChecker(ddReason, type, condition.name, edgeNode))
						return true;
			if (printError)
				System.err.println("\n[" + condition + "] not found!\n");
			return false;
		} else {
			if (condition instanceof CUDR && options.is(Options.DEPENDENCY)
					|| condition instanceof DUCR && !options.is(Options.DEPENDENCY)) {
				if (!condition.proove(getDUReasons().size())) {
					print(condition + " failed", true);
					return false;
				}
				print(condition + " accepted");
			} else if (condition instanceof CRDR && options.is(Options.DEPENDENCY)
					|| condition instanceof DRCR && !options.is(Options.DEPENDENCY)) {
				if (!condition.proove(getDRReasons().size())) {
					print(condition + " failed", true);
					return false;
				}
				print(condition + " accepted");
			} else if (condition instanceof CDDR && options.is(Options.DEPENDENCY)
					|| condition instanceof DDCR && !options.is(Options.DEPENDENCY)) {
				if (!condition.proove(getDDReasons().size())) {
					print(condition + " failed", true);
					return false;
				}
				print(condition + " accepted");
			} else if (condition instanceof MCR) {
				if (!condition.proove(minimalReasons.size())) {
					print(condition + " failed", true);
					return false;
				}
				print(condition + " accepted");
			} else if (condition instanceof Edge || condition instanceof Node)
				edgeNode.add(condition);
			else if (printError)
				System.err.println("This condition don't belong here --> " + condition);
			return true;
		}

	}

	@Override
	public boolean check(Condition condition) { //Class<?> type, Condition... conditions) {
		return check(condition, true);
	}

	/**
	 * @return
	 */
	private Set<? extends Span> getDUReasons() {
		return conflictReasons;
	}

	/**
	 * @return
	 */
	private Set<? extends Span> getDDReasons() {
		Set<Span> result = new HashSet<>();
		for (Span ddcr : conflictReasons)
			if (ddcr instanceof DeleteDeleteConflictReason || ddcr instanceof CreateDeleteDependencyReason)
				result.add(ddcr);
		return result;
	}

	/**
	 * @return
	 */
	private Set<? extends Span> getDRReasons() {
		Set<Span> result = new HashSet<>();
		for (Span ddcr : conflictReasons)
			if (ddcr instanceof DeleteReadConflictReason || ddcr instanceof CreateReadDependencyReason)
				result.add(ddcr);
		return result;
	}

	/**
	 * @param conflictReason
	 */
	private boolean iChecker(Span conflictReason, Class<?> type, String shortName, List<Condition> edgeNodes) {
		Set<ModelElement> elements = conflictReason.getDeletionElementsInRule1();
		if (!checked.contains(conflictReason.getClass().getSimpleName() + "" + elements)
				&& checkReasons(elements, edgeNodes)) {
			print("Found " + shortName + ": " + elements + "\twith " + type.getSimpleName() + " " + edgeNodes);
			checked += conflictReason.getClass().getSimpleName() + "" + elements + "\n";
			iCheckedCounter++;
			return true;
		}
		return false;
	}

	public void reset() {
		iCheckedCounter = 0;
		checked = "";
	}

	public static void print(Set<? extends Span> DeleteUseConflictReasons) {
		String type = "";

		for (Span conflictReason : DeleteUseConflictReasons) {
			if (conflictReason instanceof DeleteUseConflictReason) {
				((DeleteUseConflictReason) conflictReason).print();
			} else if (conflictReason instanceof CreateUseDependencyReason) {
				((CreateUseDependencyReason) conflictReason).print();
			} else {
				if (conflictReason instanceof MinimalConflictReason)
					type = "MCR";
				else if (conflictReason instanceof MinimalDependencyReason)
					type = "MDCR";
				else if (conflictReason instanceof EssentialConflictReason)
					type = "ECR";
				else
					type = "DeleteUseConflictReason";
				System.out.println(type + ": " + conflictReason.getGraph().getEdges() + "\t| "
						+ conflictReason.getGraph().getNodes());
			}
		}
	}

	public void printMCR() {
		CDATester.print(minimalReasons);
	}

	public void printICR() {
		CDATester.print(conflictReasons);
	}

	public void printCR() {
		CDATester.print(essentialConflictReasons);

	}

	public Set<? extends Span> getEssentialConflictReasons() {
		return essentialConflictReasons;
	}

	public List<Rule> getRules() {
		ArrayList<Rule> result = new ArrayList<Rule>();
		result.add(first);
		result.add(second);
		return result;
	}

	@Override
	public void ready() {
		int iRest = conflictReasons.size() - iCheckedCounter;
		if (iRest > 0)
			print("Not all Conflict Reasons are tested. " + iRest + (iRest == 1 ? " is" : " are") + " remaining.");
		super.ready();
		reset();
	}

	@Override
	public String toString() {
		if (analyser instanceof ConflictAnalysis)
			return minimalReasons.size()
					+ (minimalReasons.size() > 1 ? " Minimal Conflict Reasons, " : " Minimal Conflict Reason, ")
					+ conflictReasons.size() + (conflictReasons.size() > 1 ? " Conflict Reasons" : " Conflict Reason");

		if (analyser instanceof DependencyAnalysis)
			return minimalReasons.size()
					+ (minimalReasons.size() > 1 ? " Minimal Dependency Reasons, " : " Minimal Dependency Reason, ")
					+ conflictReasons.size() + (conflictReasons.size() > 1 ? " Conflict Dependency" : " Dependency Reason");
		else
			return super.toString();

	}

}