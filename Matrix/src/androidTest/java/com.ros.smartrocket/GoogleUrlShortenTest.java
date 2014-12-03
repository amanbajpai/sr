package com.ros.smartrocket;

import android.content.Context;
import android.text.TextUtils;

import com.ros.smartrocket.activity.LaunchActivity;
import com.ros.smartrocket.utils.GoogleUrlShortenManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GoogleUrlShortenTest {
    private LaunchActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(LaunchActivity.class).get();
    }

    @Test
    public void testUrlShorten() {
        String longSharedLink = "www.google.com";

        GoogleUrlShortenManager googleUrlShortenManager = GoogleUrlShortenManager.getInstance();

        googleUrlShortenManager.getShortUrl(activity, longSharedLink,
                new GoogleUrlShortenManager.OnShotrUrlReadyListener() {
                    @Override
                    public void onShortUrlReady(String shortUrl) {
                        System.out.println("[short url=" + shortUrl + "]");
                        Assert.assertTrue(!TextUtils.isEmpty(shortUrl));
                    }

                    @Override
                    public void onGetShortUrlError(String errorString) {
                        System.out.println("[error=" + errorString + "]");
                        Assert.assertTrue(false);
                    }
                });


    }
}
