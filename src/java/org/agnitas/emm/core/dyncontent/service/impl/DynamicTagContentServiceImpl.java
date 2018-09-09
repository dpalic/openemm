package org.agnitas.emm.core.dyncontent.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.agnitas.beans.Admin;
import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.impl.AdminImpl;
import org.agnitas.beans.impl.DynamicTagContentImpl;
import org.agnitas.dao.DynamicTagContentDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.emm.core.dyncontent.service.ContentModel;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentNotExistException;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentService;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentWithSameOrderAlreadyExist;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentWithSameTargetIdAlreadyExist;
import org.agnitas.emm.core.dynname.service.DynamicTagNameNotExistException;
import org.agnitas.emm.core.mailing.service.MailingNotExistException;
import org.agnitas.emm.core.target.service.TargetNotExistException;
import org.agnitas.emm.core.validator.annotation.Validate;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.service.UserActivityLogService;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

public class DynamicTagContentServiceImpl implements DynamicTagContentService, ApplicationContextAware {

	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(DynamicTagContentServiceImpl.class);
	
	@Resource(name="DynamicTagContentDao")
	private DynamicTagContentDao dynamicTagContentDao;
	@Resource(name="MailingDao")
	private MailingDao mailingDao;
	@Resource(name="TargetDao")
	private TargetDao targetDao;

	private ApplicationContext applicationContext;
	
	@Override
	@Validate("deleteContentBlock")
	public boolean deleteContent(ContentModel contentModel) {
        final Admin admin = new AdminImpl();
        admin.setUsername(Utils.getUserName());
        writeUserActivityLog(admin, "delete textblock", "with id " + contentModel.getContentId());
        return dynamicTagContentDao.deleteContent(contentModel.getCompanyId(), contentModel.getContentId());
	}
	
	@Override
	@Validate("getContentBlock")
	public DynamicTagContent getContent(ContentModel contentModel) {
		DynamicTagContent content = dynamicTagContentDao.getContent(contentModel.getCompanyId(), contentModel.getContentId());
		if (content == null) {
			throw new DynamicTagContentNotExistException();
		}
		return content;
	}

	@Override
	@Validate("listContentBlocksOrNames")
	public List<DynamicTagContent> getContentList(ContentModel contentModel) {
		if (!mailingDao.exist(contentModel.getMailingId(), contentModel.getCompanyId())) {
			throw new MailingNotExistException();
		}
		return dynamicTagContentDao.getContentList(contentModel.getCompanyId(), contentModel.getMailingId());
	}

	@Override
	@Transactional
	@Validate("addContentBlock")
	@SuppressWarnings("unchecked")
	public int addContent(ContentModel contentModel) {
		Mailing mailing = mailingDao.getMailing(contentModel.getMailingId(), contentModel.getCompanyId());
		if (mailing == null || mailing.getId() == 0) {
			throw new MailingNotExistException();
		}
		DynamicTag dynamicTag = mailing.getDynTags().get(contentModel.getBlockName());
		if (dynamicTag == null) {
			throw new DynamicTagNameNotExistException();
		}
		if (contentModel.getTargetId() != 0 && targetDao.getTarget(contentModel.getTargetId(), contentModel.getCompanyId()) == null) {
			throw new TargetNotExistException(contentModel.getTargetId());
		}
        Map<String, DynamicTagContent> dContent = dynamicTag.getDynContent();

        if (dContent.containsKey(Integer.toString(contentModel.getOrder()))) {
        	throw new DynamicTagContentWithSameOrderAlreadyExist();
        }
        
        for (DynamicTagContent aContentTmp : dContent.values()) {
            if(aContentTmp.getTargetID() == contentModel.getTargetId()) {
                throw new DynamicTagContentWithSameTargetIdAlreadyExist();
            }
		}
        
        DynamicTagContent aContent = new DynamicTagContentImpl();
		aContent.setDynNameID(dynamicTag.getId());
		aContent.setId(0);
		aContent.setDynName(dynamicTag.getDynName());
		aContent.setMailingID(contentModel.getMailingId());
		aContent.setCompanyID(contentModel.getCompanyId());
		aContent.setDynOrder(contentModel.getOrder());
		aContent.setTargetID(contentModel.getTargetId());
		aContent.setDynContent(contentModel.getContent());
		dynamicTag.addContent(aContent);

        try {
        	mailing.buildDependencies(false, applicationContext, false);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }

        mailingDao.saveMailing(mailing);
        final Admin admin = new AdminImpl();
        admin.setUsername(Utils.getUserName());
        StringBuilder description = new StringBuilder();
        description.append(dynamicTag.getDynName())
                .append("(")
                .append(aContent.getId())
                .append(")")
                .append(" in the ")
                .append(mailing.isIsTemplate() ? "template " : "mailing ")
                .append(mailing.getShortname())
                .append("(")
                .append(mailing.getId())
                .append(")");
        writeUserActivityLog(admin, "create textblock", description.toString());
		return aContent.getId();
	}

	@Override
	@Transactional
	@Validate("updateContentBlock")
	@SuppressWarnings("unchecked")
	public void updateContent(ContentModel contentModel) {
		DynamicTagContent content = getContent(contentModel);
		if (contentModel.getTargetId() != 0 && targetDao.getTarget(contentModel.getTargetId(), contentModel.getCompanyId()) == null) {
			throw new TargetNotExistException(contentModel.getTargetId());
		}
		
		Mailing mailing = mailingDao.getMailing(content.getMailingID(), contentModel.getCompanyId());
		if (mailing == null) {
			throw new MailingNotExistException();
		}
		DynamicTag dynamicTag = mailing.getDynTags().get(content.getDynName());
		if (dynamicTag == null) {
			throw new DynamicTagNameNotExistException();
		}
		Map<String, DynamicTagContent> dContent = dynamicTag.getDynContent();
        final int oldOrder = content.getDynOrder();
        if (oldOrder != contentModel.getOrder() && dContent.containsKey(Integer.toString(contentModel.getOrder()))) {
			throw new DynamicTagContentWithSameOrderAlreadyExist();
		}

        content.setDynOrder(contentModel.getOrder());
        int maxOrder = 0;
        int minOrder = Integer.MAX_VALUE;
        for (DynamicTagContent aContentTmp : dContent.values()) {
            if(content.getTargetID() != contentModel.getTargetId()
                    && aContentTmp.getTargetID() == contentModel.getTargetId()) {
                throw new DynamicTagContentWithSameTargetIdAlreadyExist();
            }
            if(aContentTmp.getDynOrder() > maxOrder){
                maxOrder = aContentTmp.getDynOrder();
            }
            if(aContentTmp.getDynOrder() < minOrder){
                minOrder = aContentTmp.getDynOrder();
            }
        }
    	content.setTargetID(contentModel.getTargetId());
    	content.setDynContent(contentModel.getContent());
		dynamicTagContentDao.updateContent(content);
        if(oldOrder != contentModel.getOrder()){
            StringBuilder description = new StringBuilder();
            String action = null;
            final Admin admin = new AdminImpl();
            admin.setUsername(Utils.getUserName());
            description.append(dynamicTag.getDynName())
                    .append("(")
                    .append(content.getId())
                    .append(")")
                    .append(" in the ")
                    .append(mailing.isIsTemplate() ? "template " : "mailing ")
                    .append(mailing.getShortname())
                    .append("(")
                    .append(mailing.getId())
                    .append(")");
            if(contentModel.getOrder() > oldOrder){
                if(contentModel.getOrder() > maxOrder){
                    writeUserActivityLog(admin,"do order bottom textblock", description.toString());
                } else {
                    writeUserActivityLog(admin,"do order down textblock", description.toString());
                }
            } else {
                if(contentModel.getOrder() < minOrder){
                    writeUserActivityLog(admin,"do order top textblock", description.toString());
                } else {
                    writeUserActivityLog(admin,"do order up textblock", description.toString());
                }
            }
        }
    }

    @Override
    public DynamicTagContent getContent(int companyID, int contentID) {
        return dynamicTagContentDao.getContent(companyID,contentID);
    }

    @Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

    private UserActivityLogService userActivityLogService;

    /**
     * Set user log service
     *
     * @param userActivityLogService
     */
    @Required
    public void setUserActivityLogService(UserActivityLogService userActivityLogService) {
        this.userActivityLogService = userActivityLogService;
    }

    protected void writeUserActivityLog(Admin admin, String action, String description) {
        if (userActivityLogService != null) {
            userActivityLogService.writeUserActivityLog(admin, action, description, logger);
        } else {
            logger.error("Missing userActivityLogService in " + this.getClass().getSimpleName());
            logger.info("Userlog: " + admin.getUsername() + " " + action + " " + description);
        }
    }

}
