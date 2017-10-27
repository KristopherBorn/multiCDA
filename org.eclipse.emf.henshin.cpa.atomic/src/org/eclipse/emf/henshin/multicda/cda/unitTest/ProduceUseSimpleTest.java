package org.eclipse.emf.henshin.multicda.cda.unitTest;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.DependencyAnalysis;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.dependency.DependencyAtom;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.Assert;

public class ProduceUseSimpleTest {
	
	final String PATH = "testData/simpleTest/";
	final String henshinFileName = "simpleTgRules.henshin";

	Rule createA_Rule;
	Rule useA_Rule;

//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}
//
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}
	
	@Before
	public void setUp() throws Exception {
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		Module module = resourceSet.getModule(henshinFileName, false);
		
		createA_Rule = (Rule) module.getUnit("createA");
		useA_Rule = (Rule) module.getUnit("useA");
	}

	@Test
	public void createA_useA_Test() {
		
		
//		AtomicCoreCPA atomicCoreCPA = new AtomicCoreCPA();
		
		DependencyAnalysis produceUseAtomicCoreCPA = new DependencyAnalysis(createA_Rule, useA_Rule);
		
		Set<DependencyAtom> dependencyAtoms = produceUseAtomicCoreCPA.computeDependencyAtoms();
		
		Assert.assertEquals(1, dependencyAtoms.size());
		
		//TODO: weitere Prüfungen (Assertions) hinzufügen!
		// Prüfen, dass das DependencyAtom valide ist (je Mapping das Origin und Image)
		DependencyAtom dependencyAtom = dependencyAtoms.iterator().next();
		Assert.assertEquals(1, dependencyAtom.getMappingsInRule1().size());
		Assert.assertEquals(1, dependencyAtom.getMappingsInRule2().size());
		
		Mapping mappingInRule1 = dependencyAtom.getMappingsInRule1().iterator().next(); //get(0);
		Assert.assertTrue(mappingInRule1.getOrigin().getGraph() == dependencyAtom.getGraph());
		Assert.assertTrue(mappingInRule1.getImage() == createA_Rule.getRhs().getNodes().get(0));
		
		Mapping mappingInRule2 = dependencyAtom.getMappingsInRule2().iterator().next(); //get(0);
		Assert.assertTrue(mappingInRule2.getOrigin().getGraph() == dependencyAtom.getGraph());
		Assert.assertTrue(mappingInRule2.getImage() == useA_Rule.getLhs().getNodes().get(0));
		
		// Prüfen, dass die MinimalDependencyReasons valide sind.
		
	}

}
