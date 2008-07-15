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

package org.agnitas.beans.impl;

import java.util.*;
import java.io.*;
import org.agnitas.beans.*;
import org.apache.commons.lang.*;
import org.agnitas.util.*;



/**
 *
 * @author  mhe
 */
public class MediatypeEmailImpl implements MediatypeEmail {
    
    /** Holds value of property subject. */
    protected String subject;
    
    /** Holds value of property linefeed. */
    protected int linefeed;
    
    /** Holds value of property mailFormat. */
    protected int mailFormat;
    
    /** Holds value of property charset. */
    protected String charset;
    
    /** Holds value of property fromAdr. */
    protected String fromAdr;
    
    /**
     * Complete Reply-To Address.
     */
    protected String replyAdr;
    
    /** Creates a new instance of MediaTypeEmail */
    public MediatypeEmailImpl() {
    }
     
    /** Getter for property subject.
     * @return Value of property subject.
     *
     */
    public String getSubject() {
        return this.subject;
    }
    
    /** Setter for property subject.
     * @param subject New value of property subject.
     *
     */
    public void setSubject(String subject) {
        this.subject = subject;
        this.paramValue();
    }
    
    /** Getter for property fromAdr.
     * @return Value of property fromAdr.
     *
     */
    public String getFromAdr() {
        return this.fromAdr;
    }
    
    /** Setter for property fromAdr.
     * @param fromAdr New value of property fromAdr.
     *
     */
    public void setFromAdr(String fromAdr) {
        this.fromAdr = fromAdr;
        this.paramValue();
    }
    
    /** Getter for property linefeed.
     * @return Value of property linefeed.
     *
     */
    public int getLinefeed() {
        return this.linefeed;
    }
    
    /** Setter for property linefeed.
     * @param linefeed New value of property linefeed.
     *
     */
    public void setLinefeed(int linefeed) {
        this.linefeed = linefeed;
        this.paramValue();
    }
    
    /** Getter for property mailFormat.
     * @return Value of property mailFormat.
     *
     */
    public int getMailFormat() {
        return this.mailFormat;
    }
    
    /** Setter for property mailFormat.
     * @param mailFormat New value of property mailFormat.
     *
     */
    public void setMailFormat(int mailFormat) {
        this.mailFormat = mailFormat;
        this.paramValue();
    }
    
    /** Getter for property charset.
     * @return Value of property charset.
     *
     */
    public String getCharset() {
        return this.charset;
    }
    
    /** Setter for property charset.
     * @param charset New value of property charset.
     *
     */
    public void setCharset(String charset) {
        this.charset = charset;
        this.paramValue();
    }
    
    public String paramValue() {
        StringBuffer result=new StringBuffer();
        
        result.append("from=\"");
        result.append(AgnUtils.propertySaveString(this.fromAdr));
        result.append("\", ");
        
        result.append("subject=\"");
        result.append(AgnUtils.propertySaveString(this.subject));
        result.append("\", ");
        
        result.append("charset=\"");
        result.append(AgnUtils.propertySaveString(this.charset));
        result.append("\", ");
        
        result.append("linefeed=\"");
        result.append(AgnUtils.propertySaveString(Integer.toString(this.linefeed)));
        result.append("\", ");
        
        result.append("mailformat=\"");
        result.append(AgnUtils.propertySaveString(Integer.toString(this.mailFormat)));
        result.append("\", ");
        
        result.append("reply=\"");
        result.append(AgnUtils.propertySaveString(this.replyAdr));
        result.append("\", ");
        
        result.append("onepixlog=\"");
        result.append(AgnUtils.propertySaveString(this.onepixel));
        result.append("\", ");
        
        this.param.setParam(result.toString());
        
        return result.toString();
    }
    
    public boolean parseParam() throws Exception {
        int tmp=0;
        
        String param=this.param.getParam();
        
        this.fromAdr=AgnUtils.findParam("from", param);
        
        this.replyAdr=AgnUtils.findParam("reply", param);
        if(this.replyAdr==null) {
            this.replyAdr=this.fromAdr;
        }
        
        this.charset=AgnUtils.findParam("charset", param);
        if(this.charset==null) {
            this.charset="ISO-8859-1";
        }
        this.subject=AgnUtils.findParam("subject", param);
        try {
            tmp=Integer.parseInt(AgnUtils.findParam("mailformat", param));
        } catch (Exception e) {
            tmp=2; // default: Offline-HTML
        }
        this.mailFormat=tmp;
        try {
            tmp=Integer.parseInt(AgnUtils.findParam("linefeed", param));
        } catch (Exception e) {
            tmp=72; // default: after 72 characters
        }
        this.linefeed=tmp;
        
        this.onepixel=AgnUtils.findParam("onepixlog", param);
        if(this.onepixel==null) {
            this.onepixel=MediatypeEmailImpl.ONEPIXEL_NONE;
        }
        
        return true;
    }
    
    /**
     * Getter for property replyAdr.
     * @return Value of property replyAdr.
     */
    public String getReplyAdr() {
        
        return this.replyAdr;
    }
    
    /**
     * Setter for property replyAdr.
     * @param replyAdr New value of property replyAdr.
     */
    public void setReplyAdr(String replyAdr) {
        
        this.replyAdr = replyAdr;
        this.paramValue();
    }

    /**
     * Holds value of property onepixel.
     */
    protected String onepixel = MediatypeEmailImpl.ONEPIXEL_NONE;

    /**
     * Getter for property onepixel.
     * @return Value of property onepixel.
     */
    public String getOnepixel() {

        return this.onepixel;
    }

    /**
     * Setter for property onepixel.
     * @param onepixel New value of property onepixel.
     */
    public void setOnepixel(String onepixel) {

        this.onepixel = onepixel;
        this.paramValue();
    }

    /**
     * Holds value of property param.
     */
    protected Mediatype param;

    /**
     * Getter for property param.
     * @return Value of property param.
     */
    public Mediatype getParam() {

        return this.param;
    }

    /**
     * Setter for property param.
     * @param param New value of property param.
     * @throws java.lang.Exception 
     */
    public void setParam(Mediatype param) throws Exception {

        this.param = param;
        
        this.parseParam();
    }

    /**
     * Holds value of property mailingID.
     */
    protected int mailingID;

    /**
     * Getter for property mailingID.
     * @return Value of property mailingID.
     */
    public int getMailingID() {

        return this.mailingID;
    }

    /**
     * Setter for property mailingID.
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID) {

        this.mailingID = mailingID;
    }

    
}
