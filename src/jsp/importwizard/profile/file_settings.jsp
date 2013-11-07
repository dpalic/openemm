<%--
  The contents of this file are subject to the Common Public Attribution
  License Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
  http://www.openemm.org/cpal1.html. The License is based on the Mozilla
  Public License Version 1.1 but Sections 14 and 15 have been added to cover
  use of software over a computer network and provide for limited attribution
  for the Original Developer. In addition, Exhibit A has been modified to be
  consistent with Exhibit B.
  Software distributed under the License is distributed on an "AS IS" basis,
  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
  the specific language governing rights and limitations under the License.

  The Original Code is OpenEMM.
  The Original Developer is the Initial Developer.
  The Initial Developer of the Original Code is AGNITAS AG. All portions of
  the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
  Reserved.

  Contributor(s): AGNITAS AG.
  --%>
<%@ page language="java"
         import="org.agnitas.web.ImportProfileAction"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.forms.ImportProfileForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/tags/taglibs.jsp" %>

<controls:panelStart title="import.profile.file.settings"/>
<table>
    <tr>
        <td>
            <div id="separator" class="tooltiphelp">
                &nbsp;<bean:message key="Separator"/>:
            </div>
        </td>
        <td>
            <html:select property="profile.separator" size="1">
                <c:forEach var="separator"
                           items="${importProfileForm.separators}">
                    <html:option value="${separator.intValue}">
                        <bean:message key="${separator.publicValue}"/>
                    </html:option>
                </c:forEach>
            </html:select>
        </td>
        <td width="15px"></td>
        <td>
            <div id="charset" class="tooltiphelp">
                &nbsp;<bean:message key="Charset"/>:
            </div>
        </td>
        <td>
            <html:select property="profile.charset" size="1">
                <c:forEach var="charset"
                           items="${importProfileForm.charsets}">
                    <html:option value="${charset.intValue}">
                        <bean:message key="${charset.publicValue}"/>
                    </html:option>
                </c:forEach>
            </html:select>
        </td>
    </tr>
    <tr>
        <td></td>
    </tr>
    <tr>
        <td>
            <div id="delimiter" class="tooltiphelp">
                &nbsp;<bean:message key="Delimiter"/>:
            </div>
        </td>
        <td>
            <html:select property="profile.textRecognitionChar" size="1">
                <c:forEach var="delimiter"
                           items="${importProfileForm.delimiters}">
                    <html:option value="${delimiter.intValue}">
                        <bean:message key="${delimiter.publicValue}"/>
                    </html:option>
                </c:forEach>
            </html:select>
        </td>
        <td width="25px"></td>
        <td>
            <div id="dateformat" class="tooltiphelp">
                &nbsp;<bean:message key="dateFormat"/>:
            </div>
        </td>
        <td>
            <html:select property="profile.dateFormat" size="1">
                <c:forEach var="dateFormat"
                           items="${importProfileForm.dateFormats}">
                    <html:option value="${dateFormat.intValue}">
                        <bean:message key="${dateFormat.publicValue}"/>
                    </html:option>
                </c:forEach>
            </html:select>
        </td>
    </tr>

</table>
<controls:panelEnd/>

<script type="text/javascript">
    // separator help balloon
    var helpBalloonSeparator = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_1/Separator.xml'
    });
    $('separator').insertBefore(helpBalloonSeparator.icon, $('separator').childNodes[0]);

    // delimiter help balloon
    var helpBalloonDelimiter = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_1/Delimiter.xml'
    });
    $('delimiter').insertBefore(helpBalloonDelimiter.icon, $('delimiter').childNodes[0]);

    // charset help balloon
    var helpBalloonCharset = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_1/Charset.xml'
    });
    $('charset').insertBefore(helpBalloonCharset.icon, $('charset').childNodes[0]);

    // date format help balloon
    var helpBalloonDateFormat = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_1/DateFormat.xml'
    });
    $('dateformat').insertBefore(helpBalloonDateFormat.icon, $('dateformat').childNodes[0]);
</script>