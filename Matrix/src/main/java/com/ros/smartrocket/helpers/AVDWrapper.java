package com.ros.smartrocket.helpers;

import android.graphics.drawable.Animatable;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;


public class AVDWrapper {

    private Handler handler;
    private AppCompatImageView imageView;
    private AnimationCallback callback;
    private Runnable mAnimationDoneRunnable = new Runnable() {
        @Override
        public void run() {
            if (callback != null) {
                callback.onAnimationDone(imageView);
            }
        }
    };

    public AVDWrapper() {
        handler = new Handler();
    }

    public void start(long duration, AppCompatImageView imageView) {
        this.imageView = imageView;
        ((Animatable) imageView.getDrawable()).start();
        handler.postDelayed(mAnimationDoneRunnable, duration);
    }

    public void setCallback(AnimationCallback callback) {
        this.callback = callback;
    }

    public interface AnimationCallback {
        void onAnimationDone(AppCompatImageView imageView);
    }
}