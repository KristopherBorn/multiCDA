package org.eclipse.emf.henshin.multicda.cda;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.multicda.cda.dependency.CreateUseDependencyReason;
import org.eclipse.emf.henshin.multicda.cda.dependency.DependencyAtom;
import org.eclipse.emf.henshin.multicda.cda.dependency.DependencyReason;
import org.eclipse.emf.henshin.multicda.cda.dependency.MinimalDependencyReason;

public class DependencyAnalysis implements MultiGranularAnalysis {

	@Override
	public Span computeResultsBinary() {
		return hasDependencies();
	}

	@Override
	public Set<Span> computeResultsCoarse() {
		Set<Span> results = new HashSet<Span>();
		computeMinimalDependencyReasons().forEach(r -> results.add(r));
		return results;
	}

	@Override
	public Set<Span> computeResultsFine() {
		Set<Span> results = new HashSet<Span>();
		computeDeleteUseDependencyReasons().forEach(r -> results.add(r));
		return results;
	}

	@Override
	public Set<Span> computeAtoms() {
		Set<Span> results = new HashSet<Span>();
		computeDependencyAtoms().forEach(r -> results.add(r));
		return results;
	}

	private Rule rule1;
	private Rule rule2;
	private Rule invertedRule1;

	// Constructor
	public DependencyAnalysis(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		invertedRule1 = invertRule(rule1);
	}

	public DependencyAtom hasDependencies() {
		// Rule invertedRule1 = invertRule(rule1);
		ConflictAnalysis ca = new ConflictAnalysis(invertedRule1, rule2);
		ConflictAtom conflictAtom = ca.hasConflicts();
		if (conflictAtom != null) {
			return new DependencyAtom(conflictAtom);
		} else
			return null;
	}

	public Set<DependencyAtom> computeDependencyAtoms() {
		Set<DependencyAtom> result = new HashSet<DependencyAtom>();
		// Rule invertedRule1 = invertRule(rule1);
		ConflictAnalysis ca = new ConflictAnalysis(invertedRule1, rule2);
		List<ConflictAtom> conflictAtoms = ca.computeConflictAtoms();
		for (ConflictAtom cr : conflictAtoms) {
			result.add(new DependencyAtom(cr));
		}
		return result;
	}

	public Set<DependencyReason> computeInitialDependencyReasons() {
		Set<DependencyReason> result = new HashSet<DependencyReason>();
		// Rule invertedRule1 = invertRule(rule1);
		ConflictAnalysis ca = new ConflictAnalysis(invertedRule1, rule2);
		Set<ConflictReason> conflictReasons = ca.computeConflictReasons();
		for (ConflictReason cr : conflictReasons) {
			result.add(new DependencyReason(cr));
		}
		return result;
	}

	public Set<MinimalDependencyReason> computeMinimalDependencyReasons() {
		Set<MinimalDependencyReason> result = new HashSet<MinimalDependencyReason>();
		// Rule invertedRule1 = invertRule(rule1);
		ConflictAnalysis ca = new ConflictAnalysis(invertedRule1, rule2);
		Set<MinimalConflictReason> conflictReasons = ca.computeMinimalConflictReasons();
		for (MinimalConflictReason cr : conflictReasons) {
			result.add(new MinimalDependencyReason(cr));
		}
		return result;
	}

	public Set<Span> computeDeleteUseDependencyReasons() {
		Set<Span> result = new HashSet<Span>();
		// Rule invertedRule1 = invertRule(rule1);
		ConflictAnalysis ca = new ConflictAnalysis(invertedRule1, rule2);
		Set<Span> conflictReasons = ca.computeResultsFine();
		for (Span cr : conflictReasons) {
			result.add(new CreateUseDependencyReason(cr));
		}
		return result;

	}

	/**
	 * werden - wie wird damit umgeganen, wenn die Knoten oder Mappings nicht
	 * erstellt wurde? - wird NULL zur�ck gegeben, oder wird eine Exception
	 * geworfen? - sollte es eine M�glichkeit vorab geben zu pr�fen, ob die
	 * Regel invertierbar ist?
	 * 
	 * @param rule1
	 * @return the
	 */
	public static Rule invertRule(Rule rule1) {
		Map<Rule, Copier> mappingOfInvertedRuleToRhsToLhsCopier = new HashMap<>();

		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;

		Rule invRule1 = henshinFactory.createRule(rule1.getName() + "_INV");

		// Kopieren des RHS Graph und als LHS der neuen Regel zuordnen
		Copier copierForRhsToLhs = new Copier();
		Graph newLhs = (Graph) copierForRhsToLhs.copy(rule1.getRhs());
		copierForRhsToLhs.copyReferences();
		newLhs.setName("LHS");
		invRule1.setLhs(newLhs);
		mappingOfInvertedRuleToRhsToLhsCopier.put(invRule1, copierForRhsToLhs);

		// Kopieren des LHS Graph und als RHS der neuen Regel zuordnen
		Copier copierForLhsToRhs = new Copier();
		Graph newRhs = (Graph) copierForLhsToRhs.copy(rule1.getLhs());
		copierForLhsToRhs.copyReferences();
		newRhs.setName("RHS");
		invRule1.setRhs(newRhs);

		// notwendige Mappings erstellen. Dazu alle Mappings durchgehen und
		// ausgehend von den Nodes in der urspr�nglichen Regel �ber den copier
		// die Nodes in der neuen regel identifizieren!
		MappingList mappingsOfOriginalRule1 = rule1.getMappings();
		for (Mapping mappingInOriginalRule1 : mappingsOfOriginalRule1) {

			// identifizieren der ORIGIN in der neuen Regel
			Node imageInOriginalRule = mappingInOriginalRule1.getImage();
			EObject originInNewRule = copierForRhsToLhs.get(imageInOriginalRule);
			Node originInNewRuleNode = (Node) originInNewRule;

			// identifizieren des IMAGE in der neuen Regel
			Node originInOriginalRule = mappingInOriginalRule1.getOrigin();
			EObject imageInNewRule = copierForLhsToRhs.get(originInOriginalRule);
			Node imageInNewRuleNode = (Node) imageInNewRule;

			Mapping createdMapping = henshinFactory.createMapping(originInNewRuleNode, imageInNewRuleNode);
			invRule1.getMappings().add(createdMapping);
		}

		// ggf. als Datei speichern?
		// ABER(!): auch pr�fen, ob es rein programmatisch geht!

		return invRule1;
	}

}
