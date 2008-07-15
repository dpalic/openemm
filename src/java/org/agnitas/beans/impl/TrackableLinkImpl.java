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

import java.net.*;
import java.util.*;
import java.util.regex.*;
import org.agnitas.beans.TrackableLink;
import org.agnitas.util.*;
import org.agnitas.dao.*;
import org.agnitas.beans.*;
import org.agnitas.actions.*;
import org.springframework.context.*;
import org.springframework.jdbc.core.*;
import javax.sql.DataSource;

/**
 *
 * @author Martin Helff
 */
public class TrackableLinkImpl implements TrackableLink {
    
    protected int companyID;
    protected int id;
    protected int mailingID;
    protected int actionID;
    protected String fullUrl=null;
    
    /** Holds value of property shortname. */
    protected String shortname;
    
    /** Holds value of property usage. */
    protected int usage;
    
    //TimeoutLRUMap keyCache=new TimeoutLRUMap(AgnUtils.getDefaultIntValue("rdir.keys.maxCache"), AgnUtils.getDefaultIntValue("rdir.keys.maxCacheTimeMillis"));
    //TimeoutLRUMap baseUrlCache=new TimeoutLRUMap(AgnUtils.getDefaultIntValue("rdir.keys.maxCache"), AgnUtils.getDefaultIntValue("rdir.keys.maxCacheTimeMillis"));
    
    /** Creates new TrackableLink */
    public TrackableLinkImpl() {
    }
    
    public void setCompanyID(int aid) {
        companyID=aid;
    }
    
    public void setId(int id) {
        this.id=id;
    }
    
    public void setMailingID(int aid) {
        mailingID=aid;
    }
    
    public void setActionID(int aid) {
        actionID=aid;
    }
    
    public void setFullUrl(String url) {
        if(url==null)
            url=new String("");
        
        fullUrl=new String(url);
    }
    
    public String getFullUrl() {
        if(fullUrl==null) {
            return new String("");
        }
        
        return fullUrl;
    }
    
    public boolean performLinkAction(HashMap params, int customerID, ApplicationContext con) {
        boolean exitValue=true;
        EmmAction aAction=null;
        EmmActionDao actionDao=(EmmActionDao)con.getBean("EmmActionDao");
        
        if(actionID==0) {
            return exitValue;
        }
        
        aAction=actionDao.getEmmAction(this.actionID, this.companyID);
        
        if(params==null) {
            params=new HashMap();
        }
        params.put("customerID", new Integer(customerID));
        params.put("mailingID", new Integer(this.mailingID));
        
        exitValue=aAction.executeActions(con, params);
        
        return exitValue;
    }
    
    public String personalizeLink(int customerID, String orgUID, ApplicationContext con) {
        boolean exitValue=true;
        Matcher aMatch=null;
        Pattern aRegExp=null;
        String newUrl=new String(this.fullUrl);
        int sm=0;
        int em=0;
        LinkedList allColumnNames=new LinkedList();
        int colNum=-1;
        
        String tmpString=null;
        boolean includeUID=false;
        boolean includeMailingID=false;
        boolean includeUrlID=false;
        Iterator aIt=null;
        String tmpColname=null;
        
        try {
            aRegExp=Pattern.compile("##[^#]+##");
            aMatch=aRegExp.matcher(newUrl);
            while(true) {
                if(!aMatch.find(em)) {
                    break;
                }
                sm=aMatch.start();
                em=aMatch.end();
                
                if(newUrl.substring(sm, em).equalsIgnoreCase("##AGNUID##")) {
                    includeUID=true;
                    continue;
                }
                if(newUrl.substring(sm, em).equalsIgnoreCase("##MAILING_ID##")) {
                    includeMailingID=true;
                    continue;
                }
                if(newUrl.substring(sm, em).equalsIgnoreCase("##URL_ID##")) {
                    includeUrlID=true;
                    continue;
                }
                colNum++;
                allColumnNames.add(new String(newUrl.substring(sm+2, em-2).toLowerCase()));
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("personalizeLink: " + e.getMessage());
            exitValue=false;
        }
        if(exitValue && colNum>=0) {
            Recipient cust=(Recipient)con.getBean("Recipient");
            cust.setCompanyID(this.companyID);
            cust.setCustomerID(customerID);
            cust.loadCustDBStructure();
            cust.getCustomerDataFromDb();
            
            aIt=allColumnNames.iterator();
            while(aIt.hasNext()) {
                try {
                    tmpColname=(String)aIt.next();
                    tmpString=cust.getCustParameters(tmpColname);
                    if(tmpString==null) {
                        tmpString=new String("");
                    }
                    newUrl=SafeString.replaceIgnoreCase(newUrl, "##"+tmpColname+"##", URLEncoder.encode(tmpString, "UTF-8"));
                    // newUrl=SafeString.replace(newUrl, "##"+tmpColname+"##", tmpString);
                } catch (Exception e) {
                    AgnUtils.logger().error("personalizeLink: "+e.getMessage());
                }
            }
        }
        
        if(includeUID) {
            try {
                newUrl=SafeString.replaceIgnoreCase(newUrl, "##AGNUID##", URLEncoder.encode(orgUID, "UTF-8"));
            } catch (Exception e) {
                AgnUtils.logger().error("personalizeLink: "+e.getMessage());
            }
        }
        
        if(includeMailingID) {
            try {
                newUrl=SafeString.replaceIgnoreCase(newUrl, "##MAILING_ID##", URLEncoder.encode(Integer.toString(this.mailingID), "UTF-8"));
            } catch (Exception e) {
                AgnUtils.logger().error("personalizeLink: "+e.getMessage());
            }
        }
        
        if(includeUrlID) {
            try {
                newUrl=SafeString.replaceIgnoreCase(newUrl, "##URL_ID##", URLEncoder.encode(Integer.toString(this.id), "UTF-8"));
            } catch (Exception e) {
                AgnUtils.logger().error("personalizeLink: "+e.getMessage());
            }
        }
        
        return newUrl;
    }
    
    public boolean logClickInDB(int customerID, String remoteAddr, ApplicationContext con) {
        boolean exitValue=true;
        JdbcTemplate tmpl=new JdbcTemplate((DataSource)con.getBean("dataSource"));
        int i=0;
        String sqlUpdate="insert into rdir_log_tbl (customer_id, url_id, company_id, ip_adr, mailing_id) values (?, ?, ?, ?, ?)";
        try {
            tmpl.update(sqlUpdate, new Object[] {new Integer(customerID), new Integer(this.id), new Integer(this.companyID), remoteAddr, new Integer(this.mailingID)});
        } catch (Exception e) {
            AgnUtils.logger().error("logClickInDB: ("+i+") "+e.getMessage());
            exitValue=false;
        }
 
        return exitValue;
    }
    
    /** 
     * Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    public String getShortname() {
        return this.shortname;
    }
    
    /** 
     * Setter for property shortname.
     *
     * @param shortname New value of property shortname.
     */
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    /**
     * Getter for property usage.
     *
     * @return Value of property usage.
     */
    public int getUsage() {
        return this.usage;
    }
    
    /** 
     * Setter for property usage.
     *
     * @param usage New value of property usage.
     */
    public void setUsage(int usage) {
        this.usage = usage;
    }
    
    /**
     * Getter for property urlID.
     *
     * @return Value of property urlID.
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * Getter for property actionID.
     *
     * @return Value of property actionID.
     */
    public int getActionID() {
        return this.actionID;
    }
    
    /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    public int getCompanyID() {
        return this.companyID;
    }
    
    /**
     * Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    public int getMailingID() {
        return this.mailingID;
    }
    
    public boolean equals(Object obj) {
        return ((TrackableLink)obj).hashCode()==this.hashCode();
    }
    
    public int hashCode() {
        return getFullUrl().hashCode();
    }

    /**
     * Holds value of property relevance.
     */
    protected int relevance;

    /**
     * Getter for property relevance.
     *
     * @return Value of property relevance.
     */
    public int getRelevance() {
        return this.relevance;
    }

    /**
     * Setter for property relevance.
     *
     * @param relevance New value of property relevance.
     */
    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }
    
}
