package com.ros.smartrocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.AppVersion;
import com.ros.smartrocket.dialog.UpdateVersionDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.Version;

import cn.jpush.android.api.JPushInterface;

public class LaunchActivity extends BaseActivity implements NetworkOperationListenerInterface {
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkDeviceSettingsByOnResume(false);
        setContentView(R.layout.activity_launch);
        if (BuildConfig.CHINESE) {
            showProgressDialog(false);
            apiFacade.getAppVersion(this);
        } else {
            launchApp();
        }
    }

    private void launchApp() {
        Intent intent;
        if (!TextUtils.isEmpty(PreferencesManager.getInstance().getToken())
                && preferencesManager.getLastAppVersion() == UIUtils.getAppVersionCode(this)) {
            if (PreferencesManager.getInstance().isTandCShowed()) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, TermsAndConditionActivity.class);
                intent.putExtra(Keys.SHOULD_SHOW_MAIN_SCREEN, true);
            }
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    public void onNetworkOperationSuccess(BaseOperation operation) {
        if (Keys.GET_VERSION_OPERATION_TAG.equals(operation.getTag())) {
            dismissProgressDialog();
            final AppVersion appVersion = (AppVersion) operation.getResponseEntities().get(0);
            preferencesManager.saveAppVersion(appVersion);
            Version currentVersion = new Version(BuildConfig.VERSION_NAME);
            Version newestVersion = new Version(appVersion.getLatestVersion());
            if (currentVersion.compareTo(newestVersion) < 0) {
                new UpdateVersionDialog(this, currentVersion.toString(), newestVersion.toString(), new UpdateVersionDialog.DialogButtonClickListener() {
                    @Override
                    public void onCancelButtonPressed() {
                        launchApp();
                    }

                    @Override
                    public void onOkButtonPressed() {
                        startActivity(IntentUtils.getBrowserIntent(appVersion.getLatestVersionLink()));
                        finish();
                    }
                });
            } else {
                launchApp();
            }
        }
    }

    @Override
    public void onNetworkOperationFailed(BaseOperation operation) {
        if (Keys.GET_VERSION_OPERATION_TAG.equals(operation.getTag())) {
            dismissProgressDialog();
            UIUtils.showSimpleToast(this, operation.getResponseError(), Toast.LENGTH_LONG, Gravity.BOTTOM);
            launchApp();
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
