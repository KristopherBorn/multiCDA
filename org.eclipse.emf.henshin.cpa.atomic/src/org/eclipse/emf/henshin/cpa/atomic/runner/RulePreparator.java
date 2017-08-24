package org.eclipse.emf.henshin.cpa.atomic.runner;

import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;

public class RulePreparator {

 public static Rule prepareRule(Rule rule) {
  Module module = rule.getModule();
  Rule newRule = HenshinFactoryImpl.eINSTANCE.createRule();
  rule.getLhs().setFormula(null);
  newRule.setLhs(rule.getLhs());
  newRule.setRhs(rule.getRhs());
  newRule.getMappings().addAll(rule.getMappings());
  newRule.setName(rule.getName() + "x");
  
  for (Node node : newRule.getLhs().getNodes()) {
   node.getAttributes().clear();
  }
  for (Node node : newRule.getRhs().getNodes()) {
   node.getAttributes().clear();
  }
  module.getUnits().remove(rule);
  module.getUnits().add(newRule);
  return newRule;
 }
}