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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Cleans up the future-holder when the users sessions ends. Maybe the user close his browser while a long running task hasn't finished.
 * The task would orphan otherwise.
 * The future holder is a map which controls which running futures are bound to which session.
 * @author ms
 *
 */
public class FutureHolderCleanUpListener implements HttpSessionListener{


	@Override
	public void sessionCreated(HttpSessionEvent se) {
		// TODO Auto-generated method stub

	}


	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {

		HttpSession session = sessionEvent.getSession();
		String sessionID = session.getId();
		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
		AbstractMap<String, Future> futureHolder = (AbstractMap<String, Future>) applicationContext.getBean("futureHolder");
		Set<String> keySet =  futureHolder.keySet();
		List<String> keysToRemove = new ArrayList<String>();

		for(String key:keySet) {
			if(key.endsWith("@"+sessionID) ) {
				keysToRemove.add(key);
			}
		}

		for(String removeMe : keysToRemove) {
			futureHolder.remove(removeMe);
		}

	}

}
