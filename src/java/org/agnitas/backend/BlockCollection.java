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

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import org.agnitas.util.Log;

/**
 * Holds all Blocks of a Mailing
*/
public class BlockCollection {
    /**
     * Reference to configuration
     */
    private Data data;

    /**
     * All blocks in the mailing
     */
    public BlockData    blocks[];

    /**
     * Total number of all blocks
     */
    protected int   totalNumber = 0;

    /**
     * All dynamic blocks
     */
    public DynCollection dynContent;

    /**
     * Collection of all found dynamic names in blocks
     */
    public Vector   dynNames;

    /**
     * Number of all names in dynNames
     */
    public int  dynCount;

    /**
     * if this is a pure text mailing
     */
    public boolean  pureText = false;

    /**
     * if we have any attachments
     */
    public boolean  hasAttachment = false;

    /**
     * total amount of attachments
     */
    public int  numberOfAttachments = 0;

    /**
     * referenced database fields in conditions
     */
    public HashSet  conditionFields;

    public Object mkDynCollection (Object nData) {
        return new DynCollection ((Data) nData);
    }

    public Object mkEMMTag (String tag, boolean isCustom) throws Exception {
        return new EMMTag (data, data.company_id, tag, isCustom);
    }

    /**
     * Constructor for this class
     */
    public void setupBlockCollection (Object nData) throws Exception {
        data = (Data) nData;

        totalNumber = 0;
        blocks = null;
        readBlockdata ();

        dynContent = (DynCollection) mkDynCollection (data);
        dynContent.collectParts ();

        dynNames = new Vector ();
        dynCount = 0;

        conditionFields = new HashSet ();
    }

    /**
     * Add a string to the receiver `To:' line in the mailing
     * to mark an admin- or testmailing
     *
     * @return the optional string
     */
    public String addTo () {
        if (data.isAdminMailing ()) {
            return "Adminmail ";
        } else if (data.isTestMailing ()) {
            return "Testmail ";
        }
        return "";
    }

    /**
     * Add a string to the receiver `Subject:' line
     *
     * @return the optional string
     */
    public String addSubject () {
        return "";
    }

    public Object mkBlockData () {
        return new BlockData ();
    }

    /** Adds the `From' line to header
     * @return the from line
     */
    public String headFrom () {
        if (data.from_email.full != data.from_email.pure) {
            return "HFrom: " + data.from_email.full_puny + data.eol;
        } else {
            return "HFrom: <" + data.from_email.full_puny + ">" + data.eol;
        }
    }
    
    /** Adds the 'Reply-To' line to head
     * @return the reply-to line
     */
    public String headReplyTo () {
        if ((data.reply_to != null) && data.reply_to.valid ()) {
            return "HReply-To: " + data.reply_to.full_puny + data.eol;
        }
        return "";
    }

    /**
     * Creates the first block holding the header information.
     *
     * @return the newly created block
     */
    public BlockData createBlockZero () {
        BlockData   b = (BlockData) mkBlockData ();
        String      head;

        if ((data.from_email != null) &&
            data.from_email.valid () &&
            (data.subject != null)) {
            head =  "T[agnSYSINFO name=\"EPOCH\"]" + data.eol +
                "S<" + data.from_email.pure_puny + ">" + data.eol +
                "R<" + "[agnEMAIL code=\"punycode\"]" + ">" + data.eol +
                "H?P?Return-Path: <" + data.from_email.pure_puny +">" + data.eol +
                "HReceived: by [agnSYSINFO name=\"FQDN\" default=\"" + data.domain + "\"] for <[agnEMAIL]>; [agnSYSINFO name=\"RFCDATE\"]" + data.eol +
                "HMessage-ID: <" + EMMTag.internalTag (EMMTag.TI_MESSAGEID) + ">" + data.eol +
                "HDate: [agnSYSINFO name=\"RFCDATE\"]" + data.eol;
            head += headFrom ();
            head += headReplyTo ();
            head += "HTo: " + addTo () + "<" + "[agnEMAIL code=\"punycode\"]" + ">" + data.eol +
                "HSubject: " + addSubject () + data.subject + data.eol +
                "HX-Mailer: " + data.makeMailer () + data.eol +
                "HMIME-Version: 1.0" + data.eol;
        } else {
            head = "- unset -" + data.eol;
        }

        b.content = head;
        b.cid = "agnHead";
        b.is_parseable = true;
        b.is_text = true;
        b.type = BlockData.HEADER;
        b.media = Media.TYPE_EMAIL;
        b.comptype = 0;
        return b;
    }

    /**
     * Check the content of the text/html part to determiante if
     * this is a pure textmailing
     *
     * @return true in case of a pure text mailing
     */
    public boolean checkForPureText () {
        BlockData   text = null,
                html = null;

        for (int n = 0; n < totalNumber; ++n) {
            if ((text == null) && (blocks[n].type == BlockData.TEXT)) {
                text = blocks[n];
            } else if ((html == null) && (blocks[n].type == BlockData.HTML)) {
                html = blocks[n];
            }
        }
        if ((text != null) && (html != null)) {
            if ((html.length () < 3) || html.looksLike (text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retreives the blockdata from a SQL record
     *
     * @return the filled blockdata
     */
    public Object retreiveBlockdata (ResultSet rset) throws SQLException {
        BlockData   tmp;
        int     comptype;
        String      compname;
        String      mtype;
        int     target_id;
        Clob        emmblock;
        Blob        binary;

        comptype = rset.getInt (1);
        compname = rset.getString (2);
        mtype = rset.getString (3);
        target_id = rset.getInt (4);
        emmblock = rset.getClob (5);
        binary = rset.getBlob (6);
        tmp = (BlockData) mkBlockData ();
        tmp.media = Media.TYPE_UNRELATED;
        if (comptype == 0) {
            if (compname.equals ("agnText")) {
                tmp.type = BlockData.TEXT;
                tmp.media = Media.TYPE_EMAIL;
            } else if (compname.equals ("agnHtml")) {
                tmp.type = BlockData.HTML;
                tmp.media = Media.TYPE_EMAIL;
            } else {
                data.logging (Log.WARNING, "collect", "Invalid compname " + compname + " for comptype 0 found");
                return null;
            }
            tmp.is_parseable = true;
            tmp.is_text = true;
        } else if (comptype == 1) {
            tmp.type = BlockData.RELATED_BINARY;
        } else if (comptype == 3) {
            tmp.type = BlockData.ATTACHMENT_BINARY;
            tmp.is_attachment = true;
        } else if (comptype == 4) {
            tmp.type = BlockData.ATTACHMENT_TEXT;
            tmp.is_parseable = true;
            tmp.is_text = true;
            tmp.is_attachment = true;
        } else if (comptype == 5) {
            tmp.type = BlockData.RELATED_BINARY;
        } else {
            data.logging (Log.WARNING, "collect", "Invalid comptype " + comptype + " found");
            return null;
        }
        tmp.comptype = comptype;
        tmp.cid = compname;
        tmp.mime = mtype;
        tmp.targetID = target_id;

        // write to different String, depending on text/binary data
        if (tmp.is_parseable) {
            tmp.content =  StringOps.convertOld2New (StringOps.clob2string (emmblock));
        } else {
            tmp.parsed_content = StringOps.clob2string (emmblock);
        }
        if (binary != null) {
            tmp.binary = binary.getBytes (1, (int) binary.length ());
        } else {
            tmp.binary = null;
        }
        return tmp;
    }

    /**
     * Retreive optional related data for newly created block
     *
     * @return the optional block
     */
    public Object retreiveRelatedBlockdata (Object obd) {
        return null;
    }

    /**
     * Return mailing_id related part of the where clause to
     * retreive the components
     *
     * @return the clause part
     */
    public String mailingClause () {
        return "mailing_id = " + data.mailing_id;
    }

    public String componentFields () {
        return "comptype, compname, mtype, target_id, emmblock, binblock";
    }

    /**
     * Reads the blocks used by this mailing from the database
     */
    public void readBlockdata () throws Exception {
        String  query;

        query = "SELECT " + componentFields () + " " +
            "FROM component_tbl " +
            "WHERE company_id = " + data.company_id + " AND (" + mailingClause () + ") " +
            "ORDER BY component_id";
        try {
            Vector      collect;
            ResultSet   rset;
            int     n;

            collect = new Vector ();
            collect.addElement (createBlockZero ());
            totalNumber = 1;
            rset = data.dbase.execQuery (query);
            while (rset.next ()) {
                BlockData   tmp;

                tmp = (BlockData) retreiveBlockdata (rset);
                if (tmp == null) {
                    continue;
                }

                if ((tmp.type == BlockData.ATTACHMENT_BINARY) || (tmp.type == BlockData.ATTACHMENT_TEXT)) {
                    hasAttachment = true;
                    ++numberOfAttachments;
                }

                collect.addElement (tmp);
                ++totalNumber;
                tmp = (BlockData) retreiveRelatedBlockdata (tmp);
                if (tmp != null) {
                    collect.addElement (tmp);
                    ++totalNumber;
                }
            }
            rset.close();
            blocks = (BlockData[]) collect.toArray (new BlockData[totalNumber]);
            for (n = 0; n < totalNumber; ++n) {
                BlockData   b = blocks[n];

                data.logging (Log.DEBUG, "collect",
                          "Block " + n + " (" + totalNumber + "): " + b.cid + " [" + b.mime + "]");
            }

            Arrays.sort ((Object[]) blocks);

            for (n = 0; n < totalNumber; ++n) {
                BlockData   b = blocks[n];

                b.id = n;
                if (b.targetID != 0) {
                    rset = data.dbase.simpleQuery ("SELECT target_sql FROM dyn_target_tbl " +
                                       "WHERE target_id = " + b.targetID);
                    b.condition = rset.getString (1);
                    rset.close ();
                }
                data.logging (Log.DEBUG, "collect",
                          "Block " + n + " (" + totalNumber + "): " + b.cid + " [" + b.mime + "]");
            }
        } catch (Exception e) {
            throw new Exception ("Unable to read block: " + e);
        }
        if (checkForPureText ()) {
            pureText = true;
        }
    }

    /**
     * returns the block at the given position
     *
     * @param pos the index into the block array
     * @return the block at the requested position
     */
    public BlockData getBlock (int pos) {
        return blocks[pos];
    }

    /**
     * Parses a block, collecting all tags in a hashtable
     *
     * @param cb the block to parse
     * @param tag_table the hashtable to collect tag
     */
    public void parseBlock (BlockData cb, Hashtable tag_table) throws Exception {
        if (cb.is_parseable) {
            int tag_counter = 0;
            String current_tag;

            // get all tags inside the block
            while( (current_tag = cb.get_next_tag() ) != null){

                try{
                    // add tag and EMMTag data structure to hashtable
                    if (! tag_table.containsKey (current_tag)) {
                        EMMTag  ntag = (EMMTag) mkEMMTag (current_tag, false);
                        String  dyName;

                        if ((ntag.tagType == EMMTag.TAG_INTERNAL) &&
                            (ntag.tagSpec == EMMTag.TI_DYN) &&
                            ((dyName = (String) ntag.mTagParameters.get ("name")) != null)) {
                            int n;

                            for (n = 0; n < dynCount; ++n) {
                                if (dyName.equals ((String) dynNames.elementAt (n))) {
                                    break;
                                }
                            }
                            if (n == dynCount) {
                                dynNames.addElement (dyName);
                                dynCount++;
                            }
                        }
                        tag_table.put(current_tag, ntag);
                        data.logging (Log.DEBUG, "collect", "Added Tag: " + current_tag);
                    } else
                        data.logging (Log.DEBUG, "collect", "Skip existing Tag: " + current_tag);
                } catch (Exception e) {
                    throw new Exception (
                        "Error while trying to query block " + tag_counter + " :" +e);
                }
                tag_counter++;
            }

            // check for tagless blocks
            if ( tag_counter == 0 ) {
                cb.is_parseable = false; // block contained no tags!
                cb.parsed_content = cb.content;
            }

        }
    }

    /**
     * Validate a database field in a condition and clean it up to
     * avoid code injections
     *
     * @param condition the condition to validate
     */
    public void checkCondition (String condition) {
        if (condition != null) {
            String  c = condition.toLowerCase ();
            int l = c.length ();
            int pos = 0;
            int start;

            while ((pos = c.indexOf ("cust.", pos)) != -1) {
                pos += 5;
                start = pos;
                while (pos < l) {
                    char    chk = c.charAt (pos);

                    if ((! Character.isLetterOrDigit (chk)) && (chk != '_')) {
                        break;
                    }
                    ++pos;
                }
                if (start < pos) {
                    String  cname = c.substring (start, pos);

                    conditionFields.add (cname);
                }
            }
        }
    }

    /**
     * Substidute parts of a filename using some pattern
     *
     * @return the replacement string
     */
    public String substitudeFilename (String mod, String parm, String dflt) {
        return dflt;
    }

    /**
     * Parses all blocks returning a hashtable with all found
     * tags
     *
     * @return the hashtable with all tags
     */
    public Hashtable parseBlocks() throws Exception {
        Hashtable tag_table = new Hashtable();

        // first add all custom tags
        if (data.customTags != null) {
            for (int n = 0; n < data.customTags.size (); ++n) {
                String  tname = (String) data.customTags.get (n);

                if (! tag_table.containsKey (tname)) {
                    EMMTag  ntag = (EMMTag) mkEMMTag (tname, true);

                    tag_table.put (tname, ntag);
                }
            }
        }
        // go through all blocks
        for (int count = 0; count < this.totalNumber; count++) {
            data.logging (Log.DEBUG, "collect", "Parsing block " + count);

            parseBlock (blocks[count], tag_table);
        }
        if (dynContent != null) {
            for (Enumeration e = dynContent.names.elements (); e.hasMoreElements (); ) {
                DynName tmp = (DynName) e.nextElement ();

                for (int n = 0; n < tmp.clen; ++n) {
                    DynCont cont = (DynCont) tmp.content.elementAt (n);

                    if (cont.text != null) {
                        parseBlock (cont.text, tag_table);
                    }
                    if (cont.html != null) {
                        parseBlock (cont.html, tag_table);
                    }
                    checkCondition (cont.condition);
                }
            }
        }

        for (int count = 0; count < totalNumber; ++count) {
            BlockData   b = blocks[count];

            switch (b.comptype) {
            case 3:
            case 4:
            case 7:
                int     cur = 0;
                int     start, end;
                StringBuffer    res = new StringBuffer (b.cid.length ());

                while ((start = b.cid.indexOf ("%[", cur)) != -1) {
                    end = b.cid.indexOf ("]%", start);
                    if (end == -1) {
                        break;
                    }
                    res.append (b.cid.substring (cur, start));

                    String  cont = b.cid.substring (start + 2, end);
                    int parmoffset = cont.indexOf (':');
                    String  mod, parm;

                    if (parmoffset == -1) {
                        mod = cont;
                        parm = null;
                    } else {
                        mod = cont.substring (0, parmoffset);
                        ++parmoffset;
                        while ((parmoffset < cont.length ()) && Character.isWhitespace (cont.charAt (parmoffset))) {
                            ++parmoffset;
                        }
                        parm = cont.substring (parmoffset);
                    }
                    res.append (substitudeFilename (mod, parm, b.cid.substring (start, end + 2)));
                    cur = end + 2;
                }
                if (cur < b.cid.length ()) {
                    res.append (b.cid.substring (cur));
                }
                b.cid = res.toString ();
                break;
            case 5:
                for (Enumeration e = tag_table.elements (); e.hasMoreElements (); ) {
                    EMMTag  tag = (EMMTag) e.nextElement ();

                    if ((tag.tagType == EMMTag.TAG_DBASE) && (tag.tagSpec == EMMTag.TDB_IMAGE)) {
                        String  name = (String) tag.mTagParameters.get ("name");

                        if ((name != null) && name.equals (b.cid)) {
                            b.cid = tag.mTagValue;
                            break;
                        }
                    }
                }
                break;
            }
            checkCondition (b.condition);
        }

        return tag_table;
    }

    /**
     * create the corresponding url_string for the tags:
     * 1 - Profile
     * 2 - Unsubscribe
     * 3 - AutoURL
     * 4 - Onepixellog
     *
     * @param tag the tag itself
     * @param urlMaker an instance of TagString to create the URLs
     * @return the newly created URL
     */
    protected String create_url_tag (EMMTag tag, URLMaker urlMaker) throws Exception {
        switch (tag.tagSpec) {
        case 1: return urlMaker.profileURL ();
        case 2: return urlMaker.unsubscribeURL ();
        case 3:
            long    urlid = Long.parseLong ((String) tag.mTagParameters.get ("url"));

            if (urlid <= 0) {
                data.logging (Log.FATAL, "collect", "Invalid Autourl parameter or parameter not found");
                throw new Exception ("Failed due to missing/wrong URL parameter in auto url");
            }
            return urlMaker.autoURL (urlid);
        case 4: return urlMaker.onepixelURL ();
        default:
            throw new Exception ("Unknown tagSpec " + tag.tagSpec);
        }
    }

    /**
     * Already parse and replace tags with fixed value
     *
     * @param b the block to parse
     * @param tagTable the tag collection
     */
    public void parse_fixed_block (BlockData b, Hashtable tagTable) {
        String      cont = b.content;
        int     clen = cont.length ();
        StringBuffer    buf = new StringBuffer (clen + 128);
        Vector      pos = b.tag_position;
        int     count = pos.size ();
        int     start = 0;
        int     offset = 0;
        boolean     changed = false;

        for (int m = 0; m < count; ) {
            TagPos  tp = (TagPos) pos.get (m);
            EMMTag  tag = (EMMTag) tagTable.get (tp.tagname);
            String  value = tag.mTagValue;

            if ((value == null) && tag.fixedValue) {
                tag.fixedValue = false;
            }
            if (tag.fixedValue) {
                offset += value.length () - tag.mTagFullname.length ();
                buf.append (cont.substring (start, tp.start) + value);
                start = tp.end + 1;
                pos.removeElementAt (m);
                --count;
                changed = true;
            } else {
                if ((tp.content != null) && tp.content.is_parseable)
                    parse_fixed_block (tp.content, tagTable);
                tp.start += offset;
                tp.end += offset;
                ++m;
            }
        }
        if (changed) {
            if (start < clen) {
                buf.append (cont.substring (start));
            }
            b.content = StringOps.convertOld2New (buf.toString ());
            if (count == 0) {
                b.is_parseable = false;
            }
        }
    }

    /**
     * Parse and replace all tags with fixed value
     *
     * @param tagTable the collection of all tags
     */
    public void replace_fixed_tags (Hashtable tagTable) {
        for (int n = 0; n < totalNumber; ++n) {
            if (blocks[n].is_parseable) {
                parse_fixed_block (blocks[n], tagTable);
            }
        }
        if (dynContent != null) {
            for (Enumeration e = dynContent.names.elements (); e.hasMoreElements (); ) {
                DynName tmp = (DynName) e.nextElement ();

                for (int n = 0; n < tmp.clen; ++n) {
                    DynCont cont = (DynCont) tmp.content.elementAt (n);

                    if (cont.text != null) {
                        parse_fixed_block (cont.text, tagTable);
                    }
                    if (cont.html != null) {
                        parse_fixed_block (cont.html, tagTable);
                    }
                }
            }
        }
    }
}
