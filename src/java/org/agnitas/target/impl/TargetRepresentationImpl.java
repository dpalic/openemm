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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.target.TargetError;
import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetNodeValidator;
import org.agnitas.target.TargetNodeValidatorKit;
import org.agnitas.target.TargetRepresentation;

/**
 * 
 * @author mhe
 */
public class TargetRepresentationImpl implements TargetRepresentation {
	
	protected List<TargetNode> allNodes = null;

	private static final long serialVersionUID = -5118626285211811379L;

	public TargetRepresentationImpl() {
		allNodes = new ArrayList<TargetNode>();
	}

	@Override
	public String generateSQL() {
		boolean isFirst = true;
		StringBuffer tmpString = new StringBuffer("");

		for (TargetNode tmpNode : allNodes) {
			if (isFirst) {
				tmpNode.setChainOperator(TargetNode.CHAIN_OPERATOR_NONE);
				isFirst = false;
			}
			tmpString.append(tmpNode.generateSQL());
		}
		if ("".equals(tmpString.toString())) {
			return tmpString.toString();
		}
		return "(  (" + tmpString.toString() + ")  )"; // TODO: (AGNEMM-787) After bugfixing, change this parenthesis construct to a simple and clean one (md)
	}

	@Override
	public String generateBsh() {
		StringBuffer tmpString = new StringBuffer("");
		TargetNode tmpNode = null;
		ListIterator<TargetNode> aIt = allNodes.listIterator();
		while (aIt.hasNext()) {
			tmpNode = aIt.next();
			tmpString.append(tmpNode.generateBsh());
		}
		return tmpString.toString();
	}

	@Override
	public boolean checkBracketBalance() {
		int balance = 0;
		for (TargetNode tmpNode : allNodes) {
			if (tmpNode.isOpenBracketBefore()) {
				balance++;
			}
			if (tmpNode.isCloseBracketAfter()) {
				balance--;
			}
			if (balance < 0) {
				return false;
			}
		}
		return balance == 0;
	}

	@Override
	public void addNode(TargetNode aNode) {
		if (aNode != null) {
			allNodes.add(aNode);
		}
	}

	@Override
	public void setNode(int idx, TargetNode aNode) {
		if (aNode != null) {
			allNodes.add(idx, aNode);
		}
	}

	@Override
	public boolean deleteNode(int index) {
		allNodes.remove(index);
		return true;
	}

	@Override
	public List<TargetNode> getAllNodes() {
		return allNodes;
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField allFields = in.readFields();
		allNodes = (List<TargetNode>) allFields.get("allNodes", new ArrayList<TargetNode>());
	}
	
	@Override
	public List<Collection<TargetError>> validate( TargetNodeValidatorKit validatorKit, @VelocityCheck int companyId) {
		List<Collection<TargetError>> resultList = new Vector<Collection<TargetError>>();
		
		for( TargetNode node : allNodes) {
			resultList.add( validateNode( node, validatorKit, companyId));
		}
		
		return resultList;
	}
	
	/**
	 * Validates a single target node.
	 * 
	 * @param node node to validate
	 * @param validatorKit kit for node validation
	 * 
	 * @return Collection containing all errors for the node
	 */
	private Collection<TargetError> validateNode( TargetNode node, TargetNodeValidatorKit validatorKit, @VelocityCheck int companyId) {
		if( validatorKit != null) {
			TargetNodeValidator validator = null;
			if( node instanceof TargetNodeDate)
				validator = validatorKit.getDateNodeValidator();
			else if( node instanceof TargetNodeNumeric)
				validator = validatorKit.getNumericNodeValidator();
			else if( node instanceof TargetNodeString)
				validator = validatorKit.getStringNodeValidator();
			else if( node instanceof TargetNodeIntervalMailing)
				validator = validatorKit.getIntervalMailingNodeValidator();
			else if( node instanceof TargetNodeMailingClicked)
				validator = validatorKit.getMailingClickedNodeValidator();
			else if( node instanceof TargetNodeMailingOpened)
				validator = validatorKit.getMailingOpenedNodeValidator();
			else if( node instanceof TargetNodeMailingReceived)
				validator = validatorKit.getMailingReceivedNodeValidator();
			
			if( validator != null)
				return validator.validate( node, companyId);
		}
		// No matching validator found, so report as "cannot validate"!
		Collection<TargetError> collection = new Vector<TargetError>();
		collection.add( new TargetError( TargetError.ErrorKey.CANNOT_VALIDATE));
		return collection;
	}
}
