package com.morotech.bookApi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class GutendexBook {
    private Integer id;
    private String title;
    private List<com.morotech.bookApi.model.dto.GutendexAuthor> authors;
    private List<String> languages;
    @JsonProperty("download_count")
    private Integer downloadCount;
}