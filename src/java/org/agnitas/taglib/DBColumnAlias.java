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

import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.agnitas.beans.Admin;
import org.agnitas.util.AgnUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DBColumnAlias extends BodyBase {
    
    protected String column=null;
    
     /**
     * Setter for property column.
     * 
     * @param aCol New value of property column.
     */
    public void setColumn(String aCol) {
        if(aCol!=null) {
            column=new String(aCol);
        } else {
            column=new String("");
        }
    }
    
    /**
     * lists shortnames
     */
    public int doStartTag() throws JspTagException {

        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        JdbcTemplate jdbc=AgnUtils.getJdbcTemplate(aContext);
        String result=new String(this.column);
        String sql="SELECT shortname FROM customer_field_tbl WHERE company_id="+this.getCompanyID()+" AND col_name=? AND (admin_id=? OR admin_id=0) ORDER BY admin_id DESC";
        
        try {
        	
        	List l=jdbc.queryForList(sql, new Object[] {this.column, new Integer(((Admin)this.pageContext.getAttribute("emm.admin", PageContext.SESSION_SCOPE)).getAdminID())}, String.class);

            if(l !=null && l.size() > 0) {
                Map row=(Map)l.get(0);

                result=(String) row.get("shortname");
            }
            
        } catch (Exception e) {
        	
            AgnUtils.logger().error("doStartTag: "+e.getMessage());
            AgnUtils.getStackTrace( e );
        }
        finally {
        	writeResult( result );
        }
        return SKIP_BODY;
    }
    private void writeResult( String result ) {
        
    	try {
    		JspWriter out=null;
    		out=pageContext.getOut();
    		out.print(result);
    	}
    	catch(Exception e) {
    		AgnUtils.logger().error("doStartTag: "+e.getMessage());
    	}
    		
    		
    }
}
