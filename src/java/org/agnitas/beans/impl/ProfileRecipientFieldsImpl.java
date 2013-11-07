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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.beans.impl;

import org.agnitas.beans.ProfileRecipientFields;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Viktor Gema
 */
public class ProfileRecipientFieldsImpl implements ProfileRecipientFields {
    private String email;
    private String gender;
    private String mailtype;
    private String firstname;
    private String lastname;
    private String creation_date;
    private String change_date;
    private String title;
    private String temporaryId;
    private Integer updatedId;
    private Map<String, String> customFields;

    public ProfileRecipientFieldsImpl() {
        customFields = new HashMap<String, String>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMailtype() {
        return mailtype;
    }

    public void setMailtype(String mailtype) {
        this.mailtype = mailtype;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getChange_date() {
        return change_date;
    }

    public void setChange_date(String change_date) {
        this.change_date = change_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemporaryId() {
        return temporaryId;
    }

    public void setTemporaryId(String temporaryId) {
        this.temporaryId = temporaryId;
    }

    public Integer getUpdatedId() {
        return updatedId;
    }

    public void setUpdatedId(Integer updatedId) {
        this.updatedId = updatedId;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }
}
