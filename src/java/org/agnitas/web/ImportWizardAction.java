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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.beans.Admin;
import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.DatasourceDescription;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CsvColInfo;
import org.agnitas.util.EmmCalendar;
import org.agnitas.util.SafeString;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/** 
 * Implementation of <strong>StrutsActionBase</strong> that handles CSV-Import of Subscribers to CUSTOMER_X_TBL
 *
 * @author Martin Helff
 */

public class ImportWizardAction extends StrutsActionBase {
    
    /** 
     * Constant for Action List 
     */
    public static final int ACTION_START = 1;
    
    public static final int ACTION_CSV = 2;
    
    public static final int ACTION_PARSE = 3;
    
    public static final int ACTION_MODE = 4;
    
    public static final int ACTION_PRESCAN = 5;
    
    public static final int ACTION_MLISTS = 6;
    
    public static final int ACTION_WRITE = 7;
    
    public static final int ACTION_PREVIEW_SCROLL = 8;
    
    public static final int ACTION_VIEW_STATUS = 9;
    
    public static final int ACTION_VIEW_STATUS_WINDOW = 10;
    
    public static final int ACTION_GET_ERROR_DATE = 11;
    
    public static final int ACTION_GET_ERROR_EMAIL = 12;
    
    public static final int ACTION_GET_ERROR_EMAILDOUBLE = 13;
    
    public static final int ACTION_GET_ERROR_GENDER = 14;
    
    public static final int ACTION_GET_ERROR_MAILTYPE = 15;
    
    public static final int ACTION_GET_ERROR_NUMERIC = 16;
    
    public static final int ACTION_GET_ERROR_STRUCTURE = 17;
    
    public static final int ACTION_GET_DATA_PARSED = 18;
    
    public static final int ACTION_GET_ERROR_BLACKLIST = 19;
    
    
    // --------------------------------------------------------- Public Methods
    
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param req The HTTP request we are processing
     * @param res The HTTP response we are creating
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return returns ActionForward to resulting jsp
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {
        
        // Validate the request parameters specified by the user
        ImportWizardForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        ApplicationContext aContext=this.getWebApplicationContext();
        JdbcTemplate jdbc=new JdbcTemplate((DataSource)aContext.getBean("dataSource"));
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        if(form!=null) {
            aForm=(ImportWizardForm)form;
        } else {
            aForm=new ImportWizardForm();
        }
        
        AgnUtils.logger().info("ImportWizard action: "+aForm.getAction());

       if(!allowed("wizard.import", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }
        
        try {
            switch(aForm.getAction()) {
                
                case ImportWizardAction.ACTION_START:
                    aForm.setAction(ImportWizardAction.ACTION_MODE);
                    destination=mapping.findForward("start");
                    break;
                    
                case ImportWizardAction.ACTION_MODE:
                    aForm.setAction(ImportWizardAction.ACTION_CSV);
                    destination=mapping.findForward("mode");
                    break;
                    
                case ImportWizardAction.ACTION_CSV:
                    aForm.setAction(ImportWizardAction.ACTION_PARSE);
                    destination=mapping.findForward("mapping");
                    break;
                    
                case ImportWizardAction.ACTION_PARSE:
                    aForm.setAction(ImportWizardAction.ACTION_PRESCAN);
                    destination=mapping.findForward("verify");
                    break;
                    
                case ImportWizardAction.ACTION_PREVIEW_SCROLL:
                    aForm.setAction(ImportWizardAction.ACTION_PRESCAN);
                    destination=mapping.findForward("verify");
                    break;
                
                    // insert here csv "pre-scan" - results:
                    
                case ImportWizardAction.ACTION_PRESCAN:
                    aForm.setAction(ImportWizardAction.ACTION_MLISTS);
                    destination=mapping.findForward("prescan");
                    break;
                    
                case ImportWizardAction.ACTION_MLISTS:
                    aForm.setAction(ImportWizardAction.ACTION_WRITE);
                    destination=mapping.findForward("mlists");
                    break;
                    
                case ImportWizardAction.ACTION_WRITE:
                    aForm.setAction(ImportWizardAction.ACTION_VIEW_STATUS);
                    RequestDispatcher dp=req.getRequestDispatcher(mapping.findForward("view_status").getPath());
                    dp.forward(req, res);
                    res.flushBuffer();
                    destination=null;
                    this.writeContent(aForm, jdbc, req);
                    break;
                    
                case ImportWizardAction.ACTION_VIEW_STATUS:
			destination=mapping.findForward("view_status");
			break;
                    
                case ImportWizardAction.ACTION_VIEW_STATUS_WINDOW:
                    // log results to file:
                    if(aForm.getDbInsertStatus() >= 1000) {
                        String log_entry = "\n* * * * * * * * * * * * * * * * * *\n";
                        TimeZone zone=TimeZone.getTimeZone(((Admin)req.getSession().getAttribute("emm.admin")).getAdminTimezone());
                        
                        EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
                        my_calendar.changeTimeWithZone(zone);
                        java.util.Date my_time = my_calendar.getTime();
                        String datum = my_time.toString();
                        java.text.SimpleDateFormat format01=new java.text.SimpleDateFormat("yyyyMMdd");
                        String aktDate = format01.format(my_calendar.getTime());
                        log_entry += datum + "\n";
                        log_entry += "company_id: " + this.getCompanyID(req) + "\n";
                        log_entry += "admin_id: " + req.getSession().getAttribute("adminID") + "\n";
                        log_entry += "datasource_id: " + aForm.getDatasourceID() + "\n";
                        log_entry += "mode: " + aForm.getMode() + "\n";
                        log_entry += "doublette-check: " + aForm.getStatus().getDoubleCheck() + "\n";
                        log_entry += "ignore null-values: " + aForm.getStatus().getIgnoreNull() + "\n";
                        log_entry += "separator: " + aForm.getStatus().getSeparator() + "\n";
                        log_entry += "delimiter: " + aForm.getStatus().getDelimiter() + "\n";
                        log_entry += "key-column: " + aForm.getStatus().getKeycolumn() + "\n";
                        log_entry += "charset: " + aForm.getStatus().getCharset() + "\n";
                        log_entry += "  csv_errors_email: " + aForm.getStatus().getError(ImportWizardForm.EMAIL_ERROR) + "\n";
                        log_entry += "  csv_errors_blacklist: " + aForm.getStatus().getError(ImportWizardForm.BLACKLIST_ERROR) + "\n";
                        log_entry += "  csv_errors_double: " + aForm.getStatus().getError(ImportWizardForm.EMAILDOUBLE_ERROR) + "\n";
                        log_entry += "  csv_errors_numeric: " + aForm.getStatus().getError(ImportWizardForm.NUMERIC_ERROR) + "\n";
                        log_entry += "  csv_errors_mailtype: " + aForm.getStatus().getError(ImportWizardForm.MAILTYPE_ERROR) + "\n";
                        log_entry += "  csv_errors_gender: " + aForm.getStatus().getError(ImportWizardForm.GENDER_ERROR) + "\n";
                        log_entry += "  csv_errors_date: " + aForm.getStatus().getError(ImportWizardForm.DATE_ERROR) + "\n";
                        log_entry += "  csv_errors_linestructure: " + aForm.getStatus().getError(ImportWizardForm.STRUCTURE_ERROR) + "\n\n";
                        
                        if(aForm.getStatus().getUpdated() >= 0) {
                            log_entry += "  csv records allready in db: " + aForm.getStatus().getUpdated() + "\n";
                        }
                        
                        if(aForm.getMode()==ImportWizardForm.MODE_ADD || aForm.getMode()==ImportWizardForm.MODE_ADD_UPDATE) {
                            log_entry += "  inserted: " + aForm.getStatus().getInserted() + "\n";
                        }
                        if(aForm.getMode()==ImportWizardForm.MODE_ONLY_UPDATE || aForm.getMode()==ImportWizardForm.MODE_ADD_UPDATE) {
                            log_entry += "  updated: " + aForm.getStatus().getUpdated() + "\n";
                        }
                        
                        AgnUtils.logger().info(log_entry + "* * * * * * * * * * * * * * * * * * *\n");
                        
                        try {
                            Writer output = null;
                            try {
                                //use buffering
                                output = new BufferedWriter(new FileWriter(new String(AgnUtils.getDefaultValue("system.upload_archive") + "/" + aktDate + ".txt"), true));
                                output.write(log_entry);
                            } finally {
                                //flush and close both "output" and its underlying FileWriter
                                if (output != null) output.close();
                            }
                        } catch (Exception e) {
                            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
                        }
                    }
			if(aForm.getErrorId() != null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(aForm.getErrorId()));
				aForm.setErrorId(null);
			}
                    destination=mapping.findForward("view_status_window");
                    break;
                    
                case ACTION_GET_ERROR_DATE:
                case ACTION_GET_ERROR_EMAIL:
                case ACTION_GET_ERROR_EMAILDOUBLE:
                case ACTION_GET_ERROR_GENDER:
                case ACTION_GET_ERROR_MAILTYPE:
                case ACTION_GET_ERROR_NUMERIC:
                case ACTION_GET_ERROR_STRUCTURE:
                case ACTION_GET_ERROR_BLACKLIST:
                case ACTION_GET_DATA_PARSED:
                    String outfile=this.getDataFile(aForm, jdbc, req);
                    res.setContentType("text/plain");
                    res.setHeader("Content-Disposition", "attachment; filename=\"" + aForm.getDownloadName() + ".csv\";");
                    ServletOutputStream ostream = res.getOutputStream();
                    ostream.print(outfile);
                    destination=null;
                    break;
                    
                default:
                    aForm.setAction(ImportWizardAction.ACTION_PARSE);
                    destination=mapping.findForward("list");
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty() && destination!=null) {
System.err.println("Save Error");
            saveErrors(req, errors);
            // return new ActionForward(mapping.getForward());
        }
        
        return destination;
    }
    
    /**
     * Creates temporary tables for import process
     *
     * @param aForm InputForm for actual import process
     * @param jdbc valid JdbcTemplate to build temporary tables on
     * @param req The HttpServletRequest that caused this action
     */
    protected void createTemporaryTables(ImportWizardForm aForm, JdbcTemplate jdbc, HttpServletRequest req) {
        int companyID=this.getCompanyID(req);
        String tabName = "cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl";
        String keyIdx = "cust" + companyID + "_tmp"+aForm.getDatasourceID()+"$KEYCOL$IDX";
        String custIdx = "cust" + companyID + "_tmp"+aForm.getDatasourceID()+"$CUSTID$IDX";
        // create temporary table
        String sql=null;
        
        try {
            sql="CREATE TEMPORARY TABLE "+tabName+" AS (SELECT * FROM customer_" + companyID + "_tbl WHERE 1=0)";
            jdbc.execute(sql);
            
            sql="ALTER TABLE "+tabName+" MODIFY change_date TIMESTAMP NULL DEFAULT NULL";
            jdbc.execute(sql);
            
            sql="ALTER TABLE "+tabName+" MODIFY creation_date TIMESTAMP NULL DEFAULT current_timestamp";
            jdbc.execute(sql);
            
            sql="CREATE INDEX " + keyIdx + " ON "+tabName+" ("+SafeString.getSQLSafeString(aForm.getStatus().getKeycolumn())+")";
            jdbc.execute(sql);
            
            sql="CREATE INDEX " + custIdx +" ON "+tabName+" (customer_id)";
            jdbc.execute(sql);
            
        }   catch (Exception e) {
            AgnUtils.logger().error("createTemporaryTables: "+e.getMessage());
            AgnUtils.logger().error("Statement: "+sql);
            e.printStackTrace();
        }
    }
    
    protected void deleteTemporaryTables(ImportWizardForm aForm, JdbcTemplate jdbc, HttpServletRequest req) {
        int companyID=this.getCompanyID(req);
        String tabName = "cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl";

        if(AgnUtils.isOracleDB()) {
            try {
                jdbc.execute("DROP TABLE "+tabName);
            } catch (Exception e) {
                AgnUtils.logger().error("deleteTemporaryTables: "+e.getMessage());
                AgnUtils.logger().error("Table: "+tabName);
                e.printStackTrace();
            }
        }
    }

    /** 
     * Writes new Subscriber-Data through temporary tables to DB
     *
     * @param aForm InputForm for actual import process
     * @param jdbc valid JdbcTemplate to build temporary tables on
     * @param req The HttpServletRequest that caused this action
     */
    protected void writeContent(ImportWizardForm aForm, JdbcTemplate jdbc, HttpServletRequest req) {
        ApplicationContext aContext=this.getWebApplicationContext();
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)aContext.getBean("sessionFactory"));
        org.springframework.orm.hibernate3.HibernateTransactionManager tm=(org.springframework.orm.hibernate3.HibernateTransactionManager) (aContext.getBean("transactionManager"));
        String currentTimestamp=AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName();
        int companyID=this.getCompanyID(req);
        StringBuffer usedColumnsString=new StringBuffer();
        StringBuffer copyColumnsString=new StringBuffer();
        DefaultTransactionDefinition tdef=new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus ts=null;
        File tmpFile=null;
        ListIterator aIt=null;
        CsvColInfo aInfo=null;
        Admin admin=(Admin)req.getSession().getAttribute("emm.admin");
        
        aForm.getStatus().setCompanyID(companyID);
        aForm.getStatus().setAdminID(admin.getAdminID());
        try {
            String fileName=new String(this.getCompanyID(req)+"-"+System.currentTimeMillis());
            
            tmpFile=File.createTempFile(fileName, ".csv", new File(AgnUtils.getDefaultValue("system.upload_archive")));
            FileOutputStream aOut=new FileOutputStream(tmpFile);
            aOut.write(aForm.getCsvFile().getFileData());
            aOut.close();
        } catch (Exception e) {
            AgnUtils.logger().error("writeContent: "+e);
            e.printStackTrace();
            aForm.setErrorId("error.import.exception");
            return;
        }
        
        AgnUtils.logger().info("Import file <"+tmpFile.getAbsolutePath()+"> Original Name: <"+aForm.getCsvFile().getFileName()+">");
        
        aForm.setDbInsertStatusMessages(new LinkedList());
        
        DatasourceDescription dsDescription=getNewDatasourceDescription(companyID, aContext);
        
        dsDescription.setDescription(aForm.getCsvFile().getFileName());
        dsDescription.setCompanyID(companyID);
        
        aForm.getStatus().setDatasourceID(dsDescription.getId());
        
        tm.setDataSource(jdbc.getDataSource());
        AgnUtils.logger().info("Starting transaction");
        ts=tm.getTransaction(tdef);
        try {
            this.createTemporaryTables(aForm, jdbc, req);
            
            int errorsOnInsert=0;
            StringBuffer errorLines=new StringBuffer();
            
            // CUSTOMER_XX_TBL inserts:
            String customer_body = "INSERT INTO cust_" + companyID + "_tmp"+dsDescription.getId()+"_tbl ( datasource_id, change_date, creation_date";
            
            ArrayList usedColumns=new ArrayList();
            aIt=aForm.getCsvAllColumns().listIterator();
            int numFields=0;
            while (aIt.hasNext()) {
                aInfo=(CsvColInfo)aIt.next();
                
                if(aForm.getColumnMapping().containsKey(aInfo.getName())) {
                    String curCol=((CsvColInfo)aForm.getColumnMapping().get(aInfo.getName())).getName();
                    customer_body += ", " + curCol;
                    numFields++;
                    usedColumns.add(aInfo);
                    usedColumnsString.append(curCol+", ");
                    copyColumnsString.append("cust." + curCol + "=temp." + curCol + ", ");
                }
            }
            
            customer_body += " ) VALUES " + "(" + aForm.getDatasourceID() + ", "+currentTimestamp+ ", "+currentTimestamp;
            for(int a=1; a<=numFields; a++) {
                customer_body+=", ?";
            }
            customer_body+=")";
            // values:
            int x=0;
            Object aVal=null;
            try {
                ListIterator contentIterator=aForm.getParsedContent().listIterator();
                LinkedList aLine=null;
                
                while(contentIterator.hasNext()) {
                    try {
                        Vector params=new Vector();
                        
                        aLine=(LinkedList)contentIterator.next();
                        for(int a=0; a<numFields; a++) {
                            aInfo=(CsvColInfo)usedColumns.get(a);
                            aVal=aLine.get(a);
                            if(aInfo.getType()==CsvColInfo.TYPE_CHAR) {
                                params.add((String)aVal);
                            } else if(aInfo.getType()==CsvColInfo.TYPE_NUMERIC) {
                                if(aVal!=null) {
                                    params.add(new Double(((Double)aVal).doubleValue()));
                                } else {
                                    params.add(new Integer(0));
                                }
                            } else if(aInfo.getType()==CsvColInfo.TYPE_DATE) {
                                if(aVal!=null) {
                                    params.add((java.util.Date)aVal);
                                } else {
                                    params.add(new Integer(0));
                                }
                            }
                        }
                        jdbc.update(customer_body, params.toArray());
                    } catch (Exception e1) {
                        errorsOnInsert++;
                        AgnUtils.logger().error("writeContent: "+e1);
                        e1.printStackTrace();
                    }
                    aForm.setDbInsertStatus((int)((((double)x)/aForm.getLinesOK())*100.0));
                    x++;
                }
            } catch (Exception e) {
                AgnUtils.logger().error("writeContent: "+e);
                e.printStackTrace();
            }
            
            aForm.setError(ImportWizardForm.DBINSERT_ERROR, errorLines.toString());
            tm.commit(ts);
        }   catch (Exception e) {
            tm.rollback(ts);
            AgnUtils.logger().error("writeContent: "+e);
            e.printStackTrace();
        }
        
        ts=tm.getTransaction(tdef);
        try {
            String sql=null;
            
            if(aForm.getStatus().getDoubleCheck() == CustomerImportStatus.DOUBLECHECK_FULL) {
                try {
                	sql = "UPDATE cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl temp SET customer_id = (SELECT customer_id FROM customer_" + companyID + "_tbl cust WHERE cust." + SafeString.getSQLSafeString(aForm.getStatus().getKeycolumn()) + "=temp." + SafeString.getSQLSafeString(aForm.getStatus().getKeycolumn()) + " LIMIT 1), datasource_id=0 WHERE temp." + SafeString.getSQLSafeString(aForm.getStatus().getKeycolumn()) + " in (SELECT " + SafeString.getSQLSafeString(aForm.getStatus().getKeycolumn()) + " FROM customer_" + companyID + "_tbl)";
                    aForm.setDbInsertStatus(200);
                    aForm.addDbInsertStatusMessage("csv_delete_double_email");
                    jdbc.execute(sql);
                    
                } catch (Exception e) {
                    AgnUtils.logger().error("writeContent: "+e);
                    AgnUtils.logger().error("Statement: "+sql);
                    e.printStackTrace();
                }
                
            }
            
            aForm.getStatus().setInserted(0);
            aForm.getStatus().setUpdated(0);
            try {
                sql = "SELECT count(temp.datasource_id) FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl temp WHERE datasource_id<>0";
                aForm.getStatus().setInserted(jdbc.queryForInt(sql));
                
                sql = "SELECT count(temp.datasource_id) FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl temp WHERE datasource_id=0";
                aForm.getStatus().setUpdated(jdbc.queryForInt(sql));
            } catch (Exception e) {
                AgnUtils.logger().error("writeContent: "+e);
                AgnUtils.logger().error("Statement: "+sql);
                e.printStackTrace();
            }
            
            if(aForm.getMode()==ImportWizardForm.MODE_ADD_UPDATE || aForm.getMode()==ImportWizardForm.MODE_ONLY_UPDATE) {
                // update existing records
                if(aForm.getStatus().getIgnoreNull()==ImportWizardForm.MODE_DONT_IGNORE_NULL_VALUES) {
                    try {
                    	String tempSubTabName = "cust_" + companyID + "_tmp2_sub" + aForm.getDatasourceID() + "_tbl";
                    	sql="CREATE TEMPORARY TABLE "+tempSubTabName+" AS (SELECT * from cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl tmp WHERE tmp.datasource_id=0)";
                        jdbc.execute(sql);
                        
                    	sql = "UPDATE " +
                        			"customer_" + companyID + "_tbl cust, " +
                        			"cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl temp " +
                        		"SET " +
                        			copyColumnsString.toString()+ "cust.change_date=" + currentTimestamp + " " +
                        		"WHERE " +
                        			"temp.customer_id=cust.customer_id " +
                        		"AND " +
                        			"cust.customer_id in " +
                        				"(SELECT subtmp.customer_id from " + tempSubTabName + " subtmp)";
                        aForm.setDbInsertStatus(250);
                        aForm.addDbInsertStatusMessage("import.update_existing_records");
                        jdbc.execute(sql);
                        sql = "DROP TABLE " + tempSubTabName;
                    }   catch (Exception e) {
                        AgnUtils.logger().error("writeContent: "+e);
                        AgnUtils.logger().error("Statement: "+sql);
                        e.printStackTrace();
                    }
                } else {
                    
                    aForm.setDbInsertStatus(250);
                    aForm.addDbInsertStatusMessage("import.update_existing_records");
                    
                    aIt=aForm.getCsvAllColumns().listIterator();
                    
                    aInfo=null;
                    
                    try {
                        while (aIt.hasNext()) {
                            aInfo=(CsvColInfo)aIt.next();
                            if(aForm.getColumnMapping().containsKey(aInfo.getName())) {
                                aInfo.getName();
                                String tempSubTabName = "cust_" + companyID + "_tmp_3_sub" + aForm.getDatasourceID() + "_tbl";
                            	sql="CREATE TEMPORARY TABLE "+tempSubTabName+" AS (SELECT customer_id from cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl tmp WHERE datasource_id=0 AND "+((CsvColInfo)aForm.getColumnMapping().get(aInfo.getName())).getName()+" is not null)";
                                jdbc.execute(sql);
                            	sql = "UPDATE customer_" + companyID + "_tbl cust SET "+ ((CsvColInfo)aForm.getColumnMapping().get(aInfo.getName())).getName() +" = (SELECT "+((CsvColInfo)aForm.getColumnMapping().get(aInfo.getName())).getName() + " FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl temp WHERE cust.customer_id=temp.customer_id), change_date=" + currentTimestamp + " WHERE cust.customer_id in (SELECT customer_id from " + tempSubTabName + " )";
                                jdbc.execute(sql);
                                sql = "DROP TABLE " + tempSubTabName;
                                jdbc.execute(sql);
                                
                            }
                        }
                    }   catch (Exception e) {
                        AgnUtils.logger().error("writeContent: "+e);
                        AgnUtils.logger().error("Statement: "+sql);
                        e.printStackTrace();
                    }
                }
                
            }
            
            // Move CUSTOMER_XX_TEMP_TBL contents into CUSTOMER_XX_TBL
            // only if adding some subscribers
            if(aForm.getMode()==ImportWizardForm.MODE_ADD || aForm.getMode()==ImportWizardForm.MODE_ADD_UPDATE) {
                try {
                    sql = "INSERT INTO customer_" + companyID + "_tbl ("+usedColumnsString.toString()+"datasource_id, customer_id, change_date, creation_date) SELECT "+usedColumnsString.toString()+"datasource_id, customer_id, change_date, creation_date FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id<>0";
                    aForm.setDbInsertStatus(300);
                    aForm.addDbInsertStatusMessage("import.save_new_records");
                    jdbc.execute(sql);
                    sql = "SELECT max( cust.customer_id ) max_cust, max( cust_seq.customer_id) max_seq from customer_" + companyID + "_tbl_seq cust_seq, customer_" + companyID + "_tbl cust";
                    List<Map<String, Number>> maxIds = jdbc.queryForList( sql );
                    if ( maxIds.size() > 0 ) {
	                    long maxCust = maxIds.get(0).get("max_cust").longValue();
	                    Number maxSeqNumber = maxIds.get(0).get("max_seq");
						long maxSeq = ( maxSeqNumber != null ) ? maxSeqNumber.longValue() : 0;
                    	if ( maxCust > maxSeq ) {
                    		sql = "INSERT INTO customer_" + companyID + "_tbl_seq (customer_id) SELECT max(customer_id) FROM customer_" + companyID + "_tbl";
                    		jdbc.execute(sql);
	                    }
                    }
                }   catch (Exception e) {
                    AgnUtils.logger().error("writeContent: "+e);
                    AgnUtils.logger().error("Statement: "+sql);
                    e.printStackTrace();
                }
                
            }
            
            // BINDINGS (for inserted subscribers only, not updating existing bindings):
            String bindingStmt=null;
            String binding2=null;
            String tmpTblCreate=null;
            String tmpTblRemove=null;
            String tmpTblStat=null;
            String optout=null;
            String bounce=null;
            int mailinglistAdd=0;
            Hashtable mailinglistStat=new Hashtable();
            Enumeration mailingLists=aForm.getMailingLists().elements();
            
            aForm.addDbInsertStatusMessage("import.update_status");
            
            while(mailingLists.hasMoreElements()) {
                Object aObject=mailingLists.nextElement();
                
                try {
                    switch(aForm.getMode()) {
                        case ImportWizardForm.MODE_ADD:
                        case ImportWizardForm.MODE_ADD_UPDATE:
                            aForm.setDbInsertStatus(350);
                            mailinglistAdd=aForm.getStatus().getInserted();
                            bindingStmt = new String("INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) (SELECT customer_id, 'W', 1, 'CSV File Upload', "+currentTimestamp+", 0," + aObject + " FROM customer_" + companyID + "_tbl WHERE datasource_id = " + aForm.getDatasourceID() + ")");
                            jdbc.execute(bindingStmt);
                            tmpTblCreate=new String("CREATE TEMPORARY TABLE cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl AS (SELECT customer_id FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id=0)");
                            jdbc.execute(tmpTblCreate);
                            tmpTblRemove=new String("DELETE FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl WHERE customer_id IN (SELECT customer_id FROM customer_"+companyID+"_binding_tbl WHERE mailinglist_id="+aObject+")");
                            jdbc.execute(tmpTblRemove);
                            tmpTblStat=new String("SELECT count(*) FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl");
                            mailinglistAdd+=jdbc.queryForInt(tmpTblStat);
                            binding2=new String("INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) (SELECT customer_id, 'W', 1, 'CSV File Upload', "+currentTimestamp+", 0," + aObject + " FROM cust_" + companyID + "_exist1_tmp"+aForm.getDatasourceID()+"_tbl)");
                            jdbc.execute(binding2);
                            mailinglistStat.put(aObject, Integer.toString(mailinglistAdd));
                            break;
                            
                        case ImportWizardForm.MODE_ONLY_UPDATE:
                            aForm.setDbInsertStatus(350);
                            mailinglistAdd=0;
                            tmpTblCreate=new String("CREATE TEMPORARY TABLE cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl AS (SELECT customer_id FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id=0)");
                            jdbc.execute(tmpTblCreate);
                            tmpTblRemove=new String("DELETE FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl WHERE customer_id IN (SELECT customer_id FROM customer_"+companyID+"_binding_tbl WHERE mailinglist_id="+aObject+")");
                            jdbc.execute(tmpTblRemove);
                            tmpTblStat=new String("SELECT count(*) FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl");
                            mailinglistAdd+=jdbc.queryForInt(tmpTblStat);
                            binding2="INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) (SELECT customer_id, 'W', 1, 'CSV File Upload', "+currentTimestamp+", 0," + aObject + " FROM cust_" + companyID + "_exist1_tmp"+aForm.getDatasourceID()+"_tbl)";
                            jdbc.execute(binding2);
                            mailinglistStat.put(aObject, Integer.toString(mailinglistAdd));
                            break;
                            
                        case ImportWizardForm.MODE_UNSUBSCRIBE:
                        case ImportWizardForm.MODE_BLACKLIST:
                            aForm.setDbInsertStatus(350);
                            mailinglistAdd=0;
                            tmpTblCreate=new String("CREATE TEMPORARY TABLE cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl AS (SELECT customer_id FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id=0)");
                            jdbc.execute(tmpTblCreate);
                            tmpTblRemove=new String("DELETE FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl WHERE customer_id IN (SELECT customer_id FROM customer_"+companyID+"_binding_tbl WHERE mailinglist_id="+aObject+" AND user_status<>1)");
                            jdbc.execute(tmpTblRemove);
                            tmpTblStat=new String("SELECT count(*) FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl");
                            mailinglistAdd+=jdbc.queryForInt(tmpTblStat);
                            optout=new String("UPDATE customer_" + companyID + "_binding_tbl SET user_status="+BindingEntry.USER_STATUS_ADMINOUT+", exit_mailing_id=0, user_remark='Mass Opt-Out by Admin', timestamp=now() WHERE customer_id IN (SELECT customer_id FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl)");
                            jdbc.execute(optout);
                            mailinglistStat.put(aObject, Integer.toString(mailinglistAdd));
                            break;
                            
                        case ImportWizardForm.MODE_BOUNCE:
                            aForm.setDbInsertStatus(350);
                            mailinglistAdd=0;
                            tmpTblCreate=new String("CREATE TEMPORARY TABLE cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl AS (SELECT customer_id FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id=0)");
                            jdbc.execute(tmpTblCreate);
                            tmpTblRemove=new String("DELETE FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl WHERE customer_id IN (SELECT customer_id FROM customer_"+companyID+"_binding_tbl WHERE mailinglist_id="+aObject+" AND user_status<>1)");
                            jdbc.execute(tmpTblRemove);
                            tmpTblStat=new String("SELECT count(*) FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl");
                            mailinglistAdd+=jdbc.queryForInt(tmpTblStat);
                            bounce=new String("UPDATE customer_" + companyID + "_binding_tbl SET user_status="+BindingEntry.USER_STATUS_BOUNCED+", exit_mailing_id=0, user_remark='Mass Bounce by Admin' WHERE customer_id IN (SELECT customer_id FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl)");
                            jdbc.execute(bounce);
                            mailinglistStat.put(aObject, Integer.toString(mailinglistAdd));
                            break;
                            
                        case ImportWizardForm.MODE_REMOVE_STATUS:
                            aForm.setDbInsertStatus(350);
                            mailinglistAdd=0;
                            tmpTblCreate=new String("CREATE TEMPORARY TABLE cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl AS (SELECT customer_id FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id=0)");
                            jdbc.update(tmpTblCreate);
                            tmpTblRemove=new String("DELETE FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl WHERE customer_id IN (SELECT customer_id FROM customer_"+companyID+"_binding_tbl WHERE mailinglist_id="+aObject+" AND mediatype=0 AND user_status<>1)");
                            jdbc.execute(tmpTblRemove);
                            tmpTblStat=new String("SELECT count(*) FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl");
                            mailinglistAdd+=jdbc.queryForInt(tmpTblStat);
                            bounce=new String("DELETE FROM customer_" + companyID + "_binding_tbl WHERE mailinglist_id=" + aObject + " AND customer_id IN (SELECT customer_id FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl)");
                            jdbc.execute(bounce);
                            mailinglistStat.put(aObject, Integer.toString(mailinglistAdd));
                            break;
                    }
                }   catch (Exception f) {
                    AgnUtils.logger().error("writeContent: "+f);
                    f.printStackTrace();
                }
            }
            
            aForm.setResultMailingListAdded(mailinglistStat);
            tm.commit(ts);
            this.deleteTemporaryTables(aForm, jdbc, req);
        }   catch (Exception e) {
            tm.rollback(ts);
            AgnUtils.logger().error("writeContent: "+e);
            e.printStackTrace();
        }
        
        aForm.addDbInsertStatusMessage("csv_completed");
        aForm.setDbInsertStatus(1000);
        
        aForm.setParsedContent(null);
        aForm.setParsedData(null);
        aForm.setCsvAllColumns(null);
        aForm.getErrorMap().remove(ImportWizardForm.BLACKLIST_ERROR);
        aForm.getErrorMap().remove(ImportWizardForm.DATE_ERROR);
        aForm.getErrorMap().remove(ImportWizardForm.EMAIL_ERROR);
        aForm.getErrorMap().remove(ImportWizardForm.MAILTYPE_ERROR);
        aForm.getErrorMap().remove(ImportWizardForm.NUMERIC_ERROR);
        aForm.getErrorMap().remove(ImportWizardForm.STRUCTURE_ERROR);

        aForm.setCsvFile(null);
        
        tmpl.saveOrUpdate("CustomerImportStatus", aForm.getStatus());
    }
    
    /**
     * Retrieves new Datasource-ID for newly imported Subscribers
     *
     * @return new Datasource-ID or 0
     * @param aContext 
     */
    protected DatasourceDescription getNewDatasourceDescription(int companyID, ApplicationContext aContext) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)aContext.getBean("sessionFactory"));
        DatasourceDescription dsDescription=(DatasourceDescription) aContext.getBean("DatasourceDescription");
        
        dsDescription.setId(0);
        dsDescription.setCompanyID(companyID);
        dsDescription.setSourcegroupID(2);
        dsDescription.setCreationDate(new java.util.Date());
        dsDescription.setDescription(" ");
        tmpl.save("DatasourceDescription", dsDescription);
        return dsDescription;
    }
    
    /**
     * Loads CVS data file from Database
     *
     * @param aForm 
     * @param jdbc 
     * @param req 
     * @return data
     */
    protected String getDataFile(ImportWizardForm aForm, JdbcTemplate jdbc, HttpServletRequest req) {
        String data=new String("");
        
        switch(aForm.getAction()) {
            case ACTION_GET_ERROR_DATE:
                data=aForm.getError(ImportWizardForm.DATE_ERROR).toString();
                break;
            case ACTION_GET_ERROR_EMAIL:
                data=aForm.getError(ImportWizardForm.EMAIL_ERROR).toString();
                break;
            case ACTION_GET_ERROR_EMAILDOUBLE:
                data=aForm.getError(ImportWizardForm.EMAILDOUBLE_ERROR).toString();
                break;
            case ACTION_GET_ERROR_GENDER:
                data=aForm.getError(ImportWizardForm.GENDER_ERROR).toString();
                break;
            case ACTION_GET_ERROR_MAILTYPE:
                data=aForm.getError(ImportWizardForm.MAILTYPE_ERROR).toString();
                break;
            case ACTION_GET_ERROR_NUMERIC:
                data=aForm.getError(ImportWizardForm.NUMERIC_ERROR).toString();
                break;
            case ACTION_GET_ERROR_STRUCTURE:
                data=aForm.getError(ImportWizardForm.STRUCTURE_ERROR).toString();
                break;
            case ACTION_GET_ERROR_BLACKLIST:
                data=aForm.getError(ImportWizardForm.BLACKLIST_ERROR).toString();
                break;
            case ACTION_GET_DATA_PARSED:
                data=aForm.getParsedData().toString();
                break;
        }
        
        return data;
    }
}
