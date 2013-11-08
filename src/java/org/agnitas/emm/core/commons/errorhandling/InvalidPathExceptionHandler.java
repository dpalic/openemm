package org.agnitas.emm.core.commons.errorhandling;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;

public class InvalidPathExceptionHandler extends ExceptionHandler {
	
	@Override
	public ActionForward execute( Exception ex, 
            ExceptionConfig exConfig,
            ActionMapping mapping,
            ActionForm formInstance,
            HttpServletRequest request,
            HttpServletResponse response
		) throws ServletException {
		
		AgnUtils.logger().error("No path was found for this action.");
		// interesting point here, the ActionMapping mapping is "null". 
		// Therefore mapping.findForward("error") quits with an null pointer exception :-(
		return null;		
	}
}
