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

package org.agnitas.cms.web.forms;

import javax.servlet.http.*;
import java.net.*;
import java.util.*;
import org.agnitas.cms.web.*;
import org.apache.struts.action.*;
import org.apache.struts.upload.*;


/**
 * @author Vyacheslav Stepanov
 */
public class CMTemplateForm extends CmsBaseForm {

	protected int cmTemplateId;

	private FormFile templateFile;

	public static final List<String> CHARTERSET_LIST = Arrays
			.asList("utf-8", "iso-8859-1", "iso-8859-15", "gb2312");

	private String charset = CHARTERSET_LIST.get(0);

	protected Map<Integer, Integer> oldAssignment;

	public int getCmTemplateId() {
		return cmTemplateId;
	}

	public void setCmTemplateId(int cmTemplateId) {
		this.cmTemplateId = cmTemplateId;
	}

	public FormFile getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(FormFile templateFile) {
		this.templateFile = templateFile;
	}

	public Map<Integer, Integer> getOldAssignment() {
		return oldAssignment;
	}

	public void setOldAssignment(Map<Integer, Integer> oldAssignment) {
		this.oldAssignment = oldAssignment;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * Validate the properties that have been set from this HTTP request, and
	 * return an <code>ActionErrors</code> object that encapsulates any
	 * validation errors that have been found. If no errors are found, return
	 * <code>null</code> or an <code>ActionErrors</code> object with no
	 * recorded error messages.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 * @return errors
	 */
	public ActionErrors formSpecificValidate(ActionMapping mapping,
								 HttpServletRequest request) {
		ActionErrors actionErrors = new ActionErrors();
		
		try {
			if(templateFile != null) {
				String utf8Name = URLEncoder.encode(templateFile.getFileName(), "utf-8");
				if(!templateFile.getFileName().equals(utf8Name)) {
					actionErrors.add("global",
							new ActionMessage("error.mailing.hosted_image_filename"));
				}
			}
			if(action == CMTemplateAction.ACTION_SAVE) {
				if(name.length() < 3) {
					actionErrors.add("shortname", new ActionMessage("error.nameToShort"));
				}
			}
		} catch(Exception e) {
			// do nothing
		}
		return actionErrors;
	}

}