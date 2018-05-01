package org.eclipse.emf.henshin.multicda.cda.computation;

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
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.multicda.cda.Span;
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
					
					
					
					
					
					
					
					
					
					
					Node el1InRhsOfR1 = rule1.getMappings().getImage((Node) el1, rule1.getRhs());
					boolean nodeInR1IsDeleted = (el1InRhsOfR1 == null);

					boolean differingConstants = false;
					
					
					
					

					for (Attribute attrOfR2 : nodeInLhsOfR2.getAttributes()) {
						if (!differingConstants) {
							Attribute attrOfSameTypeInLhsOfR1 = ((Node) el1).getAttribute(attrOfR2.getType());

							
							
							boolean attrOfR2IsConstant = isAttrValueAConstant(attrOfR2, rule2);
							if (attrOfSameTypeInLhsOfR1 != null) {
								
								
								boolean attrOfSameTypeInLhsOfR1IsConstant = isAttrValueAConstant(
										attrOfSameTypeInLhsOfR1, rule1);
								if (attrOfR2IsConstant && attrOfSameTypeInLhsOfR1IsConstant) {
									
									boolean valuesIdentical = attrOfR2.getValue()
											.equals(attrOfSameTypeInLhsOfR1.getValue());
									if (valuesIdentical) { 
															
										
										
										
										
										
										
										henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
												attrOfR2.getValue());
									} else { 
										
										
										
										
										differingConstants = true;
									}
								} else {
									
									
									
									
									
									
									
									if (attrOfR2IsConstant) 
															
										
										
										henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
												attrOfR2.getValue());
									if (attrOfSameTypeInLhsOfR1IsConstant) 
																			
																			
																			
										
										
										henshinFactory.createAttribute(newNodeInS1Graph,
												attrOfSameTypeInLhsOfR1.getType(), attrOfSameTypeInLhsOfR1.getValue());
								}

							} else {
								
								
								
								
								
								
								if (attrOfR2IsConstant)
									
									
									henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
											attrOfR2.getValue());
							}
						}
					}

					if (!differingConstants) {
						for (Attribute attrOfR1Node : ((Node) el1).getAttributes()) {
							Attribute attributeInR2Node = nodeInLhsOfR2.getAttribute(attrOfR1Node.getType());
							if (attributeInR2Node == null) { 
																
																
																
								if (isAttrValueAConstant(attrOfR1Node, rule1)) {
									
									
									henshinFactory.createAttribute(newNodeInS1Graph, attrOfR1Node.getType(),
											attrOfR1Node.getValue());
								}
							}
						}
					}
					if (!differingConstants)
						result.add(S1span);
				}
			}

			
			
		}
		if (el1 instanceof Edge) {
			atomicUsageElements.addAll(rule2.getLhs().getEdges(((Edge) el1).getType()));
		}

		
		for (ModelElement el2 : atomicUsageElements) {

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


		
		for (Node nodeInLhsOfR2 : rule2.getLhs().getNodes()) {
			boolean r1NodeIsSuperTypeOfR2Node = false;
			boolean r2NodeIsSuperTypeOfR1Node = false;
				r1NodeIsSuperTypeOfR2Node = nodeInLhsOfR2.getType().getEAllSuperTypes().contains(((Node) el1).getType());
				r2NodeIsSuperTypeOfR1Node = ((Node) el1).getType().getEAllSuperTypes()
						.contains(nodeInLhsOfR2.getType());
			boolean identicalType = nodeInLhsOfR2.getType().equals((((Node) el1).getType()));

			Graph S1 = henshinFactory.createGraph();
			Set<Mapping> rule1Mappings = new HashSet<Mapping>();
			Set<Mapping> rule2Mappings = new HashSet<Mapping>();
			Node newNodeInS1Graph = addNodeToGraph((Node) el1, (Node) nodeInLhsOfR2, S1, rule1Mappings, rule2Mappings);
			Span S1span = new Span(rule1Mappings, S1, rule2Mappings);

			if (r1NodeIsSuperTypeOfR2Node || r2NodeIsSuperTypeOfR1Node || identicalType) {


				Node el1InRhsOfR1 = rule1.getMappings().getImage((Node) el1, rule1.getRhs());

				boolean differingConstants = false;

				boolean changeAttrIsUsed = false;

				for (Attribute attrOfR2 : nodeInLhsOfR2.getAttributes()) {
					if (!differingConstants) {
						Attribute attrOfSameTypeInLhsOfR1 = ((Node) el1).getAttribute(attrOfR2.getType());
						boolean attrOfR2IsConstant = isAttrValueAConstant(attrOfR2, rule2);
						if (attrOfSameTypeInLhsOfR1 != null) {

							boolean attrOfSameTypeInLhsOfR1IsConstant = isAttrValueAConstant(attrOfSameTypeInLhsOfR1,
									rule1);
							if (attrOfR2IsConstant && attrOfSameTypeInLhsOfR1IsConstant) {
								boolean valuesIdentical = attrOfR2.getValue()
										.equals(attrOfSameTypeInLhsOfR1.getValue());
								if (valuesIdentical) {
									henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
											attrOfR2.getValue());
									if (!attrOfSameTypeInLhsOfR1.getValue()
											.equals(el1InRhsOfR1.getAttribute(attrOfR2.getType()).getValue())) {
										changeAttrIsUsed = true;
									}
								} else {
									differingConstants = true;
								}
							} else {
								if (attrOfR2IsConstant)
									henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
											attrOfR2.getValue());
								if (attrOfSameTypeInLhsOfR1IsConstant)
									henshinFactory.createAttribute(newNodeInS1Graph, attrOfSameTypeInLhsOfR1.getType(),
											attrOfSameTypeInLhsOfR1.getValue());

								Attribute attrInRhsOfR1 = el1InRhsOfR1.getAttribute(attrOfSameTypeInLhsOfR1.getType());
								if (attrInRhsOfR1 == null
										|| !attrOfSameTypeInLhsOfR1.getValue().equals(attrInRhsOfR1.getValue())) {
									changeAttrIsUsed = true;
								}
								if (!attrOfR2IsConstant && !attrOfSameTypeInLhsOfR1IsConstant) {
									String valueForAttr = attrOfSameTypeInLhsOfR1.getValue() + "_"
											+ attrOfSameTypeInLhsOfR1.getValue();
									henshinFactory.createAttribute(newNodeInS1Graph, attrOfSameTypeInLhsOfR1.getType(),
											valueForAttr);
								}
							}

						} else {
							if (attrOfR2IsConstant)
								henshinFactory.createAttribute(newNodeInS1Graph, attrOfR2.getType(),
										attrOfR2.getValue());
						}

					}
				}

				if (!differingConstants) {
					for (Attribute attrOfR1Node : ((Node) el1).getAttributes()) {
						Attribute attributeInR2Node = nodeInLhsOfR2.getAttribute(attrOfR1Node.getType());
						if (attributeInR2Node == null) {
							if (isAttrValueAConstant(attrOfR1Node, rule1)) {
								henshinFactory.createAttribute(newNodeInS1Graph, attrOfR1Node.getType(),
										attrOfR1Node.getValue());
							}
						}
					}
				}

				if (!differingConstants && changeAttrIsUsed)
					result.add(S1span);
			}
		}
	}

	private boolean isAttrValueAConstant(Attribute attr, Rule rule) {
		if (isPrimitiveDataType(attr.getType())) {
			if (attributeIsParsable(attr)) {
				return true;
			}
		}
		return false;
	}

	private boolean attributeIsParsable(Attribute attrOfR2) {
		EAttribute type = attrOfR2.getType();
		EDataType eAttributeType = type.getEAttributeType();
		if (attrOfR2.getType().toString() == "EString") {
			String value = attrOfR2.getValue();
			if (value.startsWith("\"") && value.endsWith("\"")) {
				String[] split = value.split("\"");
				if (split.length == 2)
					return true;
			}
			return false;
		}
		if (attrOfR2.getType().toString() == "EChar") {
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

		
		
		
		for (Node lhsNode : rule1.getActionNodes(preserveAction)) {
			
			
			
			
			Node rhsNode = rule1.getMappings().getImage(lhsNode, rule1.getRhs());
			boolean attributeChanged = false;
			for (Attribute lhsAttr : lhsNode.getAttributes()) {
				Attribute rhsAttr = rhsNode.getAttribute(lhsAttr.getType());
				
				if (rhsAttr == null || !lhsAttr.getValue().equals(rhsAttr.getValue())) {
					attributeChanged = true;
					atomicChangeNodes.add(lhsNode);
					break;
				}
			}
			
			if (!attributeChanged) {
				for (Attribute rhsAttr : rhsNode.getAttributes()) {
					Attribute lhsAttr = lhsNode.getAttribute(rhsAttr.getType());
					
					if (lhsAttr == null) {
						attributeChanged = true;
						atomicChangeNodes.add(lhsNode);
						break;
					}
				}

			}
			
			
			
			
			
		}
		return atomicChangeNodes;
	}
}
