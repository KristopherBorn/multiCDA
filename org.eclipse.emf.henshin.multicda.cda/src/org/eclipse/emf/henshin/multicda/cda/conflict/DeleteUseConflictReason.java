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
	private final String TAG;

	/**
	 * @param span
	 */
	public DeleteUseConflictReason(Span deleteUseConflictReason) {
		this(deleteUseConflictReason, "DUCR");
	}

	protected DeleteUseConflictReason(Span s1, String tag) {
		super(s1);
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
			super(s1, "DRCR");
		}

	}

	public static class DeleteDeleteConflictReason extends DeleteUseConflictReason {
		private Span s2;

		/**
		 * @param s1
		 */
		public DeleteDeleteConflictReason(Span s1, Span s2) {
			super(s1, "DDCR");
			this.s2 = s2;
		}
		/**
		 * @param s1
		 */
		public DeleteDeleteConflictReason(CreateDeleteDependencyReason dddr) {
			super(dddr, "DDCR");
			this.s2 = dddr.getS2();
		}
		
		/**
		 * @return the s2
		 */
		public Span getS2() {
			return s2;
		}
	}
}
