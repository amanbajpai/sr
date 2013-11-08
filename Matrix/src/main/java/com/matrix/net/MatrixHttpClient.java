package com.matrix.net;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class MatrixHttpClient {
    private static HttpClient httpClientInstance;

    private MatrixHttpClient() {
        httpClientInstance = createHttpClient();
    }

    public static HttpClient getClient() {
        if (httpClientInstance == null) {
            new MatrixHttpClient();
        }
        return httpClientInstance;
    }

    public void shutdownHttpClient() {
        if (httpClientInstance != null && httpClientInstance.getConnectionManager() != null) {
            httpClientInstance.getConnectionManager().shutdown();
        }
    }

    private HttpClient createHttpClient() {
        final HttpParams params = new BasicHttpParams();
        params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
        params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
        params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpConnectionParams.setConnectionTimeout(params, 120000);
        HttpConnectionParams.setSoTimeout(params, 120000);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, registry);
        return new DefaultHttpClient(connectionManager, params);
    }

}
