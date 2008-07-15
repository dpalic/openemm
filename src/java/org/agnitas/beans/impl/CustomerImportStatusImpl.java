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

package org.agnitas.beans.impl;

import java.util.HashMap;
import java.util.Map;

import org.agnitas.beans.CustomerImportStatus;

public class CustomerImportStatusImpl implements CustomerImportStatus  {
    private static final long serialVersionUID = -2919113239423611048L;

	protected int id;

    protected int company;

    protected int admin;

    protected int datasource;

    protected int mode;

    protected int doubleCheck;

    protected int ignoreNull;

    protected String separator=";";

    protected String delimiter="";

    protected String keycolumn="email";

    protected String charset="";

    protected int recordsBefore;

    protected int inserted;

    protected int updated;

    protected Map errors=null;

    CustomerImportStatusImpl() {
        errors=new HashMap();
    }

    // * * * * *
    //  SETTER:
    // * * * * *
    public void setId(int id) {
        this.id=id;
    }

    public void setCompanyID(int company) {
        this.company=company;
    }

    public void setAdminID(int admin) {
        this.admin=admin;
    }

    public void setDatasourceID(int datasource) {
        this.datasource=datasource;
    }

    public void setMode(int mode) {
        this.mode=mode;
    }

    public void setDoubleCheck(int doubleCheck) {
        this.doubleCheck=doubleCheck;
    }

    public void setIgnoreNull(int ignoreNull) {
        this.ignoreNull=ignoreNull;
    }

    public void setSeparator(String separator) {
        if(separator.equals("t")) {
        	this.separator = "\t";
        } else {
        	this.separator=separator;
        }
    }

    public void setDelimiter(String delimiter) {
        if(delimiter != null) {
            this.delimiter=delimiter;
        } else {
            this.delimiter="";
        }
    }

    public void setKeycolumn(String keycolumn) {
        this.keycolumn=keycolumn;
    }

    public void setCharset(String charset) {
        this.charset=charset;
    }

    public void setRecordsBefore(int recordsBefore) {
        this.recordsBefore=recordsBefore;
    }

    public void setInserted(int inserted) {
        this.inserted=inserted;
    }

    public void setUpdated(int updated) {
        this.updated=updated;
    }

    public void setErrors(Map errors) {
        this.errors=errors;
    }

    public void setError(String id, Object value) {
        this.errors.put(id, value);
    }


    // * * * * *
    //  GETTER:
    // * * * * *
    public int getId() {
        return id;
    }

    public int getCompanyID() {
        return company;
    }

    public int getAdminID() {
        return admin;
    }

    public int getDatasourceID() {
        return datasource;
    }

    public int getMode() {
        return mode;
    }

    public int getDoubleCheck() {
        return doubleCheck;
    }

    public int getIgnoreNull() {
        return ignoreNull;
    }

    public String getSeparator() {
        return separator;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getKeycolumn() {
        return keycolumn;
    }

    public String getCharset() {
        if(charset == null || charset.trim().equals("")) {
            charset=new String("ISO-8859-1");
        }
        return charset;
    }

    public int getRecordsBefore() {
        return recordsBefore;
    }

    public int getInserted() {
        return inserted;
    }

    public int getUpdated() {
        return updated;
    }

    public Map getErrors() {
        return errors;
    }

    public Object getError(String id) {
        Object ret=errors.get(id);

        if(ret == null) {
            return (Object) new Integer(0);
        }
        return ret;
    }

    public void addError(String id) {
        Integer old=null;

        old=(Integer) errors.get(id);
        if(old != null) {
            errors.put(id, new Integer(old.intValue()+1));
        } else {
            errors.put(id, new Integer(1));
        }
    }
}
