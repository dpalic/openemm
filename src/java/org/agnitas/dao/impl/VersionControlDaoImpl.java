/*******************************************************************************
 * The contents of this file are subject to the OpenEMM Public License
 * Version 1.1 ("License"); You may not use this file except in compliance with
 * the License.
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
 ******************************************************************************/
package org.agnitas.dao.impl;

import org.agnitas.beans.VersionObject;
import org.agnitas.beans.impl.VersionObjectImpl;
import org.agnitas.dao.VersionControlDao;
import org.agnitas.util.AgnUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

public class VersionControlDaoImpl implements VersionControlDao {
	private static final int CONNECTION_TIMEOUT = 5000;
	private static final String URL = AgnUtils.getDefaultValue( "system.updateserver" ) + "/version/current_version.html";
	private static final String VERSION_KEY = "currentVersion";
	private static final long MAX_AGE = 60 * 60 * 1000L; // one hour cache time
	private static VersionObject versionObject = null;
	private long lastRefresh = 0L;

	/* (non-Javadoc)
	 * @see org.agnitas.dao.VersionControlDao#getServerVersion()
	 */
	public VersionObject getServerVersion(String currentVersion, String referrer ) {
		checkRefresh( currentVersion, referrer );
		return versionObject;
	}

	private void checkRefresh(String currentVersion, String referrer) {
		if ( versionObject == null || System.currentTimeMillis() - lastRefresh > MAX_AGE ) {
			String serverVersion = fetchServerVersion(currentVersion, referrer);
			versionObject = new VersionObjectImpl( currentVersion, serverVersion );
			lastRefresh  = System.currentTimeMillis();
		}
	}

	private String fetchServerVersion(String currentVersion, String referrer) {
		HttpClient client = new HttpClient();
		client.setConnectionTimeout(CONNECTION_TIMEOUT);
		HttpMethod method = new GetMethod(URL);
		method.setRequestHeader( "referer", referrer );
		NameValuePair[] queryParams = new NameValuePair[1];
		queryParams[0] = new NameValuePair( VERSION_KEY, currentVersion );
		method.setQueryString( queryParams );
        method.setFollowRedirects(true);
        String responseBody = null;
        
        try{
            client.executeMethod(method);
            responseBody = method.getResponseBodyAsString();
        } catch (Exception he) {
            AgnUtils.logger().error("Http error connecting to '" + URL + "'");
            AgnUtils.logger().error(he.getMessage());
        }

        //clean up the connection resources
        method.releaseConnection();
		return responseBody;
	}

}
