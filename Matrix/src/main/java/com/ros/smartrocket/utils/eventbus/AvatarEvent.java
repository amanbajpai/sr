package com.ros.smartrocket.utils.eventbus;

import com.ros.smartrocket.utils.image.SelectImageManager;

public class AvatarEvent {
    public final PhotoEvent.PhotoEventType type;
    public final SelectImageManager.ImageFileClass image;
    public final Integer requestCode;

    public AvatarEvent(PhotoEvent.PhotoEventType type, SelectImageManager.ImageFileClass image, Integer requestCode) {
        this.type = type;
        this.image = image;
        this.requestCode = requestCode;
    }

    public AvatarEvent(PhotoEvent.PhotoEventType type) {
        this(type, null, null);
    }

    public AvatarEvent(PhotoEvent.PhotoEventType type, SelectImageManager.ImageFileClass image) {
        this(type, image, null);
    }

}
