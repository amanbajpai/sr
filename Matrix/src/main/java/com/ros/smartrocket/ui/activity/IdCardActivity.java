package com.ros.smartrocket.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.MatrixContextWrapper;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IdCardActivity extends Activity {
    public static final String ARG_WAVE = "com.ros.smartrocket.ui.activity.IdCardActivity.ARG_WAVE";

    @BindView(R.id.idCardUserPhoto)
    ImageView userPhoto;
    @BindView(R.id.idCardLogo)
    ImageView logo;
    @BindView(R.id.idCardText)
    TextView text;
    @BindView(R.id.idCardAgentName)
    TextView agentName;
    @BindView(R.id.idCardAgentId)
    TextView agentId;

    public static void launch(Context context, Wave wave) {
        Intent intent = new Intent(context, IdCardActivity.class);
        intent.putExtra(ARG_WAVE, wave);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card);
        ButterKnife.bind(this);

        Wave wave = (Wave) getIntent().getSerializableExtra(ARG_WAVE);
        final MyAccount myAccount = App.getInstance().getMyAccount();

        if (TextUtils.isEmpty(myAccount.getPhotoUrl())) {
            userPhoto.setVisibility(View.GONE);
        } else {
            userPhoto.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext()).load(myAccount.getPhotoUrl()).into(userPhoto);
        }

        if (!TextUtils.isEmpty(wave.getIdCardLogo())) {
            Picasso.with(getApplicationContext()).load(wave.getIdCardLogo()).into(logo);
        }

        if (!TextUtils.isEmpty(wave.getIdCardText())) {
            text.setText(Html.fromHtml(wave.getIdCardText()));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        agentName.setText(myAccount.getSingleName());
        agentId.setText(getString(R.string.id_card_agent_id, myAccount.getId()));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.idCardBackButton)
    void onBackClick() {
        LocaleUtils.setCurrentLanguage();
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleUtils.setCurrentLanguage();
        Locale newLocale = LocaleUtils.getCurrentLocale();
        Context context = MatrixContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }
}
