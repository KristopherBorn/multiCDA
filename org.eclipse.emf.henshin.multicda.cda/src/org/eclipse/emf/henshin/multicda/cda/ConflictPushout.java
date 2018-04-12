/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;
import org.eclipse.emf.henshin.model.impl.NodePair;

/**
 * @author vincentcuccu 02.04.2018
 */
public class ConflictPushout {
	HenshinFactoryImpl henshinFactoryImpl = new HenshinFactoryImpl();
	private Set<Mapping> mappingsFromSpan1;
	private Set<Mapping> mappingsFromSpan2;
	private Set<Mapping> mappingsToSpan1;
	private Set<Mapping> mappingsToSpan2;
	private Graph graph;
	private Span span1;
	private Span sap;
	private Span span2;

	/**
	 * @param graph1
	 * @param sap
	 * @param graph2
	 */
	public ConflictPushout(Span span1, Span sap, Span span2) {

		this.setSpan1(span1);
		this.setSap(sap);
		this.setSpan2(span2);
		Graph s1Graph = span1.getGraph();
		EList<Node> s1Nodes = s1Graph.getNodes();
		Graph s2Graph = span2.getGraph();
		EList<Node> s2Nodes = s2Graph.getNodes();
		Graph s = henshinFactoryImpl.createGraph("S");

		setMappingsFromSpan1(new HashSet<Mapping>());
		setMappingsFromSpan2(new HashSet<Mapping>());
		setMappingsToSpan1(new HashSet<Mapping>());
		setMappingsToSpan2(new HashSet<Mapping>());

		Set<Node> nodes1 = new HashSet<Node>();
		Set<Node> cheked1 = new HashSet<Node>();
		Set<Node> cheked2 = new HashSet<Node>();
		span1.getGraph().getNodes().forEach(n -> nodes1.add(n));
		Set<Node> nodes2 = new HashSet<Node>();
		span2.getGraph().getNodes().forEach(n -> nodes2.add(n));
		Set<Node> nodesFromS1 = new HashSet<Node>();
		Set<Node> nodesFromS2 = new HashSet<Node>();
		EList<Node> snodes = s.getNodes();
		setGraph(sap.getGraph());
		EList<Node> nodes = getGraph().getNodes();
		EList<Edge> sedges = s.getEdges();

		for (Node node : nodes) {
			NodePair pair = (NodePair) node;
			System.out.println(node);
			Node pair1 = pair.getNode1();
			System.out.println(pair1);
			Node pair2 = pair.getNode2();
			System.out.println(pair2);

			nodesFromS1.add(pair1);
			nodesFromS2.add(pair2);

			for (Node node1 : s1Nodes) {

				if (checkOriginNodes(node1, pair1)) {
					if (!cheked1.contains(node1)) {
						cheked1.add(node1);
					}
					Mapping createMapping = henshinFactoryImpl.createMapping(node1, node);
					getMappingsFromSpan1().add(createMapping);
					Mapping createMapping2 = henshinFactoryImpl.createMapping(node, node1);
					getMappingsToSpan1().add(createMapping2);
				}
			}

			for (Node node2 : s2Nodes) {
				if (checkOriginNodes(node2, pair2)) {
					if (!cheked2.contains(node2)) {
						cheked2.add(node2);
					}
					Mapping createMapping = henshinFactoryImpl.createMapping(node2, node);
					getMappingsFromSpan2().add(createMapping);
					Mapping createMapping2 = henshinFactoryImpl.createMapping(node, node2);
					getMappingsToSpan2().add(createMapping2);
				}
			}

		}

		for (Node node : nodes1) {
			if (!cheked1.contains(node)) {
				Node c = copyNode(node);
				getMappingsFromSpan1().add(henshinFactoryImpl.createMapping(node, c));
				nodes.add(c);

			}
		}

		for (Node node : nodes2) {
			if (!cheked2.contains(node)) {
				Node c = copyNode(node);
				getMappingsFromSpan2().add(henshinFactoryImpl.createMapping(node, c));
				nodes.add(c);

			}
		} //TODO VC: Wie geht man hier mit den Kanten von Knoten auf PaarKnoten um?

	}

	/**
	 * @param node
	 * @return
	 */
	private Node copyNode(Node node) {
		Graph graph = node.getGraph();
		EClass type = node.getType();
		String name = node.getName();

		return henshinFactoryImpl.createNode(graph, type, name);

	}

	/**
	 * @param originNode
	 * @param origin
	 * @return
	 */
	private boolean checkOriginNodes(Node originNode, Node originNode2) {
		String[] revert = originNode.getName().split("_");
		String[] revertMapping = originNode2.getName().split("_");
		return (revert[0].equals(revertMapping[0]) && revert[1].equals(revertMapping[1])
				|| revert[0].equals(revertMapping[1]) && revert[1].equals(revertMapping[0]))
				&& (originNode.getType().equals(originNode2.getType()));
	}

	public Set<Mapping> getMappingsFromSpan1() {
		return mappingsFromSpan1;
	}

	public void setMappingsFromSpan1(Set<Mapping> mappingsFromSpan1) {
		this.mappingsFromSpan1 = mappingsFromSpan1;
	}

	public Set<Mapping> getMappingsFromSpan2() {
		return mappingsFromSpan2;
	}

	public void setMappingsFromSpan2(Set<Mapping> mappingsFromSpan2) {
		this.mappingsFromSpan2 = mappingsFromSpan2;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public Span getSpan1() {
		return span1;
	}

	public void setSpan1(Span span1) {
		this.span1 = span1;
	}

	public Span getSap() {
		return sap;
	}

	public void setSap(Span sap) {
		this.sap = sap;
	}

	public Span getSpan2() {
		return span2;
	}

	public void setSpan2(Span span2) {
		this.span2 = span2;
	}

	public Set<Mapping> getMappingsToSpan1() {
		return mappingsToSpan1;
	}

	public void setMappingsToSpan1(Set<Mapping> mappingsToSpan1) {
		this.mappingsToSpan1 = mappingsToSpan1;
	}

	public Set<Mapping> getMappingsToSpan2() {
		return mappingsToSpan2;
	}

	public void setMappingsToSpan2(Set<Mapping> mappingsToSpan2) {
		this.mappingsToSpan2 = mappingsToSpan2;
	}

	public Node getMappingIntoSpan1(Node origin) {

		for (Mapping mapping : mappingsToSpan1) {
			if (mapping.getOrigin() == origin) {
				return mapping.getImage();
			}
		}

		return null;

	}
	
	public Node getMappingIntoSpan2(Node origin) {

		for (Mapping mapping : mappingsToSpan2) {
			if (mapping.getOrigin() == origin) {
				return mapping.getImage();
			}
		}

		return null;

	}

}
