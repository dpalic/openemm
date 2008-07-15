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
import java.util.*;
import java.io.IOException;

public class ShowTableOffset extends BodyBase {
    
    protected int numRows;
    protected int startOffset;
    protected int maxRows;
    protected int maxPages;
    protected int endRow, sW, a;
    protected String id=null;
    
    public void setId(String aId) {
        id=aId;
    }
    
    public void setMaxPages(String num) {
        try {
            maxPages=Integer.parseInt(num);
        } catch (Exception e) {
            maxPages=0;
        }
    }
    
    /**
     * Prepares the offset.
     */
    public int doStartTag() throws JspTagException {
        
        if(id==null) {
            id=new String("");
        }
        
        try {
            maxRows=((Integer)pageContext.getAttribute("__"+id+"_MaxRows")).intValue();
        } catch (Exception e) {
            maxRows=0;
            return SKIP_BODY;
        }
        
        try {
            numRows=((Integer)pageContext.getAttribute("__"+id+"_ShowTableRownum")).intValue();
        } catch (Exception e) {
            numRows=0;
        }
        
        try {
            startOffset=Integer.parseInt(pageContext.getRequest().getParameter("startWith"));
        } catch (Exception e) {
            startOffset=0;
        }
        
        endRow=(numRows/maxRows);
        
        if (startOffset>0) {
            sW = startOffset/maxRows;
        } else {
            sW = 0;
        }
        a=0;
        if(a>=endRow)
            return SKIP_BODY;
        
        return doAfterBody();
    }
    
    /**
     * Sets attribute for the pagecontext.
     */
    public int doAfterBody() throws JspTagException {
        
        // pageContext.setAttribute("index", new Integer(a));
        if((a>endRow) || (a>maxPages))
            return SKIP_BODY;
        
        pageContext.setAttribute("startWith", Integer.toString(a*maxRows));
        pageContext.setAttribute("pageNum", Integer.toString(a+1));
        if(a==sW) {
            pageContext.setAttribute("activePage", "1");
        } else {
            pageContext.removeAttribute("activePage");
        }
        a++;
        
        return EVAL_BODY_BUFFERED;
    }
    
}
