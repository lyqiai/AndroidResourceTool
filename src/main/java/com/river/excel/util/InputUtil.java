package com.river.excel.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class InputUtil {
    public static File readFile(boolean checkFileExists) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        boolean xmlFileExists = false;
        File xmlFile = null;

        try {
            while (!xmlFileExists) {
                String xmlPath = bufferedReader.readLine();

                xmlFile = new File(xmlPath);
                if (!xmlFile.exists() && checkFileExists) {
                    System.out.println(String.format("文件：%s不存在", xmlPath));
                }

                xmlFileExists = xmlFile.exists() || !checkFileExists;
            }

            return xmlFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean checkStringsXmlExists(String dir) {
        return new File(getXmlPath(dir)).exists();
    }

    public static String getXmlPath(String path) {
        final String xmlFileName = "strings.xml";
        return path + "\\" + xmlFileName;
    }

    public static ArrayList<String> readMutiFile() {
        ArrayList<String> files = new ArrayList<>();

        boolean isCompareInputAll = false;

        while (!isCompareInputAll) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            boolean xmlFileExists = false;
            try {
                while (!xmlFileExists) {
                    String xmlPath = bufferedReader.readLine();
                    if (xmlPath.equals("yes")) {
                        isCompareInputAll = true;
                        xmlFileExists = true;
                    } else {
                        File xmlFile = new File(xmlPath);
                        if (!xmlFile.exists()) {
                            System.out.println(String.format("文件：%s不存在", xmlPath));
                        }

                        xmlFileExists = true;
                        files.add(xmlFile.getAbsolutePath());

                        System.out.println("添加文件成功！当前已添加文件:");
                        for (String path : files) {
                            System.out.println("文件：" + path);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return files;
    }

    public static File readDir(boolean checkDirExists) {
        try {
            while (true) {
                BufferedReader projectBis = new BufferedReader(new InputStreamReader(System.in));
                String dirPath = projectBis.readLine();
                File dir = new File(dirPath);
                if ((!dir.exists() || !dir.isDirectory()) && checkDirExists) {
                    System.out.println("路径有误");
                } else {
                    return dir;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
