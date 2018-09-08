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

package org.agnitas.cms.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Vyacheslav Stepanov
 */
public class CmsUtils {
	private static final transient Logger logger = Logger.getLogger(CmsUtils.class);

	public static final String UNKNOWN_MIME_TYPE = "application/octet-stream";

	public static String appendImageURLsWithSystemUrl(String html) {
		String systemUrl = AgnUtils.getDefaultValue("system.url");
		String resultHtml = html.replaceAll("/cms_image", systemUrl + "/cms_image");
		return resultHtml;
	}

	public static String removeSystemUrlFromImageUrls(String html) {
		String systemUrl = AgnUtils.getDefaultValue("system.url");
		String resultHtml = html.replaceAll(systemUrl + "/cms_image", "/cms_image");
		return resultHtml;
	}

	public static String generateMediaFileUrl(int imageId) {
		return "/cms_image?fid=" + imageId;
	}

	public static String getDefaultPlaceholderName() {
		return "default placeholder";
	}

	public static String getDefaultCMTemplate() {
		return "[agnDYN name=\"" + getDefaultPlaceholderName() + "\"/]";
	}

	public static String fixEncoding(String sourceStr) {
		try {
			return new String(sourceStr.getBytes(Charset.forName("iso-8859-1").name()), Charset.forName("UTF-8").name());
		} catch (UnsupportedEncodingException e) {
			logger.warn("Wrong charset name", e);
		}
		return "";
	}

	public static void generateClassicTemplate(final int mailingId, final HttpServletRequest request, final WebApplicationContext aContext) {
		final ClassicTemplateGenerator classicTemplateGenerator = (ClassicTemplateGenerator) aContext.getBean("ClassicTemplateGenerator");
		classicTemplateGenerator.generate(mailingId, request, true, true);
	}

	public static boolean isOracleDB() {
		org.hibernate.dialect.Dialect dialect = org.hibernate.dialect.DialectFactory.buildDialect(CmsUtils.getDefaultValue("jdbc.cmsDB.dialect"));

		if (dialect instanceof org.hibernate.dialect.Oracle9Dialect || dialect instanceof org.hibernate.dialect.OracleDialect) {
			return true;
		}
		return false;
	}

	public static String getDefaultValue(String key) {
		ResourceBundle defaults = null;
		String result = null;

		try {
			defaults = ResourceBundle.getBundle("cms");
		} catch (Exception e) {
			logger.error("getDefaultValue: " + e.getMessage());
			return null;
		}

		try {
			result = defaults.getString(key);
		} catch (Exception e) {
			logger.error("getDefaultValue: " + e.getMessage());
			result = null;
		}
		return result;
	}
}
