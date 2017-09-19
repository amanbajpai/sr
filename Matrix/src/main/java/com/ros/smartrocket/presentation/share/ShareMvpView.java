package com.ros.smartrocket.presentation.share;

import com.ros.smartrocket.db.entity.Sharing;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface ShareMvpView extends NetworkMvpView {
    void onSharingLoaded(Sharing sharing);
}
