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

import javax.sql.DataSource;

import org.agnitas.beans.Mailinglist;
import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.springframework.jdbc.core.JdbcTemplate;


public class MailinglistImpl implements Mailinglist {
    
    private static final long serialVersionUID = -3657876518429344904L;
	/**
     * ID of the mailinglist.
     */
    protected int id;
    /**
     * Company ID of the account
     */
    protected int companyID;
    /**
     * shortname to be displayed in mailinglist list.
     */
    protected String shortname;
    /**
     * a short mailinglist description for the frontend
     */
    protected String description="";
    /**
     * number of subscribers with the IP adress not explicitely named in the list
     */
    
    
    /** CONSTRUCTOR */
    public MailinglistImpl() {     
    }
    
    /**
     * deletes the bindings for this mailinglist
     * (invocated before the mailinglist is deleted to avoid
     * orphaned mailinglist bindings)
     * @return return code
     */
    public boolean deleteBindings() {
        
        JdbcTemplate myJdbcTempl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
        String sqlStmt = "delete from customer_" + this.companyID + "_binding_tbl where mailinglist_id= " + this.id;

        try {
            myJdbcTempl.execute(sqlStmt);
        } catch(Exception e) {
            AgnUtils.logger().error("deleteBindings: "+e);
            AgnUtils.logger().error("SQL: "+sqlStmt);
        }
        return true;
    }
    
    // SETTER:
    
    public void setCompanyID(int cid) {
        companyID=cid;
    }
    
    public void setId(int listid) {
        this.id=listid;
    }
    
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    public void setDescription(String description) {
        if(description != null) {
            this.description = description;
        } else {
            this.description = "";
        }
    }
    
    
    
    
    // GETTER:
    
    public int getId() {
        
        return id;
    }
    
    public int getCompanyID() {
        return companyID;
    }
    
    public String getShortname() {
        return shortname;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getNumberOfActiveSubscribers(boolean admin, boolean test, boolean world, int targetID) {
        int numOfSubscribers=0;
        String sqlSelection=null;
        Target aTarget=null;
        TargetDao tDao=(TargetDao)this.applicationContext.getBean("TargetDao");
        
        
        // no target-group if pure admin/test-mailing
        if(!world) {
            targetID=0;
        }
        
        if(targetID==0) {
            sqlSelection=new String(" ");
        } else {
            aTarget=tDao.getTarget(targetID, this.companyID);
            if(aTarget!=null) {
                sqlSelection=new String(" AND ("  + aTarget.getTargetSQL() + ") ");
            } else {
                sqlSelection=new String(" ");
            }
        }
        
        if(admin && !test && !world) {
            sqlSelection=sqlSelection+" AND (bind.user_type='A')";
        }
        
        if(admin && test && !world) {
            sqlSelection=sqlSelection+" AND (bind.user_type='A' OR bind.user_type='T')";
        }
        
        // if(admin && test && world) {
        //    sqlSelection=sqlSelection+" AND (bind.user_type='A' OR bind.user_type='T' OR bind.user_type='W')";
        //}
        
        String sqlStatement="SELECT count(*) FROM customer_" + this.companyID + "_tbl cust, customer_" +
                this.companyID + "_binding_tbl bind WHERE bind.mailinglist_id=" + this.id +
                " AND cust.customer_id=bind.customer_id" + sqlSelection + " AND bind.user_status=1";
        
        try {
            JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));

            numOfSubscribers=(int)tmpl.queryForLong(sqlStatement);
        } catch (Exception e) {
            numOfSubscribers=0;
            AgnUtils.logger().error("getNumberOfActiveSubscribers: "+e);
            AgnUtils.logger().error("SQL: "+sqlStatement);
        }
        
        return numOfSubscribers;
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
}
