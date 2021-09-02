package com.river.excel.deleteRepeatImage;

import com.river.excel.Constant;
import com.river.excel.util.DependenciesUtil;
import com.river.excel.util.FileUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DependenciesManager {
    /**
     * key: 模块路径
     * value: 依赖模块key的模块路径
     */
    private HashMap<String, ArrayList<String>> data = new HashMap<>();

    public void init(File project) {
        FileUtil.mapFiles(project, file -> {
            if (file.getName().equals(Constant.BUILD_GRADLE_FILE)) {
                try {
                    List<String> strings = FileUtils.readLines(file);
                    for (String line : strings) {
                        if (!DependenciesUtil.isDependProject(line)) {
                            continue;
                        }
                        String key = file.getParentFile().getAbsolutePath();

                        String projectPath = project.getAbsolutePath() + DependenciesUtil.getDependProjectPath(line);

                        if (!data.containsKey(projectPath)) {
                            data.put(projectPath, new ArrayList<String>());
                        }
                        data.get(projectPath).add(key);

                        deepDependencies(project, data.get(projectPath), projectPath + File.separator + Constant.BUILD_GRADLE_FILE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void map(MapDependenciesCallback callback) {
        data.forEach((dependencies, module) -> {
            for (String item : module) {
                callback.call(dependencies, item);
            }
        });
    }

    private void deepDependencies(File projectDir, ArrayList<String> dependencies, String gradlePath) {
        try {
            List<String> lines = FileUtils.readLines(new File(gradlePath));
            for (String line : lines) {
                boolean dependProject = DependenciesUtil.isDeepDependProject(line);
                if (!dependProject) {
                    continue;
                }

                String projectPath = projectDir.getAbsolutePath() + DependenciesUtil.getDependProjectPath(line);
                dependencies.add(projectPath);

                deepDependencies(projectDir, dependencies, projectPath + File.separator + Constant.BUILD_GRADLE_FILE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface MapDependenciesCallback {
        void call(String dependencies, String module);
    }
}
