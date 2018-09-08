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
package org.agnitas.beans;

import java.io.Serializable;

/**
 *
 * @author Zhykhariev Dmytro
 */

public interface AdminPreferences extends Serializable{
    public static final int MAILING_CONTENT_HTML_EDITOR = 1;
    public static final int MAILING_CONTENT_HTML_CODE = 0;

    public static final int MAILING_SETTINGS_EXPANDED = 0;
    public static final int MAILING_SETTINGS_COLLAPSED = 1;

    /**
     * Getter for property adminID.
     *
     * @return Value of property id of this Admin.
     */
    public int getAdminID();

    /**
     * Setter for property adminID.
     *
     * @param adminID the new value for the adminID.
     */
    public void setAdminID(int adminID);

    /**
     * Getter for the preferred mailing content view type
     */
    public int getMailingContentView();

    /**
     * Setter for the preferred mailing content view type
     *
     * @param mailingContentView the new value of mailingContentViewType
     */
    public void setMailingContentView(int mailingContentView);

    /**
     * Getter for the preferred list size
     */
    public int getListSize();

    /**
     * Setter for the preferred list size
     *
     * @param listSize the new value of list size
     */
    public void setListSize(int listSize);

    /**
     * Getter for the preferred mailing settings view type
     */
    public int getMailingSettingsView();

    /**
     * Setter for the preferred mailing settings view type
     *
     * @param mailingSettingsView the new value of mailingSettingsViewType
     */
    public void setMailingSettingsView(int mailingSettingsView);

}
