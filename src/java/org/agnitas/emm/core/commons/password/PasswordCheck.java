package org.agnitas.emm.core.commons.password;

import org.agnitas.beans.Admin;

/**
 * Interface for password checking code.
 * 
 * @author md
 *
 */
public interface PasswordCheck {

	/**
	 * Check admin password. Checks some constraint (length, structure). If {@code admin} is not {@code null}, then 
	 * it's checked, that given password differs from current admin password.
	 * 
	 * @param password password to check
	 * @param admin admin for comparison with current password or {@code null}
	 * @param handler handler for dealing with errors
	 * 
	 * @return {@code true} if password is ok, otherwise {@code false}
	 */
	public boolean checkAdminPassword(String password, Admin admin, PasswordCheckHandler handler);

    /**
     * Check if admin password changed.
     * Method checked if given password differs from existed.
     *
     * @param password password to check (new password)
     * @param username user login
     *
     * @return {@code true} if password changed, otherwise {@code false}
     */
    public boolean passwordChanged(String username, String password);
	
}
