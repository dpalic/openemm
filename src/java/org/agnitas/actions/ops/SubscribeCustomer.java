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
public class SubscribeCustomer extends ActionOperation implements Serializable {
    
    static final long serialVersionUID = 3086814575002603882L;
    
    /**
     * Holds value of property doubleCheck.
     */
    protected boolean doubleCheck=true;
    
    /**
     * Holds value of property keyColumn.
     */
    protected String keyColumn="EMAIL";
    
    /**
     * Holds value of property doubleOptIn.
     */
    protected boolean doubleOptIn=false;
    
    /** 
     * Creates new ActionOperationUpdateCustomer 
     */
    public SubscribeCustomer() {
    }
    
    /**
     * Reads an Object and puts the read fields into allFields
     * Gets keyColumn, doubleCheck and doubleoptIn from allFields
     * throws IOException or ClassNotFoundException
     *
     * @param in inputstream from Object
     */
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        
        allFields=in.readFields();
        this.keyColumn=(String)allFields.get("keyColumn", new String("EMAIL"));
        this.doubleCheck=allFields.get("doubleCheck", true);
        this.doubleOptIn=allFields.get("doubleOptIn", false);
        return;
    }
    
    /**
     * Checks if subscription of customer is blocked
     * Checks customer
     * Checks blacklist
     * Creats user information
     * 
     * @return true==sucess
     * false=error
     * @param con 
     * @param companyID 
     * @param params HashMap containing all available informations
     */
    public boolean executeOperation(ApplicationContext con, int companyID, HashMap params) {
        Integer tmpNum=null;
        Recipient aCust=(Recipient)con.getBean("Recipient");
        String keyVal=null;
        boolean isNewCust=false;
        boolean identifiedByUid=false;
        
        if(params.get("subscribeCustomer")!=null && params.get("subscribeCustomer").equals("no")) {
            return true; // do nothing, manually blocked
        }
        
        aCust.setCompanyID(companyID);
        aCust.loadCustDBStructure();
        
        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            aCust.setCustomerID(tmpNum.intValue());
            identifiedByUid=true;
        }
        
        if(aCust.getCustomerID()==0) {
            if(this.doubleCheck) {
                keyVal=(String)((HashMap)params.get("requestParameters")).get(this.keyColumn.toUpperCase());
                aCust.findByKeyColumn(this.keyColumn, keyVal);
            }
        }
        
        if(aCust.getCustomerID()!=0) {
            aCust.getCustomerDataFromDb();
        } else {
            isNewCust=true;
        }
        
        aCust.importRequestParameters((HashMap)params.get("requestParameters"), null);
        
        if(aCust.blacklistCheck()) {
            return false; // abort, EMAIL is on blacklist
        }
        
        if(!aCust.updateInDB()) {  // return error on failure
            return false;
        }
        
        aCust.loadAllListBindings();
        aCust.updateBindingsFromRequest(params, this.doubleOptIn, identifiedByUid);
        
        if(this.doubleOptIn) {
            params.put("__agn_USER_STATUS", "5"); // next Event-Mailing goes to a user with status 5
        }
        
        params.put("customerID", new Integer(aCust.getCustomerID()));
        
        if(isNewCust && aCust.getCustomerID()!=0) {
            // generate new agnUID
            try {
                UID aUID=(UID)con.getBean("UID");
                aUID.setCompanyID((long)companyID);
                aUID.setCustomerID((long)aCust.getCustomerID());
                CompanyDao dao=(CompanyDao)con.getBean("CompanyDao");
                Company company=dao.getCompany(companyID);
                aUID.setPassword(company.getSecret());
                aUID.setURLID(0);
                aUID.setMailingID(0);
                params.put("agnUID", aUID.makeUID());
            } catch (Exception e) {
                AgnUtils.logger().error("executeOperation: "+e);
            }
        }
        
        return true;
    }
    
    /**
     * Getter for property doubleCheck.
     *
     * @return Value of property doubleCheck.
     */
    public boolean isDoubleCheck() {
        return this.doubleCheck;
    }
    
    /**
     * Setter for property doubleCheck.
     *
     * @param doubleCheck New value of property doubleCheck.
     */
    public void setDoubleCheck(boolean doubleCheck) {
        this.doubleCheck = doubleCheck;
    }
    
    /**
     * Getter for property keyColumn.
     *
     * @return Value of property keyColumn.
     */
    public String getKeyColumn() {
        return this.keyColumn;
    }
    
    /**
     * Setter for property keyColumn.
     *
     * @param keyColumn New value of property keyColumn.
     */
    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }
    
    /**
     * Getter for property doubleOptIn.
     *
     * @return Value of property doubleOptIn.
     */
    public boolean isDoubleOptIn() {
        return this.doubleOptIn;
    }
    
    /**
     * Setter for property doubleOptIn.
     *
     * @param doubleOptIn New value of property doubleOptIn.
     */
    public void setDoubleOptIn(boolean doubleOptIn) {
        this.doubleOptIn = doubleOptIn;
    }
    
}
