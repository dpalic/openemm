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

package org.agnitas.beans.impl;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.Recipient;
import org.agnitas.dao.RecipientDao;
import org.apache.commons.collections.map.CaseInsensitiveMap;

/**
 * Handles all kind of operations to be done with subscriber-data.
 * Requires that a valid companyID is set after creating a new instance.
 * @author mhe
 */
public class RecipientImpl implements Recipient {

	public boolean blacklistCheck() {
		RecipientDao dao = (RecipientDao) applicationContext.getBean("RecipientDao");
		return dao.blacklistCheck(((String) this.getCustParameters().get("email")).trim());
	}
	
	public boolean updateInDB() {
		RecipientDao dao = (RecipientDao) applicationContext.getBean("RecipientDao");
		return dao.updateInDB(this);
	}
	
	public int findByColumn(String col, String value) {
		RecipientDao dao = (RecipientDao) applicationContext.getBean("RecipientDao");
		return dao.findByColumn(companyID, col, value);
	}
	
	public int findByKeyColumn(String col, String value) {
		RecipientDao dao = (RecipientDao) applicationContext.getBean("RecipientDao");
		return dao.findByKeyColumn(this, col, value);
	}
	
	public void deleteCustomerDataFromDb() {
		RecipientDao dao = (RecipientDao) applicationContext.getBean("RecipientDao");
		dao.deleteCustomerDataFromDb(companyID, customerID);
	}
	
	public int findByUserPassword(String userCol, String userValue, String passCol, String passValue) {
		RecipientDao dao = (RecipientDao) applicationContext.getBean("RecipientDao");
		return dao.findByUserPassword(companyID, userCol, userValue, passCol, passValue);
	}
	
	public Map getCustomerDataFromDb() {
		RecipientDao dao = (RecipientDao) applicationContext.getBean("RecipientDao");
		this.custParameters = dao.getCustomerDataFromDb(companyID, customerID);
		return this.custParameters;
	}
	
	public Hashtable loadAllListBindings() {
		RecipientDao dao = (RecipientDao) applicationContext.getBean("RecipientDao");
		return (listBindings=dao.loadAllListBindings(companyID, customerID));
	}
	
	public int insertNewCust() {
		RecipientDao dao = (RecipientDao) applicationContext.getBean("RecipientDao");
		return dao.insertNewCust(this);
	}
	
    /**
     * Holds value of property customerID.
     */
    private int customerID;

    /**
     * Holds value of property companyID.
     */
    protected int companyID;

    /**
     * Holds value of property listBindings.
     */
    private Hashtable listBindings;

    /**
     * Stores information about the profile fields (fieldname and type).
     */
    protected Map<String, String> custDBStructure;
    
    /**
     * Holds value of property custDBProfileStructure.
     */
    protected Map<String, Map> custDBProfileStructure;

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
     * Load structure of Customer-Table for the given Company-ID in member variable "companyID".
     * Load profile data into map.
     * Has to be done before working with customer-data in class instance
     *
     * @return true on success
     */
    public boolean loadCustDBStructure() {
        this.custDBStructure = new CaseInsensitiveMap();
        this.custDBProfileStructure = new Hashtable<String, Map>();
        boolean returnCode = true;
        Map tmp = null;

        try {
            Map tmp2 = org.agnitas.taglib.ShowColumnInfoTag.getColumnInfo(this.applicationContext, this.companyID, "%");

            Iterator it = tmp2.values().iterator();
            while(it.hasNext()) {
                tmp = (Map)it.next();
                String column = (String) tmp.get("column");

                this.custDBStructure.put(column, (String) tmp.get("type"));
                this.custDBProfileStructure.put( column, tmp);
            }
        } catch (Exception e) {
            returnCode = false;

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

    public boolean isChangeFlag() {
		return changeFlag;
	}

	public void setChangeFlag(boolean changeFlag) {
		this.changeFlag = changeFlag;
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
				String fieldname = name+field[c]+suffix;
				Object o = req.get(fieldname);
				s = o.toString();
				setCustParameters(fieldname, s);
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
			String aName = new String((String)e.next());
			String name = aName.toUpperCase();

			if(!isAllowedName(aName)) {
				continue;
			}
			colType=(String)this.custDBStructure.get(aName);
			if(colType.equalsIgnoreCase("DATE")) {
				copyDate(req, aName, suffix);
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
				if(name.equalsIgnoreCase("DATASOURCE_ID")) {
					if(this.getCustParameters(aName) == null) {
						this.setCustParameters(aName, aValue);
					}
				} else {
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
        RecipientDao dao = (RecipientDao) applicationContext.getBean("RecipientDao");
        
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
            sourceCust.setCustParameters(dao.getCustomerDataFromDb(this.companyID, this.customerID));
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
                dao.updateInDB(sourceCust);
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
		RecipientDao dao = (RecipientDao) applicationContext.getBean("RecipientDao");

		return dao.getAllMailingLists(customerID, companyID);
	}

	public Map getCustDBProfileStructure() {
		return custDBProfileStructure;
	}

	public void setCustDBProfileStructure(Map<String, Map> custDBProfileStructure) {
		this.custDBProfileStructure = custDBProfileStructure;
	}

}
