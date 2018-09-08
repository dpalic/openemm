package org.agnitas.emm.core.autoexport.service;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.sql.DataSource;

import org.agnitas.beans.ExportPredef;
import org.agnitas.dao.TargetDao;
import org.agnitas.emm.core.autoexport.bean.AutoExport;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.ExportWizardAction;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class ExportWizardService {

    private static final transient Logger logger = Logger.getLogger(ExportWizardService.class);

    private DataSource dataSource;
    private TargetDao targetDao;

    public static String CSV_CREATE_DATE_FORMAT = "yyyyMMdd-hh:mm";
    public static String DATES_DB_FORMAT = "yyyy-MM-dd";
    public static String DATES_PARAMETER_FORMAT = "dd.MM.yyyy";

    public File createExportFile(AutoExport autoExport, ExportPredef exportProfile, int companyID){
        File outFile = null;
        Locale loc_old = Locale.getDefault();

        Target aTarget = targetDao.getTarget(exportProfile.getTargetID(), companyID);

        String charset = exportProfile.getCharset();
        if (charset == null || charset.trim().equals("")) {
            charset = "UTF-8";
        }

        StringBuffer usedColumnsString = new StringBuffer();
        int exportStartColumn = 2;
        for (String columnName : exportProfile.getColumns().split(";")) {
            // customer_id is selected by default in the the base sql statement
            if ("customer_id".equalsIgnoreCase(columnName)) {
                // mark customer_id to be exported too
                exportStartColumn = 1;
            } else {
                usedColumnsString.append(", cust." + columnName + " " + columnName);
            }
        }

        if (exportProfile.getMailinglists() != null) {
            String[] mailingLists = exportProfile.getMailinglists().split(";");
            for (int i = 0; i < mailingLists.length; i++) {
                String ml = mailingLists[i];
                if (!ml.isEmpty()){
                    usedColumnsString.append(", (SELECT m" + ml + ".user_status FROM customer_" + companyID + "_binding_tbl m" + ml + " WHERE m" + ml + ".customer_id = cust.customer_id AND m" + ml + ".mailinglist_id = " + ml + " AND m" + ml + ".mediatype = 0) as Userstate_Mailinglist_" + ml);
                    usedColumnsString.append(", (SELECT m" + ml + "." + AgnUtils.changeDateName() + " FROM customer_" + companyID + "_binding_tbl m" + ml + " WHERE m" + ml + ".customer_id = cust.customer_id AND m" + ml + ".mailinglist_id = " + ml + " AND m" + ml + ".mediatype = 0) as Mailinglist_" + ml + "_Timestamp");
                }
            }
        }

        StringBuffer whereString = new StringBuffer("");
        StringBuffer customerTableSql = new StringBuffer("SELECT * FROM (SELECT DISTINCT cust.customer_id" + usedColumnsString.toString() + " FROM customer_" + companyID + "_tbl cust");

        if (exportProfile.getMailinglistID() != -1 && (exportProfile.getMailinglistID() > 0 || !exportProfile.getUserType().equals("E") || exportProfile.getUserStatus() != 0)) {
            customerTableSql.append(", customer_" + companyID + "_binding_tbl bind");
            whereString.append(" cust.customer_id = bind.customer_id AND bind.mediatype=0");
        }

        if (exportProfile.getMailinglistID() > 0) {
            whereString.append(" and bind.mailinglist_id = " + exportProfile.getMailinglistID());
        }

        if (exportProfile.getMailinglistID() == ExportWizardAction.NO_MAILINGLIST) {
            whereString.append(" NOT EXISTS (SELECT 1 FROM customer_" + companyID + "_binding_tbl bind WHERE cust.customer_id = bind.customer_id) ");
        } else {
            if (!exportProfile.getUserType().equals("E")) {
            	// Value must be set later
                whereString.append(" AND bind.user_type = ?");
            }

            if (exportProfile.getUserStatus() != 0) {
                whereString.append(" AND bind.user_status = " + exportProfile.getUserStatus());
            }
        }

        if (exportProfile.getTargetID() != 0) {
            if (exportProfile.getMailinglistID() != 0 || !exportProfile.getUserType().equals("E") || exportProfile.getUserStatus() != 0) {
                whereString.append(" AND ");
            }
            whereString.append(" (" + aTarget.getTargetSQL() + ")");
        }

        String datesParametersString = getDatesParametersString(exportProfile);
        if(!StringUtils.isEmpty(whereString.toString())){
            whereString.append(" and ");
        }
        whereString.append(datesParametersString);

        if (whereString.length() > 0) {
            customerTableSql.append(" WHERE " + whereString);
        }

        logger.info("Generated export SQL query: " + customerTableSql);

        Connection con = DataSourceUtils.getConnection(dataSource);

        PrintWriter out = null;
        PreparedStatement preparedStatement = null;
        ResultSet rset = null;
        try {
            File systemUploadDirectory = AgnUtils.createDirectory(AgnUtils.getDefaultValue("system.upload"));
            String filename = getExportCSVFileName();
            outFile = new File(systemUploadDirectory, filename + ".csv");

            logger.info("Export file <" + outFile.getAbsolutePath() + ">");

            preparedStatement = con.prepareStatement(customerTableSql.toString());
            if (exportProfile.getMailinglistID() != ExportWizardAction.NO_MAILINGLIST && !exportProfile.getUserType().equals("E")) {
            	// Setting value for "bind.user_type = ?"
            	preparedStatement.setString(1, exportProfile.getUserType());
            }
            rset = preparedStatement.executeQuery();

            Locale.setDefault(new Locale("en"));
            out = new PrintWriter(outFile);

            ResultSetMetaData mData = rset.getMetaData();
            int columnCount = mData.getColumnCount();

            // Write CSV-Header line
            for (int i = exportStartColumn; i <= columnCount; i++) {
                if (i > exportStartColumn) {
                    out.print(exportProfile.getSeparator());
                }
                String columnName = mData.getColumnName(i);
                out.print(exportProfile.getDelimiter() + escapeChars(columnName, exportProfile.getDelimiter()) + exportProfile.getDelimiter());
            }
            out.print("\n");

            // Write data lines
            while (rset.next()) {
                for (int i = exportStartColumn; i <= columnCount; i++) {
                    if (i > exportStartColumn) {
                        out.print(exportProfile.getSeparator());
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
                        aValue = escapeChars(aValue, exportProfile.getDelimiter());
                        aValue = exportProfile.getDelimiter() + aValue + exportProfile.getDelimiter();
                    }
                    out.print(aValue);
                }
                out.print("\n");
            }
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
            Locale.setDefault(loc_old);
        }
        return outFile;
    }

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

    private String getDatesParametersString(ExportPredef exportProfile) {
        String datesParameters = " 1 = 1 ";
        Date timestampStart = exportProfile.getTimestampStart();
        if (timestampStart != null) {
            String dbDate = formatFormDateToDB(timestampStart.toString());
            datesParameters += " and cust." + getTimestampColumnName() + " >= " + getDateSqlParam(dbDate);
        }
        Date timestampEnd = exportProfile.getTimestampEnd();
        if (timestampEnd != null) {
            if(!datesParameters.isEmpty()){
                datesParameters += " and ";
            }
            String dbDate = formatFormDateToDB(timestampEnd.toString());
            datesParameters += "cust." + getTimestampColumnName() + " <= " + getDateSqlParam(dbDate);
        }
        Date creationDateStart = exportProfile.getCreationDateStart();
        if (creationDateStart != null) {
            if(!datesParameters.isEmpty()){
                datesParameters += " and ";
            }
            String dbDate = formatFormDateToDB(creationDateStart.toString());
            datesParameters += "cust.creation_date >= " + getDateSqlParam(dbDate);
        }
        Date creationDateEnd = exportProfile.getCreationDateEnd();
        if (creationDateEnd != null) {
            if(!datesParameters.isEmpty()){
                datesParameters += "and ";
            }
            String dbDate = formatFormDateToDB(creationDateEnd.toString());
            datesParameters += "cust.creation_date <= " + getDateSqlParam(dbDate);
        }
        String[] mailinglists = null;
        String mailinglistsString = exportProfile.getMailinglists();
        if (mailinglistsString != null){
            mailinglists = mailinglistsString.split(";");
        }
        boolean isNotAvailableMailinglists = mailinglists == null || mailinglists.length <= 0 || (mailinglists.length == 1 &&  mailinglists[0].isEmpty() );
        datesParameters += " )" + getSubqueryAlias();

        if (!isNotAvailableMailinglists) {
            Date mailinglistBindStart = exportProfile.getMailinglistBindStart();
            boolean andRequired = false;
            if (mailinglistBindStart != null) {
                datesParameters += " WHERE ";
                for (int i = 0; i < mailinglists.length; i++) {
                    if(i != 0){
                        datesParameters += " and ";
                    }
                    String dbDate = formatFormDateToDB(mailinglistBindStart.toString());
                    datesParameters += "Mailinglist_"+mailinglists[i]+"_Timestamp >= " + getDateSqlParam(dbDate);
                }
                andRequired = true;
            }
            Date mailinglistBindEnd = exportProfile.getMailinglistBindEnd();
            if (mailinglistBindEnd != null) {
                if (andRequired) {
                    datesParameters += " and ";
                }
                for (int i = 0; i < mailinglists.length; i++) {
                    if (i != 0) {
                        datesParameters += " and ";
                    }
                    String dbDate = formatFormDateToDB(mailinglistBindEnd.toString());
                    datesParameters += "Mailinglist_" + mailinglists[i] + "_Timestamp <= " + getDateSqlParam(dbDate);
                }
            }
        }
        return datesParameters;
    }

    private String formatFormDateToDB(String dateString){
        SimpleDateFormat formatter = new SimpleDateFormat(ExportWizardService.DATES_DB_FORMAT);
        try {
            java.util.Date parsed = formatter.parse(dateString);
            formatter.applyPattern(ExportWizardService.DATES_PARAMETER_FORMAT);
            return formatter.format(parsed);
        } catch (ParseException e) {
            logger.error("formatFormDateToDB: "+e, e);
            return "";
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    protected String getDateSqlParam(String dateString) {
        if (AgnUtils.isOracleDB()) {
            return "to_date('" + dateString + "', '" + ExportWizardService.DATES_PARAMETER_FORMAT + "') ";
        }
        return "'"+dateString+"'";
    }

    protected String getTimestampColumnName(){
        if (AgnUtils.isOracleDB()) {
            return "timestamp";
        }
        return "change_date";
    }

    protected String getSubqueryAlias() {
        if (AgnUtils.isOracleDB()) {
            return "";
        }
        return " AS customer_data ";
    }

    public String getExportCSVFileName(){
        SimpleDateFormat formatter = new SimpleDateFormat(ExportWizardService.CSV_CREATE_DATE_FORMAT);
        String formattedCreateDate = formatter.format(new Date());
        return "export_" + formattedCreateDate;
    }

}
