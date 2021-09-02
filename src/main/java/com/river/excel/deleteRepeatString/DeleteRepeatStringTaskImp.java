package com.river.excel.deleteRepeatString;

import com.river.excel.Constant;
import com.river.excel.Task;
import com.river.excel.util.DependenciesUtil;
import com.river.excel.util.FileUtil;
import com.river.excel.util.InputUtil;
import com.river.excel.util.XmlUtil;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class DeleteRepeatStringTaskImp implements Task {

    File projectDir;

    @Override
    public void process() {
        System.out.println("请输入项目路径");
        projectDir = InputUtil.readDir(true);

        HashMap<String, ArrayList<String>> dependenciesMap = new HashMap<>();

        HashMap<String, HashMap<String, LinkedHashMap<String, String>>> moduleData = new HashMap<>();

        ArrayList<String> stringsXmlPathList = new ArrayList<>();

        FileUtil.mapFiles(projectDir, file -> {
            if (file.getName().equals(Constant.BUILD_GRADLE_FILE)) {
                try {
                    List<String> strings = FileUtils.readLines(file);
                    for (String line : strings) {
                        boolean dependProject = DependenciesUtil.isDependProject(line);
                        if (!dependProject) {
                            continue;
                        }
                        String key = file.getParentFile().getAbsolutePath();

                        if (!dependenciesMap.containsKey(key)) {
                            dependenciesMap.put(key, new ArrayList<>());
                        }

                        String projectPath = projectDir.getAbsolutePath() + DependenciesUtil.getDependProjectPath(line);

                        dependenciesMap.get(key).add(projectPath);

                        deepDependencies(dependenciesMap.get(key), projectPath + File.separator + Constant.BUILD_GRADLE_FILE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (file.getName().equals("strings.xml")) {
                LinkedHashMap<String, String> data = XmlUtil.xml2map(file.getAbsolutePath());
                String moduleRootPath = FileUtil.getModuleRootPath(file.getAbsolutePath());
                if (!moduleData.containsKey(moduleRootPath)) {
                    moduleData.put(moduleRootPath, new HashMap<>());
                }
                String lan = FileUtil.xmlFileLan(file.getAbsolutePath());
                if (lan == null) {
                    lan = "default";
                }
                moduleData.get(moduleRootPath).put(lan, data);

                stringsXmlPathList.add(file.getAbsolutePath());
            }
        });

        for (String stringsXml : stringsXmlPathList) {
            String moduleRootPath = FileUtil.getModuleRootPath(stringsXml);
            String lan = FileUtil.xmlFileLan(stringsXml);
            if (lan == null) {
                lan = "default";
            }

            if (dependenciesMap.containsKey(moduleRootPath)) {
                try {
                    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = documentBuilder.parse(stringsXml);
                    NodeList nodeList = document.getElementsByTagName("string");
                    ArrayList<Node> deleteNode = new ArrayList<>();
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node item = nodeList.item(i);
                        NamedNodeMap attributes = item.getAttributes();
                        Node name = attributes.getNamedItem("name");
                        boolean isNeedDelete = false;
                        ArrayList<String> depModules = dependenciesMap.get(moduleRootPath);
                        for (String depModule : depModules) {
                            if (!moduleData.containsKey(depModule)) {
                                continue;
                            }

                            boolean isNeedBreak = false;
                            HashMap<String, LinkedHashMap<String, String>> depData = moduleData.get(depModule);
                            if (depData.containsKey(lan)) {
                                LinkedHashMap<String, String> langData = depData.get(lan);
                                if (langData.containsKey(name.getNodeValue())) {
                                    isNeedDelete = true;
                                    isNeedBreak = true;
                                    break;
                                }
                            }

                            if (isNeedBreak) {
                                break;
                            }
                        }
                        if (isNeedDelete) {
                            deleteNode.add(item);
                        }
                    }
                    System.out.println(String.format("需删除%s模块中%d条", stringsXml, deleteNode.size()));
                    for (Node node : deleteNode) {
                        System.out.println("\t-删除Key: " + node.getAttributes().getNamedItem("name").getNodeValue());
                        node.getParentNode().removeChild(node);
                    }
                    TransformerFactory tff = TransformerFactory.newInstance();
                    Transformer transformer = tff.newTransformer();
                    transformer.transform(new DOMSource(document), new StreamResult(stringsXml));
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deepDependencies(ArrayList<String> dependencies, String gradlePath) {
        try {
            List<String> lines = FileUtils.readLines(new File(gradlePath));
            for (String line : lines) {
                boolean dependProject = DependenciesUtil.isDeepDependProject(line);
                if (!dependProject) {
                    continue;
                }

                String projectPath = projectDir.getAbsolutePath() + DependenciesUtil.getDependProjectPath(line);
                dependencies.add(projectPath);

                deepDependencies(dependencies, projectPath + File.separator + Constant.BUILD_GRADLE_FILE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
