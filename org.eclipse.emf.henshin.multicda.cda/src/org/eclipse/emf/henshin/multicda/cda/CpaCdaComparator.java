package org.eclipse.emf.henshin.multicda.cda;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.util.InterpreterUtil;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.conflict.EssentialConflictReason;
import org.eclipse.emf.henshin.multicda.cpa.result.Conflict;
import org.eclipse.emf.henshin.multicda.cpa.result.CriticalPair;

/**
 * This class is intended to identify ess.CPs and ConflictReasons without a matching partner in the in each others cp set.  
 * This class is intended NOT to: 
 * - implement the the comparable interface
 * - make use of java.util.Comparator 
 * @author born
 *
 */
public class CpaCdaComparator {
	
	

	public CpaCdaComparator() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public CompareResult compare(Set<CriticalPair> essentialCPs, Set<EssentialConflictReason> conflictReasons){
		CompareResult compareResult = new CompareResult(essentialCPs, conflictReasons);
		return compareResult;
	}
	
	
	//TODO: exctract to normal class
	public class CompareResult {
		
		Engine engine = new EngineImpl();
		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;
		
		Set<CriticalPair> unassignedCPs;
		Set<EssentialConflictReason> unassignedCRs;
		
		Map<CriticalPair, Set<EssentialConflictReason>> CpaToCda = new HashMap<CriticalPair, Set<EssentialConflictReason>>();
		Map<EssentialConflictReason, Set<CriticalPair>> CdaToCpa = new HashMap<EssentialConflictReason, Set<CriticalPair>>();
		
		
		/* Konstruktor:
		 * bei n CPs und m CRs - ausführen von n*m pattern matching mit der Engine! 
		 */
		public CompareResult(Set<CriticalPair> essentialCPs, Set<EssentialConflictReason> conflictReasons){
			unassignedCPs = new HashSet<CriticalPair>(essentialCPs);
			unassignedCRs = new HashSet<EssentialConflictReason>(conflictReasons);
			for(CriticalPair cp : essentialCPs){
				for(EssentialConflictReason cr : conflictReasons){
					
					Pushout pr = cr.getPushoutResult();
					
					// Load the instance into an EGraph:
//					EObject minimalModelOfCP = cp.getMinimalModel();
					// hierbei handelt es sich leider nur um eine Instanz des Metamodells von ECore
//					EGraph graph = new EGraphImpl(minimalModelOfCP); // (TODO: was verbirgt sich dahinter, dass auch eine EMenge von EObjects übergeben werden kan udn akn man sich das zu nutze machen?)
					
//					// Create a rule application:
//					RuleApplication ra = new RuleApplicationImpl(engine);
//					ra.setEGraph(graph);
					
					Rule dummyPO_rule = henshinFactory.createRule();
					//TODO: hier überprüfen, dass "pr.getResultGraph()" noch '==' null ist
					dummyPO_rule.setLhs(pr.getResultGraph());
					
					// creating a appropriate module for the export, with the aim to keep the provided rules and their modules unchanged
					Module module = (Module) EcoreUtil.copy(cr.getRule1().getModule()); //TODO: introduce some checks and a more sophisticated way to get a valid module.
					// since even unsatisfactory copies of the rules and units have been created, those have to be removed.
					module.getUnits().clear(); module.getImports().get(0); ((Conflict)cp).getMatch1().getNodeTargets();
					Conflict conflict = ((Conflict)cp);
					EGraph graph = conflict.getMinimalModelEGraph();
					module.getUnits().add(dummyPO_rule);
					
//					System.err.println("number of Nodes/Edges in LHS rule:"+pr.getResultGraph().getNodes().size()+"/"+pr.getResultGraph().getEdges().size());
//					System.err.println("number of Nodes/Edges in graph:"+graph.toString());
					
//					Module module = cr.getRule1().getModule();
//					module.get
//					dummyPO_rule.get
					
//					ra.setRule(dummyPO_rule);
//					ra.setParameterValue("client", "Alice");
//					ra.setParameterValue("accountId", 5);
					Iterable<Match> matches = engine.findMatches(dummyPO_rule, graph, null);
					// TODO: über die beim Pushout gebildete Zuordnung von Regel zu Graph (mappings), könnte ein partial match vorgegeben werden.
					
					boolean matchesFound = false;
					int numberOfMatches = 0;
					for(Match match : matches){ 
						matchesFound = true; // kürzer über: matches.iterator(). hasNext() - ABER: kein debugging ob es zu mehreren matches gekommen ist.
						numberOfMatches++;
					}

					// TODO: Zuordnung der Ergebnisse
					boolean numberOfNodesAreEqual = pr.getResultGraph().getNodes().size() == graph.size();
					boolean numberOfEdgesAreEqual = pr.getResultGraph().getEdges().size() == InterpreterUtil.countEdges(graph);
//					System.err.println("matches found: "+matchesFound+" Nodes: "+numberOfNodesAreEqual +" Edges: "+numberOfEdgesAreEqual);
					if(matchesFound && numberOfNodesAreEqual && numberOfEdgesAreEqual){
						unassignedCPs.remove(cp);
						unassignedCRs.remove(cr);
						

						// Map<CriticalPair, Set<ConflictReason>> 
						Set<EssentialConflictReason> crs = CpaToCda.get(cp);
						if(crs == null){
							crs = new HashSet<EssentialConflictReason>();
							CpaToCda.put(cp, crs);
						}
						crs.add(cr);
						
						// Map<ConflictReason, Set<CriticalPair>> 
						Set<CriticalPair> cps = CdaToCpa.get(cr);
						if(cps == null){
							cps = new HashSet<CriticalPair>();
							CdaToCpa.put(cr, cps);
						}
						cps.add(cp);
					}
				}
			}
		}


		/**
		 * @return the unassignedCPs
		 */
		public Set<CriticalPair> getUnassignedCPs() {
			return unassignedCPs;
		}


		/**
		 * @return the unassignedCRs
		 */
		public Set<EssentialConflictReason> getUnassignedCRs() {
			return unassignedCRs;
		}

		/**
		 * @return the unassignedCPs
		 */
		public Set<CriticalPair> getAssignedCPs() {
			return CpaToCda.keySet();
		}

		/**
		 * @return the unassignedCRs
		 */
		public Set<EssentialConflictReason> getAssignedCRs() {
			return CdaToCpa.keySet();
		}
		
		/**
		 * @return the unassignedCPs
		 */
		public Set<CriticalPair> getCPsOfCR(EssentialConflictReason conflictReason) {
			return CdaToCpa.get(conflictReason);
		}

		/**
		 * @return the unassignedCRs
		 */
		public Set<EssentialConflictReason> getCRsOfCP(CriticalPair criticalPair) {
			return CpaToCda.get(criticalPair);
		}
		
		
		/* Methoden für:
		 * - [done] Abruf der nicht zugeordneten CPs (Ergebnis: Set)
		 * - [done] Abruf der nicht zugeordneten CRs (Ergebnis: Set)
		 * - [done] Abruf der zugeordneten CPs (Ergebnis: Set)
		 * - [done] Abruf der zugeordneten CRs (Ergebnis: Set)
		 * - [done] Abruf der CPs zu einem CR (Ergebnis: Set)
		 * - [done] Abruf der CRs zu einem CP (Ergebnis: Set)
		 * - Abruf ob es in allen Ergebnissen Mehrfachzuordnungen gab.
		 * - Abruf aller Mehrfachzuordnungen.
		 */
		
	}
	
	

}
