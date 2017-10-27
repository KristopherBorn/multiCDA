package org.eclipse.emf.henshin.multicda.cda.unitTest.jevTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.multicda.cda.tester.CDATester;
import org.eclipse.emf.henshin.multicda.cda.tester.CPATester;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.CP;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.Conditions;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.ICR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.MCR;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.Node;
import org.eclipse.ui.internal.handlers.WizardHandler.New;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AttributeTestOldStyle {
	private String path = "testData/jevsTests/attribute/attributeRules.henshin";
	private String change = "change";
	private String changeN = "changeN";
	private String use = "use";
	private String useN = "useN";
	private String changeAndConstantFoo = "changeAndConstantFoo";
	private String useAndConstantBaa = "useAndConstantBaa";
	private String useOne = "useOne";
	private String pathCases = "testData/jevsTests/attribute/attributeCasesRules.henshin";
	private String deleteAttr = "deleteAttributes";
	private String delete = "delete";

	
	@Test
	public void extendedAChangeUseAtomicTest(){
		final String PATH = "testData/jevsTests/attribute/";
		final String henshinFileName = "attributeRules.henshin";

		Rule firstRule = null;
		Rule secondRule = null;

			HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
			Module module = resourceSet.getModule(henshinFileName, false);

			for (Unit unit : module.getUnits()) {
				if (unit.getName().equals(change))
					firstRule = (Rule) unit;
				if (unit.getName().equals(use))
					secondRule = (Rule) unit;
			}
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(firstRule, secondRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		assertEquals(1, computedConflictAtoms.size());
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(1, allMinimalConflictReasons.size());
		
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : allMinimalConflictReasons){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		
		MinimalConflictReason mcr = minimalConflictReasons.iterator().next();
		Assert.assertEquals(1, mcr.getGraph().getNodes().size());
		org.eclipse.emf.henshin.model.Node mcrNode = mcr.getGraph().getNodes().get(0);
		String mcrNodeName = mcrNode.getName();
		Assert.assertEquals("1_2", mcrNodeName);

		Assert.assertEquals(1, mcrNode.getAttributes().size());
		
		Attribute attribute = mcrNode.getAttributes().get(0);
		Assert.assertEquals("Number", attribute.getType().getName());
		Assert.assertEquals("0", attribute.getValue());
		
		Set<InitialReason> computeInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(1, computeInitialReason.size());
		
	}

	// CPA bereits in original Test enthalten
//	@Test
//	public void AChangeUseCPA() {
//		System.out.println("\t\t\tCPA Essential");
//		CPATester tester = new CPATester(path, new String[] { change }, new String[] { use });
//		tester.print();
//		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
//		tester.ready();
//		
//		System.out.println("\t\t\tCPA");
//		tester = new CPATester(path, false, new String[] { change }, new String[] { use });
//		tester.print();
//		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
//		tester.ready();
//	}

	
	@Test
	public void extendedBChangeUseNAtomicTest(){
		final String PATH = "testData/jevsTests/attribute/";
		final String henshinFileName = "attributeRules.henshin";

		Rule firstRule = null;
		Rule secondRule = null;

			HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
			Module module = resourceSet.getModule(henshinFileName, false);

			for (Unit unit : module.getUnits()) {
				if (unit.getName().equals(change))
					firstRule = (Rule) unit;
				if (unit.getName().equals(useN))
					secondRule = (Rule) unit;
			}
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(firstRule, secondRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		assertEquals(1, computedConflictAtoms.size());
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(1, allMinimalConflictReasons.size());
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : allMinimalConflictReasons){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		
		MinimalConflictReason mcr = minimalConflictReasons.iterator().next();
		Assert.assertEquals(1, mcr.getGraph().getNodes().size());
		org.eclipse.emf.henshin.model.Node mcrNode = mcr.getGraph().getNodes().get(0);
		String mcrNodeName = mcrNode.getName();
		Assert.assertEquals("1_4", mcrNodeName);

		Assert.assertEquals(1, mcrNode.getAttributes().size());
		
		Attribute attribute = mcrNode.getAttributes().get(0);
		Assert.assertEquals("Number", attribute.getType().getName());
		Assert.assertEquals("0", attribute.getValue());
		
		Set<InitialReason> computeInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(1, computeInitialReason.size());
	}

	// CPA bereits in original Test enthalten
//	@Test
//	public void BChangeUseNCPA() {
//		System.out.println("\t\t\tCPA Essential");
//		CPATester tester = new CPATester(path, new String[] { change }, new String[] { useN });
//		tester.print();
//		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
//		tester.ready();
//		
//		System.out.println("\t\t\tCPA");
//		tester = new CPATester(path, false, new String[] { change }, new String[] { useN });
//		tester.print();
//		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
//		tester.ready();
//	}

	
	@Test
	public void extendedCChangeNUseAtomicTest(){
		final String PATH = "testData/jevsTests/attribute/";
		final String henshinFileName = "attributeRules.henshin";

		Rule firstRule = null;
		Rule secondRule = null;

			HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
			Module module = resourceSet.getModule(henshinFileName, false);

			for (Unit unit : module.getUnits()) {
				if (unit.getName().equals(changeN))
					firstRule = (Rule) unit;
				if (unit.getName().equals(use))
					secondRule = (Rule) unit;
			}
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(firstRule, secondRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		assertEquals(1, computedConflictAtoms.size());
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(1, allMinimalConflictReasons.size());
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : allMinimalConflictReasons){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		
		MinimalConflictReason mcr = minimalConflictReasons.iterator().next();
		Assert.assertEquals(1, mcr.getGraph().getNodes().size());
		org.eclipse.emf.henshin.model.Node mcrNode = mcr.getGraph().getNodes().get(0);
		String mcrNodeName = mcrNode.getName();
		Assert.assertEquals("3_2", mcrNodeName);

		Assert.assertEquals(1, mcrNode.getAttributes().size());
		
		Attribute attribute = mcrNode.getAttributes().get(0);
		Assert.assertEquals("Number", attribute.getType().getName());
		Assert.assertEquals("0", attribute.getValue());
		
		Set<InitialReason> computeInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(1, computeInitialReason.size());
	}

	// CPA bereits in original Test enthalten
//	@Test
//	public void CChangeNUseCPA() {
//		System.out.println("\t\t\tCPA Essential");
//		CPATester tester = new CPATester(path, new String[] { changeN }, new String[] { use });
//		tester.print();
//		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
//		tester.ready();
//		
//		System.out.println("\t\t\tCPA");
//		tester = new CPATester(path, false, new String[] { changeN }, new String[] { use });
//		tester.print();
//		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
//		tester.ready();
//	}

	
	@Test
	public void extendedDChangeNUseNAtomic(){
		final String PATH = "testData/jevsTests/attribute/";
		final String henshinFileName = "attributeRules.henshin";

		Rule firstRule = null;
		Rule secondRule = null;

			HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
			Module module = resourceSet.getModule(henshinFileName, false);

			for (Unit unit : module.getUnits()) {
				if (unit.getName().equals(changeN))
					firstRule = (Rule) unit;
				if (unit.getName().equals(useN))
					secondRule = (Rule) unit;
			}
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(firstRule, secondRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		assertEquals(1, computedConflictAtoms.size());
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(1, allMinimalConflictReasons.size());
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : allMinimalConflictReasons){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		
		MinimalConflictReason mcr = minimalConflictReasons.iterator().next();
		Assert.assertEquals(1, mcr.getGraph().getNodes().size());
		org.eclipse.emf.henshin.model.Node mcrNode = mcr.getGraph().getNodes().get(0);
		String mcrNodeName = mcrNode.getName();
		Assert.assertEquals("3_4", mcrNodeName);

		Assert.assertEquals(1, mcrNode.getAttributes().size());
		
		Attribute attribute = mcrNode.getAttributes().get(0);
		Assert.assertEquals("Number", attribute.getType().getName());
		Assert.assertEquals("N_N", attribute.getValue());
		
		Set<InitialReason> computeInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(1, computeInitialReason.size());
	}

	// CPA bereits in original Test enthalten
//	@Test
//	public void DChangeNUseNCPA() {
//		System.out.println("\t\t\tCPA Essential");
//		CPATester tester = new CPATester(path, new String[] { changeN }, new String[] { useN });
//		tester.print();
//		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
//		tester.ready();
//		
//		System.out.println("\t\t\tCPA");
//		tester = new CPATester(path, false, new String[] { changeN }, new String[] { useN });
//		tester.print();
//		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
//		tester.ready();
//	}
	

	@Test
	public void extendedG_changeAndConstantFoo_useAndConstantBaa(){
		final String PATH = "testData/jevsTests/attribute/";
		final String henshinFileName = "attributeRules.henshin";

		Rule firstRule = null;
		Rule secondRule = null;

			HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
			Module module = resourceSet.getModule(henshinFileName, false);

			for (Unit unit : module.getUnits()) {
				if (unit.getName().equals(changeAndConstantFoo))
					firstRule = (Rule) unit;
				if (unit.getName().equals(useAndConstantBaa))
					secondRule = (Rule) unit;
			}
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(firstRule, secondRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		assertEquals(0, computedConflictAtoms.size());
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(0, allMinimalConflictReasons.size());
	}

	@Test
	public void G_changeAndConstantFoo_useAndConstantBaa_CPA() {
		System.out.println("\t\t\tCPA Essential");
		CPATester tester = new CPATester(path, new String[] { changeAndConstantFoo }, new String[] { useAndConstantBaa });
		tester.print();
		assertTrue("Critical Pairs are not 0", tester.check(new CP(0)));
		tester.ready();
		
		System.out.println("\t\t\tCPA");
		tester = new CPATester(path, new String[] { changeAndConstantFoo }, new String[] { useAndConstantBaa }, false);
		tester.print();
		assertTrue("Critical Pairs are not 0", tester.check(new CP(0)));
		tester.ready();
	}
	
	@Test
	public void extendedH_change_useOne(){
		final String PATH = "testData/jevsTests/attribute/";
		final String henshinFileName = "attributeRules.henshin";

		Rule firstRule = null;
		Rule secondRule = null;

			HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
			Module module = resourceSet.getModule(henshinFileName, false);

			for (Unit unit : module.getUnits()) {
				if (unit.getName().equals(change))
					firstRule = (Rule) unit;
				if (unit.getName().equals(useOne))
					secondRule = (Rule) unit;
			}
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(firstRule, secondRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		assertEquals(0, computedConflictAtoms.size());
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(0, allMinimalConflictReasons.size());
	}

	@Test
	public void H_change_useOne_CPA() {
		System.out.println("\t\t\tCPA Essential");
		CPATester tester = new CPATester(path, new String[] { change }, new String[] { useOne });
		tester.print();
		assertTrue("Critical Pairs are not 0", tester.check(new CP(0)));
		tester.ready();
		
		System.out.println("\t\t\tCPA");
		tester = new CPATester(path, new String[] { change }, new String[] { useOne }, false);
		tester.print();
		assertTrue("Critical Pairs are not 0", tester.check(new CP(0)));
		tester.ready();
	}
	
	@Test
	public void extendedI_change_useAndConstantBaa(){
		final String PATH = "testData/jevsTests/attribute/";
		final String henshinFileName = "attributeRules.henshin";

		Rule firstRule = null;
		Rule secondRule = null;

			HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
			Module module = resourceSet.getModule(henshinFileName, false);

			for (Unit unit : module.getUnits()) {
				if (unit.getName().equals(change))
					firstRule = (Rule) unit;
				if (unit.getName().equals(useAndConstantBaa))
					secondRule = (Rule) unit;
			}
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(firstRule, secondRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		assertEquals(1, computedConflictAtoms.size());
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(1, allMinimalConflictReasons.size());
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : allMinimalConflictReasons){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		
		MinimalConflictReason mcr = minimalConflictReasons.iterator().next();
		Assert.assertEquals(1, mcr.getGraph().getNodes().size());
		org.eclipse.emf.henshin.model.Node mcrNode = mcr.getGraph().getNodes().get(0);
		String mcrNodeName = mcrNode.getName();
		Assert.assertEquals("1_6", mcrNodeName);

		Assert.assertEquals(2, mcrNode.getAttributes().size());
		
		boolean numberAttrFound = false;
		boolean nameAttrFound = false;
		for(Attribute attr : mcrNode.getAttributes()){
			if(attr.getType().getName().equals("Number") && attr.getValue().equals("0"))
				numberAttrFound = true;

			if(attr.getType().getName().equals("Name") && attr.getValue().equals("\"baa\""))
				nameAttrFound = true;
		}
		Attribute attribute = mcrNode.getAttributes().get(0);
		Assert.assertEquals(true, numberAttrFound);
		Assert.assertEquals(true, nameAttrFound);
		
		Set<InitialReason> computeInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(1, computeInitialReason.size());
	}

	@Test
	public void I_change_useAndConstantBaa_CPA() {
		System.out.println("\t\t\tCPA Essential");
		CPATester tester = new CPATester(path, new String[] { change }, new String[] { useAndConstantBaa });
		tester.print();
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();
		
		System.out.println("\t\t\tCPA");
		tester = new CPATester(path, new String[] { change }, new String[] { useAndConstantBaa }, false);
		tester.print();
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();
	}

	@Test
	public void extendedJ_changeAndConstantFoo_use(){
		final String PATH = "testData/jevsTests/attribute/";
		final String henshinFileName = "attributeRules.henshin";

		Rule firstRule = null;
		Rule secondRule = null;

			HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
			Module module = resourceSet.getModule(henshinFileName, false);

			for (Unit unit : module.getUnits()) {
				if (unit.getName().equals(changeAndConstantFoo))
					firstRule = (Rule) unit;
				if (unit.getName().equals(use))
					secondRule = (Rule) unit;
			}
		
		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(firstRule, secondRule);
		List<ConflictAtom> computedConflictAtoms = atomicCoreCPA.computeConflictAtoms();
		assertEquals(1, computedConflictAtoms.size());
		
		Set<Span> allMinimalConflictReasons = new HashSet<Span>();
		for(ConflictAtom conflictAtom : computedConflictAtoms){
			Set<MinimalConflictReason> reasons = conflictAtom.getMinimalConflictReasons();
			Assert.assertEquals(1, reasons.size());
			allMinimalConflictReasons.addAll(reasons);
		}
		Assert.assertEquals(1, allMinimalConflictReasons.size());
		
		Set<MinimalConflictReason> minimalConflictReasons = new HashSet<MinimalConflictReason>();
		for(Span minimalConflictReason : allMinimalConflictReasons){
			minimalConflictReasons.add(new MinimalConflictReason(minimalConflictReason));
		}
		
		MinimalConflictReason mcr = minimalConflictReasons.iterator().next();
		Assert.assertEquals(1, mcr.getGraph().getNodes().size());
		org.eclipse.emf.henshin.model.Node mcrNode = mcr.getGraph().getNodes().get(0);
		String mcrNodeName = mcrNode.getName();
		Assert.assertEquals("5_2", mcrNodeName);

		Assert.assertEquals(2, mcrNode.getAttributes().size());
		
		boolean numberAttrFound = false;
		boolean nameAttrFound = false;
		for(Attribute attr : mcrNode.getAttributes()){
			if(attr.getType().getName().equals("Number") && attr.getValue().equals("0"))
				numberAttrFound = true;

			if(attr.getType().getName().equals("Name") && attr.getValue().equals("\"foo\""))
				nameAttrFound = true;
		}
		Attribute attribute = mcrNode.getAttributes().get(0);
		Assert.assertEquals(true, numberAttrFound);
		Assert.assertEquals(true, nameAttrFound);
		
		Set<InitialReason> computeInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(1, computeInitialReason.size());
	}

	@Test
	public void J_changeAndConstantFoo_use_CPA() {
		System.out.println("\t\t\tCPA Essential");
		CPATester tester = new CPATester(path, new String[] { changeAndConstantFoo }, new String[] { use });
		tester.print();
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();
		
		System.out.println("\t\t\tCPA");
		tester = new CPATester(path, new String[] { changeAndConstantFoo }, new String[] { use }, false);
		tester.print();
		assertTrue("Critical Pairs are not 1", tester.check(new CP(1)));
		tester.ready();
	}
}
