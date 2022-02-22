package com.example.news.service;

import com.example.news.model.*;
import com.example.news.repository.KeywordRepository;
import com.example.news.repository.UserKeywordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KeywordService {
    @Autowired
    private UserKeywordRepository userKeywordRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @CacheEvict(value = "keywordsByUser", key = "#userInfo.code")
    public UserKeywordDto saveUserKeyword(UserKeywordDto userKeywordDto, UserInfo userInfo) {
        Keyword keyword = userKeywordDto.getKeyword();
        Keyword keywordByDB = keywordRepository.findByName(keyword.getName());
        // 키워드 없을경우 추가
        if (keywordByDB == null) {
            keywordByDB = keywordRepository.save(keyword);
        }
        List<NewsProvider> newsProviders = userKeywordDto.getNewsProviders();
        // 매핑정보 추가
        for (NewsProvider newsProvider : newsProviders) {
            userKeywordRepository.save(UserKeyword.builder().keyword(keywordByDB).userInfo(userInfo).newsProvider(newsProvider).build());
        }
        userKeywordDto.setCode(keywordByDB.getCode());
        return userKeywordDto;
    }

    @CacheEvict(value = "keywordsByUser", key = "#userInfo.code")
    public UserKeywordDto modifyUserKeyword(UserKeywordDto userKeywordDto, UserInfo userInfo) {
        Keyword keyword = userKeywordDto.getKeyword();
        // 수정요청된 목록
        List<NewsProvider> newsProviders = userKeywordDto.getNewsProviders();
        // DB에 저장된 목록
        List<UserKeyword> userKeywordsPrev = userKeywordRepository.findAllByUserInfoAndKeyword(userInfo, keyword);

        // DB에 없는 데이터 추가
        for (NewsProvider newsProvider : newsProviders) {
            List<Long> prevCodeList = new ArrayList<>();
            for (UserKeyword userKeywordPrev : userKeywordsPrev) {
                prevCodeList.add(userKeywordPrev.getNewsProvider().getCode());
            }
            if (!prevCodeList.contains(newsProvider.getCode())) {
                userKeywordRepository.save(UserKeyword.builder().keyword(keyword).userInfo(userInfo).newsProvider(newsProvider).build());
            }
        }

        // DB에 있는 목록 삭제
        for (UserKeyword userKeyword: userKeywordsPrev) {
            List<Long> newCodeList = new ArrayList<>();
            for (NewsProvider newsProvider: newsProviders) {
                newCodeList.add(newsProvider.getCode());
            }
            if (!newCodeList.contains(userKeyword.getNewsProvider().getCode())) {
                userKeywordRepository.delete(userKeyword);
            }
        }

        return userKeywordDto;
    }

    @CacheEvict(value = "keywordsByUser", key = "#userInfo.code")
    public void deleteUserKeyword(UserKeywordDto userKeywordDto, UserInfo userInfo) {
        Keyword keyword = userKeywordDto.getKeyword();
        List<NewsProvider> newsProviders = userKeywordDto.getNewsProviders();
        for (NewsProvider newsProvider: newsProviders) {
            userKeywordRepository.deleteAllByUserInfoAndKeywordAndNewsProvider(userInfo, keyword, newsProvider);
            log.info("삭제 : " + newsProvider.getName());
        }
    }

    @Cacheable(value = "keywordsByUser", key = "#userInfo.code")
    public List<UserKeywordDto> getKeywordsByUser(UserInfo userInfo) {
        List<UserKeyword> userKeywords = userKeywordRepository.findKeywordByUserInfo(userInfo);
        return convertUserKeywordToUserKeywordResponseDto(userKeywords);
    }

    /**
     * API response 를 위한 가공
     *
     * @param userKeywords
     * @return
     */
    private List<UserKeywordDto> convertUserKeywordToUserKeywordResponseDto(List<UserKeyword> userKeywords) {
        HashMap<Long, UserKeywordDto> map = new HashMap<>();
        for (UserKeyword userKeyword : userKeywords) {
            UserKeywordDto prevData = map.get(userKeyword.getKeyword().getCode());

            List<NewsProvider> newsProviders = new ArrayList<>();
            if (prevData != null) {
                newsProviders = prevData.getNewsProviders();
                newsProviders.add(userKeyword.getNewsProvider());
                prevData.setNewsProviders(newsProviders);
            } else {
                newsProviders.add(userKeyword.getNewsProvider());
            }
            UserKeywordDto userKeywordDto = UserKeywordDto.builder().code(userKeyword.getKeyword().getCode()).keyword(userKeyword.getKeyword()).newsProviders(newsProviders).build();
            map.put(userKeyword.getKeyword().getCode(), userKeywordDto);
        }
        return map.values().stream().collect(Collectors.toCollection(ArrayList::new));
    }
}
