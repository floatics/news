package com.example.news.model;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Builder
@Entity
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @Id
    @Column(name = "code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long code;

    @Column(name = "key")
    private String key; // 중복수집방지 식별키 (뉴스 url을 encode하여 사용)

    @ManyToOne
    @JoinColumn(name = "keywordCode", referencedColumnName = "code")
    private Keyword keyword;

    @ManyToOne
    @JoinColumn(name = "providerCode", referencedColumnName = "code")
    private NewsProvider newsProvider;

    @Column(name = "title")
    private String title;

    @Column(name = "link")
    private String link;

    @Column(name = "description")
    private String description;

    @Column(name = "pubDate")
    private Date pubDate; // 뉴스 발행일자, 정렬기준값

    @Column(name = "insDate", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null")
    private Date insDate;
}
