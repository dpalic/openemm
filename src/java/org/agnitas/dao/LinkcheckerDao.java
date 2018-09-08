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
package org.agnitas.dao;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContextAware;

public interface LinkcheckerDao extends ApplicationContextAware {

	/**
	 * This method returns the timeout for ONE Link, not all together.
	 * @return timeout in mls.
	 */
	public int getLinkTimeout();

	/**
	 * returns the amount of parallel threads which are started to check
	 * if a link is valid. If the value is 50, then up to 50 links are parallel checked.
	 * @return amount of parallel threads.
	 */
	public int getThreadCount();

    /**
     * Getter for property dataSource.
     *
     * @return Value of property dataSource.
     */
	public DataSource getDataSource();

    /**
     * Setter for property dataSource.
     *
     * @param dataSource
     *          New value of property dataSource.
     */
	public void setDataSource(DataSource dataSource);
}
