package com.example.news.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class NaverNewsApiResponse {
    Date lastBuildDate;
    Long total;
    Long start;
    Long display;
    List<NaverNewsItem> items;
}
