package com.river.excel.deleteRepeatImage;

import com.river.excel.ITask;
import com.river.excel.Task;
import com.river.excel.util.DrawableUtil;
import com.river.excel.util.FileUtil;
import com.river.excel.util.ImageUtil;
import com.river.excel.util.InputUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Task(id = 7, name = "自动处理项目重复Image资源")
public class DeleteRepeatImageTask implements ITask {
    private File projectDir;
    private DependenciesManager dependenciesManager = new DependenciesManager();

    @Override
    public void process() {
        System.out.println("请输入项目路径：");
        projectDir = InputUtil.readDir(true);
        dependenciesManager.init(projectDir);
        dependenciesManager.map((dependencies, module) -> {
            Map<String, ArrayList<String>> dependenciesData = getModuleAllDrawable(dependencies);
            Map<String, ArrayList<String>> moduleData = getModuleAllDrawable(module);
            moduleData.forEach((key, value) -> {
                if (!dependenciesData.containsKey(key)) {
                    return;
                }
                ArrayList<File> temp = new ArrayList<File>(dependenciesData.get(key).stream().map(item -> new File(item)).collect(Collectors.toCollection(ArrayList::new)));

                for (String item : value) {
                    File file = new File(item);
                    ArrayList<File> findList = temp.stream().filter(it -> it.getName().equals(file.getName())).collect(Collectors.toCollection(ArrayList::new));
                    if (findList.isEmpty()) {
                        continue;
                    }

                    File findFile = findList.get(0);
                    if (findFile.length() != file.length()) {
                        continue;
                    }

                    if (file.getName().endsWith(".xml") && DrawableUtil.compareDrawable(file.getAbsolutePath(), findFile.getAbsolutePath())) {
                        confirmDelete(file);
                    } else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") && ImageUtil.compareImage(file, findFile)) {
                        confirmDelete(file);
                    }
                }
            });
        });
    }

    private void confirmDelete(File file) {
        System.out.println("重复资源：" + file.getAbsolutePath() + ",是否删除该文件？（y/n）");
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
        String read = scanner.nextLine();
        if (read.equals("y")) {
            file.delete();
        }
    }

    private Map<String, ArrayList<String>> getModuleAllDrawable(String module) {
        HashMap<String, ArrayList<String>> moduleDrawable = new HashMap<>();
        File moduleResourceDir = new File(module + File.separator + "src" + File.separator + "main" + File.separator + "res");
        if (moduleResourceDir.exists()) {
            FileUtil.mapDir(moduleResourceDir, dir -> {
                if (dir.getName().contains("drawable") || dir.getName().contains("mipmap")) {
                    FileUtil.mapFiles(dir, file -> {
                        if (!moduleDrawable.containsKey(dir.getName())) {
                            moduleDrawable.put(dir.getName(), new ArrayList<>());
                        }
                        moduleDrawable.get(dir.getName()).add(file.getAbsolutePath());
                    });
                }
            });
        }
        return moduleDrawable;
    }
}
