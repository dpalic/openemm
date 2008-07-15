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

import java.sql.*;
import java.io.*;
import org.agnitas.beans.Company;
import org.agnitas.util.AgnUtils;

public class CompanyImpl implements Company {
    
    protected int companyID;
    protected int creatorID;
    protected String shortname;
    protected String description;
    protected String status;
    
    
    // CONSTRUCTOR:
    public CompanyImpl() {
        companyID=0;
        creatorID=0;
    }
   
    public boolean initTables(Connection dbConn) {
        boolean returnValue=true;
        Statement agnStatement=null;
        int cid=0;
        
        if(this.companyID==0) {
            return false;
        }
             
        cid=this.companyID;
        
        String sql4ever="CREATE TABLE customer_" + cid + "_tbl ( customer_id NUMBER, email VARCHAR2(100), firstname VARCHAR2(100), lastname VARCHAR2(100), title VARCHAR2(100), gender NUMBER(1), mailtype NUMBER(1), timestamp DATE DEFAULT sysdate, creation_date DATE DEFAULT sysdate, datasource_id NUMBER )";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="CREATE TABLE customer_" + cid + "_binding_tbl (customer_id NUMBER, mailinglist_id NUMBER, user_type CHAR(1), user_status NUMBER, user_remark VARCHAR2(150), change_date timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP, creation_date timestamp NOT NULL default '0000-00-00 00:00:00', exit_mailing_id NUMBER, mediatype number default 0 )";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE customer_" + cid + "_tbl ADD CONSTRAINT cust" + cid + "$cid$pk primary key (customer_id)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE customer_" + cid + "_tbl ADD CONSTRAINT cust" + cid + "$email$nn check (email IS NOT NULL)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE customer_" + cid + "_tbl ADD CONSTRAINT cust" + cid + "$gender$ck check (gender IN (0,1,2,3,4,5))";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE customer_" + cid + "_tbl ADD CONSTRAINT cust" + cid + "$gender$nn check (gender IS NOT NULL)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE customer_" + cid + "_tbl ADD CONSTRAINT cust" + cid + "$mailtype$ck check (mailtype IN (0,1,2))";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE customer_" + cid + "_tbl ADD CONSTRAINT cust" + cid + "$mailtype$nn check (mailtype IS NOT NULL)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE customer_" + cid + "_binding_tbl ADD CONSTRAINT cust" + cid + "b$cid$fk foreign key (customer_id) references customer_" + cid + "_tbl(customer_id)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE customer_" + cid + "_binding_tbl ADD CONSTRAINT cust" + cid + "b$cid_mid_mt$pk primary key (customer_id, mailinglist_id, mediatype)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE customer_" + cid + "_binding_tbl ADD CONSTRAINT cust" + cid + "b$mid$fk foreign key (mailinglist_id) references mailinglist_tbl(mailinglist_id)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE customer_" + cid + "_binding_tbl ADD CONSTRAINT cust" + cid + "b$cid$nn check (customer_id IS NOT NULL)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE customer_" + cid + "_binding_tbl ADD CONSTRAINT cust" + cid + "b$mid$nn check (mailinglist_id IS NOT NULL)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="CREATE SEQUENCE customer_" + cid + "_tbl_seq start with 1000 increment by 1 nocache";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="CREATE TABLE cust" + cid + "_ban_tbl ( email VARCHAR2(150), timestamp DATE DEFAULT sysdate)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="CREATE TABLE cust_" + cid + "_desc_tbl (admin_id number, col_name varchar2(50), shortname varchar2(100), description varchar2(200), default_value varchar2(200), mode_edit number default 0, mode_insert number default 0, timestamp date default sysdate)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="CREATE TABLE mailing_" + cid + "_mt_tbl (mailing_id number, mediatype number, priority number, status number, param varchar2(4000))";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="CREATE TABLE mailtrack_" + cid + "_tbl (maildrop_status_id number, customer_id number, timestamp date default sysdate)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE mailtrack_" + cid + "_tbl ADD CONSTRAINT mt" + cid + "$cuid$nn check (customer_id IS NOT NULL)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever="ALTER TABLE mailtrack_" + cid + "_tbl ADD CONSTRAINT mt" + cid + "$mdsid$nn check (maildrop_status_id IS NOT NULL)";
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever=new String("CREATE TABLE ONEPIXELLOG_"+getId()+"_TBL (CUSTOMER_ID NUMBER NOT NULL ENABLE, MAILING_ID NUMBER NOT NULL ENABLE, COMPANY_ID NUMBER NOT NULL ENABLE, IP_ADR VARCHAR2(15) NOT NULL ENABLE, TIMESTAMP DATE DEFAULT sysdate, OPEN_COUNT NUMBER)");
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever=new String("CREATE INDEX ONEPIX"+getId()+"$MLID$IDX ON ONEPIXELLOG_"+getId()+"_TBL (MAILING_ID) TABLESPACE INDX");
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever=new String("CREATE INDEX ONEPIX"+getId()+"$MLID_CUID$IDX ON ONEPIXELLOG_"+getId()+"_TBL (mailing_id, customer_id) TABLESPACE INDX");
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever=new String("CREATE TABLE RDIRLOG_"+getId()+"_TBL (CUSTOMER_ID NUMBER NOT NULL ENABLE, URL_ID NUMBER NOT NULL ENABLE, COMPANY_ID NUMBER NOT NULL ENABLE, TIMESTAMP DATE DEFAULT sysdate, IP_ADR VARCHAR2(15) NOT NULL ENABLE, MAILING_ID NUMBER)");
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever=new String("CREATE INDEX RLOG"+getId()+"$MLID_URLID_CUID$IDX ON RDIRLOG_"+getId()+"_TBL (MAILING_ID, URL_ID, CUSTOMER_ID) TABLESPACE INDX");
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        sql4ever=new String("CREATE INDEX CUST"+getId()+"$EMAIL$IDX ON CUSTOMER_"+getId()+"_TBL (EMAIL) TABLESPACE INDX NOLOGGING");
        try {
            agnStatement=dbConn.createStatement();
            agnStatement.executeQuery(sql4ever);
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().error("initTables: " + e.getMessage());
            returnValue=false;
        }
        
        return returnValue;
    }
    
    // * * * * *
    //  SETTER:
    // * * * * *
    public void setId(int id) {
        companyID=id;
    }
    
    public void setShortname(String name) {
        shortname=name;
    }
    
    public void setDescription(String sql) {
        description=sql;
    }
    
    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    
    
    
    // * * * * *
    //  GETTER:
    // * * * * *
    public int getCreatorID() {
        return this.creatorID;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public int getId() {
        return companyID;
    }
    
    public String getShortname() {
        return shortname;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Holds value of property rdirDomain.
     */
    protected String rdirDomain;

    /**
     * Getter for property rdirDomain.
     * @return Value of property rdirDomain.
     */
    public String getRdirDomain() {

        return this.rdirDomain;
    }

    /**
     * Setter for property rdirDomain.
     * @param rdirDomain New value of property rdirDomain.
     */
    public void setRdirDomain(String rdirDomain) {

        this.rdirDomain = rdirDomain;
    }

    /**
     * Holds value of property secret.
     */
    protected String secret;

    /**
     * Getter for property secret.
     * @return Value of property secret.
     */
    public String getSecret() {

        return this.secret;
    }

    /**
     * Setter for property secret.
     * @param secret New value of property secret.
     */
    public void setSecret(String secret) {

        this.secret = secret;
    }

    /**
     * Holds value of property mailloopDomain.
     */
    private String mailloopDomain;

    /**
     * Getter for property mailloopDomain.
     * @return Value of property mailloopDomain.
     */
    public String getMailloopDomain() {
        return this.mailloopDomain;
    }

    /**
     * Setter for property mailloopDomain.
     * @param mailloopDomain New value of property mailloopDomain.
     */
    public void setMailloopDomain(String mailloopDomain) {
        this.mailloopDomain = mailloopDomain;
    }
}
