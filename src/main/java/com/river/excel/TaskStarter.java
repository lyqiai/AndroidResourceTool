package com.river.excel;

import com.river.excel.anno.Task;
import com.river.excel.task.ITask;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO 目前所欲任务都是串行执行，若任务处理时间过长需优化将引入Thread-Worker模式提高性能
 */
public class TaskStarter {
    private final ApplicationContext context;
    private final List<ITask> tasks;

    public TaskStarter() {
        context = new AnnotationConfigApplicationContext(Config.class);
        Map<String, Object> tasks = context.getBeansWithAnnotation(Task.class);
        this.tasks = tasks.values().stream().map(it -> (ITask) it).sorted().collect(Collectors.toList());
    }

    public void start() throws Exception {
        System.out.println("请选择你需要的功能：");
        for (ITask tast : tasks) {
            System.out.println(tast.getFullName());
        }
        while (true) {
            BufferedReader funReader = new BufferedReader(new InputStreamReader(System.in));
            String line = funReader.readLine().trim();
            int taskId = Integer.valueOf(line);
            for (ITask task : tasks) {
                if (taskId == task.getId()) {
                    task.process();
                    return;
                }
            }
            System.out.println("当前任务编号不存在！请重新输入");
        }
    }
}
