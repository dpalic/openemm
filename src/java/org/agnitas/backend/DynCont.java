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

/**
 * Holds all information about one dynamic content block
 */
public class DynCont {
    /** constant for always matching */
    public static final long    MATCH_NEVER = -1;
    /** constant for never matching */
    public static final long    MATCH_ALWAYS = 0;
    /** Unique content ID */
    public long     id;
    /** ID for the target condiition */
    public long     targetID;
    /** order to describe importance of this part */
    public long     order;
    /** textual content */
    protected BlockData text;
    /** HTML content */
    protected BlockData html;
    /** the condition */
    protected String condition;

    /** Guess if this string is HTML code
     * @param str input string
     * @return true if this looks like HTML
     */
    public boolean isItHTML (String str) {
        int slen = str.length ();
        int state = 0;
        int open = 0, close = 0, pair = 0, amp = 0, entity = 0;

        for (int n = 0; n < slen; ++n) {
            char    ch = str.charAt (n);

            switch (ch) {
            case '<':   ++open;     break;
            case '>':   ++close;    break;
            }
            switch (state) {
            default:
            case 0:
                if (ch == '<')
                    state = 1;
                else if (ch == '&') {
                    state = 10;
                    ++amp;
                }
                break;
            case 1:
                if (ch == '>') {
                    state = 0;
                    ++pair;
                } else if (ch == '"')
                    state = 2;
                break;
            case 2:
                if (ch == '"')
                    state = 1;
                else if (ch == '>') {
                    state = 0;
                    ++pair;
                }
                break;
            case 10:
                if (ch == ';') {
                    state = 0;
                    ++entity;
                } else if (! Character.isLetter (ch))
                    state = 0;
                break;
            }
        }

        boolean rc;

        // trivial cases
        if ((state == 0) && (open == close) && (open == pair) && (amp == entity))
            rc = true;
        else if ((open == 0) && (close == 0) && (amp == 0) && (entity == 0))
            rc = false;
        else {
            int good, bad;

            good = 0;
            bad = 0;
            if ((open > 2) && (close > 2) && (Math.abs (open - close) < 3) && (Math.abs (Math.max (open, close) - pair) < 3))
                ++good;
            else
                ++bad;
            if ((amp > 2) && (entity > 2) && (Math.abs (amp - entity) < 3))
                ++good;
            else
                ++bad;
            if (state == 0)
                ++good;
            else
                ++bad;
            if (good > bad)
                rc = true;
            else if (good < bad)
                rc = false;
            else
                rc = true;  // mhh ...
        }
        return rc;
    }

    /**
     * Removed HTML entities and tags from the input
     * @param src input string
     * @return the de-HTMLd string
     */
    public String removeHTMLTags (String src) {
        for (int state = 0; state < 2; ++state) {
            StringBuffer    dest;
            int     slen;
            int     start, end, next;
            String      startPattern, endPattern;
            String      append;

            slen = src.length ();
            dest = new StringBuffer (slen);
            start = 0;
            next = 0;
            if (state == 0) {
                startPattern = "<";
                endPattern = ">";
            } else if (state == 1) {
                startPattern = "&";
                endPattern = ";";
            } else {
                startPattern = null;
                endPattern = null;
            }
            append = null;
            while (start < slen) {
                next = src.indexOf (startPattern, start);
                if (next == -1) {
                    next = slen;
                    end = slen;
                } else {
                    int pos;

                    end = next;
                    if ((pos = src.indexOf (endPattern, next)) != -1)
                        next = pos + 1;
                    else
                        next = slen;
                    if (state == 0) {
                        if ((end + 4 < next) && src.substring (end, end + 4).equals ("<agn"))
                            end = next;
                    } else if (state == 1) {
                        if (end + 2 < next) {
                            String  chk = src.substring (end + 1, next - 1);

                            if ((append = StringOps.decodeEntity (chk)) == null)
                                next = ++end;
                        } else
                            end = next;
                    }
                }
                if (end > start)
                    dest.append (src.substring (start, end));
                if (append != null) {
                    dest.append (append);
                    append = null;
                }
                start = next;
            }
            src = dest.toString ();
        }
        return src;
    }

    /** Constructor
     * @param dynContId the unique ID
     * @param dynTarget the optional target id
     * @param dynOrder the order value
     * @param dynContent the content of the block
     */
    public DynCont (long dynContId, long dynTarget, long dynOrder, String dynContent) {
        id = dynContId;
        targetID = dynTarget;
        order = dynOrder;
        text = new BlockData (removeHTMLTags (dynContent), null, null, null, BlockData.TEXT, 0, 0, "text/plain", true, true);
        html = new BlockData (dynContent, null, null, null, BlockData.HTML, 0, 0, "text/html", true, true);
        condition = null;
    }

    public DynCont () {
        condition = null;
    }
}
