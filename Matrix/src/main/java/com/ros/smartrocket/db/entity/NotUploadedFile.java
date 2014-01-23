package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.utils.L;

/**
 * Data model of NotUploadedFile entity
 */
public class NotUploadedFile extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    public enum NotificationStepId {
        none(0), min_15(1), min_30(2), min_60(3);

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
    private Integer QuestionId;
    private String FileUri;
    private Long AddedToUploadDateTime;
    private Long EndDateTime;
    private Boolean use3G;
    private Long fileSizeB;
    private Integer showNotificationStepId;

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
            result.setUse3G(c.getInt(NotUploadedFileDbSchema.Query.USE_3G) == 0 ? false : true);
            result.setFileSizeB(c.getLong(NotUploadedFileDbSchema.Query.FILE_SIZE_B));
            result.setShowNotificationStepId(c.getInt(NotUploadedFileDbSchema.Query.SHOW_NOTIFICATION_STEP_ID));
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

}
