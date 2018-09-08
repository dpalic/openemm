package org.agnitas.emm.core.commons.annotations.dev;

/**
 * This annotation can be used to tag elements for
 * future work related to some issues.
 * 
 * Annotate methods, classes, ... like {@code @RelatedIssues({"AGNEMM-1", "OPEN-2"})}.
 *  
 * 
 * @author md
 */
public @interface RelatedIssues {
	String[] value();
}
