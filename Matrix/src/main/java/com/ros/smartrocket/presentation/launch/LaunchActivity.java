package com.ros.smartrocket.presentation.launch;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.github.euzee.permission.CallbackBuilder;
import com.github.euzee.permission.PermissionCallback;
import com.github.euzee.permission.PermissionUtil;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.login.LoginActivity;
import com.ros.smartrocket.presentation.login.terms.TermsAndConditionActivity;
import com.ros.smartrocket.presentation.main.MainActivity;
import com.ros.smartrocket.ui.dialog.UpdateVersionDialog;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.Version;

import cn.jpush.android.api.JPushInterface;

public class LaunchActivity extends BaseActivity implements LaunchMvpView {
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private LaunchMvpPresenter<LaunchMvpView> presenter;
    private PermissionCallback permissionCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkDeviceSettingsByOnResume(false);
        setContentView(R.layout.activity_launch);
        presenter = new LaunchPresenter<>();
        presenter.attachView(this);
        initPermissionCallbacks();
    }

    public void launchApp() {
        Intent intent;
        if (!TextUtils.isEmpty(PreferencesManager.getInstance().getToken())
                && preferencesManager.getLastAppVersion() == UIUtils.getAppVersionCode(this)) {
            if (preferencesManager.isTandCShowed()) {
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

    @Override
    public void showUpdateAppDialog(Version currentVersion, Version newestVersion, String versionLink) {
        new UpdateVersionDialog(this, currentVersion.toString(), newestVersion.toString(), new UpdateVersionDialog.DialogButtonClickListener() {
            @Override
            public void onCancelButtonPressed() {
                launchApp();
            }

            @Override
            public void onOkButtonPressed() {
                startActivity(IntentUtils.getBrowserIntent(versionLink));
                finish();
            }
        });
    }

    @Override
    public void showNetworkError(BaseNetworkError error) {
        UIUtils.showSimpleToast(this, error.getErrorMessageRes(), Toast.LENGTH_LONG, Gravity.BOTTOM);
        launchApp();
    }

    protected void onResume() {
        super.onResume();
        checkPermission();
        JPushInterface.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void checkPermission() {
        PermissionUtil.locationBoth(this, permissionCallback);
    }

    private void initPermissionCallbacks() {
        permissionCallback = new CallbackBuilder()
                .onGranted(() -> presenter.checkVersion())
                .onDenied(this::launchApp)
                .build();
    }
}
