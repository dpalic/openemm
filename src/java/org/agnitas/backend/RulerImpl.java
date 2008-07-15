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

import  java.util.Vector;
import  java.util.Hashtable;
import  java.util.Calendar;
import  java.util.GregorianCalendar;
import  java.sql.ResultSet;
import  java.sql.SQLException;
import  org.agnitas.util.Log;

/** Handle date controled mailings
 */
public class RulerImpl implements Ruler {
    /** Simple struct to keep track of status_id/mailing_id
     * mapping for delayed generation
     */
    private class Entry {
        long    statusID;
        long    mailingID;
        boolean active;

        /** Constructor
         * @param nStatusID the status ID
         * @param nMailingID the mailing ID
         */
        public Entry (long nStatusID, long nMailingID) {
            statusID = nStatusID;
            mailingID = nMailingID;
            active = false;
        }

        /** Getter for status id
         * @return the status ID
         */
        public long getStatusID () {
            return statusID;
        }

        /** Getter for status id as string
         * @return the status ID in string form
         */
        public String getStatusIDasString () {
            return Long.toString (statusID);
        }

        /** Getter for mailing id
         * @return the mailing ID
         */
        public long getMailingID () {
            return mailingID;
        }

        /** Setter for active
         * @param nActive new active flag
         */
        public void setActive (boolean nActive) {
            active = nActive;
        }

        /** Getter for active
         * @return current active state
         */
        public boolean getActive () {
            return active;
        }
    }

    /** Reference to configuration */
    private Data    data;
    /** The hour for which to send mailings */
    private int hour;

    /** Allocates new data structure
     * @return new instance
     */
    public Object mkData () throws Exception {
        return new Data ("ruler");
    }
    
    public Object mkMailgunImpl () {
        return new MailgunImpl ();
    }
    
    /** Constructor
     */
    public RulerImpl () throws Exception {
        super ();
        data = (Data) mkData ();
        hour = -1;
    }

    /** Cleanup
     */
    public void done () throws Exception {
        if (data != null) {
            data.done ();
            data = null;
        }
    }

    /** Check for database connection and try to reopen it
     */
    private void checkDatabaseConnection () throws Exception {
        try {
            ResultSet   rset = data.dbase.simpleQuery ("SELECT 1 FROM dual");

            rset.close ();
        } catch (Exception e) {
            done ();
            data = (Data) mkData ();
            if (data == null)
                throw new Exception ("Unable to resetup data");
        }
    }

    /** Setter for hour
     * @param nhour the new hour to send mailings for
     */
    public void setHour (int nhour) {
        hour = nhour;
    }

    /** Wrapper for kickOff to be used in Quartz scheduler
     */
    public void kickOffSimple () {
        try {
            kickOff ();
        } catch (Exception e) {
            data.logging (Log.ERROR, "rule", "Failed in kickOffSimple: " + e.getMessage ());
        }
    }

    /** Returns the query to get current date
     * @return query as string
     */
    public String getQueryNow () {
        return "SELECT date_format(" + data.dbase.sysdate + ", '%Y-%m-%d') FROM dual";
    }
    
    public String getQueryLastsent () {
        return "SELECT mailing_id, date_format(lastsent, '%Y-%m-%d') FROM rulebased_sent_tbl";
    }

    public String getFormatHour () {
        return "date_format(senddate, '%H')";
    }
    
    public String getQueryLastsent (Long mid) {
        return "SELECT date_format(lastsent, '%Y-%m-%d') FROM rulebased_sent_tbl WHERE mailing_id = " + mid;
    }


    /** Loop over all entries for today and start the
     * mailings, which are ready to run
     */
    public synchronized String kickOff () throws Exception {
        String  msg;

        msg = "Ruler:";

        String      now;
        Hashtable   sent;
        Vector      ids, mids;
        int     queryHour;
        String      query;
        ResultSet   rset;

        checkDatabaseConnection ();
        query = getQueryNow ();
        rset = data.dbase.execQuery (query);
        if (rset.next ())
            now = rset.getString (1);
        else
            now = null;
        rset.close ();
        if (now == null)
            throw new Exception ("Unable to get current date from database");
        sent = new Hashtable ();
        query = getQueryLastsent ();
        rset = data.dbase.execQuery (query);
        while (rset.next ()) {
            Long    mailing_id = new Long (rset.getLong (1));
            String  lastsent = rset.getString (2);

            sent.put (mailing_id, lastsent);
        }
        rset.close ();
        ids = new Vector ();
        mids = new Vector ();
        query = "SELECT status_id, mailing_id FROM maildrop_status_tbl WHERE status_field = 'R' AND genstatus = 1 AND ";
        if ((hour >= 0) && (hour <= 24)) {
            queryHour = hour;
        } else {
            queryHour = (new GregorianCalendar ()).get (Calendar.HOUR_OF_DAY);
        }

        query += getFormatHour () + " = '" + StringOps.format_number (queryHour, 2) + "'";
        rset = data.dbase.execQuery (query);
        while (rset.next ()) {
            Long    id = new Long (rset.getLong (1));
            Long    mid = new Long (rset.getLong (2));

            if ((! sent.containsKey (mid)) || (! sent.get (mid).equals (now))) {
                ids.addElement (new Long (rset.getLong (1)));
                mids.addElement (new Long (rset.getLong (2)));
            } else
                data.logging (Log.WARNING, "rule", "Mailing ID " + mid + " already sent today");
        }
        rset.close ();
        data.logging (Log.INFO, "rule", "Read " + ids.size () + " maildrop entr" + Log.exty (ids.size ()));
        for (int n = 0; n < ids.size (); ++n) {
            Long    id = (Long) ids.elementAt (n);
            Long    mid = (Long) mids.elementAt (n);
            boolean valid = false;

            try {
                long    del;

                rset = data.dbase.execQuery ("SELECT deleted FROM mailing_tbl WHERE mailing_id = " + mid.toString ());
                if (rset.next ()) {
                    del = rset.getLong (1);
                    if (del == 0)
                        valid = true;
                    else
                        data.logging (Log.WARNING, "rule", "Skipping deleted mailing " + mid);
                } else
                    data.logging (Log.WARNING, "rule", "Entry without mailing found " + mid);
                rset.close ();
                if (valid) {
                    query = getQueryLastsent (mid);
                    rset = data.dbase.execQuery (query);
                    if (rset.next ()) {
                        String  lastsent = rset.getString (1);

                        if (lastsent.equals (now)) {
                            data.logging (Log.WARNING, "rule", "Rule based mailing " + mid + " already sent!");
                            valid = false;
                        }
                    }
                    rset.close ();
                }
            } catch (SQLException e) {
                data.logging (Log.WARNING, "rule", "Unable to get entry from mailing_tbl for " + mid + ": " + e);
            }
            if (valid) {
                valid = false;
                if (sent.containsKey (mid))
                    query = "UPDATE rulebased_sent_tbl SET lastsent = " + data.dbase.sysdate + " WHERE mailing_id = " + mid.toString ();
                else
                    query = "INSERT INTO rulebased_sent_tbl (mailing_id, lastsent) VALUES (" + mid.toString () + ", " + data.dbase.sysdate + ")";
                try {
                    data.dbase.execUpdate (query);
                    valid = true;
                } catch (SQLException e) {
                    data.logging (Log.ERROR, "rule", "Unable to update lastsent using: " + query + " (" + e.toString () + ")");
                }
                if (valid) {
                    if (n > 0)
                        Thread.sleep (2 * 1000);
                    data.logging (Log.DEBUG, "rule", "Execute maildrop_status_id " + id);
                    try {
                        MailgunImpl mg = (MailgunImpl) mkMailgunImpl ();
                        String      mmsg;

                        mg.initializeMailgun (id.toString (), null);
                        mmsg = mg.fire (null);
                        msg += "\n" + id + ": " + (mmsg == null ? "*unset*" : mmsg);
                        data.logging (Log.DEBUG, "rule", "Mailgun returns " + (mmsg == null ? "nothing" : mmsg));
                    } catch (Exception e) {
                        msg += "\n" + id + ": [Exception] " + e.toString ();
                        data.logging (Log.DEBUG, "rule", "Mailgun fails with " + e.toString ());
                    }
                }
            }
        }
        msg += "done.";
        return msg;
    }

    /** Start delayed mail generation
     */
    public synchronized void kickOffDelayed () {
        String  query = null;

        try {
            Vector      mids;
            ResultSet   rset;

            checkDatabaseConnection ();
            query = "SELECT status_id, mailing_id FROM maildrop_status_tbl " +
                "WHERE genstatus = 0 AND status_field = 'W' AND gendate < now() ORDER BY gendate";
            mids = new Vector ();
            rset = data.dbase.execQuery (query);
            while (rset.next ()) {
                mids.add (new Entry (rset.getLong (1), rset.getLong (2)));
            }
            rset.close ();

            int entries = mids.size ();
            if (entries > 0) {
                data.logging (Log.INFO, "rule", "Found " + entries + " delayed mailing" + Log.exts (entries));
                for (int n = 0; n < entries; ++n) {
                    Entry   e = (Entry) mids.elementAt (n);

                    query = "SELECT deleted FROM mailing_tbl WHERE mailing_id = " + e.getMailingID ();
                    rset = data.dbase.execQuery (query);
                    if (rset.next ()) {
                        int deleted = rset.getInt (1);

                        if (deleted == 0) {
                            e.setActive (true);
                        } else {
                            data.logging (Log.WARNING, "rule", "Deleted mailing " + e.getMailingID () + " found");
                        }
                    } else {
                        data.logging (Log.WARNING, "rule", "Mailing " + e.getMailingID () + " has no entry in mailing_tbl");
                    }
                    rset.close ();
                    if (e.getActive ()) {
                        try {
                            MailgunImpl mg = (MailgunImpl) mkMailgunImpl ();
                            String      msg;

                            query = "UPDATE maildrop_status_tbl SET genstatus = 1, genchange = now() WHERE status_id = " + e.getStatusID () + " AND genstatus = 0";
                            data.dbase.execUpdate (query);
                            mg.initializeMailgun (e.getStatusIDasString (), null);
                            msg = mg.fire (null);
                            data.logging (Log.DEBUG, "rule", "Mailgun returns " + (msg == null ? "*nothing*" : msg));
                        } catch (Exception ec) {
                            data.logging (Log.DEBUG, "rule", "Mailgun fails with " + ec.toString ());
                        }
                    } else {
                        query = "UPDATE maildrop_status_tbl SET genstatus = 4, genchange = now() WHERE status_id = " + e.getStatusID () + " AND genstatus < 3";
                        data.dbase.execUpdate (query);
                    }
                }
            }
        } catch (Exception e) {
            data.logging (Log.ERROR, "rule", "Failed in delayedKickOff on query \"" + query + "\": " + e.toString ());
        }
    }
}
