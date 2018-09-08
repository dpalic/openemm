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
package org.agnitas.cms.utils.preview;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

public class TrustedHttpsHandler extends sun.net.www.protocol.https.Handler {
	private static final transient Logger logger = Logger.getLogger(TrustedHttpsHandler.class);

	private SSLSocketFactory trustedSslSocketFactory = null;
	private HostnameVerifier kindHostnameVerifier = null;

    public TrustedHttpsHandler() {
        trustedSslSocketFactory = createTrustedSocketFactory();
        kindHostnameVerifier = new PreviewHostnameVerifier();
    }

    public SSLSocketFactory getTrustedSslSocketFactory() {
        return trustedSslSocketFactory;
    }

    private SSLSocketFactory createTrustedSocketFactory() {
        try {
            final SSLContext context = SSLContext.getInstance("ssl");
            final X509TrustManager trustManager = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            context.init(null, new TrustManager[]{trustManager}, null);
            return context.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
        	logger.error(e);
        } catch (KeyManagementException e) {
        	logger.error(e);
        }
        return null;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        URLConnection connection = super.openConnection(url);
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection urlConnection = (HttpsURLConnection) connection;
            HostnameVerifier hostnameVerifier = urlConnection.getHostnameVerifier();
            if (hostnameVerifier != kindHostnameVerifier) {
                urlConnection.setHostnameVerifier(kindHostnameVerifier);
            }
            if (urlConnection.getSSLSocketFactory() != trustedSslSocketFactory && trustedSslSocketFactory != null) {
                urlConnection.setSSLSocketFactory(trustedSslSocketFactory);
            }
        }
        return connection;
    }

    @Override
    protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        URLConnection connection = super.openConnection(url, proxy);
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection urlConnection = (HttpsURLConnection) connection;
            HostnameVerifier hostnameVerifier = urlConnection.getHostnameVerifier();
            if (hostnameVerifier != kindHostnameVerifier) {
                urlConnection.setHostnameVerifier(kindHostnameVerifier);
            }
            if (trustedSslSocketFactory != null) {
                urlConnection.setSSLSocketFactory(trustedSslSocketFactory);
            }
        }
        return connection;
    }

    class PreviewHostnameVerifier implements HostnameVerifier {
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
}
