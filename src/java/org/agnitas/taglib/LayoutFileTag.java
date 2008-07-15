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

package org.agnitas.taglib;

import javax.servlet.jsp.JspException;
import org.apache.struts.taglib.TagUtils;

/** Convert a relative file path.
 * Converts a relative path to point to the layout folder for the admin.
 */
public class LayoutFileTag extends BodyBase {
    
	protected String	file=null;
	protected String	scope="session";
    
	/**
	 * Setter for property column.
	 * 
	 * @param aCol New value of property column.
	 */
	public void	setFile(String file) {
		if(file!=null) {
			this.file=file;
		} else {
			this.file="";
		}
	}
    
	public void	setScope(String scope) {
		this.scope = scope;
	}

 
	/**
	 * lists shortnames
	 */
	public int doStartTag() throws JspException {
		// Look up the requested bean (if necessary)
		if(TagUtils.getInstance().lookup(pageContext, "emm.layout", scope) == null) {
			TagUtils.getInstance().write(pageContext, file);
			return SKIP_BODY;
		}

		String value = (String) TagUtils.getInstance().lookup(pageContext, "emm.layout", "baseUrl", scope);
        
		if(value == null) {
			TagUtils.getInstance().write(pageContext, file);
			return SKIP_BODY;
		}

		// Print this property value to our output writer
		TagUtils.getInstance().write(pageContext, value+file);

		// Continue processing this page
		return SKIP_BODY;
	}
}
