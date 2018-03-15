package org.eclipse.emf.henshin.multicda.cda;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.computation.AtomCandidateComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.ConflictReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.DeleteUseConflictReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.MinimalReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.preprocessing.NonDeletingPreparator;

public class ConflictAnalysis implements MultiGranularAnalysis {

	private Rule rule1;
	private Rule rule2NonDelete;
	private Rule rule1NonDelete;
	private ConflictReasonComputation conflictHelper;
	private Set<Span> conflictReasonsFromR2 = new HashSet<Span>();
	private Rule rule2;
	

	/**
	 * @param rule1
	 * @param rule2
	 */
	public ConflictAnalysis(Rule rule1, Rule rule2) {
		checkNull(rule1);
		checkNull(rule2);
			this.rule1 = rule1;
			this.rule1NonDelete = NonDeletingPreparator.prepareNoneDeletingsVersionsRules(rule1);
			this.rule2 = rule2;
			this.rule2NonDelete = NonDeletingPreparator.prepareNoneDeletingsVersionsRules(rule2);
			
	}

	@Override
	public Set<Span> computeAtoms() {
		Set<Span> results = new HashSet<Span>();
		computeConflictAtoms().forEach(r -> results.add(r));
		return results;
	}

	@Override
	public Span computeResultsBinary() {
		ConflictAtom result = hasConflicts();
		if (result == null)
			return null;
		else
			return result;
	}

	@Override
	public Set<Span> computeResultsCoarse() {
		Set<Span> results = new HashSet<Span>();
		computeMinimalConflictReasons().forEach(r -> results.add(r));
		return results;
	}

	@Override
	public Set<Span> computeResultsFine() {
		Set<Span> results = new HashSet<Span>();
		Set<Span> conflictReasons = new HashSet<Span>();
		conflictHelper = new ConflictReasonComputation(rule2, rule1NonDelete);
		conflictHelper.computeConflictReasons().forEach(r -> conflictReasonsFromR2.add(r));
		computeConflictReasons().forEach(r -> conflictReasons.add(r));
		computeDeleteUseConflictReasons(conflictReasons).forEach(r -> results.add(r));
		return results;
	}

	public ConflictAtom hasConflicts() {
		List<ConflictAtom> cas = computeConflictAtoms(true);
		if (cas.isEmpty())
			return null;
		else
			return cas.get(0);
	}

	public List<ConflictAtom> computeConflictAtoms(boolean... earlyExit) {
		List<ConflictAtom> result = new LinkedList<ConflictAtom>();
		List<Span> candidates = new AtomCandidateComputation(rule1, rule2NonDelete).computeAtomCandidates();
		for (Span candidate : candidates) {
			Set<MinimalConflictReason> minimalConflictReasons = new HashSet<>();
			new MinimalReasonComputation(rule1, rule2NonDelete).computeMinimalConflictReasons(candidate, minimalConflictReasons);

			minimalConflictReasons.addAll(minimalConflictReasons);
			if (!minimalConflictReasons.isEmpty()) {
				result.add(new ConflictAtom(candidate, minimalConflictReasons));
			}

			if (!result.isEmpty() && earlyExit.length > 0 && earlyExit[0] == true)
				return result;
		}

		return result;
	}

	public List<Span> computeConflictAtomCandidates() {
		return new AtomCandidateComputation(rule1, rule2NonDelete).computeAtomCandidates();
	}

	public Set<MinimalConflictReason> computeMinimalConflictReasons() {
		return new MinimalReasonComputation(rule1, rule2NonDelete).computeMinimalConflictReasons();
	}

	public Set<ConflictReason> computeConflictReasons() {
		return new ConflictReasonComputation(rule1, rule2NonDelete).computeConflictReasons();
	}

	public Set<ConflictReason> computeConflictReasons(Set<MinimalConflictReason> minimalConflictReasons) {
		return new ConflictReasonComputation(rule1, rule2NonDelete).computeConflictReasons(minimalConflictReasons);
	}

	public boolean isRuleSupported(Rule rule) {
		if (rule.getMultiRules().size() > 0) {
			if (rule.getLhs().getNACs().size() > 0)
				throw new RuntimeException("negative application conditions (NAC) are not supported");
			if (rule.getLhs().getPACs().size() > 0)
				throw new RuntimeException("positive application conditions (PAC) are not supported");
		}
		return true;
	}

	public static void checkNull(Object o) throws IllegalArgumentException {
		checkNull(o, "object");
	}

	public static void checkNull(Object o, String name) throws IllegalArgumentException {
		if (null == o)
			throw new IllegalArgumentException(name + " must not be null");
	}

	public Span newSpan(Mapping nodeInRule1Mapping, Graph s1, Mapping nodeInRule2Mapping) {
		return new Span(nodeInRule1Mapping, s1, nodeInRule2Mapping);
	}

	public Span newSpan(Set<Mapping> rule1Mappings, Graph s1, Set<Mapping> rule2Mappings) {
		return new Span(rule1Mappings, s1, rule2Mappings);
	}

	public List<Span> getCandidates() {
		return new AtomCandidateComputation(rule1, rule2NonDelete).computeAtomCandidates();
	}

	public Set<MinimalConflictReason> getMinimalConflictReasons() {
		return null;
	}

	private Set<DeleteUseConflictReason> computeDeleteUseConflictReasons(Set<Span> conflictReasons){
		return new DeleteUseConflictReasonComputation(rule1, rule2,conflictReasonsFromR2).computeDeleteUseConflictReason(conflictReasons);

	}

}