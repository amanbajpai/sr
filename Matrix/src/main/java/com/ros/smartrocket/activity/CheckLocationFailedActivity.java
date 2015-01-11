package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.dialog.RegistrationSubscribeSuccessDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

public class CheckLocationFailedActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    private APIFacade apiFacade = APIFacade.getInstance();
    private EditText countryEditText;
    private EditText cityEditText;
    private EditText emailEditText;

    private int districtId;
    private int countryId;
    private int cityId;
    private String countryName;
    private String cityName;
    private Double latitude;
    private Double longitude;

    public CheckLocationFailedActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_checking_failed);

        if (getIntent() != null) {
            districtId = getIntent().getIntExtra(Keys.DISTRICT_ID, 0);
            countryId = getIntent().getIntExtra(Keys.COUNTRY_ID, 0);
            cityId = getIntent().getIntExtra(Keys.CITY_ID, 0);
            countryName = getIntent().getStringExtra(Keys.COUNTRY_NAME);
            cityName = getIntent().getStringExtra(Keys.CITY_NAME);
            latitude = getIntent().getDoubleExtra(Keys.LATITUDE, 0);
            longitude = getIntent().getDoubleExtra(Keys.LONGITUDE, 0);
        }

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        countryEditText = (EditText) findViewById(R.id.countryEditText);
        cityEditText = (EditText) findViewById(R.id.cityEditText);

        countryEditText.setText(countryName);
        cityEditText.setText(cityName);

        findViewById(R.id.subscribeButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);

        checkDeviceSettingsByOnResume(false);
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.SUBSCRIBE_OPERATION_TAG.equals(operation.getTag())) {
                new RegistrationSubscribeSuccessDialog(this);
            }
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subscribeButton:
                String email = emailEditText.getText().toString().trim();
                String countryName = countryEditText.getText().toString().trim();
                String cityName = cityEditText.getText().toString().trim();

                UIUtils.setEditTextColorByState(this, emailEditText, UIUtils.isEmailValid(email));
                UIUtils.setEmailEditTextImageByState(emailEditText, UIUtils.isEmailValid(email));

                if (!UIUtils.isEmailValid(email) || latitude == null || longitude == null) {
                    break;
                }

                apiFacade.subscribe(this, email, countryName, cityName, latitude, longitude,
                        districtId, countryId, cityId);
                break;
            case R.id.cancelButton:
                startActivity(IntentUtils.getLoginIntentForLogout(this));
                finish();
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
