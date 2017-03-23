package de.fhg.iese.cl.cm;

/**
 * @author anastaso
 *
 */
public class CMException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public CMException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CMException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public CMException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param arg0
	 */
	public CMException(String arg0) {
		super(arg0);
	}

}
