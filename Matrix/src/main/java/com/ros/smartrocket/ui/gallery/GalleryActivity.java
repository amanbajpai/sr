package com.ros.smartrocket.ui.gallery;

import android.database.Cursor;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryActivity extends BaseActivity implements View.OnClickListener {
    private String folderName = "";
    private GalleryAdapter galleryAdapter;
    private RecyclerView recyclerview;
    private Map<Integer,String> selectedImgPath;
        private ImageView iv_back;
        private TextView tv_ok,tv_count;
        private RelativeLayout ly_select_image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        hideActionBar();

        tv_ok = findViewById(R.id.tv_ok);
        tv_count = findViewById(R.id.tv_count);
        iv_back = findViewById(R.id.iv_back);
        recyclerview = findViewById(R.id.recyclerview);
        ly_select_image = findViewById(R.id.ly_select_image);

        if(getIntent() != null){
            folderName = getIntent().getStringExtra("folderName");

            List<GalleryInfo> galleryList = getImagesByBucket(folderName);
            selectedImgPath = new HashMap<>();
            galleryAdapter = new GalleryAdapter(galleryList, this, new GalleryClick() {
                @Override
                public void onNormalClick(int adapterPosition, GalleryInfo galleryInfo) {
                    if(selectedImgPath.size()>0){
                        if(galleryList.get(adapterPosition).isSelected){
                            galleryList.get(adapterPosition).isSelected = false;
                            selectedImgPath.remove(adapterPosition);
                        }else {
                            if(selectedImgPath.size()>=10) {
                                Toast.makeText(GalleryActivity.this, R.string.cant_select_more_then_10_img, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            galleryList.get(adapterPosition).isSelected = true;
                            selectedImgPath.put(adapterPosition,galleryInfo.imagePath);
                        }

                        galleryAdapter.notifyItemChanged(adapterPosition);
                    }

                }

                @Override
                public void onLongPress(int adapterPosition, GalleryInfo galleryInfo) {
                    if(galleryList.get(adapterPosition).isSelected){
                        galleryList.get(adapterPosition).isSelected = false;
                        selectedImgPath.remove(adapterPosition);
                    }else {
                        if(selectedImgPath.size()>=10) {
                            Toast.makeText(GalleryActivity.this, R.string.cant_select_more_then_10_img, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ly_select_image.setVisibility(View.VISIBLE);
                        galleryList.get(adapterPosition).isSelected = true;
                        selectedImgPath.put(adapterPosition,galleryInfo.imagePath);
                    }

                    galleryAdapter.notifyItemChanged(adapterPosition);

                }
            });
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
            recyclerview.setLayoutManager(gridLayoutManager);
            recyclerview.setAdapter(galleryAdapter);

            galleryAdapter.notifyDataSetChanged();
        }
    }

    public List<GalleryInfo> getImagesByBucket(@NonNull String bucketPath){
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String [] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME+" =?";
        String orderBy = MediaStore.Images.Media.DATE_ADDED+" DESC";

        List<GalleryInfo> images = new ArrayList<>();

        Cursor cursor = getContentResolver().query(uri, projection, selection,new String[]{bucketPath}, orderBy);

        if(cursor != null){
            File file;
            while (cursor.moveToNext()){
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.tv_ok:
                break;
        }
    }
}
