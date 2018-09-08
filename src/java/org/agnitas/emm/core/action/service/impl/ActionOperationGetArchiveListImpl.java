package org.agnitas.emm.core.action.service.impl;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.MediatypeEmail;
import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationGetArchiveList;
import org.agnitas.emm.core.action.service.EmmActionOperation;
import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDService;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;

public class ActionOperationGetArchiveListImpl implements EmmActionOperation, ApplicationContextAware {
	
	private static final Logger logger = Logger.getLogger(ActionOperationGetArchiveListImpl.class);

	private ApplicationContext con;
	private DataSource dataSource;
	private ExtensibleUIDService uidService;
	private MailingDao mailingDao;

	@Override
	public boolean execute(AbstractActionOperation operation, Map<String, Object> params) {
		
		ActionOperationGetArchiveList op =(ActionOperationGetArchiveList) operation;
		int companyID = op.getCompanyId();
		int campaignID = op.getCampaignID();
		
        JdbcTemplate jdbc=new JdbcTemplate(dataSource);
        Integer tmpNum=null;
        int customerID=0;
        String sqlQuery=null;
        Mailing aMailing=null;
        int tmpMailingID=0;
        Hashtable<String, String> shortnames = new Hashtable<String, String>();
        Hashtable<String, String> uids = new Hashtable<String, String>();
        Hashtable<String, String> subjects=new Hashtable<String, String>();
        LinkedList<String> mailingids=new LinkedList<String>();
        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            customerID=tmpNum.intValue();
        } else {
            return false;
        }

        ExtensibleUID uid = uidService.newUID();
        uid.setCompanyID(companyID);
        uid.setCustomerID(customerID);
        
        sqlQuery="select mailing_id, shortname from mailing_tbl where deleted = 0 and is_template=0 and company_id=? and campaign_id=? and archived=1 order by mailing_id desc" ;

        try {
            List list=jdbc.queryForList(sqlQuery, new Object[] { companyID, campaignID });
            Iterator i=list.iterator();

            while(i.hasNext()) {
                Map map=(Map) i.next();

                tmpMailingID=((Number) map.get("mailing_id")).intValue();
                aMailing=mailingDao.getMailing(tmpMailingID, companyID);

//                aMailing.getMediaTypesFromDB(dbConn);
                MediatypeEmail aType = aMailing.getEmailParam();

                mailingids.add(Integer.toString(tmpMailingID));
                shortnames.put(Integer.toString(tmpMailingID), SafeString.getHTMLSafeString((String) map.get("shortname")));
                subjects.put(Integer.toString(tmpMailingID), aMailing.getPreview(aType.getSubject(), Mailing.INPUT_TYPE_HTML, customerID, con));
                uid.setMailingID(tmpMailingID);
                try {
                    uids.put(Integer.toString(tmpMailingID), uidService.buildUIDString( uid));
                } catch (Exception e) {
                	logger.error("problem encrypt: "+e, e);
                    return false;
                }
            }
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("SQL: "+sqlQuery, e);
        	logger.error("problem: "+e, e);
        	return false;
        }

        params.put("archiveListSubjects", subjects);
        params.put("archiveListNames", shortnames);
        params.put("archiveListUids", uids);
        params.put("archiveListMailingIDs", mailingids);

        if (logger.isInfoEnabled()) logger.info("generated feed");
        return true;

	}
	
	@Override
	public void setApplicationContext(ApplicationContext con) throws BeansException {
		this.con = con;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setUidService(ExtensibleUIDService uidService) {
		this.uidService = uidService;
	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

}
