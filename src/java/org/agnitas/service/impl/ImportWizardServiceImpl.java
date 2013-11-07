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

package org.agnitas.service.impl;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.mail.internet.InternetAddress;

import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.Recipient;
import org.agnitas.dao.RecipientDao;
import org.agnitas.service.ImportWizardService;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.Blacklist;
import org.agnitas.util.CsvColInfo;
import org.agnitas.util.CsvTokenizer;
import org.agnitas.util.SafeString;

/**
 * Holds the parsed data for ImportWizard
 * @author ms
 */
public class ImportWizardServiceImpl implements ImportWizardService  {
	private CustomerImportStatus status = null;

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
	private Map dbAllColumns;

	/**
	 * Holds value of property mode.
	 */
	private int mode;

	/**
	 * Holds value of property dateFormat.
	 */
	private String dateFormat = "dd.MM.yyyy HH:mm";

	/**
	 * Holds value of property linesOK.
	 */
	private int linesOK;
	
	/**
	 * number of read lines
	 */
	private int readlines;

	/**
	 * Holds value of property dbInsertStatus.
	 */
	private int dbInsertStatus;

	/**
	 * Holds value of property errorData.
	 */
	private HashMap errorData = new HashMap();

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

//	/**
//	 * Holds value of property blacklist.
//	 */
//	protected HashSet blacklist;
	
	private Blacklist blacklistHelper;
	

	protected int csvMaxUsedColumn = 0;

	/**
	 * Holds value of property previewOffset.
	 */
	private int previewOffset;
	
	/**
	 * user may choose a default mailing-type in case of no column for mailing-type has been assigned
	 */

	private String manualAssignedMailingType =  Integer.toString(Recipient.MAILTYPE_HTML); 
	private String manualAssignedGender = Integer.toString(Recipient.GENDER_UNKNOWN); 
	
	private boolean mailingTypeMissing = false;
	private boolean genderMissing = false;
	
	private RecipientDao recipientDao;

	private int companyID;

	private Locale locale;

	private byte[] fileData;

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#doParse()
	 */
	public void doParse() throws ImportWizardContentParseException {
		// start at the top of the csv file:
		previewOffset = 0;
		// change this to process the column name mapping from --
		// previous action:
		parseContent();
		//dbAllColumns = recipientDao.readDBColumns( companyID);
	}
	
	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getDatasourceID()
	 */
	public int getDatasourceID() {
		return status.getDatasourceID();
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setError(java.lang.String, java.lang.String)
	 */
	public void setError(String id, String desc) {
		status.addError(id);
		if (!errorData.containsKey(id)) {
			errorData.put(id, new StringBuffer());
		}
		((StringBuffer) errorData.get(id)).append(desc + "\n");
		status.addError("all");
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getError(java.lang.String)
	 */
	public StringBuffer getError(String id) {
		return (StringBuffer) errorData.get(id);
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getErrorMap()
	 */
	public HashMap getErrorMap() {
		return errorData;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getStatus()
	 */
	public CustomerImportStatus getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setStatus(org.agnitas.beans.CustomerImportStatus)
	 */
	public void setStatus(CustomerImportStatus status) {
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getCsvAllColumns()
	 */
	public ArrayList getCsvAllColumns() {
		return this.csvAllColumns;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setCsvAllColumns(java.util.ArrayList)
	 */
	public void setCsvAllColumns(ArrayList csvAllColumns) {
		this.csvAllColumns = csvAllColumns;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getMailingLists()
	 */
	public Vector getMailingLists() {
		return this.mailingLists;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setMailingLists(java.util.Vector)
	 */
	public void setMailingLists(Vector mailingLists) {
		this.mailingLists = mailingLists;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getUsedColumns()
	 */
	public ArrayList getUsedColumns() {
		return this.usedColumns;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setUsedColumns(java.util.ArrayList)
	 */
	public void setUsedColumns(ArrayList usedColumns) {
		this.usedColumns = usedColumns;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getParsedContent()
	 */
	public LinkedList getParsedContent() {
		return this.parsedContent;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setParsedContent(java.util.LinkedList)
	 */
	public void setParsedContent(LinkedList parsedContent) {
		this.parsedContent = parsedContent;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getUniqueValues()
	 */
	public HashSet getUniqueValues() {
		return this.uniqueValues;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setUniqueValues(java.util.HashSet)
	 */
	public void setUniqueValues(HashSet uniqueValues) {
		this.uniqueValues = uniqueValues;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getDbAllColumns()
	 */
	public Map getDbAllColumns() {
		return this.dbAllColumns;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setDbAllColumns(java.util.Hashtable)
	 */
	public void setDbAllColumns(Hashtable dbAllColumns) {
		this.dbAllColumns = dbAllColumns;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getMode()
	 */
	public int getMode() {
		return this.mode;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setMode(int)
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#parseLine(java.lang.String)
	 */
	public LinkedList parseLine(String input) {
		// EnhStringTokenizer aLine = null;
		CsvTokenizer aLine = null;
		int j = 0;
		String aValue = null;
		CsvColInfo aInfo = null;
		CsvColInfo aCsvInfo = null;
		LinkedList valueList = new LinkedList();
		int tmp = 0;

		if (this.dateFormat == null || this.dateFormat.trim().length() == 0) {
			this.dateFormat = new String("dd.MM.yyyy HH:mm");
		}

		SimpleDateFormat aDateFormat = new SimpleDateFormat(this.dateFormat);
		aLine = new CsvTokenizer(input, status.getSeparator(), status.getDelimiter());
		
		try {
			boolean addedGenderDummyValue = false;
			boolean addedMailtypeDummyValue = false;
			while ((aValue = aLine.nextToken()) != null) {
				aCsvInfo = (CsvColInfo) this.csvAllColumns.get(j);

				// only when mapping for this column is found:
				if (this.getColumnMapping().containsKey(aCsvInfo.getName())) {
					// get real CsvColInfo object:
					aInfo = (CsvColInfo) this.getColumnMapping().get(aCsvInfo.getName());
					aValue = aValue.trim();
					// do this before eventual duplicate check on Col Email
					if (aInfo.getName().equalsIgnoreCase("email")) {
						aValue = aValue.toLowerCase();
					}
					if (status.getDoubleCheck() != CustomerImportStatus.DOUBLECHECK_NONE
							&& this.status.getKeycolumn().equalsIgnoreCase(aInfo.getName())) {
						if (this.uniqueValues.add(aValue) == false) {
							setError(EMAILDOUBLE_ERROR, input + "\n");
							AgnUtils.logger().error("Duplicate email: " + input);
							return null;
						}
					}
					if (aInfo.getName().equalsIgnoreCase("email")) {
						if (aValue.length() == 0) {
							setError(EMAIL_ERROR, input + "\n");
							AgnUtils.logger().error("Empty email: " + input);
							return null;
						}
						if (aValue.indexOf('@') == -1) {
							setError(EMAIL_ERROR, input + "\n");
							AgnUtils.logger().error("No @ in email: " + input);
							return null;
						}

						try {
							new InternetAddress(aValue);
						} catch (Exception e) {
							setError(EMAIL_ERROR, input + "\n");
							AgnUtils.logger().error("InternetAddress error: " + input);
							return null;
						}
						// check blacklist
						//if (AgnUtils.matchCollection(aValue, this.blacklist)) {
						if( blacklistHelper.isBlackListed(aValue) != null ) {
							setError(BLACKLIST_ERROR, input + "\n");
							AgnUtils.logger().error("Blacklisted: " + input);
							return null;
						}
					} else if (aInfo.getName().equalsIgnoreCase(MAILTYPE_KEY)) {
						try {
							tmp = Integer.parseInt(aValue);
							if (tmp < 0 || tmp > 2) {
								throw new Exception("Invalid mailtype");
							}
						} catch (Exception e) {
							if (aInfo.getName().equalsIgnoreCase(MAILTYPE_KEY)) {
								if (!aValue.equalsIgnoreCase("text")
										&& !aValue.equalsIgnoreCase("txt")
										&& !aValue.equalsIgnoreCase("html")
										&& !aValue.equalsIgnoreCase("offline")) {
									setError(MAILTYPE_ERROR, input + "\n");
									AgnUtils.logger().error("Invalid mailtype: " + input);
									return null;
								}
							}
						}
					} else if (aInfo.getName().equalsIgnoreCase(GENDER_KEY)) {
						try {
							tmp = Integer.parseInt(aValue);
							if (tmp < 0 || tmp > 5) {
								throw new Exception("Invalid gender");
							}
						} catch (Exception e) {
							if (aInfo.getName().equalsIgnoreCase(GENDER_KEY)) {
								if (!aValue.equalsIgnoreCase("Herr")
										&& !aValue.equalsIgnoreCase("Herrn")
										&& !aValue.equalsIgnoreCase("m")
										&& !aValue.equalsIgnoreCase("Frau")
										&& !aValue.equalsIgnoreCase("w")) {
									setError(GENDER_ERROR,input
													+ ";"
													+ SafeString.getLocaleString("import.error.GenderFormat",locale)
													+ aInfo.getName() + "\n");
									AgnUtils.logger().error("Invalid gender: " + aValue);
									return null;
								}
							}
						}
					}
					if (aInfo != null && aInfo.isActive()) {
						if (aValue.length() == 0) { // is null value
							valueList.add(null);
						} else {
							switch (aInfo.getType()) {
							case CsvColInfo.TYPE_CHAR:
								valueList.add(SafeString.cutByteLength(aValue,aInfo.getLength()));
								break;

							case CsvColInfo.TYPE_NUMERIC:
								try {
									valueList.add(Double.valueOf(aValue));
								} catch (Exception e) {
									if (aInfo.getName().equalsIgnoreCase(
											GENDER_KEY) && !columnMapping.containsKey(GENDER_KEY+"_dummy")) {
										if (aValue.equalsIgnoreCase("Herr")
												|| aValue.equalsIgnoreCase("Herrn")
												|| aValue.equalsIgnoreCase("m")) {
											valueList.add(Double.valueOf(0));
										} else if (aValue.equalsIgnoreCase("Frau")
												|| aValue.equalsIgnoreCase("w")) {
											valueList.add(Double.valueOf(1));
										} else {
											valueList.add(Double.valueOf(2));
										}
									} else if (aInfo.getName().equalsIgnoreCase(
											MAILTYPE_KEY) && !columnMapping.containsKey(MAILTYPE_KEY+"_dummy")) {
										if (aValue.equalsIgnoreCase("text")
												|| aValue.equalsIgnoreCase("txt")) {
											valueList.add(Double.valueOf(0));
										} else if (aValue.equalsIgnoreCase("html")) {
											valueList.add(Double.valueOf(1));
										} else if (aValue.equalsIgnoreCase("offline")) {
											valueList.add(Double.valueOf(2));
										} 
									} else {
										setError(NUMERIC_ERROR,	input+ ";"+ SafeString.getLocaleString("import.error.NumberFormat",locale)
														+ aInfo.getName()
														+ "\n");
										AgnUtils.logger().error("Numberformat error: " + input);
										return null;
									}
								}
								break;

							case CsvColInfo.TYPE_DATE:
								try {
									valueList.add(aDateFormat.parse(aValue));
								} catch (Exception e) {
									setError(DATE_ERROR, input
											+ ";"
											+ SafeString.getLocaleString(
													"import.error.DateFormat",
													locale) + aInfo.getName()
											+ "\n");
									AgnUtils.logger().error("Dateformat error: " + input);
									return null;
								}
							}
						}
					}
				}
				j++;
			}
			if (this.getColumnMapping().containsKey(GENDER_KEY+"_dummy" ) && !addedGenderDummyValue ) {
				valueList.add(getManualAssignedGender());
				addedGenderDummyValue = true;
			}
			if (this.getColumnMapping().containsKey(MAILTYPE_KEY+"_dummy" ) && !addedMailtypeDummyValue ) {
				valueList.add(getManualAssignedMailingType());
				addedMailtypeDummyValue = true;
			}
			
			
		} catch (Exception e) {
			setError(STRUCTURE_ERROR, input + "\n");
			AgnUtils.logger().error("parseLine: " + e);
			return null;
		}

		if (this.csvMaxUsedColumn != j) {
			setError(STRUCTURE_ERROR, input + "\n");
			AgnUtils.logger().error(
					"MaxusedColumn: " + this.csvMaxUsedColumn + ", " + j);
			return null;
		}
		return valueList;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#mapColumns(java.util.Map)
	 */
	public void mapColumns(Map<String,String> mapParameters) {
		int i = 1;
		CsvColInfo aCol = null;
		// initialize columnMapping hashtable:
		columnMapping = new Hashtable();

		for (i = 1; i < (csvAllColumns.size() + 1); i++) {
			String pName = new String("map_" + i);
			if (mapParameters.get(pName) != null) {
				aCol = (CsvColInfo) csvAllColumns.get(i - 1);
				if(mapParameters.get(pName) == null  ) {
					continue;
				}
				
				if (!"NOOP".equals(mapParameters.get(pName))) {
					CsvColInfo aInfo = (CsvColInfo) dbAllColumns.get(mapParameters.get(pName));
					columnMapping.put(aCol.getName(), aInfo);

					aInfo.setActive(true);
					// write db column (set active now) back to dbAllColums:
					dbAllColumns.put(mapParameters.get(pName),aInfo);

					// adjust & write back csvAllColumns hashtable entry:
					aCol.setActive(true);
					aCol.setLength(aInfo.getLength());
					aCol.setType(aInfo.getType());
					csvAllColumns.set(i - 1, aCol);
				}
			}
		}
		
		if (!columnIsMapped(GENDER_KEY) && (mode == ImportWizardService.MODE_ADD || mode == ImportWizardService.MODE_ADD_UPDATE)) {
			CsvColInfo genderCol = new CsvColInfo();
			genderCol.setName(GENDER_KEY);
			genderCol.setType(CsvColInfo.TYPE_CHAR);
			columnMapping.put(GENDER_KEY+"_dummy", genderCol );
			setGenderMissing(true);
		}
		
		if (!columnIsMapped(MAILTYPE_KEY) && (mode == ImportWizardService.MODE_ADD || mode == ImportWizardService.MODE_ADD_UPDATE)) {
			CsvColInfo mailtypeCol = new CsvColInfo();
			mailtypeCol.setName(MAILTYPE_KEY);
			mailtypeCol.setType(CsvColInfo.TYPE_CHAR);			
			columnMapping.put(MAILTYPE_KEY+"_dummy", mailtypeCol );
			setMailingTypeMissing(true);
		}
		
		// check if the mailtype/ gender is allready in columnmapping , if we find only a dummy -> add a dummy to csvAllColumns too 
		if(getColumnMapping().containsKey(GENDER_KEY+"_dummy")) {
			CsvColInfo mailtypeDummy = new CsvColInfo();
			mailtypeDummy.setName(GENDER_KEY+"_dummy");
			mailtypeDummy.setActive(true);
			mailtypeDummy.setType(CsvColInfo.TYPE_CHAR);
			csvAllColumns.add(mailtypeDummy);
		}
		
		if(getColumnMapping().containsKey(MAILTYPE_KEY+"_dummy")) {
			CsvColInfo mailtypeDummy = new CsvColInfo();
			mailtypeDummy.setName(MAILTYPE_KEY+"_dummy");
			mailtypeDummy.setActive(true);
			mailtypeDummy.setType(CsvColInfo.TYPE_CHAR);
			csvAllColumns.add(mailtypeDummy);
		}		
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#parseFirstline()
	 */
	public void parseFirstline()  throws ImportWizardContentParseException{
		String csvString = new String("");
		String firstline = null;
		int colNum = 0;

		// try to read csv file:
		try {
			csvString = new String(fileData, status.getCharset());
		} catch (Exception e) {
			AgnUtils.logger().error("parseFirstline: " + e);
			throw new ImportWizardContentParseException("error.import.charset",e);
		}

		if (csvString.length() == 0) {
			throw new ImportWizardContentParseException("error.import.no_file");
		}
			
		dbAllColumns = recipientDao.readDBColumns(companyID);
		if (!(mode != ImportWizardServiceImpl.MODE_ADD && mode != ImportWizardService.MODE_ADD_UPDATE && status.getKeycolumn().equals("customer_id"))) {
			dbAllColumns.remove("customer_id");
		}
				
		csvAllColumns = new ArrayList();
		LineNumberReader aReader = new LineNumberReader(new StringReader(csvString));
	
		try {
			// read first line:
			if ((firstline = aReader.readLine()) != null) {
				aReader.close(); // 
				
				// split line into tokens:
				CsvTokenizer st = new CsvTokenizer(firstline, status
						.getSeparator(), status.getDelimiter());
				String curr = "";
				CsvColInfo aCol = null;
				List<String> tempList = new ArrayList<String>();
				while ((curr = st.nextToken()) != null) {
					// curr = (String)st.nextElement();
					curr = curr.trim();
					curr = curr.toLowerCase();
					aCol = new CsvColInfo();
					aCol.setName(curr);
					aCol.setActive(false);
					aCol.setType(CsvColInfo.TYPE_UNKNOWN);

					// add column to csvAllColumns:
					if (!tempList.contains(aCol.getName())) {
						tempList.add(aCol.getName());
					} else {
						throw new ImportWizardContentParseException("error.import.column");
					}
					csvAllColumns.add(aCol);
					colNum++;
					this.csvMaxUsedColumn = colNum;
				}
			}
		} catch (Exception e) {
			AgnUtils.logger().error("parseFirstline: " + e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#parseContent()
	 */
	public void parseContent() throws ImportWizardContentParseException {
		LinkedList aLineContent = null;
		String firstline = null;
		String csvString = new String("");
		boolean hasGENDER = false;
		boolean hasMAILTYPE = false;
		boolean hasKeyColumn = false;

		this.uniqueValues = new HashSet();
		this.parsedContent = new LinkedList();
		this.linesOK = 0;

		this.dbInsertStatus = 0;
		
		try {
			csvString = new String(fileData, status.getCharset());
		} catch (UnsupportedEncodingException e) {
			throw new ImportWizardContentParseException("error.import.charset",e);
		}
		
		try {
		 HashSet<String> blacklistEntries = (HashSet<String>) recipientDao.loadBlackList(companyID);
		 blacklistHelper = new Blacklist();
		 for(String blackListEntry:blacklistEntries) {
			blacklistHelper.add(blackListEntry, false); 
		 }	 
		 
		} catch (Exception e) {
			throw new ImportWizardContentParseException("error.import.blacklist",e); 
		}		
		
		LineNumberReader aReader = new LineNumberReader(new StringReader( csvString));
		String myline = null;

		// check in the columnMapping for the key column,
		// and eventually for gender and mailtype:
		String aKey = "";
		CsvColInfo aCol = null;
		Enumeration aMapEnu = this.columnMapping.keys();
		
		while (aMapEnu.hasMoreElements()) {
			aKey = (String) aMapEnu.nextElement();
			aCol = (CsvColInfo) this.columnMapping.get(aKey);
									
			if (aCol.getName().equalsIgnoreCase(GENDER_KEY) ) {
				hasGENDER = true;
			}

			if (aCol.getName().equalsIgnoreCase(MAILTYPE_KEY)) {
				hasMAILTYPE = true;
			}

			if (aCol.getName().equalsIgnoreCase(this.status.getKeycolumn())) {
				hasKeyColumn = true;
			}
		}

		if (!hasKeyColumn) {
			throw new ImportWizardContentParseException("error.import.no_keycolumn_mapping");
		}

		if (this.getMode() == MODE_ADD || this.getMode() == MODE_ADD_UPDATE) {
			if (!hasGENDER) {
				throw new ImportWizardContentParseException("error.import.no_gender_mapping");
			}
			if (!hasMAILTYPE) {
				throw new ImportWizardContentParseException("error.import.no_mailtype_mapping");
			}
		}

		try {
			// read first csv line again; do not parse (allready parsed in
			// parseFirstline):
			if ((myline = aReader.readLine()) != null) {
				firstline = myline;
			}

			// prepare download-files for errors and parsed data
			errorData.put(DATE_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(EMAIL_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(EMAILDOUBLE_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(GENDER_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(MAILTYPE_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(NUMERIC_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(STRUCTURE_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(BLACKLIST_ERROR, new StringBuffer(firstline + '\n'));
			parsedData = new StringBuffer(firstline + '\n');

			// read the rest of the csv-file:
			// StringTokenizer file = new StringTokenizer(csvString, "\n");
			readlines = 0;
			int maxrows = BLOCK_SIZE;
			this.linesOK = 0;
			while ((myline = aReader.readLine()) != null
					&& this.linesOK < maxrows) { // Bug-Fix just read the
													// first 1000 lines to avoid
													// trouble with heap space
				if (myline.trim().length() > 0) {
					aLineContent = parseLine(myline);
					if (aLineContent != null) {
						parsedContent.add(aLineContent);
						this.parsedData.append(myline + "\n");
						this.linesOK++;
					}
				}
				readlines++;
			}
			aReader.close();
		} catch (Exception e) {
			AgnUtils.logger().error("parseContent: " + e);
		}
	}

	private boolean columnIsMapped(String key) {
		Enumeration<CsvColInfo> elements = columnMapping.elements();
		while(elements.hasMoreElements()) {
			CsvColInfo colInfo = elements.nextElement();
			if(key.equalsIgnoreCase(colInfo.getName())) {
				return true;
			}			
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getLinesOK()
	 */
	public int getLinesOK() {
		return this.linesOK;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setLinesOK(int)
	 */
	public void setLinesOK(int linesOK) {
		this.linesOK = linesOK;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getDbInsertStatus()
	 */
	public int getDbInsertStatus() {
		return this.dbInsertStatus;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setDbInsertStatus(int)
	 */
	public void setDbInsertStatus(int dbInsertStatus) {
		this.dbInsertStatus = dbInsertStatus;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getParsedData()
	 */
	public StringBuffer getParsedData() {
		return this.parsedData;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setParsedData(java.lang.StringBuffer)
	 */
	public void setParsedData(StringBuffer parsedData) {
		this.parsedData = parsedData;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getDownloadName()
	 */
	public String getDownloadName() {
		return this.downloadName;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setDownloadName(java.lang.String)
	 */
	public void setDownloadName(String downloadName) {
		this.downloadName = downloadName;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getDbInsertStatusMessages()
	 */
	public LinkedList getDbInsertStatusMessages() {
		return this.dbInsertStatusMessages;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setDbInsertStatusMessages(java.util.LinkedList)
	 */
	public void setDbInsertStatusMessages(LinkedList dbInsertStatusMessages) {
		this.dbInsertStatusMessages = dbInsertStatusMessages;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#addDbInsertStatusMessage(java.lang.String)
	 */
	public void addDbInsertStatusMessage(String message) {
		if (this.dbInsertStatusMessages == null) {
			this.dbInsertStatusMessages = new LinkedList();
		}
		this.dbInsertStatusMessages.add(message);
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getResultMailingListAdded()
	 */
	public Hashtable getResultMailingListAdded() {
		return this.resultMailingListAdded;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setResultMailingListAdded(java.util.Hashtable)
	 */
	public void setResultMailingListAdded(Hashtable resultMailingListAdded) {
		this.resultMailingListAdded = resultMailingListAdded;
	}

//	/* (non-Javadoc)
//	 * @see org.agnitas.service.impl.ImportWizardService#getBlacklist()
//	 */
//	public HashSet getBlacklist() {
//		return this.blacklist;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.agnitas.service.impl.ImportWizardService#setBlacklist(java.util.HashSet)
//	 */
//	public void setBlacklist(HashSet blacklist) {
//		this.blacklist = blacklist;
//	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getPreviewOffset()
	 */
	public int getPreviewOffset() {
		return this.previewOffset;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setPreviewOffset(int)
	 */
	public void setPreviewOffset(int previewOffset) {
		this.previewOffset = previewOffset;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getDateFormat()
	 */
	public String getDateFormat() {
		return this.dateFormat;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setDateFormat(java.lang.String)
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * Holds value of property columnMapping.
	 */
	private Hashtable columnMapping;

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getColumnMapping()
	 */
	public Hashtable getColumnMapping() {
		return this.columnMapping;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setColumnMapping(java.util.Hashtable)
	 */
	public void setColumnMapping(Hashtable columnMapping) {
		this.columnMapping = columnMapping;
	}

	private boolean extendedEmailCheck = true;

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#isExtendedEmailCheck()
	 */
	public boolean isExtendedEmailCheck() {
		return extendedEmailCheck;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setExtendedEmailCheck(boolean)
	 */
	public void setExtendedEmailCheck(boolean extendedEmailCheck) {
		this.extendedEmailCheck = extendedEmailCheck;
	}

	private String errorId = null;

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getErrorId()
	 */
	public String getErrorId() {
		return errorId;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setErrorId(java.lang.String)
	 */
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getManualAssignedMailingType()
	 */
	public String getManualAssignedMailingType() {
		return manualAssignedMailingType;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setManualAssignedMailingType(java.lang.String)
	 */
	public void setManualAssignedMailingType(String manualAssignedMailingType) {
		this.manualAssignedMailingType = manualAssignedMailingType;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getManualAssignedGender()
	 */
	public String getManualAssignedGender() {
		return manualAssignedGender;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setManualAssignedGender(java.lang.String)
	 */
	public void setManualAssignedGender(String manualAssignedGender) {
		this.manualAssignedGender = manualAssignedGender;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#isMailingTypeMissing()
	 */
	public boolean isMailingTypeMissing() {
		return mailingTypeMissing;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setMailingTypeMissing(boolean)
	 */
	public void setMailingTypeMissing(boolean mailingTypeMissing) {
		this.mailingTypeMissing = mailingTypeMissing;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#isGenderMissing()
	 */
	public boolean isGenderMissing() {
		return genderMissing;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setGenderMissing(boolean)
	 */
	public void setGenderMissing(boolean genderMissing) {
		this.genderMissing = genderMissing;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getReadlines()
	 */
	public int getReadlines() {
		return readlines;
	}	
	
	 /* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getLinesOKFromFile()
	 */
    public int getLinesOKFromFile() throws IOException {
    	String csvString =  new String(fileData , this.getStatus().getCharset());
      	LineNumberReader aReader = new LineNumberReader(new StringReader(csvString));
        String myline = "";
		int linesOK = 0;
		this.getUniqueValues().clear();	
		aReader.readLine(); // skip header
		while ((myline = aReader.readLine()) != null ) { 
			if (myline.trim().length() > 0) {
				if(  this.parseLine(myline) != null) {
					linesOK++;
				}						
			}
		}
		aReader.close();
		return linesOK;
     }

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setRecipientDao(org.agnitas.dao.RecipientDao)
	 */
	public void setRecipientDao(RecipientDao recipientDao) {
		this.recipientDao = recipientDao;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getLocale()
	 */
	public Locale getLocale() {
		return locale;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getErrorData()
	 */
	public HashMap getErrorData() {
		return errorData;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setErrorData(java.util.HashMap)
	 */
	public void setErrorData(HashMap errorData) {
		this.errorData = errorData;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getFileData()
	 */
	public byte[] getFileData() {
		return fileData;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setFileData(byte[])
	 */
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#getCompanyID()
	 */
	public int getCompanyID() {
		return companyID;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.service.impl.ImportWizardService#setCompanyID(int)
	 */
	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}
}