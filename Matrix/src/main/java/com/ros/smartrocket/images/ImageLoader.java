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
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private static final int STAGE_STEP = 20;
    private static final int THREAD_COUNT = 5;
    private static final int ANIMATION_DURATION = 1000;
    private static final int TIMEOUT = 30000;
    private static final int MAX_RANDOM_ID = 1000000;
    private static final int COMPRESS_IMAGE = 100;
    private static ImageLoader instance = null;
    private MemoryCache memoryCache = new MemoryCache();
    private FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;
    public static final int BIG_IMAGE = 0;
    public static final int NORMAL_IMAGE = 1;
    public static final int SMALL_IMAGE = 2;

    public static final int BIG_IMAGE_VAR = 3;
    public static final int NORMAL_IMAGE_VAR = 4;
    public static final int SMALL_IMAGE_VAR = 5;

    private static final int BIG_SIZE = 400;
    private static final int NORMAL_SIZE = 200;
    private static final int SMALL_SIZE = 100;

    private static final int BIG_SIZE_VAR = (int) App.getInstance().getResources().getDimension(R.dimen
            .big_image_size);
    private static final int NORMAL_SIZE_VAR = (int) App.getInstance().getResources().getDimension(R.dimen
            .normal_image_size);
    private static final int SMALL_SIZE_VAR = (int) App.getInstance().getResources().getDimension(R.dimen
            .small_image_size);

    private static final int LOADING_BIG_IMAGE_RES_ID = R.drawable.loading_big;
    private static final int LOADING_NORMAL_IMAGE_RES_ID = R.drawable.loading_normal;
    private static final int LOADING_SMALL_IMAGE_RES_ID = R.drawable.loading_small;
    private static final int NO_IMAGE_RES_ID = R.drawable.no_image;

    public ImageLoader() {
        fileCache = new FileCache();
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    public void displayImage(String url, ImageView imageView, int sizeType, boolean needAnimation, boolean needGone,
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
                    a.setDuration(ANIMATION_DURATION);
                    imageView.startAnimation(a);
                }
            } else {
                if (sizeType == BIG_IMAGE || sizeType == BIG_IMAGE_VAR) {
                    imageView.setImageResource(LOADING_BIG_IMAGE_RES_ID);
                } else if (sizeType == NORMAL_IMAGE || sizeType == NORMAL_IMAGE_VAR) {
                    imageView.setImageResource(LOADING_NORMAL_IMAGE_RES_ID);
                } else if (sizeType == SMALL_IMAGE || sizeType == SMALL_IMAGE_VAR) {
                    imageView.setImageResource(LOADING_SMALL_IMAGE_RES_ID);
                } else {
                    imageView.setImageResource(LOADING_SMALL_IMAGE_RES_ID);
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
                    imageView.setImageResource(NO_IMAGE_RES_ID);
                }
            }
        }
    }

    public void loadBitmap(String url, OnFetchCompleteListener onFetchCompleteListener) {
        loadBitmap(url, NORMAL_IMAGE, onFetchCompleteListener);
    }

    public void loadBitmap(String url, int sizeType, OnFetchCompleteListener onFetchCompleteListener) {
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
                conn.setConnectTimeout(TIMEOUT);
                conn.setReadTimeout(TIMEOUT);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                Utils.copyStream(is, os);
                os.close();
                return f;
            } catch (Exception e) {
                L.e(TAG, "getFile: " + url, e);
                return null;
            }
        }

    }

    public Bitmap getStageBitmap(String[] urlList, int sizeType) {
        int imageCount = urlList.length;
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();

        for (int i = 0; i < imageCount; i++) {
            Bitmap bitmap = getBitmap(urlList[i], null, sizeType);
            bitmapList.add(bitmap);
        }
        return getStageBitmap(bitmapList, sizeType);
    }

    public Bitmap getStageBitmap(List<Bitmap> bitmapList, int sizeType) {
        Bitmap resultBitmap = null;

        switch (sizeType) {
            case BIG_IMAGE:
                resultBitmap = Bitmap.createBitmap(BIG_SIZE, BIG_SIZE, Config.ARGB_8888);
                break;
            case NORMAL_IMAGE:
                resultBitmap = Bitmap.createBitmap(NORMAL_SIZE, NORMAL_SIZE, Config.ARGB_8888);
                break;
            case SMALL_IMAGE:
                resultBitmap = Bitmap.createBitmap(SMALL_SIZE, SMALL_SIZE, Config.ARGB_8888);
                break;
            case BIG_IMAGE_VAR:
                resultBitmap = Bitmap.createBitmap(BIG_SIZE_VAR, BIG_SIZE_VAR, Config.ARGB_8888);
                break;
            case NORMAL_IMAGE_VAR:
                resultBitmap = Bitmap.createBitmap(NORMAL_SIZE_VAR, NORMAL_SIZE_VAR, Config.ARGB_8888);
                break;
            case SMALL_IMAGE_VAR:
                resultBitmap = Bitmap.createBitmap(SMALL_SIZE_VAR, SMALL_SIZE_VAR, Config.ARGB_8888);
                break;
            default:
                break;
        }

        Canvas canvas = new Canvas(resultBitmap);
        int stage = 0;
        for (Bitmap bitmap : bitmapList) {
            Bitmap bitmapToCanvas = Bitmap.createScaledBitmap(bitmap,
                    resultBitmap.getWidth() - (bitmapList.size() - 1) * STAGE_STEP,
                    resultBitmap.getHeight() - (bitmapList.size() - 1) * STAGE_STEP, false);
            canvas.drawBitmap(bitmapToCanvas, stage, stage, null);
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
            case BIG_IMAGE:
                bitmapSize = BIG_SIZE;
                break;
            case NORMAL_IMAGE:
                bitmapSize = NORMAL_SIZE;
                break;
            case SMALL_IMAGE:
                bitmapSize = SMALL_SIZE;
                break;
            case BIG_IMAGE_VAR:
                bitmapSize = BIG_SIZE_VAR;
                break;
            case NORMAL_IMAGE_VAR:
                bitmapSize = NORMAL_SIZE_VAR;
                break;
            case SMALL_IMAGE_VAR:
                bitmapSize = SMALL_SIZE_VAR;
                break;
            default:
                break;
        }

        try {
            // decode photoMask size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(f.getAbsolutePath(), o);

            // Find the correct scale value. It should be the power of 2.
            int widthTmp = o.outWidth;
            int heightTmp = o.outHeight;

            while (true) {
                if (widthTmp / 2 < bitmapSize || heightTmp / 2 < bitmapSize) {
                    break;
                }
                widthTmp /= 2;
                heightTmp /= 2;
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(f.getAbsolutePath(), o2);
        } catch (Exception e) {
            L.e(TAG, "DecodeFile error" + e.getMessage(), e);
        }
        return null;
    }

    public Bitmap decodeBitmap(Bitmap b, int sizeType) {
        int bitmapSize = NORMAL_SIZE;
        int scale = 1;
        switch (sizeType) {
            case BIG_IMAGE:
                bitmapSize = BIG_SIZE;
                break;
            case NORMAL_IMAGE:
                bitmapSize = NORMAL_SIZE;
                break;
            case SMALL_IMAGE:
                bitmapSize = SMALL_SIZE;
                break;
            case BIG_IMAGE_VAR:
                bitmapSize = BIG_SIZE_VAR;
                break;
            case NORMAL_IMAGE_VAR:
                bitmapSize = NORMAL_SIZE_VAR;
                break;
            case SMALL_IMAGE_VAR:
                bitmapSize = SMALL_SIZE_VAR;
                break;
            default:
                break;
        }

        try {
            // Find the correct scale value. It should be the power of 2.
            int widthTmp = b.getWidth(), heightTmp = b.getHeight();
            while (true) {
                if (widthTmp < bitmapSize || heightTmp < bitmapSize) {
                    break;
                }
                widthTmp /= 2;
                heightTmp /= 2;
                scale *= 2;
            }

            return Bitmap.createScaledBitmap(b, b.getWidth() / scale, b.getHeight() / scale, true);
        } catch (Exception e) {
            L.e(TAG, "DecodeBitmap error" + e.getMessage(), e);
        }
        return null;
    }

    // Task for the queue
    private static class PhotoToLoad {
        private String url;
        private ImageView imageView;
        private boolean anim;
        private boolean gone;
        private int sizeType;

        public PhotoToLoad(String u, ImageView i, int s, boolean a, boolean g) {
            this.url = u;
            this.imageView = i;
            this.anim = a;
            this.gone = g;
            this.sizeType = s;
        }
    }

    class PhotosLoader implements Runnable {
        private PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            Bitmap bmp;
            String[] urlList = photoToLoad.url.split(",,");
            if (urlList.length > 1) {
                bmp = getStageBitmap(urlList, photoToLoad.sizeType);
            } else {
                bmp = getBitmap(photoToLoad.url, photoToLoad.imageView, photoToLoad.sizeType);
            }

            memoryCache.put(photoToLoad.url, bmp);

            if (imageViewReused(photoToLoad)) {
                return;
            }
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
        if (tag == null || !tag.equals(photoToLoad.url)) {
            return true;
        }
        return false;
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        private Bitmap bitmap;
        private PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
                if (photoToLoad.anim) {
                    Animation a = new AlphaAnimation(0.00f, 1.00f);
                    a.setDuration(ANIMATION_DURATION);
                    photoToLoad.imageView.startAnimation(a);
                }
            } else {
                if (photoToLoad.gone) {
                    photoToLoad.imageView.setVisibility(View.GONE);
                } else {
                    photoToLoad.imageView.setImageResource(NO_IMAGE_RES_ID);
                }
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    public File getRawResourceAsFile(Activity activity, int resId) {
        File f = fileCache.getFile(String.valueOf(resId));

        if (f.exists()) {
            return f;
        }

        InputStream is;
        OutputStream os;
        try {
            is = activity.getResources().openRawResource(resId);
            os = new FileOutputStream(f);
            Utils.copyStream(is, os);
            os.close();
        } catch (Exception e) {
            L.e(TAG, "GetRawResourceAsFile error" + e.getMessage(), e);
        }

        return f;
    }

    public Bitmap getBitmapByPath(String path, int sizeType) {
        if (!TextUtils.isEmpty(path)) {
            return decodeFile(new File(path), sizeType);
        } else {
            return BitmapFactory.decodeResource(App.getInstance().getResources(), R.drawable.no_image);
        }
    }

    public File getFileByBitmap(Bitmap bitmap) {
        File f = new File(fileCache.getCacheDir(), "photo_to_upload_" + UIUtils.getRandomInt(MAX_RANDOM_ID) + ".png");

        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_IMAGE, out);
            out.close();
        } catch (Exception e) {
            L.e(TAG, "GetFileByBitmap error" + e.getMessage(), e);
        }
        return f;
    }

    public interface OnFetchCompleteListener {
        void onFetchComplete(Bitmap result);
    }
}