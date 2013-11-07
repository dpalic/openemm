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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.beans.ExportPredef;
import org.agnitas.dao.ExportPredefDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CsvTokenizer;
import org.agnitas.util.SafeString;
import org.agnitas.web.forms.ExportWizardForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;


/**
 * Implementation of <strong>Action</strong> that handles customer exports
 *
 * @author Martin Helff
 */

public class ExportWizardAction extends StrutsActionBase {

    public static final int ACTION_QUERY = ACTION_LAST+1;

    public static final int ACTION_COLLECT_DATA = ACTION_LAST+2;

    public static final int ACTION_VIEW_STATUS = ACTION_LAST+3;

    public static final int ACTION_DOWNLOAD = ACTION_LAST+4;

    public static final int ACTION_VIEW_STATUS_WINDOW = ACTION_LAST+5;

    public static final int ACTION_CONFIRM_DELETE = ACTION_LAST+6;

    public static final int ACTION_SAVE_QUESTION = ACTION_LAST+7;

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
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        // Validate the request parameters specified by the user
        ExportWizardForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        ApplicationContext aContext=this.getWebApplicationContext();

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        if(form!=null) {
            aForm=(ExportWizardForm)form;
        } else {
            aForm=new ExportWizardForm();
        }

        AgnUtils.logger().info("Action: "+aForm.getAction());

        if(!allowed("wizard.export", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }
        try {
            switch(aForm.getAction()) {

                case ExportWizardAction.ACTION_LIST:
                    destination=mapping.findForward("list");
                    break;

                case ExportWizardAction.ACTION_QUERY:
                    if( req.getParameter("exportPredefID").toString().compareTo("0")!=0) {
                        loadPredefExportFromDB(aForm, aContext, req);
                    } else {
                        // clear form data if the "back" button has not been pressed:
                        if(req.getParameter("exp_back.x")==null) {
                            aForm.clearData();
                        }
                    }
                    aForm.setAction(ExportWizardAction.ACTION_COLLECT_DATA);
                    destination=mapping.findForward("query");
                    break;

                case ExportWizardAction.ACTION_COLLECT_DATA:
                    if(aForm.tryCollectingData()) {
                        aForm.setAction(ExportWizardAction.ACTION_VIEW_STATUS);
                        RequestDispatcher dp=req.getRequestDispatcher(mapping.findForward("view").getPath());
                        dp.forward(req, res);
                        res.flushBuffer();
                        destination=null;
                        collectContent(aForm, aContext, req);
                        aForm.resetCollectingData();
                    } else {
                        errors.add("global", new ActionMessage("error.export.already_exporting"));
                    }
                    break;

                case ExportWizardAction.ACTION_VIEW_STATUS_WINDOW:
                    destination=mapping.findForward("view_status");
                    break;

                case ExportWizardAction.ACTION_DOWNLOAD:
                    byte bytes[]=new byte[16384];
                    int len=0;
                    File outfile=aForm.getCsvFile();

                    if(outfile!=null && aForm.tryCollectingData()) {
                        if(req.getSession().getAttribute("notify_email")!=null) {
                            String to_email=(String)req.getSession().getAttribute("notify_email");
                            if(to_email.trim().length()>0) {
                                AgnUtils.sendEmail("service@agnitas.de", to_email, "EMM Data-Export", this.generateReportText(aForm, req), null, 0, "iso-8859-1");
                            }
                        }

                        aForm.resetCollectingData();
                        FileInputStream instream=new FileInputStream(outfile);
                        res.setContentType("application/zip");
                        res.setHeader("Content-Disposition", "attachment; filename=\"" + outfile.getName()+"\";");
                        res.setContentLength((int)outfile.length());
                        ServletOutputStream ostream = res.getOutputStream();
                        while((len=instream.read(bytes))!=-1) {
                            ostream.write(bytes, 0, len);
                        }
                        destination=null;
                    } else {
                        errors.add("global", new ActionMessage("error.export.file_not_ready"));
                    }
                    break;

                case ExportWizardAction.ACTION_SAVE_QUESTION:
                    aForm.setAction(ExportWizardAction.ACTION_SAVE);
                    destination=mapping.findForward("savemask");
                    break;

                case ExportWizardAction.ACTION_SAVE:

                    if(aForm.getExportPredefID() != 0) {
                        saveExport(aForm, aContext, req);
                    } else {
                        insertExport(aForm, aContext, req);
                    }
                    destination=mapping.findForward("list");
                    break;

                case ExportWizardAction.ACTION_CONFIRM_DELETE:
                	if( !"0".equals(req.getParameter("exportPredefID"))) {
                        loadPredefExportFromDB(aForm, aContext, req);
                    }
                	aForm.setAction(ExportWizardAction.ACTION_DELETE);
                    destination=mapping.findForward("delete_question");
                    break;

                case ExportWizardAction.ACTION_DELETE:
                    if(req.getParameter("exportPredefID")!="0") {
                        markExportDeletedInDB(aForm, aContext, req);
                    }
                    destination=mapping.findForward("list");
                    break;

                default:
                    aForm.setAction(ExportWizardAction.ACTION_QUERY);
                    destination=mapping.findForward("query");
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            if(destination==null)
                return new ActionForward(mapping.getInput());
        }

        return destination;
    }

    /**
     * Loads predefinition from database.
     *
     * @return true==success
     *false==error
     */
    protected boolean loadPredefExportFromDB(ExportWizardForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        CsvTokenizer aTok = null;
        ExportPredefDao exportPredefDao=(ExportPredefDao) aContext.getBean("ExportPredefDao");
        ExportPredef exportPredef=exportPredefDao.get(aForm.getExportPredefID(), getCompanyID(req));

        aForm.setShortname(exportPredef.getShortname());
        aForm.setDescription(exportPredef.getDescription());
        aForm.setCharset(exportPredef.getCharset());
        aForm.setDelimiter(exportPredef.getDelimiter());
        aForm.setSeparatorInternal(exportPredef.getSeparator());
        aForm.setTargetID(exportPredef.getTargetID());
        aForm.setMailinglistID(exportPredef.getMailinglistID());
        aForm.setUserStatus(exportPredef.getUserStatus());
        aForm.setUserType(exportPredef.getUserType());


        // process columns:
        try {
            aTok = new CsvTokenizer(exportPredef.getColumns(), ";");
            aForm.setColumns(aTok.toArray());

			if(exportPredef.getMailinglists().trim().length()>0) {
                aTok = new CsvTokenizer(exportPredef.getMailinglists(), ";");
                aForm.setMailinglists(aTok.toArray());
			}
        } catch (Exception e) {
            AgnUtils.logger().error("loadPredefExportFromDB: "+e);
            return false;
        }

        return true;
    }

    /**
     * Inserts predefinition into database.
     */
    protected boolean insertExport(ExportWizardForm aForm, ApplicationContext aContext, HttpServletRequest req) {

        ExportPredefDao exportPredefDao=(ExportPredefDao) aContext.getBean("ExportPredefDao");
        ExportPredef exportPredef=exportPredefDao.get(0, getCompanyID(req));

        // perform insert:
        exportPredef.setShortname(aForm.getShortname());
        exportPredef.setDescription(aForm.getDescription());
        exportPredef.setCharset(aForm.getCharset());
        exportPredef.setColumns(CsvTokenizer.join(aForm.getColumns(), ";"));
        exportPredef.setMailinglists(CsvTokenizer.join(aForm.getMailinglists(), ";"));
        exportPredef.setMailinglistID(aForm.getMailinglistID());
        exportPredef.setDelimiter(aForm.getDelimiter());
        String separator = aForm.getSeparator();
        separator = "\t".equals( separator ) ? "t" : separator;
		exportPredef.setSeparator(separator);
        exportPredef.setTargetID(aForm.getTargetID());
        exportPredef.setUserStatus(aForm.getUserStatus());
        exportPredef.setUserType(aForm.getUserType());
        exportPredefDao.save(exportPredef);

        return true;
    }

    /**
     * Saves predef
     */
    protected boolean saveExport(ExportWizardForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        ExportPredefDao exportPredefDao=(ExportPredefDao) aContext.getBean("ExportPredefDao");
        ExportPredef exportPredef=exportPredefDao.get(aForm.getExportPredefID(), getCompanyID(req));

        // perform update in db:
        exportPredef.setShortname(aForm.getShortname());
        exportPredef.setDescription(aForm.getDescription());
        exportPredef.setCharset(aForm.getCharset());
        exportPredef.setColumns(CsvTokenizer.join(aForm.getColumns(), ";"));
        exportPredef.setMailinglists(CsvTokenizer.join(aForm.getMailinglists(), ";"));
        exportPredef.setMailinglistID(aForm.getMailinglistID());
        exportPredef.setDelimiter(aForm.getDelimiter());
        String separator = aForm.getSeparator();
        separator = "\t".equals( separator ) ? "t" : separator;
		exportPredef.setSeparator(separator);
        exportPredef.setTargetID(aForm.getTargetID());
        exportPredef.setUserStatus(aForm.getUserStatus());
        exportPredef.setUserType(aForm.getUserType());
        exportPredefDao.save(exportPredef);

        return true;
    }

    /**
     * Sets a mark for deletion into database.
     */
    protected boolean markExportDeletedInDB(ExportWizardForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        ExportPredefDao exportPredefDao=(ExportPredefDao) aContext.getBean("ExportPredefDao");
        ExportPredef exportPredef=exportPredefDao.get(aForm.getExportPredefID(), getCompanyID(req));

        exportPredef.setDeleted(1);
        exportPredefDao.save(exportPredef);

        return true;
    }

    /**
     * Gets the content to a request form database.
     */
    protected void collectContent(ExportWizardForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        DataSource ds=(DataSource) getBean("dataSource");
        int i=0;
        int columnCount=0;
        int companyID=this.getCompanyID(req);
        String charset=null;
        String aValue=null;
        StringBuffer usedColumnsString=new StringBuffer();
        TargetDao targetDao=(TargetDao) aContext.getBean("TargetDao");
        Target aTarget=null;
        Locale loc = new Locale("en");
        Locale loc_old = Locale.getDefault();

        aForm.setDbExportStatusMessages(new LinkedList());
        aForm.setDbExportStatus(100);
        aForm.setLinesOK(0);

        if(aForm.getTargetID()!=0) {
            aTarget=targetDao.getTarget(aForm.getTargetID(), companyID);
            aForm.setTargetID(aTarget.getId());
        }

        charset=aForm.getCharset();
        if(charset == null || charset.trim().equals("")) {
            charset = "UTF-8";
            aForm.setCharset(charset); //charset also in form
        }

        for(i=0; i < aForm.getColumns().length; i++) {
            if(i != 0) {
                usedColumnsString.append(", ");
            }
            usedColumnsString.append("cust."+aForm.getColumns()[i]);
        }

        if(aForm.getMailinglists()!=null) {
            for(i=0; i<aForm.getMailinglists().length; i++) {
                String ml=aForm.getMailinglists()[i];
                usedColumnsString.append(", (select m"+ml+".user_status FROM customer_"+companyID+"_binding_tbl m"+ml+" WHERE m"+ml+".customer_id=cust.customer_id AND m"+ml+".mailinglist_id="+ml+" AND m"+ml+".mediatype=0) as agn_m"+ml);
                usedColumnsString.append(", (select m"+ml+"."+AgnUtils.changeDateName()+" FROM customer_"+companyID+"_binding_tbl m"+ml+" WHERE m"+ml+".customer_id=cust.customer_id AND m"+ml+".mailinglist_id="+ml+" AND m"+ml+".mediatype=0) as agn_mt"+ml);
            }
        }

        StringBuffer whereString=new StringBuffer("");
        StringBuffer customerTableSql=new StringBuffer("SELECT DISTINCT(cust.customer_id), "+usedColumnsString.toString()+" FROM customer_"+companyID+"_tbl cust");
        if(aForm.getMailinglistID()!=0 || !aForm.getUserType().equals("E") || aForm.getUserStatus()!=0) {
            customerTableSql.append(", customer_"+companyID+"_binding_tbl bind");
            whereString.append(" cust.customer_id=bind.customer_id and bind.mediatype=0");
        }

        if(aForm.getMailinglistID()!=0) {
            whereString.append(" and bind.mailinglist_id="+aForm.getMailinglistID());
        }

        if(!aForm.getUserType().equals("E")) {
            whereString.append(" and bind.user_type='"+SafeString.getSQLSafeString(aForm.getUserType())+"'");
        }

        if(aForm.getUserStatus()!=0) {
            whereString.append(" and bind.user_status="+aForm.getUserStatus());
        }

        if(aForm.getTargetID()!=0) {
            if(aForm.getMailinglistID()!=0 || !aForm.getUserType().equals("E") || aForm.getUserStatus()!=0) {
                whereString.append(" and ");
            }
            whereString.append(" ("+aTarget.getTargetSQL()+")");
        }

        if(whereString.length()>0) {
            customerTableSql.append(" WHERE "+whereString);
        }

        Connection con=DataSourceUtils.getConnection(ds);

        aForm.setCsvFile(null);
        try {
            File outFile=File.createTempFile("exp"+companyID+"_", ".zip", new File(AgnUtils.getDefaultValue("system.upload")));
            ZipOutputStream aZip=new ZipOutputStream(new FileOutputStream(outFile));
            AgnUtils.logger().info("Export file <"+outFile.getAbsolutePath()+">");

            Statement stmt=con.createStatement();
            ResultSet rset=stmt.executeQuery(customerTableSql.toString());

            aZip.putNextEntry(new ZipEntry("emm_export.csv"));
            Locale.setDefault(loc);
            PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(aZip, charset)));

//            rset=jdbc.queryForRowSet(customerTableSql.toString());
            ResultSetMetaData mData=rset.getMetaData();
            columnCount=mData.getColumnCount();

            // build CSV-Header
            for(i=2; i<=columnCount; i++) {
                if(i!=2) {
                    out.print(aForm.getSeparator());
                }
                out.print(aForm.getDelimiter()+escapeChars(mData.getColumnName(i), aForm.getDelimiter())+aForm.getDelimiter());
            }
            out.print("\n");

            while(rset.next()) {
                // custID=new Integer(rset.getInt(1));
                for(i=2; i<=columnCount; i++) {
                    if(i!=2) {
                        out.print(aForm.getSeparator());
                    }
                    try{
                    	aValue=rset.getString(i);
                    }
                    catch ( Exception ex ) {
                    	aValue= null; // Exceptions should not break the export,
                    	AgnUtils.logger().error( "Exception in export:collectContent:", ex );// but we have to log it
                    }
                    if(aValue == null) { // null values should be empty, not String "null"
                        aValue="";
                    } else {
                        aValue=escapeChars(aValue, aForm.getDelimiter());
                        aValue=aForm.getDelimiter()+aValue+aForm.getDelimiter();
                    }
                    out.print(aValue);
                }
                out.print("\n");
                aForm.setLinesOK(aForm.getLinesOK()+1);
            }
            out.close();
            aForm.setCsvFile(outFile);
            rset.close();
            stmt.close();
        } catch (Exception e) {
            AgnUtils.logger().error("collectContent: "+e);
            e.printStackTrace();
        }
        DataSourceUtils.releaseConnection(con, ds);
        aForm.setDbExportStatus(1001);
        Locale.setDefault(loc_old);
    }

    /**
     * Separates special characters from input string.
     */
    protected String escapeChars(String input, String sepChar) {
        int pos=0;
        StringBuffer tmp=new StringBuffer(input);
        while((pos=input.indexOf(sepChar, pos))!=-1) {
            tmp=new StringBuffer(input);
            tmp.insert(pos, sepChar);
            pos+=sepChar.length()+1;
            input=tmp.toString();
        }
        return input;
    }

    /**
     * Generates a report text.
     */
    protected String generateReportText(ExportWizardForm aForm, HttpServletRequest req) {
        StringBuffer report=new StringBuffer("");

        report.append("Target-Group: "+aForm.getTargetID()+"\n");
        report.append("Mailing-List: "+aForm.getMailinglistID()+"\n");
        report.append("Number of Records: "+aForm.getLinesOK()+"\n");
        report.append("IP-Adress while download: "+req.getRemoteAddr()+"\n");
        report.append("Admin-ID: "+req.getSession().getAttribute("adminID")+"\n");
        report.append("Filename: "+aForm.getDownloadName()+"\n");

        return report.toString();
    }
}
