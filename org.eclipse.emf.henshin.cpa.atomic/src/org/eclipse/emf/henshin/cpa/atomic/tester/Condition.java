package org.eclipse.emf.henshin.cpa.atomic.tester;

import static org.junit.Assert.assertTrue;

public class Condition {
	protected String[] values;
	protected String name = "Condition";

	public Condition(String... values) {
		this.values = values;
	}

	public boolean proove(Object... elements) {
		if (elements.length != values.length)
			assertTrue("Not the same number of conditions: conditions=" + values + " != elements=" + elements.length,
					values.length == elements.length);
		for (int i = 0; i < values.length; i++)
			if (!elements[i].equals(values[i]))
				return false;
		return true;
	}

	public static class InitialConditions extends Conditions {
		public InitialConditions(Condition... conditions) {
			super(conditions);
		}
	}

	public static class MinimalConditions extends Conditions {
		public MinimalConditions(Condition... conditions) {
			super(conditions);
		}
	}

	public static class CriticalConditions extends Conditions {
		public CriticalConditions(Condition... conditions) {
			super(conditions);
		}
	}
	public static class CriticalRightConditions extends Conditions {
		public CriticalRightConditions(Condition... conditions) {
			super(conditions);
		}
	}

	public static class Conditions {
		private Condition[] conditions;

		public Conditions(Condition... conditions) {
			this.conditions = conditions;
		}

		public Condition[] getConditions() {
			return conditions;
		}

		@Override
		public String toString() {
			String result = "";
			for (Condition condition : conditions)
				result += ", " + condition;
			return getClass().getSimpleName() + ": " + result.substring(2);
		}
	}

	public static class Edge extends Condition {
		public Edge(int a, int b) {
			this(a + "", b + "");
		}

		public Edge(String a, String b) {
			super(a, b);
			name = "Edge";
		}

		@Override
		public boolean proove(Object... elements) {
			for (Object element : elements) {
				if (!(element instanceof org.eclipse.emf.henshin.model.Edge))
					return false;
				if (!((org.eclipse.emf.henshin.model.Edge) element).getSource().getName().equals(values[0])
						|| !((org.eclipse.emf.henshin.model.Edge) element).getTarget().getName().equals(values[1]))
					return false;
			}
			return true;
		}
	}

	public static class Node extends Condition {
		public Node(int a) {
			this(a + "");
		}

		public Node(String a) {
			super(a);
			name = "Node";
		}

		@Override
		public boolean proove(Object... elements) {
			for (Object element : elements) {
				if (!(element instanceof org.eclipse.emf.henshin.model.Node))
					return false;
				if (!((org.eclipse.emf.henshin.model.Node) element).getName().equals(values[0]))
					return false;
			}
			return true;
		}
	}
	
	private static abstract class CriticalSize extends Condition{
		public CriticalSize(int value) {
			this(value + "");
		}

		public CriticalSize(String value) {
			super(value);
		}

		@Override
		public boolean proove(Object... elements) {
			for (Object element : elements) {
				assertTrue(
						"Element type of \"" + element + "\" is not Integer but: " + element.getClass().getSimpleName(),
						element instanceof Integer);
				if (!(element + "").equals(values[0]))
					return false;
			}
			return true;
		}
	}

	public static class MCR extends CriticalSize {
		public MCR(int value) {
			this(value + "");
		}

		public MCR(String value) {
			super(value);
			name = "Minimal Conflict Reasons";
		}
	}

	public static class ICR extends CriticalSize {
		public ICR(int value) {
			this(value +"");
		}

		public ICR(String value) {
			super(value);
			name = "Initial Conflict Reasons";
		}
	}

	public static class CP extends CriticalSize {
		public CP(int value) {
			this(value +"");
		}

		public CP(String value) {
			super(value);
			name = "Critical Pairs";
		}
	}
	
	public static class CR extends CriticalSize {
		public CR(int value)
		{
			this(value +"");
		}

		public CR(String value) {
			super(value);
			name = "Conflict Reason";
		}
	}

	@Override
	public String toString() {
		String result = "";
		for (String string : values)
			result += ", " + string;
		return name + ":" + " (" + result.substring(2) + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Condition && ((Condition) obj).name.equals(name)
				&& ((Condition) obj).values.length == values.length) {
			for (int i = 0; i < values.length; i++)
				if (!values[i].equals(((Condition) obj).values[i]))
					return false;
			return true;
		}
		return false;
	}
}