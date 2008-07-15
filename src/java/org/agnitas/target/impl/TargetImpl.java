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

package org.agnitas.target.impl;

import javax.sql.*;
import java.io.*;
import bsh.*;
import org.agnitas.target.*;
import org.agnitas.util.AgnUtils;
import org.springframework.context.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.rowset.*;

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
    

    
}
