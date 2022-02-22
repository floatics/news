package com.example.news.collector;

import com.example.news.model.*;
import com.example.news.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class NaverArticleCollectorImpl implements ArticleCollector {
    @Value("${naver.api.news.uri}")
    private String uri;

    @Value("${naver.api.news.hostname}")
    private String hostname;

    @Value("${naver.api.news.clientId}")
    private String clientId;

    @Value("${naver.api.news.clientSecret}")
    private String clientSecret;

    @Value("${naver.api.news.display}")
    private String display;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public String getCollectorType() {
        return ArticleCollectorType.NAVER;
    }

    private String getEnv(String key) {
        Environment environment = applicationContext.getEnvironment();
        return environment.getProperty(key);
    }

    /**
     * properties 에 값이 없을경우 환경변수에서 읽어오도록 처리
     *
     * @return Naver api client id 정보
     */
    public String getClientId() {
        return clientId.equals("") ? getEnv("NAVER_API_CLIENT_ID") : clientId;
    }

    /**
     * properties 에 값이 없을경우 환경변수에서 읽어오도록 처리
     *
     * @return Naver api client id 정보
     */
    public String getClientSecret() {
        return clientId.equals("") ? getEnv("NAVER_API_CLIENT_SECRET") : clientSecret;
    }

    /**
     * 뉴스 수집
     *
     * @param keyword      키워드 정보
     * @param newsProvider 뉴스공급자 정보 (naver)
     */
    @Override
    public void collect(Keyword keyword, NewsProvider newsProvider) {
        WebClient webClient = getWebClient();
        NaverNewsApiResponse naverNewsApiResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(uri)
                        .queryParam("query", keyword.getName())
                        .queryParam("display", display)
                        .queryParam("start", 1)
                        .queryParam("sort", "date")
                        .build()).retrieve().bodyToMono(NaverNewsApiResponse.class).block();

        List<NaverNewsItem> naverNewsItems = naverNewsApiResponse.getItems();

        for (NaverNewsItem naverNewsItem : naverNewsItems) {
            String articleKey = getArticleKey(keyword.getCode().toString(), naverNewsItem.getLink());
            Article article = Article.builder()
                    .description(naverNewsItem.getDescription())
                    .key(articleKey)
                    .pubDate(naverNewsItem.getPubDate())
                    .title(naverNewsItem.getTitle())
                    .keyword(keyword)
                    .newsProvider(newsProvider)
                    .link(naverNewsItem.getLink()).build();
            // 중복수집 방지
            if (!articleRepository.existsByKey(articleKey)) {
                Article savedArticle = articleRepository.save(article);
                log.info("SAVE : " + savedArticle.getKeyword().getName() + " / " + savedArticle.getTitle());
            }
        }
    }

    /**
     * naver API 호출을 위한 WebClient 객체 반환
     *
     * @return web client
     */
    private WebClient getWebClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(hostname);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
        WebClient webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl(hostname)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add("X-Naver-Client-Id", getClientId());
                    httpHeaders.add("X-Naver-Client-Secret", getClientSecret());
                })
                .build();
        return webClient;
    }

    /**
     * 중복수집을 방지하기위한 key 정보 조합
     *
     * @param keywordCode keyword 의 unique한 코드값
     * @param link        뉴스글의 link address
     * @return 뉴스 게시글 중복방지 unique 키
     */
    private String getArticleKey(String keywordCode, String link) {
        return Base64.getEncoder().encodeToString((keywordCode + link).getBytes());
    }
}
