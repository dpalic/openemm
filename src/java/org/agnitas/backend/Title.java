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

import java.util.Vector;

/** Collection of titles
 */
class Title {
    /** the unique ID of this title */
    protected Long  id;
    /** The titles for each gender */
    private Vector  title;

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
            if ((nTitle != null) && nTitle.endsWith (" ")) {
                nTitle = nTitle.substring (0, nTitle.length () - 1);
            }

            int size = title.size ();

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
    
    /** Check for valid input strings
     * @param s the input string
     * @return true if string is not empty, false otherwise
     */
    private boolean isValid (String s) {
        return (s != null) && (s.length () > 0);
    }

    /** Create the title string using customer related data
     * @param cinfo the customer information data
     * @return the title string
     */
    public String makeTitle (Custinfo cinfo, boolean full) {
        String  s = "";
        int gender;
        String  tstr;
        String  name = null;

        if ((cinfo.gender < 0) || (cinfo.gender >= title.size ())) {
            gender = 2;
        } else {
            gender = cinfo.gender;
        }
        if (gender < title.size ()) {
            tstr = (String) title.elementAt (gender);
        } else {
            tstr = null;
        }
        if ((tstr == null) && (gender != 2) && (2 < title.size ())) {
            gender = 2;
            tstr = (String) title.elementAt (gender);
        }
        if (gender < 2) {
            String  custtitle = "";

            if (isValid (cinfo.title)) {
                custtitle = cinfo.title + " ";
            }
            if (full) {
                if (isValid (cinfo.firstname)) {
                    if (isValid (cinfo.lastname)) {
                        name = cinfo.firstname + " " + cinfo.lastname;
                    }
                } else if (isValid (cinfo.lastname)) {
                    name = cinfo.lastname;
                }
                if (name != null) {
                    name = custtitle + name;
                }
            } else if (isValid (cinfo.lastname)) {
                name = custtitle + cinfo.lastname;
            }
            if (name == null) {
                gender = 2;
                if (gender < title.size ()) {
                    tstr = (String) title.elementAt (gender);
                } else {
                    tstr = null;
                }
            }
        }
        if (tstr != null) {
            if (name != null) {
                s = tstr + " " + name;
            } else {
                s = tstr;
            }
        }
        return s;
    }
/*
    public static void p (String s) {
        System.out.println (">>" + s + "<<");
    }
    public static void main (String args[]) {
        Title       t = new Title (new Long (7));
        Custinfo    cinfo = new Custinfo ();

        cinfo.setFirstname ("First");
        cinfo.setLastname ("Last");
        cinfo.setTitle ("Prof.");
        for (int n=0;n<3;++n){cinfo.setGender(n);p("MK: '" + t.makeTitle (cinfo, false) + "', full: '" + t.makeTitle (cinfo, true) + "'");}
        for (int n=0;n<t.title.size();++n)p("" + n + ": " + (String) t.title.elementAt (n));p("");
        t.setTitle (1, "Sehr geehrte Frau");
        for (int n=0;n<3;++n){cinfo.setGender(n);p("MK: '" + t.makeTitle (cinfo, false) + "', full: '" + t.makeTitle (cinfo, true) + "'");}
        for (int n=0;n<t.title.size();++n)p("" + n + ": " + (String) t.title.elementAt (n));p("");
        t.setTitle (0, "Sehr geehrter Herr");
        for (int n=0;n<3;++n){cinfo.setGender(n);p("MK: '" + t.makeTitle (cinfo, false) + "', full: '" + t.makeTitle (cinfo, true) + "'");}
        for (int n=0;n<t.title.size();++n)p("" + n + ": " + (String) t.title.elementAt (n));p("");
        t.setTitle (2, "Liebe Gemeinde");
        for (int n=0;n<3;++n){cinfo.setGender(n);p("MK: '" + t.makeTitle (cinfo, false) + "', full: '" + t.makeTitle (cinfo, true) + "'");}
        for (int n=0;n<t.title.size();++n)p("" + n + ": " + (String) t.title.elementAt (n));p("");
    }
/**/
}
