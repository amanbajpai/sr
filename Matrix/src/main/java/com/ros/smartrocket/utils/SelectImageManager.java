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
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class SelectImageManager {
    private static final String TAG = "SelectImageManager";
    public static final int GALLERY = 101;
    public static final int CAMERA = 102;
    public static final int CUSTOM_CAMERA = 103;

    private static final int NONE = 0;
    private static final int HORIZONTAL = 1;
    private static final int VERTICAL = 2;
    private static final int[][] OPERATIONS = new int[][]{new int[]{0, NONE}, new int[]{0, HORIZONTAL},
            new int[]{180, NONE}, new int[]{180, VERTICAL}, new int[]{90, HORIZONTAL}, new int[]{90, NONE},
            new int[]{90, HORIZONTAL}, new int[]{0, NONE}, new int[]{90, NONE}};
    /*private static final int[][] CUSTOM_CAMERA_OPERATIONS = new int[][]{new int[]{90,NONE}, new int[]{90,HORIZONTAL},
            new int[]{180, NONE}, new int[]{180, VERTICAL}, new int[]{90, HORIZONTAL}, new int[]{90, NONE},
            new int[]{90, HORIZONTAL}, new int[]{-90, NONE},};*/

    private static SelectImageManager instance = null;
    private OnImageCompleteListener imageCompleteListener;
    private Activity activity;

    // Configuration
    private static final int MAX_SIZE_IN_PX = 500;
    public static final int SIZE_IN_PX_2_MP = 1600;
    private static final long MAX_SIZE_IN_BYTE = 1 * 1000 * 1000;

    private Dialog selectImageDialog;
    private File lastFile;
    private Boolean lastFileFromGallery = true;

    public static SelectImageManager getInstance() {
        if (instance == null) {
            instance = new SelectImageManager();
        }
        return instance;
    }

    public SelectImageManager() {
    }

    public void startGallery(Activity activity) {
        this.activity = activity;

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (!IntentUtils.isIntentAvailable(activity, i)) {
            i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("photo/*");
        }
        activity.startActivityForResult(i, GALLERY);
    }

    public void startCamera(Activity activity) {
        this.activity = activity;

        lastFile = getTempFile(activity);
        lastFileFromGallery = false;

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(lastFile));
        activity.startActivityForResult(i, CAMERA);
    }

    /*public void startCustomCamera(Activity activity) {
        this.activity = activity;

        lastFile = getTempFile(activity);

        Intent i = new Intent(activity, TakePhotoActivity.class);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(lastFile));
        activity.startActivityForResult(i, CUSTOM_CAMERA);
    }*/

    public Dialog showSelectImageDialog(final Activity activity, final boolean showRemoveButton) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.select_image_dialog, null);
        v.findViewById(R.id.gallery).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageDialog.dismiss();
                startGallery(activity);
            }
        });

        v.findViewById(R.id.camera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageDialog.dismiss();
                startCamera(activity);
            }
        });

        v.findViewById(R.id.remove).setVisibility(showRemoveButton ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.remove).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageDialog.dismiss();
                imageCompleteListener.onImageComplete(null);
            }
        });

        v.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageDialog.dismiss();
            }
        });

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(v);
        selectImageDialog = dialog;
        dialog.show();

        return dialog;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == SelectImageManager.GALLERY) {
                bitmap = getBitmapFromGallery(intent);

            } else if (requestCode == SelectImageManager.CAMERA || requestCode == SelectImageManager.CUSTOM_CAMERA) {
                bitmap = getBitmapFromCamera(intent);
            }

            if (imageCompleteListener != null) {
                if (bitmap != null) {
                    imageCompleteListener.onImageComplete(bitmap);
                } else {
                    imageCompleteListener.onSelectImageError(requestCode);
                }
            }
        }
    }

    public Bitmap getBitmapFromGallery(Intent intent) {
        Bitmap resultBitmap = null;
        try {
            if (intent != null && intent.getData() != null) {
                Cursor cursor = activity.getContentResolver().query(intent.getData(), null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(ImageColumns.DATA);
                    if (idx != -1) {
                        String fileUri = cursor.getString(idx);
                        lastFile = copyFileToTempFolder(activity, new File(fileUri));
                        lastFileFromGallery = true;

                        resultBitmap = prepareBitmap(lastFile, MAX_SIZE_IN_PX, MAX_SIZE_IN_BYTE, false);
                    }
                }
            }
        } catch (Exception e) {
            L.e(TAG, "GetBitmapFromGallery error" + e.getMessage(), e);
        }
        return resultBitmap;
    }

    public Bitmap getBitmapFromCamera(Intent intent) {
        InputStream is;
        File file = lastFile;

        if (!file.exists()) {
            try {
                Uri u = intent.getData();
                is = activity.getContentResolver().openInputStream(u);
                FileOutputStream fos = new FileOutputStream(file, false);
                OutputStream os = new BufferedOutputStream(fos);
                byte[] buffer = new byte[1024];
                int byteRead;
                while ((byteRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, byteRead);
                }
                fos.close();
            } catch (Exception e) {
                L.e(TAG, "GetBitmapFromCamera error" + e.getMessage(), e);
            }
        }

        return prepareBitmap(file, MAX_SIZE_IN_PX, MAX_SIZE_IN_BYTE, true);
    }

    public static Bitmap prepareBitmap(File f) {
        return prepareBitmap(f, MAX_SIZE_IN_PX, MAX_SIZE_IN_BYTE, false);
    }

    public static Bitmap prepareBitmap(File f, int maxSizeInPx, long maxSizeInByte, boolean rotateByExif) {
        Bitmap resultBitmap = null;
        try {
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

    public static Bitmap getScaledBitmapByPxSize(File f, int maxSizeInPx) {
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

    public static Bitmap getScaledBitmapByByteSize(Bitmap sourceBitmap, long maxSizeInByte) {
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

    public static Bitmap rotateByExif(String imagePath, Bitmap bitmap) {
        try {
            ExifInterface oldExif = new ExifInterface(imagePath);
            int index = Integer.valueOf(oldExif.getAttribute(ExifInterface.TAG_ORIENTATION));
            int imageWidth = Integer.valueOf(oldExif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
            int degrees = OPERATIONS[index][0];

            Matrix matrix = new Matrix();

            if (imageWidth <= 1600 || index == 8) {
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

    public File getScaledFile(File file, int maxSizeInPx, long maxSizeInByte) {
        Bitmap bitmap = prepareBitmap(file, maxSizeInPx, maxSizeInByte, !lastFileFromGallery);

        try {
            FileOutputStream fos = new FileOutputStream(file, false);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
        } catch (Exception e) {
            L.e(TAG, "GetScaledFile error" + e.getMessage(), e);
        }

        return file;
    }

    public static File copyFileToTempFolder(Context context, File file) {
        File resultFile = getTempFile(context);
        try {
            InputStream in = new FileInputStream(file);
            OutputStream out = new FileOutputStream(resultFile);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            L.e(TAG, "CopyFileToTempFolder error" + e.getMessage(), e);
        }
        return resultFile;
    }

/*    public static String getFileAsString(Uri uri, int maxSizeInPx, long maxSizeInByte) {
        String resultString = "";
        File file = new File(uri.getPath());
        Bitmap bitmap = prepareBitmap(file, maxSizeInPx, maxSizeInByte);
        try {
            //byte[] fileAsBytesArray = FileUtils.readFileToByteArray(file);
            byte[] fileAsBytesArray = BytesBitmap.getBytes(bitmap);
            resultString = Base64.encodeToString(fileAsBytesArray, 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bitmap.recycle();
        }
        return resultString;
    }*/

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

    public static File getTempFile(Context context) {
        File ret = null;
        try {
            String state = Environment.getExternalStorageState();

            File cacheDir;
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                cacheDir = new File(Config.CACHE_DIR, "images");
            } else {
                cacheDir = context.getFilesDir();
            }

            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                ret = new File(cacheDir, Calendar.getInstance().getTimeInMillis() + ".jpg");
            } else {
                ret = new File(cacheDir + "/", Calendar.getInstance().getTimeInMillis() + ".jpg");
            }
        } catch (Exception e) {
            L.e(TAG, "GetTempFile error", e);
        }
        return ret;
    }

    /*private void deleteTempFile() {
        try {
            File file = getTempFile(activity);
            if (file != null && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            L.w(TAG, e.toString());
        }
    }*/

    public File getLastFile() {
        return lastFile;
    }

    public interface OnImageCompleteListener {
        void onImageComplete(Bitmap bitmap);

        void onSelectImageError(int imageFrom);
    }

    public OnImageCompleteListener getImageCompleteListener() {
        return imageCompleteListener;
    }

    public void setImageCompleteListener(OnImageCompleteListener imageCompleteListener) {
        this.imageCompleteListener = imageCompleteListener;
    }
}
