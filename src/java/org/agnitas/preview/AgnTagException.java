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

import java.util.List;
import java.util.Vector;

// Throw it when the TagCheckImpl decides that tag or content contains errors
public class AgnTagException extends RuntimeException {


	private static final long serialVersionUID = -4720583899796412192L;
	private List<String[]> report; // each element of the report is an array with 3 elements :  [0]=the block which contains the error(s), [1]= the tag which is wrong, [2] = an error description
	private Vector<String> failures;

	public AgnTagException(String message,List<String[]> report, Vector<String> failures) {
		super(message);
		this.report = report;
		this.failures = failures;
	}

	public AgnTagException(String message,List<String[]> report ) {
		super(message);
		this.report = report;
	}

	public List<String[]> getReport() {
		return report;
	}
	public Vector<String> getFailures() {
		return failures;
	}


}
