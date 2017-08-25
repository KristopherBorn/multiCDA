package de.bigtrafo.benchmark.util;

import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;

import de.bigtrafo.measurement.compactness.RuleSetMetricsCalculator;
import metrics.RuleMetrics;
import metrics.RuleSetMetrics;

public class MaintainabilityBenchmarkUtil {

	public static void runMaintainabilityBenchmark(Module module) {
	    Set<Rule> rules = module.getUnits().stream().filter(p -> p instanceof Rule)
	            .map(p -> (Rule) p).collect(Collectors.toSet());
		RuleSetMetricsCalculator c = new RuleSetMetricsCalculator();
		c.calculcate(rules);
		RuleSetMetrics ruleSetMetrics = c.getRuleSetMetrics();
		RuleMetrics largestRuleMetrics = c.getLargestRuleMetrics();
		
		System.out.println("Metrics for the overall rule set: (Nodes / Edges / Attributes)");
		System.out.println(ruleSetMetrics.createPresentationString());
		System.out.println();
		System.out.println("Metrics for the largest rule in the rule set: (Nodes / Edges / Attributes)");
		System.out.println(largestRuleMetrics.createPresentationString());
	}

}
