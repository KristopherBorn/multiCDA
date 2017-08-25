package org.eclipse.emf.henshin.cpa.atomic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.dependency.DependencyAtom;
import org.eclipse.emf.henshin.cpa.atomic.dependency.InitialDependencyReason;
import org.eclipse.emf.henshin.cpa.atomic.dependency.MinimalDependencyReason;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

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
		computeInitialDependencyReasons().forEach(r -> results.add(r));
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

	// Constructor
	public DependencyAnalysis(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
	}



	public DependencyAtom hasDependencies() {
		Rule invertedRule1 = invertRule(rule1);
		ConflictAnalysis ca = new ConflictAnalysis(invertedRule1, rule2);
		 ConflictAtom conflictAtom = ca.hasConflicts();
		if (conflictAtom != null) {
			return new DependencyAtom(conflictAtom.getSpan());
		} else return null;
	}

	public Set<DependencyAtom> computeDependencyAtoms() {
		Set<DependencyAtom> result = new HashSet<DependencyAtom>();
		Rule invertedRule1 = invertRule(rule1);
		ConflictAnalysis ca = new ConflictAnalysis(invertedRule1, rule2);
		 List<ConflictAtom> conflictAtoms = ca.computeConflictAtoms();
		for (ConflictAtom cr : conflictAtoms) {
			result.add(new DependencyAtom(cr.getSpan()));
		}
		return result;
	}


	public Set<InitialDependencyReason> computeInitialDependencyReasons() {
		Set<InitialDependencyReason> result = new HashSet<InitialDependencyReason>();
		Rule invertedRule1 = invertRule(rule1);
		ConflictAnalysis ca = new ConflictAnalysis(invertedRule1, rule2);
		Set<InitialReason> conflictReasons = ca.computeInitialReasons();
		for (InitialReason cr : conflictReasons) {
			result.add(new InitialDependencyReason(cr));
		}
		return result;
	}
	
	public Set<MinimalDependencyReason> computeMinimalDependencyReasons() {
		Set<MinimalDependencyReason> result = new HashSet<MinimalDependencyReason>();
		Rule invertedRule1 = invertRule(rule1);
		ConflictAnalysis ca = new ConflictAnalysis(invertedRule1, rule2);
		Set<MinimalConflictReason> conflictReasons = ca.computeMinimalConflictReasons();
		for (MinimalConflictReason cr : conflictReasons) {
			result.add(new MinimalDependencyReason(cr));
		}
		return result;
	}
	
	/**
	 * TODO: add information here z.B.: - welche Regel-features unterstützt
	 * werden - wie wird damit umgeganen, wenn die Knoten oder Mappings nicht
	 * erstellt wurde? - wird NULL zurück gegeben, oder wird eine Exception
	 * geworfen? - sollte es eine Möglichkeit vorab geben zu prüfen, ob die
	 * Regel invertierbar ist?
	 * 
	 * @param rule1
	 * @return the
	 */
	public static Rule invertRule(Rule rule1) {
		Map<Rule, Copier> mappingOfInvertedRuleToRhsToLhsCopier = new HashMap<>();
		
		// TODO: invert first rule
		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;

		// erstellen einer Regel mit ursprünglichem Namen + "_INV"
		// TODO: In welches MODULe kommt die Regel überhaupt ????
		// eigenes? Imports? Copy?
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
		// ausgehend von den Nodes in der ursprünglichen Regel über den copier
		// die Nodes in der neuen regel identifizieren!
		MappingList mappingsOfOriginalRule1 = rule1.getMappings();
		for (Mapping mappingInOriginalRule1 : mappingsOfOriginalRule1) {

			// identifizieren der ORIGIN in der neuen Regel
			Node imageInOriginalRule = mappingInOriginalRule1.getImage();
			EObject originInNewRule = copierForRhsToLhs.get(imageInOriginalRule);
			Node originInNewRuleNode = (Node) originInNewRule; // TODO: add NULL
																// check!

			// identifizieren des IMAGE in der neuen Regel
			Node originInOriginalRule = mappingInOriginalRule1.getOrigin();
			EObject imageInNewRule = copierForLhsToRhs.get(originInOriginalRule);
			Node imageInNewRuleNode = (Node) imageInNewRule; // TODO: add NULL
																// check!

			Mapping createdMapping = henshinFactory.createMapping(originInNewRuleNode, imageInNewRuleNode);
			invRule1.getMappings().add(createdMapping);
		}

		// ggf. als Datei speichern?
		// ABER(!): auch prüfen, ob es rein programmatisch geht!

		return invRule1;
	}

}
