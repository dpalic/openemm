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

package org.agnitas.beans.factory.impl;

import org.agnitas.actions.ActionOperation;
import org.agnitas.actions.ops.ActivateDoubleOptIn;
import org.agnitas.actions.ops.ExecuteScript;
import org.agnitas.actions.ops.GetArchiveList;
import org.agnitas.actions.ops.GetArchiveMailing;
import org.agnitas.actions.ops.GetCustomer;
import org.agnitas.actions.ops.SendMailing;
import org.agnitas.actions.ops.ServiceMail;
import org.agnitas.actions.ops.SubscribeCustomer;
import org.agnitas.actions.ops.UnsubscribeCustomer;
import org.agnitas.actions.ops.UpdateCustomer;
import org.agnitas.beans.factory.ActionOperationFactory;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationActivateDoubleOptIn;
import org.agnitas.emm.core.action.operations.ActionOperationExecuteScript;
import org.agnitas.emm.core.action.operations.ActionOperationGetArchiveList;
import org.agnitas.emm.core.action.operations.ActionOperationGetArchiveMailing;
import org.agnitas.emm.core.action.operations.ActionOperationGetCustomer;
import org.agnitas.emm.core.action.operations.ActionOperationSendMailing;
import org.agnitas.emm.core.action.operations.ActionOperationServiceMail;
import org.agnitas.emm.core.action.operations.ActionOperationSubscribeCustomer;
import org.agnitas.emm.core.action.operations.ActionOperationUnsubscribeCustomer;
import org.agnitas.emm.core.action.operations.ActionOperationUpdateCustomer;

public class ActionOperationFactoryImpl implements ActionOperationFactory {
	
	private static final String GET_ARCHIVE_LIST = "GetArchiveList";
	private static final String GET_ARCHIVE_MAILING = "GetArchiveMailing";
	private static final String ACTIVATE_DOUBLE_OPT_IN = "ActivateDoubleOptIn";
	private static final String SUBSCRIBE_CUSTOMER = "SubscribeCustomer";
	private static final String UNSUBSCRIBE_CUSTOMER = "UnsubscribeCustomer";
	private static final String UPDATE_CUSTOMER = "UpdateCustomer";
	private static final String GET_CUSTOMER = "GetCustomer";
	private static final String EXECUTE_SCRIPT = "ExecuteScript";
	private static final String SEND_MAILING = "SendMailing";
	private static final String SERVICE_MAIL = "ServiceMail";
	
	private final String[] types = new String[] { 
			GET_ARCHIVE_LIST,
	        GET_ARCHIVE_MAILING,
	        ACTIVATE_DOUBLE_OPT_IN,
	        SUBSCRIBE_CUSTOMER,
	        UNSUBSCRIBE_CUSTOMER,
	        UPDATE_CUSTOMER,
	        GET_CUSTOMER,
	        EXECUTE_SCRIPT,
	        SEND_MAILING,
	        SERVICE_MAIL
		};
		
	@Override
    public AbstractActionOperation newActionOperation(String type) {
		AbstractActionOperation operation = null;
		if (type.equals(GET_ARCHIVE_LIST))				operation = new ActionOperationGetArchiveList(type);
		else if (type.equals(GET_ARCHIVE_MAILING))   	operation = new ActionOperationGetArchiveMailing(type);
		else if (type.equals(ACTIVATE_DOUBLE_OPT_IN))	operation = new ActionOperationActivateDoubleOptIn(type);
		else if (type.equals(SUBSCRIBE_CUSTOMER))		operation = new ActionOperationSubscribeCustomer(type);
		else if (type.equals(UNSUBSCRIBE_CUSTOMER))		operation = new ActionOperationUnsubscribeCustomer(type);
		else if (type.equals(UPDATE_CUSTOMER))			operation = new ActionOperationUpdateCustomer(type);
		else if (type.equals(GET_CUSTOMER))				operation = new ActionOperationGetCustomer(type);
		else if (type.equals(EXECUTE_SCRIPT))			operation = new ActionOperationExecuteScript(type);
		else if (type.equals(SEND_MAILING))				operation = new ActionOperationSendMailing(type);
		else if (type.equals(SERVICE_MAIL))				operation = new ActionOperationServiceMail(type);
		else throw new RuntimeException("Unsupported type");
		return operation;
    }

	@Override
	public String[] getTypes() {
		return types;
	}

	@Override
	public String getType(ActionOperation actionOperation) {
		if (actionOperation instanceof AbstractActionOperation) {
			return ((AbstractActionOperation) actionOperation).getType();
		}
		if (actionOperation instanceof GetArchiveList) 		return GET_ARCHIVE_LIST;
		if (actionOperation instanceof GetArchiveMailing)	return GET_ARCHIVE_MAILING;
		if (actionOperation instanceof ActivateDoubleOptIn)	return ACTIVATE_DOUBLE_OPT_IN;
		if (actionOperation instanceof SubscribeCustomer)	return SUBSCRIBE_CUSTOMER;
		if (actionOperation instanceof UnsubscribeCustomer) return UNSUBSCRIBE_CUSTOMER;
		if (actionOperation instanceof UpdateCustomer) 		return UPDATE_CUSTOMER;
		if (actionOperation instanceof GetCustomer) 		return GET_CUSTOMER;
		if (actionOperation instanceof ExecuteScript) 		return EXECUTE_SCRIPT;
		if (actionOperation instanceof SendMailing) 		return SEND_MAILING;
		if (actionOperation instanceof ServiceMail) 		return SERVICE_MAIL;
		throw new RuntimeException("Unsupported type");
	}
}
