package com.ros.smartrocket.flow.login.location;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.db.entity.RegistrationPermissions;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.flow.login.promo.PromoCodeActivity;
import com.ros.smartrocket.flow.login.location.failed.CheckLocationFailedActivity;
import com.ros.smartrocket.flow.login.referral.ReferralCasesActivity;
import com.ros.smartrocket.flow.login.registration.RegistrationActivity;
import com.ros.smartrocket.flow.login.terms.TermsAndConditionActivity;
import com.ros.smartrocket.flow.login.TutorialActivity;
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.ui.dialog.CheckLocationDialog;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

public class CheckLocationActivity extends BaseActivity implements View.OnClickListener, CheckLocationMvpView {
    private CheckLocationDialog checkLocationDialog;
    private RegistrationPermissions registrationPermissions;

    private CheckLocationMvpPresenter<CheckLocationMvpView> checkLocationPresenter;

    public CheckLocationActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_check_location);
        registrationPermissions = PreferencesManager.getInstance().getRegPermissions();
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        findViewById(R.id.checkMyLocationButton).setOnClickListener(this);
        checkDeviceSettingsByOnResume(false);
        checkLocationPresenter = new CheckLocationPresenter<>();
        checkLocationPresenter.attachView(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkMyLocationButton:
                LocaleUtils.setCurrentLanguage();
                if (UIUtils.isDeviceReady(this)) {
                    checkLocationPresenter.checkLocation();
                }
                break;
            default:
                break;
        }
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

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        // do nothing
    }

    @Override
    public void onLocationChecked(CheckLocationResponse serverResponse, double latitude, double longitude) {
        if (checkLocationDialog != null) {
            checkLocationDialog.checkLocationSuccess();
        }
        if (serverResponse != null) {
            if (serverResponse.getStatus()) {
                continueFlow(serverResponse, latitude, longitude);
            } else {
                openLocationFailedActivity(serverResponse, latitude, longitude);
            }
        }
    }

    private void continueFlow(CheckLocationResponse serverResponse, double latitude, double longitude) {
        Intent intent;
        if (registrationPermissions.isSlidersEnable()) {
            intent = new Intent(CheckLocationActivity.this, TutorialActivity.class);
        } else if (registrationPermissions.isTermsEnable()) {
            intent = new Intent(CheckLocationActivity.this, TermsAndConditionActivity.class);
        } else if (registrationPermissions.isReferralEnable()) {
            intent = new Intent(CheckLocationActivity.this, ReferralCasesActivity.class);
        } else if (registrationPermissions.isSrCodeEnable()) {
            intent = new Intent(CheckLocationActivity.this, PromoCodeActivity.class);
        } else {
            intent = new Intent(CheckLocationActivity.this, RegistrationActivity.class);
        }
        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        intent.putExtra(Keys.COUNTRY_ID, serverResponse.getCountryId());
        intent.putExtra(Keys.CITY_ID, serverResponse.getCityId());
        intent.putExtra(Keys.DISTRICT_ID, serverResponse.getDistrictId());
        intent.putExtra(Keys.COUNTRY_NAME, serverResponse.getCountryName());
        intent.putExtra(Keys.CITY_NAME, serverResponse.getCityName());
        intent.putExtra(Keys.LATITUDE, latitude);
        intent.putExtra(Keys.LONGITUDE, longitude);
        startActivity(intent);
    }

    private void openLocationFailedActivity(CheckLocationResponse serverResponse, double latitude, double longitude) {
        Intent intent = new Intent(CheckLocationActivity.this, CheckLocationFailedActivity.class);
        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        if (serverResponse != null) {
            intent.putExtra(Keys.COUNTRY_ID, serverResponse.getCountryId());
            intent.putExtra(Keys.CITY_ID, serverResponse.getCityId());
            intent.putExtra(Keys.DISTRICT_ID, serverResponse.getDistrictId());
            intent.putExtra(Keys.COUNTRY_NAME, serverResponse.getCountryName());
            intent.putExtra(Keys.CITY_NAME, serverResponse.getCityName());
        }
        intent.putExtra(Keys.LATITUDE, latitude);
        intent.putExtra(Keys.LONGITUDE, longitude);

        startActivity(intent);
    }

    @Override
    public void onLocationCheckFailed() {
        if (checkLocationDialog != null) {
            checkLocationDialog.checkLocationFail();
        }
    }

    @Override
    public void showLocationCheckDialog() {
        checkLocationDialog = new CheckLocationDialog(this);
    }
}
