/**
 * 
 */
package org.eclipse.emf.henshin.model.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.Node;

/**
 * @author vincentcuccu
 * 24.03.2018
 */
public class NodePair extends NodeImpl implements GraphElement {

	private Node node1;
	private Node node2;

	/**
	 * 
	 */
	public NodePair() {
		
	}

	/**
	 * @param name
	 * @param type
	 */
	public NodePair(String name, EClass type) {
		super(name, type);
		
	}
	
	public NodePair(Node node1, Node node2){
		this.setNode1(node1);
		this.setNode2(node2);
	}

	public Node getNode1() {
		return node1;
	}

	public void setNode1(Node node1) {
		this.node1 = node1;
	}

	public Node getNode2() {
		return node2;
	}

	public void setNode2(Node node2) {
		this.node2 = node2;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.henshin.model.impl.NodeImpl#toString()
	 */
	@Override
	public String toString() {
		if (type != null && type.getName() == null) {
			EcoreUtil.resolveAll(this);
		}
		String node1Name = (node1.getName() != null) ? node1.getName() : "";
		String node2Name = (node2.getName() != null) ? node2.getName() : "";
		String type1Name = (node1.getType() != null) ? ":" + node1.getType().getName() : "";
		String type2Name = (node2.getType() != null) ? ":" + node2.getType().getName() : "";
		return ("Node " + "(" + node1Name + type1Name + ") <> (" + node2Name + type2Name + ")").trim();
	}
	

}
