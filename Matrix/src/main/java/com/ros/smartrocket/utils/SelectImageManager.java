package com.ros.smartrocket.utils;

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
import android.view.View.OnClickListener;
import android.view.Window;
import com.ros.smartrocket.R;
import com.ros.smartrocket.eventbus.PhotoEvent;
import de.greenrobot.event.EventBus;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Calendar;
import java.util.Random;

import static com.squareup.picasso.Picasso.with;

public final class SelectImageManager {
    public static final String EXTRA_PREFIX = "com.ros.smartrocket.EXTRA_PREFIX";
    public static final String EXTRA_PHOTO_FILE = "com.ros.smartrocket.EXTRA_PHOTO_FILE";
    public static final String PREFIX_PROFILE = "profile";

    private static final String TAG = SelectImageManager.class.getSimpleName();
    private static final int NONE = 0;
    private static final int GALLERY = 101;
    private static final int CAMERA = 102;
    private static final int CUSTOM_CAMERA = 103;

    private static final int HORIZONTAL = 1;
    private static final int VERTICAL = 2;
    private static final int[][] OPERATIONS = new int[][]{new int[]{0, NONE}, new int[]{0, HORIZONTAL},
            new int[]{180, NONE}, new int[]{180, VERTICAL}, new int[]{90, HORIZONTAL}, new int[]{90, NONE},
            new int[]{90, HORIZONTAL}, new int[]{0, NONE}, new int[]{90, NONE}};

    private final static PreferencesManager preferencesManager = PreferencesManager.getInstance();

    // Configuration
    private static final int MAX_SIZE_IN_PX = 700;
    public static final int SIZE_IN_PX_2_MP = 1600;
    public static final long MAX_SIZE_IN_BYTE = 1 * 1000 * 1000;

    private static final int ONE_KB_IN_B = 1024;
    private static final Random RANDOM = new Random();

    private SelectImageManager() {
    }

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

    public static void startCamera(Activity activity, File filePath) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePath));
        activity.startActivityForResult(i, CAMERA);
    }

    public static void startCamera(Fragment sourceFragment, File filePath) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePath));
        sourceFragment.startActivityForResult(i, CAMERA);
    }

    public static void showSelectImageDialog(final Fragment fragment, final boolean showRemoveButton,
                                              final File file) {
        showSelectImageDialog(showRemoveButton, file, fragment, null);
    }

    public static void showSelectImageDialog(final Activity activity, final boolean showRemoveButton, final File file) {
        showSelectImageDialog(showRemoveButton, file, null, activity);
    }

    private static void showSelectImageDialog(final boolean showRemoveButton, final File file,
                                             final Fragment fragment, final Activity activity) {
        Activity contextActivity = fragment != null ? fragment.getActivity() : activity;

        LayoutInflater inflater = (LayoutInflater) contextActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.select_image_dialog, null);

        final Dialog dialog = new Dialog(contextActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(v);

        v.findViewById(R.id.gallery).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (fragment != null) {
                    startGallery(fragment);
                } else {
                    startGallery(activity);
                }
            }
        });

        v.findViewById(R.id.camera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (fragment != null) {
                    startCamera(fragment, file);
                } else {
                    startCamera(activity, file);
                }
            }
        });

        v.findViewById(R.id.remove).setVisibility(showRemoveButton ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.remove).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                EventBus.getDefault().post(new PhotoEvent(PhotoEvent.PhotoEventType.IMAGE_COMPLETE));
            }
        });

        v.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent intent, Context context) {
        if (resultCode == Activity.RESULT_OK) {
            new GetBitmapAsyncTask(requestCode, intent, context).execute();
        }
    }

    public static Bitmap prepareBitmap(File f) {
        return prepareBitmap(f, MAX_SIZE_IN_PX, MAX_SIZE_IN_BYTE, false);
    }

    public static Bitmap prepareBitmap(File f, int maxSizeInPx, long maxSizeInByte, boolean rotateByExif) {
        Bitmap resultBitmap = null;
        try {
            L.i(TAG, "Source file size: " + f.length() + "bytes");
            resultBitmap = getScaledBitmapByPxSize(f, maxSizeInPx);

            if (maxSizeInByte > 0) {
                resultBitmap = getScaledBitmapByByteSize(resultBitmap, maxSizeInByte);
            }

            if (rotateByExif) {
                resultBitmap = rotateByExif(f.getAbsolutePath(), resultBitmap);
            }
        } catch (Exception e) {
            L.e(TAG, "PrepareBitmap error" + e.getMessage(), e);
        }
        return resultBitmap;
    }

    public static File getScaledFile(File file, int maxSizeInPx, long maxSizeInByte, boolean lastFileFromGallery) {
        Bitmap bitmap = prepareBitmap(file, maxSizeInPx, maxSizeInByte, !lastFileFromGallery);

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
                    String unusablePath = intent.getData().getPath();
                    int startIndex = unusablePath.indexOf("external/");
                    int endIndex = unusablePath.indexOf("/ACTUAL");
                    String embeddedPath = unusablePath.substring(startIndex, endIndex);

                    Uri.Builder builder = intent.getData().buildUpon();
                    builder.path(embeddedPath);
                    builder.authority("media");
                    uri = builder.build();
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
                        Bitmap image = with(context).load(imagePath).resize(MAX_SIZE_IN_PX, MAX_SIZE_IN_PX).get();
                        lastFile = saveBitmapToFile(context, image, prefix);
                        image.recycle();

                        resultBitmap = prepareBitmap(lastFile, MAX_SIZE_IN_PX, MAX_SIZE_IN_BYTE, true);
                    } else {
                        lastFile = copyFileToTempFolder(context, new File(imagePath), prefix);
                        resultBitmap = prepareBitmap(lastFile, MAX_SIZE_IN_PX, MAX_SIZE_IN_BYTE, true);
                    }
                } else {
                    ParcelFileDescriptor parcelFileDescriptor
                            = context.getContentResolver().openFileDescriptor(intent.getData(), "r");
                    Bitmap image = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor());
                    parcelFileDescriptor.close();

                    lastFile = saveBitmapToFile(context, image, prefix);
                    image.recycle();
                    resultBitmap = prepareBitmap(lastFile, MAX_SIZE_IN_PX, MAX_SIZE_IN_BYTE, true);
                }
            }
        } catch (Exception e) {
            L.e(TAG, "GetBitmapFromGallery error" + e.getMessage(), e);
        }
        return new ImageFileClass(resultBitmap, lastFile, true);
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

        return new ImageFileClass(prepareBitmap(file, MAX_SIZE_IN_PX, MAX_SIZE_IN_BYTE, true), file, false);
    }

    private static Bitmap getScaledBitmapByPxSize(File f, int maxSizeInPx) {
        int scale = 1;
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(f.getAbsolutePath(), o);

            if (o.outHeight > maxSizeInPx || o.outWidth > maxSizeInPx) {
                double maxSourceSideSize = (double) Math.max(o.outHeight, o.outWidth);
                scale = (int) Math.pow(2,
                        (int) Math.round(Math.log(maxSizeInPx / maxSourceSideSize) / Math.log(0.5)));
            }

            L.e(TAG, "getScaledBitmapBySideSize Scale: " + scale);

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(f.getAbsolutePath(), o2);
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

    private static Bitmap rotateByExif(String imagePath, Bitmap bitmap) {
        try {
            ExifInterface oldExif = new ExifInterface(imagePath);
            int index = Integer.valueOf(oldExif.getAttribute(ExifInterface.TAG_ORIENTATION));
            int degrees = OPERATIONS[index][0];

            Matrix matrix = new Matrix();

            if (index == 8) {
                float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                matrix = new Matrix();

                Matrix matrixMirrorY = new Matrix();
                matrixMirrorY.setValues(mirrorY);

                matrix.postConcat(matrixMirrorY);

                matrix.postRotate(degrees);
            } else {
                matrix.postRotate(degrees);
            }

            /*if (degrees == 0) {
                return bitmap;
            } else {*/
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            //}
        } catch (Exception e) {
            L.e(TAG, "RotateByExif error" + e.getMessage(), e);
        }

        return bitmap;
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

    private static File saveBitmapToFile(Context context, Bitmap bitmap, @Nullable String prefix) {
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

    /// ======================================================================================================= ///
    /// =========================================== INTERNAL CLASSES ========================================== ///
    /// ======================================================================================================= ///

    public static class GetBitmapAsyncTask extends AsyncTask<Void, Void, ImageFileClass> {
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
            EventBus.getDefault().post(new PhotoEvent(PhotoEvent.PhotoEventType.START_LOADING));
        }

        @Override
        protected ImageFileClass doInBackground(Void... params) {
            ImageFileClass image = null;
            if (requestCode == SelectImageManager.GALLERY) {
                image = getBitmapFromGallery(intent, context);

            } else if (requestCode == SelectImageManager.CAMERA || requestCode == SelectImageManager.CUSTOM_CAMERA) {
                image = getBitmapFromCamera(intent, context);

                if (preferencesManager.getUseSaveImageToCameraRoll() &&
                        context != null && context.getContentResolver() != null && image != null) {
                    MediaStore.Images.Media.insertImage(context.getContentResolver(), image.bitmap, "", "");
                }
            }

            return image;
        }

        @Override
        protected void onPostExecute(ImageFileClass image) {
            if (image != null && image.bitmap != null && image.imageFile != null) {
                EventBus.getDefault().post(new PhotoEvent(PhotoEvent.PhotoEventType.IMAGE_COMPLETE, image));
            } else {
                EventBus.getDefault().post(new PhotoEvent(PhotoEvent.PhotoEventType.SELECT_IMAGE_ERROR, requestCode));
            }
        }
    }

    public static class ImageFileClass {
        public final Bitmap bitmap;
        public final File imageFile;
        public final boolean isFileFromGallery;

        public ImageFileClass(Bitmap bitmap, File imageFile, boolean isFileFromGallery) {
            this.bitmap = bitmap;
            this.imageFile = imageFile;
            this.isFileFromGallery = isFileFromGallery;
        }
    }
}