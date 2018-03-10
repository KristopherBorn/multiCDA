/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.computation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;

import agg.util.Pair;

import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;

/**
 * 
 * @author vincentcuccu 17.12.2017
 */
public class DeleteUseConflictReasonComputation {

	private Rule rule1;
	private static Rule rule2;
	private HashSet<Span> checked;
	private Set<Span> conflictReasonsFromR2;

	private ConflictAnalysis analyser;
	private Set<Mapping> L1S1L2;
	private ConflictReasonComputation conflictHelper;
	private Span conflictReason;

	/**
	 * constructor
	 * 
	 * @param rule1
	 * @param rule2
	 * @param conflictReasonsFromR22
	 */
	public DeleteUseConflictReasonComputation(Rule rule1, Rule rule2, Set<Span> conflictReasonsFromR2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		this.conflictReasonsFromR2 = conflictReasonsFromR2;
		setChecked(new HashSet<Span>());
	}

	/**
	 * constructs all Initial Reasons as candidates for r1 and r2
	 * 
	 * @param conflictReasons
	 * @return result
	 */
	public Set<DeleteUseConflictReason> computeDeleteUseConflictReason(Set<Span> conflictReasons) {
		Set<DeleteUseConflictReason> result = new HashSet<DeleteUseConflictReason>();
		for (Span conflictReason : conflictReasons) {
			computeDeleteUseConflictReasons(conflictReason, result);
		}
		return result;

	}

	/**
	 * the Method to encounter the delete read conflict reasons
	 * 
	 * @param conflictReason
	 * @param result
	 */
	private void computeDeleteUseConflictReasons(Span conflictReason, Set<DeleteUseConflictReason> result) {
		this.conflictReason = conflictReason;
		Rule rule1 = conflictReason.getRule1();
		Rule conflictRule2 = conflictReason.getRule2();
		// System.out.println("Mappings in Rule 1: " + L1S1L2);
		MinimalReasonComputation helperForCheckDangling = new MinimalReasonComputation(rule1, rule2);

		if (findEmbeddingS1toK2(conflictReason, rule2)) {// If (there exists
															// embedding
			// S1 to K2 with S1 to K2 to
			// L2 = mappingsInRule2) {

			Pushout pushout = new Pushout(rule1, conflictReason, conflictRule2);
			if (helperForCheckDangling.findDanglingEdgesOfRule1(rule1, pushout.getRule1Mappings()).isEmpty()
					&& helperForCheckDangling.findDanglingEdgesOfRule1(conflictRule2, pushout.getRule2Mappings())
							.isEmpty()) { // fullfillDanglingG(pushout)
				DeleteUseConflictReason res = new DeleteUseConflictReason(conflictReason);
				result.add(res);
			}
		} else {
			System.out.println("noEmbeddingS1ToK2");
			L1S1L2 = conflictReason.mappingsInRule1;
			Object DeleteDeleteSet = ConstructDeleteDeleteSet(rule1, rule2, conflictReason);
			// If DD(s1) is nonEmpty
			// Then For each pair s2 in DD(s1):
			// Add (s1,s2) to DDCR
		}
	}

	/**
	 * @param r2
	 * @param r1
	 * @param conflictReason
	 * @return
	 */
	private Object ConstructDeleteDeleteSet(Rule r1, Rule r2, Span conflictReason) {

		Pair<Span, Span> ddSet;
		for (Span span : conflictReasonsFromR2) {
			Set<Mapping> L2S2L1 = span.mappingsInRule2;
			Object sSmall = compatibleSpans(L1S1L2, L2S2L1);

			// TODO Vincent Nach compatible hier weiter!!!!
		}

		return null;
	}

	/**
	 * @param s1
	 * @param s2
	 * @return
	 */
	private Object compatibleSpans(Set<Mapping> s1, Set<Mapping> s2) {
		System.out.println("l1s1l2: " + s1);
		System.out.println("l2s2l1: " + s2);
		HenshinFactoryImpl helper = new HenshinFactoryImpl();
		Graph SApostrophe = helper.createGraph();
		Span sApostrophe = null;
		Object S1Apostrophe = null;
		Object S2Apostrophe = null;
		addCompatibleElements(s1, s2, S1Apostrophe, helper);

		return null;
	}

	/**
	 * @param s1
	 * @param s2
	 * @param s1Apostrophe
	 * @param helper
	 */
	private void addCompatibleElements(Set<Mapping> s1, Set<Mapping> s2, Object s1Apostrophe,
			HenshinFactoryImpl helper) {
		Graph S = helper.createGraph();
		for (Mapping s : s1) {
			Object y = existCompatibleElement(s, s1, s2);
		}

	}

	/**
	 * @param mapping
	 * @param s1
	 * @param s2 
	 * @param conflictReason2
	 * @return
	 */
	private Object existCompatibleElement(Mapping mapping, Set<Mapping> s1, Set<Mapping> s2) {
		Graph cr = conflictReason.getGraph();
		if (contains(cr, mapping.getOrigin())) {
			for (Mapping mapping2 : s2) {
				if () //TODO Vincent
			}
		}
		return null;
	}

	/**
	 * @param cr
	 * @param origin
	 * @return
	 */
	private boolean contains(Graph cr, Node origin) {
		EList<Node> nodes = cr.getNodes();
		for (Node node : nodes) { // TODO Vincent Prüfen ob hier nicht lieber
									// Strings verglichen werden sollten.
			if (node.equals(origin)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * returns true, if there is a match from S1 to K2, which is equal to the
	 * match of S1 to lhs of rule 2
	 * 
	 * @param conflictReason
	 * @param rule
	 * @return boolean
	 */
	public static boolean findEmbeddingS1toK2(Span conflictReason, Rule rule) {
		// Rule rule2 = conflictReason.getRule2();
		Graph s1 = conflictReason.getGraph();
		Graph l2 = rule.getLhs();
		Action preserve = new Action(Action.Type.PRESERVE);
		// Get preserve Nodes of rule 2
		EList<Node> k2nodes = rule.getActionNodes(preserve);
		EList<Edge> k2Edges = rule.getActionEdges(preserve);
		// System.out.println("findEmbeddingS1toK2, k2edges: " + k2Edges);
		EList<Edge> l2Edges = conflictReason.getRule2().getActionEdges(preserve);
		// System.out.println("findEmbeddingS1toK2, l2edges: " + l2Edges);

		// S1 -> K2
		ArrayList<Mapping> s1tok2 = computeMappings(s1.getNodes(), k2nodes);
		// S1 -> L2
		ArrayList<Mapping> s1tol2 = computeMappings(s1.getNodes(), l2.getNodes());

		// Comparator to define sorting of Mappings
		// TODO: Push to MappingImpl?
		Comparator<Mapping> comp = new Comparator<Mapping>() {

			@Override
			public int compare(Mapping o1, Mapping o2) {
				String o1Origin = o1.getOrigin().getName();
				String o2Origin = o2.getOrigin().getName();
				String o1Image = o1.getImage().getName();
				String o2Image = o2.getImage().getName();
				int origins = o1Origin.compareTo(o2Origin);
				if (origins == 0) {
					return o1Image.compareTo(o2Image);
				}
				return origins;
			}
		};

		// System.out.println("vor sortiertung: " + s1tok2);
		s1tok2.sort(comp);
		// System.out.println("nach sortierung: " + s1tok2);
		// System.out.println("vor sortiertung: " + s1tol2);
		s1tol2.sort(comp);
		// System.out.println("nach sortierung: " + s1tol2);

		if (s1tok2.toString().equals(s1tol2.toString())) {
			if (k2Edges.toString().equals(l2Edges.toString())) {
				return true;
			}
		}
		return false;

	}

	/**
	 * computes Mappings of two ELists of Nodes our of two Graphs
	 * 
	 * @param graphNodes1
	 * @param graphNodes2
	 * @return
	 */
	private static ArrayList<Mapping> computeMappings(EList<Node> graphNodes1, EList<Node> graphNodes2) {
		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE; // wird zur
																	// Erstellung
																	// der
																	// Mappings
																	// benötigt
		ArrayList<Mapping> G1toG2 = new ArrayList<Mapping>();

		for (Node origin : graphNodes1) { // Alle Knoten in S1 sollen auf alle
											// Knoten in L1 gemappt werden
			for (Node image : graphNodes2) {
				if (origin.getType() == image.getType()) { // Nur wenn Typen
															// gleich sind.
					Mapping mapping = henshinFactory.createMapping(origin, image);
					G1toG2.add(mapping);
				}
			}
		}
		return G1toG2;
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

}
