package com.ros.smartrocket.db.entity;

/**
 * Data model of NotUploadedFile entity
 */
public class FileToUpload extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private Integer SurveyId;
    private Integer TaskId;
    private Integer QuestionId;
    private String FileBase64;

    public FileToUpload() {
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


    public Integer getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(Integer questionId) {
        QuestionId = questionId;
    }


    public String getFileBase64() {
        return FileBase64;
    }

    public void setFileBase64(String fileBase64) {
        this.FileBase64 = fileBase64;
    }

}
