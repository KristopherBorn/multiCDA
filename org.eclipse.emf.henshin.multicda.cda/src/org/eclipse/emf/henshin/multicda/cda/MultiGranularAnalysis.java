package org.eclipse.emf.henshin.multicda.cda;

import java.util.Set;

public interface MultiGranularAnalysis {
	
	
	public Span computeResultsBinary();
	public Set<Span> computeResultsCoarse();
	public Set<Span> computeResultsFine();
	//computeCriticalPairsCpa();
	
	public Set<Span> computeAtoms();
}
