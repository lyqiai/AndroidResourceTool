package com.river.excel.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependenciesUtil {
    public static boolean isDependProject(String text) {
        final String reg = "(implementation|api)\\s+project\\((path:\\s+)?('|\").+('|\")\\)";
        return Pattern.matches(reg, text.trim());
    }

    public static boolean isDeepDependProject(String text) {
        final String reg = "api\\s+project\\((path:\\s+)?('|\").+('|\")\\)";
        return Pattern.matches(reg, text.trim());
    }

    public static String getDependProjectPath(String text) {
        assert isDependProject(text);

        final String reg = "(:(\\w|-|:)+)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(text.trim());
        if (matcher.find()) {
            return matcher.group(0).replace(":", File.separator);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getDependProjectPath("implementation project(path: \":common:yunlu-base\")"));
    }
}
