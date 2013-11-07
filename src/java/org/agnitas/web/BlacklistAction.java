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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.Recipient;
import org.agnitas.dao.BlacklistDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.service.BlacklistQueryWorker;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.BlacklistForm;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;


/**
 * Implementation of <strong>Action</strong> that handles Blacklists
 *
 * @author Alexander Schmoeller
 */

public class BlacklistAction extends StrutsActionBase {
	
	public static final int ACTION_DOWNLOAD = ACTION_LAST + 1;

    // --------------------------------------------------------- Public Methods


	/**
	 * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it).
	 * Return an <code>ActionForward</code> instance describing where and
	 * how control should be forwarded, or <code>null</code> if the response
	 * has already been completed.
	 *
	 * @param form
	 * @param req
	 * @param res
	 * @param mapping The ActionMapping used to select this instance
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a servlet exception occurs
	 * @return destination
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
				HttpServletRequest req, HttpServletResponse res)
				throws IOException, ServletException {
		ActionMessages errors = new ActionMessages();
		ActionForward destination=null;

		if(!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}
		Integer action;

		try {
			action = Integer.parseInt(req.getParameter("action"));
		} catch (Exception e) {
			action = BlacklistAction.ACTION_LIST;
		}		
        
		AgnUtils.logger().info("Action: "+ action );

		try {
			destination = executeIntern(mapping, form, req, errors, destination, action);
		} catch (Exception e) {
			AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			destination = mapping.findForward("error");
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(req, errors);
		}
		return destination;
	}

	
	// What the hell is that ??? Using huge execute methods with switch/ case in the other actions and now an executeIntern where you have again a switch/case ?
	// A really cool improvement ...
	protected ActionForward executeIntern(ActionMapping mapping, ActionForm form, HttpServletRequest req, ActionMessages errors, ActionForward destination, Integer action) {

		BlacklistDao	dao= (BlacklistDao) getBean("BlacklistDao");
		String email = null;

		BlacklistForm  blacklistForm = (BlacklistForm) form;
        ActionMessages messages = new ActionMessages();

		if( blacklistForm.getColumnwidthsList() == null ) {
			blacklistForm.setColumnwidthsList(getInitializedColumnWidthList(3));
		}
		switch( action ) {
			case BlacklistAction.ACTION_LIST:
				if(allowed("blacklist", req)) {
					errors.add(blacklistForm.getErrors());
					blacklistForm.setErrors(null);
					
					destination = prepareList(mapping, req, errors,
							destination, dao, blacklistForm);
			        		
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;
			case BlacklistAction.ACTION_SAVE:
				email = blacklistForm.getNewemail();
				
				if(email.equals("")) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.email.empty"));
					blacklistForm.setErrors(errors);
					destination = prepareList(mapping, req, errors, destination, dao, blacklistForm);
				} else {
					try {
						if(dao.insert(getCompanyID(req), email)) {
							updateUserStatus(email.trim(), req);
							
							messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
							blacklistForm.setMessages(messages);
						}
						destination = prepareList(mapping, req, errors, destination, dao, blacklistForm);
					} catch(Exception e ) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.blacklist.recipient.isalreadyblacklisted", blacklistForm.getNewemail()));
						blacklistForm.setErrors(errors);
						destination = prepareList(mapping, req, errors, destination, dao, blacklistForm);
					}
				}
				
				blacklistForm.setNewemail(null);
				break;
			case ACTION_CONFIRM_DELETE:
				destination=mapping.findForward("delete");
				break;
			case BlacklistAction.ACTION_DELETE:
				email = req.getParameter("delete");
				dao.delete(getCompanyID(req), email);
				destination = prepareList(mapping, req, errors, destination, dao, blacklistForm);
				
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
				blacklistForm.setMessages(messages);
				break;
			default:
				destination=mapping.findForward("list");
		}
		return destination;
	}


	private ActionForward prepareList(ActionMapping mapping,
			HttpServletRequest req, ActionMessages errors,
			ActionForward destination, BlacklistDao dao,
			BlacklistForm blacklistForm) {
		
		ActionMessages messages = null;
		
		try { 
			  setNumberOfRows(req, blacklistForm);
			   destination = mapping.findForward("loading");
			   if( blacklistForm.getCurrentFuture() == null ) {
				   blacklistForm.setCurrentFuture(getBlacklistFuture(dao, req, getWebApplicationContext() , blacklistForm));
			   }							
			  if ( blacklistForm.getCurrentFuture() != null  && blacklistForm.getCurrentFuture().isDone()) { 
					req.setAttribute("blackListEntries", blacklistForm.getCurrentFuture().get());
					destination = mapping.findForward("list");
					blacklistForm.setCurrentFuture(null);
					blacklistForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
					
					messages = blacklistForm.getMessages();
					
					if(messages != null && !messages.isEmpty()) {
						saveMessages(req, messages);
						blacklistForm.setMessages(null);
					}
			  }
			  else {
					if( blacklistForm.getRefreshMillis() < 1000 ) { // raise the refresh time
				 	blacklistForm.setRefreshMillis( blacklistForm.getRefreshMillis() + 50 );
					}
					blacklistForm.setError(false);
				}
		}
		catch(Exception e){
				AgnUtils.logger().error("blacklist: " + e + "\n" + AgnUtils.getStackTrace(e));
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
				blacklistForm.setError(true); // do not refresh when an error has been occurred
			}
		return destination;
	}
	
	/**
	 * Loads a customer by email.
	 * Set all bindings to status
	 * @param newEmail
	 * @param req
	 */
	protected void updateUserStatus(String newEmail, HttpServletRequest req) {
		ApplicationContext aContext=this.getWebApplicationContext();
		Recipient cust = (Recipient) aContext.getBean("Recipient");
		RecipientDao dao = (RecipientDao) aContext.getBean("RecipientDao");
		
		cust.setCompanyID(this.getCompanyID(req));
		int customerID = dao.findByKeyColumn(cust, "email", newEmail);
		cust.setCustomerID(customerID);
		
		cust.setCustParameters(dao.getCustomerDataFromDb(cust.getCompanyID(), cust.getCustomerID()));
		
		Hashtable hash = dao.loadAllListBindings(cust.getCompanyID(), cust.getCustomerID());
		Iterator it = hash.keySet().iterator();
		
		while(it.hasNext()) {
			String mailinglist = (String) it.next();
			Hashtable list = (Hashtable) hash.get(mailinglist);
			Iterator iter = list.keySet().iterator();
			while(iter.hasNext()) {
				String media = (String) iter.next();
				BindingEntry entry = (BindingEntry) list.get(media);
				entry.setUserStatus(BindingEntry.USER_STATUS_BLACKLIST);
				entry.setUserRemark("Blacklisted by " + AgnUtils.getAdmin(req).getAdminID());
				entry.updateStatusInDB(cust.getCompanyID());
			}
		}		
	}
	
protected Future getBlacklistFuture( BlacklistDao blacklistDao, HttpServletRequest request, ApplicationContext aContext, StrutsFormBase aForm  ) throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {
		
		String sort = getSort(request, aForm);
     	String direction = request.getParameter("dir");

     	int rownums = aForm.getNumberofRows();	
     	if( direction == null ) {
     		direction = aForm.getOrder();     		
     	} else {
     		aForm.setOrder(direction);
     	}
     	
     	String pageStr = request.getParameter("page");
     	if ( pageStr == null || "".equals(pageStr.trim()) ) {
     		if ( aForm.getPage() == null || "".equals(aForm.getPage().trim())) {
     				aForm.setPage("1");
     		} 
     		pageStr = aForm.getPage();
     	}
     	else {
     		aForm.setPage(pageStr);
     	}
     	
     	if( aForm.isNumberOfRowsChanged() ) {
     		aForm.setPage("1");
     		aForm.setNumberOfRowsChanged(false);
     		pageStr = "1";
     	}
     	
     	int companyID = AgnUtils.getCompanyID(request);
      	
     	ExecutorService service = (ExecutorService) aContext.getBean("workerExecutorService");
     	Future future = service.submit(new  BlacklistQueryWorker(blacklistDao,companyID,sort, direction,Integer.parseInt(pageStr),rownums) );
     	
     	return future;
     	
	}
	
	
}
