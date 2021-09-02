package com.river.excel.compare.translator;

public interface Translator {
    String translate(String langFrom, String langTo, String word) throws Exception;

    String parseResult(String inputJson);
}
