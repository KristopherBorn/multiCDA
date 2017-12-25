package org.eclipse.emf.henshin.multicda.cda.conflict;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.cpa.result.Conflict;
import org.eclipse.emf.henshin.cpa.result.CriticalElement;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.util.HenshinEGraph;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.exporters.HenshinAGGExporter;
import org.eclipse.emf.henshin.model.impl.MappingListImpl;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.SpanMappings;

public class ConflictReasonCreator /*extends InitialConflictReason */{
	

	/**
	 * @param cp
	 * @return
	 */
	public static ConflictReason createConflictReason(Conflict cp){
		
		HenshinFactory henshinFactory = HenshinFactory.eINSTANCE;
		
		Match match1 = cp.getMatch1();
		Match match2 = cp.getMatch2();
		// TODO: check if this contains edges!
		List<CriticalElement> criticalElements = cp.getCriticalElements();
		Rule rule1 = cp.getFirstRule();
		Rule rule2 = cp.getSecondRule();
				
//		Set<Mapping> mappingsInRule1;
		MappingList mappingList1 = new MappingListImpl();
//		Set<Mapping> mappingsInRule2;
		MappingList mappingList2 = new MappingListImpl();
		
		Graph graph = henshinFactory.createGraph();
		
		for(Node lhs1Node : rule1.getLhs().getNodes()){
			EObject nodeTargetOfR1 = match1.getNodeTarget(lhs1Node);
			if(nodeTargetOfR1 != null){
				for(Node lhs2Node : rule2.getLhs().getNodes()){
					EObject nodeTargetOfR2 = match2.getNodeTarget(lhs2Node);
					if(nodeTargetOfR2 != null){
						if(nodeTargetOfR1 == nodeTargetOfR2){
							// here we have identified that we require an appropriate node in the S1 graph
							EClass type = null;
							//identify Type for inheritance situations. should always be the most concrete class. 
							// 	dosnt support situation where the node in the overlap should be a commonsubtype of both nodes
							EClass typeOfRule1 = lhs1Node.getType();
							EClass typeOfRule2 = lhs2Node.getType();
							EList<EClass> eAllSuperTypesOfR1EClass = typeOfRule1.getEAllSuperTypes();
							EList<EClass> eAllSuperTypesOfR2EClass = typeOfRule2.getEAllSuperTypes();
							if(eAllSuperTypesOfR1EClass.contains(typeOfRule2)){
								type = typeOfRule1;
							}else if (eAllSuperTypesOfR2EClass.contains(typeOfRule1)) {
								type = typeOfRule2;
							} else if (typeOfRule1 == typeOfRule2) {
								type = typeOfRule1;
							}
							if(type != null){
								String nameOfNode = lhs1Node.getName()+"_"+lhs2Node.getName();
								Node createdNode = henshinFactory.createNode(graph, type, nameOfNode);
								Mapping mappingR1 = henshinFactory.createMapping(createdNode, lhs1Node);
								mappingList1.add(mappingR1);
								Mapping mappingR2 = henshinFactory.createMapping(createdNode, lhs2Node);
								mappingList2.add(mappingR2);
								
									
									
									//TODO: zur Sicherheit hier pr�fen, ob beide Knoten im gleichen Graph der LHS von R1 liegen 
									//	(andernfalls liegt ein Programmierfehler vor)
									


	//								�ber alle Knoten die f�r den S1 erstellt wurden iterieren
	//								pr�fen, ob es in der Regel1 Kanten zu diesem Knoten gibt.
	//								pr�fen, ob es auch in der Regel2 zwischen den beiden zugeordneten Knoten eine Kante des gelichen Typs gibt
	//								pr�fen, ob im CPA overlap eine Kante mit dem Namensschema gibt (HenshinExporter Methode nutzen!)
									// (besser/einfacher ging das nat�rlich wenn man nur �ber die EReferences im ECore Modell geht, 
									// 	aber dort ist wieder das Problem der Zuordnung der EReferences zu den Kantentypen die im, 
									// 	den Regeln zugrundeliegenden, ECore Modell definiert sind (auch EReferences) ))
									for(Node nodeInS1 : graph.getNodes()){
										// gibt es Kanten zu dem Knoten in Regel 1
										Node potentialConnectedNodeInR1 = mappingList1.getImage(nodeInS1, rule1.getLhs());
//										EObject nodeTargetOfR1 = match1.getNodeTarget(lhs1Node);
										EList<Edge> outgoingEdgesOfL1Node = lhs1Node.getOutgoing();
										for(Edge outgoingEdgeOfL1Node : outgoingEdgesOfL1Node){
											if(outgoingEdgeOfL1Node.getTarget() == potentialConnectedNodeInR1){
												// gibt es Kante zu dem Knoten in Regel 2
												Node potentialConnectedNodeInR2 = mappingList2.getImage(nodeInS1, rule2.getLhs());
												EList<Edge> outgoingEdgesOfL2Node = lhs2Node.getOutgoing();
												for(Edge outgoingEdgeOfL2Node : outgoingEdgesOfL2Node){
													if(outgoingEdgeOfL2Node.getTarget() == potentialConnectedNodeInR2){
														if(nodeTargetOfR1 instanceof EClass){ // should always be true!
															EClass eClassOfOverlapBeeingProcessed = (EClass) nodeTargetOfR1;
															EList<EReference> eAllReferences = eClassOfOverlapBeeingProcessed.getEAllReferences();
															for(EReference eReference : eAllReferences){
																String uniqueReferenceNameOfOverlap = eReference.getName();
																String uniqueReferenceNameOfR1 = "";
																String uniqueReferenceNameOfR2 = "";
																try {
																	uniqueReferenceNameOfR1 = HenshinAGGExporter.getUniqueReferenceName(outgoingEdgeOfL1Node.getType());
																} catch (Exception e) {
																	// TODO Auto-generated catch block
																	e.printStackTrace();
																}
																try {
																	uniqueReferenceNameOfR2 = HenshinAGGExporter.getUniqueReferenceName(outgoingEdgeOfL2Node.getType());
																} catch (Exception e) {
																	// TODO Auto-generated catch block
																	e.printStackTrace();
																}
																if(uniqueReferenceNameOfOverlap.equals(uniqueReferenceNameOfR1) && uniqueReferenceNameOfOverlap.equals(uniqueReferenceNameOfR2)){
																	// Kante in �berlappungsgraph mit zugeh�rigen Kanten in den Regeln entdeckt.
																	// ggf vereinfachen mit reinem abgelich der Namen der KAnten.
//																	TODO: aus den bekannten Knoten und Kanten den passenden Knoten im overlap suchen!
																	henshinFactory.createEdge(createdNode, potentialConnectedNodeInR1, outgoingEdgeOfL1Node.getType());
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		Span span = new Span(new HashSet<Mapping>(mappingList1), graph, new HashSet<Mapping>(mappingList2));
		return new ConflictReason(span);
		}
}