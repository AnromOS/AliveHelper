package org.ancode.alivelib.http;

import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

/**
 * 证书信任管理
 * Created by andyliu on 16-12-8.
 */
public class MyX509TrustManager implements X509TrustManager {

    X509TrustManager sunJSSEX509TrustManager = null;

    public MyX509TrustManager(String fileName, String password) throws Exception {
        // create a "default" JSSE X509TrustManager.
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(fileName), password.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
        tmf.init(ks);
        TrustManager tms[] = tmf.getTrustManagers();

        /*
         * Iterate over the returnedtrustmanagers, look
         * for an instance ofX509TrustManager.  If found,
         * use that as our"default" trust manager.**/

        for (int i = 0; i < tms.length; i++) {
            if (tms[i] instanceof X509TrustManager) {
                sunJSSEX509TrustManager = (X509TrustManager) tms[i];
                return;
            }

        }
        /*
         * Find some other way toinitialize, or else we have to fail the
         * constructor.**/

        throw new Exception("Couldn't initialize");
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
