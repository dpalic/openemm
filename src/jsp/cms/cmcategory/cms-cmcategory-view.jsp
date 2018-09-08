<%@ page language="java" contentType="text/html; charset=utf-8" errorPage="/error.jsp" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/cms_cmcategory" focus="name">
    <html:hidden property="cmcId"/>
    <input type="hidden" name="action" id="action">
    <input type="hidden" name="save" value="" id="save">

    <jsp:include page="cms-cmcategory-view-pure.jsp"/>

</html:form>