/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.dao;

import java.util.List;
import org.agnitas.target.Target;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author mhe
 */
public interface TargetDao extends ApplicationContextAware {
    boolean deleteTarget(int targetID, int companyID);
    /**
     * Getter for property target by target id and company id.
     *
     * @return Value of target.
     */
    Target getTarget(int targetID, int companyID);
    
    /**
     * Getter target by target name and company id.
     *
     * @return target.
     */
    Target getTargetByName(String targetName, int companyID);
    

    /**
     * Getter for property target by company id.
     *
     * @return Value of target.
     */
    List getTargets(int companyID);

    /**
     * Saves target.
     *
     * @return Saved target.
     */
    int saveTarget(Target target);
    
}
