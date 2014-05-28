package com.ros.smartrocket.activity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.RegistrationFieldTextWatcher;
import com.ros.smartrocket.utils.UIUtils;

import java.util.List;

public class SetNewPasswordActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    //private static final String TAG = SetNewPasswordActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private CustomProgressDialog progressDialog;
    private EditText passwordEditText;
    private TextView passwordValidationText;
    private Button setPasswordButton;
    private String email;
    private String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_activate_account);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        setPasswordButton = (Button) findViewById(R.id.activateAccountButton);
        setPasswordButton.setOnClickListener(this);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordValidationText = (TextView) findViewById(R.id.passwordValidationText);

        checkDeviceSettingsByOnResume(false);

        if (getIntent() != null) {
            Uri data = getIntent().getData();
            if (data != null) {
                String scheme = data.getScheme(); // "http"
                String host = data.getHost(); // "twitter.com"
                List<String> params = data.getPathSegments();
                email = params.get(0);
                token = params.get(1);

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(token)) {
                    setPasswordButton.setEnabled(false);
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setPasswordButton:
                if (!UIUtils.isOnline(this)) {
                    DialogUtils.showNetworkDialog(this);
                } else {
                    progressDialog = CustomProgressDialog.show(this);
                    setPasswordButton.setEnabled(false);

                    String password = passwordEditText.getText().toString().trim();

                    boolean isPasswordValid = UIUtils.isPasswordValid(password);
                    UIUtils.setEditTextColorByState(this, passwordEditText, isPasswordValid);
                    UIUtils.setPasswordEditTextImageByState(passwordEditText, isPasswordValid);
                    passwordValidationText.setVisibility(isPasswordValid ? View.GONE : View.VISIBLE);

                    if (!isPasswordValid) {
                        passwordEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this,
                                passwordEditText, passwordValidationText));
                        break;
                    }

                    apiFacade.setPassword(this, email, token, password);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        setSupportProgressBarIndeterminateVisibility(false);
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.SET_PASSWORD_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.dismiss();
                UIUtils.showSimpleToast(this, R.string.success);
                startActivity(IntentUtils.getLoginIntentForLogout(this));
            }

        } else {
            if (Keys.SET_PASSWORD_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.dismiss();
                setPasswordButton.setEnabled(true);
                UIUtils.showSimpleToast(this, operation.getResponseError());
            }
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
