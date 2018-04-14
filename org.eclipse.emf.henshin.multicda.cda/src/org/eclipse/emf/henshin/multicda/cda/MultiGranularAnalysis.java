package org.eclipse.emf.henshin.multicda.cda;

import java.util.Set;

public interface MultiGranularAnalysis {
	
	
	public Span computeResultsBinary();
	public Set<? extends Span> computeResultsCoarse();
	public Set<? extends Span> computeResultsFine();
	//computeCriticalPairsCpa();
	
	public Set<? extends Span> computeAtoms();
}
