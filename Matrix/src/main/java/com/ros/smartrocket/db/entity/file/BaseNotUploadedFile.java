package com.ros.smartrocket.db.entity.file;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class BaseNotUploadedFile extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;
    @SerializedName("FileUri")
    private String fileUri;
    @SerializedName("AddedToUploadDateTime")
    private Long addedToUploadDateTime;
    @SerializedName("EndDateTime")
    private Long endDateTime;
    @SerializedName("Portion")
    private Integer portion;
    @SerializedName("FileCode")
    private String fileCode;
    @SerializedName("FileName")
    private String fileName;
    private Long fileSizeB;
    private String fileId;

    public Long getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Long endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public Long getAddedToUploadDateTime() {
        return addedToUploadDateTime;
    }

    public void setAddedToUploadDateTime(Long addedToUploadDateTime) {
        this.addedToUploadDateTime = addedToUploadDateTime;
    }

    public Integer getPortion() {
        return portion;
    }

    public void setPortion(Integer portion) {
        this.portion = portion;
    }

    public String getFileCode() {
        return fileCode;
    }

    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSizeB() {
        return fileSizeB;
    }

    public void setFileSizeB(Long fileSizeB) {
        this.fileSizeB = fileSizeB;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public enum NotificationStepId {
        NONE(0), MIN_15(1), MIN_30(2), MIN_60(3);

        private int stepId;

        private NotificationStepId(int stepId) {
            this.stepId = stepId;
        }

        public int getStepId() {
            return stepId;
        }

        public static NotificationStepId getStep(int id) {
            for (NotificationStepId v : values()) {
                if (v.getStepId() == id) {
                    return v;
                }
            }
            return null;
        }
    }


}
