package com.river.excel;

import com.river.excel.util.XmlUtil;

import java.util.LinkedHashMap;

public class Test {

    public void process() {
        String xmlPath = this.getClass().getClassLoader().getResource("strings.xml").getPath();
        LinkedHashMap<String, String> data = XmlUtil.xml2map(xmlPath);

        System.out.println(String.format(data.get("tomorrow_d_d"), 0, 0));
        data.forEach((key, content) -> {
            try {
                if (content.contains("%s") || content.contains("s%")) {
                    String.format(content, "test replace");
                }

                if (content.contains("%d") || content.contains("d%")) {
                    String.format(content, 0);
                }
            } catch (Exception e) {
                System.out.println("key:" + key +",content:" + content);
            }
        });
    }

    public static void main(String[] args) {
        new Test().process();
    }
}
