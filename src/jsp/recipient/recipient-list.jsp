<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	import="org.agnitas.util.AgnUtils"
	buffer="32kb"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript">
<!--
	function parametersChanged(){
		document.getElementsByName('recipientForm')[0].numberOfRowsChanged.value = true;
	}
//-->
</script>
<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
	var prevX = -1;
    var tableID = 'recipient';
    var columnindex = 0;
    var dragging = false;

   document.onmousemove = drag;
   document.onmouseup = dragstop;


    Event.observe(window, 'load', function() {
        <c:if test="${not recipientForm.advancedSearchVisible}">
            $$('.advanced_search_filter_container').invoke('hide');
        </c:if>
    });

    function toggleContainer(container, name){
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');
        if( document.recipientForm[name].value == 'true') {
            document.recipientForm[name].value = 'false';
        }
        else {
            document.recipientForm[name].value = 'true';
        }
        $(container).next().next().toggle();
    }
</script>

<agn:ShowColumnInfo id="colsel" table="<%=AgnUtils.getCompanyID(request)%>" />


	<html:form action="/recipient.do?action=${ACTION_LIST}" styleId="filterForm">
		<html:hidden property="numberOfRowsChanged" />
<html:hidden property="advancedSearchVisible"/>
<html:hidden property="searchPage" value="false"/>

                       <div id="filterbox_container">
    <div id="suche_label"></div>
    <div class="filterbox_form_container">

        <div id="filterbox_top"></div>
         <div id="suchbox_content">
						<div class="search_columns_wrapper">
                            <div class="search_column1">
							<label><bean:message key="Mailinglist" />:</label>

							<html:select styleClass="search_select_short" property="listID" onchange="parametersChanged()">
								<html:option value="0" key="statistic.All_Mailinglists" />
								<agn:ShowTable id="agntbl2"	sqlStatement='<%=new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id="+ AgnUtils.getCompanyID(request))%>' maxRows="100">
									<html:option value="${_agntbl2_mailinglist_id}">${_agntbl2_shortname}</html:option>
								</agn:ShowTable>
							</html:select>

						</div>

						<div class="search_column2">
							<label><bean:message key="target.Target" />:</label>

							<html:select styleClass="search_select_short" property="targetID" onchange="parametersChanged()">
								<html:option value="0" key="default.All" />
								<agn:ShowTable id="agntbl3" sqlStatement='<%=new String( "SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id=" + AgnUtils.getCompanyID(request) + \" AND deleted=0\")%>' maxRows="200">
									<html:option value="${_agntbl3_target_id}">${_agntbl3_target_shortname}</html:option>
								</agn:ShowTable>
							</html:select>

						</div>
						<div class="search_column4">
							<label><bean:message key="recipient.RecipientType" />:</label>

							<html:select styleClass="search_select_short" property="user_type" onchange="parametersChanged()">
								<!-- usr type; 'E' for everybody -->
								<html:option value="E" key="default.All" />
								<html:option value="A" key="recipient.Administrator" />
								<html:option value="T" key="recipient.TestSubscriber" />
								<html:option value="W" key="recipient.NormalSubscriber" />
							</html:select>

						</div>
						<div class="search_column5">
							<label><bean:message key="recipient.RecipientStatus" />:</label>
							<html:select styleClass="search_select_short" property="user_status" onchange="parametersChanged()">
								<!-- usr status; '0' is for everybody -->
								<html:option value="0" key="default.All" />
								<html:option value="1" key="recipient.Active" />
								<html:option value="2" key="recipient.Bounced" />
								<html:option value="3" key="recipient.OptOutAdmin" />
								<html:option value="4" key="recipient.OptOutUser" />
								<html:option value="5" key="recipient.MailingState5"/>
								<html:option value="6" key="recipient.MailingState6"/>
								<html:option value="7" key="recipient.MailingState7"/>
							</html:select>

						</div>
						<div class="search_column6">
                    <div class="filterbox_form_button"><a href="#" onclick="document.recipientForm.submit();"><span><bean:message key="button.OK" /></span></a></div>
                </div>
					</div>
             </div>

        <div id="filterbox_bottom"></div>

        <div id="advanced_search_top"></div>
        <div id="advanced_search_content">
            <div class="advanced_search_toggle toggle_closed" onclick="toggleContainer(this, 'advancedSearchVisible');"><a href="#"><bean:message key="recipient.AdvancedSearch"/></a></div>
            <div class="info_bubble_container">
                <div id="advancedsearch" class="info_bubble">
                    &nbsp;
                </div>
            </div>
            <div class="advanced_search_filter_container">
                <div class="advanced_search_filter">
                    <div class="advanced_search_filter_list">
                        <%--<div class="advanced_search_filter_list_item">--%>
                            <table border="0" cellspacing="2" cellpadding="0">
                                <c:set var="FORM_NAME" value="recipientForm" scope="page" />
                                <%@include file="/rules/rule_add.jsp" %>
                            </table>
                         <%--</div>--%>
                     </div>
                </div>
                <div class="advanced_search_filter_list">
                    <h2><bean:message key="recipient.search"/></h2>
                    <table border="0" cellspacing="2" cellpadding="0">
                       <%@include file="/rules/rules_list.jsp" %>
                    </table>

                    <div class="advanced_search_filter_button">
                        <input type="hidden" id="Update" name="Update" value=""/>
                        <div class="filterbox_form_button"><a href="#" onclick="document.getElementById('Update').value='Update'; document.recipientForm.submit(); return false;"><span><bean:message key="settings.Update"/></span></a></div>
                    </div>
                </div>

            </div>
        </div>
        <div id="advanced_search_bottom"></div>
        </div>
		</div>

<div class="list_settings_container">
    <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
    <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label for="list_settings_length_0">20</label></div>
    <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label for="list_settings_length_1">50</label></div>
    <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label for="list_settings_length_2">100</label></div>
    <logic:iterate collection="${recipientForm.columnwidthsList}" indexId="i" id="width">
        <html:hidden property="columnwidthsList[${i}]"/>
    </logic:iterate>
</div>

	</html:form>

			<display:table class="list_table"
				pagesize="${recipientForm.numberofRows}" id="recipient"
				name="recipientList" sort="external" excludedParams="*"
				requestURI="/recipient.do?action=${ACTION_LIST}&__fromdisplaytag=true"
				partialList="true" size="${recipientList.fullListSize}">
				<display:column class="name" headerClass="head_name"
					titleKey="recipient.Salutation">
					<bean:message key="recipient.gender.${recipient.gender}.short" />
				</display:column>
				<display:column class="name" headerClass="head_name"
					property="firstname" titleKey="recipient.Firstname" sortable="true" />
				<display:column class="name" headerClass="head_name"
					property="lastname" titleKey="recipient.Lastname" sortable="true" />
				<display:column class="name" headerClass="head_name"
					property="email" titleKey="mailing.E-Mail" sortable="true"
					paramId="recipientID" paramProperty="customerID"
					url="/recipient.do?action=${ACTION_VIEW}" />
				<display:column class="edit" >
					   <agn:ShowByPermission token="recipient.view">
             <html:link styleClass="mailing_edit" titleKey="recipient.RecipientEdit" page="/recipient.do?action=${ACTION_VIEW}&recipientID=${recipient.customerID}"> </html:link>
        </agn:ShowByPermission>
        <agn:ShowByPermission token="recipient.delete">
             <html:link styleClass="mailing_delete" titleKey="recipient.RecipientDelete" page="/recipient.do?action=${ACTION_CONFIRM_DELETE}&recipientID=${recipient.customerID}"> </html:link>
        </agn:ShowByPermission>
				</display:column>

	</display:table>
		<script type="text/javascript">
    table = document.getElementById('recipient');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#recipient tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>


<script language="javascript">

    if ($('advancedsearch')) {
        var helpBalloonAdvancedsearch= new HelpBalloon({
            dataURL: 'help_${helplanguage}/recipient/AdvancedSearchMsg.xml'
        });
        $('advancedsearch').insertBefore(helpBalloonAdvancedsearch.icon, $('advancedsearch').childNodes[0]);
    }

    /*addTitleToOptions();*/

</script>