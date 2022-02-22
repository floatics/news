package com.example.news.collector;

import com.example.news.model.Keyword;
import com.example.news.model.NewsProvider;
import org.springframework.stereotype.Component;

@Component
public interface ArticleCollector {
    String getCollectorType();
    void collect(Keyword keyword, NewsProvider newsProvider);
}
