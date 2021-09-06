package com.river.excel.task;

import com.river.excel.anno.Task;

public interface ITask extends Comparable<ITask> {
    void process();

    default int getId() {
        Task annotation = this.getClass().getAnnotation(Task.class);
        return annotation.id();
    }

    default String getName() {
        Task annotation = this.getClass().getAnnotation(Task.class);
        return annotation.name();
    }

    default String getFullName() {
        return String.format("%d.%s", getId(), getName());
    }

    @Override
    default int compareTo(ITask old) {
        return getId() - old.getId();
    }
}
