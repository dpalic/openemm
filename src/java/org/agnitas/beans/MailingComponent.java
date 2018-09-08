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

import java.sql.Blob;
import java.util.Date;

import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 * 
 * @author Martin Helff
 */
public interface MailingComponent extends java.io.Serializable {
	public static final int TYPE_ATTACHMENT = 3;
	public static final int TYPE_PERSONALIZED_ATTACHMENT = 4;
	public static final int TYPE_HOSTED_IMAGE = 5;
	public static final int TYPE_IMAGE = 1;
	public static final int TYPE_TEMPLATE = 0;
	public static final int TYPE_PREC_ATTACHMENT = 7;
	public static final int TYPE_THUMBMAIL_IMAGE = 8;

	public boolean loadContentFromURL();

	public String makeEMMBlock();

	public byte[] getBinaryBlock();

	public int getId();

	public String getComponentName();

	public String getComponentNameUrlEncoded();

	public String getEmmBlock();

	public String getMimeType();

	public int getTargetID();

	public int getType();

	public void setBinaryBlock(byte[] cid);

	public void setBinaryBlob(Blob imageBlob);

	public Blob getBinaryBlob();

	public void setCompanyID(@VelocityCheck int cid);

	public void setId(int cid);

	public void setComponentName(String cid);

	public void setEmmBlock(String cid);

	public void setMailingID(int cid);

	public void setMimeType(String cid);

	public void setTargetID(int targetID);

	public void setType(int cid);

	public int getMailingID();

	public int getCompanyID();

	public Date getTimestamp();

	public void setTimestamp(Date timestamp);

	public String getLink();

	public void setLink(String link);

	public int getUrlID();

	public void setUrlID(int urlID);

	public String getDescription();

	public void setDescription(String description);

	public Date getStartDate();

	public void setStartDate(Date startDate);

	public Date getEndDate();

	public void setEndDate(Date endDate);
}
