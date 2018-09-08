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
 ********************************************************************************/package org.agnitas.webservice;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 *
 * @author mhe (mhe@agnitas.de)
 */
public class EmmSoapClient {
	private static final transient Logger logger = Logger.getLogger(EmmSoapClient.class);
    
    public static void main(String args[]) throws Exception {
        // try {
        String command = null;
        int intResult = 0;
        if ( args.length > 0 ) {
        	command = args[0];
        }
        else {
        	command = "help";
        }
        
        if(command.equals("help")) {
            if (logger.isInfoEnabled()) logger.info("newEmailMailing username password shortname description mailinglistID targetID mailingType templateID emailSubject emailSender emailCharset emailLinefeed emailFormat");
            if (logger.isInfoEnabled()) logger.info("newEmailMailingWithReply username password shortname description mailinglistID targetID mailingType templateID emailSubject emailSender emailReply emailCharset emailLinefeed emailFormat");
            if (logger.isInfoEnabled()) logger.info("updateEmailMailing username password mailingID shortname description mailinglistID targetID mailingType emailSubject emailSender emailReply emailCharset emailLinefeed emailFormat");
            if (logger.isInfoEnabled()) logger.info("insertContent username password mailingID blockName blockContent targetID priority");
            if (logger.isInfoEnabled()) logger.info("deleteContent username password contentID");
            if (logger.isInfoEnabled()) logger.info("sendMailing username password mailingID sendGroup sendTime stepping blocksize");
            if (logger.isInfoEnabled()) logger.info("addMailinglist username password shortname description");
            if (logger.isInfoEnabled()) logger.info("deleteMailinglist username password mailinglistID");
            if (logger.isInfoEnabled()) logger.info("addSubscriber username password doubleCheck keyColumn overwrite paramNames paramValues");
            if (logger.isInfoEnabled()) logger.info("getSubscriber username password customerID");
            if (logger.isInfoEnabled()) logger.info("findSubscriber username password keyColumn value");
            if (logger.isInfoEnabled()) logger.info("deleteSubscriber username password customerID");
            if (logger.isInfoEnabled()) logger.info("setSubscriberBinding username password customerID mailinglistID mediatype status bindingType remark exitMailingID");
            if (logger.isInfoEnabled()) logger.info("getSubscriberBinding username password customerID mailinglistID mediatype");
            if (logger.isInfoEnabled()) logger.info("updateSubscriber username password customerID paramNames paramValues");
        }
        
        EmmWebService_Service aLoc = new EmmWebService_ServiceLocator();
        EmmWebService_Port aService = aLoc.getEmmWebService(new URL("http://localhost:8080/emm_webservice"));
        // EmmWebService_Port aService = aLoc.getEmmWebService(new URL("http://172.16.13.183:8080/emm_webservice"));
        
        
        if(command.equals("newEmailMailing")) {
            if (logger.isInfoEnabled()) logger.info("Subject: " + args[9]);
            if (logger.isInfoEnabled()) logger.info("Sender: " + args[10]);
            if (logger.isInfoEnabled()) logger.info("Charset: " + args[11]);
            intResult = aService.newEmailMailing(args[1], args[2], args[3], args[4], to_int(args[5]), toStringArrayType(args[6]), to_int(args[7]), to_int(args[8]), args[9], args[10], args[11], to_int(args[12]), to_int(args[13]));
            if (logger.isInfoEnabled()) logger.info("MAILING_ID: " + intResult);
        }
        
        if(command.equals("newEmailMailingWithReply")) {
            if (logger.isInfoEnabled()) logger.info("Subject: " + args[9]);
            if (logger.isInfoEnabled()) logger.info("Sender: " + args[10]);
            if (logger.isInfoEnabled()) logger.info("Charset: " + args[12]);
            intResult = aService.newEmailMailingWithReply(args[1], args[2], args[3], args[4], to_int(args[5]), toStringArrayType(args[6]), to_int(args[7]), to_int(args[8]), args[9], args[10], args[11], args[12], to_int(args[13]), to_int(args[14]));
            if (logger.isInfoEnabled()) logger.info("MAILING_ID: " + intResult);
        }

        if(command.equals("updateEmailMailing")) {
        	boolean boolResult;
            if (logger.isInfoEnabled()) logger.info("Subject: " + args[9]);
            if (logger.isInfoEnabled()) logger.info("Sender: " + args[10]);
            if (logger.isInfoEnabled()) logger.info("Charset: " + args[12]);
            boolResult = aService.updateEmailMailing(args[1], args[2], to_int(args[3]), args[4], args[5], to_int(args[6]), toStringArrayType(args[7]), to_int(args[8]), args[9], args[10], args[11], args[12],to_int(args[13]), to_int(args[14]));
            if(boolResult) {
            	if (logger.isInfoEnabled()) logger.info("update successfull");
            } else {
            	if (logger.isInfoEnabled()) logger.info("update failed");
            }
        }
        
        if(command.equals("insertContent")) {
            intResult = aService.insertContent(args[1], args[2], to_int(args[3]), args[4], args[5], to_int(args[6]), to_int(args[7]));
            if (logger.isInfoEnabled()) logger.info("chars written: " + intResult);
        }
        
        if(command.equals("deleteContent")) {
            intResult = aService.deleteContent(args[1], args[2], to_int(args[3]));
            if (logger.isInfoEnabled()) logger.info("status: " + intResult);
        }
        
        if(command.equals("sendMailing")) {
            intResult = aService.sendMailing(args[1], args[2], to_int(args[3]), args[4], to_int(args[5]), to_int(args[6]), to_int(args[7]));
            if (logger.isInfoEnabled()) logger.info("status: " + intResult);
        }
        
        if(command.equals("addMailinglist")) {
        	intResult = aService.addMailinglist(args[1], args[2], args[3], args[4]);
        	if (logger.isInfoEnabled()) logger.info("status: " + intResult);
        }
        
        if(command.equals("deleteMailinglist")) {
        	intResult = aService.deleteMailinglist(args[1], args[2], to_int(args[3]));
        	if (logger.isInfoEnabled()) logger.info("status: " + intResult);
        }
        
        if(command.equals("getSubscriber")) {
            SubscriberData aSubscriber = null;
            aSubscriber = aService.getSubscriber(args[1], args[2], to_int(args[3]));
            String[] tmpKeys = aSubscriber.getParamNames().getX();
            String[] tmpValues = aSubscriber.getParamValues().getX();
            if (logger.isInfoEnabled()) logger.info("customerID=" + aSubscriber.getCustomerID());
            for(int i=0; i<tmpKeys.length; i++) {
                if (logger.isInfoEnabled()) logger.info(tmpKeys[i] + ": " + tmpValues[i]);
            }
        }
        
        if(command.equals("findSubscriber")) {
            intResult = aService.findSubscriber(args[1], args[2], args[3], args[4]);
            if (logger.isInfoEnabled()) logger.info("CUSTOMER_ID: " + intResult);
        }
        
        if(command.equals("addSubscriber")) {
            intResult = aService.addSubscriber(args[1], args[2], to_boolean(args[3]), args[4], to_boolean(args[5]), toStringArrayType(args[6]), toStringArrayType(args[7]));
            if (logger.isInfoEnabled()) logger.info("CUSTOMER_ID: " + intResult);
        }
        
        if(command.equals("deleteSubscriber")) {
            intResult = aService.deleteSubscriber(args[1], args[2], to_int(args[3]));
            if (logger.isInfoEnabled()) logger.info("status: " + intResult);
        }
        
        if(command.equals("setSubscriberBinding")) {
            intResult = aService.setSubscriberBinding(args[1], args[2], to_int(args[3]), to_int(args[4]), to_int(args[5]), to_int(args[6]), args[7], args[8], to_int(args[9]));
            if (logger.isInfoEnabled()) logger.info("status: " + intResult);
        }
        
        if(command.equals("getSubscriberBinding")) {
            String result = aService.getSubscriberBinding(args[1], args[2], to_int(args[3]), to_int(args[4]), to_int(args[5]));
            if (logger.isInfoEnabled()) logger.info("binding: " + result);
        }
        
        if(command.equals("updateSubscriber")) {
        	boolean boolResult = aService.updateSubscriber(args[1], args[2], to_int(args[3]), toStringArrayType(args[4]), toStringArrayType(args[5]));
        	if(boolResult) {
            	if (logger.isInfoEnabled()) logger.info("update successfull");
            } else {
            	if (logger.isInfoEnabled()) logger.info("update failed");
            }
        }
    }
    
    public static int to_int(String val) {
        int result = 0;
        try {
            result = Integer.parseInt(val);
        } catch (Exception e) {
            result = 0;
        }
        
        return result;
    }
    
    public static boolean to_boolean(String val) {
        boolean result = false;
        
        if(val.toLowerCase().equals("true")) {
            result = true;
        }
        if(val.toLowerCase().equals("1")) {
            result = true;
        }
        
        return result;
    }
    
    public static StringArrayType toStringArrayType(String val) {
        StringArrayType aType = new StringArrayType();
        String[] result = null;
        List<String> aList = new ArrayList<String>();
        int startPos = 0;
        int endPos = 0;
        String aVal = null;
        
        while((endPos = val.indexOf(';', startPos)) != -1) {
            aVal = val.substring(startPos, endPos);
            aList.add(aVal);
            startPos = endPos + 1;
        }
        if(startPos<val.length()) {
            aList.add(val.substring(startPos));
        }
        
        result = new String[aList.size()];
        int i = 0;
        Iterator<String> aIt = aList.iterator();
        while(aIt.hasNext()) {
            result[i] = aIt.next();
            i++;
        }
        
        aType.setX(result);
        
        return aType;
    }
}
