<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
         
<% String aNameBase=null; 
                   String aNamePart=null;
                   String aName=null;
                   String aktName=new String("");
                %>
                
<% int tmpMailingID=0;
   MailingBaseForm aForm=null;
   String tmpShortname=new String("");
   if((aForm=(MailingBaseForm)session.getAttribute("mailingBaseForm"))!=null) {
      tmpMailingID=((MailingBaseForm)session.getAttribute("mailingBaseForm")).getMailingID();
      tmpShortname=((MailingBaseForm)session.getAttribute("mailingBaseForm")).getShortname();
   }
   if(aForm.isIsTemplate()) {
       aForm.setShowTemplate(true);
   }
   String permToken=null;
   boolean showTargetMode=false;
   boolean showNeedsTarget=false;
   if(aForm.getTargetGroups()!=null) {
       if(aForm.getTargetGroups().size()>=2) {
           showTargetMode=true;
       }
   }
%>

<agn:ShowByPermission token="mailing.needstarget">
<%  showNeedsTarget=true;  %>
</agn:ShowByPermission>
<table border="0" cellspacing="0" cellpadding="0" width="100%">

    <tr>
        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>tagw_left.gif" border="0"></td>
        <td class="tag"><b><bean:message key="General"/></b></td>
        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>tagw_right.gif" border="0"></td>
        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="20" height="10" border="0"></td>
        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>tagw_left.gif" border="0"></td>
        <td class="tag"><b><bean:message key="Targets"/></b></td>
        <td><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>tagw_right.gif" border="0"></td>
    </tr>

    <tr>
    <td bgcolor="#EBEBEB"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="1" height="1" border="0"></td>
    <td height="100%" valign="top" bgcolor="#EBEBEB">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
            <td><bean:message key="Mailinglist"/>:&nbsp;</td>
            <td> 
                <html:select property="mailinglistID" size="1" disabled="<%= aForm.isWorldMailingSend() %>">
                    <agn:ShowTable id="agntbl2" sqlStatement="<%= new String("SELECT mailinglist_id, shortname FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+ " ORDER BY shortname") %>" maxRows="100">
                        <html:option value="<%= (String)(pageContext.getAttribute("_agntbl2_mailinglist_id")) %>"><%= pageContext.getAttribute("_agntbl2_shortname") %></html:option>
                    </agn:ShowTable>
                </html:select>
            </td>
            </tr>
            <agn:ShowByPermission token="campaign.show">
                <tr>
                <td><bean:message key="Campaign"/>:&nbsp;</td>
                <td>
                    <html:select property="campaignID">
                        <html:option value="0"><bean:message key="NoCampaign"/></html:option>
                        <agn:ShowTable id="agntbl55" sqlStatement="<%= new String("SELECT campaign_id, shortname FROM campaign_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+ " ORDER BY shortname") %>" maxRows="500">
                            <html:option value="<%= (String)(pageContext.getAttribute("_agntbl55_campaign_id")) %>"><%= pageContext.getAttribute("_agntbl55_shortname") %></html:option>
                        </agn:ShowTable>
                    </html:select>&nbsp;
                </td>
                </tr>
            </agn:ShowByPermission>

 
            <tr>
            <td><agn:ShowByPermission token="mailing.show.types"><bean:message key="Mailing_Type"/>:&nbsp;</agn:ShowByPermission></td>
            <td>
                <agn:ShowByPermission token="mailing.show.types">
                    <html:select property="mailingType" size="1" disabled="<%= aForm.isWorldMailingSend() %>">
                        <html:option value="<%= Integer.toString(Mailing.TYPE_NORMAL) %>"><bean:message key="Normal_Mailing"/></html:option>
                        <html:option value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>"><bean:message key="Event_Mailing"/></html:option>
                        <html:option value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>"><bean:message key="Rulebased_Mailing"/></html:option>
                    </html:select>
                </agn:ShowByPermission>
            </td>
            </tr>

            <agn:ShowByPermission token="action.archive">
                <tr> 
                    <td><bean:message key="mailing.archived"/>:&nbsp;</td>
                    <td><html:checkbox property="archived"/></td>
                </tr>
            </agn:ShowByPermission>    

            <logic:equal name="mailingBaseForm" property="isTemplate" value="false">

                    <logic:equal name="mailingBaseForm" property="mailingID" value="0">
                    
                        <logic:equal name="mailingBaseForm" property="copyFlag" value="false">
                            <tr> 
                            <td><bean:message key="Template"/>:&nbsp;</td>
                            <td> 
                                <html:select property="templateID" onchange="document.mailingBaseForm.action.value=7; document.mailingBaseForm.submit();">
                                    <html:option value="0"><bean:message key="No_Template"/></html:option>
                                    <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT mailing_id, shortname FROM mailing_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" AND is_template=1 AND deleted=0 ORDER BY shortname") %>" maxRows="500">
                                        <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_mailing_id")) %>"><%= pageContext.getAttribute("_agntbl3_shortname") %></html:option>
                                    </agn:ShowTable> 
                                </html:select>&nbsp;
                            </td>
                            </tr>
                        </logic:equal>
                        
                        <logic:equal name="mailingBaseForm" property="copyFlag" value="true">
                            <tr> 
                            <td><bean:message key="Template"/>:&nbsp;</td>
                            <td>
                                    <% boolean template = false; %>
                                <agn:ShowTable id="agntbl44" sqlStatement="<%= new String("SELECT shortname FROM mailing_tbl WHERE mailing_id=" + ((MailingBaseForm)session.getAttribute("mailingBaseForm")).getTemplateID() + " AND company_id=" + AgnUtils.getCompanyID(request)) %>" maxRows="500">
                                       <% if(((String)(pageContext.getAttribute("_agntbl44_shortname"))).compareTo("")!=0) { 
                                            template = true; %>
                                         <%= (String)(pageContext.getAttribute("_agntbl44_shortname")) %>
                                       <% } %>
                                </agn:ShowTable>
                                       <% if(!template) { %>
                                <bean:message key="No_Template"/>
                                       <% } %>
                            </td>
                            </tr>
                        </logic:equal>
                        
                    </logic:equal>

                    <logic:notEqual name="mailingBaseForm" property="mailingID" value="0">
                        <tr> 
                        <td><bean:message key="Template"/>:&nbsp;</td>
                        <td>
                                <% boolean template = false; %>
                            <agn:ShowTable id="agntbl44" sqlStatement="<%= new String("SELECT shortname FROM mailing_tbl WHERE mailing_id=" + ((MailingBaseForm)session.getAttribute("mailingBaseForm")).getTemplateID() + " AND company_id=" + AgnUtils.getCompanyID(request)) %>" maxRows="500">
                                   <% if(((String)(pageContext.getAttribute("_agntbl44_shortname"))).compareTo("")!=0) { 
                                        template = true; %>
                                     <%= (String)(pageContext.getAttribute("_agntbl44_shortname")) %>
                                   <% } %>
                            </agn:ShowTable>
                                   <% if(!template) { %>
                            <bean:message key="No_Template"/>
                                   <% } %>
                        </td>
                        </tr>
                    </logic:notEqual>
            
            </logic:equal>            
            
        </table>
    </td>
    <td bgcolor="#EBEBEB"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="1" height="1" border="0"></td>
    
    <td>
        &nbsp;
    </td>

    <td bgcolor="#EBEBEB"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="1" height="1" border="0"></td>
    <td height="100%" width="100%" valign="top" bgcolor="#EBEBEB">
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
           <tr width="100%">
            <td colspan="2">
                <logic:present name="mailingBaseForm" property="targetGroups">
                <logic:iterate name="mailingBaseForm" property="targetGroups" id="aTarget">
                   <agn:ShowTable id="agntbl3a" sqlStatement="<%= new String("SELECT target_shortname FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" AND target_id="+pageContext.getAttribute("aTarget")) %>" maxRows="2">
                        <%= pageContext.getAttribute("_agntbl3a_target_shortname") %>&nbsp;<logic:equal name="mailingBaseForm" property="worldMailingSend" value="false"><html:image src="<%= new String(((EmmLayout)session.getAttribute("emm.layout")).getBaseUrl() + "delete.gif") %>" border="0" property="<%= new String("removetarget"+pageContext.getAttribute("aTarget")) %>"/></logic:equal><br>
                    </agn:ShowTable>
                </logic:iterate>
                </logic:present>
                <logic:notPresent name="mailingBaseForm" property="targetGroups">
                   <bean:message key="All_Subscribers"/><br>
                </logic:notPresent>
                <logic:equal name="mailingBaseForm" property="worldMailingSend" value="false">
                <html:select property="targetID" size="1">
                    <html:option value="0">---</html:option>
                    <agn:ShowTable id="agntbl3" sqlStatement="<%= new String("SELECT target_id, target_shortname FROM dyn_target_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" ORDER BY target_shortname") %>" maxRows="500">
                        <% if(aForm.getTargetGroups()!=null && !aForm.getTargetGroups().contains(Integer.valueOf((String)(pageContext.getAttribute("_agntbl3_target_id"))))) {
                        %>
                        <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                        <% } %>
                        <% if(aForm.getTargetGroups()==null) {
                        %>
                        <html:option value="<%= (String)(pageContext.getAttribute("_agntbl3_target_id")) %>"><%= pageContext.getAttribute("_agntbl3_target_shortname") %></html:option>
                        <% } %>

                    </agn:ShowTable>
                </html:select>
                &nbsp;<html:image src="button?msg=Add" border="0" property="addtarget" value="addtarget"/>
                </logic:equal>
                <% if(showTargetMode) { %>
                    <br><input type="hidden" name="__STRUTS_CHECKBOX_targetMode" value="0"/>
                    <html:checkbox property="targetMode" value="1" disabled="<%= aForm.isWorldMailingSend() %>">&nbsp;<bean:message key="mailing.targetmode.and"/></html:checkbox>
                    <% if(aForm.isWorldMailingSend()) { %>
                        <html:hidden property="targetMode"/>
                    <% } %>
                <% } else { %>
                    <html:hidden property="targetMode"/>
                <% } %>
                <% if(showNeedsTarget) { %>
                    <input type="hidden" name="__STRUTS_CHECKBOX_needsTarget" value="false"/>
                    <br><html:checkbox property="needsTarget" disabled="<%= (aForm.isWorldMailingSend() || (aForm.getMailingType()==Mailing.TYPE_DATEBASED)) %>">&nbsp;<bean:message key="mailing.needsTarget"/></html:checkbox>
                    <% if(aForm.isWorldMailingSend() || (aForm.getMailingType()==Mailing.TYPE_DATEBASED)) { %>
                        <html:hidden property="needsTarget"/>
                    <% } %>
                <% } else { %>
                    <html:hidden property="needsTarget"/>
                <% } %>
            </td>
            </tr>
            

        </table>    
    </td>
    <td bgcolor="#EBEBEB"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="1" height="1" border="0"></td>
    </tr>

    
</table>
