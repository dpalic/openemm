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

import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import org.agnitas.util.Log;

/** Class EMMTAG
 * - stores information about a single agnitas-tag
 * - constructor rectrieves selectvalues associated with tag-name from dbase
 * - after db query for a record set (user), EmmTag.mTagValue holds the value
 *   for this tag
 */
class EMMTag {
    /** This tag is taken from the database */
    final static int	TAG_DBASE = 0;
    /** This tag leads into an coded URL */
    final static int	TAG_URL = 1;
    /** This tag is handled internally */
    final static int	TAG_INTERNAL = 2;
    /** This is a forced custom tag */
    final static int	TAG_CUSTOM = 3;
    /** Internal tag, virtual Database column */
    final static int	TI_DBV = 0;
    /** Internal tag, database column */
    final static int	TI_DB = 1;
    /** Internal tag, email address */
    final static int	TI_EMAIL = 2;
    /** Internal tag, message ID */
    final static int	TI_MESSAGEID = 3;
    /** Internal tag, UID */
    final static int	TI_UID = 4;
    /** Internal tag, number of subscriber for this mailing */
    final static int	TI_SUBSCRIBERCOUNT = 5;
    /** Internal tag, current date */
    final static int	TI_DATE = 6;
    /** Internal tag, system information created during final mail creation */
    final static int	TI_SYSINFO = 7;
    /** Internal tag, dynamic condition */
    final static int	TI_DYN = 8;
    /** Internal tag, dynamic content */
    final static int	TI_DYNVALUE = 9;
    /** Handle title tags */
    final static int	TI_TITLE = 10;
    /** Handle full title tags */
    final static int	TI_TITLEFULL = 11;
    /** Names of all internal tags */
    final static String[]	TAG_INTERNALS = {
        "agnDBV",
        "agnDB",
        "agnEMAIL",
        "agnMESSAGEID",
        "agnUID",
        "agnSUBSCRIBERCOUNT",
        "agnDATE",
        "agnSYSINFO",
        "agnDYN",
        "agnDVALUE"
        ,"agnTITLE",
        "agnTITLEFULL"
    };
    /** Database tag, no special handling */
    final static int	TDB_UNSPEC = 0;
    /** Database tag, image stored in database */
    final static int	TDB_IMAGE = 1;
    /** Names of all database tags */
    final static String[]	TAG_DB = {
        null,
        "agnIMAGE"
    };
    /** The full name of this tag including all parameters */
    protected String	mTagFullname; 
    /** The name of the tag */
    protected String	mTagName;
    /** All parameters parsed into a hash */
    protected Hashtable	mTagParameters;
    /** Number of available parameter */
    private int		mNoOfParameters;
    /** Is this a complex, e.g. dynamic changable tag */
    private boolean		isComplex;
    /** Howto select this tag from the database */
    protected String	mSelectString;
    /** Result of this tag, is set for each customer, if not fixed or global */
    protected String	mTagValue;
    /** The tag type */
    protected int		tagType;
    /** The tag type specification */
    protected int		tagSpec;
    /** If this tag is fixed, e.g. can be inserted already here */
    protected boolean	fixedValue;
    /** If this tag is global, but will be inserted during final mail creation */
    protected boolean	globalValue;

    /** Internal used value on how to code an email */
    private int		emailCode;
    /** Internal used format, if this is a date tag */
    private SimpleDateFormat	dateFormat;
    /** Internal used title type */
    private Long		titleType;
    
    /** Is this character a whitespace?
     * @param ch the character to inspect
     * @return true, if character is whitespace
     */
    private boolean isspace (char ch) {
        return ((ch == ' ') || (ch == '\t') || (ch == '\n') || (ch == '\r') || (ch == '\f'));
    }

    /** Split a tag into its elements
     * @return Vector of all elements
     */
    private Vector splitTag () throws Exception {
        int	tlen;
        String	tag;
        
        tlen = mTagFullname.length ();
        if ((tlen > 0) && (mTagFullname.charAt (0) == '[')) {
            tag = mTagFullname.substring (1);
            --tlen;
        } else
            tag = mTagFullname;
        if ((tlen > 0) && (tag.charAt (tlen - 1) == ']'))
            if ((tlen > 1) && (tag.charAt (tlen - 2) == '/'))
                tag = tag.substring (0, tlen - 2);
            else
                tag = tag.substring (0, tlen - 1);
        tlen = tag.length ();
        
        Vector		rc = new Vector ();
        int		rccnt = 0;
        int		state = 0;
        StringBuffer	scratch = new StringBuffer (tlen);

        for (int n = 0; n <= tlen; ) {
            char	ch;
            
            if (n < tlen)
                ch = tag.charAt (n);
            else {
                ch = '\0';
                state = 99;
                ++n;
            }
            switch (state) {
            default:
                throw new Exception ("Invalid state " + state + " for " + mTagFullname);
            case 0:
                if (! isspace (ch)) {
                    scratch.setLength (0);
                    state = 1;
                } else
                    ++n;
                break;
            case 1:
                if (isspace (ch))
                    state = 99;
                else {
                    scratch.append (ch);
                    if (ch == '=')
                        state = 2;
                }
                ++n;
                break;
            case 2:
                if (isspace (ch))
                    state = 99;
                else {
                    scratch.append (ch);
                    if (ch == '"')
                        state = 3;
                    else
                        state = 4;
                }
                ++n;
                break;
            case 3:
                scratch.append (ch);
                if (ch == '"')
                    state = 99;
                ++n;
                break;
            case 4:
                if (isspace (ch))
                    state = 99;
                else
                    scratch.append (ch);
                ++n;
                break;
            case 99:
                if (scratch.length () > 0) {
                    rc.addElement (scratch.toString ());
                    rccnt++;
                }
                state = 0;
                break;
            }
        }
        return rccnt > 0 ? rc : null;
    }

    /** Constructor
     * @param data Reference to configuration
     * @param dbase Reference to database interface
     * @param companyID the company ID for this tag
     * @param tag the tag itself
     * @param isCustom if this is handled elsewhere
     */
    public EMMTag(Data data, DBase dbase, long companyID, String tag, boolean isCustom) throws Exception {
        this.mTagFullname = tag;
        this.mTagParameters = new Hashtable();
        
        // parse the tag
        Vector	parsed = splitTag ();
        int	pcnt;
        
        if ((parsed == null) || ((pcnt = parsed.size ()) == 0))
            throw new Exception ("Failed in parsing (empty?) tag " + mTagFullname);

        mTagName = (String) parsed.elementAt (0);
        for (int n = 1; n < pcnt; ++n) {
            String	parm = (String) parsed.elementAt (n);
            int	pos = parm.indexOf ('=');
            
            if (pos != -1) {
                String	variable = parm.substring (0, pos);
                String	value = parm.substring (pos + 1);
                
                if ((value.length () > 0) && (value.charAt (0) == '"'))
                    value = value.substring (1, value.length () - 1);
                mTagParameters.put (variable, value);
            }
        }
        mNoOfParameters = mTagParameters.size ();
        
        // check for special URL Tags
        
        // return if tag is a url tag, otherwise get tag info from database
        if (isCustom) {
            mTagValue = null;
            tagType = TAG_CUSTOM;
            tagSpec = 0;
            fixedValue = false;
            globalValue = false;
        } else if(check_tags(data, dbase) == TAG_DBASE){
        
            // SQL now!

            try {
                // get selectvalue (an SQL-statement) of the tag
                // store in this.mSelectstring
                //
                Statement stmt;
                ResultSet rset;
            
                stmt = dbase.createStatement ();
                rset = dbase.execQuery (stmt,
                            "SELECT selectvalue, type " +
                            "FROM tag_tbl " +
                            "WHERE tagname = '" + this.mTagName + "' AND (company_id = " + companyID + " OR company_id = 0 OR company_id IS null)");
            
                for ( int loop = 0; rset.next(); loop++ ) {
                    this.mSelectString = rset.getString(1);
                    if ( rset.getString(2).equals("COMPLEX") ) // TODO: replace with static var
                        this.isComplex = true;

                    if ( loop > 0 )
                        throw new EMMTagException(data,
                            "ERROR-Code AGN-2003: more then one valid entry for tagname = '" + 
                            this.mTagName + "'");
                }
                
                if (this.mSelectString == null)
                    throw new EMMTagException(data,
                        "ERROR-Code AGN-2004: no valid entry found for tagname =" 
                        + this.mTagName + " and company_id = " + companyID);
                    
                if ( !this.isComplex && this.mNoOfParameters > 0)
                    throw new EMMTagException(data,
                        "ERROR-Code AGN-2007: a simple tag cannot have additional parameters!");
                rset.close ();
                dbase.closeStatement (stmt);

            } catch (SQLException e) {
                throw new EMMTagException(data,
                    "ERROR-Code AGN-2002: sql failure while querying tag = '" +
                    this.mTagName + "' for company_id = '" + companyID + "': " + e);
            }

            int	pos, end;
                
            pos = 0;
            while ((pos = mSelectString.indexOf ("[", pos)) != -1)
                if ((end = mSelectString.indexOf ("]", pos + 1)) != -1) {
                    String	id = mSelectString.substring (pos + 1, end);
                    String	rplc = null;

                    if (id.equals ("company-id"))
                        rplc = Long.toString (data.company_id);
                    else if (id.equals ("mailinglist-id"))
                        rplc = Long.toString (data.mailinglist_id);
                    else if (id.equals ("mailing-id"))
                        rplc = Long.toString (data.mailing_id);
                    else if (id.equals ("rdir-domain"))
                        rplc = data.rdirDomain;
                    else
                        throw new EMMTagException (data, "Unknown ID [" + id + "] found for tag " + mTagName);
                    mSelectString = (pos > 0 ? mSelectString.substring (0, pos) : "") +
                            (rplc == null ? "" : rplc) +
                            (end < mSelectString.length () - 1 ? mSelectString.substring (end + 1) : "");
                    pos += rplc.length () - (id.length () + 2) + 1;
                } else
                    break;
        
            // replace arguments of complex tags (in curly braces)
            //
            if (this.isComplex) {
                for ( Enumeration e = this.mTagParameters.keys(); e.hasMoreElements(); ) {
                    String alias = "{" + (String)e.nextElement() + "}";
                    if (this.mSelectString.indexOf(alias) == -1)
                        throw new EMMTagException(data,
                            "ERROR-Code AGN-2005: parameter '" + alias + 
                            "' not found in tag entry");
                    this.mSelectString = StringOps.replace( this.mSelectString,
                        alias, (String)this.mTagParameters.get(alias.substring(1, alias.length() - 1)) );
                }
            
                if ( this.mSelectString.indexOf("{") != -1 )
                    throw new EMMTagException(data,
                                  "ERROR-Code AGN-2006: missing required parameter '" + this.mSelectString.substring(mSelectString.indexOf("{") + 1, this.mSelectString.indexOf("}")) + "' in tag = '" + this.mTagName + "'");
            }
            
            if (tagSpec == TDB_IMAGE)
                mTagValue = StringOps.unSqlString (mSelectString);
            // end if check tags
        } else if ((tagType == TAG_INTERNAL) && (tagSpec == TI_DB)) {
            mSelectString = ((String) mTagParameters.get ("column")).trim ();
            
            if (mSelectString == null)
                throw new EMMTagException (data, "Missing column name for " + internalTag (TI_DB));
            
            int	len = mSelectString.length ();
            int	n;
            for (n = 0; n < len; ++n) {
                char	ch = mSelectString.charAt (n);
                
                if (((n == 0) && (! Character.isLetter (ch)) && (ch != '_')) ||
                    ((n > 0) && (! Character.isLetterOrDigit (ch)) && (ch != '_')))
                    break;
            }
            mSelectString = "cust." + (n < len ? mSelectString.substring (0, n) : mSelectString);
        } else if ((tagType == TAG_INTERNAL) && (tagSpec == TI_DBV)) {
            mSelectString = (String) mTagParameters.get ("column");
            
            if (mSelectString != null)
                mSelectString = mSelectString.trim ().toUpperCase ();
        }
    }


    /** Determinate the type of the tag
     * @param data Reference to configuration
     * @param dbase Reference to database interface
     */
    private int check_tags(Data data, DBase dbase) {
        fixedValue = false;
        globalValue = false;
        if(this.mTagName.equals("agnPROFILE") ){
            tagType = TAG_URL;
            tagSpec = 1;
        } else if(this.mTagName.equals("agnUNSUBSCRIBE") ){
            tagType = TAG_URL;
            tagSpec = 2;
        } else if(this.mTagName.equals("agnAUTOURL") ){
            tagType = TAG_URL;
            tagSpec = 3;
        } else if (mTagName.equals ("agnONEPIXEL")) {
            tagType = TAG_URL;
            tagSpec = 4;
        } else if (mTagName.equals ("agnARCHIVE")) {
            tagType = TAG_URL;
            tagSpec = 5;
        } else {
            int	n;
            
            for (n = 0; n < TAG_INTERNALS.length; ++n)
                if (mTagName.equals (TAG_INTERNALS[n]))
                    break;
            if (n < TAG_INTERNALS.length) {
                tagType = TAG_INTERNAL;
                tagSpec = n;
                initializeInternalTag (data, dbase);
            } else {
                tagType = TAG_DBASE;
                tagSpec = 0;
                for (n = 0; n < TAG_DB.length; ++n)
                    if ((TAG_DB[n] != null) && (mTagName.equals (TAG_DB[n]))) {
                        tagSpec = n;
                        break;
                    }
            }
        }
        return tagType;
    }
    
    /** Initialize the tag, if its an internal one
     * @param data Reference to configuration
     * @param dbase Reference to database interface
     */
    private void initializeInternalTag (Data data, DBase dbase) {
        switch (tagSpec) {
        case TI_EMAIL:
            emailCode = 0;
            {
                String	code = (String) mTagParameters.get ("code");
                
                if (code != null)
                    if (code.equals ("punycode"))
                        emailCode = 1;
                    else
                        data.logging (Log.WARNING, "emmtag", "Unknown coding for email found: " + code);
            }
            break;
        case TI_SUBSCRIBERCOUNT:
            {
                long	cnt = data.totalSubscribers;
                String	format = null;
                String	str;

                if (((format = (String) mTagParameters.get ("format")) == null) &&
                    ((str = (String) mTagParameters.get ("type")) != null)) {
                    str = str.toLowerCase ();
                    if (str.equals ("us"))
                        format = "#,###,###";
                    else if (str.equals ("de"))
                        format = "#.###.###";
                }
                if ((str = (String) mTagParameters.get ("round")) != null) {
                    try {
                        int	round = Integer.parseInt (str);
                        
                        if (round > 0)
                            cnt = (cnt + round - 1) / round;
                    } catch (NumberFormatException e) {
                        ;
                    }
                }
                if (format != null) {
                    int	len = format.length ();
                    boolean	first = true;
                    int	last = -1;
                    mTagValue = "";

                    for (int n = len - 1; n >= 0; --n)
                        if (format.charAt (n) == '#')
                            last = n;
                    for (int n = len - 1; n >= 0; --n)
                        if (format.charAt (n) == '#') {
                            if (first || (cnt != 0)) {
                                if (n == last) {
                                    str = Long.toString (cnt);
                                    cnt = 0;
                                } else {
                                    str = Long.toString (cnt % 10);
                                    cnt /= 10;
                                }
                                mTagValue = str + mTagValue;
                                first = false;
                            }
                        } else if ((n < last) || (cnt != 0))
                            mTagValue = format.substring (n, n + 1) + mTagValue;
                } else
                    mTagValue = Long.toString (cnt);
            }
            fixedValue = true;
            break;
        case TI_DATE: 
            {
                String	temp;
                int	type;
                String	lang;
                String	country;
                String	typestr;
            
                if ((temp = (String) mTagParameters.get ("type")) != null)
                    type = Integer.parseInt (temp);
                else
                    type = 0;
                if ((temp = (String) mTagParameters.get ("language")) != null)
                    lang = temp;
                else
                    lang = "de";
                if ((temp = (String) mTagParameters.get ("country")) != null)
                    country = temp;
                else
                    country = "DE";

                typestr = "d.M.yyyy";
                try {
                    Statement	stmt;
                    ResultSet	rset;

                    stmt = dbase.createStatement ();
                    rset = dbase.execQuery (stmt, 
                                "SELECT format " +
                                "FROM date_tbl " +
                                "WHERE type = " + type);
                    if (rset.next ())
                        typestr = rset.getString (1);
                    else
                        data.logging (Log.WARNING, "emmtag", "No format in date_tbl found for " + mTagFullname);
                    rset.close ();
                    dbase.closeStatement (stmt);
                } catch (Exception e) {
                    data.logging (Log.WARNING, "emmtag", "Query failed for data_tbl: " + e);
                }

                dateFormat = new SimpleDateFormat (typestr, new Locale (lang, country));
            }
            globalValue = true;
            break;
        case TI_SYSINFO:
            {
                String	dflt = (String) mTagParameters.get ("default");
                
                mTagValue = dflt == null ? "" : dflt;
            }
            globalValue = true;
            break;
        case TI_TITLE:
        case TI_TITLEFULL:
            {
                String	temp;

                if ((temp = (String) mTagParameters.get ("type")) != null) {
                    titleType = new Long (temp);
                } else {
                    titleType = new Long (0);
                }
                if ((tagSpec == TI_TITLE) && (data.titleUsage < 1)) {
                    data.titleUsage = 1;
                } else if ((tagSpec == TI_TITLEFULL) && (data.titleUsage < 2)) {
                    data.titleUsage = 2;
                }
            }
            break;
        }
    }

    /** Handle special cases on internal tags
     * @param data Reference to configuration
     */
    public String makeInternalValue (Data data, Custinfo cinfo) throws Exception {
        if (tagType != TAG_INTERNAL) {
            throw new Exception ("Call makeInternalValue with tag type " + tagType);
        }
        switch (tagSpec) {
        case TI_DBV:
        case TI_DB:			// is set before in Mailgun.realFire ()
            break;
        case TI_EMAIL:
            switch (emailCode) {
            case 1:
                if (mTagValue != null)
                    mTagValue = StringOps.punycoded (mTagValue);
                break;
            }
            break;
        case TI_MESSAGEID:		// is set before in MailWriter.writeMail()
        case TI_UID:			// is set before in MailWriter.writeMail()
        case TI_SUBSCRIBERCOUNT:	// is set here in initializeInternalTag () from check_tags
            break;
        case TI_DATE:			// is prepared here in initializeInternalTag () from check_tags
            mTagValue = dateFormat.format (data.currentSendDate);
            break;
        case TI_SYSINFO:		// is set here in initializeInternalTag () from check_tags
            break;
        case TI_DYN:			// is handled in xml backend
        case TI_DYNVALUE:		// dito
            break;
        case TI_TITLE:
        case TI_TITLEFULL:
            {
                Title	title = (Title) data.titles.get (titleType);

                if (title != null) {
                    mTagValue = title.makeTitle (cinfo, tagSpec == TI_TITLEFULL);
                } else {
                    mTagValue = "";
                }
            }
            break;
        default:
            throw new Exception ("Unknown internal tag spec: " + toString ());
        }
        return mTagValue;
    }

    /** String representation of outself
     * @return our representation
     */
    public String toString () {
        return mTagFullname +
            " (" + (isComplex ? "complex," : "") + tagType + "," + tagSpec + ")" +
            " = " +
            (mSelectString == null ? "" : "[" + mSelectString + "]") +
            (mTagValue == null ? "*unset*" : "\"" + mTagValue + "\"");
    }

    /** Create a string of an internal tag
     * @param ti the internal tag ID
     * @return its string representation
     */
    static public String internalTag (int ti) {
        if ((ti >= 0) && (ti < TAG_INTERNALS.length))
            return "[" + TAG_INTERNALS[ti] + "]";
        return null;
    }
}
