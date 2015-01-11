package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.location.Location;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.CheckLocation;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.utils.L;

import org.json.JSONObject;

/**
 * Dialog for checking location and showing results
 */

public class CheckLocationDialog extends Dialog {
    private static final String TAG = CheckLocationDialog.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private Activity activity;
    private ImageView statusImage;
    private TextView statusText;
    private boolean locationChecked = false;
    private CheckLocation checkLocationEntity;
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
        MatrixLocationManager.getAddressByCurrentLocation(false, new MatrixLocationManager.GetAddressListener() {
            @Override
            public void onGetAddressSuccess(Location location, String countryName, String cityName, String districtName) {
                CheckLocationDialog.this.countryName = countryName;
                CheckLocationDialog.this.cityName = cityName;

                apiFacade.checkLocationForRegistration(activity, countryName, cityName,
                        districtName, location.getLatitude(), location.getLongitude());
            }
        });

    }

    public void onNetworkOperation(BaseOperation operation) {
        if (Keys.CHECK_LOCATION_OPERATION_TAG.equals(operation.getTag())) {
            statusImage.clearAnimation();

            checkLocationEntity = (CheckLocation) operation.getEntities().get(0);
            if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
                checkLocationResponse = (CheckLocationResponse) operation.getResponseEntities().get(0);

                if (checkLocationResponse.getStatus()) {
                    checkLocationSuccess();
                } else {
                    checkLocationFail(operation);
                }
            } else {
                checkLocationFail(operation);
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (locationChecked) {
                                checkLocationListener.onLocationChecked(CheckLocationDialog.this,
                                        countryName, cityName, checkLocationEntity.getLatitude(),
                                        checkLocationEntity.getLongitude(), checkLocationResponse);
                            } else {
                                checkLocationListener.onCheckLocationFailed(CheckLocationDialog.this,
                                        countryName, cityName, checkLocationEntity.getLatitude(),
                                        checkLocationEntity.getLongitude(), checkLocationResponse);
                            }
                            dismiss();
                        }
                    }, 1000);
                }
            });
        }
    }

    public void checkLocationSuccess() {
        statusImage.setImageResource(R.drawable.ok_progress);
        statusText.setText(activity.getString(R.string.check_location_dialog_text2));

        if (TextUtils.isEmpty(countryName) && !TextUtils.isEmpty(checkLocationResponse.getCountryName())) {
            this.countryName = checkLocationResponse.getCountryName();
        }
        if (TextUtils.isEmpty(cityName) && !TextUtils.isEmpty(checkLocationResponse.getCityName())) {
            this.cityName = checkLocationResponse.getCityName();
        }

        locationChecked = true;
    }

    public void checkLocationFail(BaseOperation operation) {
        statusImage.setImageResource(R.drawable.error_progress);
        statusText.setText(activity.getString(R.string.check_location_dialog_text3));

        try {
            JSONObject responseJson = new JSONObject(operation.getResponseString());
            String locationData = responseJson.getString("Data");

            checkLocationResponse = new Gson().fromJson(locationData,
                    CheckLocationResponse.class);

        } catch (Exception e) {
            L.e(TAG, "Error in onNetworkOperation method.", e);
        }

        locationChecked = false;
    }

    public interface CheckLocationListener {
        void onLocationChecked(Dialog dialog, String countryName, String cityName, double latitude,
                               double longitude, CheckLocationResponse serverResponse);

        void onCheckLocationFailed(Dialog dialog, String countryName, String cityName, double latitude,
                                   double longitude, CheckLocationResponse serverResponse);
    }
}
