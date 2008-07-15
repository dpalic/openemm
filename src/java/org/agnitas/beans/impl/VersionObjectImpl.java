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
package org.agnitas.beans.impl;

import org.agnitas.beans.VersionObject;
import org.apache.commons.lang.StringUtils;

public class VersionObjectImpl implements VersionObject {

	private static final char TOKEN_SECURITY = 's';
	private static final char TOKEN_UPDATE = 'u';
	private final String serverVersion;
	private boolean latestVersion = true;
	private final boolean securityExploit;
	private final boolean update;

	/** creates new VersionObjectImpl **/
	public VersionObjectImpl(String currentVersion, String serverVersion) {
		if ( serverVersion != null ) {
			this.latestVersion = StringUtils.equalsIgnoreCase( currentVersion, serverVersion.trim() );
				
			if ( serverVersion.trim().endsWith( String.valueOf( TOKEN_SECURITY ) ) ) {
				securityExploit = true;
				update = false;
				this.serverVersion = serverVersion.substring( 0, serverVersion.lastIndexOf( TOKEN_SECURITY ) );
			} else if ( serverVersion.trim().endsWith( String.valueOf( TOKEN_UPDATE ) ) ) {
				securityExploit = false;
				update = true;
				this.serverVersion = serverVersion.substring( 0, serverVersion.lastIndexOf( TOKEN_UPDATE ) );
			} else {
				securityExploit = false;
				update = false;
				this.serverVersion = serverVersion;
			}
		} else {
			securityExploit = false;
			update = false;
			this.serverVersion = serverVersion;
		}
		
	}

	/* (non-Javadoc)
	 * @see org.agnitas.bean.VersionObject#isLatestVersion()
	 */
	public boolean isLatestVersion() {
		return latestVersion;
	}
	
	/* (non-Javadoc)
	 * @see org.agnitas.bean.VersionObject#isSecurityExploit()
	 */
	public boolean isSecurityExploit() {
		return securityExploit;
	}
	
	/* (non-Javadoc)
	 * @see org.agnitas.bean.VersionObject#isUpdate()
	 */
	public boolean isUpdate() {
		return update;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.bean.VersionObject#getServerVersion()
	 */
	public String getServerVersion() {
		return serverVersion;
	}

}
