package com.river.excel;

import com.river.excel.compare.CompareTaskImp;
import com.river.excel.deleteRepeatImage.DeleteRepeatImageTask;
import com.river.excel.deleteRepeatString.DeleteRepeatStringTaskImp;
import com.river.excel.excelTransfer.Excel2stringTaskImp;
import com.river.excel.merge.MergeString2OneFileTaskImp;
import com.river.excel.sort.StringSortTaskImp;
import com.river.excel.string2excel.String2ExcelTask;
import com.river.excel.txtTransfer.Txt2stringTaskImp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * TODO 目前所欲任务都是串行执行，若任务处理时间过长需优化将引入Thread-Worker模式提高性能
 */
public class TaskStarter {
    private Class<?>[] funList = new Class<?>[]{
            DeleteRepeatStringTaskImp.class,
            Excel2stringTaskImp.class,
            CompareTaskImp.class,
            Txt2stringTaskImp.class,
            StringSortTaskImp.class,
            DeleteRepeatImageTask.class,
            MergeString2OneFileTaskImp.class,
            String2ExcelTask.class
    };
    private HashMap<Integer, ITask> tasks = new HashMap<>();

    public void start() throws Exception {
        System.out.println("请选择你需要的功能：");
        for (Class clz : funList) {
            Constructor emptyConstructor = clz.getDeclaredConstructor();
            ITask tast = (ITask) emptyConstructor.newInstance();
            Task annotation = tast.getClass().getAnnotation(Task.class);

            tasks.put(annotation.id(), tast);

            System.out.println(String.format("%d.%s", annotation.id(), annotation.name()));
        }

        BufferedReader funReader = new BufferedReader(new InputStreamReader(System.in));
        String line = funReader.readLine().trim();
        int taskId = Integer.valueOf(line);
        assert tasks.containsKey(taskId);

        tasks.get(taskId).process();
    }
}
