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

package org.agnitas.actions.ops;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.agnitas.actions.ActionOperation;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.impl.MediatypeEmailImpl;
import org.agnitas.dao.MailingDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.agnitas.util.UID;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 *
 * @author  mhe
 * @version
 */
public class GetArchiveList extends ActionOperation implements Serializable {

    static final long serialVersionUID = -4469612062539630032L;

    /**
     * Holds value of property campaignID.
     */
    private int campaignID;

    /** Creates new ActionOperationUpdateCustomer */
    public GetArchiveList() {
    }

    /** Executes ActionOperation  */
    public boolean executeOperation(Connection dbConn, int companyID, int customerID, int callerMailingID, HttpServletRequest aReq) {

        // do nothing, deprecated
        return false;
    }

    /**
     *
     * @param req
     * @param index
     * @return
     */
    public boolean buildOperationFromRequest(ServletRequest req, int index) {
        boolean exitValue=true;

        try {
            this.campaignID=Integer.parseInt(req.getParameter("op_mlid"+index));
        } catch(Exception e) {
            this.campaignID=0;
        }
        return exitValue;
    }

    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;

        allFields=in.readFields();
        this.campaignID=allFields.get("campaignID", 0);
    }

    public boolean executeOperation(Connection dbConn, int companyID, HashMap params) {
        return false;
    }
    /**
     *
     * @param dbConn
     * @param companyID
     * @param params
     * @return
     */
    public boolean executeOperation(ApplicationContext con, int companyID, HashMap params) {
        DataSource ds=(DataSource) con.getBean("dataSource");
        JdbcTemplate jdbc=new JdbcTemplate(ds);
        Integer tmpNum=null;
        int customerID=0;
        String sqlQuery=null;
        Mailing aMailing=null;
        int tmpMailingID=0;
        UID tmpUID=null;
        Hashtable shortnames=new Hashtable();
        Hashtable uids=new Hashtable();
        Hashtable subjects=new Hashtable();
        LinkedList mailingids=new LinkedList();
        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            customerID=tmpNum.intValue();
        } else {
            return false;
        }

        try {
        	tmpUID=(UID)con.getBean("UID");
        } catch (Exception e) {
            return false;
        }

        tmpUID.setCompanyID(companyID);
        tmpUID.setCustomerID(customerID);
        
        sqlQuery="select mailing_id, shortname from mailing_tbl where deleted<>1 and is_template=0 and company_id=" + companyID + " and campaign_id="+this.campaignID + " and archived=1 order by mailing_id desc" ;

        try {
            MailingDao dao=(MailingDao) con.getBean("MailingDao");
            List list=jdbc.queryForList(sqlQuery);
            Iterator i=list.iterator();

            while(i.hasNext()) {
                Map map=(Map) i.next();

                tmpMailingID=((Number) map.get("mailing_id")).intValue();
                aMailing=dao.getMailing(tmpMailingID, companyID);

//                aMailing.getMediaTypesFromDB(dbConn);
                MediatypeEmailImpl aType=(MediatypeEmailImpl) aMailing.getEmailParam(con);

                mailingids.add(Integer.toString(tmpMailingID));
                shortnames.put(Integer.toString(tmpMailingID), SafeString.getHTMLSafeString((String) map.get("shortname")));
                subjects.put(Integer.toString(tmpMailingID), aMailing.getPreview(aType.getSubject(), Mailing.INPUT_TYPE_HTML, customerID, con));
                tmpUID.setMailingID(tmpMailingID);
                try {
                    uids.put(Integer.toString(tmpMailingID), tmpUID.makeUID());
                } catch (Exception e) {
                	AgnUtils.logger().error("problem encrypt: "+e);
                	AgnUtils.logger().error(AgnUtils.getStackTrace(e));
                    return false;
                }
            }
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("SQL: "+sqlQuery, e);
        	AgnUtils.logger().error("problem: "+e);
        	AgnUtils.logger().error(AgnUtils.getStackTrace(e));
        }

        params.put("archiveListSubjects", subjects);
        params.put("archiveListNames", shortnames);
        params.put("archiveListUids", uids);
        params.put("archiveListMailingIDs", mailingids);

        AgnUtils.logger().info("generated feed");
        return true;
    }

    /**
     * Getter for property mailinglistID.
     * @return Value of property mailinglistID.
     */
    public int getCampaignID() {
        return this.campaignID;
    }

    /**
     * Setter for property mailinglistID.
     * @param mailinglistID New value of property mailinglistID.
     */
    public void setCampaignID(int campaignID) {
        this.campaignID = campaignID;
    }
}
