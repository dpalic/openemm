<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*"
         contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/autoimport">
    <input type="hidden" name="method" value="deleteconfirm" id="action_method" />

    <div class="new_mailing_start_description">
        <bean:message key="autoImport.delete.question"/>
    </div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="kill" name="kill" value=""/>
                <div class="big_button">
                    <a href="#" onclick="document.autoImportForm.submit(); return false;">
                        <span><bean:message key="button.Delete"/></span>
                    </a>
                </div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button">
                    <c:if test="${empty fromListPage}">
                        <c:set var="cancelUrl" value="/autoimport.do?method=view&autoImportId=${autoImportForm.autoImportId}"/>
                    </c:if>
                    <c:if test="${not empty fromListPage}">
                        <c:set var="cancelUrl" value="/autoimport.do?method=list"/>
                    </c:if>
                    <a href='<html:rewrite page="${cancelUrl}" />'>
                        <span><bean:message key="button.Cancel"/></span>
                    </a>
                </div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>
</html:form>


