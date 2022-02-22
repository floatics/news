package com.example.news.repository;

import com.example.news.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findAllByCodeIn(List<Long> keywordCode);

    Keyword findByName(String name);

}
