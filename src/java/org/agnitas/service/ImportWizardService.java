package org.agnitas.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.dao.RecipientDao;
import org.agnitas.service.impl.ImportWizardContentParseException;

public interface ImportWizardService {

	public static final int BLOCK_SIZE = 1000;
	public static final String MAILTYPE_KEY = "mailtype";
	public static final String GENDER_KEY = "gender";
	public static final String PARSE_ERRORS = "parseErrors";
	public static final String DATE_ERROR = "date";
	public static final String EMAIL_ERROR = "email";
	public static final String EMAILDOUBLE_ERROR = "emailDouble";
	public static final String GENDER_ERROR = GENDER_KEY;
	public static final String MAILTYPE_ERROR = MAILTYPE_KEY;
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
	public static final int MODE_DONT_IGNORE_NULL_VALUES = 0;
	public static final int MODE_IGNORE_NULL_VALUES = 1;

	public abstract void doParse() throws ImportWizardContentParseException;

	/**
	 * Getter for property datasourceID.
	 * 
	 * @return Value of property datasourceID.
	 */
	public abstract int getDatasourceID();

	/**
	 * Sets an error.
	 */
	public abstract void setError(String id, String desc);

	/**
	 * Getter for property error.
	 * 
	 * @return Value of property error.
	 */
	public abstract StringBuffer getError(String id);

	/**
	 * Getter for property errorMap.
	 * 
	 * @return Value of property errorMap.
	 */
	public abstract HashMap getErrorMap();

	/**
	 * Getter for property status.
	 * 
	 * @return Value of property status.
	 */
	public abstract CustomerImportStatus getStatus();

	/**
	 * Setter for property charset.
	 * 
	 * @param status
	 *            New value of property status.
	 */
	public abstract void setStatus(CustomerImportStatus status);

	/**
	 * Getter for property csvAllColumns.
	 * 
	 * @return Value of property csvAllColumns.
	 */
	public abstract ArrayList getCsvAllColumns();

	/**
	 * Setter for property csvAllColumns.
	 * 
	 * @param csvAllColumns
	 *            New value of property csvAllColumns.
	 */
	public abstract void setCsvAllColumns(ArrayList csvAllColumns);

	/**
	 * Getter for property mailingLists.
	 * 
	 * @return Value of property mailingLists.
	 * 
	 */
	public abstract Vector getMailingLists();

	/**
	 * Setter for property mailingLists.
	 * 
	 * @param mailingLists
	 *            New value of property mailingLists.
	 */
	public abstract void setMailingLists(Vector mailingLists);

	/**
	 * Getter for property usedColumns.
	 * 
	 * @return Value of property usedColumns.
	 */
	public abstract ArrayList getUsedColumns();

	/**
	 * Setter for property usedColumns.
	 * 
	 * @param usedColumns
	 *            New value of property usedColumns.
	 */
	public abstract void setUsedColumns(ArrayList usedColumns);

	/**
	 * Getter for property parsedContent.
	 * 
	 * @return Value of property parsedContent.
	 */
	public abstract LinkedList getParsedContent();

	/**
	 * Setter for property parsedContent.
	 * 
	 * @param parsedContent
	 *            New value of property parsedContent.
	 */
	public abstract void setParsedContent(LinkedList parsedContent);

	/**
	 * Getter for property emailAdresses.
	 * 
	 * @return Value of property emailAdresses.
	 */
	public abstract HashSet getUniqueValues();

	/**
	 * Setter for property emailAdresses.
	 * 
	 * @param uniqueValues
	 */
	public abstract void setUniqueValues(HashSet uniqueValues);

	/**
	 * Getter for property dbAllColumns.
	 * 
	 * @return Value of property dbAllColumns.
	 */
	public abstract Map getDbAllColumns();

	/**
	 * Setter for property dbAllColumns.
	 * 
	 * @param dbAllColumns
	 *            New value of property dbAllColumns.
	 */
	public abstract void setDbAllColumns(Hashtable dbAllColumns);

	/**
	 * Getter for property mode.
	 * 
	 * @return Value of property mode.
	 */
	public abstract int getMode();

	/**
	 * Setter for property mode.
	 * 
	 * @param mode
	 *            New value of property mode.
	 */
	public abstract void setMode(int mode);

	/**
	 * Creates a simple date format When mapping for a column is found get real
	 * csv column information Checks email / email adress / email adress on
	 * blacklist. ?????
	 */
	public abstract LinkedList parseLine(String input);

	/**
	 * Maps columns from database.
	 * Side effects:
	 * -columnMapping will be initialized
	 * -corresponding columns in dbAllColumns will be activated
	 * -csvAllColumns will be updated too 
	 */
	public abstract void mapColumns(Map<String, String> mapParameters);

	/**
	 * Tries to read csv file Reads database column structure reads first line
	 * splits line into tokens
	 */
	public abstract void parseFirstline()
			throws ImportWizardContentParseException;

	/**
	 * check in the columnMapping for the key column, and eventually for gender
	 * and mailtype read first csv line again; do not parse (allready parsed in
	 * parseFirstline) prepare download-files for errors and parsed data read
	 * the rest of the csv-file
	 */
	public abstract void parseContent()
			throws ImportWizardContentParseException;

	/**
	 * Getter for property linesOK.
	 * 
	 * @return Value of property linesOK.
	 */
	public abstract int getLinesOK();

	/**
	 * Setter for property linesOK.
	 * 
	 * @param linesOK
	 *            New value of property linesOK.
	 */
	public abstract void setLinesOK(int linesOK);

	/**
	 * Getter for property dbInsertStatus.
	 * 
	 * @return Value of property dbInsertStatus.
	 */
	public abstract int getDbInsertStatus();

	/**
	 * Setter for property dbInsertStatus.
	 * 
	 * @param dbInsertStatus
	 *            New value of property dbInsertStatus.
	 */
	public abstract void setDbInsertStatus(int dbInsertStatus);

	/**
	 * Getter for property parsedData.
	 * 
	 * @return Value of property parsedData.
	 */
	public abstract StringBuffer getParsedData();

	/**
	 * Setter for property parsedData.
	 * 
	 * @param parsedData
	 *            New value of property parsedData.
	 */
	public abstract void setParsedData(StringBuffer parsedData);

	/**
	 * Getter for property downloadName.
	 * 
	 * @return Value of property downloadName.
	 */
	public abstract String getDownloadName();

	/**
	 * Setter for property downloadName.
	 * 
	 * @param downloadName
	 *            New value of property downloadName.
	 */
	public abstract void setDownloadName(String downloadName);

	/**
	 * Getter for property dbInsertStatusMessages.
	 * 
	 * @return Value of property dbInsertStatusMessages.
	 */
	public abstract LinkedList getDbInsertStatusMessages();

	/**
	 * Setter for property dbInsertStatusMessages.
	 * 
	 * @param dbInsertStatusMessages
	 *            New value of property dbInsertStatusMessages.
	 */
	public abstract void setDbInsertStatusMessages(
			LinkedList dbInsertStatusMessages);

	public abstract void addDbInsertStatusMessage(String message);

	/**
	 * Getter for property resultMailingListAdded.
	 * 
	 * @return Value of property resultMailingListAdded.
	 */
	public abstract Hashtable getResultMailingListAdded();

	/**
	 * Setter for property resultMailingListAdded.
	 * 
	 * @param resultMailingListAdded
	 *            New value of property resultMailingListAdded.
	 */
	public abstract void setResultMailingListAdded(
			Hashtable resultMailingListAdded);

//	/**
//	 * Getter for property blacklist.
//	 * 
//	 * @return Value of property blacklist.
//	 */
//	public abstract HashSet getBlacklist();
//
//	/**
//	 * Setter for property blacklist.
//	 * 
//	 * @param blacklist
//	 *            New value of property blacklist.
//	 */
//	public abstract void setBlacklist(HashSet blacklist);

	/**
	 * Getter for property previewOffset.
	 * 
	 * @return Value of property previewOffset.
	 */
	public abstract int getPreviewOffset();

	/**
	 * Setter for property previewOffset.
	 * 
	 * @param previewOffset
	 *            New value of property previewOffset.
	 */
	public abstract void setPreviewOffset(int previewOffset);

	/**
	 * Getter for property dateFormat.
	 * 
	 * @return Value of property dateFormat.
	 */
	public abstract String getDateFormat();

	/**
	 * Setter for property dateFormat.
	 * 
	 * @param dateFormat
	 *            New value of property dateFormat.
	 */
	public abstract void setDateFormat(String dateFormat);

	/**
	 * Getter for property columnMapping.
	 * 
	 * @return Value of property columnMapping.
	 */
	public abstract Hashtable getColumnMapping();

	/**
	 * Setter for property columnMapping.
	 * 
	 * @param columnMapping   New value of property columnMapping.
	 */
	public abstract void setColumnMapping(Hashtable columnMapping);

	public abstract boolean isExtendedEmailCheck();

	public abstract void setExtendedEmailCheck(boolean extendedEmailCheck);

	public abstract String getErrorId();

	public abstract void setErrorId(String errorId);

	public abstract String getManualAssignedMailingType();

	public abstract void setManualAssignedMailingType(
			String manualAssignedMailingType);

	public abstract String getManualAssignedGender();

	public abstract void setManualAssignedGender(String manualAssignedGender);

	public abstract boolean isMailingTypeMissing();

	public abstract void setMailingTypeMissing(boolean mailingTypeMissing);

	public abstract boolean isGenderMissing();

	public abstract void setGenderMissing(boolean genderMissing);

	public abstract int getReadlines();

	/**
	 * read all lines of the file
	 * @param aForm
	 * @param req
	 * @return
	 * @throws IOException
	 */
	public abstract int getLinesOKFromFile() throws IOException;

	public abstract void setRecipientDao(RecipientDao recipientDao);

	public abstract Locale getLocale();

	public abstract void setLocale(Locale locale);

	public abstract HashMap getErrorData();

	public abstract void setErrorData(HashMap errorData);

	public abstract byte[] getFileData();

	public abstract void setFileData(byte[] fileData);

	public abstract int getCompanyID();

	public abstract void setCompanyID(int companyID);

}