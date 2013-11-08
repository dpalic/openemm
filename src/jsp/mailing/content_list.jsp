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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 

<agn:CheckLogon/>

<agn:Permission token="mailing.content.show"/>

<% int tmpMailingID=0;
    String tmpShortname=new String("");
    EmmLayout aLayout=(EmmLayout)session.getAttribute("emm.layout");
    MailingContentForm aForm=null;
    if(session.getAttribute("mailingContentForm")!=null) {
        aForm=(MailingContentForm)session.getAttribute("mailingContentForm");
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getShortname();
    }
%>

<logic:equal name="mailingContentForm" property="isTemplate" value="true">
    <% // template navigation:
        pageContext.setAttribute("sidemenu_active", new String("Templates"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("templateView"));
        pageContext.setAttribute("agnHighlightKey", new String("Content"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnTitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>

<logic:equal name="mailingContentForm" property="isTemplate" value="false">
    <%
        // mailing navigation:
        pageContext.setAttribute("sidemenu_active", new String("Mailings"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("mailingView"));
        pageContext.setAttribute("agnHighlightKey", new String("Content"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
        pageContext.setAttribute("agnTitleKey", new String("Mailing"));
        pageContext.setAttribute("agnSubtitleKey", new String("Mailing"));
    %>
</logic:equal>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
                
    <tr> 
        <td colspan="3">
            <html:form action="/mailingsend">
                <input type="hidden" name="mailingID" value="<%= tmpMailingID %>"/>
                <input type="hidden" name="action" value="<%= MailingSendAction.ACTION_PREVIEW_SELECT %>">
                <bean:message key="Recipient"/>:&nbsp;
                <html:select property="previewCustomerID" size="1">
                    <agn:ShowTable id="agntbl" sqlStatement="<%= new String("SELECT bind.customer_id, cust.email, cust.firstname, cust.lastname FROM customer_"+AgnUtils.getCompanyID(request)+"_tbl cust, customer_"+AgnUtils.getCompanyID(request)+"_binding_tbl bind WHERE (bind.user_type='A' OR bind.user_type='T') AND bind.user_status=1 AND bind.mailinglist_id="+aForm.getMailinglistID()+" AND bind.customer_id=cust.customer_id ORDER BY bind.user_type, bind.customer_id") %>" maxRows="100">
                        <% if(aForm.getPreviewCustomerID()==0) {
            aForm.setPreviewCustomerID(Integer.parseInt((String)pageContext.getAttribute("_agntbl_customer_id")));
                            } %>
                        <html:option value="<%= (String)(pageContext.getAttribute("_agntbl_customer_id")) %>"><%= pageContext.getAttribute("_agntbl_firstname") %>&nbsp;<%= pageContext.getAttribute("_agntbl_lastname") %>&nbsp;(<%= pageContext.getAttribute("_agntbl_email") %>)</html:option>
                    </agn:ShowTable>
                </html:select>
                &nbsp;&nbsp;
                <bean:message key="Format"/>:&nbsp;
                <html:select property="previewFormat" size="1">
                    <html:option value="0"><bean:message key="Text"/></html:option>
                    <logic:greaterThan name="mailingContentForm" property="mailFormat" value="0">
                        <html:option value="1"><bean:message key="HTML"/></html:option>
                    </logic:greaterThan>
                </html:select>
                &nbsp;&nbsp;    
                <bean:message key="Size"/>:&nbsp;
                <html:select property="previewSize" size="1">
                    <html:option value="4">640x480</html:option>
                    <html:option value="1">800x600</html:option>
                    <html:option value="2">1024x768</html:option>
                    <html:option value="3">1280x1024</html:option>
                </html:select>
                &nbsp;&nbsp;&nbsp;<html:image src="button?msg=Preview" border="0"/>
                <hr size="1">
            </html:form>
        </td>
    </tr>

                
    <tr><td><span class="head3"><bean:message key="Text_Module"/>&nbsp;</span></td><td><span class="head3"><bean:message key="Target"/>&nbsp;</span></td><td><span class="head3"><bean:message key="Content"/>&nbsp;</span></td></tr>
    <tr><td colspan="3"><hr></td></tr>
    <%
        String dyn_target_bgcolor=null;
        boolean bgColor=true;
        boolean newTag=false;
    %>

    <% DynamicTag dynTag=null;
        DynamicTagContent tagContent=null;
    %>
   
<% int prev_group=-1; %> 
    <logic:iterate id="dyntag" name="mailingContentForm" property="content">
        <%  Map.Entry ent=(Map.Entry)pageContext.getAttribute("dyntag");
            dynTag=(DynamicTag)ent.getValue();
            newTag=true;
        	if(dynTag.getGroup() != prev_group) {
	            if(bgColor) {
	                dyn_target_bgcolor=aLayout.getNormalColor();
	                bgColor=false;
	            } else {
	                dyn_target_bgcolor=new String("#FFFFFF");
	                bgColor=true;
	            }
            }
        %>
       
        <logic:iterate id="dyncontent" name="dyntag" property="value.dynContent">
            <%  Map.Entry ent2=(Map.Entry)pageContext.getAttribute("dyncontent");
                tagContent=(DynamicTagContent)ent2.getValue(); %>
            <tr>
                <% if(newTag) { %>
                <td bgcolor="<%= dyn_target_bgcolor %>">
                    <a name="<%= dynTag.getId() %>">
                    &nbsp;<html:link page="<%= new String("/mailingcontent.do?action=" + MailingContentAction.ACTION_VIEW_TEXTBLOCK + "&dynNameID=" + dynTag.getId() + "&mailingID=" + tmpMailingID) %>"><b><%= dynTag.getDynName() %></b></html:link>
                    &nbsp;&nbsp;
                </td>
                <% } else { %>
                <td bgcolor="<%= dyn_target_bgcolor %>">
                    &nbsp;&nbsp;
                </td>
                <% } %>
                <td bgcolor="<%= dyn_target_bgcolor %>"><html:link page="<%= new String("/mailingcontent.do?action=" + MailingContentAction.ACTION_VIEW_TEXTBLOCK + "&dynNameID=" + dynTag.getId() + "&mailingID=" + tmpMailingID + "#" + tagContent.getId()) %>">
                    <% if(tagContent.getTargetID()==0) { %>
                    <bean:message key="All_Subscribers"/>
                    <% } else { %>
                    <logic:iterate id="trgt" name="targetGroups" scope="request">
                        <logic:equal name="trgt" property="id" value="<%= Integer.toString(tagContent.getTargetID()) %>">
                            <c:choose>
                              <c:when test="${trgt.deleted == 0}">
								${trgt.getTargetName()}
                              </c:when>
                              <c:otherwise>
                              	<span class="warning">${trgt.targetName} (<bean:message key="Deleted" />)</span>
                              </c:otherwise>
                            </c:choose>
                        </logic:equal>
                    </logic:iterate>
                    <% } %>
                </html:link>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td bgcolor="<%= dyn_target_bgcolor %>"><%= SafeString.getHTMLSafeString(tagContent.getDynContent(), 35) %>&nbsp;</td>
            </tr>
            <% newTag=false; %>
        </logic:iterate>



        <tr>
            <% if(newTag) { %>
            <td bgcolor="<%= dyn_target_bgcolor %>"><a name="<%= dynTag.getId() %>">
                &nbsp;<html:link page="<%= new String("/mailingcontent.do?action=" + MailingContentAction.ACTION_VIEW_TEXTBLOCK + "&dynNameID=" + dynTag.getId() + "&mailingID=" + tmpMailingID) %>"><b><%= dynTag.getDynName() %></b></html:link>
                &nbsp;&nbsp;
            </td>
            <% } else { %>
            <td bgcolor="<%= dyn_target_bgcolor %>">
                &nbsp;&nbsp;
            </td>
            <% } %>
            <td bgcolor="<%= dyn_target_bgcolor %>"><html:link page="<%= new String("/mailingcontent.do?action=" + MailingContentAction.ACTION_VIEW_TEXTBLOCK  + "&dynNameID=" + dynTag.getId() + "&mailingID=" + tmpMailingID + "#0" )  %>"><bean:message key="New_Content"/></html:link>&nbsp;&nbsp;&nbsp;&nbsp;</td>
            <td bgcolor="<%= dyn_target_bgcolor %>">&nbsp;</td>
        </tr>
        <% newTag=false; %>
       
    </logic:iterate>

</table>

<%@include file="/footer.jsp"%>
