/**
 * <copyright>
 * Copyright (c) 2010-2016 Henshin developers. All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * </copyright>
 */
package org.eclipse.emf.henshin.multicda.cpa.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.interpreter.Change;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.RuleApplication;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.MatchImpl;
import org.eclipse.emf.henshin.interpreter.impl.RuleApplicationImpl;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

/**
 * This class represents a conflict, which is on of the two forms of a critical pair.
 * 
 * @author Florian Heß, Kristopher Born
 *
 */
public class Conflict extends CriticalPair {

	/**
	 * The match of one of the two involved rules into the minimal model.
	 */
	Match match1;

	/**
	 * The match of one of the two involved rules into the minimal model.
	 */
	Match match2;

	/**
	 * Kind of the conflict.
	 */
	ConflictKind conflictKind;
	
	
	EGraph minimalModelEGraph;

	/**
	 * Default constructor.
	 * 
	 * @param r1 One of the two rules of the conflict.
	 * @param r2 The other rule of the two rules of the conflict.
	 * @param cpaEPackage The minimal model on which both rules are applicable.
	 * @param match1 The match of the rule <code>r1</code> into the <code>minimalModel</code>.
	 * @param match2 The match of the rule <code>r2</code> into the <code>minimalModel</code>.
	 * @param conflictKind The kind of the conflict.
	 */
	public Conflict(Rule r1, Rule r2, EPackage cpaEPackage, Match match1, Match match2, ConflictKind conflictKind) {
		super(r1, r2, cpaEPackage);
		this.match1 = match1;
		this.match2 = match2;
		this.conflictKind = conflictKind;
	}

	/**
	 * Returns the match of rule r1 into the minimal model.
	 * 
	 * @return The match of rule r1 into the minimal model.
	 */
	public Match getMatch1() {
		return match1;
	}

	/**
	 * Returns the match of rule r2 into the minimal model.
	 * 
	 * @return The match of rule r2 into the minimal model.
	 */
	public Match getMatch2() {
		return match2;
	}

	/**
	 * Returns the kind of the conflict.
	 * 
	 * @return The kind of the conflict.
	 */
	public ConflictKind getConflictKind() {
		return conflictKind;
	}

	public EGraph getMinimalModelEGraph() {
		if(minimalModelEGraph == null){
			
			HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;
			Engine engine = new EngineImpl(); // TODO: maybe there is a way to improve this instantiation to take performance into account
			
			
//			Graph minimalModelGraph = henshinFactory.createGraph(); // scheint falsch/überflüssig zu sein!
//			minimalModelEGraph = new EGraphImpl(minimalModelGraph); // scheint falsch/überflüssig zu sein!
			minimalModelEGraph = new EGraphImpl();
			
			/* Wie kommt man zur Instanz?
			 * - Kopien beider LHS anfertigen
			 * 	- (gemeinsames) Module für beide mit notwendigen imports erstellen
			 * - r1Create-Regel durch Kopie von r1Lhs erstellen mit dem kopierten Graph in der RHS
			 *  - Wie mit den Attributen verfahren? - Konstanten beibehalten. Paramter -> ??? (TODO!)
			 *  - Wie mit ACs umgehen? Elemente der PACs müssten in der Isntanz auch vorhanden sein!
			 * - r2Create-Regel durch Kopie von r2Lhs erstellen mit dem kopierten Graph in der RHS
			 * 	- zusätzlich muss dabei auch noch die LHS geschaffen werden und die Mappings zwischen LHS und RHS
			 * 	 - Die LHSKnoten und Kanten leiten sich aus den beiden matches ab!
			 * - anwenden von r1Create-Regel auf einem leeren EGraph
			 * - anwenden der r2Create-Regel auf das vorherige Ergebnis unter zuhilfenahme der abgeleiteten matches
			 * - festhalten der mappings der beiden Regelanwendungen  
			 */
			
			
			 // - Kopien beider LHS anfertigen
			 // 	- (gemeinsames) Module für beidetemporären Regeln mit notwendigen imports erstellen
			Copier importsCopier = new Copier();
			Collection<EPackage> copyOfAllImports = importsCopier.copyAll(getFirstRule().getModule().getImports());
			importsCopier.copyReferences();
			
			Module module = henshinFactory.createModule();
			module.getImports().addAll(copyOfAllImports); //TODO: maybe check that 'getImports()' doesnt return NULL
			
			
			// - r1Create-Regel durch Kopie von r1Lhs erstellen mit dem kopierten Graph in der RHS
			//	- Wie mit den Attributen verfahren? - Konstanten beibehalten. Parameter -> ??? (TODO!)
			Rule r1TempRule = henshinFactory.createRule();
			module.getUnits().add(r1TempRule);
			Copier r1Copier = new Copier();
			Graph r1LhsCopy = (Graph) r1Copier.copy(getFirstRule().getLhs());
			r1LhsCopy.setName("Rhs");
			r1Copier.copyReferences();
			//  - Wie mit ACs umgehen? Elemente der PACs müssten in der Isntanz auch vorhanden sein!
			//  --> TODO!
			r1TempRule.setRhs(r1LhsCopy);
			
			// - anwenden von r1Create-Regel auf einem leeren EGraph
			RuleApplication ruleApplicationR1Temp =  new RuleApplicationImpl(engine, minimalModelEGraph, r1TempRule, null);
//			Match completeMatchOfTempR1 = new MatchImpl(r1TempRule, false); // scheint falsch/überflüssig zu sein!
//			Match resultMatchOfTempR1 = new MatchImpl(r1TempRule, true); // scheint falsch/überflüssig zu sein!
//			Change createdChange = engine.createChange(r1TempRule, minimalModelEGraph, completeMatchOfTempR1, resultMatchOfTempR1);
			ruleApplicationR1Temp.execute(null);
			Match resultMatchOfTempR1 = ruleApplicationR1Temp.getResultMatch();
			
			// only for debug purpose
			List<EObject> roots = minimalModelEGraph.getRoots();
			
			 // - r2Create-Regel durch Kopie von r2Lhs erstellen mit dem kopierten Graph in der RHS
			//	- Wie mit den Attributen verfahren? - Konstanten beibehalten. Parameter -> ??? (TODO!)
			Graph lhsOfR2 = getSecondRule().getLhs();
			Rule r2TempRule = henshinFactory.createRule();
			module.getUnits().add(r2TempRule);
			Copier r2Copier = new Copier();
			Graph r2LhsCopy = (Graph) r2Copier.copy(lhsOfR2);
			r2Copier.copyReferences();
			r2LhsCopy.setName("Rhs");
			r2TempRule.setRhs(r2LhsCopy);
			 // 	- zusätzlich muss dabei auch noch die LHS geschaffen werden und die Mappings zwischen LHS und RHS
			 //		 - Die LHSKnoten und Kanten leiten sich aus den beiden matches ab!
			
//			TODO: r2Lhs und mappings müssen noch erstellt werden!
			Copier r2NodeCopier = new Copier();
			Graph tempR2Lhs = henshinFactory.createGraph("Lhs");
			r2TempRule.setLhs(tempR2Lhs);
			
			List<EObject> match2NodeTargets = match2.getNodeTargets();
			if(match2NodeTargets.size() == 0){ //TODO: entfernen. Das schient nicht zu funktionieren. Es gibt brauchbare matches, aber die zurückerhaltene NodeTargets Liste ist aus unbekanntem Grund leer. 
				System.err.println("Hier stimmt was nicht! Wenn es keine matches der zweiten REgel in den overlap gibt, dann kann es auch keien Konflikt geben.");
				System.err.println("Konflikt ist hinsichtlich des match2 fehlerhaft!");
			}
			// - ableiten des notwendigen matches für die Anwendung der r2Create-Regel
			Match completeMatchOfTempR2 = new MatchImpl(r2TempRule, false);
//			Match resultMatchOfTempR2 = new MatchImpl(r2TempRule, true);
//			Graph
			for(Node nodeOfR2Lhs : lhsOfR2.getNodes()){ // wieso nciht direkt über r2TempRule.getRhs() gehen?
				EObject minimalModelEObjectByR1 = match2.getNodeTarget(nodeOfR2Lhs);
				if(minimalModelEObjectByR1 != null){
				// find associated Node and match into rule1
					for(Node nodeInR1 : getFirstRule().getLhs().getNodes()){
						if(match1.getNodeTarget(nodeInR1) == minimalModelEObjectByR1){ //should only happen once
							Node nodeOfR1LhsCopy = (Node) r1Copier.get(nodeInR1);
							//TODO: nutzen des matches des durch die Anwendung der neuen temporären R1 entstanden ist!
							EObject newMinimalModelEObject = resultMatchOfTempR1.getNodeTarget(nodeOfR1LhsCopy);
							Node r2TempLhsNode = (Node) r2NodeCopier.copy(nodeOfR2Lhs);
							r2NodeCopier.copyReferences();
							tempR2Lhs.getNodes().add(r2TempLhsNode);
							completeMatchOfTempR2.setNodeTarget(r2TempLhsNode, newMinimalModelEObject);
							//TODO: hier ist der Fehler! es wird 
							Node r2TempRhsNode = (Node) r2Copier.get(nodeOfR2Lhs);  
							Mapping r2TempMapping = henshinFactory.createMapping(r2TempLhsNode, r2TempRhsNode);
							if(r2TempLhsNode.getGraph().getRule() != r2TempRhsNode.getGraph().getRule()){
								System.err.println("FALSCH!!!");
							}
//							DONE: in der LHS der zweiten Temp Regel fehlen die Knoten (und Kanten?) -> Knoten müssen dieser noch hinzugefügt werden!
							tempR2Lhs.getNodes().add(r2TempLhsNode);
							r2TempRule.getMappings().add(r2TempMapping); // TODO: maybe add a break here to end early.
						}
					}
				}
			}
			
			// hinzufügen der Kanten in der LHS von r2TempRule auf Grundlage der Kanten in der RHS
			MappingList r2TempMappings = r2TempRule.getMappings();
			System.err.println("Anzahl tempR2Rhs Kanten vorher: "+r2TempRule.getRhs().getEdges().size());
			// scheint überflüssig zu sein! Kanten werden bereits durch die erste temp Regel erzeugt. Ob die zweite temp Regel diese erneut erzeugt spielt keine Rolle! 
			for(Node tempR2LhsNode : tempR2Lhs.getNodes()){
				// passenden Knoten der RHS abrufen
				Node tempR2RhsNode = r2TempMappings.getImage(tempR2LhsNode, r2TempRule.getRhs());
				// über alle ausgehenden Kanten iterieren (die relevanten eingehenden Kanten werden durch die Bearbeitung des jeweils anderen Knoten abgedeckt)
				Set<Edge> outgoingEdges = new HashSet(tempR2RhsNode.getOutgoing());
				for(Edge outgoingEdge : outgoingEdges){
					//	jeweils prüfen, es für den Knoten einen passenden Knoten in der LHS gibt
					Node originNodeInLhsOfTempR2 = r2TempMappings.getOrigin(outgoingEdge.getTarget());
					if(originNodeInLhsOfTempR2 != null){
//						tempR2RhsNode.getOutgoing().remove(outgoingEdge);
						r2TempRule.getRhs().getEdges().remove(outgoingEdge);
						//	 ggf. erzeugen der Kante in der LHS.
//						henshinFactory.createEdge(originNodeInLhsOfTempR2, tempR2LhsNode, outgoingEdge.getType());
					}
				}
			}
			System.err.println("Anzahl tempR2Lhs Kanten nachher: "+r2TempRule.getRhs().getEdges().size());
			
			// Überflüssig: -->
			boolean completeMatch2 = completeMatchOfTempR2.isComplete(); System.err.println("completeMatch2: "+completeMatch2);
			boolean validMatch2 = completeMatchOfTempR2.isValid();
			boolean containsAll = minimalModelEGraph.containsAll(completeMatchOfTempR2.getNodeTargets());
			// anwenden der r2Create-Regel auf das vorherige ERgebnis unter zuhilfenahme der abgeleiteten matches
//			engine.createChange(r2TempRule, minimalModelEGraph, completeMatchOfTempR2, resultMatchOfTempR2); // falscher Ansatz!
			Iterable<Match> findMatches = engine.findMatches(r2TempRule, minimalModelEGraph, null);
			int numberOfMatches = 0;
			for(Match match : findMatches){numberOfMatches++;
				List<EObject> nodeTargets = match.getNodeTargets();
				System.err.println("amount of nodeTargets: "+nodeTargets.size());
			}
			System.out.println("amount of matches: "+numberOfMatches);
			
			Match completeMatch = null;
			
			Iterable<Match> findMatches2 = engine.findMatches(r2TempRule, minimalModelEGraph, completeMatchOfTempR2);
			int numberOfMatches2 = 0;
			for(Match match : findMatches2){numberOfMatches++;
				List<EObject> nodeTargets = match.getNodeTargets();
				System.err.println("amount of nodeTargets: "+nodeTargets.size());
				completeMatch = match;
			}
			System.out.println("amount of matches: "+numberOfMatches2);
			// <-- Überflüssig
			
			RuleApplication ruleApplicationR2Temp =  new RuleApplicationImpl(engine, minimalModelEGraph, r2TempRule, completeMatchOfTempR2);
//			ruleApplicationR2Temp.setCompleteMatch(completeMatch/*completeMatchOfTempR2*/);r2TempRule.getMappings()
			boolean execute2 = ruleApplicationR2Temp.execute(null);
			if(!execute2) System.err.println("Execution of second temp rule NOT successfull!");
			return minimalModelEGraph;
		}else {
			return minimalModelEGraph;	
		}
	}
}


