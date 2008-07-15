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

package org.agnitas.util;

import java.io.Serializable;

/**
 *
 * @author  mhe
 */
public class CsvColInfo implements Serializable {

    private static final long serialVersionUID = -1346306660375779837L;

	/** 
     * Holds value of property name. 
     */
    private String name;
    
    /**
     * Holds value of property type. 
     */
    private int type;
    
    /**
     * Holds value of property lenght. 
     */
    private int length;
    
    /**
     * Holds value of property active. 
     */
    private boolean active;
    
    public static final int TYPE_CHAR = 1;
    
    public static final int TYPE_NUMERIC = 2;
    
    public static final int TYPE_DATE = 3;
    
    public static final int TYPE_UNKNOWN = 0;
    
    /**
     * Creates a new instance of CsvColInfo 
     */
    public CsvColInfo() {
    }

    /**
     * Getter for property name.
     *
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Setter for property name.
     *
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Getter for property type.
     *
     * @return Value of property type.
     */
    public int getType() {
        return this.type;
    }
    
    /**
     * Setter for property type.
     *
     * @param type New value of property type.
     */
    public void setType(int type) {
        this.type = type;
    }
    
    /**
     * Getter for property lenght.
     *
     * @return Value of property lenght.
     */
    public int getLength() {
        return this.length;
    }
    
    /**
     * Setter for property lenght.
     *
     * @param len 
     */
    public void setLength(int len) {
        this.length = len;
    }
    
    /**
     * Getter for property active.
     *
     * @return Value of property active.
     */
    public boolean isActive() {
        return this.active;
    }
    
    /**
     * Setter for property active.
     *
     * @param active New value of property active.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Getter for property active.
     *
     * @return Value of property active.
     */
    public String getActive() {
        if(this.active) {
            return "true";
        } else {
            return "false";
        }
    }
    
}
