<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*, java.text.*, java.sql.*, javax.sql.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>
<agn:Permission token="recipient.view"/>	 

<% 
    ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(application);
    RecipientForm recipient=(RecipientForm) session.getAttribute("recipientForm");
    Recipient cust=(Recipient) aContext.getBean("Recipient");

    if(recipient == null) {
        recipient=new RecipientForm();
    }
    
    
    
%>
<%
if(recipient.getRecipientID()!=0) {
     pageContext.setAttribute("sidemenu_sub_active", new String("none"));
     pageContext.setAttribute("agnHighlightKey", new String("Recipient"));
  } else {
     pageContext.setAttribute("sidemenu_sub_active", new String("NewRecipient"));
     pageContext.setAttribute("agnHighlightKey", new String("NewRecipient"));
  }

%>
<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Recipient")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Recipient")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_editor")); %>
<% pageContext.setAttribute("agnSubtitleValue", recipient.getEmail()); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("")); %>
<%@include file="/header.jsp"%>

<table border="0" cellspacing="0" cellpadding="0">
    <html:form action="/recipient">
    <html:hidden property="action"/>
    <html:hidden property="recipientID"/>
    <html:hidden property="user_type"/>
    <html:hidden property="user_status"/>
    <html:hidden property="listID"/>

<% if(request.getParameter("action")!=null && request.getParameter("action").equals("bouncedel")) { %>
    <tr>
        <td colspan="2">
            <font color="red">Softbounce-Scoring wurde zur&uuml;ckgesetzt<font><br><br>
        </td>
    </tr>
<% } %>
    <tr>
       <td><b><bean:message key="Salutation"/>:</b></td>
       <td>
           <html:select property="gender" size="1">
               <html:option value="0"><bean:message key="gender.0.short"/></html:option>
               <html:option value="1"><bean:message key="gender.1.short"/></html:option>
               <agn:ShowByPermission token="use_extended_gender">
                   <html:option value="3"><bean:message key="gender.3.short"/></html:option>
                   <html:option value="4"><bean:message key="gender.4.short"/></html:option>
                   <html:option value="5"><bean:message key="gender.5.short"/></html:option>
               </agn:ShowByPermission>
               <html:option value="2"><bean:message key="gender.2.short"/></html:option>
           </html:select>
        </td>
    </tr>
    <tr>
        <td><b><bean:message key="Title"/>:</b></td>
        <td><html:text property="title" size="40"/></td>
    </tr>
    <tr>
        <td><b><bean:message key="Firstname"/>:</b></td>
        <td><html:text property="firstname" size="40"/></td>
    </tr>
    <tr>
        <td><b><bean:message key="Lastname"/>:</b></td>
        <td><html:text property="lastname" size="40"/></td>
    </tr>
    <tr>
        <td><b><bean:message key="E-Mail"/>:</b></td>
        <td><html:text property="email" size="40"/></td>
    </tr>
    <tr>
        <td><b><bean:message key="Mailtype"/>:</b></td>
        <td>
            <html:radio property="mailtype" value="0"/>Text
            <html:radio property="mailtype" value="1"/>HTML
            <html:radio property="mailtype" value="2"/>Offline-HTML
        </td>
    </tr>
    <tr>
        <td colspan=2>
            <br><hr><span class="head3"><bean:message key="More_Profile_Data"/>:</span><br><br>
        </td>
    </tr>
                
    <agn:ShowColumnInfo id="agnTbl" table="<%= AgnUtils.getCompanyID(request) %>" hide="bounceload">
<%
String colName=(String) pageContext.getAttribute("_agnTbl_column_name");

if(colName == null) {
    colName=(String) pageContext.getAttribute("_agnTbl_column");
}

Set disabled=new HashSet();

disabled.add("email");
disabled.add("customer_id");
disabled.add("title");
disabled.add("gender");
disabled.add("mailtype");
disabled.add("firstname");
disabled.add("lastname");

if(!disabled.contains(colName.toLowerCase())) {
    int mode=((Integer) pageContext.getAttribute("_agnTbl_editable")).intValue();
    String colDate=new String("column("+colName+"_DAY_DATE)");
    String colMonth=new String("column("+colName+"_MONTH_DATE)");
    String colYear=new String("column("+colName+"_YEAR_DATE)");
    String colLabel=new String("column("+colName+")");

    if(((String) pageContext.getAttribute("_agnTbl_data_type")).equals("DATE")) {
        switch(mode) {
            case 0:
%>
                   <tr>
                       <td><b><%= (String) pageContext.getAttribute("_agnTbl_shortname") %>:&nbsp;</b></td>
                       <td><html:text property="<%= colDate %>" size="2"/>.<html:text property="<%= colMonth %>" size="2"/>.<html:text property="<%= colYear %>" size="4"/></td>
                   </tr>
                   <% break;
            case 1: %>
                   <tr>
                       <td><b><%= (String) pageContext.getAttribute("_agnTbl_shortname") %>:&nbsp;</b></td>
                       <td><html:text property="<%= colDate %>" size="2" readonly="true"/>.<html:text property="<%= colMonth %>" size="2" readonly="true"/>.<html:text property="<%= colYear %>" size="4" readonly="true"/></td>
                   </tr>
                   <% break;
            case 2: %>
                   <html:hidden property="<%= colDate %>"/>
                   <html:hidden property="<%= colMonth %>"/>
                   <html:hidden property="<%= colYear %>"/>
                   <% break; } %>
<% } else {
       switch(mode) {
            case 0: %>
                   <tr>
                       <td><b><%= (String) pageContext.getAttribute("_agnTbl_shortname") %>:&nbsp;</b></td>
                       <td><html:text property="<%= colLabel %>" size="40"/></td>
                   </tr>
                   <% break;
            case 1: %>
                   <tr>
                       <td><b><%= (String) pageContext.getAttribute("_agnTbl_shortname") %>:&nbsp;</b></td>
                       <td><html:text property="<%= colLabel %>" size="40" readonly="true"/></td>
                   </tr>
                   <% break;
            case 2: %>
                   <html:hidden property="<%= colLabel %>"/>
                   <% break; } %>
<%
    }
} %>
</agn:ShowColumnInfo>
   <tr>
       <td colspan=2>
           <br><hr><br><span class="head3"><bean:message key="Mailinglists"/>:</span><br>&nbsp;<br>&nbsp;<br>
       </td>
   </tr>

<%
BindingEntry tmpStatusEntry=null;
int tmpUserStatus;
String tmpUserType=null;
String tmpUserRemark=null;
java.util.Date tmpUserDate=null;
DateFormat aFormat=DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
boolean aMType =false;
int k=0;

// just for debugging:
// please clean me up asap:
Map MTL = new HashMap();
Integer mailingListId;

// for agn:ShowByPermission keys
String[] ES={ "email" }; 

cust.setCompanyID(AgnUtils.getCompanyID(request));
cust.setCustomerID(recipient.getRecipientID());

Map allCustLists=cust.getAllMailingLists();
%>

    <agn:ShowTable id="agnTbl" sqlStatement="<%= "SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id=" + AgnUtils.getCompanyID(request)+ " ORDER BY shortname"%>" maxRows="1000">
        <tr>
            <td><b><%= pageContext.getAttribute("_agnTbl_shortname") %>:</b></td>
            <td>&nbsp;</td>
        </tr>
<%
    mailingListId = new Integer(Integer.parseInt((String)pageContext.getAttribute("_agnTbl_mailinglist_id")));
    if(allCustLists.get(mailingListId)!=null) {
        MTL = (Map)(allCustLists.get(mailingListId));
    } else {
        MTL = new HashMap();
        allCustLists.put(mailingListId, MTL);
    }

   for(k=0; k < ES.length; k++) {
       if(ES[k] == null) {
           continue;
       }
       tmpStatusEntry=((BindingEntry)(MTL.get(new Integer(k))));
       if(tmpStatusEntry==null) {
          tmpStatusEntry=new org.agnitas.beans.impl.BindingEntryImpl();
          tmpStatusEntry.setCustomerID(recipient.getRecipientID());
          tmpStatusEntry.setMailinglistID( mailingListId );
       }
       tmpUserType=tmpStatusEntry.getUserType();
       tmpUserStatus=tmpStatusEntry.getUserStatus();
       tmpUserRemark=tmpStatusEntry.getUserRemark();
       tmpUserDate=tmpStatusEntry.getChangeDate();
       int mti=Integer.parseInt((String) pageContext.getAttribute("_agnTbl_mailinglist_id"));
       recipient.setBindingEntry(mti, tmpStatusEntry);
    %>

        <tr>
            <td>
                <br>&nbsp;&nbsp;<html:checkbox property="<%= "bindingEntry["+mti+"].userStatus" %>" value="1"/><input type="hidden" name="<%= "__STRUTS_CHECKBOX_bindingEntry["+mti+"].userStatus" %>" value="<%= ((tmpUserStatus == BindingEntry.USER_STATUS_ACTIVE)?3:tmpUserStatus) %>"><b>&nbsp;<bean:message key="<%= new String("MediaType."+k) %>"/></b
            </td>
            <td>
                &nbsp;&nbsp;<bean:message key="Type"/>:
                <html:select property="<%= "bindingEntry["+mti+"].userType"%>" size="1">
                    <html:option value="A"><bean:message key="Administrator"/></html:option>
                    <html:option value="T"><bean:message key="TestSubscriber"/></html:option>
                    <html:option value="W"><bean:message key="NormalSubscriber"/></html:option>
                </html:select>
            </td>
        </tr>
        <tr>
            <td>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="Status"/>:&nbsp;
                <% if(tmpUserStatus > 0 && tmpUserStatus <= 5) { %>
                       <bean:message key="<%= "MailingState"+tmpUserStatus %>"/>
                <% } %>
            </td>
            <td>
                &nbsp;&nbsp;<bean:message key="Remark"/>:&nbsp;<%= tmpUserRemark %>
                <% if(tmpUserDate!=null) { %>
                       <br>&nbsp;&nbsp;<%= aFormat.format(tmpUserDate) %>
                <% } %>
            </td>
        </tr>
        <tr><td colspan="2"><br></td></tr>
<%
    } 
%>
    <tr><td colspan="2"><hr></td></tr>
</agn:ShowTable>

    <tr>
        <td colspan="2">
            <agn:ShowByPermission token="recipient.change">
                <html:image src="button?msg=Save" border="0" property="save" value="save"/>&nbsp;
            </agn:ShowByPermission>
            <html:link page="<%= new String("/recipient.do?action=" + RecipientAction.ACTION_LIST + "&user_type=" + request.getParameter("user_type") + "&user_status=" + request.getParameter("listID") + "&listID=" + request.getParameter("listID")) %>"><html:img src="button?msg=Cancel" border="0"/></html:link>
        </td>
    </tr>
    <agn:ShowByPermission token="SubscriberSoftbounceReset">
      <% if(recipient.getRecipientID()!=0) { %>    
        <tr>
            <td colspan="2"><br>
                <html:link page="<%= new String("/subscriber_view.jsp?action=bouncedel&recipientID="+ recipient.getRecipientID() +"&user_type=" + request.getParameter("user_type") + "&user_status=" + request.getParameter("user_status") + "&listID=" + request.getParameter("listID")) %>"><bean:message key="ResetSoftbounceScoring"/></html:link>
            </td>
        </tr>
      <% } %>  
        </agn:ShowByPermission>
    </html:form>
</table>
<%@include file="/footer.jsp"%>
