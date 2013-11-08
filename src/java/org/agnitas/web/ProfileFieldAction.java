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
import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.ProfileField;
import org.agnitas.beans.factory.ProfileFieldFactory;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.target.TargetNode;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.KeywordList;
import org.agnitas.util.SafeString;
import org.agnitas.web.forms.StrutsFormBase;
import org.agnitas.service.ColumnInfoService;
import org.agnitas.service.ProfilefieldListQueryWorker;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Handles all actions on profile fields.
 */
public class ProfileFieldAction extends StrutsActionBase {
    private static final String FUTURE_TASK = "GET_PROFILEFIELD_LIST";

    
    // --------------------------------------------------------- Dependency Injection code
    protected ProfileFieldFactory profileFieldFactory;
    protected ColumnInfoService columnInfoService;
    protected KeywordList databaseKeywordList;
    protected ProfileFieldDao profileFieldDao;
    protected TargetDao targetDao;
    protected ExecutorService workerExecutorService;
    protected AbstractMap<String, Future> futureHolder;
        
    public void setProfileFieldFactory( ProfileFieldFactory factory) {
    	this.profileFieldFactory = factory;
    }
    
    public void setColumnInfoService( ColumnInfoService service) {
    	this.columnInfoService = service;
    }
    
    public void setDatabaseKeywordList( KeywordList keywordList) {
    	this.databaseKeywordList = keywordList;
    }
    
    public void setProfileFieldDao( ProfileFieldDao profileFieldDao) {
    	this.profileFieldDao = profileFieldDao;
    }
    
    public void setTargetDao( TargetDao targetDao) {
    	this.targetDao = targetDao;
    }
    
    public void setWorkerExecutorService( ExecutorService executorService) {
    	this.workerExecutorService = executorService;
    }
    
    public void setFutureHolder( AbstractMap<String, Future> futureHolder) {
    	this.futureHolder = futureHolder;
    }
    
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
            throws IOException, ServletException, Exception {

        // Validate the request parameters specified by the user
        ProfileFieldForm aForm=null;
        ActionMessages errors = new ActionErrors();
        ActionMessages messages = new ActionMessages();
        ActionForward destination=null;

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        aForm=(ProfileFieldForm)form;

        if(req.getParameter("delete")!=null) {
            aForm.setAction(ACTION_CONFIRM_DELETE);
        }

        AgnUtils.logger().info("Action: "+aForm.getAction());

        try {
            switch(aForm.getAction()) {
                case ProfileFieldAction.ACTION_LIST:
                    if(allowed("profileField.show", req)) {
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case ProfileFieldAction.ACTION_VIEW:
                    if(allowed("profileField.show", req)) {
                        if(req.getParameter("fieldname")!=null) {
                            loadProfileField(aForm, req);
                            aForm.setAction(ProfileFieldAction.ACTION_SAVE);
                        } else {
                            aForm.setAction(ProfileFieldAction.ACTION_NEW);
                            // For creating a new field set default values
                        	aForm.setFieldType("DOUBLE");                            
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    destination=mapping.findForward("view");
                    break;

                case ProfileFieldAction.ACTION_SAVE:
                    if(req.getParameter("save")!=null) {
                    	if( !isReservedWord(aForm.getFieldname())) {
	                        saveProfileField(aForm, req, errors);
	                        destination=mapping.findForward("list");
	                        
	                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    	} else {
                    		destination = mapping.findForward("view");
                    		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.profiledb.invalid_fieldname", aForm.getFieldname()));
                    	}
                    }
                    break;

                case ProfileFieldAction.ACTION_NEW:
                    if(allowed("profileField.show", req)) {
                        if(req.getParameter("save")!=null) {
                        	if( !isReservedWord( aForm.getFieldname())) {
	                            if(newProfileField(aForm, req, errors)){
	                                aForm.setAction(ProfileFieldAction.ACTION_LIST);
	                                destination=mapping.findForward("list");
	                                
	                                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
	                            } else {
	                                destination=mapping.findForward("view");
	                            }
                        	} else {
                        		destination = mapping.findForward("list");
                        		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.profiledb.invalid_fieldname", aForm.getFieldname()));
                        	}
                        }
                    }
                    break;

                case ProfileFieldAction.ACTION_CONFIRM_DELETE:
                    loadProfileField(aForm, req);
                    String tg_ret = checkForTargetGroups(aForm, req);

                    if(tg_ret.compareTo("ok")==0 ) {
                        aForm.setAction(ProfileFieldAction.ACTION_DELETE);
                        destination=mapping.findForward("delete");
                    } else {
                        if(tg_ret.compareTo("error")!=0 ) {
                            aForm.setAction(ProfileFieldAction.ACTION_LIST);
                            aForm.setTargetsDependent(tg_ret);
                            destination=mapping.findForward("delete_error");
                        } else {
                            aForm.setAction(ProfileFieldAction.ACTION_LIST);
                            destination=mapping.findForward("list");

                        }
                    }
                    break;

                case ProfileFieldAction.ACTION_DELETE:
                    if(req.getParameter("kill")!=null) {
                        deleteProfileField(aForm, req);
                        aForm.setAction(ProfileFieldAction.ACTION_LIST);
                        destination=mapping.findForward("list");
                        
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    }
                    break;

                default:
                    aForm.setAction(ProfileFieldAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
            throw new ServletException(e);
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            destination=mapping.findForward("list");
        }

        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }

        return destination;
    }

    /**
     * Loads a profile field.
     */
    protected void loadProfileField(ProfileFieldForm aForm, HttpServletRequest req) {

        int compID = this.getCompanyID(req);
        String description = "";
        String shortname = "";
        String fieldDefault = "";
        String fieldType = "";
        int length = 0;
        boolean isNull = true;
        Map<String, Map> list=null;

        try {
            list = this.columnInfoService.getColumnInfo(compID, aForm.getFieldname());
        } catch(Exception e) {
            AgnUtils.logger().error(e.getMessage());
            AgnUtils.logger().error(AgnUtils.getStackTrace(e));
            return;
        }

        Iterator<String> it = list.keySet().iterator();
        if(it.hasNext()) {
            Map col = list.get(it.next());

            if(col.get("column") != null) {
                if(col.size() > 3) {
                    shortname = (String) col.get("shortname");
                    fieldDefault = (String) col.get("default");
                    description = (String) col.get("description");
                } else {
                    shortname = "";
                    fieldDefault = "";
                    description = "";
                }
                fieldType = (String) col.get("type");
                length = ((Integer) col.get("length")).intValue();
                if(((Integer) col.get("nullable")).intValue() == 0) {
                    isNull = false;
                }
            }
        }
        aForm.setCompanyID(compID);
        aForm.setFieldType(fieldType);
        aForm.setDescription(description);
        aForm.setShortname(shortname);
        aForm.setFieldDefault(fieldDefault);
        aForm.setFieldLength(length);
        aForm.setFieldNull(isNull);
    }

    /**
     * Saves profile field.
     * @param errors 
     */
    protected void saveProfileField(ProfileFieldForm aForm, HttpServletRequest req, ActionMessages errors) {
    	String shortname = aForm.getShortname();
    	int companyID = getCompanyID(req);
        
    	String fieldname = SafeString.getSQLSafeString(aForm.getFieldname());
		ProfileField field = this.profileFieldDao.getProfileField(companyID, fieldname);

        if(field == null) {
            field = this.profileFieldFactory.newProfileField();
            field.setCompanyID(companyID);
            field.setColumn(fieldname);
        }

        field.setDescription(SafeString.getSQLSafeString(aForm.getDescription()));
		field.setShortname(SafeString.getSQLSafeString(shortname));
        field.setDefaultValue(SafeString.getSQLSafeString(aForm.getFieldDefault()));
        this.profileFieldDao.saveProfileField(field);
    }

    /**
     * Creates a profile field.
     * @param errors 
     */
    protected boolean newProfileField(ProfileFieldForm aForm, HttpServletRequest req, ActionMessages errors) throws Exception {
		// check if shortname is already in use
		String shortname = aForm.getShortname();
		int companyID = getCompanyID(req);
        
		ProfileField fieldByShortname = this.profileFieldDao.getProfileFieldByShortname(companyID, shortname);

		if ( fieldByShortname != null ) {
			errors.add("settings.NewProfileDB_Field", new ActionMessage("error.profiledb.exists"));
			return false;
		}
	
		if(!this.profileFieldDao.addProfileField(companyID,
					aForm.getFieldname(), 
					aForm.getFieldType(),
					aForm.getFieldLength(),
					aForm.getFieldDefault(),
					!aForm.isFieldNull())) {
				
			errors.add("settings.NewProfileDB_Field", new ActionMessage("error.profiledb.fieldname"));
			return false;
		}
		aForm.setFieldDefault(aForm.getFieldDefault());
		saveProfileField(aForm, req, errors);
		return true;
	}

	/**
	 * Removes a profile field.
	 */
	protected void deleteProfileField(ProfileFieldForm aForm, HttpServletRequest req) {
		String fieldname = SafeString.getSQLSafeString(aForm.getFieldname());

		this.profileFieldDao.removeProfileField(getCompanyID(req), fieldname); 
	}

    /**
     * Checks for target groups.
     */
    protected String checkForTargetGroups(ProfileFieldForm aForm, HttpServletRequest req) {

        int compID = getCompanyID(req);
        String fieldname = aForm.getFieldname();
        String ids = "";
        List targets = this.targetDao.getTargets(compID);

        for(int c=0; c < targets.size(); c++) {
            Object obj=targets.get(c);

            if(obj instanceof java.lang.String) {
	        continue;
            }
            Target aTarget=(Target) targets.get(c);

            if(aTarget != null && aTarget.getTargetStructure() != null) {
                ArrayList<TargetNode> aList = aTarget.getTargetStructure().getAllNodes();
                ListIterator<TargetNode> aIter = aList.listIterator();

                while(aIter.hasNext()) {
                    TargetNode aNode = aIter.next();

                    if(aNode.getPrimaryField().compareTo(fieldname)==0) {
                        if(targets.contains(aTarget.getTargetName())==false) {
                            targets.add(aTarget.getTargetName());
                            if(ids.equals("")) {
                                ids += aTarget.getTargetName();
                            } else {
                                ids += "<br>" + aTarget.getTargetName();
                            }
                        }
                    }
                }
            }
        }

        if(ids.length()>0) {
            return ids;
        } else {
            return "ok";
        }
    }

   private ActionForward prepareList(ActionMapping mapping,
			HttpServletRequest req, ActionMessages errors,
			ActionForward destination,ProfileFieldForm profileFieldForm) {
		ActionMessages messages = null;

		try {
			  setNumberOfRows(req, profileFieldForm);
			   destination = mapping.findForward("loading");
			   String key =  FUTURE_TASK+"@"+ req.getSession(false).getId();
			   if( ! futureHolder.containsKey(key) ) {
				   Future profilefieldFuture = getProfileFieldlistFuture( this.profileFieldDao, req, profileFieldForm);
				   futureHolder.put(key,profilefieldFuture);
			   }
			  if ( futureHolder.containsKey(key)  && futureHolder.get(key).isDone()) {
					req.setAttribute("profilefieldEntries", futureHolder.get(key).get());
					destination = mapping.findForward("list");
					futureHolder.remove(key);
					profileFieldForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
					messages = profileFieldForm.getMessages();

					if(messages != null && !messages.isEmpty()) {
						saveMessages(req, messages);
						profileFieldForm.setMessages(null);
					}
			  }
			  else {
					if( profileFieldForm.getRefreshMillis() < 1000 ) { // raise the refresh time
				 	profileFieldForm.setRefreshMillis( profileFieldForm.getRefreshMillis() + 50 );
					}
					profileFieldForm.setError(false);
				}
		}
		catch(Exception e){
				AgnUtils.logger().error("proflefield: " + e + "\n" + AgnUtils.getStackTrace(e));
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
				profileFieldForm.setError(true); // do not refresh when an error has been occurred
			}
		return destination;
	}

    protected Future getProfileFieldlistFuture(ProfileFieldDao profileFieldDao, HttpServletRequest request, StrutsFormBase aForm) throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {

        String sort = getSort(request, aForm);
        String direction = request.getParameter("dir");

        int rownums = aForm.getNumberofRows();
        if (direction == null) {
            direction = aForm.getOrder();
        } else {
            aForm.setOrder(direction);
        }

        String pageStr = request.getParameter("page");
        if (pageStr == null || "".equals(pageStr.trim())) {
            if (aForm.getPage() == null || "".equals(aForm.getPage().trim())) {
                aForm.setPage("1");
            }
            pageStr = aForm.getPage();
        } else {
            aForm.setPage(pageStr);
        }

        if (aForm.isNumberOfRowsChanged()) {
            aForm.setPage("1");
            aForm.setNumberOfRowsChanged(false);
            pageStr = "1";
        }

        int companyID = AgnUtils.getCompanyID(request);

        Future future = this.workerExecutorService.submit(new ProfilefieldListQueryWorker(profileFieldDao, companyID, sort, direction, Integer.parseInt(pageStr), rownums));

        return future;

    }
    
    protected boolean isReservedWord( String word) {
    	return this.databaseKeywordList.containsKeyWord(word);
    }
}
