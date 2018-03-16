package com.ros.smartrocket.utils.image;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

public class PaymentImageTarget implements Target {
    private Context context;
    private ImageView imageView;

    public PaymentImageTarget(Context context, ImageView imageView) {
        this.context = context;
        this.imageView = imageView;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        File file = SelectImageManager.saveBitmapToFile(context, bitmap, "");
        Bitmap result = SelectImageManager.prepareBitmap(file, 500, 0);
        imageView.setImageBitmap(result);
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
