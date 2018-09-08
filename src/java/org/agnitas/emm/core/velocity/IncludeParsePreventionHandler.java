package org.agnitas.emm.core.velocity;

import org.apache.log4j.Logger;
import org.apache.velocity.app.event.IncludeEventHandler;

/**
 * Implementation of {@link IncludeEventHandler} to avoid accessing external resources.
 * 
 * @author md
 *
 */
public class IncludeParsePreventionHandler implements IncludeEventHandler {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger( IncludeParsePreventionHandler.class);
	
	@Override
	public String includeEvent(String includeResourcePath, String currentResourcePath, String directiveName) {
		logger.warn( "Script attempts to use Velocity directive '" + directiveName + "' on resource '" + includeResourcePath + "' (" + currentResourcePath + ")");
		
		return null;	// According to Velocity API, null blocks inclusion of references resource (see API of IncludeEventHandler#includeEvent(...))
	}

}
