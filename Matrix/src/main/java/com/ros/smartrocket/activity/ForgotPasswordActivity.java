package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    private APIFacade apiFacade = APIFacade.getInstance();
    private EditText emailEditText;
    private Button sendButton;
    private ImageView mailImageView;

    public ForgotPasswordActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forgot_password);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.orange));

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        mailImageView = (ImageView) findViewById(R.id.mailImageView);

        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

        findViewById(R.id.cancelButton).setOnClickListener(this);

        checkDeviceSettingsByOnResume(false);
    }

    @Override
    public void onNetworkOperationSuccess(BaseOperation operation) {
        if (Keys.FORGOT_PASSWORD_OPERATION_TAG.equals(operation.getTag())) {
            finish();
            startActivity(IntentUtils.getForgotPasswordSuccessIntent(this,
                    emailEditText.getText().toString().trim()));
            dismissProgressDialog();
        }

    }

    @Override
    public void onNetworkOperationFailed(BaseOperation operation) {
        if (Keys.FORGOT_PASSWORD_OPERATION_TAG.equals(operation.getTag())) {
            UIUtils.setEditTextColorByState(this, emailEditText, false);
            UIUtils.setEmailImageByState(mailImageView, false);

            sendButton.setEnabled(true);

            UIUtils.showSimpleToast(this, operation.getResponseError());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendButton:
                String email = emailEditText.getText().toString().trim();

                UIUtils.setEditTextColorByState(this, emailEditText, UIUtils.isEmailValid(email));
                UIUtils.setEmailImageByState(mailImageView, UIUtils.isEmailValid(email));

                if (TextUtils.isEmpty(email) || !UIUtils.isEmailValid(email)) {
                    break;
                }

                showProgressDialog(false);
                sendButton.setEnabled(false);
                apiFacade.forgotPassword(this, email);

                break;
            case R.id.cancelButton:
                finish();
                break;
            default:
                break;
        }
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
