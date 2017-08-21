package org.eclipse.emf.henshin.cpa.atomic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.cpa.atomic.AtomicCoreCPA;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.dependency.DependencyAtom;
import org.eclipse.emf.henshin.cpa.atomic.dependency.MinimalDependencyReason;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ProduceUseAtomicCoreCPA {
	
	Map<Rule, Copier> mappingOfInvertedRuleToRhsToLhsCopier;
	

	// equivalent to MinimalConflictReasonst
	Set<MinimalDependencyReason> overallMinimalDependencyReasons;
	
	/**
	
	// Constructor
	public ProduceUseAtomicCoreCPA(){
		mappingOfInvertedRuleToRhsToLhsCopier = new HashMap<>();
	}
	
	// equivalent to MinimalConflictReasonst
	/**
	 * @return the reasons
	 */
	public Set<MinimalDependencyReason> getMinimalDependencyReasons() {
		return overallMinimalDependencyReasons;
	}
	
	public List<DependencyAtom> computeDependencyAtoms(Rule rule1, Rule rule2){
		
		List<DependencyAtom> dependencyAtoms = new LinkedList<DependencyAtom>();
		overallMinimalDependencyReasons = new HashSet<MinimalDependencyReason>();
		mappingOfInvertedRuleToRhsToLhsCopier = new HashMap<Rule, Copier>();
		
		Rule invertedRule = invertRule(rule1);
		
		AtomicCoreCPA atomicCoreCPA = new AtomicCoreCPA();
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms(invertedRule, rule2);
		Set<MinimalConflictReason> minimalConflictReasons = atomicCoreCPA.getMinimalConflictReasons();
		
//		TODO: als n�chstes machen!
		BiMap<MinimalConflictReason, MinimalDependencyReason> mcrToMdrMap = HashBiMap.create ();
		BiMap<ConflictAtom, DependencyAtom> caToDaMap = HashBiMap.create ();
		Set<Graph> transformedGraphMappingIntoR1RHS = new HashSet<Graph>();
		//TODO: introduce bijektive mapping between MinimalConflictReason and MinimalDependencyReason 
		//		and by using a MinimalDependencyReason constructor fill up the total mapping 
		
		Copier copierOfFormerRhsToLhs = mappingOfInvertedRuleToRhsToLhsCopier.get(invertedRule);
				
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			
			
			
			//TODO: jedes ConflictAtom besteht aus einem Span (Graph, Set<mappingInR1>, Set<mappingInR2>) und einer Menge von MinimalConflictReason zu denen diese beitragen.
			//		- noch ist unklar, ob f�r die dependencies die mappings des Span in die erste Regel ge�ndert werden sollten, oder ein komplett neuer Span und neue Mappings erzeugt werden sollten??
			//		- erste �berlegung: lieber �ndern/Anpassen der Mappings, da so ggf. auch die minimal conflict reasons weiterverwendet werden k�nnen!
			//			- was ist dann noch f�r die minimalConflictReason anzupassen, sodass es minimalDependencyReason werden?
			
			Span spanOfConflictAtom = conflictAtom.getSpan();
			adjustMappingFromLhsToRhs(rule1, copierOfFormerRhsToLhs, spanOfConflictAtom);
			
			DependencyAtom dependencyAtom = new DependencyAtom(spanOfConflictAtom);
			caToDaMap.put(conflictAtom, dependencyAtom);
			
			
			//DONE: neues Dependency Atom erstellen!
			//TODO: Was ist mit den Reasons? Diese m�ssen auch noch angepasst werden!!!
				//TODO: ein MCR ist mehreren Spans zugeordnet! Es muss also ein L�sung gefunden werden, sodass MCRs in MDRs �berf�hrt werden und allen zugeh�rigen dependencyAtoms zugeordnet werden! 
			
			dependencyAtoms.add(dependencyAtom);
		}
		
		List<MinimalConflictReason> orderedBySizeMinimalConflictReasons = orderMinimalConflictsReasonsByIncreasingSize(minimalConflictReasons);
		for(MinimalConflictReason minimalConflictReason : orderedBySizeMinimalConflictReasons){
//			Graph graphOfMcr = minimalConflictReason.getGraph();
//			minimalConflictReason.getContainedConflictAtoms()
			if(!isAdjustedToRhs(minimalConflictReason))
				adjustMappingFromLhsToRhs(rule1, copierOfFormerRhsToLhs, minimalConflictReason);
			
			
			 Set<MinimalDependencyReason> originMDRs =  new HashSet<MinimalDependencyReason>();//die MDRs aus denen sich der neue MDR ggf zusammensetzt ermitteln!
			 for(MinimalConflictReason mcr : minimalConflictReason.getOriginMCRs()){
				 originMDRs.add(mcrToMdrMap.get(mcr));
			 }
			
			MinimalDependencyReason mdr = new MinimalDependencyReason(minimalConflictReason, originMDRs);
			//TODO: MDR auf Grundlage des jeweiligen MCR erstellen. 
			//		ggf. erst die "kleinen" MCRs bzw. MCRs abarbeiten und dann die gro�en, da die gro�en aus den kleinen zusammengesetzt sein k�nnen!
			// 			dabei die zuvor erstellten DA nutzen.
			//			mapping der erstellten MDRs aus MCRs fortf�hren!
			 
			for(ConflictAtom conflictAtom : minimalConflictReason.getContainedConflictAtoms()){
				DependencyAtom dependencyAtom = caToDaMap.get(conflictAtom);
				if(dependencyAtom != null) //null check should be superfluous but better be careful
					mdr.addContainedDependencyAtom(dependencyAtom);
			}
			mcrToMdrMap.put(minimalConflictReason, mdr);
			overallMinimalDependencyReasons.add(mdr);
			
		}
		
		// jedem MDR seine passendenden DA zuweisen
		//		UND jedem DA seine minimalConflictReasons zuweisen! 
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			DependencyAtom dependencyAtom = caToDaMap. get(conflictAtom);
			for(MinimalConflictReason mcr : conflictAtom.getMinimalConflictReasons()){
				MinimalDependencyReason minimalDependencyReason = mcrToMdrMap.get(mcr);
				dependencyAtom.addMinimalDependencyReasons(minimalDependencyReason);
				minimalDependencyReason.addContainedDependencyAtom(dependencyAtom);
			}
		}
		
		return dependencyAtoms;
	}


	private boolean isAdjustedToRhs(MinimalConflictReason minimalConflictReason) {
		Node nodeInRule = minimalConflictReason.getMappingsInRule1().iterator().next().getImage(); // WARNING! source for NPE!!!
		if(nodeInRule.getGraph() == minimalConflictReason.getRule1().getLhs()){
			return false;
		}else if(nodeInRule.getGraph() == minimalConflictReason.getRule1().getRhs()){
			return true;
		}
		// something went wrong!
		return false;
	}

	private List<MinimalConflictReason> orderMinimalConflictsReasonsByIncreasingSize(
			Set<MinimalConflictReason> minimalConflictReasons) {
//		List<MinimalConflictReason> orderMinimalConflictsReasonsByIncreasingSize = new LinkedList<MinimalConflictReason>();

		// first order by size
		Set<MinimalConflictReason> minimalConflictReasonsToOrder = new HashSet<MinimalConflictReason>(minimalConflictReasons);
		List<MinimalConflictReason> minimalConflictReasonsOrderByOriginMCRs = new LinkedList<MinimalConflictReason>();
		int amountOfMCRs = 0;
		while(minimalConflictReasonsToOrder.size()>0){
			for(MinimalConflictReason minimalConflictReasonToOrder : minimalConflictReasonsToOrder){
				if(minimalConflictReasonToOrder.getOriginMCRs().size() == amountOfMCRs){
					minimalConflictReasonsOrderByOriginMCRs.add(minimalConflictReasonToOrder);
				}
			}
			amountOfMCRs++;
			minimalConflictReasonsToOrder.removeAll(minimalConflictReasonsOrderByOriginMCRs); //TODO: f�hrt das zu einer "ConcurrentModificationException"?
		}
			
		// then order by amount of ConflictReasons
			// superfluous
		
		return minimalConflictReasonsOrderByOriginMCRs;
	}

	private void adjustMappingFromLhsToRhs(Rule rule1, Copier copierOfFormerRhsToLhs, Span span) {
		//			List<Mapping> mappingsFromConflictAtomGraphInRule1 = spanOfConflictAtom.getMappingsInRule1();
		
		for(Node rhsNodeInOrigRule1 : rule1.getRhs().getNodes()){
			// zugeh�rigen Knoten in der LHS der invertierten Regel finden
			// dazu den Copier nutzen
			EObject eObject = copierOfFormerRhsToLhs.get(rhsNodeInOrigRule1);
			Node lhsNodeInInvertedRule1 = (Node) eObject;
			// mapping des berechneten delete-use-Konflikts abrufen, das als IMAGE auf den RHS Knoten der invRule1 zeigt
				// ggf. gibt es kein Mapping, wenn der Knoten nicht in den Konflikt (bzw. die Abh�ngigkeit involviert ist).
			Mapping mappingFromGraphToRule1 = span.getMappingFromGraphToRule1(lhsNodeInInvertedRule1);
			// im Mapping das image durch den Knoten der orignal Rule 1 ersetzen
			if( mappingFromGraphToRule1 != null){					
				mappingFromGraphToRule1.setImage(rhsNodeInOrigRule1);
			}
			//TODO: "sicherungsmechanismus" einf�hren, der �berpr�ft, dass auch alle Mappings vom ConflictAtom in invertedRule1 auf rule1 ver�ndert wruden!!! 
		}
	}
	
//	public void analyzeDependencyAtomsAndMinimalConflictReason(Rule rule1, Rule rule2){
//		
//		Rule invertedRule = invertRule(rule1);
//		
//		AtomicCoreCPA atomicCoreCPA = new AtomicCoreCPA();
//		List<AtomicCoreCPA.Span> conflictAtomCandidates = atomicCoreCPA.computeAtomCandidates(invertedRule,
//				rule2);
//		Set<Span> reasons = new HashSet<>();//
//		for (Span candidate : conflictAtomCandidates) {
//			atomicCoreCPA.computeMinimalConflictReasons(rule1, rule2, candidate, reasons);
//		}
//		
//		Set<Span> minimalConflictReasons = reasons;
//		// TODO: transfer result back
//		
//		atomicCoreCPA.computeConflictAtoms(rule1, rule2)
//		
//		unklar ist an der Stelle noch immer ob eine Berechnung die Ergebnisse der anderen mit ermittelt!
//		Oder einfach beide methoden voneinander getrennt behandeln? (Auch gerade im Hinblick auf den/die runner?)
//		
//		
//	}


	/**
	 * TODO: add information here
	 * 		z.B.: 
	 * 			- welche Regel-features unterst�tzt werden
	 * 			- wie wird damit umgeganen, wenn die Knoten oder Mappings nicht erstellt wurde? 
	 * 				- wird NULL zur�ck gegeben, oder wird eine Exception geworfen?
	 * 				- sollte es eine M�glichkeit vorab geben zu pr�fen, ob die Regel invertierbar ist?
	 * 
	 * @param rule1
	 * @return the 
	 */
	public Rule invertRule(Rule rule1) {
		// TODO: invert first rule
		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;
		
		// erstellen einer Regel mit urspr�nglichem Namen + "_INV"
		// TODO: In welches MODULe kommt die Regel �berhaupt ????
		//		eigenes? Imports? Copy?
		Rule invRule1 = henshinFactory.createRule(rule1.getName()+"_INV");
		
		
		// Kopieren des RHS Graph und als LHS der neuen Regel zuordnen
		Copier copierForRhsToLhs = new Copier();
		Graph newLhs = (Graph) copierForRhsToLhs.copy(rule1.getRhs());
		copierForRhsToLhs.copyReferences();
		newLhs.setName("LHS");
		invRule1.setLhs(newLhs);
		mappingOfInvertedRuleToRhsToLhsCopier.put(invRule1, copierForRhsToLhs);
		
		// Kopieren des LHS Graph und als RHS der neuen Regel zuordnen
		Copier copierForLhsToRhs = new Copier();
		Graph newRhs = (Graph) copierForLhsToRhs.copy(rule1.getLhs());
		copierForLhsToRhs.copyReferences();
		newRhs.setName("RHS");
		invRule1.setRhs(newRhs);
		
		// notwendige Mappings erstellen. Dazu alle Mappings durchgehen und ausgehend von den Nodes in der urspr�nglichen Regel �ber den copier die Nodes in der neuen regel identifizieren!
		MappingList mappingsOfOriginalRule1 = rule1.getMappings();
		for(Mapping mappingInOriginalRule1 : mappingsOfOriginalRule1){
			
			//identifizieren der ORIGIN in der neuen Regel
			Node imageInOriginalRule = mappingInOriginalRule1.getImage();
			EObject originInNewRule = copierForRhsToLhs.get(imageInOriginalRule);
			Node originInNewRuleNode = (Node) originInNewRule; //TODO: add NULL check!
			
			// identifizieren des IMAGE in der neuen Regel
			Node originInOriginalRule = mappingInOriginalRule1.getOrigin();
			EObject imageInNewRule = copierForLhsToRhs.get(originInOriginalRule);
			Node imageInNewRuleNode = (Node) imageInNewRule; //TODO: add NULL check!
			
			Mapping createdMapping = henshinFactory.createMapping(originInNewRuleNode, imageInNewRuleNode);
			invRule1.getMappings().add(createdMapping);
		}
		
		// ggf. als Datei speichern?
		// ABER(!): auch pr�fen, ob es rein programmatisch geht!
		
		return invRule1;
	}
	

}
