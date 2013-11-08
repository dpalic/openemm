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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.dao.TargetDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.target.Target;
import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetNodeFactory;
import org.agnitas.target.TargetRepresentation;
import org.agnitas.target.TargetRepresentationFactory;
import org.agnitas.target.impl.TargetNodeDate;
import org.agnitas.target.impl.TargetNodeNumeric;
import org.agnitas.target.impl.TargetNodeString;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action</strong> that handles Targets
 * 
 * @author Martin Helff, Nicole Serek
 */

public class TargetAction extends StrutsActionBase {

	public static final int ACTION_CREATE_ML = ACTION_LAST + 1;

	public static final int ACTION_CLONE = ACTION_LAST + 2;
	
	public static final int ACTION_DELETE_RECIPIENTS_CONFIRM = ACTION_LAST + 3;
	
	public static final int ACTION_DELETE_RECIPIENTS = ACTION_LAST + 4;
	
	public static final int ACTION_BACK_TO_MAILINGWIZARD = ACTION_LAST + 5;
	
	
	
	
	
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
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet exception occurs
	 * @return destination
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		TargetForm aForm = null;
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if (!this.checkLogon(req)) {
			return mapping.findForward("logon");
		}

		if (form != null) {
			aForm = (TargetForm) form;
		} else {
			aForm = new TargetForm();
		}

		boolean removeSelected = this.updateTargetFormProperties(aForm, req);
		
		AgnUtils.logger().info("Action: " + aForm.getAction());

		if (!allowed("targets.show", req)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
			saveErrors(req, errors);
			return null;
		}

		try {
			switch (aForm.getAction()) {
			case ACTION_LIST:
				destination = mapping.findForward( listTargetGroups( aForm, req));
				break;

			case ACTION_VIEW:
				if (aForm.getTargetID() != 0) {
					aForm.setAction(TargetAction.ACTION_SAVE);
					loadTarget(aForm, req);
				} else {
					aForm.clearRules();
					aForm.setAction(TargetAction.ACTION_NEW);
				}
				destination = mapping.findForward("view");
				break;

			case ACTION_SAVE:
				if (!aForm.getAddTargetNode() && !removeSelected) {
					if( saveTarget(aForm, req) != 0) {
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
						destination = mapping.findForward("success");
					} else {
						errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.target.saving"));
						destination = mapping.findForward("view");
					}
				} else {
					destination = mapping.findForward("success");
				}
				
				break;

			case ACTION_NEW:
				if (!aForm.getAddTargetNode()) {				
					if( saveTarget(aForm, req) != 0) {
						aForm.setAction(TargetAction.ACTION_SAVE);
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved")); 
					} else {
						aForm.setAction(TargetAction.ACTION_SAVE);
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.target.saving"));
					}
				}
				destination = mapping.findForward("view");
				
				break;

			case ACTION_CONFIRM_DELETE:
				loadTarget(aForm, req);
				destination = mapping.findForward("delete");
				aForm.setAction(TargetAction.ACTION_DELETE);
				break;

			case ACTION_DELETE:
				this.deleteTarget(aForm, req);
				aForm.setAction(TargetAction.ACTION_LIST);
				destination = mapping.findForward(listTargetGroups( aForm, req));
				
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				break;

			case ACTION_CREATE_ML:
				destination = mapping.findForward("create_ml");
				break;

			case ACTION_CLONE:
				if (aForm.getTargetID() != 0) {
					loadTarget(aForm, req);
					cloneTarget(aForm, req);
					aForm.setAction(TargetAction.ACTION_SAVE);
				}
				destination = mapping.findForward("view");
				break;
				
			case ACTION_DELETE_RECIPIENTS_CONFIRM:
				loadTarget(aForm, req);
				this.getRecipientNumber(aForm, req);
				destination = mapping.findForward("delete_recipients");
				break;
				
			case ACTION_DELETE_RECIPIENTS:
				loadTarget(aForm, req);
				this.deleteRecipients(aForm, req);				
				aForm.setAction(TargetAction.ACTION_LIST);
				destination = mapping.findForward(listTargetGroups( aForm, req));
				break;
				
			case ACTION_BACK_TO_MAILINGWIZARD:							// TODO: Move it to ComTargetAction
				destination = mapping.findForward("back_mailingwizard");
				break;
				
			default:
				destination = mapping.findForward(listTargetGroups( aForm, req));
				break;
			}

		} catch (Exception e) {
			AgnUtils.logger().error(
					"execute: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"error.exception"));
		}
		
		if( "success".equals(destination.getName())) {
			req.setAttribute("targetlist", loadTargetList(req) );
			setNumberOfRows(req, aForm);
		}
		

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(req, errors);
			return (new ActionForward(mapping.getInput()));
		}

		// Report any message (non-errors) we have discovered
		if (!messages.isEmpty()) {
			saveMessages(req, messages);
		}

		return destination;

	}

	protected String listTargetGroups( TargetForm form, HttpServletRequest request) {
		if ( form.getColumnwidthsList() == null) {
        	form.setColumnwidthsList(getInitializedColumnWidthList(3));
        }		
		
		request.setAttribute("targetlist", loadTargetList(request) );
		setNumberOfRows(request, form);
		
		return "list";
	}
	
	/**
	 * Loads target.
	 */
	protected void loadTarget(TargetForm aForm, HttpServletRequest req) throws Exception {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");
		Target aTarget = targetDao.getTarget(aForm.getTargetID(),
				getCompanyID(req));

		if (aTarget.getId() == 0) {
			AgnUtils.logger().warn(
					"loadTarget: could not load target " + aForm.getTargetID());
			aTarget = (Target) getBean("Target");
			aTarget.setId(aForm.getTargetID());
		}
		aForm.setShortname(aTarget.getTargetName());
		aForm.setDescription(aTarget.getTargetDescription());
		fillFormFromTargetRepresentation(aForm, aTarget.getTargetStructure());
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": do load target group  " + aForm.getShortname());
		AgnUtils.logger().info("loadTarget: target " + aForm.getTargetID() + " loaded");
	}

	/**
	 * Clone target.
	 */
	protected void cloneTarget(TargetForm aForm, HttpServletRequest req) throws Exception {
		aForm.setTargetID(0);
		aForm.setShortname(SafeString.getLocaleString("mailing.CopyOf", (Locale) req
				.getSession().getAttribute(Globals.LOCALE_KEY))
				+ " " + aForm.getShortname());
		saveTarget(aForm, req);
	}

	/**
	 * Saves target.
	 */
	protected int saveTarget(TargetForm aForm, HttpServletRequest req) throws Exception {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");
		Target aTarget = targetDao.getTarget(aForm.getTargetID(),
				getCompanyID(req));

		if (aTarget == null) {
			// be sure to use id 0 if there is no existing object
			aForm.setTargetID(0);
			aTarget = (Target) getBean("Target");
			aTarget.setCompanyID(this.getCompanyID(req));
		}
		
		TargetRepresentation targetRepresentation = createTargetRepresentationFromForm(aForm);

		aTarget.setTargetName(aForm.getShortname());
		aTarget.setTargetDescription(aForm.getDescription());
		aTarget.setTargetSQL(targetRepresentation.generateSQL());
		aTarget.setTargetStructure(targetRepresentation);

		int result = targetDao.saveTarget(aTarget);
        if (aForm.getTargetID() == 0) {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": create target group  " + aForm.getShortname());
        } else {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": edit target group  " + aForm.getShortname());
        }
		AgnUtils.logger().info("saveTarget: save target " + aTarget.getId());
		
		if( aForm.getTargetID() == 0)
			aForm.setTargetID(aTarget.getId());
		
		return result;
	}

	/**
	 * Removes target.
	 */
	protected void deleteTarget(TargetForm aForm, HttpServletRequest req) {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");

		targetDao.deleteTarget(aForm.getTargetID(), getCompanyID(req));
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": delete target group  " + aForm.getShortname());
	}
	
	/**
	 * Gets number of recipients affected in a target group.
	 */
	protected void getRecipientNumber(TargetForm aForm, HttpServletRequest req) {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");
		Target target = (Target) getBean("Target");
		RecipientDao recipientDao = (RecipientDao) getBean("RecipientDao");
		
		target = targetDao.getTarget(aForm.getTargetID(), aForm.getCompanyID(req));
		int numOfRecipients = recipientDao.sumOfRecipients(aForm.getCompanyID(req), target.getTargetSQL());
		
		aForm.setNumOfRecipients(numOfRecipients);
		
	}
	
	/**
	 * Removes recipients affected in a target group.
	 */
	protected void deleteRecipients(TargetForm aForm, HttpServletRequest req) {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");
		Target target = (Target) getBean("Target");
		RecipientDao recipientDao = (RecipientDao) getBean("RecipientDao");

		target = targetDao.getTarget(aForm.getTargetID(), aForm.getCompanyID(req));
		recipientDao.deleteRecipients(aForm.getCompanyID(req), target.getTargetSQL());
	}
	
	/**
	 * load the list of targets
	 * @param request
	 * @return
	 */
	private List loadTargetList(HttpServletRequest request) {
		TargetDao targetDao = (TargetDao) getBean("TargetDao");
		return targetDao.getTargets(AgnUtils.getCompanyID(request));
	}
	
	private boolean updateTargetFormProperties(TargetForm form, HttpServletRequest request) {
		int lastIndex = form.getNumTargetNodes();
        int removeIndex = -1;

        // If "add" was clicked, add new rule
		if (form.getAddTargetNode()) {
           	form.setColumnAndType(lastIndex, form.getColumnAndTypeNew());
        	form.setChainOperator(lastIndex, form.getChainOperatorNew());
        	form.setParenthesisOpened(lastIndex, form.getParenthesisOpenedNew());
        	form.setPrimaryOperator(lastIndex, form.getPrimaryOperatorNew());
        	form.setPrimaryValue(lastIndex, form.getPrimaryValueNew());
        	form.setParenthesisClosed(lastIndex, form.getParenthesisClosedNew());
        	form.setDateFormat(lastIndex, form.getDateFormatNew());
        	form.setSecondaryOperator(lastIndex, form.getSecondaryOperatorNew());
        	form.setSecondaryValue(lastIndex, form.getSecondaryValueNew());

        	form.setAddTargetNode( false);
        	
        	lastIndex++;
        }

		int nodeToRemove = form.getTargetNodeToRemove();

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
	
	private void fillFormFromTargetRepresentation(TargetForm form, TargetRepresentation target) {
		// First, remove all previously defined rules from target form
		form.clearRules();
		
		// Now, convert target nodes to form data
		Iterator<TargetNode> it = target.getAllNodes().iterator();
		int index = 0;
		while (it.hasNext()) {
			TargetNode node = it.next();

			form.setChainOperator(index, node.getChainOperator());
			form.setColumnAndType(index, node.getPrimaryField() + "#" + node.getPrimaryFieldType());
			form.setPrimaryOperator(index, node.getPrimaryOperator());
			form.setPrimaryValue(index, node.getPrimaryValue());
			form.setColumnName(index, node.getPrimaryField());
			form.setParenthesisOpened(index, node.isOpenBracketBefore() ? 1 : 0);
			form.setParenthesisClosed(index, node.isCloseBracketAfter() ? 1 : 0);
			
			if (node instanceof TargetNodeString) {
				form.setColumnType(index, TargetForm.COLUMN_TYPE_STRING);
				form.setValidTargetOperators(index, TargetNodeString.getValidOperators());
			} else if (node instanceof TargetNodeNumeric) {
				TargetNodeNumeric numericNode = (TargetNodeNumeric) node;
				
				form.setColumnType(index, TargetForm.COLUMN_TYPE_NUMERIC);
				form.setSecondaryOperator(index, numericNode.getSecondaryOperator());
				form.setSecondaryValue(index, Integer.toString(numericNode.getSecondaryValue()));
				form.setValidTargetOperators(index, TargetNodeNumeric.getValidOperators());
			} else if (node instanceof TargetNodeDate) {
				TargetNodeDate dateNode = (TargetNodeDate) node;
				
				form.setDateFormat(index, dateNode.getDateFormat());
				form.setColumnType(index, TargetForm.COLUMN_TYPE_DATE);
				form.setValidTargetOperators(index, TargetNodeDate.getValidOperators());
			} else {
				// uh oh. It seems, somebody forgot to add a new target node type here :(
				AgnUtils.logger().warn("cannot handle target node class " + node.getClass().getCanonicalName());
				throw new RuntimeException("cannot handle target node class " + node.getClass().getCanonicalName());
			}
			
			index++;
		}
	}
	
	private TargetRepresentation createTargetRepresentationFromForm(TargetForm form) {
		TargetRepresentationFactory factory = (TargetRepresentationFactory) getWebApplicationContext().getBean("TargetRepresentationFactory"); // TODO: Change this to real DI
        TargetRepresentation target = factory.newTargetRepresentation();
       
        int lastIndex = form.getNumTargetNodes(); 
       
        for(int index = 0; index < lastIndex; index++) {
    		String colAndType = form.getColumnAndType(index);
    		String column = colAndType.substring(0, colAndType.indexOf('#'));
    		String type = colAndType.substring(colAndType.indexOf('#') + 1);
    		
    		TargetNode node = null;
    		
    		if (type.equalsIgnoreCase("VARCHAR") || type.equalsIgnoreCase("VARCHAR2") || type.equalsIgnoreCase("CHAR")) {
    			node = createStringNode(form, column, type, index);
    		} else if (type.equalsIgnoreCase("INTEGER") || type.equalsIgnoreCase("DOUBLE") || type.equalsIgnoreCase("NUMBER")) {
    			node = createNumericNode(form, column, type, index);
    		} else if (type.equalsIgnoreCase("DATE")) {
    			node = createDateNode(form, column, type, index);
    		}
    		
            target.addNode(node);
        }
        
        return target;
	}
	
	private TargetNodeString createStringNode(TargetForm form, String column, String type, int index) {
		TargetNodeFactory factory = (TargetNodeFactory) getWebApplicationContext().getBean("TargetNodeFactory");   // TODO: Change this using DI-setter
		
		return factory.newStringNode(
				form.getChainOperator(index), 
				form.getParenthesisOpened(index), 
				column, 
				type, 
				form.getPrimaryOperator(index), 
				form.getPrimaryValue(index), 
				form.getParenthesisClosed(index));
	}
	
	private TargetNodeNumeric createNumericNode(TargetForm form, String column, String type, int index) {
		TargetNodeFactory factory = (TargetNodeFactory) getWebApplicationContext().getBean("TargetNodeFactory");   // TODO: Change this using DI-setter
		
		int primaryOperator = form.getPrimaryOperator(index);
		int secondaryOperator = form.getSecondaryOperator(index);
		int secondaryValue = 0;
		
    	if(primaryOperator == TargetNode.OPERATOR_MOD.getOperatorCode()) {
            try {
                secondaryOperator = form.getSecondaryOperator(index);
            } catch (Exception e) {
                secondaryOperator = TargetNode.OPERATOR_EQ.getOperatorCode();
            }
            try {
                secondaryValue = Integer.parseInt(form.getSecondaryValue(index));
            } catch (Exception e) {
                secondaryValue = 0;
            }
        }
		
		return factory.newNumericNode(
				form.getChainOperator(index), 
				form.getParenthesisOpened(index), 
				column, 
				type, 
				primaryOperator, 
				form.getPrimaryValue(index), 
				secondaryOperator, 
				secondaryValue, 
				form.getParenthesisClosed(index));
	}
	
	private TargetNodeDate createDateNode(TargetForm form, String column, String type, int index) {
		TargetNodeFactory factory = (TargetNodeFactory) getWebApplicationContext().getBean("TargetNodeFactory");   // TODO: Change this using DI-setter

		return factory.newDateNode(
				form.getChainOperator(index), 
				form.getParenthesisOpened(index), 
				column, 
				type, 
				form.getPrimaryOperator(index), 
				form.getDateFormat(index), 
				form.getPrimaryValue(index), 
				form.getParenthesisClosed(index));
	}
}
