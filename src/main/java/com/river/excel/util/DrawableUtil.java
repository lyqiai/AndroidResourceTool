package com.river.excel.util;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class DrawableUtil {
    public static boolean compareDrawable(String sourcePath, String targetPath) {
        try {
            XMLUnit.setIgnoreWhitespace(true);
            XMLUnit.setIgnoreAttributeOrder(true);
            FileInputStream sourceFileInputStream = new FileInputStream(sourcePath);
            BufferedReader sourceBufferedReader = new BufferedReader(new InputStreamReader(sourceFileInputStream));
            FileInputStream targetFileInputStream = new FileInputStream(targetPath);
            BufferedReader targetBufferedReader = new BufferedReader(new InputStreamReader(targetFileInputStream));

            Diff diff = XMLUnit.compareXML(sourceBufferedReader, targetBufferedReader);
            DetailedDiff detailedDiff = new DetailedDiff(diff);
            List<?> allDifferences = detailedDiff.getAllDifferences();
            return allDifferences.size() == 0;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
