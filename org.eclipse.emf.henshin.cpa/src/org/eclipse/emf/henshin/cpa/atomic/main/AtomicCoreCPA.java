package org.eclipse.emf.henshin.cpa.atomic.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
//import org.eclipse.emf.henshin.cpa.atomic.AtomicCoreCPA;
import org.eclipse.emf.henshin.cpa.atomic.main.AtomicCoreCPA.Span;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.EdgeImpl;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;

public class AtomicCoreCPA {

	// TODO: Felder für Candidates und MinimalReasons einführen - DONE
	List<Span> candidates;
	Set<Span> overallReasons;
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
	public Set<Span> getMinimalConflictReasons() {
		return overallReasons;
	}

	HenshinFactory henshinFactory = new HenshinFactoryImpl();

	public List<ConflictAtom> computeConflictAtoms(Rule rule1, Rule rule2) {
		
		checkNull(rule1, "rule1");
		checkNull(rule2, "rule2");

		List<ConflictAtom> result = new LinkedList<ConflictAtom>();
		candidates = computeAtomCandidates(rule1, rule2);
		overallReasons = new HashSet<>();
		for (Span candidate : candidates) {

			Set<Span> reasons = new HashSet<>();//
			computeMinimalConflictReasons(rule1, rule2, candidate, reasons);

//			if (rule1.getName().contains("Feature_FROM_Feature_children_TO_Feature_Fea")
//					&& rule2.getName().contains("factoring_1-3")) {
//				System.out.println("maybe here begins the mistake!");
//			}

			overallReasons.addAll(reasons); // to know the total amount after the analysis!
			if (!reasons.isEmpty()) {
				result.add(new ConflictAtom(candidate, reasons));
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
		List<ModelElement> atomicDeletionElements = new LinkedList<ModelElement>(rule1.getActionNodes(deleteAction));
		for (Edge deletionEdge : rule1.getActionEdges(deleteAction)) {
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
					if (edge.getTarget() == targetNodeRhs) {
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
					atomicDeletionElements.add(deletionEdge);
			}
		}
		for (ModelElement el1 : atomicDeletionElements) {
			List<ModelElement> atomicUsageElements = new LinkedList<ModelElement>();
			if (el1 instanceof Node) {
				atomicUsageElements.addAll(rule2.getLhs().getNodes(((Node) el1).getType()));
				// EList<Node> nodes = rule2.getLhs().getNodes(((Node) el1).getType());
			}
			if (el1 instanceof Edge) {
				atomicUsageElements.addAll(rule2.getLhs().getEdges(((Edge) el1).getType()));
			}
			for (ModelElement el2 : atomicUsageElements) {

				Graph S1 = henshinFactory.createGraph();

				List<Mapping> rule1Mappings = new LinkedList<Mapping>();
				List<Mapping> rule2Mappings = new LinkedList<Mapping>();

				if (el2 instanceof Node) {

					addNodeToGraph(el1, el2, S1, rule1Mappings, rule2Mappings);
					Span S1span = new Span(rule1Mappings, S1, rule2Mappings);
					result.add(S1span);

					// EClass type = ((Node) el2).getType();
					// EPackage singleEPackageOfDomainModel = type.getEPackage();
					// EFactory eFactoryInstance = singleEPackageOfDomainModel.getEFactoryInstance();
					//
					// EObject create = eFactoryInstance.create(type);
					// el2.eResolveProxy
				}
				if (el2 instanceof Edge) {

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
		return result;
	}

	private Node addNodeToGraph(ModelElement el1, ModelElement el2, Graph S1, List<Mapping> rule1Mappings,
			List<Mapping> rule2Mappings) {
		Node commonNode = henshinFactory.createNode(S1, ((Node) el2).getType(),
				((Node) el1).getName() + "_" + ((Node) el2).getName());

		rule1Mappings.add(henshinFactory.createMapping(commonNode, (Node) el1));
		rule2Mappings.add(henshinFactory.createMapping(commonNode, (Node) el2));
		return commonNode;
	}

	/**
	 * ??? TODO!
	 * 
	 * @param rule1 First rule
	 * @param rule2 Second rule
	 * @param s1 a single span between rule1 and rule2
	 * @param reasons Ergebnissmenge der MinimalConflictReasons. Wird durch die Methode um weitere MCR erweitert, sofern weitere existieren. 
	 */
	public void computeMinimalConflictReasons(Rule rule1, Rule rule2, Span s1, Set<Span> reasons) {
		checkNull(rule1, "rule1");
		checkNull(rule2, "rule2");
		if (isMinReason(rule1, rule2, s1)) {
			reasons.add(s1);
			return;
		}
		// is this part of the backtracking?
		Set<Span> extendedSpans = findExtensions(rule1, rule2, s1, reasons);
		for (Span extendedSpan : extendedSpans) {
			computeMinimalConflictReasons(rule1, rule2, extendedSpan, reasons);
		}
	}
	
	
	public Set<Span> computeConflictReason(Set<Span> minimalConflictReasons){
		Set<Span> conflictReason = new TreeSet<Span>();
		for(Span currentMCR : minimalConflictReasons){
			Set<Span> remainingMCR = new TreeSet<>(minimalConflictReasons);
			remainingMCR.remove(currentMCR);
			
			conflictReason.addAll(computeConflictReasons(currentMCR, remainingMCR));
		}
		return conflictReason;
	}
	
	private Set<Span> computeConflictReasons(Span currentMCR, Set<Span> combinationMCR){
		Set<Span> resultConflictReasons = new TreeSet<>(); 
		Set<Span> processedMCR = new TreeSet<>();
		for(Span combinedMCR : combinationMCR){
			processedMCR.add(combinedMCR);
				// finden der gemeinsamen Knoten 
				// (solche bei den LHS und RHS Knoten in beiden MCR übereinstimmen)
				Set<List<Node>> commonNodes = findCommonNodes(currentMCR, combinedMCR);
				// Erzeugen eines neuen ConflictReason aus den beiden MCR. 
				//	Dabei überlappen an gefundenen gemeinsamen Knoten.				
				Span conflictReason = joinToNewSpan(currentMCR, combinedMCR, commonNodes);
				resultConflictReasons.add(conflictReason);
				
				//weitere Kombinationen aus neuen ConflictReason mit restlichen MCR bilden:
				Set<Span> remainingMCR = new TreeSet<Span>(combinationMCR);
				remainingMCR.removeAll(processedMCR);
				resultConflictReasons.addAll(computeConflictReasons(conflictReason, remainingMCR));
		}
		return resultConflictReasons;
	}

	

	private Span joinToNewSpan(Span currentMCR, Span combinedMCR, Set<List<Node>> commonNodes) {
		// TODO Auto-generated method stub
		return null;
	}

	private Set<List<Node>> findCommonNodes(Span currentMCR, Span combinedMCR) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isMinReason(Rule rule1, Rule rule2, Span s1) {
		// TODO: ist hier der Zusammenhang zwischen C_1 und L_1, also c_1 klar?
		PushoutResult pushoutResult = constructPushout(rule1, rule2, s1);
		// TODO: wofür wird G benötigt? Vermutlich nur als Ziel der matches, oder?
		// Oder ist das nicht normalerweise das minimale Modell?
		boolean isMatchM1 = findDanglingEdges(rule1, pushoutResult.getMappingsOfRule1()).isEmpty(); // TODO: über den
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

	private Set<Span> findExtensions(Rule rule1, Rule rule2, Span s1, Set<Span> reasons) {
		if (rule1.getName().contains("Feature_FROM_Feature_children_TO_Feature_Fea")
				&& rule2.getName().contains("factoring_1-3")) {
			System.out.println("maybe here begins the mistake!");
		}

		PushoutResult pushoutResult = constructPushout(rule1, rule2, s1);
		List<Edge> danglingEdges = findDanglingEdges(rule1, pushoutResult.getMappingsOfRule1());
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
	// TODO: die zweite Optimierung haeb ich noch nciht verstanden!
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

			HashMap<Node, Node> rule1ToS1 = new HashMap<Node, Node>();
			HashMap<Node, Node> s1ToRule1 = new HashMap<Node, Node>();
			for (Mapping mapping : extSpan.getMappingsInRule1()) {
				s1ToRule1.put(mapping.getOrigin(), mapping.getImage());
				rule1ToS1.put(mapping.getImage(), mapping.getOrigin());
			}
			HashMap<Node, Node> rule2ToS1 = new HashMap<Node, Node>();
			HashMap<Node, Node> s1ToRule2 = new HashMap<Node, Node>();
			for (Mapping mapping : extSpan.getMappingsInRule2()) {
				s1ToRule2.put(mapping.getOrigin(), mapping.getImage());
				rule2ToS1.put(mapping.getImage(), mapping.getOrigin());
			}
			Node fixingSource = fixingEdge.getSource();
			Node fixingTarget = fixingEdge.getTarget();
			if (rule1ToS1.get(fixingSource) != null && rule1ToS1.get(fixingTarget) != null)
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
			if (rule1ToS1.get(fixingSource) == null) { // source ist baumelnd!
				// Knoten in graph von Span erstellen
				extNode = henshinFactory.createNode(extSpan.getGraph(), fixingSource.getType(),
						fixingSource.getName() + "_");
				s1Source = extNode;
				// TODO: Mapping in den Span hinzufügen!
				Mapping newSourceNodeMapping = henshinFactory.createMapping(extNode, fixingSource);
				extSpan.mappingsInRule1.add(newSourceNodeMapping);
				s1Target = rule1ToS1.get(fixingTarget);
				s1Existing = s1Target;
				System.err.println(" source war baumelnd!");
			} else
				// wenn NULL - erstellen von Knoten und Kante in graph, und mapping
				if (rule1ToS1.get(fixingTarget) == null) {
				// Knoten in graph von Span erstellen
				extNode = henshinFactory.createNode(extSpan.getGraph(), fixingTarget.getType(),
						fixingTarget.getName() + "_");
				s1Target = extNode;
				// TODO: Mapping in den Span hinzufügen!
				Mapping newSourceNodeMapping = henshinFactory.createMapping(extNode, fixingTarget);
				extSpan.mappingsInRule1.add(newSourceNodeMapping);
				s1Source = rule1ToS1.get(fixingSource);
				s1Existing = s1Source;
				System.err.println(" target war baumelnd!");
			} else {
				throw new RuntimeException("weder source noch target war baumelnd!");
			}
			// create corresponding edge of fixingEdge in graph of span.
			Edge s1Fixing = henshinFactory.createEdge(s1Source, s1Target, fixingEdge.getType());
			Node r2existing = s1ToRule2.get(s1Existing);
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
	public List<Edge> findDanglingEdges(Rule rule, List<Mapping> embedding) {
		HashMap<Node, Node> l1ToOverlap = new HashMap<Node, Node>();
		HashMap<Node, Node> overlapToL1 = new HashMap<Node, Node>();
		for (Mapping mapping : embedding) {
			l1ToOverlap.put(mapping.getOrigin(), mapping.getImage());
			overlapToL1.put(mapping.getImage(), mapping.getOrigin());
		}

		EList<Node> l1DeletingNodes = rule.getActionNodes(new Action(Action.Type.DELETE));
		List<Edge> danglingEdges = new LinkedList<Edge>();
		// für jeden gelöschten Knoten prüfen, dass auch all seine Kanten gelöscht werden.
		for (Node l1Deleting : l1DeletingNodes) {
			Node poDeleting = l1ToOverlap.get(l1Deleting);
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

	// TODO: noch ist unklar ob eine solche Datenstruktur notwendig ist,
	// oder es sich um Instanzen einer bereits bekannten Datenstruktur handelt.
	// Je nach Ergebnis löschen oder in eigenständiges class-file auslagern.
	public class ConflictAtom {
		Span span;

		/**
		 * @return the span
		 */
		public Span getSpan() {
			return span;
		}

		Set<Span> reasons;

		// in Algo Zeile 6 wird ein Atom mit den Parametern candidate und reasons initilisiert.
		// dennoch ist die Datenstruktur noch nicht klar!
		public ConflictAtom(Span candidate, Set<Span> reasons) {
			this.span = candidate;
			this.reasons = reasons;
		}
		
		// bisher werden die "reason's" nicht berücksichtigt, 
		// da auch nicht klar ist wozu diese eigentlich da sind um was es sich dabei handelt. 
		@Override
		public String toString(){
			return span.toString();
		}

		public String toShortString() {
			return span.toShortString();
		}

		/**
		 * @return the reasons
		 */
		public Set<Span> getReasons() {
			return reasons;
		}

	}

	public Span newSpan(Mapping nodeInRule1Mapping, Graph s1, Mapping nodeInRule2Mapping) {
		return new Span(nodeInRule1Mapping, s1, nodeInRule2Mapping);
	}

	public Span newSpan(List<Mapping> rule1Mappings, Graph s1, List<Mapping> rule2Mappings) {
		return new Span(rule1Mappings, s1, rule2Mappings);
	}

	// TODO: noch ist unklar ob eine solche Datenstruktur notwendig ist,
	// oder es sich um Instanzen einer bereits bekannten Datenstruktur handelt.
	// Je nach Ergebnis löschen oder in eigenständiges class-file auslagern.

	// Generell: Laut Definition 3 ist ein Span z.B. C1<-incl-S1-match->L2
	// d.h. drei Graphen verbunden über eine Inklusion und einen Match

	public class Span {

		List<Mapping> mappingsInRule1;
		List<Mapping> mappingsInRule2;

		Graph graph;

		private Copier copierForSpanAndMappings;

		// Scheint derzeit ncoh überflüssig zu sein!
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			// result = prime * result + getOuterType().hashCode();
			// result = prime * result + ((graph == null) ? 0 : graph.hashCode());
			// result = prime * result + ((mappingsInRule1 == null) ? 0 : mappingsInRule1.hashCode()); // no application
			// due to missing knwoledge on the hashCode of two lists with equal content but different order
			// result = prime * result + ((mappingsInRule2 == null) ? 0 : mappingsInRule2.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Span [mappingsInRule1=" + mappingsInRule1 + ", mappingsInRule2=" + mappingsInRule2 + ", graph: "
					+ graph.getNodes().size() + " Nodes, " + graph.getEdges().size() + " Edges" + "]";
		}
		
		public String toShortString() {
			StringBuilder sB = new StringBuilder();
			for(Edge edge : graph.getEdges()){
				sB.append(shortStringInfoOfGraphEdge(edge));
				sB.append(", ");
			}
			for(Node node : graph.getNodes()){
				sB.append(shortStringInfoOfGraphNode(node));
				sB.append(", ");
			}
			//remove last superfluous appendency
			if(sB.length()>0)
				sB.delete(sB.length()-2, sB.length());
			return "Span [" + sB.toString() + "]";
		}

		// e.g.  1,11->2,13:methods
		private Object shortStringInfoOfGraphEdge(Edge edge) {
			StringBuilder sB = new StringBuilder();
			Node src = edge.getSource();
			Node tgt = edge.getTarget();
			sB.append(getMappingIntoRule1(src).getImage().getName());
			sB.append(",");
			sB.append(getMappingIntoRule2(src).getImage().getName());
			sB.append("->");
			sB.append(getMappingIntoRule1(tgt).getImage().getName());
			sB.append(",");
			sB.append(getMappingIntoRule2(tgt).getImage().getName());
			sB.append(":");
			sB.append(edge.getType().getName());
			return sB.toString();
		}

		// e.g.: 2,3:Method
		private String shortStringInfoOfGraphNode(Node node) {
			StringBuilder sB = new StringBuilder();
			Mapping mappingIntoRule1 = getMappingIntoRule1(node);
			Mapping mappingIntoRule2 = getMappingIntoRule2(node);
			sB.append(mappingIntoRule1.getImage().getName());
			sB.append(",");
			sB.append(mappingIntoRule2.getImage().getName());
			sB.append(":");
			sB.append(node.getType().getName());
			return sB.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Span other = (Span) obj;
			// if (!getOuterType().equals(other.getOuterType())) //specific for inner class - might be irrelevant by
			// extraction to own class file!
			// return false;
			if (graph == null) { // should never happen!
				if (other.graph != null) // should never happen!
					return false;
			} else {
				// !graph.equals(other.graph)
				/*
				 * TODO: 1. vergleichen, dass die Anzahl der Elemente im Graph gleich sind 2. für jedes Element im Graph
				 * das jeweils passende im anderen Graph finden! 3. je Element prüfen, dass die Mappings das selbe Ziel
				 * haben. Vorteil hierbei ist, dass die Regeln die selben sind und osmit die ziele mit "==" verglichen
				 * werden können.
				 */
				// compare "size" of Graphs
				EList<Node> ownNodes = graph.getNodes();
				EList<Node> otherNodes = other.graph.getNodes();
				if (ownNodes.size() != otherNodes.size()) {
					return false;
				}
				EList<Edge> ownEdges = graph.getEdges();
				EList<Edge> otherEdges = other.getGraph().getEdges();
				if (ownEdges.size() != otherEdges.size()) {
					return false;
				}

				// build allocation between own graph elements and others graph elements.
				// all mappings in the first rule should be the same!
				for (Node nodeInOwnGraph : ownNodes) {
					// get mapping and node in rule1
					Mapping mappingOfNodeInRule1 = getMappingIntoRule1(nodeInOwnGraph);
					if (mappingOfNodeInRule1 == null)
						return false; // TODO: isnt that a bug instead of an unequal Span?
					// the nodes of the span should all have a mapping into both rules.
					// somehow the nodes in the graph of the span or the nodes in the mapping must bewrong.
					// it shouldnt be possible to change the graph and the mappings of aspan and everytime they are
					// created they should be checked to be consistent!
					Node associatedNodeInRule1 = mappingOfNodeInRule1.getImage();
					// get mapping in otherGraph
					Mapping mappingOfOtherNodeInRule1 = other.getMappingFromGraphToRule1(associatedNodeInRule1);
					if (mappingOfOtherNodeInRule1 == null)
						return false;
					Node nodeInOtherGraph = mappingOfOtherNodeInRule1.getOrigin();
					// check that both mappings in rule 2 have the same target
					Mapping mappingOfNodeInRule2 = getMappingIntoRule2(nodeInOwnGraph);
					if (getMappingIntoRule2(nodeInOwnGraph) == null)
						System.out.println("bla");

					// gibt NULL zurück, wenn es kein passendes mapping gibt!
					Node associatedNodeInRule2 = mappingOfNodeInRule2.getImage();
					Mapping mappingOfOtherNodeInRule2 = other.getMappingIntoRule2(nodeInOtherGraph);
					Node associatedNodeOfOtherGraphInRule2 = mappingOfOtherNodeInRule2.getImage();
					if (!(associatedNodeOfOtherGraphInRule2 == associatedNodeInRule2))
						return false;
				}
				return true;

				//

				// return false;
			}
			// if (mappingsInRule1 == null) {
			// if (other.mappingsInRule1 != null)
			// return false;
			// } else if (!mappingsInRule1.equals(other.mappingsInRule1))
			// return false;
			// if (mappingsInRule2 == null) {
			// if (other.mappingsInRule2 != null)
			// return false;
			// } else if (!mappingsInRule2.equals(other.mappingsInRule2))
			// return false;
			return false;
		}

		/**
		 * @return the mappingsInRule1
		 */
		public List<Mapping> getMappingsInRule1() {
			return mappingsInRule1;
		}

		/**
		 * @return the mappingsInRule2
		 */
		public List<Mapping> getMappingsInRule2() {
			return mappingsInRule2;
		}

		public Span(Mapping nodeInRule1Mapping, Graph s1, Mapping nodeInRule2Mapping) {
			//TODO: introduce a check, that all mappings for rule1 are targeting to a common rule
			//TODO: introduce a check, that all mappings for rule2 are targeting to a common rule
			//TODO: introduce a check that rule 1 != rule2
			//TODO: afterwards "pushout" tests have to be adapted!
			this.graph = s1;
			mappingsInRule1 = new LinkedList<Mapping>();
			mappingsInRule1.add(nodeInRule1Mapping);
			mappingsInRule2 = new LinkedList<Mapping>();
			mappingsInRule2.add(nodeInRule2Mapping);
		}

		public Mapping getMappingFromGraphToRule2(Node imageNode) {
			for (Mapping mappingInRule2 : mappingsInRule2) {
				if (mappingInRule2.getImage() == imageNode)
					return mappingInRule2;
			}
			return null;
		}

		public Mapping getMappingFromGraphToRule1(Node imageNode) {
			for (Mapping mappingInRule1 : mappingsInRule1) {
				if (mappingInRule1.getImage() == imageNode)
					return mappingInRule1;
			}
			return null;
		}

		public Span(List<Mapping> rule1Mappings, Graph s1, List<Mapping> rule2Mappings) {
			this.mappingsInRule1 = rule1Mappings;
			this.mappingsInRule2 = rule2Mappings;
			this.graph = s1;
		}

		public Span(Span s1) {
			// copy Graph and mappings!
			// Copier
			copierForSpanAndMappings = new Copier();
			// copy of graph
			Graph copiedGraph = (Graph) copierForSpanAndMappings.copy(s1.getGraph());
			copierForSpanAndMappings.copyReferences();
			this.graph = copiedGraph;

			// TODO: extract to method
			List<Mapping> mappingsInRule1 = new LinkedList<Mapping>();
			for (Mapping mapping : s1.getMappingsInRule1()) {
				Mapping copiedMapping = (Mapping) copierForSpanAndMappings.copy(mapping);
				copierForSpanAndMappings.copyReferences();
				mappingsInRule1.add(copiedMapping);
			}
			this.mappingsInRule1 = mappingsInRule1;

			List<Mapping> mappingsInRule2 = new LinkedList<Mapping>();
			for (Mapping mapping : s1.getMappingsInRule2()) {
				Mapping copiedMapping = (Mapping) copierForSpanAndMappings.copy(mapping);
				copierForSpanAndMappings.copyReferences();
				mappingsInRule2.add(copiedMapping);
			}
			this.mappingsInRule2 = mappingsInRule2;
		}
		

		public Span (Span extSpan, Node origin, Node image) {
			this(extSpan);
			Node transformedOrigin = (Node) copierForSpanAndMappings.get(origin);
			
			Mapping r2Mapping = henshinFactory.createMapping(transformedOrigin,
					image);
			mappingsInRule2.add(r2Mapping);
		}

		public Graph getGraph() {
			return graph;
		}

		// TODO use "getMappingWithImage(...)" method
		public Mapping getMappingIntoRule1(Node originNode) {
			for (Mapping mapping : mappingsInRule1) {
				if (mapping.getOrigin() == originNode)
					return mapping;
			}
			return null;
		}

		// TODO use "getMappingWithImage(...)" method
		public Mapping getMappingIntoRule2(Node originNode) {
			for (Mapping mapping : mappingsInRule2) {
				if (mapping.getOrigin() == originNode)
					return mapping;
			}
			return null;
		}

		private AtomicCoreCPA getOuterType() {
			return AtomicCoreCPA.this;
		}

		//TODO: prüfen, dass es zu jedem Knoten im Graph des Span zwei Mappings gibt, die dem Span-Knoten jeweils einen Knoten in der LHS der Regel1 und Regel2 zuordnet
		//		FRAGE: auch prüfen, dass die Kanten im Span auch in den beiden Regeln vorhanden sind?
		public boolean validate(Rule rule1, Rule rule2) {
			//missing or superfluous mappings or nodes in the graph of the span
			if(mappingsInRule1.size() != graph.getNodes().size() || mappingsInRule2.size() != graph.getNodes().size())
				return false;
			// check all nodes of the graph of the span for valid mappings in the rules
			for(Node node : graph.getNodes()){
				Mapping mappingIntoRule1 = getMappingIntoRule1(node);
				if(mappingIntoRule1.getImage() == null)
					return false;
				Node imageInRule1 = mappingIntoRule1.getImage();
				if(imageInRule1.eContainer() != rule1.getLhs())
					return false;
				if(imageInRule1.getType() !=  node.getType()) //TODO: fix this regarding inheritance!
					return false;
				Mapping mappingIntoRule2 = getMappingIntoRule2(node);
				if(mappingIntoRule2.getImage() == null)
					return false;
				Node imageInRule2 = mappingIntoRule2.getImage();
				if(imageInRule2.eContainer() != rule2.getLhs())
					return false;
				if(imageInRule2.getType() !=  node.getType()) //TODO: fix this regarding inheritance!
					return false;
				
			}
			// Edges of the graph could be checked additionally.
			// If this is done some tests should be set up as negative examples for such situations.
			return true;
		}

	}

	public PushoutResult newPushoutResult(Rule rule1, Span span, Rule rule2) {
		return new PushoutResult(rule1, span, rule2);
	}

	// TODO: noch ist unklar ob eine solche Datenstruktur notwendig ist,
	// oder es sich um Instanzen einer bereits bekannten Datenstruktur handelt.
	// Je nach Ergebnis löschen oder in eigenständiges class-file auslagern.

	// Generell: muss die matches m1 und m2 aus L1 und L2 enthalten und somit auch G.
	// daher kennt es oder referenziert es auch (indirekt?) die beiden Regeln
	public class PushoutResult {

		/**
		 * @return the mappingsOfRule1
		 */
		public List<Mapping> getMappingsOfRule1() {
			return toMappingList(mappingsOfRule1);
		}

		/**
		 * @return the mappingsOfRule2
		 */
		public List<Mapping> getMappingsOfRule2() {
			return toMappingList(mappingsOfRule1);
		}

		private List<Mapping> toMappingList(HashMap<Node, Node> map) {
			List<Mapping> result = new LinkedList<Mapping>();
			for (Node node : map.keySet()) {
				result.add(henshinFactory.createMapping(node, map.get(node)));
			}
			return result;
		}

		/**
		 * @return the resultGraph
		 */
		public Graph getResultGraph() {
			return resultGraph;
		}

		Graph resultGraph;

		private HashMap<Node, Node> mappingsOfRule1;
		private HashMap<Node, Node> mappingsOfRule2;

		public PushoutResult(Rule rule1, Span s1span, Rule rule2) {
			
//			TODO: prüfen, dass alle mappings in die beiden Regeln verweisen, bzw. keine der Regeln NULL ist. Sonst werfen einer Exception!
//			throw new IllegalStateException("blabla")
//			ggf. in "static" Methode (Span) s1span.isValid() auslagern die eine "IllegalStateException" wirft 
//			s1span.validate(rule1, rule2);
			checkNull(rule1);
			checkNull(s1span);
			checkNull(rule2);
			if(!s1span.validate(rule1, rule2))
				throw new IllegalArgumentException("Span is in invalide state.");			

//	        Predicate<Object> a = Objects::nonNull; // AHA Prädikate - keine Ahnung!
//			Objects::nonNull(rule1); // bringt nichts, nur boolscher Rückgabewert!
//			Objects.nonNull(rule2); // bringt nichts, nur boolscher Rückgabewert!
			
			Graph l1 = rule1.getLhs();
			mappingsOfRule1 = new HashMap<Node,Node>();
			Copier copierForRule1 = new Copier();
			Graph pushout = (Graph) copierForRule1.copy(l1);
			copierForRule1.copyReferences();
			for (Node node : l1.getNodes()) {
				Node copyResultNode = (Node) copierForRule1.get(node);
				mappingsOfRule1.put(node, copyResultNode);
			}
			Graph l2 = rule2.getLhs();
			mappingsOfRule2 = new HashMap<Node,Node>();
			Copier copierForRule2 = new Copier();
			Graph l2copy = (Graph) copierForRule2.copy(l2);
			copierForRule2.copyReferences();
			for (Node node : l2.getNodes()) {
				Node copyResultNode = (Node) copierForRule2.get(node);
				mappingsOfRule2.put(node, copyResultNode);
			}

			Graph s1 = s1span.getGraph();
			// replace common nodes in copyOfRule2 with the one in copyOfRule1
			for (Node node : s1.getNodes()) {
				// retarget associated edges
				// get associated node and mapping in both copies
				if (s1span.getMappingIntoRule2(node) == null) { //TODO: remove!
					System.out.println("bla");
				}
				
				Node l1node  = s1span.getMappingIntoRule1(node).getImage();
				Node l2node = s1span.getMappingIntoRule2(node).getImage();
				
				
				if (l1node == null || l2node == null) {
					System.out.println("Did not find a L1 or L2 counterpart for one of the nodes in S1!");
//					TODO: Exception werfen! 
				} else {
					Node mergedNode = mappingsOfRule1.get(l1node);
					Node discardNode =  mappingsOfRule2.get(l2node);
					mergedNode.setName(mergedNode.getName()+","+discardNode.getName());

					List<Edge> l2nodesIncoming = new LinkedList<Edge>(discardNode.getIncoming());
					for (Edge eIn : l2nodesIncoming) {
						eIn.setTarget(mergedNode);
					}

					List<Edge> l2nodesOutgoing = new LinkedList<Edge>(discardNode.getOutgoing());
					for (Edge eOut : l2nodesOutgoing) {
						eOut.setSource(mergedNode);
					}

					mappingsOfRule2.put(l2node, mergedNode);
					
					if (discardNode.getAllEdges().size() > 0) {
						System.err.println("All Edges of should have been removed, but still "
								+ l2node.getAllEdges().size() + " are remaining!");
					}
					
					// löschen des Knoten "nodeInL2y"
					Graph graphOfNodeL2 = discardNode.getGraph();
					boolean removedNode = graphOfNodeL2.removeNode(discardNode);
					System.err.println("removedNode: " + removedNode);
				}
			}

			List<Node> nodesInCopyOfLhsOfRule2 = new LinkedList<Node>(l2copy.getNodes());
			for (Node nodeInCopyOfLhsOfRule2 : nodesInCopyOfLhsOfRule2) {
				nodeInCopyOfLhsOfRule2.setGraph(pushout);
			}
			List<Edge> edgesInCopyOfLhsOfRule2 = new LinkedList<Edge>(l2copy.getEdges());
			for (Edge edgeInCopyOfLhsOfRule2 : edgesInCopyOfLhsOfRule2) {
				edgeInCopyOfLhsOfRule2.setGraph(pushout);
			}
			
			// check that NO edges and nodes are remaining in copyOfLhsOfRule2
			if (l2copy.getEdges().size() > 0) {
				System.err.println(l2copy.getEdges().size() + " edges reaming in " + l2copy
						+ ", but should be 0");
			}
			if (l2copy.getNodes().size() > 0) {
				System.err.println(l2copy.getNodes().size() + " nodes reaming in " + l2copy
						+ ", but should be 0");
			}
			
			// check that the number of edges and nodes in edgeInCopyOfLhsOfRule1 is correct
			int numberOfExpectedNodes = (l1.getNodes().size() + l2.getNodes().size()
					- s1.getNodes().size());
			if (pushout.getNodes().size() != numberOfExpectedNodes) {
				System.err.println("Amount of nodes in created result graph of pushout not as expected. Difference: "
						+ (pushout.getNodes().size() - numberOfExpectedNodes));
			}
			// set copyOfLhsOfRule1 as resultGraph
			resultGraph = pushout;

		}

		private Mapping getMappingOfOrigin(List<Mapping> mappingsOfRules, Node origin) {
			for (Mapping mapping : mappingsOfRules) {
				if (mapping.getOrigin() == origin)
					return mapping;
			}
			return null;
		}

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

}