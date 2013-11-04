package com.matrix.db.entity;

import android.database.Cursor;
import com.matrix.db.TaskDbSchema;

public class Task extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;


    private String name;
    private String description;

    public Task() {
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Task fromCursor(Cursor c) {
        Task result = new Task();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(TaskDbSchema.Query._ID));
            result.setId(c.getString(TaskDbSchema.Query.ID));
            result.setName(c.getString(TaskDbSchema.Query.NAME));
            result.setDescription(c.getString(TaskDbSchema.Query.DESCRIPTION));
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
