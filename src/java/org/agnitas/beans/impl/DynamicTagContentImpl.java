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

import java.util.*;
import java.io.*;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.util.*;

/**
 *
 * @author Martin Helff 
 */
public class DynamicTagContentImpl implements DynamicTagContent {

    protected int mailingID;
    protected int companyID;
    protected int dynNameID;
    protected int id;
    protected String dynName;
    protected int dynOrder;
    protected int targetID;
    protected String dynContent;
    
    public static int WITHOUT_CLOB_CONTENT=0;
    public static int WITH_CLOB_CONTENT=1;
    
    /** Creates new DynamicTagContent */
    public DynamicTagContentImpl() {
    }
    
    public void setDynNameID(int id) {
        dynNameID=id;
    }

    public void setId(int id) {
        this.id=id;
    }

    public void setDynName(String name) {
        dynName=name;
    }

    public void setDynContent(String content) {
        dynContent=content;
    }
    
    public void setCompanyID(int id) {
        companyID=id;
    }

    public void setMailingID(int id) {
        mailingID=id;
    }

    public void setDynOrder(int id) {
        dynOrder=id;
    }

    public void setTargetID(int tid) {
        targetID=tid;
    }
    
    public int getDynOrder() {
        return dynOrder;
    }
    
    public int getDynNameID() {
        return dynNameID;
    }

    public int getId() {
        return this.id;
    }

    public String getDynName() {
        return dynName;
    }
    
    public String getDynContent() {
        return dynContent;
    }
    
    public int getTargetID() {
        return targetID;
    }
        
    public boolean equals(Object obj) {
        return ((DynamicTagContent)obj).hashCode()==this.hashCode();
    }

    public int hashCode() {
        return this.dynContent.hashCode();
    }

    /**
     * Getter for property mailingID.
     * @return Value of property mailingID.
     */
    public int getMailingID() {
        return this.mailingID;
    }

    /**
     * Getter for property companyID.
     * @return Value of property companyID.
     */
    public int getCompanyID() {
        return this.companyID;
    }
    
}

