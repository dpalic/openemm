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
package org.agnitas.preview;

import  java.util.Hashtable;
import  java.util.Enumeration;
import  java.util.ResourceBundle;
import java.util.StringTokenizer;
import  java.util.regex.Pattern;
import  java.util.regex.Matcher;
import  org.agnitas.backend.MailgunImpl;
import  org.agnitas.util.Log;

public class Preview {
    /* Special IDs for identifing parts of message */
    /** The ID for the complete header */
    static final public String  ID_HEAD = "__head__";
    /** The ID for a hashtable for individual header lines */
    static final public String  ID_HDETAIL = "__head_detail__";
    /** The ID for the text part */
    static final public String  ID_TEXT = "__text__";
    /** The ID for the HTML part */
    static final public String  ID_HTML = "__html__";
    /** The ID for an error, if one had occured */
    static final public String  ID_ERROR = "__error__";

    /** Pattern to find entities to escape */
    static private Pattern      textReplace = Pattern.compile ("[&<>'\"]");
    /** Values to escape found entities */
    static private Hashtable <String, String>
                    textReplacement = new Hashtable <String, String> ();
    static {
        textReplacement.put ("&", "&amp;");
        textReplacement.put ("<", "&lt;");
        textReplacement.put (">", "&gt;");
        textReplacement.put ("'", "&apos;");
        textReplacement.put ("\"", "&quot;");
    }

    /** PCache (Page Cache)
     * This class is used to cache full generated pages for a single
     * customer
     */
    class PCache {
        class PEntry {
            protected long      timestamp;
            protected Hashtable <String, Object>
                        cont;

            protected PEntry (long nTimestamp, Hashtable <String, Object> nCont) {
                timestamp = nTimestamp;
                cont = nCont;
            }
        }
        private int     maxAge;
        private int     maxEntries;
        private int     size;
        private Hashtable <String, PEntry>
                    cache;

        protected PCache (int nMaxAge, int nMaxEntries) {
            maxAge = nMaxAge;
            maxEntries = nMaxEntries;
            size = 0;
            cache = new Hashtable <String, PEntry> ();
        }

        protected void done () {
            cache.clear ();
            size = 0;
        }

        protected Hashtable <String, Object> find (long mailingID, long customerID, long now) {
            String      key = mkKey (mailingID, customerID);
            PEntry      ent = (PEntry) cache.get (key);
            Hashtable <String, Object>
                    rc = null;

            if (ent != null) {
                if (ent.timestamp + maxAge < now) {
                    cache.remove (ent);
                    --size;
                } else {
                    rc = ent.cont;
                }
            }
            return rc;
        }

        protected void store (long mailingID, long customerID, long now, Hashtable <String, Object> cont) {
            String      key = mkKey (mailingID, customerID);
            PEntry      ent;

            while (size + 1 >= maxEntries) {
                PEntry  cur = null;

                for (Enumeration e = cache.elements (); e.hasMoreElements (); ) {
                    PEntry  chk = (PEntry) e.nextElement ();

                    if ((cur == null) || (cur.timestamp > chk.timestamp))
                        cur = chk;
                }
                if (cur != null) {
                    cache.remove (cur);
                    --size;
                } else
                    break;
            }
            ent = new PEntry (now, cont);
            cache.put (key, ent);
            ++size;
        }

        protected int getSize () {
            return size;
        }

        private String mkKey (long mailingID, long customerID) {
            return "[" + mailingID + "/" + customerID + "]";
        }
    }
    /** limited list for caching mailings */
    private Cache   mhead, mtail;
    /** max age in seconds for an entry in the cache */
    private int maxAge;
    /** max number of entries in the cache */
    private int maxEntries;
    /** current number of entries */
    private int msize;
    /** cache for generated pages */
    private PCache  pcache;
    /** last statistics report */
    private long    lastrep;
    /** logger */
    protected Log   log;

    /**
     * converts a string to an interger, using a default value
     * on errors or unset input
     * 
     * @param s the string to convert
     * @param dflt the default, if string is unset or unparsable
     * @return the integer for the input string
     */
    private int atoi (String s, int dflt) {
        int rc;

        if (s == null)
            rc = dflt;
        else
            try {
                rc = Integer.parseInt (s);
            } catch (NumberFormatException e) {
                rc = dflt;
            }
        return rc;
    }

    /** Preview
     * the constructor reading the configuration
     * from emm.properties
     */
    public Preview () {
        String  age = null;
        String  size = null;
        String  pcage = null;
        String  pcsize = null;
        String  logname = null;
        String  loglevel = null;
        try {
            ResourceBundle  rsc;

            rsc = ResourceBundle.getBundle ("emm");
            if (rsc != null) {
                age = rsc.getString ("preview.mailgun.cache.age");
                size = rsc.getString ("preview.mailgun.cache.size");
                pcage = rsc.getString ("preview.page.cache.age");
                pcsize = rsc.getString ("preview.page.cache.size");
                logname = rsc.getString ("preview.logname");
                loglevel = rsc.getString ("preview.loglevel");
            }
        } catch (Exception e) {
            ;//System.out.println (e.toString ());
        }
        mhead = null;
        mtail = null;
        maxAge = atoi (age, 300);
        maxEntries = atoi (size, 20);
        msize = 0;
        pcache = new PCache (atoi (pcage, 120), atoi (pcsize, 50));

        if (logname == null) {
            logname = "preview";
        }
        int level;
        if (loglevel == null)
            level = Log.INFO;
        else
            try {
                level = Log.matchLevel (loglevel);
            } catch (NumberFormatException e) {
                level = Log.INFO;
            }
        lastrep = 0;
        log = new Log (logname, level);
    }

    /** done
     * CLeanup code
     */
    public void done () {
        Cache   temp;

        while (mhead != null) {
            temp = mhead;
            mhead = mhead.next;
            try {
                temp.release ();
            } catch (Exception e) {
                log.out (Log.ERROR, "done", "Failed releasing cache: " + e.toString ());
            }
        }
        mhead = null;
        mtail = null;
        msize = 0;
        pcache.done ();
    }

    
    public int getMaxAge () {
        return maxAge;
    }

    public void setMaxAge (int nMaxAge) {
        maxAge = nMaxAge;
    }

    public int getMaxEntries () {
        return maxEntries;
    }

    public synchronized void setMaxEntries (int nMaxEntries) {
        if (nMaxEntries >= 0) {
            maxEntries = nMaxEntries;
            while (msize > maxEntries) {
                Cache   c = pop ();

                try {
                    c.release ();
                } catch (Exception e) {
                    log.out (Log.ERROR, "max", "Failed releasing cache: " + e.toString ());
                }
            }
        }
    }

    /** mkMailgun
     * Creates a new instance for a mailgun
     * @return the new instance
     */
    public Object mkMailgun () throws Exception {
        return new MailgunImpl ();
    }

    /** createPreview
     * The main entrance for this class, a preview for all
     * parts of the mail is generated into a hashtable for
     * the given mailing and customer. If cacheable is set
     * to true, the result is cached for speed up future
     * access.
     * @param mailingID the mailing-id to create the preview for
     * @param customerID the customer-id to create the preview for
     * @param cacheable if the result should be cached
     * @return Hashtable containing the created preview parts
     */
    public Hashtable <String, Object> createPreview (long mailingID, long customerID, boolean cachable) {
        long        now;
        String      error;
        Cache       c;
        Hashtable <String, Object>
                rc;

        now = System.currentTimeMillis () / 1000;
        error = null;
        if (cachable) synchronized (pcache) {
            if (lastrep + 3600 < now) {
                log.out (Log.INFO, "stat", "Mailing cache: " + msize + ", Page cache: " + pcache.getSize ());
                lastrep = now;
            }
            rc = pcache.find (mailingID, customerID, now);
            if (rc == null) {
                for (c = mhead; c != null; c = c.next)
                    if (c.mailingID == mailingID)
                        break;
                if (c != null) {
                    pop (c);
                    if (c.ctime + maxAge < now) {
                        log.out (Log.VERBOSE, "create", "Found entry for " + mailingID + "/" + customerID + " in cache, but it is expired");
                        try {
                            c.release ();
                        } catch (Exception e) {                         ;
                            log.out (Log.ERROR, "create", "Failed releasing cache: " + e.toString ());
                        }
                    } else {
                        log.out (Log.VERBOSE, "create", "Found entry for " + mailingID + "/" + customerID + " in cache");
                        push (c);
                    }
                }
                if (c == null) {
                    try {
                        c = new Cache (mailingID, now, this);
                        push (c);
                    } catch (Exception e) {
                        c = null;
                        error = e.toString ();
                        log.out (Log.ERROR, "create", "Failed to create new cache entry for " + mailingID + "/" + customerID + ": " + error);
                    }
                }
                if (c != null) {
                    try {
                        rc = c.createPreview (customerID);
                    } catch (Exception e) {
                        error = e.toString ();
                        log.out (Log.ERROR, "create", "Failed to create preview for " + mailingID + "/" + customerID + ": " + error);
                    }
                    if ((rc != null) && (error == null)) {
                        pcache.store (mailingID, customerID, now, rc);
                    }
                }
            }
        } else {
            rc = null;
            try {
                c = new Cache (mailingID, now, this);
                rc = c.createPreview (customerID);
                c.release ();
            } catch (Exception e) {
                error = e.toString ();
                log.out (Log.ERROR, "create", "Failed to create uncached preview for " + mailingID + "/" + customerID + ": " + error);
            }
        }
        if (error != null) {
            if (rc == null)
                rc = new Hashtable <String, Object> ();
            if (rc != null)
                rc.put (ID_ERROR, error);
        }
        error = (String) rc.get (ID_ERROR);
        if (error != null)
            log.out (Log.INFO, "create", "Found error for " + mailingID + "/" + customerID + ": " + error);
        return rc;
    }

    /** escapeEntities
     * This method escapes the HTML entities to be displayed
     * in a HTML context
     * @param s the input string
     * @return null, if input string had been null,
     *         the escaped version of s otherwise
     */
    private String escapeEntities (String s) {
        if (s != null) {
            int     slen = s.length ();
            Matcher     m = textReplace.matcher (s);
            StringBuffer    buf = new StringBuffer (slen + 128);
            int     pos = 0;

            while (m.find (pos)) {
                int next = m.start ();
                String  ch = m.group ();

                if (pos < next)
                    buf.append (s.substring (pos, next));
                buf.append ((String) textReplacement.get (ch));
                pos = m.end ();
            }
            if (pos != 0) {
                if (pos < slen)
                    buf.append (s.substring (pos));
                s = buf.toString ();
            }
        }
        return s;
    }

    /** encode
     * Encodes a string to a byte stream using the given character set,
     * if escape is true, HTML entities are escaped prior to encoding
     * @param s the string to encode
     * @param charset the character set to convert the string to
     * @param escape if HTML entities should be escaped
     * @return the coded string as a byte stream
     */
    private byte[] encode (String s, String charset, boolean escape) {
        if (escape && (s != null)) {
            s = "<pre>\n" + escapeEntities (s) + "</pre>\n";
        }
        try {
            return s == null ? null : s.getBytes (charset);
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
    }
    
    /** get
     * a null input save conversion variant
     * @param s the input string
     * @param escape to escape HTML entities
     * @return the converted string
     */
    private String convert (String s, boolean escape) {
        if (escape && (s != null)) {
            return escapeEntities (s);
        }
        return s;
    }

    /**
     * Get header-, text- or HTML-part from hashtable created by
     * createPreview as byte stream
     */
    public byte[] getHeaderPart (Hashtable <String, Object> output, String charset, boolean escape) {
        return encode ((String) output.get (ID_HEAD), charset, escape);
    }
    public byte[] getHeaderPart (Hashtable <String, Object> output, String charset) {
        return getHeaderPart (output, charset, false);
    }

    public byte[] getTextPart (Hashtable <String, Object> output, String charset, boolean escape) {
        return encode ((String) output.get (ID_TEXT), charset, escape);
    }
    public byte[] getTextPart (Hashtable <String, Object> output, String charset) {
        return getTextPart (output, charset, false);
    }

    public byte[] getHTMLPart (Hashtable <String, Object> output, String charset, boolean escape) {
        return encode ((String) output.get (ID_HTML), charset, escape);
    }
    public byte[] getHTMLPart (Hashtable <String, Object> output, String charset) {
        return getHTMLPart (output, charset, false);
    }
    
    /**
     * Get header-, text- or HTML-part as strings
     */
    public String getHeader (Hashtable <String, Object> output, boolean escape) {
        return convert ((String) output.get (ID_HEAD), escape);
    }
    public String getHeader (Hashtable <String, Object> output) {
        return getHeader (output, false);
    }
    
    public String getText (Hashtable <String, Object> output, boolean escape) {
        return convert ((String) output.get (ID_TEXT), escape);
    }
    public String getText (Hashtable <String, Object> output) {
        return getText (output, false);
    }
    
    public String getHTML (Hashtable <String, Object> output, boolean escape) {
        return convert ((String) output.get (ID_HTML), escape);
    }
    public String getHTML (Hashtable <String, Object> output) {
        return getHTML (output, false);
    }

    /**
     * Get individual lines from the header
     */
    @SuppressWarnings ("unchecked")
    public String[] getHeaderField (Hashtable <String, Object> output, String field) {
        String[]    rc = null;

        synchronized (output) {
            Hashtable <String, String[]>
                    header = (Hashtable <String, String[]>) output.get (ID_HDETAIL);

            if (header == null) {
                String  head = (String) output.get (ID_HEAD);

                header = new Hashtable <String, String[]> ();
                if (head != null) {
                    String[]    lines = head.split ("\r?\n");
                    String      cur = null;

                    for (int n = 0; n <= lines.length; ++n) {
                        String  line = (n < lines.length ? lines[n] : null);

                        if ((line == null) || ((line.indexOf (' ') != 0) && (line.indexOf ('\t') != 0))) {
                            if (cur != null) {
                                String[]    parsed = cur.split (": +", 2);

                                if (parsed.length == 2) {
                                    String      key = parsed[0].toLowerCase ();
                                    String[]    content = (String[]) header.get (key);
                                    int     nlen = (content == null ? 1 : content.length + 1);
                                    String[]    ncontent = new String[nlen];

                                    if (content != null)
                                        for (int m = 0; m < content.length; ++m)
                                            ncontent[m] = content[m];
                                    ncontent[nlen - 1] = parsed[1];
                                    header.put (key, ncontent);
                                }
                            }
                            cur = line;
                        } else if (cur != null) {
                            cur += '\n' + line;
                        }
                    }
                }
            }
            rc = (String[]) header.get (field.toLowerCase ());
        }
        return rc;
    }
    public String getPartOfHeader (Hashtable <String, Object> output, boolean escape, String headerKeyword) {
        String      rc = null;
        String[]    head = getHeaderField (output, headerKeyword);

        if ((head != null) && (head.length > 0)) {
            rc = escape ? escapeEntities (head[0]) : head[0];
        }
        return rc;
    }

    private Cache pop (Cache c) {
        if (c != null) {
            if (c.next != null) {
                c.next.prev = c.prev;
            } else {
                mtail = c.prev;
            }
            if (c.prev != null) {
                c.prev.next = c.next;
            } else {
                mhead = c.next;
            }
            c.next = null;
            c.prev = null;
            --msize;
        }
        return c;
    }

    private Cache pop () {
        Cache   rc;

        rc = mtail;
        if (rc != null) {
            mtail = mtail.prev;
            if (mtail != null) {
                mtail.next = null;
            } else {
                mhead = null;
            }
            --msize;
            rc.next = null;
            rc.prev = null;
        }
        return rc;
    }

    private void push (Cache c) {
        if (msize >= maxEntries) {
            Cache   tmp = pop ();

            if (tmp != null) {
                try {
                    tmp.release ();
                } catch (Exception e) {
                    log.out (Log.ERROR, "push", "Failed releasing cache: " + e.toString ());
                }
                --msize;
            }
        }
        c.next = mhead;
        c.prev = null;
        if (mhead != null) {
            mhead.prev = c;
        }
        mhead = c;
        ++msize;
    }

    public static void main (String[] args) {
        Preview     p = new Preview ();
        Hashtable   h = p.createPreview (62, 1, true);

        for (java.util.Enumeration e = h.keys (); e.hasMoreElements (); ) {
            String  key = (String) e.nextElement ();
            String  cont = (String) h.get (key);

            System.out.println (key + ":\n" + cont + "\n\n");
        }
        p.done ();
    }



    // well, we could create a global Hashmap containing all the values for this preview
    // but the part-Method is called not very often, so its more efficient to parse
    // the header if we need it.
    // As parameter give the "Keyword" you will get then the appropriate return String.
    // Possible Values for the Header are:
    // "Return-Path", "Received", "Message-ID", "Date", "From", "To", "Subject", "X-Mailer", "MIME-Version"
    // warning! We do a "startswith" comparison, that means, if you give "Re" as parameter, you will
    // get either "Return-Path" or "Received", depending on what comes at last.
    @Deprecated
    public String getPartOfHeader(Hashtable <String, Object> output, String charset, boolean forHTML, String headerKeyword) {
       String returnString = null;
       String tmpLine = null;
       // use just \n as line delimiter. Warning, if you use Windows, that will not work...
       StringTokenizer st = new StringTokenizer( new String(getHeaderPart(output, charset, forHTML)) , "\n");
       while (st.hasMoreElements() ) {
           // get next line and cut the leading and trailing whitespaces of.
           tmpLine = ((String) st.nextElement()).trim();
           // convert Header String to lower and compare with lower-case given String
           if (tmpLine.toLowerCase().startsWith(headerKeyword.toLowerCase())) {
               // get index of first :
               int endOfHeaderKeyword = tmpLine.indexOf(':') +1;
               // return everything from first ":" and remove trailing whitespaces..
               returnString = tmpLine.substring(endOfHeaderKeyword).trim();
           }
       }
       return returnString;
    }

}
