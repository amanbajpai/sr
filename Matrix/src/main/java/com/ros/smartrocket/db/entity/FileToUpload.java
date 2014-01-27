package com.ros.smartrocket.db.entity;

/**
 * Data model of NotUploadedFile entity
 */
public class FileToUpload extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private Integer TaskId;
    private Integer QuestionId;
    private String FileCode;
    private String Filename;
    private Long FileLength;
    private Long FileOffset;
    private String FileBase64String;

    public FileToUpload() {
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

    public String getFileCode() {
        return FileCode;
    }

    public void setFileCode(String fileCode) {
        FileCode = fileCode;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String filename) {
        Filename = filename;
    }

    public Long getFileLength() {
        return FileLength;
    }

    public void setFileLength(Long fileLength) {
        FileLength = fileLength;
    }

    public String getFileBase64String() {
        return FileBase64String;
    }

    public void setFileBase64String(String fileBase64String) {
        FileBase64String = fileBase64String;
    }


    public Long getFileOffset() {
        return FileOffset;
    }

    public void setFileOffset(Long fileOffset) {
        FileOffset = fileOffset;
    }

}
