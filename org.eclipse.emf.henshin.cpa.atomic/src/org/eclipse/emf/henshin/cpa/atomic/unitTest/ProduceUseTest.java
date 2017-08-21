package org.eclipse.emf.henshin.cpa.atomic.unitTest;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.ProduceUseAtomicCoreCPA;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.dependency.DependencyAtom;
import org.eclipse.emf.henshin.cpa.atomic.dependency.MinimalDependencyReason;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

public class ProduceUseTest {
	
	final String PATH = "testData/produceUseDepTest/";
	final String henshinFileName = "refactorings.henshin";

	Rule encapsulateAttribute;
	Rule pushDownEncapsulatedAttribute;

	
	@Before
	public void setUp() throws Exception {
		HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
		Module module = resourceSet.getModule(henshinFileName, false);
		
		encapsulateAttribute = (Rule) module.getUnit("encapsulateAttribute");
		pushDownEncapsulatedAttribute = (Rule) module.getUnit("pushDownEncapsulatedAttribute");
	}

	@Test
	public void encapsulateAttribute_pushDownEncapsulatedAttribute_Test() {
		
		
//		AtomicCoreCPA atomicCoreCPA = new AtomicCoreCPA();
		
		ProduceUseAtomicCoreCPA produceUseAtomicCoreCPA = new ProduceUseAtomicCoreCPA();
		
		List<DependencyAtom> dependencyAtoms = produceUseAtomicCoreCPA.computeDependencyAtoms(encapsulateAttribute, pushDownEncapsulatedAttribute);
		
		Assert.assertEquals(3, dependencyAtoms.size());
		
		//TODO: weitere Prüfungen (Assertions) hinzufügen!
		// Prüfen, dass das DependencyAtom valide ist (je Mapping das Origin und Image)
		DependencyAtom depAtom_Method_4_4 = null;
		DependencyAtom depAtom_Method_4_5 = null;
		DependencyAtom depAtom_Parameter_4_6 = null;
		for(DependencyAtom dependencyAtom : dependencyAtoms){
			// all three atoms shall have a single node
			Assert.assertEquals(1, dependencyAtom.getSpan().getGraph().getNodes().size());
			Node nodeOfAtom = dependencyAtom.getSpan().getGraph().getNodes().get(0);
			if(nodeOfAtom.getType().getName().equals("Parameter")){
				depAtom_Parameter_4_6 = dependencyAtom;
			}
			else {
				if(nodeOfAtom.getType().getName().equals("Method")){
					if(nodeOfAtom.getName().contains("4.4")){
						depAtom_Method_4_4 = dependencyAtom;
					}
					if(nodeOfAtom.getName().contains("4.5")){
						depAtom_Method_4_5 = dependencyAtom;
					}
				}
			}
		}
		Assert.assertNotNull(depAtom_Method_4_4);
		Assert.assertNotNull(depAtom_Method_4_5);
		Assert.assertNotNull(depAtom_Parameter_4_6);
		
		
		Set<MinimalDependencyReason> dependencyReasonsOfMethod_4_4 = depAtom_Method_4_4.getMinimalDependencyReasons();
		Assert.assertEquals(1, dependencyReasonsOfMethod_4_4.size());
		Set<MinimalDependencyReason> dependencyReasonsOfMethod_4_5 = depAtom_Method_4_5.getMinimalDependencyReasons();
		Assert.assertEquals(1, dependencyReasonsOfMethod_4_5.size());
		Set<MinimalDependencyReason> dependencyReasonsOfMethod_4_6 = depAtom_Parameter_4_6.getMinimalDependencyReasons();
		Assert.assertEquals(1, dependencyReasonsOfMethod_4_6.size());

		Span dependencyReasonOfMethod_4_4 = dependencyReasonsOfMethod_4_4.iterator().next();
		Span dependencyReasonOfMethod_4_5 = dependencyReasonsOfMethod_4_5.iterator().next();
		Span dependencyReasonOfParameter_4_6 = dependencyReasonsOfMethod_4_6.iterator().next();
		Assert.assertTrue(dependencyReasonOfMethod_4_4.equals(dependencyReasonOfParameter_4_6));

		// Prüfen, dass die MinimalDependencyReasons valide sind.
		
		
		Assert.assertEquals(4, dependencyReasonOfMethod_4_4.getGraph().getNodes().size());
		Node class_4_1;
		Node method_4_4;
		Node parameter_4_6;
		Node class_4_2;
//		List<Mapping> mappingsInRule1 = dependencyReasonOfMethod_4_4.getMappingsInRule1();
		for(Mapping mappingInRule1 : dependencyReasonOfMethod_4_4.getMappingsInRule1()){
			Assert.assertTrue(mappingInRule1.getOrigin().getGraph() == dependencyReasonOfMethod_4_4.getGraph());
			Assert.assertTrue(mappingInRule1.getImage().getGraph() == encapsulateAttribute.getRhs());
//			TODO: hier stellt man fest, dass die "DependencyReasons" noch nicht angepasst wurden, sondern es sich noch immer um die "ConflictReasons" handelt! 
		}
//		List<Mapping> mappingsInRule2 = dependencyReasonOfMethod_4_4.getMappingsInRule2();
		for(Mapping mappingInRule2 : dependencyReasonOfMethod_4_4.getMappingsInRule2()){
			Assert.assertTrue(mappingInRule2.getOrigin().getGraph() == dependencyReasonOfMethod_4_4.getGraph());
			Assert.assertTrue(mappingInRule2.getImage().getGraph() == pushDownEncapsulatedAttribute.getLhs());
		}
		
//		DependencyAtom dependencyAtom = dependencyAtoms.get(0);
//		Assert.assertEquals(1, dependencyAtom.getSpan().getMappingsInRule1().size());
//		Assert.assertEquals(1, dependencyAtom.getSpan().getMappingsInRule2().size());
//		
//		Mapping mappingInRule1 = dependencyAtom.getSpan().getMappingsInRule1().get(0);
//		Assert.assertTrue(mappingInRule1.getOrigin().getGraph() == dependencyAtom.getSpan().getGraph());
//		Assert.assertTrue(mappingInRule1.getImage() == createA_Rule.getRhs().getNodes().get(0));
//		
//		Mapping mappingInRule2 = dependencyAtom.getSpan().getMappingsInRule2().get(0);
//		Assert.assertTrue(mappingInRule2.getOrigin().getGraph() == dependencyAtom.getSpan().getGraph());
//		Assert.assertTrue(mappingInRule2.getImage() == useA_Rule.getLhs().getNodes().get(0));
		
		
		Assert.assertEquals(3, dependencyReasonsOfMethod_4_5.iterator().next().getGraph().getNodes().size());
//		Node class_4_1;
//		Node method_4_5;
//		Node class_4_2;
//		List<Mapping> mappingsInRule1 = dependencyReasonOfMethod_4_5.getMappingsInRule1();
		for(Mapping mappingInRule1 : dependencyReasonOfMethod_4_5.getMappingsInRule1()){
			Assert.assertTrue(mappingInRule1.getOrigin().getGraph() == dependencyReasonOfMethod_4_5.getGraph());
			Assert.assertTrue(mappingInRule1.getImage().getGraph() == encapsulateAttribute.getRhs());
//			TODO: hier stellt man fest, dass die "DependencyReasons" noch nicht angepasst wurden, sondern es sich noch immer um die "ConflictReasons" handelt! 
		}
//		List<Mapping> mappingsInRule2 = dependencyReasonOfMethod_4_5.getMappingsInRule2();
		for(Mapping mappingInRule2 : dependencyReasonOfMethod_4_5.getMappingsInRule2()){
			Assert.assertTrue(mappingInRule2.getOrigin().getGraph() == dependencyReasonOfMethod_4_5.getGraph());
			Assert.assertTrue(mappingInRule2.getImage().getGraph() == pushDownEncapsulatedAttribute.getLhs());
		}
		
		
		Assert.assertEquals(4, dependencyReasonOfParameter_4_6.getGraph().getNodes().size());
//		Node class_4_1;
//		Node method_4_4;
//		Node parameter_4_6;
//		Node class_4_2;
//		List<Mapping> mappingsInRule1 = dependencyReasonOfParameter_4_6.getMappingsInRule1();
		for(Mapping mappingInRule1 : dependencyReasonOfParameter_4_6.getMappingsInRule1()){
			Assert.assertTrue(mappingInRule1.getOrigin().getGraph() == dependencyReasonOfParameter_4_6.getGraph());
			Assert.assertTrue(mappingInRule1.getImage().getGraph() == encapsulateAttribute.getRhs());
//			TODO: hier stellt man fest, dass die "DependencyReasons" noch nicht angepasst wurden, sondern es sich noch immer um die "ConflictReasons" handelt! 
		}
//		List<Mapping> mappingsInRule2 = dependencyReasonOfParameter_4_6.getMappingsInRule2();
		for(Mapping mappingInRule2 : dependencyReasonOfParameter_4_6.getMappingsInRule2()){
			Assert.assertTrue(mappingInRule2.getOrigin().getGraph() == dependencyReasonOfParameter_4_6.getGraph());
			Assert.assertTrue(mappingInRule2.getImage().getGraph() == pushDownEncapsulatedAttribute.getLhs());
		}
		
	}

}
