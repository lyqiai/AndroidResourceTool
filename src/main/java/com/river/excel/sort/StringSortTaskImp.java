package com.river.excel.sort;

import com.river.excel.Task;
import com.river.excel.model.ExcelBean;
import com.river.excel.util.InputUtil;
import com.river.excel.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class StringSortTaskImp implements Task {
    @Override
    public void process() {
        try {
            System.out.println("请输入需要排序整理的res路径");

            File file = InputUtil.readFile(true);

            boolean directory = file.isDirectory();
            if (!directory) {
                System.out.println(file.getAbsolutePath() + "不是目录");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            File[] files = file.listFiles();
            File baseFile = null;
            ArrayList<File> fileList = new ArrayList<>();
            ArrayList<String> dirList = new ArrayList<>();
            ArrayList<Map<String, String>> mapList = new ArrayList<>();
            ArrayList<Document> documentList = new ArrayList<>();
            ArrayList<Element> elementList = new ArrayList<>();
            for (File file1 : files) {
                if (file1.getName().equals("values")) {
                    baseFile = new File(file1.getAbsolutePath(), "strings.xml");
                } else if (file1.getName().contains("values-")) {
                    File file2 = new File(file1.getAbsolutePath(), "strings.xml");
                    if (!file2.exists()) {
                        continue;
                    }
                    fileList.add(file2);

                    dirList.add(file1.getName());

                    LinkedHashMap<String, String> map = XmlUtil.xml2map(file2.getAbsolutePath());
                    mapList.add(map);

                    Document document = builder.newDocument();
                    documentList.add(document);
                    Element rootElement = document.createElement("resources");
                    document.appendChild(rootElement);
                    elementList.add(rootElement);
                }
            }


            Document document = builder.parse(baseFile.getAbsolutePath());
            XmlUtil.mapXml(document, "string", (String key, String value) -> {
                for (Document document1 : documentList) {
                    int index = documentList.indexOf(document1);
                    Map<String, String> map = mapList.get(index);
                    XmlUtil.addRow(document1, elementList.get(index), new ExcelBean(key, map.get(key)));
                }
            });

            for (Document document1 : documentList) {
                int index = documentList.indexOf(document1);
                File file1 = fileList.get(index);
                XmlUtil.document2file(document1, file1.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
