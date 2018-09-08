package org.agnitas.emm.core.action.dao.impl;

import org.agnitas.dao.impl.BaseDaoImpl;
import org.agnitas.emm.core.action.dao.ActionOperationDao;
import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public abstract class AbstractActionOperationDaoImpl<T> extends BaseDaoImpl implements ActionOperationDao, InitializingBean {

	protected SimpleJdbcTemplate jdbcTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {
		jdbcTemplate = getSimpleJdbcTemplate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public final void getOperation(AbstractActionOperation operation) {
		processGetOperation((T) operation);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final void saveOperation(AbstractActionOperation operation) {
		processSaveOperation((T) operation);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final void updateOperation(AbstractActionOperation operation) {
		processUpdateOperation((T) operation);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void deleteOperation(AbstractActionOperation operation) {
		processDeleteOperation((T) operation);
	}
	
	protected abstract void processGetOperation(T operation);

	protected abstract void processSaveOperation(T operation);

	protected abstract void processUpdateOperation(T operation);
	
	protected abstract void processDeleteOperation(T operation);

}
