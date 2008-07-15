<%@ page language="java" import="org.agnitas.util.*" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>

<agn:Permission token="stats.mailing"/>

<% pageContext.setAttribute("sidemenu_active", new String("Statistics")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("comparison")); %>
<% pageContext.setAttribute("agnTitleKey", new String("comparison")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("statsCompare")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("comparison")); %>

<%@include file="/header.jsp"%> 
<html:errors/>
          <table border="0" cellspacing="0" cellpadding="0">
<html:form action="/mailing_compare">
<html:hidden property="action"/>
<tr><td><span class="head3"><bean:message key="Mailing"/> <bean:message key="comparison"/></span></td></tr>
<tr><td colspan="3">&nbsp;</td></tr>

<tr>
    <td><bean:message key="Target"/>:&nbsp;            
            <html:select property="targetID" size="1">
                <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request)) %>" maxRows="500">
                    <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                </agn:ShowTable>
            </html:select>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
</tr>

<tr><td colspan="3">&nbsp;</td></tr>

<tr><td colspan="3">
<table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><b><bean:message key="Mailing"/>&nbsp;&nbsp;</b></td>
                    <td><b><bean:message key="Description"/>&nbsp;&nbsp;</b></td>
                    <td><div align=right><b><bean:message key="compare"/></b></div></td>
                </tr>
                <tr><td colspan="3"><hr size='1'></td></tr>
                <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT mailing_id, shortname, description FROM mailing_tbl A WHERE company_id="+AgnUtils.getCompanyID(request)+ " AND deleted<>1 AND is_template=0 ORDER BY mailing_id DESC")%>" maxRows="50">
                    <tr>
                        <td><html:link page="<%= new String("/mailing_stat.do?action=7&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><b><%= pageContext.getAttribute("_agnTbl_shortname") %></b></html:link>&nbsp;&nbsp;</td>
                        <td><html:link page="<%= new String("/mailing_stat.do?action=7&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_description"), 40) %></html:link>&nbsp;&nbsp;</td>
                        <td><div align=right><input type="checkbox" name="MailCompID_<%= pageContext.getAttribute("_agnTbl_mailing_id") %>"></div></td>
                    </tr>
                </agn:ShowTable>
                <tr><td colspan="3"><hr></td></tr>
                <tr>
                    <td colspan="2"><html:image src="button?msg=compare" border="0"/></td>
                    <td></td>
                </tr>

</table></td></tr>
</html:form>

        </table>
<%@include file="/footer.jsp"%>
