/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.computation;

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
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;
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

		if (!DeleteReadConflictReasonComputation.findEmbeddingS1toK2(initialReason)) {
			DeleteUseConflictReason DD = ConstructDeleteDeleteSet(initialReason);
			if (true) {

			}
		}

	}

	/**
	 * @param rule12
	 * @param rule22
	 * @param graph
	 * @return
	 */
	private DeleteUseConflictReason ConstructDeleteDeleteSet(InitialReason initialReason) {
		Rule r1 = initialReason.getRule1();
		Rule r2 = initialReason.getRule2();
		DeleteUseConflictReason dd = new DeleteUseConflictReason();
		//Graph c1 = createC1(r1);
		//ExtendedSpan extSpan = extendSpan(initialReason, c1);
		Set<Mapping> S1toL1 = initialReason.mappingsInRule1;
		Set<Mapping> S1toL2 = initialReason.mappingsInRule2;
		
		return dd;
	}

	

//	/**
//	 * @param r1
//	 * @return
//	 */
//	private Graph createC1(Rule r1) {
//		Action delete = new Action(Action.Type.DELETE);
//		EList<Edge> c1Edges = r1.getLhs().getEdges();
//		EList<Node> c1Nodes = r1.getLhs().getNodes();
//		System.out.println("C1Nodes: " + c1Nodes);
//		//HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;
//		Graph c1 = r1.getLhs();
//
//		//System.out.println(c1.getNodes());
//		//System.out.println(c1.getEdges());
//		ArrayList<Node> nodesToBeDeleted = new ArrayList<Node>();
//		//System.out.println("Knoten die gelöscht werden vorher: " + nodesToBeDeleted);
//		ArrayList<Edge> edgesToBeDeleted = new ArrayList<Edge>();
//		//System.out.println("Kanten die gelöscht werden vorher: " + edgesToBeDeleted);
//		for (Node node : c1Nodes) {
//			//System.out.println(node + " : " + node.getAction().toString()); //TODO
//			if (!node.getAction().equals(delete)) {
//				
//				if (!hasActionIncoming(delete, node) && !hasActionOutgoing(delete, node)) {
//					nodesToBeDeleted.add(node);
//				}
//			}
//		}
//		//System.out.println("Knoten die gelöscht werden nachher: " + nodesToBeDeleted);
//
//
//		for (Edge edge : c1Edges) {
//			if (!edge.getAction().equals(delete)) {
//				edgesToBeDeleted.add(edge);
//			}
//		}
//		//System.out.println("Kanten die gelöscht werden nachher: " + edgesToBeDeleted);
//
//
//		for (Edge edge : edgesToBeDeleted) {
//			c1.removeEdge(edge);
//		}
//
//		for (Node node : nodesToBeDeleted) {
//			c1.removeNode(node);
//		}
//
//		//System.out.println("C1E: " + c1.getEdges());
//		//System.out.println("C1N: " + c1.getNodes());
//		return c1;
//	}
//
//	/**
//	 * @param delete
//	 * @param node
//	 */
//	private boolean hasActionOutgoing(Action delete, Node node) {
//		for (Edge edgeOut : node.getOutgoing()) {// Prüfe jede ausgehende Kante
//													// ob sie Löschend ist
//			if (edgeOut.getAction().equals(delete)) {
//				return true;
//			}
//
//		}
//		return false;
//	}
//
//	/**
//	 * @param delete
//	 * @param node
//	 */
//	private boolean hasActionIncoming(Action delete, Node node) {
//		for (Edge edgeIn : node.getIncoming()) { // Prüfe jede eingehende Kante
//													// ob diese Löschen ist
//			if (edgeIn.getAction().equals(delete)) {
//				return true;
//			}
//
//		}
//		return false;
//	}

}
