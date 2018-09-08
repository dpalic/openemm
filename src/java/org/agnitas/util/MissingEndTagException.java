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

/**
 * Exception indicating a missing end tag.
 *
 * Do not mistake this exception with {@link UnclosedTagException} that indicates a
 * missing &quot;]&quot;.
 *
 * @author md
 */
public class MissingEndTagException extends DynTagException {

	/** Serial version UID. */
	private static final long serialVersionUID = -4429200150965765977L;

	/**
	 * Create a new exception.
	 *
	 * @param lineNumber line number in which the error occurred
	 * @param tag name of erroneous tag
	 */
	public MissingEndTagException( int lineNumber, String tag) {
		super( lineNumber, tag, "Missing value tag in '" + tag + "' at line " + lineNumber);
	}
}
