package com.example.news.repository;

import com.example.news.model.NewsProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsProviderRepository extends JpaRepository<NewsProvider, Long> {
}
