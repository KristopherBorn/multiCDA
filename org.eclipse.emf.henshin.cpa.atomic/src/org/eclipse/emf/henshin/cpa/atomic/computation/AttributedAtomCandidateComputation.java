package org.eclipse.emf.henshin.cpa.atomic.computation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

public class AttributedAtomCandidateComputation extends AtomCandidateComputation {


	public AttributedAtomCandidateComputation(Rule rule1, Rule rule2) {
		super(rule1, rule2);
	}

	public List<Span> computeAtomCandidates() {
		List<Span> result = super.computeAtomCandidates();
		doAttributeTreatment(result);
		return result;
	}

	private void doAttributeTreatment(List<Span> result) {
		Set<Node> atomicChangeNodes = getAtomicChangeNodes();
		for (Node el1 : atomicChangeNodes) {
			addChangeUseAtomCandidates(result, el1);
		}
	}

	/**
	 * This method also adds the context nodes of change-use atom candidates,
	 * rather than just the delete-use atom candidates. Could be refactored to
	 * split these two computations.
	 */
	protected void addDeleteUseAtomCandidates(Rule rule1, Rule rule2, List<Span> result, ModelElement el1) {
		List<ModelElement> atomicUsageElements = new LinkedList<ModelElement>();
		if (el1 instanceof Node) {
			// nach sub- und super-typ überprüfen!
			// TODO: überprüfen, ob es Attribute mit abweichenden konstanten
			// Werten gibt!
			for (Node nodeInLhsOfR2 : rule2.getLhs().getNodes()) {
				boolean r1NodeIsSuperTypeOfR2Node = false;
				boolean r2NodeIsSuperTypeOfR1Node = false;
					r1NodeIsSuperTypeOfR2Node = nodeInLhsOfR2.getType().getESuperTypes()
							.contains(((Node) el1).getType());
					r2NodeIsSuperTypeOfR1Node = ((Node) el1).getType().getEAllSuperTypes()
							.contains(nodeInLhsOfR2.getType());
				
				boolean identicalType = nodeInLhsOfR2.getType().equals((((Node) el1).getType()));

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
												attrOfSameTypeInLhsOfR1.getType(), attrOfSameTypeInLhsOfR1.getValue());
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

	private void addChangeUseAtomCandidates(List<Span> result, Node el1) {

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

		// nach sub- und super-typ überprüfen!
		for (Node nodeInLhsOfR2 : rule2.getLhs().getNodes()) {
			boolean r1NodeIsSuperTypeOfR2Node = false;
			boolean r2NodeIsSuperTypeOfR1Node = false;
				r1NodeIsSuperTypeOfR2Node = nodeInLhsOfR2.getType().getEAllSuperTypes().contains(((Node) el1).getType());
				r2NodeIsSuperTypeOfR1Node = ((Node) el1).getType().getEAllSuperTypes()
						.contains(nodeInLhsOfR2.getType());
			boolean identicalType = nodeInLhsOfR2.getType().equals((((Node) el1).getType()));

			// TODO: hier schon den möglichen S-Graph mit dem passenden
			// Knoten erzeugen!
			// dabei den Typ des weiter unten in der Vererbungshierarchie
			// stehenden Knotens nutzen!
			// wird bereits in "addNodeToGraph" gemacht
			Graph S1 = henshinFactory.createGraph();
			Set<Mapping> rule1Mappings = new HashSet<Mapping>();
			Set<Mapping> rule2Mappings = new HashSet<Mapping>();
			Node newNodeInS1Graph = addNodeToGraph((Node) el1, (Node) nodeInLhsOfR2, S1, rule1Mappings, rule2Mappings);
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
							boolean attrOfSameTypeInLhsOfR1IsConstant = isAttrValueAConstant(attrOfSameTypeInLhsOfR1,
									rule1);
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
									henshinFactory.createAttribute(newNodeInS1Graph, attrOfSameTypeInLhsOfR1.getType(),
											attrOfSameTypeInLhsOfR1.getValue());

								// handelt es sich bei dem R1Attr um einen
								// change?
								// wenn die erste Regel das Attr löscht, so
								// handelt es sich auch um einen Attr
								// Change!
								Attribute attrInRhsOfR1 = el1InRhsOfR1.getAttribute(attrOfSameTypeInLhsOfR1.getType());
								if (attrInRhsOfR1 == null
										|| !attrOfSameTypeInLhsOfR1.getValue().equals(attrInRhsOfR1.getValue())) {
									changeAttrIsUsed = true;
								}
								// TODO: für VAR-VAR muss noch das Attribute
								// im S1-Graph erstellt werden.
								if (!attrOfR2IsConstant && !attrOfSameTypeInLhsOfR1IsConstant) {
									String valueForAttr = attrOfSameTypeInLhsOfR1.getValue() + "_"
											+ attrOfSameTypeInLhsOfR1.getValue();
									henshinFactory.createAttribute(newNodeInS1Graph, attrOfSameTypeInLhsOfR1.getType(),
											valueForAttr);
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

	private Set<Node> getAtomicChangeNodes() {
		Set<Node> atomicChangeNodes = new HashSet<Node>();

		// ATTRIBUTE deletion or change (also attribute change -
		// change-use-conflicts) - (sammeln preserve Knoten mit geänderten
		// Attributen von R1)
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
		return atomicChangeNodes;
	}
}
