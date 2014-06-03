package com.ros.smartrocket.activity;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.dialog.RegistrationSubscribeSuccessDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

public class CheckLocationFailedActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    private APIFacade apiFacade = APIFacade.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private EditText countryEditText;
    private EditText cityEditText;
    private EditText emailEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_checking_failed);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        countryEditText = (EditText) findViewById(R.id.countryEditText);
        cityEditText = (EditText) findViewById(R.id.cityEditText);

        setCurrentAddressByLocation();

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

                if (TextUtils.isEmpty(countryName) || TextUtils.isEmpty(cityName) || !UIUtils.isEmailValid(email)) {
                    break;
                }

                apiFacade.subscribe(this, email, countryName, cityName);
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

    private void setCurrentAddressByLocation() {
        Location location = lm.getLocation();
        if (location != null) {
            lm.getAddress(location, new MatrixLocationManager.IAddress() {
                @Override
                public void onUpdate(Address address) {
                    if (address != null) {
                        countryEditText.setText(address.getCountryName());
                        cityEditText.setText(address.getLocality());
                    }
                }
            });
        }
    }
}
