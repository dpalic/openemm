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
import org.agnitas.beans.*;
import org.agnitas.dao.*;
import org.springframework.context.*;
import org.springframework.jdbc.core.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.agnitas.actions.ActionOperation;

/**
 *
 * @author Martin Helff
 */
public class ActivateDoubleOptIn extends ActionOperation implements Serializable {
    
    static final long serialVersionUID = 7821487725578716506L;
    
    /** 
     * Creates new ActionOperationUpdateCustomer 
     */
    public ActivateDoubleOptIn() {
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
        return;
    }
    
    /**
     * Checks if customer id and mailing id are filled
     * Checks user status
     *
     * @return true==sucess
     * false=error
     * @param con
     * @param companyID
     * @param params HashMap containing all available informations
     */
    public boolean executeOperation(ApplicationContext con, int companyID, HashMap params) {
        int customerID=0;
        int mailingID=0;
        Integer tmpNum=null;
        Recipient aCust=(Recipient)con.getBean("Recipient");
        boolean returnValue=false;
        HttpServletRequest request=(HttpServletRequest)params.get("_request");
        Mailing aMailing=null;

        aCust.setCompanyID(companyID);
        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            customerID=tmpNum.intValue();
        }
        
        if(params.get("mailingID")!=null) {
            tmpNum=(Integer)params.get("mailingID");
            mailingID=tmpNum.intValue();
        }
        
        if(customerID!=0 && mailingID!=0) {
            aCust.setCustomerID(customerID);
            aCust.loadCustDBStructure();
            aCust.loadAllListBindings();
            
            MailingDao mailingDao=(MailingDao)con.getBean("MailingDao");
            aMailing=mailingDao.getMailing(mailingID, companyID);
            
            int mailinglistID=aMailing.getMailinglistID();
            Hashtable aTbl=aCust.getListBindings();
            
            if(aTbl.containsKey(new Integer(mailinglistID).toString())) {
                Hashtable aTbl2=(Hashtable)aTbl.get(new Integer(mailinglistID).toString());
                if(aTbl2.containsKey(new Integer(BindingEntry.MEDIATYPE_EMAIL).toString())) {
                    BindingEntry aEntry=(BindingEntry)aTbl2.get(new Integer(BindingEntry.MEDIATYPE_EMAIL).toString());
                    switch(aEntry.getUserStatus()) {
                        case BindingEntry.USER_STATUS_WAITING_FOR_CONFIRM:
                            aEntry.setUserStatus(BindingEntry.USER_STATUS_ACTIVE);
                            aEntry.setUserRemark("Opt-In-IP: " + request.getRemoteAddr());
                            aEntry.updateStatusInDB(companyID);
                            returnValue=true;
                            break;
                            
                        case BindingEntry.USER_STATUS_ACTIVE:
                            returnValue=true;
                            break;
                            
                        default:
                            returnValue=false;
                    }
                }
            }
        }
        
        return returnValue;
    }
    
}
