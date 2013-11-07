<%--
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.web.forms.*, java.util.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/ajaxanywhere.tld" prefix="aa" %>




<agn:CheckLogon/>

<agn:Permission token="campaign.show"/> 

<% int tmpCampaignID=0;
   String tmpShortname=new String("");
	  
	if(session.getAttribute("campaignForm")!=null) {
		CampaignForm campaignForm = (CampaignForm) session.getAttribute("campaignForm");	
		tmpCampaignID = campaignForm.getCampaignID();
		tmpShortname = campaignForm.getShortname();
		campaignForm.setAction(CampaignAction.ACTION_STAT);
		

//      tmpCampaignID=((CampaignForm)request.getAttribute("campaignForm")).getCampaignID();
//      tmpShortname=((CampaignForm)request.getAttribute("campaignForm")).getShortname();     
//      tmpIsReady=((CampaignForm)request.getAttribute("campaignForm")).isStatReady();
}

 pageContext.setAttribute("agnSubtitleKey", new String("Campaign")); 
 pageContext.setAttribute("agnSubtitleValue", tmpShortname); 
 pageContext.setAttribute("agnNavigationKey", new String("Campaign"));
 pageContext.setAttribute("agnHighlightKey", new String("Campaign"));
 pageContext.setAttribute("sidemenu_sub_active", new String("NewCampaign")); 


 pageContext.setAttribute("sidemenu_active", new String("Campaigns")); 

 pageContext.setAttribute("agnTitleKey", new String("Campaigns")); 
 pageContext.setAttribute("agnNavHrefAppend", new String("&campaignID="+tmpCampaignID)); 
 %>
<%-- pageContext.setAttribute("agnRefresh", new String("2")); --%>



<%@include file="/header.jsp"%>

<!--  The following part is for reloading the page and as soon as possible move
on to the Result-Page of the Database request. -->
<script>	
	/* calls the submit-procedure */
    function go() {
        document.getElementsByName('campaignForm')[0].submit();
    }

	/* this method returns the zones which shall be reloaded. */
    ajaxAnywhere.getZonesToReload = function () {
        return "loading"
    };

	/* this method is our starting-point */
    ajaxAnywhere.onAfterResponseProcessing = function () {	       	
		if(! ${campaignForm.error } ) {		
    		window.setTimeout("go();", ${campaignForm.refreshMillis});
    	} else {
    		System.err.println("Fehler: " + campaignForm.error);
    	}   		
    }
    
    /* We do not have any loading message */
    ajaxAnywhere.showLoadingMessage = function(){};

	/* call it.*/
	ajaxAnywhere.onAfterResponseProcessing();  
</script>

<aa:zone name="loading" >	<!-- all within these tags will be reloaded, nothing more -->

  <%@include file="/messages.jsp" %>

  <html:form action="/campaign">
    <html:hidden property="action"/>
    <html:hidden property="campaignID"/>
    <html:hidden property="error"/>

<% 
// CampaignForm campaignForm = null;
// if ((CampaignForm)session.getAttribute("campaignForm") != null) {
// 	campaignForm=(CampaignForm)session.getAttribute("campaignForm");		
//	campaignForm.setAction(CampaignAction.ACTION_STAT);
// } 
%>

        <table border="0" cellspacing="0" cellpadding="0" width="400">
            <tr>
                <td>
                    <b>&nbsp;<b>
                </td>
            </tr>
            <tr>
                <td>                	
                    <img border="0" width="44" height="48" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>wait.gif"/>
                </td>
            </tr>
            <tr>
                <td>
                    <b>&nbsp;<b>
                </td>
            </tr>
            <tr>
                <td>
                    <b><bean:message key="StatSplashMessage"/><b>
                </td>
            </tr>           
            <tr> 
                <td>
                    <b>&nbsp;<b>
                </td> 
            </tr>

        </table>

  </html:form>
</aa:zone>      
                     
<%@include file="/footer.jsp"%>
