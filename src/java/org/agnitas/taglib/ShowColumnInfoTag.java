/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.taglib;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.sql.DataSource;

import org.agnitas.beans.Admin;
import org.agnitas.beans.ProfileField;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.util.AgnUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Prepares a list of userdefined fields for use in web-templates.
 */
public class ShowColumnInfoTag extends BodyBase {
    
    private static final long serialVersionUID = -1235292192519826728L;
	// global variables:
    protected String id=null;
    protected int table=0;
    protected Set sys_columns=new HashSet();
    
    /**
     * Set the id of the table to show info for.
     *
     * @param table companyID to show the table for.
     */
    public void setTable(int table) {
        this.table=table;
    }
    
    /**
     * Set the id to use for session variables.
     *
     * @param id the id to use for global variables.
     */
    public void setId(String id) {
        this.id=id;
    }
    
    
    /**
     * Set the hidden columns for this query.
     *
     * @param hide a comma sepereated list of columnnames which should not be
     *             shown.
     */
    public void setHide(String hide) {
        StringTokenizer tok=new StringTokenizer(hide, ",");
        
        while (tok.hasMoreTokens()) {
            sys_columns.add(tok.nextToken().trim());
        }
    }
    
    
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
    public static TreeMap getColumnInfo(ApplicationContext context, int customer,
            String column
            ) throws Exception {
        DataSource ds=(DataSource)context.getBean("dataSource");
        Connection con=null;
        TreeMap list=new TreeMap();
        ResultSet rset=null;
        
        con=DataSourceUtils.getConnection(ds);
        try {
            if(AgnUtils.isOracleDB()) {
                rset=con.getMetaData().getColumns(null, null, "CUSTOMER_"+customer+"_TBL", column.toUpperCase());
            } else {
                rset=con.getMetaData().getColumns(null, null, "customer_"+customer+"_tbl", column);
            }
            if(rset!=null) {
                while(rset.next()) {
                    String type=null;
                    String col=rset.getString(4).toLowerCase();
                    Hashtable m=new Hashtable();

                    m.put("column", col);
                    m.put("shortname", col);
                    switch(rset.getInt(5)) {
                        case java.sql.Types.BIGINT:
                        case java.sql.Types.INTEGER:
                        case java.sql.Types.SMALLINT:
                            type=new String("INTEGER");
                            break;
                            
                        case java.sql.Types.DECIMAL:
                        case java.sql.Types.DOUBLE:
                        case java.sql.Types.FLOAT:
                        case java.sql.Types.NUMERIC:
                        case java.sql.Types.REAL:
                            type=new String("DOUBLE");
                            break;
                            
                        case java.sql.Types.CHAR:
                            type=new String("CHAR"); break;
                            
                        case java.sql.Types.VARCHAR:
                        case java.sql.Types.LONGVARCHAR:
                        case java.sql.Types.CLOB:
                            type=new String("VARCHAR");
                            break;
                            
                        case java.sql.Types.DATE:
                        case java.sql.Types.TIMESTAMP:
                        case java.sql.Types.TIME:
                            type=new String("DATE");
                            break;
                            
                        default:
                            type=new String("UNKNOWN("+rset.getInt(5)+")");
                    }
                    m.put("type", type);
                    m.put("length", new Integer(rset.getInt(7)));
                    if(rset.getInt(11) == DatabaseMetaData.columnNullable)
                        m.put("nullable", new Integer(1));
                    else
                        m.put("nullable", new Integer(0));
                    
                    if(customer > 0) {
                        ProfileFieldDao fieldDao=(ProfileFieldDao) context.getBean("ProfileFieldDao");
                        ProfileField field=fieldDao.getProfileField(customer, col);
                        
                        if(field != null) {
                            m.put("shortname", field.getShortname());
                            m.put("default", field.getDefaultValue());
                            m.put("description", field.getDescription());
                            m.put("editable", new Integer(field.getModeEdit()));
                            m.put("insertable", new Integer(field.getModeInsert()));
                        }
                    }
                    list.put(m.get("shortname"), m);
                }
            }
            rset.close();
        } catch ( Exception e) {
            DataSourceUtils.releaseConnection(con, ds);
            throw e;
        }
        DataSourceUtils.releaseConnection(con, ds);
        return list;
    }
    
    /**
     * Shows column information.
     */
    public int doStartTag() throws JspTagException {
        TreeMap list=null;
        
        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        
        if(id==null) {
            id=new String("");
        }
        
        if(table==0) {
            this.table=((Admin)pageContext.getSession().getAttribute("emm.admin")).getCompany().getId();
        }
        
        try {
            list=getColumnInfo(aContext, table, "%");
        } catch (Exception e) {
            throw new JspTagException(e);
        }
        
        TreeMap collist=new TreeMap();
        Map tmp=null;
        Iterator aIt=list.values().iterator();
        while(aIt.hasNext()) {
            tmp=(Map)aIt.next();
            collist.put(tmp.get("column"), tmp);
        }
        
        if(list.size() <= 0) {
            return SKIP_BODY;
        }
        pageContext.setAttribute("__"+id+"_data", list.values().iterator());
        pageContext.setAttribute("__"+id+"_map", list);
        pageContext.setAttribute("__"+id+"_colmap", collist);
        try {
            return doAfterBody();
        }   catch ( Exception e) {
            throw new JspTagException("Error: " + e);
        }
    }
    
    /**
     * Sets attributes for pagecontext.
     */
    public int doAfterBody() throws JspException {
        Iterator	i=(Iterator) pageContext.getAttribute("__"+id+"_data");
        
        try {
            while(i.hasNext()) {
                Map v=(Map) i.next();
                
                if(sys_columns.contains(((String) v.get("column")).toLowerCase())) {
                    continue;
                }
                
                pageContext.setAttribute("_"+id+"_column_name", v.get("column"));
                pageContext.setAttribute("_"+id+"_data_type", v.get("type").toString());
                pageContext.setAttribute("_"+id+"_data_length", v.get("length").toString());
                pageContext.setAttribute("_"+id+"_shortname", v.get("shortname"));
                if(v.get("default") != null) {
                    pageContext.setAttribute("_"+id+"_data_default", v.get("default"));
                } else {
                    pageContext.setAttribute("_"+id+"_data_default", new String(""));
                }
                if(v.get("editable") != null) {
                    pageContext.setAttribute("_"+id+"_editable", v.get("editable"));
                } else {
                    pageContext.setAttribute("_"+id+"_editable", new Integer("0"));
                }
                if(v.get("insertable") != null) {
                    pageContext.setAttribute("_"+id+"_insertable", v.get("insertable"));
                } else {
                    pageContext.setAttribute("_"+id+"_insertable", new String("0"));
                }
                return EVAL_BODY_BUFFERED;
            }
        } catch (Exception e) {
            AgnUtils.logger().error(e.getMessage());
        }
        return SKIP_BODY;
    }
    
}
