package com.river.excel.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
    public static void mapDir(File dir, MapDir callback) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            callback.map(dir);

            for (File file : files) {
                if (file.isDirectory() && !file.getName().equals("build")) {
                    mapDir(file, callback);
                }
            }
        }
    }

    public static void mapFiles(File dir, MapDir callback) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            callback.map(dir);

            for (File file : files) {
                if (file.isDirectory() && !file.getName().equals("build")) {
                    mapFiles(file, callback);
                } else if (file.isFile()){
                    callback.map(file);
                }
            }
        }
    }

    public static String xmlFileLan(String xmlFilePath) {
        final String reg = ".*values-(.{2,})\\\\strings.xml";

        Pattern compile = Pattern.compile(reg);
        Matcher matcher = compile.matcher(xmlFilePath);
        if (matcher.find()) {
            String lan = matcher.group(1);
            return lan;
        }
        return null;
    }

    public static String getModuleRootPath(String stringsXmlPath) {
        return new File(stringsXmlPath)
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getAbsolutePath();
    }

    public interface MapDir {
        void map(File dir);
    }
}
