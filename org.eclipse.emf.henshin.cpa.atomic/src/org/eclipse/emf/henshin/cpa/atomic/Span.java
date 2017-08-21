package org.eclipse.emf.henshin.cpa.atomic;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

public class Span {
	
	HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;
	
	protected Rule rule1;
	protected Rule rule2;

	protected Set<Mapping> mappingsInRule1;
	protected Set<Mapping> mappingsInRule2;

	protected Graph graph;

	/**
	 * @return the rule1
	 */
	public Rule getRule1() {
		return rule1;
	}

	/**
	 * @return the rule2
	 */
	public Rule getRule2() {
		return rule2;
	}

	private Copier copierForSpanAndMappings;

	// Scheint derzeit ncoh überflüssig zu sein!
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// result = prime * result + getOuterType().hashCode();
		// result = prime * result + ((graph == null) ? 0 : graph.hashCode());
		// result = prime * result + ((mappingsInRule1 == null) ? 0 : mappingsInRule1.hashCode()); // no application
		// due to missing knwoledge on the hashCode of two lists with equal content but different order
		// result = prime * result + ((mappingsInRule2 == null) ? 0 : mappingsInRule2.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Span [mappingsInRule1=" + mappingsInRule1 + ", mappingsInRule2=" + mappingsInRule2 + ", graph: "
				+ graph.getNodes().size() + " Nodes, " + graph.getEdges().size() + " Edges" + "]";
	}
	
	public String toShortString() {
		StringBuilder sB = new StringBuilder();
		for(Edge edge : graph.getEdges()){
			sB.append(shortStringInfoOfGraphEdge(edge));
			sB.append(", ");
		}
		for(Node node : graph.getNodes()){
			sB.append(shortStringInfoOfGraphNode(node));
			sB.append(", ");
		}
		//remove last superfluous appendency
		if(sB.length()>0)
			sB.delete(sB.length()-2, sB.length());
		return "Span [" + sB.toString() + "]";
	}

	// e.g.  1,11->2,13:methods
	private Object shortStringInfoOfGraphEdge(Edge edge) {
		StringBuilder sB = new StringBuilder();
		Node src = edge.getSource();
		Node tgt = edge.getTarget();
		sB.append(getMappingIntoRule1(src).getImage().getName());
		sB.append(",");
		sB.append(getMappingIntoRule2(src).getImage().getName());
		sB.append("->");
		sB.append(getMappingIntoRule1(tgt).getImage().getName());
		sB.append(",");
		sB.append(getMappingIntoRule2(tgt).getImage().getName());
		sB.append(":");
		sB.append(edge.getType().getName());
		return sB.toString();
	}

	// e.g.: 2,3:Method
	private String shortStringInfoOfGraphNode(Node node) {
		StringBuilder sB = new StringBuilder();
		Mapping mappingIntoRule1 = getMappingIntoRule1(node);
		Mapping mappingIntoRule2 = getMappingIntoRule2(node);
		sB.append(mappingIntoRule1.getImage().getName());
		sB.append(",");
		sB.append(mappingIntoRule2.getImage().getName());
		sB.append(":");
		sB.append(node.getType().getName());
		return sB.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Span other = (Span) obj;
		// if (!getOuterType().equals(other.getOuterType())) //specific for inner class - might be irrelevant by
		// extraction to own class file!
		// return false;
		if (graph == null) { // should never happen!
			if (other.graph != null) // should never happen!
				return false;
		} else {
			// !graph.equals(other.graph)
			/*
			 * TODO: 1. vergleichen, dass die Anzahl der Elemente im Graph gleich sind 2. für jedes Element im Graph
			 * das jeweils passende im anderen Graph finden! 3. je Element prüfen, dass die Mappings das selbe Ziel
			 * haben. Vorteil hierbei ist, dass die Regeln die selben sind und osmit die ziele mit "==" verglichen
			 * werden können.
			 */
			// compare "size" of Graphs
			EList<Node> ownNodes = graph.getNodes();
			EList<Node> otherNodes = other.graph.getNodes();
			if (ownNodes.size() != otherNodes.size()) {
				return false;
			}
			EList<Edge> ownEdges = graph.getEdges();
			EList<Edge> otherEdges = other.getGraph().getEdges();
			if (ownEdges.size() != otherEdges.size()) {
				return false;
			}

			// build allocation between own graph elements and others graph elements.
			// all mappings in the first rule should be the same!
			for (Node nodeInOwnGraph : ownNodes) {
				// get mapping and node in rule1
				Mapping mappingOfNodeInRule1 = getMappingIntoRule1(nodeInOwnGraph);
				if (mappingOfNodeInRule1 == null)
					return false; // TODO: isnt that a bug instead of an unequal Span?
				// the nodes of the span should all have a mapping into both rules.
				// somehow the nodes in the graph of the span or the nodes in the mapping must bewrong.
				// it shouldnt be possible to change the graph and the mappings of a span and everytime they are
				// created they should be checked to be consistent!
				Node associatedNodeInRule1 = mappingOfNodeInRule1.getImage();
				// get mapping in otherGraph
				Mapping mappingOfOtherNodeInRule1 = other.getMappingFromGraphToRule1(associatedNodeInRule1);
				if (mappingOfOtherNodeInRule1 == null)
					return false;
				Node nodeInOtherGraph = mappingOfOtherNodeInRule1.getOrigin();
				// check that both mappings in rule 2 have the same target
				Mapping mappingOfNodeInRule2 = getMappingIntoRule2(nodeInOwnGraph);
				if (getMappingIntoRule2(nodeInOwnGraph) == null)
					System.out.println("bla");

				// gibt NULL zurück, wenn es kein passendes mapping gibt!
				Node associatedNodeInRule2 = mappingOfNodeInRule2.getImage();
				Mapping mappingOfOtherNodeInRule2 = other.getMappingIntoRule2(nodeInOtherGraph);
				Node associatedNodeOfOtherGraphInRule2 = mappingOfOtherNodeInRule2.getImage();
				if (!(associatedNodeOfOtherGraphInRule2 == associatedNodeInRule2))
					return false;
			}
			return true;

			//

			// return false;
		}
		// if (mappingsInRule1 == null) {
		// if (other.mappingsInRule1 != null)
		// return false;
		// } else if (!mappingsInRule1.equals(other.mappingsInRule1))
		// return false;
		// if (mappingsInRule2 == null) {
		// if (other.mappingsInRule2 != null)
		// return false;
		// } else if (!mappingsInRule2.equals(other.mappingsInRule2))
		// return false;
		return false;
	}

	/**
	 * @return the mappingsInRule1
	 */
	public Set<Mapping> getMappingsInRule1() {
		return mappingsInRule1;
	}

	/**
	 * @return the mappingsInRule2
	 */
	public Set<Mapping> getMappingsInRule2() {
		return mappingsInRule2;
	}

	public Span(Mapping nodeInRule1Mapping, Graph s1, Mapping nodeInRule2Mapping) {
		//TODO: introduce a check, that all mappings for rule1 are targeting to a common rule
		//TODO: introduce a check, that all mappings for rule2 are targeting to a common rule
		//TODO: introduce a check that rule 1 != rule2
		//TODO: afterwards "pushout" tests have to be adapted!
		this.graph = s1;
		mappingsInRule1 = new HashSet<Mapping>();
		mappingsInRule1.add(nodeInRule1Mapping);
		mappingsInRule2 = new HashSet<Mapping>();
		mappingsInRule2.add(nodeInRule2Mapping);
	}

	public Mapping getMappingFromGraphToRule2(Node imageNode) {
		for (Mapping mappingInRule2 : mappingsInRule2) {
			if (mappingInRule2.getImage() == imageNode)
				return mappingInRule2;
		}
		return null;
	}

	public Mapping getMappingFromGraphToRule1(Node imageNode) {
		for (Mapping mappingInRule1 : mappingsInRule1) {
			if (mappingInRule1.getImage() == imageNode)
				return mappingInRule1;
		}
		return null;
	}

	/**
	 * returns the kernel rule of the first mapping or <code>null</code> if the set <code>mappings</code> is empty.
	 * @param mappings
	 * @return a <code>Rule</code> or null. 
	 */
	private Rule getRuleOfMappings(Set<Mapping> mappings) {
		try {
			return mappings.iterator().next().getImage().getGraph().getRule();
		} catch (Exception e) {
			// nothing to do here
		}
		return null;
	}

	public Span(Set<Mapping> rule1Mappings, Graph s1, Set<Mapping> rule2Mappings) {
		this.mappingsInRule1 = rule1Mappings; //TODO: wie verhält es sich mit einem leeren Graph, bzw. leeren mappngs?
		this.mappingsInRule2 = rule2Mappings;
		this.graph = s1;
		this.rule1 = getRuleOfMappings(rule1Mappings); // might return null. Needs to be improved. if rules are not set NPE might occure. 
		this.rule2 = getRuleOfMappings(rule2Mappings);
	}

	public Span(Span s1) {
		// copy Graph and mappings!
		// Copier
		copierForSpanAndMappings = new Copier();
		// copy of graph
		Graph copiedGraph = (Graph) copierForSpanAndMappings.copy(s1.getGraph());
		copierForSpanAndMappings.copyReferences();
		this.graph = copiedGraph;

		// TODO: extract to method
		Set<Mapping> mappingsInRule1 = new HashSet<Mapping>();
		for (Mapping mapping : s1.getMappingsInRule1()) {
			Mapping copiedMapping = (Mapping) copierForSpanAndMappings.copy(mapping);
			copierForSpanAndMappings.copyReferences();
			mappingsInRule1.add(copiedMapping);
		}
		this.mappingsInRule1 = mappingsInRule1;

		Set<Mapping> mappingsInRule2 = new HashSet<Mapping>();
		for (Mapping mapping : s1.getMappingsInRule2()) {
			Mapping copiedMapping = (Mapping) copierForSpanAndMappings.copy(mapping);
			copierForSpanAndMappings.copyReferences();
			mappingsInRule2.add(copiedMapping);
		}
		this.mappingsInRule2 = mappingsInRule2;

		this.rule1 = getRuleOfMappings(mappingsInRule1); 
		this.rule2 = getRuleOfMappings(mappingsInRule2);
	}
	

	public Span (Span extSpan, Node origin, Node image) {
		this(extSpan);
		Node transformedOrigin = (Node) copierForSpanAndMappings.get(origin);
		
		Mapping r2Mapping = henshinFactory.createMapping(transformedOrigin,
				image);
		mappingsInRule2.add(r2Mapping);
	}

	public Graph getGraph() {
		return graph;
	}

	// TODO use "getMappingWithImage(...)" method
	public Mapping getMappingIntoRule1(Node originNode) {
		for (Mapping mapping : mappingsInRule1) {
			if (mapping.getOrigin() == originNode)
				return mapping;
		}
		return null;
	}

	// TODO use "getMappingWithImage(...)" method
	public Mapping getMappingIntoRule2(Node originNode) {
		for (Mapping mapping : mappingsInRule2) {
			if (mapping.getOrigin() == originNode)
				return mapping;
		}
		return null;
	}

	//superfluous
//	private AtomicCoreCPA getOuterType() {
//		return AtomicCoreCPA.this;
//	}

	//TODO: prüfen, dass es zu jedem Knoten im Graph des Span zwei Mappings gibt, die dem Span-Knoten jeweils einen Knoten in der LHS der Regel1 und Regel2 zuordnet
	//		FRAGE: auch prüfen, dass die Kanten im Span auch in den beiden Regeln vorhanden sind?
	public boolean validate(Rule rule1, Rule rule2) {
		//missing or superfluous mappings or nodes in the graph of the span
		if(mappingsInRule1.size() != graph.getNodes().size() || mappingsInRule2.size() != graph.getNodes().size())
			return false;
		// check all nodes of the graph of the span for valid mappings in the rules
		for(Node node : graph.getNodes()){
			Mapping mappingIntoRule1 = getMappingIntoRule1(node);
			if(mappingIntoRule1.getImage() == null)
				return false;
			Node imageInRule1 = mappingIntoRule1.getImage();
			if(imageInRule1.eContainer() != rule1.getLhs())
				return false;
//			if(imageInRule1.getType() !=  node.getType()) //TODO: fix this regarding inheritance!
//				return false;
				//TODO: muss gleicher, sub oder supertype sein
			Mapping mappingIntoRule2 = getMappingIntoRule2(node);
			if(mappingIntoRule2.getImage() == null)
				return false;
			Node imageInRule2 = mappingIntoRule2.getImage();
			if(imageInRule2.eContainer() != rule2.getLhs())
				return false;
//			if(imageInRule2.getType() !=  node.getType()) //TODO: fix this regarding inheritance!
//				return false;
			//TODO: muss gleicher, sub oder supertype sein
			
		}
		// Edges of the graph could be checked additionally.
		// If this is done some tests should be set up as negative examples for such situations.
		return true;
	}

}
