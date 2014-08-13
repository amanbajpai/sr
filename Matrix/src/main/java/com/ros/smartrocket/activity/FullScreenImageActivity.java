package com.ros.smartrocket.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.MenuItem;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.ImageEditorView;

import java.io.File;

public class FullScreenImageActivity extends ActionBarActivity {
    //private static final String TAG = FullScreenImageActivity.class.getSimpleName();
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_full_screen_image);

        Display display = getWindowManager().getDefaultDisplay();

        String photoUri = getIntent().getStringExtra(Keys.BITMAP_FILE_PATH);

        File photoFile = new File(photoUri);
        bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

        ImageEditorView photo = (ImageEditorView) findViewById(R.id.photo);
        photo.setViewSize(display.getWidth(), display.getHeight() - UIUtils.getPxFromDp(this, 70));
        photo.setCanRotate(false);
        photo.setBitmap(bitmap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null) {
            bitmap.recycle();
        }
        super.onDestroy();
    }
}
