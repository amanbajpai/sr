package com.ros.smartrocket.utils.eventbus;

import com.ros.smartrocket.utils.image.SelectImageManager;

public class AvatarEvent extends PhotoEvent {
    public AvatarEvent(PhotoEventType type) {
        super(type);
    }

    public AvatarEvent(PhotoEventType type, SelectImageManager.ImageFileClass image) {
        super(type, image);
    }
}
