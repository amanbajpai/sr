package com.ros.smartrocket.flow.launch;

import com.ros.smartrocket.flow.base.NetworkMvpView;
import com.ros.smartrocket.utils.Version;

interface LaunchMvpView extends NetworkMvpView {
    void launchApp();

    void showUpdateAppDialog(Version currentVersion, Version newestVersion, String versionLink);
}
