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
import org.agnitas.util.*;
import org.agnitas.beans.Admin;

/**
 * Connect: Connect to a database Table
 *
 * <Connect table="..." />
 */

public class ShowByPermissionTag extends BodyBase {
    protected BodyContent bodyContent=null;
    protected HttpSession session;
    protected JspWriter	out;
    protected String token;
    
    //***************************************
    //* Implementations for Tag
    //***************************************
    
    public void setToken(String mode) {
        if(mode!=null) {
            token=mode;
        } else {
            token=new String("");
        }
    }
    
    /**
     * permission control
     */
    public int	doStartTag() throws JspException	{
        Admin aAdmin=null;
        session=pageContext.getSession();
        out=pageContext.getOut();
        
        aAdmin=(Admin)session.getAttribute("emm.admin");
        if(aAdmin!=null)
            if(aAdmin.permissionAllowed(token))
                return EVAL_BODY_BUFFERED;
        
        return SKIP_BODY;
    }
    
     /**
     * Setter for property bodyContent.
     * 
     * @param b New value of property bodyContent.
     */
    public void	setBodyContent(BodyContent b)	{ bodyContent=b; }
    
    /**
     * Writes the body content.
     */
    public int doEndTag() throws JspException	{
        try {
            if(bodyContent != null) {
                JspWriter w=bodyContent.getEnclosingWriter();
                
                if(w != null)
                    bodyContent.writeOut(w);
            }
        } catch(Exception e) {
            throw new JspException(e.getMessage());
        }
        return EVAL_PAGE;
    }
    
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
}
