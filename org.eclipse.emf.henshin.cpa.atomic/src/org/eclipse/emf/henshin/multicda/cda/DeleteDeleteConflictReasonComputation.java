/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.computation.InitialReasonComputation;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;
import org.eclipse.emf.henshin.multicda.cda.tester.DDSpan;

/**
 * @author vincentcuccu 23.02.2018
 */
public class DeleteDeleteConflictReasonComputation {

	private Rule rule1;
	private Rule rule2;
	private HashSet<Span> checked;

	/**
	 * constructor
	 * 
	 * @param rule1
	 * @param rule2
	 */
	public DeleteDeleteConflictReasonComputation(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		setChecked(new HashSet<Span>());
	}

	/**
	 * 
	 * @return HashSet<Span>
	 */
	public HashSet<Span> getChecked() {
		return checked;
	}

	/**
	 * 
	 * void
	 * 
	 * @param checked
	 */
	public void setChecked(HashSet<Span> checked) {
		this.checked = checked;
	}

	/**
	 * constructs all Initial Reasons as candidates for r1 and r2
	 * 
	 * @return result
	 */
	public Set<DDSpan> computeDeleteDeleteConflictReason() {
		Set<DDSpan> result = new HashSet<DDSpan>();
		Set<InitialReason> initialReasons = new InitialReasonComputation(rule1, rule2).computeInitialReasons();
		for (InitialReason initalReason : initialReasons) {
			computeDeleteDeleteConflictReason(initalReason, result);
		}

		return result;

	}

	/**
	 * @param initalReason
	 * @param result
	 */
	private void computeDeleteDeleteConflictReason(InitialReason initalReason, Set<DDSpan> result) {
		// TODO Vincent hier weiter
		
	}

	
}
