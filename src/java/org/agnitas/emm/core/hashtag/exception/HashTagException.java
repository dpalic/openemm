package org.agnitas.emm.core.hashtag.exception;

/**
 * Exception indicating errors during processing a hash tag.
 * 
 * @author md
 */
public class HashTagException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = 4903080404814558515L;

	
	/**
	 * Instantiates a new hash tag exception.
	 */
	public HashTagException() {
		// Nothing to do here
	}

	/**
	 * Instantiates a new hash tag exception.
	 *
	 * @param message the message
	 */
	public HashTagException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new hash tag exception.
	 *
	 * @param cause the cause
	 */
	public HashTagException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new hash tag exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public HashTagException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new hash tag exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace whether or not the stack trace should be writable
	 */
	public HashTagException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
