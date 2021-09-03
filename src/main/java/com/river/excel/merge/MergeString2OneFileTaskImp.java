package com.river.excel.merge;

import com.river.excel.ITask;
import com.river.excel.Task;
import com.river.excel.util.FileUtil;
import com.river.excel.util.InputUtil;
import com.river.excel.util.XmlUtil;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Task(id = 8, name = "合并各模块String资源")
public class MergeString2OneFileTaskImp implements ITask {

    @Override
    public void process() {
        System.out.println("请输入项目路径");
        File projectDir = InputUtil.readDir(true);

        System.out.println("请输入输出路径");
        File outputDir = InputUtil.readDir(false);

        HashMap<String, LinkedHashMap<String, String>> data = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> count = new HashMap<>();

        FileUtil.mapFiles(projectDir, file -> {
            if (file.getName().equals("strings.xml")) {
                System.out.println("处理文件：" + file.getAbsolutePath());

                String lan = FileUtil.xmlFileLan(file.getAbsolutePath());
                if (lan == null) {
                    lan = "default";
                }

                if (!data.containsKey(lan)) {
                    data.put(lan, new LinkedHashMap<>());
                }
                HashMap<String, String> dataMap = data.get(lan);
                if (!count.containsKey(lan)) {
                    count.put(lan, new HashMap<>());
                }
                HashMap<String, Integer> countMap = (HashMap<String, Integer>) count.get(lan);

                LinkedHashMap<String, String> xmlMap = XmlUtil.xml2map(file.getAbsolutePath());
                xmlMap.forEach((key, value) -> {
                    if (dataMap.containsKey(key)) {
                        Integer cnt;
                        if (!countMap.containsKey(key)) {
                            cnt = 1;
                        } else {
                            cnt = countMap.get(key) + 1;
                        }
                        countMap.put(key, cnt);

//                        dataMap.put(key + "_repeat_" + cnt, value);
                    } else {
                        dataMap.put(key, value);
                    }
                });
            }
        });

        data.forEach((lan, dataMap) -> {
            String outputPath = String.format("%s%s%s\\strings.xml", outputDir.getAbsolutePath(), File.separator, lan);
            System.out.println("输出文件：" + outputPath);
            XmlUtil.map2xml(outputPath, dataMap);
        });
    }
}
