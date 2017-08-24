package org.eclipse.emf.henshin.cpa.atomic;

import java.util.Collection;
import java.util.Collections;
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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialReason;
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

public class ConflictAnalysis {
	private static final boolean supportInheritance = true;

	List<Span> candidates;
	Set<MinimalConflictReason> overallMinimalConflictReasons;
	private Rule rule1;
	private Rule rule2;

	public ConflictAnalysis(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
	}

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

	public Set<MinimalConflictReason> computeMinimalConflictReasons() {
		if (overallMinimalConflictReasons == null) {
			computeConflictAtoms();
		}
		return overallMinimalConflictReasons;
	}

	public ConflictAtom hasConflicts() {
		List<ConflictAtom> cas = computeConflictAtoms(true);
		if (cas.isEmpty())
			return null;
		else
			return cas.get(0);
	}

	public List<ConflictAtom> computeConflictAtoms(boolean... earlyExit) {

		checkNull(rule1, "rule1");
		checkNull(rule2, "rule2");

		List<ConflictAtom> result = new LinkedList<ConflictAtom>();
		candidates = computeAtomCandidates(rule1, rule2);
		overallMinimalConflictReasons = new HashSet<MinimalConflictReason>();
		for (Span candidate : candidates) {

			Set<MinimalConflictReason> minimalConflictReasons = new HashSet<>();
			computeMinimalConflictReasons(rule1, rule2, candidate, minimalConflictReasons);

			overallMinimalConflictReasons.addAll(minimalConflictReasons); // to
																			// know
																			// the
																			// total
																			// amount
																			// after
																			// the
																			// analysisDuration!
			if (!minimalConflictReasons.isEmpty()) {
				result.add(new ConflictAtom(candidate, minimalConflictReasons));
			}

			if (!result.isEmpty() && earlyExit.length > 0 && earlyExit[0] == true)
				return result;
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
		// EDGE deletion - (sammeln aller löschender Kanten an zwei bewahrenden
		// Knoten von R1)
		atomicDeletionElements.addAll(identifyAtomicDeletionEdges(rule1));
		// ATTRIBUTE change - (sammeln aller Knoten mit Attributänderungen von
		// R1)
		Set<Node> atomicChangeNodes = new HashSet<Node>();

		// ATTRIBUTE deletion or change (also attribute change -
		// change-use-conflicts) - (sammeln preserve Knoten mit geänderten
		// Attributen von R1)
		Action preserveAction = new Action(Action.Type.PRESERVE);
		for (Node lhsNode : rule1.getActionNodes(preserveAction)) {
			// System.out.println("lhsNode.getGraph().toString()
			// "+lhsNode.getGraph().toString());
			// wenn eines der Attribute auf der LHS und RHS voneinader abweicht,
			// so handelt es sich um einen change
			Node rhsNode = rule1.getMappings().getImage(lhsNode, rule1.getRhs());
			boolean attributeChanged = false;
			for (Attribute lhsAttr : lhsNode.getAttributes()) {
				Attribute rhsAttr = rhsNode.getAttribute(lhsAttr.getType());
				// delete Attr. || change Attr.
				if (rhsAttr == null || !lhsAttr.getValue().equals(rhsAttr.getValue())) {
					attributeChanged = true;
					atomicChangeNodes.add(lhsNode);
					break;
				}
			}
			// CHECK create Attr.
			if (!attributeChanged) {
				for (Attribute rhsAttr : rhsNode.getAttributes()) {
					Attribute lhsAttr = lhsNode.getAttribute(rhsAttr.getType());
					// create Attr.
					if (lhsAttr == null) {
						attributeChanged = true;
						atomicChangeNodes.add(lhsNode);
						break;
					}
				}

			}
			// an dieser Stelle wurde zwar identifiziert, dass es eine Änderung
			// des Attributwertes durch die
			// erste Regel in einem preserve Knoten gibt. Ob ein Knoten dieses
			// Typs aber überhaupt in der zweiten
			// Regel genutzt wird ist hier noch nicht geklärt
		}

		// IDENTIFIKATION potentieller nutzender Knoten! (fuer
		// delete-use-confl.)
		// abarbeitung jedes einzelnen deletionElements
		for (ModelElement el1 : atomicDeletionElements) {
			List<ModelElement> atomicUsageElements = new LinkedList<ModelElement>();
			if (el1 instanceof Node) {
				// nach sub- und super-typ überprüfen!
				// TODO: überprüfen, ob es Attribute mit abweichenden konstanten
				// Werten gibt!
				for (Node nodeInLhsOfR2 : rule2.getLhs().getNodes()) {
					boolean r1NodeIsSuperTypeOfR2Node = false;
					boolean r2NodeIsSuperTypeOfR1Node = false;
					if (supportInheritance) {
						r1NodeIsSuperTypeOfR2Node = nodeInLhsOfR2.getType().getESuperTypes()
								.contains(((Node) el1).getType());
						r2NodeIsSuperTypeOfR1Node = ((Node) el1).getType().getEAllSuperTypes()
								.contains(nodeInLhsOfR2.getType());
					}
					boolean identicalType = nodeInLhsOfR2.getType().equals((((Node) el1).getType()));

					// TODO: hier schon den möglichen S-Graph mit dem passenden
					// Knoten erzeugen!
					// dabei den Typ des weiter unten in der
					// Vererbungshierarchie stehenden Knotens nutzen!
					// wird bereits in "addNodeToGraph" gemacht
					Graph S1 = henshinFactory.createGraph();
					Set<Mapping> rule1Mappings = new HashSet<Mapping>();
					Set<Mapping> rule2Mappings = new HashSet<Mapping>();
					Node newNodeInS1Graph = addNodeToGraph((Node) el1, (Node) nodeInLhsOfR2, S1, rule1Mappings,
							rule2Mappings);
					Span S1span = new Span(rule1Mappings, S1, rule2Mappings);

					if (r1NodeIsSuperTypeOfR2Node || r2NodeIsSuperTypeOfR1Node || identicalType) {
						// Für jedes Attribut der beiden Knoten muss geprüft
						// werden, ob es eine Konstante ist String und Zahlen.
						// Wenn es eine Konstante ist, dann muss überprüft
						// werden, ob diese übereinstimmen.
						// Wenn es eine Variable ist, so ist der potentielle
						// Konflikt nur vorhanden wenn die Variable für das
						// entsprechende Attribut beider Knoten und somit den
						// Span identisch sind.
						// boolean differingAttributeConstantsLhsR1 = false;
						// boolean differingAttributeConstantsRhsR1 = false;
						Node el1InRhsOfR1 = rule1.getMappings().getImage((Node) el1, rule1.getRhs());
						boolean nodeInR1IsDeleted = (el1InRhsOfR1 == null);
						// TODO: hinzufügen und hier gebrauchen machen von einer
						// "Steuervariablen" um das einbeziehen von Attributen
						// zu de-/aktivieren

						boolean differingConstants = false;
						// as soon as both nodes have an attribute with a common
						// attribute with different constant values,
						// the analysis can stop early since the both nodes of
						// R1 and R2 do not match to each other.

						for (Attribute attrOfR2 : nodeInLhsOfR2.getAttributes()) {
							if (!differingConstants) {
								Attribute attrOfSameTypeInLhsOfR1 = ((Node) el1).getAttribute(attrOfR2.getType());

								// prüfen, ob es sich beim Attr in R1 um eine
								// Konstanten handelt
								boolean attrOfR2IsConstant = isAttrValueAConstant(attrOfR2, rule2);
								if (attrOfSameTypeInLhsOfR1 != null) {
									// prüfen, ob es sich beim Attr in R1 um
									// eine Konstanten handelt
									boolean attrOfSameTypeInLhsOfR1IsConstant = isAttrValueAConstant(
											attrOfSameTypeInLhsOfR1, rule1);
									if (attrOfR2IsConstant && attrOfSameTypeInLhsOfR1IsConstant) {
										// prüfen ob die Werte gleich sind.
										boolean valuesIdentical = attrOfR2.getValue()
												.equals(attrOfSameTypeInLhsOfR1.getValue());
										if (valuesIdentical) { // Situation:
																// KONST - KONST
											// alles gut!
											// ein entsprechender Wert im
											// S-Graph bzw. overlap muss gesetzt
											// werden!
											// newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue());
											// //wrong code
											henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
													attrOfR2.getValue());
										} else { // Situation: KONST - KONST
											// zwei Konstanten mit abweichenden
											// Werten -> Knoten passen nicht
											// zueinander!
											// d.h. "differingConstants" = true
											differingConstants = true;
										}
									} else {
										// mindestens eines der beiden Attribute
										// ist eine variable
										// -> alles gut
										// wenn eines der beiden Attribute eine
										// Konstante ist muss ein entsprechender
										// Wert im S-Graph bzw. overlap gesetzt
										// werden!
										if (attrOfR2IsConstant) // Situation:
																// VAR - KONST
											// newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue());
											// //wrong code
											henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
													attrOfR2.getValue());
										if (attrOfSameTypeInLhsOfR1IsConstant) // Situation:
																				// KONST
																				// -
																				// VAR
											// newNodeInS1Graph.getAttribute(attrOfSameTypeInLhsOfR1.getType()).setValue(attrOfSameTypeInLhsOfR1.getValue());
											// //wrong code
											henshinFactory.createAttribute(newNodeInS1Graph,
													attrOfSameTypeInLhsOfR1.getType(),
													attrOfSameTypeInLhsOfR1.getValue());
									}

								} else {
									// Attribute des entsprechenden EAttribute
									// Typs war nur in R2 vorhanden!
									// -> alles gut
									// wenn das Attribute in R2 eine Konstante
									// ist muss ein entsprechender Wert im
									// S-Graph bzw. overlap gesetzt werden!
									if (attrOfR2IsConstant)
										// newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue());
										// //wrong code
										henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
												attrOfR2.getValue());
									// TODO: wenn für das EAttribute nur eine
									// VARiable in R2 vorliegt diese auch in den
									// S1-Graph aufnehmen?
								}
							}
						}

						if (!differingConstants) {
							// TODO: bis hierher sind alle Attribute von R2
							// behandelt worden.
							// Was fehlt ist die Behandlung der Attribute die
							// nur in R1 vorkommen.
							// Wenn es sich dabei um eine Konstante handelt, so
							// sollte diese auch im S-Graph bzw. overlap gesetzt
							// werden!
							for (Attribute attrOfR1Node : ((Node) el1).getAttributes()) {
								Attribute attributeInR2Node = nodeInLhsOfR2.getAttribute(attrOfR1Node.getType());
								if (attributeInR2Node == null) { // Attribute
																	// nur in R1
																	// Knoten
																	// vorhanden.
									if (isAttrValueAConstant(attrOfR1Node, rule1)) {
										// newNodeInS1Graph.getAttribute(attributeInR1Node.getType()).setValue(attributeInR1Node.getValue());
										// //wrong code
										henshinFactory.createAttribute(newNodeInS1Graph, attrOfR1Node.getType(),
												attrOfR1Node.getValue());
									}
									// TODO: wenn für das EAttribute nur eine
									// VARiable in R1 vorliegt diese auch in den
									// S1-Graph aufnehmen?
								}
							}
						}
						if (!differingConstants)
							result.add(S1span);
					}
				}

				// EList<Node> nodes = rule2.getLhs().getNodes(((Node)
				// el1).getType());
			}
			if (el1 instanceof Edge) {
				atomicUsageElements.addAll(rule2.getLhs().getEdges(((Edge) el1).getType()));
			}

			// CREATION des Graph S1 und der Mapings in R1 und R2
			for (ModelElement el2 : atomicUsageElements) {

				// findet jetzt schon bei der Identifikation der nutzenden
				// Knoten statt
				// if (el2 instanceof Node) {
				// Node newNodeInS1Graph = addNodeToGraph((Node)el1, (Node)el2,
				// S1, rule1Mappings, rule2Mappings);
				// Span S1span = new Span(rule1Mappings, S1, rule2Mappings);
				// result.add(S1span);
				// //TODO: newNodeInS1Graph müssen noch die jeweiligen Attribute
				// hinzugefügt werden!
				// // um diese erneut zu ermitteln wird der S-Graph bereits oben
				// erzeugt!
				//
				// // EClass type = ((Node) el2).getType();
				// // EPackage singleEPackageOfDomainModel = type.getEPackage();
				// // EFactory eFactoryInstance =
				// singleEPackageOfDomainModel.getEFactoryInstance();
				// //
				// // EObject create = eFactoryInstance.create(type);
				// // el2.eResolveProxy
				// }
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

		// IDENTIFIKATION potentieller nutzender Knoten! (fuer
		// change-use-confl.)
		// abarbeitung jedes einzelnen Elements mit einem AttrChange!
		// TODO TODO TODO TODO
		// seperation of concern! Es handelt sich um zwei unterschiedliche Fälle
		// die zwar Ähnlichkeiten aufweisen,
		// aber im Programmcode nicht miteinander vermischt werden sollten!
		// subroutinene die identisch sind können auch als gemeinsame Methode
		// existieren.
		// (Dazu die Sub-Methoden sauber definieren um mögliche Unterschiede zu
		// identifizieren!)

		for (Node el1 : atomicChangeNodes) {
			List<Node> atomicUsageElements = new LinkedList<Node>();
			// nach sub- und super-typ überprüfen!
			for (Node nodeInLhsOfR2 : rule2.getLhs().getNodes()) {
				boolean r1NodeIsSuperTypeOfR2Node = false;
				boolean r2NodeIsSuperTypeOfR1Node = false;
				if (supportInheritance) {
					r1NodeIsSuperTypeOfR2Node = nodeInLhsOfR2.getType().getESuperTypes()
							.contains(((Node) el1).getType());
					r2NodeIsSuperTypeOfR1Node = ((Node) el1).getType().getEAllSuperTypes()
							.contains(nodeInLhsOfR2.getType());
				}
				boolean identicalType = nodeInLhsOfR2.getType().equals((((Node) el1).getType()));

				// TODO: hier schon den möglichen S-Graph mit dem passenden
				// Knoten erzeugen!
				// dabei den Typ des weiter unten in der Vererbungshierarchie
				// stehenden Knotens nutzen!
				// wird bereits in "addNodeToGraph" gemacht
				Graph S1 = henshinFactory.createGraph();
				Set<Mapping> rule1Mappings = new HashSet<Mapping>();
				Set<Mapping> rule2Mappings = new HashSet<Mapping>();
				Node newNodeInS1Graph = addNodeToGraph((Node) el1, (Node) nodeInLhsOfR2, S1, rule1Mappings,
						rule2Mappings);
				Span S1span = new Span(rule1Mappings, S1, rule2Mappings);

				if (r1NodeIsSuperTypeOfR2Node || r2NodeIsSuperTypeOfR1Node || identicalType) {

					// Für jedes Attribut muss geprüft werden, ob es eine
					// Konstante ist String und Zahlen.
					// Genrell sind je Attribut folgende Fälle zu unterscheiden.
					// 1. Attr wird von R1 geändert und von R2 benutzt
					// (verändern kann create&delete des Attr sein, sowie
					// Wertänderung über Konstanten und Variablen. Kurz gesagt:
					// LHS Value != RHS Value)
					// Ursache des change-use Konflikts -> In Ordnung
					// Im S1-Graph muss der Wert beider LHS sein. Ist eines von
					// beiden eine Konstante, so ist dieser Wert zu nutzen.
					// Sind beides Variablen, so bietet es sich an im S1_Graph
					// die namen der Variablen mit einem Unterstrich zu
					// Kontainieren (valR1_valR2)
					// 2. Attr ist nur in einer von beiden Regeln (d.h. das Attr
					// taucht in LHS von R2 oder in beiden Seiten von R1 nicht
					// auf)
					// 3. Attr ist in beiden Regeln und ändert sich in R1 nicht.
					// Wenn es sich in lhsR1 und lhsR2 und Konstanten handelt
					// müssen diese übereisntimmen.
					// Andernfalls handelt es sich nicht um einen relevanten
					// 'use' Knoten
					// handelt es sich bei einem der beiden (oder beiden) um
					// eine Variable, so ist es irrelevant

					Node el1InRhsOfR1 = rule1.getMappings().getImage((Node) el1, rule1.getRhs());
					// TODO: hinzufügen und hier gebrauchen machen von einer
					// "Steuervariablen" um das einbeziehen von Attributen zu
					// de-/aktivieren

					boolean differingConstants = false;
					// as soon as both nodes have an attribute with a common
					// attribute with different constant values,
					// the analysis can stop early since the both nodes of R1
					// and R2 do not match to each other.

					boolean changeAttrIsUsed = false;
					// at least one changed attributed has to be used by the
					// node of the second rule. otherwise its not a
					// chnage-use-conflict!
					// TODO: set up a test case for a situation with a changed
					// attribute that ist not used!

					for (Attribute attrOfR2 : nodeInLhsOfR2.getAttributes()) {
						if (!differingConstants) {
							Attribute attrOfSameTypeInLhsOfR1 = ((Node) el1).getAttribute(attrOfR2.getType());
							// prüfen, ob es sich beim Attr in R1 um eine
							// Konstanten handelt
							boolean attrOfR2IsConstant = isAttrValueAConstant(attrOfR2, rule2);
							if (attrOfSameTypeInLhsOfR1 != null) {

								// Fälle:
								// R1KONST R2KONST
								// !equals: "differingConstants = true;" ->
								// break early
								//
								// prüfen ob bei R1 eine Änderung vorliegt
								// (r1LHS.getValue 1= r1HRS.getValue)
								// -> "changeAttrIsUsed = true;"
								// & generell setzen des Konst Wertes oder einer
								// Variablenkombination in S1Graph

								// prüfen, ob es sich beim Attr in R1 um eine
								// Konstanten handelt
								boolean attrOfSameTypeInLhsOfR1IsConstant = isAttrValueAConstant(
										attrOfSameTypeInLhsOfR1, rule1);
								if (attrOfR2IsConstant && attrOfSameTypeInLhsOfR1IsConstant) {
									// prüfen ob die Werte gleich sind.
									boolean valuesIdentical = attrOfR2.getValue()
											.equals(attrOfSameTypeInLhsOfR1.getValue());
									if (valuesIdentical) { // Situation: KONST -
															// KONST
										// alles gut!
										// ein entsprechender Wert im S-Graph
										// bzw. overlap muss gesetzt werden!
										// newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue());
										// //wrong code
										henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
												attrOfR2.getValue());
										// handelt es sich bei dem R1Attr um
										// einen change?
										if (!attrOfSameTypeInLhsOfR1.getValue()
												.equals(el1InRhsOfR1.getAttribute(attrOfR2.getType()).getValue())) {
											changeAttrIsUsed = true;
										}
									} else { // Situation: KONST - KONST
										// zwei Konstanten mit abweichenden
										// Werten -> Knoten passen nicht
										// zueinander!
										// d.h. "differingConstants" = true
										differingConstants = true;
									}
								} else {
									// mindestens eines der beiden Attribute ist
									// eine variable
									// -> alles gut
									// wenn eines der beiden Attribute eine
									// Konstante ist muss ein entsprechender
									// Wert im S-Graph bzw. overlap gesetzt
									// werden!
									if (attrOfR2IsConstant) // Situation: VAR -
															// KONST
										// newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue());
										// //wrong code
										henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
												attrOfR2.getValue());
									if (attrOfSameTypeInLhsOfR1IsConstant) // Situation:
																			// KONST
																			// -
																			// VAR
										// newNodeInS1Graph.getAttribute(attrOfSameTypeInLhsOfR1.getType()).setValue(attrOfSameTypeInLhsOfR1.getValue());
										// //wrong code
										henshinFactory.createAttribute(newNodeInS1Graph,
												attrOfSameTypeInLhsOfR1.getType(), attrOfSameTypeInLhsOfR1.getValue());

									// handelt es sich bei dem R1Attr um einen
									// change?
									// wenn die erste Regel das Attr löscht, so
									// handelt es sich auch um einen Attr
									// Change!
									Attribute attrInRhsOfR1 = el1InRhsOfR1
											.getAttribute(attrOfSameTypeInLhsOfR1.getType());
									if (attrInRhsOfR1 == null
											|| !attrOfSameTypeInLhsOfR1.getValue().equals(attrInRhsOfR1.getValue())) {
										changeAttrIsUsed = true;
									}
									// TODO: für VAR-VAR muss noch das Attribute
									// im S1-Graph erstellt werden.
									if (!attrOfR2IsConstant && !attrOfSameTypeInLhsOfR1IsConstant) {
										String valueForAttr = attrOfSameTypeInLhsOfR1.getValue() + "_"
												+ attrOfSameTypeInLhsOfR1.getValue();
										henshinFactory.createAttribute(newNodeInS1Graph,
												attrOfSameTypeInLhsOfR1.getType(), valueForAttr);
									}
								}

							} else {
								// Attribute des entsprechenden EAttribute Typs
								// war nur in R2 vorhanden!
								// -> alles gut
								// wenn das Attribute in R2 eine Konstante ist
								// muss ein entsprechender Wert im S-Graph bzw.
								// overlap gesetzt werden!
								if (attrOfR2IsConstant)
									// newNodeInS1Graph.getAttribute(attrOfR2.getType()).setValue(attrOfR2.getValue());
									// //wrong code
									henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
											attrOfR2.getValue());
								// TODO: wenn für das EAttribute nur eine
								// VARiable in R2 vorliegt diese auch in den
								// S1-Graph aufnehmen?
							}

						}
					}

					if (!differingConstants) {
						// TODO: bis hierher sind alle Attribute von R2
						// behandelt worden.
						// Was fehlt ist die Behandlung der Attribute die nur in
						// R1 vorkommen.
						// Wenn es sich dabei um eine Konstante handelt, so
						// sollte diese auch im S-Graph bzw. overlap gesetzt
						// werden!
						for (Attribute attrOfR1Node : ((Node) el1).getAttributes()) {
							Attribute attributeInR2Node = nodeInLhsOfR2.getAttribute(attrOfR1Node.getType());
							if (attributeInR2Node == null) { // Attribute nur in
																// R1 Knoten
																// vorhanden.
								if (isAttrValueAConstant(attrOfR1Node, rule1)) {
									// newNodeInS1Graph.getAttribute(attrOfR1Node.getType()).setValue(attrOfR1Node.getValue());
									// //wrong code
									henshinFactory.createAttribute(newNodeInS1Graph, attrOfR1Node.getType(),
											attrOfR1Node.getValue());
								}
								// TODO: wenn für das EAttribute nur eine
								// VARiable in R1 vorliegt diese auch in den
								// S1-Graph aufnehmen?
							}
						}
					}

					if (!differingConstants && changeAttrIsUsed)
						result.add(S1span);
				}
			}
		}

		return result;
	}

	/*
	 * Was ist schlimmer: Wenn eine Konstante nicht als solche erkannt wird?
	 * Oder wenn eine Variable als Konstante eingeordnet wird? Generell muss es
	 * doch ähnliche Prüfungen bzw. Behanldungen auch bereits im Kern bzw.
	 * Interpreter von Henshin geben. siehe auch
	 * "HenshinValidator.validateAttributeCondition_conditionAllParametersAreDeclared"
	 * für eine entsprechende statische Prüfung!
	 */
	private boolean isAttrValueAConstant(Attribute attr, Rule rule) {
		/*
		 * Vorgehen um AttrValue als Konstante zu identifizieren. - Konstante
		 * geht nur bei entsprechendem Datentyp - Bei EString sollte es
		 * Anführungszeichen haben und davon abgesehen parsable sein. - bei
		 * numerischen Datentypen solte es entsprechend parsable sein.
		 * 
		 */
		if (isPrimitiveDataType(attr.getType())) {
			if (attributeIsParsable(attr)) {
				return true;
			}
		}

		// TODO: hier kann/sollte noch geprüft werden, ob es auch einen
		// passenden Regelparameter gibt.
		// EList<Parameter> parameters = rule.getParameters();
		// for(Parameter parameter : parameters){
		// parameter.get
		// }
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
				// TODO: additional "deletion edge check" due to some unresolved
				// Bug. Edges are loaded with different
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
					if (edge.getTarget() == targetNodeRhs) { // TODO: based on
																// tests this
																// seems to be
																// dead code.
																// Why? Tests
																// missing or
																// superfluous
																// code?
						// check same name of URI:
						URI uriOfPotentialAssociatedEdgeType = EcoreUtil.getURI(edge.getType());
						if (uriOfDeletionEdgeType.toString().equals(uriOfPotentialAssociatedEdgeType.toString()))
							isHoweverAPreserveEdge = true;
					}
				}
				// try to resolve problems when "FeatureModelPackage.eINSTANCE"
				// wasnt loaded!
				// allOutgoing.get(0).getType().eCrossReferences()
				// getEGenericType().equals(allOutgoing.get(1).getType());
				// EList<Edge> outgoingWithType =
				// sourceNodeRhs.getOutgoing(deletionEdge.getType());
				// URI outgoing0_uri =
				// EcoreUtil.getURI(allOutgoing.get(0).getType());outgoing0_uri.toString()
				// URI outgoing1_uri =
				// EcoreUtil.getURI(allOutgoing.get(1).getType());
				// URI deleteEdge_uri =
				// EcoreUtil.getURI(deletionEdge.getType());
				// System.out.println("HALT");
				if (!isHoweverAPreserveEdge)
					atomicDeletionEdges.add(deletionEdge);
			}
		}
		return atomicDeletionEdges;
	}

	private boolean nodeHasAttributeWithDifferingConstantValue(Node el1, EAttribute typeOfComparedAttribute,
			String valueOfComparedAttribute) {
		Attribute attribute = el1.getAttribute(typeOfComparedAttribute);
		if (attribute != null) {
			if (attributeIsParsable(attribute)) {
				// TODO: muss hier gegenebenfalls eien Anpassung vorgenommen
				// werden für variablen (diese sind nciht parsable, können aber
				// zum Konflikt führen!)
				if (!attribute.getValue().equals(valueOfComparedAttribute))
					return true;
			}
		}
		return false;
	}

	private boolean attributeIsParsable(Attribute attrOfR2) {
		// boolean isParsable = true;
		EAttribute type = attrOfR2.getType();
		EDataType eAttributeType = type.getEAttributeType();
		// eAttributeType.getName()
		if (attrOfR2.getType().toString() == "EString") {
			// TODO:
			// 1. check for quotes ("")
			// 2. try to parse the value!
			String value = attrOfR2.getValue();
			if (value.startsWith("\"") && value.endsWith("\"")) {
				String[] split = value.split("\"");
				if (split.length == 2)
					return true;
			}
			return false;
		}
		if (attrOfR2.getType().toString() == "EChar") {
			// TODO:
			// 1. maybe check for quotes ("")
			// 2. try to parse the value!
		}
		if (eAttributeType.getName() == "EInt") {
			try {
				Integer.parseInt(attrOfR2.getValue());
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		if (attrOfR2.getType().toString() == "EDouble") {
			try {
				Double.parseDouble(attrOfR2.getValue());
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		if (attrOfR2.getType().toString() == "ELong") {
			try {
				Long.parseLong(attrOfR2.getValue());
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		if (attrOfR2.getType().toString() == "EFloat") {
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
		if (eAttributeType != null) {
			System.err.println("eAttributeType.getName() " + eAttributeType.getName());
			// TODO: muss noch vervollständigt werden um (String, Char, Int,
			// Double, Long) zu identifizieren.
			if (eAttributeType.getName().equals("EInt"))
				return true;
			if (eAttributeType.getName().equals("EDouble"))
				return true;
			if (eAttributeType.getName().equals("ELong"))
				return true;
			if (eAttributeType.getName().equals("EString"))
				return true;
			if (eAttributeType.getName().equals("EChar"))
				return true;
		}
		return false;
	}

	private Node addNodeToGraph(Node nodeInRule1, Node nodeInRule2, Graph S1, Set<Mapping> rule1Mappings,
			Set<Mapping> rule2Mappings) {
		// to support inheritance the subtype of both nodes must be considered
		EClass subNodeType = identifySubNodeType(nodeInRule1, nodeInRule2); // TODO:
																			// might
																			// return
																			// null
																			// -
																			// Handle
																			// this!
		Node commonNode = henshinFactory.createNode(S1, subNodeType,
				nodeInRule1.getName() + "_" + nodeInRule2.getName());

		// TODO: beim Erstellen des Knoten auch ggf. die notwendigen Attribute
		// mit erstellen!
		// ?? Alle aus beiden Knoten, oder nur die übereinstimmenden?
		// ACHTUNG! hier müssen die vier Fälle aus KONST und VAR in Einklang
		// gebracht werden!

		rule1Mappings.add(henshinFactory.createMapping(commonNode, nodeInRule1));
		rule2Mappings.add(henshinFactory.createMapping(commonNode, nodeInRule2));
		return commonNode;
	}

	/**
	 * identify the type of the both nodes which is the subtype of the other
	 * node.
	 * 
	 * @param node1
	 * @param node2
	 * @return returns the subnode type if one of both is, otherwise null.
	 */
	private EClass identifySubNodeType(Node node1, Node node2) {
		if (node1.getType().equals(node2.getType()))
			return node1.getType();
		if (node1.getType().getEAllSuperTypes().contains(node2.getType()))
			return node1.getType();
		if (node2.getType().getEAllSuperTypes().contains(node1.getType()))
			return node2.getType();
		return null;
	}

	public void computeMinimalConflictReasons(Rule rule1, Rule rule2, Span s1,
			Set<MinimalConflictReason> minimalConflictReasons) {
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
		// TODO: wofür wird G benötigt? Vermutlich nur als Ziel der matches,
		// oder?
		// Oder ist das nicht normalerweise das minimale Modell?
		boolean isMatchM1 = findDanglingEdgesByLHSOfRule1(rule1, pushoutResult.getMappingsOfRule1()).isEmpty(); // TODO:
																												// über
																												// den
		// jeweiligen match
		// sollte doch die
		// Regel auch
		// "erreichbar"
		// sein. Regel als
		// Parameter daher
		// überflüssig.
		// ÜBERFLÜSSIG nach ERKENNTNIS! boolean isMatchM2 =
		// findDanglingEdges(rule2,
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
	 * Grundidee: die dangling edge hat als einen ihrer beiden Knoten definitiv
	 * einen Knoten aus dem Graph S1 des Spans! Aus dem Papier: "A naïve
	 * implementation of this function may enumerate all adjacent edges in L1
	 * OHNE S1 of e's context node in
	 * S1" Optimierung aus dem Papier: nur "löschende Kanten" von L_1
	 * berücksichtigen, da der Knoten auch bereits löschend war. (andernfalls
	 * wäre es ja nicht zur dangling edge gekommen.) Sollte es keine fixing
	 * edges geben wird eine leere Menge zurückgegeben. Ansonsten kann es
	 * antürlich mehrere fixing edges geben. Diese werden ALLE zurückgegeben!
	 * 
	 */
	// TODO: höchstwahrscheinlich werden noch als zusätzliche Übergabeparameter
	// die Mappings m_1 und m_2 benötigt
	// --> Algo anpassen! (ansonsten kann der zugehörige knoten zur dangling
	// edge nicht eindeutig in S1 bestimmt
	// werden.)
	// TODO: die zweite Optimierung haeb ich noch nicht verstanden!
	// TODO: mwenn (mit Daniel?) geklärt ist, dass die matches notwendig sind,
	// dann könnte man überlegen die beiden
	// Listen von mapping edges
	// und zusätzlich auch noch den Span s1 durch das "pushoutResult" zu
	// ersetzen, da dieses alle drei kennen
	// könnte(sollte?)
	// Alternativ wird das hinfällig wenn eine(/mehrere) zentrale Instanz(en)
	// die MAppings verwaltet.(Stichwort
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
		HashMap<Node, Node> s1ToRule1 = new HashMap<Node, Node>();
		for (Mapping mapping : s1.mappingsInRule1) {
			s1ToRule1.put(mapping.getOrigin(), mapping.getImage());
			rule1ToS1.put(mapping.getImage(), mapping.getOrigin());
		}

		// Suitable edges for this purpose are all adjacent edges in L1\S1 of
		// e's adjacent node in S1

		List<Edge> fixingEdges = new LinkedList<Edge>();
		Node poDanglingSource = poDangling.getSource();
		Node poDanglingTarget = poDangling.getTarget();

		// VORSICHT! vermutlich NPE!!!
		// Lösung: zweistufiges vorgehen: erst Mapping holen und nur wenn dieses
		// != null ist darauf zugreifen!
		Node r1DanglingSource = overlapToRule1.get(poDanglingSource);
		Node r1DanglingTarget = overlapToRule1.get(poDanglingTarget);
		Node s1DanglingSource = rule1ToS1.get(r1DanglingSource);
		Node s1DanglingTarget = rule1ToS1.get(r1DanglingTarget);

		if (s1DanglingSource == null && s1DanglingTarget == null) {
			throw new RuntimeException("By definition of the pushout, it cannot be the case that both adjacent nodges "
					+ "of a dangling edge are in S1!");
		}

		// System.out.println("Current graph: "+s1.getGraph().getNodes() + " "
		// +s1.getGraph().getEdges());

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

		// System.out.println("found "+fixingEdges.size()+ " fixing edges:
		// "+fixingEdges);
		return fixingEdges;
	}

	// TODO: bisher nicht weiter spezifiziert!
	public Set<Span> enumerateExtensions(Span s1, List<Edge> fixingEdges) {
		Set<Span> extensions = new HashSet<Span>(); // LinkedList<Span>();
		// Für jede Kante in fixingEdges wird ein neuer Span erzeugt und dieser
		// um die jeweilige Kante vergrößert.
		for (Edge fixingEdge : fixingEdges) {
			// Dabei müssen auch entsprechend neue Mappings erzeugt werden!
			// TODO: die Kopie für dne neuen Span muss zuerst erstellt werden
			// und die neuen Knotne und KAnten in der
			// Kopie erstellt werden,
			// sowie die neuen Mappings der Kopie hinzugefügt werden!
			Span extSpan = new Span(s1);
			SpanMappings maps = new SpanMappings(extSpan);

			Node fixingSource = fixingEdge.getSource();
			Node fixingTarget = fixingEdge.getTarget();
			if (maps.rule1ToS1.get(fixingSource) != null && maps.rule1ToS1.get(fixingTarget) != null)
				throw new RuntimeException("Fixing edge is already present in S1!");

			// TODO: prüfen, dass die Art des erstellens einer Kopie korrekt
			// ist.
			// ToDo: (/Fehler!) zur Erweiterung des Span um eine Kante der Regel
			// 1 kann es mehrere passende Kanten der
			// Regel 2 geben.
			// -> eine weitere Schleife ist notwendig!
			Node extNode = null;
			Node s1Existing = null;
			Node s1Source = null;
			Node s1Target = null;

			// Mapping mappingOfsourceNodeOfFixingEdgeInRule1 = newSpan
			// .getMappingFromGraphToRule1(fixingSource);

			// wenn NULL - erstellen von Knoten und Kante in graph, und mapping
			if (maps.rule1ToS1.get(fixingSource) == null) { // source ist
															// baumelnd!
				// Knoten in graph von Span erstellen
				extNode = henshinFactory.createNode(extSpan.getGraph(), fixingSource.getType(),
						fixingSource.getName() + "_");
				s1Source = extNode;
				// TODO: Mapping in den Span hinzufügen!
				Mapping newSourceNodeMapping = henshinFactory.createMapping(extNode, fixingSource);
				extSpan.mappingsInRule1.add(newSourceNodeMapping);
				s1Target = maps.rule1ToS1.get(fixingTarget);
				s1Existing = s1Target;
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
	// Funktionalität gibt es sehr wahrscheinlich schon in Henshin. (NEIN! -
	// hier wird mit mappings anstelle von matches
	// gearbeitet!)
	// Spezifikation der MEthode: Gibt die Menge der Kanten aus der Regel
	// zurück, die beim anwenden auf den overlapGraph
	// zu einer dangling edge führen würden!
	public List<Edge> findDanglingEdgesByLHSOfRule1(Rule rule, List<Mapping> embedding) {
		HashMap<Node, Node> l1ToOverlap = new HashMap<Node, Node>();
		HashMap<Node, Node> overlapToL1 = new HashMap<Node, Node>();
		for (Mapping mapping : embedding) { // Hier kommt es zu Problemen, wenn
											// durch die Mappings zwei Knoten
											// aus einer Regel einem Knoten im
											// Graph zugeordnet sind.
			// eigentlich dürfte das nicht passieren, da wir injektives matching
			// erlauben, aber unsicher ist es dadurch im Fehlerfall dennoch!
			l1ToOverlap.put(mapping.getOrigin(), mapping.getImage());
			overlapToL1.put(mapping.getImage(), mapping.getOrigin());
		}

		EList<Node> l1DeletingNodes = rule.getActionNodes(new Action(Action.Type.DELETE));
		List<Edge> danglingEdges = new LinkedList<Edge>();
		// für jeden gelöschten Knoten prüfen, dass auch all seine Kanten
		// gelöscht werden.
		for (Node l1Deleting : l1DeletingNodes) {
			Node poDeleting = l1ToOverlap.get(l1Deleting); // (18.04.2017) durch
															// "get()" kann null
															// zurückgegeben
															// werden und es
															// kommt dann im
															// Anschluss zur
															// NPE!

			EList<Edge> poDeletingsEdges = poDeleting.getAllEdges();
			for (Edge poDeletingsEdge : poDeletingsEdges) {
				Node poDelSource = poDeletingsEdge.getSource();
				Node l1DelSource = overlapToL1.get(poDelSource);
				if (l1DelSource == null) {
					danglingEdges.add(poDeletingsEdge);
					continue;
				}

				Node poDelTarget = poDeletingsEdge.getTarget();
				Node l1DelTarget = overlapToL1.get(poDelTarget);
				if (l1DelTarget == null) {
					danglingEdges.add(poDeletingsEdge);
				}
			}
		}

		// System.out.println(embedding.get(0).getImage().getGraph().getNodes().size());
		// System.out.println("found "+danglingEdges.size()+ " dangling edges:
		// "+danglingEdges);
		return danglingEdges;
	}

	// Spezifikation der Methode: Gibt die Menge der Kanten aus der Regel
	// zurück, die beim Anwenden auf den overlapGraph
	// zu einer dangling edge führen würden!
	public List<Edge> findDanglingEdgesByLHSOfRule2(Set<Mapping> mappingInRule1, Rule rule,
			Set<Mapping> mappingInRule2) {

		HashMap<Node, Node> l1ToOverlap = new HashMap<Node, Node>();
		HashMap<Node, Node> overlapToL1 = new HashMap<Node, Node>();
		for (Mapping mapping : mappingInRule1) { // Hier kommt es zu Problemen,
													// wenn durch die Mappings
													// zwei Knoten aus einer
													// Regel einem Knoten im
													// Graph zugeordnet sind.
			// eigentlich dürfte das nicht passieren, da wir injektives matching
			// vorsehen, aber unsicher ist es dadurch im Fehlerfall dennoch!
			overlapToL1.put(mapping.getOrigin(), mapping.getImage());
			l1ToOverlap.put(mapping.getImage(), mapping.getOrigin());
		}

		HashMap<Node, Node> l2ToOverlap = new HashMap<Node, Node>();
		HashMap<Node, Node> overlapToL2 = new HashMap<Node, Node>();
		for (Mapping mapping : mappingInRule2) { // Hier kommt es zu Problemen,
													// wenn durch die Mappings
													// zwei Knoten aus einer
													// Regel einem Knoten im
													// Graph zugeordnet sind.
			// eigentlich dürfte das nicht passieren, da wir injektives matching
			// vorsehen, aber unsicher ist es dadurch im Fehlerfall dennoch!
			overlapToL2.put(mapping.getOrigin(), mapping.getImage());
			l2ToOverlap.put(mapping.getImage(), mapping.getOrigin());
		}

		EList<Node> l2DeletingNodes = rule.getActionNodes(new Action(Action.Type.DELETE));
		List<Edge> danglingEdges = new LinkedList<Edge>();
		// für jeden gelöschten Knoten prüfen, dass auch all seine Kanten
		// gelöscht werden.
		for (Node l2Deleting : l2DeletingNodes) {
			Node nodeInOverlapToBeDeletedByRule2 = l2ToOverlap.get(l2Deleting); // (18.04.2017)
																				// durch
																				// "get()"
																				// kann
																				// null
																				// zurückgegeben
																				// werden
																				// und
																				// es
																				// kommt
																				// dann
																				// im
																				// Anschluss
																				// zur
																				// NPE!
			if (nodeInOverlapToBeDeletedByRule2 != null) {
				EList<Edge> incomingEdgesInOverlapToCheck = nodeInOverlapToBeDeletedByRule2.getIncoming();// getAllEdges();
				for (Edge potentialDanglingEdgeInOverlap : incomingEdgesInOverlapToCheck) {
					// Da der Knoten durch die zweite Regel gelöscht wird müssen
					// auch all seine Kanten
					// mit denen er im Overlap verbunden ist durch die zweite
					// REgel gelöscht werden
					Node sourceNodeOfPotentialDanglingEdgeInOverlap = potentialDanglingEdgeInOverlap.getSource();
					Node sourceNodeOfPotentialDanglingEdgeInRule2 = overlapToL2
							.get(sourceNodeOfPotentialDanglingEdgeInOverlap); // könnte
																				// null
																				// zurückgeben!
					if (sourceNodeOfPotentialDanglingEdgeInRule2 != null) {
						Edge potentialDanglingEdgeInRule2 = sourceNodeOfPotentialDanglingEdgeInRule2
								.getOutgoing(potentialDanglingEdgeInOverlap.getType(), l2Deleting);
						if (potentialDanglingEdgeInRule2 == null)
							danglingEdges.add(potentialDanglingEdgeInOverlap);
					} else {
						danglingEdges.add(potentialDanglingEdgeInOverlap);
					}
				}
				EList<Edge> outgoingEdgesInOverlapToCheck = nodeInOverlapToBeDeletedByRule2.getOutgoing();
				for (Edge potentialDanglingEdgeInOverlap : outgoingEdgesInOverlapToCheck) {
					// Da der Knoten durch die zweite Regel gelöscht wird müssen
					// auch all seine Kanten
					// mit denen er im Overlap verbunden ist durch die zweite
					// REgel gelöscht werden
					Node targetNodeOfPotentialDanglingEdgeInOverlap = potentialDanglingEdgeInOverlap.getTarget();
					Node targetNodeOfPotentialDanglingEdgeInRule2 = overlapToL2
							.get(targetNodeOfPotentialDanglingEdgeInOverlap); // könnte
																				// null
																				// zurückgeben!
					if (targetNodeOfPotentialDanglingEdgeInRule2 != null) {
						Edge potentialDanglingEdgeInRule2 = targetNodeOfPotentialDanglingEdgeInRule2
								.getIncoming(potentialDanglingEdgeInOverlap.getType(), l2Deleting);
						if (potentialDanglingEdgeInRule2 == null)
							danglingEdges.add(potentialDanglingEdgeInOverlap);
					} else {
						danglingEdges.add(potentialDanglingEdgeInOverlap);
					}
				}
				// TODO: kannes sein, dass hier die asugehenden Kanten der
				// ersten REgel vernachlässigt werden? Auch diese dürfen nicht
				// hängend bleiben?
				// Sobald es durch Regel1 noch weitere ausgehende Kanten gibt,
				// die nicht durch REgel2 abgedeckt sind kommt es zu eienr
				// dangling edge und somit invaliden initialReasons
				// Allerdings din ddie KAnten dabei ausgenommen, die bereits
				// durch den overlap abgedeckt waren!
				// Im Umkehrschluss heißt das, dass alle dem Knoten anhängen
				// Kanten in Regel1 ausgenommen sind, die bereits durch den
				// Graph abgedeckt sind.
				Node associatedNodeInRule1 = overlapToL1.get(nodeInOverlapToBeDeletedByRule2);
				// alle Kanten prüfen, ob diese bereits durch den Graph
				// abgedeckt sind
				// oder
				EList<Edge> allIncomingEdgesInRule1ConnectedToDeletedNodeByRule2 = associatedNodeInRule1.getIncoming();
				for (Edge incomingEdgeInRule1 : allIncomingEdgesInRule1ConnectedToDeletedNodeByRule2) {
					// TODO: Wenn eine Kante vom gleichen Typ in gleicher
					// Richtung an diesem Knoten auch bereits von Regel2
					// gelöscht wird, so ist diese als potentielle dangling-edge
					// ausgenommen!
					EList<Edge> incomingEdgesInRule2OfType = l2Deleting.getIncoming(incomingEdgeInRule1.getType());
					boolean referenceDeletionByRule2 = false;
					for (Edge incomingEdgeInRule2OfType : incomingEdgesInRule2OfType) {
						if (incomingEdgeInRule2OfType.getAction().getType() == Action.Type.DELETE)
							referenceDeletionByRule2 = true;
					}
					if (!referenceDeletionByRule2) {
						Node sourceOfIncomingInRule1 = incomingEdgeInRule1.getSource();
						Node sourceNodeInOverlap = l1ToOverlap.get(sourceOfIncomingInRule1);
						if (sourceNodeInOverlap == null) {
							// wenn es den verbundenen Knoten der Regel1 nicht
							// im Overlap gibt, dann kann die zweite Regel nicht
							// TODO: überprüfen wie es sich verhält, wenn die
							// zweite REgel einfach das größere löschende Muster
							// hat und das löschende Muster der ersten Regel nur
							// ein Teil dessen ist.
							// Aber dann würde vermutlich die Anwendung von
							// Regel1 zu dangling Kanten der Regel 2 führen. Wo
							// ist das abgedeckt?
							danglingEdges.add(incomingEdgeInRule1);
						}
					}
				}
				EList<Edge> allOutgoingEdgesInRule1ConnectedToDeletedNodeByRule2 = associatedNodeInRule1.getOutgoing();
				for (Edge outgoingEdgeInRule1 : allOutgoingEdgesInRule1ConnectedToDeletedNodeByRule2) {
					// TODO: Wenn eine Kante vom gleichen Typ in gleicher
					// Richtung an diesem Knoten auch bereits von Regel2
					// gelöscht wird, so ist diese als potentielle dangling-edge
					// ausgenommen!
					EList<Edge> outgoingEdgesInRule2OfType = l2Deleting.getOutgoing(outgoingEdgeInRule1.getType());
					boolean referenceDeletionByRule2 = false;
					for (Edge outgoingEdgeInRule2OfType : outgoingEdgesInRule2OfType) {
						if (outgoingEdgeInRule2OfType.getAction().getType() == Action.Type.DELETE)
							referenceDeletionByRule2 = true;
					}
					if (!referenceDeletionByRule2) {
						Node targetOfOutgoingInRule1 = outgoingEdgeInRule1.getTarget();
						Node targetNodeInOverlap = l1ToOverlap.get(targetOfOutgoingInRule1);
						if (targetNodeInOverlap == null) {
							// wenn es den verbundenen Knoten der Regel1 nicht
							// im Overlap gibt, dann kann die zweite Regel nicht
							// TODO: überprüfen wie es sich verhält, wenn die
							// zweite REgel einfach das größere löschende Muster
							// hat und das löschende Muster der ersten Regel nur
							// ein Teil dessen ist.
							// Aber dann würde vermutlich die Anwendung von
							// Regel1 zu dangling Kanten der Regel 2 führen. Wo
							// ist das abgedeckt?
							danglingEdges.add(outgoingEdgeInRule1);
						}
					}
				}
			}
		}

		return danglingEdges;
	}

	private enum ConflictAtomKind {
		DELETE_NODE, DELETE_EDGE, CHANGE_ATTR
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
		// TODO
	}

	// TODO: extract to "ExceptionUtilities" class
	/**
	 * Checks to see if an object is null, and if so generates an
	 * IllegalArgumentException with a fitting message.
	 * 
	 * @param o
	 *            The object to check against null.
	 * @param name
	 *            The name of the object, used to format the exception message
	 *
	 * @throws IllegalArgumentException
	 *             if o is null.
	 */
	public static void checkNull(Object o, String name) throws IllegalArgumentException {
		if (null == o)
			throw new IllegalArgumentException(name + " must not be null");
	}

	public static void checkNull(Object o) throws IllegalArgumentException {
		checkNull(o, "object");
	}

	/*
	 * not supported: - multi rules - application conditions - inheritance? (-
	 * attribute conditions [NACs & PACs])
	 * 
	 * TODO: normalen CPA check zur Hilfe nehmen um zu erkennen was noch geprüft
	 * werden könnte.
	 */
	public boolean isRuleSupported(Rule rule) {
		if (rule.getMultiRules().size() > 0) {
			throw new RuntimeException("multi rules are not supported");
			// TODO: nochmal nachlesen wie Exception und return value ggf. doch
			// zu vereinbaren sind und ob das hier ggf. Sinn macht.
			// return false;
		}
		if (rule.getLhs().getNACs().size() > 0)
			throw new RuntimeException("negative application conditions (NAC) are not supported");
		// TODO: nochmal nachlesen wie Exception und return value ggf. doch zu
		// vereinbaren sind und ob das hier ggf. Sinn macht.
		// return false;
		if (rule.getLhs().getPACs().size() > 0)
			throw new RuntimeException("positive application conditions (PAC) are not supported");
		// TODO: nochmal nachlesen wie Exception und return value ggf. doch zu
		// vereinbaren sind und ob das hier ggf. Sinn macht.
		// return false;
		return true;
	}

	public Set<InitialReason> computeInitialReasons() {
		if (overallMinimalConflictReasons == null) {
			computeConflictAtoms();
		}
		return computeInitialReasons(overallMinimalConflictReasons);
	}

	public Set<InitialReason> computeInitialReasons(Set<MinimalConflictReason> minimalConflictReasons) {
		Set<InitialReason> result = new HashSet<InitialReason>();
		for (MinimalConflictReason mcr : minimalConflictReasons) {
			Set<MinimalConflictReason> remainingMCR = new HashSet<MinimalConflictReason>(minimalConflictReasons);
			remainingMCR.remove(mcr);

			result.addAll(computeInitialReasons(mcr, remainingMCR));

			InitialReason singletonIR = new InitialReason(mcr);
			result.add(singletonIR);
		}
		return result;
	}

	private Set<InitialReason> computeInitialReasons(InitialReason current,
			Set<MinimalConflictReason> remaining) {
		Set<InitialReason> result = new HashSet<InitialReason>();
		for (MinimalConflictReason mcr : remaining) {
			if (!haveCommonDeletionElement(current, mcr)) {
				InitialReason initialReason = joinToNewInitialReason(current,
						mcr);
				if (initialReason != null) {
					result.add(initialReason);
					Set<MinimalConflictReason> remainingMCR = new HashSet<MinimalConflictReason>(remaining);
					result.addAll(computeInitialReasons(initialReason, remainingMCR));
				}
			}
		}
		return result;
	}

	// wenn
	private boolean haveCommonDeletionElement(InitialReason current,
			MinimalConflictReason extensionCandidate) {
		Set<ModelElement> deletionElementsCur = current.getDeletionElementsInRule1();
		Set<ModelElement> deletionElementsCand = extensionCandidate.getDeletionElementsInRule1();
		return !Collections.disjoint(deletionElementsCur, deletionElementsCand);
	}

	/**
	 * 
	 * @param span1
	 * @param span2
	 * @return a new InitialConflictReason or <code>null</code> if this ist not
	 *         possible
	 */
	private InitialReason joinToNewInitialReason(InitialReason span1, InitialReason span2) {
		Map<Node, Node> s2ToS1 = getS2toS1Map(span1, span2);
		if (s2ToS1 == null)  // is null iff we cannot join them
			return null;
		
		// Copy G1 and its mappings to rules 1 and 2
		Copier g1ToCopy = new Copier();
		Graph graph1Copy = (Graph) g1ToCopy.copy(span1.getGraph());
		g1ToCopy.copyReferences(); // NOTWENDIG!!!

		Copier mappingS1Copier = new Copier();
		Collection<Mapping> mappingsS1R1copies = mappingS1Copier.copyAll(span1.getMappingsInRule1());
		mappingS1Copier.copyReferences();
		Collection<Mapping> mappingS1R2copies = mappingS1Copier.copyAll(span1.getMappingsInRule2());
		mappingS1Copier.copyReferences();
		for (Mapping mapping : mappingsS1R1copies) {
			Node newOrigin = (Node) g1ToCopy.get(mapping.getOrigin());
			mapping.setOrigin(newOrigin);
		}
		for (Mapping mapping : mappingS1R2copies) {
			Node newOrigin = (Node) g1ToCopy.get(mapping.getOrigin()); 
			mapping.setOrigin(newOrigin);
		}

		// Copy G1 and its mappings to rules 1 and 2
		Copier g2toCopy = new Copier();
		Graph graph2Copy = (Graph) g2toCopy.copy(span2.getGraph());
		g2toCopy.copyReferences(); // NOTWENDIG!!!

		// MAPPINGS of Graph2:
		Copier mappingsS2Copier = new Copier();
		Collection<Mapping> mappingsS2R1copies = mappingsS2Copier.copyAll(span2.getMappingsInRule1());
		mappingsS2Copier.copyReferences();
		Collection<Mapping> mappingS2R2copies = mappingsS2Copier.copyAll(span2.getMappingsInRule2());
		mappingsS2Copier.copyReferences();
		
		// DONE: alle Mappings ausgehend von Graph2 auf copyOfGraph2 anpassen
		for (Mapping mapping : mappingsS2R1copies) {
			Node newOrigin = (Node) g2toCopy.get(mapping.getOrigin()); 
			mapping.setOrigin(newOrigin);
		}
		for (Mapping mapping : mappingS2R2copies) {
			Node newOrigin = (Node) g2toCopy.get(mapping.getOrigin()); 
			mapping.setOrigin(newOrigin);
		}

		// Identify redundant nodes in G2's copy, and reroute
		// their adjacent edges to G1's copy
		List<Node> toDeleteInG2Copy = new LinkedList<Node>();
		for (Edge edgeG2 : span2.getGraph().getEdges()) {
			Edge edgeG2Copy = (Edge)g2toCopy.get(edgeG2);
			if (s2ToS1.containsKey(edgeG2.getSource())) {
				Node nodeInGraph1 = s2ToS1.get(edgeG2.getSource());
				Node newSourceG1Copy = (Node) g1ToCopy.get(nodeInGraph1);
				toDeleteInG2Copy.add(edgeG2Copy.getSource());
				edgeG2Copy.setSource(newSourceG1Copy);
			}
			if (s2ToS1.containsKey(edgeG2.getTarget())) {
				Node nodeInGraph1 = s2ToS1.get(edgeG2.getTarget());
				Node newTargetG1Copy = (Node)  g1ToCopy.get(nodeInGraph1);
				toDeleteInG2Copy.add(edgeG2Copy.getTarget());
				edgeG2Copy.setTarget(newTargetG1Copy);
			}
		}

		// Remove redundant nodes from G2's copy and their mappings into
		// rules 1 and 2
		removeRedundantNodes(graph2Copy, mappingsS2R1copies, mappingS2R2copies, toDeleteInG2Copy);
		
		// Add nodes, edges, and mappings to those of G1
		graph1Copy.getNodes().addAll(graph2Copy.getNodes());
		graph1Copy.getEdges().addAll(graph2Copy.getEdges());
		Set<Mapping> mappingsToR1 = new HashSet<Mapping>();
		mappingsToR1.addAll(mappingsS1R1copies);
		mappingsToR1.addAll(mappingsS2R1copies);
		Set<Mapping> mappingsToR2 = new HashSet<Mapping>();
		mappingsToR2.addAll(mappingS1R2copies);
		mappingsToR2.addAll(mappingS2R2copies);

		Set<MinimalConflictReason> originMCRs = new HashSet<MinimalConflictReason>();
		if (span1 instanceof MinimalConflictReason) {
			originMCRs.add((MinimalConflictReason) span1);
		} else {
			originMCRs.addAll(span1.getOriginMCRs());
		}
		if (span2 instanceof MinimalConflictReason) {
			originMCRs.add((MinimalConflictReason) span2);
		} else {
			originMCRs.addAll(span2.getOriginMCRs());
		}
		InitialReason newInitialReason = new InitialReason(mappingsToR1, graph1Copy,
				mappingsToR2, originMCRs);

		return newInitialReason;
	}

	private void removeRedundantNodes(Graph graph2Copy, Collection<Mapping> mappingsS2Rule1Copies,
			Collection<Mapping> mappingsS2Rule2Copies, List<Node> toDeleteInG2Copy) {
		List<Mapping> toDeleteMappingsToRule1 = new LinkedList<Mapping>();
		for (Mapping mapS2ToRule1 : mappingsS2Rule1Copies) {
			if (toDeleteInG2Copy.contains(mapS2ToRule1.getOrigin())) {
				toDeleteMappingsToRule1.add(mapS2ToRule1);
			}
		}
		mappingsS2Rule1Copies.removeAll(toDeleteMappingsToRule1);

		List<Mapping> mappingsInRule2ToRemove = new LinkedList<Mapping>();
		for (Mapping mappingOfSpan2InRule2 : mappingsS2Rule2Copies) {
			if (toDeleteInG2Copy.contains(mappingOfSpan2InRule2.getOrigin())) {
				mappingsInRule2ToRemove.add(mappingOfSpan2InRule2);
			}
		}
		mappingsS2Rule2Copies.removeAll(mappingsInRule2ToRemove);
		graph2Copy.getNodes().removeAll(toDeleteInG2Copy);
	}

	private Map<Node, Node> getS2toS1Map(InitialReason span1, InitialReason span2) {
		Map<Node, Node> s2ToS1 = new HashMap<Node, Node>();
		for (Node n1 : span1.getGraph().getNodes()) {
			for (Node n2 : span2.getGraph().getNodes()) {
				if (n1.getType() == n2.getType()) {
					boolean sameImageInRule1 = (span1.getMappingIntoRule1(n1).getImage() == span2
							.getMappingIntoRule1(n2).getImage());
					boolean sameImageInRule2 = (span1.getMappingIntoRule2(n1).getImage() == span2
							.getMappingIntoRule2(n2).getImage());
					if (sameImageInRule1 && sameImageInRule2) {
						s2ToS1.put(n2, n1);
					} else if (sameImageInRule1 ^ sameImageInRule2) {
						return null;
					}
				}
			}
		}
		return s2ToS1;
	}

	public Set<ConflictAtom> extractEdgeConflictAtoms(List<ConflictAtom> computedConflictAtoms) {
		Set<ConflictAtom> edgeConflictAtoms = new HashSet<ConflictAtom>();
		for (ConflictAtom ca : computedConflictAtoms) {
			if (ca.isDeleteEdgeConflictAtom())
				edgeConflictAtoms.add(ca);
		}
		return edgeConflictAtoms;
	}

	public Set<ConflictReason> computeConflictReasons(List<ConflictAtom> conflictAtoms,
			Set<InitialReason> initialReasons) {
		Set<ConflictReason> conflictReasonsDerivedFromInitialReason = new HashSet<ConflictReason>();
		Set<MinimalConflictReason> originMCRs = new HashSet<MinimalConflictReason>();
		for (InitialReason initialReason : initialReasons) {
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