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

package org.agnitas.taglib;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;

import org.agnitas.util.AgnUtils;

/**
 *  Display the navigation for a page. the navigation is specified by a
 *  properties file in org.agnitas.util.properties.navigation in the format
 *  token_i, href_i, msg_i, where i determines the order in which navigation is
 *  displayed.
 *
 */

public class ShowNavigationTag extends BodyBase {

    private static final long serialVersionUID = 4835357820387405302L;
	private String navigation;
    private String highlightKey ;
    private String prefix=null;
    private PropertyResourceBundle resNav;
    private int navIndex;
    private int navNumber;

    /**
     * Setter for property navigation.
     *
     * @param myNavigation New value of property navigation.
     */
    public void setNavigation(String myNavigation) {
        navigation=myNavigation;
    }

    /**
     * Setter for property highlightKey.
     *
     * @param myHighlightKey New value of property highlightKey.
     */
    public void setHighlightKey(String myHighlightKey) {
        highlightKey=myHighlightKey;
    }

    /**
     * Setter for property prefix.
     *
     * @param myPrefix New value of property prefix.
     */
    public void setPrefix(String myPrefix) {
        prefix=myPrefix;
    }

    /**
     * Resets navigation path.
     *
     */
    public int	doStartTag() throws JspException{
        if(prefix==null) {
            prefix=new String("");
        }
        String resNavPath = "navigation" + "." + navigation;
        try {
            resNav=(PropertyResourceBundle)ResourceBundle.getBundle(resNavPath);
            Enumeration navKeys=resNav.getKeys();
            navIndex=0;
            navNumber=0;
            while( navKeys.hasMoreElements() ){
                ++navNumber;
                navKeys.nextElement();
            }
            navNumber/=3;
        } catch (MissingResourceException mre){
            AgnUtils.logger().error(mre.getMessage());
            return SKIP_BODY;
        }
        return doAfterBody();
    }

    /**
     * Sets attributes for pagecontext.
     */
    public int doAfterBody() throws JspException {

        if(navIndex < navNumber) {
            try {
                ++navIndex;
                String token=resNav.getString("token_" + navIndex);  // the keys are specified in properties files
                String href=resNav.getString("href_"  + navIndex);  // and must be token_1, href_1, msg_1
                String navMsg=resNav.getString("msg_"   + navIndex);
                if( navMsg.equals(highlightKey) ){
                    pageContext.setAttribute(prefix+"_navigation_switch",  new String("on"));
                    pageContext.setAttribute(prefix+"_navigation_isHighlightKey", new Boolean(true));
                } else {
                    pageContext.setAttribute(prefix+"_navigation_switch",  new String("off"));
                    pageContext.setAttribute(prefix+"_navigation_isHighlightKey", new Boolean(false));
                }

                pageContext.setAttribute(prefix+"_navigation_token", new String( token.trim()));
                pageContext.setAttribute(prefix+"_navigation_href",  new String( href.trim()));
                pageContext.setAttribute(prefix+"_navigation_navMsg",new String( navMsg.trim()));
                return  EVAL_BODY_BUFFERED;
            } catch (Exception e) {
                AgnUtils.logger().error(e.getMessage());
                return SKIP_BODY;
            }
        } else {
            return SKIP_BODY;
        }
    }
}
