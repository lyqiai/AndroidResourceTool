package com.river.excel.excelTransfer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.river.excel.ITask;
import com.river.excel.Task;
import com.river.excel.model.ExcelBean;
import com.river.excel.util.InputUtil;
import com.river.excel.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

@Task(id = 2, name = "读取Excel转String资源文件")
public class Excel2stringTaskImp implements ITask {
    @Override
    public void process() {
        System.out.println("请输入excel路径：");
        File xmlFile = InputUtil.readFile(true);

        System.out.println("请输入string资源文件输出路径：");
        File outputFile = InputUtil.readFile(false);

        final ExcelReadAnalysisEventListener readListener = new ExcelReadAnalysisEventListener(outputFile.getAbsolutePath());

        EasyExcel.read(xmlFile.getAbsolutePath(), ExcelBean.class, readListener).sheet().doRead();
    }


    static class ExcelReadAnalysisEventListener extends AnalysisEventListener<ExcelBean> {
        private String outputPath;
        private DocumentBuilderFactory factory;
        private DocumentBuilder builder;
        private Document document;
        private Element rootElement;
        private int row = 0;

        public ExcelReadAnalysisEventListener(String outputPath) {
            this.outputPath = outputPath;

            try {
                factory = DocumentBuilderFactory.newInstance();
                builder = factory.newDocumentBuilder();
                document = builder.newDocument();

                rootElement = document.createElement("resources");
                document.appendChild(rootElement);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void invoke(ExcelBean excelBean, AnalysisContext analysisContext) {
            if (!excelBean.allowTransfer()) {
                return;
            }
            row++;

            XmlUtil.addRow(document, rootElement, excelBean);

            System.out.println(String.format("处理%d行：%s", row, excelBean));
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            XmlUtil.document2file(document, "D:\\value.xml");
            System.out.println("文件处理完成！");
        }
    }
}
