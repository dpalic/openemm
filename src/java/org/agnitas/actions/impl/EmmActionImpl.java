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
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.actions.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.agnitas.actions.ActionOperation;
import org.agnitas.actions.EmmAction;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.service.EmmActionService;
import org.springframework.context.ApplicationContext;

/** Main Container for Actions. Allows managing and executing Actions with an easy interface
 *
 * @author mhe
 * @version 2.0
 */
public class EmmActionImpl implements EmmAction {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -5500708140184014085L;

	/** Holds value of property companyID. */
    protected int companyID;
    
    /**
     * Holds value of property id.
     */
    protected int id;
    
    /** Holds value of property shortname. */
    protected String shortname;
    
    /** Holds value of property description. */
    protected String description = "";
    
    /** Holds value of property actions. */
    protected List<ActionOperation> actions;
    
    private List<AbstractActionOperation> actionOperations;
    
    /**
     * Holds value of property type.
     */
    protected int type;
    
    /**
     * Number of forms , which use this Action
     */
    private int used;

    /**
     * Names of forms , which use this Action
     */
    protected String formNames;
    
    protected Timestamp changeDate;
    
    protected Timestamp creationDate;
    
    
    /** Creates new Action */
    public EmmActionImpl() {
    }
       
     /**
     * Executes all ActionOperations for this Action in ArrayList actions
     * 
     * @return true==sucess
     * false=error
     * @param con 
     * @param params Map containing all available informations
    * @deprecated replaced by <code>EmmActionService.executeActions(int, int, Map<String, Object>)</code>.
    */
    @Override
	@Deprecated
    public boolean executeActions(ApplicationContext con, Map params) {
    	EmmActionService service = (EmmActionService) con.getBean("emmActionService");
    	return service.executeActions(getId(), getCompanyID(), params);
//        boolean returnValue=true;
//        ActionOperation aOperation;
//        
//        if(actions==null) {
//            return false;
//        }
//        
//        ListIterator<ActionOperation> allActions=actions.listIterator();
//        
//        if(allActions==null) {
//            return false;
//        }
//        
//        while(allActions.hasNext()) {
//            aOperation = allActions.next();
//            returnValue=aOperation.executeOperation(con, this.companyID, params);
//            if(returnValue==false) {
//                break;
//            }
//        }
//        
//        return returnValue;
    }
    
    /** Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    @Override
    public int getCompanyID() {
        return companyID;
    }
    
    /** Setter for property companyID.
     *
     * @param companyID New value of property companyID.
     */
    @Override
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
    
    /**
     * Getter for property id.
     * 
     * @return Value of property id.
     */
    @Override
    public int getId() {
        return id;
    }
    
    /**
     * Setter for property id.
     * 
     * @param actionID 
     */
    @Override
    public void setId(int actionID) {
        this.id = actionID;
    }
    
    /** Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    @Override
    public String getShortname() {
        return shortname;
    }
    
    /** Setter for property shortname.
     *
     * @param shortname New value of property shortname.
     */
    @Override
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    /** Getter for property description.
     *
     * @return Value of property description.
     */
    @Override
    public String getDescription() {
        return description;
    }
    
    /** Setter for property description.
     *
     * @param description New value of property description.
     */
    @Override
    public void setDescription(String description) {
        if(description == null || description.length() < 1) {
            description = " ";
        }
        this.description = description;
    }
    
    /** Getter for property actions.
     *
     * @return Value of property actions.
     */
    @Override
    public List<ActionOperation> getActions() {
        return actions;
    }
    
    /** Setter for property actions.
     *
     * @param actions New value of property actions.
     */
    @Override
    public void setActions(List<ActionOperation> actions) {
        this.actions = actions;
    }
    
    /**
	 * @return the actionOperations
	 */
    @Override
	public List<AbstractActionOperation> getActionOperations() {
		return actionOperations;
	}

	/**
	 * @param actionOperations the actionOperations to set
	 */
    @Override
	public void setActionOperations(List<AbstractActionOperation> actionOperations) {
		this.actionOperations = actionOperations;
	}

	/**
     * Getter for property type.
     *
     * @return Value of property type.
     */
    @Override
    public int getType() {
        return this.type;
    }
    
    /**
     * Setter for property type.
     *
     * @param type New value of property type.
     */
    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
	public int getUsed() {
		return used;
	}

    @Override
	public void setUsed(int used) {
		this.used = used;
	}

    @Override
    public String getFormNames() {
        return formNames;
    }

    @Override
    public void setFormNames(String formNames) {
        this.formNames = formNames;
    }

	public Timestamp getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Timestamp changeDate) {
		this.changeDate = changeDate;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}
}
