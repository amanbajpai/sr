package com.ros.smartrocket.utils.eventbus;

import com.ros.smartrocket.utils.image.SelectImageManager;

public class PhotoEvent {
    public enum PhotoEventType {
        START_LOADING, IMAGE_COMPLETE, SELECT_IMAGE_ERROR
    }

    public final PhotoEventType type;
    public final SelectImageManager.ImageFileClass image;
    public final Integer requestCode;

    public PhotoEvent(PhotoEventType type, SelectImageManager.ImageFileClass image, Integer requestCode) {
        this.type = type;
        this.image = image;
        this.requestCode = requestCode;
    }

    public PhotoEvent(PhotoEventType type) {
        this(type, null, null);
    }

    public PhotoEvent(PhotoEventType type, SelectImageManager.ImageFileClass image) {
        this(type, image, null);
    }
}
