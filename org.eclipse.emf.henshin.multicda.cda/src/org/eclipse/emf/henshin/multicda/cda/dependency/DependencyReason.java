package org.eclipse.emf.henshin.multicda.cda.dependency;

import java.util.Set;

import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.multicda.cda.Span;

public class DependencyReason extends Span{

	public DependencyReason(Mapping nodeInRule1Mapping, Graph s1, Mapping nodeInRule2Mapping) {
		super(nodeInRule1Mapping, s1, nodeInRule2Mapping);
	}

	public DependencyReason(Set<Mapping> rule1Mappings, Graph s1, Set<Mapping> rule2Mappings) {
		super(rule1Mappings, s1, rule2Mappings);
	}

	public DependencyReason(Span extSpan, Node origin, Node image) {
		super(extSpan, origin, image);
	}

	public DependencyReason(Span s1) {
		super(s1);
	}
	

}
