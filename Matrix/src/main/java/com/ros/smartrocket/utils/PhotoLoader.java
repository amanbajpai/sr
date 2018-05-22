package com.ros.smartrocket.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Created by ankurrawal on 18/5/18.
 */


public class PhotoLoader {
    private static String imageSavedPath = null;
    private static Bitmap bitmapLoaded = null;

    public static String SaveImageToDisc(String image_url, String imageDir, final String imageName) {
        Picasso.get()
                .load(image_url)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try {
                            File myDir = new File(imageDir);
                            if (!myDir.exists()) {
                                myDir.mkdirs();
                            }
                            String name = imageName + ".jpg";
                            myDir = new File(myDir, name);
                            FileOutputStream out = new FileOutputStream(myDir);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                            out.flush();
                            out.close();
                            imageSavedPath = myDir.getAbsolutePath();
                            Log.v("OkHttp", imageSavedPath);
                        } catch (Exception e) {
                            // some action
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        return imageSavedPath;
    }


    public static Bitmap getBitmapFromUrl(String image_url, Target target) {

        Picasso.get()
                .load(image_url)
                .into(target);
        return bitmapLoaded;
    }


}