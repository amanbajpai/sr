package com.ros.smartrocket.db.entity;

public class Tasks extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private Task[] Tasks;

    public Tasks() {
    }

    public Task[] getTasks() {
        return Tasks;
    }

    public void setTasks(Task[] tasks) {
        Tasks = tasks;
    }
}
