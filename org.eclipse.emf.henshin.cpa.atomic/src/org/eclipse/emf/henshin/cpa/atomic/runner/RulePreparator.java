package org.eclipse.emf.henshin.cpa.atomic.runner;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;

public class RulePreparator {

	public static Rule prepareRule(Rule rule) {
		Rule newRule = HenshinFactoryImpl.eINSTANCE.createRule();
		newRule.setLhs(rule.getLhs());
		newRule.setRhs(rule.getRhs());
		newRule.getMappings().addAll(rule.getMappings());
		newRule.setName(rule.getName());
		
		for (Node node : newRule.getLhs().getNodes()) {
			node.getAttributes().clear();
		}
		for (Node node : newRule.getRhs().getNodes()) {
			node.getAttributes().clear();
		}

		return newRule;
	}
}
