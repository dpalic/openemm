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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.Recipient;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.dialect.Dialect;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

/**
 * Handles all kind of operations to be done with subscriber-data.
 * Requires that a valid companyID is set after creating a new instance.
 * @author mhe
 */
public final class RecipientImpl implements Recipient {

    /**
     * Holds value of property customerID.
     */
    private int customerID;

    /**
     * Holds value of property companyID.
     */
    private int companyID;

    /**
     * Holds value of property listBindings.
     */
    private Hashtable listBindings;

    /**
     * Stores information about the profile fields (fieldname and type).
     */
    private Map<String, String> custDBStructure;
    
    /**
     * Holds value of property custDBProfileStructure.
     */
    private Map<String, Map> custDBProfileStructure;

    /**
     * Holds value of property custParameters.
     */
    private Map custParameters;

    /**
     * shows if some information loaded from Database was changed by setter-methods
     */
    private boolean changeFlag = false;

    /** Creates a new instance of Customer */
    public RecipientImpl() {
        this.custParameters=new CaseInsensitiveMap();
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

	if(this.companyID == 0) {
		return false;
	}
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
            System.err.println("Exception:" + e);
            System.err.println(AgnUtils.getStackTrace(e));
        }

        AgnUtils.logger().debug("new customerID: "+this.customerID);

        return returnValue;
    }

	private boolean	isBlank(String s) {
		if(StringUtils.isEmpty(s)) {
			return true;
		}
		if(s.trim().length() <= 0) {
			return true;
		}
		return false;
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
        boolean hasDefault = false;
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

        Iterator<String> i=this.custDBStructure.keySet().iterator();
        while(i.hasNext()) {
            aColumn=i.next();
            ColType=this.custDBStructure.get(aColumn);
            appendIt=false;
			hasDefault = false;
if(!aColumn.equalsIgnoreCase("customer_id")) { 
            if(aColumn.equalsIgnoreCase("creation_date") || aColumn.equalsIgnoreCase("timestamp")) {
            	appendValue=new String("current_timestamp");
                appendColumn=new String(aColumn);
                appendIt=true;
            } else if(ColType.equalsIgnoreCase("DATE")) {
                if(this.getCustParameters(aColumn+"_DAY_DATE")!=null && this.getCustParameters(aColumn+"_MONTH_DATE")!=null && this.getCustParameters(aColumn+"_YEAR_DATE")!=null) {
                    aFormat1=new DecimalFormat("00");
                    aFormat2=new DecimalFormat("0000");
                    try {
                        if(!this.getCustParameters(aColumn+"_DAY_DATE").trim().equals("")) {
                            day=Integer.parseInt(this.getCustParameters(aColumn+"_DAY_DATE"));
                            month=Integer.parseInt(this.getCustParameters(aColumn+"_MONTH_DATE"));
                            year=Integer.parseInt(this.getCustParameters(aColumn+"_YEAR_DATE"));
                            hour = extractInt(aColumn+"_HOUR_DATE", 0);
                            minute = extractInt(aColumn+"_MINUTE_DATE", 0);
                            second = extractInt(aColumn+"_SECOND_DATE", 0);
                            
                            if ( AgnUtils.isOracleDB() ) {
                            	appendValue=new String("to_date('"+ aFormat1.format(day) +"."+aFormat1.format(month)+"."+aFormat2.format(year)+" "+ aFormat1.format(hour)+":"+aFormat1.format(minute)+":"+aFormat1.format(second)+"', 'DD.MM.YYYY HH24:MI:SS')");
                            } else {
                            	appendValue=new String("STR_TO_DATE('"+ aFormat1.format(day) +"."+aFormat1.format(month)+"."+aFormat2.format(year)+" "+ aFormat1.format(hour)+":"+aFormat1.format(minute)+":"+aFormat1.format(second)+"',  '%d.%m.%Y %H:%i:%s')");
                            }
                            appendColumn=new String(aColumn);
                            appendIt=true;
                        } else {
                        	Map tmp = this.custDBProfileStructure.get( aColumn );
				if (tmp != null) {
					String defaultValue = (String)tmp.get( "default" );

					if (!isBlank(defaultValue)) {
						appendValue = "'" + defaultValue + "'";
						hasDefault = true;
					}
                        	}
                        	if (!hasDefault) {                        	
                        		appendValue=new String("null");
                        	}
                        	appendColumn=new String(aColumn);
                        	appendIt=true;
                        }
                    } catch (Exception e1) {
                        AgnUtils.logger().error("insertNewCust: ("+aColumn+ ") "+e1.getMessage());
                    }
                }
                else {
                	Map tmp = this.custDBProfileStructure.get( aColumn );

			if (tmp != null) {
               			String defaultValue = (String)tmp.get( "default" );

				if (!isBlank(defaultValue)) {
					appendValue = "'" + defaultValue + "'";
					hasDefault = true;
				}
                	}
                	if (hasDefault) {
				appendColumn=new String(aColumn);
				appendIt=true;
                	}
                }
            }
            if(ColType.equalsIgnoreCase("INTEGER") || ColType.equalsIgnoreCase("DOUBLE")) {
                aParameter=this.getCustParameters(aColumn);
                if(!StringUtils.isEmpty( aParameter )) {
                    try {
                        intValue=Integer.parseInt(aParameter);
                    } catch (Exception e1) {
                        intValue=0;
                    }
                    appendValue=new String(Integer.toString(intValue));
                    appendColumn=new String(aColumn);
                    appendIt=true;
                }
                else {
                	Map tmp = this.custDBProfileStructure.get( aColumn );

			if (tmp != null) {
				String defaultValue = (String)tmp.get("default");

                		if (!isBlank(defaultValue)) {
					appendValue = defaultValue;
					hasDefault = true;
                		}
                	}
                	if (hasDefault) {    
				appendColumn=new String(aColumn);
				appendIt=true;
                	}
                }
            }
            if(ColType.equalsIgnoreCase("VARCHAR") || ColType.equalsIgnoreCase("CHAR")) {
                aParameter=this.getCustParameters(aColumn);
                if(!StringUtils.isEmpty( aParameter ) ) {
                    appendValue=new String("'" + SafeString.getSQLSafeString(aParameter) + "'");
                    appendColumn=new String(aColumn);
                    appendIt=true;
                }else {
                	Map tmp = this.custDBProfileStructure.get( aColumn );
					if ( tmp != null ) {
                		String defaultValue = (String)tmp.get( "default" );
                		if (!isBlank(defaultValue) ) {
                            appendValue = "'" + defaultValue + "'";
                            hasDefault = true;
                		}
                	}
                	if ( hasDefault ) { 
                		appendColumn=new String(aColumn);
                        appendIt=true;
                	}
                }
            }

            if(appendIt) {
                Columns.append(", ");
                Values.append(", ");
                Columns.append(appendColumn.toLowerCase());
                Values.append(appendValue);
            }
        }
        }

        Columns.append(")");
        Values.append(")");

        insertCust.append(Columns.toString());
        insertCust.append(Values.toString());
        try{
            JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            tmpl.execute(insertCust.toString());
            AgnUtils.logger().debug("insertCust: "+insertCust.toString());
        } catch (Exception e3) {
            AgnUtils.logger().error("insertNewCust: " + e3.getMessage());
            AgnUtils.logger().error(AgnUtils.getStackTrace(e3));
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
     * Load profile data into map.
     * Has to be done before working with customer-data in class instance
     *
     * @return true on success
     */
    public boolean loadCustDBStructure() {
        this.custDBStructure=new CaseInsensitiveMap();
        this.custDBProfileStructure = new Hashtable<String, Map>();
        boolean returnCode=true;
        Map tmp=null;

        try {
            Map tmp2 = org.agnitas.taglib.ShowColumnInfoTag.getColumnInfo(this.applicationContext, this.companyID, "%");

            Iterator it=tmp2.values().iterator();
            while(it.hasNext()) {
                tmp=(Map)it.next();
                String column=(String) tmp.get("column");

                this.custDBStructure.put(column, (String) tmp.get("type"));
                this.custDBProfileStructure.put( column, tmp);
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
    public Map<String, String> getCustDBStructure() {
        return this.custDBStructure;
    }

    /**
     * Setter for property custDBStructure.
     * @param custDBStructure New value of property custDBStructure.
     */
    public void setCustDBStructure(Map<String, String> custDBStructure) {
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

	/** Check security of a request parameter.
	 * Checks the give string for certain patterns that could be used
	 * for exploits.
	 */ 
	private boolean	isSecure(String value) {
		if(value.indexOf('<') >= 0) {
			return false;
		}
		return true;
	}

	/** Copy a date from reqest to database values.
	 * @param req a Map of request parameters (name/value pairs).
	 * @param name the name of the field to copy.
	 * @param suffix a suffix for the parameters in the map.
	 * @return true when the copying was successfull. 
	 */
	private boolean	copyDate(Map req, String name, String suffix)	{
		String[] field={
			"_DAY_DATE", "_MONTH_DATE", "_YEAR_DATE",
			"_HOUR_DATE", "_MINUTE_DATE", "_SECOND_DATE"
		};
		String s=null;

		name=name.toUpperCase();
		for(int c=0;c < field.length; c++) {
			if(req.get(name+field[c]+suffix)!=null) {
				s=new String((String)
						req.get(name+field[c]+suffix));
				setCustParameters(name+field[c], s);
			}
		}
		return true;
	}

	/** Check if the given name is allowed for requests.
	 * This is used to ensure that system columns are not changed by
	 * form requests.
	 * @param name the name to check for allowance.
	 * @return true when field may be writen.
	 */
	private boolean	isAllowedName(String name) {
		name=name.toLowerCase();
		if(name.startsWith("agn")) {
			return false;
		}
		if(name.equals("customer_id") || name.equals("change_date")) {
			return false;
		}
		if(name.equals("timestamp")|| name.equals("creation_date")) {
			return false;
		}
		return true;
	}

	/**
	 * Updates customer data by analyzing given HTTP-Request-Parameters
	 * @return true on success
	 * @param suffix Suffix appended to Database-Column-Names when searching
	 *		for corresponding request parameters
	 * @param req Map containing all HTTP-Request-Parameters as
	 *		key-value-pair.
	 */
	public boolean importRequestParameters(Map src, String suffix) {
		CaseInsensitiveMap req=new CaseInsensitiveMap(src);
		String aValue=null;
		String colType=null;
	
		if(suffix==null) {
			suffix=new String("");
		}
		Iterator e=this.custDBStructure.keySet().iterator();
	
		while(e.hasNext()) {
			//postfix=new String("");
			String aName=new String((String)e.next());
			String name=aName.toUpperCase();

			if(!isAllowedName(aName)) {
				continue;
			}
			colType=(String)this.custDBStructure.get(aName);
			if(colType.equalsIgnoreCase("DATE")) {
				copyDate(req, aName, suffix);
	/*
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
	*/
			} else if(req.get(name+suffix)!=null) {
				aValue=new String((String)req.get(name+suffix));
				if(name.equalsIgnoreCase("EMAIL")) {
					if(aValue.length()==0) {
						aValue=new String(" ");
					}
					aValue=aValue.toLowerCase();
					aValue=aValue.trim();
				} else if(name.length() > 4) {
					if(name.substring(0, 4).equals("SEC_")
						|| name.equals("FIRSTNAME")
						|| name.equals("LASTNAME")) {
						if(!isSecure(aValue)) {
							return false;
						}
					}
				}
				this.setCustParameters(aName, aValue);
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
    public boolean updateBindingsFromRequest(Map params, boolean doubleOptIn, boolean tafWriteBack, String remoteAddr) {
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
                        aEntry.setRemoteAddr(remoteAddr);

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

	public boolean updateBindingsFromRequest(Map params, boolean doubleOptIn, boolean tafWriteBack) {
		return updateBindingsFromRequest(params, doubleOptIn, tafWriteBack, null);
	}
    /**
     * Updates Customer in DB. customerID must be set to a valid id, customer-data is taken from this.customerData
     *
     * @return true on success
     */
    public boolean updateInDB() {
        //String currentTimestamp=AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName();
        String currentTimestamp=AgnUtils.getSQLCurrentTimestampName();
        String aColumn;
        String colType=null;
        boolean appendIt=false;
        StringBuffer updateCust=new StringBuffer("UPDATE customer_" + this.companyID + "_tbl SET "+AgnUtils.changeDateName()+"="+currentTimestamp);
        NumberFormat aFormat1=null;
        NumberFormat aFormat2=null;
        int day, month, year;
        String aParameter=null;
        int intValue;
        String appendValue=null;
        boolean result=true;
        boolean hasDefault = false;

        if(this.customerID==0) {
            AgnUtils.logger().info("updateInDB: creating new customer");
            if(this.insertNewCust()==0) {
                result=false;
            }
        } else {
            if(this.changeFlag) { // only if something has changed

                Iterator<String> i=this.custDBStructure.keySet().iterator();
                while(i.hasNext()) {
                    aColumn=i.next();
                    colType=(String)this.custDBStructure.get(aColumn);
                    appendIt=false;
                    hasDefault = false;

                    if(aColumn.equalsIgnoreCase("customer_id") || aColumn.equalsIgnoreCase("change_date") || aColumn.equalsIgnoreCase("timestamp") || aColumn.equalsIgnoreCase("creation_date")) {
                        continue;
                    }

                    if(colType.equalsIgnoreCase("DATE")) {
                        if((this.getCustParameters(aColumn+"_DAY_DATE")!=null) && (this.getCustParameters(aColumn+"_MONTH_DATE")!=null) && (this.getCustParameters(aColumn+"_YEAR_DATE")!=null)) {
                            aFormat1=new DecimalFormat("00");
                            aFormat2=new DecimalFormat("0000");
                            try {
                                if(!this.getCustParameters(aColumn+"_DAY_DATE").trim().equals("")) {
                                	day=Integer.parseInt(this.getCustParameters(aColumn+"_DAY_DATE"));
                                    month=Integer.parseInt(this.getCustParameters(aColumn+"_MONTH_DATE"));
                                    year=Integer.parseInt(this.getCustParameters(aColumn+"_YEAR_DATE"));
                                    // appendValue=new String(aColumn.toLowerCase()+"='"+ aFormat1.format(year) +"-"+aFormat1.format(month)+"-"+aFormat2.format(day)+"'");
                                    if (AgnUtils.isOracleDB()) {
                                        appendValue=new String(aColumn.toLowerCase()+"=to_date('"+ aFormat1.format(day) +"-"+aFormat1.format(month)+"-"+aFormat2.format(year)+"', 'DD-MM-YYYY')");
                                    } else {
                                    	appendValue=new String(aColumn.toLowerCase()+"=STR_TO_DATE('"+ aFormat1.format(day) +"-"+aFormat1.format(month)+"-"+aFormat2.format(year)+"',  '%d-%m-%Y')");
                                    }
                                    appendIt=true;
                                } else {
                                	Map tmp = this.custDBProfileStructure.get(aColumn);
                                	if (tmp != null) {
                                		String defaultValue = (String)tmp.get("default");
                                		if (!isBlank(defaultValue) && !defaultValue.equals("null")) {
                                            appendValue = aColumn.toLowerCase()+"='" + defaultValue + "'";
                                            hasDefault = true;
                                		}
                                	}
                                	if (!hasDefault) {
                                		appendValue=new String(aColumn.toLowerCase()+"=null");
                                	}
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
                        if(!StringUtils.isEmpty( aParameter ) ){
                            try {
                                intValue=Integer.parseInt(aParameter);
                            } catch (Exception e1) {
                                intValue=0;
                            }
                            appendValue=new String(aColumn.toLowerCase() + "=" + intValue);
                            appendIt=true;
                        } else {
                        	Map tmp = this.custDBProfileStructure.get( aColumn );
                        	if ( tmp != null ) {
                        		String defaultValue = (String)tmp.get( "default" );
                        		if (!isBlank(defaultValue)) {
                                    appendValue = aColumn.toLowerCase()+"=" + defaultValue;
                                    hasDefault = true;
                        		}
                        	}
                        	if ( !hasDefault ) {
                        		appendValue=new String(aColumn.toLowerCase() + "=null");
                        	}
                            appendIt=true;
                        }

                    } else if(colType.equalsIgnoreCase("DOUBLE")) {
                        double dValue;

                        aParameter=(String)this.getCustParameters(aColumn);
                        if(!StringUtils.isEmpty(aParameter)){
                            try {
                                dValue=Double.parseDouble(aParameter);
                            } catch (Exception e1) {
                                dValue=0;
                            }
                            appendValue=new String(aColumn.toLowerCase() + "=" + dValue);
                            appendIt=true;
                        } else {
                        	Map tmp = this.custDBProfileStructure.get( aColumn );
                        	if ( tmp != null ) {
                        		String defaultValue = (String)tmp.get( "default" );
                        		if (!isBlank(defaultValue)) {
                                    appendValue = aColumn.toLowerCase()+"=" + defaultValue;
                                    hasDefault = true;
                        		}
                        	}
                        	if (!hasDefault) {
                        		appendValue=new String(aColumn.toLowerCase() + "=null");
                        	}
                            appendIt=true;
                        }

                    } else /* if(colType.equalsIgnoreCase("VARCHAR") || colType.equalsIgnoreCase("CHAR"))*/ {
                        aParameter=(String)this.getCustParameters(aColumn);
                        if(!StringUtils.isEmpty(aParameter)) {
                            appendValue=new String(aColumn.toLowerCase() + "='" + SafeString.getSQLSafeString(aParameter) + "'");
                            appendIt=true;
                        } else {
                        	Map tmp = this.custDBProfileStructure.get( aColumn );
                        	if ( tmp != null ) {
                        		String defaultValue = (String)tmp.get( "default" );
                        		if (!isBlank(defaultValue)) {
                                    appendValue = aColumn.toLowerCase()+"='" + defaultValue + "'";
                                    hasDefault = true;
                        		}
                        	}
                        	if ( !hasDefault ) {
                        		appendValue=new String(aColumn.toLowerCase() + "=null");
                        	}
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
                    result=false;
                }

            } else {
                AgnUtils.logger().info("updateInDB: nothing changed");
            }
        }

        return result;
    }

    
    /**
     * Extract an int parameter from CustParameters
     *
     * @return the int value or the default value in case of an exception
     * @param column Column-Name
     * @param defaultValue Value to be returned in case of exception
     */
    private int extractInt(String column, int defaultValue) {
		try {
		    return Integer.parseInt( this.getCustParameters( column ) );
		} catch (Exception e1) {
		    return defaultValue;
		}
	}

    /**
     * Find Subscriber by providing a column-name and a value. Only exact machtes possible.
     *
     * @return customerID or 0 if no matching record found
     * @param col Column-Name
     * @param value Value to search for in col
     */
    public int findByKeyColumn(String col, String value) {
        int val=0;
        String aType=null;
        String getCust=null;

        try {
            if(this.custDBStructure==null) {
                this.loadCustDBStructure();
            }
    
            if(col.toLowerCase().equals("email")) {
                value=value.toLowerCase();
            }
    
            aType=(String)this.custDBStructure.get(col);

            if(aType!=null) {
                if(aType.equalsIgnoreCase("DECIMAL") || aType.equalsIgnoreCase("INTEGER") || aType.equalsIgnoreCase("DOUBLE")) {
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
AgnUtils.logger().error("Query: "+getCust);
                JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                // cannot use queryForInt, because of possible existing doublettes
                List<Map<String,Integer>> custList = (List<Map<String,Integer>>) tmpl.queryForList( getCust );
                Map  map=new CaseInsensitiveMap(custList.get(0));

                this.customerID=((Number) map.get("customer_id")).intValue();
            }
        } catch (Exception e) {
            System.err.println("findByKeyColumn: "+e.getMessage());
            System.err.println(AgnUtils.getStackTrace(e));
            this.customerID=0;
        }

        return this.customerID;
    }

    public int findByColumn(String col, String value) {
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
            if(aType.equalsIgnoreCase("VARCHAR") || aType.equalsIgnoreCase("CHAR")) {
                getCust="SELECT CUSTOMER_ID FROM CUSTOMER_" + this.companyID + "_TBL cust WHERE lower(cust."+SafeString.getSQLSafeString(col, 30)+")=lower('"+SafeString.getSQLSafeString(value)+"')";
            } else {
                try {
                    val=Integer.parseInt(value);
                } catch (Exception e) {
                    val=0;
                }
                getCust="SELECT CUSTOMER_ID FROM CUSTOMER_" + this.companyID + "_TBL cust WHERE cust."+SafeString.getSQLSafeString(col, 30)+"="+val;
            }
            try {
                JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                custID=tmpl.queryForInt(getCust);
            } catch (Exception e) {
                custID=0;
            }
        }
        return custID;
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

        if(this.custParameters==null) {
            this.custParameters=new CaseInsensitiveMap();
        }

        String getCust="SELECT * FROM customer_" + this.companyID + "_tbl WHERE customer_id=" + this.customerID;

        if(this.custDBStructure==null) {
            this.loadCustDBStructure();
        }

        DataSource ds=(DataSource)this.applicationContext.getBean("dataSource");
        Connection con=DataSourceUtils.getConnection(ds);

        try {
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
                                this.setCustParameters(aName, new String(""));
                            } else {
                                GregorianCalendar aCal=new GregorianCalendar();
                                aCal.setTime(aTime);
                                this.setCustParameters(aName+"_DAY_DATE", Integer.toString(aCal.get(GregorianCalendar.DAY_OF_MONTH)));
                                this.setCustParameters(aName+"_MONTH_DATE", Integer.toString(aCal.get(GregorianCalendar.MONTH)+1));
                                this.setCustParameters(aName+"_YEAR_DATE", Integer.toString(aCal.get(GregorianCalendar.YEAR)));
                                this.setCustParameters(aName+"_HOUR_DATE", Integer.toString(aCal.get(GregorianCalendar.HOUR_OF_DAY)));
                                this.setCustParameters(aName+"_MINUTE_DATE", Integer.toString(aCal.get(GregorianCalendar.MINUTE)));
                                this.setCustParameters(aName+"_SECOND_DATE", Integer.toString(aCal.get(GregorianCalendar.SECOND)));
                                SimpleDateFormat bdfmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                this.setCustParameters(aName, bdfmt.format(aCal.getTime()));
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

        } catch (Exception e) {
            AgnUtils.logger().error("getCustomerDataFromDb: "+e.getMessage());
        }
        DataSourceUtils.releaseConnection(con, ds);

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
     * Checks if E-Mail-Adress given in customerData-HashMap is valid
     *
     * @return true if E-Mail-Adress is valid
     */
    public boolean emailValid() {
        String email=(String)custParameters.get("email");

        if(email == null) {
            return false;
        }
        email=email.trim();
        if(email == null) {
            return false;
        }

        if(!Pattern.matches("[^<@]+@[^<@.]+[.][^<@]+", email)) {
            return false;
        }
        return true;
    }

    /**
     * Checks if E-Mail-Adress given in customerData-HashMap is registered in blacklist(s)
     *
     * @return true if E-Mail-Adress is blacklisted
     */
    public boolean blacklistCheck() {
        boolean returnValue=false;

        try {
            JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            String email=((String)custParameters.get("email")).trim();
            List list=null;
            String sqlSelect=null;

            sqlSelect="SELECT email FROM cust_ban_tbl WHERE '" + SafeString.getSQLSafeString(email) + "' LIKE email";
            list=tmpl.queryForList(sqlSelect);
            if(list.size() > 0) {
                returnValue=true;
            }
            sqlSelect="SELECT email FROM cust_ban_tbl WHERE '" + SafeString.getSQLSafeString(email) + "' LIKE email";
            list=tmpl.queryForList(sqlSelect);
            if(list.size() > 0) {
                returnValue=true;
            }
        } catch (Exception e) {
            AgnUtils.logger().error(e);
            returnValue=true;
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

	public Map getCustDBProfileStructure() {
		return custDBProfileStructure;
	}

}
