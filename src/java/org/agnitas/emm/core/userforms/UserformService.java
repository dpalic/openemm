package org.agnitas.emm.core.userforms;

/**
 * Service layer for forms.
 * 
 * @author md
 */
public interface UserformService {
	
	/**
	 * Checks, if there is another form with same name.
	 * 
	 * @param formName name of form
	 * @param formId ID of current form
	 * @param companyId company ID
	 * 
	 * @return true, if form name is already in use
	 */
	public boolean isFormNameInUse( String formName, int formId, int companyId);

	/**
	 * Checks, if the form name does not contain invalid characters.
	 * 
	 * @param formName form name to check
	 * 
	 * @return true, if form name is valid
	 */
	public boolean isValidFormName(String formName);
}
