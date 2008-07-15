/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.*;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Implementation of <strong>Action</strong> that handles Blacklists
 *
 * @author Alexander Schmoeller
 */

public class BlacklistAction extends StrutsActionBase {
	
	public static final int ACTION_DOWNLOAD = ACTION_LAST + 1;

    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param form
     * @param req
     * @param res
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */
    public ActionForward execute(ActionMapping mapping,
    ActionForm form,
    HttpServletRequest req,
    HttpServletResponse res)
    throws IOException, ServletException {

        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        JdbcTemplate template = getJdbcTemplate();
        

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        Integer action;
		try {
			action = Integer.parseInt(req.getParameter("action"));
		} catch (Exception e) {
			action = BlacklistAction.ACTION_LIST;
		}		
        
        AgnUtils.logger().info("Action: "+ action );

        try {
            destination = executeIntern(mapping, req, errors, destination, template, action);

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }

        return destination;
    }

	protected ActionForward executeIntern(ActionMapping mapping, HttpServletRequest req, ActionMessages errors, ActionForward destination, JdbcTemplate template, Integer action) {
		String deleteEmail = req.getParameter("delete");
		String newEmail = req.getParameter("newemail");
		switch( action ) {
		    case BlacklistAction.ACTION_LIST:
		        if(allowed("settings.show", req)) {
		            destination=mapping.findForward("list");
		        } else {
		            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
		        }
		        break;
		    case BlacklistAction.ACTION_SAVE:
		    	if( StringUtils.isNotEmpty( newEmail ) ) {
		            String sqlInsert="INSERT INTO cust_ban_tbl (company_id, email) VALUES (" + getCompanyID( req ) + ", '" +
		            	SafeString.getSQLSafeString(newEmail.toLowerCase().trim()) + "')";
		            template.update(sqlInsert);
		    	}
		    	destination=mapping.findForward("list");
		    	break;
		    case BlacklistAction.ACTION_DELETE:
		    	if( StringUtils.isNotEmpty( deleteEmail ) ) {
		            String sqlDelete="DELETE FROM cust_ban_tbl WHERE company_id=" + getCompanyID( req ) + " AND email='" +
		            	SafeString.getSQLSafeString(deleteEmail.toLowerCase()) + "'";
		            template.update(sqlDelete);
		        }
		    	destination=mapping.findForward("list");
		    	break;

		    default:
		        destination=mapping.findForward("list");
		}
		return destination;
	}
}
