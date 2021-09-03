package com.river.excel.string2excel;

import com.alibaba.excel.EasyExcel;
import com.river.excel.ITask;
import com.river.excel.Task;
import com.river.excel.util.InputUtil;
import com.river.excel.util.XmlUtil;

import java.io.File;
import java.util.*;

@Task(id = 9, name = "strings.xml转excel")
public class String2ExcelTask implements ITask {
    private File output;
    private List<String> files;
    private HashMap<String, LinkedHashMap<String, String>> data = new HashMap<>();
    private LinkedHashSet<String> keys = new LinkedHashSet<>();

    @Override
    public void process() {
        System.out.println("请输入strings资源文件路径：（yes结束录入）");
        files = InputUtil.readMutiFile();
        System.out.println("请输入生成excel文件路径：");
        output = InputUtil.readFile(false);

        for (String file : files) {
            LinkedHashMap<String, String> data = XmlUtil.xml2map(file);
            this.data.put(file, data);
            keys.addAll(data.keySet());
        }

        // 写法1
        String fileName = "D:\\output.xlsx";
        EasyExcel.write(fileName).head(head()).sheet("模板").doWrite(dataList());
    }


    private List<List<String>> head() {
        List<List<String>> list = new ArrayList<List<String>>();

        List<String> head0 = new ArrayList<String>();
        head0.add("Key");
        list.add(head0);

        for (String file : files) {
            List<String> lan = new ArrayList<String>();
            lan.add("头部" + files.indexOf(file));
            list.add(lan);
        }
        return list;
    }

    private List<List<Object>> dataList() {
        List<List<Object>> list = new ArrayList<List<Object>>();
        keys.forEach((key) -> {
            List<Object> data = new ArrayList<Object>();
            data.add(key);
            this.data.forEach((file, mapData) -> {
                data.add(mapData.containsKey(key) ? mapData.get(key) : "lost value!!!");
            });
            list.add(data);
        });
        return list;
    }


    public static void main(String[] args) {
        new String2ExcelTask().process();
    }
}
