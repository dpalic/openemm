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

package org.agnitas.web;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.TrackableLink;
import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 *
 * @author  mhe, Nicole Serek
 */
public class MailingWizardForm extends StrutsFormBase {
    
   
    private static final long serialVersionUID = 9104717555855628618L;

	/** Creates a new instance of TemplateForm */
    public MailingWizardForm() {
    }
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     * 
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     * @return errors
     */
    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();
        return errors;
    }

    /**
     * Holds value of property action.
     */
    private String action;

    /**
     * Getter for property action.
     *
     * @return Value of property action.
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Setter for property action.
     *
     * @param action New value of property action.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Holds value of property mailing.
     */
    private Mailing mailing;

    /**
     * Getter for property mailing.
     *
     * @return Value of property mailing.
     */
    public Mailing getMailing() {
        return this.mailing;
    }

    /**
     * Setter for property mailing.
     *
     * @param mailing New value of property mailing.
     */
    public void setMailing(Mailing mailing) {
        this.mailing = mailing;
    }

    /**
     * Holds value of property aktTracklinkID.
     */
    private Iterator tracklinkIterator=null;
    private TrackableLink tracklink=null;

    /**
     * Setter for property aktTracklinkID..
     */
    public boolean nextTracklink() {
        if(tracklinkIterator.hasNext()) {
            String id=(String) tracklinkIterator.next();

            tracklink=(TrackableLink) mailing.getTrackableLinks().get(id); 
            return true;
        }
        tracklink=null;
        return false;
    }

    /**
     * Getter for property linkUrl.
     *
     * @return Value of property linkUrl.
     */
    public String getLinkUrl() {
        if(tracklink != null) {
            return tracklink.getFullUrl();
        }
        return "";
    }

    /**
     * Setter for property linkUrl.
     */
    public void setLinkUrl(String linkURL) {
        if(tracklink != null) {
            tracklink.setFullUrl(linkURL);
        } else {
            AgnUtils.logger().error("setLinkUrl: Trying to set url for invalid tracklink");
        }
    }

    /**
     * Getter for property linkName.
     *
     * @return Value of property linkName.
     */
    public String getLinkName() {
        if(tracklink != null) {
            return tracklink.getShortname();
        }
        return "";
    }

    /**
     * Setter for property linkName.
     *
     * @param name New value of property linkName.
     */
    public void setLinkName(String name) {
        if(tracklink != null) {
            tracklink.setShortname(name);
        } else {
            AgnUtils.logger().error("setLinkName: Trying to set name for invalid tracklink");
        }
    }

    /**
     * Setter for property aktTracklinkID.
     */
    public void clearAktTracklink() {
        tracklinkIterator = mailing.getTrackableLinks().keySet().iterator();
System.err.println("Got links: "+tracklinkIterator);
System.err.println("Linklist: "+ mailing.getTrackableLinks());
    }

    /**
     * Holds value of property trackable.
     */
    private int trackable;

    /**
     * Getter for property trackable.
     *
     * @return Value of property trackable.
     */
    public int getTrackable() {
        return trackable;
    }

    /**
     * Setter for property trackable.
     */
    public void setTrackable(int trackable) {
        this.trackable = trackable;
    }

    /**
     * Holds value of property linkAction.
     */
    private int linkAction;

    /**
     * Getter for property linkAction.
     *
     * @return Value of property linkAction.
     */
    public int getLinkAction() {
        return linkAction;
    }

    /**
     * Setter for property linkAction.
     *
     * @param linkAction New value of property linkAction.
     */
    public void setLinkAction(int linkAction) {
        this.linkAction = linkAction;
    }

    /**
     * Holds value of property targetID.
     */
    private int targetID;

    /**
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    public int getTargetID() {
        return this.targetID;
    }

    /**
     * Setter for property targetID.
     *
     * @param targetID New value of property targetID.
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }

    /**
     * Holds value of property senderEmail.
     */
    private String senderEmail;

    /**
     * Getter for property senderEmail.
     *
     * @return Value of property senderEmail.
     */
    public String getSenderEmail() {
        return this.senderEmail;
    }

    /**
     * Setter for property senderEmail.
     *
     * @param senderEmail New value of property senderEmail.
     */
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    /**
     * Holds value of property senderFullname.
     */
    private String senderFullname;

    /**
     * Getter for property senderFullname.
     *
     * @return Value of property senderFullname.
     */
    public String getSenderFullname() {
        return this.senderFullname;
    }

    /**
     * Setter for property senderFullname.
     *
     * @param senderFullname New value of property senderFullname.
     */
    public void setSenderFullname(String senderFullname) {
        this.senderFullname = senderFullname;
    }
    
    /**
     * Holds value of property replyEmail.
     */
    private String replyEmail;

    /**
     * Getter for property replyEmail.
     *
     * @return Value of property replyEmail.
     */
    public String getReplyEmail() {
        return this.replyEmail;
    }

    /**
     * Setter for property replyEmail.
     *
     * @param replyEmail New value of property replyEmail.
     */
    public void setReplyEmail(String replyEmail) {
        this.replyEmail = replyEmail;
    }
    
    
    /**
     * Holds value of property replyFullname.
     */
    private String replyFullname;

    /**
     * Getter for property replyFullname.
     *
     * @return Value of property replyFullname.
     */
    public String getReplyFullname() {
        return this.replyFullname;
    }

    /**
     * Setter for property replyFullname.
     *
     * @param replyFullname New value of property replyFullname.
     */
    public void setReplyFullname(String replyFullname) {
        this.replyFullname = replyFullname;
    }

    /**
     * Holds value of property emailSubject.
     */
    private String emailSubject;

    /**
     * Getter for property emailSubject.
     *
     * @return Value of property emailSubject.
     */
    public String getEmailSubject() {
        return this.emailSubject;
    }

    /**
     * Setter for property emailSubject.
     *
     * @param emailSubject New value of property emailSubject.
     */
    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    /**
     * Holds value of property emailFormat.
     */
    private int emailFormat;

    /**
     * Getter for property emailFormat.
     *
     * @return Value of property emailFormat.
     */
    public int getEmailFormat() {
        return this.emailFormat;
    }

    /**
     * Setter for property emailFormat.
     *
     * @param emailFormat New value of property emailFormat.
     */
    public void setEmailFormat(int emailFormat) {
        this.emailFormat = emailFormat;
    }

    /**
     * Holds value of property emailOnepixel.
     */
    private String emailOnepixel;

    /**
     * Getter for property emailOnepixel.
     *
     * @return Value of property emailOnepixel.
     */
    public String getEmailOnepixel() {
        return this.emailOnepixel;
    }

    /**
     * Setter for property emailOnepixel.
     *
     * @param emailOnepixel New value of property emailOnepixel.
     */
    public void setEmailOnepixel(String emailOnepixel) {
        this.emailOnepixel = emailOnepixel;
    }

    /**
     * Holds value of property removeTargetID.
     */
    private int removeTargetID;

    /**
     * Getter for property removeTargetID.
     *
     * @return Value of property removeTargetID.
     */
    public int getRemoveTargetID() {
        return this.removeTargetID;
    }

    /**
     * Setter for property removeTargetID.
     *
     * @param removeTargetID New value of property removeTargetID.
     */
    public void setRemoveTargetID(int removeTargetID) {
        this.removeTargetID = removeTargetID;
    }

    /**
     * Holds value of property dynName.
     */
    private String dynName;

    /**
     * Getter for property dynName.
     *
     * @return Value of property dynName.
     */
    public String getDynName() {
        return this.dynName;
    }

    /**
     * Setter for property dynName.
     *
     * @param dynName New value of property dynName.
     */
    public void setDynName(String dynName) {
        this.dynName = dynName;
    }

    /**
     * Holds value of property newContent.
     */
    private String newContent;

    /**
     * Getter for property newContent.
     *
     * @return Value of property newContent.
     */
    public String getNewContent() {
        return this.newContent;
    }

    /**
     * Setter for property newContent.
     *
     * @param newContent New value of property newContent.
     */
    public void setNewContent(String newContent) {
        this.newContent = newContent;
    } 
    
    /**
     * Holds value of property newAttachmentType.
     */
    private int newAttachmentType;
    
    /**
     * Getter for property newAttachmentType.
     * @return Value of property newAttachmentType.
     */
    public int getNewAttachmentType() {
        
        return this.newAttachmentType;
    }
    
    /**
     * Setter for property newAttachmentType.
     * @param newAttachmentType New value of property newAttachmentType.
     */
    public void setNewAttachmentType(int newAttachmentType) {
        
        this.newAttachmentType = newAttachmentType;
    }
    
    /**
     * Holds value of property NewFile.
     */
    private FormFile newFile;

    
    /**
     * Getter for property NewFile.
     *
     * @return Value of property NewFile.
     */
    public FormFile getNewAttachment() {
        return this.newFile;
    }

    /**
     * Setter for property NewFile.
     *
     * @param newImage
     */
    public void setNewAttachment(FormFile newImage) {
        this.newFile = (FormFile)newImage;
    }
    
    /**
     * Holds value of property newAttachmentName.
     */
    private String newAttachmentName;

    /**
     * Getter for property newAttachmentName.
     *
     * @return Value of property newAttachmentName.
     */
    public String getNewAttachmentName() {
        return this.newAttachmentName;
    }

    /**
     * Setter for property newAttachmentName.
     *
     * @param newAttachmentName New value of property newAttachmentName.
     */
    public void setNewAttachmentName(String newAttachmentName) {
        this.newAttachmentName = newAttachmentName;
    }
    
    /**
     *  Holds value of property newAttachmentBackground.
     */
    private FormFile newAttachmentBackground;
    
    /** Getter for property newAttachmentBackground.
     * @return Value of property newAttachmentBackground.
     *
     */
    public FormFile getNewAttachmentBackground() {
        return this.newAttachmentBackground;
    }

    /** Setter for property newAttachmentBackground.
     * @param newAttachmentBackground New value of property newAttachmentBackground.
     *
     */
    public void setNewAttachmentBackground(Object newAttachmentBackground) {
    	System.err.println( newAttachmentBackground.getClass() );
        this.newAttachmentBackground = (FormFile)newAttachmentBackground;
    }
    
    /**
     *  Holds value of property attachmentTargetID.
     */
    private int attachmentTargetID;
    
    /** Getter for property attachmentTargetID.
     * @return Value of property attachmentTargetID.
     */
    public int getAttachmentTargetID() {
        return this.attachmentTargetID;
    }

    /** Setter for property attachmentTargetID.
     * @param attachmentTargetID New value of property attachmentTargetID.
     */
    public void setAttachmentTargetID(int attachmentTargetID) {
        this.attachmentTargetID = attachmentTargetID;
    }

	public DynamicTagContent getContent(int index) {
		DynamicTag	tag=(DynamicTag) mailing.getDynTags().get(dynName);
		DynamicTagContent content=(DynamicTagContent) tag.getDynContent().get(Integer.toString(index));

		return content;
	}
}
