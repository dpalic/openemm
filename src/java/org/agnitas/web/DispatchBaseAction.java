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
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.web;

import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.StrutsFormBase;
import org.springframework.web.struts.DispatchActionSupport;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


public class DispatchBaseAction extends DispatchActionSupport {

    /**
     * Sets the number of rows to be shown in tables to the form. If the value is not set yet - takes the value from
     * user settings, if it is empty - takes default value (<code>StrutsFormBase.DEFAULT_NUMBER_OF_ROWS</code>)
     *
     * @param req servlet request object
     * @param aForm StrutsFormBase object
     */
	public void setNumberOfRows(HttpServletRequest req, StrutsFormBase aForm) {
		if( aForm.getNumberOfRows() == -1 ) {
			int numberOfRows = AgnUtils.getAdminPreferences(req).getListSize();
			if( numberOfRows == 0 ) {
				aForm.setNumberOfRows(StrutsFormBase.DEFAULT_NUMBER_OF_ROWS);
			}else {
				aForm.setNumberOfRows(numberOfRows);
			}
		}
	}
	
    /**
     * Initialize the list which keeps the current width of the columns, with a default value of '-1'
     * A JavaScript in the corresponding jsp will set the style.width of the column.
     *
     * @param size number of columns
     * @return the list of column width
     */
    protected List<String> getInitializedColumnWidthList(int size) {
		List<String> columnWidthList = new ArrayList<String>();
		for ( int i=0; i< size ; i++ ) {
			columnWidthList.add("-1");
		}
		return columnWidthList;
	}
    

    /**
     * Checks if sort property is contained in request, if yes - puts it also to form, if not - gets it from form;
     * returns the obtained sort property.
     *
     * @param request servlet request object
     * @param aForm StrutsFormBase object
     * @return String value of sort
     */
    protected String getSort(HttpServletRequest request, StrutsFormBase aForm) {
        String sort = request.getParameter("sort");
        if (sort == null) {
            sort = aForm.getSort();
        } else {
            aForm.setSort(sort);
        }
        return sort;
	}

    /**
     * If the page width is undefined - sets it to wide if user has appropriate permission, if not - sets it to normal.
     *
     * @param request servlet request object
     * @param aForm StrutsFormBase object
     */
    protected void resolveFormWidth(HttpServletRequest request, StrutsFormBase aForm){
        if (aForm.getExtendedWidthState() == StrutsFormBase.WIDTH_STATE_UNDEFINED) {
            if (AgnUtils.allowed("layout.supersize", request)) {
                aForm.setExtendedWidthState(StrutsFormBase.WIDTH_STATE_WIDE);
            } else {
                aForm.setExtendedWidthState(StrutsFormBase.WIDTH_STATE_NORMAL);
            }
        }
    }
}