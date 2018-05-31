/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.computation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;
import org.eclipse.emf.henshin.model.impl.RuleImpl;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteDeleteConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteUseConflictReason;

/**
 * 
 * @author vincentcuccu 17.12.2017
 */
public class DeleteUseConflictReasonComputation {

	private static final String NODESEPARATOR = "_";
	private static final String INTERSECTIONSEPERATOR = "%";
	private Rule rule1;
	private Rule rule2;
	private Set<Span> conflictReasonsFromR2;
	private MinimalReasonComputation helperForCheckDangling = new MinimalReasonComputation(rule1, rule2);
	private static HenshinFactoryImpl helper = new HenshinFactoryImpl();
	private static Action preserve = new Action(Action.Type.PRESERVE);
	private static Action delete = new Action(Action.Type.DELETE);

	/**
	 * constructor
	 * 
	 * @param rule1
	 * @param rule2
	 * @param conflictReasonsFromR2
	 */
	public DeleteUseConflictReasonComputation(Rule rule1, Rule rule2, Set<Span> conflictReasonsFromR2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		this.conflictReasonsFromR2 = conflictReasonsFromR2;
	}

	/**
	 * constructs all Initial Reasons as candidates for r1 and r2
	 * 
	 * @param conflictReasons
	 * @return result
	 */
	public Set<DeleteUseConflictReason> computeDeleteUseConflictReason(Set<Span> conflictReasons) {
		Set<DeleteUseConflictReason> result = new HashSet<DeleteUseConflictReason>();
		for (Span conflictReason : conflictReasons) {
			computeDeleteUseConflictReasons(conflictReason, result);
		}
		return result;
	}

	/**
	 * the Method to encounter the delete read conflict reasons
	 * 
	 * @param conflictReason
	 * @param result
	 */
	private void computeDeleteUseConflictReasons(Span conflictReason, Set<DeleteUseConflictReason> result) {
		setMappingsForRule2(conflictReason, rule2);
		if (isDeleteReadConflictReason(conflictReason, rule2)) {
			Pushout pushout = new Pushout(rule1, conflictReason, rule2);
			if (helperForCheckDangling.findDanglingEdgesOfRule1(rule1, pushout.getRule1Mappings()).isEmpty()
					&& helperForCheckDangling.findDanglingEdgesOfRule1(rule2, pushout.getRule2Mappings()).isEmpty()) {
				DeleteUseConflictReason res = new DeleteReadConflictReason(conflictReason);
				result.add(res);
			}
		} else {
			Set<DeleteUseConflictReason> ddset = ConstructDeleteDeleteSet(rule1, rule2, conflictReason);
			if (!ddset.isEmpty()) {
				ddset.forEach(s -> result.add(s));
			}
		}
	}

	/**
	 * @param conflictReason
	 * @param rule22
	 */
	private void setMappingsForRule2(Span conflictReason, Rule rule) {
		HashSet<Mapping> newMappingsToRule2 = new HashSet<Mapping>();
		Set<Mapping> mappingsInRule2 = conflictReason.getMappingsInRule2();
		for (Mapping mapping : mappingsInRule2) {
			Node image = mapping.getImage();
			String name = image.getName();
			EClass type = image.getType();
			Node origin = mapping.getOrigin();
			EList<Node> nodes = rule.getLhs().getNodes();
			for (Node node : nodes) {
				String name2 = node.getName();
				EClass type2 = node.getType();
				if (name.equals(name2) && type.equals(type2)) {
					Mapping newMapping = helper.createMapping(origin, node);
					newMappingsToRule2.add(newMapping);
				}
			}
		}

		if (!newMappingsToRule2.isEmpty()) {
			conflictReason.setMappingsInRule2(newMappingsToRule2);
			conflictReason.setRule2(rule);
		}

	}

	/**
	 * returns true, if there is a match from S1 to K2, which is equal to the
	 * match of S1 to lhs of rule 2
	 * 
	 * @param conflictReason
	 * @param rule2
	 * @return boolean
	 */
	public static boolean isDeleteReadConflictReason(Span conflictReason, Rule rule2) {
		Set<Mapping> mappingsInRule2 = conflictReason.getMappingsInRule2();
		for (Mapping mapping : mappingsInRule2) {
			Node origin = mapping.getOrigin();
			Node image = mapping.getImage();
			Action action = image.getAction();
			Mapping mappingIntoRule1 = conflictReason.getMappingIntoRule1(origin);
			if (mappingIntoRule1.getImage().getAction() == delete){
				if (!action.equals(preserve))
					return false;
			}
		}
		EList<Edge> edges = conflictReason.getGraph().getEdges();
		for (Edge edge : edges) {
			Node source = edge.getSource();
			Node target = edge.getTarget();
			Mapping mappingOfSource = conflictReason.getMappingIntoRule2(source);
			Mapping mappingOfTarget = conflictReason.getMappingIntoRule2(target);
			Node image = mappingOfTarget.getImage();
			EList<Edge> outgoing = mappingOfSource.getImage().getOutgoing();
			for (Edge edge2 : outgoing) {
				Node target2 = edge2.getTarget();
				if (target2.equals(image)) {
					Action action = edge2.getAction();
					if (!action.equals(preserve))
						return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param r2
	 * @param r1
	 * @param sp1
	 * @return
	 */
	private Set<DeleteUseConflictReason> ConstructDeleteDeleteSet(Rule r1, Rule r2, Span sp1) {
		Set<DeleteUseConflictReason> result = new HashSet<>();
		for (Span sp2 : conflictReasonsFromR2) {
			setMappingsForRule2(sp2, rule1);
			boolean sp2DRCR = isDeleteReadConflictReason(sp2, r1);
			if (sp2DRCR) {
				// DeleteReadConflictReason deleteReadConflictReason = new
				// DeleteReadConflictReason(sp2); //TODO Das hier unkommentieren
				// und man hat sogar die Rückrichtung
				// result.add(deleteReadConflictReason);
			} else {
				Span s = computeCompatibleSpans(sp1, sp2);
				if (s != null) {
					if (!isEmpty(s.getGraph())) {
						Rule sRule1 = s.getRule1();
						Rule sRule2 = s.getRule2();
						if (sRule1 != null && sRule2 != null) {
							Pushout pushout = new Pushout(sRule1, s, sRule2);
							if (pushout != null) {
								Span l1Sl2 = computePushoutSpanFromStoRules(r1, pushout, r2, s, sp1, sp2);
								if (l1Sl2 != null) {
									Pushout po = new Pushout(r1, l1Sl2, r2);
									if (helperForCheckDangling.findDanglingEdgesOfRule1(r1, po.getRule1Mappings())
											.isEmpty()
											&& helperForCheckDangling
													.findDanglingEdgesOfRule1(r2, po.getRule2Mappings()).isEmpty()) {
										DeleteDeleteConflictReason res = new DeleteDeleteConflictReason(sp1, sp2);
										result.add(res);
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param rule1
	 * @param pushout
	 * @param sap
	 * @param sp2
	 * @param sp1
	 * @return
	 */
	private Span computePushoutSpanFromStoRules(Rule rule1, Pushout pushout, Rule rule2, Span sap, Span sp1, Span sp2) {
		Span uniqueSpan = null;
		Graph pushoutGraph = pushout.getResultGraph();
		boolean precondition = precondition(sap, sp1, sp2);
		if (!precondition) {
			return null;
		}
		Set<Mapping> mappingsInL1 = computeMappingStoL(pushout, rule1, sp1, sp2);
		Set<Mapping> mappingsInL2 = computeMappingStoL(pushout, rule2, sp2, sp1);
		uniqueSpan = new Span(mappingsInL1, pushoutGraph, mappingsInL2);
		uniqueSpan.setRule1(rule1);
		uniqueSpan.setRule2(rule2);
		return uniqueSpan;
	}

	/**
	 * @param pushout
	 * @param sap
	 * @param sp2
	 * @param sp1
	 * @param rule12
	 * @return
	 */
	private Set<Mapping> computeMappingStoL(Pushout pushout, Rule rule, Span sp1, Span sp2) {
		HashSet<Mapping> result = new HashSet<Mapping>();
		Graph pushoutGraph = pushout.getResultGraph();
		List<Mapping> s1ToS = pushout.getRule1Mappings();
		List<Mapping> s2ToS = pushout.getRule2Mappings();
		EList<Node> nodes = pushoutGraph.getNodes();
		for (Node node : nodes) {
			Mapping c = getMappingFromSpan(node, s1ToS);
			Mapping d = getMappingFromSpan(node, s2ToS);
			Node sElement = null;
			if (c != null)
				sElement = c.getOrigin();
			else if (d != null)
				sElement = d.getOrigin();
			else
				return null;
			Mapping sMapping = getMappingFromSpans(sElement, sp1, sp2);
			if (sMapping == null)
				return null;
			Node element = sMapping.getImage();
			Graph lhs = rule.getLhs();
			nodes = lhs.getNodes();
			if (nodes.contains(element)) {
				Mapping createMapping = helper.createMapping(node, element);
				result.add(createMapping);
			}
		}

		return result;
	}

	/**
	 * @param origin
	 * @param sp1
	 * @param sp2
	 * @return
	 */
	private Mapping getMappingFromSpans(Node origin, Span sp1, Span sp2) {
		Mapping image1 = sp1.getMappingIntoRule1(origin);
		if (image1 != null)
			return image1;
		Mapping image2 = sp2.getMappingIntoRule2(origin);
		if (image2 != null)
			return image2;
		return null;
	}

	private boolean precondition(Span sap, Span sp1, Span sp2) {
		boolean result = false;
		Graph sapGraph = sap.getGraph();
		EList<Node> sapNodes = sapGraph.getNodes();
		for (Node node : sapNodes) {
			Mapping a = sap.getMappingIntoRule1(node);
			Mapping b = sap.getMappingIntoRule2(node);
			if (a != null && b != null) {
				Node s1Element = a.getImage();
				Node s2Element = b.getImage();
				Mapping s1l1 = sp1.getMappingIntoRule1(s1Element);
				Mapping s2l1 = sp2.getMappingIntoRule2(s2Element);
				Mapping s1l2 = sp1.getMappingIntoRule2(s1Element);
				Mapping s2l2 = sp2.getMappingIntoRule1(s2Element);
				if (s1l1 != null && s2l1 != null && s1l2 != null && s2l2 != null) {
					Node e1Element = s1l1.getImage();
					Node f1Element = s2l1.getImage();
					Node e2Element = s2l2.getImage();
					Node f2Element = s1l2.getImage();
					if (e1Element != null && f1Element != null && e2Element != null && f2Element != null) {
						if (e1Element.equals(f1Element) && e2Element.equals(f2Element)) {
							result = true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return result;
	}

	/**
	 * @param node
	 * @param mappings
	 * @return
	 */
	private Mapping getMappingFromSpan(Node node, List<Mapping> mappings) {
		for (Mapping mapping : mappings) {
			if (node.equals(mapping.getImage())) {
				return mapping;
			}
		}
		return null;
	}

	/**
	 * @param graph
	 * @return
	 */
	private boolean isEmpty(Graph graph) {
		if (graph.getNodes().isEmpty() && graph.getEdges().isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * @param sp2
	 * @param sp1
	 * @return
	 */
	private Span computeCompatibleSpans(Span sp1, Span sp2) {
		Span s1Apostrophe = computeCompatibleElements(sp1, sp2);
		if (s1Apostrophe != null) {
			Span s2Apostrophe = computeCompatibleElements(sp2, sp1);
			if (s2Apostrophe != null) {
				Span intersection = computeIntersection(s1Apostrophe, s2Apostrophe);
				Graph sApostroph = intersection.getGraph();
				if (intersection != null && sApostroph != null) {
					RuleImpl ruleSp1 = new RuleImpl();
					ruleSp1.setLhs(sp1.getGraph());
					RuleImpl ruleSp2 = new RuleImpl();
					ruleSp2.setLhs(sp2.getGraph());
					intersection.setRule1(ruleSp1);
					intersection.setRule2(ruleSp2);
					return intersection;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
		return null;
	}

	/**
	 * @param graph1
	 * @param graph2
	 * @param sp2
	 * @param sp1
	 * @return
	 */
	private Span computeIntersection(Span sp1, Span sp2) {
		Graph result = helper.createGraph("S'");
		Graph graph1 = sp1.getGraph();
		Graph graph2 = sp2.getGraph();
		EList<Node> s1Nodes = graph1.getNodes();
		EList<Node> s2Nodes = graph2.getNodes();
		Set<Mapping> mappingsIntoSpan1 = new HashSet<Mapping>();
		Set<Mapping> mappingsIntoSpan2 = new HashSet<Mapping>();
		for (Node node1 : s1Nodes) {
			Mapping mapping11 = sp1.getMappingIntoRule1(node1);
			Mapping mapping12 = sp1.getMappingIntoRule2(node1);
			for (Node node2 : s2Nodes) {
				Mapping mapping21 = sp2.getMappingIntoRule1(node2);
				Mapping mapping22 = sp2.getMappingIntoRule2(node2);
				Node image11 = mapping11.getImage();
				Node image21 = mapping21.getImage();
				Node image12 = mapping12.getImage();
				Node image22 = mapping22.getImage();
				if (image11.equals(image22) && image12.equals(image21)) {
					Node newNode = helper.createNode(result, node1.getType(), node1.getName());
					Mapping mappingFromGraphToRule12 = sp1.getMappingIntoRule1(node1);
					Node mappingFromGraphToRule1 = mappingFromGraphToRule12.getImage();
					Mapping createMapping = helper.createMapping(newNode, mappingFromGraphToRule1);
					mappingsIntoSpan1.add(createMapping);
					Mapping mappingIntoRule2 = sp2.getMappingIntoRule1(node2);
					Node image = mappingIntoRule2.getImage();
					Mapping createMapping2 = helper.createMapping(newNode, image);
					mappingsIntoSpan2.add(createMapping2);
				}
			}
		}
		Span span = new Span(mappingsIntoSpan1, result, mappingsIntoSpan2);
		return span;
	}

	/**
	 * @param sp1
	 * @param sp2
	 * @param s1Apostrophe
	 * @return
	 */
	private Span computeCompatibleElements(Span sp1, Span sp2) {
		Graph compatibleGraph = helper.createGraph();
		EList<Node> s1Nodes = sp1.getGraph().getNodes();
		Set<Mapping> mappingsIntoSpan1 = new HashSet<Mapping>();
		Set<Mapping> mappingsIntoSpan2 = new HashSet<Mapping>();
		for (Node x : s1Nodes) {
			try {
				GraphElement y = findCompatibleElement(x, sp1, sp2);
				if (y != null) {
					EClass xType = ((Node) x).getType();
					Node xNode = (Node) x;
					Node yNode = (Node) y;
					String newName = xNode.getName() + INTERSECTIONSEPERATOR + yNode.getName();
					Node newNode = helper.createNode(compatibleGraph, xType, newName);
					Mapping createMapping = helper.createMapping(newNode, xNode);
					Mapping createMapping2 = helper.createMapping(newNode, yNode);
					mappingsIntoSpan1.add(createMapping);
					mappingsIntoSpan2.add(createMapping2);
				}
			} catch (NotCompatibleException e) {
				// e.printStackTrace();
				break;
			}

		}
		Span comSpan = new Span(mappingsIntoSpan1, compatibleGraph, mappingsIntoSpan2);
		return comSpan;
	}

	/**
	 * @param x
	 * 
	 * @param sp1
	 * 
	 * @param sp2
	 * 
	 * @param conflictReason2
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	private GraphElement findCompatibleElement(Node x, Span sp1, Span sp2) throws NotCompatibleException {
		EList<Node> s1Nodes = sp1.getGraph().getNodes();
		EList<Node> s2Nodes = sp2.getGraph().getNodes();
		if (s1Nodes.contains(x)) {
			for (Node y : s2Nodes) {
				int result = checkEqualityOfElements(x, y, sp1, sp2);
				if (result == 2) {
					return y;
				} else if (result == 1)
					throw new NotCompatibleException();
			}
			return null;
		} else {
			throw new NotCompatibleException();
		}
	}

	/**
	 * @param x1
	 * @param y1
	 * @param sp1
	 * @param sp2
	 * @return
	 */
	private int checkEqualityOfElements(Node x, Node y, Span sp1, Span sp2) {
		Mapping s11 = getMappingInRule(x, sp1.getMappingsInRule1());
		Mapping s21 = getMappingInRule(y, sp2.getMappingsInRule2());

		Mapping s12 = getMappingInRule(x, sp1.getMappingsInRule2());
		Mapping s22 = getMappingInRule(y, sp2.getMappingsInRule1());

		if (s11 != null && s21 != null && s12 != null && s22 != null) {
			boolean b1 = s11.getImage().getName().equals(s21.getImage().getName())
					&& s11.getImage().getType() == s21.getImage().getType();
			boolean b2 = s12.getImage().getName().equals(s22.getImage().getName())
					&& s12.getImage().getType() == s22.getImage().getType();
			return (b1 ? 1 : 0) + (b2 ? 1 : 0);
		}
		return 0;
	}

	/**
	 * @param originNode
	 * @param mappingsInRule
	 * @return Mapping
	 */
	public Mapping getMappingInRule(Node originNode, Set<Mapping> mappingsInRule) {
		for (Mapping mapping : mappingsInRule) {
			Node origin = mapping.getOrigin();
			if (originNode.equals(origin)) {
				return mapping;
			}
		}
		return null;
	}
}