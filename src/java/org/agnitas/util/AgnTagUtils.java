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
package org.agnitas.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.TagDefinition;
import org.agnitas.beans.TagDefinition.TagType;
import org.agnitas.beans.TagDetails;
import org.agnitas.beans.Title;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.dao.TagDao;
import org.agnitas.dao.TitleDao;
import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDConstants;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDService;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.RequiredInformationMissingException;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.UIDStringBuilderException;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class AgnTagUtils {

	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(AgnTagUtils.class);

	public static String processTag(TagDetails aDetail, int customerID, ApplicationContext con, int mailingID, int mailinglistID, int companyID) {
		Map<String, String> allValues = aDetail.getTagParameters();
		if (allValues == null) {
			allValues = new Hashtable<String, String>();
		}

		String tagName = aDetail.getTagName();
		if ("agnONEPIXEL".equals(tagName)) {
			return processAgnOnepixelTag();
		} else if ("agnDATE".equals(tagName)) {
			return processAgnDateTag(allValues, con);
		}

		TagDao tagDao = (TagDao) con.getBean("TagDao");
		TagDefinition tagDefinition = tagDao.getTag(companyID, aDetail.getTagName());
		if (tagDefinition == null) {
			logger.error("Couldn't get tag " + aDetail.getTagName());
			return null;
		}
		String selectVal = tagDefinition.getSelectValue();

		// replace [company-id], [mailinglist-id] and [mailing-id] with real
		// values
		selectVal = SafeString.replace(selectVal, "[company-id]", Integer.toString(companyID));
		selectVal = SafeString.replace(selectVal, "[mailinglist-id]", Integer.toString(mailinglistID));
		selectVal = SafeString.replace(selectVal, "[mailing-id]", Integer.toString(mailingID));
		if (selectVal.contains("[rdir-domain]")) {
			CompanyDao cDao = (CompanyDao) con.getBean("CompanyDao");
			selectVal = SafeString.replace(selectVal, "[rdir-domain]", cDao.getCompany(companyID).getRdirDomain());
		}

		if ("agnPROFILE".equals(tagName) || "agnUNSUBSCRIBE".equals(tagName)) {
			return processAgnProfileAgnUnsubscribe(selectVal);
		}

		if ("agnFORM".equals(tagName)) {
			return processAgnForm(aDetail, selectVal);
		}

		String value = null;

		if (tagDefinition.getType() == TagType.COMPLEX) { // search and replace parameters
			if (aDetail.getTagName().equals("agnTITLE") || aDetail.getTagName().equals("agnTITLEFULL") || aDetail.getTagName().equals("agnTITLEFIRST")) {
				return processAgnTitle(aDetail, allValues, customerID, companyID, con);
			}

			int startPos = 0;
			int endPos = 0;
			while ((startPos = selectVal.indexOf('{')) != -1) {
				endPos = selectVal.indexOf('}', startPos);
				if (endPos == -1) {
					return null;
				}
				String paramName = selectVal.substring(startPos + 1, endPos);
				value = SafeString.getSQLSafeString(allValues.get(paramName));
				if (value == null) {
					return null; // no value found!
				}
				StringBuffer aBuf = new StringBuffer(selectVal);
				aBuf.replace(startPos, endPos + 1, value);
				selectVal = aBuf.toString();
			}
		}

		if (selectVal.contains("[agnUID]")) {
			selectVal = processAgnUid(value, selectVal, companyID, customerID, mailingID, con);
		}

		RecipientDao recipientDao = (RecipientDao) con.getBean("RecipientDao");
		String result = recipientDao.getField(selectVal, customerID, companyID);
		if (result == null) {
			logger.error("Error processing tag " + aDetail.getTagName() + " (" + aDetail.getFullText() + "). (mailing ID " + mailingID + ", customer ID " + customerID + ")");
			return null;
		} else {
			return result;
		}
	}

	/**
	 * Processed the {@code agnONEPIXEL} tag.
	 *
	 * @return substitute for tag
	 */
	private static String processAgnOnepixelTag() {
		return "";
	}

	/**
	 * Processed the {@code agnDATE} tag.
	 *
	 * @param allValues
	 *            parameters for tag
	 * @param con
	 *            Spring application context for accessing DAOs
	 *
	 * @return substitute for tag
	 */
	private static String processAgnDateTag(Map<String, String> allValues, ApplicationContext con) {
		String lang = allValues.get("language");
		if (lang == null) {
			lang = "de";
		}

		String country = allValues.get("country");
		if (country == null) {
			country = "DE";
		}

		// look for "type" in tag. Default to 0 if no tag is found.
		int type = 0;
		if (allValues.get("type") != null) {
			// if we found a "type" Attribute, take it.
			type = Integer.parseInt(allValues.get("type"));
		}
		MailingDao dao = (MailingDao) con.getBean("MailingDao");
		String format = dao.getFormat(type);

		SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale(lang, country));
		String date = sdf.format(new Date());
		return date;
	}

	private static String processAgnUid(String componentName, String selectVal, @VelocityCheck int companyID, int customerID, int mailingID, ApplicationContext con) {
		// create and replace agnUID
		try {
			int urlID = 0;

			try {
				MailingComponent component = (MailingComponent) con.getBean("MailingComponent");
				MailingComponentDao dao = (MailingComponentDao) con.getBean("MailingComponentDao");

				component = dao.getMailingComponentByName(mailingID, companyID, componentName);
				urlID = component.getUrlID();
			} catch (Exception e) {
				urlID = 0;
			}
			String uidstr = makeUIDString(companyID, customerID, mailingID, urlID, con);
			logger.error("UID: " + uidstr);
			selectVal = SafeString.replace(selectVal, "[agnUID]", uidstr);
		} catch (Exception e) {
			logger.error("processTag", e);
			// ???
		}

		return selectVal;
	}

	/**
	 * The correct replacement for a agnFORM-Tag is only created by backend via its function code
	 * This method only creates some dummy by its DB text value in tag_tbl
	 *
	 * @param tagDetail
	 * @param selectVal
	 * @return
	 */
	private static String processAgnForm(TagDetails tagDetail, String selectVal) {
		selectVal = SafeString.replace(selectVal, "{name}", tagDetail.getName());

		if (!selectVal.isEmpty() && selectVal.startsWith("'") && selectVal.endsWith("'")) {
			// trim the ' at start and end
			selectVal = selectVal.substring(1, selectVal.length() - 1);
		}

		return selectVal;
	}

	private static String processAgnProfileAgnUnsubscribe(String selectVal) {
		if (!selectVal.isEmpty() && selectVal.startsWith("'") && selectVal.endsWith("'")) {
			// trim the ' at start and end
			selectVal = selectVal.substring(1, selectVal.length() - 1);
		}

		return selectVal;
	}

	private static String processAgnTitle(TagDetails aDetail, Map<String, String> allValues, int customerID, @VelocityCheck int companyID, ApplicationContext con) {
		try {
			int titleID = Integer.parseInt(allValues.get("type"));
			return generateSalutation(titleID, customerID, aDetail.getTagName(), con, companyID);
		} catch (Exception e) {
			return null;
		}
	}

	private static String makeUIDString(@VelocityCheck int companyID, int customerID, int mailingID, int urlID, ApplicationContext con) {
		ExtensibleUIDService service = (ExtensibleUIDService) con.getBean(ExtensibleUIDConstants.SERVICE_BEAN_NAME);

		ExtensibleUID uid = service.newUID();
		uid.setCompanyID(companyID);
		uid.setCustomerID(customerID);
		uid.setMailingID(mailingID);
		uid.setUrlID(urlID);

		try {
			return service.buildUIDString(uid);
		} catch (UIDStringBuilderException e) {
			logger.error("makeUIDString", e);
			return "";
		} catch (RequiredInformationMissingException e) {
			logger.error("makeUIDString", e);
			return "";
		}
	}

	private static String generateSalutation(int titleID, int customerID, String titleType, ApplicationContext con, @VelocityCheck int companyID) {
		TitleDao titleDao = (TitleDao) con.getBean("TitleDao");
		Title title = titleDao.getTitle(titleID, companyID);

		if (title == null) {
			return null;
		}

		Recipient cust = (Recipient) con.getBean("Recipient");
		cust.setCompanyID(companyID);
		cust.setCustomerID(customerID);
		Map<String, Object> custData = cust.getCustomerDataFromDb();

		Integer gender = new Integer(Title.GENDER_UNKNOWN);
		String firstname = "";
		String lastname = "";
		String titleString = "";

		try {
			gender = new Integer(Integer.parseInt((String) custData.get("gender")));
		} catch (Exception e) {
			// do nothing
		}

		try {
			firstname = ((String) custData.get("firstname")).trim();
		} catch (Exception e) {
			// do nothing
		}

		try {
			lastname = ((String) custData.get("lastname")).trim();
		} catch (Exception e) {
			// do nothing
		}
		if (StringUtils.isEmpty(lastname)) {
			// generate salutation for gender unknown if no lastname ist
			// available
			gender = new Integer(Title.GENDER_UNKNOWN);
		}

		try {
			titleString = ((String) custData.get("title")).trim();
		} catch (Exception e) {
			// do nothing
		}

		String returnValue = (String) title.getTitleGender().get(gender);
		if (gender.intValue() != Title.GENDER_UNKNOWN) {
			if (!titleString.equals("")) {
				returnValue = returnValue + " " + titleString;
			}
			if (titleType.equals("agnTITLEFULL")) {
				returnValue = returnValue + " " + firstname + " " + lastname;
			} else if (titleType.equals("agnTITLE")) {
				returnValue = returnValue + " " + lastname;
			} else if (titleType.equals("agnTITLEFIRST")) {
				returnValue = (String) title.getTitleGender().get(gender) + " " + firstname;
			}
		}
		return returnValue;
	}
}
