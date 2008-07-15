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

package org.agnitas.web;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TextFileDownload extends HttpServlet {

    private static final long serialVersionUID = 5844323149267914354L;

	/**
     * Gets parameters.
     * reads file.
     * prints outputstream.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse response)
                      throws IOException, ServletException {

        response.setContentType("text/plain");
        Hashtable map = (Hashtable)(req.getSession().getAttribute("map"));
        String outfile = (String)map.get(req.getParameter("key"));

        response.setHeader("Content-Disposition", "attachment; filename=\"" + req.getParameter("key") + ".csv\";");
        ServletOutputStream ostream = response.getOutputStream();
        ostream.print(outfile);
    }
}
