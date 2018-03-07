package org.eclipse.emf.henshin.multicda.cda;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;
import org.eclipse.emf.henshin.multicda.cda.computation.AtomCandidateComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.ConflictReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.DeleteUseConflictReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.MinimalReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;

public class ConflictAnalysis implements MultiGranularAnalysis {

	private Rule rule1;
	private Rule rule2;
	HenshinFactory henshinFactory = new HenshinFactoryImpl();
	
	public ConflictAnalysis(Rule rule1, Rule rule2) {
		checkNull(rule1);
		checkNull(rule2);
		this.rule1 = rule1;
		this.rule2 = rule2;
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
		computeConflictReasons().forEach(r -> results.add(r));
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
		List<Span> candidates = new AtomCandidateComputation(rule1, rule2).computeAtomCandidates();
		for (Span candidate : candidates) {
			Set<MinimalConflictReason> minimalConflictReasons = new HashSet<>();
			new MinimalReasonComputation(rule1, rule2).computeMinimalConflictReasons(candidate, minimalConflictReasons);

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
		return new AtomCandidateComputation(rule1, rule2).computeAtomCandidates();
	}

	public Set<MinimalConflictReason> computeMinimalConflictReasons() {
		return new MinimalReasonComputation(rule1, rule2).computeMinimalConflictReasons();
	}

	public Set<ConflictReason> computeConflictReasons() {
		return new ConflictReasonComputation(rule1, rule2).computeConflictReasons();
	}

	public Set<ConflictReason> computeCoflictReasons(Set<MinimalConflictReason> minimalConflictReasons) {
		return new ConflictReasonComputation(rule1, rule2).computeInitialReasons(minimalConflictReasons);
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
		return new AtomCandidateComputation(rule1, rule2).computeAtomCandidates();
	}

	public Set<MinimalConflictReason> getMinimalConflictReasons() {
		return null;
	}


	/**
	 * @param initialReasonsR2R1NonDel 
	 * @param initialReasonsR1R2NonDel 
	 * @return
	 */
	public Set<DeleteReadConflictReason> computeDeleteConflictReasons(Set<ConflictReason> initialReasonsR1R2NonDel, Set<ConflictReason> initialReasonsR2R1NonDel) {
		return new DeleteUseConflictReasonComputation(rule1, rule2).computeDeleteUseConflictReason(initialReasonsR1R2NonDel, initialReasonsR2R1NonDel);
	}
	
	public Set<DeleteUseConflictReason> computeDeleteUse(Set<ConflictReason> initialReasonsR1R2NonDel, Set<ConflictReason> initialReasonsR2R1NonDel) {
		Set<DeleteUseConflictReason> results = new HashSet<DeleteUseConflictReason>();
		computeDeleteConflictReasons(initialReasonsR1R2NonDel, initialReasonsR2R1NonDel).forEach(r -> results.add(r));
		return results;

	}

}