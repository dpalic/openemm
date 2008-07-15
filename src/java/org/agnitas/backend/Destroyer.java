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

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

import org.agnitas.util.Log;

/** this class is used to remove pending mailings
 */
public class Destroyer {
    /** Class to filter filenames for deletion
     */
    private class DestroyFilter implements FilenameFilter {
        /** the mailing ID */
        private long    mailingID;

        /** Constructor
         * @param mailing_id the mailing ID to filter files for
         */
        public DestroyFilter (long mailing_id) {
            super ();
            mailingID = mailing_id;
        }

        /** If a file matches the filter
         * @param dir home directory of the file
         * @param name name of the file
         * @return true, if it should be deleted
         */
        public boolean accept (File dir, String name) {
            boolean     st;
            StringTokenizer tok;

            st = false;
            tok = new StringTokenizer (name, "=");
            if (tok.countTokens () == 6) {
                int n;
                long    mid;

                for (n = 0; n < 3; ++n) {
                    tok.nextToken ();
                }
                mid = Long.decode (tok.nextToken ()).longValue ();
                if (mid == mailingID) {
                    st = true;
                }
            }
            return st;
        }
    }

    /** The mailing ID */
    private long    mailingID;
    /** Reference to configuration */
    private Data    data;

    public void setData (Object nData) {
        data = (Data) nData;
    }
    
    public void mkData () throws Exception {
        setData (new Data ("destroyer"));
    }

    /** Constructor
     * @param mailing_id the mailing ID for the mailing to destroy
     */
    public Destroyer (long mailing_id) throws Exception {
        if (mailing_id <= 0) {
            throw new Exception ("Mailing_id is less or equal 0");
        }
        mailingID = mailing_id;
        mkData ();
    }

    /** Cleanup
     */
    public void done () throws Exception {
        data.done ();
    }

    /** Remove file(s) found in directory
     * @param path the directory to search for
     * @return number of files deleted
     */
    private int doDestroy (String path) throws Exception {
        File    file;
        File    files[];    
        int n;

        file = new File (path);
        files = file.listFiles (new DestroyFilter (mailingID));
        for (n = 0; n < files.length; ++n) {
            if (! files[n].delete ()) {
                data.logging (Log.ERROR, "destroy", "File " + files[n] + " cannot be removed");
            }
        }
        return files.length;
    }

    /** Start destruction
     * @return message string
     */
    public String destroy () throws Exception {
        String  msg;
        String  path;

        msg = "Destroy:";
        path = data.metaDir ();
        msg += " [" + path;
        try {
            msg += " " + doDestroy (path);
            msg += " done";
        } catch (Exception e) {
            msg += " failed: " + e;
        }
        msg += "]";
        return msg;
    }

}
