package com.matrix.db;

import com.matrix.db.entity.Survey;
import com.matrix.db.entity.Task;

@SuppressWarnings("rawtypes")
public enum Table {
    TASK("Task", TaskDbSchema.class, Task.class.getSimpleName()),
    SURVEY("Survey", SurveyDbSchema.class, Survey.class.getSimpleName());

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
