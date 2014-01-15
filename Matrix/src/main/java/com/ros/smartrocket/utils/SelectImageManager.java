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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.TakePhotoActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
            new int[]{90, HORIZONTAL}, new int[]{-90, NONE}};
    /*private static final int[][] CUSTOM_CAMERA_OPERATIONS = new int[][]{new int[]{90, NONE}, new int[]{90, HORIZONTAL},
            new int[]{180, NONE}, new int[]{180, VERTICAL}, new int[]{90, HORIZONTAL}, new int[]{90, NONE},
            new int[]{90, HORIZONTAL}, new int[]{-90, NONE},};*/

    private static SelectImageManager instance = null;
    private OnImageCompleteListener imageCompliteListener;
    private Activity activity;

    // Configuration
    private static int MAX_SIZE_IN_PX = 600;
    private static long MAX_SIZE_IN_BYTE = 2 * 1000 * 1000;
    private static boolean CHECK_SCALE_BY_BYTE_SIZE = true;

    private Dialog selectImageDialog;
    private File lastFile;

    public static SelectImageManager getInstance() {
        if (instance == null) {
            instance = new SelectImageManager();
        }
        return instance;
    }

    public SelectImageManager() {
    }

    public void seMaxSize(int maxSixeInPx) {
        MAX_SIZE_IN_PX = maxSixeInPx;
    }

    public void seMaxSizeInByte(long maxSizeInByte) {
        MAX_SIZE_IN_BYTE = maxSizeInByte;
    }

    public void startGallery(Activity activity) {
        this.activity = activity;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        activity.startActivityForResult(i, GALLERY);
    }

    public void startCamera(Activity activity) {
        this.activity = activity;
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile()));
        activity.startActivityForResult(i, CAMERA);
    }

    public void startCustomCamera(Activity activity) {
        this.activity = activity;
        Intent i = new Intent(activity, TakePhotoActivity.class);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile()));
        activity.startActivityForResult(i, CUSTOM_CAMERA);
    }

    public void showSelectImageDialog(final Activity activity, final boolean showRemoveButton,
                                      final OnImageCompleteListener imageCompliteListener) {
        this.activity = activity;
        this.imageCompliteListener = imageCompliteListener;

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
                imageCompliteListener.onImageComplete(null);
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
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == SelectImageManager.GALLERY) {
                bitmap = getBitmapFromGalery(intent);

            } else if (requestCode == SelectImageManager.CAMERA || requestCode == SelectImageManager.CUSTOM_CAMERA) {
                bitmap = getBitmapFromCamera(intent);
            }

            if (imageCompliteListener != null) {
                imageCompliteListener.onImageComplete(bitmap);
            }
        }
    }

    public Bitmap getBitmapFromGalery(Intent intent) {
        try {
            Cursor cursor = activity.getContentResolver().query(intent.getData(), null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(ImageColumns.DATA);
            String fileUri = cursor.getString(idx);
            lastFile = new File(fileUri);

            return prepareBitmap(lastFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getBitmapFromCamera(Intent intent) {
        InputStream is = null;
        File file = getTempFile();
        lastFile = file;

        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {

            try {
                Uri u = intent.getData();
                is = activity.getContentResolver().openInputStream(u);
                FileOutputStream fos = new FileOutputStream(file, false);
                OutputStream os = new BufferedOutputStream(fos);
                byte[] buffer = new byte[1024];
                int byteRead = 0;
                while ((byteRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, byteRead);
                }
                fos.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        try {
            return prepareBitmap(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap prepareBitmap(File f) {
        Bitmap resultBitmap = null;

        try {
            resultBitmap = getScaledBitmapByPxSize(f);

            if (CHECK_SCALE_BY_BYTE_SIZE) {
                resultBitmap = getScaledBitmapByByteSize(resultBitmap);
            }

            resultBitmap = rotateByExif(f.getAbsolutePath(), resultBitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultBitmap;
    }

    private Bitmap getScaledBitmapByPxSize(File f) {
        int scale = 1;
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(f.getAbsolutePath(), o);

            if (o.outHeight > MAX_SIZE_IN_PX || o.outWidth > MAX_SIZE_IN_PX) {
                double maxSourceSideSize = (double) Math.max(o.outHeight, o.outWidth);
                scale = (int) Math.pow(2,
                        (int) Math.round(Math.log(MAX_SIZE_IN_PX / maxSourceSideSize) / Math.log(0.5)));
            }

            L.e(TAG, "getScaledBitmapBySideSize Scale: " + scale);

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(f.getAbsolutePath(), o2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getScaledBitmapByByteSize(Bitmap sourceBitmap) {
        try {
            long sourceBitmapByte = BytesBitmap.getBytes(sourceBitmap).length;

            if (sourceBitmapByte > MAX_SIZE_IN_BYTE) {
                int scale = (int) Math.pow(2,
                        (int) Math.round(Math.log(MAX_SIZE_IN_BYTE / (double) sourceBitmapByte) / Math.log(0.5)));

                L.e(TAG, "getScaledBitmapByByteSize Scale: " + scale);

                int resultWidth = (int) (sourceBitmap.getWidth() / scale);
                int resultHeight = (int) (sourceBitmap.getHeight() / scale);

                return Bitmap.createScaledBitmap(sourceBitmap, resultWidth, resultHeight, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sourceBitmap;
    }

    public Bitmap rotateByExif(String imagePath, Bitmap bitmap) {
        try {
            ExifInterface oldExif = new ExifInterface(imagePath);
            int index = Integer.valueOf(oldExif.getAttribute(ExifInterface.TAG_ORIENTATION));
            int degrees = OPERATIONS[index][0];

            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);

            if (degrees == 0) {
                return bitmap;
            } else {
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;

    }

    public File getTempFile() {
        File ret = null;
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                ret = new File(Environment.getExternalStorageDirectory(), "PostTmp.jpg");
            } else {
                File mydir = activity.getDir("mydir", Context.MODE_PRIVATE);
                ret = new File(mydir.getAbsolutePath() + "/", "PostTmp.jpg");
            }
        } catch (Exception e) {
            L.e(TAG, "Error get Temp File");
        }
        return ret;
    }

    private void deleteTempFile() {
        try {
            File file = getTempFile();
            if (file != null && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            L.w(TAG, e.toString());
        }
    }

    /*
     * protected int getBitmapSize(Bitmap data) { if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
     * return data.getRowBytes() * data.getHeight(); } else { return data.getByteCount(); } }
     */


    public File getLastFile() {
        return lastFile;
    }

    public interface OnImageCompleteListener {
        void onImageComplete(Bitmap bitmap);
    }
}
