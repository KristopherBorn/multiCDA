package org.eclipse.emf.henshin.multicda.cda.computation;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.MinimalConflictReason;
import org.junit.Before;
import org.junit.Test;

public class ComputeMinConflReasonsTest {

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
	public void refactoringCandidatesDecapsulateEncapsulateTest() {


		AtomCandidateComputation candComp = new AtomCandidateComputation(decapsulateAttributeRule,
				pullUpEncapsulatedAttributeRule);
		List<Span> conflictAtomCandidates = candComp.computeAtomCandidates();

		System.out.println("HALT");

		assertEquals(5, conflictAtomCandidates.size());

		for (Span candidate : conflictAtomCandidates) {
			EList<Node> nodesOfCandidate = candidate.getGraph().getNodes();
			assertEquals(1, nodesOfCandidate.size());
		}

		ConflictAnalysis atomicCoreCPA = new ConflictAnalysis(decapsulateAttributeRule,
				pullUpEncapsulatedAttributeRule);
		Set<MinimalConflictReason> reasons = new HashSet<>();//
		for (Span candidate : conflictAtomCandidates) {
			atomicCoreCPA.computeMinimalConflictReasons(candidate,
					reasons);
		}

		assertEquals(2, reasons.size());

		boolean threeNodeMinReasonFound = false;
		boolean fourNodeMinReasonFound = false;
		// checks that both spans with their graphs have been created and found
		for (Span minReason : reasons) {
			Graph graphOfMinReason = minReason.getGraph();
			EList<Node> nodesOfMinReason = graphOfMinReason.getNodes();

			// checks that the following minReason is contained: Class->Method->Class
			if (nodesOfMinReason.size() == 3) {
				Node methodNode = null;
				for (Node nodeOfMinReason : nodesOfMinReason) {
					if (nodeOfMinReason.getType().getName().equals("Method"))
						methodNode = nodeOfMinReason;
				}
				assertNotNull(methodNode); // "Method" node found in minReason
				EList<Edge> incoming = methodNode.getIncoming();
				assertEquals(1, incoming.size());
				Node methodContainingClass = incoming.get(0).getSource();
				assertEquals("Class", methodContainingClass.getType().getName());

				EList<Edge> outgoing = methodNode.getOutgoing();
				assertEquals(1, outgoing.size());
				Node methodsTypeClass = outgoing.get(0).getTarget();
				assertEquals("Class", methodsTypeClass.getType().getName());

				threeNodeMinReasonFound = true;
			}

			// checks that the following minReason is contained: Class->Method->Parameter->Class
			if (nodesOfMinReason.size() == 4) {
				Node methodNode = null;
				Node prameterNode = null;
				for (Node nodeOfMinReason : nodesOfMinReason) {
					if (nodeOfMinReason.getType().getName().equals("Method"))
						methodNode = nodeOfMinReason;
					if (nodeOfMinReason.getType().getName().equals("Parameter"))
						prameterNode = nodeOfMinReason;
				}
				assertNotNull(methodNode); // "Method" node found in minReason
				assertNotNull(prameterNode); // "Parameter" node found in minReason

				EList<Edge> methodsIncoming = methodNode.getIncoming();
				assertEquals(1, methodsIncoming.size());
				Node methodContainingClass = methodsIncoming.get(0).getSource();
				assertEquals("Class", methodContainingClass.getType().getName());

				EList<Edge> parametersOutgoing = prameterNode.getOutgoing();
				assertEquals(1, parametersOutgoing.size());
				Node methodsTypeClass = parametersOutgoing.get(0).getTarget();
				assertEquals("Class", methodsTypeClass.getType().getName());

				EList<Edge> methodsOutgoing = methodNode.getOutgoing();
				assertEquals(1, methodsOutgoing.size());
				boolean methodToParameter = (methodsOutgoing.get(0).getTarget() == prameterNode);
				assertTrue(methodToParameter);

				fourNodeMinReasonFound = true;
			}

		}

		assertTrue(threeNodeMinReasonFound);
		assertTrue(fourNodeMinReasonFound);
	}
}
