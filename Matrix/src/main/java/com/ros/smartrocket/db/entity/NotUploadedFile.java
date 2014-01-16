package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.utils.L;

/**
 * Data model of NotUploadedFile entity
 */
public class NotUploadedFile extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private Integer TaskId;
    private Integer QuestionId;
    private String FileUri;
    private String EndDateTime;
    private Boolean use3G;
    private Long fileSizeB;

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
            result.setEndDateTime(c.getString(NotUploadedFileDbSchema.Query.END_DATE_TIME));
            result.setUse3G(c.getInt(NotUploadedFileDbSchema.Query.USE_3G) == 0 ? false : true);
            result.setFileSizeB(c.getLong(NotUploadedFileDbSchema.Query.FILE_SIZE_B));
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

    public String getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(String endDateTime) {
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

}
