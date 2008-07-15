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

package org.agnitas.web;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.apache.struts.upload.*;
import org.agnitas.util.*;
import org.agnitas.beans.*;
import java.util.*;
import java.sql.*;
import javax.sql.*;
import java.text.*;
import java.io.*;
import javax.mail.internet.InternetAddress;
import org.springframework.context.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.rowset.*;
import org.hibernate.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.orm.hibernate3.*;

/**
 *
 * @author  mhe
 */
public class ImportWizardForm extends StrutsFormBase {

    private CustomerImportStatus status=null;
 
    /**
     * Holds value of property action. 
     */
    private int action;
    
    /**
     * Holds value of property csvFile. 
     */
    private FormFile csvFile;
    
    /**
     * Holds value of property csvAllColumns. 
     */
    private ArrayList csvAllColumns;
    
    /**
     * Holds value of property mailingLists. 
     */
    private Vector mailingLists;
    
    /**
     * Holds value of property usedColumns. 
     */
    private ArrayList usedColumns;
    
    /**
     * Holds value of property parsedContent. 
     */
    private LinkedList parsedContent;
    
    /**
     * Holds value of property uniqueValues. 
     */
    private HashSet uniqueValues;
    
    /**
     * Holds value of property dbAllColumns. 
     */
    private Hashtable dbAllColumns;
    
    /**
     * Holds value of property mode. 
     */
    private int mode;

    /**
     * Holds value of property dateFormat. 
     */
    private String dateFormat="dd.MM.yyyy HH:mm";
   
    /**
     * Constants
     */
    public static final String DATE_ERROR = "date";

    public static final String EMAIL_ERROR = "email";

    public static final String EMAILDOUBLE_ERROR = "emailDouble";

    public static final String GENDER_ERROR = "gender";

    public static final String MAILTYPE_ERROR = "mailtype";

    public static final String NUMERIC_ERROR = "numeric";

    public static final String STRUCTURE_ERROR = "structure";

    public static final String BLACKLIST_ERROR = "blacklist";

    public static final String DBINSERT_ERROR = "dbinsert";
 

    public static final int MODE_ADD = 1;
    
    public static final int MODE_ADD_UPDATE = 2;
    
    public static final int MODE_ONLY_UPDATE = 3;
    
    public static final int MODE_UNSUBSCRIBE = 4;
    
    public static final int MODE_BOUNCE = 5;
    
    public static final int MODE_BLACKLIST = 6;
    
    public static final int MODE_DELETE = 7;
    
    public static final int MODE_REMOVE_STATUS = 8;
    
/*    public static final int DOUBLECHECK_FULL = 0;
    
    public static final int DOUBLECHECK_CSV = 1;
    
    public static final int DOUBLECHECK_NONE = 2;
*/
    
    /** 
     * Holds value of property linesOK. 
     */
    private int linesOK;
    
    /**
     * Holds value of property dbInsertStatus. 
     */
    private int dbInsertStatus;
    
    /**
     * Holds value of property errorData. 
     */
    private HashMap errorData=new HashMap();    

    /**
     * Holds value of property parsedData. 
     */
    private StringBuffer parsedData;
    
    /**
     * Holds value of property downloadName. 
     */
    private String downloadName;
    
    /**
     * Holds value of property dbInsertStatusMessages. 
     */
    private LinkedList dbInsertStatusMessages;
    
    /** 
     * Holds value of property resultMailingListAdded. 
     */
    private Hashtable resultMailingListAdded;
    
    /** 
     * Holds value of property blacklist. 
     */
    private HashSet blacklist;
    
    public static final int MODE_DONT_IGNORE_NULL_VALUES = 0;
    
    public static final int MODE_IGNORE_NULL_VALUES = 1;
    
    protected int csvMaxUsedColumn = 0;
    
    /** 
     * Holds value of property keyColumn. 
     */
    private String keyColumn = new String("email");
    
    /**
     * Holds value of property previewOffset. 
     */
    private int previewOffset;
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionMessages</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionMessages</code> object with no
     * recorded error messages.
     * 
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     * @return errors
     */
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ApplicationContext aContext=this.getWebApplicationContext();
        ActionErrors errors = new ActionErrors();
        
        AgnUtils.logger().info("validate: "+this.action);

        switch(this.action) {
            
            case ImportWizardAction.ACTION_START:
                Integer id=null;

                status=(CustomerImportStatus) aContext.getBean("CustomerImportStatus"); 
                status.setId(0);
                break;

            case ImportWizardAction.ACTION_CSV:
                if(request.getParameter("mode_back.x")!=null) {
                    this.action=ImportWizardAction.ACTION_START;
                } else {
                    errors=parseFirstline(request);
                }
                break;
                
            case ImportWizardAction.ACTION_PARSE:
                if(request.getParameter("mapping_back.x")!=null) {
                    this.action=ImportWizardAction.ACTION_MODE;
                } else {
                    // do column mapping:
                    mapColumns(request);
                    // start at the top of the csv file:
                    this.previewOffset=0;
                    // change this to process the column name mapping from previous action:
                    errors=this.parseContent(request);
                    // we have too many rows:
                    if(this.linesOK>Integer.parseInt(AgnUtils.getDefaultValue("import.maxrows"))) {
                        errors.add("global", new ActionMessage("error.import.too_many_records"));
                    }
                }
                break;
                
            case ImportWizardAction.ACTION_MODE:
                break;
                
                
                
            case ImportWizardAction.ACTION_PRESCAN:
                if(request.getParameter("verify_back.x")!=null) {
                    this.action=ImportWizardAction.ACTION_CSV;
                } else {
                    // default keyColumn to "EMAIL"
                    if(this.keyColumn==null || this.keyColumn.trim().equals("")) {
                        this.keyColumn=new String("email");
                    }
                }
                break;
                
            case ImportWizardAction.ACTION_MLISTS:
                if(request.getParameter("prescan_back.x")!=null) {
                    this.action=ImportWizardAction.ACTION_PREVIEW_SCROLL;
                }
                break;
                
            case ImportWizardAction.ACTION_WRITE:
                if(request.getParameter("mlists_back.x")!=null) {
                    this.action=ImportWizardAction.ACTION_PRESCAN;
                } else {
                    getMailinglistsFromRequest(request);
                }
                break;
                
            case ImportWizardAction.ACTION_PREVIEW_SCROLL:
                if(this.parsedContent!=null) {
                    if(this.previewOffset>=parsedContent.size()) {
                        this.previewOffset=parsedContent.size()-6;
                    }
                }
                if(this.previewOffset<0) {
                    this.previewOffset=0;
                }
                break;
                
        }
        
        return errors;
        
    }
   
    /**
     * Getter for property datasourceID.
     *
     * @return Value of property datasourceID.
     */
    public int getDatasourceID() {
        return status.getDatasourceID();
    }
    
    /**
     * Sets an error.
     */
    public void setError(String id, String desc) {
        status.addError(id);
        if(!errorData.containsKey(id)) {
            errorData.put(id, new StringBuffer());
        }
        ((StringBuffer) errorData.get(id)).append(desc+"\n");
        status.addError("all");
    }

    /**
     * Getter for property error.
     *
     * @return Value of property error.
     */
    public StringBuffer getError(String id) {
        return (StringBuffer) errorData.get(id);
    }

    /**
     * Getter for property errorMap.
     *
     * @return Value of property errorMap.
     */
    public HashMap getErrorMap() {
        return errorData;
    }

    /** 
     * Getter for property status.
     *
     * @return Value of property status.
     */
    public CustomerImportStatus getStatus() {
        return status;
    }
    
    /**
     * Setter for property charset.
     * 
     * @param status  New value of property status.
     */
    public void setStatus(CustomerImportStatus status) {
        this.status=status;
    }
    
    /** 
     * Getter for property action.
     *
     * @return Value of property action.
     */
    public int getAction() {
        return this.action;
    }
    
    /**
     * Setter for property action.
     *
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }
    
    /**
     * Getter for property csvFile.
     *
     * @return Value of property csvFile.
     */
    public FormFile getCsvFile() {
        return this.csvFile;
    }
    
    /** 
     * Setter for property csvFile.
     *
     * @param csvFile New value of property csvFile.
     */
    public void setCsvFile(FormFile csvFile) {
        this.csvFile = csvFile;
    }
    
    /** 
     * Getter for property csvAllColumns.
     *
     * @return Value of property csvAllColumns.
     */
    public ArrayList getCsvAllColumns() {
        return this.csvAllColumns;
    }
    
    /**
     * Setter for property csvAllColumns.
     *
     * @param csvAllColumns New value of property csvAllColumns.
     */
    public void setCsvAllColumns(ArrayList csvAllColumns) {
        this.csvAllColumns = csvAllColumns;
    }
    
    /**
     * Getter for property mailingLists.
     *
     * @return Value of property mailingLists.
     *
     */
    public Vector getMailingLists() {
        return this.mailingLists;
    }
    
    /** 
     * Setter for property mailingLists.
     *
     * @param mailingLists New value of property mailingLists.
     */
    public void setMailingLists(Vector mailingLists) {
        this.mailingLists = mailingLists;
    }
    
    /** 
     * Getter for property usedColumns.
     *
     * @return Value of property usedColumns.
     */
    public ArrayList getUsedColumns() {
        return this.usedColumns;
    }
    
    /** 
     * Setter for property usedColumns.
     *
     * @param usedColumns New value of property usedColumns.
     */
    public void setUsedColumns(ArrayList usedColumns) {
        this.usedColumns = usedColumns;
    }
    
    /**
     * Getter for property parsedContent.
     *
     * @return Value of property parsedContent.
     */
    public LinkedList getParsedContent() {
        return this.parsedContent;
    }
    
    /** 
     * Setter for property parsedContent.
     *
     * @param parsedContent New value of property parsedContent.
     */
    public void setParsedContent(LinkedList parsedContent) {
        this.parsedContent = parsedContent;
    }
    
    /** 
     * Getter for property emailAdresses.
     *
     * @return Value of property emailAdresses.
     */
    public HashSet getUniqueValues() {
        return this.uniqueValues;
    }
    
    /**
     * Setter for property emailAdresses.
     * 
     * @param uniqueValues 
     */
    public void setUniqueValues(HashSet uniqueValues) {
        this.uniqueValues = uniqueValues;
    }
    
    /**
     * Getter for property dbAllColumns.
     *
     * @return Value of property dbAllColumns.
     */
    public Hashtable getDbAllColumns() {
        return this.dbAllColumns;
    }
    
    /**
     * Setter for property dbAllColumns.
     *
     * @param dbAllColumns New value of property dbAllColumns.
     */
    public void setDbAllColumns(Hashtable dbAllColumns) {
        this.dbAllColumns = dbAllColumns;
    }
    
    /** 
     * Getter for property mode.
     *
     * @return Value of property mode.
     */
    public int getMode() {
        return this.mode;
    }
    
    /**
     * Setter for property mode.
     *
     * @param mode New value of property mode.
     */
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    /**
     * Reads columns from database.
     */ 
    protected void readDBColumns(int companyID, JdbcTemplate jdbc) {
        SqlRowSet rset=null;
        String sqlGetTblStruct = "SELECT * FROM customer_" + companyID + "_tbl WHERE 1=0";
        CsvColInfo aCol=null;
        String colType=null;

        dbAllColumns=new Hashtable();
        try {
            rset=jdbc.queryForRowSet(sqlGetTblStruct);
            SqlRowSetMetaData meta=rset.getMetaData();
            
            for(int i=1; i<=meta.getColumnCount(); i++) {
                if(!meta.getColumnName(i).equals("change_date") && !meta.getColumnName(i).equals("creation_date") && !meta.getColumnName(i).equals("datasource_id")) {
                    if(meta.getColumnName(i).equals("customer_id")) {
                        if(!(this.mode==ImportWizardForm.MODE_ONLY_UPDATE && this.keyColumn.equals("customer_id"))) {
                            continue;
                        }
                    }
                    
                    aCol=new CsvColInfo();
                    aCol.setName(meta.getColumnName(i));
                    aCol.setLength(meta.getColumnDisplaySize(i));
                    aCol.setType(CsvColInfo.TYPE_UNKNOWN);
                    aCol.setActive(false);
                    colType=meta.getColumnTypeName(i);
                    if(colType.startsWith("VARCHAR")) {
                        aCol.setType(CsvColInfo.TYPE_CHAR);
                    } else if(colType.startsWith("CHAR")) {
                        aCol.setType(CsvColInfo.TYPE_CHAR);
                    } else if(colType.startsWith("NUM")) {
                        aCol.setType(CsvColInfo.TYPE_NUMERIC);
                    } else if(colType.startsWith("INTEGER")) {
                        aCol.setType(CsvColInfo.TYPE_NUMERIC);
                    } else if(colType.startsWith("DOUBLE")) {
                        aCol.setType(CsvColInfo.TYPE_NUMERIC);
                    } else if(colType.startsWith("TIME")) {
                        aCol.setType(CsvColInfo.TYPE_DATE);
                    } else if(colType.startsWith("DATE")) {
                        aCol.setType(CsvColInfo.TYPE_DATE);
                    }
                    this.dbAllColumns.put(meta.getColumnName(i), aCol);
                }
            }
        } catch (Exception e) {
            AgnUtils.logger().error("readDBColumns: "+e);
        }
    }
    
    /**
     * Loads blacklist.
     */
    protected void loadBlacklist(int companyID, JdbcTemplate jdbc) throws Exception {
        SqlRowSet rset=null;
        String blackList=null;
        Object[] params=new Object[] { new Integer(companyID) };

        this.blacklist=new HashSet();
        try {
            blackList="SELECT email FROM cust_ban_tbl WHERE company_id=? OR company_id=0";
            rset=jdbc.queryForRowSet(blackList, params);
            while(rset.next()) {
                this.blacklist.add(rset.getString(1).toLowerCase());
            }

        } catch (Exception e) {
            AgnUtils.logger().error("loadBlacklist: "+e);
            throw new Exception(e.getMessage());
        }
        
        return;
    }
    
    /**
     * Creates a simple date format
     * When mapping for a column is found get real csv column information
     * Checks email / email adress / email adress on blacklist. 
     */
    protected LinkedList parseLine(String input) {
        //EnhStringTokenizer aLine = null;
        CsvTokenizer aLine = null;
        int j=0;
        String aValue=null;
        CsvColInfo aInfo=null;
        CsvColInfo aCsvInfo=null;
        LinkedList valueList=new LinkedList();
        int tmp=0;
        int atPos=0;
        // SimpleDateFormat aDateFormat=new SimpleDateFormat("dd.MM.yyyy HH:mm");
        InternetAddress adr=null;

        if(this.dateFormat==null || this.dateFormat.trim().length()==0) {
            this.dateFormat=new String("dd.MM.yyyy HH:mm");
        }
        
        SimpleDateFormat aDateFormat=new SimpleDateFormat(this.dateFormat);

        aLine = new CsvTokenizer(input, status.getSeparator(), status.getDelimiter());
        try {
            while((aValue=aLine.nextToken())!=null) {
                aCsvInfo=(CsvColInfo)this.csvAllColumns.get(j);
                
                // only when mapping for this column is found:
                if(this.getColumnMapping().containsKey(aCsvInfo.getName())) {

                    // get real CsvColInfo object:
                    aInfo=(CsvColInfo)this.getColumnMapping().get(aCsvInfo.getName());
                    
                    aValue=aValue.trim();
                    // do this before eventual duplicate check on Col Email
                    if(aInfo.getName().equalsIgnoreCase("email")) {
                        aValue=aValue.toLowerCase();
                    }
                    if(status.getDoubleCheck() != CustomerImportStatus.DOUBLECHECK_NONE && this.keyColumn.equalsIgnoreCase(aInfo.getName())) {
                        if(this.uniqueValues.add(aValue)==false) {
                            setError(EMAILDOUBLE_ERROR, input+"\n");
                            AgnUtils.logger().error("Duplicate email: "+input);
                            return null;
                        }
                    }
                    if(aInfo.getName().equalsIgnoreCase("email")) {
                        if(aValue.length()==0) {
                            setError(EMAIL_ERROR, input+"\n");
                            AgnUtils.logger().error("Empty email: "+input);
                            return null;
                        }
                        if((atPos=aValue.indexOf('@'))==-1) {
                            setError(EMAIL_ERROR, input+"\n");
                            AgnUtils.logger().error("No @ in email: "+input);
                            return null;
                        }
                        
                        try {
                            adr=new InternetAddress(aValue);
                        } catch (Exception e) {
                            setError(EMAIL_ERROR, input+"\n");
                            AgnUtils.logger().error("InternetAddress error: "+input);
                            return null;
                        }
                        // check blacklist
                        if(AgnUtils.matchCollection(aValue, this.blacklist)) {
                            setError(BLACKLIST_ERROR, input+"\n");
                            AgnUtils.logger().error("Blacklisted: "+input);
                            return null;
                        }
                    } else if(aInfo.getName().equals("mailtype")) {
                        try {
                            tmp=Integer.parseInt(aValue);
                            if(tmp<0 || tmp>2) {
                                throw new Exception("Invalid mailtype");
                            }
                        } catch (Exception e) {
                            setError(MAILTYPE_ERROR, input+"\n");
                            AgnUtils.logger().error("Invalid mailtype: "+input);
                            return null;
                        }
                    } else if(aInfo.getName().equals("gender")) {
                        try {
                            tmp=Integer.parseInt(aValue);
                            if(tmp<0 || tmp>5) {
                                throw new Exception("Invalid gender");
                            }
                        } catch (Exception e) {
                            setError(GENDER_ERROR, input+"\n");
                            AgnUtils.logger().error("Invalid gender: "+aValue);
                            return null;
                        }
                    }
                    if(aInfo!=null && aInfo.isActive()) {
                        if(aValue.length()==0) { // is null value
                            valueList.add(null);
                        } else {
                            switch (aInfo.getType()) {
                                case CsvColInfo.TYPE_CHAR:
                                    valueList.add(SafeString.cutByteLength(aValue, aInfo.getLength()));
                                    break;
                                    
                                case CsvColInfo.TYPE_NUMERIC:
                                    try {
                                        valueList.add(Double.valueOf(aValue));
                                    } catch (Exception e) {
                                        setError(NUMERIC_ERROR, input+"\n");
                                        AgnUtils.logger().error("Numberformat error: "+input);
                                        return null;
                                    }
                                    break;
                                    
                                case CsvColInfo.TYPE_DATE:
                                    try {
                                        valueList.add(aDateFormat.parse(aValue));
                                    } catch (Exception e) {
                                        setError(DATE_ERROR, input+"\n");
                                        AgnUtils.logger().error("Dateformat error: "+input);
                                        return null;
                                    }
                            }
                        }
                    }
                }
                j++;
            }
        } catch (Exception e) {
            setError(STRUCTURE_ERROR, input+"\n");
            AgnUtils.logger().error("parseLine: "+e);
            return null;
        }
        
        if(this.csvMaxUsedColumn!=j) {
            setError(STRUCTURE_ERROR, input+"\n");
            AgnUtils.logger().error("MaxusedColumn: "+this.csvMaxUsedColumn+", "+j);
            return null;
        }
        
        return valueList;
    }
    
    /**
     * Maps columns from database.
     */
    protected void mapColumns(HttpServletRequest req) {
        
        int i = 1;
        CsvColInfo aCol=null;
        CsvColInfo bCol=null;
        
        // initialize columnMapping hashtable:
        this.columnMapping=new Hashtable();
        
        for(i=1; i<(csvAllColumns.size()+1);i++) {
            String pName=new String("map_"+i);
            if(req.getParameter(pName)!=null) {
                aCol=(CsvColInfo)csvAllColumns.get(i-1);
                if(req.getParameter(pName).compareTo("NOOP")!=0) {
                    CsvColInfo aInfo = (CsvColInfo)dbAllColumns.get(req.getParameter(pName));
                    this.columnMapping.put(aCol.getName(), aInfo);
                                     
                    aInfo.setActive(true);
                    // write db column (set active now) back to dbAllColums:
                    this.dbAllColumns.put(new String(req.getParameter(pName)), aInfo);

                    // adjust & write back csvAllColumns hashtable entry:
                    aCol.setActive(true);
                    aCol.setLength(aInfo.getLength());
                    aCol.setType(aInfo.getType());
  
                    this.csvAllColumns.set(i-1, aCol); 
                }
            }
        }  
        return;
    }
    
    /**
     * Tries to read csv file
     * Reads database column structure
     * reads first line
     * splits line into tokens
     */
    protected ActionErrors parseFirstline(HttpServletRequest req) {
        ApplicationContext aContext=this.getWebApplicationContext();
        JdbcTemplate jdbc=new JdbcTemplate((DataSource)aContext.getBean("dataSource")); 
        String csvString=new String("");
        String firstline=null;
        int tmp_length=0;
        int lastPos=0;
        ActionErrors errors=new ActionErrors();
        CsvColInfo aDbCol=null;
        int colNum=0;

        // try to read csv file:
        try {
            csvString=new String(this.getCsvFile().getFileData(), status.getCharset());
        } catch (Exception e) {
            AgnUtils.logger().error("parseFirstline: "+e);
            errors.add("global", new ActionMessage("error.import.charset"));
            return errors;
        }
        
        if(csvString.length()==0) {
            errors.add("global", new ActionMessage("error.import.no_file"));
        }
        
        // read out DB column structure:
        this.readDBColumns(this.getCompanyID(req), jdbc);
        this.csvAllColumns=new ArrayList();
        LineNumberReader aReader=new LineNumberReader(new StringReader(csvString));
        String myline=null;
        
        try {
            int ndx = csvString.indexOf("\n");
            // read first line:
            if((firstline=aReader.readLine())!=null) {
                lastPos=ndx+1;
                //split line into tokens:
                CsvTokenizer st=new CsvTokenizer(firstline, status.getSeparator(), status.getDelimiter());
                String curr = "";
                CsvColInfo aCol=null;
                // while (st.hasMoreTokens()) {
                while((curr=st.nextToken())!=null) {
                    
                    // curr = (String)st.nextElement();
                    curr=curr.trim();
                    curr=curr.toLowerCase();
                    aCol=new CsvColInfo();
                    aCol.setName(curr);
                    aCol.setActive(false);
                    aCol.setType(CsvColInfo.TYPE_UNKNOWN);
                    
                    // add column to csvAllColumns:
                    csvAllColumns.add(aCol);
                    colNum++;
                    this.csvMaxUsedColumn=colNum;
                }
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("parseFirstline: "+e);
        }
        
        return errors;
    }
    
    /**
     * check in the columnMapping for the key column,
     * and eventually for gender and mailtype
     * read first csv line again; do not parse (allready parsed in parseFirstline)
     * prepare download-files for errors and parsed data
     * read the rest of the csv-file
     */
    protected ActionErrors parseContent(HttpServletRequest req) {
        ApplicationContext aContext=this.getWebApplicationContext();
        JdbcTemplate jdbc=new JdbcTemplate((DataSource)aContext.getBean("dataSource")); 
        ResultSet rset=null;
        LinkedList aLineContent=null;
        String firstline=null;
        CsvColInfo aDbCol=null;
        String csvString=new String("");
        ActionErrors errors=new ActionErrors();
        boolean hasGENDER=false;
        boolean hasMAILTYPE=false;
        boolean hasKeyColumn=false;
        int colNum=0;
        
        this.uniqueValues=new HashSet();
        this.parsedContent=new LinkedList();
        this.linesOK=0;
        //this.csvMaxUsedColumn=0;
        
        this.dbInsertStatus=0;
        
        try {
            csvString=new String(this.getCsvFile().getFileData(), status.getCharset());
        } catch (Exception e) {
            AgnUtils.logger().error("parseContent: "+e);
            errors.add("global", new ActionMessage("error.import.charset"));
            return errors;
        }
        
        try {
            this.loadBlacklist(this.getCompanyID(req), jdbc);
        } catch (Exception e) {
            errors.add("global", new ActionMessage("import.blacklist.read"));
            return errors;
        }
        
        int tmp_length=0;
        int lastPos=0;
        LineNumberReader aReader=new LineNumberReader(new StringReader(csvString));
        
        String myline=null;
        
        // check in the columnMapping for the key column,
        // and eventually for gender and mailtype:
        String aKey= "";
        CsvColInfo aCol = null;
        Enumeration aMapEnu = this.columnMapping.keys();
        while(aMapEnu.hasMoreElements()) {
            aKey=(String)aMapEnu.nextElement();
            aCol=(CsvColInfo)this.columnMapping.get(aKey);
            
            if(aCol.getName().equals("gender")) {
                hasGENDER=true;
            }
            
            if(aCol.getName().equals("mailtype")) {
                hasMAILTYPE=true;
            }
            
            if(aCol.getName().equalsIgnoreCase(this.keyColumn)) {
                hasKeyColumn=true;
            }
        }
        
        if(!hasKeyColumn) {
            errors.add("global", new ActionMessage("error.import.no_keycolumn_mapping"));
        }
        
        if(this.getMode()==ImportWizardForm.MODE_ADD || this.getMode()==ImportWizardForm.MODE_ADD_UPDATE) {
            if(!hasGENDER) {
                errors.add("global", new ActionMessage("error.import.no_gender_mapping"));
            }
            if(!hasMAILTYPE) {
                errors.add("global", new ActionMessage("error.import.no_mailtype_mapping"));
            }
        }
    
        try {
            
            
            // read first csv line again; do not parse (allready parsed in parseFirstline):
            int ndx = csvString.indexOf("\n");
            if((myline=aReader.readLine())!=null) {
                lastPos=ndx+1;
                firstline=myline;
            }
            
            // prepare download-files for errors and parsed data
            errorData.put(DATE_ERROR, new StringBuffer(firstline+'\n'));
            errorData.put(EMAIL_ERROR, new StringBuffer(firstline+'\n'));
            errorData.put(EMAILDOUBLE_ERROR, new StringBuffer(firstline+'\n'));
            errorData.put(GENDER_ERROR, new StringBuffer(firstline+'\n'));
            errorData.put(MAILTYPE_ERROR, new StringBuffer(firstline+'\n'));
            errorData.put(NUMERIC_ERROR, new StringBuffer(firstline+'\n'));
            errorData.put(STRUCTURE_ERROR, new StringBuffer(firstline+'\n'));
            errorData.put(BLACKLIST_ERROR, new StringBuffer(firstline+'\n'));
            parsedData=new StringBuffer(firstline+'\n');
            
            // read the rest of the csv-file:
            //        StringTokenizer file =  new StringTokenizer(csvString, "\n");
            
            if(errors.isEmpty()) {
                String col_tmp=null;
                //        while (file.hasMoreTokens()) {
                //            String myline = file.nextToken();
                while((myline=aReader.readLine())!=null) {
                    if(myline.trim().length() > 0) {
                        aLineContent=parseLine(myline);
                        if(aLineContent!=null) {
                            parsedContent.add(aLineContent);
                            this.parsedData.append(myline+"\n");
                            this.linesOK++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            AgnUtils.logger().error("parseContent: "+e);
        }
        return errors;
    }
    
    /**
     * Gets mailing lists from request.
     */
    protected void getMailinglistsFromRequest(HttpServletRequest req) {
        String aParam=null;
        this.mailingLists=new Vector();
        Enumeration e = req.getParameterNames();
        while(e.hasMoreElements()) {
            aParam =(String)e.nextElement();
            if(aParam.startsWith("agn_mlid_")) {
                this.mailingLists.add(aParam.substring(9));
            }
        }
    }
    
    /** 
     * Getter for property linesOK.
     *
     * @return Value of property linesOK.
     */
    public int getLinesOK() {
        return this.linesOK;
    }
    
    /**
     * Setter for property linesOK.
     *
     * @param linesOK New value of property linesOK.
     */
    public void setLinesOK(int linesOK) {
        this.linesOK = linesOK;
    }
    
    /** 
     * Getter for property dbInsertStatus.
     *
     * @return Value of property dbInsertStatus.
     */
    public int getDbInsertStatus() {
        return this.dbInsertStatus;
    }
    
    /**
     * Setter for property dbInsertStatus.
     *
     * @param dbInsertStatus New value of property dbInsertStatus.
     */
    public void setDbInsertStatus(int dbInsertStatus) {
        this.dbInsertStatus = dbInsertStatus;
    }
    
    /**
     * Getter for property parsedData.
     *
     * @return Value of property parsedData.
     */
    public StringBuffer getParsedData() {
        return this.parsedData;
    }
    
    /** 
     * Setter for property parsedData.
     *
     * @param parsedData New value of property parsedData.
     */
    public void setParsedData(StringBuffer parsedData) {
        this.parsedData = parsedData;
    }
    
    /**
     * Getter for property downloadName.
     *
     * @return Value of property downloadName.
     */
    public String getDownloadName() {
        return this.downloadName;
    }
    
    /**
     * Setter for property downloadName.
     *
     * @param downloadName New value of property downloadName.
     */
    public void setDownloadName(String downloadName) {
        this.downloadName = downloadName;
    }
    
    /**
     * Getter for property dbInsertStatusMessages.
     *
     * @return Value of property dbInsertStatusMessages.
     */
    public LinkedList getDbInsertStatusMessages() {
        return this.dbInsertStatusMessages;
    }
    
    /**
     * Setter for property dbInsertStatusMessages.
     *
     * @param dbInsertStatusMessages New value of property dbInsertStatusMessages.
     */
    public void setDbInsertStatusMessages(LinkedList dbInsertStatusMessages) {
        this.dbInsertStatusMessages = dbInsertStatusMessages;
    }
    
    public void addDbInsertStatusMessage(String message) {
        if(this.dbInsertStatusMessages==null) {
            this.dbInsertStatusMessages=new LinkedList();
        }
        
        this.dbInsertStatusMessages.add(message);
    }
    
    /**
     * Getter for property resultMailingListAdded.
     *
     * @return Value of property resultMailingListAdded.
     */
    public Hashtable getResultMailingListAdded() {
        return this.resultMailingListAdded;
    }
    
    /**
     * Setter for property resultMailingListAdded.
     *
     * @param resultMailingListAdded New value of property resultMailingListAdded.
     */
    public void setResultMailingListAdded(Hashtable resultMailingListAdded) {
        this.resultMailingListAdded = resultMailingListAdded;
    }
    
    /**
     * Getter for property blacklist.
     *
     * @return Value of property blacklist.
     */
    public HashSet getBlacklist() {
        return this.blacklist;
    }
    
    /**
     * Setter for property blacklist.
     *
     * @param blacklist New value of property blacklist.
     */
    public void setBlacklist(HashSet blacklist) {
        this.blacklist = blacklist;
    }
    
    /**
     * Getter for property previewOffset.
     *
     * @return Value of property previewOffset.
     */
    public int getPreviewOffset() {
        return this.previewOffset;
    }
    
    /**
     * Setter for property previewOffset.
     *
     * @param previewOffset New value of property previewOffset.
     */
    public void setPreviewOffset(int previewOffset) {
        this.previewOffset = previewOffset;
    }
    
    /**
     * Getter for property dateFormat.
     *
     * @return Value of property dateFormat.
     */
    public String getDateFormat() {
        
        return this.dateFormat;
    }
    
    /**
     * Setter for property dateFormat.
     *
     * @param dateFormat New value of property dateFormat.
     */
    public void setDateFormat(String dateFormat) {
        
        this.dateFormat = dateFormat;
    }
    
    /**
     * Holds value of property columnMapping.
     */
    private Hashtable columnMapping;
    
    /**
     * Getter for property columnMapping.
     *
     * @return Value of property columnMapping.
     */
    public Hashtable getColumnMapping() {
        
        return this.columnMapping;
    }
    
    /**
     * Setter for property columnMapping.
     *
     * @param columnMapping New value of property columnMapping.
     */
    public void setColumnMapping(Hashtable columnMapping) {
        
        this.columnMapping = columnMapping;
    }  
}
