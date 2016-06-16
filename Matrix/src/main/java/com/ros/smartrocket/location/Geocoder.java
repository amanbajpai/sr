package com.ros.smartrocket.location;

import android.content.Context;
import android.location.Address;
import android.net.http.AndroidHttpClient;
import android.text.TextUtils;
import android.util.Base64;

import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class Geocoder {
    private static final String TAG = "Geocoder";
    private static String key = "W5p8-Mt3zmaPqLT0c9rpin64Dno=";
    /**
     * Indicates that no errors occurred; the address was successfully parsed and at least one geocode was returned.
     */
    private static final String STATUS_OK = "OK";
    private static final String BAIDU_STATUS_OK = "0";

    /**
     * Indicates that you are over your quota.
     */
    private static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";

    private final Context context;
    private Locale locale = Locale.getDefault();
    private AndroidHttpClient client;

    /**
     * Constructs a Geocoder whose responses will be localized for the default system Locale.
     *
     * @param context the Context of the calling Activity
     */
    public Geocoder(Context context) {
        this.context = context;
    }

    public Geocoder(Context context, Locale locale) {
        this.context = context;
        if (locale != null) {
            this.locale = locale;
        }
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
     * @throws java.io.IOException      if the network is unavailable or any other I/O problem occurs
     */
    public Address getFromLocation(double latitude, double longitude) {
        client = AndroidHttpClient.newInstance(TAG, context);

        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("latitude == " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("longitude == " + longitude);
        }

        Address address;
        String url;

        if (!Config.USE_BAIDU) {
            url = getGoogleGeocodingUrl(latitude, longitude);
            String json = sendGetRequest(url);
            address = getAddress(json, latitude, longitude);
        } else {
            /*url = getBaiduGeocodingUrl(latitude, longitude);
            String json = sendGetRequest(url);
            address = getBaiduAddress(json, latitude, longitude);*/

            //Don't use geocoding for baidu map
            address = null;
        }

        client.close();

        return address;
    }

    private String getGoogleGeocodingUrl(double latitude, double longitude) {
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

        String signature = getSignature(url.toString(), key);

        url.append("&signature=");
        try {
            url.append(URLEncoder.encode(signature, "UTF-8"));
        } catch (Exception e) {
            L.e(TAG, "Error in getFromLocation method. Signature encode error", e);
        }
        return url.toString();
    }

    private String getBaiduGeocodingUrl(double latitude, double longitude) {
        StringBuilder url = new StringBuilder(Config.GEOCODER_URL);
        url.append("/?ak=");
        url.append(Config.BAIDU_API_KEY);
        //url.append("&callback=renderReverse");
        url.append("&location=");
        url.append(latitude);
        url.append(',');
        url.append(longitude);
        url.append("&output=json&pois=0&mcode=");
        url.append(UIUtils.getCertificateSHA1Fingerprint(context) + ";" + BuildConfig.APPLICATION_ID);

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

    /**
     * Returns an array of Addresses that are known to describe the named location, which may be a place name such as
     * "Dalvik, Iceland", an address such as "1600 Amphitheatre Parkway, Mountain View,
     * CA", an airport code such as "SFO", etc.. The returned addresses will be localized for the locale provided to
     * this class's constructor.
     * <p/>
     * <p/>
     * The query will block and returned values will be obtained by means of a network lookup. The results are a best
     * guess and are not guaranteed to be meaningful or correct. It may be useful to call this method from a thread
     * separate from your primary UI thread.
     *
     * @param locationName a user-supplied description of a location
     * @param maxResults   max number of results to return. Smaller numbers (1 to 5) are recommended
     * @return a list of Address objects. Returns null or empty list if no matches were found or there is no backend
     * service available.
     * @throws IllegalArgumentException if locationName is null
     * @throws java.io.IOException      if the network is unavailable or any other I/O problem occurs
     */
    public List<Address> getFromLocationName(String locationName, int maxResults)
            throws IOException, LimitExceededException {
        if (locationName == null) {
            throw new IllegalArgumentException("locationName == null");
        }

        client = AndroidHttpClient.newInstance(TAG, context);

        StringBuilder request = new StringBuilder(Config.GEOCODER_URL + "/maps/api/geocode/json?sensor=false");
        request.append("&language=").append(locale.getLanguage());
        request.append("&address=").append(URLEncoder.encode(locationName, "UTF-8"));

        String json = sendGetRequest(request.toString());

        client.close();

        return getAddressList(json);
    }

    public String sendGetRequest(final String url) {
        String result = null;

        try {
            final HttpGet get = new HttpGet(url);

            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 120000);
            HttpConnectionParams.setSoTimeout(params, 120000);

            HttpResponse response = client.execute(get);
            result = readResponse(response.getEntity().getContent());
        } catch (Exception e) {
            L.e(TAG, "Send geocoder request: " + e.toString(), e);

        }
        return result;
    }

    private List<Address> getAddressList(String json) {
        List<Address> addressList = new ArrayList<Address>();

        try {
            JSONObject o = new JSONObject(json);
            String status = o.getString("status");
            if (status.equals(STATUS_OK)) {
                JSONArray a = o.getJSONArray("results");

                for (int i = 0; i < a.length(); i++) {
                    Address address = new Address(locale);
                    JSONObject item = a.getJSONObject(i);

                    fillInAddress(address, item);
                    addressList.add(address);
                }

            } else if (status.equals(STATUS_OVER_QUERY_LIMIT)) {
                throw new LimitExceededException();
            }
        } catch (LimitExceededException e) {
            L.e(TAG, "Error getAddressList ", e);
        } catch (Exception e) {
            L.e(TAG, "parseJson error: " + e.getMessage(), e);
        }

        return addressList;
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

    private Address getBaiduAddress(String json, double latitude, double longitude) {
        Address address = new Address(locale);
        address.setLatitude(latitude);
        address.setLongitude(longitude);

        try {
            JSONObject o = new JSONObject(json);
            String status = o.getString("status");
            if (status.equals(BAIDU_STATUS_OK)) {
                fillInBaiduAddress(address, o.optJSONObject("result"));
            }
        } catch (Exception e) {
            L.e(TAG, "parseJson error: " + e.getMessage(), e);
        }

        return address;
    }

    public void fillInBaiduAddress(Address address, JSONObject item) {
        try {
            JSONObject location = item.getJSONObject("addressComponent");

            String districtName = location.getString("district");
            String cityName = location.optString("city");

            address.setCountryName("中国");
            address.setSubLocality(districtName);
            address.setLocality(cityName);
        } catch (Exception e) {
            L.e(TAG, "parseJson error: " + e.getMessage(), e);
        }
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
            L.e(TAG, "ReadResponse error: " + e.getMessage(), e);
            return null;
        } finally {
            try {
                br.close();
                is.close();
            } catch (IOException e) {
                L.e(TAG, "ReadResponse closeStream error: " + e.getMessage(), e);
            }
        }

        return response.toString();

    }

    /**
     * Is thrown when the query was over limit before 24 hours
     */
    public static final class LimitExceededException extends Exception {
        private static final long serialVersionUID = -1243645207607944474L;
    }
}