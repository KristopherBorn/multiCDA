/**
 * 
 */
package org.eclipse.emf.henshin.multicda.cda.computation;

/**
 * @author vincentcuccu 15.03.2018
 */
@SuppressWarnings("serial")
public class NotCompatibleException extends Exception {

	/**
	 * 
	 */
	public NotCompatibleException() {

	}

	/**
	 * @param message
	 */
	public NotCompatibleException(String message) {
		super(message);

	}

	/**
	 * @param cause
	 */
	public NotCompatibleException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotCompatibleException(String message, Throwable cause) {
		super(message, cause);
		//
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public NotCompatibleException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		//
	}

}
