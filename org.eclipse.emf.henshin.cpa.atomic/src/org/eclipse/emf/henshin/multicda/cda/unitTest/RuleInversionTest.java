package org.eclipse.emf.henshin.multicda.cda.unitTest;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.DependencyAnalysis;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.Assert;

public class RuleInversionTest {

	/** 
	 * Relative path to the test model files.
	 */
	public static final String PATH = "testData/ruleInversion";

	@Test
	public void test() {

		// Create a resource set with a base directory:
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		
		// Load the module:
		Module module = resourceSet.getModule("bank.henshin", false);

		Unit unit = module.getUnit("ruleToInvert");
		Rule ruleToInvert = (Rule) unit;
		
		
		//TODO: provide rule inverison function somewhere else than in the 
		Rule invertedRule = DependencyAnalysis.invertRule(ruleToInvert);
		
		// check that LHS contains 3 nodes, 3 edges and the nodes are of type :Bank, :Client and :Manager
		Assert.assertEquals(3, invertedRule.getLhs().getNodes().size());
		Assert.assertEquals(3, invertedRule.getLhs().getEdges().size());
		EList<Node> nodesInLhs = invertedRule.getLhs().getNodes();
		Set<String> nameOfTypesInLHS = new HashSet<String>();
		for(Node node : nodesInLhs){
			nameOfTypesInLHS.add(node.getType().getName());
		}
		Assert.assertTrue(nameOfTypesInLHS.contains("Bank"));
		Assert.assertTrue(nameOfTypesInLHS.contains("Client"));
		Assert.assertTrue(nameOfTypesInLHS.contains("Account"));
		
		// check that LHS contains 3 nodes, 3 edges and the nodes are of type :Bank, :Client and :Manager
		Assert.assertEquals(3, invertedRule.getRhs().getNodes().size());
		Assert.assertEquals(3, invertedRule.getRhs().getEdges().size());
		EList<Node> nodesInRhs = invertedRule.getRhs().getNodes();
		Set<String> nameOfTypesInRHS = new HashSet<String>();
		for(Node node : nodesInRhs){
			nameOfTypesInRHS.add(node.getType().getName());
		}
		Assert.assertTrue(nameOfTypesInRHS.contains("Bank"));
		Assert.assertTrue(nameOfTypesInRHS.contains("Client"));
		Assert.assertTrue(nameOfTypesInRHS.contains("Manager"));
		
		// check that two Mappings exist
		Assert.assertEquals(2, invertedRule.getMappings().size());
		
		// check that :Manager is created and :Account is deleted.
		Action deleteAction = new Action(Action.Type.DELETE);
		EList<Node> deleteActionNodes = invertedRule.getActionNodes(deleteAction);
		Assert.assertEquals(1, deleteActionNodes.size());
		Node deletionNode = deleteActionNodes.get(0);
		Assert.assertEquals("Account", deletionNode.getType().getName());
		Action createAction = new Action(Action.Type.CREATE);
		EList<Node> createActionNodes = invertedRule.getActionNodes(createAction);
		Assert.assertEquals(1, createActionNodes.size());
		Node creationNode = createActionNodes.get(0);
		Assert.assertEquals("Manager", creationNode.getType().getName());
		
		Module newModule = HenshinFactory.eINSTANCE.createModule();
		EPackage bankEPackage = module.getImports().get(0);
		newModule.getImports().add(bankEPackage);
		newModule.getUnits().add(invertedRule);
		
		// save result in FileSystem!
		HenshinResourceSet resultResourceSet = new HenshinResourceSet(PATH);
		resultResourceSet.saveEObject(newModule, "bank_INV.henshin");
	}

}
