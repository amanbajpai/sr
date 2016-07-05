package com.ros.smartrocket.eventbus;

public final class UploadProgressEvent {
    private boolean isDone;

    public UploadProgressEvent(boolean isDone) {
        this.isDone = isDone;
    }

    public boolean isDone() {
        return isDone;
    }
}
