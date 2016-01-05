package com.ros.smartrocket.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.squareup.picasso.Picasso;

public class IdCardActivity extends Activity {
    @Bind(R.id.idCardUserPhoto)
    ImageView userPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card);
        ButterKnife.bind(this);

        String photoUrl = App.getInstance().getMyAccount().getPhotoUrl();
        Picasso.with(getApplicationContext()).load(photoUrl).into(userPhoto);

//        if (!TextUtils.isEmpty(photoUrl)) {
//            ImageLoader.getInstance().loadBitmap(photoUrl, ImageLoader.SMALL_IMAGE_VAR,
//                    new ImageLoader.OnFetchCompleteListener() {
//
//                        @Override
//                        public void onFetchComplete(final Bitmap result) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    photoImageView.setImageBitmap(result);
//                                }
//                            });
//                        }
//                    }
//            );
//        } else {
//            photoImageView.setImageResource(R.drawable.cam);
//        }

    }
}
