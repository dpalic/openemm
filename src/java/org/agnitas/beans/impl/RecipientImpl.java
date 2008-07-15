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

import java.util.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.rowset.*;
import org.springframework.jdbc.object.*;
import org.springframework.jdbc.support.*;
import org.springframework.jdbc.datasource.*;
import org.apache.commons.collections.map.*;
import java.text.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import javax.sql.*;
import org.agnitas.beans.BindingEntry;
import org.agnitas.util.*;
import org.agnitas.beans.*;
import org.springframework.orm.hibernate3.*;
import org.hibernate.dialect.*;
import org.hibernate.cfg.*;

/**
 * Handles all kind of operations to be done with subscriber-data.
 * Requires that a valid companyID is set after creating a new instance.
 * @author mhe
 */
public class RecipientImpl implements Recipient {
    
    /**
     * Holds value of property customerID.
     */
    protected int customerID;
    
    /**
     * Holds value of property companyID.
     */
    protected int companyID;
    
    /**
     * Holds value of property listBindings.
     */
    protected Hashtable listBindings;
    
    /**
     * Holds value of property custDBStructure.
     */
    protected Hashtable custDBStructure;
    
    /**
     * Holds value of property custParameters.
     */
    protected Map custParameters;
    
    /**
     * shows if some information loaded from Database was changed by setter-methods
     */
    protected boolean changeFlag = false;
    
    /** Creates a new instance of Customer */
    public RecipientImpl() {
        this.custParameters=new HashMap();
    }
    
    /**
     * Getter for property customerID.
     * @return Value of property customerID.
     */
    public int getCustomerID() {
        return this.customerID;
    }
    
    /**
     * Setter for property customerID.
     * @param customerID New value of property customerID.
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    
    /**
     * Getter for property companyID.
     * @return Value of property companyID.
     */
    public int getCompanyID() {
        return this.companyID;
    }
    
    /**
     * Setter for property companyID.
     * @param companyID New value of property companyID.
     */
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
    
    /**
     * Getter for property listBindings.
     * @return Value of property listBindings.
     */
    public Hashtable getListBindings() {
        return this.listBindings;
    }
    
    /**
     * Setter for property listBindings.
     * @param listBindings New value of property listBindings.
     */
    public void setListBindings(Hashtable listBindings) {
        this.listBindings = listBindings;
    }
    
    /**
     * Gets new customerID from Database-Sequence an stores it in member-variable "customerID"
     * 
     * @return true on success
     */
    public boolean getNewCustomerID() {
        boolean returnValue=true;
        String sqlStatement=null;
        this.customerID=0;
        
        Dialect dialect=AgnUtils.getHibernateDialect();
        
        try {
            if(dialect.supportsSequences()) {
                JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                sqlStatement="select customer_" + this.companyID + "_tbl_seq.nextval FROM dual";
                this.customerID=tmpl.queryForInt(sqlStatement);
            } else {
                //sqlStatement="update customer_" + this.companyID + "_tbl_seq set customer_id=customer_id+1";
                sqlStatement="insert into customer_" + this.companyID + "_tbl_seq () values ()";
                SqlUpdate updt=new SqlUpdate((DataSource)this.applicationContext.getBean("dataSource"), sqlStatement);
                updt.setReturnGeneratedKeys(true);
                GeneratedKeyHolder key=new GeneratedKeyHolder();
                this.customerID=updt.update(null, key);
                this.customerID=key.getKey().intValue();
            }
        } catch (Exception e) {
            returnValue=false;
            this.customerID=0;
        }
        
        AgnUtils.logger().debug("new customerID: "+this.customerID);
        
        return returnValue;
    }
    
    /**
     * Inserts new customer record in Database with a fresh customer-id
     * 
     * @return true on success
     */
    public int insertNewCust() {
        StringBuffer Columns=new StringBuffer("(");
        StringBuffer Values=new StringBuffer(" VALUES (");
        String aColumn=null;
        String aParameter=null;
        String ColType=null;
        int intValue=0;
        int day, month, year, hour, minute, second=0;
        StringBuffer insertCust=new StringBuffer("INSERT INTO customer_" + companyID + "_tbl ");
        boolean exitNow=false;
        boolean appendIt=false;
        String appendColumn=null;
        String appendValue=null;
        NumberFormat aFormat1=null;
        NumberFormat aFormat2=null;
        
        if(this.custDBStructure==null) {
            this.loadCustDBStructure();
        }
        
        if(!this.getNewCustomerID()) {
            return 0;
        }
        
        Columns.append("customer_id");
        Values.append(Integer.toString(this.customerID));
        
        Enumeration e=this.custDBStructure.keys();
        while(e.hasMoreElements()) {
            aColumn=(String)e.nextElement();
            ColType=(String)this.custDBStructure.get(aColumn);
            appendIt=false;
            
            if(ColType.equalsIgnoreCase("DATE")) {
                if(this.getCustParameters(aColumn+"_DAY_DATE")!=null && this.getCustParameters(aColumn+"_MONTH_DATE")!=null && this.getCustParameters(aColumn+"_YEAR_DATE")!=null) {
                    aFormat1=new DecimalFormat("00");
                    aFormat2=new DecimalFormat("0000");
                    try {
                        if(!this.getCustParameters(aColumn+"_DAY_DATE").trim().equals("")) {
                            day=Integer.parseInt(this.getCustParameters(aColumn+"_DAY_DATE"));
                            month=Integer.parseInt(this.getCustParameters(aColumn+"_MONTH_DATE"));
                            year=Integer.parseInt(this.getCustParameters(aColumn+"_YEAR_DATE"));
                            try {
                                hour=Integer.parseInt(this.getCustParameters(aColumn+"_HOUR_DATE"));
                            } catch (Exception e1) {
                                hour=0;
                            }
                            try {
                                minute=Integer.parseInt(this.getCustParameters(aColumn+"_MINUTE_DATE"));
                            } catch (Exception e2) {
                                minute=0;
                            }
                            try {
                                second=Integer.parseInt(this.getCustParameters(aColumn+"_SECOND_DATE"));
                            } catch (Exception e3) {
                                second=0;
                            }
                            appendValue=new String("to_date('"+ aFormat1.format(day) +"."+aFormat1.format(month)+"."+aFormat2.format(year)+" "+ aFormat1.format(hour)+":"+aFormat1.format(minute)+":"+aFormat1.format(second)+"', 'DD.MM.YYYY HH24:MI:SS')");
                            appendColumn=new String(aColumn);
                            appendIt=true;
                        } else {
                            appendValue=new String("null");
                            appendColumn=new String(aColumn);
                            appendIt=true;
                        }
                    } catch (Exception e1) {
                        AgnUtils.logger().error("insertNewCust: ("+aColumn+ ") "+e1.getMessage());
                    }
                }
            }
            if(ColType.equalsIgnoreCase("INTEGER") || ColType.equalsIgnoreCase("DOUBLE")) {
                aParameter=this.getCustParameters(aColumn);
                if(aParameter!=null) {
                    try {
                        intValue=Integer.parseInt(aParameter);
                    } catch (Exception e1) {
                        intValue=0;
                    }
                    appendValue=new String(Integer.toString(intValue));
                    appendColumn=new String(aColumn);
                    appendIt=true;
                }
            }
            if(ColType.equalsIgnoreCase("VARCHAR") || ColType.equalsIgnoreCase("CHAR")) {
                aParameter=this.getCustParameters(aColumn);
                if(aParameter!=null) {
                    appendValue=new String("'" + SafeString.getSQLSafeString(aParameter) + "'");
                    appendColumn=new String(aColumn);
                    appendIt=true;
                }
            }
            
            if(appendIt) {
                Columns.append(", ");
                Values.append(", ");
                Columns.append(appendColumn.toLowerCase());
                Values.append(appendValue);
            }
        }
        Columns.append(", creation_date");
        Values.append(", "+AgnUtils.getSQLCurrentTimestamp());
        Columns.append(")");
        Values.append(")");
        
        insertCust.append(Columns.toString());
        insertCust.append(Values.toString());
        try{
            JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            tmpl.execute(insertCust.toString());
        } catch (Exception e3) {
            AgnUtils.logger().error("insertNewCust: " + e3.getMessage());
            exitNow=true;
            this.customerID=0;
            // return 0;
        }
        
        if(exitNow==true)
            return 0;
        
        return this.customerID;
    }
    
    /**
     * Load structure of Customer-Table for the given Company-ID in member variable "companyID".
     * Has to be done before working with customer-data in class instance
     * 
     * @return true on success
     */
    public boolean loadCustDBStructure() {
        this.custDBStructure=new Hashtable();
        boolean returnCode=true;
        Map tmp=null;
        
        try {
            TreeMap tmp2 = org.agnitas.taglib.ShowColumnInfoTag.getColumnInfo(this.applicationContext, this.companyID, "%");
            
            Iterator it=tmp2.values().iterator();
            while(it.hasNext()) {
                tmp=(Map)it.next();
                String column=(String) tmp.get("column");

                if(!column.equals("creation_date")) {
                    this.custDBStructure.put(column, tmp.get("type"));
                }
            }
        } catch (Exception e) {
            returnCode=false;
        }
        
        return returnCode;
    }
    
    /**
     * Getter for property custDBStructure.
     * @return Value of property custDBStructure.
     */
    public Hashtable getCustDBStructure() {
        return this.custDBStructure;
    }
    
    /**
     * Setter for property custDBStructure.
     * @param custDBStructure New value of property custDBStructure.
     */
    public void setCustDBStructure(Hashtable custDBStructure) {
        this.custDBStructure = custDBStructure;
    }
    
    /**
     * Indexed getter for property custParameters.
     * @return Value of the property at <CODE>key</CODE>.
     * @param key Name of Database-Field
     */
    public String getCustParameters(String key) {
        return (String)(this.custParameters.get(key));
    }
    
    /**
     * Getter for property custParameters.
     * @return Value of property custParameters.
     */
    public Map getCustParameters() {
        return this.custParameters;
    }
    
    /**
     * Indexed setter for property custParameters.
     * @param aKey identifies field in customer-record, must be the same like in Database
     * @param custParameters New value of the property at <CODE>aKey</CODE>.
     */
    public void setCustParameters(String aKey, String custParameters) {
        String key=new String(aKey);
        String aValue=null;
        
        if(key.endsWith("_DAY_DATE")) {
            key=key.substring(0, key.length()-"_DAY_DATE".length());
        }
        if(key.endsWith("_MONTH_DATE")) {
            key=key.substring(0, key.length()-"_MONTH_DATE".length());
        }
        if(key.endsWith("_YEAR_DATE")) {
            key=key.substring(0, key.length()-"_YEAR_DATE".length());
        }
        if(key.endsWith("_HOUR_DATE")) {
            key=key.substring(0, key.length()-"_HOUR_DATE".length());
        }
        if(key.endsWith("_MINUTE_DATE")) {
            key=key.substring(0, key.length()-"_MINUTE_DATE".length());
        }
        if(key.endsWith("_SECOND_DATE")) {
            key=key.substring(0, key.length()-"_SECOND_DATE".length());
        }

        if(this.custDBStructure.containsKey(key)) {
            aValue=null;
            if(this.custParameters.get(aKey)!=null) {
                aValue=(String)this.custParameters.get(aKey);
            }
            if(!custParameters.equals(aValue)) {
                this.changeFlag=true;
                this.custParameters.put(aKey, custParameters);
            }
        }
    }
    
    /**
     * Setter for property custParameters.
     * @param custParameters New value of property custParameters.
     */
    public void setCustParameters(Map custParameters) {
        /* At the moment replacing the map always invalidates values.
         * This should be changed to check for changed values.
         */
        this.custParameters = custParameters;
        this.changeFlag=true;
    }
    
    /**
     * Updates customer data by analyzing given HTTP-Request-Parameters
     * @return true on success
     * @param suffix Suffix appended to Database-Column-Names when searching for corresponding request parameters
     * @param req Map containing all HTTP-Request-Parameters as key-value-pair.
     */
    public boolean importRequestParameters(Map src, String suffix) {
        CaseInsensitiveMap req=new CaseInsensitiveMap(src);
        String aName=null;
        String aValue=null;
        String colType=null;
        
        if(suffix==null) {
            suffix=new String("");
        }
        
        Iterator e=this.custDBStructure.keySet().iterator();

        while(e.hasNext()) {
            //postfix=new String("");
            aName=new String((String)e.next());
            if(aName.startsWith("agn") || aName.equalsIgnoreCase("customer_id") || aName.equalsIgnoreCase("change_date") || aName.equalsIgnoreCase("timestamp") || aName.equalsIgnoreCase("creation_date") || aName.startsWith("AGN_TAF_")) {
                continue;
            }
            colType=(String)this.custDBStructure.get(aName);
            if(colType.equalsIgnoreCase("DATE")) {
                if(req.get(aName.toUpperCase()+"_DAY_DATE"+suffix)!=null) {
                    aValue=new String((String)req.get(aName+"_DAY_DATE"+suffix));
                    this.setCustParameters(aName+"_DAY_DATE", aValue);
                }
                if(req.get(aName.toUpperCase()+"_MONTH_DATE"+suffix)!=null) {
                    aValue=new String((String)req.get(aName+"_MONTH_DATE"+suffix));
                    this.setCustParameters(aName+"_MONTH_DATE", aValue);
                }
                if(req.get(aName.toUpperCase()+"_YEAR_DATE"+suffix)!=null) {
                    aValue=new String((String)req.get(aName+"_YEAR_DATE"+suffix));
                    this.setCustParameters(aName+"_YEAR_DATE", aValue);
                }
                if(req.get(aName.toUpperCase()+"_HOUR_DATE"+suffix)!=null) {
                    aValue=new String((String)req.get(aName+"_HOUR_DATE"+suffix));
                    this.setCustParameters(aName+"_HOUR_DATE", aValue);
                }
                if(req.get(aName.toUpperCase()+"_MINUTE_DATE"+suffix)!=null) {
                    aValue=new String((String)req.get(aName+"_MINUTE_DATE"+suffix));
                    this.setCustParameters(aName+"_MINUTE_DATE", aValue);
                }
                if(req.get(aName.toUpperCase()+"_SECOND_DATE"+suffix)!=null) {
                    aValue=new String((String)req.get(aName+"_SECOND_DATE"+suffix));
                    this.setCustParameters(aName+"_SECOND_DATE", aValue);
                }
            } else {
                if(req.get(aName.toUpperCase()+suffix)!=null) {
                    aValue=new String((String)req.get(aName.toUpperCase()+suffix));
                    if(aName.equalsIgnoreCase("email")) {
                        if(aValue.length()==0) {
                            aValue=new String(" ");
                        }
                        aValue=aValue.toLowerCase();
                        aValue=aValue.trim();
                    }
                    this.setCustParameters(aName, aValue);
                }
            }
        }
        
        return true;
    }
    
    /**
     * Updates internal Datastructure for Mailinglist-Bindings of this customer by analyzing HTTP-Request-Parameters
     * 
     * @return true on success
     * @param tafWriteBack if true, eventually existent TAF-Information will be written back to source-customer
     * @param params Map containing all HTTP-Request-Parameters as key-value-pair.
     * @param doubleOptIn true means use Double-Opt-In
     */
    public boolean updateBindingsFromRequest(Map params, boolean doubleOptIn, boolean tafWriteBack) {
        String aName=null;
        HttpServletRequest request=(HttpServletRequest)params.get("_request");
        Map req=(Map)params.get("requestParameters");
        String postfix=null;
        int mailinglistID=0;
        int mediatype=0;
        int subscribeStatus=0;
        String tmpName=null;
        boolean changeit=false;
        Hashtable mList=null;
        BindingEntry aEntry=null;
        int mailingID=0;
        
        try {
            Integer tmpNum=(Integer)params.get("mailingID");
            mailingID=tmpNum.intValue();
        } catch (Exception e) {
            mailingID=0;
        }
        
        Iterator e=req.keySet().iterator();
        while(e.hasNext()) {
            postfix=new String("");
            aName=new String((String)e.next());
            if(aName.startsWith("agnSUBSCRIBE")) {
                mediatype=0;
                mailinglistID=0;
                subscribeStatus=0;
                aEntry=null;
                if(aName.length()>"agnSUBSCRIBE".length()) {
                    postfix=aName.substring("agnSUBSCRIBE".length());
                }
                try {
                    subscribeStatus=Integer.parseInt((String)req.get(aName));
                } catch (Exception e1) {
                    subscribeStatus=0;
                }
                
                tmpName="agnMAILINGLIST"+postfix;
                try {
                    mailinglistID=Integer.parseInt((String)req.get(tmpName));
                } catch (Exception e1) {
                    mailinglistID=0;
                }
                
                tmpName="agnMEDIATYPE"+postfix;
                try {
                    mediatype=Integer.parseInt((String)req.get(tmpName));
                } catch (Exception e1) {
                    mediatype=0;
                }
                
                // find BindingEntry or create new one
                mList=(Hashtable)this.listBindings.get(Integer.toString(mailinglistID));
                if(mList!=null) {
                    aEntry=(BindingEntry)mList.get(Integer.toString(mediatype));
                }
                
                if(aEntry!=null) {
                    changeit=false;
                    // put changes in db
                    int oldStatus=aEntry.getUserStatus();
                    switch(oldStatus) {
                        case BindingEntry.USER_STATUS_ADMINOUT:
                        case BindingEntry.USER_STATUS_BOUNCED:
                        case BindingEntry.USER_STATUS_OPTOUT:
                            if(subscribeStatus==1) {
                                changeit=true;
                            }
                            break;
                            
                        case BindingEntry.USER_STATUS_WAITING_FOR_CONFIRM:
                        case BindingEntry.USER_STATUS_ACTIVE:
                            if(subscribeStatus==0) {
                                changeit=true;
                            }
                            break;
                    }
                    if(changeit) {
                        switch(subscribeStatus) {
                            case 0:
                                aEntry.setUserStatus(BindingEntry.USER_STATUS_OPTOUT);
                                if(mailingID!=0) {
                                    aEntry.setUserRemark("Opt-Out-Mailing: " + mailingID);
                                    aEntry.setExitMailingID(mailingID);
                                } else {
                                    aEntry.setUserRemark("User-Opt-Out: "+request.getRemoteAddr());
                                    aEntry.setExitMailingID(0);
                                }
                                break;
                                
                            case 1:
                                if(!doubleOptIn) {
                                    aEntry.setUserStatus(BindingEntry.USER_STATUS_ACTIVE);
                                    aEntry.setUserRemark("Opt-In-IP: " + request.getRemoteAddr());
                                } else {
                                    aEntry.setUserStatus(BindingEntry.USER_STATUS_WAITING_FOR_CONFIRM);
                                    aEntry.setUserRemark("Opt-In-IP: " + request.getRemoteAddr());
                                }
                                break;
                        }
                        aEntry.updateStatusInDB(this.companyID);
                    }
                } else {
                    if(subscribeStatus==1) {
                        aEntry=(BindingEntry) applicationContext.getBean("BindingEntry");
                        aEntry.setCustomerID(this.customerID);
                        aEntry.setMediaType(mediatype);
                        aEntry.setMailinglistID(mailinglistID);
                        aEntry.setUserType(BindingEntry.USER_TYPE_WORLD);
                        
                        if(!doubleOptIn) {
                            aEntry.setUserStatus(BindingEntry.USER_STATUS_ACTIVE);
                            aEntry.setUserRemark("Opt-In-IP: " + request.getRemoteAddr());
                            if(tafWriteBack) {  // only if there was never a binding for adress...
                                this.tellFriendWriteback(); // make taf-writeback to originating customer
                            }
                        } else {
                            aEntry.setUserStatus(BindingEntry.USER_STATUS_WAITING_FOR_CONFIRM);
                            aEntry.setUserRemark("Opt-In-IP: " + request.getRemoteAddr());
                        }
                        
                        aEntry.insertNewBindingInDB(this.companyID);
                        if(mList==null) {
                            mList=new Hashtable();
                            this.listBindings.put(Integer.toString(mailinglistID), mList);
                        }
                        mList.put(Integer.toString(mediatype), aEntry);
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Updates Customer in DB. customerID must be set to a valid id, customer-data is taken from this.customerData
     * 
     * @return true on success
     */
    public boolean updateInDB() {
        String currentTimestamp=AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName();
        String aColumn;
        String colType=null;
        boolean appendIt=false;
        StringBuffer updateCust=new StringBuffer("UPDATE customer_" + this.companyID + "_tbl SET "+AgnUtils.changeDateName()+"="+currentTimestamp);
        NumberFormat aFormat1=null;
        NumberFormat aFormat2=null;
        int day, month, year, hour, minute, second=0;
        String aParameter=null;
        int intValue;
        String appendValue=null;
        boolean exitNow=false;
        boolean updateClobs=false;
        boolean result=true;
        
        if(this.customerID==0) {
            AgnUtils.logger().info("updateInDB: creating new customer");
            if(this.insertNewCust()==0) {
                result=false;
            }
        } else {
            if(this.changeFlag) { // only if something has changed
                
                Enumeration e=this.custDBStructure.keys();
                while(e.hasMoreElements()) {
                    aColumn=(String)e.nextElement();
                    colType=(String)this.custDBStructure.get(aColumn);
                    appendIt=false;
                    
                    if(aColumn.equalsIgnoreCase("customer_id") || aColumn.equalsIgnoreCase("change_date") || aColumn.equalsIgnoreCase("timestamp") || aColumn.equalsIgnoreCase("creation_date")) {
                        continue;
                    }

                    if(colType.equalsIgnoreCase("DATE")) {
                        if((this.getCustParameters(aColumn+"_DAY_DATE")!=null) && (this.getCustParameters(aColumn+"_MONTH_DATE")!=null) && (this.getCustParameters(aColumn+"_YEAR_DATE")!=null)) {
                            aFormat1=new DecimalFormat("00");
                            aFormat2=new DecimalFormat("0000");
                            try {
                                if(!this.getCustParameters(aColumn+"_DAY_DATE").trim().equals("")) {
                                    try {
                                        hour=Integer.parseInt(this.getCustParameters(aColumn+"_HOUR_DATE"));
                                    } catch (Exception e1) {
                                        hour=0;
                                    }
                                    try {
                                        minute=Integer.parseInt(this.getCustParameters(aColumn+"_MINUTE_DATE"));
                                    } catch (Exception e2) {
                                        minute=0;
                                    }
                                    try {
                                        second=Integer.parseInt(this.getCustParameters(aColumn+"_SECOND_DATE"));
                                    } catch (Exception e3) {
                                        second=0;
                                    }
                                    day=Integer.parseInt(this.getCustParameters(aColumn+"_DAY_DATE"));
                                    month=Integer.parseInt(this.getCustParameters(aColumn+"_MONTH_DATE"));
                                    year=Integer.parseInt(this.getCustParameters(aColumn+"_YEAR_DATE"));
                                    appendValue=new String(aColumn.toLowerCase()+"='"+ aFormat1.format(year) +"-"+aFormat1.format(month)+"-"+aFormat2.format(day)+"'");
                                    appendIt=true;
                                } else {
                                    appendValue=new String(aColumn.toLowerCase()+"=null");
                                    appendIt=true;
                                }
                            } catch (Exception e1) {
                                AgnUtils.logger().error("updateInDB: Could not parse Date "+aColumn + " because of "+e1.getMessage());
                            }
                        } else {
                            AgnUtils.logger().error("updateInDB: Parameter missing!");
                        }
                    } else if(colType.equalsIgnoreCase("INTEGER")) {
                        aParameter=(String)this.getCustParameters(aColumn);
                        if(aParameter!=null) {
                            try {
                                intValue=Integer.parseInt(aParameter);
                            } catch (Exception e1) {
                                intValue=0;
                            }
                            appendValue=new String(aColumn.toLowerCase() + "=" + intValue);
                            appendIt=true;
                        }
                    } else if(colType.equalsIgnoreCase("DOUBLE")) {
                        double dValue;

                        aParameter=(String)this.getCustParameters(aColumn);
                        if(aParameter!=null) {
                            try {
                                dValue=Double.parseDouble(aParameter);
                            } catch (Exception e1) {
                                dValue=0;
                            }
                            appendValue=new String(aColumn.toLowerCase() + "=" + dValue);
                            appendIt=true;
                        }
                    } else /* if(colType.equalsIgnoreCase("VARCHAR") || colType.equalsIgnoreCase("CHAR"))*/ {
                        aParameter=(String)this.getCustParameters(aColumn);
                        if(aParameter!=null) {
                            appendValue=new String(aColumn.toLowerCase() + "='" + SafeString.getSQLSafeString(aParameter) + "'");
                            appendIt=true;
                        }
                    }
                    
                    if(appendIt) {
                        updateCust.append(", ");
                        updateCust.append(appendValue);
                    }
                }
                
                updateCust.append(" WHERE customer_id=" + this.customerID);
                
                try{
                    JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));

                    AgnUtils.logger().info("updateInDB: " + updateCust.toString());
                    tmpl.execute(updateCust.toString());
                } catch (Exception e3) {
                    // Util.SQLExceptionHelper(e3,dbConn);
                    AgnUtils.logger().error("updateInDB: " + e3.getMessage());
                    exitNow=true;
                    result=false;
                }
                
            } else {
                AgnUtils.logger().info("updateInDB: nothing changed");
            }
        }
        
        return result;
    }
    
    /**
     * Find Subscriber by providing a column-name and a value. Only exact machtes possible.
     * 
     * @return customerID or 0 if no matching record found
     * @param col Column-Name
     * @param value Value to search for in col
     */
    public int findByKeyColumn(String col, String value) {
        int custID=0;
        int val=0;
        String aType=null;
        String getCust=null;
        if(this.custDBStructure==null) {
            this.loadCustDBStructure();
        }
        
        if(col.toLowerCase().equals("email")) {
            value=value.toLowerCase();
        }
        
        aType=(String)this.custDBStructure.get(col.toLowerCase());
        if(aType!=null) {
            if(aType.equalsIgnoreCase("DECIMAL") || aType.equalsIgnoreCase("INTEGER")) {
                try {
                    val=Integer.parseInt(value);
                } catch (Exception e) {
                    val=0;
                }
                getCust="SELECT customer_id FROM customer_" + this.companyID + "_tbl cust WHERE cust."+SafeString.getSQLSafeString(col, 30)+"="+val;
            }
            
            if(aType.equalsIgnoreCase("VARCHAR") || aType.equalsIgnoreCase("CHAR")) {
                getCust="SELECT customer_id FROM customer_" + this.companyID + "_tbl cust WHERE cust."+SafeString.getSQLSafeString(col, 30)+"='"+SafeString.getSQLSafeString(value)+"'";
            }
            try {
                JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                this.customerID=tmpl.queryForInt(getCust);
            } catch (Exception e) {
                AgnUtils.logger().error("findByKeyColumn: "+e.getMessage());
                this.customerID=0;
            }
        }
        
        return this.customerID;
    }
    
    /**
     * Find Subscriber by providing a username and password. Only exact machtes possible.
     * 
     * @return customerID or 0 if no matching record found
     * @param userCol Column-Name for Username
     * @param userValue Value for Username
     * @param passCol Column-Name for Password
     * @param passValue Value for Password
     */
    public int findByUserPassword(String userCol, String userValue, String passCol, String passValue) {
        int custID=0;
        int val=0;
        Integer aType=null;
        String getCust=null;
        
        if(userCol.toLowerCase().equals("email")) {
            userValue=userValue.toLowerCase();
        }
        
        getCust="SELECT customer_id FROM customer_" + this.companyID + "_tbl cust WHERE cust."+SafeString.getSQLSafeString(userCol, 30)+"='"+SafeString.getSQLSafeString(userValue)+"' AND cust."+SafeString.getSQLSafeString(passCol, 30)+"='"+SafeString.getSQLSafeString(passValue)+"'";
        
        try {
            JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            this.customerID=tmpl.queryForInt(getCust);
        } catch (Exception e) {
            this.customerID=0;
            AgnUtils.logger().error("findByUserPassword: "+e.getMessage());
        }
        
        return this.customerID;
    }
    
    /**
     * Load complete Subscriber-Data from DB. customerID must be set first for this method.
     * 
     * @return Map with Key/Value-Pairs of customer data
     */
    public Map getCustomerDataFromDb() {
        String aName=null;
        String aValue=null;
        int a;
        java.sql.Timestamp aTime=null;
        DateFormat aFormat=SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM);
        
        if(this.custParameters==null) {
            this.custParameters=new HashMap();
        }
        
        String getCust="SELECT * FROM customer_" + this.companyID + "_tbl WHERE customer_id=" + this.customerID;

        if(this.custDBStructure==null) {
            this.loadCustDBStructure();
        }
        
        try {
            DataSource ds=(DataSource)this.applicationContext.getBean("dataSource");
            Connection con=DataSourceUtils.getConnection(ds);
            Statement stmt=con.createStatement();
            ResultSet rset=stmt.executeQuery(getCust);
            AgnUtils.logger().info("getCustomerDataFromDb: "+getCust);

            if(rset.next()) {
                ResultSetMetaData aMeta=rset.getMetaData();

                for(a=1; a<=aMeta.getColumnCount(); a++) {
                    aValue=null;
                    aName=new String(aMeta.getColumnName(a).toLowerCase());
                    switch(aMeta.getColumnType(a)) {
                        case java.sql.Types.TIMESTAMP:
                        case java.sql.Types.TIME:
                        case java.sql.Types.DATE:
                            aTime=rset.getTimestamp(a);
                            if(aTime==null) {
                                this.setCustParameters(aName+"_DAY_DATE", new String(""));
                                this.setCustParameters(aName+"_MONTH_DATE", new String(""));
                                this.setCustParameters(aName+"_YEAR_DATE", new String(""));
                                this.setCustParameters(aName+"_HOUR_DATE", new String(""));
                                this.setCustParameters(aName+"_MINUTE_DATE", new String(""));
                                this.setCustParameters(aName+"_SECOND_DATE", new String(""));
                            } else {
                                GregorianCalendar aCal=new GregorianCalendar();
                                aCal.setTime(aTime);
                                this.setCustParameters(aName+"_DAY_DATE", Integer.toString(aCal.get(GregorianCalendar.DAY_OF_MONTH)));
                                this.setCustParameters(aName+"_MONTH_DATE", Integer.toString(aCal.get(GregorianCalendar.MONTH)+1));
                                this.setCustParameters(aName+"_YEAR_DATE", Integer.toString(aCal.get(GregorianCalendar.YEAR)));
                                this.setCustParameters(aName+"_HOUR_DATE", Integer.toString(aCal.get(GregorianCalendar.HOUR_OF_DAY)));
                                this.setCustParameters(aName+"_MINUTE_DATE", Integer.toString(aCal.get(GregorianCalendar.MINUTE)));
                                this.setCustParameters(aName+"_SECOND_DATE", Integer.toString(aCal.get(GregorianCalendar.SECOND)));
                            }
                            break;
                            
                        default:
                            aValue=rset.getString(a);
                            if(aValue==null) {
                                aValue=new String("");
                            }
                            this.setCustParameters(aName, aValue);
                            break;
                    }
                }
            }
            rset.close();
            stmt.close();
            DataSourceUtils.releaseConnection(con, ds);
        } catch (Exception e) {
            AgnUtils.logger().error("getCustomerDataFromDb: "+e.getMessage());
        }
        
        this.changeFlag=false;
        
        return this.custParameters;
    }
    
    /**
     * Delete complete Subscriber-Data from DB. customerID must be set first for this method.
     */
    public void deleteCustomerDataFromDb() {
        String sql=null;
        Object[] params=new Object[] { new Integer(this.customerID) };

        try {
            JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));

            sql="DELETE FROM customer_" + this.companyID + "_binding_tbl WHERE customer_id=?";
            tmpl.update(sql, params);

            sql="DELETE FROM customer_" + this.companyID + "_tbl WHERE customer_id=?";
            tmpl.update(sql, params);
        } catch (Exception e) {
            AgnUtils.logger().error("deleteCustomerDataFromDb: "+e.getMessage());
        }
    }
    
    /**
     * Loads complete Mailinglist-Binding-Information for given customer-id from Database
     * 
     * @return Map with key/value-pairs as combinations of mailinglist-id and BindingEntry-Objects
     */
    public Hashtable loadAllListBindings() {
        this.listBindings=new Hashtable(); // MailingList_ID as keys
        Hashtable mTable=new Hashtable(); // Media_ID as key, contains rest of data (user type, status etc.)
        
        BindingEntry aEntry=null;
        
        int tmpMLID = 0;
        
        try {
            String sqlGetLists="SELECT mailinglist_id, user_type, user_status, user_remark, "+AgnUtils.changeDateName()+", mediatype FROM customer_" + this.companyID + "_binding_tbl WHERE customer_id=" +
                    this.customerID + " ORDER BY mailinglist_id, mediatype";
            JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            List list=tmpl.queryForList(sqlGetLists);
            Iterator i=list.iterator();
            
            while(i.hasNext()) {
                Map map=(Map) i.next();
                int listID=((Number) map.get("mailinglist_id")).intValue();
                Integer mediaType=new Integer(((Number) map.get("mediatype")).intValue());
 
                aEntry=(BindingEntry) applicationContext.getBean("BindingEntry");
                aEntry.setCustomerID(this.customerID);
                aEntry.setMailinglistID(listID);
                aEntry.setUserType((String) map.get("user_type"));
                aEntry.setUserStatus(((Number) map.get("user_status")).intValue());
                aEntry.setUserRemark((String) map.get("user_remark"));
                aEntry.setChangeDate((java.sql.Timestamp) map.get(AgnUtils.changeDateName()));
                aEntry.setMediaType(mediaType.intValue());
                
                if(tmpMLID != listID) {
                    if(tmpMLID!=0) {
                        this.listBindings.put(new Integer(tmpMLID).toString(), mTable);
                        mTable=new Hashtable();
                        mTable.put(mediaType.toString(), aEntry);
                        tmpMLID=listID;
                    } else {
                        mTable.put(mediaType.toString(), aEntry);
                        tmpMLID=listID;
                    }
                } else {
                    mTable.put(mediaType.toString(), aEntry);
                }
            }
            
            this.listBindings.put(new Integer(tmpMLID).toString() , mTable);
            
        } catch (Exception e) {
            AgnUtils.logger().error("loadAllListBindings: "+e.getMessage());
            return null;
        }
        
        return this.listBindings;
    }
    
    /**
     * Iterates through already loaded Mailinglist-Informations and checks if subscriber is active on at least one mailinglist
     * @return true if subscriber is active on a mailinglist
     */
    public boolean isActiveSubscriber() {
        boolean returnValue=false;
        Enumeration tmpList=null;
        BindingEntry tmpEntry=null;
        
        if(this.listBindings!=null) {
            Enumeration allBindings=this.listBindings.elements();
            while(allBindings.hasMoreElements()) {
                tmpList=((Hashtable)allBindings.nextElement()).elements();
                while(tmpList.hasMoreElements()) {
                    tmpEntry=(BindingEntry)tmpList.nextElement();
                    if(tmpEntry.getUserStatus()==BindingEntry.USER_STATUS_ACTIVE) {
                        returnValue=true;
                        break;
                    }
                }
            }
        }
        
        return returnValue;
    }
    
    /**
     * Checks if E-Mail-Adress given in customerData-HashMap is registered in blacklist(s)
     * 
     * @return true if E-Mail-Adress is blacklisted
     */
    public boolean blacklistCheck() {
        boolean returnValue=false;
        String email=null;
        String sqlSelect=null;
        
        try {
            email=((String)custParameters.get("email")).trim();
            sqlSelect=new String("SELECT email FROM cust_ban_tbl WHERE company_id=" + this.companyID + " and '" +
                    SafeString.getSQLSafeString(email) + "' LIKE email");
        } catch (Exception e) {
            returnValue=true;
        }
        
        if(returnValue==false) {
            try {
                JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                List list=tmpl.queryForList(sqlSelect);

                if(list.size() > 0) {
                    returnValue=true;
                }
            } catch (Exception e) {
                returnValue=true;
            }
        }
        
        return returnValue;
    }
    
    /**
     * Writes TAF-Info back to source-customer record for tracking purposes
     * 
     * @return always true
     */
    protected boolean tellFriendWriteback() {
        boolean result=true;
        Recipient sourceCust=null;
        int custID=0;
        int tafNum=0;
        String tmp=null;
        String custStr=null;
        
        // add check if fields exist in db-structure!
        if(!this.custDBStructure.containsKey("AGN_TAF_SOURCE") || !this.custDBStructure.containsKey("AGN_TAF_NUMBER") || !this.custDBStructure.containsKey("AGN_TAF_CUSTOMER_IDS")) {
            return true;
        }
        
        if(this.getCustParameters("AGN_TAF_SOURCE")!=null) {
            try {
                custID=Integer.parseInt((String)this.getCustParameters("AGN_TAF_SOURCE"));
            } catch (Exception e) {
                custID=0;
            }
        }
        
        if(custID!=0) {
            sourceCust=(Recipient)this.applicationContext.getBean("Recipient");
            sourceCust.setCompanyID(this.companyID);
            sourceCust.setCustomerID(custID);
            sourceCust.loadCustDBStructure();
            sourceCust.getCustomerDataFromDb();
            if(sourceCust.getCustParameters("AGN_TAF_CUSTOMER_IDS")!=null) {
                tmp=(String)sourceCust.getCustParameters("AGN_TAF_CUSTOMER_IDS");
            } else {
                tmp=new String("");
            }
            custStr=new String(" "+this.customerID+";");
            if(tmp.indexOf(custStr)==-1) {
                tmp=new String(tmp+custStr);
                sourceCust.setCustParameters("AGN_TAF_CUSTOMER_IDS", tmp);
                
                
                try {
                    tafNum=Integer.parseInt((String)sourceCust.getCustParameters("AGN_TAF_NUMBER"));
                } catch (Exception e) {
                    tafNum=0;
                }
                tafNum++;
                sourceCust.setCustParameters("AGN_TAF_NUMBER", Integer.toString(tafNum));
                sourceCust.updateInDB();
            }
        }
        
        return result;
    }
    
    /**
     * resets internal customer-parameter hashmap.
     */
    public void resetCustParameters() {
        this.custParameters.clear();
    }
    
    /**
     * Holds value of property applicationContext.
     */
    protected org.springframework.context.ApplicationContext applicationContext;
    
    /**
     * Setter for property applicationContext.
     * @param applicationContext New value of property applicationContext.
     */
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) {
        
        this.applicationContext = applicationContext;
    }

    public Map getAllMailingLists() {
        JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
        String sql="SELECT mailinglist_id, user_type, user_status, user_remark, "+AgnUtils.changeDateName()+", mediatype FROM customer_" + companyID + "_binding_tbl WHERE customer_id=" + customerID + " ORDER BY mailinglist_id, mediatype";
AgnUtils.logger().info("getAllMailingLists: "+sql);
        List list=tmpl.queryForList(sql);
        Iterator i=list.iterator();
        BindingEntry entry=null;
        Map result=new HashMap();

        while(i.hasNext()) {
            Map map=(Map) i.next();
            int listID=((Number) map.get("mailinglist_id")).intValue();
            int mediaType=((Number) map.get("mediatype")).intValue();
            Map sub=(Map) result.get(new Integer(listID));

            if(sub == null) {
                sub=new HashMap();
            }
            entry=(BindingEntry) applicationContext.getBean("BindingEntry");
            entry.setCustomerID(customerID);
            entry.setMailinglistID(listID);
            entry.setUserType((String) map.get("user_type"));
            entry.setUserStatus(((Number) map.get("user_status")).intValue());
            entry.setUserRemark((String) map.get("user_remark"));
            entry.setChangeDate((java.sql.Timestamp) map.get(AgnUtils.changeDateName()));
            entry.setMediaType(mediaType);
            sub.put(new Integer(mediaType), entry);
            result.put(new Integer(listID), sub);
        } 
        return result;
    }

}
