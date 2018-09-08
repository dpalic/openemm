package org.agnitas.emm.core.action.service.impl;

import java.util.HashMap;
import java.util.Map;

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
import org.agnitas.emm.core.action.service.EmmActionOperation;
import org.agnitas.emm.core.action.service.EmmActionOperationService;
import org.agnitas.emm.core.action.service.UnableConvertException;
import org.springframework.beans.factory.InitializingBean;

public class EmmActionOperationServiceImpl implements EmmActionOperationService, InitializingBean {
	
	private ActionOperationFactory actionOperationFactory;

	private Map<String, EmmActionOperation> operations = new HashMap<String, EmmActionOperation>();
	protected Map<Class<? extends ActionOperation>, Converter<? extends ActionOperation, ? extends AbstractActionOperation>> converters = new HashMap<Class<? extends ActionOperation>, Converter<? extends ActionOperation, ? extends AbstractActionOperation>>();
	protected Map<Class<? extends AbstractActionOperation>, ReverseConverter<? extends AbstractActionOperation, ? extends ActionOperation>> reverseConverters = new HashMap<Class<? extends AbstractActionOperation>, ReverseConverter<? extends AbstractActionOperation, ? extends ActionOperation>>();

	protected static abstract class Converter<T1 extends ActionOperation, T2 extends AbstractActionOperation> {
		
		protected abstract T2 convertInternal(String type, T1 operation);

		@SuppressWarnings("unchecked")
		AbstractActionOperation convert(String type, ActionOperation operation) {
			return convertInternal(type, (T1) operation);
		}
	}

	protected static abstract class ReverseConverter<T1 extends AbstractActionOperation, T2 extends ActionOperation> {
		
		protected abstract T2 convertInternal(T1 operation);
		
		@SuppressWarnings("unchecked")
		ActionOperation convert(ActionOperation operation) {
			return convertInternal((T1) operation);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		converters.put(ExecuteScript.class, new Converter<ExecuteScript, ActionOperationExecuteScript>() {
			@Override
			public ActionOperationExecuteScript convertInternal(String type, ExecuteScript operation) {
				ActionOperationExecuteScript op = new ActionOperationExecuteScript(type);
				op.setScript(operation.getScript());
				return op;
			}
		});
		reverseConverters.put(ActionOperationExecuteScript.class, new ReverseConverter<ActionOperationExecuteScript, ExecuteScript>() {
			@Override
			public ExecuteScript convertInternal(ActionOperationExecuteScript operation) {
				ExecuteScript op = new ExecuteScript();
				op.setScript(operation.getScript());
				return op;
			}
		});
		converters.put(UpdateCustomer.class, new Converter<UpdateCustomer, ActionOperationUpdateCustomer>() {
			@Override
			public ActionOperationUpdateCustomer convertInternal(String type, UpdateCustomer operation) {
				ActionOperationUpdateCustomer op = new ActionOperationUpdateCustomer(type);
				op.setColumnName(operation.getColumnName());
				op.setUpdateType(operation.getUpdateType());
				op.setUpdateValue(operation.getUpdateValue());
				op.setColumnType(operation.getColumnType());
				return op;
			}
		});
		reverseConverters.put(ActionOperationUpdateCustomer.class, new ReverseConverter<ActionOperationUpdateCustomer, UpdateCustomer>() {
			@Override
			public UpdateCustomer convertInternal(ActionOperationUpdateCustomer operation) {
				UpdateCustomer op = new UpdateCustomer();
				op.setColumnName(operation.getColumnName());
				op.setUpdateType(operation.getUpdateType());
				op.setUpdateValue(operation.getUpdateValue());
				op.setColumnType(operation.getColumnType());
				return op;
			}
		});
		converters.put(GetCustomer.class, new Converter<GetCustomer, ActionOperationGetCustomer>() {
			@Override
			public ActionOperationGetCustomer convertInternal(String type, GetCustomer operation) {
				ActionOperationGetCustomer op = new ActionOperationGetCustomer(type);
				op.setLoadAlways(operation.isLoadAlways());
				return op;
			}
		});
		reverseConverters.put(ActionOperationGetCustomer.class, new ReverseConverter<ActionOperationGetCustomer, GetCustomer>() {
			@Override
			public GetCustomer convertInternal(ActionOperationGetCustomer operation) {
				GetCustomer op = new GetCustomer();
				op.setLoadAlways(operation.isLoadAlways());
				return op;
			}
		});
		converters.put(ActivateDoubleOptIn.class, new Converter<ActivateDoubleOptIn, ActionOperationActivateDoubleOptIn>() {
			@Override
			public ActionOperationActivateDoubleOptIn convertInternal(String type, ActivateDoubleOptIn operation) {
				ActionOperationActivateDoubleOptIn op = new ActionOperationActivateDoubleOptIn(type);
				return op;
			}
		});
		reverseConverters.put(ActionOperationActivateDoubleOptIn.class, new ReverseConverter<ActionOperationActivateDoubleOptIn, ActivateDoubleOptIn>() {
			@Override
			public ActivateDoubleOptIn convertInternal(ActionOperationActivateDoubleOptIn operation) {
				ActivateDoubleOptIn op = new ActivateDoubleOptIn();
				return op;
			}
		});
		converters.put(SubscribeCustomer.class, new Converter<SubscribeCustomer, ActionOperationSubscribeCustomer>() {
			@Override
			public ActionOperationSubscribeCustomer convertInternal(String type, SubscribeCustomer operation) {
				ActionOperationSubscribeCustomer op = new ActionOperationSubscribeCustomer(type);
				op.setDoubleCheck(operation.isDoubleCheck());
				op.setKeyColumn(operation.getKeyColumn());
				op.setDoubleOptIn(operation.isDoubleOptIn());
				return op;
			}
		});
		reverseConverters.put(ActionOperationSubscribeCustomer.class, new ReverseConverter<ActionOperationSubscribeCustomer, SubscribeCustomer>() {
			@Override
			public SubscribeCustomer convertInternal(ActionOperationSubscribeCustomer operation) {
				SubscribeCustomer op = new SubscribeCustomer();
				op.setDoubleCheck(operation.isDoubleCheck());
				op.setKeyColumn(operation.getKeyColumn());
				op.setDoubleOptIn(operation.isDoubleOptIn());
				return op;
			}
		});
		converters.put(UnsubscribeCustomer.class, new Converter<UnsubscribeCustomer, ActionOperationUnsubscribeCustomer>() {
			@Override
			public ActionOperationUnsubscribeCustomer convertInternal(String type, UnsubscribeCustomer operation) {
				ActionOperationUnsubscribeCustomer op = new ActionOperationUnsubscribeCustomer(type);
				return op;
			}
		});
		reverseConverters.put(ActionOperationUnsubscribeCustomer.class, new ReverseConverter<ActionOperationUnsubscribeCustomer, UnsubscribeCustomer>() {
			@Override
			public UnsubscribeCustomer convertInternal(ActionOperationUnsubscribeCustomer operation) {
				UnsubscribeCustomer op = new UnsubscribeCustomer();
				return op;
			}
		});
		converters.put(SendMailing.class, new Converter<SendMailing, ActionOperationSendMailing>() {
			@Override
			public ActionOperationSendMailing convertInternal(String type, SendMailing operation) {
				ActionOperationSendMailing op = new ActionOperationSendMailing(type);
				op.setMailingID(operation.getMailingID());
				op.setDelayMinutes(operation.getDelayMinutes());
				return op;
			}
		});
		reverseConverters.put(ActionOperationSendMailing.class, new ReverseConverter<ActionOperationSendMailing, SendMailing>() {
			@Override
			public SendMailing convertInternal(ActionOperationSendMailing operation) {
				SendMailing op = new SendMailing();
				op.setMailingID(operation.getMailingID());
				op.setDelayMinutes(operation.getDelayMinutes());
				return op;
			}
		});
		converters.put(ServiceMail.class, new Converter<ServiceMail, ActionOperationServiceMail>() {
			@Override
			public ActionOperationServiceMail convertInternal(String type, ServiceMail operation) {
				ActionOperationServiceMail op = new ActionOperationServiceMail(type);
				op.setTextMail(operation.getTextMail());
				op.setSubjectLine(operation.getSubjectLine());
				op.setToAdr(operation.getToAdr());
				op.setMailtype(operation.getMailtype());
				op.setHtmlMail(operation.getHtmlMail());
				return op;
			}
		});
		reverseConverters.put(ActionOperationServiceMail.class, new ReverseConverter<ActionOperationServiceMail, ServiceMail>() {
			@Override
			public ServiceMail convertInternal(ActionOperationServiceMail operation) {
				ServiceMail op = new ServiceMail();
				op.setTextMail(operation.getTextMail());
				op.setSubjectLine(operation.getSubjectLine());
				op.setToAdr(operation.getToAdr());
				op.setMailtype(operation.getMailtype());
				op.setHtmlMail(operation.getHtmlMail());
				return op;
			}
		});
		converters.put(GetArchiveList.class, new Converter<GetArchiveList, ActionOperationGetArchiveList>() {
			@Override
			public ActionOperationGetArchiveList convertInternal(String type, GetArchiveList operation) {
				ActionOperationGetArchiveList op = new ActionOperationGetArchiveList(type);
				op.setCampaignID(operation.getCampaignID());
				return op;
			}
		});
		reverseConverters.put(ActionOperationGetArchiveList.class, new ReverseConverter<ActionOperationGetArchiveList, GetArchiveList>() {
			@Override
			public GetArchiveList convertInternal(ActionOperationGetArchiveList operation) {
				GetArchiveList op = new GetArchiveList();
				op.setCampaignID(operation.getCampaignID());
				return op;
			}
		});
		converters.put(GetArchiveMailing.class, new Converter<GetArchiveMailing, ActionOperationGetArchiveMailing>() {
			@Override
			public ActionOperationGetArchiveMailing convertInternal(String type, GetArchiveMailing operation) {
				ActionOperationGetArchiveMailing op = new ActionOperationGetArchiveMailing(type);
				return op;
			}
		});
		reverseConverters.put(ActionOperationGetArchiveMailing.class, new ReverseConverter<ActionOperationGetArchiveMailing, GetArchiveMailing>() {
			@Override
			public GetArchiveMailing convertInternal(ActionOperationGetArchiveMailing operation) {
				GetArchiveMailing op = new GetArchiveMailing();
				return op;
			}
		});
	}

	@Override
	public boolean executeOperation(AbstractActionOperation operation, Map<String, Object> params) {
		return operations.get(operation.getType()).execute(operation, params);
	}

	public void setOperations(Map<String, EmmActionOperation> operations) {
		this.operations = operations;
	}
	
	@Override
	public AbstractActionOperation convert(ActionOperation operation) {
		Converter<? extends ActionOperation, ? extends AbstractActionOperation> converter = converters.get(operation.getClass());
		if (converter == null) {
			throw new UnableConvertException();
		}
		return converter.convert(actionOperationFactory.getType(operation), operation);
	}

	@Override
	public ActionOperation convert(AbstractActionOperation operation) {
		ReverseConverter<? extends AbstractActionOperation, ? extends ActionOperation> converter = reverseConverters.get(operation.getClass());
		if (converter == null) {
			throw new RuntimeException("Not implemented for " + operation.getClass());
		}
		return converter.convert(operation);
	}

	public void setActionOperationFactory(ActionOperationFactory actionOperationFactory) {
		this.actionOperationFactory = actionOperationFactory;
	}

}
