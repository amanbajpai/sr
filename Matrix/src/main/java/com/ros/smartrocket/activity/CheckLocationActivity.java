package com.ros.smartrocket.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.ros.smartrocket.App;
import com.ros.smartrocket.BaseActivity;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

public class CheckLocationActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    public final static String TAG = CheckLocationActivity.class.getSimpleName();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private APIFacade apiFacade = APIFacade.getInstance();
    private Address currentAddress;
    private String groupCode = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_check_location);

        if (getIntent() != null) {
            groupCode = getIntent().getStringExtra(Keys.GROUP_CODE);
        }

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        findViewById(R.id.checkMyLocationButton).setOnClickListener(this);

        setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkMyLocationButton:
                if (!UIUtils.isOnline(this)) {
                    DialogUtils.showNetworkDialog(this);
                } else if (!UIUtils.isGpsEnabled(this)) {
                    DialogUtils.showLocationDialog(this);
                } else if (!UIUtils.isGooglePlayServicesEnabled(this)) {
                    DialogUtils.showGoogleSdkDialog(this);
                } else if (UIUtils.isMockLocationEnabled(this)) {
                    DialogUtils.showMockLocationDialog(this);
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
                    /*apiFacade.checkLocationForRegistration(CheckLocationActivity.this,
                            address.getCountryName(), address.getLocality(),
                            address.getLatitude(), address.getLongitude());*/

                    // TODO: FIX it to real data before production
                    apiFacade.checkLocationForRegistration(CheckLocationActivity.this,
                            "China", "Hong Kong", 3.1, 4.1);
                } else {
                    UIUtils.showSimpleToast(CheckLocationActivity.this,
                            R.string.current_location_not_defined);
                }
            }
        });
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        setSupportProgressBarIndeterminateVisibility(false);
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.CHECK_LOCATION_OPERATION_TAG.equals(operation.getTag())) {
                CheckLocationResponse checkLocationResponse = (CheckLocationResponse) operation.getResponseEntities().get(0);
                if (checkLocationResponse.getStatus()) {
                    UIUtils.showSimpleToast(this, R.string.success);

                    Intent intent = new Intent(this, RegistrationActivity.class);
                    intent.putExtra(Keys.COUNTRY_ID, checkLocationResponse.getCountryId());
                    intent.putExtra(Keys.COUNTRY_NAME, currentAddress.getCountryName());
                    intent.putExtra(Keys.CITY_ID, checkLocationResponse.getCityId());
                    intent.putExtra(Keys.CITY_NAME, currentAddress.getLocality());
                    intent.putExtra(Keys.GROUP_CODE, groupCode);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, CheckLocationFailedActivity.class));
                }
            }
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError());
            //TODO Remove
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
