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

import java.sql.*;
import java.util.*;
import java.io.*;
import javax.activation.*;
import javax.mail.internet.MimeUtility;
import org.agnitas.util.ConfigException;
import org.agnitas.util.Log;
import org.agnitas.util.Blacklist;
import org.agnitas.util.Blackdata;
import org.agnitas.beans.BindingEntry;

/** Central control class for generating mails
 */
public class MailgunImpl implements Mailgun {
    /** the status id for the maildrop_status_tbl */
    private int statusID;
    /** Reference to configuration */
    private Data data;
    /** All content blocks */
    private BlockCollection allBlocks = null;
    /** All tags for this mailing */
    private Hashtable tagNames = null;
    /** Creator for all URLs */
    private URLMaker urlMaker = null;
    /** The blacklist information for this mailing */
    private Blacklist blist = null;

    /** Constructor
     * must be followed by initializeMailung ()
     */
    public MailgunImpl () {
        statusID = -1;
        data = null;
    }
    
    /**
     * Initialize internal data
     * @param status_id the string version of the statusID to use
     * @param conn optional open database connection
     */
    public void initializeMailgun (String status_id, Connection conn) throws Exception {
        statusID = Integer.parseInt (status_id);
        data = null;
        try {
            data = new Data ("mailgun", status_id, "meta:xml/gz", conn);
        } catch (ConfigException e) {
            throw new Exception ("Error reading ini file: " + e);
        }
        data.suspend (conn);
    }
    
    /** Setup a mailgun without starting generation
     * @param conn optional open database connection
     * @param opts options to control the setup beyond DB information
     */
    public void prepareMailgun (Connection conn, Hashtable opts) throws Exception {
        doPrepare (conn, opts);
    }
    
    /** Execute an already setup mailgun
     * @param conn optional open database connection
     * @param opts options to control the execution beyond DB information
     */
    public synchronized void
    executeMailgun (Connection conn, Hashtable opts) throws Exception {
        doExecute (conn, opts);
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
            } catch (ConfigException e) {
                data = null;
                throw new Exception ("Failed in cleanup: " + e);
            }
            data = null;
        }
    }
    
    /** Change report in database
     * @param msg the new message in the DB report
     */
    public void dbReport (String msg) throws Exception {
        if (data != null)
            try {
                data.report (msg);
            } catch (ConfigException e) {
                throw new Exception (e.toString ());
            }
    }

    /** Read in the global and local blacklist
     */
    private void readBlacklist () throws Exception {
        blist = new Blacklist ();
        try {
            ResultSet	rset = data.dbase.execQuery ("SELECT company_id, email FROM cust_ban_tbl WHERE company_id = 0 OR company_id = " + data.company_id);
            while (rset.next ()) {
                int	cid = rset.getInt (1);
                String	email = rset.getString (2);
                
                blist.add (email, cid == 0);
            }
            rset.close ();
        } catch (Exception e) {
            data.logging (Log.FATAL, "readblist", "Unable to get blacklist: " + e.toString ());
            throw new Exception ("Unable to get blacklist: " + e.toString ());
        }
            
        data.logging (Log.INFO, "readblist", "Found " + blist.globalCount () + " entr" + Log.exty (blist.globalCount ()) + " in global blacklist, " +
                  				   blist.localCount () + " entr" + Log.exty (blist.localCount ()) + " in local blacklist");
    }

    /** Check the sample email receiver if they should receive
     * the sample for this mailing
     * @param inp the expression to validate
     * @return null, if no mail should be sent, otherwise the email address
     */
    private String validateSampleEmail (String inp) {
        String	email;
        int	n = inp.indexOf (':');
        
        if (n != -1) {
            String	minsub;
            
            minsub = inp.substring (0, n);
            try {
                long	minsubscriber = Long.parseLong (minsub);
                
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

    /** Prepare the mailgun
     * @param conn optional open database connection
     * @param opts options to control the setup beyond DB information
     */
    private void doPrepare (Connection conn, Hashtable opts) throws Exception {
        data.resume (conn);
        data.options (opts, 1);

        data.logging (Log.DEBUG, "prepare", "Starting firing");
        // create new Block collection and store in member var
        allBlocks = new BlockCollection(data);

        data.logging (Log.DEBUG, "prepare", "Parse blocks");
        // read all tag names contained in the blocks into Hashtable
        // - read selectvalues and store in EMMTag associated with tag name in Hashtable
        tagNames = allBlocks.parseBlocks();
        data.setUsedFieldsInLayout (allBlocks.conditionFields);

        // add default tags to Hastable
        try{
            String[]	preset = {
                "agnCUSTOMERID",
                "agnMAILTYPE",
                "agnONEPIXEL"
            };
            for (int n = 0; n < preset.length; ++n) {
                boolean	useit;
                
                switch (n) {
                default:
                    useit = true;
                    break;
                case 2:
                    useit = data.onepixlog != Data.OPL_NONE;
                    break;
                }
                if (useit) {
                    String	tn = "[" + preset[n] + "]";
                
                    if (! tagNames.containsKey (tn))
                        tagNames.put(tn, new EMMTag(data, data.dbase, data.company_id, tn, false));
                }
            }
        } catch (Exception e){
            throw new Exception("Error adding default tags: " + e);
        }
        allBlocks.replace_fixed_tags (tagNames);

        // prepare special url string maker
        try{
            urlMaker = new URLMaker (data);
        } catch (Exception e){
            throw new Exception("Error in TagString Constructor: " + e);
        }
        
        readBlacklist ();
        
        data.suspend (conn);
    }

    /** Execute a prepared mailgun
     * @param conn optional open database connection
     * @param opts options to control the execution beyond DB information
     */
    private void doExecute (Connection conn, Hashtable opts) throws Exception {
        String	table;

        data.resume (conn);
        data.options (opts, 2);
        data.sanityCheck ();
        
        data.pass++;

        // get constructed selectvalue based on tag names in Hashtable
        String select_query = get_selectvalue (tagNames, allBlocks);
        String wselect_query = select_query;

        if (allBlocks.pureText && (data.masterMailtype != 0)) {
            data.logging (Log.INFO, "execute", "Pure text mailing detected, prechecking mailing type");
            try {
                String		chkQuery = get_selectvalue (null, allBlocks) + " AND cust.mailtype != 0))";
                ResultSet	rset = data.dbase.simpleQuery (chkQuery);
                long		count = rset.getLong (1);
                    
                rset.close ();
                if (count > 0) {
                    data.logging (Log.FATAL, "mailgun", "Pure textmailing: " + count + " receiver has invalid mailtype (!= 0)");
                    dbReport ("Pure textmailing violation");
                    throw new Exception ("Mailtype check failed for " + count + " customer" + Log.exts (count));
                }
            } catch (Exception e) {
                throw new Exception ("Failed in precheck: " + e);
            }
            data.logging (Log.DEBUG, "execute", "No invalid mailtype detected");
        }

        // instantiate new MailWriter. This creates filenames, boundaries and
        // message ids
        MailWriter mailer;
        
        switch (data.outMode) {
        case Data.OUT_META:
            mailer = new MailWriterMeta (data, allBlocks, tagNames);
            break;
        default:
            throw new Exception ("Output mode " + data.outModeDescription () + " not supported");
        }

        int	columnCount = 0;
        Vector	email_tags = new Vector ();
        int	email_count = 0;
        boolean	hasOverwriteOrVirtualData = false;
        
        for (Enumeration e = tagNames.elements (); e.hasMoreElements (); ) {
            EMMTag	tag = (EMMTag) e.nextElement ();

            if ((tag.tagType == EMMTag.TAG_DBASE) || ((tag.tagType == EMMTag.TAG_INTERNAL) && (tag.tagSpec == EMMTag.TI_DB)))
                ++columnCount;
            else if ((tag.tagType == EMMTag.TAG_INTERNAL) && (tag.tagSpec == EMMTag.TI_EMAIL)) {
                email_tags.add (tag);
                email_count++;
            } else if (tag.tagType == EMMTag.TAG_CUSTOM) {
                if ((data.customMap != null) && data.customMap.containsKey (tag.mTagFullname))
                    tag.mTagValue = (String) data.customMap.get (tag.mTagFullname);
                else
                    tag.mTagValue = null;
            }
        }
        if (data.lusecount > 0)
            columnCount += data.lusecount;
        if ((data.overwriteMap != null) || (data.overwriteMapMulti != null) ||
            (data.virtualMap != null) || (data.virtualMapMulti != null))
            hasOverwriteOrVirtualData = true;

        try {
            data.logging (Log.INFO, "execute", "Start creation of mails");
            
            boolean	needSamples = data.isWorldMailing () && (data.sampleEmails () != null); // && (data.totalSubscribers >= 100);
            HashSet	seen = new HashSet ((int) data.totalSubscribers + 1);
                
            for (int state = 0; state < 2; ++state) {
                String	query;

                if (data.isWorldMailing ()) {
                    if (state == 0) {
                        String	orclause = null;
                        
                        query = select_query + " AND bind.user_type IN ('A', 'T'))";
                        if (orclause != null)
                            query += " OR " + orclause;
                        query += ")";
                    } else
                        query = wselect_query + " AND bind.user_type = 'W'))";
                } else {
                    if (state == 0)
                        query = select_query + "))";
                    else
                        query = null;
                }
                if (query == null)
                    continue;
                if ((mailer.blockSize > 0) && (mailer.inBlockCount > 0))
                    mailer.checkBlock (true);
                // main SQL query: Returns all customers of this mailinglist
                ResultSet
                            rset = data.dbase.execQuery (query);
                ResultSetMetaData	meta = rset.getMetaData ();
                int			metacount = meta.getColumnCount ();
                Column[]		rmap = new Column[metacount];
                int			index_email = -1;
                int			index_gender = -1;
                int			index_firstname = -1;
                int			index_lastname = -1;
                Custinfo		cinfo = new Custinfo ();
                EMMTag			customer_tag =(EMMTag) tagNames.get("[agnCUSTOMERID]");
                EMMTag			mailtype_tag = (EMMTag) tagNames.get ("[agnMAILTYPE]");
                
                for (int n = 0; n < metacount; ++n) {
                    String	cname = meta.getColumnName (n + 1);
                    int	ctype = meta.getColumnType (n + 1);

                    if (Column.typeStr (ctype) != null) {
                        rmap[n] = new Column (cname, ctype);
                        cname = cname.toLowerCase ();
                        if ((index_email == -1) && cname.equals ("email")) {
                            index_email = n;
                        } else if ((index_gender == -1) && cname.equals ("gender")) {
                            index_gender = n;
                        } else if ((index_firstname == -1) && cname.equals ("firstname")) {
                            index_firstname = n;
                        } else if ((index_lastname == -1) && cname.equals ("lastname")) {
                            index_lastname = n;
                        }
                    } else
                        rmap[n] = null;
                }

                while (rset.next ()) {
                    int count = 1;
                    int offset = 1;
                    EMMTag tmp_tag=null;

                    // get values from this recordset
                    // store in Emmtag inside Hashtable
                    // the tags are in the correct order
                    //
                    for ( Enumeration e = tagNames.elements(); e.hasMoreElements(); ) {
                        tmp_tag = (EMMTag) e.nextElement();
                        if ((tmp_tag.tagType == EMMTag.TAG_DBASE) || ((tmp_tag.tagType == EMMTag.TAG_INTERNAL) && (tmp_tag.tagSpec == EMMTag.TI_DB))) {
                            tmp_tag.mTagValue = null;
                            if (rmap[count] != null) {
                                rmap[count].set (rset, count + offset);
                                if (! rmap[count].isNull ())
                                    tmp_tag.mTagValue = rmap[count].get ();
                            }
                            ++count;
                        }
                    } // end for

                    for (int n = count; n < metacount; ++n)
                        if (rmap[n] != null)
                            rmap[n].set (rset, n + offset);

                    if (data.lusecount > 0) {
                        int	m;
                        
                        m = 0;
                        for (int n = 0; n < data.lcount; ++n)
                            if (data.columnUse (n))
                                data.columnSet (n, rset, count + offset + m++);
                    }

                    // retrieve otherwise used tags
                    String customer_id = customer_tag.mTagValue;

                    long	cid = Long.parseLong (customer_id);
                    Long	ocid = new Long (cid);
                    if (seen.contains (ocid))
                        continue;
                    seen.add (ocid);

                    if (hasOverwriteOrVirtualData)
                        for (Enumeration e = tagNames.elements(); e.hasMoreElements();) {
                            tmp_tag = (EMMTag) e.nextElement();
                            if ((tmp_tag.tagType == EMMTag.TAG_INTERNAL) && (tmp_tag.tagSpec == EMMTag.TI_DB)) {
                                String	nval = data.overwriteData (ocid, tmp_tag.mSelectString.substring (5).toUpperCase ());
                            
                                if (nval != null)
                                    tmp_tag.mTagValue = nval;
                            } else if ((tmp_tag.tagType == EMMTag.TAG_INTERNAL) && (tmp_tag.tagSpec == EMMTag.TI_DBV))
                                tmp_tag.mTagValue = data.virtualData (ocid, tmp_tag.mSelectString);
                        }

                    String mailtype = (mailtype_tag != null ? mailtype_tag.mTagValue : null);
                    int mtype = Integer.parseInt (mailtype);
                    if (mtype > data.masterMailtype)
                        mtype = data.masterMailtype;
                    cinfo.clear ();

                    if (index_email != -1) {
                        cinfo.setEmail (rmap[index_email].get ());
                    }
                    if (index_gender != -1) {
                        cinfo.setGender (rmap[index_gender].get ());
                    }
                    if (index_firstname != -1) {
                        cinfo.setFirstname (rmap[index_firstname].get ());
                    }
                    if (index_lastname != -1) {
                        cinfo.setLastname (rmap[index_lastname].get ());
                    }
                    for (int n = 0; n < email_count; ++n) {
                        EMMTag	etag = (EMMTag) email_tags.elementAt (n);
                        
                        etag.mTagValue = cinfo.email;
                    }

                    boolean		isblisted = false;

                    for (int blstate = 0; blstate < 3; ++blstate) {
                        String	check = null;
                        String	what = null;
                        
                        switch (blstate) {
                        case 0:
                            check = cinfo.email;
                            what = "EMail";
                            break;
                        }
                        if (check == null)
                            continue;

                        Blackdata	bl = blist.isBlackListed (check);
                        if (bl != null) {
                            String	where, whereid;
                        
                            if (bl.isGlobal ()) {
                                where = "global";
                                whereid = "G";
                            } else {
                                where = "local";
                                whereid = "L";
                            }
                            data.logging (Log.WARNING, "mailgun", "Found " + what + ": " + check + " (" + customer_id + ") in " + where + " blacklist, ignored");
                            data.logging (Log.WARNING, "mailgun", "==BLACKLIST== [" + whereid + "] (" + data.company_id + " / " + data.mailing_id + "): " + customer_id + " - " + what + " - " + check);
                            blist.writeBounce (data.mailing_id, cid);
                            isblisted = true;
                        }
                    }
                    if (isblisted)
                        continue;
                    urlMaker.setCustomerID (cid);
                    mailer.writeMail (cinfo, 0, mtype, customer_id, cid, "email", tagNames, urlMaker);


                    if (needSamples) {
                        urlMaker.setCustomerID (0);
                        customer_tag.mTagValue = "0";

                        Vector	v = StringOps.splitString (data.sampleEmails ());
                            
                        for (int mcount = 0; mcount < v.size (); ++mcount) {
                            String	email = validateSampleEmail ((String) v.elementAt (mcount));
                                
                            if ((email != null) && (email.length () > 3) && (email.indexOf ('@') != -1)) {
                                cinfo.setEmail (email);
                                for (int n = 0; n < email_count; ++n) {
                                    EMMTag	etag = (EMMTag) email_tags.elementAt (n);
                        
                                    etag.mTagValue = email;
                                }
                                for (int n = 0; n < 3; ++n)
                                    if ((n <= data.masterMailtype) && ((! allBlocks.pureText) || (n == 0))) {
                                        mailtype_tag.mTagValue = Integer.toString (n);
                                        mailer.writeMail (cinfo, mcount + 1, n, customer_tag.mTagValue, 0, "email", tagNames, urlMaker);
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

        // do reporting and finalizing -- not to be omitted
        mailer.done ();

        table = "mailing_creation_tbl";
        if (data.tableExists (table)) {
            String	query;
            
            query = "INSERT INTO " + table + " (" +
                "mailing_id, company_id, status_field, block_count, block_size, start_time, end_time) " +
                "VALUES (" +
                data.mailing_id + ", " + data.company_id + ", '" + data.status_field + "', " + mailer.blockCount + ", " + mailer.blockSize + ", " +
                StringOps.sqlDate (mailer.startExecutionTime) + ", " + StringOps.sqlDate (mailer.endExecutionTime) + ")";
            try {
                data.dbase.execUpdate (query);
            } catch (Exception e) {
                data.logging (Log.ERROR, "execute", "Unable to add mailcreation information: " + e.toString ());
            }
        }
        
        try {
            String	query = "INSERT INTO " + data.mailtracking_table + " " +
                    "(company_id, status_id, mailing_id, customer_id) " +
                    "SELECT " + data.company_id + ", " + data.maildrop_status_id + ", " + data.mailing_id + ", cust.customer_id " +
                    get_fromclause () + " " +
                    get_whereclause (true);

            data.dbase.execUpdate (query);
        } catch (Exception e) {
            data.logging (Log.ERROR, "execute", "Unable to add mailtrack information: " + e.toString ());
        }
        data.updateGenerationState ();

        data.logging (Log.DEBUG, "execute", "Successful end");
        data.suspend (conn);
    }


    /** Full execution of a mail generation
     * @param custid optional customer id
     * @return Status string
     */
    public String fire (String custid) throws Exception {
        String	str;
        
        str = null;
        try {
            data.logging (Log.INFO, "mailgun", "Starting up");
            Hashtable	opts = new Hashtable ();
            
            if (custid != null)
                opts.put ("customer-id", custid);
            doPrepare (null, opts);
            doExecute (null, opts);
            str = "Success: Mailgun fired.";
        } catch (Exception e) {
            dbReport ("Creation failed, please consult administrativa");
            if ((data != null) && (data.mailing_id > 0)) {
                Destroyer	d = new Destroyer ((int) data.mailing_id);

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

    /** Build the from part for the big query
     * @return the FROM part
     */
    private String get_fromclause () {
        return "FROM customer_" + data.company_id + "_tbl cust, customer_" + data.company_id + "_binding_tbl bind";
    }
    
    /** Build the where clause for the big query
     * @param complete if we want to build the complete query
     * @return the WHERE clause
     */
    private String get_whereclause (boolean complete) throws Exception {
        String	where = "WHERE bind.customer_id = cust.customer_id AND (" +
                "bind.mailinglist_id = " + data.mailinglist_id + " AND (";
        if (data.isCampaignMailing () && (data.campaignUserStatus != BindingEntry.USER_STATUS_ACTIVE))
            where += "bind.user_status IN (" + data.campaignUserStatus + ", " + BindingEntry.USER_STATUS_ACTIVE + ")";
        else
            where += "bind.user_status = " + BindingEntry.USER_STATUS_ACTIVE;

        String	extra;
        
        if (data.isAdminMailing ())
            extra = "bind.user_type = 'A'";
        else if (data.isTestMailing ())
            extra = "bind.user_type IN ('A', 'T')";
        else if (data.isCampaignMailing ()) {
            if (data.campaignTransactionID > 0)
                extra = "cust.transaction_id = " + data.campaignTransactionID;
            else {
                if (data.campaignCustomerID <= 0)
                    throw new Exception ("Campaign mailing without customer-ID initiated");
                extra = "cust.customer_id = " + data.campaignCustomerID;
            }
        } else {
            if ((data.campaignCustomerID > 0) || (data.campaignTransactionID > 0))
                throw new Exception ("World mailing with set customer-ID or transaction-ID");
            extra = null;
        }
        if ((extra != null) && (extra.length () > 0))
            where += " AND " + extra;
        if (data.subselect != null)
            where += " AND (" + data.subselect + ")";
        
        String	tmp = data.getCampaignSubselect ();
        if (tmp != null)
            where += " AND (" + tmp + ")";

        if (complete)
            where += "))";
        return where;
    }
    
    /** Build the complete big query
     * @param tagNames the tags
     * @param allBlocks all content information
     * @return the created query
     */
    private String get_selectvalue(Hashtable tagNames,
                       BlockCollection allBlocks
                       ) throws Exception
    {
        StringBuffer	select_string = new StringBuffer();

        select_string.append("SELECT ");
        if (tagNames != null) {
            EMMTag current_tag=null;
        
            // append all select string values of all tags
            select_string.append ("cust.customer_id");
            for ( Enumeration e = tagNames.elements(); e.hasMoreElements(); ) {
                current_tag = (EMMTag) e.nextElement(); // new
                if ((current_tag.tagType == EMMTag.TAG_DBASE) ||
                    ((current_tag.tagType == EMMTag.TAG_INTERNAL) && (current_tag.tagSpec == EMMTag.TI_DB))) { // only use dabatase tags
                    select_string.append("," + current_tag.mSelectString);
                }
            }
            if (data.lusecount > 0)
                for (int n = 0; n < data.lcount; ++n)
                    if (data.columnUse (n))
                        select_string.append (",cust." + data.columnName (n));
            // remove last comma
            // select_string.deleteCharAt(select_string.length() - 1);
         } else
            select_string.append ("count(distinct(cust.customer_id))");

        select_string.append (" " + get_fromclause () +
                      " " + get_whereclause (false));

        // turn stringbuffer into string
        String result = select_string.toString();

        data.logging (Log.DEBUG, "selectvalue", "SQL-String: " + result);
        
        return result;
    }

    public static void main (String args[]) {
        if (args.length < 1) {
            System.out.println ("Missing statusID");
            System.exit (1);
        }

        String		status_id = args[0];
        MailgunImpl	mailgun = null;
        try {
            mailgun = new MailgunImpl ();
            mailgun.initializeMailgun (status_id, null);
            mailgun.doPrepare (null, null);
            mailgun.doExecute (null, null);
        } catch (Exception e) {
            System.err.println ("Error during mailgun execution: " + e);
            if (mailgun != null) {
                try {
                    mailgun.done ();
                } catch (Exception e2) {
                    System.err.println ("Error closing database: " + e2);
                }
            }
            System.exit (1);
        }
    }
}