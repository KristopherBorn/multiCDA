package org.eclipse.emf.henshin.cpa.atomic.dependency;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.ModelElement;

public class InitialDependencyReason extends Span{
	
	Set<MinimalDependencyReason> originMDRs;
	
	protected Set<ModelElement> creationElementsInRule1;
	
	
	public InitialDependencyReason(Set<Mapping> mappingsOfNewSpanInRule1, Graph graph1Copy,
			Set<Mapping> mappingsOfNewSpanInRule2, Set<MinimalDependencyReason> originMDRs) {
		super(mappingsOfNewSpanInRule1, graph1Copy, mappingsOfNewSpanInRule2);
		creationElementsInRule1 = new HashSet<ModelElement>();
		for(MinimalDependencyReason originMDR : originMDRs){
			creationElementsInRule1.addAll(originMDR.getCreationElementsInRule1());
		}
		this.originMDRs = originMDRs;
	}

	public InitialDependencyReason(Span s1) {
		super(s1);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the originMDRs
	 */
	public Set<MinimalDependencyReason> getOriginMDRs() {
		return originMDRs;
	}
	
	/**
	 * @return the creationElementsInRule1
	 */
	public Set<ModelElement> getCreationElementsInRule1() {
		return creationElementsInRule1;
	}
	
}
