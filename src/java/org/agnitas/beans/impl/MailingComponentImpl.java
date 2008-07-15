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

/**
 *
 * @author  mhe
 * @version
 */

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;
import com.sun.mail.util.*;
import org.agnitas.beans.MailingComponent;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.protocol.*;
import org.agnitas.util.AgnUtils;

public class MailingComponentImpl implements MailingComponent {
    
    protected int id;
    protected String mimeType;
    protected String componentName;
    protected int mailingID;
    protected int companyID;
    protected int type;
    protected StringBuffer emmBlock;
    protected byte[] binaryBlock;
    
    /** Holds value of property targetID. */
    protected int targetID;
    
    public static final int TYPE_PERSONALIZED_ATTACHMENT = 4;
    public static final int TYPE_FONT = 6;
    
    /** Creates new MailingComponent */
    public MailingComponentImpl() {
        id=0;
        componentName=null;
        mimeType=new String(" ");
        mailingID=0;
        companyID=0;
        type=TYPE_IMAGE;
        emmBlock=null;
        targetID=0;
    }
    
    public void setComponentName(String cid) {
        if(cid!=null) {
            componentName=new String(cid);
        } else {
            componentName=new String("");
        }
    }
    
    public void setType(int cid) {
        type=cid;
        if((type!=TYPE_IMAGE) && (type!=TYPE_TEMPLATE) && (type!=TYPE_ATTACHMENT) && (type!=TYPE_PERSONALIZED_ATTACHMENT) && (type!=TYPE_HOSTED_IMAGE) && (type!=TYPE_FONT)) {
            type=TYPE_IMAGE;
        }
    }
    
    public void setMimeType(String cid) {
        if(cid!=null) {
            mimeType=new String(cid);
        } else {
            mimeType=new String("");
        }
    }
    
    public void setId(int cid) {
        id=cid;
    }
    
    public String getComponentName() {
        if(componentName!=null) {
            return componentName;
        }
        
        return new String("");
    }
    
    public void setMailingID(int cid) {
        mailingID=cid;
    }
    
    public void setCompanyID(int cid) {
        companyID=cid;
    }
    
    public void setEmmBlock(String cid) {
        emmBlock=new StringBuffer(cid);
    }
    
    public void setBinaryBlock(byte[] cid) {
        binaryBlock=cid;
    }
    
    public boolean loadContentFromURL() {
        URL aUrl=null;
        URLConnection aConnection=null;
        // String type=null;
        int len=0;
        DataInputStream in=null;
        byte[] bytes=null;
        String emm=null;
        boolean returnValue=true;
        
        // return false;
        
        if((type!=TYPE_IMAGE) && (type!=TYPE_ATTACHMENT)) {
            return false;
        }
        
        try {
            HttpClient client=new HttpClient();
            
            GetMethod get = new GetMethod(componentName);
            get.setFollowRedirects(true);
            client.getHttpConnectionManager().getConnection(get.getHostConfiguration()).setConnectionTimeout(5000);
            
            if(client.executeMethod(get)==200) {
                if(!get.getResponseHeader("Content-Length").getValue().equals("0")) {
                    this.binaryBlock=get.getResponseBody();
                    setEmmBlock(makeEMMBlock());
                    mimeType=get.getResponseHeader("Content-Type").getValue();
                }
            }
        } catch ( Exception e) {
            AgnUtils.logger().error("loadContentFromURL: " + e.getMessage());
            returnValue=false;
        }
        AgnUtils.logger().info("loadContentFromURL: loaded "+componentName);
        return returnValue;
    }
    
    public String makeEMMBlock() {
        ByteArrayOutputStream baos=null;
        if(type==TYPE_TEMPLATE) {
            try {
                return new String(binaryBlock, "UTF8");
            } catch (Exception e) {
                AgnUtils.logger().error("makeEMMBlock: encoding error");
                return new String(" ");
            }
        } else {
            try {
                baos = new ByteArrayOutputStream();
                OutputStream dos = MimeUtility.encode(new DataOutputStream(baos), "base64");
                dos.write(binaryBlock);
                dos.flush();
                return baos.toString();
            } catch (Exception e) {
                return null;
            }
        }
    }
    
    public String getEmmBlock() {
        return new String(emmBlock);
    }
    
    public int getId() {
        return id;
    }
    
    public String getMimeType() {
        return new String(mimeType);
    }
    
    /** Getter for property targetID.
     * @return Value of property targetID.
     */
    public int getTargetID() {
        return this.targetID;
    }
    
    /** Setter for property targetID.
     * @param targetID New value of property targetID.
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
    
    /** Getter for property type.
     * @return Value of property type.
     */
    public int getType() {
        return this.type;
    }
    
    /** Getter for property binaryBlock.
     * @return Value of property binaryBlock.
     *
     */
    public byte[] getBinaryBlock() {
        return this.binaryBlock;
    }

    /**
     * Getter for property mailingID.
     * @return Value of property mailingID.
     */
    public int getMailingID() {
        return this.mailingID;
    }

    /**
     * Getter for property companyID.
     * @return Value of property companyID.
     */
    public int getCompanyID() {
        return this.companyID;
    }
    
}
