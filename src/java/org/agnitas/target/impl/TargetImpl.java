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

package org.agnitas.target.impl;

import java.util.ArrayList;

import org.agnitas.target.Target;
import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetRepresentation;
import org.agnitas.util.AgnUtils;
import org.springframework.context.ApplicationContext;

import bsh.Interpreter;

/**
 *
 * @author Martin Helff
 */
public class TargetImpl implements Target {

    protected int companyID;
    protected int id;
    protected String targetName;
    protected String targetSQL;
    protected String targetDescription;
    protected int deleted;

    /** Holds value of property targetStructure. */
    protected TargetRepresentation targetStructure;

    /** Creates new Target */
    public TargetImpl() {
    }

    public TargetImpl(int id, String name) {
        setId(id);
        setTargetName(name);
    }

    public void setId(int id) {
        this.id=id;
    } 

    public void setCompanyID(int id) {
        companyID=id;
    }

    public void setTargetName(String name) {
        targetName=name;
    }

    public void setTargetSQL(String sql) {
        targetSQL=sql;
    }

    public void setTargetDescription(String sql) {
        targetDescription=sql;
    }

    public int getId() {
        return this.id;
    }

    public int getCompanyID() {
        return companyID;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getTargetSQL() {
        return targetSQL;
    }

    public String getTargetDescription() {
        return targetDescription;
    }

    /** Getter for property targetStructure.
     * @return Value of property targetStructure.
     */
    public TargetRepresentation getTargetStructure() {
        return this.targetStructure;
    }

    /** Setter for property targetStructure.
     * @param targetStructure New value of property targetStructure.
     */
    public void setTargetStructure(TargetRepresentation targetStructure) {
        if (targetStructure.getClass().getName().equals("com.agnitas.query.TargetRepresentation")) {
            TargetRepresentationImpl    newrep = new TargetRepresentationImpl();
            ArrayList           nodes = targetStructure.getAllNodes();
            
            for (int n = 0; n < nodes.size (); ++n) {
                TargetNode  tmp = (TargetNode) nodes.get(n);
                String      prim = tmp.getPrimaryField();

                if (prim != null) {
                    tmp.setPrimaryField(prim.toLowerCase());
                }
                
                String      tname = tmp.getClass().getName();
                TargetNode  newtarget = null;
                
                if (tname.equals ("com.agnitas.query.TargetNodeNumeric")) {
                    newtarget = (TargetNode) new TargetNodeNumeric ();
                } else if (tname.equals ("com.agnitas.query.TargetNodeString")) {
                    newtarget = (TargetNode) new TargetNodeString ();
                } else if (tname.equals ("com.agnitas.query.TargetNodeDate")) {
                    newtarget = (TargetNode) new TargetNodeDate ();
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

    public boolean isCustomerInGroup(Interpreter aBsh) {
        boolean answer=false;
        try {
            Boolean result=(Boolean)aBsh.eval("return ("+this.targetStructure.generateBsh()+")");
            answer=result.booleanValue();
        } catch (Exception e) {
            AgnUtils.logger().error("isCustomerInGroup: "+e.getMessage());
            answer=false;
        }
        return answer;
    }

    public boolean isCustomerInGroup(int customerID, ApplicationContext con) {
        Interpreter aBsh=AgnUtils.getBshInterpreter(this.companyID, customerID, con);
        if(aBsh==null) {
            return false;
        }

        return this.isCustomerInGroup(aBsh);
    }
    
    public void setDeleted(int deleted) {
    	this.deleted = deleted;
    }
    
    public int getDeleted() {
    	return this.deleted;
    }
}
