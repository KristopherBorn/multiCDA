/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.computation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;

/**
 * 
 * @author vincentcuccu 17.12.2017
 */
public class DeleteUseConflictReasonComputation {

	private Rule rule1;
	private Rule rule2;
	private HashSet<Span> checked;
	private Set<InitialReason> initialReasonsR2R1NonDel;
	private Set<InitialReason> initialReasonsR1R2NonDel;
	private ConflictAnalysis analyser;
	private Set<Mapping> L1S1L2;

	/**
	 * constructor
	 * 
	 * @param rule1
	 * @param rule2
	 */
	public DeleteUseConflictReasonComputation(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		setChecked(new HashSet<Span>());
	}

	/**
	 * constructs all Initial Reasons as candidates for r1 and r2
	 * 
	 * @return result
	 */
	public Set<DeleteReadConflictReason> computeDeleteUseConflictReason(Set<InitialReason> initialReasonsR1R2NonDel, Set<InitialReason> initialReasonsR2R1NonDel) {
		this.initialReasonsR1R2NonDel = initialReasonsR1R2NonDel;
		this.initialReasonsR2R1NonDel = initialReasonsR2R1NonDel;
		Set<DeleteReadConflictReason> result = new HashSet<DeleteReadConflictReason>();
		Set<InitialReason> initialReasons = new InitialReasonComputation(rule1, rule2).computeInitialReasons();
		for (InitialReason initalReason : initialReasonsR1R2NonDel) {
			computeDeleteReadConflictReason(initalReason, result);
		}

		return result;

	}

	/**
	 * the Method to encounter the delete read conflict reasons
	 * 
	 * @param initialReason
	 * @param result
	 */
	private void computeDeleteReadConflictReason(InitialReason initialReason, Set<DeleteReadConflictReason> result) {
		initialReason.getRule1().getLhs();
		Rule rule1 = initialReason.getRule1();
		Rule rule2 = initialReason.getRule2();
		L1S1L2 = initialReason.mappingsInRule1;
		//System.out.println("Mappings in Rule 1: " + L1S1L2);
		initialReason.getGraph();
		new ArrayList<Mapping>();
		MinimalReasonComputation helperForCheckDangling = new MinimalReasonComputation(rule1, rule2);

		if (findEmbeddingS1toK2(initialReason)) {// If (there exists embedding
													// S1 to K2 with S1 to K2 to
													// L2 = mappingsInRule2) {

			Pushout pushout = new Pushout(rule1, initialReason, rule2);
			if (helperForCheckDangling.findDanglingEdgesOfRule1(rule1, pushout.getRule1Mappings()).isEmpty()
					&& helperForCheckDangling.findDanglingEdgesOfRule1(rule2, pushout.getRule2Mappings()).isEmpty()) { // fullfillDanglingG(pushout)
				DeleteReadConflictReason res = new DeleteReadConflictReason();
				res.setSpan1(initialReason);
				result.add(res);
			}
		} else {
			Object DeleteDeleteSet = ConstructDeleteDeleteSet(initialReason);
			 
				     // If DD(s1) is nonEmpty  
				           //DD(s1) empty cannot occur since s1 would be a SDUCR then
				     // Then For each pair s2 in DD(s1):
				                //  Add (s1,s2) to DDCR
		}
	}

	/**
	 * @param rule12
	 * @param rule22
	 * @param initialReason
	 * @return
	 */
	private Object ConstructDeleteDeleteSet(InitialReason initialReason) {
		
		for (Span span : initialReasonsR2R1NonDel) {
			Set<Mapping> L2S2L1 = span.mappingsInRule2;
			Object sSmall = compatibleSpans(L1S1L2,L2S2L1);
			
			//TODO Vincent Nach compatible hier weiter!!!!
		}
		
		return null;
	}

	/**
	 * @param l1s1l22
	 * @param l2s2l1
	 * @return
	 */
	private Object compatibleSpans(Set<Mapping> l1s1l2, Set<Mapping> l2s2l1) {
		System.out.println("l1s1l2: " + l1s1l2);
		System.out.println("l2s2l1: " + l2s2l1);
		return null;
	}

	/**
	 * returns true, if there is a match from S1 to K2, which is equal to the
	 * match of S1 to lhs of rule 2
	 * 
	 * @param initialReason
	 * @return boolean
	 */
	public static boolean findEmbeddingS1toK2(InitialReason initialReason) {
		initialReason.getRule1();
		Rule rule2 = initialReason.getRule2();
		Graph s1 = initialReason.getGraph();
		Graph l2 = rule2.getLhs();
		rule2.getRhs();
		// Get KernelRule
		EList<Node> k2nodes = rule2.getActionNodes(new Action(Action.Type.PRESERVE));
		// System.out.println(k2nodes);

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

		s1tok2.sort(comp);
		s1tol2.sort(comp);

		if (s1tok2.toString().equals(s1tol2.toString())) {
			return true;
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
