package com.ros.smartrocket.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.text.Html;
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
import com.ros.smartrocket.utils.L;
import com.squareup.picasso.Picasso;

public class LevelUpDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = LevelUpDialog.class.getSimpleName();

    public LevelUpDialog(final Activity activity) {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            L.e(TAG, "Show dialog error" + e.getMessage(), e);
        }
        setContentView(R.layout.dialog_level_up);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        MyAccount account = App.getInstance().getMyAccount();

        ((TextView) findViewById(R.id.rankDescription)).setText(Html.fromHtml(activity.getString(R.string
                        .you_have_been_promoted,
                account.getLevelName(), String.valueOf(account.getLevelNumber()))));
        ((TextView) findViewById(R.id.levelDescription)).setText(account.getLevelDescription());

        String levelIconUrl = account.getLevelIconUrl();
        if (!TextUtils.isEmpty(levelIconUrl)) {
            Picasso.with(App.getInstance()).load(levelIconUrl)
                    .into((ImageView) findViewById(R.id.levelIcon));
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
