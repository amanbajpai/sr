package com.ros.smartrocket.eventbus;

import com.ros.smartrocket.utils.image.SelectImageManager;

public class PhotoEvent {
    public enum PhotoEventType {
        START_LOADING, IMAGE_COMPLETE, SELECT_IMAGE_ERROR
    }

    public final PhotoEventType type;
    public final SelectImageManager.ImageFileClass image;
    public final int imageFrom;

    private PhotoEvent(PhotoEventType type, SelectImageManager.ImageFileClass image, int imageFrom) {
        this.type = type;
        this.image = image;
        this.imageFrom = imageFrom;
    }

    public PhotoEvent(PhotoEventType type) {
        this(type, null, -1);
    }

    public PhotoEvent(PhotoEventType type, int imageFrom) {
        this(type, null, imageFrom);
    }

    public PhotoEvent(PhotoEventType type, SelectImageManager.ImageFileClass image) {
        this(type, image, -1);
    }
}
