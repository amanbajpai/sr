package com.ros.smartrocket.images;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private static final int STAGE_STEP = 20;
    private static int PADDING = 80;
    private static ImageLoader instance = null;
    private MemoryCache memoryCache = new MemoryCache();
    private FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;
    public int BIG_IMAGE = 0;
    public int NORMAL_IMAGE = 1;
    public int SMALL_IMAGE = 2;

    public int BIG_IMAGE_VAR = 3;
    public int NORMAL_IMAGE_VAR = 4;
    public int SMALL_IMAGE_VAR = 5;

    private int BIG_SIZE = 400;
    private int NORMAL_SIZE = 200;
    private int SMALL_SIZE = 100;

    private int BIG_SIZE_VAR = (int) App.getInstance().getResources().getDimension(R.dimen.big_image_size);
    private int NORMAL_SIZE_VAR = (int) App.getInstance().getResources().getDimension(R.dimen.normal_image_size);
    private int SMALL_SIZE_VAR = (int) App.getInstance().getResources().getDimension(R.dimen.small_image_size);

    public static interface OnFetchCompleteListener {
        public void onFetchComplete(Bitmap result);
    }

    public static ImageLoader getInstance() {
        if (instance == null) instance = new ImageLoader();
        return instance;
    }

    public ImageLoader() {
        // LogUtils.e(TAG, "new ImageLoader");
        fileCache = new FileCache();
        executorService = Executors.newFixedThreadPool(5);
    }

    final int loading_big = R.drawable.loading_big;
    final int loading_normal = R.drawable.loading_normal;
    final int loading_small = R.drawable.loading_small;
    final int no_image_id = R.drawable.no_image;

    public void DisplayImage(String url, ImageView imageView, int sizeType) {
        DisplayImage(url, imageView, sizeType, false, false, 0, true);
    }

    public void DisplayImageNoCache(String url, ImageView imageView, int sizeType) {
        DisplayImage(url, imageView, sizeType, false, false, 0, false);
    }

    public void DisplayImage(String url, ImageView imageView, int sizeType, int noImageRes) {
        DisplayImage(url, imageView, sizeType, false, false, noImageRes, true);
    }

    public void DisplayImage(String url, ImageView imageView, int sizeType, boolean needGone) {
        DisplayImage(url, imageView, sizeType, false, needGone, 0, true);
    }

    public void DisplayImage(String url, ImageView imageView, int sizeType, boolean needGone, boolean needAnimation) {
        DisplayImage(url, imageView, sizeType, needAnimation, needGone, 0, true);
    }

    public void DisplayImage(String url, ImageView imageView, int sizeType, boolean needAnimation, boolean needGone,
                             int noImageRes, boolean fromMemory) {
        if (url != null && !url.equals("")) {
            imageView.setVisibility(View.VISIBLE);
            imageViews.put(imageView, url);
            Bitmap bitmap = null;
            if (fromMemory) {
                bitmap = memoryCache.get(url);
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                if (needAnimation) {
                    Animation a = new AlphaAnimation(0.00f, 1.00f);
                    a.setDuration(1000);
                    imageView.startAnimation(a);
                }
            } else {
                if (sizeType == 0 || sizeType == 3) {
                    imageView.setImageResource(loading_big);
                } else if (sizeType == 1 || sizeType == 4) {
                    imageView.setImageResource(loading_normal);
                } else if (sizeType == 2 || sizeType == 5) {
                    imageView.setImageResource(loading_small);
                } else {
                    imageView.setImageResource(loading_small);
                }
                queuePhoto(url, imageView, sizeType, needAnimation, needGone);
            }
        } else {
            if (needGone) {
                imageView.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.VISIBLE);
                if (noImageRes != 0) {
                    imageView.setImageResource(noImageRes);
                } else {
                    imageView.setImageResource(no_image_id);
                }
            }
        }
    }

    public void LoadBitmap(String url, OnFetchCompleteListener onFetchCompleteListener) {
        LoadBitmap(url, NORMAL_IMAGE, onFetchCompleteListener);
    }

    public void LoadBitmap(String url, int sizeType, OnFetchCompleteListener onFetchCompleteListener) {
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            onFetchCompleteListener.onFetchComplete(bitmap);
        } else {
            executorService.submit(new BitmapLoader(url, sizeType, onFetchCompleteListener));
        }
    }

    private void queuePhoto(String url, ImageView imageView, int sizeType, boolean needAnimation, boolean needGone) {
        PhotoToLoad p = new PhotoToLoad(url, imageView, sizeType, needAnimation, needGone);
        executorService.submit(new PhotosLoader(p));
    }

    public Bitmap getBitmap(String url, ImageView imageView, int sizeType) {
        File f = getFileByUrl(url);

        Bitmap b = decodeFile(f, sizeType);
        if (imageView != null) {
            imageView.setTag(f.getAbsolutePath());
        }
        return b;

    }

    public File getFileByUrl(String url) {
        File f = fileCache.getFile(url);
        if (f.exists()) {
            return f;
        } else {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                Utils.CopyStream(is, os);
                os.close();
                return f;
            } catch (Exception e) {
                L.e(TAG, "getFile: " + e);
                return null;
            }
        }

    }

    public Bitmap getStageBitmap(String[] urlList, int sizeType) {
        int imageCount = urlList.length;
        ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

        for (int i = 0; i < imageCount; i++) {
            Bitmap bitmap = getBitmap(urlList[i], null, sizeType);
            bitmapList.add(bitmap);
        }
        return getStageBitmap(bitmapList, sizeType);
    }

    public Bitmap getStageBitmap(ArrayList<Bitmap> bitmapList, int sizeType) {
        Bitmap resultBitmap = null;

        switch (sizeType) {
            case 0:
                resultBitmap = Bitmap.createBitmap(BIG_SIZE, BIG_SIZE, Config.ARGB_8888);
                break;
            case 1:
                resultBitmap = Bitmap.createBitmap(NORMAL_SIZE, NORMAL_SIZE, Config.ARGB_8888);
                break;
            case 2:
                resultBitmap = Bitmap.createBitmap(SMALL_SIZE, SMALL_SIZE, Config.ARGB_8888);
                break;
            case 3:
                resultBitmap = Bitmap.createBitmap(BIG_SIZE_VAR, BIG_SIZE_VAR, Config.ARGB_8888);
                break;
            case 4:
                resultBitmap = Bitmap.createBitmap(NORMAL_SIZE_VAR, NORMAL_SIZE_VAR, Config.ARGB_8888);
                break;
            case 5:
                resultBitmap = Bitmap.createBitmap(SMALL_SIZE_VAR, SMALL_SIZE_VAR, Config.ARGB_8888);
                break;
        }

        Canvas canvas = new Canvas(resultBitmap);
        int stage = 0;
        for (Bitmap bitmap : bitmapList) {
            Bitmap bitmapToCanvas = Bitmap.createScaledBitmap(bitmap,
                    (int) (resultBitmap.getWidth() - (bitmapList.size() - 1) * STAGE_STEP),
                    (int) (resultBitmap.getHeight() - (bitmapList.size() - 1) * STAGE_STEP), false);
            canvas.drawBitmap(bitmapToCanvas, 0 + stage, 0 + stage, null);
            stage = stage + STAGE_STEP;
        }
        return resultBitmap;
    }

    public static Bitmap getCroppedBitmap(Bitmap source, double resultWidth, double resultHeight) {
        double coefficient;
        double sourceWidth = source.getWidth();
        double sourceHeight = source.getHeight();

        if (sourceHeight - resultHeight > sourceWidth - resultWidth) {
            coefficient = resultWidth / sourceWidth;
        } else {
            coefficient = resultHeight / sourceHeight;
        }

        sourceWidth = (coefficient * sourceWidth);
        sourceHeight = (coefficient * sourceHeight);

        int left = (int) -(sourceWidth - resultWidth) / 2;
        int top = (int) -(sourceHeight - resultHeight) / 2;

        Bitmap newSourceBitmap = Bitmap.createScaledBitmap(source, (int) sourceWidth, (int) sourceHeight, false);
        Bitmap bitmapToCanvas = Bitmap.createBitmap((int) resultWidth, (int) resultHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapToCanvas);
        canvas.drawBitmap(newSourceBitmap, left, top, null);
        return bitmapToCanvas;
    }

    // decodes photoMask and scales it to reduce memory consumption
    private Bitmap decodeFile(File f, int sizeType) {
        int bitmapSize = NORMAL_SIZE;
        int scale = 1;
        switch (sizeType) {
            case 0:
                bitmapSize = BIG_SIZE;
                break;
            case 1:
                bitmapSize = NORMAL_SIZE;
                break;
            case 2:
                bitmapSize = SMALL_SIZE;
                break;
            case 3:
                bitmapSize = BIG_SIZE_VAR;
                break;
            case 4:
                bitmapSize = NORMAL_SIZE_VAR;
                break;
            case 5:
                bitmapSize = SMALL_SIZE_VAR;
                break;
        }

        try {
            // decode photoMask size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(f.getAbsolutePath(), o);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;

            while (true) {
                if (width_tmp / 2 < bitmapSize || height_tmp / 2 < bitmapSize) break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // LogUtils.e(TAG,
            // "BIG_SIZE_VAR: "+BIG_SIZE_VAR+" NORMAL_SIZE_VAR: "+NORMAL_SIZE_VAR+" SMALL_SIZE_VAR: "+SMALL_SIZE_VAR);
            // LogUtils.e(TAG, "width_tmp: "+width_tmp+" height_tmp: "+height_tmp+" scale: "+scale);
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(f.getAbsolutePath(), o2);
        } catch (Exception e) {
        }
        return null;
    }

    public Bitmap decodeBitmap(Bitmap b, int sizeType) {
        int bitmapSize = NORMAL_SIZE;
        int scale = 1;
        switch (sizeType) {
            case 0:
                bitmapSize = BIG_SIZE;
                break;
            case 1:
                bitmapSize = NORMAL_SIZE;
                break;
            case 2:
                bitmapSize = SMALL_SIZE;
                break;
            case 3:
                bitmapSize = BIG_SIZE_VAR;
                break;
            case 4:
                bitmapSize = NORMAL_SIZE_VAR;
                break;
            case 5:
                bitmapSize = SMALL_SIZE_VAR;
                break;
        }

        try {

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = b.getWidth(), height_tmp = b.getHeight();
            while (true) {
                if (width_tmp < bitmapSize || height_tmp < bitmapSize) break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            return Bitmap.createScaledBitmap(b, b.getWidth() / scale, b.getHeight() / scale, true);
        } catch (Exception e) {
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public boolean anim;
        public boolean gone;
        public int sizeType;

        public PhotoToLoad(String u, ImageView i, int s, boolean a, boolean g) {
            this.url = u;
            this.imageView = i;
            this.anim = a;
            this.gone = g;
            this.sizeType = s;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad)) return;
            Bitmap bmp = null;
            String[] urlList = photoToLoad.url.split(",,");
            if (urlList.length > 1) {
                bmp = getStageBitmap(urlList, photoToLoad.sizeType);
            } else {
                bmp = getBitmap(photoToLoad.url, photoToLoad.imageView, photoToLoad.sizeType);
            }

            memoryCache.put(photoToLoad.url, bmp);

            if (imageViewReused(photoToLoad)) return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    class BitmapLoader implements Runnable {
        private String url;
        private OnFetchCompleteListener listener;
        private int sizeType;

        BitmapLoader(String url, int sizeType, OnFetchCompleteListener listener) {
            this.url = url;
            this.listener = listener;
            this.sizeType = sizeType;
        }

        @Override
        public void run() {
            Bitmap bmp = getBitmap(url, null, sizeType);
            memoryCache.put(url, bmp);

            if (listener != null) {
                listener.onFetchComplete(bmp);
            }

        }

    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url)) return true;
        return false;
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) return;
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
                if (photoToLoad.anim) {
                    Animation a = new AlphaAnimation(0.00f, 1.00f);
                    a.setDuration(500);
                    photoToLoad.imageView.startAnimation(a);
                }
            } else {
                if (photoToLoad.gone) {
                    photoToLoad.imageView.setVisibility(View.GONE);
                } else {
                    photoToLoad.imageView.setImageResource(no_image_id);
                }
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    public File getRawRsourceAsFile(Activity activity, int resId) {
        File f = fileCache.getFile(String.valueOf(resId));

        if (f.exists()) return f;

        InputStream is;
        OutputStream os;
        try {
            is = activity.getResources().openRawResource(resId);
            os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
        } catch (Exception e) {
            L.e(TAG, "error copyRsourceToFile");
        }

        return f;
    }

    public Bitmap getBitmapByPath(String path, int sizeType) {
        if (!TextUtils.isEmpty(path)) {
            return decodeFile(new File(path), sizeType);
        } else {
            return BitmapFactory.decodeResource(App.getInstance().getResources(), R.drawable.no_photo);
        }
    }

    public File getFileByBitmap(Bitmap bitmap) {
        File f = new File(fileCache.getCacheDir(), "photo_to_upload_" + UIUtils.getRandomInt(1000000) + ".png");

        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }
}