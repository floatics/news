package com.example.news.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class NaverNewsItem {
    String title;
    String link;
    String description;
    Date pubDate;
}
