package org.eclipse.emf.henshin.cpa.atomic.cc;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.MultiGranularAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.model.Rule;

public class ConflictAnalysis2 implements MultiGranularAnalysis {


	private Rule rule1;
	private Rule rule2;

	public ConflictAnalysis2(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
	}

	@Override
	public Span computeResultsBinary() {
		ConflictAtom result = hasConflicts();
		if (result == null)
			return null;
		else
			return result.getSpan();
	}

	@Override
	public Set<Span> computeResultsCoarse() {
		Set<Span> results = new HashSet<Span>();
		MinimalReasonComputation minimalReasonComputation = new MinimalReasonComputation(rule1,rule2);
		minimalReasonComputation.computeMinimalConflictReasons().forEach(r -> results.add(r));
		return results;
	}

	@Override
	public Set<Span> computeResultsFine() {
		Set<Span> results = new HashSet<Span>();
		InitialReasonComputation initialReasonComputation = new InitialReasonComputation(rule1,rule2);
		initialReasonComputation.computeInitialReasons().forEach(r -> results.add(r));
		return results;
	}

	private ConflictAtom hasConflicts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Span> computeAtoms() {
		Set<Span> results = new HashSet<Span>();
		// computeConflictAtoms().forEach(r -> results.add(r));
		return results;
	}


}

class Pair<T1, T2> {
	T1 component1;

	public T1 getComponent1() {
		return component1;
	}

	public T2 getComponent2() {
		return component2;
	}

	T2 component2;

	public Pair(T1 component1, T2 component2) {
		super();
		this.component1 = component1;
		this.component2 = component2;
	}

}
