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
package org.agnitas.target;

/**
 * Error indicator for target rule.
 *
 * @author md
 */
public class TargetError {

	/**
	 * List of defined error keys.
	 *
	 * @author md
	 */
	public static enum ErrorKey {
		/** Key indicates rule that cannot be validated. Maybe validator is missing. */
		CANNOT_VALIDATE( "error.target.validate.cannot_validate"),

		/** Indicator for invalid mailing ID. */
		INVALID_MAILING( "error.target.validate.invalid_mailing"),

		/** Indicator for mailing ID that is not an internval mailing. */
		NOT_AN_INTERVAL_MAILING( "error.target.validate.not_an_interval_mailing");

		/** Error key. */
		final String key;

		/**
		 * Creates a new enum item with given error key.
		 *
		 * @param key error key
		 */
		ErrorKey( String key) {
			this.key = key;
		}

		/**
		 * Returns the error key.
		 *
		 * @return error key
		 */
		public String getKey() {
			return this.key;
		}
	}

	/** Key for error */
	private final ErrorKey errorKey;

	/**
	 *
	 * @param errorKey
	 */
	public TargetError( ErrorKey errorKey) {
		this.errorKey = errorKey;
	}

	/**
	 * Returns the key of the error.
	 *
	 * @return key of error
	 */
	public String getErrorKey() {
		return this.errorKey.getKey();
	}
}
