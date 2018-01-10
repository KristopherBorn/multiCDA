/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.computation;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.SpanMappings;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;
import org.eclipse.emf.henshin.multicda.cda.runner.RulePreparator;

/**
 * @author vincentcuccu
 *
 */
public class DeleteReadConflictReasonComputation {

	private Rule rule1;
	private Rule rule2;
	private HashSet<Span> checked;

	/**
	 * @param rule1
	 * @param rule2
	 */
	public DeleteReadConflictReasonComputation(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		setChecked(new HashSet<Span>());
	}
	
	/*
	 * Initialize DRCR = empty set	 	 
	 * Construct all conflict reasons (s1: C1 <-- S1 --> L2) for (r1,r2') //r2' non-deleting variant of r2
	 * 
	 * For each s1:
	 * If (there exists embedding S1 -> K2 with S1 -> K2 -> L2 = S1 -> L2)
	 * s1' = extendSpan(s1,c1: C1 --> L1) //after extension: (s1': L1 <-- C1 <-- S1 --> L2)
	 * Compute (L1 -m1-> G <-m2- L2) as PO of s1'
	 * If (r1,m1:L1 --> G) and (r2,m2: L2 --> G) fulfill the dangling condition
	 * Then add s1 to DRCR
	 * return DRCR
	 */ 

	/**
	 * constructs all Initial Reasons as candidates for r1 and r2
	 * 
	 * @return result
	 */
	public Set<DeleteReadConflictReason> computeDeleteReadConflictReason() {
		Set<DeleteReadConflictReason> result = new HashSet<DeleteReadConflictReason>();
		Set<InitialReason> initialReasons = new InitialReasonComputation(rule1, rule2).computeInitialReasons();
		for (InitialReason initalReason : initialReasons) {
			computeDeleteReadConflictReason(initalReason, result);
		}
		return result;
	}

	private void computeDeleteReadConflictReason(InitialReason initialReason, Set<DeleteReadConflictReason> result) {
		Graph lhs1 = initialReason.getRule1().getLhs();
		Rule rule1 = initialReason.getRule1();
		Rule rule2 = initialReason.getRule2();
		//Graph lhs2 = RulePreparator.prepareRule(initialReason.getRule2()).getLhs();
		//Set<ModelElement> c1 = initialReason.getDeletionElementsInRule1();
		Graph s1 = initialReason.getGraph();
		HashSet<Mapping> s1tol1 = new HashSet<Mapping>();
		EList<Node> s1Nodes = s1.getNodes();
		EList<Node> lhs1Nodes = lhs1.getNodes();
		EList<Node> k2Nodes = rule2.getRootRule().getLhs().getNodes();
		
		if (findEmbeddingS1toK2(s1Nodes, k2Nodes, lhs1Nodes)){// If (there exists embedding S1 -> K2 with S1 -> K2 -> L2 = mappingsInRule2) {
		
		s1tol1 = computeMappings(s1Nodes, lhs1Nodes); // s1' = extendSpan(s1,c1: C1 --> L1) //after extension: (s1': L1 <-- C1 <-- S1 --> L2)
		
		Pushout pushout = new Pushout(rule1, initialReason, rule2);// Compute (L1 -m1-> G <-m2- L2) as PO of s1'
				if (true) {// If (r1,m1:L1 --> G) and (r2,m2: L2 --> G) fulfill the dangling condition{
			result.add(new DeleteReadConflictReason(initialReason));// Then add s1 to DRCR
		}
		}
	}

	private boolean findEmbeddingS1toK2(EList<Node> s1Nodes, EList<Node> k2Nodes, EList<Node> lhs1Nodes) {
		
		HashSet<Mapping> computeMappingsS1K2 = computeMappings(s1Nodes, k2Nodes);
		HashSet<Mapping> mappingsInRule22 = computeMappings(s1Nodes, lhs1Nodes);
		for (Mapping mapping:computeMappingsS1K2){
			if (!mappingsInRule22.contains(mapping));
			return false;
		}
		return true;
	}

	

	/**
	 * @param henshinFactory
	 * @param graphNodes1
	 * @param graphNodes2
	 * @return 
	 */
	private HashSet<Mapping> computeMappings(EList<Node> graphNodes1, EList<Node> graphNodes2) {
		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE; //wird zur Erstellung der Mappings benötigt
		HashSet<Mapping> G1toG2 = new HashSet<Mapping>();
		
		for (Node origin : graphNodes1) { //Alle Knoten in S1 sollen auf alle Knoten in L1 gemappt werden
			for (Node image : graphNodes2) {
				if (origin.getType() == image.getType()) { //Nur wenn Typen gleich sind.
					Mapping mapping = henshinFactory.createMapping(origin, image);
					G1toG2.add(mapping);
				}
			}
		}
		return G1toG2;
	}

	private SpanMappings mappings(Span span) {
		return new SpanMappings(span);
		
	}
	

	

	// für Compute PO: result = new Pushout(rule1, s1, rule2);

	/**
	 * @return checked
	 */
	public HashSet<Span> getChecked() {
		return checked;
	}

	/**
	 * @param checked
	 */
	public void setChecked(HashSet<Span> checked) {
		this.checked = checked;
	}
	
}
