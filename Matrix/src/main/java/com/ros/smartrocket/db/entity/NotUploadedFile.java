package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.utils.L;

/**
 * Data model of NotUploadedFile entity
 */
public class NotUploadedFile extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private Integer SurveyId;
    private Integer TaskId;
    private Integer QuestionId;
    private String FileUri;
    private String EndDateTime;

    public NotUploadedFile() {
    }

    public static NotUploadedFile fromCursor(Cursor c) {
        NotUploadedFile result = new NotUploadedFile();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(NotUploadedFileDbSchema.Query._ID));
            result.setId(c.getInt(NotUploadedFileDbSchema.Query.ID));
            result.setSurveyId(c.getInt(NotUploadedFileDbSchema.Query.SURVEY_ID));
            result.setTaskId(c.getInt(NotUploadedFileDbSchema.Query.TASK_ID));
            result.setQuestionId(c.getInt(NotUploadedFileDbSchema.Query.QUESTION_ID));
            result.setFileUri(c.getString(NotUploadedFileDbSchema.Query.FILE_URI));
            result.setEndDateTime(c.getString(NotUploadedFileDbSchema.Query.END_DATE_TIME));
        }
        L.d("NotUploadedFile", result.toString());
        return result;
    }


    public Integer getSurveyId() {
        return SurveyId;
    }

    public void setSurveyId(Integer surveyId) {
        SurveyId = surveyId;
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

}
