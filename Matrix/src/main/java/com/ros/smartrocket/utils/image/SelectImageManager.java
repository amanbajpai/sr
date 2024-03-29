package com.ros.smartrocket.utils.image;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.BytesBitmap;
import com.ros.smartrocket.utils.FileProcessingManager;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.StorageManager;
import com.ros.smartrocket.utils.eventbus.PhotoEvent;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Random;

import de.greenrobot.event.EventBus;

import static com.ros.smartrocket.utils.image.RequestCodeImageHelper.getLittlePart;

public class SelectImageManager {
    public static final String EXTRA_PREFIX = "com.ros.smartrocket.EXTRA_PREFIX";
    public static final String EXTRA_PHOTO_FILE = "com.ros.smartrocket.EXTRA_PHOTO_FILE";
    public static final String PREFIX_PROFILE = "profile";

    private static final String TAG = SelectImageManager.class.getSimpleName();

    /**
     * It is used binary mask for request codes. So only 4 bits for size allowed.
     */
    private static final int NONE = 0x0;
    private static final int GALLERY = 0x1;
    private static final int CAMERA = 0x2;
    private static final int CUSTOM_CAMERA = 0x3;

    private static final int HORIZONTAL = 1;
    private static final int VERTICAL = 2;
    private static final int[][] OPERATIONS = new int[][]{new int[]{0, NONE}, new int[]{0, HORIZONTAL},
            new int[]{180, NONE}, new int[]{180, VERTICAL}, new int[]{90, HORIZONTAL}, new int[]{90, NONE},
            new int[]{90, HORIZONTAL}, new int[]{0, NONE}, new int[]{90, NONE}};

    private final static PreferencesManager preferencesManager = PreferencesManager.getInstance();

    // Configuration
    private static final int MAX_SIZE_IN_PX = 700;
    public static final int SIZE_IN_PX_2_MP = 1600;
    public static final int SIZE_THUMB = 300;
    public static final long MAX_SIZE_IN_BYTE = 1 * 1000 * 1000;

    private static final int ONE_KB_IN_B = 1024;
    private static final Random RANDOM = new Random();

    /// ======================================================================================================= ///
    /// ============================================= PUBLIC METHODS ========================================== ///
    /// ======================================================================================================= ///

    public static void startGallery(Activity activity) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (!IntentUtils.isIntentAvailable(activity, i)) {
            i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("photo/*");
        }
        activity.startActivityForResult(i, GALLERY);
    }

    public static void startGallery(Fragment fragment) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (!IntentUtils.isIntentAvailable(fragment.getActivity(), i)) {
            i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("photo/*");
        }
        fragment.startActivityForResult(i, GALLERY);
    }

    public static void startCamera(Activity activity, File file) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, FileProcessingManager.getUriFromFile(file));
        activity.startActivityForResult(i, CAMERA);
    }

    public static void startCamera(Fragment sourceFragment, File file) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, FileProcessingManager.getUriFromFile(file));
        sourceFragment.startActivityForResult(i, CAMERA);
    }

    public static void startCamera(Fragment sourceFragment, File file, int bigPartCode) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, FileProcessingManager.getUriFromFile(file));
        sourceFragment.startActivityForResult(i, RequestCodeImageHelper.makeRequestCode(bigPartCode, CAMERA));
    }

    public static void startGallery(Fragment fragment, int bigPartCode) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (!IntentUtils.isIntentAvailable(fragment.getActivity(), i)) {
            i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("photo/*");
        }
        fragment.startActivityForResult(i, RequestCodeImageHelper.makeRequestCode(bigPartCode, GALLERY));
    }


    public void showSelectImageDialog(final Fragment fragment, final boolean showRemoveButton,
                                      final File file, int code) {
        showSelectImageDialog(showRemoveButton, file, fragment, null, code);
    }

    public void showSelectImageDialog(final Activity activity, final boolean showRemoveButton, final File file) {
        showSelectImageDialog(showRemoveButton, file, null, activity, 0);
    }

    private void showSelectImageDialog(final boolean showRemoveButton, final File file,
                                       final Fragment fragment, final Activity activity, int code) {
        Activity contextActivity = fragment != null ? fragment.getActivity() : activity;

        LayoutInflater inflater = (LayoutInflater) contextActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.select_image_dialog, null);

        final Dialog dialog = new Dialog(contextActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(v);

        v.findViewById(R.id.gallery).setOnClickListener(v1 -> {
            dialog.dismiss();
            if (fragment != null) {
                startGallery(fragment, code);
            } else {
                startGallery(activity);
            }
        });

        v.findViewById(R.id.camera).setOnClickListener(v12 -> {
            dialog.dismiss();
            if (fragment != null) {
                startCamera(fragment, file, code);
            } else {
                startCamera(activity, file);
            }
        });

        v.findViewById(R.id.remove).setVisibility(showRemoveButton ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.remove).setOnClickListener(v13 -> {
            dialog.dismiss();
            onImageRemoved();
        });

        v.findViewById(R.id.cancelButton).setOnClickListener(v14 -> dialog.dismiss());

        dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent, Context context) {
        if (resultCode == Activity.RESULT_OK) {
            new GetBitmapAsyncTask(requestCode, intent, context).execute();
        }
    }

    public static Bitmap prepareBitmap(File f) {
        return prepareBitmap(f, SIZE_IN_PX_2_MP);
    }

    public static Bitmap prepareBitmap(File f, int maxSizeInPx) {
        Bitmap resultBitmap = null;
        try {
            resultBitmap = getScaledBitmapByPxSize(f, maxSizeInPx);
            resultBitmap = rotateByExif(f, resultBitmap);
        } catch (Exception e) {
            L.e(TAG, "PrepareBitmap error" + e.getMessage(), e);
        }
        return resultBitmap;
    }

    public static File getScaledFile(File file, int maxSizeInPx) {
        Bitmap bitmap = prepareBitmap(file, maxSizeInPx);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            L.e(TAG, "GetScaledFile error" + e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    L.e(TAG, "GetScaledFile error" + e.getMessage(), e);
                }
            }
        }

        return file;
    }

    public static String getFileAsString(File file) {
        String resultString = "";
        try {
            if (file != null) {
                byte[] fileAsBytesArray = FileUtils.readFileToByteArray(file);
                resultString = Base64.encodeToString(fileAsBytesArray, 0);
            }
        } catch (Exception e) {
            L.e(TAG, "GetFileAsString error" + e.getMessage(), e);
        }
        return resultString;
    }

    public static File getTempFile(Context context, @Nullable String prefix) {
        File dir = StorageManager.getImageStoreDir(context);
        return new File(dir, prefix + "_" + Calendar.getInstance().getTimeInMillis() + "_"
                + RANDOM.nextInt(Integer.MAX_VALUE) + ".jpg");
    }

    /// ======================================================================================================= ///
    /// ============================================= PRIVATE METHODS ========================================= ///
    /// ======================================================================================================= ///

    private static ImageFileClass getBitmapFromGallery(Intent intent, Context context) {
        Bitmap resultBitmap = null;
        File lastFile = null;
        try {
            if (intent != null && intent.getData() != null) {
                final String prefix = intent.getStringExtra(EXTRA_PREFIX);
                Uri uri = intent.getData();
                if ("com.google.android.apps.photos.contentprovider".equals(uri.getAuthority())) {
                    try {
                        InputStream is = context.getContentResolver().openInputStream(uri);
                        if (is != null) {
                            Bitmap pictureBitmap = BitmapFactory.decodeStream(is);
                            lastFile = saveBitmapToFile(context, pictureBitmap, prefix);
                            rotateByExif(lastFile, pictureBitmap);
                            pictureBitmap.recycle();
                            resultBitmap = prepareBitmap(lastFile, SIZE_IN_PX_2_MP);
                            return new ImageFileClass(resultBitmap, lastFile);
                        }
                    } catch (FileNotFoundException e) {
                        L.e(TAG, "GetBitmapFromGallery error" + e.getMessage(), e);
                        return new ImageFileClass(null, null);
                    }
                }

                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

                if (cursor != null) {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(ImageColumns.DATA);
                    String imagePath;

                    if (idx != -1) {
                        imagePath = cursor.getString(idx);
                        cursor.close();
                    } else {
                        imagePath = intent.getData().getLastPathSegment();
                    }

                    if (imagePath.startsWith("http")) {
                        Bitmap image = Picasso.get().load(imagePath).resize(SIZE_IN_PX_2_MP, SIZE_IN_PX_2_MP).get();
                        lastFile = saveBitmapToFile(context, image, prefix);
                        rotateByExif(lastFile, image);
                        image.recycle();
                        resultBitmap = prepareBitmap(lastFile, MAX_SIZE_IN_PX);
                    } else {
                        lastFile = copyFileToTempFolder(context, new File(imagePath), prefix);
                        resultBitmap = prepareBitmap(lastFile, MAX_SIZE_IN_PX);
                    }
                } else {
                    ParcelFileDescriptor parcelFileDescriptor
                            = context.getContentResolver().openFileDescriptor(intent.getData(), "r");
                    Bitmap image = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor());
                    parcelFileDescriptor.close();

                    lastFile = saveBitmapToFile(context, image, prefix);
                    rotateByExif(lastFile, image);
                    image.recycle();
                    resultBitmap = prepareBitmap(lastFile, MAX_SIZE_IN_PX);
                }
            }
        } catch (Exception e) {
            L.e(TAG, "GetBitmapFromGallery error" + e.getMessage(), e);
        }
        return new ImageFileClass(resultBitmap, lastFile);
    }

    private static ImageFileClass getBitmapFromCamera(Intent intent, Context context) {
        InputStream is = null;
        FileOutputStream fos = null;
        OutputStream os = null;
        File file = null;

        if (intent != null) {
            file = (File) intent.getSerializableExtra(EXTRA_PHOTO_FILE);
        }

        if (file != null && !file.exists()) {
            try {
                Uri u = intent.getData();
                is = context.getContentResolver().openInputStream(u);
                fos = new FileOutputStream(file, false);
                os = new BufferedOutputStream(fos);
                byte[] buffer = new byte[ONE_KB_IN_B];
                int byteRead;
                while ((byteRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, byteRead);
                }
            } catch (Exception e) {
                L.e(TAG, "GetBitmapFromCamera error" + e.getMessage(), e);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (Exception e) {
                    L.e(TAG, "GetScaledFile error" + e.getMessage(), e);
                }
            }
        }

        return new ImageFileClass(prepareBitmap(file, SIZE_IN_PX_2_MP), file);
    }

    private static Bitmap getScaledBitmapByPxSize(File f, int maxSizeInPx) {
        int scale = 1;
        try {

            boolean isCompress = PreferencesManager.getInstance().getBoolean(Keys.IS_COMPRESS_PHOTO, true);
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            try {
                BitmapFactory.decodeFile(f.getPath(), o);
            } catch (OutOfMemoryError e) {
                try {
                    o.inSampleSize = 2;
                    BitmapFactory.decodeFile(f.getPath(), o);
                } catch (Exception e1) {
                    e.printStackTrace();
                }
            }

            if (isCompress) {
                if (o.outHeight > maxSizeInPx || o.outWidth > maxSizeInPx) {
                    double maxSourceSideSize = (double) Math.max(o.outHeight, o.outWidth);
                    scale = (int) Math.pow(2,
                            (int) Math.round(Math.log(maxSizeInPx / maxSourceSideSize) / Math.log(0.5)));
                }
            }

            L.e(TAG, "getScaledBitmapBySideSize Scale: " + scale);
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(f.getPath(), o2);
        } catch (Exception e) {
            L.e(TAG, "getScaledBitmapByPxSize" + e.getMessage(), e);
        }
        return null;
    }

    private static Bitmap getScaledBitmapByByteSize(Bitmap sourceBitmap, long maxSizeInByte) {
        try {
            long sourceBitmapByte = BytesBitmap.getBytes(sourceBitmap).length;

            if (sourceBitmapByte > maxSizeInByte) {
                int scale = (int) Math.pow(2,
                        (int) Math.round(Math.log(maxSizeInByte / (double) sourceBitmapByte) / Math.log(0.5)));

                L.e(TAG, "getScaledBitmapByByteSize Scale: " + scale);

                int resultWidth = sourceBitmap.getWidth() / scale;
                int resultHeight = sourceBitmap.getHeight() / scale;

                return Bitmap.createScaledBitmap(sourceBitmap, resultWidth, resultHeight, false);
            }
        } catch (Exception e) {
            L.e(TAG, "getScaledBitmapByByteSize" + e.getMessage(), e);
        }
        return sourceBitmap;
    }

    private static Bitmap rotateByExif(File file, Bitmap bitmap) {
        try {
            ExifInterface oldExif = new ExifInterface(file.getAbsolutePath());
            final int rotation = Integer.valueOf(oldExif.getAttribute(ExifInterface.TAG_ORIENTATION));
            final int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotationInDegrees != 0) matrix.preRotate(rotationInDegrees);
            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (rotationInDegrees != 0) saveBitmapToFile(result, file);
            return result;
        } catch (Exception e) {
            L.e(TAG, "RotateByExif error" + e.getMessage(), e);
        }

        return bitmap;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private static File copyFileToTempFolder(Context context, File file, String prefix) {
        File resultFile = getTempFile(context, prefix);

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new FileOutputStream(resultFile);

            // Transfer bytes from in to out
            byte[] buf = new byte[ONE_KB_IN_B];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

        } catch (Exception e) {
            L.e(TAG, "CopyFileToTempFolder error" + e.getMessage(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                L.e(TAG, "GetScaledFile error" + e.getMessage(), e);
            }

        }
        return resultFile;
    }

    public static File saveBitmapToFile(Context context, Bitmap bitmap, @Nullable String prefix) {
        File resultFile = getTempFile(context, prefix);

        try {
            FileOutputStream fos = new FileOutputStream(resultFile, false);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resultFile;
    }

    public static void saveBitmapToFile(Bitmap bitmap, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /// ======================================================================================================= ///
    /// =========================================== INTERNAL CLASSES ========================================== ///
    /// ======================================================================================================= ///

    public class GetBitmapAsyncTask extends AsyncTask<Void, Void, ImageFileClass> {
        private Intent intent;
        private int requestCode;
        private Context context;

        public GetBitmapAsyncTask(int requestCode, Intent intent, Context context) {
            this.intent = intent;
            this.requestCode = requestCode;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            onImageStartLoading();
        }

        @Override
        protected ImageFileClass doInBackground(Void... params) {
            ImageFileClass image = null;
            int littleRequestCode = getLittlePart(requestCode);
            onImageStartLoading();
            if (littleRequestCode == SelectImageManager.GALLERY) {
                image = getBitmapFromGallery(intent, context);
            } else if (littleRequestCode == SelectImageManager.CAMERA
                    || littleRequestCode == SelectImageManager.CUSTOM_CAMERA) {
                image = getBitmapFromCamera(intent, context);

                if (preferencesManager.getUseSaveImageToCameraRoll() &&
                        context != null && context.getContentResolver() != null && image != null) {
//                    MediaStore.Images.Media.insertImage(context.getContentResolver(), image.bitmap, "", "");
                    storeImageInSDCard("", image.bitmap);
                }
            }

            return image;
        }

        @Override
        protected void onPostExecute(ImageFileClass image) {
            if (image != null && image.bitmap != null && image.imageFile != null) {
                onImageCompleteLoading(image, requestCode);
            } else {
                onImageErrorLoading();
            }
        }
    }

    public void storeImageInSDCard(@Nullable String prefix, Bitmap bitmap) {
        File dir = StorageManager.getImageDir();
        try {
            File finalImage = new File(dir, prefix + "_" + Calendar.getInstance().getTimeInMillis() + "_"
                    + RANDOM.nextInt(Integer.MAX_VALUE) + ".jpg");
            FileOutputStream out = new FileOutputStream(finalImage);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static class ImageFileClass {
        public final Bitmap bitmap;
        public final File imageFile;

        public ImageFileClass(Bitmap bitmap, File imageFile) {
            this.bitmap = bitmap;
            this.imageFile = imageFile;
        }
    }

    protected void onImageStartLoading() {
        EventBus.getDefault().post(new PhotoEvent(PhotoEvent.PhotoEventType.START_LOADING));
    }

    protected void onImageCompleteLoading(ImageFileClass image, int requestCode) {
        EventBus.getDefault().post(new PhotoEvent(PhotoEvent.PhotoEventType.IMAGE_COMPLETE, image, requestCode));
    }

    protected void onImageErrorLoading() {
        EventBus.getDefault().post(new PhotoEvent(PhotoEvent.PhotoEventType.SELECT_IMAGE_ERROR));
    }

    protected void onImageRemoved() {
        EventBus.getDefault().post(new PhotoEvent(PhotoEvent.PhotoEventType.IMAGE_COMPLETE));
    }

}