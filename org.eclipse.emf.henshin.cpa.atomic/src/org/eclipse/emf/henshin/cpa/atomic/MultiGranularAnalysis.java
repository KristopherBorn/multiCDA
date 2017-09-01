package org.eclipse.emf.henshin.cpa.atomic;

import java.util.Set;

public interface MultiGranularAnalysis {
	
	
	public Span computeResultsBinary();
	public Set<Span> computeResultsCoarse();
	public Set<Span> computeResultsFine();
	public Set<Span> computeAtoms();
}
