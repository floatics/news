package com.example.news.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter @Builder
public class UserKeywordDto {
    private Long code;
    private Keyword keyword;
    private List<NewsProvider> newsProviders;
}
