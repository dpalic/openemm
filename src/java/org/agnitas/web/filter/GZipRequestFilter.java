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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;
import org.springframework.ws.transport.http.HttpTransportConstants;

public class GZipRequestFilter implements Filter {
	private static final transient Logger logger = Logger.getLogger( GZipRequestFilter.class);

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws IOException, ServletException {
//		long startTime = System.nanoTime();

        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpRes = (HttpServletResponse) res;

        String encoding = httpReq.getHeader("Content-Encoding");
        if(encoding != null){
            if(encoding.equalsIgnoreCase("gzip")){
                req = new GZIPServletRequestWrapper(httpReq);
                logger.debug("GZipRequestFilter: request is wrapped to uncompress.");
            }
        }
        String acceptEncoding = httpReq.getHeader(HttpTransportConstants.HEADER_ACCEPT_ENCODING);
        if(acceptEncoding != null){
            if(acceptEncoding.indexOf("gzip") != -1){
            	httpRes.addHeader("Content-Encoding", "gzip");
                res = new GZIPServletResponseWrapper(httpRes);
                logger.debug("GZipRequestFilter: response is wrapped to compress.");
            }
        }

        fc.doFilter(req, res);

        if (res instanceof GZIPServletResponseWrapper) {
            ((GZIPServletResponseWrapper)res).close();
        }

//		long estimatedTime = System.nanoTime() - startTime;
//		logger.warn("GZipRequestFilter spent about  " + estimatedTime + "ns");
    }

    @Override
    public void destroy() {
    }

    private class GZIPServletResponseWrapper extends HttpServletResponseWrapper{
        private GZIPServletOutputStream gzipOutputStream = null;
        private PrintWriter printWriter = null;

        public GZIPServletResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (this.printWriter != null) {
                throw new IllegalStateException(
                    "PrintWriter obtained already - cannot get OutputStream");
            }
            if (this.gzipOutputStream == null) {
                this.gzipOutputStream = new GZIPServletOutputStream(
                    getResponse().getOutputStream());
            }
            return this.gzipOutputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (this.printWriter == null && this.gzipOutputStream != null) {
                throw new IllegalStateException(
                    "OutputStream obtained already - cannot get PrintWriter");
            }
            if (this.printWriter == null) {
                this.gzipOutputStream = new GZIPServletOutputStream(
                    getResponse().getOutputStream());
                this.printWriter = new PrintWriter(new OutputStreamWriter(
                    this.gzipOutputStream, getResponse().getCharacterEncoding()));
            }
            return this.printWriter;
        }

        public void close() throws IOException {
            if (this.printWriter != null) {
                this.printWriter.close();
            }

            if (this.gzipOutputStream != null) {
                this.gzipOutputStream.close();
            }
        }

        @Override
        public void setContentLength(int len) {
            //ignore, since content length of zipped content
            //does not match content length of unzipped content.
        }

    }

    private class GZIPServletOutputStream extends ServletOutputStream{
        private OutputStream gzipOutputStream;

        public GZIPServletOutputStream(OutputStream output) throws IOException {
            this.gzipOutputStream = new GZIPOutputStream(output);
        }

        @Override
        public void close() throws IOException {
            this.gzipOutputStream.close();
        }

        @Override
        public void write(int ch) throws IOException {
            this.gzipOutputStream.write(ch);
        }

        @Override
        public void flush() throws IOException {
        	this.gzipOutputStream.flush();
        }

        @Override
        public void write(byte[] bytes) throws IOException {
        	this.gzipOutputStream.write(bytes);
        }

        @Override
        public void write(byte[] bytes, int offset, int length)
                throws IOException {
        	this.gzipOutputStream.write(bytes, offset, length);
        }
   }

    private class GZIPServletRequestWrapper extends HttpServletRequestWrapper{

        public GZIPServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new GZIPServletInputStream(super.getInputStream());
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(new GZIPServletInputStream(super.getInputStream())));
        }
    }

    private class GZIPServletInputStream extends ServletInputStream{
        private InputStream input;

        public GZIPServletInputStream(InputStream input) throws IOException {
            this.input = new GZIPInputStream(input);
        }

        @Override
        public int read() throws IOException {
            return this.input.read();
        }
    }
}