package com.river.excel;

import com.river.excel.compare.CompareTaskImp;
import com.river.excel.deleteRepeatImage.DeleteRepeatImageTask;
import com.river.excel.deleteRepeatString.DeleteRepeatStringTaskImp;
import com.river.excel.excelTransfer.Excel2stringTaskImp;
import com.river.excel.merge.MergeString2OneFileTaskImp;
import com.river.excel.model.TaskBean;
import com.river.excel.sort.StringSortTaskImp;
import com.river.excel.txtTransfer.Txt2stringTaskImp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * TODO 目前所欲任务都是串行执行，若任务处理时间过长需优化将引入Thread-Worker模式提高性能
 */
public class TaskStarter {
    static TaskBean[] funList = new TaskBean[]{
            new TaskBean("1.自动处理项目string资源重复key", DeleteRepeatStringTaskImp.class),
            new TaskBean("2.读取excel转string资源文件", Excel2stringTaskImp.class),
            new TaskBean("3.对比string资源文件", CompareTaskImp.class),
            new TaskBean("4.过滤无用string资源文件", null),
            new TaskBean("5.txt转String资源文件", Txt2stringTaskImp.class),
            new TaskBean("6.排序", StringSortTaskImp.class),
            new TaskBean("7.自动处理项目重复image资源", DeleteRepeatImageTask.class),
            new TaskBean("8.合并各模块string", MergeString2OneFileTaskImp.class),
    };

    public static void start() {
        System.out.println("请选择你需要的功能：");
        for (TaskBean fun : funList) {
            System.out.println(fun.getTaskName());
        }

        BufferedReader funReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = funReader.readLine().trim();
            int position = Integer.valueOf(line) - 1;
            if (position >= funList.length) {
                start();
                return;
            }

            TaskBean taskBean = funList[position];

            Class taskClz = taskBean.getTaskClz();
            Constructor emptyConstructor = taskClz.getDeclaredConstructor();
            Task task = (Task) emptyConstructor.newInstance();

            task.process();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
