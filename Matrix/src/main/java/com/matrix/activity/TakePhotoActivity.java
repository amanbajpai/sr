package com.matrix.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.matrix.R;
import com.matrix.utils.L;
import com.matrix.views.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class TakePhotoActivity extends Activity implements View.OnClickListener {
    public final static String TAG = TakePhotoActivity.class.getSimpleName();
    private CameraPreview cameraPreview;
    private Camera camera;
    private Uri tempPhotoFileUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        if (getIntent() != null) {
            tempPhotoFileUri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        }

        //cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);

        findViewById(R.id.cancelButton).setOnClickListener(this);
        findViewById(R.id.captureButton).setOnClickListener(this);

        camera = getCameraInstance();

        cameraPreview = new CameraPreview(this, camera);
        ((FrameLayout) findViewById(R.id.cameraLayout)).addView(cameraPreview);

        setCameraDisplayOrientation();
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    private Camera.PictureCallback takePictureListener = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = new File(tempPhotoFileUri.getPath());

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (Exception e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            setResult(RESULT_OK, new Intent());
            finish();
        }
    };

    public void setCameraDisplayOrientation() {
        if (camera == null) {
            L.d(TAG, "setCameraDisplayOrientation - camera null");
            return;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);

        WindowManager winManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int rotation = winManager.getDefaultDisplay().getRotation();

        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setCameraDisplayOrientation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.captureButton:
                camera.takePicture(null, null, takePictureListener);
                break;
            case R.id.cancelButton:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
