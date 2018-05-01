package org.eclipse.emf.henshin.multicda.cda;

import java.security.cert.CertPathValidatorException.Reason;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;
import org.eclipse.emf.henshin.preprocessing.RulePair;

public abstract class Utils {
	private static HenshinFactory factory = HenshinFactory.eINSTANCE;

	/**
	 * werden - wie wird damit umgeganen, wenn die Knoten oder Mappings nicht
	 * erstellt wurde? - wird NULL zurück gegeben, oder wird eine Exception
	 * geworfen? - sollte es eine Möglichkeit vorab geben zu prüfen, ob die
	 * Regel invertierbar ist?
	 * 
	 * @param rule1
	 * @return the
	 */
	public static Rule invertRule(Rule rule1) {

		String desc = rule1.getDescription();
		Rule invRule1 = factory.createRule(rule1.getName());
		invRule1.setDescription("INV");

		// Kopieren des RHS Graph und als LHS der neuen Regel zuordnen
		Copier copierForRhsToLhs = new Copier();
		Graph newLhs = (Graph) copierForRhsToLhs.copy(rule1.getRhs());
		copierForRhsToLhs.copyReferences();
		newLhs.setName("LHS");
		invRule1.setLhs(newLhs);

		// Kopieren des LHS Graph und als RHS der neuen Regel zuordnen
		Copier copierForLhsToRhs = new Copier();
		Graph newRhs = (Graph) copierForLhsToRhs.copy(rule1.getLhs());
		copierForLhsToRhs.copyReferences();
		newRhs.setName("RHS");
		invRule1.setRhs(newRhs);

		// notwendige Mappings erstellen. Dazu alle Mappings durchgehen und
		// ausgehend von den Nodes in der ursprünglichen Regel über den copier
		// die Nodes in der neuen regel identifizieren!
		MappingList mappingsOfOriginalRule1 = rule1.getMappings();
		for (Mapping mappingInOriginalRule1 : mappingsOfOriginalRule1) {

			// identifizieren der ORIGIN in der neuen Regel
			Node imageInOriginalRule = mappingInOriginalRule1.getImage();
			EObject originInNewRule = copierForRhsToLhs.get(imageInOriginalRule);
			Node originInNewRuleNode = (Node) originInNewRule;

			// identifizieren des IMAGE in der neuen Regel
			Node originInOriginalRule = mappingInOriginalRule1.getOrigin();
			EObject imageInNewRule = copierForLhsToRhs.get(originInOriginalRule);
			Node imageInNewRuleNode = (Node) imageInNewRule;

			Mapping createdMapping = factory.createMapping(originInNewRuleNode, imageInNewRuleNode);
			invRule1.getMappings().add(createdMapping);
		}

		// ggf. als Datei speichern?
		// ABER(!): auch prüfen, ob es rein programmatisch geht!

		return invRule1;
	}

	public static List<RulePair> prepareNonDeletingVersions(List<Rule> rules) {
		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		List<RulePair> copiesOfRulesWithoutDeletion = new LinkedList<RulePair>();
		for (Rule ruleToCopy : rules) {
			Copier copierForRule = new Copier();
			Rule copyOfRule = (Rule) copierForRule.copy(ruleToCopy);

			String dsc = ruleToCopy.getDescription();
			dsc = dsc == null ? "" : dsc;
			copyOfRule.setDescription(dsc + "_nonDelete");
			copierForRule.copyReferences();

			MappingList mappings = copyOfRule.getMappings();
			mappings.clear();
			for (Node nodeInLhs : copyOfRule.getLhs().getNodes()) {
				nodeInLhs.getAttributes().clear();
			}

			Copier copierForLhsGraph = new Copier();

			Graph copiedLhs = (Graph) copierForLhsGraph.copy(copyOfRule.getLhs());
			copierForLhsGraph.copyReferences();

			copiedLhs.setName("Rhs");
			copyOfRule.setRhs(copiedLhs);

			for (Node nodeInLhsOfCopiedRule : copyOfRule.getLhs().getNodes()) {
				Node nodeInNewRhs = (Node) copierForLhsGraph.get(nodeInLhsOfCopiedRule);
				Mapping createdMapping = henshinFactory.createMapping(nodeInLhsOfCopiedRule, nodeInNewRhs);
				mappings.add(createdMapping);
			}

			copiesOfRulesWithoutDeletion.add(new RulePair(copyOfRule, ruleToCopy));

		}
		return copiesOfRulesWithoutDeletion;
	}

	public static List<Rule> prepareNoneDeletingsVersionsRules(List<Rule> rules) {
		List<Rule> result = new ArrayList<Rule>();
		List<RulePair> pairs = prepareNonDeletingVersions(rules);
		for (RulePair rulePair : pairs) {
			result.add(rulePair.getCopy());
		}
		return result;
	}

	public static Rule nonDeleteRule(Rule rule) {
		List<Rule> result = new ArrayList<Rule>();
		result.add(rule);
		List<RulePair> pairs = prepareNonDeletingVersions(result);
		return pairs.get(0).getCopy();
	}

	public static Set<Rule> createNACRule(Rule rule1) {
		HenshinFactory factory = HenshinFactory.eINSTANCE;
		EList<NestedCondition> nacs = rule1.getLhs().getNACs();
		Set<Rule> result = new HashSet<>();
		if (nacs == null || nacs.isEmpty())
			return result;

		int nodeId = 0;
		int id = 0;
		for (NestedCondition nac : nacs) {
			Graph nacGraph = nac.getConclusion();
			Rule nacRule = factory.createRule(rule1.getName());
			nacRule.setDescription(": NACrule_" + id);

			Graph newRhs;
			Graph newLhs;

			Map<Node, Node> added = new HashMap<>();

			// Kopieren des NAC Graph und als LHS der neuen Regel zuordnen
			Copier lhsCopier = new Copier();
			newLhs = (Graph) lhsCopier.copy(nacGraph);
			lhsCopier.copyReferences();
			nacRule.setLhs(newLhs);
			newLhs.setName("LHS");
			newLhs.setDescription(": this is a merge of NAC and L Graphs of the Original Rule");

			// Kopieren des LHS und RHS Graph und als RHS der neuen Regel zuordnen
			Copier rhsCopier = new Copier();
			newRhs = (Graph) rhsCopier.copy(rule1.getLhs());
			rhsCopier.copyReferences();
			nacRule.setRhs(newRhs);
			newRhs.setName("RHS");
			newRhs.setDescription(": this is the L Graph of the Original Rule");

			//Mappings 
			MappingList nacMappings = nac.getMappings();
			for (Mapping nacMapping : nacMappings) {

				Node imageInOriginalRule = nacMapping.getImage();
				EObject originInNewRule = lhsCopier.get(imageInOriginalRule);
				Node originInNewRuleNode = (Node) originInNewRule;

				Node originInOriginalRule = nacMapping.getOrigin();
				EObject imageInNewRule = rhsCopier.get(originInOriginalRule);
				Node imageInNewRuleNode = (Node) imageInNewRule;

				originInNewRuleNode.setName(imageInNewRuleNode.getName());

				Mapping createdMapping = factory.createMapping(originInNewRuleNode, imageInNewRuleNode);
				nacRule.getMappings().add(createdMapping);
			}
			for (Node n : nacRule.getLhs().getNodes())
				if (n.getName() == null || n.getName().isEmpty())
					n.setName("|f" + nodeId++ + "|");
			result.add(nacRule);
		}

		return result;
	}

	public static Node addNodeToGraph(Node nodeInRule1, Node nodeInRule2, Graph S1, Set<Mapping> rule1Mappings,
			Set<Mapping> rule2Mappings) {
		EClass subNodeType = identifySubNodeType(nodeInRule1, nodeInRule2);
		Node commonNode = factory.createNode(S1, subNodeType, nodeInRule1.getName() + "_" + nodeInRule2.getName());

		rule1Mappings.add(factory.createMapping(commonNode, nodeInRule1));
		rule2Mappings.add(factory.createMapping(commonNode, nodeInRule2));
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
	public static EClass identifySubNodeType(Node node1, Node node2) {
		if (node1.getType().equals(node2.getType()))
			return node1.getType();
		if (node1.getType().getEAllSuperTypes().contains(node2.getType()))
			return node1.getType();
		if (node2.getType().getEAllSuperTypes().contains(node1.getType()))
			return node2.getType();
		return null;
	}

	public static void checkNull(Object o) throws IllegalArgumentException {
		checkNull(o, "object");
	}

	public static void checkNull(Object o, String name) throws IllegalArgumentException {
		if (null == o)
			throw new IllegalArgumentException(name + " must not be null");
	}

	public boolean isRuleSupported(Rule rule) {
		if (rule.getMultiRules().size() > 0) {
			if (rule.getLhs().getPACs().size() > 0)
				throw new RuntimeException("positive application conditions (PAC) are not supported");
		}
		return true;
	}

	/**
	 * @param graph
	 * @return
	 */
	public static EPackage graphToEPackage(Span r) {
		Set<String> added = new HashSet<String>();
		EPackage result = EcoreFactory.eINSTANCE.createEPackage();
		result.setName(r.getRule1().getName() + "_" + r.getRule2().getName());
		result.setNsURI("http://cdapackage/" + r.getRule1().getName() + "/" + r.getRule2().getName() + "/"
				+ r.getClass().getSimpleName());
		result.setNsPrefix("CDAPackage");
		EList<EClassifier> classifiers = result.getEClassifiers();

		for (Node node : r.graph.getNodes()) {
			EClass n = getClassifier(r, node);
			added.add(n.getName());
			result.getEClassifiers().add(n);
		}

		for (Edge edge : r.graph.getEdges()) {
			EClass s = getClassifier(r, edge.getSource());
			EClass t = getClassifier(r, edge.getTarget());

			if (!added.contains(s.getName())) {
				classifiers.add(s);
				added.add(s.getName());
			} else
				s = (EClass) result.getEClassifier(s.getName());
			if (!added.contains(t.getName())) {
				classifiers.add(t);
				added.add(t.getName());
			} else
				t = (EClass) result.getEClassifier(t.getName());

			EReference ref = EcoreFactory.eINSTANCE.createEReference();
			ref.setName(edge.getType().getName());
			if (!r.getRule1().getRhs().getEdges().contains(edge)) {
				ref.setName("#" + ref.getName() + "#");
			}
			ref.setEType(t);
			s.getEStructuralFeatures().add(ref);

		}
		return result;
	}

	public static EClass getClassifier(Span r, Node node) {
		EClassifier nodeClass = node.getType();
		EClass eclass = EcoreFactory.eINSTANCE.createEClass();
		eclass.setName(node.getName() + ":" + nodeClass.getName());

		Node image = r.getMappingIntoRule1(node).getImage();
		if (image.getAction().getType() == Type.DELETE)
			eclass.setName("#" + eclass.getName() + "#");
		return eclass;
	}
}
