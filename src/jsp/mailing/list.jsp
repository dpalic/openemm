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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.text.*, java.util.*" buffer="32kb" %>
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

<% SimpleDateFormat parsedate=new SimpleDateFormat("yyyy-MM-dd");
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
                    <td><span class="head3">&nbsp;&nbsp;</span></td>
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
                <tr><td colspan="6"><hr></td></tr>
<%	EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
	String dyn_bgcolor=null;
    boolean bgColor=true;
 %>                
                <agn:ShowTable id="agnTbl" sqlStatement="<%= "select *, case when senddate is null then 0 else 1 end as send_null from ( SELECT a.mailing_id, a.shortname, a.description, a.mailinglist_id, ( SELECT min( c."+AgnUtils.changeDateName()+" ) FROM mailing_account_tbl c WHERE a.mailing_id =c.mailing_id AND c.status_field = 'W' ) AS senddate FROM mailing_tbl a WHERE a.company_id = "+AgnUtils.getCompanyID(request)+" AND a.deleted <> 1 AND a.is_template = "+isTemplate+" ) te ORDER BY send_null ASC, senddate DESC, mailing_id DESC"%>" startOffset="<%= request.getParameter("startWith") %>" maxRows="50">
<% 	if(bgColor) {
   		dyn_bgcolor=aLayout.getNormalColor();
    	bgColor=false;
    } else {
    	dyn_bgcolor=new String("#FFFFFF");
        bgColor=true;
    }
 %>        
            <tr bgcolor="<%= dyn_bgcolor %>">
            	<% if(isTemplate==0) { %>
                        <td><html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_USED_ACTIONS + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><img border="0" title="<bean:message key="action_link" />" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>extlink.gif"></html:link>&nbsp;&nbsp;</td>
                <% } %>        
                        <td><html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><b><%= pageContext.getAttribute("_agnTbl_shortname") %></b></html:link>&nbsp;&nbsp;</td>
                        <td><html:link page="<%= new String("/mailingbase.do?action=" + MailingBaseAction.ACTION_VIEW + "&mailingID=" + pageContext.getAttribute("_agnTbl_mailing_id")) %>"><%= SafeString.cutLength((String)pageContext.getAttribute("_agnTbl_description"), 35) %></html:link>&nbsp;&nbsp;</td>
                        <td>
                            <% Map map=pageContext.getRequest().getParameterMap();
                               Object startWith=null;

                               if(map.containsKey("startWith")) {
                                   startWith=map.get("startWith");
                                   map.remove("startWith");
                               }
                            %>
                            <agn:HibernateQuery id="ml" query="<%= "from Mailinglist where id=" + pageContext.getAttribute("_agnTbl_mailinglist_id") + " and companyID="+AgnUtils.getCompanyID(request) %>">
                                ${ml.getShortname()}
                            </agn:HibernateQuery>
                            <%
                               if(startWith != null) {
                                   map.put("startWith",startWith);
                               }
                            %>
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
                <tr><td colspan="6"><hr></td></tr>
                <tr><td colspan="6"><center>
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
