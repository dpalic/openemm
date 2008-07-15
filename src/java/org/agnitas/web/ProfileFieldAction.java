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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.ProfileField;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.target.TargetNode;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;

/**
 * Handles all actions on profile fields.
 */
public class ProfileFieldAction extends StrutsActionBase {

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
            throws IOException, ServletException, Exception {

        // Validate the request parameters specified by the user
        ProfileFieldForm aForm=null;
        ActionMessages errors = new ActionErrors();
        ActionForward destination=null;

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        aForm=(ProfileFieldForm)form;

        if(req.getParameter("delete.x")!=null) {
            aForm.setAction(ACTION_CONFIRM_DELETE);
        }

        AgnUtils.logger().info("Action: "+aForm.getAction());

        try {
            switch(aForm.getAction()) {
                case ProfileFieldAction.ACTION_LIST:
                    if(allowed("profileField.show", req)) {
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ProfileFieldAction.ACTION_VIEW:
                    if(allowed("profileField.show", req)) {
                        if(req.getParameter("fieldname")!=null) {
                            loadProfileField(aForm, req);
                            aForm.setAction(ProfileFieldAction.ACTION_SAVE);
                        } else {
                            aForm.setAction(ProfileFieldAction.ACTION_NEW);
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    destination=mapping.findForward("view");
                    break;

                case ProfileFieldAction.ACTION_SAVE:
                    if(req.getParameter("save.x")!=null) {
                        saveProfileField(aForm, req, errors);
                        destination=mapping.findForward("list");
                    }
                    break;

                case ProfileFieldAction.ACTION_NEW:
                    if(allowed("profileField.show", req)) {
                        if(req.getParameter("save.x")!=null) {
                            if(newProfileField(aForm, req, errors)){
                                aForm.setAction(ProfileFieldAction.ACTION_LIST);
                                destination=mapping.findForward("list");
                            } else {
                                destination=mapping.findForward("view");
                            }
                        }
                    }
                    break;

                case ProfileFieldAction.ACTION_CONFIRM_DELETE:
                    loadProfileField(aForm, req);
                    String tg_ret = checkForTargetGroups(aForm, req);

                    if(tg_ret.compareTo("ok")==0 ) {
                        aForm.setAction(ProfileFieldAction.ACTION_DELETE);
                        destination=mapping.findForward("delete");
                    } else {
                        if(tg_ret.compareTo("error")!=0 ) {
                            aForm.setAction(ProfileFieldAction.ACTION_LIST);
                            aForm.setTargetsDependent(tg_ret);
                            destination=mapping.findForward("delete_error");
                        } else {
                            aForm.setAction(ProfileFieldAction.ACTION_LIST);
                            destination=mapping.findForward("list");

                        }
                    }
                    break;

                case ProfileFieldAction.ACTION_DELETE:
                    if(req.getParameter("kill.x")!=null) {
                        deleteProfileField(aForm, req);
                        aForm.setAction(ProfileFieldAction.ACTION_LIST);
                        destination=mapping.findForward("list");
                    }
                    break;

                default:
                    aForm.setAction(ProfileFieldAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
            throw new ServletException(e);
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            destination=mapping.findForward("list");
        }

        return destination;
    }

    /**
     * Loads a profile field.
     */
    protected void loadProfileField(ProfileFieldForm aForm, HttpServletRequest req) {

        int compID = this.getCompanyID(req);
        String description = "";
        String shortname = "";
        String fieldDefault = "";
        String fieldType = "";
        int length = 0;
        boolean isNull = true;
        ApplicationContext aContext=this.getWebApplicationContext();
        Map list=null;

        try {
            list=org.agnitas.taglib.ShowColumnInfoTag.getColumnInfo(aContext, compID, aForm.getFieldname());
        } catch(Exception e) {
            AgnUtils.logger().error(e.getMessage());
            AgnUtils.logger().error(AgnUtils.getStackTrace(e));
            return;
        }

        Iterator it = list.keySet().iterator();
        if(it.hasNext()) {
            Map col=(Map) list.get(it.next());

            if(col.get("column") != null) {
                if(col.size() > 3) {
                    shortname = (String) col.get("shortname");
                    fieldDefault = (String) col.get("default");
                    description = (String) col.get("description");
                } else {
                    shortname = "";
                    fieldDefault = "";
                    description = "";
                }
                fieldType = (String) col.get("type");
                length = ((Integer) col.get("length")).intValue();
                if(((Integer) col.get("nullable")).intValue() == 0) {
                    isNull = false;
                }
            }
        }
        aForm.setCompanyID(compID);
        aForm.setFieldType(fieldType);
        aForm.setDescription(description);
        aForm.setShortname(shortname);
        aForm.setFieldDefault(fieldDefault);
        aForm.setFieldLength(length);
        aForm.setFieldNull(isNull);
    }

    /**
     * Saves profile field.
     * @param errors 
     */
    protected void saveProfileField(ProfileFieldForm aForm, HttpServletRequest req, ActionMessages errors) {
    	String shortname = aForm.getShortname();
    	int companyID = getCompanyID(req);
        
    	String fieldname = SafeString.getSQLSafeString(aForm.getFieldname());
        ProfileFieldDao dao=(ProfileFieldDao) getBean("ProfileFieldDao");
		ProfileField field = dao.getProfileField(companyID, fieldname);

        if(field == null) {
            field=(ProfileField) getBean("ProfileField");
            field.setCompanyID(companyID);
            field.setColumn(fieldname);
        }

        field.setDescription(SafeString.getSQLSafeString(aForm.getDescription()));
		field.setShortname(SafeString.getSQLSafeString(shortname));
        field.setDefaultValue(SafeString.getSQLSafeString(aForm.getFieldDefault()));
        dao.saveProfileField(field);
    }

    /**
     * Creates a profile field.
     * @param errors 
     */
    protected boolean newProfileField(ProfileFieldForm aForm, HttpServletRequest req, ActionMessages errors) throws Exception {
		// check if shortname is already in use
		String shortname = aForm.getShortname();
		int companyID = getCompanyID(req);
        
		ProfileFieldDao dao=(ProfileFieldDao) getBean("ProfileFieldDao");
		ProfileField fieldByShortname = dao.getProfileFieldByShortname(companyID, shortname);

		if ( fieldByShortname != null ) {
			errors.add("NewProfileDB_Field", new ActionMessage("error.profiledb.exists"));
			return false;
		}
	
		if(!dao.addProfileField(companyID,
					aForm.getFieldname(), 
					aForm.getFieldType(),
					aForm.getFieldLength(),
					aForm.getFieldDefault(),
					!aForm.isFieldNull())) {
				
			errors.add("NewProfileDB_Field", new ActionMessage("error.profiledb.fieldname"));
			return false;
		}
		aForm.setFieldDefault(aForm.getFieldDefault());
		saveProfileField(aForm, req, errors);
		return true;
	}

	/**
	 * Removes a profile field.
	 */
	protected void deleteProfileField(ProfileFieldForm aForm, HttpServletRequest req) {
		String fieldname = SafeString.getSQLSafeString(aForm.getFieldname());
		ProfileFieldDao dao=(ProfileFieldDao) getBean("ProfileFieldDao");

		dao.removeProfileField(getCompanyID(req), fieldname); 
	}

    /**
     * Checks for target groups.
     */
    protected String checkForTargetGroups(ProfileFieldForm aForm, HttpServletRequest req) {

        int compID = getCompanyID(req);
        String fieldname = aForm.getFieldname();
        String ids = "";
        TargetDao targetDao = (TargetDao) getBean("TargetDao");
        List targets=targetDao.getTargets(compID);

        for(int c=0; c < targets.size(); c++) {
            Object obj=targets.get(c);

            if(obj instanceof java.lang.String) {
	        continue;
            }
            Target aTarget=(Target) targets.get(c);

            if(aTarget != null && aTarget.getTargetStructure() != null) {
                ArrayList aList = aTarget.getTargetStructure().getAllNodes();
                ListIterator aIter = aList.listIterator();

                while(aIter.hasNext()) {
                    TargetNode aNode = (TargetNode)aIter.next();

                    if(aNode.getPrimaryField().compareTo(fieldname)==0) {
                        if(targets.contains(aTarget.getTargetName())==false) {
                            targets.add(new String(aTarget.getTargetName()));
                            if(ids.equals("")) {
                                ids += aTarget.getTargetName();
                            } else {
                                ids += "<br>" + aTarget.getTargetName();
                            }
                        }
                    }
                }
            }
        }

        if(ids.length()>0) {
            return ids;
        } else {
            return new String("ok");
        }
    }
}
