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
package org.agnitas.dao;

import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Andreas Rehak
 */
public interface OnepixelDao extends ApplicationContextAware	{

    /**
     *  Increases count of mailing openings.
     *
     * @param companyID
     *          The id of mailing company.
     * @param recipientID
     *          The id of opener.
     * @param mailingID
     *          The id of opened mailing.
     * @param remoteAddr
     *          The ip address of opener.
     * @return  true on success.
     */

	boolean	writePixel( @VelocityCheck int companyID, int recipientID, int mailingID, String remoteAddr);

/*
	boolean	writeClick( @VelocityCheck int companyID, int recipientID, int mailingID, String remoteAddr, int si, String pageTag);

	boolean  writeValnum( @VelocityCheck int companyID, int recipientID, int mailingID, String remoteAddr, int si, String pageTag, double numVal);

	boolean  writeValAlpha( @VelocityCheck int companyID, int recipientID, int mailingID, String remoteAddr, int si, String pageTag, String alphaVal);
*/
}
