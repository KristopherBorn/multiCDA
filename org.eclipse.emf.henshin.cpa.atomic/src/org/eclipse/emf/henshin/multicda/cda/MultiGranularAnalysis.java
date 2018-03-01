package org.eclipse.emf.henshin.multicda.cda;

import java.util.Set;

import org.eclipse.emf.henshin.multicda.cda.tester.DDSpan;

public interface MultiGranularAnalysis {
	
	
	public Span computeResultsBinary();
	public Set<Span> computeResultsCoarse();
	public Set<Span> computeResultsFine();
	public Set<Span> computeAtoms();
	public Set<DeleteUseConflictReason> computeDRCR();
	public Set<DDSpan> computeDDCR();
	/**
	 * @return
	 */
	Set<Span> computeResultsFineBackwards();
	
	
}
