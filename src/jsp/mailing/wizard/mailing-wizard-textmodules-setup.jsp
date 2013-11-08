<%@ page language="java" import="org.agnitas.beans.Mailing, org.agnitas.web.MailingWizardForm" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.MailingWizardAction" %>
<%@ page import="org.agnitas.web.MailingContentAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>
<% MailingWizardForm aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm");
    Mailing mailing=aForm.getMailing();

    aForm.setDynName("");
%>

<agn:Permission token="mailing.show"/>


<%
// mailing navigation:
    request.setAttribute("sidemenu_active", new String("Mailings"));
    request.setAttribute("sidemenu_sub_active", new String("mailing.New_Mailing"));
    request.setAttribute("agnNavigationKey", new String("MailingNew"));
    request.setAttribute("agnHighlightKey", new String("mailing.New_Mailing"));
    request.setAttribute("agnTitleKey", new String("Mailing"));
    request.setAttribute("agnSubtitleKey", new String("Mailing"));
    request.setAttribute("agnSubtitleValue", mailing.getShortname());
    request.setAttribute( "ACTION_FINISH", MailingWizardAction.ACTION_FINISH);
    request.setAttribute( "ACTION_TEXTMODULE", MailingWizardAction.ACTION_TEXTMODULE);
    request.setAttribute( "ACTION_DELETE_TEXTBLOCK", MailingContentAction.ACTION_DELETE_TEXTBLOCK);
%>
