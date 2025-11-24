package com.morotech.bookApi.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class GutendexApiResponse {
    private Integer count;
    private String next;
    private String previous;
    private List<com.morotech.bookApi.model.dto.GutendexBook> results;
}
