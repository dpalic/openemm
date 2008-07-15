<%@ page language="java" import="org.agnitas.util.*,org.agnitas.web.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.components.show"/>

<% int tmpMailingID=Integer.parseInt(request.getParameter("mailingID"));
   Company company=((Admin)session.getAttribute("emm.admin")).getCompany();
%>

<html>
<head>
<script type="text/javascript">
<!--
  function updateImg() {
    document.theimage.src=normalizeName(document.selform.imgsel.value);
    return 1;
  }
  
  function submit_image() {
    window.opener.SetUrl(normalizeName(document.selform.imgsel.value));
    window.close();
    return 1;
  }
  
  function normalizeName(fname) {
    if(fname.substr(0,4).toLowerCase()!='http') {
       fname='<%= company.getRdirDomain() %>/image?ci=<%= company.getId() %>&mi=<%= tmpMailingID %>&name='+fname;
    }
    // alert(fname);
    return fname;
  }
-->
</script>
</head>

<body onload="updateImg()">

  <%  String query="from MailingComponent where (comptype=1 or comptype=5) and mailing_id=" + 
                          tmpMailingID + 
                          " and company_id=" + company.getId() + " order by comptype desc, compname";
   %>
            <form name="selform" id="selform" action="">
            <table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
                <tr height="10%" width="100%">
                    <td>
                        <bean:message key="Graphics_Component"/>:&nbsp;
                        <select name="imgsel" id="imgsel" onchange="updateImg()" size="1">
                        <agn:HibernateQuery id="comp" query="<%= query %>" maxRows="100">
                            <option value="${comp.getComponentName()}">${comp.getComponentName()}</option>
                        </agn:HibernateQuery>
                        </select>
   
                        &nbsp;&nbsp;<html:img page="/button?msg=Select" border="0" onclick="submit_image()"/>
                        </form>
                        <hr>
                    </td>
                </tr>
                <tr height="90%" width="100%">
                    <td align="center" valign="center">
                        <img src="images/spacer.gif" id="theimage" border="1">
                    </td>
                </tr>
            </table>
</body>
</html>
