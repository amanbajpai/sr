package com.ros.smartrocket.utils.image;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.ros.smartrocket.presentation.base.BaseActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ActionBarIconTarget implements Target {
    BaseActivity activity;

    public ActionBarIconTarget(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (activity != null && activity.getSupportActionBar() != null)
            activity.runOnUiThread(() -> {
                Drawable drawable = new BitmapDrawable(activity.getResources(), bitmap);
                activity.getSupportActionBar().setLogo(drawable);
            });
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        // do nothing
    }
}
