<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.Admin, java.util.*" contentType="text/html; charset=utf-8" buffer="64kb" errorPage="/error.jsp"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="admin.show"/>

<% int tmpAdminID = 0;
   int tmpCompID = 0;
   String tmpUser = "";
   int i=1;
   Set userrights=null;
   Set grouprights=null;

   tmpCompID=AgnUtils.getCompanyID(request);
   if(request.getAttribute("adminForm")!=null) {
      tmpUser=((AdminForm)request.getAttribute("adminForm")).getUsername();   
      tmpAdminID=((AdminForm)request.getAttribute("adminForm")).getAdminID();
      userrights=((AdminForm)request.getAttribute("adminForm")).getUserRights();
      grouprights=((AdminForm)request.getAttribute("adminForm")).getGroupRights();
   } %>

<% pageContext.setAttribute("agnSubtitleKey", new String("Admin")); %>              <!-- ueber rechte Seite -->
<% pageContext.setAttribute("sidemenu_active", new String("Settings")); %>          <!-- links Button -->
<% pageContext.setAttribute("sidemenu_sub_active", new String("Admins")); %>        <!-- links unter Button -->
<% pageContext.setAttribute("agnTitleKey", new String("Admins")); %>                <!-- Titelleiste -->
<% pageContext.setAttribute("agnNavigationKey", new String("admin")); %>            <!-- Karteileiste -->
<% pageContext.setAttribute("agnHighlightKey", new String("UserRights")); %>        <!-- markiertes Element -->


<% pageContext.setAttribute("agnSubtitleValue", tmpUser); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&adminID="+tmpAdminID)); %>

<%@include file="/header.jsp"%>

<html:errors/>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <html:form action="admin">
  <html:hidden property="adminID"/>
  <html:hidden property="companyID"/>
  <html:hidden property="action"/>
  <% String aCategory=new String(""); 
     boolean isFirst=true;
     LinkedList keyList=new LinkedList();
     ResourceBundle res=ResourceBundle.getBundle("messages");
     Enumeration aEnum=res.getKeys();
     while(aEnum.hasMoreElements()) {
         keyList.add(aEnum.nextElement());
     }
     
     Collections.sort(keyList);
     String tmpKey=null;
     String tmpCategory=new String("");
     String tmpValue=null;

     for(int j=0; j<keyList.size(); j++) {
         tmpKey=(String)keyList.get(j);
         if(tmpKey.startsWith("UserRight.")) {
            tmpKey=tmpKey.substring(10);
            tmpCategory=tmpKey.substring(0, tmpKey.indexOf('.'));
            tmpKey=tmpKey.substring(tmpKey.indexOf('.')+1);
            if(AgnUtils.allowed(tmpKey, request)) {
                if(!userrights.contains(tmpKey)) {
                    tmpValue=new String("user");
                } else {
                    tmpValue=new String("");
                }
                if(!aCategory.equals(tmpCategory)) {
                    aCategory=new String(tmpCategory);
            %>
            <tr>
              <td><% if(!isFirst) { %><hr size="1"><% } else { isFirst=false; } %><span class="head3"><bean:message key="<%= aCategory %>"/></span></td>
            </tr>
            <% } %>
                <% if(!grouprights.contains(tmpKey)) { %>
               <tr>
                 <td><input type="checkbox" name="user_right<%= i++ %>" value="user__<%= tmpKey %>"<% if(userrights.contains(tmpKey)) { %> checked <% } %>>&nbsp;<bean:message key="<%= new String("UserRight."+tmpCategory+"."+tmpKey) %>"/>&nbsp;&nbsp;</td>
               </tr>
               <% } else { %>
               <tr>
                 <td><input type="checkbox" name="user_right<%= i++ %>" value="group__<%= tmpKey %>" checked disabled>&nbsp;<bean:message key="<%= new String("UserRight."+tmpCategory+"."+tmpKey) %>"/>&nbsp;&nbsp;</td>
               </tr>
               <% } %>
<%          }
          }
      }
   %>
 
 <agn:ShowByPermission token="admin.change">
  <tr><td><html:image src="button?msg=Save" property="save" value="save"/></td></tr>
 </agn:ShowByPermission>
 </html:form> 
</table>
        

<%@include file="/footer.jsp"%>
