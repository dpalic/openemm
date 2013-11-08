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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.beans.impl;

import org.agnitas.beans.ColumnMapping;
import org.agnitas.beans.ImportProfile;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Vyacheslav Stepanov
 */
public class ImportProfileImpl implements ImportProfile {

    protected int id;

    protected int adminId;

    protected int companyId;

    protected String name;

    protected int separator;

    protected int textRecognitionChar;

    protected int charset;

    protected int dateFormat;

    protected int importMode;

    protected int checkForDuplicates;

    protected int nullValuesAction;

    protected String keyColumn;

    protected boolean extendedEmailCheck;

    protected Map<String, Integer> genderMapping;

    protected Map<String, Integer> genderMappingList;

    protected List<ColumnMapping> columnMapping;

    protected String mailForReport;

    protected int defaultMailType;

    private boolean updateAllDuplicates;

	private int importId;

    public ImportProfileImpl() {
        genderMapping = Collections.synchronizedMap(new HashMap<String, Integer>());
		genderMappingList = Collections.synchronizedMap(new HashMap<String, Integer>());
        columnMapping = Collections.synchronizedList(new ArrayList<ColumnMapping>());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeparator() {
        return separator;
    }

    public void setSeparator(int separator) {
        this.separator = separator;
    }

    public int getTextRecognitionChar() {
        return textRecognitionChar;
    }

    public void setTextRecognitionChar(int textRecognitionChar) {
        this.textRecognitionChar = textRecognitionChar;
    }

    public int getCharset() {
        return charset;
    }

    public void setCharset(int charset) {
        this.charset = charset;
    }

    public int getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(int dateFormat) {
        this.dateFormat = dateFormat;
    }

    public int getImportMode() {
        return importMode;
    }

    public void setImportMode(int importMode) {
        this.importMode = importMode;
    }

    public int getCheckForDuplicates() {
        return checkForDuplicates;
    }

    public void setCheckForDuplicates(int checkForDuplicates) {
        this.checkForDuplicates = checkForDuplicates;
    }

    public int getNullValuesAction() {
        return nullValuesAction;
    }

    public void setNullValuesAction(int nullValuesAction) {
        this.nullValuesAction = nullValuesAction;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public boolean getExtendedEmailCheck() {
        return extendedEmailCheck;
    }

    public void setExtendedEmailCheck(boolean extendedEmailCheck) {
        this.extendedEmailCheck = extendedEmailCheck;
    }

    public Map<String, Integer> getGenderMapping() {
        return genderMapping;
    }

    public void setGenderMapping(Map<String, Integer> genderMapping) {
        this.genderMapping = genderMapping;
    }

    public Map<String, Integer> getGenderMappingList() {
        return genderMappingList;
    }

    public void setGenderMappingList(Map<String, Integer> genderMappingList) {
        this.genderMappingList = genderMappingList;
    }
    public List<ColumnMapping> getColumnMapping() {
        return columnMapping;
    }

    public void setColumnMapping(List<ColumnMapping> columnMapping) {
        this.columnMapping = columnMapping;
    }

    public String getMailForReport() {
        return mailForReport;
    }

    public void setMailForReport(String mailForReport) {
        this.mailForReport = mailForReport;
    }

    public int getDefaultMailType() {
        return defaultMailType;
    }

    public void setDefaultMailType(int defaultMailType) {
        this.defaultMailType = defaultMailType;
    }

    @Override
    public boolean equals(Object profileObject) {
        if (!(profileObject instanceof ImportProfile)) {
            return false;
        }
        ImportProfile profile = (ImportProfile) profileObject;

        if (!StringUtils.equals(name, profile.getName())) {
            return false;
        }
        if (!StringUtils.equals(mailForReport, profile.getMailForReport())) {
            return false;
        }
        if (!StringUtils.equals(keyColumn, profile.getKeyColumn())) {
            return false;
        }
        return id == profile.getId()
                && adminId == profile.getAdminId()
                && companyId == profile.getCompanyId()
                && separator == profile.getSeparator()
                && textRecognitionChar == profile.getTextRecognitionChar()
                && charset == profile.getCharset()
                && dateFormat == profile.getDateFormat()
                && importMode == profile.getImportMode()
                && checkForDuplicates == profile.getCheckForDuplicates()
                && nullValuesAction == profile.getNullValuesAction()
                && extendedEmailCheck == profile.getExtendedEmailCheck()
                && defaultMailType == profile.getDefaultMailType()
                && updateAllDuplicates == profile.getUpdateAllDuplicates();
    }

    public Integer getGenderValueByFieldValue(String fieldValue) {
        if (isParsableToInt(fieldValue)) {
            return Integer.valueOf(fieldValue);
        } else {
            return genderMapping.get(fieldValue);
        }
    }

	public void storeGenderMappingSequence(String stringGenderSequence, int intGender) {
		StringTokenizer stringTokenizerNewGender = new StringTokenizer(stringGenderSequence, ",");
        while(stringTokenizerNewGender.hasMoreTokens()){
           genderMapping.put(stringTokenizerNewGender.nextToken().trim(), intGender);
        }
	}

    private boolean isParsableToInt(String i) {
        try {
            Integer.parseInt(i);
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean getUpdateAllDuplicates() {
        return updateAllDuplicates;
    }

    public void setUpdateAllDuplicates(boolean updateAllDuplicates) {
        this.updateAllDuplicates = updateAllDuplicates;
    }

	public int getImportId() {
		return importId;
	}

	public void setImportId(int importId) {
		this.importId = importId;
	}
}
