package com.river.excel.txtTransfer;

import com.river.excel.task.ITask;
import com.river.excel.anno.Task;
import com.river.excel.model.ExcelBean;
import com.river.excel.util.InputUtil;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.river.excel.util.XmlUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

@Task(id = 5, name = "Txt转String资源文件")
@Component
public class Txt2stringTaskImp implements ITask {
    File outputFile;

    @Override
    public void process() {
        try {
            System.out.println("请输入key文件路径：");
            File keyFile = InputUtil.readFile(true);

            System.out.println("请输入content文件路径：");
            File contentFile = InputUtil.readFile(true);

            System.out.println("请输入string资源文件输出路径：");
            outputFile = InputUtil.readFile(false);

            ArrayList<String> keys = readFile(keyFile.getAbsolutePath());
            ArrayList<String> contents = readFile(contentFile.getAbsolutePath());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element rootElement = document.createElement("resources");
            document.appendChild(rootElement);

            for (String key : keys) {
                int index = keys.indexOf(key);
                String value = "";
                if (index < contents.size()) {
                    value = contents.get(index);
                }

                XmlUtil.addRow(document, rootElement, new ExcelBean(key, value));
            }

            XmlUtil.document2file(document, outputFile.getAbsolutePath());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<String> readFile(String filePath) {
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            ArrayList<String> keys = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    keys.add(line);
                }
            }

            return keys;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
