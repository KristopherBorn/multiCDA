/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda;

import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Rule;

/**
 * @author vincentcuccu
 * 23.02.2018
 */
public class DDSet {
	
	private Rule r1;
	private Rule r2;
	private Graph s1;
	public Rule getR1() {
		return r1;
	}
	public void setR1(Rule r1) {
		this.r1 = r1;
	}
	public Rule getR2() {
		return r2;
	}
	public void setR2(Rule r2) {
		this.r2 = r2;
	}
	public Graph getS1() {
		return s1;
	}
	public void setS1(Graph s1) {
		this.s1 = s1;
	}
	
	public boolean isEmpty() {
		
		
		
		return true;
		
	}
	
	

}
