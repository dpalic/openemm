package org.agnitas.emm.core.velocity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.agnitas.emm.core.commons.packages.AbstractPackageInclusionChecker;
import org.apache.log4j.Logger;

/**
 * Implementation of {@link AbstractPackageInclusionChecker} including all org.agnitas.* packages 
 * and sub-packages.
 * 
 * @author md
 *
 */
public class VelocityCheckPackageInclusionCheckerImpl extends AbstractPackageInclusionChecker {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger( VelocityCheckPackageInclusionCheckerImpl.class);
	
	/** Regular expression for package names. */
	private static final transient Pattern pattern = Pattern.compile( "^org\\.agnitas(?:\\..+)?");
	

	@Override
	public boolean includePackage(String packageName) {
		if( logger.isInfoEnabled())
			logger.info( "Check exclusion of package " + packageName);
		
		Matcher matcher = pattern.matcher( packageName);

		return matcher.matches();
	}

}
