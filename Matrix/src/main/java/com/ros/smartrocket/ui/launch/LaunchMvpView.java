package com.ros.smartrocket.ui.launch;

import com.ros.smartrocket.ui.base.NetworkMvpView;
import com.ros.smartrocket.utils.Version;

interface LaunchMvpView extends NetworkMvpView {
    void launchApp();

    void showUpdateAppDialog(Version currentVersion, Version newestVersion, String versionLink);
}
