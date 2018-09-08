<%@ page errorPage="/error.jsp" %>
<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="action_button">
    <a href="#" onclick="document.autoImportForm.submit(); return false;">
        <span><bean:message key="button.Save"/></span>
    </a>
</div>
<c:if test="${autoImportForm.autoImportId != 0}">
    <div class="action_button">
        <a href='<html:rewrite page="/autoimport.do?method=delete&autoImportId=${autoImportForm.autoImportId}"/>'>
            <span><bean:message key="button.Delete"/></span>
        </a>
    </div>

    <%--@todo: this button should be removed--%>
    <div class="action_button">
        <a href='<html:rewrite page="/autoimport.do?method=doimport&autoImportId=${autoImportForm.autoImportId}"/>'>
            <span><bean:message key="import.UploadSubscribers"/></span>
        </a>
    </div>

    <c:if test="${not autoImportForm.autoImport.active}">
        <div class="action_button">
            <a href='<html:rewrite page="/autoimport.do?method=changeActiveStatus&activeStatus=true&autoImportId=${autoImportForm.autoImportId}"/>'>
                <span><bean:message key="btnactivate"/></span>
            </a>
        </div>
    </c:if>

    <c:if test="${autoImportForm.autoImport.active}">
        <div class="action_button">
            <a href='<html:rewrite page="/autoimport.do?method=changeActiveStatus&activeStatus=false&autoImportId=${autoImportForm.autoImportId}"/>'>
                <span><bean:message key="btndeactivate"/></span>
            </a>
        </div>
    </c:if>
</c:if>