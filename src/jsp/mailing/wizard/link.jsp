<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*, org.apache.struts.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<%
   int tmpMailingID=0;
   MailingWizardForm aForm=null;
   String permToken=null;
   String tmpShortname=new String("");

   if((aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm"))!=null) {
       tmpMailingID=aForm.getMailing().getId();
       tmpShortname=aForm.getMailing().getShortname();
   }
   
%>

<agn:Permission token="mailing.show"/>

<%
// mailing navigation:
    pageContext.setAttribute("sidemenu_active", new String("Mailings")); 
    pageContext.setAttribute("sidemenu_sub_active", new String("New_Mailing"));
    pageContext.setAttribute("agnNavigationKey", new String("MailingWizard"));
    pageContext.setAttribute("agnHighlightKey", new String("MailingWizard"));
    pageContext.setAttribute("agnTitleKey", new String("Mailing")); 
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); 
    pageContext.setAttribute("agnSubtitleValue", tmpShortname);
%>

<%@include file="/header.jsp"%>

<html:errors/>


<html:form action="/mwLink" focus="Description" enctype="application/x-www-form-urlencoded">
    <html:hidden property="action"/>
 
    <b><font color=#73A2D0><bean:message key="MWizardStep_9_of_11"/></font></b>
    
    <br>
    <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="400" height="10" border="0">
    <br>
    <b><bean:message key="ChooseThenPressSave"/>.</b>
    <br><br>

                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><bean:message key="URL"/>:&nbsp;</td>
                        <td><bean:write name="mailingWizardForm" property="linkUrl"/></td>
                    </tr>
                    <tr><td colspan="2"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" height="2" border="0"></td></tr>
                    <tr>
                        <td><bean:message key="Description"/>:&nbsp;</td>
                        <td><html:text property="linkName" size="52" maxlength="99"/></td>
                    </tr>
                    <tr><td colspan="2"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" height="2" border="0"></td></tr>
                    <tr>
                        <td><bean:message key="Trackable"/>:&nbsp;</td>
                        <td><html:select property="trackable">
                                <html:option value="0"><bean:message key="Not_Trackable"/></html:option>
                                <html:option value="1"><bean:message key="Only_Text_Version"/></html:option>
                                <html:option value="2"><bean:message key="Only_HTML_Version"/></html:option>
                                <html:option value="3"><bean:message key="Text_and_HTML_Version"/></html:option>
                            </html:select></td>
                     </tr>
                     <tr><td colspan="2"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" height="2" border="0"></td></tr>
                     
                     <tr>
                        <td><bean:message key="Action"/>:&nbsp;</td>
                        <td><html:select property="linkAction" size="1">
                                <html:option value="0"><bean:message key="No_Action"/></html:option>
                                <agn:ShowTable id="agntbl2" sqlStatement="<%= new String("SELECT action_id, shortname FROM rdir_action_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" AND action_type<>1") %>" maxRows="500">
                                    <html:option value="<%= (String)pageContext.getAttribute("_agntbl2_action_id") %>"><%= pageContext.getAttribute("_agntbl2_shortname") %></html:option>
                                </agn:ShowTable>
                            </html:select>
                         </td>
                      </tr>
                      <tr><td colspan="2"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" height="2" border="0"></td></tr>
                      <tr><td></td><td><html:image src="button?msg=Save" border="0" property="mlink_save" value="mlink_save"/></td></tr>
                      
                  </table>
    
    <BR>
    <BR> 


    <% // wizard navigation: %>
    <br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;
                <html:image src="button?msg=Back"  border="0" property="mlink_back" value="mlink_back"/>
                &nbsp;
                <html:image src="button?msg=Proceed"  border="0" property="mlink_proceed" value="mlink_proceed"/>
                &nbsp;
                <html:link page="<%=new String("/mailingwizard.do?action=" + MailingWizardAction.ACTION_ATTACHMENT)%>"><html:img src="button?msg=Skip" border="0"/></html:link>
                &nbsp;
                <html:link page="<%=new String("/mailingwizard.do?action=" + MailingWizardAction.ACTION_SENDADDRESS)%>"><html:img src="button?msg=Finish" border="0"/></html:link>
                &nbsp;
            </td>
        </tr>
    </table>             
    
</html:form>
<%@include file="/footer.jsp"%>
