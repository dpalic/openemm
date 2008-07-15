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

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.agnitas.actions.EmmAction;
import org.agnitas.beans.Company;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.agnitas.util.TimeoutLRUMap;
import org.agnitas.util.UID;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Martin Helff
 */
public class TrackableLinkImpl implements TrackableLink {

    protected int companyID;
    protected int customerID;
    protected int id;
    protected int mailingID;
    protected int actionID;
    protected String fullUrl=null;

    /** Holds value of property shortname. */
    protected String shortname;

    /** Holds value of property usage. */
    protected int usage;

    private TimeoutLRUMap baseUrlCache=new TimeoutLRUMap(AgnUtils.getDefaultIntValue("rdir.keys.maxCache"), AgnUtils.getDefaultIntValue("rdir.keys.maxCacheTimeMillis"));

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

        this.customerID=customerID;
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
                // newUrl=SafeString.replaceIgnoreCase(newUrl, "##AGNUID##", URLEncoder.encode(deepTrackingUID, "UTF-8"));
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

   public String encodeTagStringLinkTracking(ApplicationContext con, int custID) {
        String tag=new String("");
        String baseUrl=null;

        if(baseUrlCache != null) {
            baseUrl=(String)baseUrlCache.get(Long.toString(this.mailingID));
        }

        if(baseUrl==null) {
            try {
                if ( AgnUtils.isOracleDB() ) { 
	                JdbcTemplate tmpl=new JdbcTemplate((DataSource)con.getBean("dataSource"));
	
	                System.err.println("Query: SELECT AUTO_URL FROM MAILING_TBL WHERE MAILING_ID="+this.mailingID);
	                baseUrl=(String) tmpl.queryForObject("SELECT AUTO_URL FROM MAILING_TBL WHERE MAILING_ID=?", new Object[]{new Integer(this.mailingID)}, tag.getClass());
                }
                if(baseUrl == null) {
                	// TODO: extract to emm.properties
                	if (!AgnUtils.isOracleDB()) {
                		baseUrl="http://rdir.de/r?";
                	} else {
                		CompanyDao cDao=(CompanyDao)con.getBean("CompanyDao");
                		Company company = cDao.getCompany( this.companyID );
                		baseUrl = company.getRdirDomain() + "/r.html?";
                	}
                }
                if(baseUrlCache!=null) {
                    baseUrlCache.put(Long.toString(mailingID), baseUrl);
                }
            } catch (Exception e) {
                System.err.println("Exception: "+e);
                System.err.println(AgnUtils.getStackTrace(e));
                tag=null;
            }
        }

        if(tag!=null) {
            UID uid=(UID)con.getBean("UID");

            uid.setCompanyID(this.companyID);
            uid.setCustomerID(custID);
            uid.setMailingID(this.mailingID);
            uid.setURLID(this.id);

            try	{
	        tag="uid="+uid.makeUID(custID, this.id);
            } catch(Exception e) {
	        System.err.println("Exception in UID: "+e);
            }
        }

        return baseUrl+tag;
    }

    
    /**
     * logs a click out of an email into rdirlog__tbl
     * @param customerID th customer id
     * @param remoteAddr the remote address
     * @param con the context
     */
    public boolean logClickInDB(int customerID, String remoteAddr, ApplicationContext con) {
    	boolean exitValue=true;
        JdbcTemplate tmpl=new JdbcTemplate((DataSource)con.getBean("dataSource"));
        int i=0;

        String rdirlogTbl = AgnUtils.isOracleDB() ? "rdirlog_"+this.companyID+"_tbl" : "rdir_log_tbl";
		String sqlUpdate="insert into " + rdirlogTbl + " (customer_id, url_id, company_id, ip_adr, mailing_id) values (?, ?, ?, ?, ?)";
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

	public boolean addDeepTrackingParameters(ApplicationContext con) {
		// not implemented
		return false;
	}

	public String encodeTagStringDeepTracking(ApplicationContext con) {
		// not implemented
		return null;
	}

	public int getDeepTracking() {
		// not implemented
		return 0;
	}

	public String getDeepTrackingSession() {
		// not implemented
		return null;
	}

	public String getDeepTrackingUID() {
		// not implemented
		return null;
	}

	public String getDeepTrackingUrl() {
		// not implemented
		return null;
	}

	public int getRelevance() {
		// not implemented
		return 0;
	}

	public void setDeepTracking(int deepTracking) {
		// not implemented
		
	}

	public void setRelevance(int relevance) {
		// not implemented
		
	}
    
}
