package de.fhg.iese.cl.cm;

/**
 * @author anastaso
 *
 */
public class CMErrorMessage {
	
    /**
     * 
     */
    private String message;
	
	/**
	 * @author anastaso
	 *
	 */
	public enum CMErrorType { TYPE_ERROR, TYPE_WARNING, UNKNOWN_ERROR_MESSAGE };
	
	/**
	 * 
	 */
	private CMErrorType type;

	/**
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @return
	 */
	public CMErrorType getType() {
		return type;
	}

	/**
	 * @param message
	 * @param type
	 */
	public CMErrorMessage(String message, CMErrorType type) {
		super();
		this.message = message;
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[CM Error Message] type="+type+" message=" +message;
	}

}
