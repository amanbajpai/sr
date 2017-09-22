package com.ros.smartrocket.map.location;

import android.location.Address;
import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.utils.L;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;

final class GeoCoder {
    private static final String TAG = "GeoCoder";
    private static String key = "W5p8-Mt3zmaPqLT0c9rpin64Dno=";
    private static final String STATUS_OK = "OK";
    private static final String BAIDU_STATUS_OK = "0";
    private static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    private Locale locale = Locale.getDefault();

    GeoCoder(Locale locale) {
        if (locale != null) this.locale = locale;
    }

    Address getFromLocation(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0)
            throw new IllegalArgumentException("latitude == " + latitude);

        if (longitude < -180.0 || longitude > 180.0)
            throw new IllegalArgumentException("longitude == " + longitude);
        Address address = null;
        String url;
        if (!Config.USE_BAIDU) {
            url = getGoogleGeoCodingUrl(latitude, longitude);
            String json = sendGetRequest(url);
            address = getAddress(json, latitude, longitude);
        }
        return address;
    }

    private String getGoogleGeoCodingUrl(double latitude, double longitude) {
        return Config.GEOCODER_URL + "/maps/api/geocode/json?sensor=true&latlng=" +
                latitude +
                ',' +
                longitude +
                "&language=" +
                locale.getLanguage() +
                "&key=" +
                BuildConfig.SERVER_API_KEY +
                "&client=" +
                "gme-redoceansolutions";
    }

    String sendGetRequest(final String url) {
        String result = null;
        try {
            Call<ResponseBody> call = App.getInstance().getApi().getGeoCoding(url);
            ResponseBody responseBody = call.execute().body();
            if (responseBody != null)
                result = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
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
                fillInAddress(address, a.getJSONObject(0));
            } else if (status.equals(STATUS_OVER_QUERY_LIMIT))
                throw new LimitExceededException();
        } catch (LimitExceededException e) {
            L.e(TAG, "Error getAddress ", e);
        } catch (Exception e) {
            L.e(TAG, "parseJson error: " + e.getMessage(), e);
        }

        return address;
    }

    private void fillInAddress(Address address, JSONObject item) {
        try {
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
        } catch (Exception e) {
            L.e(TAG, "parseJson error: " + e.getMessage(), e);
        }
    }

    static final class LimitExceededException extends Exception {
        private static final long serialVersionUID = -1243645207607944474L;
    }
}