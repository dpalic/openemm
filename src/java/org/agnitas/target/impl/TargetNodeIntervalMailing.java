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

import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetOperator;
import org.apache.log4j.Logger;

/**
 * @author aso
 */
public class TargetNodeIntervalMailing extends TargetNode implements Serializable {
	private static final long serialVersionUID = -7645581813922614421L;

	private static final transient Logger logger = Logger.getLogger(TargetNodeIntervalMailing.class);
	
	public static String PSEUDO_COLUMN_NAME = "interval_mailing_pseudo_column_name";

	protected boolean openBracketBefore;
	protected boolean closeBracketAfter;
	protected int chainOperator;
	
	protected int companyID;
	protected boolean checkForPositiveReceiving;
	protected int mailingID;

	public TargetNodeIntervalMailing(int companyID, int chainOperator, int openBracketBefore, int operator, String mailingIdString, int closeBracketAfter) throws Exception {
		initializeOperatorLists();
		
		this.chainOperator = chainOperator;
		this.openBracketBefore = openBracketBefore > 0;
		this.closeBracketAfter = closeBracketAfter > 0;

		this.companyID = companyID;
		try {
			this.mailingID = Integer.parseInt(mailingIdString);
		} catch (Exception e) {
			throw new Exception("Invalid mailing id");
		}
		
		if (operator == OPERATOR_EQ.getOperatorCode()) {
			checkForPositiveReceiving = true;
		} else if (operator == OPERATOR_NEQ.getOperatorCode()) {
			checkForPositiveReceiving = false;
		} else {
			throw new Exception("Invalid operator found: " + operator);
		}
	}

	public static TargetOperator[] getValidOperators() {
		return new TargetOperator[] {
				OPERATOR_EQ,
				OPERATOR_NEQ,
		};
	}

	@Override
	protected void initializeOperatorLists() {
		typeOperators = TargetNodeIntervalMailing.getValidOperators();
	}

	@Override
	public String generateSQL() {
		StringBuffer tmpSQL = new StringBuffer("");

		switch (chainOperator) {
			case TargetNode.CHAIN_OPERATOR_AND:
				tmpSQL.append(" AND ");
				break;
			case TargetNode.CHAIN_OPERATOR_OR:
				tmpSQL.append(" OR ");
				break;
			default:
				tmpSQL.append(" ");
		}

		if (openBracketBefore) {
			tmpSQL.append("(");
		}

		tmpSQL.append("(" + (checkForPositiveReceiving ? "" : "NOT ") + "EXISTS (SELECT 1 FROM interval_track_" + companyID + "_tbl track WHERE track.mailing_id = " + mailingID + " AND track.customer_id = cust.customer_id))");

		if (closeBracketAfter) {
			tmpSQL.append(")");
		}

		return tmpSQL.toString();
	}

	@Override
	public String generateBsh() {
		return null;
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		try {
			ObjectInputStream.GetField allFields = in.readFields();
			chainOperator = allFields.get("chainOperator", TargetNode.CHAIN_OPERATOR_NONE);
			closeBracketAfter = allFields.get("closeBracketAfter", false);
			openBracketBefore = allFields.get("openBracketBefore", false);
			
			companyID = allFields.get("companyID", -1);
			checkForPositiveReceiving = allFields.get("checkForPositiveReceiving", true);
			mailingID = allFields.get("mailingID", -1);
		} catch (Exception e) {
			logger.error("readObject: " + e.getMessage());
		}
		initializeOperatorLists();
	}

	@Override
	public boolean isOpenBracketBefore() {
		return openBracketBefore;
	}

	@Override
	public void setOpenBracketBefore(boolean openBracketBefore) {
		this.openBracketBefore = openBracketBefore;
	}

	@Override
	public boolean isCloseBracketAfter() {
		return closeBracketAfter;
	}

	@Override
	public void setCloseBracketAfter(boolean closeBracketAfter) {
		this.closeBracketAfter = closeBracketAfter;
	}

	@Override
	public int getChainOperator() {
		return chainOperator;
	}
	
	@Override
	public void setChainOperator(int chainOperator) {
		this.chainOperator = chainOperator;
	}
	
	@Override
    public String getPrimaryFieldType() {
        return null;
    }
	
	@Override
    public void setPrimaryField(String primaryField) {
		// do nothing
    }
	
	@Override
    public void setPrimaryFieldType(String primaryField) {
		// do nothing
    }

	@Override
    public String getPrimaryValue() {
        return Integer.toString(mailingID);
    }

	@Override
	public int getPrimaryOperator() {
		return checkForPositiveReceiving ? OPERATOR_EQ.getOperatorCode() : OPERATOR_NEQ.getOperatorCode();
	}

	@Override
	public String getPrimaryField() {
		return TargetNodeIntervalMailing.PSEUDO_COLUMN_NAME;
	}

	@Override
	public void setPrimaryOperator(int primaryOperator) {
		// do nothing
	}

	@Override
	public void setPrimaryValue(String primaryValue) {
		// do nothing
	}
}
