package com.ros.smartrocket.net;

import com.ros.smartrocket.db.entity.BaseEntity;

public class AttachmentRequestEntity extends BaseEntity {
    private static final long serialVersionUID = 6699669671101562485L;

    private String attachmentPath;

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }
}