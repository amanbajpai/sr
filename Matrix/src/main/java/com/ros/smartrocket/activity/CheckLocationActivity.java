package com.ros.smartrocket.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.db.entity.RegistrationPermissions;
import com.ros.smartrocket.dialog.CheckLocationDialog;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

public class CheckLocationActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    private CheckLocationDialog checkLocationDialog;
    private RegistrationPermissions registrationPermissions;

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkMyLocationButton:
                if (!UIUtils.isOnline(this)) {
                    DialogUtils.showNetworkDialog(this);
                } else if (!UIUtils.isAllLocationSourceEnabled(this)) {
                    DialogUtils.showLocationDialog(this, true);
                } else if (UIUtils.isMockLocationEnabled(this, App.getInstance().getLocationManager().getLocation())) {
                    DialogUtils.showMockLocationDialog(this, true);
                } else {
                    checkLocationDialog = new CheckLocationDialog(this,
                            new CheckLocationDialog.CheckLocationListener() {
                                @Override
                                public void onLocationChecked(Dialog dialog, String countryName, String cityName,
                                                              double latitude, double longitude,
                                                              CheckLocationResponse serverResponse) {
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
                                    intent.putExtra(Keys.COUNTRY_NAME, countryName);
                                    intent.putExtra(Keys.CITY_NAME, cityName);
                                    intent.putExtra(Keys.LATITUDE, latitude);
                                    intent.putExtra(Keys.LONGITUDE, longitude);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCheckLocationFailed(Dialog dialog, String countryName, String cityName,
                                                                  double latitude, double longitude,
                                                                  CheckLocationResponse serverResponse) {
                                    Intent intent = new Intent(CheckLocationActivity.this, CheckLocationFailedActivity.class);
                                    if (getIntent().getExtras() != null) {
                                        intent.putExtras(getIntent().getExtras());
                                    }

                                    if (serverResponse != null) {
                                        intent.putExtra(Keys.COUNTRY_ID, serverResponse.getCountryId());
                                        intent.putExtra(Keys.CITY_ID, serverResponse.getCityId());
                                        intent.putExtra(Keys.DISTRICT_ID, serverResponse.getDistrictId());
                                    }
                                    intent.putExtra(Keys.COUNTRY_NAME, countryName);
                                    intent.putExtra(Keys.CITY_NAME, cityName);
                                    intent.putExtra(Keys.LATITUDE, latitude);
                                    intent.putExtra(Keys.LONGITUDE, longitude);

                                    startActivity(intent);
                                }
                            }
                            , true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (checkLocationDialog != null) {
            checkLocationDialog.onNetworkOperation(operation);
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
    protected void onStart() {
        super.onStart();
        addNetworkOperationListener(this);
    }

    @Override
    protected void onStop() {
        removeNetworkOperationListener(this);
        super.onStop();
    }
}
