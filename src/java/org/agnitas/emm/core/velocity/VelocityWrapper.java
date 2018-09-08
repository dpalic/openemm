package org.agnitas.emm.core.velocity;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Interface hiding all the Velocity boilerplate code.
 * 
 * @author md
 *
 */
public interface VelocityWrapper {

	/**
	 * Evaluates a script. <b>This method is deprecated.</b> Use @link{VelocityWrapper#evaluate(Map, String, Writer, int, int, int)} instead.
	 * This is kept intentionally, because we do not get the form or action ID in some cases. 
	 * 
	 * @param params identifier and its values made available to Velocity
	 * @param template the Velocity script
	 * @param writer writer for the result
	 * 
	 * @return {@link VelocityResult} containing status of the executing
	 * 
	 * @throws IOException on errors writing to {@code writer}
	 * 
	 * @see #evaluate(Map, String, Writer, int, int)
	 */
	@Deprecated
	public VelocityResult evaluate( Map params, String template, Writer writer) throws IOException;

	/**
	 * Evaluates a script. The given IDs are used to create a log tag in case of writing a log message. 
	 * For company ID the value given to constructor is used.
	 * 
	 * @param params identifier and its values made available to Velocity
	 * @param template the Velocity script
	 * @param writer writer for the result
	 * @param formId form ID that contains the script
	 * @param actionId action ID that contains the script
	 * 
	 * @return {@link VelocityResult} containing status of the executing
	 * 
	 * @throws IOException on errors writing to {@code writer}
	 */
	public VelocityResult evaluate( Map params, String template, Writer writer, int formId, int actionId) throws IOException;
	
	/**
	 * Evaluates a script with logging.
	 * 
	 * @param params identifier and its values made available to Velocity
	 * @param template the Velocity script
	 * @param writer writer for the result
	 * @param logTag log tag
	 * 
	 * @return {@link VelocityResult} containing status of the executing
	 * 
	 * @throws IOException on errors writing to {@code writer}
	 */
	public VelocityResult evaluate( Map params, String template, Writer writer, String logTag) throws IOException;

	/**
	 * Returns the company ID in which the Velocity engine is running.
	 * 
	 * @return company ID
	 */
	public int getCompanyId();
}
