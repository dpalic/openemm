package org.agnitas.emm.core.blacklist.service.impl;

import java.util.List;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.BlackListEntry;
import org.agnitas.beans.Mailinglist;
import org.agnitas.dao.BlacklistDao;
import org.agnitas.emm.core.binding.service.BindingService;
import org.agnitas.emm.core.binding.service.BindingServiceException;
import org.agnitas.emm.core.blacklist.service.BlacklistAlreadyExistException;
import org.agnitas.emm.core.blacklist.service.BlacklistModel;
import org.agnitas.emm.core.blacklist.service.BlacklistService;
import org.agnitas.emm.core.validator.annotation.Validate;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.apache.log4j.Logger;

public class BlacklistServiceImpl implements BlacklistService {

	/** The logger. */
	private static final transient Logger logger = Logger.getLogger( BlacklistServiceImpl.class);
	
	/** DAO for blacklists. */
	private BlacklistDao blacklistDao;
	
	/** Binding service to update binding status when blacklisting mail address. */
	private BindingService bindingService;
	
	@Override
	@Validate("commonBlacklist")
	public boolean insertBlacklist(BlacklistModel model) {
		if (checkBlacklist(model)) {
			throw new BlacklistAlreadyExistException(); 
		}
		
		boolean result = blacklistDao.insert(model.getCompanyId(), model.getEmail());
		
		if( result) {
			try {
				bindingService.updateBindingStatusByEmailPattern( model.getCompanyId(), model.getEmail(), BindingEntry.USER_STATUS_BLACKLIST, "Blacklisted");
			} catch( BindingServiceException e) {
				logger.error( "Unable to update binding status for blacklisted email: " + model.getEmail(), e);
			}
		}
		
		return result;
	}

	@Override
	@Validate("commonBlacklist")
	public boolean deleteBlacklist(BlacklistModel model) {
		return blacklistDao.delete(model.getCompanyId(), model.getEmail());
	}

	@Override
	@Validate("commonBlacklist")
	public boolean checkBlacklist(BlacklistModel model) {
		List<String> list = blacklistDao.getBlacklist(model.getCompanyId());
		for (String regex : list) {
			if (model.getEmail().toLowerCase().matches(regex.replace("%", ".+").toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> getEmailList(@VelocityCheck int companyID) throws Exception {
		return blacklistDao.getBlacklist(companyID);
	}

    @Override
    public List<BlackListEntry> getRecipientslList(@VelocityCheck int companyID) throws Exception {
        return blacklistDao.getBlacklistedRecipients(companyID);
    }

	@Override
	public List<Mailinglist> getMailinglistsWithBlacklistedBindings( BlacklistModel model) {
		return this.blacklistDao.getMailinglistsWithBlacklistedBindings( model.getCompanyId(), model.getEmail());
	}

	@Override
	public void updateBlacklistedBindings(BlacklistModel bm, List<Integer> mailinglistIds, int userStatus) {
		if( mailinglistIds.size() == 0) {
			if( logger.isInfoEnabled())
				logger.info( "List of mailinglist IDs is empty - doing nothing");
			
			return;
		}
			
		this.blacklistDao.updateBlacklistedBindings( bm.getCompanyId(), bm.getEmail(), mailinglistIds, userStatus);
	}

	// ------------------------------------------------------------------------------------------ Dependency Injection
	
    @Override
	public void setBlacklistDao(BlacklistDao blacklistDao) {
		this.blacklistDao = blacklistDao;
	}

    /**
     * Set binding service.
     * 
     * @param bindingService binding service
     */
    public void setBindingService( BindingService bindingService) {
    	this.bindingService = bindingService;
    }
}
