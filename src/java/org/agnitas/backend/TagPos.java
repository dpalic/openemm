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

/** container class for start/end values of found tags in a parsed block
 */
class TagPos {
    /** its start position */
    public int start;
    /** its end position */
    public int end;
    /** the name of the tag */
    public String tagname;
    /** the tag with stripped of [ .. ] */
    public String tagid;
    /** if this is a simple tag */
    public boolean simpleTag;
    /** the content if this is dynamic */
    public BlockData content;

    /** extract the tagid from the tagname
     */
    private void checkTagname () {
        int	len = tagname.length ();

        if ((len > 2) && (tagname.lastIndexOf ("/]") == len - 2)) {
//			tagname = tagname.substring (0, len - 2) + ">";
//			--len;
            simpleTag = true;
        }
        
        int	n;
        
        for (n = 1; n < len - 1; ++n)
            if ((tagname.charAt (n) == ' ') || (tagname.charAt (n) == '/') || (tagname.charAt (n) == ']'))
                break;
        tagid = tagname.substring (1, n);
    }

    /** Constructor
     * @param start start position of tag
     * @param end end position of tag
     * @param tagname the full tagname
     */
    public TagPos(int start, int end, String tagname) {
        this.start=start;
        this.tagname = tagname;
        this.end=end;
        tagid = null;
        simpleTag = false;
        content = null;

        checkTagname ();
    }
    
    /** Checks if this is the agnDYN tag
     * @return true, it this is the case
     */
    public boolean isDynamic () {
        return tagid.equals (EMMTag.TAG_INTERNALS[EMMTag.TI_DYN]);
    }
    
    /** Checks if this is the agnDVALUE tag
     * @return true, it this is the case
     */
    public boolean isDynamicValue () {
        return tagid.equals (EMMTag.TAG_INTERNALS[EMMTag.TI_DYNVALUE]) || (isDynamic () && simpleTag);
    }

    /** Set the content for this tag
     * @param bd the block content
     */
    public void setContent (BlockData bd) {
        content = bd;
    }
}
