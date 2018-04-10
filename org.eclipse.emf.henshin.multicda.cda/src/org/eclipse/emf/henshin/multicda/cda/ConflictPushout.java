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
	private Graph graph;

	/**
	 * @param graph1
	 * @param sap
	 * @param graph2
	 */
	public ConflictPushout(Span span1, Span sap, Span span2) {

		Graph s1Graph = span1.getGraph();
		EList<Node> s1Nodes = s1Graph.getNodes();
		Graph s2Graph = span2.getGraph();
		EList<Node> s2Nodes = s2Graph.getNodes();
		Graph s = henshinFactoryImpl.createGraph("S");

		mappingsFromSpan1 = new HashSet<Mapping>();
		mappingsFromSpan2 = new HashSet<Mapping>();

		Set<Node> nodes1 = new HashSet<Node>();
		Set<Node> cheked1 = new HashSet<Node>();
		Set<Node> cheked2 = new HashSet<Node>();
		span1.getGraph().getNodes().forEach(n -> nodes1.add(n));
		Set<Node> nodes2 = new HashSet<Node>();
		span2.getGraph().getNodes().forEach(n -> nodes2.add(n));
		Set<Node> nodesFromS1 = new HashSet<Node>();
		Set<Node> nodesFromS2 = new HashSet<Node>();
		EList<Node> snodes = s.getNodes();
		graph = sap.getGraph();
		EList<Node> nodes = graph.getNodes();
		
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
					mappingsFromSpan1.add(createMapping);
				}
			}

			for (Node node2 : s2Nodes) {
				if (checkOriginNodes(node2, pair2)) {
					if (!cheked2.contains(node2)) {
						cheked2.add(node2);
					}
					Mapping createMapping = henshinFactoryImpl.createMapping(node2, node);
					mappingsFromSpan2.add(createMapping);
				}
			}

		}

		for (Node node : nodes1) {
			if (!cheked1.contains(node)){
				Node c = copyNode(node);; //TODO ist heir eine Kopie gut=?
				mappingsFromSpan1.add(henshinFactoryImpl.createMapping(node, c));
				nodes.add(c);
				
			}
		}
		
		for (Node node : nodes2) {
			if (!cheked2.contains(node)){
				Node c = copyNode(node); //TODO ist heir eine Kopie gut=?
				mappingsFromSpan2.add(henshinFactoryImpl.createMapping(node, c));
				nodes.add(c);
				
			}
		}

		System.out.print(graph.getNodes());
		

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

}
