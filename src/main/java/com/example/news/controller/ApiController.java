package com.example.news.controller;

import com.example.news.model.NewsProvider;
import com.example.news.model.UserInfo;
import com.example.news.model.UserInfoDto;
import com.example.news.model.UserKeywordDto;
import com.example.news.service.ArticleService;
import com.example.news.service.KeywordService;
import com.example.news.service.NewsProvidersService;
import com.example.news.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
public class ApiController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private NewsProvidersService newsProvidersService;

    @PostMapping("/users")
    public UserInfoDto addUser(UserInfoDto userInfoDto, HttpServletResponse response) throws IOException {
        userInfoDto.setAuth("ROLE_USER");
        UserInfo userInfo = userService.addUser(userInfoDto);
        UserInfo.builder().email(userInfo.getEmail()).build();
        response.sendRedirect("/login");
        return UserInfoDto.builder().email(userInfo.getEmail()).build();
    }

    @GetMapping("/providers")
    public Object getProviders() {
        return newsProvidersService.getNewsProviders();
    }

    @GetMapping("/users/my/articles")
    public Object getMyArticles(Pageable pageable) {
        return articleService.getUserArticles(userService.getSignedUserInfo(), pageable);
    }
    @GetMapping("/users/my/keywords")
    public Object getMyKeywords() {
        return keywordService.getKeywordsByUser(userService.getSignedUserInfo());
    }

    @PostMapping("/users/my/keywords")
    public Object postMyKeywords(@RequestBody UserKeywordDto userKeywordDto) {
        UserKeywordDto userKeywordDtoNew = keywordService.saveUserKeyword(userKeywordDto, userService.getSignedUserInfo());
        collectKeywords(userKeywordDtoNew);
        return ResponseEntity.ok().body(userKeywordDtoNew);
    }

    @PutMapping("/users/my/keywords/{code}")
    public Object putMyKeywords(@PathVariable Long code, @RequestBody UserKeywordDto userKeywordDto) throws Exception {
        if (!userKeywordDto.getCode().equals(code)) {
            throw new Exception("오류");
        }
        UserKeywordDto userKeywordDtoNew = keywordService.modifyUserKeyword(userKeywordDto, userService.getSignedUserInfo());
        collectKeywords(userKeywordDtoNew);
        return userKeywordDtoNew;
    }

    @DeleteMapping("/users/my/keywords/{code}")
    public void deleteMyKeywords(@PathVariable Long code, @RequestBody UserKeywordDto userKeywordDto) throws Exception {
        if (!userKeywordDto.getCode().equals(code)) {
            throw new Exception("오류");
        }
        keywordService.deleteUserKeyword(userKeywordDto, userService.getSignedUserInfo());
        log.info("code : " + code);
    }

    private void collectKeywords(UserKeywordDto userKeywordDto) {
        List<NewsProvider> newsProviders = userKeywordDto.getNewsProviders();
        for (NewsProvider newsProvider : newsProviders) {
            articleService.collectByProvider(userKeywordDto.getKeyword(), newsProvider);
        }
    }
}
