package org.agnitas.emm.core.mediatypes.dao;

/**
 * Exception indicating error during accessing media types.
 * 
 * @author md
 */
public class MediatypesDaoException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = -2271896955498916758L;

	public MediatypesDaoException() {
		// Nothing to do here
	}

	public MediatypesDaoException(String message) {
		super(message);
	}

	public MediatypesDaoException(Throwable cause) {
		super(cause);
	}

	public MediatypesDaoException(String message, Throwable cause) {
		super(message, cause);
	}

}
