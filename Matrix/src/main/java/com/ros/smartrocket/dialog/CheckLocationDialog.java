package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.location.Address;
import android.location.Location;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.utils.L;

/**
 * Dialog for checking location and showing results
 */

public class CheckLocationDialog extends Dialog {
    private static final String TAG = CheckLocationDialog.class.getSimpleName();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private APIFacade apiFacade = APIFacade.getInstance();
    private Activity activity;
    private ImageView statusImage;
    private TextView statusText;
    private boolean locationChecked = false;
    private CheckLocationResponse checkLocationResponse;
    private CheckLocationListener checkLocationListener;
    private String countryName = "";
    private String cityName = "";

    public CheckLocationDialog(final Activity activity, final CheckLocationListener checkLocationListener) {
        super(activity);
        this.activity = activity;
        this.checkLocationListener = checkLocationListener;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            L.e(TAG, "Show dialog error" + e.getMessage(), e);
        }
        setContentView(R.layout.dialog_check_location_success);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        statusImage = (ImageView) findViewById(R.id.statusImage);
        statusImage.setImageResource(R.drawable.round_progress);
        statusImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate));

        statusText = (TextView) findViewById(R.id.statusText);
        statusText.setText(activity.getString(R.string.check_location_dialog_text1));

        getLocation();
    }

    public void getLocation() {
        Location location = lm.getLocation();
        if (location != null) {
            getAddressByLocation(location);
        } else {
            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                @Override
                public void onUpdate(Location location) {
                    getAddressByLocation(location);
                }
            });
        }
    }

    public void getAddressByLocation(final Location location) {
        lm.getAddress(location, new MatrixLocationManager.IAddress() {
            @Override
            public void onUpdate(Address address) {
                if (address != null) {
                    countryName = !TextUtils.isEmpty(address.getCountryName()) ? address.getCountryName() : "";
                    cityName = !TextUtils.isEmpty(address.getLocality()) ? address.getLocality() : "";
                }

                apiFacade.checkLocationForRegistration(activity, countryName, cityName,
                        location.getLatitude(), location.getLongitude());
            }
        });
    }

    public void onNetworkOperation(BaseOperation operation) {
        if (Keys.CHECK_LOCATION_OPERATION_TAG.equals(operation.getTag())) {
            statusImage.clearAnimation();

            if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
                checkLocationResponse = (CheckLocationResponse) operation.getResponseEntities().get(0);

                if (checkLocationResponse.getStatus()) {
                    statusImage.setImageResource(R.drawable.ok_progress);
                    statusText.setText(activity.getString(R.string.check_location_dialog_text2));

                    locationChecked = true;
                } else {
                    statusImage.setImageResource(R.drawable.error_progress);
                    statusText.setText(activity.getString(R.string.check_location_dialog_text3));

                    locationChecked = false;
                }
            } else {
                statusImage.setImageResource(R.drawable.error_progress);
                statusText.setText(activity.getString(R.string.check_location_dialog_text3));

                locationChecked = false;
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (locationChecked) {
                                countryName = !TextUtils.isEmpty(checkLocationResponse.getCountry()) ?
                                        checkLocationResponse.getCountry() : countryName;
                                cityName = !TextUtils.isEmpty(checkLocationResponse.getCity()) ?
                                        checkLocationResponse.getCity() : cityName;

                                checkLocationListener.onLocationChecked(CheckLocationDialog.this,
                                        countryName, cityName, checkLocationResponse);
                            } else {
                                checkLocationListener.onCheckLocationFailed(CheckLocationDialog.this);
                            }
                            dismiss();
                        }
                    }, 1000);
                }
            });
        }

    }

    public interface CheckLocationListener {
        void onLocationChecked(Dialog dialog, String countryName, String cityName,
                               CheckLocationResponse serverResponse);

        void onCheckLocationFailed(Dialog dialog);
    }
}
