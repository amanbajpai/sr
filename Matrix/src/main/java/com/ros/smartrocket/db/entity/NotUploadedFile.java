package com.ros.smartrocket.db.entity;

import android.database.Cursor;

import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.utils.L;

/**
 * Data model of NotUploadedFile entity
 */
public class NotUploadedFile extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

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

    private Integer TaskId;
    private String TaskName;
    private Integer QuestionId;
    private String FileUri;
    private Long AddedToUploadDateTime;
    private Long EndDateTime;
    private Boolean use3G;
    private Long fileSizeB;
    private Integer showNotificationStepId;
    private Integer Portion;
    private String FileCode;
    private String FileName;

    private Double LongitudeToValidation;
    private Double LatitudeToValidation;

    public NotUploadedFile() {
    }

    public static NotUploadedFile fromCursor(Cursor c) {
        NotUploadedFile result = new NotUploadedFile();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(NotUploadedFileDbSchema.Query._ID));
            result.setId(c.getInt(NotUploadedFileDbSchema.Query.ID));
            result.setTaskId(c.getInt(NotUploadedFileDbSchema.Query.TASK_ID));
            result.setQuestionId(c.getInt(NotUploadedFileDbSchema.Query.QUESTION_ID));
            result.setFileUri(c.getString(NotUploadedFileDbSchema.Query.FILE_URI));
            result.setAddedToUploadDateTime(c.getLong(NotUploadedFileDbSchema.Query.ADDED_TO_UPLOAD_DATE_TIME));
            result.setEndDateTime(c.getLong(NotUploadedFileDbSchema.Query.END_DATE_TIME));
            result.setUse3G(c.getInt(NotUploadedFileDbSchema.Query.USE_3G) != 0);
            result.setFileSizeB(c.getLong(NotUploadedFileDbSchema.Query.FILE_SIZE_B));
            result.setShowNotificationStepId(c.getInt(NotUploadedFileDbSchema.Query.SHOW_NOTIFICATION_STEP_ID));

            result.setPortion(c.getInt(NotUploadedFileDbSchema.Query.PORTION));
            result.setFileCode(c.getString(NotUploadedFileDbSchema.Query.FILE_CODE));
            result.setFileName(c.getString(NotUploadedFileDbSchema.Query.FILE_NAME));

            result.setTaskName(c.getString(NotUploadedFileDbSchema.Query.TASK_NAME));

            result.setLatitudeToValidation(c.getDouble(NotUploadedFileDbSchema.Query.LATITUDE_TO_VALIDATION));
            result.setLongitudeToValidation(c.getDouble(NotUploadedFileDbSchema.Query.LONGITUDE_TO_VALIDATION));
        }
        L.d("NotUploadedFile", result.toString());
        return result;
    }

    public Integer getTaskId() {
        return TaskId;
    }

    public void setTaskId(Integer taskId) {
        TaskId = taskId;
    }

    public Long getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(Long endDateTime) {
        EndDateTime = endDateTime;
    }

    public Integer getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(Integer questionId) {
        QuestionId = questionId;
    }

    public String getFileUri() {
        return FileUri;
    }

    public void setFileUri(String fileUri) {
        FileUri = fileUri;
    }


    public Boolean getUse3G() {
        return use3G;
    }

    public void setUse3G(Boolean use3G) {
        this.use3G = use3G;
    }

    public Long getFileSizeB() {
        return fileSizeB;
    }

    public void setFileSizeB(Long fileSizeB) {
        this.fileSizeB = fileSizeB;
    }


    public Long getAddedToUploadDateTime() {
        return AddedToUploadDateTime;
    }

    public void setAddedToUploadDateTime(Long addedToUploadDateTime) {
        AddedToUploadDateTime = addedToUploadDateTime;
    }


    public Integer getShowNotificationStepId() {
        return showNotificationStepId;
    }

    public void setShowNotificationStepId(Integer showNotificationStepId) {
        this.showNotificationStepId = showNotificationStepId;
    }


    public Integer getPortion() {
        return Portion;
    }

    public void setPortion(Integer portion) {
        Portion = portion;
    }

    public String getFileCode() {
        return FileCode;
    }

    public void setFileCode(String fileCode) {
        FileCode = fileCode;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public Double getLongitudeToValidation() {
        return LongitudeToValidation == null ? 0 : LongitudeToValidation;
    }

    public void setLongitudeToValidation(Double longitudeToValidation) {
        LongitudeToValidation = longitudeToValidation;
    }

    public Double getLatitudeToValidation() {
        return LatitudeToValidation == null ? 0 : LatitudeToValidation;
    }

    public void setLatitudeToValidation(Double latitudeToValidation) {
        LatitudeToValidation = latitudeToValidation;
    }
}
