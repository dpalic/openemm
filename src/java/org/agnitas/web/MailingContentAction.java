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

package org.agnitas.web;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminPreferences;
import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.factory.DynamicTagContentFactory;
import org.agnitas.beans.factory.MailingFactory;
import org.agnitas.dao.AdminPreferencesDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.exceptions.CharacterEncodingValidationException;
import org.agnitas.preview.AgnTagError;
import org.agnitas.preview.PreviewHelper;
import org.agnitas.preview.TAGCheck;
import org.agnitas.preview.TAGCheckFactory;
import org.agnitas.preview.TagSyntaxChecker;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CharacterEncodingValidator;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public class MailingContentAction extends StrutsActionBase {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(MailingContentAction.class);
    
    public static final int ACTION_VIEW_CONTENT = ACTION_LAST+1;
    
    public static final int ACTION_VIEW_TEXTBLOCK = ACTION_LAST+2;
    
    public static final int ACTION_ADD_TEXTBLOCK = ACTION_LAST+3;
    
    public static final int ACTION_SAVE_TEXTBLOCK = ACTION_LAST+4;
    
    public static final int ACTION_SAVE_COMPONENT_EDIT = ACTION_LAST+5;
    
    public static final int ACTION_DELETE_TEXTBLOCK = ACTION_LAST+6;
    
    public static final int ACTION_CHANGE_ORDER_UP = ACTION_LAST+7;
    
    public static final int ACTION_CHANGE_ORDER_DOWN = ACTION_LAST+8;
    
    public static final int ACTION_CHANGE_ORDER_TOP = ACTION_LAST+9;
    
    public static final int ACTION_CHANGE_ORDER_BOTTOM = ACTION_LAST+10;

    public static final int ACTION_SAVE_TEXTBLOCK_AND_BACK = ACTION_LAST + 11;
    
    public static final int ACTION_MAILING_CONTENT_LAST = ACTION_LAST+11;

    protected TAGCheckFactory tagCheckFactory;
    protected CharacterEncodingValidator characterEncodingValidator;
    protected MailingDao mailingDao;
    protected MailingFactory mailingFactory;
    protected DynamicTagContentFactory dynamicTagContentFactory;
    protected TargetDao targetDao;
    protected RecipientDao recipientDao;
	protected ConfigService configService;
    protected AdminPreferencesDao adminPreferencesDao;
	protected TagSyntaxChecker tagSyntaxChecker;

	@Required
    public void setTagSyntaxChecker(TagSyntaxChecker tagSyntaxChecker) {
        this.tagSyntaxChecker = tagSyntaxChecker;
    }

    // --------------------------------------------------------- Public Methods
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
	 * ACTION_VIEW_CONTENT: loads mailing from database and puts its data into the form. Loads list of target groups
     *     and puts it into request. Forwards to "list".
	 * <br><br>
	 * ACTION_VIEW_TEXTBLOCK: loads mailing from database and puts its data into the form. Loads list of target groups
     *     and puts it into request. Forwards to "view".
	 * <br><br>
	 * ACTION_SAVE_TEXTBLOCK: saves content of current text module into database and forwards to content view page.
     *     Before saving performs TagCheck validation and validation for invalid characters for given character set.
     *     Saves new content only if no errors found. Loads list of target groups and puts it into request. Forwards
     *     to "view".
	 * <br><br>
	 * ACTION_SAVE_TEXTBLOCK_AND_BACK: saves content of current text module into database and forwards to content view page.
     *     Before saving performs TagCheck validation and validation for invalid characters for given character set.
     *     Saves new content only if no errors found. Loads list of target groups and puts it into request. Forwards
     *     to "list".
	 * <br><br>
     * ACTION_ADD_TEXTBLOCK: creates a new content for current text module. Builds mailing dependencies and saves
     *     whole mailing. Loads mailing into form. Forwards to content view page.
     * ACTION_DELETE_TEXTBLOCK: deletes selected content for current text module. Builds mailing dependencies and
     *     saves whole mailing. Loads mailing into form. Forwards to content view page.
     * ACTION_CHANGE_ORDER_UP: moves content one position up in current text module. Builds mailing dependencies and
     *     saves whole mailing. Loads mailing into form. Forwards to content view page.
     * ACTION_CHANGE_ORDER_DOWN:  moves content one position down in current text module. Builds mailing dependencies
     *     and saves whole mailing. Loads mailing into form. Forwards to content view page.
     * ACTION_CHANGE_ORDER_TOP: moves content on top in current text module. Builds mailing dependencies and saves
     *     whole mailing. Loads mailing into form. Forwards to content view page.
     * ACTION_CHANGE_ORDER_BOTTOM: moves content on bottom in current text module. Builds mailing dependencies and saves
     *     whole mailing. Loads mailing into form. Forwards to content view page.
	 * <br><br>
     * If destination is "list" - loads list of admin- and test-recipients of current mailing to request (is needed
     * for live preview).
     *
	 * @param form data for the action filled by the jsp
	 * @param req request from jsp
	 * @param res response
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination specified in struts-config.xml to forward to next jsp
     */
	@Override
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {
        
        // Validate the request parameters specified by the user
        MailingContentForm aForm=null;
        ActionMessages errors = new ActionMessages();
    	ActionMessages messages = new ActionMessages();
        ActionForward destination=null;
        boolean hasErrors;
        List<String[]> errorReport;
        
        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }
        
		aForm = (MailingContentForm) form;
		if (logger.isInfoEnabled()) logger.info("Action: " + aForm.getAction());

		if (!allowed("mailing.content.show", req)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
			saveErrors(req, errors);
			return null;
		}
        
        try {
            switch(aForm.getAction()) {
                case MailingContentAction.ACTION_VIEW_CONTENT:
                    loadMailing(aForm, req);
                    destination=mapping.findForward("list");
                    break;
                    
                case MailingContentAction.ACTION_VIEW_TEXTBLOCK:
                    aForm.setAction(MailingContentAction.ACTION_SAVE_TEXTBLOCK);
                    loadMailing(aForm, req);
                    destination=mapping.findForward("view");
                    break;
                    
                case MailingContentAction.ACTION_SAVE_TEXTBLOCK:
                	hasErrors = false;
                	errorReport = new ArrayList<String[]>();

                    fixAngTagsQuotes(aForm);
                    
					List<AgnTagError> agnTagSyntaxErrors = new ArrayList<AgnTagError>();

                	if( aForm.getMailingID() != 0 ) {
                		Map<String, DynamicTagContent> contentMap =  aForm.getContent();
                		TAGCheck tagCheck = tagCheckFactory.createTAGCheck(aForm.getMailingID());
                		for(Entry<String,DynamicTagContent> entry : contentMap.entrySet()) {
							// Check for valid agnTag-Syntax
							String textBlockName = entry.getValue().getDynName();
							String textBlockContent = entry.getValue().getDynContent();
							if (!tagSyntaxChecker.check(AgnUtils.getCompanyID(req), textBlockContent, agnTagSyntaxErrors)) {
								for (AgnTagError agnTagError : agnTagSyntaxErrors) {
									appendErrorToList(errorReport, textBlockName, agnTagError.getFullAgnTagText(), agnTagError.getLocalizedMessage(req.getLocale()));
								}
								hasErrors = true;
							}
							
                			StringBuffer tagErrorReport = new StringBuffer();
                			Vector<String> failures = new Vector<String>();
                			if( !tagCheck.checkContent(entry.getValue().getDynContent(),tagErrorReport, failures)) {
                				appendErrorsToList(entry.getValue().getDynName(), errorReport, tagErrorReport);
                				hasErrors = true;
                			}
                			
                		}
                		tagCheck.done();
                	}
                	
                	if( !hasErrors ){
                        performContentValidation(aForm, errors);
                        try {
                            this.saveContent(aForm, req);
                        } catch (TransientDataAccessResourceException e) {
                            logger.error("execute", e);
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.hibernate.attachmentTooLarge"));
                        }
                        aForm.setAction(MailingContentAction.ACTION_SAVE_TEXTBLOCK);
                        loadMailing(aForm, req);
                        // Show "changes saved"
                        if (errors.isEmpty()) {
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        }
                	}
                	else {
                		req.setAttribute("errorReport", errorReport);
						if (!agnTagSyntaxErrors.isEmpty()) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(agnTagSyntaxErrors.get(0).getErrorKey().getMessageKey(), agnTagSyntaxErrors.get(0).getAdditionalErrorDataWithLineInfo()));
						} else {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.template.dyntags"));
						}
                	}
            		putTargetGroupsInRequest(req);
                	
                	destination=mapping.findForward("view");
                	
                    break;
                case MailingContentAction.ACTION_SAVE_TEXTBLOCK_AND_BACK:
                	hasErrors = false;
                	errorReport = new ArrayList<String[]>();

                    fixAngTagsQuotes(aForm);

                	if( aForm.getMailingID() != 0 ) {
                		Map<String, DynamicTagContent> contentMap =  aForm.getContent();
                		TAGCheck tagCheck = tagCheckFactory.createTAGCheck(aForm.getMailingID());
                		for(Entry<String,DynamicTagContent> entry:contentMap.entrySet()) {
                			StringBuffer tagErrorReport = new StringBuffer();
                			Vector<String> failures = new Vector<String>();
                			if( !tagCheck.checkContent(entry.getValue().getDynContent(),tagErrorReport, failures)) {
                				appendErrorsToList(entry.getValue().getDynName(), errorReport, tagErrorReport);
                				hasErrors = true;
                			}
                			
                		}
                		tagCheck.done();
                	}
                	
                	if( !hasErrors ){
                		performContentValidation(aForm, errors);
                        try {
                            this.saveContent(aForm, req);
                        } catch (TransientDataAccessResourceException e) {
                            logger.error("execute", e);
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.hibernate.attachmentTooLarge"));
                        }
            			aForm.setAction(MailingContentAction.ACTION_SAVE_TEXTBLOCK);
            			loadMailing(aForm, req);
            			// Show "changes saved"
                        if (errors.isEmpty()) {
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        }
                	}
                	else {
                		req.setAttribute("errorReport", errorReport);
                    	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.template.dyntags"));
                		
                	}
            		putTargetGroupsInRequest(req);
                	
                	destination=mapping.findForward("list");
                	
                    break;
                    
                case MailingContentAction.ACTION_ADD_TEXTBLOCK:
                case MailingContentAction.ACTION_DELETE_TEXTBLOCK:
                case MailingContentAction.ACTION_CHANGE_ORDER_UP:
                case MailingContentAction.ACTION_CHANGE_ORDER_DOWN:
                case MailingContentAction.ACTION_CHANGE_ORDER_TOP:
                case MailingContentAction.ACTION_CHANGE_ORDER_BOTTOM:
                    destination=mapping.findForward("view");
                    fixAngTagsQuotes(aForm);
                    try {
                        this.saveContent(aForm, req);
                    } catch (TransientDataAccessResourceException e) {
                        logger.error("execute", e);
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.hibernate.attachmentTooLarge"));
                    }
                    aForm.setAction(MailingContentAction.ACTION_VIEW_TEXTBLOCK);
                    loadMailing(aForm, req);
                    if (errors.isEmpty()) {
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    }
                    break;
            }
        } catch (Exception e) {
            logger.error("execute", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
        }
        
        if(destination != null && "list".equals(destination.getName())) {
        	putPreviewRecipientsInRequest(req, aForm.getMailingID(),aForm.getCompanyID(req), recipientDao);
        }
                
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }

        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }
        
        return destination;
    }
    

    protected void performContentValidation(MailingContentForm form, ActionMessages errors){
        try {
            String emailParameter = mailingDao.getEmailParameter(form.getMailingID());
            if (emailParameter != null) {
                String charset = AgnUtils.getAttributeFromParameterString(emailParameter, "charset");
                characterEncodingValidator.validate(form, charset);
            }
        } catch (CharacterEncodingValidationException e) {
            for (String mailingComponent : e.getFailedMailingComponents())
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.charset.component", mailingComponent));
            for (String dynTag : e.getFailedDynamicTags())
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.charset.content", dynTag));
        }
    }
    
    /**
     * Loads mailing.
     */
    protected Mailing loadMailing(MailingContentForm aForm, HttpServletRequest req) throws Exception {
        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getCompanyID(req));
        Admin admin = AgnUtils.getAdmin(req);
        AdminPreferences adminPreferences = adminPreferencesDao.getAdminPreferences(admin.getAdminID());

        if(aMailing==null) {
            aMailing=mailingFactory.newMailing();
            aMailing.init(AgnUtils.getCompanyID(req), getApplicationContext(req));
            aMailing.setId(0);
            aForm.setMailingID(0);
        }
        
        aForm.setShortname(aMailing.getShortname());
        aForm.setDescription(aMailing.getDescription());
        aForm.setIsTemplate(aMailing.isIsTemplate());
        aForm.setMailinglistID(aMailing.getMailinglistID());
        aForm.setMailingID(aMailing.getId());
        aForm.setMailFormat(aMailing.getEmailParam().getMailFormat());
        aForm.setTags(aMailing.getDynTags(), true);
        aForm.setWorldMailingSend(aMailing.isWorldMailingSend());
        if(aForm.getAction()==MailingContentAction.ACTION_VIEW_TEXTBLOCK || aForm.getAction()==MailingContentAction.ACTION_SAVE_TEXTBLOCK) {
            aForm.setContent(aMailing.getDynamicTagById(aForm.getDynNameID()).getDynContent());

            String dynTargetName = aMailing.getDynamicTagById(aForm.getDynNameID()).getDynName();
            boolean showHTMLEditor = calculateShowingHTMLEditor(dynTargetName, aMailing, req);
            aForm.setShowHTMLEditor(showHTMLEditor);

            aForm.setMailingContentView(adminPreferences.getMailingContentView()); /*HTML-Editor = 1, HTML-Code = 0*/

            aForm.setDynName(dynTargetName);
        }
        StringBuilder description = new StringBuilder();
        if(aForm.isIsTemplate()){
            description.append("template");
        } else {
            description.append("mailing");
        }
        description.append(" ")
                .append(aForm.getShortname())
                .append("(")
                .append(aForm.getMailingID())
                .append(")");
        writeUserActivityLog(admin, "do load", description.toString());
        putTargetGroupsInRequest(req);
        return aMailing;
    }

    protected boolean calculateShowingHTMLEditor(String dynTargetName, Mailing aMailing, HttpServletRequest req) throws Exception {
        String htmlEmmBlock = aMailing.getHtmlTemplate().getEmmBlock();
        Vector<String> tagsInHTMLTemplate = aMailing.findDynTagsInTemplates(htmlEmmBlock, getApplicationContext(req));

        if (tagsInHTMLTemplate.contains(dynTargetName)) {
             return true;
        }
        return false;
    }
    
    /**
     * Saves content.
     * @throws CharacterEncodingValidationException 
     */
    protected void saveContent(MailingContentForm aForm, HttpServletRequest req) throws CharacterEncodingValidationException {
        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getCompanyID(req));
        DynamicTagContent aContent=null;
        
        if(aMailing!=null) {
            DynamicTag aTag=aMailing.getDynamicTagById(aForm.getDynNameID());
        
        
            if(aTag!=null) {
                aTag.setDynContent(aForm.getContent());
                
                switch(aForm.getAction()) {
                    case MailingContentAction.ACTION_ADD_TEXTBLOCK:
                        aContent=dynamicTagContentFactory.newDynamicTagContent();
                        aContent.setCompanyID(AgnUtils.getCompanyID(req));
                        aContent.setDynContent(aForm.getNewContent());
                        aContent.setTargetID(aForm.getNewTargetID());
                        aContent.setDynOrder(aTag.getMaxOrder()+1);
                        aContent.setDynNameID(aTag.getId());
                        aContent.setMailingID(aTag.getMailingID());
                        aTag.addContent(aContent);
                        break;
                        
                    case MailingContentAction.ACTION_DELETE_TEXTBLOCK:
                    	
                    	// reload mailing to get a persisted dynContent table
                    	aMailing=mailingDao.getMailing(aForm.getMailingID(), AgnUtils.getCompanyID(req));
                    	aTag=aMailing.getDynamicTagById(aForm.getDynNameID());
                        aTag.removeContent(aForm.getContentID());
                        
                        
                        break;
                        
                    case MailingContentAction.ACTION_CHANGE_ORDER_UP:
                        // aTag.changeContentOrder(aForm.getContentID(), 1);
                        aTag.moveContentDown(aForm.getContentID(), -1);
                        break;
                        
                    case MailingContentAction.ACTION_CHANGE_ORDER_DOWN:
                        aTag.moveContentDown(aForm.getContentID(), 1);
                        // aTag.changeContentOrder(aForm.getContentID(), 2);
                        break;

                    case MailingContentAction.ACTION_CHANGE_ORDER_TOP:
                        for (int numOfContent = 0; numOfContent < aTag.getDynContentCount(); numOfContent++) {
                            aTag.moveContentDown(aForm.getContentID(), -1);
                        }
                        break;
                        
                    case MailingContentAction.ACTION_CHANGE_ORDER_BOTTOM:
                        for (int numOfContent = 0; numOfContent < aTag.getDynContentCount(); numOfContent++) {
                            aTag.moveContentDown(aForm.getContentID(), 1);
                        }
                        break;
                }
            }
            try {
                aMailing.buildDependencies(false, getApplicationContext(req), false);
            } catch (Exception e) {
                logger.error( "Error building dependencies", e);
            }

            // save
   			mailingDao.saveMailing(aMailing);
   			
   			if(aForm.getAction() == ACTION_DELETE_TEXTBLOCK) {
                mailingDao.deleteContent(aForm.getContentID());
   			}

            writeTextblockChangeLog(aForm, AgnUtils.getAdmin(req), aMailing, aContent);
        }
        if (logger.isInfoEnabled()) logger.info("change content of mailing: "+aForm.getMailingID());
    }
    
    /**
     * Format and write to user event log message about textblock
     * @param aForm - MailingContentForm
     * @param admin - Admin
     * @param aMailing - Mailing which is container for textblock
     * @param aContent - DynamicTagContent, changed textblock.
     */
    protected void writeTextblockChangeLog(MailingContentForm aForm, Admin admin, Mailing aMailing, DynamicTagContent aContent) {
        String entityName = aMailing.isIsTemplate() ? "template" : "mailing";
        DynamicTag aTag=aMailing.getDynamicTagById(aForm.getDynNameID());
        String blockName = aTag.getDynName();
        final String messagePattern = String.format("%s %s %s %s (%d)",blockName, "(%d) %s", entityName, aMailing.getShortname(), aMailing.getId());
        switch (aForm.getAction()) {
            case MailingContentAction.ACTION_ADD_TEXTBLOCK:
                writeUserActivityLog(admin, "create textblock", String.format(messagePattern, aContent.getId(),"in the"));
                break;

            case MailingContentAction.ACTION_DELETE_TEXTBLOCK:
                writeUserActivityLog(admin, "delete textblock", String.format(messagePattern, aForm.getContentID(), "from"));

                break;

            case MailingContentAction.ACTION_CHANGE_ORDER_UP:
                writeUserActivityLog(admin, "do order up textblock", String.format(messagePattern, aForm.getContentID(), "from"));
                break;

            case MailingContentAction.ACTION_CHANGE_ORDER_DOWN:
                writeUserActivityLog(admin, "do order down textblock", String.format(messagePattern, aForm.getContentID(), "from"));
                break;

            case MailingContentAction.ACTION_CHANGE_ORDER_TOP:
                writeUserActivityLog(admin, "do order top textblock", String.format(messagePattern, aForm.getContentID(), "from"));
                break;

            case MailingContentAction.ACTION_CHANGE_ORDER_BOTTOM:
                writeUserActivityLog(admin, "do order bottom textblock", String.format(messagePattern, aForm.getContentID(), "from"));
                break;
        }
    }

    protected void fixAngTagsQuotes(MailingContentForm aForm) {
        Map<String, DynamicTagContent> content = aForm.getContent();
        Iterator<DynamicTagContent> contentIterator = content.values().iterator();
        DynamicTagContent dynContent;
        while (contentIterator.hasNext()) {
            dynContent = contentIterator.next();
            String text = dynContent.getDynContent();
            String fixedText = AgnUtils.fixQuotesInAgnTags(text);
            dynContent.setDynContent(fixedText);
        }
    }
    
    protected void appendErrorsToList(String blockName, List<String[]> errorReports, StringBuffer templateReport) {
		Map<String,String> tagsWithErrors = PreviewHelper.getTagsWithErrors(templateReport);
		for(Entry<String,String> entry:tagsWithErrors.entrySet()) {
			String[] errorRow = new String[3];
			errorRow[0] = blockName; // block
			errorRow[1] =  entry.getKey(); // tag
			errorRow[2] =  entry.getValue(); // value
			
			errorReports.add(errorRow);
		}
		List<String> errorsWithoutATag = PreviewHelper.getErrorsWithoutATag(templateReport);
		for(String error:errorsWithoutATag){
			String[] errorRow = new String[3];
			errorRow[0] = blockName;
			errorRow[1] = "";
			errorRow[2] = error;
			errorReports.add(errorRow);
		}
	}
    
    protected void appendErrorToList(List<String[]> errorReports, String blockName, String errorneousText, String errorDescription) {
		errorReports.add(new String[] { blockName, errorneousText, errorDescription });
	}

    @Override
    protected void putTargetGroupsInRequest(HttpServletRequest req) {
        req.setAttribute("targetGroups", targetDao.getTargets(AgnUtils.getCompanyID(req), true));
    }

    protected WebApplicationContext getApplicationContext(HttpServletRequest req){
        return WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext());
    }

    public void setTagCheckFactory(TAGCheckFactory tagCheckFactory) {
        this.tagCheckFactory = tagCheckFactory;
    }

    public void setCharacterEncodingValidator(CharacterEncodingValidator characterEncodingValidator) {
        this.characterEncodingValidator = characterEncodingValidator;
    }

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }

    public void setMailingFactory(MailingFactory mailingFactory) {
        this.mailingFactory = mailingFactory;
    }

    public void setDynamicTagContentFactory(DynamicTagContentFactory dynamicTagContentFactory) {
        this.dynamicTagContentFactory = dynamicTagContentFactory;
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public void setRecipientDao(RecipientDao recipientDao) {
        this.recipientDao = recipientDao;
    }

    @Required
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    @Required
    public void setAdminPreferencesDao(AdminPreferencesDao adminPreferencesDao) {
        this.adminPreferencesDao = adminPreferencesDao;
    }
}
