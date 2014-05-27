package com.ros.smartrocket.location;

import android.content.Context;
import android.location.Address;
import android.net.http.AndroidHttpClient;
import com.ros.smartrocket.utils.L;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Geocoder {
    private static final String TAG = "Geocoder";
    /**
     * Indicates that no errors occurred; the address was successfully parsed and at least one geocode was returned.
     */
    private static final String STATUS_OK = "OK";

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
     * @param latitude   the latitude a point for the search
     * @param longitude  the longitude a point for the search
     * @param maxResults max number of addresses to return. Smaller numbers (1 to 5) are recommended
     * @return a list of Address objects. Returns null or empty list if no matches were found or there is no backend
     * service available.
     * @throws IllegalArgumentException if latitude is less than -90 or greater than 90
     * @throws IllegalArgumentException if longitude is less than -180 or greater than 180
     * @throws java.io.IOException      if the network is unavailable or any other I/O problem occurs
     */
    public List<Address> getFromLocation(double latitude, double longitude, int maxResults) throws IOException, LimitExceededException {
        client = AndroidHttpClient.newInstance(TAG, context);

        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("latitude == " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("longitude == " + longitude);
        }

        List<Address> results = new ArrayList<Address>();

        StringBuilder url = new StringBuilder("http://maps.googleapis.com/maps/api/geocode/json?sensor=true&latlng=");
        url.append(latitude);
        url.append(',');
        url.append(longitude);
        url.append("&language=");
        url.append(locale.getLanguage());

        String json = sendGetRequest(url.toString());
        if (json != null) {
            parseJson(results, maxResults, json);
        }

        client.close();

        return results;
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
    public List<Address> getFromLocationName(String locationName, int maxResults) throws IOException, LimitExceededException {
        if (locationName == null) {
            throw new IllegalArgumentException("locationName == null");
        }

        client = AndroidHttpClient.newInstance(TAG, context);

        List<Address> results = new ArrayList<Address>();

        StringBuilder request = new StringBuilder("http://maps.googleapis.com/maps/api/geocode/json?sensor=false");
        request.append("&language=").append(locale.getLanguage());
        request.append("&address=").append(URLEncoder.encode(locationName, "UTF-8"));

        String json = sendGetRequest(request.toString());
        if (json != null) {
            parseJson(results, maxResults, json);
        }

        client.close();

        return results;
    }

    public String sendGetRequest(final String url) {
        //L.i(TAG, "Geocoder URL: "+url);
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
        //L.i(TAG, "Response geocoder json: " + result);
        return result;
    }

    private void parseJson(List<Address> address, int maxResults, String json) throws LimitExceededException {
        try {
            JSONObject o = new JSONObject(json);
            String status = o.getString("status");
            if (status.equals(STATUS_OK)) {

                JSONArray a = o.getJSONArray("results");

                for (int i = 0; i < maxResults && i < a.length(); i++) {
                    Address current = new Address(locale);
                    JSONObject item = a.getJSONObject(i);

                    current.setFeatureName(item.getString("formatted_address"));
                    JSONObject location = item.getJSONObject("geometry").getJSONObject("location");
                    current.setLatitude(location.getDouble("lat"));
                    current.setLongitude(location.getDouble("lng"));

                    JSONArray areaArray = item.getJSONArray("address_components");
                    for (int j = 0; j < areaArray.length(); j++) {
                        JSONObject areaObject = (JSONObject) areaArray.get(j);
                        if (areaObject.getString("types").contains("\"country\"")) {
                            current.setCountryName(areaObject.getString("long_name"));
                        } else if (areaObject.getString("types").contains("\"administrative_area_level_1\"")) {
                            current.setAdminArea(areaObject.getString("long_name"));
                        } else if (areaObject.getString("types").contains("\"locality\"")) {
                            current.setLocality(areaObject.getString("long_name"));
                        }
                    }

                    address.add(current);
                }

            } else if (status.equals(STATUS_OVER_QUERY_LIMIT)) {

                throw new LimitExceededException();

            }
        } catch (LimitExceededException e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
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

    /**
     * Is thrown when the query was over limit before 24 hours
     */
    public static final class LimitExceededException extends Exception {
        private static final long serialVersionUID = -1243645207607944474L;
    }
}
