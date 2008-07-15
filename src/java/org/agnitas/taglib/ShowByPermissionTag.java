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
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

import org.agnitas.beans.Admin;

/**
 * Connect: Connect to a database Table
 *
 * <Connect table="..." />
 */

public class ShowByPermissionTag extends BodyBase {
    private static final long serialVersionUID = 2088220971349294443L;
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
