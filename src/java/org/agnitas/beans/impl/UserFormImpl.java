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

package org.agnitas.beans.impl;

import org.agnitas.beans.*;
import org.agnitas.dao.*;
import org.agnitas.util.*;
import org.springframework.context.*;
import java.io.*;
import java.util.*;
import org.apache.velocity.*;
import org.apache.velocity.app.*;

/**
 *
 * @author  mhe
 */
public class UserFormImpl implements UserForm {
    
    /**
     * Holds value of property companyID.
     */
    protected int companyID;
    
    /**
     * Holds value of property formName.
     */
    protected String formName;
    
    /**
     * Holds value of property id.
     */
    protected int id;
    
    /**
     * Holds value of property startActionID.
     */
    protected int startActionID;
    
    /**
     * Holds value of property endActionID.
     */
    protected int endActionID;
    
    /**
     * Holds value of property successTemplate.
     */
    protected String successTemplate;
    
    /**
     * Holds value of property errorTemplate.
     */
    protected String errorTemplate;
    
    /**
     * Holds value of property description.
     */
    protected String description;
    
    /**
     * Holds value of property startAction.
     */
    protected org.agnitas.actions.EmmAction startAction;
    
    /**
     * Holds value of property endAction.
     */
    protected org.agnitas.actions.EmmAction endAction;
    
    /** Creates a new instance of UserForm */
    public UserFormImpl() {
    }
    
    /**
     * Getter for property companyID.
     * @return Value of property companyID.
     */
    public int getCompanyID() {
        return this.companyID;
    }
    
    /**
     * Setter for property companyID.
     * @param companyID New value of property companyID.
     */
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
    
    /**
     * Getter for property formName.
     * @return Value of property formName.
     */
    public String getFormName() {
        return this.formName;
    }
    
    /**
     * Setter for property formName.
     * @param formName New value of property formName.
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }
    
    /**
     * Getter for property id.
     * 
     * @return Value of property id.
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * Setter for property id.
     * 
     * @param formID 
     */
    public void setId(int formID) {
        this.id = formID;
    }
    
    /**
     * Getter for property startActionID.
     * @return Value of property startActionID.
     */
    public int getStartActionID() {
        return this.startActionID;
    }
    
    /**
     * Setter for property startActionID.
     * @param startActionID New value of property startActionID.
     */
    public void setStartActionID(int startActionID) {
        this.startActionID = startActionID;
    }
    
    /**
     * Getter for property endActionID.
     * @return Value of property endActionID.
     */
    public int getEndActionID() {
        return this.endActionID;
    }
    
    /**
     * Setter for property endActionID.
     * @param endActionID New value of property endActionID.
     */
    public void setEndActionID(int endActionID) {
        this.endActionID = endActionID;
    }
    
    /**
     * Getter for property sucessTemplate.
     * @return Value of property sucessTemplate.
     */
    public String getSuccessTemplate() {
        return this.successTemplate;
    }
    
    /**
     * Setter for property sucessTemplate.
     * @param successTemplate 
     */
    public void setSuccessTemplate(String successTemplate) {
        this.successTemplate = successTemplate;
    }
    
    /**
     * Getter for property errorTemplate.
     * @return Value of property errorTemplate.
     */
    public String getErrorTemplate() {
        return this.errorTemplate;
    }
    
    /**
     * Setter for property errorTemplate.
     * @param errorTemplate New value of property errorTemplate.
     */
    public void setErrorTemplate(String errorTemplate) {
        this.errorTemplate = errorTemplate;
    }
    
    
    /**
     * Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Getter for property startAction.
     * @return Value of property startAction.
     */
    public org.agnitas.actions.EmmAction getStartAction() {
        return this.startAction;
    }
    
    /**
     * Setter for property startAction.
     * @param startAction New value of property startAction.
     */
    public void setStartAction(org.agnitas.actions.EmmAction startAction) {
        this.startAction = startAction;
    }
    
    /**
     * Getter for property endAction.
     * @return Value of property endAction.
     */
    public org.agnitas.actions.EmmAction getEndAction() {
        return this.endAction;
    }
    
    /**
     * Setter for property endAction.
     * @param endAction New value of property endAction.
     */
    public void setEndAction(org.agnitas.actions.EmmAction endAction) {
        this.endAction = endAction;
    }
    
    protected boolean evaluateAction(ApplicationContext con, org.agnitas.actions.EmmAction aAction, HashMap params) {
        boolean result=true;
        
        if(aAction==null) {
            return result;
        }
       
        try {
            result=aAction.executeActions(con, params);
        } catch (Exception e) {
            AgnUtils.logger().error("evaluateAction: "+e.getMessage());
            result=false;
        }
        
        return result;
    }
    
    public boolean evaluateStartAction(ApplicationContext con, HashMap params) {
        
        if(this.startActionID!=0 && this.startAction==null) {
            EmmActionDao dao=(EmmActionDao)con.getBean("EmmActionDao");
            
            this.startAction=dao.getEmmAction(this.startActionID, this.companyID);
            if(this.startAction==null) {
                return false;
            }
        }
        
        return evaluateAction(con, this.startAction, params);
    }
    
    public boolean evaluateEndAction(ApplicationContext con, HashMap params) {
        
        if(this.endActionID!=0 && this.endAction==null) {
            EmmActionDao dao=(EmmActionDao)con.getBean("EmmActionDao");
            
            this.endAction=dao.getEmmAction(this.endActionID, this.companyID);
            
            if(this.endAction==null) {
                return false;
            }
        }
        
        return evaluateAction(con, this.endAction, params);
    }
    
    public String evaluateForm(ApplicationContext con, HashMap params) {
        
        String result=null;
        boolean actionResult=true;
        StringWriter aWriter=new StringWriter();
        
        actionResult=this.evaluateStartAction(con, params);
        
        if(!actionResult) {
            params.put("_error", "1");
        }
        
        try {
            Velocity.setProperty("runtime.log", AgnUtils.getDefaultValue("system.script_logdir")+"/velocity.log");
            Velocity.init();
            if(actionResult) {
                Velocity.evaluate(new VelocityContext(params), aWriter, null, this.successTemplate);
            } else {
                Velocity.evaluate(new VelocityContext(params), aWriter, null, this.errorTemplate);
            }
        } catch(Exception e) {
            AgnUtils.logger().error("evaluateForm: "+e.getMessage());
        }
        
        result=aWriter.toString();
        
        return result;
    }
    
}
