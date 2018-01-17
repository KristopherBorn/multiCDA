/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.computation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;

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

		// System.out.println(result);
		return result;

	}

	private void computeDeleteReadConflictReason(InitialReason initialReason, Set<DeleteReadConflictReason> result) {
		Graph lhs1 = initialReason.getRule1().getLhs();
		Rule rule1 = initialReason.getRule1();
		Rule rule2 = initialReason.getRule2();
		//Graph lhs2 = RulePreparator.prepareRule(initialReason.getRule2()).getLhs();
		//Set<ModelElement> c1 = initialReason.getDeletionElementsInRule1();
		Graph s1 = initialReason.getGraph();
		ArrayList<Mapping> s1tol1 = new ArrayList<Mapping>();
		EList<Node> s1Nodes = s1.getNodes();
		EList<Node> lhs1Nodes = lhs1.getNodes();
		EList<Node> k2Nodes = rule2.getRootRule().getLhs().getNodes();
		
		if (findEmbeddingS1toK2(initialReason)){// If (there exists embedding S1 -> K2 with S1 -> K2 -> L2 = mappingsInRule2) {
		
			
			// TODO brauch man das?
		s1tol1 = computeMappings(s1Nodes, lhs1Nodes); // s1' = extendSpan(s1,c1: C1 --> L1) //after extension: (s1': L1 <-- C1 <-- S1 --> L2)
		
		Pushout pushout = new Pushout(rule1, initialReason, rule2);// Compute (L1 -m1-> G <-m2- L2) as PO of s1'
				if (fullfillDanglingG(pushout)) {// If (r1,m1:L1 --> G) and (r2,m2: L2 --> G) fulfill the dangling condition{
				result.add(new DeleteReadConflictReason(initialReason));// Then
																		// add
																		// s1 to
																		// DRCR
			}
		}
	}

	/**
	 * @param pushout 
	 * @return
	 */
	private boolean fullfillDanglingG(Pushout pushout) {
		List<Mapping> m1 = pushout.getRule1Mappings();
		List<Mapping> m2 = pushout.getRule2Mappings();
		
		for (Mapping mapping : m1){
			if (mapping.getOrigin() == null || mapping.getImage() == null){
				Exception exception = new Exception(mapping + "is not dangling free!");
				return false;
			}
		}
		
		for (Mapping mapping : m2){
			if (mapping.getOrigin() == null || mapping.getImage() == null){
				Exception exception = new Exception(mapping + "is not dangling free!");
				return false;
			}
		}
		
		return true;
	}

	private boolean findEmbeddingS1toK2(InitialReason initialReason) {
		// TODO Auto-generated method stub
		// If (there exists embedding S1 -> K2 with S1 -> K2 -> L2 = mappingsInRule2)
		Rule rule1 = initialReason.getRule1();
		Rule rule2 = initialReason.getRule2();
		Graph s1 = initialReason.getGraph();
		Graph l2 = rule2.getLhs();
		Graph r2 = rule2.getRhs();
		Graph k2 = generateK(rule2, l2, r2);

		// S1 -> K2
		ArrayList<Mapping> s1tok2 = computeMappings(s1.getNodes(), k2.getNodes());
		// S1 -> L2
		ArrayList<Mapping> s1tol2 = computeMappings(s1.getNodes(), l2.getNodes());
		
				
		Comparator<Mapping> comp = new Comparator<Mapping>() {

			@Override
			public int compare(Mapping o1, Mapping o2) {
				// TODO Auto-generated method stub
				String o1Origin = o1.getOrigin().getName();
				String o2Origin = o2.getOrigin().getName();
				String o1Image = o1.getImage().getName();
				String o2Image = o2.getImage().getName();
				int origins = o1Origin.compareTo(o2Origin);
				if (origins == 0){
					return o1Image.compareTo(o2Image);
				}
				return origins;
			}
		};
		s1tok2.sort(comp);
		s1tol2.sort(comp);
//		System.out.println(s1tok2);
//		System.out.println(s1tol2);
		
		if (s1tok2.toString().equals(s1tol2.toString())){
			return true;
		}
		return false;
		
	}

	/**
	 * @param rule
	 * @param l
	 * @param r
	 */
	private Graph generateK(Rule rule, Graph l, Graph r) {

		Graph k = rule.getLhs();
		List<Edge> toRemoveEdges = new ArrayList<>();
		List<Node> toRemoveNodes = new ArrayList<>();

//		System.out.println(r.getEdges());
//		System.out.println(l.getEdges());

		for (Edge lEdge : l.getEdges()) {
			String linkeKante = lEdge.toString();
			Boolean ergebnis = false;
			for (Edge rEdge : r.getEdges()) {
				String rechteKante = rEdge.toString();
				if (linkeKante.equals(rechteKante)) {
					ergebnis = true;
					break;
				}

			}
			if (!ergebnis) {
				toRemoveEdges.add(lEdge);
			}
		}

		for (Edge edge : toRemoveEdges) {
			k.removeEdge(edge);
		}

		for (Node lNode : l.getNodes()) {
			String linkerKnoten = lNode.toString();
			Boolean ergebnis = false;
			for (Node rNode : r.getNodes()) {
				String rechterKnoten = rNode.toString();
				if (linkerKnoten.equals(rechterKnoten)) {
					ergebnis = true;
					break;
				}

			}
			if (!ergebnis) {
				toRemoveNodes.add(lNode);
			}
		}

		for (Node node : toRemoveNodes) {
			k.removeNode(node);
		}

		k.setName("KernelRule");
		return k;
	}

	/**
	 * @param henshinFactory
	 * @param graphNodes1
	 * @param graphNodes2
	 * @return 
	 */
	private ArrayList<Mapping> computeMappings(EList<Node> graphNodes1, EList<Node> graphNodes2) {
		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE; //wird zur Erstellung der Mappings benötigt
		ArrayList<Mapping> G1toG2 = new ArrayList<Mapping>();
		
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
