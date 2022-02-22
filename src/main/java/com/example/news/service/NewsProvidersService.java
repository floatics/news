package com.example.news.service;

import com.example.news.model.NewsProvider;
import com.example.news.repository.NewsProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsProvidersService {
    @Autowired
    private NewsProviderRepository newsProviderRepository;

    @Cacheable("newsProvider")
    public List<NewsProvider> getNewsProviders() {
        return newsProviderRepository.findAll();
    }
}
