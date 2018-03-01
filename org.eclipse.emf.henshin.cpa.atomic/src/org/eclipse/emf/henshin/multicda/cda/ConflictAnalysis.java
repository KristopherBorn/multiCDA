package org.eclipse.emf.henshin.multicda.cda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.formula.ptg.MemErrPtg;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;
import org.eclipse.emf.henshin.multicda.cda.computation.AtomCandidateComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.DeleteDeleteConflictReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.DeleteUseConflictReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.InitialReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.MinimalReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.multicda.cda.tester.DDSpan;

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
		computeConflictAtoms().forEach(r -> results.add(r.span));
		return results;
	}

	@Override
	public Span computeResultsBinary() {
		ConflictAtom result = hasConflicts();
		if (result == null)
			return null;
		else
			return result.span;
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
		computeInitialReasons().forEach(r -> results.add(r));
		return results;
	}
	
	public Set<Span> computeResultsFineBackwards() {
		Set<Span> results = new HashSet<Span>();
		computeInitialReasonsFromRule2ToRule1().forEach(r -> results.add(r));
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

	/**
	 * @return
	 */
	public Set<DeleteReadConflictReason> computeDeleteReadConflictReasons() {
		return new DeleteUseConflictReasonComputation(rule1, rule2).computeDeleteUseConflictReason();
	}

	public Set<InitialReason> computeInitialReasons() {
		return new InitialReasonComputation(rule1, rule2).computeInitialReasons();
	}
	
	public Set<InitialReason> computeInitialReasonsFromRule2ToRule1() {
		return new InitialReasonComputation(rule2, rule1).computeInitialReasons();
	}

	public Set<InitialReason> computeInitialReasons(Set<MinimalConflictReason> minimalConflictReasons) {
		return new InitialReasonComputation(rule1, rule2).computeInitialReasons(minimalConflictReasons);
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

	@Override
	public Set<DeleteUseConflictReason> computeDeleteUse() {
		Set<DeleteUseConflictReason> results = new HashSet<DeleteUseConflictReason>();
		computeDeleteReadConflictReasons().forEach(r -> results.add(r));
		return results;

	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.emf.henshin.multicda.cda.MultiGranularAnalysis#computeDDCR()
//	 */
//	@Override
//	public Set<DDSpan> computeDDCR() {
//		// TODO Vincent Compute
//		Set<DDSpan> result = new HashSet<DDSpan>();
//		computeDeleteDeleteConflictReasons().forEach(r -> result.add(r));
//		return result;
//	}

	/**
	 * @return
	 */
	private Set<DDSpan> computeDeleteDeleteConflictReasons() {
		return new DeleteDeleteConflictReasonComputation(rule1, rule2).computeDeleteDeleteConflictReason();
	}
}