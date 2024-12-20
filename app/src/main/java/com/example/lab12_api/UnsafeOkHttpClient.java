package com.example.lab12_api;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;

public class UnsafeOkHttpClient {

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // 建立一個信任所有憑證的 X509TrustManager
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                            // 不執行任何動作，信任所有用戶端憑證
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                            // 不執行任何動作，信任所有伺服器憑證
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            // 返回空陣列表示接受所有發行商的憑證
                            return new X509Certificate[0];
                        }
                    }
            };

            // 初始化 SSLContext 物件，並使用信任所有憑證的 X509TrustManager 初始化
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            // 建立一個新的 OkHttpClient，並設定 SSLSocketFactory 和 hostname verifier，以此忽略憑證
            // 並將 OkHttpClient 返回
            return new OkHttpClient
                    .Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}