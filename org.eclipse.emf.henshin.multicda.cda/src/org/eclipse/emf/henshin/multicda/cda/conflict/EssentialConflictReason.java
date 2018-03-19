package org.eclipse.emf.henshin.multicda.cda.conflict;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.SpanMappings;

//nur der Vollst�ndigkeit eingef�hrt und zur Doifferenzierung 
// zur Bildung der entsprechenden Ergebnisse f�r Abgleiche mit ess. CPA
public class EssentialConflictReason extends ConflictReason {
	

	Set<ConflictAtom> additionalConflictAtoms;



	public EssentialConflictReason(ConflictReason initialReason, Node boundaryNodeOfCA, Node usedNodeInLhsOfR2, ConflictAtom additionalConflictAtom) {
//		eigene Kopie des S1 Graph
//		eigene Kopie der Mappings in R1
//		eigene Kopie der Mappings in R2
		super(initialReason); // erledigt alles! 
		
		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;
		
		// lhs boundary node of rule 1
		Node boundaryNodeOfRule1 = additionalConflictAtom.getMappingIntoRule1(boundaryNodeOfCA).getImage();
		
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
		//wenn das urspr�ngliche "InitialConflictReason initialReason" bereits ein CR ist, 
		// so m�ssen dessen additionallyInvolvedConflictAtoms auch noch dem neuen CR hinzugef�gt werden.
		if(initialReason instanceof EssentialConflictReason){
			additionalConflictAtoms.addAll(((EssentialConflictReason) initialReason).getAdditionallyInvolvedConflictAtoms());
		}
	}

	public EssentialConflictReason(ConflictReason initialReason) {
		super(initialReason); // erledigt alles! 
		additionalConflictAtoms = new HashSet<ConflictAtom>();
	}

	public EssentialConflictReason(Span span) {
		super(span); 
		additionalConflictAtoms = new HashSet<ConflictAtom>();
	}


	public Set<Node> getLhsNodesOfR2UsedByAdditionalCAs() {
		Set<Node> result = new HashSet<Node>();
		for(ConflictAtom ca : additionalConflictAtoms){
			Set<Mapping> mappingsInRule2 = ca.getMappingsInRule2();
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

	public Pushout getPushoutResult() {
		// 		- Notwendig f�r die h�ufig Nutzung mit dem Comparator
		return new Pushout(rule1, this, rule2);
	}



	
	
	
}
