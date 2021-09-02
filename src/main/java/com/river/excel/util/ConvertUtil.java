package com.river.excel.util;

public class ConvertUtil {
    public static String wrap(String value) {
        return value.replace("/S", "/s").trim();
    }
}
