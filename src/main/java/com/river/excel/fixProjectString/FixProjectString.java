package com.river.excel.fixProjectString;

import com.river.excel.anno.Task;
import com.river.excel.task.ITask;
import com.river.excel.util.FileUtil;
import com.river.excel.util.InputUtil;
import com.river.excel.util.XmlUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

@Component
@Task(id = 10, name = "修复替换项目中string资源文件")
public class FixProjectString implements ITask {
    private File projectDir;
    private File keyFile;
    private Scanner scanner;
    private HashMap<String, File> lanFileMap = new HashMap<>();
    private HashMap<String, List<String>> lanContentMap = new HashMap<>();
    private String defaultLan;
    private List<String> keyStrings;

    public FixProjectString() {
        scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
    }

    @Override
    public void process() {
        System.out.println("请输入项目路径：");
        projectDir = InputUtil.readDir(true);
        System.out.println("请输入项目value目录strings.xml默认语言缩写");
        defaultLan = scanner.nextLine();
        System.out.println("请输入key文件路径：");
        keyFile = InputUtil.readFile(true);
        System.out.println("请输入content文件路径：");
        while (true) {
            File file = InputUtil.readFile(true);
            System.out.println("请输入该文件在Android工程中values-xx的语言缩写例如zh(yes结束)");
            String lan = scanner.nextLine();
            if (lan.equals("yes")) {
                break;
            } else {
                lanFileMap.put(lan, file);
            }
        }

        try {
            keyStrings = FileUtils.readLines(keyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        lanFileMap.forEach((key, file) -> {
            try {
                List<String> strings = FileUtils.readLines(file);
                lanContentMap.put(key, strings);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        FileUtil.mapDir(projectDir, dir -> {
            final String stringsFilePath = dir.getAbsolutePath() + File.separator + "strings.xml";
            final String valueDirReg = "value|value-\\w+";

            if (dir.getName().matches(valueDirReg) && new File(stringsFilePath).exists()) {
                String lan = dir.getName().equals("value") ? defaultLan : dir.getName().split("-")[1];

                if (lanContentMap.containsKey(lan)) {
                    fixXml(stringsFilePath, keyStrings, lanContentMap.get(lan));
                }
            }
        });
    }

    private void fixXml(String xmlPath, List<String> keys, List<String> contents) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlPath);
            NodeList nodeList = document.getElementsByTagName("string");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);

                NamedNodeMap attributes = item.getAttributes();
                Node nameNode = attributes.getNamedItem("name");
                String name = nameNode.getNodeValue();
                int index = keys.indexOf(name);
                if (index != -1 && index < contents.size()) {
                    String content = contents.get(index);
                    if (!item.getFirstChild().getNodeValue().equals(content)) {
                        System.out.println(String.format("修改%s值%s为%s", name, item.getFirstChild().getNodeValue(), content));
                    }
                    item.setTextContent(content);
                }
            }

            XmlUtil.document2file(document, xmlPath);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
