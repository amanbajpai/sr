package com.ros.smartrocket.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

public class CheckLocationActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    private static final String TAG = CheckLocationActivity.class.getSimpleName();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private APIFacade apiFacade = APIFacade.getInstance();
    private Address currentAddress;
    private int countryId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_check_location);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            countryId = getIntent().getIntExtra(Keys.COUNTRY_ID, 0);
        }

        findViewById(R.id.checkMyLocationButton).setOnClickListener(this);

        setSupportProgressBarIndeterminateVisibility(false);

        checkDeviceSettingsByOnResume(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkMyLocationButton:
                if (countryId != 0) {
                    Intent intent = new Intent(this, RegistrationActivity.class);
                    intent.putExtras(getIntent().getExtras());
                    startActivity(intent);
                } else {

                    if (!UIUtils.isOnline(this)) {
                        DialogUtils.showNetworkDialog(this);
                    } else if (!UIUtils.isGpsEnabled(this)) {
                        DialogUtils.showLocationDialog(this, true);
                    } else if (!UIUtils.isGooglePlayServicesEnabled(this)) {
                        DialogUtils.showGoogleSdkDialog(this);
                    } else if (UIUtils.isMockLocationEnabled(this)) {
                        DialogUtils.showMockLocationDialog(this, true);
                    } else {
                        setSupportProgressBarIndeterminateVisibility(true);

                        Location location = lm.getLocation();
                        if (location != null) {
                            getAddressByLocation(location);
                        } else {
                            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                                @Override
                                public void onUpdate(Location location) {
                                    L.i(TAG, "Location Updated!");
                                    getAddressByLocation(location);
                                }
                            });
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    public void getAddressByLocation(Location location) {
        lm.getAddress(location, new MatrixLocationManager.IAddress() {
            @Override
            public void onUpdate(Address address) {
                if (address != null) {
                    currentAddress = address;
                    apiFacade.checkLocationForRegistration(CheckLocationActivity.this,
                            address.getCountryName(), address.getLocality(),
                            address.getLatitude(), address.getLongitude());

                } else if (UIUtils.isOnline(CheckLocationActivity.this)) {
                    UIUtils.showSimpleToast(CheckLocationActivity.this,
                            R.string.current_location_not_defined);
                }
            }
        });
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        setSupportProgressBarIndeterminateVisibility(false);
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.CHECK_LOCATION_OPERATION_TAG.equals(operation.getTag())) {
                CheckLocationResponse checkLocationResponse =
                        (CheckLocationResponse) operation.getResponseEntities().get(0);

                if (checkLocationResponse.getStatus()) {
                    UIUtils.showSimpleToast(this, R.string.success);

                    Intent intent = new Intent(this, RegistrationActivity.class);
                    if (getIntent().getExtras() != null) {
                        intent.putExtras(getIntent().getExtras());
                    }
                    intent.putExtra(Keys.COUNTRY_ID, checkLocationResponse.getCountryId());
                    intent.putExtra(Keys.COUNTRY_NAME, currentAddress.getCountryName());
                    intent.putExtra(Keys.CITY_ID, checkLocationResponse.getCityId());
                    intent.putExtra(Keys.CITY_NAME, currentAddress.getLocality());
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, CheckLocationFailedActivity.class));
                }
            }
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError());
            startActivity(new Intent(this, CheckLocationFailedActivity.class));
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
