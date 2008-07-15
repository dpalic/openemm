<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.text.*, java.util.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<logic:equal name="mailingBaseForm" property="isTemplate" value="false">
<% pageContext.setAttribute("sidemenu_active", new String("Mailings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("MailingsOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>

<% pageContext.setAttribute("agnTitleKey", new String("Mailings")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailings")); %>
</logic:equal>

<logic:equal name="mailingBaseForm" property="isTemplate" value="true">
<% pageContext.setAttribute("sidemenu_active", new String("Templates")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("Overview")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("TemplatesOverview")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Overview")); %>

<% pageContext.setAttribute("agnTitleKey", new String("Templates")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Templates")); %>
</logic:equal>

<% SimpleDateFormat parsedate=new SimpleDateFormat("yyyyMMdd");
   DateFormat showdate=DateFormat.getDateInstance(DateFormat.MEDIUM, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
   String aDate=new String("");
   Date tmpDate=null;
   int isTemplate=0;
   if(((MailingBaseForm)session.getAttribute("mailingBaseForm")).isIsTemplate()) {
       isTemplate=1;
   }
%>

<% if(isTemplate==0) { %>
<agn:Permission token="mailing.show"/>
<% } else { %>
<agn:Permission token="template.show"/>
<% } %>

<%@include file="/header.jsp"%>
<html:errors/>
              <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <logic:equal name="mailingBaseForm" property="isTemplate" value="false">
                    <td><span class="head3"><bean:message key="Mailing"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3"><bean:message key="Description"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3"><bean:message key="Mailinglist"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3"><bean:message key="mailing.senddate"/>&nbsp;&nbsp;</span></td>
                    </logic:equal>
                    
                    <logic:equal name="mailingBaseForm" property="isTemplate" value="true">
                    <td><span class="head3"><bean:message key="Template"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3"><bean:message key="Description"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3"><bean:message key="Mailinglist"/>&nbsp;&nbsp;</span></td>
                    <td><span class="head3">&nbsp;</span></td>
                    </logic:equal>
                    
                    <td><span class="head3">&nbsp;</span></td>
                </tr>
                <tr><td colspan="5"><hr></td></tr>
                <agn:ShowTable id="agnTbl" sqlStatement="<%= new String("SELECT a.mailing_id, a.shortname, a.description, a.mailinglist_id, (SELECT min(c.change_date) FROM mailing_account_tbl c WHERE a.mailing_id=c.mailing_id AND c.status_field='W') AS senddate FROM mailing_tbl a WHERE a.company_id="+AgnUtils.getCompanyID(request)+" AND a.deleted<>1 AND a.is_template="+isTemplate+" ORDER BY senddate ASC, mailing_id DESC")%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">
                    <tr>
                        <td><html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><b><%= pageContext.getAttribute("_agnTbl_shortname") %></b></html:link>&nbsp;&nbsp;</td>
                        <td><html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_description"), 35) %></html:link>&nbsp;&nbsp;</td>
                        <td>
                            <agn:HibernateQuery id="ml" query="<%= "from Mailinglist where id=" + pageContext.getAttribute("_agnTbl_mailinglist_id") + " and companyID="+AgnUtils.getCompanyID(request) %>">
                                ${ml.getShortname()}
                            </agn:HibernateQuery>
                        &nbsp;&nbsp;</td>
                        <% try{
                             tmpDate=parsedate.parse((String)pageContext.getAttribute("_agnTbl_senddate"));
                             aDate=showdate.format(tmpDate);
                            } catch (Exception e) {
                                 aDate=new String("");
                            }
                        %>
                        <td><logic:equal name="mailingBaseForm" property="isTemplate" value="false"><%= aDate %>&nbsp;</logic:equal>&nbsp;</td>
                        <td><nobr>
                            <agn:ShowByPermission token="mailing.delete">
                                <html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_CONFIRM_DELETE + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif" alt="<bean:message key="Delete"/>" border="0"></html:link>
                            </agn:ShowByPermission>
                            <html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>bearbeiten.gif" alt="<bean:message key="Edit"/>" border="0"></html:link>
                        </nobr>
                        </td>
                    </tr>
                </agn:ShowTable>
                <tr><td colspan="5"><hr></td></tr>
                <tr><td colspan="5"><center>
                     <agn:ShowTableOffset id="agnTbl" maxPages="10">
                        <html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_LIST + "&startWith=" + pageContext.getAttribute("startWith")) %>">
                        <% if(pageContext.getAttribute("activePage")!=null) { %>
                            <span class="activenumber">&nbsp;
                        <% } %>
                        <%= pageContext.getAttribute("pageNum") %>
                        <% if(pageContext.getAttribute("activePage")!=null) { %>
                            &nbsp;</span>
                        <% } %>
                        </html:link>&nbsp;
                     </agn:ShowTableOffset></center></td></tr>
              </table>

              
              
<%@include file="/footer.jsp"%>
