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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.taglib;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Check for a valid User logged on in the current session.  If there is no
 * such user, forward control to the logon page.
 *
 * @author Craig R. McClanahan
 * @author Marius Barduta
 * @version $Revision: 1.1 $ $Date: 2006/08/03 08:47:45 $
 */

public final class CheckLogonTag extends TagSupport {
     
    // --------------------------------------------------- Instance Variables
     
    private static final long serialVersionUID = -4706642742651352150L;
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
