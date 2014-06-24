package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.FontUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.RegistrationFieldTextWatcher;
import com.ros.smartrocket.utils.UIUtils;

public class SetNewPasswordActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface, CompoundButton.OnCheckedChangeListener {
    private APIFacade apiFacade = APIFacade.getInstance();
    private CustomProgressDialog progressDialog;
    private EditText passwordEditText;
    private TextView passwordValidationText;
    private Button setPasswordButton;
    private String email;
    private String token;

    public SetNewPasswordActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_set_new_password);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        setPasswordButton = (Button) findViewById(R.id.setPasswordButton);
        setPasswordButton.setOnClickListener(this);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordValidationText = (TextView) findViewById(R.id.passwordValidationText);

        ((ToggleButton) findViewById(R.id.showPasswordToggleButton)).setOnCheckedChangeListener(this);

        checkDeviceSettingsByOnResume(false);

        if (getIntent() != null) {
            email = getIntent().getStringExtra(Keys.EMAIL);
            token = getIntent().getStringExtra(Keys.TOKEN);

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(token)) {
                setPasswordButton.setEnabled(false);
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

                    progressDialog = CustomProgressDialog.show(this);
                    setPasswordButton.setEnabled(false);

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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.showPasswordToggleButton:
                String text = passwordEditText.getText().toString().trim();
                if (isChecked) {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                passwordEditText.setSelection(text.length());
                passwordEditText.setTypeface(FontUtils.loadFontFromAsset(getAssets(), FontUtils.getFontAssetPath(2)));
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
