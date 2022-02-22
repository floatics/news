package com.example.news.repository;

import com.example.news.model.Keyword;
import com.example.news.model.NewsProvider;
import com.example.news.model.UserInfo;
import com.example.news.model.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    @Query(value = "SELECT DISTINCT keyword_code FROM USER_KEYWORD WHERE provider_code = :providerCode", nativeQuery = true)
    List<Long> findAllByProviderCode(Long providerCode);
    List<UserKeyword> findKeywordByUserInfo(UserInfo userInfo);

    List<UserKeyword> findAllByUserInfoAndKeyword(UserInfo userInfo, Keyword keyword);
    List<UserKeyword> findAllByUserInfoAndKeywordAndNewsProvider(UserInfo userInfo, Keyword keyword, NewsProvider newsProvider);

    @Transactional
    void deleteAllByUserInfoAndKeywordAndNewsProvider(UserInfo userInfo, Keyword keyword, NewsProvider newsProvider);
}
