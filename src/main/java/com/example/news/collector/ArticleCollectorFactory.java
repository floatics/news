package com.example.news.collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleCollectorFactory {
    @Autowired
    private List<ArticleCollector> articleCollectors;

    public ArticleCollector getArticleCollector(String articleCollectorType) {
        for (ArticleCollector articleCollector : articleCollectors) {
            if (articleCollector.getCollectorType().equals(articleCollectorType)) {
                return articleCollector;
            }
        }
        return null;
    }
}
