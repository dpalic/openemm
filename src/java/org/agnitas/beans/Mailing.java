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
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.beans;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.agnitas.dao.MaildropStatusDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.preview.AgnTagError;
import org.agnitas.target.Target;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Martin Helff
 */
public interface Mailing extends java.io.Serializable, MailingBase {
	public static int INPUT_TYPE_TEXT = 0;
    public static int INPUT_TYPE_HTML = 1;

    public static int TARGET_MODE_OR = 0;
    public static int TARGET_MODE_AND = 1;

    public static int TYPE_NORMAL = 0;
    public static int TYPE_ACTIONBASED = 1;
    public static int TYPE_DATEBASED = 2;
    public static int TYPE_FOLLOWUP = 3;
    public static int TYPE_INTERVAL = 4;

    public static final String TYPE_FOLLOWUP_NON_OPENER = "non-opener";
    public static final String TYPE_FOLLOWUP_OPENER = "opener";
    public static final String TYPE_FOLLOWUP_NON_CLICKER = "non-clicker";
    public static final String TYPE_FOLLOWUP_CLICKER = "clicker";
    
    /**
     * Adds an attachment
     *
     * @param aComp
     */
    public void addAttachment(MailingComponent aComp);

    /**
     * Adds a component
     *
     * @param aComp
     */
    public void addComponent(MailingComponent aComp);

    /**
     * @return true
     */
    public boolean checkIfOK();

	/**
	 * Removes all deleted mails
	 */
	public boolean cleanupMaildrop(MaildropStatusDao maildropStatusDao);

    /**
     * Removes all deleted mails
     */
    public boolean cleanupMaildrop(ApplicationContext con);

    /**
     * Search for tags and adds then to a vector.
     *
     * @return Vector of added tags.
     */
    public Vector<String> findDynTagsInTemplates(String aTemplate, ApplicationContext con) throws Exception;

    /**
     * Search for a tag.
     * 	
     * @return Dynamic tag
     */ 
    public DynamicTag findNextDynTag(String aTemplate, ApplicationContext con) throws Exception;

    /**
     * Creates a new mailing
     *
     * @return true==sucess
     * false=error
     */
    public boolean sendEventMailing(int customerID, int delayMinutes, String userStatus, Map<String, String> overwrite, ApplicationContext con);

    /**
     * Getter for property template.
     *
     * @return Value of property template.
     */
    public MailingComponent getTemplate(String id);

    /**
     * Getter for property textTemplate.
     *
     * @return Value of property textTemplate.
     */
    public MailingComponent getTextTemplate();

    /**
     * Getter for property components.
     *
     * @return Value of property components.
     */
    public Map<String, MailingComponent> getComponents();

    /**
     * Getter for property dynTags.
     *
     * @return Value of property dynTags.
     */
    public  Map<String, DynamicTag> getDynTags();

    /**
     * Getter for property htmlTemplate.
     *
     * @return Value of property htmlTemplate.
     */
    public MailingComponent getHtmlTemplate();

    /**
     * Getter for property mailTemplateID.
     *
     * @return Value of property mailTemplateID.
     */
    public int getMailTemplateID();

    /**
     * Getter for property mailingType.
     *
     * @return Value of property mailingType.
     */
    public int getMailingType();

    /**
     * Getter for property creationDate.
     *
     * @return creationDate.
     */
    public Timestamp getCreationDate();

    /**
     * Getter for property targetGroups.
     *
     * @return Value of property targetGroups.
     */
    public Collection<Integer> getTargetGroups();

    /**
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    public int getTargetID();

    /**
     * Getter for property targetMode.
     *
     * @return Value of property targetMode.
     */
    public int getTargetMode();

    /**
     * Getter for property templateOK.
     *
     * @return Value of property templateOK.
     */
    public int getTemplateOK();

    /**
     * Getter for property worldMailingSend.
     *
     * @return Value of property worldMailingSend.
     */
    public boolean isWorldMailingSend();

    /**
     * Getter for property isTemplate.
     *
     * @return Value of property isTemplate.
     */
    public boolean isIsTemplate();

    /**
     * Removes dynamic tags
     */
    public List<String> cleanupDynTags(Vector<String> keepTags);

    /**
     * Removes trackable links
     */
    public void cleanupTrackableLinks(Vector<String> keepLinks);

    /**
     * Removes mailing components
     */
    public void cleanupMailingComponents(Vector<String> keepComps);

    public boolean parseTargetExpression(String tExp);

    /**
     * Personalizes the text
     */
    public String personalizeText(String input, int customerID, ApplicationContext con) throws Exception;

    /**
     * Implements macros
     */
    public String processTag(TagDetails aDetail, int customerID, ApplicationContext con);

    /**
     * Getter for property preview.
     *
     * @return Value of property preview.
     */
    public String getPreview(String input, int inputType, int customerID, boolean overwriteMailtype, ApplicationContext con) throws Exception;

    /**
     * Getter for property preview.
     *
     * @return Value of property preview.
     */
    public String getPreview(String input, int inputType, int customerID, ApplicationContext con) throws Exception;

    /**
     * search for components
     */
//    Vector<String> scanForComponents(String aText1, ApplicationContext con);

    /**
     * search for links
     *
     * @return Vector of links.
     */
    public Vector<String> scanForLinks(String aText1, ApplicationContext con);

    /**
     * search for links
     *
     * @return Vector of links.
     */
    public Vector<String> scanForLinks(ApplicationContext con) throws Exception;

    /**
     * Sends mailing.
     */
    public boolean triggerMailing(int maildropStatusId, Map<String, Object> opts, ApplicationContext con);

    /**
     * Setter for property asciiTemplate.
     *
     * @param asciiTemplate New value of property asciiTemplate.
     */
    public void setTextTemplate(MailingComponent asciiTemplate);

    /**
     * Setter for property components.
     *
     * @param components New value of property components.
     */
    public void setComponents(Map<String, MailingComponent> components);

    /**
     * Setter for property dynTags.
     *
     * @param dynTags New value of property dynTags.
     */
    public void setDynTags(Map<String, DynamicTag> dynTags);

    /**
     * Setter for property htmlTemplate.
     *
     * @param htmlTemplate New value of property htmlTemplate.
     */
    public void setHtmlTemplate(MailingComponent htmlTemplate);

    /**
     * Setter for property isTemplate.
     *
     * @param isTemplate New value of property isTemplate.
     */
    public void setIsTemplate(boolean isTemplate);

    /**
     * Setter for property mailTemplateID.
     *
     * @param id New value of proerty mailTemplateID.
     */
    public void setMailTemplateID(int id);

    /**
     * Setter for property mailingType.
     *
     * @param mailingType New value of property mailingType.
     */
    public void setMailingType(int mailingType);

    /**
     * Setter for the creationDate.
     * @param creationDate the new value for the creationDate.
     */
    public void setCreationDate(Timestamp creationDate);

    /**
     * Setter for property targetGroups.
     * This is automatically set when calling {@link #setTargetExpression(String)}.
     *
     * @param targetGroups New value of property targetGroups.
     */
    @Deprecated
    public void setTargetGroups(Collection<Integer> targetGroups);

    /**
     * Setter for property targetID
     *
     * @param id New value of proerty targetID.
     */
    public void setTargetID(int id);

    /**
     * Setter for property targetMode.
     * This is automatically set when {@link #setTargetExpression(String)} is called.
     *
     * @param targetMode New value of property targetMode.
     */
    @Deprecated
    public void setTargetMode(int targetMode);

    /**
     * Setter for property templateOK.
     *
     * @param templateOK New value of property templateOK.
     */
    public void setTemplateOK(int templateOK);

    /**
     * Getter for property targetExpression.
     *
     * @return Value of property targetExpression.
     */
    public String getTargetExpression();

    /**
     * Setter for property targetExpression.
     *
     * @param targetExpression New value of property targetExpression.
     */
    public void setTargetExpression(String targetExpression);

    /**
     * Getter for property mediatypes.
     *
     * @return Value of property mediatypes.
     */
    public Map<Integer, Mediatype> getMediatypes();

    /**
     * Setter for property mediatypes.
     *
     * @param mediatypes New value of property mediatypes.
     */
    public void setMediatypes(Map<Integer, Mediatype> mediatypes);

    /**
     * Getter for property emailParam.
     *
     * @return Value of property emailParam in dependency of the context.
     */
    public MediatypeEmail getEmailParam();

    /**
     * Getter for property trackableLinks.
     *
     * @return Value of property trackableLinks.
     */
    public Map<String, TrackableLink> getTrackableLinks();

    /**
     * Setter for property trackableLinks.
     *
     * @param trackableLinks New value of property trackableLinks.
     */
    public void setTrackableLinks(Map<String, TrackableLink> trackableLinks);

    /**
     * Initialising
     */
    public void init( @VelocityCheck int companyID, ApplicationContext con);

    /**
     * Getter for property dynamicTagById.
     *
     * @return Value of property dynamicTagById.
     */
    public DynamicTag getDynamicTagById(int dynId);

    /**
     * Getter for trackableLinkById.
     *
     * @return Value of property trackableLinkById.
     */
    public TrackableLink getTrackableLinkById(int urlID);

    /**
     * Search for all dependency
     */
    public boolean buildDependencies(boolean scanDynTags, ApplicationContext con, boolean cleanupComponents) throws Exception;
    
    public boolean buildDependencies(boolean scanDynTags, List<String> dynNamesForDeletion, ApplicationContext con, boolean cleanupComponents) throws Exception;

    /**
     * Getter for property maildropStatus.
     *
     * @return Value of property maildropStatus.
     */
    public Set<MaildropEntry> getMaildropStatus();

    /**
     * Setter for property maildropStatus.
     *
     * @param maildropStatus New value of property maildropStatus.
     */
    public void setMaildropStatus(Set<MaildropEntry> maildropStatus);

    /**
     * Adds a dynamic tag.
     */
    public void addDynamicTag(DynamicTag aTag);

    /**
     * Creates a copy of the mailing.
     *
     * @return Mailingobject.
     */
    public Object clone(ApplicationContext con);

    /**
     * Getter for property deleted.
     *
     * @return Value of property deleted.
     */
    public int getDeleted();

    /**
     * Setter for property deleted.
     *
     * @param deleted New value of property deleted.
     */
    public void setDeleted(int deleted);

    public Map<Integer, Target> getAllowedTargets(ApplicationContext myContext);

    /**
     * Getter for property needsTarget.
     *
     * @return Value of property needTarget.
     */
    public boolean getNeedsTarget();

    /**
     * Setter for property needsTarget.
     *
     * @param needsTarget New value of property needsTarget.
     */
    public void setNeedsTarget(boolean needsTarget);

    public int getLocked();

    public void setLocked(int locked);

    public void setSearchPos(int pos);
    
    public int getArchived();

    public void setArchived(int archived);

    public int getOpenActionID();

    public void setOpenActionID(int id);

    public int getClickActionID();

    public void setClickActionID(int id);

    public void updateTargetExpression();

    public void modifyEveryPositionLink(String link, ApplicationContext con) throws Exception;

    public boolean hasComplexTargetExpression();
	
	public Map<String, List<AgnTagError>> checkAgnTagSyntax(ApplicationContext applicationContext) throws Exception;
}
