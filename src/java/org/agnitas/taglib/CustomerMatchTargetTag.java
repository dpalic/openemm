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

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import org.agnitas.dao.*;
import org.agnitas.util.*;
import org.agnitas.target.*;
import org.springframework.context.*;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Connect: Connect to a database Table
 *
 * <Connect table="..." />
 */

public class CustomerMatchTargetTag extends BodyBase {
    protected int customerID;
    protected int targetID;
    
    //***************************************
    //* Implementations for Tag
    //***************************************
    
     /**
     * Setter for property customerID.
     * 
     * @param custID New value of property customerID.
     */
    public void setCustomerID(int custID) {
        this.customerID=custID;
    }
    
     /**
     * Setter for property targetID.
     * 
     * @param targID New value of property targetID.
     */
    public void setTargetID(int targID) {
        this.targetID=targID;
    }
    
    /**
     * checks if customer belongs to target group
     */
    public int	doStartTag() throws JspException	{
        int returnValue=SKIP_BODY;
        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        TargetDao tDao=(TargetDao)aContext.getBean("TargetDao");
        
        if(this.targetID==0) {
            return EVAL_BODY_BUFFERED;
        }
        
        Target aTarget=tDao.getTarget(this.targetID, this.getCompanyID());
        
        if(aTarget!=null) {
            if(aTarget.isCustomerInGroup(this.customerID, aContext)) {
                returnValue=EVAL_BODY_BUFFERED;
            }
        } else {
            returnValue=EVAL_BODY_BUFFERED;
        }
        
        return returnValue;
    }
    
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
}
