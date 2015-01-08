package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class Tasks extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    @SerializedName("Tasks")
    private Task[] tasks;

    public Tasks() {
    }

    public Task[] getTasks() {
        return tasks;
    }

    public void setTasks(Task[] tasks) {
        this.tasks = tasks;
    }
}
