package org.eclipse.emf.henshin.cpa.atomic.tester;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.emf.henshin.cpa.atomic.tester.Condition.Conditions;

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

	protected static String getContent(Object... objects) {
		return getContent(false, objects);
	}

	private static String getContent(boolean clazz, Object... objects) {
		String result = "";
		for (Object o : objects)
			result += ", "
					+ (clazz ? o instanceof Class ? ((Class) o).getSimpleName() : o.getClass().getSimpleName() : o);
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
		return NAME + ": " + (failed ? executed ? "Execution failed " : "Initialisation failed " : "") + (method == null
				? ""
				: method.getName() + getContent(parameters)
						+ (result == null ? "" : "\n\treturns " + result.getClass().getSimpleName() + " : " + result));
	}

	public void print() {
		System.out.println(NAME + ": " + this);
	}

	protected String print(String message, boolean... out) {
		if (out.length == 0 || out.length != 0 && out[0])
			System.out.println(NAME + ": " + message);
		return NAME + ": " + message;
	}

	public boolean check(Conditions conditions) {
		return check(conditions.getConditions());
	}

	public boolean check(Condition... conditions) {
		return false;
	}

	public void ready() {
		print("Ready");
	}
}
