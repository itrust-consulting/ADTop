/**
 * 
 */
package lu.itrust.adtop.exception;

/**
 * @author eomar
 *
 */
public class ADException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code;

	private Object[] parameters;

	/**
	 * 
	 */
	public ADException() {
	}

	/**
	 * @param cause
	 */
	public ADException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param code
	 * @param parameters
	 */
	public ADException(String code, String message, Object... parameters) {
		super(message);
		this.code = code;
		if (parameters.length == 1 && parameters[0] instanceof Throwable)
			this.initCause((Throwable) parameters[0]);
		else
			this.parameters = parameters;
	}

	/**
	 * @param message
	 * @param cause
	 * @param code
	 * @param parameters
	 */
	public ADException(String code, String message, Throwable cause, Object... parameters) {
		super(message, cause);
		this.code = code;
		this.parameters = parameters;
	}

	/**
	 * @param code
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 * @param parameters
	 */
	public ADException(String code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... parameters) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.code = code;
		this.parameters = parameters;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the parameters
	 */
	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

}
