<%--
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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*,java.util.*, org.agnitas.web.ImportWizardForm, org.agnitas.beans.Admin" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.import"/>

<% ImportWizardForm aForm=null;
Map aDbAllColumns=new Hashtable();
ArrayList aCsvList = null;
int aMode=0;
if((aForm=(ImportWizardForm)session.getAttribute("importWizardForm"))!=null) {
    aDbAllColumns=aForm.getDbAllColumns();
    aCsvList = aForm.getCsvAllColumns();
    aMode = aForm.getMode();
} %>

<% pageContext.setAttribute("sidemenu_active", new String("Recipients")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("csv_upload")); %>
<% pageContext.setAttribute("agnTitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_import")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ImportWizard")); %>

<%@include file="/header.jsp"%>
<html:errors/>
             

<html:form action="/importwizard" enctype="multipart/form-data">
    <html:hidden property="action"/>

    <b><font color=#73A2D0><bean:message key="ImportWizStep_3_of_7"/></font></b>
    <br>
    
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
    
        <tr><td colspan="2"><b><bean:message key="CsvMappingMsg"/>:</b><br>&nbsp;</td></tr>

        <tr>
            <td><b><bean:message key="CsvColumn"/></b></td>
            <td>&nbsp;&nbsp;&nbsp;&nbsp;<b><bean:message key="DbColumn"/></b></td>
        </tr>

        <tr><td colspan="2"><hr></td></tr>

        <%
        Map<String, String> linkedMap=new LinkedHashMap<String, String>();
        %>
        <agn:ShowColumnInfo id="agnTbl" table="<%= AgnUtils.getCompanyID(request) %>" hide="timestamp, change_date, creation_date, bounceload, datasource_id">
        <%
            String colName=(String) pageContext.getAttribute("_agnTbl_column_name");
            String aliasName=(String) pageContext.getAttribute("_agnTbl_shortname");

            linkedMap.put(colName, aliasName);
        %>
        </agn:ShowColumnInfo>
        <%
        String aktCsvColname=""; 
        CsvColInfo aCsvColInfo=null;
        for(int j=0; j<aCsvList.size(); j++) {
               aCsvColInfo=(CsvColInfo)aCsvList.get(j); %>
        <tr>
        <% System.err.println(aCsvColInfo.getName()); %>
            <td><%=aCsvColInfo.getName()%></td>
            <td>&nbsp;&nbsp;
                <select name="<%=new String("map_"+(j+1))%>">
                    <option value="NOOP"><bean:message key="NoMapping"/></option>
                    <%
                    Iterator<String> i=linkedMap.keySet().iterator();

                    while(i.hasNext()) {
                        String colName=i.next();
                        String aliasName=linkedMap.get(colName);
//                        if( !colName.equalsIgnoreCase("CUSTOMER_ID") || (colName.equalsIgnoreCase("CUSTOMER_ID") && aMode!=ImportWizardForm.MODE_ADD && aMode!=ImportWizardForm.MODE_ADD_UPDATE) ) {
                          if( !colName.equalsIgnoreCase("CUSTOMER_ID") ) { %>
                            <option value="<%=new String(colName)%>" <%if(colName.trim().equalsIgnoreCase(aCsvColInfo.getName().trim())) { %>selected<% } %>><%= aliasName %></option>
                    <%
                        }
                    }
                    %>
                </select>
            </td>
        </tr>
        <% } %>
        
        <tr>
            <td colspan="2">
                <hr>
                <html:image src="button?msg=Back"  border="0" property="mapping_back" value="mapping_back"/>
                &nbsp;&nbsp;&nbsp;
                <html:image src="button?msg=Proceed" border="0"/>
            </td>
        </tr>
        
    </table>                      

</html:form>

<%@include file="/footer.jsp"%>
