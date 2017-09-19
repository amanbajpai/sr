package com.ros.smartrocket.presentation.login.location.failed;

import android.os.Bundle;
import android.view.View;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Subscription;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.ui.dialog.RegistrationSubscribeSuccessDialog;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckLocationFailedActivity extends BaseActivity implements FailedLocationMvpView {

    @BindView(R.id.emailEditText)
    CustomEditTextView emailEditText;
    @BindView(R.id.countryEditText)
    CustomEditTextView countryEditText;
    @BindView(R.id.cityEditText)
    CustomEditTextView cityEditText;

    private int districtId;
    private int countryId;
    private int cityId;
    private String countryName;
    private String cityName;
    private Double latitude;
    private Double longitude;
    private FailedLocationMvpPresenter<FailedLocationMvpView> presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_failed);
        ButterKnife.bind(this);
        presenter = new FailedLocationPresenter<>();
        presenter.attachView(this);
        fetchArguments();
        initUI();
    }

    private void initUI() {
        hideActionBar();
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        countryEditText.setText(countryName);
        cityEditText.setText(cityName);
        checkDeviceSettingsByOnResume(false);
    }

    private void fetchArguments() {
        if (getIntent() != null) {
            districtId = getIntent().getIntExtra(Keys.DISTRICT_ID, 0);
            countryId = getIntent().getIntExtra(Keys.COUNTRY_ID, 0);
            cityId = getIntent().getIntExtra(Keys.CITY_ID, 0);
            countryName = getIntent().getStringExtra(Keys.COUNTRY_NAME);
            cityName = getIntent().getStringExtra(Keys.CITY_NAME);
            latitude = getIntent().getDoubleExtra(Keys.LATITUDE, 0);
            longitude = getIntent().getDoubleExtra(Keys.LONGITUDE, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @OnClick({R.id.subscribeButton, R.id.cancelButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.subscribeButton:
                String email = emailEditText.getText().toString().trim();
                String countryName = countryEditText.getText().toString().trim();
                String cityName = cityEditText.getText().toString().trim();

                UIUtils.setEditTextColorByState(this, emailEditText, UIUtils.isEmailValid(email));
                UIUtils.setEmailEditTextImageByState(emailEditText, UIUtils.isEmailValid(email));

                presenter.subscribe(getSubscription(email, countryName, cityName));
                break;
            case R.id.cancelButton:
                startActivity(IntentUtils.getLoginIntentForLogout(this));
                finish();
                break;
        }
    }

    private Subscription getSubscription(String email, String countryName, String cityName) {
        Subscription subscriptionEntity = new Subscription();
        subscriptionEntity.setEmail(email);
        subscriptionEntity.setCountry(countryName);
        subscriptionEntity.setCity(cityName);
        subscriptionEntity.setLatitude(latitude);
        subscriptionEntity.setLongitude(longitude);
        subscriptionEntity.setDistrictId(districtId);
        subscriptionEntity.setCountryId(countryId);
        subscriptionEntity.setCityId(cityId);
        return subscriptionEntity;
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(this, networkError.getErrorMessageRes());
    }

    @Override
    public void onLocationFailed() {
        UIUtils.showSimpleToast(this, R.string.current_location_not_defined);
    }

    @Override
    public void onSubscriptionSuccess() {
        new RegistrationSubscribeSuccessDialog(this);
    }
}
