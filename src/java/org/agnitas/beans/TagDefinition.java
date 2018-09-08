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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class TagDefinition {
	private static Pattern complexParameterPattern = Pattern.compile("\\{[A-Za-z0-9_]+\\}");

	public enum TagType {
		/**
		 * Selects in DB without replacements like {name}
		 */
		SIMPLE,

		/**
		 * Selects in DB with replacements like {name}
		 */
		COMPLEX,

		/**
		 * Executes LUA-Code from tag_function_tbl
		 */
		FUNCTION,

		/**
		 * Only agnDYN and agnDVALUE with special evaluation
		 */
		FLOW;

		public static TagType getTypeFromString(String value) throws Exception {
			if ("SIMPLE".equalsIgnoreCase(value)) {
				return SIMPLE;
			} else if ("COMPLEX".equalsIgnoreCase(value)) {
				return COMPLEX;
			} else if ("FUNCTION".equalsIgnoreCase(value)) {
				return FUNCTION;
			} else if ("FLOW".equalsIgnoreCase(value)) {
				return FLOW;
			} else {
				throw new Exception("Invalid TagDefinitionType");
			}
		}
	}

	/**
	 * Only names starting with "agn" are allowed
	 */
	private String name;

	/**
	 * Type of this tag
	 */
	private TagType type;

	/**
	 * selectvalue String of this tag.
	 * This is only effective for SIMPLE and COMPLEX tags which select data from DB
	 */
	private String selectValue;

	/**
	 * Mandatory Parameters which are included in the selectvalue like {name}.
	 * This is only effective for COMPLEX and FLOW tags
	 *
	 * An agnTag in a text component my have other non mandatory parameters also.
	 * These do not have to be included in the selectvalue.
	 */
	private List<String> mandatoryParameters;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TagType getType() {
		return type;
	}

	public void setType(TagType type) {
		this.type = type;
	}

	public void setTypeString(String typeString) throws Exception {
		this.type = TagType.getTypeFromString(typeString);
	}

	public String getSelectValue() {
		return selectValue;
	}

	public void setSelectValue(String selectValue) {
		this.selectValue = selectValue;
		mandatoryParameters = new ArrayList<String>();
		if (StringUtils.isNotBlank(selectValue) && (type == TagType.COMPLEX || type == TagType.FLOW)) {
			Matcher complexParameterMatcher = complexParameterPattern.matcher(selectValue);
			while (complexParameterMatcher.find()) {
				String parameterName = complexParameterMatcher.group();
				mandatoryParameters.add(parameterName.substring(1, parameterName.length() - 1));
			}
		}
	}

	/**
	 * Only COMPLEX and FLOW tags have effective mandatory parameters
	 *
	 * @return
	 */
	public List<String> getMandatoryParameters() {
		return mandatoryParameters;
	}

	/**
	 * Only COMPLEX and FLOW tags have effective mandatory parameters
	 *
	 * @param mandatoryParameters
	 */
	public void setMandatoryParameters(List<String> mandatoryParameters) {
		this.mandatoryParameters = mandatoryParameters;
	}
}
