/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.beans.impl;

import java.security.MessageDigest;
import java.util.Locale;

import org.agnitas.beans.Admin;
import org.agnitas.util.AgnUtils;

public class AdminImpl implements Admin {
    
    private static final long serialVersionUID = -6728189620613687946L;
	protected org.agnitas.beans.Company company=new org.agnitas.beans.impl.CompanyImpl();
    protected int adminID;
    protected int layoutID;
    protected String adminCountry;
    protected String shortname;
    protected String username;
    protected String password;
    protected String fullname;
    protected String adminLang;
    protected String adminLangVariant="";
    protected String adminTimezone;
    protected java.sql.Timestamp creationDate;
    protected java.util.Date lastPasswordChange=new java.util.Date();
    protected int mailtracking=0;
    protected int preferredListSize;
    private int defaultImportProfileID;
    
    /**
     * Holds value of property group.
     */
    protected org.agnitas.beans.AdminGroup group=new org.agnitas.beans.impl.AdminGroupImpl();
    
    // CONSTRUCTOR:
    public AdminImpl() {
    }
    
    // * * * * *
    //  SETTER:
    // * * * * *
    public void setCompany(org.agnitas.beans.Company id) {
        company=id;
    }
    
    public void setShortname(String name) {
        shortname=name;
    }
    
    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }
    
    public void setCompanyID(int companyID) {
        company.setId(companyID);
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setCreationDate(java.sql.Timestamp creationDate) {
        this.creationDate = creationDate;
    }
    
    public void setLastPasswordChange(java.util.Date lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }
    
    public void setPassword(String password) {
        this.password = password;
        try {
            this.passwordHash=MessageDigest.getInstance("MD5").digest(password.getBytes());
        } catch (Exception e) {
            AgnUtils.logger().error("fatal: "+e.getMessage());
        }
    }
    
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    
    public void setAdminLang(String adminLang) {
        this.adminLang = adminLang;
    }
    
    public void setAdminLangVariant(String adminLangVariant) {
        this.adminLangVariant = adminLangVariant;
    }
    
    public void setAdminTimezone(String adminTimezone) {
        this.adminTimezone = adminTimezone;
    }
    
    public void setLayoutID(int layoutID) {
        this.layoutID = layoutID;
    }
    
    public void setAdminCountry(String adminCountry) {
        this.adminCountry = adminCountry;
    }
    
    public void setMailtracking(int mailtracking) {
        this.mailtracking=mailtracking;
    }
    
    
    // * * * * *
    //  GETTER:
    // * * * * *
    public org.agnitas.beans.Company getCompany() {
        
        return company;
    }
    
    public String getShortname() {
        return shortname;
    }
    
    public String getPassword() {
        return password;
    }
    
    public int getAdminID() {
        return this.adminID;
    }
    
    public int getCompanyID() {
        return company.getId();
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public java.sql.Timestamp getCreationDate() {
        return creationDate;
    }
    
    public java.util.Date getLastPasswordChange() {
        return lastPasswordChange;
    }
    
    public String getFullname() {
        return this.fullname;
    }
    
    public String getAdminLang() {
        return this.adminLang;
    }
    
    public String getAdminLangVariant() {
        return this.adminLangVariant;
    }
    
    public String getAdminTimezone() {
        return this.adminTimezone;
    }
    
    public int getLayoutID() {
        return this.layoutID;
    }
    
    public String getAdminCountry() {
        return this.adminCountry;
    }
    
    public int getMailtracking() {
        return mailtracking;
    }
    
    /**
     * Getter for property groupID.
     * @return Value of property groupID.
     */
    public org.agnitas.beans.AdminGroup getGroup() {
        
        return this.group;
    }
    
    /**
     * Setter for property groupID.
     * @param group
     */
    public void setGroup(org.agnitas.beans.AdminGroup group) {
        
        this.group = group;
    }
    
    /**
     * Holds value of property adminPermissions.
     */
    protected java.util.Set adminPermissions;
    
    /**
     * Getter for property adminPermissions.
     * @return Value of property adminPermissions.
     */
    public java.util.Set getAdminPermissions() {
        
        return this.adminPermissions;
    }
    
    /**
     * Setter for property adminPermissions.
     * @param adminPermissions New value of property adminPermissions.
     */
    public void setAdminPermissions(java.util.Set adminPermissions) {
        
        this.adminPermissions = adminPermissions;
    }
    
    public boolean permissionAllowed(String token) {
        boolean result=false;

        if(this.adminPermissions.contains(token)) {
            result=true;
        }
        
        if(this.group!=null) {
            if(this.group.permissionAllowed(token)) {
                result=true;
            }
        }
        
        return result;
    }
    
    public java.util.Locale getLocale() {
        return new Locale(this.adminLang, this.adminCountry);
    }
    
    /**
     * Holds value of property passwordHash.
     */
    private byte[] passwordHash;
    
    /**
     * Getter for property passwordHash.
     * @return Value of property passwordHash.
     */
    public byte[] getPasswordHash() {
        return this.passwordHash;
    }
    
    /**
     * Setter for property passwordHash.
     * @param passwordHash New value of property passwordHash.
     */
    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

	public int getPreferredListSize() {
		return this.preferredListSize;
	}

	public void setPreferredListSize(int preferredlistsize) {
		this.preferredListSize = preferredlistsize;
		
	}
    
    public int getDefaultImportProfileID() {
        return defaultImportProfileID;
    }

    public void setDefaultImportProfileID(int defaultImportProfileID) {
        this.defaultImportProfileID = defaultImportProfileID;
    }
}
