package com.ros.smartrocket.helpers;

import android.graphics.drawable.Animatable;
import android.os.Handler;


public class AVDWrapper {

    private Handler handler;
    private Animatable animatable;
    private Callback callback;
    private Runnable mAnimationDoneRunnable = new Runnable() {
        @Override
        public void run() {
            if (callback != null)
                callback.onAnimationDone();
        }
    };

    public AVDWrapper(Animatable animatable) {
        this.animatable = animatable;
        handler = new Handler();
    }

    public void start(long duration) {
        animatable.start();
        handler.postDelayed(mAnimationDoneRunnable, duration);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onAnimationDone();
    }
}