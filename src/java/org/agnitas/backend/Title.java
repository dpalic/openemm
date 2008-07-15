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
package org.agnitas.backend;

import	java.util.Vector;

/** Collection of titles
 */
class Title {
    /** the unique ID of this title */
    protected Long	id;
    /** The titles for each gender */
    private Vector	title;
    
    /* Constructor
     * @param nID new unique id
     */
    public Title (Long nID) {
        id = nID;
        title = new Vector ();
    }
    
    /** Set/Add a title for a gender
     * @param gender numeric representation for the gender
     * @param nTitle title for this gender
     */
    public void setTitle (int gender, String nTitle) {
        if (gender >= 0) {
            int	size = title.size ();

            if (size <= gender) {
                while (size < gender) {
                    title.add (size, null);
                    ++size;
                }
                title.add (gender, nTitle);
            } else {
                title.set (gender, nTitle);
            }
        }
    }
    
    /** Create the title string using customer related data
     * @param cinfo the customer information data
     * @return the title string
     */
    public String makeTitle (Custinfo cinfo, boolean full) {
        String	s = "";
        String	tstr;

        if ((cinfo.gender >= 0) && (cinfo.gender < title.size ()) &&
            ((tstr = (String) title.elementAt (cinfo.gender)) != null)) {
            String	name = null;
            
            if (cinfo.gender < 2) {
                if (full) {
                    if (cinfo.firstname != null) {
                        if (cinfo.lastname != null) {
                            name = cinfo.firstname + " " + cinfo.lastname;
                        }
                    } else if (cinfo.lastname != null) {
                        name = cinfo.lastname;
                    }
                } else if (cinfo.lastname != null) {
                    name = cinfo.lastname;
                }
            }
            if (name != null) {
                s = tstr + " " + name;
            } else {
                s = tstr;
            }
        }
        return s;
    }
}
