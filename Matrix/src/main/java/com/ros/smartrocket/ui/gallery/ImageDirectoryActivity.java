package com.ros.smartrocket.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.ui.gallery.adapter.ImageDirectoryAdapter;
import com.ros.smartrocket.ui.gallery.listner.BucketClick;
import com.ros.smartrocket.ui.gallery.model.BucketInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class ImageDirectoryActivity extends BaseActivity {

    RecyclerView recyclerview;

    private ImageDirectoryAdapter directoryAdapter;
    private List<BucketInfo> imageBucketList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_directory);
        hideActionBar();
        recyclerview = findViewById(R.id.recyclerview);
        imageBucketList = new ArrayList<>();

        directoryAdapter = new ImageDirectoryAdapter(this, imageBucketList, bucketInfo ->{
            Intent  intent = new Intent(this,GalleryActivity.class);
            intent.putExtra("folderName",bucketInfo.getName());
            startActivityForResult(intent, 101);
        }
                );

        GridLayoutManager manager = new GridLayoutManager(this,2);
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(directoryAdapter);

        getImageAlbumList();

    }

    private int photoCountByAlbum(String bucketName) {
        try {
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            String searchParams = null;
            String bucket = bucketName;
            searchParams = "bucket_display_name = \"" + bucket + "\"";
            Cursor mPhotoCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, searchParams, null, orderBy + " DESC");

            if (mPhotoCursor.getCount() > 0) {
                return mPhotoCursor.getCount();
            }
            mPhotoCursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void getImageAlbumList() {
        imageBucketList.clear();
        String[] PROJECTION_BUCKET = {MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATA};
        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
        String BUCKET_ORDER_BY = "MAX(datetaken) DESC";
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = getContentResolver().query(images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);
        BucketInfo album;
        if (cur.moveToFirst()) {
            String bucket;
            String date;
            String data;
            long bucketId;
            int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int dateColumn = cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
            int bucketIdColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            do {
                // Get the field values
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);
                data = cur.getString(dataColumn);
                bucketId = cur.getInt(bucketIdColumn);
                if (bucket != null && bucket.length() > 0) {
                    album = new BucketInfo();
                    album.setBucketId(bucketId);
                    album.setName(bucket);
                    album.setDateTaken(date);
                    album.setFirstImageContainedPath(data);
                    album.setMediaCount(photoCountByAlbum(bucket));
                    imageBucketList.add(album);
                }


            } while (cur.moveToNext());

            directoryAdapter.notifyDataSetChanged();
        }

        cur.close();

    }



   /* @OnClick({R.id.recyclerview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnMyLocation:

            break;
        }
    }*/
}
