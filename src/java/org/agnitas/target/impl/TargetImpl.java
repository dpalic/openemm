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

package org.agnitas.target.impl;

import java.util.Date;
import java.util.List;

import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.target.Target;
import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetRepresentation;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import bsh.Interpreter;

/**
 *
 * @author Martin Helff
 */
public class TargetImpl implements Target {
	private static final transient Logger logger = Logger.getLogger(TargetImpl.class);

    protected int companyID;
    protected int id;
    protected String targetName;
    protected String targetSQL;
    protected String targetDescription;
    protected int deleted;

    /** Holds value of property targetStructure. */
    protected TargetRepresentation targetStructure;

	protected Date creationDate;
	protected Date changeDate;

    /** Creates new Target */
    public TargetImpl() {
    }

    public TargetImpl(int id, String name) {
        setId(id);
        setTargetName(name);
    }

    @Override
	public void setId(int id) {
        this.id=id;
    } 

    @Override
	public void setCompanyID(@VelocityCheck int id) {
        companyID=id;
    }

    @Override
	public void setTargetName(String name) {
        targetName=name;
    }

    @Override
	public void setTargetSQL(String sql) {
    	
    	// TODO: The following code block ("if") is for debugging (see AGNEMM-787)
    	if( this.targetSQL != null) {
    		// Check only, when new SQL statement is longer than the old one 
    		if( sql.length() > targetSQL.length()) {
    			String tmp = targetSQL;
    			int parCount = 0;
    			
    			// Add "(" and ")" to the old statement until its length is greater or equals to the new one
    			while( tmp.length() < sql.length()) {
    				tmp = "(" + tmp + ")";
    				parCount++;
    			}
    			
    			// When both statement are equal by content then we got a problem!
    			if( tmp.equals( sql)) {
    				try {
    					throw new RuntimeException( "POSSIBLE PROBLEM WITH PARENTHESIS DETECTED - " + parCount + " new parenthesis levels added");
    				} catch( RuntimeException e) {
    					logger.error( "possible error with parenthesis detected", e);
    					logger.error( "target ID: " + id);
    					logger.error( "company ID: " + companyID);
    					logger.error( "target name: " + targetName);
    					logger.error( "old SQL: " + this.targetSQL);
    					logger.error( "new SQL: " + sql);
    				}
    			}
    		}
    	}
    	
        targetSQL=sql;
    }

    @Override
	public void setTargetDescription(String sql) {
        targetDescription=sql;
    }

    @Override
	public int getId() {
        return this.id;
    }

    @Override
	public int getCompanyID() {
        return companyID;
    }

    @Override
	public String getTargetName() {
        return targetName;
    }

    @Override
	public String getTargetSQL() {
    	/*
    	 * Outer parenthesis has been removed here.
    	 * Outer parenthesis is already added in TargetRepresentationImpl.generateSQL().
    	 * Adding parenthesis here may cause problem when loading and saving Target
    	 * without generating SQL from TargetRepresentation.
    	 * 
    	 * See JIRA-787 for more informations.
    	 */
        return targetSQL; 
    }

    @Override
	public String getTargetDescription() {
        return targetDescription;
    }

    /** Getter for property targetStructure.
     * @return Value of property targetStructure.
     */
    @Override
	public TargetRepresentation getTargetStructure() {
        return this.targetStructure;
    }

    /** Setter for property targetStructure.
     * @param targetStructure New value of property targetStructure.
     */
    @Override
	public void setTargetStructure(TargetRepresentation targetStructure) {
        if (targetStructure.getClass().getName().equals("com.agnitas.query.TargetRepresentation")) {
            TargetRepresentationImpl newrep = new TargetRepresentationImpl();
            List<TargetNode> nodes = targetStructure.getAllNodes();
            
            for (int n = 0; n < nodes.size (); ++n) {
                TargetNode  tmp = nodes.get(n);
                String      prim = tmp.getPrimaryField();

                if (prim != null) {
                    tmp.setPrimaryField(prim.toLowerCase());
                }
                
                String      tname = tmp.getClass().getName();
                TargetNode  newtarget = null;
                
                if (tname.equals ("com.agnitas.query.TargetNodeNumeric")) {
                    newtarget = new TargetNodeNumeric ();
                } else if (tname.equals ("com.agnitas.query.TargetNodeString")) {
                    newtarget = new TargetNodeString ();
                } else if (tname.equals ("com.agnitas.query.TargetNodeDate")) {
                    newtarget = new TargetNodeDate ();
                }
                if (newtarget != null) {
                    newtarget.setOpenBracketBefore (tmp.isOpenBracketBefore ());
                    newtarget.setCloseBracketAfter (tmp.isCloseBracketAfter ());
                    newtarget.setChainOperator (tmp.getChainOperator ());
                    newtarget.setPrimaryOperator (tmp.getPrimaryOperator ());
                    newtarget.setPrimaryField (tmp.getPrimaryField ());
                    newtarget.setPrimaryFieldType (tmp.getPrimaryFieldType ());
                    newtarget.setPrimaryValue (tmp.getPrimaryValue ());
                    
                    tmp = newtarget;
                }
                newrep.addNode (tmp);
            }
            targetStructure = newrep;
        }
        this.targetStructure = targetStructure;
    }

    @Override
	public boolean isCustomerInGroup(Interpreter aBsh) {
        boolean answer=false;
        try {
            Boolean result=(Boolean)aBsh.eval("return ("+this.targetStructure.generateBsh()+")");
            answer=result.booleanValue();
        } catch (Exception e) {
            logger.error("isCustomerInGroup: "+e.getMessage());
            answer=false;
        }
        return answer;
    }

    @Override
	public boolean isCustomerInGroup(int customerID, ApplicationContext con) {
        Interpreter aBsh=AgnUtils.getBshInterpreter(this.companyID, customerID, con);
        if(aBsh==null) {
            return false;
        }

        return this.isCustomerInGroup(aBsh);
    }
    
    @Override
	public void setDeleted(int deleted) {
    	this.deleted = deleted;
    }
    
    @Override
	public int getDeleted() {
    	return this.deleted;
    }

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public Date getChangeDate() {
		return changeDate;
	}

	@Override
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
}
