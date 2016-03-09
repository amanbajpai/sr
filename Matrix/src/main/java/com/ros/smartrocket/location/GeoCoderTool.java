package com.ros.smartrocket.location;

import android.location.Address;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.MyLog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class GeoCoderTool {
    public static final int TIMEOUT = 120;
    private static final String TAG = "GeoCoder";
    private static final String KEY = "W5p8-Mt3zmaPqLT0c9rpin64Dno=";
    /**
     * Indicates that no errors occurred; the address was successfully parsed and at least one geocode was returned.
     */
    private static final String STATUS_OK = "OK";
    private static final String BAIDU_STATUS_OK = "0";

    /**
     * Indicates that you are over your quota.
     */
    private static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";

    private final OkHttpClient client;
    private Locale locale = Locale.getDefault();

    public GeoCoderTool(Locale locale) {
        if (locale != null) {
            this.locale = locale;
        }

        client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS).build();
    }

    /**
     * Returns an array of Addresses that are known to describe the area immediately surrounding the given latitude and
     * longitude. The returned addresses will be localized for the locale provided to this class's constructor.
     * <p/>
     * <p/>
     * The returned values may be obtained by means of a network lookup. The results are a best guess and are not
     * guaranteed to be meaningful or correct. It may be useful to call this method from a thread separate from your
     * primary UI thread.
     *
     * @param latitude  the latitude a point for the search
     * @param longitude the longitude a point for the search
     * @return a list of Address objects. Returns null or empty list if no matches were found or there is no backend
     * service available.
     * @throws IllegalArgumentException if latitude is less than -90 or greater than 90
     * @throws IllegalArgumentException if longitude is less than -180 or greater than 180
     */
    public Address getFromLocation(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("latitude == " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("longitude == " + longitude);
        }

        Address address;
        String url;

        if (!Config.USE_BAIDU) {
            url = getGoogleGeoCodingUrl(latitude, longitude);
            String json = sendGetRequest(url);
            address = getAddress(json, latitude, longitude);
        } else {
            //Don't use geo coding for baidu map
            address = null;
        }

        return address;
    }

    private String getGoogleGeoCodingUrl(double latitude, double longitude) {
        StringBuilder url = new StringBuilder(Config.GEOCODER_URL);

        url.append("/maps/api/geocode/json?sensor=true&latlng=");
        url.append(latitude);
        url.append(',');
        url.append(longitude);
        url.append("&language=");
        url.append(locale.getLanguage());

        url.append("&key=");
        url.append(BuildConfig.SERVER_API_KEY);
        url.append("&client=");
        url.append("gme-redoceansolutions");

        String signature = getSignature(url.toString(), KEY);

        url.append("&signature=");
        try {
            url.append(URLEncoder.encode(signature, "UTF-8"));
        } catch (Exception e) {
            L.e(TAG, "Error in getFromLocation method. Signature encode error", e);
        }
        return url.toString();
    }

    private String getSignature(String baseUrl, String baseKey) {
        String result = "";

        try {
            URL url = new URL(baseUrl);

            baseKey = baseKey.replace('-', '+');
            baseKey = baseKey.replace('_', '/');

            byte[] key = Base64.decode(baseKey, Base64.DEFAULT);

            String resource = url.getPath() + '?' + url.getQuery();

            // Get an HMAC-SHA1 signing key from the raw key bytes
            SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

            // Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(sha1Key);

            // compute the binary signature for the request
            byte[] sigBytes = mac.doFinal(resource.getBytes());

            // base 64 encode the binary signature
            String signature = Base64.encodeToString(sigBytes, Base64.DEFAULT);

            // convert the signature to 'web safe' base 64
            signature = signature.replace('+', '-');
            signature = signature.replace('/', '_');

            result = signature;

        } catch (Exception e) {
            L.e(TAG, "Error in getSignature method.", e);
        }

        return result;
    }

    @Nullable
    public String sendGetRequest(final String url) {
        String result = null;
        Request request = new Request.Builder().url(url).build();
        try {
            result = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            MyLog.logStackTrace(e);
        }

        return result;
    }

    private Address getAddress(String json, double latitude, double longitude) {
        Address address = new Address(locale);
        address.setLatitude(latitude);
        address.setLongitude(longitude);

        try {
            JSONObject o = new JSONObject(json);
            String status = o.getString("status");
            if (status.equals(STATUS_OK) || status.equals(BAIDU_STATUS_OK)) {
                JSONArray a = o.optJSONArray("results");

                for (int i = 0; i < a.length(); i++) {
                    JSONObject item = a.getJSONObject(i);

                    fillInAddress(address, item);
                }

            } else if (status.equals(STATUS_OVER_QUERY_LIMIT)) {
                throw new LimitExceededException();
            }
        } catch (LimitExceededException e) {
            L.e(TAG, "Error getAddress ", e);
        } catch (Exception e) {
            L.e(TAG, "parseJson error: " + e.getMessage(), e);
        }

        return address;
    }

    public void fillInAddress(Address address, JSONObject item) {
        try {
            JSONArray typeJSONArray = item.getJSONArray("types");
            String typeName = typeJSONArray.getString(0);

            if (typeJSONArray.length() > 0 && (typeName.equals("administrative_area_level_2")
                    || typeName.equals("sublocality_level_1"))) {
                address.setFeatureName(item.getString("formatted_address"));

                JSONObject location = item.getJSONObject("geometry").getJSONObject("location");
                address.setLatitude(location.getDouble("lat"));
                address.setLongitude(location.getDouble("lng"));

                JSONArray areaArray = item.getJSONArray("address_components");
                for (int j = 0; j < areaArray.length(); j++) {
                    JSONObject areaObject = (JSONObject) areaArray.get(j);
                    String longName = areaObject.getString("long_name");

                    JSONArray addressTypeJSONArray = areaObject.getJSONArray("types");

                    if (addressTypeJSONArray.length() > 0) {
                        String addressTypeName = addressTypeJSONArray.getString(0);

                        if (addressTypeName.equals("country")) {
                            address.setCountryName(longName);
                        } else if (addressTypeName.equals("administrative_area_level_2")
                                && (TextUtils.isEmpty(address.getSubLocality()) || Config.USE_BAIDU)) {
                            address.setSubLocality(longName);
                        } else if (addressTypeName.equals("sublocality_level_1")) {
                            address.setSubLocality(longName);
                        } else if (addressTypeName.equals("locality")) {
                            address.setLocality(longName);
                        } else if (addressTypeName.equals("administrative_area_level_1")
                                && TextUtils.isEmpty(address.getLocality())) {
                            address.setLocality(longName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            L.e(TAG, "parseJson error: " + e.getMessage(), e);
        }
    }

    /**
     * Is thrown when the query was over limit before 24 hours
     */
    public static final class LimitExceededException extends Exception {
        private static final long serialVersionUID = -1243645207607944474L;
    }
}
