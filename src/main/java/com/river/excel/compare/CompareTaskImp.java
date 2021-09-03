package com.river.excel.compare;

import com.river.excel.ITask;
import com.river.excel.Task;
import com.river.excel.util.ConvertUtil;
import com.river.excel.util.InputUtil;
import com.river.excel.util.XmlUtil;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Task(id = 3, name = "对比String资源文件")
public class CompareTaskImp implements ITask {

    private String baseXmlPath;

    private ArrayList<String> compareXmls;

    private LinkedHashMap<String, String> baseData = new LinkedHashMap<>();

    private String outputPath;

    @Override
    public void process() {
        try {
            System.out.println("请输入参考文件路径,如(D:\\xmlDir\\strings.xml)：");
            File baseXmlFile = InputUtil.readFile(true);
            baseXmlPath = baseXmlFile.getAbsolutePath();


            System.out.println("请输入需要处理的文件路径，如（D:\\xmlDir\\strings.xml）,结束以yes结束：");
            compareXmls = InputUtil.readMutiFile();

            System.out.println("请输入输出目录：");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            outputPath = bufferedReader.readLine();
            File output = new File(outputPath);
            if (!output.exists()) {
                output.mkdir();
            }

            baseData = XmlUtil.xml2map(baseXmlPath);

            for (String compareXml : compareXmls) {
                compare(compareXml);
            }

            System.out.println("xml com.river.excel.compare done, all pass!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compare(String xmlPath) {
        System.out.println(String.format("======================START COMPARE FILE %s===========================", xmlPath));

        LinkedHashMap<String, String> map = XmlUtil.xml2map(xmlPath);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element rootElement = document.createElement("resources");
            document.appendChild(rootElement);

            Document baseDoc = builder.parse(baseXmlPath);
            NodeList nodeList = baseDoc.getElementsByTagName("string");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                NamedNodeMap attributes = item.getAttributes();
                Node nameNode = attributes.getNamedItem("name");
                String name = nameNode.getNodeValue();

                Element child = document.createElement("string");
                if (map.containsKey(name)) {
                    String value = map.get(name);
                    child.setTextContent(ConvertUtil.wrap(value));
                    child.setAttribute("name", name);

                    rootElement.appendChild(child);
                } else {
                    String value = baseData.get(name);
                    child.setTextContent(ConvertUtil.wrap(value));
                    child.setAttribute("name", name);

                    rootElement.appendChild(child);
                }
            }

            XmlUtil.document2file(document, outputPath + File.separator + "strings.xml");

            System.out.println(String.format("======================DONE COMPARE FILE %s===========================", xmlPath));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
