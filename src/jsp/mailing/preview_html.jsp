<%@ page language="java" contentType="text/html; charset=utf-8" import="org.apache.struts.action.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<agn:CheckLogon/>
<html:errors/>
<bean:write name="mailingSendForm" property="preview" filter="false"/>
