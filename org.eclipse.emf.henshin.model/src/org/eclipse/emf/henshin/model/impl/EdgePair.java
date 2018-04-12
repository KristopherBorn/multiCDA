/**
 * 
 */
package org.eclipse.emf.henshin.model.impl;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.Node;

/**
 * @author vincentcuccu
 * 24.03.2018
 */
public class EdgePair extends EdgeImpl implements GraphElement {

	private Edge edge2;
	private Edge edge1;
	private NodePair source;
	private NodePair target;

	/**
	 * 
	 */
	public EdgePair() {
	}

	/**
	 * @param source
	 * @param target
	 * @param type
	 */
	public EdgePair(Node source, Node target, EReference type) {
		super(source, target, type);
	}

	/**
	 * @param x
	 * @param y
	 */
	public EdgePair(Edge x, Edge y) {
		this.setEdge1(x);
		this.setEdge2(y);
	}

	/**
	 * @return
	 */
	public Edge getEdge2() {
		return edge2;
	}

	/**
	 * @param edge2
	 */
	public void setEdge2(Edge edge2) {
		this.edge2 = edge2;
	}

	/**
	 * @return
	 */
	public Edge getEdge1() {
		return edge1;
	}

	/**
	 * @param edge1
	 */
	public void setEdge1(Edge edge1) {
		this.edge1 = edge1;
	}
	

	public NodePair getSource() {
		return source;
	}
	
	public NodePair getTarget() {
		return target;
	}

	/**
	 * @param source
	 */
	public void setSource(NodePair source) {
		this.source = source;
	}

	/**
	 * @param target
	 */
	public void setTarget(NodePair target) {
		this.target = target;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.henshin.model.impl.EdgeImpl#toString()
	 */
	@Override
	public String toString() {
		if (type!=null && type.getName()==null) {
			EcoreUtil.resolveAll(this);
		}
		String src1Name = (edge1.getSource()!=null) ? ((edge1.getSource().getName()!=null) ? edge1.getSource().getName() : "_") : "?";
		String trg1Name = (edge1.getTarget()!=null) ? ((edge1.getTarget().getName()!=null) ? edge1.getTarget().getName() : "_") : "?";
		String edge1Type = ("(" + ((edge1.getType()!=null) ? edge1.getType().getName() : "?") + ")");
		String src2Name = (edge2.getSource()!=null) ? ((edge2.getSource().getName()!=null) ? edge2.getSource().getName() : "_") : "?";
		String trg2Name = (edge2.getTarget()!=null) ? ((edge2.getTarget().getName()!=null) ? edge2.getTarget().getName() : "_") : "?";
		String edge2Type = ("(" + ((edge2.getType()!=null) ? edge2.getType().getName() : "?") + ")");
		return "Edge (" + edge1Type + "," + edge2Type + ") (" + src1Name +","+ src2Name + ") -> (" + trg1Name +","+ trg2Name +")";
	}

}
