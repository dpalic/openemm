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
public class SendMailing extends ActionOperation implements Serializable {
    
    static final long serialVersionUID = 712043294800920235L;
    
    /** 
     * Holds value of property mailingID. 
     */
    protected int mailingID;
    
    /**
     * Holds value of property delayMinutes. 
     */
    protected int delayMinutes;
    
    /**
     * Creates new ActionOperationSendMailing 
     */
    public SendMailing() {
    }
    
    /** Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    public int getMailingID() {
        return mailingID;
    }
    
    /** Setter for property mailingID.
     *
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID) {
        this.mailingID = mailingID;
    }
     
    /**
     * Reads an Object and puts the read fields into allFields
     * Gets mailing id from allFields
     * Tries to get minutes of delay from allFields
     * throws IOException or ClassNotFoundException
     *
     * @param in inputstream from Object
     */
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        
        allFields=in.readFields();
        mailingID=allFields.get("mailingID", 0);
        try {
            delayMinutes=allFields.get("delayMinutes", 0);
        } catch (Exception e) {
            AgnUtils.logger().error("readObject: "+e);
        }
        
        return;
    }
    
    /** Getter for property delayMinutes.
     *
     * @return Value of property delayMinutes.
     */
    public int getDelayMinutes() {
        return this.delayMinutes;
    }
    
    /** Setter for property delayMinutes.
     *
     * @param delayMinutes New value of property delayMinutes.
     */
    public void setDelayMinutes(int delayMinutes) {
        this.delayMinutes = delayMinutes;
    }
    
    /**
     * Checks if customer id, mailing id and user status are filled
     * Sends mailing
     * Logges status (sent or failed)
     *
     * @return true==sucess
     * false=error
     * @param con
     * @param companyID
     * @param params HashMap containing all available informations
     */
    public boolean executeOperation(ApplicationContext con, int companyID, HashMap params) {
        int customerID=0;
        int callerMailingID=0;
        Integer tmpNum=null;
        Mailing aMailing=null;
        MailingDao mDao=(MailingDao)con.getBean("MailingDao");
        boolean exitValue=true;
        String userStatus=null;

        if(params.get("customerID")==null) {
            return false;
        }
        tmpNum=(Integer)params.get("customerID");
        customerID=tmpNum.intValue();
        
        if(params.get("mailingID")!=null) {
            tmpNum=(Integer)params.get("mailingID");
            callerMailingID=tmpNum.intValue();
        }
        
        if(params.get("__agn_USER_STATUS")!=null) {
            userStatus=(String)params.get("__agn_USER_STATUS");
        }
        
        aMailing=mDao.getMailing(this.mailingID, companyID);
        if(aMailing!=null) {
            if(aMailing.sendEventMailing(customerID, delayMinutes, userStatus, null, con)) {
                AgnUtils.logger().info("executeOperation: Mailing "+mailingID+" to "+customerID+" sent");
                exitValue=true;
            } else {
                AgnUtils.logger().error("executeOperation: Mailing "+mailingID+" to "+customerID+" failed");
                exitValue=false;
            }
        }
        return exitValue;
    }
    
}
