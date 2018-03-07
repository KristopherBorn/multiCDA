/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cpa.persist;

import org.eclipse.emf.common.util.URI;

/**
 * @author Jevgenij Huebert
 *
 */
public class CriticalPairNode extends SpanNode {



	/**
	 * @param numberedNameOfCPKind
	 * @param firstRuleURI
	 * @param secondRuleURI
	 * @param minimalModelURI
	 * @param criticalPairURI
	 */
	public CriticalPairNode(String numberedNameOfCPKind, URI firstRuleURI, URI secondRuleURI, URI minimalModelURI,
			URI criticalPairURI) {
		super(numberedNameOfCPKind, firstRuleURI, secondRuleURI, criticalPairURI);
		this.minimalModelURI = minimalModelURI;
	}



}
