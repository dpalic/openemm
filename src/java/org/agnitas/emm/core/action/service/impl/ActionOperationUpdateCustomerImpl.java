package org.agnitas.emm.core.action.service.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.agnitas.emm.core.action.operations.AbstractActionOperation;
import org.agnitas.emm.core.action.operations.ActionOperationUpdateCustomer;
import org.agnitas.emm.core.action.service.EmmActionOperation;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

public class ActionOperationUpdateCustomerImpl implements EmmActionOperation {
	
	private static final Logger logger = Logger.getLogger(ActionOperationUpdateCustomerImpl.class);

	private DataSource dataSource;
	
	@Override
	public boolean execute(AbstractActionOperation operation, Map<String, Object> params) {
		
		ActionOperationUpdateCustomer op =(ActionOperationUpdateCustomer) operation;
		int companyID = op.getCompanyId();
		String columnName = op.getColumnName();
		int updateType = op.getUpdateType();
		String updateValue = op.getUpdateValue();
		String columnType = op.getColumnType();
		
        int customerID = 0;
        boolean exitValue=true;
        JdbcTemplate tmpl = new JdbcTemplate(dataSource);
        
        if(params.get("customerID")==null) {
            return false;
        }
        
        customerID = (Integer)params.get("customerID");
        
        Object[] sqlData = buildUpdateStatement(companyID, columnName, updateType, columnType, generateUpdateValue(params, updateValue));
        
        try {
        	if (sqlData[1] == null)
        		tmpl.update(sqlData[0].toString(), new Object[]{ customerID });
        	else
        		tmpl.update(sqlData[0].toString(), new Object[] { sqlData[1], customerID });
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("SQL: " + sqlData[0], e);
            logger.error("executeOperation: " + e);
            logger.error("SQL: " + sqlData[0]);
            logger.error("  param: " + sqlData[1]);
            exitValue=false;
        }
        
        return exitValue;
	}
	
    /**
     * Generates the values for the update
     *
     * @param params HashMap containing all available informations
     */
    private String generateUpdateValue(Map<String, Object> params, String updateValue) {
        Matcher aMatcher=null;
        Pattern aRegExp=Pattern.compile("##[^#]+##");
        StringBuffer aBuf=new StringBuffer(updateValue);
        String tmpString=null;
        String tmpString2=null;
        
        try {
            // aRegExp=new RE("##[^#]+##");
            aMatcher=aRegExp.matcher(aBuf);
            while(aMatcher.find()) {
                tmpString=aBuf.toString().substring(aMatcher.start()+2, aMatcher.end()-2);
                tmpString2 = "";
                if(params.get(tmpString)!=null) {
                    tmpString2=params.get(tmpString).toString();
                }
                aBuf.replace(aMatcher.start(), aMatcher.end(), tmpString2);
                aMatcher=aRegExp.matcher(aBuf);
            }
        } catch (Exception e) {
            logger.error("generateUpdateValue: "+e);
        }
        return aBuf.toString();
    }

    /**
     * Declaration of decrement operator, increment operator
     * and equal operator depending on the column type
     * ...
     */
    private Object[] buildUpdateStatement(int companyID, String columnName, int updateType, String columnType, String updateValue) {
    	Object value = null;
    	
        String decOp=null;
        String incOp=null;
        String eqOp=null;
    	
        StringBuffer updateStatement = new StringBuffer("UPDATE customer_");
        updateStatement.append(companyID);
        updateStatement.append("_tbl SET change_date=");
        updateStatement.append(AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName());
        updateStatement.append(", ");
        
        if (columnType.equalsIgnoreCase("INTEGER")) {
            decOp = columnName + " - ?";
            incOp = columnName + " + ?";
            eqOp = "?";
        } else if (columnType.equalsIgnoreCase("DOUBLE")) {	
            decOp = columnName + " - ?";
            incOp = columnName + " + ?";
            eqOp = "?";
        } else  if (columnType.equalsIgnoreCase("CHAR") || columnType.equalsIgnoreCase("VARCHAR")) {
            decOp = columnName + " - ?";
            incOp = "concat(" + columnName + ", ?)";
            eqOp = "?";
        } else if (columnType.equalsIgnoreCase("DATE")) {
            decOp = columnName + " - ?";
            incOp = columnName + " + ?";
            eqOp = "?";
        }
        
        updateStatement.append(columnName);
        switch(updateType) {
            case ActionOperationUpdateCustomer.TYPE_INCREMENT_BY:
                updateStatement.append("=");
                updateStatement.append(incOp);
                break;
                
            case ActionOperationUpdateCustomer.TYPE_DECREMENT_BY:
                updateStatement.append("=");
                updateStatement.append(decOp);
                break;
                
            case ActionOperationUpdateCustomer.TYPE_SET_VALUE:
                updateStatement.append("=");
                
                // Assignment for type "date" is handled in a special way in the next "if"-block
                if(!columnType.equalsIgnoreCase("DATE"))
                	updateStatement.append(eqOp);
                break;
        }
        
        if (columnType.equalsIgnoreCase("INTEGER")) {
        	try {
        		value = Integer.parseInt(updateValue);
        	} catch (Throwable t) {
        		value = 0.0;
        	}
        } else if (columnType.equalsIgnoreCase("DOUBLE")) {
        	try {
        		value = Double.parseDouble(updateValue);
        	} catch (Throwable t) {
        		value = 0.0;
        	}
        } else if (columnType.equalsIgnoreCase("CHAR") || columnType.equalsIgnoreCase("VARCHAR")) {
        	value = updateValue;
        } else if (columnType.equalsIgnoreCase("DATE")) {
            if (updateType==ActionOperationUpdateCustomer.TYPE_INCREMENT_BY || updateType==ActionOperationUpdateCustomer.TYPE_DECREMENT_BY) {
            	try {
            		value = Double.parseDouble(updateValue);
            	} catch (Throwable t) {
            		value = 0.0;
            	}
            } else {
                if (updateValue.startsWith("sysdate")) {
                	value = null;
                    updateStatement.append(AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName());
                } else {
                	value = updateValue;
                    updateStatement.append("?");
                }
            }
        }
        
        updateStatement.append(" WHERE customer_id = ?");
        
        return new Object[]{ updateStatement.toString(), value};
    }

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
