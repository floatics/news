package com.example.news.service;

import com.example.news.model.*;
import com.example.news.repository.*;
import com.example.news.collector.ArticleCollector;
import com.example.news.collector.ArticleCollectorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@EnableAsync
public class ArticleService {
    @Autowired
    private UserKeywordRepository userKeywordRepository;

    @Autowired
    private NewsProviderRepository newsProviderRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleCollectorFactory articleCollectorFactory;

    @Cacheable(value = "userArticles") // 수집주기에따라 만료주기도 동일하게 설정필요함. 가급적이면 stampede 적용
    public Page<Article> getUserArticles(UserInfo userInfo, Pageable pageable) {
        List<UserKeyword> UserKeywords = userKeywordRepository.findKeywordByUserInfo(userInfo);
        List<Keyword> keywords = new ArrayList<>();
        for (UserKeyword userKeyword : UserKeywords) {
            keywords.add(userKeyword.getKeyword());
        }
        return articleRepository.findAllByKeywordInOrderByPubDateDescInsDateDesc(keywords, pageable);
    }

    @Async
    @CacheEvict(value = "userArticles", allEntries = true)
    public void collectArticles() {
        List<NewsProvider> newsProviders = newsProviderRepository.findAll();
        for (NewsProvider newsProvider : newsProviders) {
            Long newsProviderCode = newsProvider.getCode();
            List<Long> userKeywordCodes = userKeywordRepository.findAllByProviderCode(newsProviderCode);
            List<Keyword> keywords = keywordRepository.findAllByCodeIn(userKeywordCodes);
            for (Keyword keyword : keywords) {
                collectByProvider(keyword, newsProvider);
            }
        }
    }

    @Async
    @CacheEvict(value = "userArticles", allEntries = true)
    public void collectByProvider(Keyword keyword, NewsProvider newsProvider) {
        log.info("수집 시작 : " + keyword.getName() + " / " + newsProvider.getName());
        ArticleCollector articleCollector = articleCollectorFactory.getArticleCollector(newsProvider.getName());
        if (articleCollector != null) {
            articleCollector.collect(keyword, newsProvider);
        }
    }
}
