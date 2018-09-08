package org.agnitas.emm.core.userforms.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.agnitas.dao.UserFormDao;
import org.agnitas.emm.core.userforms.UserformService;

/**
 * Implementation of {@link UserformService}.
 * 
 * @author md
 *
 */
public class UserformServiceImpl implements UserformService {

	// ------------------------------------------------------------- Business Code

	/** Regular expression for validation of form name. */
	private static final Pattern FORM_NAME_PATTERN = Pattern.compile( "^[a-zA-Z0-9\\-_]+$"); 
	
	@Override
	public boolean isFormNameInUse(String formName, int formId, int companyId) {
		return userFormDao.isFormNameInUse( formName, formId, companyId);
	}

	@Override
	public boolean isValidFormName(String formName) {
		Matcher matcher = FORM_NAME_PATTERN.matcher( formName);
		
		return matcher.matches();
	}

	// ------------------------------------------------------------- Dependency Injection
	/**
	 * DAO for accessing userform data.
	 */
	protected UserFormDao userFormDao;

	/**
	 * Set DAO for accessing userform data.
	 * 
	 * @param userformDao DAO for accessing userform data
	 */
	public void setUserFormDao( UserFormDao userformDao) {
		this.userFormDao = userformDao;
	}

}
