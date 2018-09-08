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
package org.agnitas.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class HttpUtils {
	private static final transient Logger logger = Logger.getLogger(HttpUtils.class);

	public static final String SECURE_HTTP_PROTOCOL_SIGN = "https://";
	public static final String HTTP_PROTOCOL_SIGN = "http://";

	private static TrustManager TRUSTALLCERTS_TRUSTMANAGER = new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}
	};

	private static HostnameVerifier TRUSTALLHOSTNAMES_HOSTNAMEVERIFIER = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	public static String convertToParameterString(Map<String, Object> parameterMap) {
		StringBuilder returnValue = new StringBuilder();

		if (parameterMap != null) {
			try {
				for (Entry<String, Object> entry : parameterMap.entrySet()) {
					if (returnValue.length() > 0)
						returnValue.append("&");
					returnValue.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
					returnValue.append("=");
					if (entry.getValue() != null)
						returnValue.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
				}
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
			}
		}

		return returnValue.toString();
	}

	public static String executeHttpGetRequest(String httpUrl, Map<String, Object> httpGetParameter) throws Exception {
		return executeHttpRequest(httpUrl, httpGetParameter, null);
	}

	public static String executeHttpGetRequest(String httpUrl, Map<String, Object> httpGetParameter, boolean checkSslServerCert) throws Exception {
		return executeHttpRequest(httpUrl, httpGetParameter, null, checkSslServerCert);
	}

	public static String executeHttpPostRequest(String httpUrl, Map<String, Object> httpPostParameter) throws Exception {
		return executeHttpRequest(httpUrl, null, httpPostParameter);
	}

	public static String executeHttpPostRequest(String httpUrl, Map<String, Object> httpPostParameter, boolean checkSslServerCert) throws Exception {
		return executeHttpRequest(httpUrl, null, httpPostParameter, checkSslServerCert);
	}

	public static String executeHttpRequest(String httpUrlString, Map<String, Object> httpGetParameter, Map<String, Object> httpPostParameter) throws Exception {
		return executeHttpRequest(httpUrlString, httpGetParameter, httpPostParameter, true);
	}

	public static String executeHttpRequest(String httpUrlString, Map<String, Object> httpGetParameter, Map<String, Object> httpPostParameter, boolean checkSslServerCert) throws Exception {
		if (StringUtils.isBlank(httpUrlString)) {
			throw new RuntimeException("Invalid empty URL for http request");
		}

		StringBuilder input = new StringBuilder();
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String urlString = httpUrlString.toLowerCase();

		// Check for protocol "https://" or "http://" (fallback: "http://")
		if (!urlString.startsWith(SECURE_HTTP_PROTOCOL_SIGN)
				&& !urlString.startsWith(HTTP_PROTOCOL_SIGN))
			urlString = HTTP_PROTOCOL_SIGN + urlString;

		try {
			if (httpGetParameter != null && httpGetParameter.size() > 0) {
				// Prepare Get parameter data
				String getParameterString = convertToParameterString(httpGetParameter);
				urlString += "?" + getParameterString;
			}

			HttpURLConnection urlConnection = (HttpURLConnection) new URL(urlString).openConnection();

			if (urlString.startsWith(SECURE_HTTP_PROTOCOL_SIGN) && !checkSslServerCert) {
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, new TrustManager[] { TRUSTALLCERTS_TRUSTMANAGER }, new java.security.SecureRandom());
				SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
				((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslSocketFactory);
				((HttpsURLConnection) urlConnection).setHostnameVerifier(TRUSTALLHOSTNAMES_HOSTNAMEVERIFIER);
			}

			if (httpPostParameter != null && httpPostParameter.size() > 0) {
				// Send post parameter data
				urlConnection.setDoOutput(true);
			    out = new OutputStreamWriter(urlConnection.getOutputStream());
			    out.write(HttpUtils.convertToParameterString(httpPostParameter));
			    out.flush();
			}

			int responseCode = urlConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					input.append(inputLine);
					input.append("\n");
				}
			} else {
				throw new HttpException(urlString, responseCode);
			}
		} catch (HttpException e) {
			throw e;
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}

		return input.toString();
	}
}
