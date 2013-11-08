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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.agnitas.util.Log;

/** Implements writing of mailing information to
 * a XML file
 */
public class MailWriterMeta extends MailWriter {
    /** Write a log entry to the database after that number of mails */
    private int     logSize;
    /** Reference to available tagnames */
    protected Hashtable tagNames;
    /** Base pathname without extension to write to */
    private String      fname;
    /** The pathname for the real XML file */
    private String      pathname;
    /** Output stream */
    private OutputStream    out;
    /** Output buffer */
    public StringBuffer buf;
    /** Counter to give each block written an unique ID */
    private int     blockID;
    /** if we should keep admin/test mails for debug purpose */
    public  boolean     keepATmails;
    /** after this number of records flush buffer to disk */
    public int      flushCount;

    /** Flush the created buffer to disk
     */
    private void flushBuffer () throws Exception {
        if (out == null)
            throw new Exception ("Try to flush buffer to not existing stream");
        if (buf.length () > 0) {
            int len = buf.length ();

            for (int n = 0; n < len; ) {
                char    ch = buf.charAt (n);

                if ((ch < ' ') && (ch != '\r') && (ch != '\n') && (ch != '\t')) {
                    buf.deleteCharAt (n);
                    --len;
                } else
                    ++n;
            }

            try {
                out.write (buf.toString ().getBytes ("UTF-8"));
            } catch (IOException e) {
                data.logging (Log.ERROR, "writer/meta", "Unable to flush output (disk full?): " + e);
                throw new Exception ("Unable to flush output: " + e);
            }
            buf.setLength (0);
        }
    }

    /** Escape a string to be XML conform
     * @param scratch the buffer to write to
     * @param str the source to convert
     */
    private void xml (StringBuffer scratch, String str) {
        if (str != null) {
            int len = str.length ();
            char    ch;

            for (int n = 0; n < len; ++n) {
                ch = str.charAt (n);
                switch (ch) {
                case 60:    // <
                    scratch.append ("&lt;");
                    break;
                case 62:    // >
                    scratch.append ("&gt;");
                    break;
                case 38:    // &
                    scratch.append ("&amp;");
                    break;
                case 39:    // '
                    scratch.append ("&apos;");
                    break;
                case 34:    // "
                    scratch.append ("&quot;");
                    break;
                default:
                    scratch.append (ch);
                    break;
                }
            }
        }
    }

    /** Escape a string into the output buffer
     * @param str the source to convert
     */
    public void xmlIt (String str) {
        xml (buf, str);
    }

    /** Escape a string and return the convert version
     * @param str the source to convert
     * @return the converted string
     */
    public String xmlStr (String str) {
        if (str != null) {
            StringBuffer    scratch = new StringBuffer (str.length () + 64);

            xml (scratch, str);
            return scratch.toString ();
        } else
            return "";
    }

    /** Encode a byte sequence using base64
     * @param cont the byte sequence
     */
    public void base64 (byte[] cont) {
        final String    code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        int     len;
        int     limit;
        int     count;
        int     i0, i1, i2;

        len = cont.length;
        limit = ((len + 2) / 3) * 3;
        count = 0;
        for (int n = 0; n < limit; n += 3) {
            if (count == 0)
                buf.append ('\n');
            i0 = cont[n] & 0xff;
            if (n + 1 < len) {
                i1 = cont[n + 1] & 0xff;
                if (n + 2 < len)
                    i2 = cont[n + 2] & 0xff;
                else
                    i2 = 0;
            } else
                i1 = i2 = 0;
            buf.append (code.charAt (i0 >>> 2));
            buf.append (code.charAt (((i0 & 0x3) << 4) | (i1 >>> 4)));
            if (n + 1 < len) {
                buf.append (code.charAt (((i1 & 0xf) << 2) | (i2 >>> 6)));
                if (n + 2 < len)
                    buf.append (code.charAt (i2 & 0x3f));
                else
                    buf.append ("=");
            } else
                buf.append ("==");
            count += 4;
            if (count >= 76)
                count = 0;
        }
        buf.append ('\n');
    }

    /** Start the mail generating backend
     * @param output detailed output description
     * @param filename pathname to the XML file
     */
    private void startXMLBack (Vector <String> options, String output, String filename) throws Exception {
        Vector <String> command = new Vector <String> ();
        String      cmd;
        File        efile;
        int     rc;

        try {
            efile = File.createTempFile ("error", null);
        } catch (Exception e) {
            String  javaTemp = System.getProperty ("java.io.tmpdir");
            data.logging (Log.ERROR, "write/meta", "Failed to create temp.file due to: " + e.toString () + " (missing temp.directory '" + javaTemp + "'?)");
            throw e;
        }

        command.add (data.xmlBack ());
        if (options != null) {
            for (int n = 0; n < options.size (); ++n)
                command.add (options.elementAt (n));
        }
        command.add ("-q");
        if (data.eol.equals ("\n")) {
            command.add ("-l");
        }
        command.add ("-E" + efile.getAbsolutePath ());
        command.add ("-o" + output);
        command.add (filename);
        cmd = command.toString ();
        data.markToRemove (efile);
        try {
            data.logging (Log.DEBUG, "write/meta", "Try to execute " + cmd);

            ProcessBuilder  bp = new ProcessBuilder (command);
            Process     proc = bp.start ();

            rc = proc.waitFor ();

            FileInputStream err;
            String      msg;
            int     size;

            try {
                err = new FileInputStream (efile);
            } catch (FileNotFoundException e) {
                err = null;
            }
            msg = null;
            size = 0;
            if (err != null) {
                size = err.available ();
                if (size > 0) {
                    int use = size > 4096 ? 4096 : size;
                    byte[]  buf = new byte[use];

                    err.read (buf);
                    msg = new String (buf);
                }
                err.close ();
            }
            if ((rc != 0) || (msg != null)) {
                data.logging (rc == 0 ? Log.INFO : Log.ERROR, "writer/meta", "command " + cmd + " returns " + rc + (msg != null ? ":\n" + msg : ""));
                if (rc != 0)
                    throw new Exception ("command returns " + rc + (msg != null ? " (" + size + ")" : ""));
            }
        } catch (Exception e) {
            data.logging (Log.ERROR, "writer/meta", "command " + cmd + " failed (Missing binary? Wrong permissions?): " + e);
            throw new Exception ("Execution of " + cmd + " failed: " + e);
        } finally {
            if (efile.delete ())
                data.unmarkToRemove (efile);
        }
    }

    /** Constructor
     * @param data Reference to configuration
     * @param allBlocks all content blocks
     * @param nTagNames all tag definitions
     */
    public MailWriterMeta (Data data, BlockCollection allBlocks, Hashtable nTagNames) throws Exception {
        super (data, allBlocks);
        logSize = data.mailLogNumber ();
        tagNames = nTagNames;
        fname = null;
        pathname = null;
        out = null;
        buf = new StringBuffer ();
        if (data.isAdminMailing () || data.isTestMailing () || data.isCampaignMailing () || data.isPreviewMailing ())
            blockSize = 0;
        else
            blockSize = data.blockSize ();
        blockID = 1;
        keepATmails = false;
        flushCount = 100;
    }

    /** Create xmlback generation string
     * @return the newly formed string
     */
    public String generateOutputOptions () {
        return "generate:temporary=true;syslog=false;account-logfile=" + data.accLogfile () + ";bounce-logfile=" + data.bncLogfile () + ";media=email;path=" + data.mailDir ();
    }
    public String previewOutputOptions (String output) {
        return "preview:path=" + output;
    }
    public void previewOptions (Vector <String> options) {
        options.add ("-r");
    }

    /** Cleanup
     */
    public void done () throws Exception {
        super.done ();
        if (data.isAdminMailing () || data.isTestMailing ()) {
            if (pathname != null) {
                if (! keepATmails) {
                    data.markToRemove (pathname);
                }

                String  gen = generateOutputOptions ();

                startXMLBack (null, gen, pathname);
                if (! keepATmails) {
                    if ((new File (pathname)).delete ()) {
                        data.unmarkToRemove (pathname);
                    }
                }
            }
        } else if (data.isPreviewMailing ()) {
            if (pathname != null) {
                File    output = File.createTempFile ("preview", ".xml");
                String  path = output.getAbsolutePath ();
                String  opts = previewOutputOptions (path);
                String  error = null;

                data.markToRemove (pathname);
                data.markToRemove (path);
                try {
                    Vector <String> options = new Vector <String> ();

                    previewOptions (options);
                    startXMLBack (options, opts, pathname);
                } catch (Exception e) {
                    error = e.toString ();
                }
                if (data.previewOutput != null) {
                    try {
                        DocumentBuilderFactory
                                docBuilderFactory = DocumentBuilderFactory.newInstance ();
                        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder ();
                        Document    doc = docBuilder.parse (output);
                        Element     root = doc.getDocumentElement ();
                        NodeList    nlist = root.getElementsByTagName ("content");
                        int     ncount = nlist.getLength ();

                        for (int n = 0; n < ncount; ++n) {
                            Node        node = nlist.item (n);
                            NamedNodeMap    attr = node.getAttributes ();
                            Node        name = attr.getNamedItem ("name");

                            if (name != null) {
                                Node    text = node.getFirstChild ();

                                data.previewOutput.addContent (name.getNodeValue (), (text == null ? "" : text.getNodeValue ()));
                            }
                        }
                    } catch (Exception e) {
                        if (error != null)
                            error += "\n" + e.toString ();
                        else
                            error = e.toString ();
                    }
                    if (error != null)
                        data.previewOutput.setError (error);
                }
                if ((new File (path)).delete ()) {
                    data.unmarkToRemove (path);
                }
                if ((new File (pathname)).delete ()) {
                    data.unmarkToRemove (pathname);
                }
            }
        } else if (fname != null)
            try {
                FileOutputStream    temp;
                String          msg;

                msg = data.company_id + "-" + data.mailing_id + "-" + blockCount + "\t" +
                      "Start: " + startExecutionTime + "\tEnd: " + endExecutionTime + "\n";
                temp = new FileOutputStream (fname + ".final");
                temp.write (msg.getBytes ());
                temp.close ();
            } catch (FileNotFoundException e) {
                throw new Exception ("Unable to write final stamp file " + fname + ".final: " + e);
            }
    }

    /** Get encoding for block
     * @param b the block to examine
     * @return the encoding for this block
     */
    public String getEncoding (Object ob) {
        BlockData b = (BlockData) ob;
        String  encode;

        if (b.is_text) {
            if (b.media == Media.TYPE_EMAIL)
                encode = data.encoding;
            else
                encode = "none";
        } else {
            encode = "base64";
        }
        return encode;
    }

    /** Write entry part for a single block
     * @param indent indention to format output
     * @param b the block to write
     * @param encode the encoding for this block
     */
    public void emitBlockEntry (String indent, Object ob, String encode) {
        BlockData b = (BlockData) ob;
        String  cid;
        String  flag;

        if (b.mime != null)
            buf.append (" mimetype=\"" + xmlStr (b.getContentMime ()) + "\"");
        buf.append (" charset=\"" + xmlStr (data.charset) + "\"");
        buf.append (" encode=\"" + xmlStr (encode) + "\"");
        cid = b.getContentFilename ();
        if (cid != null)
            buf.append (" cid=\"" + xmlStr (cid) + "\"");
        if (b.is_parseable)
            flag = "is_parsable";
        else if (b.is_text)
            flag = "is_text";
        else
            flag = "is_binary";
        buf.append (" " + flag + "=\"true\"");
        if (b.is_attachment)
            buf.append (" is_attachment=\"true\"");
        if (b.media != Media.TYPE_UNRELATED)
            buf.append (" media=\"" + b.mediaType () + "\"");
        if (b.condition != null)
            buf.append (" condition=\"" + xmlStr (b.condition) + "\"");
    }

    /** Write content part for a single block
     * @param indent indention to format output
     * @param b the block to write
     */
    public void emitBlockContent (String indent, Object ob) {
        BlockData b = (BlockData) ob;
        String  content;

        if (b.content != null)
            content = b.content;
        else if ((! b.is_parseable) && (b.parsed_content != null))
            content = b.parsed_content;
        else if (b.binary == null)
            content = "";
        else
            content = null;
        if ((content == null) || (content.length () > 0)) {
            buf.append (indent + " <content>");
            if (content == null)
                base64 (b.binary);
            else
                xmlIt (content);
            buf.append ("</content>\n");
        } else
            buf.append (indent + " <content/>\n");
    }

    /** Write blocks tag information
     * @param indent indention to format output
     * param b the block to write
     */
    public void emitBlockTags (String indent, BlockData b, int isHeader) {
        if (b.is_parseable && (b.tag_position != null)) {
            Vector  p = b.tag_position;
            int count = p.size ();

            for (int m = 0; m < count; ++m) {
                TagPos  tp = (TagPos) p.elementAt (m);
                int type;

                buf.append (indent + " <tagposition name=\"" + xmlStr (tp.tagname) + "\" " +
                        "hash=\"" + tp.tagname.hashCode () + "\"");
                type = 0;
                if (tp.isDynamic ())
                    type |= 0x1;
                if (tp.isDynamicValue ())
                    type |= 0x2;
                if (type != 0)
                    buf.append (" type=\"" + type + "\"");
                if (tp.content != null) {
                    buf.append (">\n");
                    emitBlock (indent + " ", tp.content, (isHeader != 0 ? isHeader + 1 : 0), 0);
                    buf.append ("</tagposition>\n");
                } else
                    buf.append ("/>\n");
            }
        }
    }

    /** Write a single block
     * @param indent indention to format output
     * @param b the block to write
     * @param isHeader if this is the header block
     * @param index the unique block number
     */
    private void emitBlock (String indent, BlockData b, int isHeader, int index) {
        String      encode;

        if (isHeader != 0) {
            encode = "header";
        } else {
            encode = getEncoding (b);
        }
        buf.append (indent + "<block id=\"" + blockID++ + "\" nr=\"" + index + "\"");
        emitBlockEntry (indent, b, encode);
        buf.append (">\n");
        emitBlockContent (indent, b);
        emitBlockTags (indent, b, isHeader);
        buf.append (indent + "</block>\n");
    }

    /** Write description entity
     * @param indent indention to format output
     */
    public void emitDescription (String indent) {
        if (data.companyInfo != null) {
            buf.append (indent + "<company id=\"" + data.company_id + "\">\n");
            for (Enumeration e = data.companyInfo.keys (); e.hasMoreElements (); ) {
                String  name = (String) e.nextElement ();
                String  value = data.companyInfo.get (name);

                buf.append (indent + " <info name=\"" + xmlStr (name) + "\">" + xmlStr (value) + "</info>\n");
            }
            buf.append (indent + "</company>\n");
        } else
            buf.append (indent + "<company id=\"" + data.company_id + "\"/>\n");
        buf.append (indent + "<mailinglist id=\"" + data.mailinglist_id + "\"/>\n" +
                indent + "<mailing id=\"" + data.mailing_id + "\" name=\"" + xmlStr (data.mailing_name) + "\"/>\n" +
                indent + "<maildrop status_id=\"" + data.maildrop_status_id +"\"/>\n" +
                indent + "<status field=\"" + xmlStr (data.status_field) + "\"/>\n");
    }

    /** Get transfer encoding
     * @param b the block to emit
     * @return the encoding
     */
    public String getTransferEncoding (Object ob) {
        BlockData b = (BlockData) ob;

        return b.is_text ? xmlStr (data.encoding) : "base64";
    }

    public String getDynamicInfo (Object od) {
        return "";
    }

    public String generalURLs () {
        return "  <profile_url>" + xmlStr (data.profileURL) + "</profile_url>\n" +
               "  <unsubscribe_url>" + xmlStr (data.unsubscribeURL) + "</unsubscribe_url>\n" +
               "  <auto_url>" + xmlStr (data.autoURL) + "</auto_url>\n" +
               "  <onepixel_url>" + xmlStr (data.onePixelURL) + "</onepixel_url>\n";
    }

    public String secrets () {
        return "  <password>" + xmlStr (data.password) + "</password>\n";
    }

    public String layout () {
        StringBuffer temp = new StringBuffer ();

        if (data.lusecount > 0) {
            temp.append (" <layout count=\"" + data.lusecount + "\">\n");
            for (int n = 0; n < data.lcount; ++n) {
                Column c = data.columnByIndex (n);

                if (c.inUse ()) {
                    temp.append ("  <element name=\"");
                    temp.append (xmlStr (c.name));
                    temp.append ("\"");
                    if (c.ref != null) {
                        temp.append (" ref=\"");
                        temp.append (xmlStr (c.ref));
                        temp.append ("\"");
                    }
                    temp.append (" type=\"");
                    temp.append (c.typeStr ());
                    temp.append ("\"/>\n");
                }
            }
            temp.append (" </layout>\n\n");
        } else
            temp.append (" <!-- no layout -->\n\n");
        return temp.toString ();
    }

    /** Start writing a new block
     */
    public void startBlock () throws Exception {
        super.startBlock ();
        fname = data.metaDir () + dirSeparator + filenamePattern;
        if (data.isAdminMailing () || data.isTestMailing ()) {
            pathname = fname + ".xml";
            out = new FileOutputStream (pathname);
        } else {
            pathname = fname + ".xml.gz";
            out = new GZIPOutputStream (new FileOutputStream (pathname));
        }
        buf.append ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buf.append ("<blockmail>\n");
        buf.append (" <description>\n");
        emitDescription ("  ");
        buf.append (" </description>\n\n");
        buf.append (" <general>\n" +
                "  <subject>" + xmlStr (data.subject) + "</subject>\n" +
                "  <from_email>" + xmlStr (data.fromEmail == null ? null : data.fromEmail.full) + "</from_email>\n" +
                generalURLs () +
                secrets () +
                "  <total_subscribers>" + data.totalSubscribers + "</total_subscribers>\n" +
                " </general>\n" +
                "\n" +
                " <mailcreation>\n" +
                "  <blocknr>" + blockCount + "</blocknr>\n" +
                "  <innerboundary>" + xmlStr (innerBoundary) + "</innerboundary>\n" +
                "  <outerboundary>" + xmlStr (outerBoundary) + "</outerboundary>\n" +
                "  <attachboundary>" + xmlStr (attachBoundary) + "</attachboundary>\n" +
                " </mailcreation>\n" +
                "\n");

        int mediasize;
        Media   tmp;

        for (tmp = data.media, mediasize = 0; tmp != null; tmp = (Media) tmp.next)
            ++mediasize;

        buf.append (" <mediatypes count=\"" + mediasize + "\"");
        if (mediasize > 0) {
            buf.append (">\n");
            for (tmp = data.media; tmp != null; tmp = (Media) tmp.next) {
                buf.append ("  <media type=\"" + xmlStr (tmp.typeName ()) + "\" priority=\"" + xmlStr (tmp.priorityName ()) + "\" status=\"" + xmlStr (tmp.statusName ()) + "\"");

                Vector  vars = tmp.getParameterVariables ();
                if ((vars != null) && (vars.size () > 0)) {
                    buf.append (">\n");
                    for (int m = 0; m < vars.size (); ++m) {
                        String  name = (String) vars.elementAt (m);
                        buf.append ("   <variable name=\"" + name + "\"");

                        Vector  vals = tmp.findParameterValues (name);
                        if ((vals != null) && (vals.size () > 0)) {
                            buf.append (">\n");
                            for (int o = 0; o < vals.size (); ++o) {
                                String  value = (String) vals.elementAt (o);

                                buf.append ("    <value>" + xmlStr (value) + "</value>\n");
                            }
                            buf.append ("   </variable>\n");
                        } else
                            buf.append ("/>\n");
                    }
                    buf.append ("  </media>\n");
                } else
                    buf.append ("/>\n");
            }
            buf.append (" </mediatypes>\n");
        } else
            buf.append ("/>\n");
        buf.append ("\n");

        buf.append (" <blocks count=\"" + allBlocks.totalNumber + "\">\n");
        for (int n = 0; n < allBlocks.totalNumber; ++n) {
            BlockData   b = allBlocks.getBlock (n);

            emitBlock ("  ", b, (b.type == BlockData.HEADER ? 1 : 0), n);
        }
        buf.append (" </blocks>\n" +
                "\n");

        buf.append (" <types count=\"3\">\n");
        for (int n = 0; n < 3; ++n) {
            int end;

            buf.append ("  <type mailtype=\"" + n + "\">\n");
            switch (n) {
            case 0:
                end = 2;
                break;
            case 1:
                end = 3;
                break;
            case 2:
                end = allBlocks.totalNumber;
                break;
            default:
                end = 0;
                break;
            }
            if (end > allBlocks.totalNumber)
                end = allBlocks.totalNumber;

            Vector <BlockData>
                use = new Vector <BlockData> ();
            int used, part;
            int pos;

            for (pos = 0; pos < end; ++pos)
                use.add (allBlocks.getBlock (pos));
            while (pos < allBlocks.totalNumber) {
                BlockData   b = allBlocks.getBlock (pos);

                if ((b.type == BlockData.ATTACHMENT_TEXT) || (b.type == BlockData.ATTACHMENT_BINARY))
                    use.add (b);
                ++pos;
            }
            used = use.size ();
            part = used;
            for (int m = 0; m < used; ++m) {
                BlockData   b = use.elementAt (m);

                if ((b.type != BlockData.ATTACHMENT_TEXT) && (b.type != BlockData.ATTACHMENT_BINARY))
                    part = m;
            }

            for (int m = 0; m < used; ++m) {
                BlockData   b = use.elementAt (m);

                buf.append ("   <blockspec nr=\"" + b.id + "\"");
                if ((b.id == 1) && (data.lineLength > 0))
                    buf.append (" linelength=\"" + data.lineLength + "\"");
                else if ((b.id == 2) && (data.onepixlog != Data.OPL_NONE)) {
                    String  opl;

                    switch (data.onepixlog) {
                    default:
                        opl = null;
                        break;
                    case Data.OPL_TOP:
                        opl = "top";
                        break;
                    case Data.OPL_BOTTOM:
                        opl = "bottom";
                        break;
                    }
                    if (opl != null)
                        buf.append (" onepixlog=\"" + opl + "\"");
                }
                buf.append (">\n");
                if (b.id == 0) {    // block zero: header
                    buf.append ("    <postfix output=\"0\">\n");
                    buf.append ("     <fixdata valid=\"simple\">");
                    if (n == 0)     // simple text mail
                        buf.append ("HContent-Type: text/plain; charset=\"" + xmlStr (data.charset) + "\"" + data.eol +
                                "HContent-Transfer-Encoding: " + xmlStr (data.encoding) + data.eol);
                    else if (n == 1)    // online HTML
                        buf.append ("HContent-Type: multipart/alternative;" + data.eol +
                                "\tboundary=\"" + xmlStr (outerBoundary) + "\"" + data.eol);
                    else            // offline HTML
//                      buf.append ("HContent-Type: multipart/related; type=\"multipart/alternative\";" + data.eol +
//                                 "\tboundary=\"" + xmlStr (outerBoundary) + "\"" + data.eol);
                        buf.append ("HContent-Type: multipart/related;" + data.eol +
                                "\tboundary=\"" + xmlStr (outerBoundary) + "\"" + data.eol);
                    buf.append ("." + data.eol);
                    buf.append ("</fixdata>\n");
                    buf.append ("     <fixdata valid=\"attach\">");
                    buf.append ("HContent-Type: multipart/mixed; boundary=\"" + xmlStr (attachBoundary) +"\"" + data.eol +
                            "." + data.eol);
                    buf.append ("</fixdata>\n");
                    buf.append ("    </postfix>\n");
                } else if (b.id == 1) { // text part
                    buf.append ("    <prefix>\n");
                    if (n > 0) {
                        buf.append ("     <fixdata valid=\"simple\">");
                        buf.append ("This is a multi-part message in MIME format." + data.eol +
                                data.eol +
                                "--" + xmlStr (outerBoundary) + data.eol);
                        if (n == 2)
                            buf.append ("Content-Type: multipart/alternative;" + data.eol +
                                    "        boundary=\"" + xmlStr (innerBoundary) + "\"" + data.eol +
                                    data.eol +
                                    "--" + xmlStr (innerBoundary) + data.eol);
                        buf.append ("Content-Type: text/plain; charset=\"" + xmlStr (data.charset) + "\"" + data.eol +
                                "Content-Transfer-Encoding: " + xmlStr (data.encoding) + data.eol +
                                data.eol);
                        buf.append ("</fixdata>\n");
                    }
                    buf.append ("     <fixdata valid=\"attach\">");
                    buf.append ("--" + xmlStr (attachBoundary) + data.eol);
                    if (n == 0)
                        buf.append ("Content-Type: text/plain; charset=\"" + xmlStr (data.charset) + "\"" + data.eol +
                                "Content-Transfer-Encoding: " + xmlStr (data.encoding) + data.eol +
                                data.eol);
                    else if (n == 1)
                        buf.append ("Content-Type: multipart/alternative;" + data.eol +
                                "\tboundary=\"" + xmlStr (outerBoundary) + "\"" + data.eol +
                                data.eol);
                    else
                        buf.append ("Content-Type: multipart/related;" + data.eol +
                                "\tboundary=\"" + xmlStr (outerBoundary) + "\"" + data.eol +
                                data.eol);
                    if (n > 0) {
                        buf.append ("--" + xmlStr (outerBoundary) + data.eol);
                        if (n == 2)
                            buf.append ("Content-Type: multipart/alternative;" + data.eol +
                                    "        boundary=\"" + xmlStr (innerBoundary) + "\"" + data.eol +
                                    data.eol +
                                    "--" + xmlStr (innerBoundary) + data.eol);
                        buf.append ("Content-Type: text/plain; charset=\"" + xmlStr (data.charset) + "\"" + data.eol +
                                "Content-Transfer-Encoding: " + xmlStr (data.encoding) + data.eol +
                                data.eol);
                    }
                    buf.append ("</fixdata>\n");
                    buf.append ("    </prefix>\n");
                    if (n == 2) {
                        buf.append ("    <postfix output=\"2\" pid=\"inner\">\n");
                        buf.append ("     <fixdata valid=\"all\">");
                        buf.append ("--" + xmlStr (innerBoundary) + "--" + data.eol +
                                data.eol);
                        buf.append ("</fixdata>\n");
                        buf.append ("    </postfix>\n");
                    }
                    if (n > 0) {
                        buf.append ("    <postfix output=\"" + part + "\" pid=\"outer\">\n");
                        buf.append ("     <fixdata valid=\"all\">");
                        buf.append ("--" + xmlStr (outerBoundary) + "--" + data.eol +
                                data.eol);
                        buf.append ("</fixdata>\n");
                        buf.append ("    </postfix>\n");
                    }
                    buf.append ("    <postfix output=\"" + allBlocks.totalNumber + "\" pid=\"attach\">\n");
                    buf.append ("     <fixdata valid=\"attach\">");
                    buf.append ("--" + xmlStr (attachBoundary) + "--" + data.eol +
                            data.eol);
                    buf.append ("</fixdata>\n");
                    buf.append ("    </postfix>\n");
                } else if (b.id == 2) { // html part
                    buf.append ("    <prefix>\n");
                    buf.append ("     <fixdata valid=\"all\">");
                    if (n == 1)
                        buf.append ("--" + xmlStr (outerBoundary) + data.eol);
                    else
                        buf.append ("--" + xmlStr (innerBoundary) + data.eol);
                    buf.append ("Content-Type: " + xmlStr (b.getContentMime ()) + "; charset=\"" + xmlStr (data.charset) + "\"" + data.eol +
                            "Content-Transfer-Encoding: " + getTransferEncoding (b) + data.eol +
                            data.eol);
                    buf.append ("</fixdata>\n");
                    buf.append ("    </prefix>\n");
                } else {        // offline + attachments
                    buf.append ("    <prefix>\n");
                    if ((b.type == BlockData.ATTACHMENT_TEXT) || (b.type == BlockData.ATTACHMENT_BINARY)) {
                        buf.append ("     <fixdata valid=\"attach\">");
                        buf.append ("--" + xmlStr (attachBoundary) + data.eol +
                                "Content-Type: " + xmlStr (b.getContentMime ()) + data.eol +
                                "Content-Disposition: attachment; filename=\"" + xmlStr (b.getContentFilename ()) + "\"" + data.eol +
                                "Content-Transfer-Encoding: " + getTransferEncoding (b) + data.eol +
                                data.eol);
                        buf.append ("</fixdata>\n");
                    } else {
                        buf.append ("     <fixdata valid=\"all\">");
                        buf.append ("--" + xmlStr (outerBoundary) + data.eol +
                                "Content-Type: " + xmlStr (b.getContentMime ()) + data.eol +
                                "Content-Transfer-Encoding: " + getTransferEncoding (b) + data.eol +
                                "Content-Location: " + xmlStr (b.getContentFilename ()) + data.eol +
                                data.eol);
                        buf.append ("</fixdata>\n");
                    }
                    buf.append ("    </prefix>\n");
                    if ((b.type == BlockData.ATTACHMENT_TEXT) || (b.type == BlockData.ATTACHMENT_BINARY)) {
                        buf.append ("    <postfix output=\"" + allBlocks.totalNumber + "\" pid=\"attach\">\n");
                        buf.append ("     <fixdata valid=\"attach\">");
                        buf.append ("--" + xmlStr (attachBoundary) + "--" + data.eol +
                                data.eol);
                        buf.append ("</fixdata>\n");
                        buf.append ("    </postfix>\n");
                    } else {
                        buf.append ("    <postfix output=\"" + part + "\" pid=\"outer\">\n");
                        buf.append ("     <fixdata valid=\"all\">");
                        buf.append ("--" + xmlStr (outerBoundary) + "--" + data.eol +
                                data.eol);
                        buf.append ("</fixdata>\n");
                        buf.append ("    </postfix>\n");
                    }
                }
                buf.append ("   </blockspec>\n");
            }
            buf.append ("  </type>\n");
        }
        buf.append (" </types>\n" +
                "\n");

        buf.append (layout ());

        boolean found;

        found = false;
        for (Enumeration e = tagNames.elements (); e.hasMoreElements (); ) {
            EMMTag  tag = (EMMTag) e.nextElement ();

            if (! found) {
                buf.append (" <taglist count=\"" + tagNames.size () + "\">\n");
                found = true;
            }
            buf.append ("  <tag name=\"" + xmlStr (tag.mTagFullname) + "\" hash=\"" + tag.mTagFullname.hashCode () + "\"/>\n");
        }
        if (found)
            buf.append (" </taglist>\n" +
                    "\n");
        else
            buf.append (" <!-- no taglist -->\n" +
                    "\n");

        found = false;
        for (Enumeration e = tagNames.elements (); e.hasMoreElements (); ) {
            EMMTag  tag = (EMMTag) e.nextElement ();
            String  ttype = tag.getType ();
            String  value;

            switch (tag.tagType) {
            case EMMTag.TAG_INTERNAL:
                if ((tag.fixedValue || tag.globalValue) && ((value = tag.makeInternalValue (data, null)) != null)) {
                    if (! found) {
                        buf.append (" <global_tags>\n");
                        found = true;
                    }
                    buf.append ("  <tag name=\"" + xmlStr (tag.mTagFullname) + "\" " +
                            "hash=\"" + tag.mTagFullname.hashCode () + "\"" +
                            (ttype == null ? "" : " type=\"" + ttype + "\"") +
                   (value == null ? "/>\n" : ">" + xmlStr (value) + "</tag>\n"));
                }
                break;
            }
        }
        if (found)
            buf.append (" </global_tags>\n" +
                    "\n");
        else
            buf.append (" <!-- no global_tags -->\n" +
                    "\n");

        if ((allBlocks.dynContent != null) && (allBlocks.dynContent.ncount > 0)) {
            buf.append (" <dynamics count=\"" + allBlocks.dynContent.ncount + "\">\n");
            for (Enumeration e = allBlocks.dynContent.names.elements (); e.hasMoreElements (); ) {
                DynName dtmp = (DynName) e.nextElement ();

                buf.append ("  <dynamic id=\"" + dtmp.id + "\" name=\"" + xmlStr (dtmp.name) + "\"" + getDynamicInfo (dtmp) + ">\n");
                for (int n = 0; n < dtmp.clen; ++n) {
                    DynCont cont = dtmp.content.elementAt (n);

                    if (cont.targetID != DynCont.MATCH_NEVER) {
                        buf.append ("   <dyncont id=\"" + cont.id + "\" order=\"" + cont.order + "\"");
                        if ((cont.targetID != DynCont.MATCH_ALWAYS) && (cont.condition != null))
                            buf.append (" condition=\"" + xmlStr (cont.condition) + "\"");
                        buf.append (">\n");
                        if (cont.text != null)
                            emitBlock ("    ", cont.text, 0, 0);
                        if (cont.html != null)
                            emitBlock ("    ", cont.html, 0, 1);
                        buf.append ("   </dyncont>\n");
                    }
                }
                buf.append ("  </dynamic>\n");
            }
            buf.append (" </dynamics>\n" +
                    "\n");
        } else
            buf.append (" <!-- no dynamics -->\n" +
                    "\n");

        if (data.urlcount > 0) {
            buf.append (" <urls count=\"" + data.urlcount + "\">\n");
            for (int n = 0; n < data.urlcount; ++n) {
                URL url = data.URLlist.elementAt (n);

                buf.append ("  <url id=\"" + url.id + "\" " +
                        "destination=\"" + xmlStr (url.url) + "\" " +
                        "usage=\"" + url.usage + "\"/>\n");
            }
            buf.append (" </urls>\n" +
                    "\n");
        } else
            buf.append (" <!-- no urls -->\n" +
                    "\n");
        buf.append (" <receivers>\n");
    }

    /** Finalize a block
     */
    public void endBlock () throws Exception {
        super.endBlock ();
        buf.append (" </receivers>\n" +
                "\n");
        buf.append ("</blockmail>\n");
        if (out != null) {
            flushBuffer ();
            out.close ();
            out = null;

            if (data.xmlValidate ()) {
                data.logging (Log.INFO, "writer/meta", "Validating XML output");
                startXMLBack (null, "none", pathname);
                data.logging (Log.INFO, "writer/meta", "Validation done");
            } else
                data.logging (Log.INFO, "writer/meta", "Skip validation of XML document");

            if (! (data.isAdminMailing () || data.isTestMailing () || data.isPreviewMailing ()))
                try {
                    FileOutputStream    temp;
                    String          msg;

                    msg = data.company_id + "-" + data.mailing_id + "-" + blockCount + "\t" +
                          "Start: " + startBlockTime + "\tEnd: " + endBlockTime + "\n";
                    temp = new FileOutputStream (fname + ".stamp");
                    temp.write (msg.getBytes ());
                    temp.close ();
                } catch (FileNotFoundException e) {
                    throw new Exception ("Unable to write stamp file " + fname + ".stamp: " + e);
                }
        }
    }

    /** Create string for media informations
     * @param cinfo information about this customer
     * @return the media string
     */
    public String getMediaInformation (Object cinfop) {
        Custinfo cinfo = (Custinfo) cinfop;
        return cinfo.email == null ? "" : "to_email=\"" + xmlStr (cinfo.email) + "\" ";
    }

    /** Write a single receiver record
     * @param cinfo Information about the customer
     * @param mcount if more than one mail is written for this receiver
     * @param mailtype the mailtype for this receiver
     * @param icustomer_id the customer ID
     * @param tag_names the available tags
     * @param urlMaker to create the URLs
     */
    public void writeMail (Custinfo cinfo,
                   int mcount, int mailtype, long icustomer_id,
                   String mediatypes, Hashtable tag_names, URLMaker urlMaker) throws Exception {
        super.writeMail (cinfo, mcount, mailtype, icustomer_id, mediatypes, tag_names, urlMaker);
        if ((mailCount % flushCount) == 0) {
            flushBuffer ();
        }
        if ((mailCount % 100) == 0) {
            data.logging (Log.VERBOSE, "writer/meta", "Currently at " + mailCount + " mails (in block " + blockCount + ": " + inBlockCount + ") ");
        }
        if (billingCounter != null)
            if ((logSize > 0) && ((mailCount % logSize) == 0))
                billingCounter.update_log (mailCount);

        buf.append ("  <receiver customer_id=\"" + icustomer_id + "\" " +
                "user_type=\"" + xmlStr (cinfo.usertype) + "\" " +
                getMediaInformation (cinfo) +
                "message_id=\"" + xmlStr (messageID) + "\" " +
                "mailtype=\"" + mailtype + "\"");
        if (mediatypes != null)
            buf.append (" mediatypes=\"" + mediatypes + "\"");
        buf.append (">\n");
        buf.append ("   <tags>\n");

        for (Enumeration e = tag_names.elements (); e.hasMoreElements (); ) {
            EMMTag  tag = (EMMTag) e.nextElement ();
            String  value;

            switch (tag.tagType) {
            case EMMTag.TAG_DBASE:
                if (tag.mutableValue)
                    value = tag.makeMutableValue (data, cinfo);
                else if (! (tag.fixedValue || tag.globalValue))
                    value = tag.mTagValue;
                else
                    value = null;
                break;
            case EMMTag.TAG_URL:
                value = allBlocks.create_url_tag (tag, urlMaker);
                break;
            case EMMTag.TAG_INTERNAL:
                if (! (tag.fixedValue || tag.globalValue))
                    value = tag.makeInternalValue (data, cinfo);
                else
                    value = null;
                break;
            default:
                throw new Exception ("Invalid tag type: " + tag.toString ());
            }
            if (value != null)
                buf.append ("    <tag name=\"" + xmlStr (tag.mTagFullname) + "\" " +
                        "hash=\"" + tag.mTagFullname.hashCode () + "\">" +
                        xmlStr (value) + "</tag>\n");
        }
        buf.append ("   </tags>\n");
        if ((data.urlcount > 0) && (data.generateCodedURLs ())) {
            for (int n = 0; n < data.urlcount; ++n) {
                URL url = data.URLlist.elementAt (n);

                buf.append ("   <codedurl id=\"" + url.id + "\" " +
                        "destination=\"" +
                        xmlStr (urlMaker.autoURL (url.id)) +
                        "\"/>\n");
            }
        }
        if (data.lusecount > 0) {
            for (int n = 0; n < data.lcount; ++n) {
                if (data.columnUse (n)) {
                    buf.append ("   <data");
                    if (data.columnIsNull (n))
                        buf.append (" null=\"true\"");
                    buf.append (">" + xmlStr (data.columnGetStr (n)) + "</data>\n");
                }
            }
        }
        buf.append ("  </receiver>\n");
        if (billingCounter != null)
            if (icustomer_id != 0)
                billingCounter.sadd (mailtype);
        writeMailDone ();
    }
}
