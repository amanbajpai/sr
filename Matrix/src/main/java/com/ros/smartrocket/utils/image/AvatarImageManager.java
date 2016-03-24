package com.ros.smartrocket.utils.image;

import com.ros.smartrocket.eventbus.AvatarEvent;
import com.ros.smartrocket.eventbus.PhotoEvent;

import de.greenrobot.event.EventBus;

public class AvatarImageManager extends SelectImageManager {

    @Override
    protected void onImageStartLoading() {
        EventBus.getDefault().post(new AvatarEvent(PhotoEvent.PhotoEventType.START_LOADING));
    }

    @Override
    protected void onImageCompleteLoading(ImageFileClass image) {
        EventBus.getDefault().post(new AvatarEvent(PhotoEvent.PhotoEventType.IMAGE_COMPLETE, image));
    }

    @Override
    protected void onImageErrorLoading(int requestCode) {
        EventBus.getDefault().post(new AvatarEvent(PhotoEvent.PhotoEventType.SELECT_IMAGE_ERROR, requestCode));
    }

    @Override
    protected void onImageRemoved() {
        EventBus.getDefault().post(new AvatarEvent(PhotoEvent.PhotoEventType.IMAGE_COMPLETE));
    }
}
