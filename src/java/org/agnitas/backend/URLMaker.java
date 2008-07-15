/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/
package	org.agnitas.backend;

import	org.agnitas.util.UIDImpl;

/** Create redirect URLs
 */
public class URLMaker extends UIDImpl {
    /** Reference to configuration */
    private Data data = null;
    
    /** Constructor
     * @param data reference to configuration
     */
    public URLMaker (Data data) throws Exception {
        super (data.company_id, data.mailing_id, data.password);
        this.data = data;
    }
    
    /** Create a URL created out of base and given paramter
     * @param base the base url
     * @param url (optional) url id
     * @return the URL
     */
    public String makeURL (String base, long url) throws Exception {
        setURLID (url);
        return base + "uid=" + makeUID ();
    }

    /** Create a profile URL
     * @return the URL
     */
    public String profileURL () throws Exception {
        return makeURL (data.profileURL, 0);
    }
    
    /** Create an unsubscribe URL
     * @return the URL
     */
    public String unsubscribeURL () throws Exception {
        return makeURL (data.unsubscribeURL, 0);
    }
    
    /** Create an auto URL
     * @return the URL
     */
    public String autoURL (long url) throws Exception {
        return makeURL (data.autoURL, url);
    }
    
    /** Create an onepixellog URL
     * @return the URL
     */
    public String onepixelURL () throws Exception {
        return makeURL (data.onePixelURL, 0);
    }
}
