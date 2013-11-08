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
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.Recipient;
import org.agnitas.dao.RecipientDao;
import org.agnitas.service.RecipientQueryBuilder;
import org.agnitas.service.RecipientQueryWorker;
import org.agnitas.target.TargetNodeFactory;
import org.agnitas.target.TargetRepresentationFactory;
import org.agnitas.target.impl.TargetNodeDate;
import org.agnitas.target.impl.TargetNodeNumeric;
import org.agnitas.target.impl.TargetNodeString;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.ColumnInfoUtil;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.displaytag.pagination.PaginatedList;
import org.springframework.context.ApplicationContext;

/**
 * Handles all actions on profile fields.
 */
public class RecipientAction extends StrutsActionBase {

	
	public static final String FUTURE_TASK = "GET_RECIPIENT_LIST";
	public static final int ACTION_SEARCH = ACTION_LAST + 1;
	public static final int ACTION_OVERVIEW_START = ACTION_LAST + 2;
	
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
	@Override
    public ActionForward execute(ActionMapping mapping,
    ActionForm form,
    HttpServletRequest req,
    HttpServletResponse res)
    throws IOException, ServletException {

    	  	
        // Validate the request parameters specified by the user
        RecipientForm aForm = null;
        ActionMessages errors = new ActionErrors();
        ActionMessages messages = new ActionMessages();
        ActionForward destination = null;
        ApplicationContext aContext = this.getWebApplicationContext();
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        if(form != null) {
            aForm = (RecipientForm)form;
        } else {
            aForm = new RecipientForm();
        }

       	this.updateRecipientFormProperties(req, aForm);
        
        if(aForm.getDelete().isSelected()) {
            aForm.setAction(ACTION_CONFIRM_DELETE);
        }

        try {
            switch(aForm.getAction()) {
                case ACTION_LIST:
                    if(allowed("recipient.show", req)) {
                        destination = mapping.findForward("list");
                        if ( aForm.getColumnwidthsList() == null) {
                        	aForm.setColumnwidthsList(getInitializedColumnWidthList(5));
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_VIEW:
                    if(allowed("recipient.show", req)) {
                        if(req.getParameter("recipientID") != null) {
                            loadRecipient(aContext, aForm, req);
                            aForm.setAction(RecipientAction.ACTION_SAVE);
                        } else {
                            loadDefaults(aContext, aForm, req);
                            aForm.setAction(RecipientAction.ACTION_NEW);
                        }
                        destination = mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_SAVE:
                    if(allowed("recipient.change", req)) {
                    	if( req.getParameter("cancel.x") == null) {
                            saveRecipient(aContext, aForm, req);
                            aForm.setAction(RecipientAction.ACTION_LIST);
                            
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                            aForm.setMessages(messages);
                        }
                        destination = mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_NEW:
                    if(allowed("recipient.new", req)) {
                    	if( req.getParameter("cancel.x") == null) {
                            aForm.setRecipientID(0);
                            if(saveRecipient(aContext, aForm, req)){
                                aForm.setAction(RecipientAction.ACTION_LIST);
                                destination = mapping.findForward("list");
                                
                                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                                aForm.setMessages(messages);
                            } else {
                                errors.add("NewRecipient", new ActionMessage("error.subscriber.insert_in_db_error"));
                                aForm.setAction(RecipientAction.ACTION_VIEW);
                                destination = mapping.findForward("view");
                            }
                        } else {
                            destination = mapping.findForward("list");
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_CONFIRM_DELETE:
                    if(allowed("recipient.delete", req)) {
                        loadRecipient(aContext, aForm, req);
                        destination = mapping.findForward("delete");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ACTION_DELETE:
                    if(allowed("recipient.delete", req)) {
                        if(req.getParameter("kill") != null) {
                            deleteRecipient(aContext, aForm, req);
                            aForm.setAction(RecipientAction.ACTION_LIST);
                            destination = mapping.findForward("list");
                            
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                            aForm.setMessages(messages);
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                default:
                    aForm.setAction(RecipientAction.ACTION_LIST);
                    destination = mapping.findForward("list");
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        if( "list".equals(destination.getName())) {
           try {
        	   setNumberOfRows(req, aForm);
        	   destination = mapping.findForward("loading");
        	   AbstractMap<String,Future> futureHolder = (AbstractMap<String, Future>)getBean("futureHolder");
 			   String key =  FUTURE_TASK+"@"+ req.getSession(false).getId();
 			  
        	   if(!futureHolder.containsKey(key)) {
        		 Future recipientListFuture = getRecipientListFuture(req , aContext, aForm);
        		 futureHolder.put(key,recipientListFuture);
        	   }   	   
        	   
        	   if ( futureHolder.containsKey(key)  &&  futureHolder.get(key).isDone()) { 
        		   req.setAttribute("recipientList", futureHolder.get(key).get());
        		   destination = mapping.findForward("list");
        		   aForm.setAll(((PaginatedList)futureHolder.get(key).get()).getFullListSize());
        		   futureHolder.remove(key);
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
        	   AgnUtils.logger().error("recipientList: " + e + "\n" + AgnUtils.getStackTrace(e));
               errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
               aForm.setError(true); // do not refresh when an error has been occurred
           } 
        }
        
        
        // this is a hack for the recipient-search / recipient overview.
        if( "list".equals(destination.getName())) {
        	// check if we are in search-mode
        	if (!aForm.isOverview()) { 
        		// check if it is the last element in filter        	
        		if (aForm.getNumTargetNodes() == 0 && aForm.getListID() == 0 && aForm.getTargetID() == 0 && aForm.getUser_type().equals("E") && aForm.getUser_status() == 0) {        			
        			aForm.setAction(7);
        			destination = mapping.findForward("search");
        		}
        	}
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            // return new ActionForward(mapping.getForward());
        }
        
        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }
        
        return destination;
    }



	/**
     * Loads recipient.
     */
    protected void loadRecipient(ApplicationContext aContext, RecipientForm aForm, HttpServletRequest req) {
        Recipient cust = (Recipient) aContext.getBean("Recipient");
        RecipientDao dao = (RecipientDao) aContext.getBean("RecipientDao");
        Map data = null;
        Iterator i = null;

        cust.setCompanyID(this.getCompanyID(req));
        cust.setCustomerID(aForm.getRecipientID());
        data = dao.getCustomerDataFromDb(cust.getCompanyID(), cust.getCustomerID());
        i = data.keySet().iterator();
        while(i.hasNext()) {
            String key=(String) i.next();
            if(key.equals("gender")) {
                try {
                	aForm.setGender(Integer.parseInt((String) data.get("gender")));
                } catch(Exception e) {
                	aForm.setGender(2);
                }
            } else if(key.equals("title")) {
                aForm.setTitle((String) data.get(key));
            } else if(key.equals("firstname")) {
                aForm.setFirstname((String) data.get(key));
            } else if(key.equals("lastname")) {
                aForm.setLastname((String) data.get(key));
            } else if(key.equals("email")) {
                aForm.setEmail((String) data.get(key));
            } else if(key.equals("mailtype")) {
                try {
                	aForm.setMailtype(Integer.parseInt((String) data.get("mailtype")));
                } catch(Exception e) {
                	aForm.setMailtype(1);
                }
            } else {
                aForm.setColumn(key, data.get(key));
            }

        }
         AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": do  load recipient  " + cust.getCustomerID());
    }

    /**
     * Loads recipient.
     */
    protected void loadDefaults(ApplicationContext aContext, RecipientForm aForm, HttpServletRequest req) {
        Map tmp = null;

        /*
        aForm.setFirstname( "");
        aForm.setLastname( "");
        aForm.setTitle( "");
        aForm.setEmail( "");
        aForm.setGender( 0);
        */
        aForm.clearColumns();
        
        try {
            Map<String, Map> tmp2 = ColumnInfoUtil.getColumnInfo(aContext, this.getCompanyID(req), "%");

            Iterator<Map> it = tmp2.values().iterator();
            while(it.hasNext()) {
                tmp = it.next();
                String column = (String) tmp.get("column");
                aForm.setColumn(column, tmp.get("default"));
             }
        } catch (Exception e) { }
    }

    /**
     * Saves bindings.
     */
    protected void saveBindings(ApplicationContext aContext, RecipientForm recipientForm, HttpServletRequest request) {
        Recipient customer = (Recipient) aContext.getBean("Recipient");
        int companyID = getCompanyID(request);
        int customerID = recipientForm.getRecipientID();
        Map customerMailingLists = null;
        Map<Integer, Map<Integer, BindingEntry>> bindings = recipientForm.getAllBindings();
        Iterator<Integer> bindingsKeyIterator = bindings.keySet().iterator();

        customer.setCompanyID(companyID);
        customer.setCustomerID(customerID);
        customerMailingLists = customer.getAllMailingLists();
        while(bindingsKeyIterator.hasNext()) {
            Integer bindingsKey = bindingsKeyIterator.next();
            Map<Integer, BindingEntry> mailing = bindings.get(bindingsKey);
            Iterator<Integer> bindingEntryKeyIterator = mailing.keySet().iterator();

            while(bindingEntryKeyIterator.hasNext()) {
                Integer bindingEntryKey = bindingEntryKeyIterator.next();
                BindingEntry bindingEntry = mailing.get(bindingEntryKey);

                if(bindingEntry.getUserStatus() != 0) {
                    bindingEntry.setCustomerID(customerID);
                    bindingEntry.setApplicationContext(aContext);
                    if(!bindingEntry.saveBindingInDB(companyID, customerMailingLists)) {
		        AgnUtils.logger().error("saveBindings: Binding could not be saved");
                    }
                }
            }
        }
    }

    /**
     * Saves recipient.
     */
    protected boolean saveRecipient(ApplicationContext aContext, RecipientForm aForm, HttpServletRequest req) {
        Recipient cust = (Recipient) aContext.getBean("Recipient");
        RecipientDao dao = (RecipientDao) aContext.getBean("RecipientDao");
        Map data = null;
        Map column = null;
        Iterator i = null;
        int companyID = aForm.getCompanyID(req);

        cust.setCompanyID(this.getCompanyID(req));
        if(aForm.getRecipientID() != 0) {
            cust.setCustomerID(aForm.getRecipientID());

            data = dao.getCustomerDataFromDb(companyID, cust.getCustomerID());
            column = aForm.getColumnMap();
            i = column.keySet().iterator();
            while(i.hasNext()) {
                String key = (String) i.next();
                String value = (String) column.get(key);
                data.put(key, value);
            }
            data.put("gender", new Integer(aForm.getGender()).toString());
            data.put("title", aForm.getTitle());
            data.put("firstname", aForm.getFirstname());
            data.put("lastname", aForm.getLastname());
            data.put("email", aForm.getEmail());
            data.put("mailtype", new Integer(aForm.getMailtype()).toString());
            cust.setCustParameters(data);
            dao.updateInDB(cust);
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": edit recipient " + cust.getCustomerID());
        } else {
        	if(dao.mayAdd(companyID, 1) == false) {
        		return false;
        	}

            data = dao.getCustomerDataFromDb(companyID, aForm.getRecipientID());
            column = aForm.getColumnMap();
            i = column.keySet().iterator();
            while(i.hasNext()) {
                String key = (String) i.next();
                String value = (String) column.get(key);

                data.put(key, value);
            }
            data.put("gender", new Integer(aForm.getGender()).toString());
            data.put("title", aForm.getTitle());
            data.put("firstname", aForm.getFirstname());
            data.put("lastname", aForm.getLastname());
            data.put("email", aForm.getEmail());
            data.put("mailtype", new Integer(aForm.getMailtype()).toString());
            cust.setCustParameters(data);
            cust.setCustomerID(dao.insertNewCust(cust));
            aForm.setRecipientID(cust.getCustomerID());
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": create recipient " + cust.getCustomerID());
        }
        aForm.setRecipientID(cust.getCustomerID());

        saveBindings(aContext, aForm, req);
        updateCustBindingsFromAdminReq(cust, aContext, req);
        return true;
    }

    /**
     * Updates customer bindings.
     */
    public boolean updateCustBindingsFromAdminReq(Recipient cust, ApplicationContext aContext, HttpServletRequest req) {
        String aKey = null;
        String newKey = null;
        String aParam = null;
        int aMailinglistID;
        int oldSubStatus, newSubStatus;
        String tmpUT = null;
        String tmpOrgUT = null;
        Iterator aEnum = req.getParameterMap().keySet().iterator();
        BindingEntry aEntry = (BindingEntry) aContext.getBean("BindingEntry");

        while(aEnum.hasNext()) {
            aKey = (String)aEnum.next();
            if(aKey.startsWith("AGN_0_ORG_MT")) {
                oldSubStatus = Integer.parseInt((String) req.getParameter(aKey));
                aMailinglistID = Integer.parseInt(aKey.substring(12));
                newKey = "AGN_0_MTYPE" + aMailinglistID;
                aParam = (String) req.getParameter(newKey);
                if(aParam != null) {
                    newSubStatus = 1;
                } else {
                    newSubStatus = 0;
                }

                newKey = "AGN_0_MLUT" + aMailinglistID;
                tmpUT = (String) req.getParameter(newKey);
                newKey = "AGN_0_ORG_UT" + aMailinglistID;
                tmpOrgUT = (String) req.getParameter(newKey);

                if((newSubStatus != oldSubStatus) || (tmpUT.compareTo(tmpOrgUT) != 0)) {
                    aEntry.setMediaType(0);
                    aEntry.setCustomerID(cust.getCustomerID());
                    aEntry.setMailinglistID(aMailinglistID);
                    aEntry.setUserType(tmpUT);
                    if(newSubStatus == 0) { // Opt-Out
                        aEntry.setUserStatus(BindingEntry.USER_STATUS_ADMINOUT);
              //          aEntry.setUserRemark("Opt-Out by ADMIN");
                    } else { // Opt-In
                        aEntry.setUserStatus(BindingEntry.USER_STATUS_ACTIVE);
              //          aEntry.setUserRemark("Opt-In by ADMIN");
                    }
                    if(aEntry.updateBindingInDB(cust.getCompanyID()) == false) {
                        // aEntry.setUserType(BindingEntry.USER_TYPE_WORLD); // Bei Neu-Eintrag durch User entsprechenden Typ setzen
                        if(newSubStatus == 1) {
                            aEntry.insertNewBindingInDB(cust.getCompanyID());
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Removes recipient.
     */
    protected void deleteRecipient(ApplicationContext aContext, RecipientForm aForm, HttpServletRequest req) {
        Recipient cust = (Recipient) aContext.getBean("Recipient");
        RecipientDao dao = (RecipientDao) aContext.getBean("RecipientDao");

        cust.setCompanyID(this.getCompanyID(req));
        cust.setCustomerID(aForm.getRecipientID());
        dao.deleteCustomerDataFromDb(cust.getCompanyID(), cust.getCustomerID());
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": delete recipient " + cust.getCustomerID());
    }
    
    /**
     *
     * Get a list of recipients according to your filters
     * @param request
     * @param aContext
     * @param aForm
     * @return
     * @throws NumberFormatException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    
	public Future getRecipientListFuture( HttpServletRequest request, ApplicationContext aContext, RecipientForm aForm  ) throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {
		
		TargetRepresentationFactory targetRepresentationFactory = (TargetRepresentationFactory) getWebApplicationContext().getBean("TargetRepresentationFactory");  // TODO: Change this to real DI
		TargetNodeFactory targetNodeFactory = (TargetNodeFactory) getWebApplicationContext().getBean("TargetNodeFactory");  // TODO: Change this to real DI
		
		RecipientDao recipientDao = (RecipientDao) aContext.getBean("RecipientDao");
		String sqlStatementForCount = RecipientQueryBuilder.getSQLStatement(request, aContext, aForm, targetRepresentationFactory, targetNodeFactory, false );
		String sqlStatementForRows = RecipientQueryBuilder.getSQLStatement(request, aContext, aForm, targetRepresentationFactory, targetNodeFactory, true );
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
     	
     	ExecutorService service = (ExecutorService) aContext.getBean("workerExecutorService");
     	Future future = service.submit(new RecipientQueryWorker(recipientDao,sqlStatementForCount, sqlStatementForRows ,sort, direction, Integer.parseInt(pageStr), rownums, aForm.getAll() ));
     	
     	return future;
	}
    
	private boolean updateRecipientFormProperties(HttpServletRequest req, RecipientForm form) {
		int lastIndex = form.getNumTargetNodes();
        int removeIndex = -1;
        
        // If "add" was clicked, add new rule
        if (AgnUtils.parameterNotEmpty(req, "addTargetNode")) {
        	form.setColumnAndType(lastIndex, form.getColumnAndTypeNew());
        	form.setChainOperator(lastIndex, form.getChainOperatorNew());
        	form.setParenthesisOpened(lastIndex, form.getParenthesisOpenedNew());
        	form.setPrimaryOperator(lastIndex, form.getPrimaryOperatorNew());
        	form.setPrimaryValue(lastIndex, form.getPrimaryValueNew());
        	form.setParenthesisClosed(lastIndex, form.getParenthesisClosedNew());
        	form.setDateFormat(lastIndex, form.getDateFormatNew());
        	form.setSecondaryOperator(lastIndex, form.getSecondaryOperatorNew());
        	form.setSecondaryValue(lastIndex, form.getSecondaryValueNew());
        	
        	lastIndex++;
        }

		int nodeToRemove = -1;
		String nodeToRemoveStr = req.getParameter("targetNodeToRemove");
		if (AgnUtils.parameterNotEmpty(req, "targetNodeToRemove")) {
			nodeToRemove = Integer.parseInt(nodeToRemoveStr);
		}
        // Iterate over all target rules
        for(int index = 0; index < lastIndex; index++) {
        	if(index != nodeToRemove) {
        		String colAndType = form.getColumnAndType(index);
        		String column = colAndType.substring(0, colAndType.indexOf('#'));
        		String type = colAndType.substring(colAndType.indexOf('#') + 1);

    			form.setColumnName(index, column);
        		
        		if (type.equalsIgnoreCase("VARCHAR") || type.equalsIgnoreCase("VARCHAR2") || type.equalsIgnoreCase("CHAR")) {
        			form.setValidTargetOperators(index, TargetNodeString.getValidOperators());
        			form.setColumnType(index, TargetForm.COLUMN_TYPE_STRING);
        		} else if (type.equalsIgnoreCase("INTEGER") || type.equalsIgnoreCase("DOUBLE") || type.equalsIgnoreCase("NUMBER")) {
        			form.setValidTargetOperators(index, TargetNodeNumeric.getValidOperators());
        			form.setColumnType(index, TargetForm.COLUMN_TYPE_NUMERIC);
        		} else if (type.equalsIgnoreCase("DATE")) {
        			form.setValidTargetOperators(index, TargetNodeDate.getValidOperators());
        			form.setColumnType(index, TargetForm.COLUMN_TYPE_DATE);
        		}
        	} else {
        		if (removeIndex != -1)
        			throw new RuntimeException( "duplicate remove??? (removeIndex = " + removeIndex + ", index = " + index + ")");
        		removeIndex = index;
        	}
		}
        
        if (removeIndex != -1) {
        	form.removeRule(removeIndex);
        	return true;
        } else {
        	return false;
        }
	}
}
