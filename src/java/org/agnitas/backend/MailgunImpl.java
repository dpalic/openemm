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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.backend;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import org.agnitas.util.Blackdata;
import org.agnitas.util.Blacklist;
import org.agnitas.util.Log;

/** Central control class for generating mails
 */
public class MailgunImpl implements Mailgun {
    /** Reference to configuration */
    private Data        data;
    /** All content blocks */
    private BlockCollection allBlocks = null;
    /** All tags for this mailing */
    private Hashtable <String, EMMTag>
                tagNames = null;
    /** Creator for all URLs */
    private URLMaker    urlMaker = null;
    /** The blacklist information for this mailing */
    public Blacklist    blist = null;
    /** Query for normal selection */
    private String      selectQuery = null;
    /** Query for the world part selection */
    private String      wSelectQuery = null;

    /** Constructor
     * must be followed by initializeMailung ()
     */
    public MailgunImpl () {
        data = null;
    }

    /** Setter for data
     * @param nData new data instance
     */
    public void setData (Data nData) {
        data = nData;
    }

    /** Allocate new data instance
     * @param status_id the string version of the statusID to use
     * @param conn optional open database connection
     */
    public void mkData (String status_id, Connection conn) throws Exception {
        setData (new Data ("mailgun", status_id, "meta:xml/gz", conn));
    }

    /**
     * Initialize internal data
     * @param status_id the string version of the statusID to use
     * @param conn optional open database connection
     */
    public void initializeMailgun (String status_id, Connection conn) throws Exception {
        data = null;
        try {
            mkData (status_id, conn);
        } catch (Exception e) {
            done ();
            throw new Exception ("Error reading ini file: " + e, e);
        }
        data.suspend (conn);
    }

    /** Setup a mailgun without starting generation
     * @param conn optional open database connection
     * @param opts options to control the setup beyond DB information
     */
    public void prepareMailgun (Connection conn, Hashtable <String, Object> opts) throws Exception {
        try {
            doPrepare (conn, opts);
        } catch (Exception e) {
            if (data != null) {
                data.suspend (conn);
            }
            throw e;
        }
    }

    /** Execute an already setup mailgun
     * @param conn optional open database connection
     * @param opts options to control the execution beyond DB information
     */
    public synchronized void
    executeMailgun (Connection conn, Hashtable <String, Object> opts) throws Exception {
        try {
            doExecute (conn, opts);
        } catch (Exception e) {
            data.suspend (conn);
            throw e;
        }
    }

    /** retreive the current mailing id
     * @return the mailing ID
     */
    public long mailingID () {
        return data.mailing_id;
    }

    /** Cleanup
     */
    public void done () throws Exception {
        if (data != null) {
            try {
                data.done ();
            } catch (Exception e) {
                data = null;
                throw new Exception ("Failed in cleanup: " + e);
            }
            data = null;
        }
    }
    public void finalize () {
        try {
            done ();
        } catch (Exception e) {
            ;
        }
    }

    /** Retreive blacklist from database
     */
    public void retreiveBlacklist () throws Exception {
        ResultSet   rset = data.dbase.execQuery ("SELECT company_id, email FROM cust_ban_tbl WHERE company_id = 0 OR company_id = " + data.company_id);

        while (rset.next ()) {
            int cid = rset.getInt (1);
            String  email = rset.getString (2);

            if (email != null) {
                blist.add (email, cid == 0);
            }
        }
        rset.close ();
    }

    /** Read in the global and local blacklist
     */
    private void readBlacklist () throws Exception {
        blist = new Blacklist ();
        if (! data.isPreviewMailing ()) {
            try {
                retreiveBlacklist ();
            } catch (Exception e) {
                data.logging (Log.FATAL, "readblist", "Unable to get blacklist: " + e.toString ());
                throw new Exception ("Unable to get blacklist: " + e.toString ());
            }

            data.logging (Log.INFO, "readblist", "Found " + blist.globalCount () + " entr" + Log.exty (blist.globalCount ()) + " in global blacklist, " +
                                          blist.localCount () + " entr" + Log.exty (blist.localCount ()) + " in local blacklist");
        }
    }

    /** Check the sample email receiver if they should receive
     * the sample for this mailing
     * @param inp the expression to validate
     * @return null, if no mail should be sent, otherwise the email address
     */
    private String validateSampleEmail (String inp) {
        String  email;
        int n = inp.indexOf (':');

        if (n != -1) {
            String  minsub;

            minsub = inp.substring (0, n);
            try {
                long    minsubscriber = Long.parseLong (minsub);

                if (minsubscriber < data.totalSubscribers) {
                    email = inp.substring (n + 1);
                } else {
                    email = null;
                }
            } catch (NumberFormatException e) {
                email = null;
            }
        } else {
            email = inp;
        }
        return email;
    }

    public Object mkBlockCollection () throws Exception {
        return new BlockCollection ();
    }

    public Object mkURLMaker () throws Exception {
        return new URLMaker (data);
    }

    /** Prepare the mailgun
     * @param conn optional open database connection
     * @param opts options to control the setup beyond DB information
     */
    private void doPrepare (Connection conn, Hashtable <String, Object> opts) throws Exception {
        data.resume (conn);
        data.options (opts, 1);

        data.logging (Log.DEBUG, "prepare", "Starting firing");
        // create new Block collection and store in member var
        allBlocks = (BlockCollection) mkBlockCollection ();
        allBlocks.setupBlockCollection (data, data.previewInput);

        data.logging (Log.DEBUG, "prepare", "Parse blocks");
        // read all tag names contained in the blocks into Hashtable
        // - read selectvalues and store in EMMTag associated with tag name in Hashtable
        tagNames = allBlocks.parseBlocks();
        data.setUsedFieldsInLayout (allBlocks.conditionFields, tagNames);

        // add default tags to Hastable
        try{
            String[]    preset = {
                "agnMAILTYPE",
                "agnONEPIXEL"
            };
            for (int n = 0; n < preset.length; ++n) {
                boolean useit;

                switch (n) {
                default:
                    useit = true;
                    break;
                case 1:
                    useit = data.onepixlog != Data.OPL_NONE;
                    break;
                }
                if (useit) {
                    String  tn = "[" + preset[n] + "]";

                    if (! tagNames.containsKey (tn))
                        tagNames.put (tn, (EMMTag) allBlocks.mkEMMTag (tn, false));
                }
            }
        } catch (Exception e){
            throw new Exception("Error adding default tags: " + e);
        }
        allBlocks.replace_fixed_tags (tagNames);

        // prepare special url string maker
        urlMaker = (URLMaker) mkURLMaker ();
        readBlacklist ();

        data.suspend (conn);
    }

    /** Prepare collection of customers
     * @return a hashset for already seen customers
     */
    public HashSet <Long> prepareCollection () throws Exception {
        return new HashSet <Long> ((int) data.totalSubscribers + 1);
    }

    /** Get new instance for index collection
     * @return new instance
     */
    public Object mkIndices () {
        return new Indices ();
    }

    public Object mkCustinfo () {
        return new Custinfo ();
    }

    public Object mkMailWriterMeta (Object nData, Object allBlocks, Hashtable tagNames) throws Exception {
        return new MailWriterMeta ((Data) nData, (BlockCollection) allBlocks, tagNames);
    }

    /** Return used mediatypes (currently only email)
     * @param cid the customerID to get types for
     * @return mediatypes
     */
    public String getMediaTypes (long cid) {
        return "email";
    }

    /** Write final data to database
     */
    public void finalizeMailingToDatabase (MailWriter mailer) throws Exception {
        data.toMailtrack ();
    }

    /** Execute a prepared mailgun
     * @param conn optional open database connection
     * @param opts options to control the execution beyond DB information
     */
    private void doExecute (Connection conn, Hashtable <String, Object> opts) throws Exception {
        data.resume (conn);
        data.options (opts, 2);
        data.sanityCheck ();
        data.pass++;

        // get constructed selectvalue based on tag names in Hashtable
        data.startExecution ();
        selectQuery = getSelectvalue (tagNames, false);
        wSelectQuery = getSelectvalue (tagNames, true);

        MailWriter  mailer = (MailWriter) mkMailWriterMeta (data, allBlocks, tagNames);

        int columnCount = 0;
        Vector <EMMTag>
            email_tags = new Vector <EMMTag> ();
        int email_count = 0;
        boolean hasOverwriteData = false;
        boolean hasVirtualData = false;

        for (Enumeration e = tagNames.elements (); e.hasMoreElements (); ) {
            EMMTag  tag = (EMMTag) e.nextElement ();

            if ((! tag.globalValue) && (! tag.fixedValue) && (! tag.mutableValue)) {
                if (tag.tagType == EMMTag.TAG_DBASE) {
                    ++columnCount;
                } else if ((tag.tagType == EMMTag.TAG_INTERNAL) && (tag.tagSpec == EMMTag.TI_EMAIL)) {
                    email_tags.add (tag);
                    email_count++;
                } else if ((tag.tagType == EMMTag.TAG_INTERNAL) && (tag.tagSpec == EMMTag.TI_DBV)) {
                    hasVirtualData = true;
                    data.initializeVirtualData (tag.mSelectString);
                } else if (tag.tagType == EMMTag.TAG_CUSTOM) {
                    if ((data.customMap != null) && data.customMap.containsKey (tag.mTagFullname))
                        tag.mTagValue = data.customMap.get (tag.mTagFullname);
                    else
                        tag.mTagValue = null;
                }
            }
        }

        if (data.lusecount > 0)
            columnCount += data.lusecount;
        hasOverwriteData = data.overwriteData ();

        try {
            data.logging (Log.INFO, "execute", "Start creation of mails");

            boolean     needSamples = data.isWorldMailing () && (data.sampleEmails () != null) && ((data.availableMedias & (1 << Media.TYPE_EMAIL)) != 0);
            Vector      clist = data.generationClauses ();
            HashSet <Long>  seen = prepareCollection ();

            data.prefillRecipients (seen);
            for (int state = 0; state < clist.size (); ++state) {
                String  clause = (String) clist.get (state);
                if (clause == null)
                    continue;

                String  query = (state == 0 ? selectQuery : wSelectQuery) + " " + clause;

                if ((mailer.blockSize > 0) && (mailer.inBlockCount > 0))
                    mailer.checkBlock (true);
                // main SQL query: Returns all customers of this mailinglist
                ResultSet
                            rset = data.dbase.execQuery (query);
                ResultSetMetaData   meta = rset.getMetaData ();
                int         metacount = meta.getColumnCount ();
                Column[]        rmap = new Column[metacount];
                Indices         indices = (Indices) mkIndices ();
                Custinfo        cinfo = (Custinfo) mkCustinfo ();
                EMMTag          mailtype_tag = tagNames.get ("[agnMAILTYPE]");
                boolean         running = true;
                int         failcount = 0;

                cinfo.setup (data);
                for (int n = 0; n < metacount; ++n) {
                    String  cname = meta.getColumnName (n + 1);
                    int ctype = meta.getColumnType (n + 1);

                    if (Column.typeStr (ctype) != null) {
                        rmap[n] = new Column (cname, ctype);
                        cname = cname.toLowerCase ();
                        indices.checkIndex (cname, n);
                    } else
                        rmap[n] = null;
                }

                while (running) {
                    try {
                        running = rset.next ();
                    } catch (SQLException e) {
                        if (++failcount > 2) {
                            throw e;
                        }
                        data.logging (Log.ERROR, "mailgun", "Hit Exception " + e.toString () + " (" + failcount + ") for query " + query + ", try to resume");
                        try {
                            rset.close ();
                        } catch (SQLException e2) {
                            ;
                        }
                        data.dbase.resetDefaultStatement ();
                        rset = data.dbase.execQuery (query);
                        continue;
                    }
                    if (! running) {
                        continue;
                    }

                    long    cid = rset.getLong (1);
                    Long    ocid = new Long (cid);

                    if (seen.contains (ocid))
                        continue;
                    seen.add (ocid);
                    if (hasVirtualData && (! data.useRecord (ocid)))
                        continue;

                    String  userType = rset.getString (2);

                    int offset = 1;
                    int count;

                    for (count = 0; count < 2; ++count) {
                        rmap[count].set (rset, count + offset);
                    }

                    EMMTag tmp_tag=null;

                    // get values from this recordset
                    // store in Emmtag inside Hashtable
                    // the tags are in the correct order
                    //
                    for ( Enumeration e = tagNames.elements(); e.hasMoreElements(); ) {
                        tmp_tag = (EMMTag) e.nextElement();
                        if ((! tmp_tag.globalValue) && (! tmp_tag.fixedValue) && (! tmp_tag.mutableValue) && (tmp_tag.tagType == EMMTag.TAG_DBASE)) {
                            tmp_tag.mTagValue = null;
                            if (rmap[count] != null) {
                                rmap[count].set (rset, count + offset);
                                if (! rmap[count].isNull ()) {
                                    tmp_tag.mTagValue = rmap[count].get ();
                                }
                            }
                            ++count;
                        }
                    } // end for

                    for (int n = count; n < metacount; ++n)
                        if (rmap[n] != null)
                            rmap[n].set (rset, n + offset);

                    if (data.lusecount > 0) {
                        int m;

                        m = 0;
                        for (int n = 0; n < data.lcount; ++n)
                            if (data.columnUse (n))
                                data.columnSet (n, rset, count + offset + m++);
                    }

                    if (hasOverwriteData || hasVirtualData) {
                        for (Enumeration e = tagNames.elements(); e.hasMoreElements();) {
                            tmp_tag = (EMMTag) e.nextElement();

                            if (hasOverwriteData && (tmp_tag.tagType == EMMTag.TAG_INTERNAL) && (tmp_tag.tagSpec == EMMTag.TI_DB)) {
                                tmp_tag.dbOverwrite = data.overwriteData (ocid, tmp_tag.mSelectString.toUpperCase ());
                            } else if (hasVirtualData && (tmp_tag.tagType == EMMTag.TAG_INTERNAL) && (tmp_tag.tagSpec == EMMTag.TI_DBV)) {
                                tmp_tag.dbOverwrite = data.virtualData (ocid, tmp_tag.mSelectString.toUpperCase ());
                            }
                        }
                    }

                    String mailtype = (mailtype_tag != null ? mailtype_tag.mTagValue : null);
                    int mtype;

                    if (data.isPreviewMailing ()) {
                        mailtype = "1";
                        mtype = 1;
                    } else {
                        if (mailtype == null) {
                            data.logging (Log.WARNING, "mailgun", "Unset mailtype for customer_id " + cid + ", skipping");
                            continue;
                        }
                        mtype = Integer.parseInt (mailtype);
                        if (mtype > data.masterMailtype)
                            mtype = data.masterMailtype;
                    }

                    cinfo.clear ();
                    cinfo.setCustomerID (cid);
                    cinfo.setUserType (userType);
                    cinfo.setFromDatabase (rmap, indices);

                    for (int n = 0; n < email_count; ++n) {
                        EMMTag  etag = email_tags.elementAt (n);

                        etag.mTagValue = cinfo.email;
                    }

                    if (! data.isPreviewMailing ()) {
                        boolean     isblisted = false;

                        for (int blstate = 0; blstate < cinfo.checkForBlacklist; ++blstate) {
                            String  check = cinfo.blacklistValue (blstate);
                            String  what = cinfo.blacklistName (blstate);

                            if (check == null)
                                continue;

                            Blackdata   bl = blist.isBlackListed (check);
                            if (bl != null) {
                                String  where;

                                if (bl.isGlobal ()) {
                                    where = "global";
                                } else {
                                    where = "local";
                                }
                                data.logging (Log.WARNING, "mailgun", "Found " + what + ": " + check + " (" + cid + ") in " + where + " blacklist, ignored");
                                blist.writeBounce (data.mailing_id, cid);
                                isblisted = true;
                            }
                        }
                        if (isblisted)
                            continue;
                    }

                    String  mediatypes = getMediaTypes (cid);
                    if (mediatypes == null)
                        continue;

                    urlMaker.setCustomerID (cid);
                    mailer.writeMail (cinfo, 0, mtype, cid, mediatypes, tagNames, urlMaker);

                    if (needSamples) {
                        urlMaker.setCustomerID (0);

                        Vector  v = StringOps.splitString (data.sampleEmails ());

                        for (int mcount = 0; mcount < v.size (); ++mcount) {
                            String  email = validateSampleEmail ((String) v.elementAt (mcount));

                            if ((email != null) && (email.length () > 3) && (email.indexOf ('@') != -1)) {
                                cinfo.setEmail (email);
                                for (int n = 0; n < email_count; ++n) {
                                    EMMTag  etag = email_tags.elementAt (n);

                                    etag.mTagValue = email;
                                }
                                for (int n = 0; n < 3; ++n)
                                    if (n <= data.masterMailtype) {
                                        mailtype_tag.mTagValue = Integer.toString (n);
                                        mailer.writeMail (cinfo, mcount + 1, n, 0, "email", tagNames, urlMaker);
                                    }
                            }
                        }
                        needSamples = false;
                    }

                } // end while
                rset.close ();
            }
        } catch (SQLException e){
            throw new Exception("Error during main query or mail generation:" + e);
        }

        mailer.done ();
        if (! data.isPreviewMailing ()) {
            finalizeMailingToDatabase (mailer);
        }
        data.endExecution ();
        data.updateGenerationState ();

        data.logging (Log.DEBUG, "execute", "Successful end");
        data.suspend (conn);
    }

    public Object mkDestroyer (int mailingId) throws Exception {
        return new Destroyer (mailingId);
    }

    /** Full execution of a mail generation
     * @param custid optional customer id
     * @return Status string
     */
    public String fire (String custid) throws Exception {
        String  str;

        str = null;
        try {
            data.logging (Log.INFO, "mailgun", "Starting up");
            Hashtable <String, Object>  opts = new Hashtable <String, Object> ();

            if (custid != null)
                opts.put ("customer-id", custid);
            doPrepare (null, opts);
            doExecute (null, opts);
            str = "Success: Mailgun fired.";
        } catch (Exception e) {
            data.logging (Log.ERROR, "mailgun", "Creation failed: " + e.toString ());
            if ((data != null) && (data.mailing_id > 0)) {
                Destroyer   d = (Destroyer) mkDestroyer ((int) data.mailing_id);

                data.logging (Log.INFO, "mailgun", "Try to remove failed mailing: " + e);
                str = d.destroy ();
                d.done ();
            }
            try {
                done ();
            } catch (Exception temp) {
                ;
            }
            throw e;
        }
        data.logging (Log.INFO, "mailgun", "Execution done: " + str);
        try {
            done ();
        } catch (Exception e) {
            ;
        }
        return str;
    }

    /** Optional add database hint
     * @return the hint
     */
    public String getHint () {
        return "";
    }

    /** Build the complete big query
     * @param tagNames the tags
     * @param allBlocks all content information
     * @return the created query
     */
    public String getSelectvalue (Hashtable tagNames, boolean hint) throws Exception {
        StringBuffer    select_string = new StringBuffer();

        select_string.append("SELECT ");
        if (hint) {
            String  hstr = getHint ();

            if (hstr.length () > 0) {
                select_string.append (hstr);
            }
        }
        if (tagNames != null) {
            EMMTag current_tag=null;

            // append all select string values of all tags
            select_string.append ("cust.customer_id, bind.user_type");
            for ( Enumeration e = tagNames.elements(); e.hasMoreElements(); ) {
                current_tag = (EMMTag) e.nextElement(); // new
                if ((! current_tag.globalValue) && (! current_tag.fixedValue) && (! current_tag.mutableValue) && (current_tag.tagType == EMMTag.TAG_DBASE)) {
                    select_string.append(", " + current_tag.mSelectString);
                }
            }
            if (data.lusecount > 0)
                for (int n = 0; n < data.lcount; ++n) {
                    Column  c = data.columnByIndex (n);
                    
                    if (c.inuse) {
                        select_string.append (", ");
                        select_string.append (c.ref == null ? "cust" : c.ref);
                        select_string.append (".");
                        select_string.append (c.name);
                    }
                }
            // remove last comma
            // select_string.deleteCharAt(select_string.length() - 1);
        } else
            select_string.append ("count(distinct customer_id)");

        // turn stringbuffer into string
        String result = select_string.toString();

        data.logging (Log.DEBUG, "selectvalue", "SQL-String: " + result);

        return result;
    }
}
