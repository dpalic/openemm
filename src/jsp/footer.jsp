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
 --%><%@ page language="java" import="org.agnitas.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
              <br><div align="right"><a href="#"><img onclick="window.open('<%= AgnUtils.getHelpURL(request) %>','help1','width=310,height=600,left=0,top=0,scrollbars=yes');" src="images/emm/help.gif" alt="help" border="0"></a></div></td>
              <td class="content"><img src="images/emm/one_pixel.gif" alt="spacer" width="10" height="10" border="0"></td>
            </tr>
            <tr>
              <td><img src="images/emm/border_07.gif" alt="lower left" width="10" height="10" border="0"></td>
              <td class="content"><img src="images/emm/one_pixel.gif" alt="spacer" width="10" height="10" border="0"></td>
              <td><img src="images/emm/border_09.gif" alt="lower right" width="10" height="10" border="0"></td>
            </tr>
          </table>
        </td>
        <td class="right"> <%
             if(mainmenu != null && !mainmenu.equals("none")) {
                 %><img align="right" width="102" height="164" src="images/emm/sub_icons/sub_<%= mainmenu %>_2.png" alt="sub_<%= mainmenu %>"><%
             }
          %>
        </td>
        </tr>
        <tr>
      <td colspan="3" class="right" align="center"><a href="http://www.agnitas.org/" target="_blank"><span class="copyright"><bean:message key="Copyright"/></span></a>
      </td>
      </tr>
    </table>
  </body>
</html>
