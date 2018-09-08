package org.agnitas.emm.core.target.exception;

public class TargetGroupException extends Exception {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3855186440914086870L;

	public TargetGroupException() {
		super();
	}

	public TargetGroupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TargetGroupException(String message, Throwable cause) {
		super(message, cause);
	}

	public TargetGroupException(String message) {
		super(message);
	}

	public TargetGroupException(Throwable cause) {
		super(cause);
	}

}
