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
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class BaseDispatchAction extends DispatchAction {

    protected void initTableParams(int columnNum, StrutsFormBase form) {
        if (form.getColumnwidthsList() == null  || form.getColumnwidthsList().size() == 0) {
            form.setColumnwidthsList(getInitializedColumnWidthList(columnNum));
        }
    }

    protected List<String> getInitializedColumnWidthList(int size) {
        List<String> columnWidthList = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            columnWidthList.add("-1");
        }
        return columnWidthList;
    }

    protected void showSavedMessage(HttpServletRequest request) {
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
        saveMessages(request, messages);
    }

    public void setNumberOfRows(StrutsFormBase aForm, HttpServletRequest request) {

        if(!aForm.isNumberOfRowsChanged()) {
            int numberOfRows = AgnUtils.getAdminPreferences(request).getListSize();
            if (numberOfRows <= 0) {
                aForm.setNumberOfRows(StrutsFormBase.DEFAULT_NUMBER_OF_ROWS);
            } else {
                aForm.setNumberOfRows(numberOfRows);
            }
        }
        aForm.setNumberOfRowsChanged(false);
    }
}
