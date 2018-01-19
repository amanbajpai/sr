package com.ros.smartrocket.presentation.media;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.MenuItem;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.ui.views.ImageEditorView;
import com.ros.smartrocket.utils.UIUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

public class FullScreenImageActivity extends BaseActivity {
    private Target bitmapTarget;
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeAsUp();
        setContentView(R.layout.activity_full_screen_image);
        String photoUri = getIntent().getStringExtra(Keys.BITMAP_FILE_PATH);
        createBitmapTarget();
        if (!TextUtils.isEmpty(photoUri))
            loadImage(photoUri);
        else
            showError();
    }

    private void loadImage(String photoUri) {
        if (photoUri.startsWith("http")) {
            Picasso.with(this).load(photoUri).into(bitmapTarget);
        } else {
            Picasso.with(this).load(new File(photoUri)).into(bitmapTarget);
        }
    }

    private void showImage() {
        Display display = getWindowManager().getDefaultDisplay();
        if (bitmap != null) {
            ImageEditorView photo = findViewById(R.id.photo);
            photo.setViewSize(display.getWidth(), display.getHeight() - UIUtils.getPxFromDp(this, 70));
            photo.setCanRotate(false);
            photo.setBitmap(bitmap);
        } else {
            showError();
        }
    }

    private void createBitmapTarget() {
        bitmapTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap result, Picasso.LoadedFrom from) {
                bitmap = result;
                showImage();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                showError();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
    }

    private void showError() {
        UIUtils.showSimpleToast(FullScreenImageActivity.this, R.string.error);
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
        super.onDestroy();
    }
}
