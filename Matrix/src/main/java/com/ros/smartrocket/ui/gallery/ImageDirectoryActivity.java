package com.ros.smartrocket.ui.gallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.gallery.adapter.ImageDirectoryAdapter;

import butterknife.BindView;
import butterknife.OnClick;

public class ImageDirectoryActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private ImageDirectoryAdapter directoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_directory);

        directoryAdapter = new ImageDirectoryAdapter();
        GridLayoutManager manager = new GridLayoutManager(this,2);
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(directoryAdapter);

    }

   /* @OnClick({R.id.recyclerview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnMyLocation:

            break;
        }
    }*/
}
