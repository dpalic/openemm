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
package org.agnitas.service;

import java.util.List;
import java.util.Map;

import org.agnitas.beans.AgnDBTagError;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.emm.core.velocity.VelocityCheck;

public interface MailingContentService {
	public final static String AGNDBTAG_WRONG_FORMAT = "AGNDBTAG_WRONG_FORMAT";
	public final static String AGNDBTAG_UNKNOWN_COLUMN = "AGNDBTAG_UNKNOWN_COLUMN";
	public List<AgnDBTagError> getInvalidAgnDBTags(String content,@VelocityCheck int companyID) throws Exception;
	public List<String> scanForAgnDBTags(String content);
	public Map<String, List<AgnDBTagError>> getAgnDBTagErrors( Map<String, DynamicTagContent> tagMap, @VelocityCheck int companyID ) throws Exception;
}
