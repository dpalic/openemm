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

package org.agnitas.beans;

import java.sql.*;
import java.io.*;
import java.util.*;

public interface CustomerImportStatus extends Serializable {
    public static final int DOUBLECHECK_FULL = 0;

    public static final int DOUBLECHECK_CSV = 1;

    public static final int DOUBLECHECK_NONE = 2;

    /**
     * Setter for property id.
     *
     * @param id New value of property id.
     */
    public void setId(int id);

   /**
    * Setter for property companyID.
    *
    * @param company New value of property companyID.
    */
    public void setCompanyID(int company);

    /**
    * Setter for property adminID.
    *
    * @param admin New value of property adminID.
    */
    public void setAdminID(int admin);
    
    /**
    * Setter for property datasourceID.
    *
    * @param datasource New value of property datasourceID.
    */
    public void setDatasourceID(int datasource);

    /**
    * Setter for property mode.
    *
    * @param mode New value of property mode.
    */
    public void setMode(int mode);

    /**
    * Setter for property doubleCheck.
    *
    * @param doubleCheck New value of property doubleCheck.
    */
    public void setDoubleCheck(int doubleCheck);

    /**
    * Setter for property ignoreNull.
    *
    * @param ignoreNull New value of property ignoreNull.
    */
    public void setIgnoreNull(int ignoreNull);

    /**
    * Setter for property separator.
    *
    * @param separator New value of property separator.
    */
    public void setSeparator(String separator);
    
    /**
    * Setter for property delimiter.
    *
    * @param delimiter New value of property delimiter.
    */
    public void setDelimiter(String delimiter);

    /**
    * Setter for property keycolumn.
    *
    * @param keycolumn New value of property keycolumn.
    */
    public void setKeycolumn(String keycolumn);

    /**
    * Setter for property charset.
    *
    * @param charset New value of property charset.
    */
    public void setCharset(String charset);

    /**
    * Setter for property recordsBefore.
    *
    * @param recordsBefore New value of property recordsBefore.
    */
    public void setRecordsBefore(int recordsBefore);

    /**
    * Setter for property inserted.
    *
    * @param inserted New value of property inserted.
    */
    public void setInserted(int inserted);

    /**
    * Setter for property updated.
    *
    * @param updated New value of property updated.
    */
    public void setUpdated(int updated);
    
    /**
     * Setter for property errors.
     *
     * @param errors New value of property errors.
     */
    public void setErrors(Map errors);

    /**
     * Setter for property error.
     *
     * @param id New value of property id.
     * @param value New value of property value.
     */
    public void setError(String id, Object value);

    /**
     * Getter for property id.
     * 
     * @return Value of property id.
     */
    public int getId();

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    public int getCompanyID();
    
    /**
     * Getter for property adminID.
     * 
     * @return Value of property adminID.
     */
    public int getAdminID();

    /**
     * Getter for property datasourceID.
     * 
     * @return Value of property datasourceID.
     */
    public int getDatasourceID();

    /**
     * Getter for property mode.
     * 
     * @return Value of property mode.
     */
    public int getMode();

    /**
     * Getter for property doubleCheck.
     * 
     * @return Value of property doubleCheck.
     */
    public int getDoubleCheck();

    /**
     * Getter for property ignoreNull.
     * 
     * @return Value of property ignoreNull.
     */
    public int getIgnoreNull();

    /**
     * Getter for property separator.
     * 
     * @return Value of property separator.
     */
    public String getSeparator();

    /**
     * Getter for property delimiter.
     * 
     * @return Value of property delimiter.
     */
    public String getDelimiter();
    
    /**
     * Getter for property keycolumn.
     * 
     * @return Value of property keycolumn.
     */
    public String getKeycolumn();

    /**
     * Getter for property charset.
     * 
     * @return Value of property charset.
     */
    public String getCharset();

    /**
     * Getter for property recordsBefore.
     * 
     * @return Value of property recordsBefore.
     */
    public int getRecordsBefore();

    /**
     * Getter for property inserted.
     * 
     * @return Value of property inserted.
     */
    public int getInserted();

    /**
     * Getter for property updated.
     * 
     * @return Value of property updated.
     */
    public int getUpdated();

    /**
     * Getter for property errors.
     * 
     * @return Value of property errors.
     */
    public Map getErrors();

    /**
     * Getter for property error.
     * 
     * @param id ID of Error.
     * @return Value of property error.
     */
    public Object getError(String id);

    /**
     * Adds an error with id.
     * 
     * @param id ID of the error.
     */
    public void addError(String id);

}
