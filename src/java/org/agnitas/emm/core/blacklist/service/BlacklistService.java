package org.agnitas.emm.core.blacklist.service;

import java.util.List;

import org.agnitas.beans.BlackListEntry;
import org.agnitas.beans.Mailinglist;
import org.agnitas.dao.BlacklistDao;
import org.agnitas.emm.core.velocity.VelocityCheck;

public interface BlacklistService {

	boolean	insertBlacklist(BlacklistModel model);

	boolean deleteBlacklist(BlacklistModel model);

	boolean checkBlacklist(BlacklistModel model);
	
	List<String> getEmailList(@VelocityCheck int companyID) throws Exception;

    public List<BlackListEntry> getRecipientslList(@VelocityCheck int companyID) throws Exception;

    public void setBlacklistDao(BlacklistDao blacklistDao);
	
    public List<Mailinglist> getMailinglistsWithBlacklistedBindings( BlacklistModel model);

	public void updateBlacklistedBindings(BlacklistModel bm, List<Integer> mailinglists, int userStatus);
}
