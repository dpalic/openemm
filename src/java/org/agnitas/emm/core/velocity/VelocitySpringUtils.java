package org.agnitas.emm.core.velocity;

import org.springframework.context.ApplicationContext;

/**
 * Utility class for the VelocityWrapper component building the bridge to Spring.
 * 
 * @author md
 */
public class VelocitySpringUtils {

	/**
	 * Returns the VelocityWrapperFactory defined in the application context of Spring.
	 * The bean must be named &quot;VelocityWrapperFactory&quot;.
	 *  
	 * @param con Spring's application context.
	 * 
	 * @return implementation of {@link VelocityWrapperFactory}
	 */
    public static VelocityWrapperFactory getVelocityWrapperFactory( ApplicationContext con) {
    	return (VelocityWrapperFactory)con.getBean( "VelocityWrapperFactory");
    }

}
