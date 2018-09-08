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
 * Exception indicating errors with dynamic tags.
 *
 * @author md
 */
public class DynTagException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = -9022567996889651545L;

	/** Line number in which the error occurred. */
	private final int line;

	/** Name of erroneous tag. */
	private final String tag;

	/**
	 * Create a new exception without error message or cause.
	 *
	 * @param lineNumber line number in which the error occurred
	 * @param tag name of erroneous tag
	 */
	public DynTagException(int lineNumber, String tag) {
		super();

		this.line = lineNumber;
		this.tag = tag;
	}

	/**
	 * Create a new exception with error message and cause.
	 *
	 * @param lineNumber line number in which the error occurred
	 * @param tag name of erroneous tag
	 * @param message error message
	 * @param cause the cause
	 */
	public DynTagException(int lineNumber, String tag, String message, Throwable cause) {
		super(message, cause);

		this.line = lineNumber;
		this.tag = tag;
	}

	/**
	 * Create a new exception with error message.
	 *
	 * @param lineNumber line number in which the error occurred
	 * @param tag name of erroneous tag
	 * @param message error message
	 */
	public DynTagException(int lineNumber, String tag, String message) {
		super(message);

		this.line = lineNumber;
		this.tag = tag;
	}

	/**
	 * Create a new exception with cause.
	 *
	 * @param lineNumber line number in which the error occurred
	 * @param tag name of erroneous tag
	 * @param cause the cause
	 */
	public DynTagException(int lineNumber, String tag, Throwable cause) {
		super(cause);

		this.line = lineNumber;
		this.tag = tag;
	}

	/**
	 * Returns the line number in which the error occurred.
	 *
	 * @return line number
	 */
	public int getLineNumber() {
		return this.line;
	}

	/**
	 * Returns the name of the erroneous tag.
	 *
	 * @return name of tag
	 */
	public String getTag() {
		return this.tag;
	}
}
