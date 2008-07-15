<%@ page language="java" import="java.util.*, org.agnitas.beans.EmmLayout, org.agnitas.web.*, org.apache.struts.action.*, org.agnitas.util.*, org.springframework.context.*, org.springframework.orm.hibernate3.*, org.springframework.web.context.support.WebApplicationContextUtils" pageEncoding="UTF-8"%>
<jsp:directive.page import="org.agnitas.beans.VersionObject"/>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:html>
<%
   LogonForm aForm=(LogonForm)request.getAttribute("logonForm");
   ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(application);
   
   HibernateTemplate aTemplate=new HibernateTemplate((org.hibernate.SessionFactory)aContext.getBean("sessionFactory"));
   
   EmmLayout aLayout=(EmmLayout)AgnUtils.getFirstResult(aTemplate.find("from EmmLayout where companyID=0 and layoutID=?", new Integer(aForm.getLayout())));
   request.setAttribute("emm.layout", aLayout);
   
   VersionObject latestVersion = ( VersionObject )request.getAttribute( "latestVersion" );
   boolean isLatestVersion = true;
   if ( latestVersion != null && !latestVersion.isLatestVersion() ) {
   	isLatestVersion = false;
   }
%>
<head>
<title><bean:message key="logon.title"/></title>
 <link rel="stylesheet" href="<bean:write name="emm.layout" property="baseUrl" scope="request"/>stylesheet.css">
</head>
<body>
<table cellpadding="2" cellspacing="2" border="0"
 style="width: 100%; text-align: center; height: 100%;" class="right">
  <tbody>
    <tr>
      <td style="vertical-align: top;"><br>
      </td>
      <td style="vertical-align: top;"><br>
      </td>
      <td style="vertical-align: top;"><br>
      </td>
    </tr>
    <tr>
      <td style="vertical-align: top;"><br>
      </td>
      <td
 style="vertical-align: middle; height: 100%; text-align: center;">
      <table border="0" cellspacing="0" cellpadding="0" align="center" class="content">
        <tbody>
          <html:form action="/logon">
          <html:hidden property="action"/>
          <html:hidden property="layout"/>
          <tr>
            <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="request"/>border_01.gif" width="10" height="10" border="0"></td>
            <td>
                <img src="<bean:write name="emm.layout" property="baseUrl" scope="request"/>one_pixel.gif" width="10" height="10" border="0"></td>
            <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="request"/>border_03.gif" width="10" height="10" border="0"></td>
          </tr>
          <tr>
            <td>
                <img src="<bean:write name="emm.layout" property="baseUrl" scope="request"/>one_pixel.gif" width="10" height="10" border="0"></td>
            <td>
            <table border="0" cellspacing="0" cellpadding="2">
		 <tbody>
                <tr>
			<td colspan="2"><center><img src="<bean:write name="emm.layout" property="baseUrl" scope="request"/>logo_ul.gif" border="0" style="margin:10px;"><br><span class="head1"><bean:message key="logon.title"/></span></center><br></td>
	 </tr>
                <% if (!isLatestVersion) { 
                		if ( latestVersion.isSecurityExploit() ) {  %>
                  <tr>
                  	<td colspan="2" align="center"><font color="red"><bean:message key="version.available.securityExploit" /></font></td>
                  </tr>
                  <% 	} else if ( latestVersion.isUpdate() ) { %>
                  <tr>
                  	<td colspan="2" align="center"><font color="red"><bean:message key="version.available.update" /></font></td>
                  </tr>
                  <%	} else { %>
                  <tr>
                  	<td colspan="2" align="center"><font color="red"><%= latestVersion.getServerVersion() %></font></td>
                  </tr>
                  <%	}
                  }
                   %>
                <tr><td colspan="2"><html:errors/></td></tr>
                <tr>
                  <td style="vertical-align: middle;"><bean:message key="logon.username"/>:&nbsp;</td>
                  <td style="vertical-align: middle;"><html:text property="username" size="16" maxlength="20" style="width:200px;"/></td>
                </tr>
                <tr>
                  <td style="vertical-align: middle;"><bean:message key="logon.password"/>:&nbsp;</td>
                  <td style="vertical-align: middle;"><html:password property="password" size="16" maxlength="20" redisplay="false" style="width:200px;"/></td>
                </tr>
                <tr>
                  <td>&nbsp;</td>
                  <td><html:image src="button?msg=logon.login" border="0" property="submit" value="Login"/></td>
                </tr>
              </tbody>
            </table>
            </td>
            <td>
                <img src="<bean:write name="emm.layout" property="baseUrl" scope="request"/>one_pixel.gif" width="10" height="10" border="0"></td>
          </tr>
          <tr>
            <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="request"/>border_07.gif" width="10" height="10" border="0"></td>
            <td>
                <img src="<bean:write name="emm.layout" property="baseUrl" scope="request"/>one_pixel.gif" width="10" height="10" border="0"></td>
            <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="request"/>border_09.gif" width="10" height="10" border="0"></td>
          </tr>
        </html:form>
        </tbody>
      </table>
      <br>
      </td>
      <td style="vertical-align: top;"><br>
      </td>
    </tr>
    <tr>
      <td style="vertical-align: top;"><br>
      </td>
      <td style="vertical-align: top;"><br>
      </td>
      <td style="vertical-align: top;"><br>
      </td>
    </tr>
  </tbody>
</table>
<br>
</body>
</html:html>
