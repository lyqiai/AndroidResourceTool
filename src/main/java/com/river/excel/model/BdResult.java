package com.river.excel.model;

import lombok.Data;

import java.util.List;

@Data
public class BdResult {

    private String from;
    private String to;
    private List<TransResultDTO> trans_result;

    @Data
    public static class TransResultDTO {
        private String src;
        private String dst;
    }
}
