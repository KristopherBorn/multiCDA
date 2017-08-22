package org.eclipse.emf.henshin.cpa.atomic;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;

public class AtomicCoreCPA {

	private static final boolean supportInheritance = true;
	// TODO: Felder für Candidates und MinimalReasons einführen - DONE
	List<Span> candidates;
	Set<MinimalConflictReason> overallMinimalConflictReasons;
	// TODO: Methode zum abrufen dieser einführen - DONE
	// TODO: in Methode "computeConflictAtoms(...)" die Felder zu beginn zurücksetzen - wird bereits gemacht. - DONE

	/**
	 * @return the candidates
	 */
	public List<Span> getCandidates() {
		return candidates;
	}

	/**
	 * @return the reasons
	 */
	public Set<MinimalConflictReason> getMinimalConflictReasons() {
		return overallMinimalConflictReasons;
	}

	HenshinFactory henshinFactory = new HenshinFactoryImpl();

	public List<ConflictAtom> computeConflictAtoms(Rule rule1, Rule rule2) {
		
		checkNull(rule1, "rule1");
		checkNull(rule2, "rule2");

		List<ConflictAtom> result = new LinkedList<ConflictAtom>();
		candidates = computeAtomCandidates(rule1, rule2);
		overallMinimalConflictReasons = new HashSet<MinimalConflictReason>();
		for (Span candidate : candidates) {

			Set<MinimalConflictReason> minimalConflictReasons = new HashSet<>();//
			computeMinimalConflictReasons(rule1, rule2, candidate, minimalConflictReasons);

//			if (rule1.getName().contains("Feature_FROM_Feature_children_TO_Feature_Fea")
//					&& rule2.getName().contains("factoring_1-3")) {
//				System.out.println("maybe here begins the mistake!");
//			}

			overallMinimalConflictReasons.addAll(minimalConflictReasons); // to know the total amount after the analysisDuration!
			if (!minimalConflictReasons.isEmpty()) {
				result.add(new ConflictAtom(candidate, minimalConflictReasons));
				// TODO: wieso ein Atom die "reasons" benötigt ist mir noch unklar.
				// Bzw.was die Datenstruktur "Atom" überhaupt umfasst.
			}
		}
		return result;
	}

	// - elemente sammeln
	// - alle deletion-nodes
	// - alle deletion-edges mit zwei preserve-nodes
	// - vorkommen der elemente in der LHS von R2
	// - erstellen eines Graph (S1) und der Mappings in rule1 und rule2
	// Wie programmatisch instanzen des jeweiligen MM erstellen?
	// Henshins "MappingImpl" Klasse wirklich geeignet? Oder eher MatchImpl?
	public List<Span> computeAtomCandidates(Rule rule1, Rule rule2) {

		checkNull(rule1, "rule1");
		checkNull(rule2, "rule2");
		
		List<Span> result = new LinkedList<Span>();
		Action deleteAction = new Action(Action.Type.DELETE);

		// TODO: extract to Method!
		// NODE deletion - (sammeln aller löschender Knoten von R1)
		List<ModelElement> atomicDeletionElements = new LinkedList<ModelElement>(rule1.getActionNodes(deleteAction));
		// EDGE deletion - (sammeln aller löschender Kanten an zwei bewahrenden Knoten von R1)
		atomicDeletionElements.addAll(identifyAtomicDeletionEdges(rule1));
		// ATTRIBUTE change - (sammeln aller Knoten mit Attributänderungen von R1)
		Set<Node> atomicChangeNodes = new HashSet<Node>();

		// ATTRIBUTE deletion or change (also attribute change - change-use-conflicts) - (sammeln preserve Knoten mit geänderten Attributen von R1)
		Action preserveAction = new Action(Action.Type.PRESERVE);
		for(Node lhsNode : rule1.getActionNodes(preserveAction)){
//			System.out.println("lhsNode.getGraph().toString() "+lhsNode.getGraph().toString());
			// wenn eines der Attribute auf der LHS und RHS voneinader abweicht, so handelt es sich um einen change
			Node rhsNode = rule1.getMappings().getImage(lhsNode, rule1.getRhs());
			boolean attributeChanged = false;
			for(Attribute lhsAttr : lhsNode.getAttributes()){
				Attribute rhsAttr = rhsNode.getAttribute(lhsAttr.getType());
				// delete Attr. || change Attr.
				if(rhsAttr == null || !lhsAttr.getValue().equals(rhsAttr.getValue())){
					attributeChanged = true;
					atomicChangeNodes.add(lhsNode);
					break;
				}
			}
			// CHECK create Attr.
			if(!attributeChanged){
				for(Attribute rhsAttr : rhsNode.getAttributes()){
					Attribute lhsAttr = lhsNode.getAttribute(rhsAttr.getType());
					// create Attr.
					if(lhsAttr == null){
						attributeChanged = true;
						atomicChangeNodes.add(lhsNode);
						break;
					}
				}
				
			}
			// an dieser Stelle wurde zwar identifiziert, dass es eine Änderung des Attributwertes durch die 
			// erste Regel in einem preserve Knoten gibt. Ob ein Knoten dieses Typs aber überhaupt in der zweiten 
			// Regel genutzt wird ist hier noch nicht geklärt 
		}
		
		// IDENTIFIKATION potentieller nutzender Knoten! (fuer delete-use-confl.)
		// abarbeitung jedes einzelnen deletionElements
		for (ModelElement el1 : atomicDeletionElements) {
			List<ModelElement> atomicUsageElements = new LinkedList<ModelElement>();
			if (el1 instanceof Node) {
				// nach sub- und super-typ überprüfen!
				//TODO: überprüfen, ob es Attribute mit abweichenden konstanten Werten gibt!
				for(Node nodeInLhsOfR2 : rule2.getLhs().getNodes()){
					boolean r1NodeIsSuperTypeOfR2Node = false;
					boolean r2NodeIsSuperTypeOfR1Node = false;
					if(supportInheritance){
						r1NodeIsSuperTypeOfR2Node = nodeInLhsOfR2.getType().getESuperTypes().contains(((Node) el1).getType());
						r2NodeIsSuperTypeOfR1Node = ((Node) el1).getType().getEAllSuperTypes().contains(nodeInLhsOfR2.getType());
					}
					boolean identicalType = nodeInLhsOfR2.getType().equals((((Node) el1).getType()));
					
					//TODO: hier schon den möglichen S-Graph mit dem passenden Knoten erzeugen!
					// dabei den Typ des weiter unten in der Vererbungshierarchie stehenden Knotens nutzen! 
					// 		wird bereits in "addNodeToGraph" gemacht
					Graph S1 = henshinFactory.createGraph();
					Set<Mapping> rule1Mappings = new HashSet<Mapping>();
					Set<Mapping> rule2Mappings = new HashSet<Mapping>();
					Node newNodeInS1Graph = addNodeToGraph((Node)el1, (Node)nodeInLhsOfR2, S1, rule1Mappings, rule2Mappings);
					Span S1span = new Span(rule1Mappings, S1, rule2Mappings);
					
					if(r1NodeIsSuperTypeOfR2Node || r2NodeIsSuperTypeOfR1Node || identicalType){
						// Für jedes Attribut der beiden Knoten muss geprüft werden, ob es eine Konstante ist String und Zahlen.
						// Wenn es eine Konstante ist, dann muss überprüft werden, ob diese übereinstimmen.
						// Wenn es eine Variable ist, so ist der potentielle Konflikt nur vorhanden wenn die Variable für das entsprechende Attribut beider Knoten und somit den Span identisch sind.
//						boolean differingAttributeConstantsLhsR1 = false;
//						boolean differingAttributeConstantsRhsR1 = false;
						Node el1InRhsOfR1 = rule1.getMappings().getImage((Node)el1, rule1.getRhs());
						boolean nodeInR1IsDeleted = (el1InRhsOfR1 == null);
							// TODO: hinzufügen und hier gebrauchen machen von einer "Steuervariablen" um das einbeziehen von Attributen zu de-/aktivieren
						
						boolean differingConstants = false; 
						// as soon as both nodes have an attribute with a common attribute with different constant values, 
						// the analysis can stop early since the both nodes of R1 and R2 do not match to each other. 
						
						for(Attribute attrOfR2 : nodeInLhsOfR2.getAttributes()){
							if(!differingConstants){
								Attribute attrOfSameTypeInLhsOfR1 = ((Node) el1).getAttribute(attrOfR2.getType());
								
								// prüfen, ob es sich beim Attr in R1 um eine Konstanten handelt
								boolean attrOfR2IsConstant = isAttrValueAConstant(attrOfR2, rule2);
								if(attrOfSameTypeInLhsOfR1 != null){
									// prüfen, ob es sich beim Attr in R1 um eine Konstanten handelt
									boolean attrOfSameTypeInLhsOfR1IsConstant = isAttrValueAConstant(attrOfSameTypeInLhsOfR1, rule1);
									if(attrOfR2IsConstant && attrOfSameTypeInLhsOfR1IsConstant){
										//prüfen ob die Werte gleich sind.
										boolean valuesIdentical = attrOfR2.getValue().equals(attrOfSameTypeInLhsOfR1.getValue());
										if(valuesIdentical){	// Situation: KONST - KONST									
											//alles gut!
											// ein entsprechender Wert im S-Graph bzw. overlap muss gesetzt werden!
//											newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue()); //wrong code
											henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(), attrOfR2.getValue());
										}else { 	// Situation: KONST - KONST
											// zwei Konstanten mit abweichenden Werten -> Knoten passen nicht zueinander! 
											// d.h. "differingConstants" = true
											differingConstants = true;
										}
									}else {
										// mindestens eines der beiden Attribute ist eine variable
										// -> alles gut 
										// wenn eines der beiden Attribute eine Konstante ist muss ein entsprechender Wert im S-Graph bzw. overlap gesetzt werden!
										if(attrOfR2IsConstant) 	// Situation: VAR - KONST
//											newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue()); //wrong code
											henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(), attrOfR2.getValue());
										if(attrOfSameTypeInLhsOfR1IsConstant) 	// Situation: KONST - VAR
//											newNodeInS1Graph.getAttribute(attrOfSameTypeInLhsOfR1.getType()).setValue(attrOfSameTypeInLhsOfR1.getValue()); //wrong code
											henshinFactory.createAttribute(newNodeInS1Graph, attrOfSameTypeInLhsOfR1.getType(), attrOfSameTypeInLhsOfR1.getValue());
									}
									
								}else {
									// Attribute des entsprechenden EAttribute Typs war nur in R2 vorhanden!
									// -> alles gut 
									//wenn das Attribute in R2 eine Konstante ist muss ein entsprechender Wert im S-Graph bzw. overlap gesetzt werden!
									if(attrOfR2IsConstant)
//										newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue()); //wrong code
										henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(), attrOfR2.getValue());
									//TODO: wenn für das EAttribute nur eine VARiable in R2 vorliegt diese auch in den S1-Graph aufnehmen?
								}
							}
						}

						if(!differingConstants){
							//TODO: bis hierher sind alle Attribute von R2 behandelt worden.
							// Was fehlt ist die Behandlung der Attribute die nur in R1 vorkommen. 
							// Wenn es sich dabei um eine Konstante handelt, so sollte diese auch im S-Graph bzw. overlap gesetzt werden!
							for(Attribute attrOfR1Node : ((Node)el1).getAttributes()){
								Attribute attributeInR2Node = nodeInLhsOfR2.getAttribute(attrOfR1Node.getType());
								if(attributeInR2Node == null){ // Attribute nur in R1 Knoten vorhanden.
									if(isAttrValueAConstant(attrOfR1Node, rule1)){
//										newNodeInS1Graph.getAttribute(attributeInR1Node.getType()).setValue(attributeInR1Node.getValue()); //wrong code
										henshinFactory.createAttribute(newNodeInS1Graph, attrOfR1Node.getType(), attrOfR1Node.getValue());
									}
									//TODO: wenn für das EAttribute nur eine VARiable in R1 vorliegt diese auch in den S1-Graph aufnehmen?
								}
							}
						}
						if(!differingConstants)
							result.add(S1span);
					}
				}				
				
				// EList<Node> nodes = rule2.getLhs().getNodes(((Node) el1).getType());
			}
			if (el1 instanceof Edge) {
				atomicUsageElements.addAll(rule2.getLhs().getEdges(((Edge) el1).getType()));
			}
			
			
			// CREATION des Graph S1 und der Mapings in R1 und R2
			for (ModelElement el2 : atomicUsageElements) {


				//findet jetzt schon bei der Identifikation der nutzenden Knoten statt
//				if (el2 instanceof Node) {
//					Node newNodeInS1Graph = addNodeToGraph((Node)el1, (Node)el2, S1, rule1Mappings, rule2Mappings);
//					Span S1span = new Span(rule1Mappings, S1, rule2Mappings);
//					result.add(S1span);
//					//TODO: newNodeInS1Graph müssen noch die jeweiligen Attribute hinzugefügt werden!
//					// um diese erneut zu ermitteln wird der S-Graph bereits oben erzeugt!
//
//					// EClass type = ((Node) el2).getType();
//					// EPackage singleEPackageOfDomainModel = type.getEPackage();
//					// EFactory eFactoryInstance = singleEPackageOfDomainModel.getEFactoryInstance();
//					//
//					// EObject create = eFactoryInstance.create(type);
//					// el2.eResolveProxy
//				}
				if (el2 instanceof Edge) {
					
					Graph S1 = henshinFactory.createGraph();
					Set<Mapping> rule1Mappings = new HashSet<Mapping>();
					Set<Mapping> rule2Mappings = new HashSet<Mapping>();
					

					Node commonSourceNode = addNodeToGraph(((Edge) el1).getSource(), ((Edge) el2).getSource(), S1,
							rule1Mappings, rule2Mappings);
					Node commonTargetNode = addNodeToGraph(((Edge) el1).getTarget(), ((Edge) el2).getTarget(), S1,
							rule1Mappings, rule2Mappings);
					Span S1span = new Span(rule1Mappings, S1, rule2Mappings);
					result.add(S1span);

					S1.getEdges()
							.add(henshinFactory.createEdge(commonSourceNode, commonTargetNode, ((Edge) el2).getType()));
				}
			}
		}
		
		// IDENTIFIKATION potentieller nutzender Knoten! (fuer change-use-confl.)
			// abarbeitung jedes einzelnen Elements mit einem AttrChange!
			// TODO TODO TODO TODO
			// seperation of concern! Es handelt sich um zwei unterschiedliche Fälle die zwar Ähnlichkeiten aufweisen, 
			// aber im Programmcode nicht miteinander vermischt werden sollten!
			// subroutinene die identisch sind können auch als gemeinsame Methode existieren. 
			// (Dazu die Sub-Methoden sauber definieren um mögliche Unterschiede zu identifizieren!)
					
		for (Node el1 : atomicChangeNodes) {
			List<Node> atomicUsageElements = new LinkedList<Node>();
				// nach sub- und super-typ überprüfen!
				for(Node nodeInLhsOfR2 : rule2.getLhs().getNodes()){
					boolean r1NodeIsSuperTypeOfR2Node = false;
					boolean r2NodeIsSuperTypeOfR1Node = false;
					if(supportInheritance){
						r1NodeIsSuperTypeOfR2Node = nodeInLhsOfR2.getType().getESuperTypes().contains(((Node) el1).getType());
						r2NodeIsSuperTypeOfR1Node = ((Node) el1).getType().getEAllSuperTypes().contains(nodeInLhsOfR2.getType());
					}
					boolean identicalType = nodeInLhsOfR2.getType().equals((((Node) el1).getType()));
					
					//TODO: hier schon den möglichen S-Graph mit dem passenden Knoten erzeugen!
					// dabei den Typ des weiter unten in der Vererbungshierarchie stehenden Knotens nutzen! 
					// 		wird bereits in "addNodeToGraph" gemacht
					Graph S1 = henshinFactory.createGraph();
					Set<Mapping> rule1Mappings = new HashSet<Mapping>();
					Set<Mapping> rule2Mappings = new HashSet<Mapping>();
					Node newNodeInS1Graph = addNodeToGraph((Node)el1, (Node)nodeInLhsOfR2, S1, rule1Mappings, rule2Mappings);
					Span S1span = new Span(rule1Mappings, S1, rule2Mappings);
					
					if(r1NodeIsSuperTypeOfR2Node || r2NodeIsSuperTypeOfR1Node || identicalType){
						
//						Für jedes Attribut muss geprüft werden, ob es eine Konstante ist String und Zahlen.
//						Genrell sind je Attribut folgende Fälle zu unterscheiden.
//						1. Attr wird von R1 geändert und von R2 benutzt 
//							(verändern kann create&delete des Attr sein, sowie Wertänderung über Konstanten und Variablen. Kurz gesagt: LHS Value != RHS Value)
//							Ursache des change-use Konflikts -> In Ordnung
//								Im S1-Graph muss der Wert beider LHS sein. Ist eines von beiden eine Konstante, so ist dieser Wert zu nutzen.
//								Sind beides Variablen, so bietet es sich an im S1_Graph die namen der Variablen mit einem Unterstrich zu Kontainieren (valR1_valR2)
//						2. Attr ist nur in einer von beiden Regeln (d.h. das Attr taucht in LHS von R2 oder in beiden Seiten von R1 nicht auf)
//						3. Attr ist in beiden Regeln und ändert sich in R1 nicht.
//							Wenn es sich in lhsR1 und lhsR2 und Konstanten handelt müssen diese übereisntimmen.
//								Andernfalls handelt es sich nicht um einen relevanten 'use' Knoten
//							handelt es sich bei einem der beiden (oder beiden) um eine Variable, so ist es irrelevant
							
							
						Node el1InRhsOfR1 = rule1.getMappings().getImage((Node)el1, rule1.getRhs());
							// TODO: hinzufügen und hier gebrauchen machen von einer "Steuervariablen" um das einbeziehen von Attributen zu de-/aktivieren
						
						boolean differingConstants = false; 
						// as soon as both nodes have an attribute with a common attribute with different constant values, 
						// the analysis can stop early since the both nodes of R1 and R2 do not match to each other. 
						
						boolean changeAttrIsUsed = false;
						// at least one changed attributed has to be used by the node of the second rule. otherwise its not a chnage-use-conflict!
						// TODO: set up a test case for a situation with a changed attribute that ist not used!
						
						for(Attribute attrOfR2 : nodeInLhsOfR2.getAttributes()){
							if(!differingConstants){
								Attribute attrOfSameTypeInLhsOfR1 = ((Node) el1).getAttribute(attrOfR2.getType());
								// prüfen, ob es sich beim Attr in R1 um eine Konstanten handelt
								boolean attrOfR2IsConstant = isAttrValueAConstant(attrOfR2, rule2);
								if(attrOfSameTypeInLhsOfR1 != null){
									
									//Fälle:
//									R1KONST R2KONST
//									!equals: "differingConstants = true;" -> break early
//									
//									prüfen ob bei R1 eine Änderung vorliegt (r1LHS.getValue 1= r1HRS.getValue)
//										-> "changeAttrIsUsed = true;"
//										& generell setzen des Konst Wertes oder einer Variablenkombination in S1Graph

									// prüfen, ob es sich beim Attr in R1 um eine Konstanten handelt
									boolean attrOfSameTypeInLhsOfR1IsConstant = isAttrValueAConstant(attrOfSameTypeInLhsOfR1, rule1);
									if(attrOfR2IsConstant && attrOfSameTypeInLhsOfR1IsConstant){
										//prüfen ob die Werte gleich sind.
										boolean valuesIdentical = attrOfR2.getValue().equals(attrOfSameTypeInLhsOfR1.getValue());
										if(valuesIdentical){	// Situation: KONST - KONST									
											//alles gut!
											// ein entsprechender Wert im S-Graph bzw. overlap muss gesetzt werden!
//											newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue()); //wrong code
											henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(), attrOfR2.getValue());
											// handelt es sich bei dem R1Attr um einen change?
											if(!attrOfSameTypeInLhsOfR1.getValue().equals(el1InRhsOfR1.getAttribute(attrOfR2.getType()).getValue())){
												changeAttrIsUsed = true;
											}
										}else { 	// Situation: KONST - KONST
											// zwei Konstanten mit abweichenden Werten -> Knoten passen nicht zueinander! 
											// d.h. "differingConstants" = true
											differingConstants = true;
										}
									}else {
										// mindestens eines der beiden Attribute ist eine variable
										// -> alles gut 
										// wenn eines der beiden Attribute eine Konstante ist muss ein entsprechender Wert im S-Graph bzw. overlap gesetzt werden!
										if(attrOfR2IsConstant) 	// Situation: VAR - KONST
//											newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue()); //wrong code
											henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(), attrOfR2.getValue());
										if(attrOfSameTypeInLhsOfR1IsConstant) 	// Situation: KONST - VAR
//											newNodeInS1Graph.getAttribute(attrOfSameTypeInLhsOfR1.getType()).setValue(attrOfSameTypeInLhsOfR1.getValue()); //wrong code
											henshinFactory.createAttribute(newNodeInS1Graph, attrOfSameTypeInLhsOfR1.getType(), attrOfSameTypeInLhsOfR1.getValue());
										
										// handelt es sich bei dem R1Attr um einen change?
										// wenn die erste Regel das Attr löscht, so handelt es sich auch um einen Attr Change!
										Attribute attrInRhsOfR1 = el1InRhsOfR1.getAttribute(attrOfSameTypeInLhsOfR1.getType());
										if(attrInRhsOfR1 == null || !attrOfSameTypeInLhsOfR1.getValue().equals(attrInRhsOfR1.getValue())){
											changeAttrIsUsed = true;
										}
										//TODO: für VAR-VAR muss noch das Attribute im S1-Graph erstellt werden. 
										if(!attrOfR2IsConstant && !attrOfSameTypeInLhsOfR1IsConstant){
											String valueForAttr = attrOfSameTypeInLhsOfR1.getValue()+"_"+attrOfSameTypeInLhsOfR1.getValue();
											henshinFactory.createAttribute(newNodeInS1Graph, attrOfSameTypeInLhsOfR1.getType(), valueForAttr);
										}
									}
									
								}else {
									// Attribute des entsprechenden EAttribute Typs war nur in R2 vorhanden!
									// -> alles gut 
									//wenn das Attribute in R2 eine Konstante ist muss ein entsprechender Wert im S-Graph bzw. overlap gesetzt werden!
									if(attrOfR2IsConstant)
//										newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue()); //wrong code
										henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(), attrOfR2.getValue());
									//TODO: wenn für das EAttribute nur eine VARiable in R2 vorliegt diese auch in den S1-Graph aufnehmen?
								}

							}
						}

						if(!differingConstants){
							//TODO: bis hierher sind alle Attribute von R2 behandelt worden.
							// Was fehlt ist die Behandlung der Attribute die nur in R1 vorkommen. 
							// Wenn es sich dabei um eine Konstante handelt, so sollte diese auch im S-Graph bzw. overlap gesetzt werden!
							for(Attribute attrOfR1Node : ((Node)el1).getAttributes()){
								Attribute attributeInR2Node = nodeInLhsOfR2.getAttribute(attrOfR1Node.getType());
								if(attributeInR2Node == null){ // Attribute nur in R1 Knoten vorhanden.
									if(isAttrValueAConstant(attrOfR1Node, rule1)){
//										newNodeInS1Graph.getAttribute(attrOfR1Node.getType()).setValue(attrOfR1Node.getValue()); //wrong code
										henshinFactory.createAttribute(newNodeInS1Graph, attrOfR1Node.getType(), attrOfR1Node.getValue());
									}
									//TODO: wenn für das EAttribute nur eine VARiable in R1 vorliegt diese auch in den S1-Graph aufnehmen?
								}
							}
						}
						
						if(!differingConstants && changeAttrIsUsed)
							result.add(S1span);
					}
				}				
		}
					

		
		
		return result;
	}

	//TODO: diese Methode muss gut getestet werden!
	/* Was ist schlimmer: 
	 * 	Wenn eine Konstante nicht als solche erkannt wird? 
	 * 	Oder wenn eine Variable als Konstante eingeordnet wird? 
	 * Generell muss es doch ähnliche Prüfungen bzw. Behanldungen auch bereits im Kern bzw. Interpreter von Henshin geben.
	 * siehe auch "HenshinValidator.validateAttributeCondition_conditionAllParametersAreDeclared" für eine entsprechende statische Prüfung!
	 */
	private boolean isAttrValueAConstant(Attribute attr, Rule rule) {
		/* Vorgehen um AttrValue als Konstante zu identifizieren.
		 * - Konstante geht nur bei entsprechendem Datentyp
		 * 	- Bei EString sollte es Anführungszeichen haben und davon abgesehen parsable sein.
		 *  - bei numerischen Datentypen solte es entsprechend parsable sein.  
		 * 
		 */
		if(isPrimitiveDataType(attr.getType())){
			if(attributeIsParsable(attr)){
				return true;
			}
		}
			
		//TODO: hier kann/sollte noch geprüft werden, ob es auch einen passenden Regelparameter gibt.
//		EList<Parameter> parameters = rule.getParameters();
//		for(Parameter parameter : parameters){
//			parameter.get
//		}
		return false;
	}

	private List<Edge> identifyAtomicDeletionEdges(Rule rule1) {
		List<Edge> atomicDeletionEdges = new LinkedList<Edge>();
		Action deleteAction2 = new Action(Action.Type.DELETE);
		for (Edge deletionEdge : rule1.getActionEdges(deleteAction2)) {
			Action sourceNodeAction = deletionEdge.getSource().getAction();
			Action targetNodeAction = deletionEdge.getTarget().getAction();
			if (sourceNodeAction.getType().equals(Action.Type.PRESERVE)
					&& targetNodeAction.getType().equals(Action.Type.PRESERVE)) {
				// TODO: additional "deletion edge check" due to some unresolved Bug. Edges are loaded with different
				// URIs for their type.
				Edge image = rule1.getMappings().getImage(deletionEdge, rule1.getRhs());
				Node sourceNodeLhs = deletionEdge.getSource();
				Node targetNodeLhs = deletionEdge.getTarget();
				Node sourceNodeRhs = rule1.getMappings().getImage(sourceNodeLhs, rule1.getRhs());
				Node targetNodeRhs = rule1.getMappings().getImage(targetNodeLhs, rule1.getRhs());
				EList<Edge> allOutgoing = sourceNodeRhs.getOutgoing();
				URI uriOfDeletionEdgeType = EcoreUtil.getURI(deletionEdge.getType());
				boolean isHoweverAPreserveEdge = false;
				for (Edge edge : allOutgoing) {
					if (edge.getTarget() == targetNodeRhs) { //TODO: based on tests this seems to be dead code. Why? Tests missing or superfluous code?
						// check same name of URI:
						URI uriOfPotentialAssociatedEdgeType = EcoreUtil.getURI(edge.getType());
						if (uriOfDeletionEdgeType.toString().equals(uriOfPotentialAssociatedEdgeType.toString()))
							isHoweverAPreserveEdge = true;
					}
				}
				// try to resolve problems when "FeatureModelPackage.eINSTANCE" wasnt loaded!
				// allOutgoing.get(0).getType().eCrossReferences()
				// getEGenericType().equals(allOutgoing.get(1).getType());
				// EList<Edge> outgoingWithType = sourceNodeRhs.getOutgoing(deletionEdge.getType());
				// URI outgoing0_uri = EcoreUtil.getURI(allOutgoing.get(0).getType());outgoing0_uri.toString()
				// URI outgoing1_uri = EcoreUtil.getURI(allOutgoing.get(1).getType());
				// URI deleteEdge_uri = EcoreUtil.getURI(deletionEdge.getType());
				// System.out.println("HALT");
				if (!isHoweverAPreserveEdge)
					atomicDeletionEdges.add(deletionEdge);
			}
		}
		return atomicDeletionEdges;
	}

	private boolean nodeHasAttributeWithDifferingConstantValue(Node el1, EAttribute typeOfComparedAttribute, String valueOfComparedAttribute) {
		Attribute attribute = el1.getAttribute(typeOfComparedAttribute);
		if(attribute != null){
			if(attributeIsParsable(attribute)){
				//TODO: muss hier gegenebenfalls eien Anpassung vorgenommen werden für variablen (diese sind nciht parsable, können aber zum Konflikt führen!)
				if(!attribute.getValue().equals(valueOfComparedAttribute))
					return true;
			}
		}
		return false;
	}

	private boolean attributeIsParsable(Attribute attrOfR2) {
//		boolean isParsable = true;
		EAttribute type = attrOfR2.getType();
		EDataType eAttributeType = type.getEAttributeType();
//		eAttributeType.getName()
		if(attrOfR2.getType().toString() == "EString"){
			//TODO:
			// 1. check for quotes ("") 
			// 2. try to parse the value! 
			String value = attrOfR2.getValue();
			if(value.startsWith("\"") && value.endsWith("\"")){
				String[] split = value.split("\"");
				if(split.length == 2)
					return true;
			}
			return false;
		}
		if(attrOfR2.getType().toString() == "EChar"){
			//TODO:
			// 1. maybe check for quotes ("") 
			// 2. try to parse the value! 
		}
		if(eAttributeType.getName() == "EInt"){
			try {
				Integer.parseInt(attrOfR2.getValue());
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		if(attrOfR2.getType().toString() == "EDouble"){
			try {
				Double.parseDouble(attrOfR2.getValue());
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		if(attrOfR2.getType().toString() == "ELong"){
			try {
				Long.parseLong(attrOfR2.getValue());
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		if(attrOfR2.getType().toString() == "EFloat"){
			try {
				Float.parseFloat(attrOfR2.getValue());
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		return true;
	}

	private boolean isPrimitiveDataType(EAttribute type) {
		EDataType eAttributeType = type.getEAttributeType();
		if(eAttributeType != null){			
			System.err.println("eAttributeType.getName() "+eAttributeType.getName());
			//TODO: muss noch vervollständigt werden um (String, Char, Int, Double, Long) zu identifizieren.
			if(eAttributeType.getName().equals("EInt"))
				return true;
			if(eAttributeType.getName().equals("EDouble"))
				return true;
			if(eAttributeType.getName().equals("ELong"))
				return true;
			if(eAttributeType.getName().equals("EString"))
				return true;
			if(eAttributeType.getName().equals("EChar"))
				return true;
		}
		return false;
	}

	private Node addNodeToGraph(Node nodeInRule1, Node nodeInRule2, Graph S1, Set<Mapping> rule1Mappings,
			Set<Mapping> rule2Mappings) {
		// to support inheritance the subtype of both nodes must be considered 
		EClass subNodeType = identifySubNodeType(nodeInRule1, nodeInRule2); //TODO: might return null - Handle this!
		Node commonNode = henshinFactory.createNode(S1, subNodeType,
				nodeInRule1.getName() + "_" + nodeInRule2.getName());
		
		// TODO: beim Erstellen des Knoten auch ggf. die notwendigen Attribute mit erstellen!
			// ?? Alle aus beiden Knoten, oder nur die übereinstimmenden?
			// ACHTUNG! hier müssen die vier Fälle aus KONST und VAR in Einklang gebracht werden! 

		rule1Mappings.add(henshinFactory.createMapping(commonNode, nodeInRule1));
		rule2Mappings.add(henshinFactory.createMapping(commonNode, nodeInRule2));
		return commonNode;
	}

	/**
	 * identify the type of the both nodes which is the subtype of the other node. 
	 * @param node1
	 * @param node2
	 * @return returns the subnode type if one of both is, otherwise null. 
	 */
	private EClass identifySubNodeType(Node node1, Node node2) {
		if(node1.getType().equals(node2.getType()))
			return node1.getType();
		if(node1.getType().getEAllSuperTypes().contains(node2.getType()))
			return node1.getType();
		if(node2.getType().getEAllSuperTypes().contains(node1.getType()))
			return node2.getType();
		return null;
	}

	public void computeMinimalConflictReasons(Rule rule1, Rule rule2, Span s1, Set<MinimalConflictReason> minimalConflictReasons) {
		checkNull(rule1, "rule1");
		checkNull(rule2, "rule2");
		if (isMinReason(rule1, rule2, s1)) {
			minimalConflictReasons.add(new MinimalConflictReason(s1));
			return;
		}
		// is this part of the backtracking?
		Set<Span> extendedSpans = findExtensions(rule1, rule2, s1);
		for (Span extendedSpan : extendedSpans) {
			computeMinimalConflictReasons(rule1, rule2, extendedSpan, minimalConflictReasons);
		}
	}

	private boolean isMinReason(Rule rule1, Rule rule2, Span s1) {
		// TODO: ist hier der Zusammenhang zwischen C_1 und L_1, also c_1 klar?
		PushoutResult pushoutResult = constructPushout(rule1, rule2, s1);
		// TODO: wofür wird G benötigt? Vermutlich nur als Ziel der matches, oder?
		// Oder ist das nicht normalerweise das minimale Modell?
		boolean isMatchM1 = findDanglingEdgesByLHSOfRule1(rule1, pushoutResult.getMappingsOfRule1()).isEmpty(); // TODO: über den
																									// jeweiligen match
																									// sollte doch die
																									// Regel auch
																									// "erreichbar"
																									// sein. Regel als
																									// Parameter daher
																									// überflüssig.
		// ÜBERFLÜSSIG nach ERKENNTNIS! boolean isMatchM2 = findDanglingEdges(rule2,
		// pushoutResult.getMappingsOfRule2()).isEmpty();
		return (isMatchM1 /* && isMatchM2 */);
	}

	// TODO: bisher nicht weiter spezifiziert!
	/**
	 * Idee: Im prinziep
	 * 
	 * @param rule1
	 * @param rule2
	 * @param s1
	 * @return
	 */
	private PushoutResult constructPushout(Rule rule1, Rule rule2, Span s1) {
		PushoutResult pushoutResult = new PushoutResult(rule1, s1, rule2);
		return pushoutResult;
	}

	private Set<Span> findExtensions(Rule rule1, Rule rule2, Span s1) {
		PushoutResult pushoutResult = constructPushout(rule1, rule2, s1);
		List<Edge> danglingEdges = findDanglingEdgesByLHSOfRule1(rule1, pushoutResult.getMappingsOfRule1());
		System.out.println(s1.getGraph().getNodes() + " " + s1.getGraph().getEdges());
	
		List<Edge> fixingEdges = new LinkedList<>();
		for (Edge danglingEdge : danglingEdges) {
			List<Edge> fixingEdges_e = findFixingEdges(rule1, rule2, s1, danglingEdge,
					pushoutResult.getMappingsOfRule1(), pushoutResult.getMappingsOfRule2());
			if (!fixingEdges_e.isEmpty()) {
				fixingEdges.addAll(fixingEdges_e);
			} else {
				return new HashSet<Span>();// oder NULL?
			}
		}
		Set<Span> extensions = enumerateExtensions(s1, fixingEdges);
		return extensions;
	}

	/*
	 * Grundidee: die dangling edge hat als einen ihrer beiden Knoten definitiv einen Knoten aus dem Graph S1 des Spans!
	 * Aus dem Papier: "A naïve implementation of this function may enumerate all adjacent edges in L1 OHNE S1 of e's
	 * context node in S1" Optimierung aus dem Papier: nur "löschende Kanten" von L_1 berücksichtigen, da der Knoten
	 * auch bereits löschend war. (andernfalls wäre es ja nicht zur dangling edge gekommen.) Sollte es keine fixing
	 * edges geben wird eine leere Menge zurückgegeben. Ansonsten kann es antürlich mehrere fixing edges geben. Diese
	 * werden ALLE zurückgegeben!
	 * 
	 */
	// TODO: höchstwahrscheinlich werden noch als zusätzliche Übergabeparameter die Mappings m_1 und m_2 benötigt
	// --> Algo anpassen! (ansonsten kann der zugehörige knoten zur dangling edge nicht eindeutig in S1 bestimmt
	// werden.)
	// TODO: die zweite Optimierung haeb ich noch nicht verstanden!
	// TODO: mwenn (mit Daniel?) geklärt ist, dass die matches notwendig sind, dann könnte man überlegen die beiden
	// Listen von mapping edges
	// und zusätzlich auch noch den Span s1 durch das "pushoutResult" zu ersetzen, da dieses alle drei kennen
	// könnte(sollte?)
	// Alternativ wird das hinfällig wenn eine(/mehrere) zentrale Instanz(en) die MAppings verwaltet.(Stichwort
	// "MappingHandler")
	public List<Edge> findFixingEdges(Rule rule1, Rule rule2, Span s1, Edge poDangling,
			List<Mapping> mappingOfRule1InOverlapG, List<Mapping> mappingOfRule2InOverlapG) {

		HashMap<Node, Node> rule1ToOverlap = new HashMap<Node, Node>();
		HashMap<Node, Node> overlapToRule1 = new HashMap<Node, Node>();
		for (Mapping mapping : mappingOfRule1InOverlapG) {
			rule1ToOverlap.put(mapping.getOrigin(), mapping.getImage());
			overlapToRule1.put(mapping.getImage(), mapping.getOrigin());
		}
		HashMap<Node, Node> rule1ToS1 = new HashMap<Node, Node>();
		HashMap<Node, Node> s1ToRule1  = new HashMap<Node, Node>();
		for (Mapping mapping : s1.mappingsInRule1) {
			s1ToRule1.put(mapping.getOrigin(), mapping.getImage());
			rule1ToS1.put(mapping.getImage(), mapping.getOrigin());
		}
		
		//Suitable edges for this purpose are all adjacent edges in L1\S1 of e's adjacent node in S1 

		List<Edge> fixingEdges = new LinkedList<Edge>();
		Node poDanglingSource = poDangling.getSource();
		Node poDanglingTarget = poDangling.getTarget();

		// VORSICHT! vermutlich NPE!!!
		// Lösung: zweistufiges vorgehen: erst Mapping holen und nur wenn dieses != null ist darauf zugreifen!
		Node r1DanglingSource = overlapToRule1.get(poDanglingSource);
		Node r1DanglingTarget = overlapToRule1.get(poDanglingTarget);
		Node s1DanglingSource = rule1ToS1.get(r1DanglingSource);
		Node s1DanglingTarget = rule1ToS1.get(r1DanglingTarget);

		if (s1DanglingSource == null && s1DanglingTarget == null)  {
			throw new RuntimeException("By definition of the pushout, it cannot be the case that both adjacent nodges "
					+ "of a dangling edge are in S1!");
		}
		
		System.out.println("Current graph: "+s1.getGraph().getNodes() + " " +s1.getGraph().getEdges());

		if (s1DanglingSource != null) { // target is dangling
			EList<Edge> r1DanglingSourceOutgoing = r1DanglingSource.getOutgoing(poDangling.getType());
			for (Edge eOut : r1DanglingSourceOutgoing) {
				if (eOut.getAction().getType().equals(Action.Type.DELETE) && rule1ToS1.get(eOut.getTarget()) == null)
					fixingEdges.add(eOut);
			}
		} else if (s1DanglingTarget != null) { // source is dangling
			EList<Edge> r1DanglingTargetIncoming = r1DanglingTarget.getIncoming(poDangling.getType());
			for (Edge eIn : r1DanglingTargetIncoming) {
				if (eIn.getAction().getType().equals(Action.Type.DELETE) && rule1ToS1.get(eIn.getSource()) == null)
					fixingEdges.add(eIn);
			}
		} else {
			System.err.println("Neither source nor target of tangling edge were dangling!");
		}

		System.out.println("found "+fixingEdges.size()+ " fixing edges: "+fixingEdges);
		return fixingEdges;
	}

	// TODO: bisher nicht weiter spezifiziert!
	public Set<Span> enumerateExtensions(Span s1, List<Edge> fixingEdges) {
		Set<Span> extensions = new HashSet<Span>(); // LinkedList<Span>();
		// Für jede Kante in fixingEdges wird ein neuer Span erzeugt und dieser um die jeweilige Kante vergrößert.
		for (Edge fixingEdge : fixingEdges) {
			// Dabei müssen auch entsprechend neue Mappings erzeugt werden!
			// TODO: die Kopie für dne neuen Span muss zuerst erstellt werden und die neuen Knotne und KAnten in der
			// Kopie erstellt werden,
			// sowie die neuen Mappings der Kopie hinzugefügt werden!
			Span extSpan = new Span(s1);
			SpanMappings maps = new SpanMappings(extSpan);
			
			Node fixingSource = fixingEdge.getSource();
			Node fixingTarget = fixingEdge.getTarget();
			if (maps.rule1ToS1.get(fixingSource) != null && maps.rule1ToS1.get(fixingTarget) != null)
				throw new RuntimeException("Fixing edge is already present in S1!");

			// TODO: prüfen, dass die Art des erstellens einer Kopie korrekt ist.
			// ToDo: (/Fehler!) zur Erweiterung des Span um eine Kante der Regel 1 kann es mehrere passende Kanten der
			// Regel 2 geben.
			// -> eine weitere Schleife ist notwendig!
			Node extNode = null;
			Node s1Existing = null;
			Node s1Source = null;
			Node s1Target = null;

			// Mapping mappingOfsourceNodeOfFixingEdgeInRule1 = newSpan
			// .getMappingFromGraphToRule1(fixingSource);

			// wenn NULL - erstellen von Knoten und Kante in graph, und mapping
			if (maps.rule1ToS1.get(fixingSource) == null) { // source ist baumelnd!
				// Knoten in graph von Span erstellen
				extNode = henshinFactory.createNode(extSpan.getGraph(), fixingSource.getType(),
						fixingSource.getName() + "_");
				s1Source = extNode;
				// TODO: Mapping in den Span hinzufügen!
				Mapping newSourceNodeMapping = henshinFactory.createMapping(extNode, fixingSource);
				extSpan.mappingsInRule1.add(newSourceNodeMapping);
				s1Target = maps.rule1ToS1.get(fixingTarget);
				s1Existing = s1Target;
				System.err.println(" source war baumelnd!");
			} else
				// wenn NULL - erstellen von Knoten und Kante in graph, und mapping
				if (maps.rule1ToS1.get(fixingTarget) == null) {
				// Knoten in graph von Span erstellen
				extNode = henshinFactory.createNode(extSpan.getGraph(), fixingTarget.getType(),
						fixingTarget.getName() + "_");
				s1Target = extNode;
				// TODO: Mapping in den Span hinzufügen!
				Mapping newSourceNodeMapping = henshinFactory.createMapping(extNode, fixingTarget);
				extSpan.mappingsInRule1.add(newSourceNodeMapping);
				s1Source = maps.rule1ToS1.get(fixingSource);
				s1Existing = s1Source;
				System.err.println(" target war baumelnd!");
			} else {
				throw new RuntimeException("weder source noch target war baumelnd!");
			}
			// create corresponding edge of fixingEdge in graph of span.
			Edge s1Fixing = henshinFactory.createEdge(s1Source, s1Target, fixingEdge.getType());
			Node r2existing = maps.s1ToRule2.get(s1Existing);
			boolean sourceExistsInS1 = (s1Existing == s1Fixing.getSource());
			createExtension(extensions, extSpan, extNode, s1Fixing, r2existing, sourceExistsInS1);
		}
		return extensions;
	}

	private void createExtension(Set<Span> extensions, Span extSpan, Node extNode, Edge s1Fixing, Node r2existing,
			boolean outgoing) {
		EList<Edge> r2corresponding = findCorrespondingEdges(extSpan, s1Fixing, r2existing, outgoing);
		for (Edge s2cor : r2corresponding) {
			if(outgoing){//TODO: ENTFERNEN!!!!
				System.err.println("outgoing situation");
			} else {
				System.err.println("incoming situation");
			}
			System.err.println("");
			Span span = new Span(extSpan, extNode, outgoing ? s2cor.getTarget() : s2cor.getSource());
		
			extensions.add(span);
		}
	}

	private EList<Edge> findCorrespondingEdges(Span newSpan, Edge fixingEdgeInGraphOfSpan, Node r2existing,
			boolean outgoing) {
		EList<Edge> potentialUsageEdgesOfFixingEdgeInRule2EList = new BasicEList<Edge>();
		EList<Edge> edges = outgoing ? r2existing.getOutgoing(fixingEdgeInGraphOfSpan.getType())
				: r2existing.getIncoming(fixingEdgeInGraphOfSpan.getType());
		for (Edge e : edges) {
			boolean found = false;
			for (Mapping mappingInRule2 : newSpan.mappingsInRule2) {
				Node node = outgoing ? e.getTarget() : e.getSource();
				if (node == mappingInRule2.getImage())
					found = true;
			}
			if (!found) {
				potentialUsageEdgesOfFixingEdgeInRule2EList.add(e);
			}
		}
		return potentialUsageEdgesOfFixingEdgeInRule2EList;
	}

	// TODO: bisher nicht weiter spezifiziert!
	// Funktionalität gibt es sehr wahrscheinlich schon in Henshin. (NEIN! - hier wird mit mappings anstelle von matches
	// gearbeitet!)
	// Spezifikation der MEthode: Gibt die Menge der Kanten aus der Regel zurück, die beim anwenden auf den overlapGraph
	// zu einer dangling edge führen würden!
	public List<Edge> findDanglingEdgesByLHSOfRule1(Rule rule, List<Mapping> embedding) {
		HashMap<Node, Node> l1ToOverlap = new HashMap<Node, Node>();
		HashMap<Node, Node> overlapToL1 = new HashMap<Node, Node>();
		for (Mapping mapping : embedding) { //Hier kommt es zu Problemen, wenn durch die Mappings zwei Knoten aus einer Regel einem Knoten im Graph zugeordnet sind.
			//eigentlich dürfte das nicht passieren, da wir injektives matching erlauben, aber unsicher ist es dadurch im Fehlerfall dennoch!
			l1ToOverlap.put(mapping.getOrigin(), mapping.getImage()); 
			overlapToL1.put(mapping.getImage(), mapping.getOrigin());
		}

		EList<Node> l1DeletingNodes = rule.getActionNodes(new Action(Action.Type.DELETE));
		List<Edge> danglingEdges = new LinkedList<Edge>();
		// für jeden gelöschten Knoten prüfen, dass auch all seine Kanten gelöscht werden.
		for (Node l1Deleting : l1DeletingNodes) {  
			Node poDeleting = l1ToOverlap.get(l1Deleting); //(18.04.2017) durch "get()" kann null zurückgegeben werden und es kommt dann im Anschluss zur NPE!
			if(poDeleting == null)
				System.out.println();
			EList<Edge> poDeletingsEdges = poDeleting.getAllEdges();
			for (Edge poDeletingsEdge : poDeletingsEdges) {
				Node poDelSource = poDeletingsEdge.getSource();
				Node l1DelSource = overlapToL1.get(poDelSource);
				if (l1DelSource == null) {
					danglingEdges.add(poDeletingsEdge);
					continue;
				}

				Node poDelTarget= poDeletingsEdge.getTarget();
				Node l1DelTarget = overlapToL1.get(poDelTarget);
				if (l1DelTarget == null) {
					danglingEdges.add(poDeletingsEdge);
				}
			}
		}

		System.out.println(embedding.get(0).getImage().getGraph().getNodes().size());
		System.out.println("found "+danglingEdges.size()+ " dangling edges: "+danglingEdges);
		return danglingEdges;
	}
	
	// Spezifikation der Methode: Gibt die Menge der Kanten aus der Regel zurück, die beim Anwenden auf den overlapGraph
	// zu einer dangling edge führen würden!
	public List<Edge> findDanglingEdgesByLHSOfRule2(Set<Mapping> mappingInRule1, Rule rule, Set<Mapping> mappingInRule2) {
		
		HashMap<Node, Node> l1ToOverlap = new HashMap<Node, Node>();
		HashMap<Node, Node> overlapToL1 = new HashMap<Node, Node>();
		for (Mapping mapping : mappingInRule1) { //Hier kommt es zu Problemen, wenn durch die Mappings zwei Knoten aus einer Regel einem Knoten im Graph zugeordnet sind.
			//eigentlich dürfte das nicht passieren, da wir injektives matching vorsehen, aber unsicher ist es dadurch im Fehlerfall dennoch!
			overlapToL1.put(mapping.getOrigin(), mapping.getImage()); 
			l1ToOverlap.put(mapping.getImage(), mapping.getOrigin());
		}
		
		HashMap<Node, Node> l2ToOverlap = new HashMap<Node, Node>();
		HashMap<Node, Node> overlapToL2 = new HashMap<Node, Node>();
		for (Mapping mapping : mappingInRule2) { //Hier kommt es zu Problemen, wenn durch die Mappings zwei Knoten aus einer Regel einem Knoten im Graph zugeordnet sind.
			//eigentlich dürfte das nicht passieren, da wir injektives matching vorsehen, aber unsicher ist es dadurch im Fehlerfall dennoch!
			overlapToL2.put(mapping.getOrigin(), mapping.getImage()); 
			l2ToOverlap.put(mapping.getImage(), mapping.getOrigin());
		}

		EList<Node> l2DeletingNodes = rule.getActionNodes(new Action(Action.Type.DELETE));
		List<Edge> danglingEdges = new LinkedList<Edge>();
		// für jeden gelöschten Knoten prüfen, dass auch all seine Kanten gelöscht werden.
		for (Node l2Deleting : l2DeletingNodes) {  
			Node nodeInOverlapToBeDeletedByRule2= l2ToOverlap.get(l2Deleting); //(18.04.2017) durch "get()" kann null zurückgegeben werden und es kommt dann im Anschluss zur NPE!
			if(nodeInOverlapToBeDeletedByRule2 != null){
				EList<Edge> incomingEdgesInOverlapToCheck = nodeInOverlapToBeDeletedByRule2.getIncoming();// getAllEdges();
				for (Edge potentialDanglingEdgeInOverlap : incomingEdgesInOverlapToCheck) {
					// Da der Knoten durch die zweite Regel gelöscht wird müssen auch all seine Kanten 
					// mit denen er im Overlap verbunden ist durch die zweite REgel gelöscht werden
					Node sourceNodeOfPotentialDanglingEdgeInOverlap = potentialDanglingEdgeInOverlap.getSource();
					Node sourceNodeOfPotentialDanglingEdgeInRule2 = overlapToL2.get(sourceNodeOfPotentialDanglingEdgeInOverlap); //könnte null zurückgeben!
					if(sourceNodeOfPotentialDanglingEdgeInRule2 != null){
						Edge potentialDanglingEdgeInRule2 = sourceNodeOfPotentialDanglingEdgeInRule2.getOutgoing(potentialDanglingEdgeInOverlap.getType(), l2Deleting);
						if(potentialDanglingEdgeInRule2 == null)
							danglingEdges.add(potentialDanglingEdgeInOverlap);
					}else {
						danglingEdges.add(potentialDanglingEdgeInOverlap);
					}
				}
				EList<Edge> outgoingEdgesInOverlapToCheck = nodeInOverlapToBeDeletedByRule2.getOutgoing();
				for (Edge potentialDanglingEdgeInOverlap : outgoingEdgesInOverlapToCheck) {
					// Da der Knoten durch die zweite Regel gelöscht wird müssen auch all seine Kanten 
					// mit denen er im Overlap verbunden ist durch die zweite REgel gelöscht werden
					Node targetNodeOfPotentialDanglingEdgeInOverlap = potentialDanglingEdgeInOverlap.getTarget();
					Node targetNodeOfPotentialDanglingEdgeInRule2 = overlapToL2.get(targetNodeOfPotentialDanglingEdgeInOverlap); //könnte null zurückgeben!
					if(targetNodeOfPotentialDanglingEdgeInRule2 != null){
						Edge potentialDanglingEdgeInRule2 = targetNodeOfPotentialDanglingEdgeInRule2.getIncoming(potentialDanglingEdgeInOverlap.getType(), l2Deleting);
						if(potentialDanglingEdgeInRule2 == null)
							danglingEdges.add(potentialDanglingEdgeInOverlap);
					}else {
						danglingEdges.add(potentialDanglingEdgeInOverlap);
					}
				}
				//TODO: kannes sein, dass hier die asugehenden Kanten der ersten REgel vernachlässigt werden? Auch diese dürfen nicht hängend bleiben?
				// Sobald es durch Regel1 noch weitere ausgehende Kanten gibt, die nicht durch REgel2 abgedeckt sind kommt es zu eienr dangling edge und somit invaliden initialReasons
				// Allerdings din ddie KAnten dabei ausgenommen, die bereits durch den overlap abgedeckt waren!
				// Im Umkehrschluss heißt das, dass alle dem Knoten anhängen Kanten in Regel1 ausgenommen sind, die bereits durch den Graph abgedeckt sind.
				Node associatedNodeInRule1 = overlapToL1.get(nodeInOverlapToBeDeletedByRule2);
				// alle Kanten prüfen, ob diese bereits durch den Graph abgedeckt sind
				// oder
				EList<Edge> allIncomingEdgesInRule1ConnectedToDeletedNodeByRule2 = associatedNodeInRule1.getIncoming();
				for(Edge incomingEdgeInRule1 : allIncomingEdgesInRule1ConnectedToDeletedNodeByRule2){
					//TODO: Wenn eine Kante vom gleichen Typ in gleicher Richtung an diesem Knoten auch bereits von Regel2 gelöscht wird, so ist diese als potentielle dangling-edge ausgenommen!
					EList<Edge> incomingEdgesInRule2OfType = l2Deleting.getIncoming(incomingEdgeInRule1.getType());
					boolean referenceDeletionByRule2 = false;
					for(Edge incomingEdgeInRule2OfType : incomingEdgesInRule2OfType){
						if(incomingEdgeInRule2OfType.getAction().getType() == Action.Type.DELETE)
							referenceDeletionByRule2 = true;
					}
					if(!referenceDeletionByRule2){
						Node sourceOfIncomingInRule1 = incomingEdgeInRule1.getSource();
						Node sourceNodeInOverlap = l1ToOverlap.get(sourceOfIncomingInRule1);
						if(sourceNodeInOverlap == null){
							// wenn es den verbundenen Knoten der Regel1 nicht im Overlap gibt, dann kann die zweite Regel nicht 
							// TODO: überprüfen wie es sich verhält, wenn die zweite REgel einfach das größere löschende Muster hat und das löschende Muster der ersten Regel nur ein Teil dessen ist.
							// Aber dann würde vermutlich die Anwendung von Regel1 zu dangling Kanten der Regel 2 führen. Wo ist das abgedeckt?
							danglingEdges.add(incomingEdgeInRule1);
						}
					}
				}
				EList<Edge> allOutgoingEdgesInRule1ConnectedToDeletedNodeByRule2 = associatedNodeInRule1.getOutgoing();
				for(Edge outgoingEdgeInRule1 : allOutgoingEdgesInRule1ConnectedToDeletedNodeByRule2){
					//TODO: Wenn eine Kante vom gleichen Typ in gleicher Richtung an diesem Knoten auch bereits von Regel2 gelöscht wird, so ist diese als potentielle dangling-edge ausgenommen!
					EList<Edge> outgoingEdgesInRule2OfType = l2Deleting.getOutgoing(outgoingEdgeInRule1.getType());
					boolean referenceDeletionByRule2 = false;
					for(Edge outgoingEdgeInRule2OfType : outgoingEdgesInRule2OfType){
						if(outgoingEdgeInRule2OfType.getAction().getType() == Action.Type.DELETE)
							referenceDeletionByRule2 = true;
					}
					if(!referenceDeletionByRule2){
						Node targetOfOutgoingInRule1 = outgoingEdgeInRule1.getTarget();
						Node targetNodeInOverlap = l1ToOverlap.get(targetOfOutgoingInRule1);
						if(targetNodeInOverlap == null){
							// wenn es den verbundenen Knoten der Regel1 nicht im Overlap gibt, dann kann die zweite Regel nicht 
							// TODO: überprüfen wie es sich verhält, wenn die zweite REgel einfach das größere löschende Muster hat und das löschende Muster der ersten Regel nur ein Teil dessen ist.
							// Aber dann würde vermutlich die Anwendung von Regel1 zu dangling Kanten der Regel 2 führen. Wo ist das abgedeckt?
							danglingEdges.add(outgoingEdgeInRule1);
						}
					}
				}
			}
		}

		System.out.println(mappingInRule2.iterator().next().getImage().getGraph().getNodes().size());
		System.out.println("found "+danglingEdges.size()+ " dangling edges: "+danglingEdges);
		return danglingEdges;
	}

	
	private enum ConflictAtomKind{
		DELETE_NODE,
		DELETE_EDGE,
		CHANGE_ATTR
	}

	public Span newSpan(Mapping nodeInRule1Mapping, Graph s1, Mapping nodeInRule2Mapping) {
		return new Span(nodeInRule1Mapping, s1, nodeInRule2Mapping);
	}

	public Span newSpan(Set<Mapping> rule1Mappings, Graph s1, Set<Mapping> rule2Mappings) {
		return new Span(rule1Mappings, s1, rule2Mappings);
	}

	// TODO: noch ist unklar ob eine solche Datenstruktur notwendig ist,
	// oder es sich um Instanzen einer bereits bekannten Datenstruktur handelt.
	// Je nach Ergebnis löschen oder in eigenständiges class-file auslagern.

	// Generell: Laut Definition 3 ist ein Span z.B. C1<-incl-S1-match->L2
	// d.h. drei Graphen verbunden über eine Inklusion und einen Match



	public PushoutResult newPushoutResult(Rule rule1, Span span, Rule rule2) {
		return new PushoutResult(rule1, span, rule2);
	}


	
	public class UnsupportedRuleException extends RuntimeException {
//		TODO
	}

	
	
	
	//TODO: extract to "ExceptionUtilities" class
	/**
	 * Checks to see if an object is null, and if so 
	 * generates an IllegalArgumentException with a fitting message.
	 * 
	 * @param o The object to check against null.
	 * @param name The name of the object, used to format the exception message
	 *
	 * @throws IllegalArgumentException if o is null.
	 */
	public static void checkNull(Object o, String name) 
	    throws IllegalArgumentException {
	   if (null == o)
	      throw new IllegalArgumentException(name + " must not be null");
	}

	public static void checkNull(Object o) throws IllegalArgumentException {
	   checkNull(o, "object");
	} 
	
	
	/* not supported:
	 * - multi rules
	 * - application conditions
	 * - inheritance?
	 * (- attribute conditions [NACs & PACs])
	 *  
	 * TODO: normalen CPA check zur Hilfe nehmen um zu erkennen was noch geprüft werden könnte.
	 */
	public boolean isRuleSupported(Rule rule){
		if(rule.getMultiRules().size() > 0){
			throw new RuntimeException("multi rules are not supported");
			// TODO: nochmal nachlesen wie Exception und return value ggf. doch zu vereinbaren sind und ob das hier ggf. Sinn macht.
//			return false;
		}
		if(rule.getLhs().getNACs().size() > 0)
			throw new RuntimeException("negative application conditions (NAC) are not supported");
			// TODO: nochmal nachlesen wie Exception und return value ggf. doch zu vereinbaren sind und ob das hier ggf. Sinn macht.
//			return false;
		if(rule.getLhs().getPACs().size() > 0)
			throw new RuntimeException("positive application conditions (PAC) are not supported");
			// TODO: nochmal nachlesen wie Exception und return value ggf. doch zu vereinbaren sind und ob das hier ggf. Sinn macht.
//			return false;
		return true;
	}

	
	//TODO: ist dieser "zweistufige" Ansatz überhaupt gut? (Also die Trennung in die zwei Methoden)
	public Set<InitialConflictReason> computeInitialReason(Set<MinimalConflictReason> minimalConflictReasons){
		Set<InitialConflictReason> initialReason = new HashSet<InitialConflictReason>();
//		Set<InitialConflictReason> minimalConflictReasonsInternal = new HashSet<InitialConflictReason>();
//		for(Span span : minimalConflictReasons){
//			InitialConflictReason cr = new InitialConflictReason(span);
//			minimalConflictReasonsInternal.add(cr);
//		}
		for(MinimalConflictReason currentMCR : minimalConflictReasons){
			Set<MinimalConflictReason> remainingMCR = new HashSet<MinimalConflictReason>(minimalConflictReasons);
			remainingMCR.remove(currentMCR);
			
			initialReason.addAll(computeInitialReasons(currentMCR, remainingMCR));
			InitialConflictReason singleMcrCr = new InitialConflictReason(currentMCR);
			initialReason.add(singleMcrCr);
		}
//		initialReason.addAll(minimalConflictReasons); //Die einzelnen MCR sind auch CR. Dementsprechend gilt immer: CR.size() >= MCR.size() korrekt?
		return initialReason;
	}
	
	private Set<InitialConflictReason> computeInitialReasons(InitialConflictReason currentCR, Set<MinimalConflictReason> combinationMCR){
		Set<InitialConflictReason> resultInitialReasons = new HashSet<InitialConflictReason>(); 
		Set<InitialConflictReason> processedMCR = new HashSet<InitialConflictReason>();
		for(MinimalConflictReason combinedMCR : combinationMCR){
			processedMCR.add(combinedMCR);
				// (17.04.2017) ERKENNTNIS: es dürfen keine MCRs vereinigt werden die auf den gleichen "deletionElements" basieren!
				if(!crAndMcrHaveCommonDeletionElement(currentCR, combinedMCR)){
					//TODO: die Methode 'findCommonNodesAndJoinToNewInitialReason' berücksichtigt nicht, 
					//		dass es auch Fälle gibt in denen die beiden MCR vollkommen 'disjoint' sind, aber dennoch einen gemeinsamen intialReason bilden.
					//		(Siehe Beispiel aus Festschrift Papier!)
					InitialConflictReason initialReason = findCommonNodesAndJoinToNewInitialReason(currentCR, combinedMCR/*, commonNodes*/);
					if(initialReason != null){
						resultInitialReasons.add(initialReason);
						
						//weitere Kombinationen aus neuen InitialConflictReason mit restlichen MCR bilden:
						Set<MinimalConflictReason> remainingMCR = new HashSet<MinimalConflictReason>(combinationMCR);
						remainingMCR.removeAll(processedMCR);
						resultInitialReasons.addAll(computeInitialReasons(initialReason, remainingMCR));
					}
				}
		}
		return resultInitialReasons;
	}
	
	// wenn 
	private boolean crAndMcrHaveCommonDeletionElement(InitialConflictReason initialReasonToBeExtended, MinimalConflictReason extendingMinimalConflictReason) {
		Set<ModelElement> deletionElementsInInitialReason = initialReasonToBeExtended.getDeletionElementsInRule1();
		for(ModelElement elementInfMCR : extendingMinimalConflictReason.getDeletionElementsInRule1()){
			if(deletionElementsInInitialReason.contains(elementInfMCR))
				return true;
		}
		return false;
	}

	private Span joinToNewSpan(Span currentMCR, Span combinedMCR, Set<List<Node>> commonNodes) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param span1
	 * @param span2
	 * @return a new InitialConflictReason or <code>null</code> if this ist not possible
	 */
	private InitialConflictReason findCommonNodesAndJoinToNewInitialReason(InitialConflictReason span1, InitialConflictReason span2) {
		/**
		 * Wann sind zwei Knoten in 'Spans' gleich? (Also zwei Knoten im Graph des Span)
		 * Vermutlich wenn:
		 * 1. Beide Knoten (im Graph) den gleichen Typ haben
		 * (2. vermutlich, wenn diese den gelichen Namen haben. ABER: dabei kann es sich auch um einen Fehler beim neu benennen handeln)
		 * 	(UND bei Regeln diekeien Namen für die Nodes einsetzen, oder diese wiederholen kommt es zu Problemen!)
		 * DAHER:
		 * 2. Für beide Knoten das Mapping in die erste Regel das gleiche Ziel haben.
		 * UND:
		 * 3. Für beide Knoten das Mapping in die zweite Regel das geliche Ziel haben  
		 */
		//Ggf. lässt sich vom Wissen gebrauch machen, dass sich zwei MCR nur in den nicht-löschenden Knoten überlappen können. IST DAS SO???
		
		//TODO: Wenn sich zwei MCRs in drei Knoten überlagern können, gibt es dann verschiedene CRs? Solche die sich in zwei und oslche die sich in drei Kntoen überlagern???
		
		// TODO: muss die Zuordnung von MCRs zu CRs bewahrt werden? Und wozu?
		
		Map<Node,Node> nodeInGraph2ToNodeInGraph1 = new HashMap<Node,Node>();
		
		for(Node nodeOfSpan1 : span1.getGraph().getNodes()){
			for(Node nodeOfSpan2 : span2.getGraph().getNodes()){
				boolean sameType = (nodeOfSpan1.getType() == nodeOfSpan2.getType());
				if(sameType){
//					span1.getMappingIntoRule1(nodeOfSpan1);
//					span2.getMappingIntoRule2(nodeOfSpan2);
					boolean sameImageInRule1 = (span1.getMappingIntoRule1(nodeOfSpan1).getImage() == span2.getMappingIntoRule1(nodeOfSpan2).getImage());
					boolean sameImageInRule2 = (span1.getMappingIntoRule2(nodeOfSpan1).getImage() == span2.getMappingIntoRule2(nodeOfSpan2).getImage());
					if(sameImageInRule1 && sameImageInRule2){
						nodeInGraph2ToNodeInGraph1.put(nodeOfSpan2, nodeOfSpan1);
					} else if (sameImageInRule1 ^ sameImageInRule2) {
						System.err.println("ERROR!!! - found illegal situation!!!1");
						System.err.println("ERROR!!! - found illegal situation!!!2");
						System.err.println("ERROR!!! - found illegal situation!!!3");
						System.err.println("ERROR!!! - found illegal situation!!!4");
						System.err.println("ERROR!!! - found illegal situation!!!5");
						return null;
					}
				}
			}
		}
		
		
//		TODO: hier fehlt scheinbar die Überprüfung, dass es durch den join zweier MCR nicht zu einer Zuordnung eines Knotens einer Regel
//		zu mehreren Knoten der anderen Regel kommt!
//		Wie lässt sich dies bewerkstelligen?
//		Ist dies nicht der Fall, wenn bei den beiden booleschen Werten oben einer von beiden True zurückgibt und der andere false?
		// DONE!!!! in else-if Block mit "return null"
		
//		if(nodeInGraph2ToNodeInGraph1.size() == 0){
//			System.err.println("gibts das? Macht für eienn 'join' eigentlich keinen Sinn!");
			// Das ist der Fall wie im Beispiel der Festschrift. Einzelne MCRs sind vollkommen disjunkt hinsichtlich der Menge an Elementen.
//			return null;
//		}
			
			
		
		// Wie "Join" von zwei Spans ermöglichen?
		// Originalspans sollen unverändert bleiben!
		
		// Die Anzahl der Mappings muss der Summe beider MAppings abzüglich der gefundenen gemeinsamen Knoten entsprechen!

		// Wie bilden eines gemeinsamen Graphs?
		
		// ERkenntniss: es kann keine doppelten Kanten geben, oder? Das könnte es vereinfachen! 
		// ABER: bei den "ge-jointen" Knoten müssen alle ein- und ausgehenden Kanten angepasst werden! 
		
		
		/**kurzer VErsuch
		 * - Graph1 kopieren in Graph1'
		 * - Graph2 kopieren in Graph2'
		 * - Kanten die mit den identifizierten DuplikatKnoten in Regel2' verbudnen sind anspassen auf den zugehörigen duplikatKnoten in Graph1'
		 * - alle Kanten von Graph2' in Graph1' werfen
		 * - Alle DuplikatKnoten aus Graph2' löschen.
		 * - Alle verbleibenden Knoten in Graph2' zu denen in Graph1' werfen.
		 * 
		 *  Was ist mit den Mappings? Diese müssen entweder kopiert oder neu erzeugt werden. 
		 *  Informationsgrundlage sind Graph1 und Graph2
		 *   Am Ende muss es für jeden Knoten im resultierenden Graph (Graph1') ein Mapping in beide Regeln geben!
		 */
		
//		TODO!
		
		 // - Graph1 kopieren in Graph1'
		Copier graph1Copier = new Copier();
		EObject copyOfGraph1 = graph1Copier.copy(span1.getGraph());
		Graph graph1Copy = (Graph) copyOfGraph1;
		graph1Copier.copyReferences(); // NOTWENDIG!!!
		
		// MAPPINGS of Graph1:
		Copier mappingOfSpan1Copier = new Copier();
		Collection<Mapping> mappingsOfSpan1InRule1Copies = mappingOfSpan1Copier.copyAll(span1.getMappingsInRule1());
		mappingOfSpan1Copier.copyReferences();
		Collection<Mapping> mappingsOfSpan1InRule2Copies = mappingOfSpan1Copier.copyAll(span1.getMappingsInRule2());
		mappingOfSpan1Copier.copyReferences();
		//DONE: alle Mappings ausgehend von Graph1 auf copyOfGraph1 anpassen
		for(Mapping mapping : mappingsOfSpan1InRule1Copies){
			Node newOrigin = (Node) graph1Copier.get(mapping.getOrigin()); //TODO VORSICHT(!) Wenn .get() 'null' zurück gibt kommt es zu einer NPE!
			mapping.setOrigin(newOrigin);
		}
		for(Mapping mapping : mappingsOfSpan1InRule2Copies){
			Node newOrigin = (Node) graph1Copier.get(mapping.getOrigin()); //TODO VORSICHT(!) Wenn .get() 'null' zurück gibt kommt es zu einer NPE!
			mapping.setOrigin(newOrigin);
		}

		
		 // - Graph2 kopieren in Graph2'
		Copier graph2Copier = new Copier();
		EObject copyOfGraph2 = graph2Copier.copy(span2.getGraph());
		Graph graph2Copy = (Graph) copyOfGraph2;
		graph2Copier.copyReferences(); // NOTWENDIG!!!
		
		// MAPPINGS of Graph2:
		Copier mappingOfSpan2Copier = new Copier();
		Collection<Mapping> mappingsOfSpan2InRule1Copies = mappingOfSpan2Copier.copyAll(span2.getMappingsInRule1());
		mappingOfSpan2Copier.copyReferences();
		Collection<Mapping> mappingsOfSpan2InRule2Copies = mappingOfSpan2Copier.copyAll(span2.getMappingsInRule2());
		mappingOfSpan2Copier.copyReferences();
		// DONE: alle Mappings ausgehend von Graph2 auf copyOfGraph2 anpassen
		for(Mapping mapping : mappingsOfSpan2InRule1Copies){
			Node newOrigin = (Node) graph2Copier.get(mapping.getOrigin()); //TODO VORSICHT(!) Wenn .get() 'null' zurück gibt kommt es zu einer NPE!
			mapping.setOrigin(newOrigin);
		}
		for(Mapping mapping : mappingsOfSpan2InRule2Copies){
			Node newOrigin = (Node) graph2Copier.get(mapping.getOrigin()); //TODO VORSICHT(!) Wenn .get() 'null' zurück gibt kommt es zu einer NPE!
			mapping.setOrigin(newOrigin);
		}
		
		// - Kanten die mit den identifizierten DuplikatKnoten in Regel2' 
		//			verbunden sind anspassen auf den zugehörigen duplikatKnoten in Graph1'
		List<Node> duplicateNodesInCopyOfGraph2 = new LinkedList<Node>();
		for(Edge edgeInGraph2 : span2.getGraph().getEdges()){
			if(nodeInGraph2ToNodeInGraph1.keySet().contains(edgeInGraph2.getSource())){
				// zugehörige Kante in kopiertem Graph2 identifizieren
				EObject edgeInCopy = graph2Copier.get(edgeInGraph2);
				Edge edgeToAdaptInGraph2Copy = (Edge) edgeInCopy;
				// entsprechend abändern des Knotens der Kopie
					// zugehörigen Knoten in kopiertem Graph1 identfizieren
				Node nodeInGraph1 = nodeInGraph2ToNodeInGraph1.get(edgeInGraph2.getSource());
				EObject newSourceInCopy = graph1Copier.get(nodeInGraph1);
				Node newSourceNodeInCopy = (Node) newSourceInCopy;
				//TODO: ggf. prüfen, dass die Kntoen vom gelichen Typ sind, oder zumindest einer der Typen vom anderen erbt!
				duplicateNodesInCopyOfGraph2.add(edgeToAdaptInGraph2Copy.getSource());
				edgeToAdaptInGraph2Copy.setSource(newSourceNodeInCopy);
			}
			if(nodeInGraph2ToNodeInGraph1.keySet().contains(edgeInGraph2.getTarget())){
				// zugehörige Kante in kopiertem Graph2 identifizieren
				EObject edgeInCopy = graph2Copier.get(edgeInGraph2);
				Edge edgeToAdaptInGraph2Copy = (Edge) edgeInCopy;
				// entsprechend abändern des Knotens der Kopie
					// zugehörigen Knoten in kopiertem Graph1 identfizieren
				Node nodeInGraph1 = nodeInGraph2ToNodeInGraph1.get(edgeInGraph2.getTarget());
				EObject newTargetInCopy = graph1Copier.get(nodeInGraph1);
				Node newTargetNodeInCopy = (Node) newTargetInCopy;
				//TODO: ggf. prüfen, dass die Kntoen vom gelichen Typ sind, oder zumindest einer der Typen vom anderen erbt!
				duplicateNodesInCopyOfGraph2.add(edgeToAdaptInGraph2Copy.getTarget());
				edgeToAdaptInGraph2Copy.setTarget(newTargetNodeInCopy);
			}				
		}
			
		
		//TODO: extract to Method?
		//MAPPINGS - entfernen der überzählingen Mappings durch das vereinen von Knoten der beiden Graphs (der beiden verinigten Spans)!
		List<Mapping> mappingsInRule1ToRemove = new LinkedList<Mapping>();
		for(Mapping mappingOfSpan2InRule1 : mappingsOfSpan2InRule1Copies){
			if(duplicateNodesInCopyOfGraph2.contains(mappingOfSpan2InRule1.getOrigin())){
				mappingsInRule1ToRemove.add(mappingOfSpan2InRule1);
			}
		}
		mappingsOfSpan2InRule1Copies.removeAll(mappingsInRule1ToRemove);
		
		List<Mapping> mappingsInRule2ToRemove = new LinkedList<Mapping>();
		for(Mapping mappingOfSpan2InRule2 : mappingsOfSpan2InRule2Copies){
			if(duplicateNodesInCopyOfGraph2.contains(mappingOfSpan2InRule2.getOrigin())){
				mappingsInRule2ToRemove.add(mappingOfSpan2InRule2);
			}
		}
		mappingsOfSpan2InRule2Copies.removeAll(mappingsInRule2ToRemove);
		
		
		// überflüssige Knoten aus kopiertem Graph 2 entfernen
		// - Alle DuplikatKnoten aus Graph2' löschen.
		graph2Copy.getNodes().removeAll(duplicateNodesInCopyOfGraph2); //TODO: prüfen, ob das erfolgreich war. Gggf. nciht, wenn es in der Liste der zu entfernenden Knoten Duplikate gibt?
		// - Alle verbleibenden Knoten in Graph2' zu denen in Graph1' werfen.
		graph1Copy.getNodes().addAll(graph2Copy.getNodes());
		// - alle Kanten von Graph2' in Graph1' werfen
		graph1Copy.getEdges().addAll(graph2Copy.getEdges());
		
		
		 // 
		 //  Was ist mit den Mappings? Diese müssen entweder kopiert oder neu erzeugt werden. 
		 //  Informationsgrundlage sind Graph1 und Graph2
		 //   Am Ende muss es für jeden Knoten im resultierenden Graph (Graph1') ein Mapping in beide Regeln geben!
		
		
		// TODO: Liste für die gemeinsamen Mappings in rule1
		Set<Mapping> mappingsOfNewSpanInRule1 = new HashSet<Mapping>();
		mappingsOfNewSpanInRule1.addAll(mappingsOfSpan1InRule1Copies);
		mappingsOfNewSpanInRule1.addAll(mappingsOfSpan2InRule1Copies);
		
		// TODO: Liste für die gemeinsamen Mappings in rule2
		Set<Mapping> mappingsOfNewSpanInRule2 = new HashSet<Mapping>();
		mappingsOfNewSpanInRule2.addAll(mappingsOfSpan1InRule2Copies);
		mappingsOfNewSpanInRule2.addAll(mappingsOfSpan2InRule2Copies);
		
//		DONE: neuen Span aus dem Graph sowie den mappings in den neuen Graph erzeugen!
		Set<MinimalConflictReason> originMCR = new HashSet<MinimalConflictReason>();
		if(span1 instanceof MinimalConflictReason){
			originMCR.add((MinimalConflictReason) span1);
		}else {
			originMCR.addAll(span1.getOriginMCRs());
		}
		if(span2 instanceof MinimalConflictReason){
			originMCR.add((MinimalConflictReason) span2);
		}else {
			originMCR.addAll(span2.getOriginMCRs());
		}
		InitialConflictReason newInitialReason =  new InitialConflictReason(mappingsOfNewSpanInRule1, graph1Copy, mappingsOfNewSpanInRule2, originMCR);
		
		return newInitialReason;
	}

	public Set<ConflictAtom> extractEdgeConflictAtoms(List<ConflictAtom> computedConflictAtoms) {
		Set<ConflictAtom> edgeConflictAtoms = new HashSet<ConflictAtom>();
		for(ConflictAtom ca : computedConflictAtoms){
			if(ca.isDeleteEdgeConflictAtom())
				edgeConflictAtoms.add(ca);
		}
		return edgeConflictAtoms;
	}

	public Set<ConflictReason> computeConflictReasons(List<ConflictAtom> conflictAtoms,
			Set<InitialConflictReason> initialReasons) {
		Set<ConflictReason> conflictReasonsDerivedFromInitialReason = new HashSet<ConflictReason>();
		Set<MinimalConflictReason> originMCRs = new HashSet<MinimalConflictReason>();
		for (InitialConflictReason initialReason : initialReasons) {
			originMCRs.addAll(initialReason.getOriginMCRs());
			Set<ConflictAtom> byInitialReasonCoveredEdgeConflictAtoms = initialReason.getCoveredEdgeConflictAtoms();
			Set<ConflictAtom> allEdgeConflictAtoms = extractEdgeConflictAtoms(conflictAtoms);
			allEdgeConflictAtoms.removeAll(byInitialReasonCoveredEdgeConflictAtoms);
			Set<ConflictAtom> byInitialReasonUncoveredConflictAtoms = allEdgeConflictAtoms;
			Set<ConflictReason> allDerivedConflictReasons = initialReason
					.getAllDerivedConflictReasons(byInitialReasonUncoveredConflictAtoms);
			conflictReasonsDerivedFromInitialReason.addAll(allDerivedConflictReasons);
		}
		return conflictReasonsDerivedFromInitialReason;
	}

}