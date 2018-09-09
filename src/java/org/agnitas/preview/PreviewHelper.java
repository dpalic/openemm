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
package org.agnitas.preview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * some helper methods for handling mail head, and error messages
 *
 * @author ms
 *
 */
public class PreviewHelper {

	public static String getFrom(String head) {
		Pattern pattern = Pattern.compile("\\s*From\\s*:(.*)");
		Matcher matcher = pattern.matcher(head);
		if (matcher.find()) {
			return matcher.group(1).trim();
		} else {
			return null;
		}
	}

	public static String getSubject(String head) {
		Pattern pattern = Pattern.compile("\\s*Subject\\s*:(.*)");
		Matcher matcher = pattern.matcher(head);
		if (matcher.find()) {
			return matcher.group(1).trim();
		} else {
			return null;
		}
	}

	/**
	 * extract the different tags and corresponding tag-errors from
	 * the error report
	 *
	 * @param report -
	 *            each line has to use the following structure
	 *            [agnTag]:errormessage#
	 * @return a map with the tag as key and the error as value
	 */
	public static Map<String, String> getTagsWithErrors(StringBuffer report) {

		Map<String, String> tagWithErrors = new HashMap<String, String>();
		String reportString = report.toString();
		Pattern tagPattern = Pattern.compile("(\\[.*?):(.*?)#");
		Matcher matcher = tagPattern.matcher(reportString);
		while (matcher.find()) {
			tagWithErrors.put(matcher.group(1), matcher.group(2));
		}
		return tagWithErrors;
	}

	/**
	 * extract the errormessages which are not related with a tag
	 * @param report
	 * @return list of strings describing the error
	 */
	public static List<String> getErrorsWithoutATag(StringBuffer report) {
			List<String> errorList =  new ArrayList<String>();
			String reportString = report.toString();
			Pattern failedToParsePattern = Pattern.compile("\\s*Failed to parse\\s*:\\s*(.*?)\\s*#");
			Matcher matcher = failedToParsePattern.matcher(reportString);
			while(matcher.find()) {
				errorList.add(matcher.group(1));
			}

			return errorList;
 	}

}
