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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.agnitas.emm.core.velocity.VelocityCheck;

/**
 * @author mhe
 */
public interface TargetRepresentation extends Serializable {
	/**
	 * Adds one node.
	 */
	public void addNode(TargetNode aNode);

	/**
	 * Checks if all opened Brackes are closed.
	 */
	public boolean checkBracketBalance();

	/**
	 * Removes one node.
	 */
	public boolean deleteNode(int index);

	/**
	 * Generates bsh.
	 */
	public String generateBsh();

	/**
	 * Generates sql statement.
	 */
	public String generateSQL();

	/**
	 * Getter for allNodes.
	 * 
	 * @return Value of allNodes.
	 */
	public List<TargetNode> getAllNodes();

	/**
	 * Setter for property node.
	 * 
	 * @param aNode
	 *            New value of property node.
	 */
	public void setNode(int idx, TargetNode aNode);
	
	/**
	 * Validate all rules of the target group. Each element of the returned list contains all errors found for the corresponding rule.
	 * Rules with no errors are shown by {@code null} or empty collections.
	 * 
	 * @param validatorKit validator kit used for validation
	 * @param companyId ID of company 
	 * 
	 * @return List of error data
	 */
	public List<Collection<TargetError>> validate( TargetNodeValidatorKit validatorKit, @VelocityCheck int companyId);
	
}
