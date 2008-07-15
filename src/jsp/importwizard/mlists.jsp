<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.Admin" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="wizard.import"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("csv_upload")); %>
<% pageContext.setAttribute("agnTitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_import")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ImportWizard")); %>

<%@include file="/header.jsp"%>


<html:form action="/importwizard">
    <html:hidden property="action"/>

    <b><font color=#73A2D0><bean:message key="ImportWizStep_6_of_7"/></font></b>
    <br>
    
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
    
        <tr>
            <td colspan=3>
                <br><span class="head3"><bean:message key="SubscribeLists"/>:</span><br><br>
            </td>
        </tr>

        <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id=" + AgnUtils.getCompanyID(request)+ " ORDER BY shortname")%>" maxRows="1000">
            <tr>
                <td colspan="3"><input type="checkbox" name="agn_mlid_<%= pageContext.getAttribute("_agnTbl_mailinglist_id") %>"> <%= pageContext.getAttribute("_agnTbl_shortname") %> (ID <%= pageContext.getAttribute("_agnTbl_mailinglist_id") %>)</td>
            </tr>
        </agn:ShowTable>
        
        <tr><td colspan="3"><hr>
        <html:image src="button?msg=Back"  border="0" property="mlists_back" value="mlists_back"/>
        &nbsp;&nbsp;&nbsp;
        <html:image src="button?msg=Proceed" border="0"/></td></tr>
        
    </table>                      

</html:form>                
          


<%@include file="/footer.jsp"%>
