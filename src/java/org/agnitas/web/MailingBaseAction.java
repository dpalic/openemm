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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import org.agnitas.service.MailingsQueryWorker;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.agnitas.web.forms.MailingBaseForm;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


/**
 * Implementation of <strong>Action</strong> that handles Mailings
 *
 * @author Martin Helff
 */

public class MailingBaseAction extends StrutsActionBase {

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
                            aForm.clearData(getCompanyID(req), getDefaultMediaType(req));
                            aForm.setMailingID(0);
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
                        saveMailing(aForm, req);
                        showTemplates=aForm.isShowTemplate();
                        loadMailing(aForm, req);
                        aForm.setShowTemplate(showTemplates);
                        destination=mapping.findForward("view");

                        // Show "changes saved"
                    	messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                    } else {
                        errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case MailingBaseAction.ACTION_SELECT_TEMPLATE:
                    loadTemplateSettings(aForm, req);
                    aForm.setAction(MailingBaseAction.ACTION_SAVE);
                    destination=mapping.findForward("view");
                    break;
                    
                case MailingBaseAction.ACTION_CLONE_AS_MAILING:
                    if(allowed("mailing.copy", req)) {
                    
                        int tmpTemplateID=aForm.getMailingID();
                        int tmpMlId=aForm.getMailinglistID();
                        String sname = aForm.getShortname();
                        int tmpFormat=aForm.getMediaEmail().getMailFormat();
                        boolean tmpl=aForm.isIsTemplate();
                        aForm.clearData(this.getCompanyID(req), this.getDefaultMediaType(req));
                        aForm.setTemplateID(tmpTemplateID);
                        aForm.setIsTemplate(tmpl);
                        loadTemplateSettings(aForm, req);
                        aForm.setMailinglistID(tmpMlId);
                        aForm.getMediaEmail().setMailFormat(tmpFormat);
                        aForm.setMailingID(0);
                        aForm.setAction(MailingBaseAction.ACTION_SAVE);
                        aForm.setShortname(new String(SafeString.getLocaleString("CopyOf", (Locale)req.getSession().getAttribute(Globals.LOCALE_KEY)) + " " + sname));
                        aForm.setDescription(SafeString.getLocaleString("default.description", (Locale)req.getSession().getAttribute(Globals.LOCALE_KEY)));
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
                        
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
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
         	   
         	   if( aForm.getCurrentFuture() == null ) {
         		  aForm.setCurrentFuture(getMailingListFuture(req,aForm.getTypes(), aForm.isIsTemplate(), aForm ));
         	   }   	   
         	   
         	   if ( aForm.getCurrentFuture() != null && aForm.getCurrentFuture().isDone()) {
         		   
         		   req.setAttribute("mailinglist", aForm.getCurrentFuture().get());
         		   destination = mapping.findForward("list");
         		   aForm.setCurrentFuture(null);
         		   aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
         		   
         		   saveMessages(req, aForm.getMessages());
         		   aForm.setMessages(null);
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
        MailingComponent comp=null;
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        Mailing aMailing=mDao.getMailing(aForm.getMailingID(), getCompanyID(req));
        MediatypeEmail type=null;

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
        
        type=aMailing.getEmailParam(this.getWebApplicationContext());
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
        
        AgnUtils.logger().info("loadMailing: mailing loaded");
    }
    
    /**
     * Removes target.
     */
    protected void removeTarget(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        Collection allTargets=aForm.getTargetGroups();
        Integer tmpInt=null;
        
        if(allTargets!=null) {
            Iterator aIt=allTargets.iterator();
            while(aIt.hasNext()) {
                tmpInt=(Integer)aIt.next();
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
                aForm.setMailingType(aTemplate.getMailingType());
                aForm.setMailinglistID(aTemplate.getMailinglistID());
                aForm.setTargetMode(aTemplate.getTargetMode());
                aForm.setTargetGroups(aTemplate.getTargetGroups());
                aForm.setMediatypes(aTemplate.getMediatypes());
                aForm.setArchived(aTemplate.getArchived() != 0);
                aForm.setCampaignID(aTemplate.getCampaignID());
                
                // load template for this mailing
                if((tmpComp=aTemplate.getHtmlTemplate())!=null) {
                    aForm.setHtmlTemplate(tmpComp.getEmmBlock());
                }
                
                if((tmpComp=aTemplate.getTextTemplate())!=null) {
                    aForm.setTextTemplate(tmpComp.getEmmBlock());
                }
                MediatypeEmail type=aTemplate.getEmailParam(this.getWebApplicationContext());
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
            }
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

        if(aForm.getMailingID()!=0) {
            aMailing=mDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        } else {
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
            paramEmail=aMailing.getEmailParam(this.getWebApplicationContext());
            
            paramEmail.setSubject(aForm.getEmailSubject());
            paramEmail.setLinefeed(aForm.getEmailLinefeed());
            paramEmail.setCharset(aForm.getEmailCharset());
            paramEmail.setOnepixel(aForm.getEmailOnepixel());
           
            aForm.getMediaEmail().syncTemplate(aMailing, getWebApplicationContext());
            
            aMailing.buildDependencies(true, this.getWebApplicationContext());
        } catch (Exception e) {
            AgnUtils.logger().error("Error in save mailing id: "+aForm.getMailingID()+" msg: "+e.getMessage());
        }
      
        mDao.saveMailing(aMailing);
        aForm.setMailingID(aMailing.getId());
    }
    
    /**
     * Deletes mailing.
     */
    protected void deleteMailing(MailingBaseForm aForm, HttpServletRequest req) throws Exception {
        
        MailingDao mDao=(MailingDao) getBean("MailingDao");
        mDao.deleteMailing(aForm.getMailingID(), this.getCompanyID(req));
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
   
    
}
