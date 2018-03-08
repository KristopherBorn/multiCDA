package org.eclipse.emf.henshin.multicda.cda;

import java.util.Set;

import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;
import org.eclipse.emf.henshin.multicda.cda.tester.DDSpan;

public interface MultiGranularAnalysis {
	
	
	public Span computeResultsBinary();
	public Set<Span> computeResultsCoarse();
	public Set<Span> computeResultsFine();
	public Set<Span> computeAtoms();
	public Set<InitialReason> computeInitialReasonsWithRulesDeclared(Rule r1, Rule r2);
	/**
	 * @param initialReasonsR2R1NonDel 
	 * @param initialReasonsR1R2NonDel 
	 * @return
	 */
	public Set<DeleteUseConflictReason> computeDeleteUse(Set<InitialReason> initialReasonsR1R2NonDel, Set<InitialReason> initialReasonsR2R1NonDel);
	
	
}
