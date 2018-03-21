package com.ros.smartrocket.utils.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class WaveTypeIconTarget implements Target {
    private Activity activity;
    private ImageView imageView;

    public WaveTypeIconTarget(Activity activity, ImageView imageView) {
        this.activity = activity;
        this.imageView = imageView;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (activity != null)
            activity.runOnUiThread(() -> imageView.setImageBitmap(bitmap));
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        // do nothing
    }
}
