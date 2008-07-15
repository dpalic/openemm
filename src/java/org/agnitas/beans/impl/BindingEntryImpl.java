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

import java.util.List;
import java.util.Map;

import org.agnitas.beans.BindingEntry;
import org.agnitas.util.AgnUtils;
import org.springframework.jdbc.core.JdbcTemplate;

/** Class holds information about a Customers "Binding" to a Mailinglist
 *
 * @author mhe
 */
public class BindingEntryImpl implements BindingEntry {
    
    /** Mailinglist ID for this BindingEntry
     */
    protected int mailinglistID;
    protected int customerID;
    protected int exitMailingID;
    protected String userType;
    protected int userStatus;
    protected String userRemark;
    protected java.util.Date changeDate;
    
    /** Holds value of property mediaType. */
    protected int mediaType;
    
    /** Creates new, empty BindingEntry
     */
    public BindingEntryImpl() {
        mailinglistID=0;
        customerID=0;
        userType=new String("W");
        userStatus=0;
        userRemark=new String("");
        mediaType=BindingEntry.MEDIATYPE_EMAIL;
    }
    
/*    public BindingEntryImpl(int ml, int ci, String ut, int us, String ur) {
        super();
        setMailinglistID(ml);
        setCustomerID(ci);
        setUserType(ut);
        setUserStatus(us);
        setUserRemark(ur);
    }
*/
    
    public void setMailinglistID(int ml) {
        mailinglistID=ml;
    }
    
    public void setExitMailingID(int mi) {
        exitMailingID=mi;
    }
    
    public int getExitMailingID() {
        return exitMailingID;
    }
    
    public void setCustomerID(int ci) {
        customerID=ci;
    }
    
    public void setUserType(String ut) {
        if(ut.compareTo(USER_TYPE_ADMIN) == 0 ||
           ut.compareTo(USER_TYPE_TESTUSER) == 0 ||
           ut.compareTo(USER_TYPE_TESTVIP) == 0 ||
           ut.compareTo(USER_TYPE_WORLD) == 0 ||
           ut.compareTo(USER_TYPE_WORLDVIP) == 0) {
            userType=ut;
        } else {
            userType=USER_TYPE_WORLD;
        }
    }
    
    public void setUserRemark(String remark) {
        if(remark == null) {
            remark=new String("");
        }
        userRemark=remark;
    }

    public void setUserStatus(int us) {
        userStatus=us;
    }
    
/*    public void setUserRemark(String ur) {
        userRemark=ur;
    }
*/
    
    public void setChangeDate(java.util.Date ts) {
        changeDate=ts;
    }
    
    public int getMailinglistID() {
        return mailinglistID;
    }
    public int getCustomerID() {
        return customerID;
    }
    public String getUserType() {
        return userType;
    }
    public int getUserStatus() {
        return userStatus;
    }
    public String getUserRemark() {
        return userRemark;
    }
    public java.util.Date getChangeDate() {
        return changeDate;
    }
    
    public boolean updateStatusInDB(int companyID) {
        String sqlUpdateStatus="UPDATE customer_" + companyID + "_binding_tbl SET user_status=?, exit_mailing_id=?, user_remark=? WHERE customer_id=? AND mailinglist_id=? AND mediatype=?";
        Object[] params=new Object[] {
                new Integer(getUserStatus()),
                new Integer(getExitMailingID()), getUserRemark(),
                new Integer(customerID), new Integer(mailinglistID),
                new Integer(getMediaType())
            };

        try {
            JdbcTemplate tmpl=AgnUtils.getJdbcTemplate(this.applicationContext);

            if(tmpl.update(sqlUpdateStatus, params) < 1) {
                return false;
            }
        } catch (Exception e) {
            AgnUtils.logger().error("updateStatusInDB: "+e.getMessage());
            return false;
        }
        
        return true;
    }
    
    public boolean saveBindingInDB(int companyID, Map allCustLists) {
        Map types=(Map) allCustLists.get(new Integer(mailinglistID));
        boolean changed=false;

        if(types != null) {
            BindingEntry old=(BindingEntry) types.get(new Integer(mediaType));

            if(old != null) {
                if(old.getExitMailingID() != exitMailingID) {
                    changed=true; 
                }
                if(!old.getUserType().equals(userType)) {
                    changed=true;
                }
                if(old.getUserStatus() != userStatus) {
                    changed=true;
                    if(userStatus == BindingEntry.USER_STATUS_ADMINOUT) { 
                        userRemark="Opt-Out by ADMIN";
                    } else {
                        userRemark="Opt-In by ADMIN";
                    }
                } else {
                    userRemark=old.getUserRemark();
                }
                if(old.getMediaType() != mediaType) {
                    changed=true;
                }
                if(changed == true) {
                    if(updateBindingInDB(companyID) != true) {
                        return false;
                    }
                }
                return true;
            } 
        }
        if(insertNewBindingInDB(companyID) == true) {
            return true;
        }
        return false;
    }

    /**
     * Updates this Binding in the Database
     * 
     * @return True: Sucess
     * False: Failure
     * @param companyID The company ID of the Binding
     */
    public boolean updateBindingInDB(int companyID) {
       
    	String dbTimeExpression = "now()"; // Expression for setting the timestamp, default now() for mySql
       
    	if( AgnUtils.isOracleDB()) {
        	dbTimeExpression = "sysdate";
        }

        String sqlUpdateStatus="UPDATE customer_" + companyID + "_binding_tbl SET user_status=?, user_remark=?, exit_mailing_id=?, user_type=?, mediatype=?, " + AgnUtils.changeDateName() + "=" + dbTimeExpression + "  WHERE customer_id=? AND mailinglist_id=? AND mediatype=?";
                
         
        Object[] param=new Object[] {
                new Integer(getUserStatus()), getUserRemark(),
                new Integer(getExitMailingID()), getUserType(),
                new Integer(getMediaType()),
               
                /* Where parameters */
                new Integer(customerID), new Integer(mailinglistID),
                new Integer(getMediaType())
            };

        try {
            JdbcTemplate tmpl=AgnUtils.getJdbcTemplate(this.applicationContext);

            if(tmpl.update(sqlUpdateStatus, param) < 1) {
                return false;
            }
        } catch (Exception e) {
            AgnUtils.logger().error("updateBindingInDB: " + e.getMessage());
            return false; 
        }
        
        return true;
    }
    
    public boolean insertNewBindingInDB(int companyID) {
        String currentTimestamp=AgnUtils.getSQLCurrentTimestampName();
        String sqlInsertBinding="INSERT INTO customer_" + companyID + "_binding_tbl "
            +"(mailinglist_id, customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mediatype) "
            +"VALUES (?, ?, ?, ?, ?, "+currentTimestamp+", ?, ?)";
            
        Object[] params=new Object[] { 
                new Integer(getMailinglistID()), 
                new Integer(getCustomerID()),
                getUserType(),
                new Integer(getUserStatus()), getUserRemark(),
                new Integer(getExitMailingID()),
                new Integer(getMediaType())
            };
        try {
            JdbcTemplate tmpl=AgnUtils.getJdbcTemplate(this.applicationContext);

            tmpl.update(sqlInsertBinding, params);
        } catch (Exception e) {
            AgnUtils.logger().error("insertNewBindingInDB: " + e.getMessage());
            return false;
        }
        
        return true;
    }
    
    // neu von ma
    public boolean getUserBindingFromDB(int companyID) {
        JdbcTemplate jdbc = AgnUtils.getJdbcTemplate(this.applicationContext);
    	String sqlGetBinding="SELECT * FROM customer_" + companyID + "_binding_tbl WHERE mailinglist_id=" +
        mailinglistID + " AND customer_id=" + customerID + " AND mediatype="+this.mediaType;
        try {
            List list = jdbc.queryForList(sqlGetBinding);

            if (list.size() > 0) {
                Map map = (Map) list.get(0);

                setUserType((String) map.get("user_type"));
                setUserStatus(((Number) map.get("user_status")).intValue());
                setUserRemark((String) map.get("user_remark"));
                setChangeDate((java.util.Date) map.get( AgnUtils.changeDateName() ));
		if(map.get("exit_mailing_id") != null) {
                	setExitMailingID(((Number) map.get("exit_mailing_id")).intValue());
		} else {
                	setExitMailingID(0);
		}
                return true;
            }
        }
    	catch (Exception e) {
    		System.err.println("getUserBindingFromDB: " + e.getMessage());
    		System.err.println(AgnUtils.getStackTrace(e));
    		AgnUtils.logger().error("getUserBindingFromDB: " + e.getMessage());
    		AgnUtils.logger().error(AgnUtils.getStackTrace(e));
    	}
   	return false;
    }
    // neu von ma
    
    public boolean optOutEmailAdr(String email, int CompanyID) {
        String operator=new String(" = ");

        if((email.indexOf('%')!=-1) || (email.indexOf('_')!=-1)) {
            operator=new String(" LIKE ");
        }
        String sqlUpdate="UPDATE customer_"+CompanyID+"_binding_tbl SET user_status=? WHERE customer_id IN (SELECT customer_id FROM customer_"+ CompanyID + "_tbl WHERE lower(email)"+operator+"?)";

        Object[] params=new Object[] {
                new Integer(USER_STATUS_ADMINOUT), email
            };
        try {
            JdbcTemplate tmpl=AgnUtils.getJdbcTemplate(this.applicationContext);

            if(tmpl.update(sqlUpdate, params) == 1) {
                return true;
            }
        } catch (Exception e) {
            AgnUtils.logger().error("optOutEmailAdr: " + e.getMessage());
        }
        
        return false;
    }
    
    /** Getter for property mediaType.
     * @return Value of property mediaType.
     *
     */
    public int getMediaType() {
        return this.mediaType;
    }
    
    /** Setter for property mediaType.
     * @param mediaType New value of property mediaType.
     *
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

	private String	remoteAddr=null;

	public void	setRemoteAddr(String remoteAddr) {
		this.remoteAddr=remoteAddr;
	}

   
	public String	getRemoteAddr()	{
		return remoteAddr;
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
   
    public String toString() { 
        return new String("List: "+mailinglistID+" Customer: "+customerID+" ExitID: "+exitMailingID+" Type: "+userType+" Status: "+userStatus+" Remark: "+userRemark+" mediaType: "+mediaType);
    }
}
