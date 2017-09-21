package com.ros.smartrocket.presentation.login.referral;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.ReferralCase;
import com.ros.smartrocket.db.entity.ReferralCases;
import com.ros.smartrocket.db.entity.RegistrationPermissions;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.login.external.ExternalAuthDetailsActivity;
import com.ros.smartrocket.presentation.login.promo.PromoCodeActivity;
import com.ros.smartrocket.presentation.login.registration.RegistrationActivity;
import com.ros.smartrocket.presentation.main.MainActivity;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.RegistrationType;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReferralCasesActivity extends BaseActivity implements ReferralMvpView,
        AdapterView.OnItemSelectedListener {

    @BindView(R.id.referralCasesSpinner)
    Spinner referralCasesSpinner;
    @BindView(R.id.continueButton)
    CustomButton continueButton;

    private int countryId;
    private ReferralCase[] referralCaseArray;
    private RegistrationPermissions registrationPermissions;
    private ReferralMvpPresenter<ReferralMvpView> presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_cases);
        ButterKnife.bind(this);
        fetchArguments();
        initUI();
        getReferralCases();
    }

    private void fetchArguments() {
        registrationPermissions = PreferencesManager.getInstance().getRegPermissions();
        if (getIntent() != null) countryId = getIntent().getIntExtra(Keys.COUNTRY_ID, 0);
    }

    private void initUI() {
        hideActionBar();
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        referralCasesSpinner.setEnabled(false);
        continueButton.setEnabled(false);
        checkDeviceSettingsByOnResume(false);
    }

    private void getReferralCases() {
        presenter = new ReferralPresenter<>();
        presenter.attachView(this);
        presenter.getReferralCases(countryId);
    }

    @Override
    public void onReferralCasesLoaded(ReferralCases cases) {
        referralCaseArray = cases.getCases();
        String[] referralCasesStringArray = new String[referralCaseArray.length + 1];
        referralCasesStringArray[0] = "";

        for (int i = 0; i < referralCaseArray.length; i++) {
            referralCasesStringArray[i + 1] = referralCaseArray[i].getCase();
        }

        ArrayAdapter casesAdapter = new ArrayAdapter<>(this, R.layout.list_item_spinner,
                R.id.name, referralCasesStringArray);
        referralCasesSpinner.setAdapter(casesAdapter);
        referralCasesSpinner.setOnItemSelectedListener(this);
        referralCasesSpinner.setEnabled(true);
    }

    @Override
    public void onReferralCasesSaved() {
        continueRegistrationFlow(getCurrentReferralCaseId());
    }

    @Override
    public void continueWithoutSendCases() {
        continueRegistrationFlow(getCurrentReferralCaseId());
    }

    public int getCurrentReferralCaseId() {
        if (referralCasesSpinner.getSelectedItemPosition() != 0)
            return referralCaseArray[referralCasesSpinner.getSelectedItemPosition() - 1].getId();
        else
            return -1;
    }

    public void continueRegistrationFlow(int referralCasesId) {
        Intent intent;
        RegistrationType type = (RegistrationType) getIntent().getSerializableExtra(Keys.REGISTRATION_TYPE);
        if (registrationPermissions.isSrCodeEnable()) {
            intent = new Intent(this, PromoCodeActivity.class);
        } else if (type == RegistrationType.SOCIAL) {
            intent = new Intent(this, MainActivity.class);
        } else if (type == RegistrationType.SOCIAL_ADDITIONAL_INFO) {
            intent = new Intent(this, ExternalAuthDetailsActivity.class);
        } else {
            intent = new Intent(this, RegistrationActivity.class);
        }

        if (referralCasesId != -1)
            intent.putExtra(Keys.REFERRAL_CASES_ID, referralCasesId);

        intent.putExtras(getIntent().getExtras());
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.referralCasesSpinner)
            continueButton.setEnabled(position != 0);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @OnClick(R.id.continueButton)
    public void onClick() {
        continueButton.setEnabled(false);
        presenter.saveReferralCases(countryId, getCurrentReferralCaseId());
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        continueRegistrationFlow(getCurrentReferralCaseId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
