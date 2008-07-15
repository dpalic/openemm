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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.sql.DataSource;

import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ShowTableTag extends BodyBase {

    private static final long serialVersionUID = 9178865921553034730L;
	// global variables:
    protected String sqlStatement;
    protected String id=null;
    protected int startOffset=0;
    protected int maxRows=10000;
    protected boolean grabAll=false;
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

        pageContext.setAttribute("__"+id+"_MaxRows", new Integer(maxRows));

        try {
            grabAll = false;
            if (maxRows == 0) {
                rset=aTemplate.queryForList(sqlStatement);
		grabAll = true;
            } else {
                rset=aTemplate.queryForList(sqlStatement+" LIMIT "+ startOffset + "," + maxRows );
            }
            if(rset!=null) {
                int rowc = getRowCount(rset, aTemplate);

                ListIterator aIt=rset.listIterator();
                pageContext.setAttribute("__"+id+"_data", aIt);
                pageContext.setAttribute("__"+id+"_ShowTableRownum", new Integer(rowc));
                return doAfterBody();
            }
        }   catch ( Exception e) {
            AgnUtils.logger().error("doStartTag: "+e);
            AgnUtils.logger().error("SQL: "+sqlStatement);
            throw new JspTagException("Error: " + e);
        }
        return SKIP_BODY;
    }

	private int getRowCount(List rset, JdbcTemplate template) {
 		int result = 0;
		try {
			result = template.queryForInt("select count(*) from ( " + sqlStatement+" ) as tmp_tbl");
		}
		catch( Exception ex ) {
			AgnUtils.logger().error("getRowCount: "+ex);
            AgnUtils.logger().error("SQL: "+sqlStatement);
		}
		return result;
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
            if(aIt.hasNext() && (grabAll || ((this.maxRows--)!=0))) {
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
