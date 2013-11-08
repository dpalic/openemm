package org.agnitas.util;

import java.util.Map;

import org.agnitas.service.ColumnInfoService;
import org.springframework.context.ApplicationContext;

public class ColumnInfoUtil {
    /**
     * Get information about database columns.
     * The information will be gathered from the DatabaseMetaData of the table
     * and the infoTable. The give infoTable should be the name of a table
     * which holds the following fields:
     * <dl>
     *    <dt>col_name
     *    <dd>Primary key to identify the column
     *    <dt>shortname
     *    <dd>Textfield for a short descriptive name of the column
     *    <dt>default_value
     *    <dd>the value which should be used for the column, when no other is given.
     * </dl>
     * The resulting list contains one row for each found column.
     * Each row is a Map consists of:
     * <dl>
     *    <dt>column
     *    <dd>the name of the column in Database
     *    <dt>type
     *    <dd>the typename as in java.sql.Types
     *        (eg. VARCHAR for a java.sql.Types.VARCHAR
     *    <dt>length
     *    <dd>the size of the column as in DatabaseMetaData.getColumns()
     *    <dt>nullable
     *    <dd>inidcates whather the column can contain NULL values (1) or not (0).
     *    <dt>shortname (optional)
     *    <dd>a descriptive name for the column
     *    <dt>default (optional)
     *    <dd>value that should be used, when no value is given.
     *    <dt>description (optional)
     *    <dd>descriptive text for the column
     * </ul>
     * 
     * @param context ApplicationContext, required to get a database connection.
     * @param customer id of the customer (required to access the correct table)
     * @param column column to query or "%" for all columns
     * @throws java.lang.Exception 
     * @return TreeMap containing column informations
     */
    public static Map<String, Map> getColumnInfo(ApplicationContext context, int customer, String column) throws Exception {
    	try {
	        ColumnInfoService columnInfoService = (ColumnInfoService) context.getBean("columnInfoService");
	        
	        return columnInfoService.getColumnInfo(customer, column);
    	} catch( Exception e) {
    		e.printStackTrace();
    		
    		throw e;
    	}
    }

}
