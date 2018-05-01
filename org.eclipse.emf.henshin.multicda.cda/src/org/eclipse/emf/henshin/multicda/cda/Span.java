package org.eclipse.emf.henshin.multicda.cda;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

public class Span implements Comparable<Span> {

	HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;

	private Rule rule1;
	private Rule rule2;

	public Set<Mapping> mappingsInRule1;
	public Set<Mapping> mappingsInRule2;

	public Graph graph;

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
		for (Edge edge : graph.getEdges()) {
			sB.append(shortStringInfoOfGraphEdge(edge));
			sB.append(", ");
		}
		for (Node node : graph.getNodes()) {
			sB.append(shortStringInfoOfGraphNode(node));
			sB.append(", ");
		}
		//remove last superfluous appendency
		if (sB.length() > 0)
			sB.delete(sB.length() - 2, sB.length());
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		Span other = (Span) obj;

		if (this == obj)
			return true;
		if (graph == null || other.graph == null)
			return false;
		if (!(obj instanceof Span))
			return false;
		if (graph.getNodes().size() != other.getGraph().getNodes().size())
			return false;
		if (graph.getEdges().size() != other.getGraph().getEdges().size())
			return false;
		if (mappingsInRule1.size() != other.getMappingsInRule1().size())
			return false;
		if (mappingsInRule2.size() != other.getMappingsInRule2().size())
			return false;

		SpanMappings spanMap = new SpanMappings(this);
		SpanMappings spanMapOther = new SpanMappings(other);

		// Are same nodes in rules 1 and 2 used?
		Set<Node> nodesRule1 = new HashSet<Node>(spanMap.s1ToRule1.values());
		Set<Node> nodesRule2 = new HashSet<Node>(spanMap.s1ToRule2.values());
		Set<Node> nodesRule1Other = new HashSet<Node>(spanMapOther.s1ToRule1.values());
		Set<Node> nodesRule2Other = new HashSet<Node>(spanMapOther.s1ToRule2.values());
		if (!(nodesRule1.equals(nodesRule1Other) && nodesRule2.equals(nodesRule2Other)))
			return false;

		// Are same edges in rules 1 and 2 used?
		Map<Edge, Edge> edgeMapS1R1 = spanMap.getEdgeMappingsS1Rule1();
		Map<Edge, Edge> edgeMapS1R2 = spanMap.getEdgeMappingsS1Rule2();
		Map<Edge, Edge> edgeMapS1R1Other = spanMapOther.getEdgeMappingsS1Rule1();
		Map<Edge, Edge> edgeMapS1R2Other = spanMapOther.getEdgeMappingsS1Rule2();

		Set<Edge> edgesRule1 = new HashSet<Edge>(edgeMapS1R1.values());
		Set<Edge> edgesRule2 = new HashSet<Edge>(edgeMapS1R2.values());
		Set<Edge> edgesRule1Other = new HashSet<Edge>(edgeMapS1R1Other.values());
		Set<Edge> edgesRule2Other = new HashSet<Edge>(edgeMapS1R2Other.values());
		if (!(edgesRule1.equals(edgesRule1Other) && edgesRule2.equals(edgesRule2Other)))
			return false;

		// Do both CRs map the span graph nodes to the same nodes in rules 1 and 2?
		Map<Node, Node> paired = getPairedNodes(this, spanMap);
		Map<Node, Node> pairedOther = getPairedNodes(other, spanMapOther);
		for (Node e1 : paired.keySet()) {
			if (paired.get(e1) != pairedOther.get(e1))
				return false;
		}

		return true;
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
	 * 
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
		this.mappingsInRule1 = rule1Mappings; //wie verh�lt es sich mit einem leeren Graph, bzw. leeren mappngs?
		this.mappingsInRule2 = rule2Mappings;
		this.graph = s1;
		this.setRule1(getRuleOfMappings(rule1Mappings)); // might return null. Needs to be improved. if rules are not set NPE might occure. 
		this.setRule2(getRuleOfMappings(rule2Mappings));
	}

	public Span(Span s1) {
		// copy Graph and mappings!
		// Copier
		copierForSpanAndMappings = new Copier();
		// copy of graph
		Graph copiedGraph = (Graph) copierForSpanAndMappings.copy(s1.getGraph());
		copierForSpanAndMappings.copyReferences();
		this.graph = copiedGraph;

		// extract to method
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

		this.setRule1(getRuleOfMappings(mappingsInRule1));
		this.setRule2(getRuleOfMappings(mappingsInRule2));
	}

	public Span(Span extSpan, Node origin, Node image) {
		this(extSpan);
		Node transformedOrigin = (Node) copierForSpanAndMappings.get(origin);

		Mapping r2Mapping = henshinFactory.createMapping(transformedOrigin, image);
		mappingsInRule2.add(r2Mapping);
	}

	public Graph getGraph() {
		return graph;
	}

	public Mapping getMappingIntoRule1(Node originNode) {
		for (Mapping mapping : mappingsInRule1) {
			if (mapping.getOrigin().equals(originNode))
				return mapping;
		}
		return null;
	}

	public Mapping getMappingIntoRule2(Node originNode) {
		for (Mapping mapping : mappingsInRule2) {
			if (mapping.getOrigin().equals(originNode))
				return mapping;
		}
		return null;
	}

	public boolean validate(Rule rule1, Rule rule2) {
		if (mappingsInRule1.size() != graph.getNodes().size() || mappingsInRule2.size() != graph.getNodes().size())
			return false;
		for (Node node : graph.getNodes()) {
			Mapping mappingIntoRule1 = getMappingIntoRule1(node);
			if (mappingIntoRule1.getImage() == null)
				return false;
			Node imageInRule1 = mappingIntoRule1.getImage();
			if (imageInRule1.eContainer() != rule1.getLhs())
				return false;
			Mapping mappingIntoRule2 = getMappingIntoRule2(node);
			if (mappingIntoRule2.getImage() == null)
				return false;
			Node imageInRule2 = mappingIntoRule2.getImage();
			if (imageInRule2.eContainer() != rule2.getLhs())
				return false;

		}
		return true;
	}

	private Map<Node, Node> getPairedNodes(Span conflictReason, SpanMappings spanMap) {
		Map<Node, Node> result = new HashMap<Node, Node>();
		for (Node n1 : spanMap.s1ToRule1.keySet()) {
			result.put(spanMap.s1ToRule1.get(n1), spanMap.s1ToRule2.get(n1));
		}
		return result;
	}

	/**
	 * @param graph
	 * @return
	 */
	public EPackage graphToEPackage() {
		Set<String> added = new HashSet<String>();
		EPackage result = EcoreFactory.eINSTANCE.createEPackage();
		result.setName(getRule1().getName() + "_" + getRule2().getName());
		result.setNsURI(
				"http://cdapackage/" + getRule1().getName() + "/" + getRule2().getName() + "/" + getClass().getSimpleName());
		result.setNsPrefix("CDAPackage");
		EList<EClassifier> classifiers = result.getEClassifiers();

		for (Node node : graph.getNodes()) {
			EClass n = getClassifier(node);
			added.add(n.getName());
			result.getEClassifiers().add(n);
		}

		for (Edge edge : graph.getEdges()) {
			EClass s = getClassifier(edge.getSource());
			EClass t = getClassifier(edge.getTarget());

			if (!added.contains(s.getName())) {
				classifiers.add(s);
				added.add(s.getName());
			} else
				s = (EClass) result.getEClassifier(s.getName());
			if (!added.contains(t.getName())) {
				classifiers.add(t);
				added.add(t.getName());
			} else
				t = (EClass) result.getEClassifier(t.getName());

			EReference ref = EcoreFactory.eINSTANCE.createEReference();
			ref.setName(edge.getType().getName());
			if (!getRule1().getRhs().getEdges().contains(edge)) {
				ref.setName("#" + ref.getName() + "#");
			}
			ref.setEType(t);
			s.getEStructuralFeatures().add(ref);

		}

		return result;
	}

	private EClass getClassifier(Node node) {
		EClassifier nodeClass = node.getType();
		EClass eclass = EcoreFactory.eINSTANCE.createEClass();
		eclass.setName(node.getName() + ":" + nodeClass.getName());

		Node image = getMappingIntoRule1(node).getImage();
		if (image.getAction().getType() == Type.DELETE)
			eclass.setName("#" + eclass.getName() + "#");
		return eclass;
	}

	public void setRule1(Rule rule1) {
		this.rule1 = rule1;
	}

	public void setRule2(Rule rule2) {
		this.rule2 = rule2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Span o) {
		if(o==null)
			return 1;
		return o.toShortString().compareTo(toShortString());
	}

}
