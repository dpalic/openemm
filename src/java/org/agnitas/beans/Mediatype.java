/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.beans;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;

/**
 *
 * @author mhe
 */
public interface Mediatype extends Serializable {
    public static final int STATUS_NOT_USED = 0;
    public static final int STATUS_INACTIVE = 1;
    public static final int STATUS_ACTIVE = 2;

    /**
     * Getter for property param.
     *
     * @return Value of property param.
     */
    public String getParam() throws Exception;

    /**
     * Setter for property param.
     *
     * @param param New value of property param.
     */
    public void setParam(String param) throws Exception;

    /**
     * Getter for property priority.
     *
     * @return Value of property priority.
     */
    public int getPriority();

    /**
     * Setter for property priority.
     *
     * @param param New value of property priority.
     */
    public void setPriority(int priority);

    /**
     * Getter for property status.
     *
     * @return Value of property status.
     */
    public int getStatus();

    /**
     * Setter for property status.
     *
     * @param param New value of property status.
     */
    public void setStatus(int status);

    /** Getter for property companyID.
     * @return Value of property companyID.
     *
     */
    public int getCompanyID();

    /** Setter for property companyID.
     * @param companyID New value of property companyID.
     *
     */
    public void setCompanyID(int companyID);

    /**
     * Getter for property param.
     *
     * @return Value of property param.
     */
    public String getTemplate();

    /**
     * Setter for property param.
     *
     * @param param New value of property param.
     */
    public void setTemplate(String template);

    public void syncTemplate(Mailing mailing, ApplicationContext con);
}
