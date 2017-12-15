package com.ros.smartrocket.map.location;

import android.location.Address;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

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
import retrofit2.Callback;
import retrofit2.Response;

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

    void getFromLocation(Location location, MatrixLocationManager.IAddress callback) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        if (latitude < -90.0 || latitude > 90.0)
            throw new IllegalArgumentException("latitude == " + latitude);

        if (longitude < -180.0 || longitude > 180.0)
            throw new IllegalArgumentException("longitude == " + longitude);
        if (!Config.USE_BAIDU) {
            String url = getGoogleGeoCodingUrl(location);
            sendGetRequest(url, location, callback);
        } else {
            callback.onUpdate(null);
        }
    }

    private String getGoogleGeoCodingUrl(Location location) {
        return Config.GEOCODER_URL + "/maps/api/geocode/json?sensor=true&latlng=" +
                location.getLatitude() +
                ',' +
                location.getLongitude() +
                "&language=" +
                locale.getLanguage() +
                "&key=" +
                BuildConfig.SERVER_API_KEY +
                "&client=" +
                "gme-redoceansolutions";
    }

    void sendGetRequest(final String url, Location location, MatrixLocationManager.IAddress callback) {
        Call<ResponseBody> call = App.getInstance().getApi().getGeoCoding(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null)
                            callback.onUpdate(getAddress(response.body().string(), location));
                    } catch (IOException e) {
                        Log.e(TAG, "IOException", e);
                        callback.onUpdate(null);
                    }
                } else {
                    callback.onUpdate(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onUpdate(null);
            }
        });
    }

    private Address getAddress(String json, Location location) {
        Address address = new Address(locale);
        address.setLatitude(location.getLatitude());
        address.setLongitude(location.getLongitude());

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