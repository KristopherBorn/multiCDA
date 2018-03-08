package de.bigtrafo.measurement.compactness;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import metrics.RuleMetrics;
import metrics.RuleSetMetrics;
import metrics.impl.MetricsFactoryImpl;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;

public class RuleSetMetricsCalculator {
	private RuleSetMetrics ruleSetMetrics;
	private RuleMetrics largestRuleMetrics;

	public void calculcate(Collection<Rule> ruleSet) {
		RuleSetMetrics result = MetricsFactoryImpl.eINSTANCE
				.createRuleSetMetrics();
		result.getRuleSet().addAll(ruleSet);

		computeRuleMetrics(ruleSet, result);
		computeTotalMetrics(result);
		ruleSetMetrics = result;
	}
	
	public RuleMetrics computeMetrics(Rule rule) {
		RuleMetrics metrics = MetricsFactoryImpl.eINSTANCE
				.createRuleMetrics();
		metrics.setNumberOfNodes(countNodes(rule));
		metrics.setNumberOfEdges(countEdges(rule));
		metrics.setNumberOfAttributes(countAttributes(rule));
		return metrics;
	}

	private void computeTotalMetrics(RuleSetMetrics result) {
		for (RuleMetrics m : result.getRuleMetrics()) {
			result.setTotalNumberOfNodes(result.getTotalNumberOfNodes()
					+ m.getNumberOfNodes());
			result.setTotalNumberOfEdges(result.getTotalNumberOfEdges()
					+ m.getNumberOfEdges());
			result.setTotalNumberOfAttributes(result
					.getTotalNumberOfAttributes() + m.getNumberOfAttributes());
		}
		result.setNumberOfRules(result.getRuleSet().size());
	}

	private void computeRuleMetrics(Collection<Rule> ruleSet,
			RuleSetMetrics result) {
		for (Rule rule : ruleSet) {
			RuleMetrics metrics = computeMetrics(rule);
			result.getRuleMetrics().add(metrics);
			
			if (largestRuleMetrics == null) {
				largestRuleMetrics = metrics;
			} else if ( isGreaterThan(metrics,largestRuleMetrics)) {
				largestRuleMetrics = metrics;
			}
		}
	}

	private boolean isGreaterThan(RuleMetrics metrics, RuleMetrics largestRuleMetrics) {
		return metrics.getNumberOfNodes()+metrics.getNumberOfEdges()>
		largestRuleMetrics.getNumberOfNodes() + largestRuleMetrics.getNumberOfEdges();
	}

	private int countNodes(Rule rule) {
		int i = 0;
		Collection<GraphElement> containedElements = getContainedElements(rule);
		for (GraphElement e : containedElements)
			if (e instanceof Node)
				i++;
		return i;
	}

	private int countEdges(Rule rule) {
		int i = 0;
		Collection<GraphElement> containedElements = getContainedElements(rule);
		for (GraphElement e : containedElements)
			if (e instanceof Edge)
				i++;
		return i;
	}

	private int countAttributes(Rule rule) {
		int i = 0;
		Collection<GraphElement> containedElements = getContainedElements(rule);
		for (GraphElement e : containedElements)
			if (e instanceof Attribute)
				i++;
		return i;
	}


	public Collection<Attribute> getAttributes(Graph graph) {
		Collection<Attribute> result = new HashSet<Attribute>();
		for (Node n : graph.getNodes()) {
			result.addAll(n.getAttributes());
		}
		return result;
	}

	public Collection<GraphElement> getContainedElements(Rule rule) {
		Collection<GraphElement> result = new HashSet<GraphElement>();
		TreeIterator<EObject> it = rule.eAllContents();
		while (it.hasNext()) {
			EObject o = it.next();
			if (o instanceof GraphElement) {
				result.add((GraphElement) o);
			}
		}
		return result;
	}


	public RuleSetMetrics getRuleSetMetrics() {
		return ruleSetMetrics;
	}

	public RuleMetrics getLargestRuleMetrics() {
		return largestRuleMetrics;
	}
}
