/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.computation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
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
import org.eclipse.ui.internal.handlers.WizardHandler.New;

import com.google.common.base.CaseFormat;

import agg.util.Pair;

import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictReason;

/**
 * 
 * @author vincentcuccu 17.12.2017
 */
public class DeleteUseConflictReasonComputation {

	private Rule rule1;
	private static Rule rule2;
	private Set<Span> conflictReasonsFromR2;

	private ConflictAnalysis analyser;
	private Set<Mapping> mapS1ToL1;
	private ConflictReasonComputation conflictHelper;
	private Span conflictReason;
	private Set<Mapping> mapS1ToL2;
	private String notCompatibleException;
	private Throwable notCompatible = new Throwable(notCompatibleException);
	private Exception compatibleException =new Exception("Juhu! Eine Exception: ", notCompatible );

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
			mapS1ToL1 = conflictReason.mappingsInRule1;
			mapS1ToL2 = conflictReason.mappingsInRule2;
			Object DeleteDeleteSet = ConstructDeleteDeleteSet(rule1, rule2, conflictReason);
			// If DD(s1) is nonEmpty
			// Then For each pair s2 in DD(s1):
			// Add (s1,s2) to DDCR
		}
	}

	/**
	 * @param r2
	 * @param r1
	 * @param sp1
	 * @return
	 */
	private Object ConstructDeleteDeleteSet(Rule r1, Rule r2, Span sp1) {

		Pair<Span, Span> ddSet;
		for (Span sp2 : conflictReasonsFromR2) {
			Object sSmall = compatibleSpans(sp1, sp2);

			// TODO Vincent Nach compatible hier weiter!!!!
		}

		return null;
	}

	/**
	 * @param sp2
	 * @param sp1
	 * @return
	 */
	private Object compatibleSpans(Span sp1, Span sp2) {
		HenshinFactoryImpl helper = new HenshinFactoryImpl();
		Graph SApostrophe = helper.createGraph();
		Span sAp = null; 
		EList<Object> s2Apostrophe;
		s2Apostrophe = null;
		EList<Object> s1Apostrophe;
		s1Apostrophe = addCompatibleElements(sp1, sp2);
		if(s1Apostrophe!=null){
			s2Apostrophe = addCompatibleElements(sp2, sp1);
			if(s2Apostrophe!=null){
				sAp = intersection(s1Apostrophe, s2Apostrophe);//TODO intersection
				//foreach (x,y) in sAP
				//add mapping ...
				//add mapping ...
			} else {return null;}
		}else{return null;}

		return SApostrophe;
	}

	/**
	 * @param s1Apostrophe
	 * @param s2Apostrophe
	 * intersection ist die Überschneidung der beiden Graphen
	 * @return
	 */
	private Span intersection(EList<Object> s1Apostrophe, EList<Object> s2Apostrophe) {
		// TODO intersection
		return null;
	}

	/**
	 * @param sp1
	 * @param sp2
	 * @param s1Apostrophe
	 * @return
	 */
	private EList<Object> addCompatibleElements(Span sp1, Span sp2) {
		// Graph S = helper.createGraph();
		EList<Object> s1Apostrophe = new BasicEList<Object>();
		EList<Node> s1Nodes = sp1.getGraph().getNodes();
		EList<Edge> s1Edges = sp1.getGraph().getEdges();
		EList<Node> s2Nodes = sp2.getGraph().getNodes();
		EList<Edge> s2Edges = sp2.getGraph().getEdges();
		//System.out.println(s1Nodes + " : " + s1Edges);
		//System.out.println(s2Nodes + " : " + s2Edges);
		EList<Object> allObjectsS1 = new BasicEList<Object>();
		s1Nodes.forEach(n -> allObjectsS1.add(n));
		s1Edges.forEach(e -> allObjectsS1.add(e));
		for (Object x : allObjectsS1) {
			try {
				Object y = existCompatibleElement(x, sp1, sp2);
				if (y.equals(null)) {
					s1Apostrophe.add(y);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage() + e.getCause());
				s1Apostrophe = null;
			}
		}
		return s1Apostrophe;
	}

	/**
	 * @param x
	 * @param sp1
	 * @param sp2
	 * @param conflictReason2
	 * @return
	 * @throws Exception
	 */
	private Object existCompatibleElement(Object x, Span sp1, Span sp2) throws Exception {
		// System.out.println("SP1: " + sp1);
		// System.out.println("SP2: " + sp2);
		Graph cr = conflictReason.getGraph();
		// Node node = x.getImage();
		// EList<Node> nodes = cr.getNodes();
		// throw exception;
		EList<Node> s1Nodes = sp1.getGraph().getNodes();
		EList<Edge> s1Edges = sp1.getGraph().getEdges();
		EList<Object> allObjectsS1 = new BasicEList<Object>();
		s1Nodes.forEach(n -> allObjectsS1.add(n));
		s1Edges.forEach(e -> allObjectsS1.add(e));
		EList<Node> s2Nodes = sp2.getGraph().getNodes();
		EList<Edge> s2Edges = sp2.getGraph().getEdges();
		EList<Object> allObjectsS2 = new BasicEList<Object>();
		s2Nodes.forEach(n -> allObjectsS2.add(n));
		s2Edges.forEach(e -> allObjectsS2.add(e));
		Set<Mapping> s11 = sp1.getMappingsInRule1();
		Set<Mapping> s12 = sp1.getMappingsInRule2();
		Set<Mapping> s21 = sp2.getMappingsInRule1();
		Set<Mapping> s22 = sp2.getMappingsInRule2();
		if (true) { // Todo: contains(x, sp1) Hier sollte eigentlich geschaut
					// werden ob x in S1 ist, obwohl ja das element x aus S1
					// übergeben wurde
			System.out.println("nodes contains x!");
			for (Object y : allObjectsS2) { // Mapping y : sp2
				if (true) { // If s11(x) = s21(y)
					if (true) { // If s12(x) = s22(y)
						return y;
					} else {

						throw compatibleException;
					}
				} else {
					return null;
				}
			}
		} else {
			throw compatibleException;
		}
		return s22;
	}

	/**
	 * @param x
	 * @param allObjectsS1
	 * @return
	 */
	private boolean contains(Object x, EList<Object> allObjectsS1) {
		System.out.println("xType: " + xType);
		boolean ret = false;
		for ( Object object: allObjectsS1) {
			System.out.println(object);
			if () {
				ret = true;
			}
		}
		return ret;
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

}
