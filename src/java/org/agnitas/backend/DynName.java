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
package	org.agnitas.backend;

import	java.util.Vector;

/**
 * Represents a name for a dynamic content
 */
class DynName {
    /** the unqiue name for this entry */
    protected String	name;
    /** the unique ID */
    protected long		id;
    /** all content with the same name */
    protected Vector	content;
    /** number of entries in content */
    protected int		clen;
    
    /**
     * The Constructor
     * @param nName the name
     * @param nId the ID
     */
    protected DynName (String nName, long nId) {
        name = nName;
        id = nId;
        content = new Vector ();
        clen = 0;
    }
    
    /**
     * Add a dynamic block for this name
     * @param cont the content to add
     */
    protected void add (DynCont cont) {
        int	n;
        
        for (n = 0; n < clen; ++n) {
            DynCont	tmp = (DynCont) content.elementAt (n);
            
            if (tmp.order > cont.order)
                break;
        }
        content.add (n, cont);
        ++clen;
    }
}