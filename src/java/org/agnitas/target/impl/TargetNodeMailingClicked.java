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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetOperator;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;

/**
 * Target node to filter customer by "clicked in a specific mailing" / "did not click in
 * a specific mailing".
 *
 * @author md
 *
 */
public class TargetNodeMailingClicked extends TargetNode implements Serializable {

	/** Serial version UID. */
	private static final long serialVersionUID = -2323074235326317234L;

	/** The logger. */
	private static final transient Logger logger = Logger.getLogger( TargetNodeMailingClicked.class);

	/** Name of pseudo column. */
	public static final String PSEUDO_COLUMN_NAME = "pseudo_column_mailing_clicked";

	/** Flag, if opening parenthesis is set. */
	private boolean openBracketBefore;

	/** Flag, if closing parenthesis is set. */
	private boolean closeBracketAfter;

	/** Type of operator for chaining rules. */
	private int chainOperator;

	/** Type of primary operator. */
	private int primaryOperator;

	/** Primary value. */
	private String primaryValue;

	/** Company ID. */
	private int companyId;

	public TargetNodeMailingClicked( @VelocityCheck int companyId, int chainOperator, int openParenthesis, int primaryOperator, String primaryValue, int closeParenthesis) {
		this.companyId = companyId;
		this.chainOperator = chainOperator;
		this.openBracketBefore = openParenthesis != 0;
		this.primaryOperator = primaryOperator;
		this.primaryValue = primaryValue;
		this.closeBracketAfter = closeParenthesis != 0;
	}

	/**
	 * Returns a list of valid operators for this target node.
	 *
	 * @return array of valid operators
	 */
	public static TargetOperator[] getValidOperators() {
		return new TargetOperator[] {
				OPERATOR_YES,
				OPERATOR_NO
				};
	}

	@Override
	protected void initializeOperatorLists() {
		typeOperators = TargetNodeMailingClicked.getValidOperators();
	}

	@Override
	public boolean isOpenBracketBefore() {
		return this.openBracketBefore;
	}

	@Override
	public void setOpenBracketBefore(boolean openBracketBefore) {
		this.openBracketBefore = openBracketBefore;
	}

	@Override
	public boolean isCloseBracketAfter() {
		return this.closeBracketAfter;
	}

	@Override
	public void setCloseBracketAfter(boolean closeBracketAfter) {
		this.closeBracketAfter = closeBracketAfter;
	}

	@Override
	public int getChainOperator() {
		return this.chainOperator;
	}

	@Override
	public void setChainOperator(int chainOperator) {
		this.chainOperator = chainOperator;
	}

	@Override
	public String generateEmbeddedSQL() {
		StringBuffer buffer = new StringBuffer();

		// Get the mailing ID
		int mailingId = Integer.parseInt( this.primaryValue);

		// Negate result if primary operator says to
		if( primaryOperator == TargetNode.OPERATOR_NO.getOperatorCode())
			buffer.append( " NOT ");

		// Build the WHERE clause
		buffer.append( " EXISTS (SELECT 1 FROM ");

		if( AgnUtils.isProjectEMM()) {
			buffer.append( "rdirlog_");
			buffer.append( companyId);
			buffer.append( "_tbl");
		} else {
			buffer.append( "rdir_log_tbl");
		}

		buffer.append( " rl WHERE rl.customer_id=cust.customer_id AND rl.mailing_id=");
		buffer.append( mailingId);
		buffer.append( " AND rl.company_id=");
		buffer.append( companyId);
		buffer.append( ")");

		return buffer.toString();
	}

	@Override
	public String generateBsh() {
		try {
			throw new RuntimeException( "BSH generation is not supported!");
		} catch( RuntimeException e) {
			logger.error( "BSH generation failed", e);
			throw e;
		}
	}

	@Override
	public int getPrimaryOperator() {
		return this.primaryOperator;
	}

	@Override
	public void setPrimaryOperator(int primaryOperator) {
		this.primaryOperator = primaryOperator;
	}

	@Override
	public String getPrimaryField() {
		return TargetNodeMailingClicked.PSEUDO_COLUMN_NAME;
	}

	@Override
	public void setPrimaryField(String primaryField) {
		// Nothing to do here
	}

	@Override
	public String getPrimaryFieldType() {
		return null;
	}

	@Override
	public void setPrimaryFieldType(String primaryFieldType) {
		// Nothing to do here
	}

	@Override
	public String getPrimaryValue() {
		return this.primaryValue;
	}

	@Override
	public void setPrimaryValue(String primaryValue) {
		this.primaryValue = primaryValue;
	}

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        try {
            allFields=in.readFields();
            this.chainOperator=allFields.get("chainOperator", TargetNode.CHAIN_OPERATOR_NONE);
            this.primaryOperator=allFields.get("primaryOperator", TargetNode.OPERATOR_EQ.getOperatorCode());
            this.primaryValue=(String)allFields.get("primaryValue", "0");
            this.closeBracketAfter=allFields.get("closeBracketAfter", false);
            this.openBracketBefore=allFields.get("openBracketBefore", false);
        } catch (Exception e) {
            logger.error("Error deserializing " + this.getClass().getCanonicalName(), e);
        }
    	this.initializeOperatorLists();
    }

}
