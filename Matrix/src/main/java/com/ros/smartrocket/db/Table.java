package com.ros.smartrocket.db;

import com.ros.smartrocket.db.entity.*;

@SuppressWarnings("rawtypes")
public enum Table {
    TASK("Task", TaskDbSchema.class, Task.class.getSimpleName()),
    WAVE("Wave", WaveDbSchema.class, Wave.class.getSimpleName()),
    QUESTION("Question", QuestionDbSchema.class, Question.class.getSimpleName()),
    ANSWER("Answer", AnswerDbSchema.class, Answer.class.getSimpleName()),
    NOT_UPLOADED_FILE("NotUploadedFile", NotUploadedFileDbSchema.class, NotUploadedFile.class.getSimpleName()),
    WAITING_VALIDATION_TASK("WaitingValidationTask", WaitingValidationTaskDbSchema.class, WaitingValidationTask.class.getSimpleName());

    private String name;
    private Class schema;
    private String entityName;

    private Table(String name, Class schema, String entityName) {
        this.name = name;
        this.schema = schema;
        this.entityName = entityName;
    }

    public String getName() {
        return name;
    }

    public Class getSchema() {
        return schema;
    }

    public String getEntityName() {
        return entityName;
    }

    public String toString() {
        return this.getName();
    }

    public static Table getTableByEntity(String type) {
        for (Table table : values()) {
            if (type.equals(table.getEntityName())) {
                return table;
            }
        }
        return null;
    }
}
