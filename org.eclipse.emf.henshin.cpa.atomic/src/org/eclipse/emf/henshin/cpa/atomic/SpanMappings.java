package org.eclipse.emf.henshin.cpa.atomic;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;

public class SpanMappings {
	public HashMap<Node, Node> rule1ToS1;
	public HashMap<Node, Node> s1ToRule1;
	public HashMap<Node, Node> rule2ToS1;
	public HashMap<Node, Node> s1ToRule2;
	public Span span;	

	public SpanMappings(Span span) {
		this.span = span;
		rule1ToS1 = new HashMap<Node, Node>();
		s1ToRule1 = new HashMap<Node, Node>();
		for (Mapping mapping : span.getMappingsInRule1()) {
			s1ToRule1.put(mapping.getOrigin(), mapping.getImage());
			rule1ToS1.put(mapping.getImage(), mapping.getOrigin());
		}
		rule2ToS1 = new HashMap<Node, Node>();
		s1ToRule2 = new HashMap<Node, Node>();
		for (Mapping mapping : span.getMappingsInRule2()) {
			s1ToRule2.put(mapping.getOrigin(), mapping.getImage());
			rule2ToS1.put(mapping.getImage(), mapping.getOrigin());
		}
	}
	
	public Map<Edge, Edge> computeEdgeMappingsS1Rule1() {
		HashMap<Edge, Edge> result = new HashMap<Edge, Edge>();
		EList<Edge> edges = span.getGraph().getEdges();
		for (Edge eSpan  : edges) {
			Node sourceRule1 = s1ToRule1.get(eSpan.getSource());
			Node targetRule1 = s1ToRule1.get(eSpan.getTarget());
			Edge counterpart = null;
			for (Edge eR1 : sourceRule1.getOutgoing(eSpan.getType())) {
				if (eR1.getTarget() == targetRule1)
					counterpart = eR1;
			}
			if (counterpart != null)
				result.put(eSpan, counterpart);
		}
		
		return result;
	}

	public Map<Edge, Edge> computeEdgeMappingsS1Rule2() {
		HashMap<Edge, Edge> result = new HashMap<Edge, Edge>();
		EList<Edge> edges = span.getGraph().getEdges();
		for (Edge eSpan  : edges) {
			Node sourceRule2 = s1ToRule2.get(eSpan.getSource());
			Node targetRule2 = s1ToRule2.get(eSpan.getTarget());
			Edge counterpart = null;
			for (Edge eR2 : sourceRule2.getOutgoing(eSpan.getType())) {
				if (eR2.getTarget() == targetRule2)
					counterpart = eR2;
			}
			if (counterpart != null)
				result.put(eSpan, counterpart);
		}
		
		return result;
	}
	
}
