package com.ros.smartrocket.utils;

import android.text.TextUtils;
import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.LongUrl;
import com.ros.smartrocket.db.entity.ShortUrl;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GoogleUrlShortenManager {
    private static final String TAG = "GoogleUrlShortenManager";
    private final static String GET_SHORT_LINK_URL =
            "https://www.googleapis.com/urlshortener/v1/url?key=" + Config.SERVER_API_KEY;
    private OnShortUrlReadyListener onShortUrlReadyListener;


    public void getShortUrl(String longUrl, OnShortUrlReadyListener onShortUrlReadyListener) {
        this.onShortUrlReadyListener = onShortUrlReadyListener;
        if (UIUtils.isOnline(App.getInstance())) {
            if (!TextUtils.isEmpty(longUrl))
                requestShortUrl(longUrl);
        } else {
            onShortUrlReadyListener.onGetShortUrlError(App.getInstance().getString(R.string.no_internet));
        }
    }

    private void requestShortUrl(String longUrl) {
        App.getInstance().getApi()
                .getShortUrl(GET_SHORT_LINK_URL, new LongUrl(longUrl))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onComplete, this::onError);
    }

    private void onComplete(ShortUrl url) {
        if (onShortUrlReadyListener != null) onShortUrlReadyListener.onShortUrlReady(url.getId());
    }

    private void onError(Throwable t) {
        Log.e(TAG, "Can't short url", t);
        if (onShortUrlReadyListener != null)
            onShortUrlReadyListener.onGetShortUrlError(t.getMessage());
    }

    public interface OnShortUrlReadyListener {
        void onShortUrlReady(String shortUrl);

        void onGetShortUrlError(String errorString);
    }
}