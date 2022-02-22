package com.example.news.collector;

import com.example.news.model.Keyword;
import com.example.news.model.NewsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DaumArticleCollectorImpl implements ArticleCollector {
    @Override
    public String getCollectorType() {
        return ArticleCollectorType.DAUM;
    }

    @Override
    public void collect(Keyword keyword, NewsProvider newsProvider) {
        log.info("SKIP '" + keyword.getName() + "' : " + ArticleCollectorType.DAUM + " API 없음");
    }

}
