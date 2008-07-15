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

package org.agnitas.actions.ops;

import java.io.*;
import java.util.*;
import org.agnitas.actions.ActionOperation;
import org.springframework.context.*;
import org.agnitas.beans.*;
import org.agnitas.util.*;
import org.agnitas.dao.*;

/**
 *
 * @author Martin Helff
 */
public class GetCustomer extends ActionOperation implements Serializable {
    
    static final long serialVersionUID = -7318143901798712109L;
    
    /**
     * Holds value of property loadAlways.
     */
    protected boolean loadAlways;
    
    /** 
     * Creates new ActionOperationUpdateCustomer
     */
    public GetCustomer() {
    }
    
    /**
     * Reads an Object and puts the read fields into allFields
     * throws IOException or ClassNotFoundException
     *
     * @param in inputstream from Object
     */
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        
        allFields=in.readFields();
        this.loadAlways=allFields.get("loadAlways", false);
        return;
    }
    
    /**
     * Checks if customerID is filled
     * Fills customer data and customer bindings into params
     *
     * @return true==sucess
     * false=error
     * @param con
     * @param companyID
     * @param params HashMap containing all available informations
     */
    public boolean executeOperation(ApplicationContext con, int companyID, HashMap params) {
        int customerID=0;
        Integer tmpNum=null;
        Recipient aCust=(Recipient)con.getBean("Recipient");
        boolean returnValue=false;
        
        aCust.setCompanyID(companyID);
        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            customerID=tmpNum.intValue();
        }
        
        if(customerID!=0) {
            aCust.setCustomerID(customerID);
            aCust.loadCustDBStructure();
            aCust.getCustomerDataFromDb();
            aCust.loadAllListBindings();
            if(this.loadAlways || aCust.isActiveSubscriber()) {
                if(!aCust.getCustParameters().isEmpty()) {
                    params.put("customerData", aCust.getCustParameters());
                    params.put("customerBindings",aCust.getListBindings());
                    returnValue=true;
                }
            }
        }
        
        return returnValue;
    }
    
    /**
     * Getter for property loadAlways.
     *
     * @return Value of property loadAlways.
     */
    public boolean isLoadAlways() {
        return this.loadAlways;
    }
    
    /**
     * Setter for property loadAlways.
     *
     * @param loadAlways New value of property loadAlways.
     */
    public void setLoadAlways(boolean loadAlways) {
        this.loadAlways = loadAlways;
    }
    
}
