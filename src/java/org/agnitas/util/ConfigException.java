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
package	org.agnitas.util;

/**
 * this is the general exception thrown by Config and its subclasses,
 * nothing fancy here
 */
public class ConfigException extends Exception {
    /** possible value for type */
    public static final int		CFG_FATAL = 0,
                    CFG_ERROR = 1,
                    CFG_WARNIG = 2,
                    CFG_INFO = 3,
                    CFG_NOTICE = 4,
                    CFG_DEBUG = 5,
                    CFG_USER = 6;
    /** string representations for types */
    public static final String[]	TYPES = {
        "FATAL",
        "ERROR",
        "WARNING",
        "INFO",
        "NOTICE",
        "DEBUG"
    };
    /** 
     * type fot this exception 
     */
    public int	type = CFG_ERROR;
    /**
     * description for this exception 
     */
    public String	desc = null;

    /**
     * Constructor without any further information 
     */
    public ConfigException () {
        super ();
    }

    /** 
     * Constructor with exception message
     *
     * @param msg the message
     */
    public ConfigException (String msg) {
        super (msg);
    }
    
    /** 
     * Construstor setting type and message
     *
     * @param type the type of the exception
     * @param msg the message
     */
    public ConfigException (int type, String msg) {
        super (msg);
        this.type = type;
    }
    
    /**
     * Construstor setting type, description and message
     *
     * @param type the type of the exception
     * @param desc the desscription of the exception
     * @param msg the message
     */
    public ConfigException (int type, String desc, String msg) {
        super (msg);
        this.type = type;
        this.desc = desc;
    }

    /** 
     * Readable representation of instance
     *
     * @return textual representation
     */
    public String toString () {
        String	msg;
        
        if ((type >= 0) && (type < TYPES.length)) {
            msg = "[" + TYPES[type] + "] ";
        } else {
            msg = "[USER " + (type - CFG_USER) + "] ";
        }
        msg += super.toString ();
        if (desc != null) {
            msg += ": " + desc;
        }
        return msg;
    }
}