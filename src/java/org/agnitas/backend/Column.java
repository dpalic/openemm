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
package org.agnitas.backend;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Representation of a single column
 */
public class Column {
    /**
     * Name of this column 
     */
    String		name;
    
    /**
     * Data type of this column 
     */
    int		type;
    
    /**
     * True if DB has NULL value 
     */
    boolean		isnull;
    
    /**
     * True if column is in use 
     */
    boolean		inuse;

    /**
     * Its numeric version 
     */
    long		ival;
    
    /**
     * Its string version 
     */
    String		sval;
    
    /**
     * Its date version 
     */
    Date		dval;
    
    /**
     * Its time version 
     */
    Time		tval;
    
    /**
     * Its timestamp version 
     */
    Timestamp	tsval;

    /**
     * Constructor
     */
    protected Column () {
        name = null;
        type = -1;
        isnull = false;
        inuse = true;
        ival = -1;
        sval = null;
        dval = null;
        tval = null;
        tsval = null;
    }
    
    /**
     * Constructor setting name and type
     * 
     * @param cName name of column
     * @param cType type of column
     */
    protected Column (String cName, int cType) {
        this ();
        name = cName;
        type = cType;
    }

    /**
     * Set value from a result set
     * 
     * @param rset the result set to use
     * @param index the index into the result set
     */
    protected void set (ResultSet rset, int index) {
        switch (type) {
        default:
            return;
        case Types.DECIMAL:	
        case Types.INTEGER:
        case Types.NUMERIC:
        case Types.SMALLINT:
        case Types.TINYINT:
            try {
                ival = rset.getLong (index);
            } catch (SQLException e) {
                ival = -1;
            }
            break;
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.BLOB:
        case Types.CLOB:
            try {
                if ((type == Types.CHAR) || (type == Types.VARCHAR)) {
                    sval = rset.getString (index);
                } else if (type == Types.BLOB) {
                    Blob	tmp = rset.getBlob (index);

                    sval = tmp == null ? null : StringOps.blob2string (tmp, "UTF-8");
                } else if (type == Types.CLOB) {
                    Clob	tmp = rset.getClob (index);
                        
                    sval = tmp == null ? null : StringOps.clob2string (tmp);
                }
            } catch (SQLException e) {
                sval = null;
            }
            break;
        case Types.DATE:
            try {
                dval = rset.getDate (index);
            } catch (SQLException e) {
                dval = null;
            }
            break;			     
        case Types.TIME:
            try {
                tval = rset.getTime (index);
            } catch (SQLException e) {
                tval = null;
            }
            break;			     
        case Types.TIMESTAMP:
            try {
                tsval = rset.getTimestamp (index);
            } catch (SQLException e) {
                tsval = null;
            }
            break;
        }
        try {
            isnull = rset.wasNull ();
        } catch (SQLException e) {
            isnull = false;
        }
    }
    
    /**
     * Get a column value as string
     * 
     * @return string version of column content
     */
    public String get () {
        String	str;

        switch (type) {
        case Types.DECIMAL:	
        case Types.INTEGER:
        case Types.NUMERIC:
        case Types.SMALLINT:
        case Types.TINYINT:
            return Long.toString (ival);
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.BLOB:
        case Types.CLOB:
            return sval != null ? sval : "";
        case Types.DATE:
        case Types.TIME:
            if (dval != null) {
                SimpleDateFormat	fmt = new SimpleDateFormat ("yyyy-MM-dd", new Locale ("en", "DE"));

//				fmt.setTimeZone (TimeZone.getTimeZone ("GMT"));
                str = fmt.format (dval);
            } else {
                str = "0000-00-00";
            }
            str += " ";
            if (tval != null) {
                SimpleDateFormat	fmt = new SimpleDateFormat ("HH:mm:ss", new Locale ("en", "DE"));

//				fmt.setTimeZone (TimeZone.getTimeZone ("GMT"));
                str += fmt.format (tval);
            } else {
                str += "00:00:00";
            }
            return str;
        case Types.TIMESTAMP:
            if (tsval != null) {
                SimpleDateFormat	fmt = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", new Locale ("en", "DE"));

//				fmt.setTimeZone (TimeZone.getTimeZone ("GMT"));
                str = fmt.format (tsval);
            } else {
                str = "0000-00-00 00:00:00";
            }
            return str;
        }
        return null;
    }
    
    /**
     * Checks for NULL value
     * 
     * @return true, if value is NULL
     */
    protected boolean isNull () {
        return isnull;
    }
    
    /**
     * Checks wether column is in use
     * 
     * @return true, if column is in use
     */
    protected boolean inUse () {
        return inuse;
    }

    /**
     * Returns the type of the given type as simple
     * string representation, either "i" for intergers,
     * "s" for strings and "d" for date types
     * 
     * @param cType the column type
     * @return the simple type string represenation
     */
    static protected String typeStr (int cType) {
        switch (cType) {
        case Types.DECIMAL:	
        case Types.INTEGER:
        case Types.NUMERIC:
        case Types.SMALLINT:
        case Types.TINYINT:
            return "i";
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.BLOB:
        case Types.CLOB:
            return "s";
        case Types.DATE:
        case Types.TIME:
        case Types.TIMESTAMP:
            return "d";
        }
        return null;
    }
    
    /**
     * Returns the type as string
     * 
     * @return the string representation
     */
    protected String typeStr () {
        return typeStr (type);
    }
}
