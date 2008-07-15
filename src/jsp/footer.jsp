<%@ page language="java" import="org.agnitas.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
              <br><div align="right"><a href="#"><img onclick="window.open('help_<%= AgnUtils.getAdmin(request).getAdminLang() %>/index.htm','help1','width=310,height=600,left=0,top=0,scrollbars=yes');" src="images/emm/help.gif" alt="help" border="0"></a></div></td>
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
                 %><img align="right" width="102" height="102" src="images/emm/sub_icons/sub_<%= mainmenu %>.gif" alt="sub_<%= mainmenu %>"><%
             }
          %>
        </td>
        </tr>
        <tr>
      <td colspan="3" class="right" align="center"><a href="http://www.agnitas.org/"><span class="copyright"><bean:message key="Copyright"/></span></a>
      </td>
      </tr>
    </table>
  </body>
</html>
