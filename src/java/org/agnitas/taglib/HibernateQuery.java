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
import java.sql.*;
import javax.sql.*;
import java.util.*;
import org.agnitas.util.*;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.context.*;
import org.springframework.orm.hibernate3.*;
import org.hibernate.*;

public class HibernateQuery extends BodyBase {
    
    // global variables:
    protected String query;
    protected String id=null;
    protected int startOffset=0;
    protected int maxRows=-1;
    protected int encodeHtml=1;
    
     /**
     * Setter for property query.
     * 
     * @param sql New value of property query.
     */
    public void setQuery(String sql) {
        query=new String(sql);
    }
     
    /**
     * Setter for property id.
     * 
     * @param aId New value of property id.
     */
    public void setId(String aId) {
        id=aId;
    }
    
     /**
     * Setter for property maxRows.
     * 
     * @param off New value of property maxRows.
     */
    public void setMaxRows(String off) {
        try {
            maxRows=Integer.parseInt(off);
        } catch (Exception e) {
            maxRows=-1;
        }
    }
    
    public int doStartTag() throws JspTagException {
        
        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        HibernateTemplate aTemplate=new HibernateTemplate((SessionFactory)aContext.getBean("sessionFactory"));
        List rset=null;
        
        if(id==null) {
            id=new String("");
        }
        
        try {
            startOffset=Integer.parseInt(pageContext.getRequest().getParameter("startWith"));
        } catch (Exception e) {
            startOffset=0;
        }
        
        pageContext.setAttribute("__"+id+"_MaxRows", new Integer(maxRows));
        
        try {
            rset=aTemplate.find(query);
            if(rset!=null && rset.size()>0) {
                ListIterator aIt=rset.listIterator(startOffset);
                pageContext.setAttribute("__"+id, rset);
                pageContext.setAttribute("__"+id+"_data", aIt);
                pageContext.setAttribute("__"+id+"_ShowTableRownum", new Integer(rset.size()));
                return doAfterBody();
            } else {
                return SKIP_BODY;
            }
            
        }   catch ( Exception e) {
            throw new JspTagException("Error: " + e);
        }
    }
    
    public int doAfterBody() throws JspException {
        ListIterator aIt=(ListIterator)pageContext.getAttribute("__"+id+"_data");
        Object aRecord=null;
        Iterator colIt=null;
        
        try {
            if(aIt.hasNext() && ((this.maxRows--)!=0)) {
                aRecord=aIt.next();
                pageContext.setAttribute(id, aRecord);
                return EVAL_BODY_BUFFERED;
            } else {
                return SKIP_BODY;
            }
        } catch (Exception e) {
            AgnUtils.logger().error(e);
        }
        return SKIP_BODY;
    }
    
}
