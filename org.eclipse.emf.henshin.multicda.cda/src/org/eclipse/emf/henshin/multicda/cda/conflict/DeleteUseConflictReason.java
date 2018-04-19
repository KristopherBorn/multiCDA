/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.conflict;

import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.dependency.CreateUseDependencyReason.CreateDeleteDependencyReason;

/**
 * @author vincentcuccu 23.02.2018
 */
public abstract class DeleteUseConflictReason extends ConflictReason {
	public final String TAG;
	public final String NAME;

	/**
	 * @param span
	 */
	public DeleteUseConflictReason(Span deleteUseConflictReason) {
		this(deleteUseConflictReason, "DUCR", "Delelete use conflict reason");
	}

	protected DeleteUseConflictReason(Span s1, String tag, String name) {
		super(s1);
		NAME = name;
		TAG = tag;
	}

	/**
	 * 
	 */
	public final void print() {
		System.out.println(TAG + ": " + this.getGraph().getEdges() + " |\t" + this.getGraph().getNodes());
	}

	public static class DeleteReadConflictReason extends DeleteUseConflictReason {

		/**
		 * @param s1
		 */
		public DeleteReadConflictReason(Span s1) {
			super(s1, "DRCR", "Delete read conflict reason");
		}

	}

	public static class DeleteDeleteConflictReason extends DeleteUseConflictReason {
		private Span s2;

		/**
		 * @param s1
		 */
		public DeleteDeleteConflictReason(Span s1, Span s2) {
			super(s1, "DDCR", "Delete delete conflict reason");
			this.s2 = s2;
		}
		/**
		 * @param s1
		 */
		public DeleteDeleteConflictReason(CreateDeleteDependencyReason dddr) {
			this(dddr, dddr.getS2());
		}
		
		/**
		 * @return the s2
		 */
		public Span getS2() {
			return s2;
		}
	}
}
