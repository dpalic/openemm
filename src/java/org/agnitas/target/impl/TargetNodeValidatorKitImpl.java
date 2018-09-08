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
package org.agnitas.target.impl;

import org.agnitas.target.TargetNodeValidator;
import org.agnitas.target.TargetNodeValidatorKit;

/**
 * Auxiliary class containing validators for all type of target nodes.
 *
 * @author md
 */
public class TargetNodeValidatorKitImpl implements TargetNodeValidatorKit {

	/** Validator for DATE nodes. */
	private TargetNodeValidator dateNodeValidator;

	/** Validator for NUMERIC nodes. */
	private TargetNodeValidator numericNodeValidator;

	/** Validator for STRING nodes. */
	private TargetNodeValidator stringNodeValidator;

	/** Validator for INTERVAL MAILING nodes. */
	private TargetNodeValidator intervalMailingNodeValidator;

	/** Validator for MAILING CLICKED nodes. */
	private TargetNodeValidator mailingClickedNodeValidator;

	/** Validator for MAILING OPENED nodes. */
	private TargetNodeValidator mailingOpenedNodeValidator;

	/** Validator for MAILING RECEIVED nodes. */
	private TargetNodeValidator mailingReceivedNodeValidator;

	/* (non-Javadoc)
	 * @see org.agnitas.target.impl.TargetNodeValidatorKit#getDateNodeValidator()
	 */
	@Override
	public TargetNodeValidator getDateNodeValidator() {
		return dateNodeValidator;
	}

	/**
	 * Set validator for DATE nodes.
	 *
	 * @param validator validator
	 */
	public void setDateNodeValidator(TargetNodeValidator validator) {
		this.dateNodeValidator = validator;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.target.impl.TargetNodeValidatorKit#getNumericNodeValidator()
	 */
	@Override
	public TargetNodeValidator getNumericNodeValidator() {
		return numericNodeValidator;
	}

	/**
	 * Set validator for NUMERIC nodes.
	 *
	 * @param validator validator
	 */
	public void setNumericNodeValidator(TargetNodeValidator validator) {
		this.numericNodeValidator = validator;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.target.impl.TargetNodeValidatorKit#getStringNodeValidator()
	 */
	@Override
	public TargetNodeValidator getStringNodeValidator() {
		return stringNodeValidator;
	}

	/**
	 * Set validator for STRING nodes.
	 *
	 * @param validator validator
	 */
	public void setStringNodeValidator(TargetNodeValidator validator) {
		this.stringNodeValidator = validator;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.target.impl.TargetNodeValidatorKit#getIntervalMailingNodeValidator()
	 */
	@Override
	public TargetNodeValidator getIntervalMailingNodeValidator() {
		return intervalMailingNodeValidator;
	}

	/**
	 * Set validator for INTERVAL MAILING nodes.
	 *
	 * @param validator validator
	 */
	public void setIntervalMailingNodeValidator( TargetNodeValidator validator) {
		this.intervalMailingNodeValidator = validator;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.target.impl.TargetNodeValidatorKit#getMailingClickedNodeValidator()
	 */
	@Override
	public TargetNodeValidator getMailingClickedNodeValidator() {
		return mailingClickedNodeValidator;
	}

	/**
	 * Set validator for MAILING CLICKED nodes.
	 *
	 * @param validator validator
	 */
	public void setMailingClickedNodeValidator( TargetNodeValidator validator) {
		this.mailingClickedNodeValidator = validator;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.target.impl.TargetNodeValidatorKit#getMailingOpenedNodeValidator()
	 */
	@Override
	public TargetNodeValidator getMailingOpenedNodeValidator() {
		return mailingOpenedNodeValidator;
	}

	/**
	 * Set validator for MAILING OPENED nodes.
	 *
	 * @param validator validator
	 */
	public void setMailingOpenedNodeValidator( TargetNodeValidator validator) {
		this.mailingOpenedNodeValidator = validator;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.target.impl.TargetNodeValidatorKit#getMailingReceivedNodeValidator()
	 */
	@Override
	public TargetNodeValidator getMailingReceivedNodeValidator() {
		return mailingReceivedNodeValidator;
	}

	/**
	 * Set validator for MAILING RECEIVED nodes.
	 *
	 * @param validator validator
	 */
	public void setMailingReceivedNodeValidator( TargetNodeValidator validator) {
		this.mailingReceivedNodeValidator = validator;
	}

}
