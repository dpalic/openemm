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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.beans.Admin;
import org.agnitas.beans.ExportPredef;
import org.agnitas.dao.ExportPredefDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.emm.core.commons.util.ConfigService;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CsvTokenizer;
import org.agnitas.util.DateUtilities;
import org.agnitas.util.SafeString;
import org.agnitas.web.forms.ExportWizardForm;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * Implementation of <strong>Action</strong> that handles customer exports
 *
 * @author Martin Helff, Nicole Serek
 */

public class ExportWizardAction extends StrutsActionBase {
	public static final String EXPORT_FILE_DIRECTORY = AgnUtils.getTempDir() + File.separator + "RecipientExport";
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(ExportWizardAction.class);

    public static final int ACTION_QUERY = ACTION_LAST+1;

    public static final int ACTION_COLLECT_DATA = ACTION_LAST+2;

    public static final int ACTION_VIEW_STATUS = ACTION_LAST+3;

    public static final int ACTION_DOWNLOAD = ACTION_LAST+4;

    public static final int ACTION_VIEW_STATUS_WINDOW = ACTION_LAST+5;

    public static final int ACTION_CONFIRM_DELETE = ACTION_LAST+6;

    public static final int ACTION_SAVE_QUESTION = ACTION_LAST+7;

    public static final int NO_MAILINGLIST = -1;

    private ExportPredefDao exportPredefDao;
    protected TargetDao targetDao;
    private MailinglistDao mailinglistDao;
    protected DataSource dataSource;
    
	protected ConfigService configService;

	@Required
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
	 * ACTION_LIST: forwards to predefined export definition list page.
	 * <br><br>
	 * ACTION_QUERY: loads chosen predefined export definition data into form or, if there was "Back" button pressed,<br>
     *     clears form data; loads lists of target group and mailing lists into form; <br>
     *     forwards to predefined export definition query page.
	 * <br><br>
     * ACTION_COLLECT_DATA: proceeds exporting recipients from database according to the export definition;<br>
     *     provides storing the export result in temporary zip file, stores name of the temporary file in form;
     *     forwards to export view page.
     * <br><br>
     * ACTION_VIEW_STATUS_WINDOW: forwards to export view page.
     * <br><br>
     * ACTION_DOWNLOAD: provides downloading prepared zip file with list of recipients for export; sends notification <br>
     *     email with export report for admin if the current admin have this option.
	 * <br><br>
     * ACTION_SAVE_QUESTION: forwards to page for edit export definition name and description.
	 * <br><br>
     * ACTION_SAVE: checks the name of export definition:<br>
     *     if it is not filled or its length is less than 3 chars - forwards to page for editing export definition
     *     name and description and shows validation error message<br>
     *     if the name is valid, checks the export definition id value. If id of export definition is 0, inserts new
     *     export definition db entry, otherwise updates db entry with given id.<br>
     *     Forwards to export definition list page.
     * <br><br>
	 * ACTION_CONFIRM_DELETE: checks if an ID of export definition is given and loads the export definition data;<br>
     *     forwards to jsp with question to confirm deletion.
	 * <br><br>
	 * ACTION_DELETE: marks the chosen predefined export definition as deleted and saves the changes in database;<br>
     *     forwards to predefined export definition list page.
	 * <br><br>
	 * Any other ACTION_* would cause a forward to "query"
     * <br><br>
     * @param form ActionForm object, data for the action filled by the jsp
     * @param req HTTP request
     * @param res HTTP response
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination specified in struts-config.xml to forward to next jsp
     */
	@Override
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        // Validate the request parameters specified by the user
        ExportWizardForm aForm = null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination = null;
        ApplicationContext aContext = getWebApplicationContext();

        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }

        if(form != null) {
            aForm = (ExportWizardForm)form;
        } else {
            aForm = new ExportWizardForm();
        }

        logger.info("Action: "+aForm.getAction());

        if(!allowed("wizard.export", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }
        try {
            switch(aForm.getAction()) {

                case ExportWizardAction.ACTION_LIST:
                    destination = mapping.findForward("list");
                    break;

                case ExportWizardAction.ACTION_QUERY:
                    if(req.getParameter("exportPredefID").toString().compareTo("0")!=0) {
                        loadPredefExportFromDB(aForm, aContext, req);
                    } else {
                        // clear form data if the "back" button has not been pressed:
                        if(!AgnUtils.parameterNotEmpty(req, "exp_back.x")) {
                            aForm.clearData();
                        }
                    }

                    int companyID = AgnUtils.getCompanyID(req);
                    aForm.setTargetGroups(targetDao.getTargets(companyID, false));
                    aForm.setMailinglistObjects(mailinglistDao.getMailinglists(companyID));

                    aForm.setAction(ExportWizardAction.ACTION_COLLECT_DATA);
                    destination = mapping.findForward("query");
                    break;

                case ExportWizardAction.ACTION_COLLECT_DATA:
                    if(aForm.tryCollectingData()) {
                        aForm.setAction(ExportWizardAction.ACTION_VIEW_STATUS);
//                        RequestDispatcher dp=req.getRequestDispatcher(mapping.findForward("view").getPath());
//                        dp.forward(req, res);
//                        res.flushBuffer();
//                        destination=null;
						destination = mapping.findForward("view");
                        collectContent(aForm, aContext, req);
                        aForm.resetCollectingData();
                        String filename = getExportFilename(req) + ".zip";
                        aForm.setDownloadName(filename);
                    } else {
                        errors.add("global", new ActionMessage("error.export.already_exporting"));
                    }
                    break;

                case ExportWizardAction.ACTION_VIEW_STATUS_WINDOW:
                    destination = mapping.findForward("view_status");
                    break;

                case ExportWizardAction.ACTION_DOWNLOAD:
                    byte bytes[] = new byte[16384];
                    int len = 0;
                    File exportedFile = checkTempRecipientExportFile(AgnUtils.getCompanyID(req), aForm.getExportedFile(), errors);

                    if(exportedFile != null && aForm.tryCollectingData()) {
                        String filename = getExportFilename(req) + ".zip";
                        aForm.setDownloadName(filename);
                        
                        sendExportNotify(req, aForm);

                        aForm.resetCollectingData();
                        FileInputStream instream = null;
                        try {
							instream = new FileInputStream(exportedFile);
							res.setContentType("application/zip");
							res.setHeader("Content-Disposition", "attachment; filename=\"" + filename +"\";");
							res.setContentLength((int)exportedFile.length());
							ServletOutputStream ostream = res.getOutputStream();
							while((len = instream.read(bytes))!=-1) {
							    ostream.write(bytes, 0, len);
							}
						} finally {
							if (instream != null) {
								instream.close();
							}
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
					if (aForm.getShortname() == null || aForm.getShortname().length() < 3) {
						errors.add("shortname", new ActionMessage("error.nameToShort"));
	                    //aForm.setAction(ExportWizardAction.ACTION_SAVE);
	                    destination=mapping.findForward("savemask");
					} else if(aForm.getExportPredefID() != 0) {
                        saveExport(aForm, aContext, req);
	                    destination=mapping.findForward("list");
                    } else {
                        insertExport(aForm, aContext, req);
	                    destination=mapping.findForward("list");
                    }
                    break;

                case ExportWizardAction.ACTION_CONFIRM_DELETE:
                	if( !"0".equals(req.getParameter("exportPredefID"))) {
                        loadPredefExportFromDB(aForm, aContext, req);
                    }
                	aForm.setAction(ExportWizardAction.ACTION_DELETE);
                    destination=mapping.findForward("delete_question");
                    break;

                case ExportWizardAction.ACTION_DELETE:
                    if(!req.getParameter("exportPredefID").equals( "0")) {
                        markExportDeletedInDB(aForm, aContext, req);
                    }
                    destination=mapping.findForward("list");
                    break;

                default:
                    aForm.setAction(ExportWizardAction.ACTION_QUERY);
                    destination=mapping.findForward("query");
            }

            List<ExportPredef> exports = exportPredefDao.getAllByCompany(AgnUtils.getCompanyID(req));
            aForm.setExportPredefList(exports);
            aForm.setExportPredefCount(exports.size());

        } catch (Exception e) {
        	logger.error("execute: " + e, e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception", configService.getValue(ConfigService.Value.SupportEmergencyUrl)));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            if(destination==null) {
                return new ActionForward(mapping.getInput());
            }
        }

        return destination;
    }

	/**
	 * Generates a filename to be used for the Download.
	 * This name appear in the download window of the clients browser and is NOT the the real name within the webserver's filesystem.
	 * This name has intentionally no fileextension, because this can be .csv or .zip. 
	 * 
	 * @param req
	 * @return
	 */
	protected String getExportFilename(HttpServletRequest req) {
		return AgnUtils.getCompany(req).getShortname() + "_" + DateUtilities.YYYYMD.format(new Date());
	}

	protected void sendExportNotify(HttpServletRequest req, ExportWizardForm aForm) {
		if(req.getSession().getAttribute("notify_email")!=null) {
		    String to_email=(String)req.getSession().getAttribute("notify_email");
		    if(to_email.trim().length()>0) {
		        AgnUtils.sendEmail("openemm@localhost", to_email, "EMM Data-Export", generateReportText(aForm, req), null, 0, "iso-8859-1");
		    }
		}
	}

    /**
     * Loads chosen predefined export data from database into form.
     *
     * @param aForm ExportWizardForm object
     * @param aContext application context
     * @param req HTTP request
     * @return true==success
     *         false==error
     */
    protected boolean loadPredefExportFromDB(ExportWizardForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        CsvTokenizer aTok = null;

        ExportPredef exportPredef=exportPredefDao.get(aForm.getExportPredefID(), AgnUtils.getCompanyID(req));

        if(exportPredef != null) {
        	aForm.setShortname(exportPredef.getShortname());
        	aForm.setDescription(exportPredef.getDescription());
        	aForm.setCharset(exportPredef.getCharset());
        	aForm.setDelimiter(exportPredef.getDelimiter());
        	aForm.setSeparatorInternal(exportPredef.getSeparator());
        	aForm.setTargetID(exportPredef.getTargetID());
        	aForm.setMailinglistID(exportPredef.getMailinglistID());
        	aForm.setUserStatus(exportPredef.getUserStatus());
        	aForm.setUserType(exportPredef.getUserType());

        	SimpleDateFormat inputDateFormat = new SimpleDateFormat(getDatesParameterFormat());
        	Date timestampStart = exportPredef.getTimestampStart();
        	if(timestampStart != null){
        		aForm.setTimestampStart(inputDateFormat.format(timestampStart));
        	}
        	Date timestampEnd = exportPredef.getTimestampEnd();
        	if(timestampEnd != null){
        		aForm.setTimestampEnd(inputDateFormat.format(timestampEnd));
        	}
        	Date creationDateStart = exportPredef.getCreationDateStart();
        	if(creationDateStart != null){
        		aForm.setCreationDateStart(inputDateFormat.format(creationDateStart));
        	}
        	Date creationDateEnd = exportPredef.getCreationDateEnd();
        	if(creationDateEnd != null){
        		aForm.setCreationDateEnd(inputDateFormat.format(creationDateEnd));
        	}
        	Date mailinglistBindStart = exportPredef.getMailinglistBindStart();
        	if(mailinglistBindStart != null){
        		aForm.setMailinglistBindStart(inputDateFormat.format(mailinglistBindStart));
        	}
        	Date mailinglistBindEnd = exportPredef.getMailinglistBindEnd();
        	if(mailinglistBindEnd != null){
        		aForm.setMailinglistBindEnd(inputDateFormat.format(mailinglistBindEnd));
        	}

        	// process columns:
        	try {
        		aTok = new CsvTokenizer(exportPredef.getColumns(), ";");
        		aForm.setColumns(aTok.toArray());

        		if(exportPredef.getMailinglists().trim().length()>0) {
        			aTok = new CsvTokenizer(exportPredef.getMailinglists(), ";");
        			aForm.setMailinglists(aTok.toArray());
        		}
        	} catch (Exception e) {
        		logger.error("loadPredefExportFromDB: "+e, e);
        		return false;
        	}
        } else {
        	logger.error("loadPredefExportFromDB - no ID given?: "+ aForm.getExportPredefID());
    		return false;
        }

        return true;
    }

    /**
     * Creates new predefined export definition database entry
     *
     * @param aForm ExportWizardForm object
     * @param aContext application context
     * @param req HTTP request
     * @return true==success
     *         false==error
     */
    protected boolean insertExport(ExportWizardForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        ExportPredef exportPredef=exportPredefDao.create(AgnUtils.getCompanyID(req));

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
        try {
            loadDateParametersFromFormToBean(aForm, exportPredef);
        } catch (ParseException e) {
        	logger.error(e, e);
        }
        exportPredefDao.save(exportPredef);

        return true;
    }

    /**
     * Updates predefined export definition database entry
     *
     * @param aForm ExportWizardForm object
     * @param aContext application context
     * @param req HTTP request
     * @return true==success
     *         false==error
     */
    protected boolean saveExport(ExportWizardForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        ExportPredef exportPredef=exportPredefDao.get(aForm.getExportPredefID(), AgnUtils.getCompanyID(req));

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
        try {
            loadDateParametersFromFormToBean(aForm, exportPredef);
        } catch (ParseException e) {
        	logger.error(e, e);
        }
        exportPredefDao.save(exportPredef);

        return true;
    }

    /**
     * Loads date values into given bean from the form; parsed dates by certain format before loading.
     *
     * @param aForm  ExportWizardForm object
     * @param exportPredef  ExportPredef bean object (is filling with data from the form inside the method)
     * @throws ParseException
     */
    protected void loadDateParametersFromFormToBean(ExportWizardForm aForm, ExportPredef exportPredef) throws ParseException {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(getDatesParameterFormat());
        String timestampStart = aForm.getTimestampStart();
        if(StringUtils.isNotEmpty(timestampStart)){
            exportPredef.setTimestampStart(inputDateFormat.parse(timestampStart));
        }else {
            exportPredef.setTimestampStart(null);
        }
        String timestampEnd = aForm.getTimestampEnd();
        if(StringUtils.isNotEmpty(timestampEnd)){
            exportPredef.setTimestampEnd(inputDateFormat.parse(timestampEnd));
        }else {
            exportPredef.setTimestampEnd(null);
        }
        String creationDateStart = aForm.getCreationDateStart();
        if(StringUtils.isNotEmpty(creationDateStart)){
            exportPredef.setCreationDateStart(inputDateFormat.parse(creationDateStart));
        }else {
            exportPredef.setCreationDateStart(null);
        }
        String creationDateEnd = aForm.getCreationDateEnd();
        if(StringUtils.isNotEmpty(creationDateEnd)){
            exportPredef.setCreationDateEnd(inputDateFormat.parse(creationDateEnd));
        }else {
            exportPredef.setCreationDateEnd(null);
        }
        String mailinglistBindStart = aForm.getMailinglistBindStart();
        if(StringUtils.isNotEmpty(mailinglistBindStart)){
            exportPredef.setMailinglistBindStart(inputDateFormat.parse(mailinglistBindStart));
        }else {
            exportPredef.setMailinglistBindStart(null);
        }
        String mailinglistBindEnd = aForm.getMailinglistBindEnd();
        if(StringUtils.isNotEmpty(mailinglistBindEnd)){
            exportPredef.setMailinglistBindEnd(inputDateFormat.parse(mailinglistBindEnd));
        }else {
            exportPredef.setMailinglistBindEnd(null);
        }
    }

    /**
     * Marks chosen export definition as deleted and updates its database entry.
     *
     * @param aForm ExportWizardForm object
     * @param aContext application context
     * @param req HTTP request
     * @return true
     */
    protected boolean markExportDeletedInDB(ExportWizardForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        ExportPredef exportPredef=exportPredefDao.get(aForm.getExportPredefID(), AgnUtils.getCompanyID(req));

        exportPredef.setDeleted(1);
        exportPredefDao.save(exportPredef);

        return true;
    }

    /**
     * Creates sql query for getting recipients according to the export definition conditions;
     * gets recipients from database;
     * stores the export result in temporary zip file.
     *
     * @param aForm ExportWizardForm object
     * @param aContext application context
     * @param req HTTP request
     */
	protected void collectContent(ExportWizardForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        // todo this method does too many things - making SQL query, getting DB data, handling CSV and writing to a file. Move this functionality to appropriate methods.

        String startCollect = new SimpleDateFormat("yyyy/MM/dd HH-mm-ss").format(Calendar.getInstance().getTime());
		int companyID = AgnUtils.getCompanyID(req);
        Locale loc_old = Locale.getDefault();

		aForm.setDbExportStatusMessages(new LinkedList<String>());
		aForm.setDbExportStatus(100);
		aForm.setLinesOK(0);

		Target aTarget = null;
		if (aForm.getTargetID() != 0) {
			aTarget = targetDao.getTarget(aForm.getTargetID(), companyID);
			aForm.setTargetID(aTarget.getId());
		}

		String charset = aForm.getCharset();
		if (charset == null || charset.trim().equals("")) {
			charset = "UTF-8";
			aForm.setCharset(charset); // charset also in form
		}

		String customerTableSql = generateSql(aForm, companyID, aTarget);

		Connection con = DataSourceUtils.getConnection(dataSource);

        aForm.setExportedFile(null);
        PrintWriter out = null;
        PreparedStatement preparedStatement = null;
        ResultSet rset = null;
		try {
			File outFile = getTempRecipientExportFile(companyID);
			ZipOutputStream aZip = new ZipOutputStream(new FileOutputStream(outFile));
			logger.info("Export file <" + outFile.getAbsolutePath() + ">");

			preparedStatement = con.prepareStatement(customerTableSql.toString());
			rset = preparedStatement.executeQuery();
			String filename = getExportFilename(req) + ".csv";
			aZip.putNextEntry(new ZipEntry(filename));
			Locale.setDefault(new Locale("en"));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(aZip, charset)));

			ResultSetMetaData mData = rset.getMetaData();
			int columnCount = mData.getColumnCount();

			// Write CSV-Header line
			for (int i = 2; i <= columnCount; i++) {
				if (i > 2) {
					out.print(aForm.getSeparator());
				}
				String columnName = mData.getColumnName(i);
				out.print(aForm.getDelimiter() + escapeChars(columnName, aForm.getDelimiter()) + aForm.getDelimiter());
			}
			out.print("\n");

			// Write data lines
			while (rset.next()) {
				for (int i = 2; i <= columnCount; i++) {
					if (i > 2) {
						out.print(aForm.getSeparator());
					}
					
					String aValue;
					try {
						aValue = rset.getString(i);
					} catch (Exception ex) {
						aValue = null;
						// Exceptions should not break the export, but should be logged
						logger.error("Exception in export:collectContent:", ex);
					}
					
					if (aValue == null) {
						// null values should be displayed as empty string
						aValue = "";
					} else {
						aValue = escapeChars(aValue, aForm.getDelimiter());
						aValue = aForm.getDelimiter() + aValue + aForm.getDelimiter();
					}
					out.print(aValue);
				}
				out.print("\n");
				aForm.setLinesOK(aForm.getLinesOK() + 1);
			}
			aForm.setExportedFile(outFile.getName());
		} catch (Exception e) {
			logger.error("collectContent: " + e, e);
		} finally {
			if (out != null) {
				out.close();
			}
			
			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
			
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
			
			DataSourceUtils.releaseConnection(con, dataSource);
			aForm.setDbExportStatus(1001);
			Locale.setDefault(loc_old);
            writeCollectContentLog(aForm, req, startCollect );

		}
    }

    /**
     * Write log collect recipients data log
     * @param aForm : ExportWizardForm object
     * @param request : request
     * @param startCollect : export start time
     */
    private void writeCollectContentLog(ExportWizardForm aForm, HttpServletRequest request, String startCollect){
        Admin admin = AgnUtils.getAdmin(request);
        int companyId = AgnUtils.getCompanyID(request);

        String mailingList = getMailingListById(aForm.getMailinglistID(), companyId);
        String targetGroup = getTargetGroupById(aForm.getTargetID(), companyId);
        String recipientType = getRecipientTypeByLetter(aForm.getUserType());
        String recipientStatus = getRecipientStatusById(aForm.getUserStatus());
        int numberOfColumns = aForm.getColumns().length;

        writeUserActivityLog(admin, "do export",
                "Export started at: " + startCollect + ". " +
                        "Number of profiles: " + aForm.getLinesOK() + ". " +
                        "Export parameters:" +
                        " mailing list: " + mailingList +
                        ", target group: " + targetGroup +
                        ", recipient type: " + recipientType +
                        ", recipient status: " + recipientStatus +
                        ", number of selected columns: " + numberOfColumns);

    }

    /**
     *  Get a text representation of export parameter "Recipient type"
     *
     * @param letter recipient type letter
     * @return text representation of recipient type
     */
    private String getRecipientTypeByLetter(String letter){
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
     *  Get a text representation of export parameter "Recipient status"
     *
     * @param statusId recipient status id
     * @return text representation of recipient status
     */
    private String getRecipientStatusById(int statusId){
        switch (statusId){
            case 0:
                return "All";
            case 1:
                return "Active";
            case 2:
                return "Bounced";
            case 3:
                return "Opt-Out by admin";
            case 4:
                return "Opt-Out by recipient";
            case 5:
                return "Waiting for user confirmation";
            case 6:
                return "blacklisted";
            case 7:
                return "suspended";
            default:
                return "not set";
        }
    }

    /**
     *  Get a text representation of export parameter "target group"
     *
     * @param targetId target group id
     * @param companyId company id
     * @return a text representation of export parameter "target group"
     */
    private String getTargetGroupById(int targetId, int companyId){
       if (targetId == 0) {
           return "All";
       } else {
           return targetDao.getTarget(targetId, companyId).getTargetName();
       }
    }

    /**
     *  Get a text representation of export parameter "Mailing list"
     *
     * @param listId mailing list id
     * @param companyId company id
     * @return a text representation of export parameter "Mailing list"
     */
    private String getMailingListById(int listId, int companyId){
       if (listId == 0) {
           return "All";
       } else if (listId == -1) {
           return "No mailing list";
       } else {
           return mailinglistDao.getMailinglist(listId, companyId).getShortname();
       }
    }

    private String getDatesParameterFormat(){
        return "dd.MM.yyyy";
    }

    protected String getDatesDBFormat(){
        return "yyyy-MM-dd";
    }

    protected String getTimestampColumnName(){
        return "change_date";
    }

    protected String getSubqueryAlias(){
        return " AS customer_data ";
    }

    protected String getDateSqlParam(String dateString){
        return "'"+dateString+"'";
    }

    protected String formatFormDateToDB(String dateString){
        SimpleDateFormat formatter = new SimpleDateFormat(getDatesParameterFormat());
        try {
            Date parsed = formatter.parse(dateString);
            formatter.applyPattern(getDatesDBFormat());
            return formatter.format(parsed);
        } catch (ParseException e) {
        	logger.error("formatFormDateToDB: "+e, e);
            return "";
        }
    }

    /**
     * Creates report with description of export to be sent to admin in notification email.
     *
     * @param aForm : ExportWizardForm object
     * @param req : request
     * @return  report text
     */
    protected String generateReportText(ExportWizardForm aForm, HttpServletRequest req) {
        StringBuffer report=new StringBuffer("");
        Locale locale = req.getLocale();
        
        String targetGroup = "";
        if(aForm.getTargetID() > 0) {
        	targetGroup = targetDao.getTarget(aForm.getTargetID(), aForm.getCompanyID(req)).getTargetName();
        } else {
        	targetGroup = SafeString.getLocaleString("statistic.All_Subscribers", locale);
        }
        
        String mailingList = "";
        if(aForm.getMailinglistID() > 0) {
        	mailingList = mailinglistDao.getMailinglist(aForm.getMailinglistID(), aForm.getCompanyID(req)).getShortname();
        } else {
        	mailingList = SafeString.getLocaleString("statistic.All_Mailinglists", locale);
        }
        
        report.append(SafeString.getLocaleString("target.Target", locale) + ": "+ targetGroup +"\n");
        report.append(SafeString.getLocaleString("mailinglist", locale) + ": "+ mailingList +"\n");
        report.append(SafeString.getLocaleString("recipient.calculate", locale) + ": "+aForm.getLinesOK()+"\n");
        report.append("IP-Adress while download: "+req.getRemoteAddr()+"\n");
        report.append("Admin-ID: "+ AgnUtils.getAdmin(req).getAdminID()+"\n");
        report.append("Filename: "+aForm.getDownloadName()+"\n");

        return report.toString();
    }

	protected String generateSql(ExportWizardForm aForm, int companyID, Target aTarget) {
		StringBuffer usedColumnsString = new StringBuffer();
		for (String columnName : aForm.getColumns()) {
			// customer_id may be selected twice, for the export starting at column 2
			usedColumnsString.append(", cust." + columnName + " " + columnName);
		}

		if (aForm.getMailinglists() != null) {
			for (int i = 0; i < aForm.getMailinglists().length; i++) {
				String mailinglistID = aForm.getMailinglists()[i];
				usedColumnsString.append(", (SELECT m" + mailinglistID + ".user_status FROM customer_" + companyID + "_binding_tbl m" + mailinglistID + " WHERE m" + mailinglistID + ".customer_id = cust.customer_id AND m" + mailinglistID + ".mailinglist_id = " + mailinglistID + " AND m" + mailinglistID + ".mediatype = 0) as Userstate_Mailinglist_" + mailinglistID);
				usedColumnsString.append(", (SELECT m" + mailinglistID + "." + AgnUtils.changeDateName() + " FROM customer_" + companyID + "_binding_tbl m" + mailinglistID + " WHERE m" + mailinglistID + ".customer_id = cust.customer_id AND m" + mailinglistID + ".mailinglist_id = " + mailinglistID + " AND m" + mailinglistID + ".mediatype = 0) as Mailinglist_" + mailinglistID + "_Timestamp");
				usedColumnsString.append(", (SELECT m" + mailinglistID + ".user_remark FROM customer_" + companyID + "_binding_tbl m" + mailinglistID + " WHERE m" + mailinglistID + ".customer_id = cust.customer_id AND m" + mailinglistID + ".mailinglist_id = " + mailinglistID + " AND m" + mailinglistID + ".mediatype = 0) AS Mailinglist_" + mailinglistID + "_UserRemark");
			}
		}
		
		StringBuffer whereString = new StringBuffer("");
		StringBuffer customerTableSql = new StringBuffer("SELECT * FROM (SELECT DISTINCT cust.customer_id AS unique_customer_id" + usedColumnsString.toString() + " FROM customer_" + companyID + "_tbl cust");
		
		if (aForm.getMailinglistID() != -1 && (aForm.getMailinglistID() > 0 || !aForm.getUserType().equals("E") || aForm.getUserStatus() != 0)) {
			customerTableSql.append(", customer_" + companyID + "_binding_tbl bind");
			whereString.append(" cust.customer_id = bind.customer_id AND bind.mediatype = 0");
		}

		if (aForm.getMailinglistID() > 0) {
			whereString.append(" AND bind.mailinglist_id = " + aForm.getMailinglistID());
		}

		if (aForm.getMailinglistID() == NO_MAILINGLIST) {
			whereString.append(" NOT EXISTS (SELECT 1 FROM customer_" + companyID + "_binding_tbl bind WHERE cust.customer_id = bind.customer_id)");
		} else {
			if (!aForm.getUserType().equals("E")) {
				// Value must be set later
				whereString.append(" AND bind.user_type = '" + aForm.getUserType() + "'");
			}

			if (aForm.getUserStatus() != 0) {
				whereString.append(" AND bind.user_status = " + aForm.getUserStatus());
			}
		}

		if (aForm.getTargetID() != 0) {
			if (aForm.getMailinglistID() != 0 || !aForm.getUserType().equals("E") || aForm.getUserStatus() != 0) {
				whereString.append(" AND ");
			}
			whereString.append(" (" + aTarget.getTargetSQL() + ")");
		}

        if (!StringUtils.isEmpty(whereString.toString())){
            whereString.append(" AND ");
        }
        
        String datesParameters = " 1 = 1 ";
        String timestampStart = aForm.getTimestampStart();
        if (StringUtils.isNotEmpty(timestampStart)) {
            String dbDate = formatFormDateToDB(timestampStart);
            datesParameters += " AND cust." + getTimestampColumnName() + " >= " + getDateSqlParam(dbDate);
        }
        String timestampEnd = aForm.getTimestampEnd();
        if (StringUtils.isNotEmpty(timestampEnd)) {
            if (!datesParameters.isEmpty()) {
                datesParameters += " AND ";
            }
            String dbDate = formatFormDateToDB(timestampEnd);
            datesParameters += "cust." + getTimestampColumnName() + " <= " + getDateSqlParam(dbDate);
        }
        String creationDateStart = aForm.getCreationDateStart();
        if (StringUtils.isNotEmpty(creationDateStart)) {
            if (!datesParameters.isEmpty()){
                datesParameters += " AND ";
            }
            String dbDate = formatFormDateToDB(creationDateStart);
            datesParameters += "cust.creation_date >= " + getDateSqlParam(dbDate);
        }
        String creationDateEnd = aForm.getCreationDateEnd();
        if (StringUtils.isNotEmpty(creationDateEnd)) {
            if (!datesParameters.isEmpty()){
                datesParameters += " AND ";
            }
            String dbDate = formatFormDateToDB(creationDateEnd);
            datesParameters += "cust.creation_date <= " + getDateSqlParam(dbDate);
        }
        String[] mailinglists = aForm.getMailinglists();
        datesParameters += " )" + getSubqueryAlias();

        if (mailinglists != null && mailinglists.length > 0) {
            String mailinglistBindStart = aForm.getMailinglistBindStart();
            boolean andRequired = false;
            if (StringUtils.isNotEmpty(mailinglistBindStart)) {
                datesParameters += " WHERE ";
                for (int i = 0; i < mailinglists.length; i++) {
                    if (i != 0){
                        datesParameters += " AND ";
                    }
                    String dbDate = formatFormDateToDB(mailinglistBindStart);
                    datesParameters += "Mailinglist_"+mailinglists[i]+"_Timestamp >= " + getDateSqlParam(dbDate);
                }
                andRequired = true;
            }
            String mailinglistBindEnd = aForm.getMailinglistBindEnd();
            if (StringUtils.isNotEmpty(mailinglistBindEnd)) {
                if (andRequired) {
                    datesParameters += " AND ";
                }
                for (int i = 0; i < mailinglists.length; i++) {
                    if (i != 0) {
                        datesParameters += " AND ";
                    }
                    String dbDate = formatFormDateToDB(mailinglistBindEnd);
                    datesParameters += "Mailinglist_" + mailinglists[i] + "_Timestamp <= " + getDateSqlParam(dbDate);
                }
            }
        }
        whereString.append(datesParameters);

        if (whereString.length() > 0) {
            customerTableSql.append(" WHERE " + whereString);
        }

        String statementString = customerTableSql.toString();
        logger.info("Generated export SQL query: " + statementString);
		return statementString;
	}

	private File getTempRecipientExportFile(int companyID) {
		File companyCsvExportDirectory = new File(EXPORT_FILE_DIRECTORY + File.separator + companyID);
		if (!companyCsvExportDirectory.exists()) {
			companyCsvExportDirectory.mkdirs();
		}
		
		String dateString = DateUtilities.YYYY_MM_DD_HH_MM_SS_FORFILENAMES.format(new Date());
		File importTempFile = new File(companyCsvExportDirectory, "RecipientExport_" + companyID + "_" + dateString + ".zip");
		int duplicateCount = 1;
		while (importTempFile.exists()) {
			importTempFile = new File(companyCsvExportDirectory, "RecipientExport_" + companyID + "_" + dateString + "_" + (duplicateCount++) + ".zip");
		}
		
		return importTempFile;
	}
	
	private File checkTempRecipientExportFile(int companyID, String fileName, ActionMessages errors) {
		File companyCsvExportDirectory = new File(EXPORT_FILE_DIRECTORY + File.separator + companyID);
		if (!companyCsvExportDirectory.exists()) {
			companyCsvExportDirectory.mkdirs();
		}
		
		if (StringUtils.isNotBlank(fileName)) {
			String mandatoryExportTempFilePrefix = "RecipientExport_" + companyID + "_";
			
			if (!fileName.startsWith(mandatoryExportTempFilePrefix) || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
				logger.error("Illegal temp file for export: " + fileName);
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				return null;
			} else {
				return new File(companyCsvExportDirectory, fileName);
			}
		} else {
			return null;
		}
	}

    public ExportPredefDao getExportPredefDao() {
        return exportPredefDao;
    }

    public void setExportPredefDao(ExportPredefDao exportPredefDao) {
        this.exportPredefDao = exportPredefDao;
    }

    public TargetDao getTargetDao() {
        return targetDao;
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMailinglistDao(MailinglistDao mailinglistDao) {
        this.mailinglistDao = mailinglistDao;
    }
}