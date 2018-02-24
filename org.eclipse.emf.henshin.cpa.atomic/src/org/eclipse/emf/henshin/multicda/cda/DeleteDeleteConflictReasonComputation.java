/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate.Factory;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.computation.DeleteReadConflictReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.InitialReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.computation.MinimalReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;
import org.eclipse.emf.henshin.multicda.cda.tester.DDSpan;
import org.eclipse.emf.henshin.preprocessing.NonDeletingPreparator;

/**
 * @author vincentcuccu 23.02.2018
 */
public class DeleteDeleteConflictReasonComputation {

	private Rule rule1;
	private Rule rule2;
	private HashSet<Span> checked;
	private Object nonDelMinimalReasons;
	private Object nonDelInitialReasons;

	/**
	 * constructor
	 * 
	 * @param rule1
	 * @param rule2
	 */
	public DeleteDeleteConflictReasonComputation(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		setChecked(new HashSet<Span>());
	}

	/**
	 * 
	 * @return HashSet<Span>
	 */
	public HashSet<Span> getChecked() {
		return checked;
	}

	/**
	 * 
	 * void
	 * 
	 * @param checked
	 */
	public void setChecked(HashSet<Span> checked) {
		this.checked = checked;
	}

	/**
	 * constructs all Initial Reasons as candidates for r1 and r2
	 * 
	 * @return result
	 */
	public Set<DDSpan> computeDeleteDeleteConflictReason() {
		Set<DDSpan> result = new HashSet<DDSpan>();
		Set<InitialReason> initialReasons = new InitialReasonComputation(rule1, rule2).computeInitialReasons();
		for (InitialReason initialReason : initialReasons) {
			computeDeleteDeleteConflictReason(initialReason, result);
		}

		return result;

	}

	/**
	 * @param initalReason
	 * @param result
	 */
	private void computeDeleteDeleteConflictReason(InitialReason initialReason, Set<DDSpan> result) {
		
		
		if (!DeleteReadConflictReasonComputation.findEmbeddingS1toK2(initialReason)){
			DDSet DD = ConstructDeleteDeleteSet(rule1, rule2, initialReason.getGraph());
			if (true){
				
			}
		}
		
			
		
	}

	/**
	 * @param rule12
	 * @param rule22
	 * @param graph
	 * @return
	 */
	private DDSet ConstructDeleteDeleteSet(Rule r1, Rule r2, Graph s1) {
		DDSet dd = new DDSet();
		Graph c1 = createC1(r1);
		
		//TODO Vincent extendSpan
		return dd;
	}

	/**
	 * @param r1
	 * @return 
	 */
	private Graph createC1(Rule r1) {
		Action delete = new Action(Action.Type.DELETE);
		EList<Edge> c1Edges = r1.getActionEdges(delete);
		EList<Node> c1Nodes = r1.getActionNodes(delete);
		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;
		Graph c1 = r1.getLhs();
		
		ArrayList<Node> nodesToBeDeleted = new ArrayList<Node>();
		ArrayList<Edge> edgesToBeDeleted = new ArrayList<Edge>();
		
		for (Node node : c1Nodes){
			if(!node.getType().equals(delete)){
				nodesToBeDeleted.add(node);
			}
		}
		
		for (Edge edge : c1Edges) {
			if(!edge.getType().equals(delete)){
				edgesToBeDeleted.add(edge);				
			}
		}
		
		for (Edge edge : edgesToBeDeleted) {
			c1.removeEdge(edge);
		}
		for (Node node : nodesToBeDeleted) {
			c1.removeNode(node);
		}
		
		edgesToBeDeleted.clear();
		
		for (Edge edge : c1.getEdges()) {
			if (nodesToBeDeleted.contains(edge.getTarget())||nodesToBeDeleted.contains(edge.getSource())) {
				edgesToBeDeleted.add(edge);
			}
		}
		for (Edge edge : edgesToBeDeleted) {
			c1.removeEdge(edge);
		}
		
		return c1;
	}
	
	
	
}
