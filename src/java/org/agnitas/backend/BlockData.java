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
import java.util.Comparator;
import java.util.Vector;

/**
 * Hold the data for one block
 */
class BlockData implements Comparable {
    /** Possible block types */
    final static int	HEADER = 0;
    final static int	TEXT = 1;
    final static int	HTML = 2;
    final static int	RELATED_TEXT = 3;
    final static int	RELATED_BINARY = 4;
    final static int	ATTACHMENT_TEXT = 5;
    final static int	ATTACHMENT_BINARY = 6;

    /** The index in the array in BlockCollection */
    protected int id;
    /** The content from the database */
    protected String content;
    /** the parsed representation of the content */
    protected String parsed_content;
    /** the related binary part for this block */
    protected byte[] binary;
    /** the content ID */
    protected String cid;
    /** Type of the block */
    protected int type;
    /** Media of the block (just EMail atm) */
    protected int media = -1;
    /** Component type */
    protected int comptype;
    /** optional assigned condition */
    protected int targetID;
    /** MIME type for block */
    protected String mime;
    /** if this block is parsable */
    protected boolean is_parseable;
    /** if this is a textual block */
    protected boolean is_text;
    /** if this should be handled as attachment */
    protected boolean is_attachment;
    /** The condition from dyn_target_tbl */
    protected String condition;

    /** Index for internal tag parseing */
    private int current_pos=0;
    /** Store positions of found tags: start/end values */
    protected Vector tag_position;
    /** Store tag names on recrusive calls */
    private Vector tag_names;

    /** Constructor for this class
     */
    public BlockData() {
        id = -1;
        content = null;
        parsed_content = null;
        binary = null;
        cid = null;
        type = -1;
        comptype = -1;
        targetID = 0;
        mime = null;
        is_parseable = false;
        is_text = false;
        is_attachment = false;
        condition = null;
        current_pos = 0;
        tag_position = new Vector();
        tag_names = new Vector ();
    }

    /**
     * check for end of found tag
     * @param start the start position of the found tag
     * @return the end position
     */
    private int endOfTag (int start) {
        /* this failes on wrong quotes, so we disable it for the moment
        int	clen = content.length ();
        boolean	quote = false;
        int	end;

        for (end = start; end < clen; ++end) {
            char	ch = content.charAt (end);

            if ((ch == '\\') && (end + 1 < clen))
                ++end;
            else if (quote) {
                if (ch == '"')
                    quote = false;
            } else if (ch == '"')
                quote = true;
            else if (ch == ']')
                break;
        }
        return end;
         */
        int	end = content.indexOf ("]", start);

        if (end == -1) {
            end = content.length ();
        }
        return end;
    }

    /** Returns the next tagname found in the Block
     *  The current position is stored in the private var current_pos
     *  @return String with Name of the found tag
     */
    public String get_next_tag () throws Exception {
        // first return names from our precollection
        if (tag_names.size () > 0) {
            return (String) tag_names.remove (0);
        }

        // if this is the first time, cleanup all tags
        if (current_pos == 0) {
            int	start, end;
            int	clen;
        
            end = 0;
            clen = content.length ();
            while ((start = content.indexOf ("[", end)) != -1) {
                int	cbegin;
                String	chk;
                boolean	quote;

                if ((start + 1 < clen) && (content.charAt (start + 1) == '/')) {
                    cbegin = start + 2;
                } else {
                    cbegin = start + 1;
                }
                chk = content.substring (cbegin, (cbegin + 3 > clen ? clen : cbegin + 3));
                end = endOfTag (start);
                if (chk.equals ("agn")) {
                    for (int n = start; n < end; ++n) {
                        char	ch = content.charAt (n);
                        int	cnt;
                
                        cnt = 0;
                        while ((n + cnt < end) && ((ch == ' ') || (ch == '\t') || (ch == '\n') || (ch == '\r') || (ch == '\f'))) {
                            ++cnt;
                            ch = content.charAt (n + cnt);
                        }
                        if (cnt > 0) {
                            content = content.substring (0, n) + ' ' + content.substring (n + cnt);
                            end -= cnt - 1;
                            clen -= cnt - 1;
                        }
                    }
                }
            }
        }
                    
        int end_position = 0;
        int new_position = this.content.indexOf("[agn", current_pos);
        if( new_position == -1){
            current_pos=0;
            return null;
        }
        current_pos = new_position;
        end_position = endOfTag (current_pos);
        
        if (end_position == content.length ()) {
            throw new Exception ("Syntax error in tag name " + content.substring (current_pos));
        }
        
        String tagname = this.content.substring(current_pos, end_position +1);
        current_pos += tagname.length();

        // store start and name
        TagPos tagpos = new TagPos(new_position, end_position, tagname);

        if (tagpos.isDynamic () && (! tagpos.simpleTag)) {
            int	depth = 1;
            int	content_start = current_pos;
            int	content_position = current_pos;

            do {
                int depth_position = content.indexOf ("[" + tagpos.tagid + " ", current_pos);

                content_position = content.indexOf ("[/" + tagpos.tagid + " ", current_pos);
                if ((depth_position != -1) && (depth_position < content_position)) {
                    int depth_end = endOfTag (depth_position);

                    if (depth_end != -1) {
                        if (content.charAt (depth_end - 1) != '/')
                            ++depth;
                        current_pos = depth_end + 1;
                    }
                } else if (content_position == -1) {
                    depth = 0;
                    content_position = content.length ();
                    current_pos = content_position;
                } else {
                    --depth;
                    current_pos = content_position + tagpos.tagid.length () + 3;
                }
            }	while (depth > 0);

            BlockData	cont = new BlockData (content.substring (content_start, content_position), null, null, null, type, comptype, mime, is_parseable, is_text
                                  );
            String		name;

            while ((name = cont.get_next_tag ()) != null) {
                tag_names.addElement (name);
            }
            tagpos.setContent (cont);
            if (((content_position = endOfTag (content_position)) != -1) && (content_position < content.length ())) {
                content_position++;
            } else {
                content_position = content.length ();
            }
            content = content.substring (0, content_start) + content.substring (content_position);
            current_pos = content_start;
        }
        tag_position.add(tagpos);
        return tagname;
    }
        
    /** Constructor with most variables set
     */
    public BlockData(String content, String parsed_cont, byte[] binary, String cid, 
             int type, int comptype, String mime,
             boolean is_parseable, boolean is_text
             ) {
        this ();
        this.id = -1;
        this.content = content;
        this.parsed_content = parsed_content;
        this.binary = binary;
        this.cid = cid;
        this.type = type;
        this.comptype = type;
        this.targetID = 0;
        this.mime = mime;
        this.is_parseable = is_parseable;
        this.is_text = is_text;
        this.is_attachment = false;
        this.condition = null;
    }

    /** returns the size of the content
     * @return the content length
     */
    public int length () {
        return content != null ? content.length () : 0;
    }

    /**
     * checks wether this and the other block are looking simular, e.g.
     * most of the contents is identical. this is a simple, heuristic
     * approach
     * @param other the block to check against
     * @return true if they look simular
     */
    public boolean looksLike (BlockData other) {
        boolean	st;
        
        st = false;
        if ((content != null) && (other.content != null)) {
            int	m, o;
            String	mcont, ocont;
            int	mlen, olen;
            char	mch, och;
            int	tolarate;

            m = 0;
            o = 0;
            mcont = content;
            ocont = other.content;
            mlen = mcont.length ();
            olen = ocont.length ();
//			tolarate = mlen / 4096;
            tolarate = 0;
            while ((m < mlen) && (o < olen) && (tolarate >= 0)) {
                mch = mcont.charAt (m);
                och = ocont.charAt (o);
                if (mch != och) {
                    --tolarate;
                    if ((o + 1 < olen) && (mch == ocont.charAt (o + 1))) {
                        ++o;
                    } else if ((m + 1 < mlen) && (och == mcont.charAt (m + 1))) {
                        ++m;
                    }
                }
                ++m;
                ++o;
            }
            if (tolarate >= 0) {
                if (m < mlen) {
                    tolarate -= mlen - m;
                }
                if (o < olen) {
                    tolarate -= olen - o;
                }
                if (tolarate >= 0) {
                    st = true;
                }
            }
        }
        return st;
    }
    
    /**
     * To make this class sortable
     * @param other the block to compare us to
     * @return the sort relation
     */
    public int compareTo (Object other) {
        BlockData	b = (BlockData) other;
        
        return type - b.type;
    }
}