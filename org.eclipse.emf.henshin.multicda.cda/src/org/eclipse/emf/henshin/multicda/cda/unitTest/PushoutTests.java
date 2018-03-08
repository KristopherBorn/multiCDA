package org.eclipse.emf.henshin.multicda.cda.unitTest;

import static org.junit.Assert.*;

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

public class PushoutTests {

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
	public void pushoutResultTest_2_13() {

		Node node2InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("2");
		Node node13InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("13");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node2InLhsOfRule1.getType(), "2,13");
		// Mapping
		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, node2InLhsOfRule1);
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, node13InLhsOfRule2);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule, pullUpEncapsulatedAttributeRule);
		Span span = atomicCoreCPA.newSpan(node2InRule1Mapping, graphOfSpan, node13InRule2Mapping);

		Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, span,
				pullUpEncapsulatedAttributeRule);
		Graph resultGraph = pushoutResult.getResultGraph();

		assertEquals(13, resultGraph.getNodes().size());

		assertEquals(16, resultGraph.getEdges().size());
	}

	@Test
	public void pushoutResultTest_2_14() {

		Node node2InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("2");
		Node node14InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("14");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node2InLhsOfRule1.getType(), "2,14");

		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, node2InLhsOfRule1);
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, node14InLhsOfRule2);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule, pullUpEncapsulatedAttributeRule);
		Span span = atomicCoreCPA.newSpan(node2InRule1Mapping, graphOfSpan, node13InRule2Mapping);

		Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, span,
				pullUpEncapsulatedAttributeRule);
		Graph resultGraph = pushoutResult.getResultGraph();

		assertEquals(13, resultGraph.getNodes().size());

		assertEquals(16, resultGraph.getEdges().size());
	}

	@Test
	public void pushoutResultTest_5_15() {

		Node node5InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("5");
		Node node15InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("15");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node5InLhsOfRule1.getType(), "5,15");

		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, node5InLhsOfRule1);
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, node15InLhsOfRule2);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule, pullUpEncapsulatedAttributeRule);
		Span span = atomicCoreCPA.newSpan(node2InRule1Mapping, graphOfSpan, node13InRule2Mapping);

		Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, span,
				pullUpEncapsulatedAttributeRule);
		Graph resultGraph = pushoutResult.getResultGraph();

		assertEquals(13, resultGraph.getNodes().size());

		assertEquals(16, resultGraph.getEdges().size());
	}
	
	
	@Test
	public void firstParameterNull(){

		Node node2InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("2");
		Node node13InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("13");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node2InLhsOfRule1.getType(), "2,13");
		// Mapping
		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, node2InLhsOfRule1);
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, node13InLhsOfRule2);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(null, pullUpEncapsulatedAttributeRule);
		Span span = atomicCoreCPA.newSpan(node2InRule1Mapping, graphOfSpan, node13InRule2Mapping);

		boolean illeagalArgumentExceptionThrown = false;
		try {			
			Pushout pushoutResult = atomicCoreCPA.newPushoutResult(null, span,
					pullUpEncapsulatedAttributeRule);
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrown = true;
		}
		
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrown);
	}
	

	@Test
	public void secondParameterNull(){

		Node node2InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("2");
		Node node13InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("13");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node2InLhsOfRule1.getType(), "2,13");
		// Mapping
		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, node2InLhsOfRule1);
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, node13InLhsOfRule2);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule, pullUpEncapsulatedAttributeRule);
		Span span = atomicCoreCPA.newSpan(node2InRule1Mapping, graphOfSpan, node13InRule2Mapping);

		boolean illeagalArgumentExceptionThrown = false;
		try {			
			Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, null, pullUpEncapsulatedAttributeRule);
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrown = true;
		}
		
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrown);
	}
	

	@Test
	public void thirdParameterNull(){

		Node node2InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("2");
		Node node13InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("13");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node2InLhsOfRule1.getType(), "2,13");
		// Mapping
		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, node2InLhsOfRule1);
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, node13InLhsOfRule2);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule, null);
		Span span = atomicCoreCPA.newSpan(node2InRule1Mapping, graphOfSpan, node13InRule2Mapping);

		boolean illeagalArgumentExceptionThrown = false;
		try {			
			Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, span, null);
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrown = true;
		}
		
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrown);
	}
	
	@Test
	public void invalideSpan_missingMappingImageInR1(){

		Node node13InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("13");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node13InLhsOfRule2.getType(), "2,13");
		// Mapping
		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, null); //
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, node13InLhsOfRule2);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule, pullUpEncapsulatedAttributeRule);
		Span span = atomicCoreCPA.newSpan(node2InRule1Mapping, graphOfSpan, node13InRule2Mapping);

		boolean illeagalArgumentExceptionThrown = false;
		try {			
			Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, span, pullUpEncapsulatedAttributeRule);
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrown = true;
		}
		
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrown);
	}

	@Test
	public void invalideSpan_missingMappingImageInR2(){

		Node node2InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("2");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node2InLhsOfRule1.getType(), "2,13");
		// Mapping
		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, node2InLhsOfRule1); //
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, null);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule, pullUpEncapsulatedAttributeRule);
		Span span = atomicCoreCPA.newSpan(node2InRule1Mapping, graphOfSpan, node13InRule2Mapping);

		boolean illeagalArgumentExceptionThrown = false;
		try {			
			Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, span, pullUpEncapsulatedAttributeRule);
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrown = true;
		}
		
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrown);
	}

	// mapping sollte in R2 sein, ist aber in R1
	@Test
	public void invalideSpan_mappingInR1InsteadOfR2(){

		Node node2InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("2");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node2InLhsOfRule1.getType(), "2,13");
		// Mapping
		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, node2InLhsOfRule1);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule, pullUpEncapsulatedAttributeRule);
		// wrong parameter passed! Intend here to test the check functionality
		Span span = atomicCoreCPA.newSpan(node2InRule1Mapping, graphOfSpan, node2InRule1Mapping);

		boolean illeagalArgumentExceptionThrown = false;
		try {			
			Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, span, pullUpEncapsulatedAttributeRule);
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrown = true;
		}
		
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrown);
	}

	// mapping sollte in R1 sein, ist aber in R2
	@Test
	public void invalideSpan_mappingInR2InsteadOfR1(){

		Node node13InLhsOfRule2 = pullUpEncapsulatedAttributeRule.getLhs().getNode("13");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node13InLhsOfRule2.getType(), "2,13");
		// Mapping
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, node13InLhsOfRule2);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule, pullUpEncapsulatedAttributeRule);
		// wrong parameter passed! Intend here to test the check functionality
		Span span = atomicCoreCPA.newSpan(node13InRule2Mapping, graphOfSpan, node13InRule2Mapping);

		boolean illeagalArgumentExceptionThrown = false;
		try {			
			Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, span, pullUpEncapsulatedAttributeRule);
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrown = true;
		}
		
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrown);
	}

	@Test
	public void invalideSpan_mappingInR1RhsInsteadOfLhs(){
		// mapping sollte in LHS der Regel 1 sein, ist aber in die RHS der R1
		// geht nciht, da die Knoten gelöscht werden sind diese in der RHS nciht vorhanden!
	}

	// mapping sollte in LHS der Regel 2 sein, ist aber in die RHS der R2
	@Test
	public void invalideSpan_mappingInR2RhsInsteadOfLhs(){

		Node node2InLhsOfRule1 = decapsulateAttributeRule.getLhs().getNode("2");
		Node node13InRhsOfRule2 = pullUpEncapsulatedAttributeRule.getRhs().getNode("13");

		HenshinFactory henshinFactory = new HenshinFactoryImpl();
		Graph graphOfSpan = henshinFactory.createGraph();
		Node commonNodeOfSpan = henshinFactory.createNode(graphOfSpan, node2InLhsOfRule1.getType(), "2,13");
		// Mapping
		Mapping node2InRule1Mapping = henshinFactory.createMapping(commonNodeOfSpan, node2InLhsOfRule1); //
		Mapping node13InRule2Mapping = henshinFactory.createMapping(commonNodeOfSpan, node13InRhsOfRule2);

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule, pullUpEncapsulatedAttributeRule);
		Span span = atomicCoreCPA.newSpan(node2InRule1Mapping, graphOfSpan, node13InRule2Mapping);

		boolean illeagalArgumentExceptionThrown = false;
		try {			
			Pushout pushoutResult = atomicCoreCPA.newPushoutResult(decapsulateAttributeRule, span, pullUpEncapsulatedAttributeRule);
		} catch (IllegalArgumentException e) {
			illeagalArgumentExceptionThrown = true;
		}
		
		assertTrue("A IllegalArgumentException where expected but hadnt been thrown.", illeagalArgumentExceptionThrown);
	}
}
