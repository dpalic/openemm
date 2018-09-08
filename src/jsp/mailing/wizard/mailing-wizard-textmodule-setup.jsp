<%@ page language="java" import="org.agnitas.beans.DynamicTag, org.agnitas.beans.Mailing, org.agnitas.util.AgnUtils, org.agnitas.web.MailingWizardForm" contentType="text/html; charset=utf-8"  errorPage="/error.jsp" %>
<%@ page import="org.agnitas.web.MailingWizardAction" %>
<%@ page import="org.agnitas.web.MailingContentAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>
 <%
    MailingWizardForm aForm=null;
    aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm");
     request.setAttribute("aForm", aForm);
    Mailing mailing=aForm.getMailing();
    request.setAttribute("mailing",mailing);
    Integer len=((DynamicTag)mailing.getDynTags().get(aForm.getDynName())).getDynContent().size();
     request.setAttribute("len",len);

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
    request.setAttribute("ACTION_TEXTMODULE_ADD", MailingWizardAction.ACTION_TEXTMODULE_ADD);
    request.setAttribute("ACTION_TEXTMODULE_SAVE", MailingWizardAction.ACTION_TEXTMODULE_SAVE);
    request.setAttribute("ACTION_FINISH", MailingWizardAction.ACTION_FINISH);
    request.setAttribute("ACTION_TEXTMODULE",MailingWizardAction.ACTION_TEXTMODULE);
%>
<c:set var="agnHelpKey" value="newMailingWizard" scope="request" />
