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

package org.agnitas.web;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.MediatypeEmail;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.exceptions.CharacterEncodingValidationException;
import org.agnitas.preview.AgnTagException;
import org.agnitas.preview.PreviewHelper;
import org.agnitas.preview.TAGCheck;
import org.agnitas.preview.TAGCheckFactory;
import org.agnitas.service.MailingsQueryWorker;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CharacterEncodingValidator;
import org.agnitas.util.SafeString;
import org.agnitas.web.forms.MailingBaseForm;
import org.agnitas.cms.utils.CmsUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.commons.lang.StringUtils;


/**
 * Implementation of <strong>Action</strong> that handles Mailings
 *
 * @author Martin Helff
 */

public class MailingBaseAction extends StrutsActionBase {

	public static final String FUTURE_TASK = "GET_MAILING_LIST";
	
    public static final int ACTION_SELECT_TEMPLATE = ACTION_LAST+1;
    
    public static final int ACTION_REMOVE_TARGET = ACTION_LAST+2;
    
    public static final int ACTION_VIEW_WITHOUT_LOAD = ACTION_LAST+3;
    
    public static final int ACTION_CLONE_AS_MAILING = ACTION_LAST+4;
    
    public static final int ACTION_USED_ACTIONS = ACTION_LAST + 5;

    public static final int ACTION_VIEW_TABLE_ONLY = ACTION_LAST +6;
    
    public static final int ACTION_MAILING_BASE_LAST = ACTION_LAST+6;
    
    // --------------------------------------------------------- Public Methods
    
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * 
     * @param form 
     * @param req 
     * @param res 
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {
        
        // Validate the request parameters specified by the user
        MailingBaseForm aForm=null;
        ActionMessages errors = new ActionMessages();
    	ActionMessages messages = new ActionMessages();
    	ActionForward destination=null;
        boolean showTemplates=false;
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        aForm=(MailingBaseForm)form;

        AgnUtils.logger().info("execute: action "+aForm.getAction());
 
        if(aForm.isIsTemplate()) {
            if(!allowed("template.show", req)) {
                errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                saveErrors(req, errors);
                return null;
            }
        } else {
            if(!allowed("mailing.show", req)) {
                errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                saveErrors(req, errors);
                return null;
            }
        }
       
		if (aForm.getAction() != MailingBaseAction.ACTION_SAVE) {
			aForm.setOriginalMailingId(0);
		}

        try {
            switch(aForm.getAction()) {
                case MailingBaseAction.ACTION_LIST:
                	if ( aForm.getColumnwidthsList() == null) {
                    	aForm.setColumnwidthsList(getInitializedColumnWidthList(5));
                    }
                    destination=mapping.findForward("list");
                   	break;
                case MailingBaseAction.ACTION_NEW:
                    if(allowed("mailing.new", req)) {
                        MailinglistDao mDao=(MailinglistDao) getBean("MailinglistDao");
                        List mlists=mDao.getMailinglists(getCompanyID(req));
                       
                        if(mlists.size() > 0) { 
                            aForm.setAction(MailingBaseAction.ACTION_SAVE);
                            int campaignID = aForm.getCampaignID();
                            aForm.clearData(getCompanyID(req), getDefaultMediaType(req));
                            aForm.setMailingID(0);
                            aForm.setCampaignID(campaignID);
                            destination=mapping.findForward("view");
                        } else {
                            errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.mailing.noMailinglist"));
                        }
                    } else {
                        errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
 
                case MailingBaseAction.ACTION_VIEW:
                    aForm.setAction(MailingBaseAction.ACTION_SAVE);
                    resetShowTemplate(req, aForm);
                    loadMailing(aForm, req);
                    destination=mapping.findForward("view");
                    break;
                    
                case MailingBaseAction.ACTION_VIEW_WITHOUT_LOAD:
                    aForm.setAction(MailingBaseAction.ACTION_SAVE);
                    destination=mapping.findForward("view");
                    break;
                    
                case MailingBaseAction.ACTION_REMOVE_TARGET:
                    removeTarget(aForm, req);
                    if(aForm.getMailingID()!=0) {
                        aForm.setAction(MailingBaseAction.ACTION_SAVE);
                        //this.saveMailing(aForm, req);
                    } else {
                        aForm.setAction(MailingBaseAction.ACTION_NEW);
                    }
                    destination=mapping.findForward("view");
                    break;
                    
                case MailingBaseAction.ACTION_SAVE:
                    if(allowed("mailing.change", req)) { 
                    	destination=mapping.findForward("view");
                    	
                    	try {
                    		validateMailing( aForm, req);
						} catch( CharacterEncodingValidationException e) {
							if( !e.isSubjectValid())
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.charset.subject"));
							for( String mailingComponent : e.getFailedMailingComponents())
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.charset.component", mailingComponent));
							for( String dynTag : e.getFailedDynamicTags())
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.charset.content", dynTag));
                    	}
                    	
                    	try {
                    		saveMailing(aForm, req);
                    		loadMailing(aForm, req);
                    	    // Show "changes saved"
                    		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    	} catch (AgnTagException e) {
                    		req.setAttribute("errorReport", e.getReport());
                    		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.template.dyntags"));
						}
                    		
                        showTemplates=aForm.isShowTemplate();
                        aForm.setShowTemplate(showTemplates);

						// copy CMS data of cloned mailing if the original
						// mailing included CMS content
						if(aForm.getOriginalMailingId() != 0) {
							if(CmsUtils.mailingHasCmsData(aForm.getOriginalMailingId(), getWebApplicationContext())) {
								CmsUtils.cloneMailingCmsData(aForm.getOriginalMailingId(), aForm.getMailingID(), getWebApplicationContext());
							}
							aForm.setOriginalMailingId(0);
						}
                    
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailingBaseAction.ACTION_SELECT_TEMPLATE:
                    loadTemplateSettings(aForm, req);
                    aForm.setAction(MailingBaseAction.ACTION_SAVE);
                    destination=mapping.findForward("view");
                    break;
                    
                case MailingBaseAction.ACTION_CLONE_AS_MAILING:
                    if(allowed("mailing.copy", req)) {
						aForm.setOriginalMailingId(aForm.getMailingID());
                        int tmpTemplateID=aForm.getMailingID();
                        int tmpMlId=aForm.getMailinglistID();
                        String sname = aForm.getShortname();
                        int tmpFormat=aForm.getMediaEmail().getMailFormat();
                        boolean tmpl=aForm.isIsTemplate();
                        String tempDescription = aForm.getDescription();
                        aForm.clearData(this.getCompanyID(req), this.getDefaultMediaType(req));
                        aForm.setTemplateID(tmpTemplateID);
                        aForm.setIsTemplate(tmpl);
                        loadTemplateSettings(aForm, req);
                        aForm.setMailinglistID(tmpMlId);
                        aForm.getMediaEmail().setMailFormat(tmpFormat);
                        aForm.setMailingID(0);
                        aForm.setAction(MailingBaseAction.ACTION_SAVE);
                        aForm.setShortname(SafeString.getLocaleString("mailing.CopyOf", (Locale)req.getSession().getAttribute(Globals.LOCALE_KEY)) + " " + sname);
//                        aForm.setDescription(SafeString.getLocaleString("default.description", (Locale)req.getSession().getAttribute(Globals.LOCALE_KEY)));
                        aForm.setDescription( tempDescription);
                        aForm.setCopyFlag(true);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailingBaseAction.ACTION_CONFIRM_DELETE:
                    if(allowed("mailing.delete", req)) {
                        aForm.setAction(MailingBaseAction.ACTION_DELETE);
                        loadMailing(aForm, req);
                        destination=mapping.findForward("delete");
                    } else {
                        errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailingBaseAction.ACTION_DELETE:
                    if(allowed("mailing.delete", req)) {
                        aForm.setAction(MailingBaseAction.ACTION_LIST);
                        deleteMailing(aForm, req);
                        destination=mapping.findForward("list");
                        
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        aForm.setMessages(messages);
                    } else {
                        errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailingBaseAction.ACTION_USED_ACTIONS:
                	loadActions(aForm, req);
                	destination = mapping.findForward("action");
                	break;
                    
                default:
                    aForm.setAction(MailingBaseAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            System.err.println("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        if(destination != null &&  "list".equals(destination.getName())) {
        	try {
         	   setNumberOfRows(req, aForm);
         	   destination = mapping.findForward("loading");
         	  AbstractMap<String,Future> futureHolder = (AbstractMap<String, Future>)getBean("futureHolder");
			  String key =  FUTURE_TASK+"@"+ req.getSession(false).getId();
         	   if( !futureHolder.containsKey(key) ) {
         		  Future mailingListFuture = getMailingListFuture(req,aForm.getTypes(), aForm.isIsTemplate(), aForm ); 
         		  futureHolder.put(key,mailingListFuture);        		   
         	   }   	   
         	   
         	   if (futureHolder.containsKey(key) && futureHolder.get(key).isDone()) {
         		   req.setAttribute("mailinglist", futureHolder.get(key).get());
         		   destination = mapping.findForward("list");
         		   futureHolder.remove(key);
         		   aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
         		   saveMessages(req, aForm.getMessages());
                   saveErrors(req, aForm.getErrors());
         		   aForm.setMessages(null);
         		   aForm.setErrors(null);
         	   }
         	   else {
         		   if( aForm.getRefreshMillis() < 1000 ) { // raise the refresh time
         			   aForm.setRefreshMillis( aForm.getRefreshMillis() + 50 );
         		   }
         		   aForm.setError(false);
         	  }
         	   			
            } catch (Exception e) {
         	   AgnUtils.logger().error("getMailingList: "+e+"\n"+AgnUtils.getStackTrace(e));
                errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
                aForm.setError(true); // do not refresh when an error has been occurred
            } 
        }  

		if ("view".equals(destination.getName())) {
			if (aForm.getMediaEmail() != null) {
				aForm.setOldMailFormat(aForm.getMediaEmail().getMailFormat());
			}
		}

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            if(destination == null) {
                destination=mapping.findForward("list");
            }
        }

        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }
        
        return destination;
    }

    protected void validateMailing( MailingBaseForm form, HttpServletRequest req) throws CharacterEncodingValidationException {
    	CharacterEncodingValidator characterEncodingValidator = (CharacterEncodingValidator) getBean( "CharacterEncodingValidator");
        MailingDao mDao = (MailingDao) getBean("MailingDao");
        Mailing mailing = mDao.getMailing(form.getMailingID(), getCompanyID(req));

		characterEncodingValidator.validate( form, mailing);
    }
    
	protected void resetShowTemplate(HttpServletRequest req, MailingBaseForm aForm) {
		String showTemplate = req.getParameter("showTemplate");
		if(showTemplate == null || !showTemplate.equals("true")) {
			aForm.setShowTemplate(false);
		}
	}
    
    /**
     * Loads mailing. 
     */
    protected void loadMailing(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        MediatypeEmail type=null;
        MailingComponent comp=null;
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), getCompanyID(req));

        if(aMailing==null) {
            aMailing=(Mailing) getBean("Mailing");
            aMailing.init(getCompanyID(req), getWebApplicationContext());
            aMailing.setId(0);
            aForm.setMailingID(0);
        }
        
        aForm.setShortname(aMailing.getShortname());
        aForm.setDescription(aMailing.getDescription());
        aForm.setMailingType(aMailing.getMailingType());
        aForm.setMailinglistID(aMailing.getMailinglistID());
        aForm.setTemplateID(aMailing.getMailTemplateID());
        aForm.setTargetGroups(aMailing.getTargetGroups());
        aForm.setMediatypes(aMailing.getMediatypes());
        aForm.setArchived(aMailing.getArchived() != 0 );
        aForm.setCampaignID(aMailing.getCampaignID());
        aForm.setTargetMode( aMailing.getTargetMode() );
        aForm.setWorldMailingSend(aMailing.isWorldMailingSend());
        
        type=aMailing.getEmailParam();
        if(type!=null) {
            aForm.setEmailSubject(type.getSubject());
            aForm.setEmailOnepixel(type.getOnepixel());
            try {
                aForm.setEmailReplytoEmail(new InternetAddress(type.getReplyAdr()).getAddress());
            } catch (Exception e) {
                // do nothing
            }
            try {
                aForm.setEmailReplytoFullname(new InternetAddress(type.getReplyAdr()).getPersonal());
            } catch (Exception e) {
                // do nothing
            }
            aForm.setEmailLinefeed(type.getLinefeed());
            aForm.setEmailCharset(type.getCharset());
        }
        
        comp=aMailing.getTextTemplate();
        if(comp!=null) {
            aForm.setTextTemplate(comp.getEmmBlock());
        }
        
        comp=aMailing.getHtmlTemplate();
        if(comp!=null) {
            aForm.setHtmlTemplate(comp.getEmmBlock());
        }

        String entityName = aMailing.isIsTemplate() ? "template" : "mailing";
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": do load " + entityName + " " + aMailing.getShortname());

        AgnUtils.logger().info("loadMailing: mailing loaded");
    }

    /**
     * Removes target.
     */
    protected void removeTarget(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        Collection<Integer> allTargets=aForm.getTargetGroups();
        Integer tmpInt=null;
        
        if(allTargets!=null) {
            Iterator<Integer> aIt=allTargets.iterator();
            while(aIt.hasNext()) {
                tmpInt = aIt.next();
                if(aForm.getTargetID()==tmpInt.intValue()) {
                    allTargets.remove(tmpInt);
                    break;
                }
            }
        }
        
        if(allTargets.isEmpty()) {
            aForm.setTargetGroups(null);
        }
    }
    
    /**
     * Loads template settings.
     */
    protected void loadTemplateSettings(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        Mailing aTemplate=null;
        MailingComponent tmpComp=null;
        
        if(aForm.getTemplateID()!=0) {
            
            MailingDao dao=(MailingDao) getBean("MailingDao");
            aTemplate=dao.getMailing(aForm.getTemplateID(), this.getCompanyID(req));
            if(aTemplate!=null) {
            	copyTemplateSettingsToMailingForm(aTemplate, aForm);
            }
        }
    }

    protected void copyTemplateSettingsToMailingForm( Mailing template, MailingBaseForm mailingBaseForm){
        MailingComponent tmpComp=null;

        mailingBaseForm.setMailingType(template.getMailingType());
        mailingBaseForm.setMailinglistID(template.getMailinglistID());
        mailingBaseForm.setTargetMode(template.getTargetMode());
        mailingBaseForm.setTargetGroups(template.getTargetGroups());
        mailingBaseForm.setMediatypes(template.getMediatypes());
        mailingBaseForm.setArchived(template.getArchived() != 0);
        mailingBaseForm.setCampaignID(template.getCampaignID());
        mailingBaseForm.setNeedsTarget(template.getNeedsTarget());

        // load template for this mailing
        if((tmpComp=template.getHtmlTemplate())!=null) {
            mailingBaseForm.setHtmlTemplate(tmpComp.getEmmBlock());
        }

        if((tmpComp=template.getTextTemplate())!=null) {
            mailingBaseForm.setTextTemplate(tmpComp.getEmmBlock());
        }
        MediatypeEmail type=template.getEmailParam();
        if(type!=null) {
            mailingBaseForm.setEmailSubject(type.getSubject());
            mailingBaseForm.setEmailOnepixel(type.getOnepixel());
            try {
                mailingBaseForm.setEmailReplytoEmail(new InternetAddress(type.getReplyAdr()).getAddress());
            } catch (Exception e) {
                // do nothing
            }
            try {
                mailingBaseForm.setEmailReplytoFullname(new InternetAddress(type.getReplyAdr()).getPersonal());
            } catch (Exception e) {
                // do nothing
            }
            mailingBaseForm.setEmailLinefeed(type.getLinefeed());
            mailingBaseForm.setEmailCharset(type.getCharset());

        }
    }

    /**
     * saves current mailing in DB
     *
     * @param aForm 
     * @param req 
     */
    protected void saveMailing(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        Mailing aMailing=null;
        Mailing aTemplate=null;
        MediatypeEmail paramEmail=null;
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        boolean newMail = false;
        
        if(aForm.getMailingID()!=0) {
            aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        } else {
        	newMail = true;
        	if(aForm.getTemplateID()!=0) {
                aTemplate=mDao.getMailing(aForm.getTemplateID(), this.getCompanyID(req));
                aMailing=(Mailing)aTemplate.clone(this.getWebApplicationContext());
                aMailing.setId(0);
                aMailing.setMailTemplateID(aForm.getTemplateID());
                aMailing.setCompanyID(this.getCompanyID(req));
            }
        }
        
        if(aMailing==null) {
            aMailing=(Mailing) getBean("Mailing");
            aMailing.init(this.getCompanyID(req), this.getWebApplicationContext());
            aMailing.setId(0);
            aForm.setMailingID(0);
        }

		if (aForm.getMediaEmail().getMailFormat() == 0) {
			aForm.getMediaEmail().setHtmlTemplate("");
		} else if (aForm.getOldMailFormat() == 0) {
			if (aForm.getMediaEmail() != null && StringUtils.isEmpty(aForm.getMediaEmail().getHtmlTemplate())) {
				aForm.getMediaEmail().setHtmlTemplate("[agnDYN name=\"HTML-Version\"/]");
			}
		}

        aMailing.setIsTemplate(aForm.isIsTemplate());
        aMailing.setCampaignID(aForm.getCampaignID());
        aMailing.setDescription(aForm.getDescription());
        aMailing.setShortname(aForm.getShortname());
        aMailing.setMailinglistID(aForm.getMailinglistID());
        aMailing.setMailingType(aForm.getMailingType());
        aMailing.setArchived(aForm.isArchived()?1:0);
        aMailing.setTargetMode(aForm.getTargetMode());
        aMailing.setTargetGroups(aForm.getTargetGroups());
        aMailing.setMediatypes(aForm.getMediatypes());

        try {
            paramEmail=aMailing.getEmailParam();
            
            paramEmail.setSubject(aForm.getEmailSubject());
            paramEmail.setLinefeed(aForm.getEmailLinefeed());
            paramEmail.setCharset(aForm.getEmailCharset());
            paramEmail.setOnepixel(aForm.getEmailOnepixel());
           
            aForm.getMediaEmail().syncTemplate(aMailing, getWebApplicationContext());
            
            aMailing.buildDependencies(true, this.getWebApplicationContext());
        } catch (Exception e) {
            AgnUtils.logger().error("Error in save mailing id: "+aForm.getMailingID()+" msg: "+e.getMessage());
        }
      
     // validate the components
		if(!newMail) {
			Set<Entry<String,MailingComponent>>  componentEntries = aMailing.getComponents().entrySet();
			List<String[]> errorReports = new ArrayList<String[]>();
			Vector<String> outFailures = new Vector<String>();
			TAGCheck tagCheck =((TAGCheckFactory)getBean("TAGCheckFactory")).createTAGCheck(aMailing.getId());
			
			for(Entry<String,MailingComponent> mapEntry:componentEntries) {
				String tagName = mapEntry.getKey();
				MailingComponent component = mapEntry.getValue();
				String emmBlock = component.getEmmBlock();
				StringBuffer contentOutReport = new StringBuffer();
				if(!tagCheck.checkContent(emmBlock, contentOutReport, outFailures) ) {
					appendErrorsToList(tagName, errorReports, contentOutReport);
				}
				
			}
			
			if( errorReports.size() > 0) {
				throw new AgnTagException("error.template.dyntags",errorReports);
			}
			
		}
        
        mDao.saveMailing(aMailing);
        aForm.setMailingID(aMailing.getId());
        String entityName = aMailing.isIsTemplate() ? "template" : "mailing";
        if (newMail) {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": create " + entityName + " " + aMailing.getShortname());
        } else {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": edit " + entityName + " " + aMailing.getShortname());
        }
    }
    
    /**
     * Deletes mailing.
     */
    protected void deleteMailing(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        mDao.deleteMailing(aForm.getMailingID(), this.getCompanyID(req));
        String entityName = aForm.isIsTemplate() ? "template" : "mailing";
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": delete " + entityName + " " + aForm.getShortname());
    }
    
    protected void loadActions(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
    	MailingDao mDao=(MailingDao) getBean("MailingDao");
    	map = mDao.loadAction(aForm.getMailingID(), this.getCompanyID(req));
    	aForm.setActions(map);
    }
    
    /** 
     * load the data for the list view
     */
    public Future getMailingListFuture(HttpServletRequest req , String types, boolean isTemplate, MailingBaseForm mailingBaseForm ) throws IllegalAccessException, InstantiationException {

    	String sort = getSort(req, mailingBaseForm);
     	
     	String direction = req.getParameter("dir");
     	if( direction == null ) {
     		direction = mailingBaseForm.getOrder();     		
     	} else {
     		mailingBaseForm.setOrder(direction);
     	}
     	
     	String pageStr  = req.getParameter("page");
     	if ( pageStr == null || "".equals(pageStr.trim()) ) {
     		if ( mailingBaseForm.getPage() == null || "".equals(mailingBaseForm.getPage().trim())) {
     			mailingBaseForm.setPage("1");
     		}
     		pageStr = mailingBaseForm.getPage();
     		
     	}
     	else {
     		mailingBaseForm.setPage(pageStr);
     	}
     	
     	if( mailingBaseForm.isNumberOfRowsChanged() ) {
     		mailingBaseForm.setPage("1");
     		mailingBaseForm.setNumberOfRowsChanged(false);
     		pageStr = "1";
     	}
     	
     	int page = Integer.parseInt(pageStr);
     	
     	int rownums = mailingBaseForm.getNumberofRows();
     	MailingDao mDao=(MailingDao) getBean("MailingDao");
     	ExecutorService service = (ExecutorService) getWebApplicationContext().getBean("workerExecutorService");
    	Future future = service.submit(new MailingsQueryWorker(mDao, AgnUtils.getCompanyID(req), types, isTemplate, sort, direction, page, rownums ));
     	return future; 
    	
    }

	protected String getSort(HttpServletRequest request, MailingBaseForm aForm) {
		String sort = request.getParameter("sort");  
		 if( sort == null ) {
			 sort = aForm.getSort();			 
		 } else {
			 aForm.setSort(sort);
		 }
		return sort;
	}
   
	protected void appendErrorsToList(String blockName, List<String[]> errorReports,
			StringBuffer templateReport) {
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
    
}
