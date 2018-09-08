package org.agnitas.emm.core.velocity;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.agnitas.util.AgnUtils;
import org.agnitas.util.EventHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.util.introspection.Uberspect;

/**
 * Wrapper for Velocity hiding all the boilerplate code.
 * 
 * @author md
 *
 */
public class AbstractVelocityWrapper implements VelocityWrapper {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger( AbstractVelocityWrapper.class);

	/** Velocity engine. */
	private final VelocityEngine engine;
	
	/** Company ID in which the script is executed. */
	private final int contextCompanyId;
	
	/**
	 * Creates a new Velocity instance with given Uberspector.
	 * 
	 * @param companyId ID of company, for that the script is executed
	 * @param factory factory to create the Uberspect delegate target
	 * 
	 * @throws Exception on errors initializing Velocity
	 */
	protected AbstractVelocityWrapper( int companyId, UberspectDelegateTargetFactory factory) throws Exception {
		this.engine = createEngine( companyId, factory);
		this.contextCompanyId = companyId;
	}
	
	/**
	 * Creates a new Velocity engine. If an Uberspector class is defined, this class will
	 * be used for tracking the company context.  
	 * 
	 * @param companyId ID of the company for that the scripts are executed
	 * @param factory factory to create the Uberspect delegate target
	 * 
	 * @return Velocity engine
	 * 
	 * @throws Exception on errors initializing Velocity
	 */
	private VelocityEngine createEngine( int companyId, UberspectDelegateTargetFactory factory) throws Exception {
		VelocityEngine ve = new VelocityEngine();
		
		ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
		ve.setProperty(RuntimeConstants.RUNTIME_LOG, AgnUtils.getDefaultValue("velocity.logdir") + "/velocity.log");
		ve.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
		ve.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
		ve.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, "org.agnitas.emm.core.velocity.IncludeParsePreventionHandler");
		
		Uberspect uberspectDelegateTarget = factory.newDelegateTarget( companyId);
		
		if( uberspectDelegateTarget != null) {
			if( logger.isInfoEnabled())
				logger.info( "Setting uberspect delegate target: " + uberspectDelegateTarget.getClass().getCanonicalName());
			
			ve.setProperty( RuntimeConstants.UBERSPECT_CLASSNAME, UberspectDelegate.class.getCanonicalName());
			ve.setProperty( UberspectDelegate.DELEGATE_TARGET_PROPERTY_NAME, uberspectDelegateTarget);
		} else
			logger.warn( "SECURITY LEAK: No uberspector defined");
		
		try {
			ve.init();
	
			return ve;
		} catch( Exception e) {
			logger.error( "Error initializing Velocity engine", e);
			
			throw e;
		}
		
	}

	@Override
	public VelocityResult evaluate( Map params, String template, Writer writer) throws IOException {
		String logTag = createLogTag( getCompanyId(), 0, 0);
		
		return evaluate( params, template, writer, logTag);
	}

	@Override
	public VelocityResult evaluate(Map params, String template, Writer writer, int formId, int actionId) throws IOException {
		String logTag = createLogTag( getCompanyId(), formId, actionId);
		
		return evaluate( params, template, writer, logTag);
	}
	
	@Override
	public VelocityResult evaluate( Map params, String template, Writer writer, String logTag) throws IOException {
        VelocityContext context = new VelocityContext(params);
        EventHandler velocityEH = new EventHandler(context);
        
        String nonNullTemplate = StringUtils.defaultString( template);
        
		boolean successful = this.engine.evaluate( context, writer, logTag, nonNullTemplate);
		
		return new VelocityResultImpl( successful, velocityEH);
	}
	
	/**
	 * Create log tag depending on given company ID, form ID and action ID.
	 * The log tag is used to identify scripts violating the company context.
	 * 
	 * @param companyId company ID
	 * @param formId form ID
	 * @param actionId action ID
	 * 
	 * @return log tag
	 */
	private String createLogTag( int companyId, int formId, int actionId) {
		StringBuffer buffer = new StringBuffer();
		
		if( companyId != 0) {
			buffer.append( "Company ID ");
			buffer.append( companyId);
		}
		
		if( formId != 0) {
			if( companyId != 0)
				buffer.append( ", ");
			buffer.append( "form ID ");
			buffer.append( formId);
		}
		
		if( actionId != 0) {
			if( companyId != 0 || formId != 0)
				buffer.append( ", ");
			buffer.append( "action ID ");
			buffer.append( actionId);
		}
		
		return buffer.toString();
	}
	
	@Override
	public int getCompanyId() {
		return this.contextCompanyId;
	}
}
