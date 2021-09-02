package com.river.excel.model;

import lombok.Data;

@Data
public class TaskBean {
    private String taskName;
    private Class taskClz;

    public TaskBean(String taskName, Class taskClz) {
        this.taskName = taskName;
        this.taskClz = taskClz;
    }
}
