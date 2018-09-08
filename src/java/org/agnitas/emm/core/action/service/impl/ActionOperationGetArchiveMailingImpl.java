package org.agnitas.emm.core.action.service.impl;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import org.agnitas.dao.MailingDao;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationGetArchiveMailing;
import org.agnitas.emm.core.action.service.EmmActionOperation;
import org.agnitas.preview.Preview;
import org.agnitas.preview.PreviewFactory;
import org.agnitas.preview.PreviewHelper;
import org.agnitas.util.SafeString;
import org.agnitas.util.TimeoutLRUMap;
import org.apache.log4j.Logger;

public class ActionOperationGetArchiveMailingImpl implements EmmActionOperation {
	
	private static final Logger logger = Logger.getLogger(ActionOperationGetArchiveMailingImpl.class);

    private TimeoutLRUMap<String, String> mailingCache;
    private TimeoutLRUMap<String, String> adrCache;
    private TimeoutLRUMap<String, String> subjectCache;
    private PreviewFactory previewFactory;
	private MailingDao mailingDao;

	@Override
	public boolean execute(AbstractActionOperation operation, Map<String, Object> params) {
		ActionOperationGetArchiveMailing op =(ActionOperationGetArchiveMailing) operation;
		int companyID = op.getCompanyId();

        Integer tmpNum=null;
        int customerID=0;
        boolean returnValue=false;
        int tmpMailingID=0;
        String key=null;
        String archiveHtml=null;
        String archiveSubject=null;
        String archiveSender=null;

        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            customerID=tmpNum.intValue();
        } else {
            return returnValue;
        }

        if(params.get("mailingID")!=null) {
            tmpNum=(Integer)params.get("mailingID");
            tmpMailingID=tmpNum.intValue();
        } else {
            return returnValue;
        }

        key = companyID + "_" + tmpMailingID + "_" + customerID;
        
        archiveHtml=(String)mailingCache.get(key);
        archiveSender=(String)adrCache.get(key);
        archiveSubject=(String)subjectCache.get(key);

        if (archiveHtml == null || archiveSender == null || archiveSubject == null) {

            try {

                Hashtable<String, Object> previewResult = generateBackEndPreview(tmpMailingID, customerID);
                if (mailingDao.exist(tmpMailingID,companyID)) {
                    archiveHtml = (String) previewResult.get(Preview.ID_HTML);
                } else {
                	Locale locale = (Locale) params.get("locale");
                    archiveHtml = SafeString.getLocaleString("mailing.content.not.avaliable", locale != null ? locale : Locale.getDefault());
                }
                String header = (String) previewResult.get(Preview.ID_HEAD);
                if (header != null) {
                    archiveSender = PreviewHelper.getFrom(header);
                    archiveSubject = PreviewHelper.getSubject(header);
                }

                if (archiveHtml != null && archiveSender != null && archiveSubject != null) {
                	mailingCache.put(key, archiveHtml);
                	adrCache.put(key, archiveSender);
                	subjectCache.put(key, archiveSubject);
                }
                returnValue = true;
            } catch (Exception e) {
                logger.error("archive problem: " + e, e);
                returnValue = false;
            }

        } else {
            returnValue=true;
        }

        if(returnValue) {
            params.put("archiveHtml", archiveHtml);
            params.put("archiveSender", archiveSender);
            params.put("archiveSubject", archiveSubject);
        }
        return returnValue;
	}

    private Hashtable<String, Object> generateBackEndPreview(int mailingID,int customerID) {
		Preview preview = previewFactory.createPreview();
		Hashtable<String,Object> output = preview.createPreview (mailingID,customerID, false);
		preview.done();
		return output;
	}

	public void setMailingCache(TimeoutLRUMap<String, String> mailingCache) {
		this.mailingCache = mailingCache;
	}

	public void setAdrCache(TimeoutLRUMap<String, String> adrCache) {
		this.adrCache = adrCache;
	}

	public void setSubjectCache(TimeoutLRUMap<String, String> subjectCache) {
		this.subjectCache = subjectCache;
	}

	public void setPreviewFactory(PreviewFactory previewFactory) {
		this.previewFactory = previewFactory;
	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

}
