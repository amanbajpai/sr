package com.ros.smartrocket.ui.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.ui.gallery.adapter.GalleryAdapter;
import com.ros.smartrocket.ui.gallery.listner.GalleryClick;
import com.ros.smartrocket.ui.gallery.model.GalleryInfo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GalleryActivity extends BaseActivity  {
    private String folderName = "";
    private GalleryAdapter galleryAdapter;
    private int imageListsize;

    private HashMap<String, GalleryInfo> selectedImgPath;

    @BindView(R.id.tv_ok)
    TextView tv_ok;
    @BindView(R.id.tv_count)
    TextView tv_count;
    @BindView(R.id.ly_select_image)
    RelativeLayout ly_select_image;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        hideActionBar();

        if (getIntent() != null) {
            imageListsize = getIntent().getIntExtra("imageListsize",10);
            folderName = getIntent().getStringExtra("folderName");

            List<GalleryInfo> galleryList = getImagesByBucket(folderName);
            selectedImgPath = new HashMap<>();
            galleryAdapter = new GalleryAdapter(galleryList, this, new GalleryClick() {
                @Override
                public void onNormalClick(int adapterPosition, GalleryInfo galleryInfo) {
                    if (selectedImgPath.size() > 0) {
                        if (galleryList.get(adapterPosition).isSelected) {
                            galleryList.get(adapterPosition).isSelected = false;
                            selectedImgPath.remove(galleryInfo.imagePath);
                        } else {
                            if (imageListsize+selectedImgPath.size() >= 10) {
                                Toast.makeText(GalleryActivity.this, R.string.cant_select_more_then_10_img, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            galleryList.get(adapterPosition).isSelected = true;
                            galleryInfo.id = adapterPosition;
                            selectedImgPath.put(galleryInfo.imagePath, galleryInfo);
                        }
                        if (selectedImgPath.size() == 0) ly_select_image.setVisibility(View.GONE);
                        tv_count.setText((selectedImgPath.size()) + "");
                        galleryAdapter.notifyItemChanged(adapterPosition);
                    }

                }

                @Override
                public void onLongPress(int adapterPosition, GalleryInfo galleryInfo) {
                    if (galleryList.get(adapterPosition).isSelected) {
                        galleryList.get(adapterPosition).isSelected = false;
                        selectedImgPath.remove(galleryInfo.imagePath);
                    } else {
                        if (imageListsize+selectedImgPath.size() >= 10) {
                            Toast.makeText(GalleryActivity.this, R.string.cant_select_more_then_10_img, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ly_select_image.setVisibility(View.VISIBLE);
                        galleryList.get(adapterPosition).isSelected = true;
                        galleryInfo.id = adapterPosition;
                        selectedImgPath.put(galleryInfo.imagePath, galleryInfo);
                    }
                    if (selectedImgPath.size() == 0) ly_select_image.setVisibility(View.GONE);
                    tv_count.setText((selectedImgPath.size()) + "");
                    galleryAdapter.notifyItemChanged(adapterPosition);

                }
            });
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            recyclerview.setLayoutManager(gridLayoutManager);
            recyclerview.setAdapter(galleryAdapter);

            galleryAdapter.notifyDataSetChanged();
        }
    }

    public List<GalleryInfo> getImagesByBucket(@NonNull String bucketPath) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?";
        String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";

        List<GalleryInfo> images = new ArrayList<>();

        Cursor cursor = getContentResolver().query(uri, projection, selection, new String[]{bucketPath}, orderBy);

        if (cursor != null) {
            File file;
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                file = new File(path);
                if (file.exists() && !images.contains(path)) {
                    GalleryInfo galleryInfo = new GalleryInfo();
                    galleryInfo.imagePath = path;
                    galleryInfo.isSelected = false;
                    images.add(galleryInfo);
                }
            }
            cursor.close();
        }
        return images;
    }

    @OnClick({R.id.iv_back, R.id.tv_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_ok:
                Intent intent = getIntent();
                intent.putExtra("selectedImgPath", selectedImgPath);
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }



}
