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
import org.springframework.jdbc.core.*;

public class ShowTableTag extends BodyBase {
    
    // global variables:
    protected String sqlStatement;
    protected String id=null;
    protected int startOffset=0;
    protected int maxRows=10000;
    protected int encodeHtml=1;
    
    /**
     * Setter for property startOffset.
     * 
     * @param offset New value of property startOffset.
     */
    public void setStartOffset(String offset) {
        try {
            startOffset=Integer.parseInt(offset);
        } catch (Exception e) {
            startOffset=0;
        }
    }
    
    /**
     * Setter for property sqlStatement.
     * 
     * @param sql New value of property sqlStatement.
     */
    public void setSqlStatement(String sql) {
        sqlStatement=new String(sql);
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
            maxRows=0;
        }
    }
    
    /**
     * Setter for property encodeHtml.
     * 
     * @param off New value of property encodeHtml.
     */
    public void setEncodeHtml(String off) {
        try {
            encodeHtml=Integer.parseInt(off);
        } catch (Exception e) {
            encodeHtml=1;
        }
    }
    
    /**
     * Sets attribute for the pagecontext.
     */
    public int doStartTag() throws JspTagException {
        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        JdbcTemplate aTemplate=new JdbcTemplate((DataSource)aContext.getBean("dataSource"));
        List rset=null;
        
        if(id==null) {
            id=new String("");
        }
        
        // dbConn = this.getConnection();
       
/* 
        try {
            startOffset=Integer.parseInt(pageContext.getRequest().getParameter("startWith"));
        } catch (Exception e) {
            startOffset=0;
        }
*/
        
        pageContext.setAttribute("__"+id+"_MaxRows", new Integer(maxRows));
        
        try {
            rset=aTemplate.queryForList(sqlStatement);
            if(rset!=null) {
                ListIterator aIt=rset.listIterator(startOffset);
                pageContext.setAttribute("__"+id+"_data", aIt);
                pageContext.setAttribute("__"+id+"_ShowTableRownum", new Integer(rset.size()));
                return doAfterBody();
            }
        }   catch ( Exception e) {
            AgnUtils.logger().error("doStartTag: "+e);
            AgnUtils.logger().error("SQL: "+sqlStatement);
            throw new JspTagException("Error: " + e);
        }
        return SKIP_BODY;
    }
    
    /**
     * Sets attribute for the pagecontext.
     */
    public int doAfterBody() throws JspException {
        ListIterator aIt=(ListIterator)pageContext.getAttribute("__"+id+"_data");
        Map aRecord=null;
        Iterator colIt=null;
        String colName=null;
        String colDataStr=null;
        Object colData=null;
        
        try {
            if(aIt.hasNext() && ((this.maxRows--)!=0)) {
                aRecord=(Map)aIt.next();
                colIt=aRecord.keySet().iterator();
                while(colIt.hasNext()) {
                    colName=(String)colIt.next();
                    colData=aRecord.get(colName);
                    if(colData!=null) {
                        colDataStr=colData.toString();
                    } else {
                        colDataStr=new String("");
                    }
                    if(encodeHtml!=0 && String.class.isInstance(colData)) {
                        pageContext.setAttribute(new String("_"+id+"_"+colName.toLowerCase()), SafeString.getHTMLSafeString(colDataStr));
                    } else {
                        pageContext.setAttribute(new String("_"+id+"_"+colName.toLowerCase()), colDataStr);
                    }
                    
                }
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
