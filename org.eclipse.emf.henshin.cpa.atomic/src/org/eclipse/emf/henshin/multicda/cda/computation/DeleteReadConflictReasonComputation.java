/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.computation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.Pushout;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.InitialReason;

/**
 * @author vincentcuccu
 *
 */
public class DeleteReadConflictReasonComputation {

	private Rule rule1;
	private Rule rule2;
	private HashSet<Span> checked;

	/**
	 * @param rule1
	 * @param rule2
	 */
	public DeleteReadConflictReasonComputation(Rule rule1, Rule rule2) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		setChecked(new HashSet<Span>());
	}
	

	/**
	 * @return result
	 */
	public Set<DeleteReadConflictReason> computeDRCR() {
		Set<DeleteReadConflictReason> result = new HashSet<DeleteReadConflictReason>();
		Set<InitialReason> candidates = new InitialReasonComputation(rule1, rule2).computeInitialReasons();
		for (Span candidate : candidates) {
			computeDeleteReadConflictReason(candidate, result);
		}
		return result;
	}

	private void computeDeleteReadConflictReason(Span candidate, Set<DeleteReadConflictReason> result) {
		// TODO Auto-generated method stub
		Span s1copy =
		
	}

	// für Compute PO: result = new Pushout(rule1, s1, rule2);

	/**
	 * @return checked
	 */
	public HashSet<Span> getChecked() {
		return checked;
	}


	/**
	 * @param checked
	 */
	public void setChecked(HashSet<Span> checked) {
		this.checked = checked;
	}
}
