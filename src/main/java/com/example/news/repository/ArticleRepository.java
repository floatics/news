package com.example.news.repository;

import com.example.news.model.Article;
import com.example.news.model.Keyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByLinkAndKeyword(String link, Keyword keyword);

    boolean existsByKey(String key);

    Page<Article> findAllByKeywordInOrderByPubDateDescInsDateDesc(List<Keyword> keywords, Pageable pageable);
}
