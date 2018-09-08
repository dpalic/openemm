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
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.beans.impl;

import java.sql.Timestamp;

import org.agnitas.beans.AdminEntry;

public class AdminEntryImpl implements AdminEntry {


    private String shortname;
    private String username;
    private String firstname;
    private String fullname;
    private String email;
    private int id;
    private Timestamp changeDate;
    private Timestamp creationDate;

    public AdminEntryImpl(Integer id, String username, String fullname, String shortname) {
        this(id, username, fullname, shortname, null);
    }

    public AdminEntryImpl(Integer id, String username, String fullname, String shortname, String email) {
        this.username = username;
        this.fullname = fullname;
        this.firstname = fullname;
        this.shortname = shortname;
        this.id = id;
        this.email = email;
        //  this.creationDate=creationDate;
        //  this.changeDate=changeDate;
    }

    public AdminEntryImpl(Integer id, String userName, String fullName, String firstName, String shortName, String email) {
        this.username = userName;
        this.fullname = fullName;
        this.firstname = firstName;
        this.shortname = shortName;
        this.id = id;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getShortname() {
        return shortname;
    }

    public int getId() {
        return id;
    }


	public Timestamp getChangeDate() {
		return changeDate;
	}


	public void setChangeDate(Timestamp changeDate) {
		this.changeDate = changeDate;
	}


	public Timestamp getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}