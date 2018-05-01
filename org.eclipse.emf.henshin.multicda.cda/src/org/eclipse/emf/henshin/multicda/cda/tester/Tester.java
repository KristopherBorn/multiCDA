package org.eclipse.emf.henshin.multicda.cda.tester;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.eclipse.emf.henshin.model.ModelElement;
import org.eclipse.emf.henshin.multicda.cda.tester.Condition.Conditions;

/**
 * @author Jevgenij Huebert
 */
public class Tester {
	private Class<?> cl;
	protected String NAME = "Tester";
	private Object instance;
	private Method method;
	private Object[] parameters;
	private Object result;
	private boolean executed;
	private boolean failed;

	public Tester() {
		executed = false;
		failed = false;
	}

	public Tester(Object instance, String methodName, Object... parameters) {
		init(instance, methodName, parameters);
	}

	public Tester init(Object instance, String methodName, Object... parameters) {
		try {
			executed = false;
			failed = false;
			this.cl = instance.getClass();
			this.instance = instance;
			this.parameters = parameters;
			for (Method m : cl.getDeclaredMethods()) {
				if (m.getName().equals(methodName) && m.getParameterCount() == parameters.length) {
					boolean access = m.isAccessible();
					m.setAccessible(true);
					try {
						m.invoke(instance, parameters);
						method = m;
						break;
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					}
					m.setAccessible(access);
				}
			}
			if (method == null) {
				failed = true;
				System.out.println("The Method " + methodName + getContent(true, parameters) + " does not exist");
			}

		} catch (SecurityException e) {
			failed = true;
			System.out.println("Access to method " + methodName + getContent(true, parameters) + "  denied");
		}
		return this;
	}

	public static Object invoke(Object instance, String className, Object... parameters) {
		Tester t = new Tester(instance, className, parameters);
		return t.invoke();
	}

	public static String getContent(Object... objects) {
		return getContent(false, objects);
	}

	public static String getContent(boolean clazz, Object... objects) {
		String result = "";
		for (Object o : objects)
			result += ", " + (clazz ? o instanceof Class ? ((Class<?>) o).getSimpleName() : o.getClass().getSimpleName() : o);
		return "(" + (result.isEmpty() ? "" : result.substring(2)) + ")";
	}

	public Object invoke() {
		executed = true;
		if (method != null) {
			try {
				result = method.invoke(instance, parameters);
				failed = false;
				return result;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| SecurityException e) {
				failed = true;
				if (e instanceof IllegalArgumentException)
					System.out.println("Wrong arguments by Method " + method.getName()
							+ getContent(true, method.getParameterTypes()) + ". Was given " + method.getName()
							+ getContent(true, parameters));
			}
		}

		failed = true;
		return null;
	}

	@Override
	public String toString() {
		return (failed ? executed ? "Execution failed " : "Initialisation failed " : "") + (method == null ? ""
				: method.getName() + getContent(parameters)
						+ (result == null ? "" : "\n\treturns " + result.getClass().getSimpleName() + " : " + result));
	}

	public void print() {
		print(this.toString());
	}

	public String print(String message, boolean... out) {
		if (out.length == 0 || out.length != 0 && out[0])
			System.out.println(NAME + ": " + message);
		return NAME + ": " + message;
	}

	public boolean check(Conditions conditions) {
		return check(conditions.getClass(), conditions.getConditions());
	}

	public boolean check(Condition... conditions) {
		return check(null, conditions);
	}

	public boolean check(Class<?> type, Condition... conditions) {
		return false;
	}

	public void ready() {
		print("Ready");
	}

	protected boolean checkReasons(Set<ModelElement> elements, Object... conditions) {
		String checked = "";
		int index = 0;
		if (elements.size() != conditions.length)
			return false;
		for (ModelElement element : elements) {
			boolean found = false;
			for (Object condition : conditions) {
				if (!(condition instanceof Condition))
					return false;
				Condition c = (Condition) condition;
				if (index >= conditions.length)
					return false;
				if (!checked.contains("::" + condition) && c.proove(element)) {
					found = true;
					index++;
					checked += "::" + condition;
					break;
				}
			}
			if (!found)
				return false;
		}
		boolean result = index == conditions.length;
//		if (PrintFounds && result)
//			print("Found elements: " + elements + "\t\twith conditions: " + getContent(conditions));
		return result;
	}

	public static class Options {
		public final static int DEPENDENCY = 1;
		public final static int ESSENTIAL = 2;
		public final static int PREPARE = 4;
		public final static int NONE_DELETION_SECOND_RULE = 8;
		public final static int PRINT_HEADER = 16;
		public final static int PRINT_RESULT = 32;
		public final static int SILENT = 64;
		public final static int ALL = 127;
		private int state = 0;

		/**
		 * Default is printHeader, printResults and silent on.
		 * 
		 * @param options 1:dependency, 2:essential, 3:prepare, 4:noneDeletionSecondRule, 5:printHeader, 6:printResults, 7:silent
		 */
		public Options(boolean... options) {
			state += options.length >= 1 && options[0] ? +DEPENDENCY : 0;
			state += options.length >= 3 && options[2] ? +PREPARE : 0;
			state += options.length >= 4 && options[3] ? +NONE_DELETION_SECOND_RULE : 0;
			state += (options.length >= 5 && options[4]) || options.length < 5 ? +PRINT_HEADER : 0;
			state += (options.length >= 6 && options[5]) || options.length < 6 ? +PRINT_RESULT : 0;
			state += (options.length >= 7 && options[6]) || options.length < 7 ? +SILENT : 0;
		}

		/**
		 * Default is printHeader, printResults and silent on.
		 * 
		 * @param options 1:dependency, 2:essential, 3:prepare, 4:noneDeletionSecondRule, 5:printHeader, 6:printResults, 7:silent
		 */
		public Options(int flag, int... flags) {
			this();
			for (int i : flags) {
				if (i >= 0 && i <= ALL)
					state |= i;
			}
		}

		public boolean is(int flag) {
			return (state & flag) == flag;
		}

		public Options add(int... flags) {
			for (Integer i : flags) {
				state |= i;
			}
			return this;
		}

		public Options remove(int... flags) {
			for (Integer i : flags) {
				state &= (ALL - i);
			}
			return this;
		}

		public String toCDAString() {
			String result = "Options: " + ((state & DEPENDENCY) == DEPENDENCY ? "Dependency, " : "")
					+ ((state & PREPARE) == PREPARE ? "Prepared, " : "")
					+ ((state & NONE_DELETION_SECOND_RULE) == NONE_DELETION_SECOND_RULE ? "Second rule none deletion, "
							: "")
					+ ((state & PRINT_HEADER) == PRINT_HEADER ? "With header, " : "")
					+ ((state & PRINT_RESULT) == PRINT_RESULT ? "With results, " : "");
			return result.substring(0, result.length() - 2) + ".";
		}

		@Override
		public String toString() {
			String result = "Options: " + ((state & DEPENDENCY) == DEPENDENCY ? "Dependency, " : "")
					+ ((state & ESSENTIAL) == ESSENTIAL ? "Essential, " : "")
					+ ((state & PREPARE) == PREPARE ? "Prepared, " : "")
					+ ((state & NONE_DELETION_SECOND_RULE) == NONE_DELETION_SECOND_RULE ? "Second rule none deletion, "
							: "")
					+ ((state & PRINT_HEADER) == PRINT_HEADER ? "With header, " : "")
					+ ((state & PRINT_RESULT) == PRINT_RESULT ? "With results, " : "")
					+ ((state & SILENT) == SILENT ? "Ignore AGG output, " : "");
			return result.substring(0, result.length() - 2) + ".";
		}
	}
}
