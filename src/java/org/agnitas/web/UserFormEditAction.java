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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.beans.UserForm;
import org.agnitas.dao.UserFormDao;
import org.agnitas.util.AgnUtils;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Implementation of <strong>Action</strong> that handle input of user forms.
 *
 * @author mhe
 */

public final class UserFormEditAction extends StrutsActionBase {

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
     * @return the action to forward to.
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        // Validate the request parameters specified by the user
        UserFormEditForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination=null;

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        aForm=(UserFormEditForm)form;
        AgnUtils.logger().info("Action: "+aForm.getAction());

        try {
            switch(aForm.getAction()) {
                case UserFormEditAction.ACTION_LIST:
                    if(allowed("forms.view", req)) {
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case UserFormEditAction.ACTION_VIEW:
                    if(allowed("forms.view", req)) {
                        if(aForm.getFormID()!=0) {
                            loadUserForm(aForm, req);
                        }
                        aForm.setAction(UserFormEditAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case UserFormEditAction.ACTION_SAVE:
                    if(allowed("forms.change", req)) {
                        destination=mapping.findForward("success");
                        saveUserForm(aForm, req);
                        
                        // Show "changes saved"
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case UserFormEditAction.ACTION_CONFIRM_DELETE:
                    if(allowed("forms.delete", req)) {
                        loadUserForm(aForm, req);
                        aForm.setAction(UserFormEditAction.ACTION_DELETE);
                        destination=mapping.findForward("delete");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case UserFormEditAction.ACTION_DELETE:
                    if(allowed("forms.delete", req)) {
                        if(req.getParameter("delete.x")!=null) {
                            deleteUserForm(aForm, req);
                        }
                        aForm.setAction(UserFormEditAction.ACTION_LIST);
                        destination=mapping.findForward("list");

                        // Show "changes saved"
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                default:
                    aForm.setAction(UserFormEditAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        if( "list".equals(destination.getName()) || "success".equals(destination.getName())) {
			if ( aForm.getColumnwidthsList() == null) {
        		aForm.setColumnwidthsList(getInitializedColumnWidthList(3));
        	}

			try {
				req.setAttribute("userformlist", getUserFromList(req));
				setNumberOfRows(req, aForm);
			} catch (Exception e) {
				AgnUtils.logger().error("userformlist: "+e+"\n"+AgnUtils.getStackTrace(e));
	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			}
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }

        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }

        return destination;
    }

    /**
     * Load a user form.
     * Retrieves the data of a form from the database.
     * @param aForm on input contains the id of the form.
     *              On exit contains the data read from the database.
     * @param req used to get the ApplicationContext.
     */
    protected void loadUserForm(UserFormEditForm aForm, HttpServletRequest req) {
        UserFormDao dao=(UserFormDao) getBean("UserFormDao");

        UserForm aUserForm=dao.getUserForm(aForm.getFormID(), this.getCompanyID(req));

        if(aUserForm!=null && aUserForm.getId()!=0) {
            aForm.setFormName(aUserForm.getFormName());
            aForm.setDescription(aUserForm.getDescription());
            aForm.setStartActionID(aUserForm.getStartActionID());
            aForm.setEndActionID(aUserForm.getEndActionID());
            aForm.setSuccessTemplate(aUserForm.getSuccessTemplate());
            aForm.setErrorTemplate(aUserForm.getErrorTemplate());
            AgnUtils.logger().info("loadUserForm: form "+aForm.getFormID()+" loaded");
        } else {
            AgnUtils.logger().warn("loadUserForm: could not load userform "+aForm.getFormID());
        }
    }

    /**
     * Save a user form.
     * Writes the data of a form to the database.
     * @param aForm contains the data of the form.
     * @param req used to get the ApplicationContext.
     */
    protected void saveUserForm(UserFormEditForm aForm, HttpServletRequest req) {
        UserFormDao dao=(UserFormDao) getBean("UserFormDao");
        UserForm aUserForm=(UserForm) getBean("UserForm");

        aUserForm.setCompanyID(this.getCompanyID(req));
        aUserForm.setId(aForm.getFormID());
        aUserForm.setFormName(aForm.getFormName());
        aUserForm.setDescription(aForm.getDescription());
        aUserForm.setStartActionID(aForm.getStartActionID());
        aUserForm.setEndActionID(aForm.getEndActionID());
        aUserForm.setSuccessTemplate(aForm.getSuccessTemplate());
        aUserForm.setErrorTemplate(aForm.getErrorTemplate());

        aForm.setFormID(dao.saveUserForm(aUserForm));
    }

    /**
     * Delete a user form.
     * Removes the data of a form from the database.
     * @param aForm contains the id of the form.
     * @param req used to get the ApplicationContext.
     */
    protected void deleteUserForm(UserFormEditForm aForm, HttpServletRequest req) {
        UserFormDao dao=(UserFormDao) getBean("UserFormDao");

        dao.deleteUserForm(aForm.getFormID(), getCompanyID(req));
    }
    
    public List<DynaBean> getUserFromList(HttpServletRequest request) throws IllegalAccessException, InstantiationException {
    	  ApplicationContext aContext= getWebApplicationContext();
	      JdbcTemplate aTemplate=new JdbcTemplate( (DataSource)aContext.getBean("dataSource"));
	      
	      String sqlStatement = "SELECT form_id, formname, description FROM userform_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+ " ORDER BY formname";
	      
	      List<Map> tmpList = aTemplate.queryForList(sqlStatement);
          
	      DynaProperty[] properties = new DynaProperty[] {
	    		  new DynaProperty("formid", Long.class),
	    		  new DynaProperty("formname",String.class),
	    		  new DynaProperty("description", String.class)
	      };

	      if(AgnUtils.isOracleDB()) {
	    	  properties = new DynaProperty[] {
	    	  	  new DynaProperty("formid", BigDecimal.class),
	    	  	  new DynaProperty("formname",String.class),
		    	  new DynaProperty("description", String.class)
		      };
	      }	      
	      
	      BasicDynaClass dynaClass = new BasicDynaClass("userform", null, properties);
	      
	      List<DynaBean> result = new ArrayList<DynaBean>();
	      for(Map row:tmpList) {
	    	  DynaBean newBean = dynaClass.newInstance();    	
	    	  newBean.set("formid", row.get("FORM_ID"));
	    	  newBean.set("formname", row.get("FORMNAME"));
	    	  newBean.set("description", row.get("DESCRIPTION"));
	    	  result.add(newBean);
	      } 
	      return result;
    }
}
