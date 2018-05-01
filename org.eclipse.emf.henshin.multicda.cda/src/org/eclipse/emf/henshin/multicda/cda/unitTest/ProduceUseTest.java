package org.eclipse.emf.henshin.multicda.cda.unitTest;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.DependencyAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.dependency.DependencyAtom;
import org.eclipse.emf.henshin.multicda.cda.dependency.MinimalDependencyReason;
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
		
		DependencyAnalysis produceUseAtomicCoreCPA = new DependencyAnalysis(encapsulateAttribute, pushDownEncapsulatedAttribute);
		
		Set<DependencyAtom> dependencyAtoms = produceUseAtomicCoreCPA.computeDependencyAtoms();
		
		Assert.assertEquals(3, dependencyAtoms.size());
		
		DependencyAtom depAtom_Method_4_4 = null;
		DependencyAtom depAtom_Method_4_5 = null;
		DependencyAtom depAtom_Parameter_4_6 = null;
		for(DependencyAtom dependencyAtom : dependencyAtoms){
			// all three atoms shall have a single node
			Assert.assertEquals(1, dependencyAtom.getGraph().getNodes().size());
			Node nodeOfAtom = dependencyAtom.getGraph().getNodes().get(0);
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
	}

}
