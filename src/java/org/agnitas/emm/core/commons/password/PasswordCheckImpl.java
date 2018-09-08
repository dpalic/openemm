package org.agnitas.emm.core.commons.password;

import org.agnitas.beans.Admin;
import org.agnitas.dao.AdminDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of {@link PasswordCheck}.
 * 
 * @author md
 */
public class PasswordCheckImpl implements PasswordCheck {

	/** DAO for accessing admin data. */
	private AdminDao adminDao;
	
	@Override
	public boolean checkAdminPassword(String password, Admin admin, PasswordCheckHandler handler) {
		try {
			// Check basic constraints
			PasswordUtil.checkPasswordConstraints(password);
			
			if(admin != null) {
				// Check that given password differs from current Admin password
				if(this.adminDao.isAdminPassword(admin, password)) {
					throw new PasswordMatchesCurrentPasswordException();
				}
			}
			
			return true;
		} catch(PasswordConstraintException e) {
			handleException(e, handler);
			
			return false;
		}
	}

	/**
	 * Resolve password exception and call corresponding method of handler.
	 *  
	 * @param ex password exception
	 * @param handler handler
	 */
	protected void handleException(PasswordConstraintException ex, PasswordCheckHandler handler) {
		try {
			throw ex;	// Just a trick to dispatch exception to handler methods. Required for subclassing...
		} catch(PasswordContainsNoLettersException e) {
			handler.handleNoLettersException();
		} catch(PasswordContainsNoDigitsException e) {
			handler.handleNoDigitsException();
		} catch(PasswordContainsNoSpecialCharactersException e) {
			handler.handleNoPunctuationException();
		} catch(PasswordTooShortException e) {
			handler.handlePasswordTooShort();
		} catch(PasswordMatchesCurrentPasswordException e) {
			handler.handleMatchesCurrentPassword();
		} catch(PasswordConstraintException e) {
			handler.handleGenericError();
		}
	}

    @Override
    public boolean passwordChanged(String username, String password) {
        Admin admin = adminDao.getAdminByLogin(username, password);
        if (StringUtils.isEmpty(password) || (admin != null && admin.getAdminID() > 0)) {
            return false;
        } else {
            return true;
        }
    }

	// ---------------------------------------------------------------------------------------------------- Dependency Injection
	/**
	 * Set DAO accessing admin data.
	 * 
	 * @param dao DAO accessing admin data.
	 */
	@Required
	public void setAdminDao(AdminDao dao) {
		this.adminDao = dao;
	}
}
