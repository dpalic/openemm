<%-- checked --%>
<%@ page language="java" import="org.agnitas.util.AgnUtils" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>


<html:form action="/recipient_stats" method="post">
    <html:hidden property="action"/>

    <div class="mailing_name_box_container">
        <div class="mailing_name_box_top"></div>
        <div class="mailing_name_box_content">
            <div class="mailing_name_box_left_column" style="float: none;">

                <div class="stat_recipient_form_item">
                    <label><bean:message key="Mailinglist"/>:</label>
                    <html:select property="mailingListID" size="1">
                        <html:option value="0"><bean:message key="statistic.All_Mailinglists"/></html:option>
                        <agn:ShowTable id="agntbl1"
                                       sqlStatement='<%= new String(\"SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id=\"+AgnUtils.getCompanyID(request)+ \" ORDER BY shortname\") %>'>
                            <html:option
                                    value='<%= (String)pageContext.getAttribute(\"_agntbl1_mailinglist_id\") %>'><%= pageContext.getAttribute("_agntbl1_shortname") %>
                            </html:option>
                        </agn:ShowTable>
                    </html:select>
                </div>

                <div class="stat_recipient_form_item">
                    <label><bean:message key="target.Target"/>:</label>
                    <html:select property="targetID" size="1">
                        <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                        <agn:ShowTable id="agntbl2"
                                       sqlStatement='<%= new String(\"SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id=\" + AgnUtils.getCompanyID(request)+ \" AND deleted=0 ORDER BY target_shortname\") %>'>
                            <html:option
                                    value='<%= (String) pageContext.getAttribute(\"_agntbl2_target_id\") %>'><%= pageContext.getAttribute("_agntbl2_target_shortname") %>
                            </html:option>
                        </agn:ShowTable>
                    </html:select>
                </div>
            </div>

            <div class="maildetail_button_container" style="margin-left:0px; width:870px;">
                <div class="maildetail_button">
                    <a href="#" onclick="document.recipientStatForm.submit();">
                        <span><bean:message key="button.Submit"/></span>
                    </a>
                </div>
            </div>
        </div>
        <div class="mailing_name_box_bottom"></div>
    </div>

</html:form>