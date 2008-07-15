/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.web;

import org.agnitas.util.*;
import org.agnitas.dao.*;
import org.agnitas.beans.*;
import org.agnitas.target.*;
import java.io.IOException;
import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.hibernate.dialect.*;
import org.apache.struts.action.*;
import org.springframework.context.*;
import org.springframework.jdbc.core.*;

/**
 * Handles all actions on profile fields.
 */
public final class ProfileFieldAction extends StrutsActionBase {
    
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
            aForm.setAction(this.ACTION_CONFIRM_DELETE);
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
                        saveProfileField(aForm, req);
                        destination=mapping.findForward("list");
                    }
                    break;
                    
                case ProfileFieldAction.ACTION_NEW:
                    if(allowed("profileField.show", req)) {
                        if(req.getParameter("save.x")!=null) {
                            if(newProfileField(aForm, req)){
                                aForm.setAction(ProfileFieldAction.ACTION_LIST);
                                destination=mapping.findForward("list");
                            } else {
                                // error message: NewProfileDBFieldError:
                                errors.add("NewProfileDB_Field", new ActionMessage("error.profiledb.insert_in_db_error"));
                                aForm.setAction(ProfileFieldAction.ACTION_VIEW);
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
        TreeMap list=null;

        try {
            list=org.agnitas.taglib.ShowColumnInfoTag.getColumnInfo(aContext, compID, aForm.getFieldname());
        } catch(Exception e) {
            AgnUtils.logger().error(e.getMessage());
            return;
        }

        if(list.size() > 0) {
            Map col=(Map) list.get(list.firstKey());
            
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
        
        return;
    }
    
    /**
     * Saves profile field.
     */
    protected void saveProfileField(ProfileFieldForm aForm, HttpServletRequest req) {
        String fieldname = SafeString.getSQLSafeString(aForm.getFieldname());
        ProfileFieldDao dao=(ProfileFieldDao) getBean("ProfileFieldDao");
        ProfileField field = dao.getProfileField(getCompanyID(req), fieldname);

        if(field == null) {
            field=(ProfileField) getBean("ProfileField");
            field.setCompanyID(getCompanyID(req));
            field.setColumn(fieldname);
        }
 
        field.setDescription(SafeString.getSQLSafeString(aForm.getDescription()));
        field.setShortname(SafeString.getSQLSafeString(aForm.getShortname()));
        field.setDefaultValue(SafeString.getSQLSafeString(aForm.getFieldDefault()));
        getHibernateTemplate().saveOrUpdate("ProfileField", field);
    }
    
    /**
     * Creates a profile field.
     */
    protected boolean newProfileField(ProfileFieldForm aForm, HttpServletRequest req) throws Exception {
        JdbcTemplate jdbc=new JdbcTemplate((DataSource) getBean("dataSource"));
 
        // get data from Form:
        String	name=AgnUtils.getDefaultValue("jdbc.dialect");
        Dialect	dia=null;
        String fieldType = aForm.getFieldType();
        int jsqlType=-1;
        String dbType = null;
        int length = aForm.getFieldLength();
        String fieldDefault = aForm.getFieldDefault();
        String defaultSQL = "";
        String sql = "";

        if(fieldDefault!=null && fieldDefault.compareTo("")!=0) {
            if(fieldType.compareTo("VARCHAR")==0) {
                defaultSQL = " DEFAULT '" + fieldDefault + "'";
            } else {
                defaultSQL = " DEFAULT " + fieldDefault;
            }
        }
        
        Class	cl=null;
            
        cl=Class.forName("java.sql.Types");
        jsqlType=cl.getDeclaredField(fieldType).getInt(null);
        cl=Class.forName(name);
        dia=(Dialect) cl.getConstructor(new Class[0]).newInstance(new Object[0]);
        dbType=dia.getTypeName(jsqlType);
        
        /* Bugfix for mysql.
         * The jdbc-Driver for mysql maps VARCHAR to longtext.
         * This might be ok in most cases, but longtext doesn't support
         * length restrictions. So the correct tpye for mysql should be
         * varchar.
         */
        if(fieldType.equals("VARCHAR") && dbType.equals("longtext") && length > 0) {
            dbType="varchar";
        }

        String fieldname = aForm.getFieldname();

        sql = "ALTER TABLE customer_" + getCompanyID(req) + "_tbl ADD (";
        sql += fieldname.toLowerCase() + " " + dbType;
        if(fieldType.compareTo("VARCHAR")==0 && length > 0) {
            sql += "(" + length + ")";
        }
        sql += defaultSQL;
        
        if(!aForm.isFieldNull()) {
            sql += " NOT NULL";
        }

        sql += ")";

        try {
            jdbc.execute(sql);
        } catch(Exception e) {
            AgnUtils.logger().error("SQL: "+sql);
            throw e;
        }

        aForm.setFieldDefault(fieldDefault);
        saveProfileField(aForm, req);
        return true;
    }
    
    /**
     * Removes a profile field.
     */
    protected void deleteProfileField(ProfileFieldForm aForm, HttpServletRequest req) {
        JdbcTemplate jdbc=new JdbcTemplate((DataSource) getBean("dataSource"));
        
        String fieldname = SafeString.getSQLSafeString(aForm.getFieldname());
        ProfileFieldDao dao=(ProfileFieldDao) getBean("ProfileFieldDao");
        ProfileField field = dao.getProfileField(getCompanyID(req), fieldname);
        String sql = null;
        
        sql = "ALTER TABLE customer_" + getCompanyID(req) + "_tbl DROP " + fieldname;
        jdbc.execute(sql);
        

        if(field != null) {
            getHibernateTemplate().delete(field);
            getHibernateTemplate().flush();
        }
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
        ListIterator tIter=targets.listIterator();

        while(tIter.hasNext()) {
            Target aTarget=(Target) tIter.next();

            if(aTarget != null) {
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
