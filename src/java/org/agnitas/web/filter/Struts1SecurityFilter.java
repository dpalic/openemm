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
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * Security filter to protect Struts 1 web application against classload manipulation.
 *
 * Security issue: CVE-2014-0114
 *
 *
 * @author md
 *
 * @see <a href="http://h30499.www3.hp.com/t5/HP-Security-Research-Blog/Protect-your-Struts1-applications/ba-p/6463188#.U39R3nI-PtS">Article to filter</a>
 * @see <a href="http://lmgtfy.com/?q=CVE-2014-0114">Articles to security issue</a>
 */
public class Struts1SecurityFilter implements Filter {

	/**
	 * Filter configuration (comes from web.xml).
	 */
    private FilterConfig filterConfig = null;

    /**
     * Wrapper around HttpServletRequest.
     * This wrapper excludes all parameters that are considered to be insecure.
     *
     * @author md
     *
     */
    private static class ParamFilteredRequest extends HttpServletRequestWrapper {

        /**
         * Regular expression used to filter request parameters.
         */
        private String regex;

        /**
         * Creates a new request wrapper for given request.
         *
         * @param request request to wrap
         * @param regex regular expression for filtered parameter names
         */
        public ParamFilteredRequest(ServletRequest request, String regex) {
            super((HttpServletRequest)request);
            this.regex = regex;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            List<String> requestParameterNames = Collections.list(super.getParameterNames());
            List<String> finalParameterNames = new Vector<>();

            for (String parameterName:requestParameterNames) {
                if (!parameterName.matches(regex)) {
                    finalParameterNames.add(parameterName);
                }
            }
            return Collections.enumeration(finalParameterNames);
        }

    }

    @Override
	public void init(FilterConfig config) throws ServletException {
        this.filterConfig = config;
    }

    @Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String regex = this.filterConfig.getInitParameter("excludeParams");
        chain.doFilter(new ParamFilteredRequest(request, regex), response);
    }

    @Override
	public void destroy() {
    	// Nothing to do on destruction of the filter
    }
}
