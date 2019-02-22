package com.ros.smartrocket.db;

import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.file.NotUploadedFile;
import com.ros.smartrocket.db.entity.Notification;
import com.ros.smartrocket.db.entity.question.CustomFieldImageUrls;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.db.entity.task.WaitingUploadTask;
import com.ros.smartrocket.db.entity.task.Wave;

@SuppressWarnings("rawtypes")
public enum Table {
    TASK("Task", TaskDbSchema.class, Task.class.getSimpleName()),
    WAVE("Wave", WaveDbSchema.class, Wave.class.getSimpleName()),
    QUESTION("Question", QuestionDbSchema.class, Question.class.getSimpleName()),
    ANSWER("Answer", AnswerDbSchema.class, Answer.class.getSimpleName()),
    NOT_UPLOADED_FILE("NotUploadedFile", NotUploadedFileDbSchema.class, NotUploadedFile.class.getSimpleName()),
    CUSTOM_FIELD_IMAGE_URL("CustomFieldImageUrl", CustomFieldImageUrlDbSchema.class, CustomFieldImageUrls.class.getSimpleName()),
    WAITING_UPLOAD_TASK("WaitingUploadTask", WaitingUploadTaskDbSchema.class, WaitingUploadTask.class.getSimpleName()),
    NOTIFICATION("Notification", NotificationDbSchema.class, Notification.class.getSimpleName());

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
