<%@ page language="java" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.cms.web.forms.ContentModuleForm" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<% ContentModuleForm aForm = (ContentModuleForm) session.getAttribute("contentModuleForm"); %>

<html:form action="/cms_contentmodule" focus="cmtId">
    <input type="hidden" name="action" id="action">
    <html:hidden property="contentModuleId" value="0"/>

    <jsp:include page="cms-contentmodule-new-pure.jsp"/>

</html:form>