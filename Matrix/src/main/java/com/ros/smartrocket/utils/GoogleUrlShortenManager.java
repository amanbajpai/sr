package com.ros.smartrocket.utils;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class GoogleUrlShortenManager {
    private static final String TAG = "GoogleUrlShortenManager";
    private final static String GET_SHORT_LINK_URL = "https://www.googleapis.com/urlshortener/v1/url?key="
            + Config.GOOGLE_API_KEY;

    private Context context;
    private static GoogleUrlShortenManager instance = null;
    private AndroidHttpClient client;
    private OnShotrUrlReadyListener onShotrUrlReadyListener;

    private final static String LONG_URL = "longUrl";
    private final static String SHORT_URL = "id";
    private final static String ERROR = "error";

    public static GoogleUrlShortenManager getInstance() {
        if (instance == null) {
            instance = new GoogleUrlShortenManager();
        }
        return instance;
    }

    private GoogleUrlShortenManager() {
    }

    public void getShortUrl(Context context, String longUrl, OnShotrUrlReadyListener onShotrUrlReadyListener) {
        this.context = context;
        this.onShotrUrlReadyListener = onShotrUrlReadyListener;

        if (UIUtils.isOnline(context)) {
            if (!TextUtils.isEmpty(longUrl)) {
                new GetShortUrlAsyncTask(longUrl).execute();
            }
        } else {
            onShotrUrlReadyListener.onGetShortUrlError(context.getString(R.string.no_internet));
        }
    }

    private class GetShortUrlAsyncTask extends AsyncTask<Void, Integer, JSONObject> {
        String longUrl;

        public GetShortUrlAsyncTask(final String longUrl) {
            this.longUrl = longUrl;
        }

        protected JSONObject doInBackground(Void... params) {
            JSONObject responseJson = null;
            client = AndroidHttpClient.newInstance(TAG, context);

            try {
                String requestJson = getShortUrlRequestJson(longUrl);
                String responseJsonString = sendPostRequest(GET_SHORT_LINK_URL, requestJson);

                responseJson = new JSONObject(responseJsonString);
            } catch (Exception e) {
                onShotrUrlReadyListener.onGetShortUrlError(e.getMessage());
            }
            client.close();

            return responseJson;
        }

        protected void onPostExecute(JSONObject responseJson) {
            String shortUrl = responseJson.optString(SHORT_URL);
            L.i(TAG, "shortUrl: " + shortUrl);
            if (!TextUtils.isEmpty(shortUrl)) {
                onShotrUrlReadyListener.onShortUrlReady(shortUrl);
            } else {
                String errorObject = responseJson.optString(ERROR);
                onShotrUrlReadyListener.onGetShortUrlError(errorObject);
            }
        }
    }


    protected String sendPostRequest(final String url, final String requestJson) throws UnsupportedEncodingException {
        String response = null;

        try {
            L.i(TAG, "Request body: " + requestJson);
            HttpEntity entity = new StringEntity(requestJson, "UTF-8");
            BasicHeader contentTypeHeader = new BasicHeader("Content-type", "application/json");

            HttpUriRequest request = new HttpPost(url);
            request.setHeader(contentTypeHeader);

            if (entity != null) {
                ((HttpPost) request).setEntity(entity);
            }

            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 120000);
            HttpConnectionParams.setSoTimeout(params, 120000);

            HttpResponse httpResponse = client.execute(request);
            response = readResponse(httpResponse.getEntity().getContent());
        } catch (Exception e) {
            L.e(TAG, "Send post request: " + e.toString());
        }
        L.i(TAG, "Response result: " + response);
        return response;
    }

    private String getShortUrlRequestJson(String longUrl) {
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put(LONG_URL, longUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestJson.toString();
    }

    private String readResponse(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        response.append("");
        try {
            while ((line = br.readLine()) != null) {
                response.append(line + "\r");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                br.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response.toString();

    }

    public interface OnShotrUrlReadyListener {
        void onShortUrlReady(String shortUrl);

        void onGetShortUrlError(String errorString);
    }
}
