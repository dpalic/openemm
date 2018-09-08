package org.agnitas.emm.core.commons.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Introspector for Annotations.
 * 
 * @author md
 *
 */
public class AnnotationIntrospector {
	
	private static final transient Logger logger = Logger.getLogger( AnnotationIntrospector.class);
	
	public static class ParameterAnnotationDescriptor {
		private final int parameterIndex;
		private final Annotation annotation;
		
		public ParameterAnnotationDescriptor( int parameterIndex, Annotation annotation) {
			this.parameterIndex = parameterIndex;
			this.annotation = annotation;
		}
		
		public int getParameterIndex() {
			return this.parameterIndex;
		}
		
		public Annotation getAnnotation() {
			return this.annotation;
		}
	}

	/**
	 * Returns a list of descriptors for method parameters annotated with given annotation.
	 *  
	 * @param method method for introspection
	 * @param annotationType annotation type
	 * 
	 * @return list of parameter descriptors
	 */
	public List<ParameterAnnotationDescriptor> getParameterAnnotation( Method method, Class<? extends Annotation> annotationType) {
		List<ParameterAnnotationDescriptor> list = new Vector<ParameterAnnotationDescriptor>();
		
		if( method != null) {
			Annotation[][] annotations = method.getParameterAnnotations();
			
			if( annotations != null) {
				// Iterator over parameters
				for( int index = 0; index < annotations.length; index++) {
					// Iterate over annotations of the parameter
					for( int anno = 0; anno < annotations[index].length; anno++) {
						if( annotations[index][anno].annotationType().equals( annotationType)) {
							list.add( new ParameterAnnotationDescriptor( index, annotations[index][anno]));
							break;	// We can stop inner loop, if we found a matching annotation. It's not possible to annotate something with the same annotation twice.
						}
					}
				}
			}
		} else {
			try {
				throw new RuntimeException( "Method is null!"); // Throw this to get a stack trace!
			} catch( RuntimeException e) {
				logger.warn( "No method to introspect!", e);
			}
		}
		
		return Collections.unmodifiableList( list);
	}
}
