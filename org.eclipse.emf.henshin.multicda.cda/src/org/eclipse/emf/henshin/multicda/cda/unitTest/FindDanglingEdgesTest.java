package org.eclipse.emf.henshin.multicda.cda.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.junit.Before;
import org.junit.Test;

public class FindDanglingEdgesTest {

	final String PATH = "testData/refactoring/";
	final String henshinFileName = "refactorings.henshin";

	Rule decapsulateAttributeRule;
	Rule pullUpEncapsulatedAttributeRule;

	@Before
	public void setUp() throws Exception {
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		Module module = resourceSet.getModule(henshinFileName, false);

		for (Unit unit : module.getUnits()) {
			if (unit.getName().equals("decapsulateAttribute"))
				decapsulateAttributeRule = (Rule) unit;
			if (unit.getName().equals("pullUpEncapsulatedAttribute"))
				pullUpEncapsulatedAttributeRule = (Rule) unit;
		}
	}

	@Test
	public void findDanglingEdges_2_13() {

		Node node2InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("2");
		Node node13InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("13");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node2InLhsOfRule1.getType(), "2,13");

		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, node2InLhsOfRule1);
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, node13InLhsOfRule2);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule,
				pullUpEncapsulatedAttributeRule);
		Span span = atomicCoreCPA.newSpan(node2InRule1Mapping, graphOfSpan, node13InRule2Mapping);

		Pushout pushoutResult = new Pushout(decapsulateAttributeRule, span,
				pullUpEncapsulatedAttributeRule);

		List<Edge> findDanglingEdges = atomicCoreCPA.findDanglingEdgesOfRule1(
				pushoutResult.getRule1Mappings());

		assertEquals(2, findDanglingEdges.size());

		boolean methodsEdgeDetected = false;
		boolean typeEdgeDetected = false;

		for (Edge danglingEdge : findDanglingEdges) {
			if (danglingEdge.getType().getName().equals("methods")) {
				// Do check source and target!
				Node sourceNode = danglingEdge.getSource();
				String sourceNodeName = sourceNode.getName();
				if (sourceNodeName.equals("11"))
					methodsEdgeDetected = true;
			}
			if (danglingEdge.getType().getName().equals("type")) {
				Node targetNode = danglingEdge.getTarget();
				String targetNodeName = targetNode.getName();
				if (targetNodeName.equals("16"))
					typeEdgeDetected = true;
			}
		}

		assertEquals(true, methodsEdgeDetected);
		assertEquals(true, typeEdgeDetected);
	}

	@Test
	public void findDanglingEdges_3_14() {

		Node node3InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("3");
		Node node14InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("14");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node3InLhsOfRule1.getType(), "3,14");

		Mapping node3InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, node3InLhsOfRule1);
		Mapping node14InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, node14InLhsOfRule2);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule,
				pullUpEncapsulatedAttributeRule);
		Span span = atomicCoreCPA.newSpan(node3InRule1Mapping, graphOfSpan, node14InRule2Mapping);

		Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, span,
				pullUpEncapsulatedAttributeRule);

		List<Edge> findDanglingEdges = atomicCoreCPA.findDanglingEdgesOfRule1(decapsulateAttributeRule,
				pushoutResult.getRule1Mappings());

		assertEquals(2, findDanglingEdges.size());

		boolean methodsEdgeDetected = false;
		boolean typeEdgeDetected = false;

		for (Edge danglingEdge : findDanglingEdges) {
			if (danglingEdge.getType().getName().equals("methods")) {
				Node sourceNode = danglingEdge.getSource();
				String sourceNodeName = sourceNode.getName();
				if (sourceNodeName.equals("11"))
					methodsEdgeDetected = true;
			}
			if (danglingEdge.getType().getName().equals("parameters")) {
				Node targetNode = danglingEdge.getTarget();
				String targetNodeName = targetNode.getName();
				if (targetNodeName.equals("15"))
					typeEdgeDetected = true;
			}
		}

		assertEquals(true, methodsEdgeDetected);
		assertEquals(true, typeEdgeDetected);
	}

	@Test
	public void findNoDanglingEdges_1_2_6_11_13_16() {

		Node node1InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("1");
		Node node2InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("2");
		Node node6InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("6");
		Node node11InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("11");
		Node node13InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("13");
		Node node16InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("16");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Set<Mapping> rule1Mappings = new HashSet<Mapping>();
		Set<Mapping> rule2Mappings = new HashSet<Mapping>();
		Node commonNode1_11OfSpan = henshinFactory.createNode(graphOfSpan, node1InLhsOfRule1.getType(), "1_11");
		Mapping node1InRule1Mapping = henshinFactory.createMapping(commonNode1_11OfSpan, node1InLhsOfRule1);
		rule1Mappings.add(node1InRule1Mapping);
		Mapping node11InRule2Mapping = henshinFactory.createMapping(commonNode1_11OfSpan, node11InLhsOfRule2);
		rule2Mappings.add(node11InRule2Mapping);
		Node commonNode2_13OfSpan = henshinFactory.createNode(graphOfSpan, node2InLhsOfRule1.getType(), "2_13");
		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNode2_13OfSpan, node2InLhsOfRule1);
		rule1Mappings.add(node2InRule1Mapping);
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNode2_13OfSpan, node13InLhsOfRule2);
		rule2Mappings.add(node13InRule2Mapping);
		Node commonNode6_16OfSpan = henshinFactory.createNode(graphOfSpan, node6InLhsOfRule1.getType(), "6_16");
		Mapping node6InRule1Mapping = henshinFactory.createMapping(commonNode6_16OfSpan, node6InLhsOfRule1);
		rule1Mappings.add(node6InRule1Mapping);
		Mapping node16InRule2Mapping = henshinFactory.createMapping(commonNode6_16OfSpan, node16InLhsOfRule2);
		rule2Mappings.add(node16InRule2Mapping);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule,
				pullUpEncapsulatedAttributeRule);
		Span span = atomicCoreCPA.newSpan(rule1Mappings, graphOfSpan, rule2Mappings);

		Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, span,
				pullUpEncapsulatedAttributeRule);

		List<Edge> findDanglingEdges = atomicCoreCPA.findDanglingEdgesOfRule1(decapsulateAttributeRule,
				pushoutResult.getRule1Mappings());

		assertEquals(0, findDanglingEdges.size());
	}
}
