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
import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.Company;
import org.agnitas.beans.ProfileField;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.factory.BindingEntryFactory;
import org.agnitas.beans.factory.RecipientFactory;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.service.ColumnInfoService;
import org.agnitas.service.RecipientBeanQueryWorker;
import org.agnitas.service.RecipientQueryBuilder;
import org.agnitas.target.TargetNodeFactory;
import org.agnitas.target.TargetRepresentationFactory;
import org.agnitas.target.impl.TargetNodeDate;
import org.agnitas.target.impl.TargetNodeNumeric;
import org.agnitas.target.impl.TargetNodeString;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CaseInsensitiveMap;
import org.agnitas.util.SqlPreparedStatementManager;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Handles all actions on recipients profile.
 */
public class RecipientAction extends StrutsActionBase {

	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(RecipientAction.class);

	public static final String COLUMN_GENDER = "gender";
	public static final String COLUMN_FIRSTNAME = "firstname";
	public static final String COLUMN_LASTNAME = "lastname";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_MAILTYPE = "mailtype";
	public static final String COLUMN_CUSTOMER_ID = "customer_id";

	public static final String FUTURE_TASK = "GET_RECIPIENT_LIST";
	public static final String DUMMY_RECIPIENT_FIELD = "dummy-recipient-field";
	public static final int ACTION_SEARCH = ACTION_LAST + 1;
	public static final int ACTION_OVERVIEW_START = ACTION_LAST + 2;
	public static final int ACTION_VIEW_WITHOUT_LOAD = ACTION_LAST + 3;
	public static final int ORG_ACTION_LAST = ACTION_LAST + 3;

	protected AbstractMap<String, Future<PaginatedListImpl<DynaBean>>> futureHolder = null;
	protected MailinglistDao mailinglistDao;
	protected TargetDao targetDao;
	protected RecipientDao recipientDao;
	protected TargetRepresentationFactory targetRepresentationFactory;
	protected TargetNodeFactory targetNodeFactory;
	protected ExecutorService executorService;
	protected RecipientQueryBuilder recipientQueryBuilder;
	protected ColumnInfoService columnInfoService;
	protected RecipientFactory recipientFactory;
	protected BindingEntryFactory bindingEntryFactory;
	protected DataSource dataSource;
	protected ConfigService configService;

	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Process the specified HTTP request, and create the corresponding HTTP response (or forward to another web component that will create it). Return an <code>ActionForward</code> instance
	 * describing where and how control should be forwarded, or <code>null</code> if the response has already been completed.<br>
	 * <br>
	 * ACTION_LIST: initializes the list of columns-widths according to number of columns selected by user;<br>
	 * checks if the number of selected columns is less or equal than max-value (currently 8):<br>
	 * - if max value exceeded - puts error to page and restores the list of selected columns from previous time<br>
	 * - if the selected column number <= max-value - stores the current selection to be used for future calls<br>
	 * forwards to list. <br>
	 * <br>
	 * ACTION_VIEW: loads recipient to form, puts list of mailinglists to request and forwards to "view" <br>
	 * <br>
	 * ACTION_SAVE: If the request parameter "cancel.x" is set - just forwards to "list". In other case saves changed or new recipient to the database; also saves the recipient bindings to
	 * mailinglists <br>
	 * <br>
	 * ACTION_NEW: if the request parameter "cancel.x" is set - just forwards to "list", otherwise forwards to the page where user can fill the data for a new recipient <br>
	 * <br>
	 * ACTION_CONFIRM_DELETE: loads recipient into form and forwards to jsp with confirmation about deletion of current recipient <br>
	 * <br>
	 * ACTION_DELETE: if the request parameter "kill" is set - removes the recipient from database (otherwise returns to previous page) <br>
	 * <br>
	 * ACTION_VIEW_WITHOUT_LOAD: forwards to view page without loading the recipient data from DB. Used as input page for struts-action in struts-config.xml. If the error occurs while saving the
	 * recipient - this action is used (as we don't need to load recipient data again) <br>
	 * <br>
	 * Any other ACTION_* would cause a forward to "list" <br>
	 * <br>
	 * If the destination is "list" - calls a FutureHolder to get the list of recipients. While FutureHolder is running destination is "loading". After FutureHolder is finished destination is "list".
	 * 
	 * @param form
	 *            data for the action filled by the jsp
	 * @param req
	 *            request from jsp.
	 * @param res
	 *            response
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet exception occurs
	 * 
	 * @return destination specified in struts-config.xml to forward to next jsp
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		// Validate the request parameters specified by the user
		RecipientForm aForm = null;
		ActionMessages errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		if (form != null) {
			aForm = (RecipientForm) form;
		} else {
			aForm = new RecipientForm();
		}

		this.updateRecipientFormProperties(req, aForm);

		if (aForm.getDelete().isSelected()) {
			aForm.setAction(ACTION_CONFIRM_DELETE);
		}

		if (aForm.getAction() == ACTION_LIST) {
			String[] selectedFields = aForm.getSelectedFields();
			if (selectedFields != null && selectedFields.length > 0) {
				aForm.setSelectedFields((String[]) ArrayUtils.removeElement(selectedFields, DUMMY_RECIPIENT_FIELD));
			}
		}

		try {
			switch (aForm.getAction()) {
			case ACTION_LIST:
				if (allowed("recipient.list", req)) {
					destination = mapping.findForward("list");
					int length = aForm.getSelectedFields().length;
					if (aForm.getColumnwidthsList() == null) {
						aForm.setColumnwidthsList(getInitializedColumnWidthList(length + 1));
					}
					if (length > 8) {
						aForm.setSelectedFields(aForm.getSelectedFieldsOld());
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.maximum.recipient.columns"));
						aForm.addErrors(errors);
					} else {
						aForm.setSelectedFieldsOld(aForm.getSelectedFields());
					}
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			case ACTION_VIEW:
				if (allowed("recipient.list", req)) {
					if (req.getParameter("recipientID") != null) {
						loadRecipient(aForm, req);
						aForm.setAction(RecipientAction.ACTION_SAVE);
					} else {
						loadDefaults(aForm, req);
						aForm.setAction(RecipientAction.ACTION_NEW);
					}
					req.setAttribute("mailinglists", mailinglistDao.getMailinglists(AgnUtils.getCompanyID(req)));
					destination = mapping.findForward("view");
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			case ACTION_SAVE:
				if (allowed("recipient.change", req)) {
					if (req.getParameter("cancel.x") == null) {
						saveRecipient(aForm, req);
						aForm.setAction(RecipientAction.ACTION_LIST);

						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
						aForm.setMessages(new ActionMessages(messages));
					}
					destination = mapping.findForward("list");
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			case ACTION_NEW:
				if (allowed("recipient.create", req)) {
					if (req.getParameter("cancel.x") == null) {
						aForm.setRecipientID(0);
						if (saveRecipient(aForm, req)) {
							aForm.setAction(RecipientAction.ACTION_LIST);
							destination = mapping.findForward("list");

							messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
							aForm.setMessages(new ActionMessages(messages));
						} else {
							errors.add("NewRecipient", new ActionMessage("error.subscriber.insert_in_db_error"));
							aForm.setAction(RecipientAction.ACTION_VIEW);
							destination = mapping.findForward("view");
						}
						req.setAttribute("mailinglists", mailinglistDao.getMailinglists(AgnUtils.getCompanyID(req)));
					} else {
						destination = mapping.findForward("list");
					}
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			case ACTION_CONFIRM_DELETE:
				if (allowed("recipient.delete", req)) {
					loadRecipient(aForm, req);
					destination = mapping.findForward("delete");
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			case ACTION_DELETE:
				if (allowed("recipient.delete", req)) {
					if (req.getParameter("kill") != null) {
						deleteRecipient(aForm, req);
						aForm.setAction(RecipientAction.ACTION_LIST);
						destination = mapping.findForward("list");

						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
						aForm.setMessages(new ActionMessages(messages));
					}
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;
			case ACTION_VIEW_WITHOUT_LOAD:
				if (allowed("recipient.list", req)) {
					req.setAttribute("mailinglists", mailinglistDao.getMailinglists(AgnUtils.getCompanyID(req)));
					destination = mapping.findForward("view");
					aForm.setAction(RecipientAction.ACTION_SAVE);
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			default:
				aForm.setAction(RecipientAction.ACTION_LIST);
				if (allowed("recipient.list", req)) {
					destination = mapping.findForward("list");
					if (aForm.getColumnwidthsList() == null) {
						int length = aForm.getSelectedFields().length;
						aForm.setColumnwidthsList(getInitializedColumnWidthList(length + 1));
					}
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
			}

		} catch (Exception e) {
			logger.error("execute: " + e, e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
		}

		if (destination != null && "list".equals(destination.getName())) {
			try {

				Map<String, String> fieldsMap = getRecipientFieldsNames(AgnUtils.getCompanyID(req), aForm.getAdminId());
				Set<String> recipientDbColumns = fieldsMap.keySet();
				req.setAttribute("fieldsMap", fieldsMap);

                if (!aForm.isNumberOfRowsChanged()) {
                    setNumberOfRows(req, aForm);
                }
				destination = mapping.findForward("loading");
				String key = FUTURE_TASK + "@" + req.getSession(false).getId();

				if (!futureHolder.containsKey(key)) {
					Future<PaginatedListImpl<DynaBean>> recipientListFuture = getRecipientListFuture(req, aForm, recipientDbColumns);
					futureHolder.put(key, recipientListFuture);
				}

				if (futureHolder.containsKey(key) && futureHolder.get(key).isDone()) {
					try {
						PaginatedListImpl<DynaBean> resultingList = futureHolder.get(key).get();
						
						req.setAttribute("recipientList", resultingList);
						req.setAttribute("mailinglists", mailinglistDao.getMailinglists(AgnUtils.getCompanyID(req)));
						req.setAttribute("targets", targetDao.getTargets(AgnUtils.getCompanyID(req)));

						// check the max recipients for company and change visualisation if needed
						aForm.setDeactivatePagination(false);
						Company company = AgnUtils.getCompany(req);
						int maxRecipients = company.getMaxRecipients();
						if (maxRecipients > 0 && resultingList.getFullListSize() > maxRecipients) {
							aForm.setPage("1");
							aForm.setDeactivatePagination(true);
						}

						destination = mapping.findForward("list");
						if (resultingList == null) {
							aForm.setDeactivatePagination(false);
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.errorneous_recipient_search"));
						} else {
							aForm.setAll(resultingList.getFullListSize());
							messages.add(aForm.getMessages());
                            aForm.setNumberOfRowsChanged(false);
							aForm.setMessages(null);
							errors.add(aForm.getErrors());
							aForm.resetErrors();
						}
					} finally {
						aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
						futureHolder.remove(key);
					}
				} else {
					if (aForm.getRefreshMillis() < 1000) { // raise the refresh time
						aForm.setRefreshMillis(aForm.getRefreshMillis() + 50);
					}
					aForm.setError(false);
				}
			} catch (Exception e) {
				logger.error("recipientList: " + e, e);
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
				aForm.setError(true); // do not refresh when an error has been occurred
			}

			if (aForm.isDeactivatePagination()) {
				Company company = AgnUtils.getCompany(req);
				int maxRecipients = company.getMaxRecipients();
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("recipient.search.max_recipients", maxRecipients));
			}
		}

		// this is a hack for the recipient-search / recipient overview.
		if (destination != null && "list".equals(destination.getName())) {
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
	 * Loads recipient data into a form. Uses recipientID property of aForm to identify the customer.
	 * 
	 * @param aForm
	 *            form to put recipient data into
	 * @param req
	 *            HTTP request
	 */
	protected void loadRecipient(RecipientForm aForm, HttpServletRequest req) {
		int companyID = AgnUtils.getCompanyID(req);
		CaseInsensitiveMap<Object> data = recipientDao.getCustomerDataFromDb(companyID, aForm.getRecipientID());
		for (String key : data.keySet()) {
            switch (key) {
                case COLUMN_GENDER:
                    try {
                        aForm.setGender(Integer.parseInt((String) data.get("gender")));
                    } catch (Exception e) {
                        aForm.setGender(2);
                    }
                    break;
                case COLUMN_TITLE:
                    aForm.setTitle((String) data.get(key));
                    break;
                case COLUMN_FIRSTNAME:
                    aForm.setFirstname((String) data.get(key));
                    break;
                case COLUMN_LASTNAME:
                    aForm.setLastname((String) data.get(key));
                    break;
                case COLUMN_EMAIL:
                    aForm.setEmail((String) data.get(key));
                    break;
                case COLUMN_MAILTYPE:
                    try {
                        aForm.setMailtype(Integer.parseInt((String) data.get(COLUMN_MAILTYPE)));
                    } catch (Exception e) {
                        aForm.setMailtype(1);
                    }
                    break;
                default:
                    aForm.setColumn(key, data.get(key));
                    break;
            }
		}
        String recipientName = aForm.getFirstname() + " " + aForm.getLastname();
        writeUserActivityLog(AgnUtils.getAdmin(req), "do load recipient, ID = " + aForm.getRecipientID(),
                "Recipient " + recipientName + " loaded");
	}

	/**
	 * Loads recipient columns list for specific admin (identified by adminID property of form) and puts it to form.
	 * 
	 * @param aForm
	 *            form
	 * @param req
	 *            HTTP request
	 */
	protected void loadDefaults(RecipientForm aForm, HttpServletRequest req) {
		aForm.clearColumns();

		try {
			List<ProfileField> list = columnInfoService.getColumnInfos(AgnUtils.getCompanyID(req), aForm.getAdminId());
			for (ProfileField profileField : list) {
				aForm.setColumn(profileField.getColumn(), profileField.getDefaultValue());
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Saves recipient bindings to mailinglists set by user on view-page.<br>
	 * The bindings-data is taken from recipientForm.
	 * 
	 * @param recipientForm
	 *            form
	 * @param request
	 *            HTTP request
     * @param isNewRecipient
	 */
	protected void saveBindings(RecipientForm recipientForm, HttpServletRequest request, boolean isNewRecipient) {
        Admin admin = AgnUtils.getAdmin(request);
		int companyID = AgnUtils.getCompanyID(request);
		int customerID = recipientForm.getRecipientID();
		Map<Integer, Map<Integer, BindingEntry>> bindings = recipientForm.getAllBindings();
		Iterator<Integer> bindingsKeyIterator = bindings.keySet().iterator();
		Map<Integer, Map<Integer, BindingEntry>> customerMailingLists = recipientDao.getAllMailingLists(customerID, companyID);

		while (bindingsKeyIterator.hasNext()) {
			Integer bindingsKey = bindingsKeyIterator.next();
			Map<Integer, BindingEntry> mailing = bindings.get(bindingsKey);
			Iterator<Integer> bindingEntryKeyIterator = mailing.keySet().iterator();

			while (bindingEntryKeyIterator.hasNext()) {
				Integer bindingEntryKey = bindingEntryKeyIterator.next();
				BindingEntry bindingEntry = mailing.get(bindingEntryKey);

				if (bindingEntry.getUserStatus() != 0) {
					bindingEntry.setCustomerID(customerID);
                    BindingEntry existingBindingEntry;
                    int newUserStatus = bindingEntry.getUserStatus();
                    int mailingListId = bindingEntry.getMailinglistID();
                    int existingUserStatus = 3;
                    String existingUserType = "";
                    String newUserType = bindingEntry.getUserType();

                    try {
                        existingBindingEntry = customerMailingLists.get(mailingListId).get(bindingEntryKey);
                        existingUserType = existingBindingEntry.getUserType();
                        existingUserStatus = existingBindingEntry.getUserStatus();

                    } catch (Exception e) {
                        logger.error("saveBindings: Existed binding could not be read");
                    }

                    if(!isNewRecipient){
                        if(!(newUserType.equals(existingUserType))){
                            writeUserActivityLog(admin,
                                    "edit recipient, ID = " + recipientForm.getRecipientID(),
                                    "Recipient type for mailingList with ID = " + mailingListId +
                                            " changed from " + getRecipientTypeByLetter(existingUserType) +
                                            " to " + getRecipientTypeByLetter(newUserType));
                        }

                        if(newUserStatus != existingUserStatus){
                            String action = newUserStatus < existingUserStatus ? " switch on" : " switch off";
                            writeUserActivityLog(admin,
                                    "edit recipient, ID = " + recipientForm.getRecipientID(),
                                    "Recipient Email for mailingList with ID = " + mailingListId + action);
                        }
                    }

					// this should be removed after refactoring of BindingEntry class
					if (bindingEntry.getBindingEntryDao() == null) {
						bindingEntry.setBindingEntryDao(bindingEntryFactory.getBindingEntryDao());
					}
					if (!bindingEntry.saveBindingInDB(companyID, customerMailingLists)) {
						logger.error("saveBindings: Binding could not be saved");
					}
				}
			}
		}
	}

	/**
	 * If customerID of aForm is not 0 - saves changed recipient to DB<br>
	 * If customerID is 0 - creates new recipient in DB (before that checks if max number of recipients is reached)<br>
	 * Recipient data is taken from form properties.<br>
	 * Also invokes method for saving recipient bindings to mailinglists.
	 * 
	 * @param aForm
	 *            form
	 * @param req
	 *            HTTP request
	 */
	protected boolean saveRecipient(RecipientForm aForm, HttpServletRequest req) {
        Admin admin = AgnUtils.getAdmin(req);
		Recipient cust = recipientFactory.newRecipient();
		int companyID = aForm.getCompanyID(req);
        boolean isNewRecipient = aForm.getRecipientID() == 0;

		cust.setCompanyID(AgnUtils.getCompanyID(req));
		if (!isNewRecipient) {
			cust.setCustomerID(aForm.getRecipientID());

            Map<String, Object> data = recipientDao.getCustomerDataFromDb(companyID, cust.getCustomerID());
            writeRecipientChangesLog(data, aForm, admin);

            Map<String, Object> column = aForm.getColumnMap();
            String value;
            for (String key : column.keySet()) {
                value = (String) column.get(key);
                data.put(key, value);
            }

            data.put(COLUMN_GENDER, Integer.toString(aForm.getGender()));
			data.put(COLUMN_TITLE, aForm.getTitle());
			data.put(COLUMN_FIRSTNAME, aForm.getFirstname());
			data.put(COLUMN_LASTNAME, aForm.getLastname());
			data.put(COLUMN_EMAIL, aForm.getEmail());
			data.put(COLUMN_MAILTYPE, Integer.toString(aForm.getMailtype()));

            storeSpecificFields(aForm, data, admin);
			cust.setCustParameters(data);
			recipientDao.updateInDB(cust);
		} else {
			if (!recipientDao.mayAdd(companyID, 1)) {
				return false;
			}

			Map<String, Object> data = recipientDao.getCustomerDataFromDb(companyID, aForm.getRecipientID());
			Map<String, Object> column = aForm.getColumnMap();
			for (String key : column.keySet()) {
				String value = (String) column.get(key);

				data.put(key, value);
			}
			data.put(COLUMN_GENDER, Integer.toString(aForm.getGender()));
			data.put(COLUMN_TITLE, aForm.getTitle());
			data.put(COLUMN_FIRSTNAME, aForm.getFirstname());
			data.put(COLUMN_LASTNAME, aForm.getLastname());
			data.put(COLUMN_EMAIL, aForm.getEmail());
			data.put(COLUMN_MAILTYPE, Integer.toString(aForm.getMailtype()));
			storeSpecificFields(aForm, data, admin);
			cust.setCustParameters(data);
			cust.setCustomerID(recipientDao.insertNewCust(cust));
			aForm.setRecipientID(cust.getCustomerID());
			writeUserActivityLog(admin, "create recipient, ID = " + cust.getCustomerID(),
                    "Recipient " + cust.getFirstname() + " " + cust.getLastname() + " created");
		}
		aForm.setRecipientID(cust.getCustomerID());

		saveBindings(aForm, req, isNewRecipient);
		updateCustBindingsFromAdminReq(cust, req);
		return true;
	}

    /**
     * Get a text representation of mail format
     *
     * @param mailFormatId mail format id from database
     * @return text representation of mail format
     */
    protected String getMailFormatById(int mailFormatId){
        switch(mailFormatId) {
            case 0:
                return "Text";
            case 1:
                return "HTML";
            case 2:
                return "OfflineHTML";
            default:
                return "not set";
        }
    }

    /**
     *  Get a text representation of salutation
     *
     * @param genderId gender id from database
     * @return salutation text
     */
    protected String getSalutationById(int genderId){
        switch (genderId){
            case 0:
                return "Mr.";
            case 1:
                return "Mrs.";
            case 2:
                return "Unknown";
            default:
                return "not set";
        }
    }

    /**
     *  Get a text representation of recipient type
     *
     * @param letter recipient type letter
     * @return text representation of recipient type
     */
    protected String getRecipientTypeByLetter(String letter){
        switch (letter){
            case "E":
                return "All";
            case "A":
                return "Administrator";
            case "T":
                return "Test recipient";
            case "W":
                return "Normal recipient";
            default:
                return "not set";
        }
    }

	/**
	 * Method for storing specific fields of recipient. Can be overridden in subclass. For OpenEMM the method currently doesn't do anything.
	 * 
	 * @param aForm
	 *            current form
	 * @param customerFields
	 *            the map of recipients fields which will be later used for saving recipient to DB
     * @param admin
     *            current form
	 */
	protected void storeSpecificFields(RecipientForm aForm, Map<String, Object> customerFields, Admin admin) {
	}

	/**
	 * Gets the list of recipient fields
	 * 
	 * @param companyId
	 *            current company ID
	 * @return recipient fields in a form of map: column -> column-shortname
	 * @throws Exception
	 *             if the exception happens in columnInfoService class
	 */
	protected Map<String, String> getRecipientFieldsNames(int companyId, int adminId) throws Exception {
		CaseInsensitiveMap<ProfileField> columnInfoMap = columnInfoService.getColumnInfoMap(companyId, adminId);
		Map<String, String> fieldsMap = new LinkedHashMap<String, String>();
		// we need predefined order for default columns: gender, firstname, lastname.
		fieldsMap.put(COLUMN_GENDER, columnInfoMap.get(COLUMN_GENDER).getShortname());
		fieldsMap.put(COLUMN_FIRSTNAME, columnInfoMap.get(COLUMN_FIRSTNAME).getShortname());
		fieldsMap.put(COLUMN_LASTNAME, columnInfoMap.get(COLUMN_LASTNAME).getShortname());
		columnInfoMap.remove(COLUMN_GENDER);
		columnInfoMap.remove(COLUMN_FIRSTNAME);
		columnInfoMap.remove(COLUMN_LASTNAME);
		// put the rest of columns to the map
		for (String column : columnInfoMap.keySet()) {
			fieldsMap.put(column, columnInfoMap.get(column).getShortname());
		}
		return fieldsMap;
	}

	/**
	 * Updates customer bindings with the data taken from request
	 * 
	 * @param cust
	 *            recipient bean
	 * @param req
	 *            HTTP request holding parameters for binding entries
	 */
	@Deprecated
	public boolean updateCustBindingsFromAdminReq(Recipient cust, HttpServletRequest req) {
		String aKey = null;
		String newKey = null;
		String aParam = null;
		int aMailinglistID;
		int oldSubStatus, newSubStatus;
		String tmpUT = null;
		String tmpOrgUT = null;
		Iterator<String> aEnum = req.getParameterMap().keySet().iterator();
		BindingEntry bindingEntry = bindingEntryFactory.newBindingEntry();

		while (aEnum.hasNext()) {
			aKey = aEnum.next();
			if (aKey.startsWith("AGN_0_ORG_MT")) {
				oldSubStatus = Integer.parseInt(req.getParameter(aKey));
				aMailinglistID = Integer.parseInt(aKey.substring(12));
				newKey = "AGN_0_MTYPE" + aMailinglistID;
				aParam = req.getParameter(newKey);
				if (aParam != null) {
					newSubStatus = 1;
				} else {
					newSubStatus = 0;
				}

				newKey = "AGN_0_MLUT" + aMailinglistID;
				tmpUT = req.getParameter(newKey);
				newKey = "AGN_0_ORG_UT" + aMailinglistID;
				tmpOrgUT = req.getParameter(newKey);

				if ((newSubStatus != oldSubStatus) || (tmpUT.compareTo(tmpOrgUT) != 0)) {
					bindingEntry.setMediaType(0);
					bindingEntry.setCustomerID(cust.getCustomerID());
					bindingEntry.setMailinglistID(aMailinglistID);
					bindingEntry.setUserType(tmpUT);
					if (newSubStatus == 0) { // Opt-Out
						bindingEntry.setUserStatus(BindingEntry.USER_STATUS_ADMINOUT);
						// bindingEntry.setUserRemark("Opt-Out by ADMIN");
					} else { // Opt-In
						bindingEntry.setUserStatus(BindingEntry.USER_STATUS_ACTIVE);
						// bindingEntry.setUserRemark("Opt-In by ADMIN");
					}
					if (bindingEntry.updateBindingInDB(cust.getCompanyID()) == false) {
						// bindingEntry.setUserType(BindingEntry.USER_TYPE_WORLD); // Bei Neu-Eintrag durch User entsprechenden Typ setzen
						if (newSubStatus == 1) {
							bindingEntry.insertNewBindingInDB(cust.getCompanyID());
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Removes recipient from a database. The ID of recipient is taken from form property recipientID.
	 * 
	 * @param aForm
	 *            form
	 * @param req
	 *            HTTP request
	 */
	protected void deleteRecipient(RecipientForm aForm, HttpServletRequest req) {
		int companyID = AgnUtils.getCompanyID(req);
        String recipientName = aForm.getFirstname() + " " + aForm.getLastname();
		recipientDao.deleteCustomerDataFromDb(companyID, aForm.getRecipientID());
		writeUserActivityLog(AgnUtils.getAdmin(req), "delete recipient, ID = " + aForm.getRecipientID(),
                "Recipient " + recipientName + " deleted");
	}

	/**
	 * Get a list of recipients according to filters user selected: mailinglist, target group, recipient status, recipient type, advanced search parameters, selected columns to show.
	 * 
	 * @param request
	 *            HTTP request
	 * @param aForm
	 *            recipient form
	 * @return the Future containing the paginated list of matching recipient
	 * @throws Exception
	 */
	public Future<PaginatedListImpl<DynaBean>> getRecipientListFuture(HttpServletRequest request, RecipientForm aForm, Set<String> recipientDbColumns) throws Exception {

		String pageStr = request.getParameter("page");
		if (pageStr == null || "".equals(pageStr.trim())) {
			if (aForm.getPage() == null || "".equals(aForm.getPage().trim())) {
				aForm.setPage("1");
			}
			pageStr = aForm.getPage();
		} else {
			aForm.setPage(pageStr);
		}

		String sort = getSort(request, aForm);
		String direction = request.getParameter("dir");

		int rownums = aForm.getNumberOfRows();
        if (rownums <= 0) {
        	rownums = StrutsFormBase.DEFAULT_NUMBER_OF_ROWS;
        }
		if (direction == null) {
			direction = aForm.getOrder();
		} else {
			aForm.setOrder(direction);
		}

		if (aForm.isNumberOfRowsChanged()) {
			aForm.setPage("1");
			pageStr = "1";
		}

		SqlPreparedStatementManager sqlStatementManagerForDataSelect = recipientQueryBuilder.getSQLStatement(request, aForm, targetRepresentationFactory, targetNodeFactory);
		String selectDataStatement = sqlStatementManagerForDataSelect.getPreparedSqlString().replaceAll("cust[.]bind", "bind").replace("lower(cust.email)", "cust.email");
		if (logger.isInfoEnabled()) {
			logger.info("Recipient Select data SQL statement: " + selectDataStatement);
		}

		Future<PaginatedListImpl<DynaBean>> future = executorService.submit(new RecipientBeanQueryWorker(recipientDao, aForm.getCompanyID(request), recipientDbColumns,
				selectDataStatement, sqlStatementManagerForDataSelect.getPreparedSqlParameters(), sort, direction, Integer.parseInt(pageStr), rownums, aForm.getAll()));

		return future;
	}

	private boolean updateRecipientFormProperties(HttpServletRequest req, RecipientForm form) {
		int lastIndex = form.getNumTargetNodes();
		int removeIndex = -1;

		// If "add" was clicked, add new rule
		if (AgnUtils.parameterNotEmpty(req, "addTargetNode") || (AgnUtils.parameterNotEmpty(req, "Update") && !StringUtils.isEmpty(form.getPrimaryValueNew()))) {
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
		for (int index = 0; index < lastIndex; index++) {
			if (index != nodeToRemove) {
				String column = form.getColumnAndType(index);
				if (column.contains("#")) {
					column = column.substring(0, column.indexOf('#'));
				}
				String type = "unknownType";
				try {
					type = columnInfoService.getColumnInfo(AgnUtils.getCompanyID(req), column).getDataType();
				} catch (Exception e) {
					logger.error("Cannot find fieldtype for companyId " + AgnUtils.getCompanyID(req) + " and column '" + column + "'", e);
				}

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
				if (removeIndex != -1) {
					throw new RuntimeException("duplicate remove??? (removeIndex = " + removeIndex + ", index = " + index + ")");
				}
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


    /**
     * Compare existed and new recipient data and write changes in user log
     *
     * @param aForm the form passed from the jsp
     * @param data existed recipient data
     * @param admin Admin
     */
    protected void writeRecipientChangesLog(Map<String, Object> data, RecipientForm aForm, Admin admin){
        try {
            ArrayList<String> handledKeys = new ArrayList<>();
            String recipientId = (String) data.get(COLUMN_CUSTOMER_ID);
            Map<String, Object> column = aForm.getColumnMap();

            //Log salutation changes
            int existedGender = Integer.valueOf((String) data.get(COLUMN_GENDER));
            handledKeys.add(COLUMN_GENDER);
            int newGender = aForm.getGender();
            if (existedGender != aForm.getGender()){
                writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                        "Recipient salutation changed from " + getSalutationById(existedGender) +
                                " to " + getSalutationById(newGender));
            }

            //Log title changes
            String existedTitle = (String) data.get(COLUMN_TITLE);
            handledKeys.add(COLUMN_TITLE);
            String newTitle = aForm.getTitle();
            if(!existedTitle.equals(newTitle)){
                if((existedTitle.length()<=0)&&(newTitle.length()>0)){
                    writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                            "Recipient title " + newTitle + " added");
                }
                if((existedTitle.length()>0)&&(newTitle.length()<=0)){
                    writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                            "Recipient title " + existedTitle + " removed");
                }
                if((existedTitle.length()>0)&&(newTitle.length()>0)){
                    writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                            "Recipient title changed from " + existedTitle + " to " + newTitle);
                }
            }

            //Log first name changes
            String existedFirstName = (String) data.get(COLUMN_FIRSTNAME);
            handledKeys.add(COLUMN_FIRSTNAME);
            String newFirstName = aForm.getFirstname();
            if(!existedFirstName.equals(newFirstName)){
                if((existedFirstName.length()<=0)&&(newFirstName.length()>0)){
                    writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                            "Recipient first name " + newFirstName + " added");
                }
                if((existedFirstName.length()>0)&&(newFirstName.length()<=0)){
                    writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                            "Recipient first name " + existedFirstName + " removed");
                }
                if((existedFirstName.length()>0)&&(newFirstName.length()>0)){
                    writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                            "Recipient first name changed from " + existedFirstName + " to " + newFirstName);
                }
            }

            //Log last name changes
            String existedLastName = (String) data.get(COLUMN_LASTNAME);
            handledKeys.add(COLUMN_LASTNAME);
            String newLastName = aForm.getLastname();
            if(!existedLastName.equals(newLastName)){
                if((existedLastName.length()<=0)&&(newLastName.length()>0)){
                    writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                            "Recipient last name " + newLastName + " added");
                }
                if((existedLastName.length()>0)&&(newLastName.length()<=0)){
                    writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                            "Recipient last name " + existedLastName + " removed");
                }
                if((existedLastName.length()>0)&&(newLastName.length()>0)){
                    writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                            "Recipient last name changed from " + existedLastName + " to " + newLastName);
                }
            }

            //Log email changes
            String existedEmail = (String) data.get(COLUMN_EMAIL);
            handledKeys.add(COLUMN_EMAIL);
            String newEmail = aForm.getEmail();
            if (!existedEmail.equals(newEmail)){
                writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                        "Recipient email changed from " + existedEmail + " to " + newEmail);
            }

            //Log mailType changes
            int existedMailType = Integer.valueOf((String)  data.get(COLUMN_MAILTYPE));
            handledKeys.add(COLUMN_MAILTYPE);
            int newMailType = aForm.getMailtype();
            if (existedMailType != newMailType){
                writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                        "Recipient mailtype changed from " + getMailFormatById(existedMailType) +
                                " to " + getMailFormatById(newMailType));
            }

            //Log additional data changes
            String existedValue, newValue;
            for (String key : column.keySet()) {
                if (!handledKeys.contains(key)){
                    newValue = (String) column.get(key);
                    existedValue = (String) data.get(key);
                    if(!existedValue.equals(newValue)){
                        if((existedValue.length()<=0)&&(newValue.length()>0)){
                            writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                                    "Recipient " + key + " " + newValue + " added");
                        }
                        if((existedValue.length()>0)&&(newValue.length()<=0)){
                            writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                                    "Recipient " + key + " " + existedValue + " removed");
                        }
                        if((existedValue.length()>0)&&(newValue.length()>0)){
                            writeUserActivityLog(admin, "edit recipient, ID = " + recipientId,
                                    "Recipient " + key + " changed from " + existedValue + " to " + newValue);
                        }
                    }
                }
            }
            if (logger.isInfoEnabled()){
                logger.info("saveOpenEMMRecipient: save recipient " + recipientId);
            }
        } catch (NumberFormatException e) {
            if (logger.isInfoEnabled()){
                logger.error("Log Recipient changes error" + e);
            }
        }
    }

	public void setFutureHolder(AbstractMap<String, Future<PaginatedListImpl<DynaBean>>> futureHolder) {
		this.futureHolder = futureHolder;
	}

	public void setMailinglistDao(MailinglistDao mailinglistDao) {
		this.mailinglistDao = mailinglistDao;
	}

	public void setTargetDao(TargetDao targetDao) {
		this.targetDao = targetDao;
	}

	public void setRecipientDao(RecipientDao recipientDao) {
		this.recipientDao = recipientDao;
	}

	public void setTargetRepresentationFactory(TargetRepresentationFactory targetRepresentationFactory) {
		this.targetRepresentationFactory = targetRepresentationFactory;
	}

	public void setTargetNodeFactory(TargetNodeFactory targetNodeFactory) {
		this.targetNodeFactory = targetNodeFactory;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	@Required
	public void setRecipientQueryBuilder(RecipientQueryBuilder recipientQueryBuilder) {
		this.recipientQueryBuilder = recipientQueryBuilder;
	}

	public void setColumnInfoService(ColumnInfoService columnInfoService) {
		this.columnInfoService = columnInfoService;
	}

	public void setRecipientFactory(RecipientFactory recipientFactory) {
		this.recipientFactory = recipientFactory;
	}

	public void setBindingEntryFactory(BindingEntryFactory bindingEntryFactory) {
		this.bindingEntryFactory = bindingEntryFactory;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}