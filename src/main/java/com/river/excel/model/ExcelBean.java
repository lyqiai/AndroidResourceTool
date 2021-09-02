package com.river.excel.model;

import lombok.Data;

@Data
public class ExcelBean {
    private String name;
    private String value;

    public ExcelBean(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public boolean allowTransfer() {
        return name != null && name.trim() != "" && value != null && value.trim() != "";
    }

    @Override
    public String toString() {
        return "ExcelBean{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
