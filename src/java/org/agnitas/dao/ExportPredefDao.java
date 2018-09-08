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

import java.util.List;

import org.agnitas.beans.ExportPredef;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author ar
 */
public interface ExportPredefDao extends ApplicationContextAware {

    /**
     * Loads an ExportPredef identified by definition id and company id.
     *
     * @param id
     *          The id of the definition that should be loaded.
     * @param companyID
     *          The companyID for the definition.
     * @return The ExportPredef for given definition id and company id
     *  null if company id == 0
     *  new instance of ExportPredef if id == 0.
     */

    ExportPredef get(int id, @VelocityCheck int companyID);
    
    /**
     * Creates an ExportPredef.
     *
     * @param companyID
     *          The companyID for the definition.
     * @return A new ExportPredef for given company id
     *  null if company id == 0
     */
    ExportPredef create(@VelocityCheck int companyID);

    /**
     * Updates or create export definition.
     *
     * @param src
     *          The ExportPredef to be saved.
     * @return ID of updated definition
     * 0 if saving failed.
     */
    int save(ExportPredef src);

    /**
     * Deletes an export definition.
     *
     * @param src
     *          The export definition to delete.
     * @return true on success.
     */
    boolean delete(ExportPredef src);

    /**
     * Deletes an export definition by ID and company id
     *
     * @param id
     *          The ID of export definition to delete.
     * @param companyID
     *          The companyID of the definition.
     * @return true on success.
     */
    boolean delete(int id, @VelocityCheck int companyID);

    /**
     * Loads all export definitions of certain company.
     *
     * @param companyId
     *                The id of the company for export definitions.
     * @return  List of ExportPredef or empty list.
     */
    public List<ExportPredef> getAllByCompany( @VelocityCheck int companyId);

}
