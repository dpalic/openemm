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
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.agnitas.dao.impl.SyncObject;

public class SerializeRequestFilter implements Filter {

	private static final String SYNC_OBJECT_KEY = "SYNC_OBJECT_KEY";

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		final HttpSession session =  ((HttpServletRequest)request).getSession();
		synchronized (getSynchronizationObject(session)) {
			chain.doFilter(request, response);
		}

	}

	private static synchronized Object getSynchronizationObject( HttpSession session)  {
		SyncObject syncObject =   (SyncObject) session.getAttribute(SYNC_OBJECT_KEY);
		if ( syncObject == null ) {
			syncObject = new SyncObject();
			session.setAttribute(SYNC_OBJECT_KEY,syncObject);
		}
		return syncObject;
	}


	public void init(FilterConfig arg0) throws ServletException {

	}



}
