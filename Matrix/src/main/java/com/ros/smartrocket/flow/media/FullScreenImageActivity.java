package com.ros.smartrocket.flow.media;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.MenuItem;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.image.SelectImageManager;
import com.ros.smartrocket.ui.views.ImageEditorView;

import java.io.File;

public class FullScreenImageActivity extends BaseActivity {
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeAsUp();
        setContentView(R.layout.activity_full_screen_image);

        Display display = getWindowManager().getDefaultDisplay();
        String photoUri = getIntent().getStringExtra(Keys.BITMAP_FILE_PATH);
        boolean rotateByExif = getIntent().getBooleanExtra(Keys.ROTATE_BY_EXIF, false);
        if (!TextUtils.isEmpty(photoUri)) {
            bitmap = SelectImageManager.prepareBitmap(new File(photoUri), SelectImageManager.SIZE_IN_PX_2_MP, 0, rotateByExif);
        }
        if (bitmap != null) {
            ImageEditorView photo = (ImageEditorView) findViewById(R.id.photo);
            photo.setViewSize(display.getWidth(), display.getHeight() - UIUtils.getPxFromDp(this, 70));
            photo.setCanRotate(false);
            photo.setBitmap(bitmap);
        } else {
            UIUtils.showSimpleToast(this, R.string.error);
        }
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
