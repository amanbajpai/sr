package com.ros.smartrocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.RegistrationPermissions;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

/* This activity asks for a promo code to a user and redirect him to ReferralCodeActivity.
    we resend getIntent() information forward to use it later
*/
public class PromoCodeActivity extends BaseActivity implements View.OnClickListener {

    private EditText promoCodeEdit;
    private RegistrationPermissions registrationPermissions;

    public PromoCodeActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_promo_code);
        registrationPermissions = PreferencesManager.getInstance().getRegPermissions();

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        promoCodeEdit = (EditText) findViewById(R.id.promoCode);

        Button continueButton = (Button) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);

        checkDeviceSettingsByOnResume(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueButton:
                startReferralCasesActivity();
                break;
            default:
                break;
        }
    }

    public void startReferralCasesActivity() {
        Intent intent;
        if (registrationPermissions != null && registrationPermissions.isReferralEnable()) {
            intent = new Intent(this, ReferralCasesActivity.class);
        } else {
            intent = new Intent(this, RegistrationActivity.class);
        }

        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }

        intent.putExtra(Keys.PROMO_CODE, promoCodeEdit.getText().toString());
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
