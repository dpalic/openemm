package org.agnitas.emm.core.target.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.dao.exception.target.TargetGroupPersistenceException;
import org.agnitas.emm.core.target.exception.TargetGroupException;
import org.agnitas.emm.core.target.exception.TargetGroupIsInUseException;
import org.agnitas.emm.core.target.service.TargetGroupLocator;
import org.agnitas.emm.core.target.service.TargetService;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.target.Target;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class TargetServiceImpl implements TargetService {

	/**
	 * The logger. 
	 */
	private static final transient Logger logger = Logger.getLogger( TargetServiceImpl.class);
	
	private static final transient Pattern TARGET_IDS_FROM_EXPRESSION_PATTERN = Pattern.compile( "^.*?(\\d+)(.*)$");
	public static final String TARGET_EXPRESSION_POSSIBLE_CHARS = "[&|()! ]";

	@Override
	public void deleteTargetGroup(int targetGroupID, @VelocityCheck int companyID) throws TargetGroupException, TargetGroupPersistenceException {
		if(logger.isInfoEnabled()) {
			logger.info("Deleting target group " + targetGroupID + " of company " + companyID);
		}
		
		if(isTargetGroupInUse(targetGroupID, companyID)) {
			if(logger.isInfoEnabled()) {
				logger.info("Cannot delete target group " + targetGroupID + " - target group is in use");
			}
			
			throw new TargetGroupIsInUseException(targetGroupID);
		}
		
		this.targetDao.deleteTarget(targetGroupID, companyID);
	}

	@Override
    public boolean hasMailingDeletedTargetGroups(Mailing mailing) {

		if( logger.isInfoEnabled())
			logger.info( "Checking mailing " + mailing.getId() + " for deleted target groups");
		
    	Set<Integer> targetIds = getAllTargetIdsForMailing( mailing);
    	
    	for( int targetId : targetIds) {
    		Target target = targetDao.getTarget(targetId, mailing.getCompanyID());

    		if( target == null) {
    			if( logger.isInfoEnabled()) {
    				logger.info( "Found non-existing target group " + targetId + ". It's assumed to be physically deleted.");
    			}

    			continue;
    		}
    		
    		if( target.getDeleted() != 0) {
    			if( logger.isInfoEnabled()) {
    				logger.info( "Found deleted target group " + targetId + ".");
    			}
    			
    			return true;
    		}
    	}
    	
    	if( logger.isInfoEnabled())
    		logger.info( "Mailing " + mailing.getId() + " does not contain any deleted target groups");
    	
    	return false;
    }
	
	private Set<Integer> getAllTargetIdsForMailing( Mailing mailing) {
		
		if( logger.isDebugEnabled()) {
			logger.debug( "Collecting target groups IDs for mailing " + mailing.getId());
		}
		
    	Set<Integer> targetIds = new HashSet<Integer>();

    	targetIds.addAll(getTargetIdsFromExpression(mailing));
    	targetIds.addAll(getTargetIdsFromContent(mailing));
    	targetIds.addAll(getTargetIdsFromAttachments(mailing));
    	
		if( logger.isDebugEnabled()) {
			logger.debug( "Collected " + mailing.getId() + " different target group IDs in total for mailing " + mailing.getId());
		}
    	
    	return targetIds;
	}
    
	@Override
    public Set<Integer> getTargetIdsFromExpression(Mailing mailing) {
		if( logger.isDebugEnabled()) {
			logger.debug( "Collecting target groups IDs for mailing " + mailing.getId() + " from mailing target expression.");
		}

    	if (mailing == null) {
    		return new HashSet<Integer>();
    	}
    	
		String expression = mailing.getTargetExpression();
		Set<Integer> targetIds = getTargetIdsFromExpression(expression);

		if( logger.isDebugEnabled()) {
			logger.debug( "Collected " + mailing.getId() + " different target group IDs from target expression for mailing " + mailing.getId());
		}

		return targetIds;
    }

	public static Set<Integer> getTargetIdsFromExpression(String targetExpression) {
		Set<Integer> targetIds = new HashSet<>();
		if( targetExpression != null) {
			Matcher matcher = TARGET_IDS_FROM_EXPRESSION_PATTERN.matcher(targetExpression);
			while( matcher.matches()) {
				targetIds.add(Integer.parseInt(matcher.group(1)));
				targetExpression = matcher.group(2);
				matcher = TARGET_IDS_FROM_EXPRESSION_PATTERN.matcher(targetExpression);
			}
		}
		return targetIds;
	}
	
	@Override
	public Set<Integer> getTargetIdsFromExpressionString(String targetExpression) {
		return getTargetIdsFromExpression(targetExpression);
	}

    private Set<Integer> getTargetIdsFromContent(Mailing mailing) {
		if( logger.isDebugEnabled()) {
			logger.debug( "Collecting target groups IDs for mailing " + mailing.getId() + " from content blocks.");
		}
		
    	Set<Integer> targetIds = new HashSet<Integer>();
    	
    	for( DynamicTag tag : mailing.getDynTags().values()) {
    		for( Object contentObject : tag.getDynContent().values()) {
    			DynamicTagContent content = (DynamicTagContent) contentObject;
    			targetIds.add( content.getTargetID());
    		}
    	}
    	
		if( logger.isDebugEnabled()) {
			logger.debug( "Collected " + mailing.getId() + " different target group IDs from content blocks for mailing " + mailing.getId());
		}
    	
    	return targetIds;
    }
    
    private Set<Integer> getTargetIdsFromAttachments(Mailing mailing) {
		if( logger.isDebugEnabled()) {
			logger.debug( "Collecting target groups IDs for mailing " + mailing.getId() + " from attachments.");
		}

		List<MailingComponent> result = mailingComponentDao.getMailingComponents(mailing.getId(), mailing.getCompanyID(), MailingComponent.TYPE_ATTACHMENT);
    	
    	Set<Integer> targetIds = new HashSet<Integer>();
    	for( MailingComponent component : result) {
    		targetIds.add( component.getTargetID());
    	}
    	
		if( logger.isDebugEnabled()) {
			logger.debug( "Collected " + mailing.getId() + " different target group IDs from attachments for mailing " + mailing.getId());
		}
    	
    	return targetIds;
    }

	@Override
	public boolean isTargetGroupInUse(int targetID, @VelocityCheck int companyID) throws TargetGroupException {
		return this.targetGroupLocator.isTargetGroupInUse(targetID, companyID);
	}

    // -------------------------------------------------------------- Dependency Injection
	/**
	 * DAO accessing target groups.
	 */
    protected TargetDao targetDao;
    
    /**
     * DAO accessing mailing components.
     */
    private MailingComponentDao mailingComponentDao;
    
    /**
     * Component to locate target groups.
     */
    private TargetGroupLocator targetGroupLocator;
    
    /**
     * Set DAO for accessing target group data.
     * 
     * @param targetDao DAO for accessing target group data
     */
    public void setTargetDao( TargetDao targetDao) {
    	this.targetDao = targetDao;
    }
    
    /**
     * Set DAO for accessing mailing component data.
     * 
     * @param mailingComponentDao DAO for accessing mailing component data
     */
    public void setMailingComponentDao( MailingComponentDao mailingComponentDao) {
    	this.mailingComponentDao = mailingComponentDao;
    }

    /**
     * Set locator for target groups.
     * 
     * @param locator locator for target groups
     */
    @Required
    public void setTargetGroupLocator(TargetGroupLocator locator) {
    	this.targetGroupLocator = locator;
    }
}
