package com.ros.smartrocket.presentation.launch;

import com.ros.smartrocket.presentation.base.NetworkMvpView;
import com.ros.smartrocket.utils.Version;

interface LaunchMvpView extends NetworkMvpView {
    void launchApp();

    void showUpdateAppDialog(Version currentVersion, Version newestVersion, String versionLink);
}
