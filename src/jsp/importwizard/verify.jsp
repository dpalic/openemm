<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, java.util.*, java.text.*, org.agnitas.web.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="wizard.import"/>

<% pageContext.setAttribute("sidemenu_active", new String("Recipient")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("csv_upload")); %>
<% pageContext.setAttribute("agnTitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("UploadSubscribers")); %>
<% pageContext.setAttribute("agnNavigationKey", new String("subscriber_import")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("ImportWizard")); %>

<%@include file="/header.jsp"%>

<% int tmpOffset=0;
int tmpOffset2=0;
int tmpSize=0;
if(session.getAttribute("importWizardForm")!=null) {
    tmpOffset=((ImportWizardForm)session.getAttribute("importWizardForm")).getPreviewOffset();
    tmpSize=((ImportWizardForm)session.getAttribute("importWizardForm")).getParsedContent().size();
}

%>

<html:form action="/importwizard" enctype="multipart/form-data">
    <html:hidden property="action"/>

    <b><font color=#73A2D0><bean:message key="ImportWizStep_4_of_7"/></font></b>
    <br>

    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr><td colspan="3"><span class="head3"><bean:message key="csv_analysis"/>:<br></span></td></tr>
        <tr><td colspan="3"><hr></td></tr>
        <tr><td><b><bean:message key="csv_used_column"/></b>&nbsp;&nbsp;</td><td><b><bean:message key="csv_unused_column_csv"/></b>&nbsp;&nbsp;</td><td><b><bean:message key="csv_unused_column_db"/></b></td></tr>
        <tr valign="top"><td>
            <logic:iterate id="element" name="importWizardForm" property="csvAllColumns" scope="session" type="CsvColInfo">
                <logic:equal name="element" property="active" value="true">
                    <bean:write name="element" property="name"/><br>
                </logic:equal>
            </logic:iterate>
        </td>
        <td>
            <logic:iterate id="element" name="importWizardForm" property="csvAllColumns" scope="session" type="CsvColInfo">
                <logic:notEqual name="element" property="active" value="true">
                    <bean:write name="element" property="name"/><br>
                </logic:notEqual>
            </logic:iterate>
        </td>
        <td>
            <logic:iterate id="hashelement" name="importWizardForm" property="dbAllColumns" scope="session">
                <bean:define id="element" name="hashelement" property="value"/>
                <logic:notEqual name="element" property="active" value="true">
                    <bean:write name="element" property="name"/><br>
                </logic:notEqual>
            </logic:iterate>
        </td></tr>

        <tr><td colspan="3"><hr><span class="head3"><bean:message key="Preview"/>:</span></td></tr>
        <tr>
            <td colspan="3">
                <table border="0" cellspacing="0" cellpadding="2" width="100%">
                    <tr>
                        <logic:iterate id="element" name="importWizardForm" property="csvAllColumns" scope="session" type="CsvColInfo">
                            <logic:equal name="element" property="active" value="true">
                                <td><b><bean:write name="element" property="name"/></b></td>
                            </logic:equal>
                        </logic:iterate>
                        <td>&nbsp;</td>
                    </tr>

                    <%
                    Object leElement=null;
                    Class leClass=null;
                    DateFormat aFormatter=DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
                    %>
                    <logic:iterate id="element2" indexId="element2idx" name="importWizardForm" offset="<%= Integer.toString(tmpOffset) %>" length="5" property="parsedContent" scope="session">
                        <tr>
                            <logic:iterate id="element3" name="element2" scope="page">
                                <td><%  leElement=pageContext.getAttribute("element3");
                                String value=null;
                                if(leElement!=null) {

                                    leClass=leElement.getClass();
                                    if(leClass.getName().equals("java.lang.String")) {
                                        value=SafeString.getHTMLSafeString((String)leElement);
                                    } else if(leClass.getName().equals("java.lang.Double")) {
                                        value=""+((Double)leElement).longValue();
                                    }
                                    if(leClass.getName().equals("java.util.Date")) {
                                        value=aFormatter.format((java.util.Date)leElement);
                                    }
/*
                                    if(leClass.getName().equals("java.lang.String")) {
                                        out.print("<input name=\"dummy\" type=\"text\" size=\"13\" value=\""+SafeString.getHTMLSafeString((String)leElement)+"\" readonly>");
                                    }
                                    if(leClass.getName().equals("java.lang.Double")) {
                                        out.print("<input name=\"dummy\" type=\"text\" size=\"8\" value=\""+((Double)leElement).longValue()+"\" readonly>");
                                    }
                                    if(leClass.getName().equals("java.util.Date")) {
                                        out.print("<input name=\"dummy\" type=\"text\" size=\"13\" value=\""+aFormatter.format((java.util.Date)leElement)+"\" readonly>");
                                    }
*/
                                } else {
                                    value="";
                                } %>
                                <input name="dummy" type="text" size="13" value="<%= value %>" readonly>
                                </td>
                            </logic:iterate>
                            <logic:equal name="element2idx" value="<%= Integer.toString(tmpOffset) %>">
                                <% tmpOffset2=tmpOffset-5; if(tmpOffset2<0) tmpOffset2=0; %>
                                <td valign="top"><html:link page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_PREVIEW_SCROLL + "&previewOffset="+tmpOffset2) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>button_up.gif" border="0"></html:link></td>
                            </logic:equal>
                            <logic:equal name="element2idx" value="<%= Integer.toString(tmpOffset+1) %>">
                                <% tmpOffset2=tmpOffset+5; if(tmpOffset2>=tmpSize) tmpOffset2=tmpSize-5; if(tmpOffset2<0) tmpOffset2=0; %>
                                <td rowspan="4" valign="bottom"><html:link page="<%= new String("/importwizard.do?action=" + ImportWizardAction.ACTION_PREVIEW_SCROLL + "&previewOffset="+tmpOffset2) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>button_down.gif" border="0"></html:link></td>
                            </logic:equal>

                        </tr>
                    </logic:iterate>

                </table>

            </td>
        </tr>

    </table>
    <hr>
    <html:image src="button?msg=Back"  border="0" property="verify_back" value="verify_back"/>
    &nbsp;&nbsp;&nbsp;
    <html:image src="button?msg=Proceed" border="0"/>

</html:form>

<%@include file="/footer.jsp"%>
