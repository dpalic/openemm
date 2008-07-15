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

package org.agnitas.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.*;
import java.util.*;
import org.agnitas.util.*;
import org.agnitas.beans.Admin;

/**
 * Connect: Connect to a database Table
 *
 * <Connect table="..." />
 */

public abstract class BodyBase extends TagSupport implements BodyTag {
    protected BodyContent bodyContent=null;
       
    //***************************************
    //* Implementations for Tag
    //***************************************
    public void	doInitBody() {
        return;
    }
    
    public int doStartTag() throws JspException	{
        return EVAL_BODY_BUFFERED;
    }
    
     /**
     * Setter for property bodyContent.
     * 
     * @param b New value of property bodyContent.
     */
    public void	setBodyContent(BodyContent b) {
        bodyContent=b;
        return;
    }
    
    /**
     * writes the body content.
     */
    public int doEndTag() throws JspException {
        try {
            if(bodyContent!=null) {
                JspWriter w=bodyContent.getEnclosingWriter();
                
                if(w!=null) {
                    bodyContent.writeOut(w);
                }
            }
        } catch(IOException e) {
            throw new JspException(e.getMessage());
        }
        
        return EVAL_PAGE;
    }
    
    /**
     * Getter for property localeString.
     *
     * @return Value of localeString.
     */
    public String getLocaleString(String key) {
        return SafeString.getLocaleString(key, ((Admin)pageContext.getSession().getAttribute("emm.admin")).getLocale());
    }
    
    /**
     * Getter for property companyID.
     *
     * @return Value of companyID.
     */
    public int getCompanyID() {
        
        int companyID=0;
        
        try {
            companyID=((Admin)pageContext.getSession().getAttribute("emm.admin")).getCompany().getId();
        } catch (Exception e) {
            AgnUtils.logger().error("getCompanyID: no companyID: "+e.getMessage());
            companyID=0;
        }
        return companyID;
    }
}

