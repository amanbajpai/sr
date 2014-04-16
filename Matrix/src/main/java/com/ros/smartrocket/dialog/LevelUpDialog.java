package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.images.ImageLoader;

public class LevelUpDialog extends Dialog implements View.OnClickListener {
    //private static final String TAG = LevelUpDialog.class.getSimpleName();
    private Activity activity;

    public LevelUpDialog(final Activity activity) {
        super(activity);
        this.activity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.dialog_level_up);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        MyAccount account = App.getInstance().getMyAccount();

        ((TextView) findViewById(R.id.levelTitle)).setText(account.getLevelName());
        //TODO Set level description
        ((TextView) findViewById(R.id.levelDescription)).setText("" + account.getLevelNumber());

        String levelIconUrl = account.getLevelIconUrl();
        if (!TextUtils.isEmpty(levelIconUrl)) {
            ImageLoader.getInstance().loadBitmap(levelIconUrl, ImageLoader.SMALL_IMAGE_VAR,
                    new ImageLoader.OnFetchCompleteListener() {

                        @Override
                        public void onFetchComplete(final Bitmap result) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((ImageView) findViewById(R.id.levelIcon)).setImageBitmap(result);
                                }
                            });
                        }
                    }
            );
        }


        findViewById(R.id.okButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okButton:
                dismiss();
                break;
            default:
                break;
        }
    }
}