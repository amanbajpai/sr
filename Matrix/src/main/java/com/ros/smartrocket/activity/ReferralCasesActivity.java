package com.ros.smartrocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.ReferralCase;
import com.ros.smartrocket.db.entity.ReferralCases;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.UIUtils;

public class ReferralCasesActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface, AdapterView.OnItemSelectedListener {
    private APIFacade apiFacade = APIFacade.getInstance();
    private int countryId;
    private Spinner referralCasesSpinner;
    private Button continueButton;
    private ReferralCase[] referralCaseArray;

    public ReferralCasesActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_referral_cases);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            countryId = getIntent().getIntExtra(Keys.COUNTRY_ID, 0);
        }

        referralCasesSpinner = (Spinner) findViewById(R.id.referralCasesSpinner);
        referralCasesSpinner.setEnabled(false);

        continueButton = (Button) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);
        continueButton.setEnabled(false);

        checkDeviceSettingsByOnResume(false);

        apiFacade.getReferralCases(this, countryId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueButton:
                continueButton.setEnabled(false);
                apiFacade.saveReferralCases(this, countryId, getCurrentReferralCaseId());
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        setSupportProgressBarIndeterminateVisibility(false);
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_REFERRAL_CASES_OPERATION_TAG.equals(operation.getTag())) {
                ReferralCases referralCases = (ReferralCases) operation.getResponseEntities().get(0);
                referralCaseArray = referralCases.getCases();

                String[] referralCasesStringArray = new String[referralCaseArray.length + 1];
                referralCasesStringArray[0] = "";
                for (int i = 0; i < referralCaseArray.length; i++) {
                    referralCasesStringArray[i + 1] = referralCaseArray[i].getCase();
                }

                ArrayAdapter educationLevelAdapter = new ArrayAdapter<>(this, R.layout.list_item_spinner,
                        R.id.name, referralCasesStringArray);
                referralCasesSpinner.setAdapter(educationLevelAdapter);
                referralCasesSpinner.setOnItemSelectedListener(this);
                referralCasesSpinner.setEnabled(true);

            } else if (Keys.SAVE_REFERRAL_CASES_OPERATION_TAG.equals(operation.getTag())) {
                startCheckLocationActivity(getCurrentReferralCaseId());
            }
        } else {
            startCheckLocationActivity(-1);
        }
    }

    public int getCurrentReferralCaseId() {
        return referralCaseArray[referralCasesSpinner.getSelectedItemPosition() - 1].getId();
    }

    public void startCheckLocationActivity(int referralCasesId) {
        Intent intent = new Intent(this, CheckLocationActivity.class);

        if (referralCasesId != -1) {
            intent.putExtra(Keys.REFERRAL_CASES_ID, referralCasesId);
        }

        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);

        finish();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.referralCasesSpinner) {
            continueButton.setEnabled(position != 0);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
