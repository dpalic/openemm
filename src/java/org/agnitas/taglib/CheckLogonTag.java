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

import java.io.IOException;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;

/**
 * Check for a valid User logged on in the current session.  If there is no
 * such user, forward control to the logon page.
 *
 * @author Craig R. McClanahan
 * @author Marius Barduta
 * @version $Revision: 3.0 $ $Date: 2006/07/14 09:57:08 $
 */

public final class CheckLogonTag extends TagSupport {
     
    // --------------------------------------------------- Instance Variables
     
    /**
     * The page to which we should forward for the user to log on.
     */
    private String page = "/login.jsp";
      
    // ----------------------------------------------------------- Properties
       
    /**
     * Return the forward page.
     *
     * @return the forward page
     */
    public String getPage() {
        return (this.page);   
    }
    
    /**
     * Set the forward page.
     *
     * @param page The new forward page
     */
    public void setPage(String page) {
        this.page = page;  
    }
    
    // ------------------------------------------------------- Public Methods
    
    /**
     * Defer our checking until the end of this tag is encountered.
     * 
     * @exception JspException if a JSP exception has occurred
     * @return always SKIP_BODY
     */
    public int doStartTag() throws JspException { 
        return (SKIP_BODY);  
    }
    
    /**
     * Perform our logged-in user check by looking for the existence of
     * a session scope bean under the specified name.  If this bean is not
     * present, control is forwarded to the specified logon page.
     * 
     * @exception JspException if a JSP exception has occurred
     * @return EVAL_PAGE or SKIP_PAGE
     */
    public int doEndTag() throws JspException {
        
        // Is there a valid user logged on?
        boolean valid = false;
        HttpSession session = pageContext.getSession();
        if ((session != null) && (session.getAttribute("emm.admin") != null))
            valid = true;
        
        // Forward control based on the results
        if (valid)
            return (EVAL_PAGE);
        else {
            try {
                pageContext.forward(page);
            } catch (Exception e) {
                throw new JspException(e.toString());
            }
            return (SKIP_PAGE);
        }  
    }
    
    /**
     * Release any acquired resources.
     */
    public void release() {  
        super.release();
        this.page = "/logon.jsp";
    }
}
