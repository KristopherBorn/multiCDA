package org.eclipse.emf.henshin.cpa.atomic.unitTest.jevTests;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.cpa.atomic.ConflictAnalysis;
import org.eclipse.emf.henshin.cpa.atomic.Span;
import org.eclipse.emf.henshin.cpa.atomic.conflict.ConflictAtom;
import org.eclipse.emf.henshin.cpa.atomic.conflict.InitialConflictReason;
import org.eclipse.emf.henshin.cpa.atomic.conflict.MinimalConflictReason;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VererbungsTestOldStyle {

	private String PATH = "testData/jevsTests/extendTest/";
	private String henshinFileName = "extendRules.henshin";
	private String deleteS = "deleteS";
	private String deleteC = "deleteC";
	private String useS = "useS";
	private String useC = "useC";

	@Test
	public void ASCatomic() {
		Rule firstRule = null;
		Rule secondRule = null;

			HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
			Module module = resourceSet.getModule(henshinFileName, false);

			for (Unit unit : module.getUnits()) {
				if (unit.getName().equals(deleteS))
					firstRule = (Rule) unit;
				if (unit.getName().equals(useC))
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
		
		String mcrNodeTypeName = mcrNode.getType().getName();
		Assert.assertEquals("Child", mcrNodeTypeName);

				
		Set<InitialConflictReason> computeInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(1, computeInitialReason.size());
	}

	@Test
	public void DCSatomic() {
		Rule firstRule = null;
		Rule secondRule = null;

			HenshinResourceSet resourceSet = new HenshinResourceSet(PATH);
			Module module = resourceSet.getModule(henshinFileName, false);

			for (Unit unit : module.getUnits()) {
				if (unit.getName().equals(deleteC))
					firstRule = (Rule) unit;
				if (unit.getName().equals(useS))
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
		
		String mcrNodeTypeName = mcrNode.getType().getName();
		Assert.assertEquals("Child", mcrNodeTypeName);

				
		Set<InitialConflictReason> computeInitialReason = atomicCoreCPA.computeInitialReasons(minimalConflictReasons);
		Assert.assertEquals(1, computeInitialReason.size());
	}

}
