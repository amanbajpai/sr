package com.ros.smartrocket.net.retrofit;

import android.text.TextUtils;
import android.util.Base64;

import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHolder {
    private static final int TIMEOUT = 180;
    private static final String DEVICE_UNIQUE_HEADER = "device-unique";
    private static final String DEVICE_TYPE_HEADER = "device-type";
    private static final String DEVICE_OS_VERSION_HEADER = "device-os-version";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String APP_VERSION_HEADER = "App-version";
    private static final String CONTENT_TYPE_HEADER = "Content-type";
    private static final String REGION_HEADER = "Region";
    private static final String ASIA_REGION = "Asia";
    private static final String ASIA_CHINA_REGION = "AsiaChina";
    private static final String SETTINGS_HEADER = "Settings";
    private MatrixApi matrixApi;

    public Retrofit getRetrofit() {
        return retrofit;
    }

    private Retrofit retrofit;

    public RetrofitHolder() {
        initRetrofit();
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Config.WEB_SERVICE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        matrixApi = retrofit.create(MatrixApi.class);
    }

    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(chain -> chain.proceed(getRequestWithHeaders(chain)));
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }

    private Request getRequestWithHeaders(Interceptor.Chain chain) {
        Request.Builder builder = chain.request().newBuilder();
        if (chain.request().url().toString().contains(Config.WEB_SERVICE_URL)) {
            builder.addHeader(DEVICE_UNIQUE_HEADER, App.getInstance().getDeviceId());
            builder.addHeader(DEVICE_TYPE_HEADER, App.getInstance().getDeviceType());
            builder.addHeader(DEVICE_OS_VERSION_HEADER, App.getInstance().getDeviceApiNumber());
            builder.addHeader(AUTHORIZATION_HEADER, "Bearer " + getToken());
            builder.addHeader(APP_VERSION_HEADER, BuildConfig.VERSION_NAME);
            builder.addHeader(CONTENT_TYPE_HEADER, "application/json");

            try {
                JSONObject settingJsonObject = new JSONObject();
                settingJsonObject.put("CurrentVersion", BuildConfig.VERSION_NAME);
                settingJsonObject.put(REGION_HEADER, ASIA_REGION);
                byte[] settingsByteArray = settingJsonObject.toString().getBytes("UTF-8");
                builder.addHeader(SETTINGS_HEADER, Base64.encodeToString(settingsByteArray, Base64.NO_WRAP));
            } catch (Exception e) {
                L.e("RetrofitHolder", "Add header settings json" + e, e);
            }
        }
        return builder.build();
    }

    private String getToken() {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        return TextUtils.isEmpty(preferencesManager.getTokenForUploadFile()) ?
                preferencesManager.getToken() :
                preferencesManager.getTokenForUploadFile();
    }

    public MatrixApi getMatrixApi() {
        return matrixApi;
    }
}
