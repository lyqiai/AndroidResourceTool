package com.river.excel.util;

import com.river.excel.model.ExcelBean;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class XmlUtil {
    public static void mapXml(Document document, String tag, MapNode callback) {
        NodeList nodeList = document.getElementsByTagName(tag);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            NamedNodeMap attributes = item.getAttributes();
            Node name = attributes.getNamedItem("name");
            String key = name.getNodeValue();

            Node firstChild = item.getFirstChild();
            String value = "";
            if (firstChild != null) {
                value = firstChild.getNodeValue();
            }

            callback.map(key, value);
        }
    }

    public static void document2file(Document document, String filePath) {
        try {
            File file = new File(filePath);
            System.out.println(file.getParentFile().getAbsolutePath());
            if (!file.getParentFile().exists()) {
                FileUtils.forceMkdir(file.getParentFile());
            }
            if (file.exists()) {
                file.delete();
            }
            TransformerFactory formerFactory = TransformerFactory.newInstance();
            Transformer transformer = formerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "YES");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.transform(new DOMSource(document), new StreamResult(file));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LinkedHashMap<String, String> xml2map(String xmlPath) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlPath);
            NodeList nodeList = document.getElementsByTagName("string");

            int length = nodeList.getLength();

            for (int i = 0; i < length; i++) {
                Node item = nodeList.item(i);

                NamedNodeMap attributes = item.getAttributes();
                Node nameNode = attributes.getNamedItem("name");
                String name = nameNode.getNodeValue();
                Node valueNode = item.getFirstChild();
                if (valueNode != null) {
                    map.put(name, valueNode.getNodeValue());
                }
            }

            return map;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return new LinkedHashMap<>();
    }

    public static void addRow(Document document, Element rootElement, ExcelBean excelBean) {
        Element child = document.createElement("string");
        child.setTextContent(excelBean.getValue());
        child.setAttribute("name", excelBean.getName());

        rootElement.appendChild(child);
    }

    public static void map2xml(String outputFilePath, HashMap<String, String> dataMap) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("resources");
            document.appendChild(rootElement);

            dataMap.forEach((key, value) -> {
                addRow(document, rootElement, new ExcelBean(key, value));
            });

            document2file(document, outputFilePath);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
