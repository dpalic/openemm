package org.agnitas.emm.core.velocity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Runtime annotation for controlling various checks in Velocity.
 * 
 * @author md
 */
@Retention( RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface VelocityCheck {
	
	/** 
	 * Returns the type of checks defined by the annotation. 
	 * Default value is {@link CheckType#COMPANY_CONTEXT}. 
	 * 
	 * @return type of checks
	 */
	CheckType[] value() default CheckType.COMPANY_CONTEXT;
}
