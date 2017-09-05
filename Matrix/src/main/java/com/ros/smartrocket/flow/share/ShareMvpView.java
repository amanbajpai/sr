package com.ros.smartrocket.flow.share;

import com.ros.smartrocket.db.entity.Sharing;
import com.ros.smartrocket.flow.base.NetworkMvpView;

interface ShareMvpView extends NetworkMvpView {
    void onSharingLoaded(Sharing sharing);
}
