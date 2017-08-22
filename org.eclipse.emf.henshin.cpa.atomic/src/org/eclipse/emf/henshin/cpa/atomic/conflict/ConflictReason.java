package org.eclipse.emf.henshin.cpa.atomic.conflict;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.PushoutResult;
import org.eclipse.emf.henshin.cpa.atomic.SpanMappings;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;

//nur der Vollständigkeit eingeführt und zur Doifferenzierung 
// zur Bildung der entsprechenden Ergebnisse für Abgleiche mit ess. CPA
public class ConflictReason extends InitialConflictReason {
	

	Set<ConflictAtom> additionalConflictAtoms;

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		ConflictReason other = (ConflictReason) obj;

		if (this == obj)
			return true;
		if (graph == null || other.graph == null)
			return false;
		if (!(obj instanceof ConflictReason))
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
		
		Set<Node> nodesRule1 = new HashSet(spanMap.s1ToRule1.values());
		Set<Node> nodesRule2 = new HashSet(spanMap.s1ToRule2.values());
		Set<Node> nodesRule1Other = new HashSet(spanMapOther.s1ToRule1.values());
		Set<Node> nodesRule2Other = new HashSet(spanMapOther.s1ToRule2.values());
		if (!(nodesRule1.equals(nodesRule1Other) &&  nodesRule2.equals(nodesRule2Other)))
				return false;
		
		Map<Edge, Edge> edgeMapS1R1 = spanMap.computeEdgeMappingsS1Rule1();
		Map<Edge, Edge> edgeMapS1R2 = spanMap.computeEdgeMappingsS1Rule2();
		Map<Edge, Edge> edgeMapS1R1Other = spanMapOther.computeEdgeMappingsS1Rule1();
		Map<Edge, Edge> edgeMapS1R2Other = spanMapOther.computeEdgeMappingsS1Rule2();
		
		Set<Edge> edgesRule1 = new HashSet(edgeMapS1R1.values());
		Set<Edge> edgesRule2 = new HashSet(edgeMapS1R2.values());
		Set<Edge> edgesRule1Other = new HashSet(edgeMapS1R1Other.values());
		Set<Edge> edgesRule2Other = new HashSet(edgeMapS1R2Other.values());
		if (!(edgesRule1.equals(edgesRule1Other) &&edgesRule2.equals(edgesRule2Other)))
			return false;

		Map<Edge,Edge> paired = getPairedEdges(edgeMapS1R1, edgeMapS1R2);
		Map<Edge,Edge> pairedOther = getPairedEdges(edgeMapS1R1Other, edgeMapS1R2Other);
		for (Edge e1 : paired.keySet()) {
			if (paired.get(e1) != pairedOther.get(e1))
				return false;
		}
		
		return true;
	}
	

	public ConflictReason(InitialConflictReason initialReason, Node boundaryNodeOfCA, Node usedNodeInLhsOfR2, ConflictAtom additionalConflictAtom) {
//		eigene Kopie des S1 Graph
//		eigene Kopie der Mappings in R1
//		eigene Kopie der Mappings in R2
		super(initialReason); // erledigt alles! 
		
		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;
		
		//TODO: 
		// lhs boundary node of rule 1
		Node boundaryNodeOfRule1 = additionalConflictAtom.getSpan().getMappingIntoRule1(boundaryNodeOfCA).getImage();
		
		// - hinzufuegen des use-nodes zum graph
		String nameOfNewBoundaryNode = boundaryNodeOfRule1.getName()+"_"+usedNodeInLhsOfR2.getName();
		Node newBoundaryNodeInSpan = henshinFactory.createNode(graph, boundaryNodeOfCA.getType(), nameOfNewBoundaryNode);
		// - mapping erstellen
		Mapping mappingInR1 = henshinFactory.createMapping(newBoundaryNodeInSpan, boundaryNodeOfRule1);
		mappingsInRule1.add(mappingInR1);
		Mapping mappingInR2 = henshinFactory.createMapping(newBoundaryNodeInSpan, usedNodeInLhsOfR2);
		mappingsInRule2.add(mappingInR2);
		// ggf. pruefen, dass es keine zu loeschende Kante gibt und somit kein vollstaendiges atom ist 
		// 		(das waere schon durch die initialReason abgedeckt!!) 
		
		additionalConflictAtoms = new HashSet<ConflictAtom>();
		additionalConflictAtoms.add(additionalConflictAtom);
		//wenn das ursprüngliche "InitialConflictReason initialReason" bereits ein CR ist, 
		// so müssen dessen additionallyInvolvedConflictAtoms auch noch dem neuen CR hinzugefügt werden.
		if(initialReason instanceof ConflictReason){
			additionalConflictAtoms.addAll(((ConflictReason) initialReason).getAdditionallyInvolvedConflictAtoms());
		}
	}

	public ConflictReason(InitialConflictReason initialReason) {
		super(initialReason); // erledigt alles! 
		additionalConflictAtoms = new HashSet<ConflictAtom>();
	}

	public Set<Node> getLhsNodesOfR2UsedByAdditionalCAs() {
		Set<Node> result = new HashSet<Node>();
		for(ConflictAtom ca : additionalConflictAtoms){
			Set<Mapping> mappingsInRule2 = ca.getSpan().getMappingsInRule2();
			for(Mapping mappingInRule2 : mappingsInRule2){
				result.add(mappingInRule2.getImage());
			}
		}
		return result;
	}

	public Set<Node> getUsedLhsNodesOfR2() {
		Set<Node> result = new HashSet<Node>();
		for(Mapping map : mappingsInRule2){
			result.add(map.getImage());
		}
		return result;
	}

	/**
	 * @return the additionallyInvolvedConflictAtoms
	 */
	public Set<ConflictAtom> getAdditionallyInvolvedConflictAtoms() {
		return additionalConflictAtoms;
	}

	// superfluous
//	private AtomicCoreCPA getOuterType() {
//		return AtomicCoreCPA.this;
//	}

	public PushoutResult getPushoutResult() {
		// TODO vielleicht einführen eines Feldes, womit das einmal erzeugte PoR gehalten wird (anstelle es wiederholt zu erzeugen) 
		// 		- Notwendig für die häufig Nutzung mit dem Comparator
		return new PushoutResult(rule1, this, rule2);
	}
	
	private Map<Edge, Edge> getPairedEdges(Map<Edge, Edge> map1, Map<Edge, Edge> map2) {
		Map<Edge,Edge> result = new HashMap<Edge, Edge>();
		for (Edge e1 : map1.keySet()) 
			result.put(map1.get(e1), map2.get(e1));
		for (Edge e2 : map2.keySet()) 
			result.put(map2.get(e2), map1.get(e2));			
		return result;
	}
	
}
