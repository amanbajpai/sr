package com.ros.smartrocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.UIUtils;

/**
 * Activity for Agents login into system
 */
public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    //private static final String TAG = ForgotPasswordActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private EditText emailEditText;
    private Button sendButton;
    private ImageView mailImageView;

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

        setSupportProgressBarIndeterminateVisibility(false);

        checkDeviceSettingsByOnResume(false);
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        setSupportProgressBarIndeterminateVisibility(false);
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.FORGOT_PASSWORD_OPERATION_TAG.equals(operation.getTag())) {
                finish();
                startActivity(new Intent(this, ForgotPasswordSuccessActivity.class));
            }
        } else {
            if (Keys.FORGOT_PASSWORD_OPERATION_TAG.equals(operation.getTag())) {
                sendButton.setEnabled(true);
                UIUtils.showSimpleToast(this, operation.getResponseError());
            }
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

                sendButton.setEnabled(false);
                setSupportProgressBarIndeterminateVisibility(true);
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
